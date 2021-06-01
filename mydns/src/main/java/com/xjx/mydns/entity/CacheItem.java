package com.xjx.mydns.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 缓存在Redis中的值
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CacheItem {
    private String ipAddress;
    private long expireTime;
}
