package com.xjx.mydns.dao;

import com.xjx.mydns.entity.Address;

import java.util.List;

public interface AddressDao {
    List<Address> selectList();

    List<Address> getByRuleId(long ruleId);

    Address selectByPrimaryKey(long id);

    int insert(Address address);

    void batchInsert(List<Address> addresses);

    int deleteByPrimaryKey(long id);

    int updateByPrimaryKey(Address address);

}
