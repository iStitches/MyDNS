package com.xjx.mydns.service;

import com.xjx.mydns.entity.Address;
import com.xjx.mydns.entity.Rule;

import java.util.List;

public interface AddressService {
    public Long create(Address address);

    public void createAddressList(String ruleName, List<Address> list);

    public int remove(Address address);

    public int update(Address address);

    public List<Address> getByRuleId(Long ruleId);

    public int removeByRule(Rule rule);

    public List<Address> getAllAddress();
}
