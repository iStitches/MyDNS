package com.xjx.mydns.transmit.query;

import com.xjx.mydns.constant.Constants;
import com.xjx.mydns.entity.Address;
import com.xjx.mydns.stat.ManagerStat;
import com.xjx.mydns.transmit.code.MessageDecoder;
import com.xjx.mydns.transmit.code.MessageEncoder;
import com.xjx.mydns.transmit.entity.*;
import com.xjx.mydns.transmit.utils.RedisSearch;
import com.xjx.mydns.transmit.utils.RulesManager;
import com.xjx.mydns.utils.BeanUtils;
import com.xjx.mydns.utils.IPUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 具体的域名解析服务
 */
@Slf4j
public class DomainNameResolver extends Thread{
    // DNS域名服务器
    DomainNameServer domainNameServer;

    // DNS报文中查询问题字段
    ArrayList<Question> questions = new ArrayList<>();

    // Redis缓存服务
    RedisSearch redisSearch = BeanUtils.createBean(RedisSearch.class);

    // 匹配规则
    RulesManager rulesManager = RulesManager.getInstance();
    SimpleDateFormat dateFormat = new SimpleDateFormat("HHmmss");
    ReentrantLock lock = new ReentrantLock();

    // 解析数据包
    public void resolvePacket() throws Exception{
        Request request = domainNameServer.getRequest();
        if(request != null){
            Packet packet = request.getPacket();

            /**
             * 报文头解析 --- Message (数据包的报文头)
             */
            Message message = MessageDecoder.decodeMessage(packet);
            if(!message.isQueryMessage()){
                log.error("error：  当前DNS报文不是查询报文....");
                return;
            }
//            log.info("DNS报文头信息： 标识 = {}  |   标志 = {}  |  问题数 = {}  |  回答 = {}  |  授权资源 = {} | 附加资源 = {}",message.getTransactionId(),message.getFlags(),message.getQuestionNum(),message.getAnswerNum(),message.getAuthorityNum(),message.getAdditionalNum());

            /**
             * 问题内容 --- packet (数据包的问题部分)
             *   注意从第13个字节开始才为问题部分,包括 查询名(域名)、类型、类
             *      3 w w w 4 g x n u 3 e d u 2 e n 0 (每个字符串字段前表示该字段的字符串长度,以1个字节格式存储)
             *      每个域名以最后的字节0结束
             */
            packet.setOffset(12);
            int len = 0,i = 0;
            questions.clear();
            // 解析问题集合,将结果装入到 questions集合中
            for(;i<message.getQuestionNum();i++){
                StringBuilder builder = new StringBuilder();
                while((len = packet.getNextByte()) != 0)
                {
                    /**
                     *  len：获取到接下来的字符串长度
                     *  questionType：问题类型,1short
                     *  questionClass：问题类,1short
                     */
                    byte[] bytes = packet.getNextBytes(len);
                    builder.append(new String(bytes));
                    builder.append(".");
                }
                builder.deleteCharAt(builder.length()-1);
                int questionType = packet.getNextByte();
                int questionClass = packet.getNextByte();
                questions.add(new Question(builder.toString(),questionType,questionClass));
            }

            // 问题处理
            if(questions.size() > 1 ){
                log.error("有多个域名请求查询，不能处理.....");
                return;
            }
            Question question = questions.get(0);

            log.info("#####################################################################################################");
            log.info("待查询的域名：  {}",question.getDomainName());
            log.info("#####################################################################################################");

            String requestIPAddr = ((InetSocketAddress)request.getSocketAddress()).toString();
            requestIPAddr = requestIPAddr.substring(1,requestIPAddr.indexOf(":"));
            int now = Integer.parseInt(dateFormat.format(new Date()));
            long sendIpAddr = IPUtils.convertByteToLong(((InetSocketAddress)request.getSocketAddress()).getAddress().getAddress(),0,4);
            // 查询统计
            ManagerStat.getInstance().recordRequest(requestIPAddr,question.getDomainName(),new Date());

            // 先根据初始内存查找
//            Address address = rulesManager.matchRules(now,sendIpAddr,question.getDomainName());
            Address address = null;
            // 内存未命中,查询Redis缓存
            if(address == null){
                String IP = redisSearch.getRuleModel(question.getDomainName());
//                log.info("Redis缓存查询结果：  {}",IP);
                if(IP!=null && IP!=""){
                    packetResponse(IP,question,message,request.getSocketAddress());
                }

            //缓存未命中,请求转发给上层DNS服务器
                else{
                    // 上游DNS服务器请求发送
                    ManagerStat.getInstance().incrUpStreamCount();
                    UpStreamServer.getInstance().putRequest(request);
                }
            }
            else{
                //响应统计
//                ManagerStat.getInstance().recordResponse(Configs.get("dns.my.server.ip"));
//                log.info("内存查询结果：   {}",address.getAddress());
                packetResponse(address.getAddress(),question,message,request.getSocketAddress());
            }
        }
    }


    /**
     * 返回Response
     * @param ipResult  最终解析得到的IP地址　－－－　字符串格式
     * @param question  报文 --- 问题字段
     * @return
     */
    public void packetResponse(String ipResult, Question question, Message message, SocketAddress socketAddress){
        /**
         * 封装回答字段：
         *    域名
         *    类型
         *    类
         *    生存时间
         *    资源数据长度
         *    资源数据
         */
        Response response = null;
        Resource[] resources = new Resource[]{new Resource(question.getDomainName(), Constants.RESPONSE_IPV4,Constants.MY_DNS_RESPONSE_TTL,IPUtils.convertStrToLong(ipResult))};
        /**
         * 完整数据包封装
         *    报文头:  标识、标志、问题数、回答数、授权资源数、额外资源记录数
         *    查询问题：问题名、问题类型、问题类
         *    回答：域名、类型、类、生存时间、资源数据长度、资源数据
         *    授权资源
         *    额外资源
         */
        byte[] respData = MessageEncoder.encode(message, question, resources);
        if(respData.length > 0)
            response= new Response((short)message.getTransactionId(),respData,socketAddress);
        if(response != null)
            domainNameServer.putResponse(response);
    }


    @Override
    public void run() {
        while(!this.isInterrupted()){
            try {
                resolvePacket();
            } catch (Exception e) {
                log.error("Error Pos   ：  DomainNameResolver error .....");
                e.printStackTrace();
                log.error("DomainNameResolver error :  报文解析出错....");
            }
        }
    }
}
