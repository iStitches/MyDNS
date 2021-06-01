package com.xjx.mydns.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    Long id;
    Long ruleId;
    String type;
    String address;

    public Address(Long ruleId,String type,String address){
        this.ruleId = ruleId;
        this.type = type;
        this.address = address;
    }
}
