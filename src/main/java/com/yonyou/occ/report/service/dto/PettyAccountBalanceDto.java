package com.yonyou.occ.report.service.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 小额账余报表
 * @author lsl
 */
@Data
public class PettyAccountBalanceDto {

    /**
     * 活动主键
     */
    private String ctDetailId;

    /**
     * 客户编码
     */
    private String customerCode;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 政策（即活动）编码
     */
    private String activityCode;

    /**
     * 政策（即活动）名称
     */
    private String activityName;

    /**
     * 促销方式
     */
    private Integer promWay;

    /**
     * 促销方式名称
     */
    private String promWayName;

    /**
     *     规则描述
     */
    private String ruleDesc;

    /**
     *    规则-方式名称
     */
    private String ruleModeName;

    /**
     *     是否必选
     */
    private Integer isRequire;

    private String whetherRequired;

    /**
     *     活动余额
     */

    private BigDecimal activityBalance;

    /**
     * 必选未清金额
     */
    private BigDecimal requireUnusedAmount;

    /**
     * 可选未清金额
     */
    private BigDecimal optionUnusedAmount;

    /**
     * 未清金额（显示已到边界值的政策余额）
     */
    private BigDecimal outstandingAmount;

    /**
     * 边界值
     */
    private BigDecimal boundaryValue;
}
