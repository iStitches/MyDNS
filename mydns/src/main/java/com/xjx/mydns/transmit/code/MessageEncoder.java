package com.xjx.mydns.transmit.code;

import com.xjx.mydns.constant.Constants;
import com.xjx.mydns.transmit.entity.Message;
import com.xjx.mydns.transmit.entity.Packet;
import com.xjx.mydns.transmit.entity.Question;
import com.xjx.mydns.transmit.entity.Resource;

/**
 * 结果集转换为最终的字节数组
 */
public class MessageEncoder {

    /**
     * 封装结果为数据包
     * @param message     报文头
     * @param question    问题部分
     * @param resources   回答部分
     * @return
     */
    public static byte[] encode(Message message, Question question, Resource[] resources){
        Packet packet = Packet.create(1024);
        //报文首部
        // 标志
        if(message == null){
            return null;
        }
        packet.addShort((short)message.getTransactionId());
        // 标识
        packet.addShort((short) 0x8000);
        // 问题数
        packet.addShort((short) 0x0001);
        // 回应数
        packet.addShort((short)resources.length);
        // 授权记录数
        packet.addShort((short) 0x0000);
        // 额外记录数
        packet.addShort((short) 0x0000);

        //报文查询问题部分
        // 查询名---域名
        packet.addBytes(convertDomainToBytes(question.getDomainName()));
        // 问题类型
        packet.addShort((short) 0x001);
        // 问题类
        packet.addShort((short) 0x0001);

        //回答部分
        for(Resource resource: resources){
            byte[] bytes = convertDomainToBytes(question.getDomainName());
            packet.addBytes(bytes);
            packet.addShort((short) resource.getType());
            packet.addShort((short) resource.getRdataClass());
            packet.addInt(resource.getTtl());
            packet.addShort((short) resource.getRdataLength());

            //数据部分根据不同协议类型进行添加
            if(resource.getType() == Constants.RESPONSE_IPV4){
                packet.addInt((int)(resource.getIpv4() & 0xffffffff));
            }
            else if(resource.getType() == Constants.RESPONSE_IPV6){
                packet.addBytes(resource.getData());
            }
        }
        return packet.getBytes();
    }

    /**
     * 将请求时的域名还原为字节的形式(2个字节)
     * @param domainName
     * @return
     */
    public static byte[] convertDomainToBytes(String domainName){
        // 域名的每一个字符代表一个字节,还要在最后加上一个0
        byte[] ans = new byte[domainName.length()+2];
        int numIndex=0,i=0,j=1,count=0;

        for(;i<domainName.length();j++,i++){
            char curWord = domainName.charAt(i);
            if(curWord == '.'){
                ans[numIndex] = (byte)count;
                numIndex = i+1;
                count = 0;
                continue;
            }
            ans[j] = (byte)(curWord);
            count++;
        }
        ans[numIndex] = (byte)count;
        ans[ans.length-1] = (byte)0;
        return ans;
    }
}
