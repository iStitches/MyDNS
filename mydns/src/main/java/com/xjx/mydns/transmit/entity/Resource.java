package com.xjx.mydns.transmit.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DNS报文回答部分
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Resource {
    //域名
    String domainName;
    //DNS协议类型,  IPV4为1,IPV6为28
    int type;
    //RDATA的类
    int rdataClass = 1;
    //生存时间,报文有效跳数,考虑到负载均衡设置为5分钟最为合适
    int ttl;
    //资源数据长度,一般为4字节
    int rdataLength = 4;
    //计算后long表示的IP地址
    long ipv4;
    //资源数据 --- 存储的就是IP地址,但是需要转换为字节形式存储
    byte[] data;

    public Resource(String domainName, int type, int ttl, byte[] data){
        this.domainName = domainName;
        this.type = type;
        this.ttl  = ttl;
        this.data = data;
    }

    public Resource(String domainName, int type, int ttl, long ipv4){
        this.domainName = domainName;
        this.type= type;
        this.ttl = ttl;
        this.ipv4 = ipv4;
    }
}
