package com.yonyou.occ.report.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 客户政策账余明细-报表DTO类
 *
 * @author Davis Tang
 * @date 2019-08-16
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
public class CustPolicyAccountDetailDto {

    /**
     * 客户分类名称
     */
    private String customerCategoryName;

    /**
     * 市场区域名称
     */
    private String marketAreaName;

    /**
     * 客户编码
     */
    private String customerCode;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 活动有效期
     */
    private String activityValidPeriod;

    /**
     * 活动编码
     */
    private String activityCode;

    /**
     * 活动名称
     */
    private String activityName;

    /**
     * 控制类型1：仅买、仅赠
     */
    private String controlType1;

    /**
     * 控制类型2：金额、数量
     */
    private String controlType2;

    /**
     * 期初余额
     */
    private BigDecimal beginningBalance;

    /**
     * 本期款项分解
     */
    private BigDecimal currentPeriodMoney;

    /**
     * 本期发货
     */
    private BigDecimal currentPeriodGoods;

    /**
     * 期末余额
     */
    private BigDecimal endingBalance;

    /**
     * 查询截点余额
     */
    private BigDecimal currentSumBalance;
}
