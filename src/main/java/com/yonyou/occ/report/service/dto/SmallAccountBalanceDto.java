package com.yonyou.occ.report.service.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 小额账余报表
 *
 */
@Data
public class SmallAccountBalanceDto {
    //客户编码
    private String customerCode;

    //客户名称
    private String customerName;

    //政策（即活动）编码
    private String activityCode;

    //政策（即活动）名称
    private String activityName;

    //促销方式
    private Integer promWay;

    private String promWayName;

    //合同金额
    private BigDecimal activityBalance;

    //未清金额
    private BigDecimal outstandingBalance;
}
