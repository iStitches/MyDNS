package com.xjx.mydns.service.impl;
import com.github.pagehelper.Constant;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xjx.mydns.constant.CommonEnum;
import com.xjx.mydns.constant.Constants;
import com.xjx.mydns.dao.RuleDao;
import com.xjx.mydns.entity.Address;
import com.xjx.mydns.entity.CacheItem;
import com.xjx.mydns.entity.Rule;
import com.xjx.mydns.exception.MyException;
import com.xjx.mydns.rabbitmq.MessageSender;
import com.xjx.mydns.service.AddressService;
import com.xjx.mydns.service.RuleService;
import com.xjx.mydns.transmit.entity.RuleVo;
import com.xjx.mydns.transmit.utils.RulesManager;
import com.xjx.mydns.utils.Configs;
import com.xjx.mydns.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.callback.Callback;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class RuleServiceImpl implements RuleService {
    @Autowired
    RuleDao ruleDao;
    @Autowired
    RedisUtils redisUtils;
    @Autowired
    MessageSender messageSender;
    @Autowired
    AddressService addressService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int removeByRuleId(Long id,Rule oldRule) {
        if(id!=null) {
            int line = ruleDao.deleteByPrimaryKey(id);
            if(line != 0){
                // 删除内存、数据库、缓存
                RulesManager.getInstance().deleteRule(oldRule);
                RuleVo oldRuleVo = new RuleVo(oldRule.getRuleName(),oldRule.getMatchMode(),oldRule.getDispatchMode());
                redisUtils.zsetRemove(Constants.DNS_RULE_VO,oldRuleVo);
                redisUtils.delList(oldRule.getRuleName());
            }
        }
        return 0;
    }

    @Override
    public int updateById(Rule rule) {
        if(rule != null)
            return ruleDao.updateByPrimaryKey(rule);
        return 0;
    }

    @Override
    public Rule getById(Long id) {
        return ruleDao.selectByPrimaryKey(id);
    }

    @Override
    public List<Rule> getRuleAndAddress() {
        List<Rule> ruleList = ruleDao.selectRuleAndAddress();
        return ruleList;
    }

    @Override
    public PageInfo getByPageInfo(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Rule> rules = ruleDao.selectRuleList();
        PageInfo pageInfo = new PageInfo(rules);
        return pageInfo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delAndUpdateRule(Rule oldRule, Rule newRule) {
        // 更新内存
        RulesManager instance = RulesManager.getInstance();
        if(oldRule!=null && newRule!=null){
            instance.deleteRule(oldRule);
            instance.addRule(newRule);
        }
        // 延迟双删：先删除缓存--->更新DB--->异步延迟再删缓存(加入重试机制,保证一定能够删除完毕)
        RuleVo oldRuleVo = new RuleVo(oldRule.getRuleName(),oldRule.getMatchMode(),oldRule.getDispatchMode());
        redisUtils.zsetRemove(Constants.DNS_RULE_VO,oldRuleVo);
        redisUtils.delList(oldRule.getRuleName());
        ruleDao.updateByPrimaryKey(newRule);
        try {
            messageSender.sendMessage(oldRuleVo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 添加新的规则和IP地址集合
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addNewRuleAndAddressList(Rule newRule, String addressList) {
        Long ruleId = -1L;
        int newId = ruleDao.insert(newRule);
        if(newId > 0){
            RuleVo newRuleVo = new RuleVo(newRule.getRuleName(),newRule.getMatchMode(),newRule.getDispatchMode());
            redisUtils.zsetAdd(Constants.DNS_RULE_VO,newRuleVo,new Date().getTime()/1000+ Configs.getInt("spring.redis.dns-domain-max-lifeTime"));
            ruleId = newRule.getId();
        }
        if(ruleId == -1)
            throw new MyException(CommonEnum.INNER_ERROR);

        // IP地址参数校验检查
        String[] ipLists = addressList.split("\n");
        List<Address> addresses = new ArrayList<>();
        for(String tmpIP: ipLists){
            if(tmpIP!=null && tmpIP!=""){
                if(tmpIP.matches("^(\\d{1,3})(\\.\\d{1,3}){3}$") == false){
                    throw new MyException(CommonEnum.PARAMS_IP_FROMAT_ERROR);
                }
                addresses.add(new Address(ruleId,"ipv4",tmpIP));
            }
        }
        if(addresses.size() == 0)
            throw new MyException("400100","IP地址列表不能为空,请至少输入一个IP地址");

        // IP地址列表更新数据库、缓存
        addressService.createAddressList(newRule.getRuleName(),addresses);

        // 更新内存
        newRule.setAddressList(addresses);
        RulesManager.getInstance().addRule(newRule);
        return true;
    }


}
