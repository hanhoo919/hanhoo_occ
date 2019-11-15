package com.yonyou.occ.report.service.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 账余报表 必选子实体  dto
 * @author Administrator
 */
@Data
public class BalanceReportRequireItemDto {

    /**
     * 产品编码
     */
    private String proCode;

    /**
     * 产品名称
     */
    private String proName;

    /**
     * 项目类型
     */
    private String proType;

    /**
     * 项目类型 具体值
     */
    private Integer proTypeCode;

    /**
     * 控制类型
     */
    private String controlType;

    /**
     * 单组数量/金额
     */
    private BigDecimal unit;

    /**
     * 回款组数
     */
    private BigDecimal returnGroup;

    /**
     * 行折扣
     */
    private BigDecimal discount;

    /**
     * 合计
     */
    private BigDecimal total;

    /**
     * 已发
     */
    private BigDecimal stockOut;

    /**
     * 未清代理商总计(回款)
     */
    private BigDecimal totalUnusedAmount;

    /**
     * 未清
     */
    private BigDecimal unUsedAmount;

    /**
     * 已发金额
     */
    private BigDecimal stockOutAmount;

}

