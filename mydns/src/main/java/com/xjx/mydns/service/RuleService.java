package com.xjx.mydns.service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.xjx.mydns.entity.Rule;

import java.util.List;

public interface RuleService {

    public int removeByRuleId(Long id,Rule oldRule);

    public int updateById(Rule rule);

    public Rule getById(Long id);

    public List<Rule> getRuleAndAddress();

    PageInfo getByPageInfo(Integer pageNum, Integer pageSize);

    public Boolean delAndUpdateRule(Rule oldRule, Rule newRule);

    public Boolean addNewRuleAndAddressList(Rule newRule, String addressList);
}
