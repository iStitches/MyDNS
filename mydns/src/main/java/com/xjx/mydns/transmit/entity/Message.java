package com.xjx.mydns.transmit.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DNS报文头
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    /**
     * Header 报文头字段：
     *    标识(2字节)          标志(2字节):QR、QP、rCode
     *    问题记录数(2字节)     回答记录数(2字节)
     *    授权记录数(2字节)     附加记录数(2字节)
     * Question 问题字段：
     *    问题名(就是域名,长度不限)
     *    问题类型(2字节)
     *    问题类(2字节)
     * Answer 回答字段：
     *    域名(就是请求报文中的域名)
     *    DNS协议类型
     *    RDATA类
     *    生存时间 TTL
     *    资源数据长度 RDLENGTH
     *    资源数据 RDATA(存放的是最终识别的IP地址)
     * Authority 授权字段
     * Additional 附加字段
     */

    //标识字段
    public int transactionId;
    //标志字段
    public int flags;
    //问题数
    public int questionNum;
    //回答字段
    public int answerNum;
    //授权资源字段
    public int authorityNum;
    //附加资源字段
    public int additionalNum;

    //标志字段中的QR字段为0
    public boolean isQueryMessage(){
        return (flags & 0x8000)==0;
    }
}
