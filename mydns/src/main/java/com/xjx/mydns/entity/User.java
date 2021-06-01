package com.xjx.mydns.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    Integer id;
    String type;
    String name;
    String password;
    String salt;
    Boolean enabled;
    Date lastLoginTime;
    String lastLoginIP;
}
