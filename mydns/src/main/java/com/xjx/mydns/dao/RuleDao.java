package com.xjx.mydns.dao;
import com.xjx.mydns.entity.Rule;

import java.util.List;

public interface RuleDao {
     List<Rule> selectRuleAndAddress();

     List<Rule> selectRuleList();

     Rule selectByPrimaryKey(long id);

     int insert(Rule rule);

     int deleteByPrimaryKey(long id);

     int updateByPrimaryKey(Rule rule);
}
