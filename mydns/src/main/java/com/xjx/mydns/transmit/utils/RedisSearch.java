package com.xjx.mydns.transmit.utils;

import com.xjx.mydns.constant.Constants;
import com.xjx.mydns.transmit.entity.RuleVo;
import com.xjx.mydns.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 内存没有,查询缓存
 *   1. 先查询 匹配模式、域名格式
 *   2. 参照LRU算法实现查询
 */
@Component
public class RedisSearch {
    @Autowired
    RedisUtils redisUtils;

    /**
     * 从Redis获取IP
     * @param domainName
     * @return
     */
    public String getRuleModel(String domainName){
        List<RuleVo> ruleList = redisUtils.zsetGet(Constants.DNS_RULE_VO, RuleVo.class);
        for(RuleVo ruleVo: ruleList){
            String matchMode = ruleVo.getMatchMode();
            String ruleName = ruleVo.getRuleName();
            String dispatchMode = ruleVo.getDispatchMode();
            if(isMatch(matchMode,ruleName,domainName)){
                return chooseByDispatchMode(dispatchMode,ruleName);
            }
        }
        return null;
    }

    public Boolean isMatch(String  matchMode, String ruleName, String domainName){
        switch(matchMode){
            case "suffix": return domainName.endsWith(ruleName);
            case "prefix": return domainName.startsWith(ruleName);
            case "contains": return domainName.contains(ruleName);
            default: return false;
        }
    }

    public String chooseByDispatchMode(String dispatchMode, String ruleName){
        String ans = null;
        if(dispatchMode.equals("random"))
            ans = redisUtils.randomGet(ruleName);
        else if(dispatchMode.equals("round-robin"))
            ans = redisUtils.sequenceGet(ruleName);
        else if(dispatchMode.equals("hash"))
            ans = redisUtils.sequenceGet(ruleName);
        return ans;
    }
}
