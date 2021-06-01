package com.xjx.mydns.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rule {
    Long id;
    Long ipFrom;
    Long ipTo;
    Integer timeFrom;
    Integer timeTo;
    String matchMode;
    String ruleName;
    Integer priority;
    Boolean enabled;
    String dispatchMode;
    //1个 Rule 可能对应多个 Address
    List<Address> addressList;
}
