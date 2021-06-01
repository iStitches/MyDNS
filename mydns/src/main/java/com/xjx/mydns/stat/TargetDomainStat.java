package com.xjx.mydns.stat;

import lombok.Data;

/**
 * Request请求目标域名次数统计
 */
@Data
public class TargetDomainStat {
    //序号
    private int id;
    //目标域名
    private String domainName;
    //目标域名总查询次数
    private int queryCount;

    public TargetDomainStat(int id, String domainName){
        this.id = id;
        this.domainName = domainName;
        this.queryCount = 1;
    }
}
