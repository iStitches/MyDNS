package com.xjx.mydns.stat;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 统计管理
 */
public class ManagerStat {
    private static volatile ManagerStat instance = null;
    // 当天总查询次数
    private AtomicInteger totalQueryCount;
    // 当天总响应次数
    private AtomicInteger totalResponseCount;
    // 当天向上游DNS服务器询问次数
    private AtomicInteger totalUpStreamServerCount;
    // 当天每分钟查询次数
    private int[] everySecQueryCount;
    // 域名对应查询情况
    private Map<String,TargetDomainStat> requestDomainMap;
    // 查询单个服务器次数排序
    private Map<String,TargetIPAddrStat> requestIPMap;
    // 查询请求序号
    int requestSequence = 0;
    int ipSequence = 0;
    ReentrantLock requestLock = new ReentrantLock();

    public ManagerStat(){
        this.totalQueryCount = new AtomicInteger(0);
        this.totalResponseCount = new AtomicInteger(0);
        this.totalUpStreamServerCount = new AtomicInteger(0);
        this.everySecQueryCount = new int[24*60];
        this.requestDomainMap = new HashMap<>();
        requestIPMap = new HashMap<>();
    }

    public static ManagerStat getInstance(){
        if(instance == null){
            synchronized (ManagerStat.class){
                if(instance == null)
                    instance = new ManagerStat();
            }
        }
        return instance;
    }

    //每天响应次数自增
    public void incrRespCount(){
        this.totalResponseCount.addAndGet(1);
    }

    //向上游查询次数自增
    public void incrUpStreamCount() {
        this.totalUpStreamServerCount.addAndGet(1);
    }

    // 请求发送成功后统计 --- 源IP地址、目标域名、查询时间
    public void recordRequest(String ip, String domainName, Date date){
        requestLock.lock();
        // 查询时刻统计
        long hours = date.getHours();
        int minutes = (int) (hours*60+date.getMinutes());
        everySecQueryCount[minutes]++;

        // 总查询统计
        totalQueryCount.addAndGet(1);

        // 域名统计
        TargetDomainStat tmp = requestDomainMap.get(domainName);
        if(tmp == null){
            tmp = new TargetDomainStat(requestSequence++,domainName);
            requestDomainMap.put(domainName,tmp);
        }
        else{
            tmp.setQueryCount(tmp.getQueryCount()+1);
        }
        recordIP(ip);
        requestLock.unlock();
    }

    // 记录请求目标服务器IP地址
    public void recordIP(String ipAddr){
        TargetIPAddrStat tmp = requestIPMap.get(ipAddr);
        if(tmp == null){
            tmp = new TargetIPAddrStat(ipSequence++,ipAddr);
            requestIPMap.put(ipAddr,tmp);
        }
        else
            tmp.setQueryCount(tmp.getQueryCount()+1);
    }

    // 返回总查询次数
    public int getTotalQueryCount() {
        return totalQueryCount.intValue();
    }

    // 返回总查询上游次数
    public int getTotalUpStreamCount() {
        return totalUpStreamServerCount.intValue();
    }

    // 返回总响应次数
    public int getTotalRespCount() {
        return totalResponseCount.intValue();
    }

    // 返回每分钟查询量
    public int[] getPerMinQueryCount() {
        Date now = new Date();
        int allMin = now.getHours()*60+now.getMinutes();
        int[] perMinQuery = new int[allMin];
        System.arraycopy(everySecQueryCount,0,perMinQuery,0,allMin);
        return perMinQuery;
    }

    // 返回今日域名查询量 TOP5
    public List<TargetDomainStat> getDomainNameTop5(){
        ArrayList<TargetDomainStat> domainNameList = new ArrayList(requestDomainMap.values());
        domainNameList.sort(new Comparator<TargetDomainStat>() {
            @Override
            public int compare(TargetDomainStat o1, TargetDomainStat o2) {
                return o2.getQueryCount()-o1.getQueryCount();
            }
        });
        List<TargetDomainStat> ans = new ArrayList<>();
        if(domainNameList.size() < 5)
            ans = domainNameList;
        else
            ans = domainNameList.subList(0,5);
        return ans;
    }

    // 今日请求查询
    public List<TargetIPAddrStat> getIPTop5(){
        ArrayList<TargetIPAddrStat> ipAddrList = new ArrayList<>(requestIPMap.values());
        ipAddrList.sort(new Comparator<TargetIPAddrStat>() {
            @Override
            public int compare(TargetIPAddrStat o1, TargetIPAddrStat o2) {
                return o2.getQueryCount()-o1.getQueryCount();
            }
        });
        ArrayList<TargetIPAddrStat> ans = new ArrayList<>();
        if(ipAddrList.size() < 5)
            ans = ipAddrList;
        else
            ans = (ArrayList<TargetIPAddrStat>) ipAddrList.subList(0,5);
        return ans;
    }

    // 今日域名查询频率排行 TOP N
    public Map<String,Double> getDomainNameFrequecy(int N){
        ArrayList<TargetDomainStat> domainList = new ArrayList(requestIPMap.values());
        domainList.sort(new Comparator<TargetDomainStat>() {
            @Override
            public int compare(TargetDomainStat o1, TargetDomainStat o2) {
                return o2.getQueryCount()-o1.getQueryCount();
            }
        });
        Map<String,Double> ans = new LinkedHashMap<>();
        for(int i=0;i<domainList.size() && i<N;i++){
            TargetDomainStat tmp = domainList.get(i);
            ans.put(tmp.getDomainName(),tmp.getQueryCount()*1.0/getTotalQueryCount());
        }
        return ans;
    }
}
