package com.xjx.mydns.constant;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Constants {
    //DNS服务器相关
    public static String MY_DNS_ServerIP = "dns.my.server.ip";
    public static String MY_DNS_ServerPORT = "dns.my.server.port";
    public static String MY_DNS_ServerNAME = "dns.my.server.name";
    public static String UPSTREAM_DNS_ServerIP = "dns.upstream.server.ip";
    public static String UPSTREAM_DNS_ServerPORT = "dns.upstream.server.port";
    public static int MY_DNS_RESPONSE_TTL = 300;

    //DNS报文格式相关
    //标志字段 QR
    public static final int QR_QUERY = 1;
    public static final int QR_ANSWER = 1;
    //标志字段 QP
    public static final int OPCODE_STANDARD = 0;
    public static final int OPCODE_REVERSE = 1;
    //标志字段 rCode
    public static final int RCODE_SUCCESS = 0;
    public static final int RCODE_FORMAT_ERROR = 1;
    public static final int RCODE_SERVER_ERROR = 2;
    public static final int RCODE_NAME_ERROR = 3;
    //响应报文字段
    public static final int RESPONSE_IPV4 = 1;
    public static final int RESPONSE_IPV6 = 28;
    public static final int RESOURCE_LEN_IPV4 = 4;
    public static final int RESOURCE_LEN_IPV6 = 16;

    //缓存域名规则
    public static final String DNS_RULE_VO = "dns.rule";

    //Controller返回
    public static final String SUM_QUERY = "dns_sum_query";
    public static final String SUM_ANSWER = "dns_sum_answer";
    public static final String SUM_UPSTREAM_QUERY = "dns_sumUpstream_query";

    //解析规则管理
    public static final Long MIN_FROM_IP = 16777217L;
    public static final Long MAX_TO_IP = 3756122592L;
    public static final Integer MIN_FROM_TIME = 0;
    public static final Integer MAX_TO_TIME = 235959;
}
