package com.yonyou.occ.report.service.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class CustActivityRuleDetail {

    /**
     * 促销合同明细主键
     */
    @Id
    private String promSaleDetailId;

    /**
     * 累计行提货金额
     */
    private BigDecimal totalrowOrderAmount;

    /**
     * 明细总金额
     */
    private BigDecimal totalrowCtAmount;

    /**
     * 累计行提货数量
     */
    private Integer totalrowOrderNum;

    /**
     * 明细总数量
     */
    private Integer totalrowCtNum;
//    @Id
//    private String id;

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
     * 活动有效期 - 开始时间
     */
    private String activityBeginDate;

    /**
     * 活动有效期 - 结束时间
     */
    private String activityEndDate;

    /**
     * 活动编码
     */
    private String activityCode;

    /**
     * 活动名称
     */
    private String activityName;

    /**
     * 促销类型，0表示买赠，1表示仅买或者仅赠
     */
    private Integer promType;

    /**
     * 商品类别: 1 商品 2 产品 3 组合 4 分类
     */
    private Integer proType;

    /**
     * 数量
     */
    private BigDecimal num;

    /**
     * 金额
     */
    private BigDecimal amount;
}
