package com.xjx.mydns.transmit.query;

import com.xjx.mydns.constant.Constants;
import com.xjx.mydns.transmit.code.MessageDecoder;
import com.xjx.mydns.transmit.code.MessageEncoder;
import com.xjx.mydns.transmit.entity.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理上游DNS服务器的 Response对象
 * 需要缓存到本地数据库 + Redis缓存
 */
@Slf4j
public class UpStreamResolver extends Thread{
    // 上游服务器
    UpStreamServer upStreamServer;
    // 报文头
    ArrayList<Question> questions = new ArrayList<>();

    public UpStreamResolver(UpStreamServer upStreamServer){
        this.upStreamServer = upStreamServer;
    }

    // 解析Response响应
    public void analysizeUpperDNS(){
        Response response = this.upStreamServer.getResponse();
        // 解析 Packet 数据包： Message(报文头)、Question(查询问题)、Resource(回答问题)
        if(response != null){
            Packet packet = Packet.create(response.getPacket());

            // 判断接收报文的类型(上游服务器返回的应该是响应报文)
            Message message = MessageDecoder.decodeMessage(packet);
            if(message.isQueryMessage() == true){
                log.error("UpStreamResolver ： 当前收到的不是响应报文....");
            }

            // 解析消息包中的应答部分（域名）
            packet.setOffset(12);
            questions.clear();
            for(int i=0;i<message.getQuestionNum();i++){
                StringBuilder builder = new StringBuilder();
                int len = 0;
                while((len = (packet.getNextByte() & 0xff))>0){
                    builder.append(new String(packet.getNextBytes(len)));
                    builder.append(".");
                }
                builder.deleteCharAt(builder.length()-1);
                // 域名类型
                int questionType = packet.getNextShort() & 0xffff;
                // 类
                int questionClass = packet.getNextShort() & 0xffff;
                questions.add(new Question(builder.toString(),questionType,questionClass));
            }
//            log.info("UpStreamResolver : 成功获取到响应包中的问题字段,问题个数为 : {}",questions.size());
            Question question = questions.get(0);

            // 解析回答字段
            List<Resource> resources = new ArrayList<>();
            int resourcesNum = message.getAnswerNum()+message.getAdditionalNum()+message.getAuthorityNum();
            for(int i=0;i<resourcesNum;i++){
                // 域名
                short domain = packet.getNextShort();
                // 类型
                short atype = packet.getNextShort();
                // 如果不是IPV4或者IPV6 , 则跳过 类、生存时间两个字段 (共6个字节)
                if(atype!=Constants.RESPONSE_IPV4  &&  atype!=Constants.RESPONSE_IPV6){
                    packet.skip(6);
                    // 剩余资源长度
                    short rlen = packet.getNextShort();
                    packet.skip(rlen);
                    continue;
                }

                packet.skip(2);
                // 生存时间
                int ttl = packet.getNextInt();
                ttl = Math.min(ttl,Integer.MAX_VALUE);
                short rlen = packet.getNextShort();
                // 资源数据长度 : 4(IPV4) 、 16(IPV6)
                if(rlen == Constants.RESOURCE_LEN_IPV4){
                    long ipv4Addr = packet.getNextInt() & 0xffffffff;
                    resources.add(new Resource(question.getDomainName(),atype,ttl,ipv4Addr));
                }
                else if(rlen == Constants.RESOURCE_LEN_IPV6){
                    byte[] ipv6Addr = packet.getNextBytes(16);
                    resources.add(new Resource(question.getDomainName(),atype,ttl,ipv6Addr));
                }
            }

            // 封装 Response,这里需要转换数据包的标识 和 Response的 sequence、socketAddress
            message.setTransactionId(response.getSequence());
            byte[] respData = MessageEncoder.encode(message,question,resources.toArray(new Resource[0]));
            // Response响应消息最终返回给 DomainNameServer进行处理,注意这里的 socketAddress是发送方的地址,不是上游服务器的地址
            DomainNameServer.getInstance().putResponse(new Response(response.getSequence(),respData,response.getSocketAddress()));
        }
    }

    @Override
    public void run() {
        while (!this.isInterrupted()){
            try {
                analysizeUpperDNS();
            } catch (Exception e) {
                e.printStackTrace();
                log.error("UpStreamResolver : 解析上游DNS服务器的Response对象失败,{}",e.getMessage());
            }
        }
    }
}
