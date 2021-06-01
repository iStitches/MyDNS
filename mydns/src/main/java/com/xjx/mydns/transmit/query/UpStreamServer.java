package com.xjx.mydns.transmit.query;

import com.xjx.mydns.stat.ManagerStat;
import com.xjx.mydns.transmit.entity.OriginalRequest;
import com.xjx.mydns.transmit.entity.Packet;
import com.xjx.mydns.transmit.entity.Request;
import com.xjx.mydns.transmit.entity.Response;
import com.xjx.mydns.utils.Configs;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 上游DNS服务器 --- 发送报文给上游服务器
 */
@Slf4j
public class UpStreamServer extends Thread{
    //单例模式
    static UpStreamServer instance = null;
    //存储请求
    ArrayBlockingQueue<Request> requestList = null;
    //存储响应
    ArrayBlockingQueue<Response> responsesList = null;
    //可用于域名解析的资源数
    UpStreamResolver[] upStreamResolvers = null;
    //存储发送方的 Request
    LinkedHashMap<Short, OriginalRequest> originSender = new LinkedHashMap<>();

    public UpStreamServer(){
        this.requestList = new ArrayBlockingQueue<>(65535);
        this.responsesList = new ArrayBlockingQueue<>(65535);
        this.upStreamResolvers = new UpStreamResolver[Runtime.getRuntime().availableProcessors()*2];
        for(int i=0;i<upStreamResolvers.length;i++){
            this.upStreamResolvers[i] = new UpStreamResolver(this);
            this.upStreamResolvers[i].setName("DNS-UpStreamResolver-"+i);
            this.upStreamResolvers[i].start();
        }
    }

    public static UpStreamServer getInstance(){
        if(instance == null){
            synchronized (UpStreamServer.class){
                if (instance == null)
                    instance = new UpStreamServer();
            }
        }
        return instance;
    }

    public Request getRequest(){
        try {
            return this.requestList.take();
        } catch (InterruptedException e) {
            log.error("获取Request等待超时....");
        }
        return null;
    }

    public void putRequest(Request request){
        try {
            this.requestList.put(request);
        } catch (InterruptedException e) {
            log.error("存放Request错误....");
        }
    }

    public void putResponse(Response response){
        try {
            this.responsesList.put(response);
        } catch (InterruptedException e) {
            log.error("存放Response错误....");
        }
    }

    public Response getResponse(){
        try {
            return this.responsesList.take();
        } catch (InterruptedException e) {
            log.error("获取Response等待超时....");
        }
        return null;
    }

    // 保存发送方 Request的 InetSocketAddrsess
    public void saveOriginalRequest(short sequence, OriginalRequest request){
        this.originSender.put(sequence,request);
    }

    // 获取发送方 Request的 InetSocketAddrsess
    public OriginalRequest getOriginalRequest(short sequence){
        return originSender.get(sequence);
    }
    /**
     * 发送DNS数据包给上层DNS服务器
     */
    public void sendPacketToUpper(){
        DatagramChannel datagramChannel = null;
        try {
            // 发送数据到Sender
            datagramChannel = DatagramChannel.open();
            InetSocketAddress socketAddress = new InetSocketAddress(Configs.get("dns.upstream.server.ip"),Configs.getInt("dns.upstream.server.port"));
            datagramChannel.configureBlocking(false);
            new Sender(this,datagramChannel,socketAddress).start();

            // 注册多路复用器
            Selector selector = Selector.open();
            datagramChannel.register(selector, SelectionKey.OP_READ);
            ByteBuffer recvBuffer = ByteBuffer.allocate(1024);

            // 持续监听上游服务器的响应
            while(!this.isInterrupted()){
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                while(iterator.hasNext()){
                     SelectionKey selectionKey = iterator.next();
                     if(selectionKey.isReadable()){
                         // 将消息存储到 recvBuffer缓冲区中
                         recvBuffer.clear();
                         SocketAddress receive = datagramChannel.receive(recvBuffer);
                         String address = receive.toString();
                         String ip = address.substring(1,address.indexOf(":"));
                         recvBuffer.flip();
                         byte[] respData = new byte[recvBuffer.limit()];
                         recvBuffer.get(respData,0,respData.length);
//                         log.info("接收到上游DNS服务器的响应：  长度为 {}",respData.length);

                         // 生存 Response交给 UpStreamResolver处理
                         short sequence = Packet.getShort(respData,0,2);
                         OriginalRequest originalRequest = getOriginalRequest(sequence);
                         this.responsesList.put(new Response(originalRequest.getSequence(),respData,originalRequest.getSocketAddress()));
                     }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("UpStreamServer 执行出错 ..... : {}",e.getMessage());
        } finally {
            try {
                datagramChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
                log.error("UpStreamServer 关闭出错 ..... : {}",e.getMessage());
            }
        }
    }


    // 请求发送上层DNS服务器
    static class Sender extends Thread{
        private UpStreamServer upStreamServer;
        private DatagramChannel datagramChannel;
        private InetSocketAddress socketAddress;

        public Sender(UpStreamServer upStreamServer, DatagramChannel datagramChannel, InetSocketAddress socketAddress){
            this.upStreamServer = upStreamServer;
            this.datagramChannel = datagramChannel;
            this.socketAddress = socketAddress;
        }
        @Override
        public void run() {
             ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
             // 转发处理,需要重新设置数据包的序号
             short sequence = 1;
             // 一直监听待发送的请求
             while(!this.isInterrupted()){

                 // 阻塞获取,没有Request对象就会一直等待
                 Request request = upStreamServer.getRequest();
                 if(request != null){
                     try {
                         // 缓存发送方 Request(发送序号 + 发送端SocketAddress)
                         request.setSequence(sequence);
                         Packet packet = request.getPacket();
                         packet.setOffset(0);
                         upStreamServer.saveOriginalRequest(sequence,new OriginalRequest(request.getPacket().getNextShort(),request.getSocketAddress()));

                         // 重新设置转发的 Request(序号为sequence)
                         packet.setOffset(0);
                         packet.setShort(sequence);
                         sequence++;

                         // 数据包 → 字节数组
                         byteBuffer.clear();
                         byteBuffer.put(packet.getBytes());
                         byteBuffer.flip();
                         datagramChannel.send(byteBuffer,socketAddress);
//                         log.info("成功向上游DNS服务器发送请求，序列号为:   {}   ,数据长度为:  {}",sequence,packet.getBytes().length);
                     } catch (Exception e) {
                         e.printStackTrace();
                         log.error("数据发送失败,请检查网络并重试.....");
                     }
                 }
             }
        }
    }

    @Override
    public void run() {
        sendPacketToUpper();
    }

    public static void init(){
        instance.start();
    }
}
