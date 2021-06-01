package com.xjx.mydns;

import com.xjx.mydns.entity.Rule;
import com.xjx.mydns.service.RuleService;
import com.xjx.mydns.transmit.query.DomainNameServer;
import com.xjx.mydns.transmit.query.UpStreamServer;
import com.xjx.mydns.transmit.utils.RulesManager;
import com.xjx.mydns.utils.BeanUtils;
import com.xjx.mydns.utils.Configs;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import sun.security.krb5.Config;

import java.util.List;

@MapperScan(value = "com.xjx.mydns.dao")
@SpringBootApplication
public class MydnsApplication {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(MydnsApplication.class, args);
        //装载上下文
        BeanUtils.init(applicationContext);
        //加载配置
        Configs.init(applicationContext);
        //启动RulesManager
        RulesManager.getInstance().init();
        //启动DomainNameServer
        DomainNameServer.getInstance().init();
        //启动UpStreamServer
        UpStreamServer.getInstance().init();
    }
}
