package com.xjx.mydns.stat;

import lombok.Data;

@Data
public class TargetIPAddrStat {
    //序号
    private int id;
    //查询来源IP
    private String serverIP;
    //目标域名总查询次数
    private int queryCount;

    public TargetIPAddrStat(int id, String serverIP){
        this.id = id;
        this.serverIP = serverIP;
        this.queryCount = 1;
    }
}
