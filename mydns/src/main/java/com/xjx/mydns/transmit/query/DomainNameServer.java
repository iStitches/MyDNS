package com.xjx.mydns.transmit.query;

import com.xjx.mydns.constant.Constants;
import com.xjx.mydns.stat.ManagerStat;
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
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * DNS域名服务器(本计算机)
 */
@Slf4j
public class DomainNameServer extends Thread{
    //单例模式
    static DomainNameServer instance = null;
    //存储请求
    ArrayBlockingQueue<Request> requestList = null;
    //存储响应
    ArrayBlockingQueue<Response> responsesList = null;
    //可用于域名解析的资源数
    DomainNameResolver[] domainNameResolvers = null;

    public static DomainNameServer getInstance(){
        if(instance == null){
            synchronized (DomainNameServer.class){
                if(instance == null){
                    instance = new DomainNameServer();
                }
            }
        }
        return instance;
    }

    public DomainNameServer(){
        //IP数据报报文长度为16位，最大65535
        this.requestList = new ArrayBlockingQueue<Request>(65535);
        this.responsesList = new ArrayBlockingQueue<Response>(65535);
        //Runtime.getRuntime().availableProcessors()：   获取逻辑CPU数(核心数*2)
        this.domainNameResolvers = new DomainNameResolver[Runtime.getRuntime().availableProcessors()*2];
        for(int i=0;i<domainNameResolvers.length;i++){
            this.domainNameResolvers[i] = new DomainNameResolver();
            this.domainNameResolvers[i].domainNameServer = this;
            this.domainNameResolvers[i].setName("DNS-NameResolver-"+i);
            this.domainNameResolvers[i].start();
        }
    }

    public Request getRequest(){
        try {
            return this.requestList.take();
        } catch (InterruptedException e) {
            log.error("获取Request等待超时....");
        }
        return null;
    }

    public void putResponse(Response response){
        try {
            this.responsesList.put(response);
        } catch (InterruptedException e) {
            log.error("存放Response错误....");
        }
    }

    public void receiveMsg(){
         //同步非阻塞NIO模式
         DatagramChannel datagramChannel = null;
        try {
            //建立起通道绑定服务器IP地址、端口
            String serverIP = Configs.get(Constants.MY_DNS_ServerIP);
            int serverPort = Configs.getInt(Constants.MY_DNS_ServerPORT);
            datagramChannel = DatagramChannel.open();
            datagramChannel.configureBlocking(false);   //注意需要将通道设置为非阻塞形式
            datagramChannel.socket().bind(new InetSocketAddress(serverIP,serverPort));
//            log.info("成功绑定本地DNS服务器，ip:{}    port:{}",serverIP,serverPort);

            //开启发送线程
            new Sender(this,datagramChannel).start();
//            log.info("成功运行发送线程-----");

            //绑定多路复用器
            datagramChannel.configureBlocking(false);
            Selector selector = Selector.open();
            datagramChannel.register(selector, SelectionKey.OP_READ);
            ByteBuffer readBuffer = ByteBuffer.allocate(1024);

            while(!this.isInterrupted()){
                // 监听请求
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();

                while(iterator.hasNext()){
                    SelectionKey next = iterator.next();
                    if(next.isReadable()){
                        readBuffer.clear();
                        SocketAddress socketAddress = datagramChannel.receive(readBuffer);
                        readBuffer.flip();
                        byte[] receiveMsg = new byte[readBuffer.limit()];
                        readBuffer.get(receiveMsg);
//                        log.info("接收到来自: {} 的数据，数据长度 = {}",socketAddress.toString(),receiveMsg.length);

                        // 封装一个请求放入队列中等待处理,阻塞队列的put操作能够在队列满时阻塞等待
                        Packet packet = Packet.create(receiveMsg);
                        requestList.put(new Request(packet,socketAddress));
                    }
                }
            }
        } catch (Exception e) {
            log.error("DomainNameServer receive error:  {}",e.getMessage());
        } finally {
//            log.error("Error Pos   ：  DomainNameServer error .....");
            try {
                datagramChannel.close();
            } catch (IOException e) {
                log.error("DomainNameServer close error: {}",e.getMessage());
            }
            log.error("DomainNameServer success close....");
        }
    }

    @Override
    public void run() {
        receiveMsg();
    }

    public void init(){
        instance.start();
    }

    // 结果响应方
    static class Sender extends Thread{
        public DomainNameServer domainNameServer = null;
        public DatagramChannel channel = null;
        public ByteBuffer sendBuffer = ByteBuffer.allocate(1024);

        public Sender(DomainNameServer domainNameServer, DatagramChannel channel){
            this.domainNameServer = domainNameServer;
            this.channel = channel;
        }

        @Override
        public void run() {
            // 发送数据循环检测
            while(!this.isInterrupted()){
                try {
                    Response resp = domainNameServer.responsesList.take();
                    ManagerStat.getInstance().incrRespCount();
                    sendBuffer.clear();
                    sendBuffer.put(resp.getPacket());
                    sendBuffer.flip();
                    channel.send(sendBuffer,resp.getSocketAddress());
//                    log.info("DNS服务器成功返回响应, To:{}  length:{}",resp.getSocketAddress().toString(),resp.getPacket().length);
                } catch (Exception e) {
                    log.error("DomainNameServer send error:  {}",e.getMessage());
                }
            }
        }
    }
}
