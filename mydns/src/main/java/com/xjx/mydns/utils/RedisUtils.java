package com.xjx.mydns.utils;

import com.alibaba.fastjson.JSONObject;
import com.xjx.mydns.entity.CacheItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * Redis工具类
 */
@Component
public class RedisUtils {
    @Autowired
    RedisTemplate<String,Object> redisTemplate;
    @Autowired
    CommonUtils commonUtils;
    Random random = new Random();

    public long len(String listName){
        Long ans = redisTemplate.opsForList().size(listName);
        return ans==null?0L:ans;
    }

    /**
     * 存储 CacheItem
     * @param domainName
     * @param cacheItem
     * @return
     */
    public void setList(String domainName, CacheItem cacheItem){
        while(redisTemplate.opsForList().size(domainName) >= Configs.getInt("spring.redis.dns-ip-max-num")){
            redisTemplate.opsForList().leftPop(domainName);
        }
        redisTemplate.opsForList().rightPush(domainName,cacheItem);
    }

    /**
     * 删除指定位置的 CacheItem
     * @param domainName  list名称
     * @param cacheItem   待删除的值
     */
    public void del(String domainName, CacheItem cacheItem){
        redisTemplate.opsForList().remove(domainName,1,cacheItem);
    }

    /**
     * list 删除整个集合
     */
    public Boolean delList(String listName){
        Boolean ans = redisTemplate.delete(listName);
        return ans;
    }

    /**
     * 判断 CacheItem 是否过期
     * @param cacheItem
     * @return
     */
    public boolean isExpire(CacheItem cacheItem){
        String now = String.valueOf(new Date().getTime()/1000);
        if(String.valueOf(cacheItem.getExpireTime()).compareTo(now) >= 0)
            return true;
        return false;
    }

    /**
     * 更新 CacheItem
     */
    public void flush(String domainName, CacheItem cacheItem){
        //删除
        redisTemplate.opsForList().remove(domainName,1,cacheItem);
        // 更新
        long now = System.currentTimeMillis()/1000;
        cacheItem.setExpireTime(now+Configs.getInt("spring.redis.dns-id-max-lifeTime"));
    }

    /**
     * 顺序获取
     * @param domainName
     * @return
     */
    public String sequenceGet(String domainName){
        String ans = null;
        CacheItem tmp = null;
        if(len(domainName) != 0L) {
            //查找到第一个没有过期的CacheItem
            do{
                if(tmp != null){
                    del(domainName,tmp);
                }
                tmp = CommonUtils.linkedHashMapToBean((LinkedHashMap<String, Object>) redisTemplate.opsForList().range(domainName,0,0).get(0),CacheItem.class);
            }while(!isExpire(tmp));
            ans = tmp.getIpAddress();
            // 删除并更新
            flush(domainName,tmp);
            // 放到右边
            setList(domainName,tmp);
        }
        return ans;
    }

    /**
     * 随机获取
     * @param domainName
     * @return
     */
    public String randomGet(String domainName){
        String ans = null;
        CacheItem tmp = null;
        int location = 0;
        do{
            if(tmp != null)
                del(domainName,tmp);
            location = (int) ((random.nextInt() & 0x7fffffff) % len(domainName));
            List<Object> list = redisTemplate.opsForList().range(domainName, location, location);
            tmp = CommonUtils.linkedHashMapToBean((LinkedHashMap<String, Object>) list.get(0),CacheItem.class);
        }while (!isExpire(tmp));

        ans = tmp.getIpAddress();
        flush(domainName,tmp);
        setList(domainName,tmp);
        return ans;
    }

    /**
     * Hash获取
     * @param domainName
     * @return
     */
    public String hashGet(String domainName, long senderIP){
        String ans = null;
        CacheItem tmp = null;
        do{
            if(tmp != null)
                del(domainName,tmp);
            int location = (int) ((senderIP & 0x7fffffff) % len(domainName));
            List<Object> list = redisTemplate.opsForList().range(domainName, location, location);
            tmp = CommonUtils.linkedHashMapToBean((LinkedHashMap<String, Object>) list.get(0),CacheItem.class);
        }while (!isExpire(tmp));
        ans = tmp.getIpAddress();
        flush(domainName,tmp);
        setList(domainName,tmp);
        return ans;
    }

    /**
     * String 类型数据获取
     * @param name
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getOneBean(String name, Class clazz, Class objClazz){
        Object obj = redisTemplate.opsForValue().get(name);
        if(clazz == int.class)
            return (T)obj;
        else if(clazz == String.class)
            return (T)obj;
        else if(clazz == Long.class)
            return (T)obj;
        else
            return (T)JSONObject.parseObject((String)obj,objClazz);
    }

    /**
     * zSet 类数据存储 --- 可设置过期时间(score)
     * @param name
     * @param obj
     * @param expireTime
     */
    public void zsetAdd(String name, Object obj, long expireTime){
        redisTemplate.opsForZSet().add(name, obj, expireTime);
    }

    /**
     * zset 类型数据判断是否过期
     * @param name      zset类 key值
     * @param nowTime   当前时间(秒)
     * @return
     */
    public boolean zsetExpire(String name, long nowTime){
        redisTemplate.opsForZSet().removeRangeByScore(name, 0, nowTime);
        return true;
    }

    /**
     * 判断对应的obj项是否过期
     * @param name
     * @param obj
     * @return
     */
    public boolean zsetisExpire(String name, Object obj){
        Long index = redisTemplate.opsForZSet().rank(name, obj);
        if(index != null)
            return false;
        return true;
    }

    /**
     * zset 删除数据
     * @param name
     * @param obj
     * @return
     */
    public boolean zsetRemove(String name, Object obj){
        if(zsetisExpire(name,obj) == true)
            return false;
        Long line = redisTemplate.opsForZSet().remove(name, obj);
        if(line > 0)
            return true;
        return false;
    }

    /**
     * zset 类数据获取 --- score为过期时间
     * @param name
     * @param objClazz
     * @param <T>
     * @return
     */
    public <T> List<T> zsetGet(String name, Class<T> objClazz){
        // 获取前先进行一次过期清理
        long now = new Date().getTime()/1000;
        zsetExpire(name,now);
        // 获取所有 RuleVo对象
        Set<Object> members = redisTemplate.opsForZSet().range(name,0,-1);
        List<T> ans = new ArrayList<>();
        Iterator iter = members.iterator();
        while(iter.hasNext()){
            LinkedHashMap<String,Object> valueMap = (LinkedHashMap<String, Object>) iter.next();
            T t = commonUtils.linkedHashMapToBean(valueMap,objClazz);
            if(t != null)
                ans.add(t);
        }
        return ans;
    }

}
