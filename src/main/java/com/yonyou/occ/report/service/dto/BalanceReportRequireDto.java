package com.yonyou.occ.report.service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 账余报表 可选实体
 */
@Data
public class BalanceReportRequireDto {

    /**
     * 参数初始化
     */
    public void init() {
        requireItemDtoList = new ArrayList<>();
        requireTotalAmount = BigDecimal.ZERO;
        requireUnusedAmount = BigDecimal.ZERO;
        requireUsedAmount = BigDecimal.ZERO;
    }

    /**
     * 单组回款金额 -必选
     */
    private BigDecimal requireUnitAmount;

    /**
     * 已回款组数 -必选
     */
    private BigDecimal requireBackGroup;

    /**
     * 活动回款总金额 -必选
     */
    private BigDecimal requireTotalAmount;

    /**
     * 活动已用总金额 -必选
     */
    private BigDecimal requireUsedAmount;

    /**
     * 活动未清总金额 -必选
     */
    private BigDecimal requireUnusedAmount;

    /**
     * 必选部分实体集合
     */
    private List<BalanceReportRequireItemDto> requireItemDtoList;

}

