package com.yonyou.occ.report.service.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 客户费用单统计报表
 * @author Administrator
 */
@Data
public class CustomerFeeBillStatisticsDto {

    private String id;

    private String customerId;

    /**
     * 客户分类（即渠道）
     */
    private String customerCategoryName;

    /**
     * 区域
     */
    private String marketAreaName;

    /**
     * 省份
     */
    private String provinceName;

    /**
     * 客户编码（即代理商编码）
     */
    private String customerCode;

    /**
     * 客户名称（即代理商名称）
     */
    private String customerName;

    /**
     * 期间
     */
    private String period;
    /**
     * 费用类型
     */
    private String castTypeId;

    private String castTypeName;

    /**
     * 期初余额
     */
    private BigDecimal openingBalance = BigDecimal.ZERO;

    /**
     * 本期增加金额
     */
    private BigDecimal currentIncreases = BigDecimal.ZERO;

    /**
     * 本期减少金额
     */
    private BigDecimal totalOutAmount = BigDecimal.ZERO;

    /**
     * 本期余额
     */
    private BigDecimal currentBalance = BigDecimal.ZERO;

}
