package com.xjx.mydns.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xjx.mydns.constant.Constants;
import com.xjx.mydns.constant.ResultObj;
import com.xjx.mydns.entity.Rule;
import com.xjx.mydns.service.RuleService;
import com.xjx.mydns.stat.ManagerStat;
import com.xjx.mydns.stat.TargetDomainStat;
import com.xjx.mydns.stat.TargetIPAddrStat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 域名解析管理
 */
@RestController
@RequestMapping("/analysis")
@CrossOrigin
public class DomainStatController {
    @Autowired
    RuleService ruleService;

    /**
     * 记录在库的分页查询所有规则
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/domainList")
        public ResultObj findRuleList(@RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                  @RequestParam(value = "pageSize",defaultValue = "5") Integer pageSize){
        System.out.println(pageNum+":"+pageSize);
        PageInfo pageInfo = ruleService.getByPageInfo(pageNum,pageSize);
        if(pageInfo.getSize() > 0){
            return ResultObj.success(pageInfo);
        }
        return ResultObj.failure("未查到指定配置规则,请检查");
    }

    /**
     * 按照IP数量排序,展示前 TOPN 的Rule
     */
    @GetMapping("/topIPNum")
    public ResultObj findTopIpNum(@RequestParam(value = "N") Integer N){
        List<Rule> allRule = ruleService.getRuleAndAddress();
        Collections.sort(allRule, new Comparator<Rule>() {
            @Override
            public int compare(Rule o1, Rule o2) {
                return o2.getAddressList().size()-o1.getAddressList().size();
            }
        });
        List<Rule> ansList = new ArrayList<>();
        if(allRule.size() > N)
        {
            for(int i=0;i<N;i++)
                ansList.add(allRule.get(i));
        }
        else
            ansList = allRule;
        return ResultObj.success(ansList);
    }

    /**
     * 展示每分钟域名查询次数
     */
    @GetMapping("/queryPerMin")
    public ResultObj findQueryPerMin(){
        int[] perMinQueryCount = ManagerStat.getInstance().getPerMinQueryCount();
        if(perMinQueryCount.length > 0)
            return ResultObj.success(perMinQueryCount);
        else
            return ResultObj.failure("查询结果为空");
    }

    /**
     * 今日热门域名查询
     */
    @GetMapping("/queryDomainNameTop5")
    public ResultObj findDomainNameTop5(){
        List<TargetDomainStat> domainNameTop5 = ManagerStat.getInstance().getDomainNameTop5();
        return ResultObj.success(domainNameTop5);
    }

    /**
     * 今日查询来源前TOP5
     */
    @GetMapping("/queryIPAddressTop5")
    public ResultObj findIPAddressTop5(){
        List<TargetIPAddrStat> ipAddressTop5 = ManagerStat.getInstance().getIPTop5();
        return ResultObj.success(ipAddressTop5);
    }

    /**
     * 总查询量、总应答量、上游查询次数统计
     */
    @GetMapping("/allQueryNum")
    public ResultObj findAllQueryNum(){
        ManagerStat instance = ManagerStat.getInstance();
        Map<String,Integer> ans = new HashMap<>();
        ans.put(Constants.SUM_QUERY,instance.getTotalQueryCount());
        ans.put(Constants.SUM_ANSWER,instance.getTotalRespCount());
        ans.put(Constants.SUM_UPSTREAM_QUERY,instance.getTotalUpStreamCount());
        return ResultObj.success(ans);
    }

    /**
     * 今日域名访问频率表
     */
    @GetMapping("/domainNameFrequency")
    public ResultObj getTodayDomainNameFrequency(){
        ManagerStat instance = ManagerStat.getInstance();
        Map<String, Double> ans = instance.getDomainNameFrequecy(5);
        if(ans!=null && ans.size()>0)
            return ResultObj.success(ans);
        else
            return ResultObj.failure();
    }
}
