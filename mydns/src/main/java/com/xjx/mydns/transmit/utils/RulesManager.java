package com.xjx.mydns.transmit.utils;

import com.xjx.mydns.constant.Constants;
import com.xjx.mydns.entity.Address;
import com.xjx.mydns.entity.CacheItem;
import com.xjx.mydns.entity.Rule;
import com.xjx.mydns.service.RuleService;

import com.xjx.mydns.transmit.entity.RuleVo;
import com.xjx.mydns.utils.BeanUtils;
import com.xjx.mydns.utils.Configs;
import com.xjx.mydns.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 进行Rule 和 Address的匹配
 */

@Slf4j
public class RulesManager {
    static volatile RulesManager instance = null;
    private ConcurrentLinkedQueue<Rule> rules = new ConcurrentLinkedQueue<>();
    private RuleService ruleService = BeanUtils.createBean(RuleService.class);
    private RedisUtils redisUtils = BeanUtils.createBean(RedisUtils.class);
    private Random random = new Random();
    private int sequence = 0;

    public static RulesManager getInstance(){
        if(instance == null){
            synchronized (RulesManager.class){
                if(instance == null)
                    instance = new RulesManager();
            }
        }
        return instance;
    }

    // 内存、缓存预热
    public void init(){
        List<Rule> allRules = ruleService.getRuleAndAddress();
        long now = System.currentTimeMillis()/1000;
        for(Rule rule: allRules){
            rules.add(rule);
            // 加载缓存---zset数据结构存储
            int anInt = Configs.getInt("spring.redis.dns-domain-max-lifeTime");
            redisUtils.zsetAdd(Constants.DNS_RULE_VO,new RuleVo(rule.getRuleName(),rule.getMatchMode(),rule.getDispatchMode()), now+Configs.getInt("spring.redis.dns-domain-max-lifeTime"));
            for(Address address: rule.getAddressList()){
                redisUtils.setList(rule.getRuleName(),new CacheItem(address.getAddress(),now+Configs.getInt("spring.redis.dns-id-max-lifeTime")));
            }
            log.info("RedisStorage success load 1 Rules and {} IP",rule.getAddressList().size());
        }
        log.info("InnerStorage success load {} rules",rules.size());
    }

    /**
     * 域名匹配
     * @param now  发送请求的时间
     * @param ip   发送方的IP地址
     * @param domainName   需要匹配的域名
     * @return
     */
    public Address matchRules(int now, long ip, String domainName){
        Iterator<Rule> iterator = rules.iterator();

        while(iterator.hasNext()){
            Rule tmpRule =  iterator.next();
            if(tmpRule.getEnabled() && matchOne(tmpRule,now,ip,domainName)){
                // 找到匹配的 Address集合
                List<Address> addressList = tmpRule.getAddressList();
                return chooseDispatchMode(ip,tmpRule.getDispatchMode(),addressList);
            }
        }
        return null;
    }

    // Rule是否匹配
    public boolean matchOne(Rule rule, int now, long ip, String domainName){
        String matchMode = rule.getMatchMode();
        String ruleName = rule.getRuleName();

        //域名匹配
        if(matchMode.equals("suffix")){
            return domainName.endsWith(ruleName);
        }
        else if(matchMode.equals("prefix"))
            return domainName.startsWith(ruleName);
        else if(matchMode.equals("equals"))
            return domainName.equals(ruleName);
        else if(matchMode.equals("contains"))
            return domainName.contains(ruleName);

        //查询时间匹配
        else if(rule.getTimeFrom()>now || rule.getTimeTo()<now)
            return false;

        //源IP地址匹配
        else if(rule.getIpFrom()>ip || rule.getIpTo()<ip)
            return false;

        return false;
    }

    // 根据IP地址分发模式进行分发
    public Address chooseDispatchMode(long senderIp, String mode, List<Address> addresses){
        int index = 0;
        Address ans = null;

        //随机匹配
        if(mode.equals("random")){
            int location = (random.nextInt() & 0x7fffffff) % addresses.size();
            ans = addresses.get(location);
        }
        //轮循匹配
        else if(mode.equals("round-robin")){
            ans = addresses.get(sequence);
            this.sequence = ((this.sequence+1) & 0x7fffffff)%addresses.size();
            System.out.println("###################################################");
            System.out.println("Sequence："+this.sequence);

        }
        //hash匹配
        else if(mode.equals("hash")){
            ans = addresses.get((int) (senderIp & 0x7fffffff) % addresses.size());
        }
        return ans;
    }

    // 允许配置规则
    public void setEnabled(Long ruleId){
        for(Rule rule: rules){
            if(rule.getId() == ruleId)
                rule.setEnabled(true);
        }
    }

    // 禁止配置规则
    public void disableEnabled(Long ruleId){
        for(Rule rule: rules){
            if(rule.getId() == ruleId)
                rule.setEnabled(false);
        }
    }

    // 删除配置规则
    public void deleteRule(Rule rule){
        rules.remove(rule);
    }

    // 新增Rule，先删后增
    public void addRule(Rule newRule) {
        rules.add(newRule);
    }
}
