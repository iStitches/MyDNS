package com.xjx.mydns.controller;

import com.xjx.mydns.constant.CommonEnum;
import com.xjx.mydns.constant.ResultObj;
import com.xjx.mydns.entity.Address;
import com.xjx.mydns.entity.Rule;
import com.xjx.mydns.exception.MyException;
import com.xjx.mydns.service.AddressService;
import com.xjx.mydns.service.RuleService;
import com.xjx.mydns.transmit.utils.RulesManager;
import com.xjx.mydns.utils.CommonUtils;
import com.xjx.mydns.utils.IPUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/manage")
@CrossOrigin
public class DomainManageController {
    @Autowired
    RuleService ruleService;
    @Autowired
    AddressService addressService;
    @Autowired
    CommonUtils commonUtils;

    /**
     * 开启、禁用解析规则
     */
    @GetMapping("/setEnable")
    public ResultObj setEnable(@RequestParam Long ruleId,
                               @RequestParam Boolean enabled){
        Rule searchRes = ruleService.getById(ruleId);
        if(searchRes == null)
            throw new MyException(CommonEnum.DATA_NOTFOUND);
        else{
            searchRes.setEnabled(enabled);
            ruleService.updateById(searchRes);
            if(enabled)
                RulesManager.getInstance().setEnabled(ruleId);
            else
                RulesManager.getInstance().disableEnabled(ruleId);
        }
        return ResultObj.success("配置成功！");
    }

