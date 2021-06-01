package com.xjx.mydns.entity;

import com.alibaba.fastjson.JSONObject;

import java.util.Set;

/**
 * 一对多处理
 */
public class One2Many extends JSONObject {
     private Set<JSONObject> addresses;

     public One2Many(){

     }
}
