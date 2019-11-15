package com.yonyou.occ.report.service.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 账余报表 可选子实体  dto
 */
@Data
public class BalanceReportOptionItemDto {

    /**
     * 产品编码
     */
    private String proCode;

    /**
     * 产品名称
     */
    private String proName;

    /**
     * 项目类型 具体值
     */
    private Integer proTypeCode;

    /**
     * 控制类型
     */
    private String controlType;

    /**
     * 未清
     */
    private BigDecimal unUsed;

    /**
     * 已发金额
     */
    private BigDecimal stockOutAmount;

}