    /**
     * 删除配置
     */
    @GetMapping("/deleteRule")
    @Transactional(rollbackFor = Exception.class)
    public ResultObj delteRule(@RequestParam Long ruleId){
        Rule searchRes = ruleService.getById(ruleId);
        try {
            if(searchRes == null)
                throw new MyException(CommonEnum.DATA_NOTFOUND);
            else{
                ruleService.removeByRuleId(ruleId,searchRes);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultObj.success("成功删除！");
    }

    /**
     * 获取对应配置的详细信息
     */
    @GetMapping("/getDetail")
    public ResultObj getRuleDetail(@RequestParam Long ruleId){
        Rule searchRes = ruleService.getById(ruleId);
        if(searchRes == null)
            throw new MyException(CommonEnum.DATA_NOTFOUND);
        else
            return ResultObj.success(searchRes);
    }

    /**
     * 更新配置
     */
    @GetMapping("/updateRule")
    public ResultObj updateRule(
                                @RequestParam Long ruleId,
                                @RequestParam String fromIP,
                                @RequestParam String toIP,
                                @RequestParam String fromTime,
                                @RequestParam String toTime,
                                @RequestParam String matchMode,
                                @RequestParam String ruleName,
                                @RequestParam String dispatchMode,
                                @RequestParam String addressList)
    {
        long lll= IPUtils.convertStrToLong("223.255.255.254");
        // 参数校验检查
        Rule searchRes = ruleService.getById(ruleId);
        Rule newRule = new Rule();
        newRule.setId(searchRes.getId());
        if(searchRes == null)
            throw new MyException(CommonEnum.PARAMS_FROMAT_ERROR);
        if(fromIP!=null && fromIP!=""){
            if(fromIP.matches("^(\\d{1,3})(\\.\\d{1,3}){3}$") == false)
                throw new MyException(CommonEnum.PARAMS_IP_FROMAT_ERROR);
            newRule.setIpFrom(IPUtils.convertStrToLong(fromIP));
        }
        if(toIP!=null && toIP!=""){
            if(toIP.matches("^(\\d{1,3})(\\.\\d{1,3}){3}$") == false)
                throw new MyException(CommonEnum.PARAMS_IP_FROMAT_ERROR);
            newRule.setIpTo(IPUtils.convertStrToLong(toIP));
        }
        if(fromTime!=null && fromTime!=""){
            if(fromTime.matches("^(\\d{2}:\\d{2}:\\d{2})$") == false)
                throw new MyException(CommonEnum.PARAMS_TIME_FORMAT_ERROR);
            newRule.setTimeFrom(commonUtils.parseTimeFormat(fromTime));
        }
        if(toTime!=null && toTime!=""){
            if(toTime.matches("^(\\d{2}:\\d{2}:\\d{2})$") == false)
                throw new MyException(CommonEnum.PARAMS_TIME_FORMAT_ERROR);
            newRule.setTimeTo(commonUtils.parseTimeFormat(toTime));
        }
        if(!matchMode.equals("prefix") && !matchMode.equals("suffix") && !matchMode.equals("contains"))
            throw new MyException(CommonEnum.PARAMS_MATCHMODE_ERROR);
        newRule.setMatchMode(matchMode);
        if(!dispatchMode.equals("random") && !dispatchMode.equals("hash") && !dispatchMode.equals("round-robin"))
            throw new MyException(CommonEnum.PRAMAS_DISPATCHMODE_ERROR);
        newRule.setDispatchMode(dispatchMode);
        if(ruleName==null || ruleName=="")
            throw new MyException(CommonEnum.PRAMAS_RULENAME_ERROR);
        newRule.setRuleName(ruleName);
        newRule.setPriority(0);
        newRule.setEnabled(true);

        // IP地址参数校验检查
        String[] ipLists = addressList.split("\n");
        List<Address> addresses = new ArrayList<>();
        Long index = 0L;
        for(String tmpIP: ipLists){
            if(tmpIP!=null && tmpIP!=""){
                if(tmpIP.matches("^(\\d{1,3})(\\.\\d{1,3}){3}$") == false){
                    throw new MyException(CommonEnum.PARAMS_IP_FROMAT_ERROR);
                }
                addresses.add(new Address(index++,searchRes.getId(),"ipv4",tmpIP));
            }
        }
        if(addresses.size() == 0)
            throw new MyException("400100","IP地址列表不能为空,请至少输入一个IP地址");
        newRule.setAddressList(addresses);

        // 内存更新、数据库更新、缓存更新
        ruleService.delAndUpdateRule(searchRes,newRule);
        searchRes.setMatchMode(matchMode);
        searchRes.setDispatchMode(dispatchMode);
        return ResultObj.success("成功");
    }

    /**
     * 新增规则
     * @param fromIP
     * @param toIP
     * @param fromTime
     * @param toTime
     * @param matchMode
     * @param ruleName
     * @param dispatchMode
     * @param addressList
     * @return
     */
    @GetMapping("/addRule")
    public ResultObj addRule(
            @RequestParam String fromIP,
            @RequestParam String toIP,
            @RequestParam String fromTime,
            @RequestParam String toTime,
            @RequestParam String matchMode,
            @RequestParam String ruleName,
            @RequestParam String dispatchMode,
            @RequestParam String addressList){
        // 参数校验检查
        Rule newRule = new Rule();
        if(fromIP!=null && fromIP!=""){
            if(fromIP.matches("^(\\d{1,3})(\\.\\d{1,3}){3}$") == false)
                throw new MyException(CommonEnum.PARAMS_IP_FROMAT_ERROR);
            newRule.setIpFrom(IPUtils.convertStrToLong(fromIP));
        }
        if(toIP!=null && toIP!=""){
            if(toIP.matches("^(\\d{1,3})(\\.\\d{1,3}){3}$") == false)
                throw new MyException(CommonEnum.PARAMS_IP_FROMAT_ERROR);
            newRule.setIpTo(IPUtils.convertStrToLong(toIP));
        }
        if(fromTime!=null && fromTime!=""){
            if(fromTime.matches("^(\\d{2}:\\d{2}:\\d{2})$") == false)
                throw new MyException(CommonEnum.PARAMS_TIME_FORMAT_ERROR);
            newRule.setTimeFrom(commonUtils.parseTimeFormat(fromTime));
        }
        if(toTime!=null && toTime!=""){
            if(toTime.matches("^(\\d{2}:\\d{2}:\\d{2})$") == false)
                throw new MyException(CommonEnum.PARAMS_TIME_FORMAT_ERROR);
            newRule.setTimeTo(commonUtils.parseTimeFormat(toTime));
        }
        if(!matchMode.equals("prefix") && !matchMode.equals("suffix") && !matchMode.equals("contains"))
            throw new MyException(CommonEnum.PARAMS_MATCHMODE_ERROR);
        newRule.setMatchMode(matchMode);
        if(!dispatchMode.equals("random") && !dispatchMode.equals("hash") && !dispatchMode.equals("round-robin"))
            throw new MyException(CommonEnum.PRAMAS_DISPATCHMODE_ERROR);
        newRule.setDispatchMode(dispatchMode);
        if(ruleName==null || ruleName=="")
            throw new MyException(CommonEnum.PRAMAS_RULENAME_ERROR);
        newRule.setRuleName(ruleName);
        newRule.setPriority(0);
        newRule.setEnabled(true);
        Boolean res = ruleService.addNewRuleAndAddressList(newRule, addressList);
        if(res == true)
            return ResultObj.success("配置新增成功");
        else
            return ResultObj.failure("配置新增失败,请检查重试");
    }
}
