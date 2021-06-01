package com.xjx.mydns.service.impl;

import com.xjx.mydns.dao.AddressDao;
import com.xjx.mydns.entity.Address;
import com.xjx.mydns.entity.CacheItem;
import com.xjx.mydns.entity.Rule;
import com.xjx.mydns.service.AddressService;
import com.xjx.mydns.utils.Configs;
import com.xjx.mydns.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    AddressDao addressDao;
    @Autowired
    RedisUtils redisUtils;

    @Override
    public Long create(Address address) {
        int newId = addressDao.insert(address);
        return (long)newId;
    }

    @Override
    @Transactional
    public void createAddressList(String ruleName, List<Address> list) {
        addressDao.batchInsert(list);
        long now = new Date().getTime()/1000+ Configs.getInt("spring.redis.dns-id-max-lifeTime");
        for(Address address:list)
            redisUtils.setList(ruleName,new CacheItem(address.getAddress(),now));
    }

    @Override
    public int remove(Address address) {
        return addressDao.deleteByPrimaryKey(address.getId());
    }

    @Override
    public int update(Address address) {
        return addressDao.updateByPrimaryKey(address);
    }

    @Override
    public List<Address> getByRuleId(Long ruleId) {
        return addressDao.getByRuleId(ruleId);
    }

    @Override
    public int removeByRule(Rule rule) {
        return addressDao.deleteByPrimaryKey(rule.getId());
    }

    @Override
    public List<Address> getAllAddress() {
        return addressDao.selectList();
    }
}
