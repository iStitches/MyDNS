package com.xjx.mydns.transmit.code;

import com.xjx.mydns.transmit.entity.Message;
import com.xjx.mydns.transmit.entity.Packet;

/**
 * 数据包报文头解码工具类
 */
public class MessageDecoder{
     public static Message decodeMessage(Packet packet){
         Message message = new Message();
         message.transactionId = packet.getNextShort();
         message.flags = packet.getNextShort();
         message.questionNum = packet.getNextShort();
         message.answerNum = packet.getNextShort();
         message.authorityNum = packet.getNextShort();
         message.additionalNum = packet.getNextShort();
         return message;
     }
}
