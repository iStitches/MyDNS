package com.xjx.mydns;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xjx.mydns.dao.AddressDao;
import com.xjx.mydns.entity.Address;
import com.xjx.mydns.utils.RedisUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest()
@RunWith(SpringRunner.class)
class MydnsApplicationTests {

    @Autowired
    AddressDao addressDao;
    @Test
    void contextLoads() {
//        redisUtils.setKey("1234","aaaa");
        PageHelper.startPage(2,4);
        List<Address> addresses = addressDao.selectList();
        PageInfo pageInfo = new PageInfo(addresses);
        System.out.println(pageInfo);
    }

}
