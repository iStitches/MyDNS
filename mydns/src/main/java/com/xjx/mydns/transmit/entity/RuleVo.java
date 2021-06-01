package com.xjx.mydns.transmit.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Redis中缓存的规则对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RuleVo implements Serializable {
    private String ruleName;     //域名格式
    private String matchMode;    //匹配模式
    private String dispatchMode; //分发模式
}
