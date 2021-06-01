package com.xjx.mydns.transmit.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DNS报文查询问题部分
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Question {
    //域名
    String domainName;
    //问题类型
    int questionType;
    //问题所属的类
    int questionClass;
}
