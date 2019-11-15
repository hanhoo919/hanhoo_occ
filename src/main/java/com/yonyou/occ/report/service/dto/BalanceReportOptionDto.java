package com.yonyou.occ.report.service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 账余报表  必选实体
 */
@Data
public class BalanceReportOptionDto {

    /**
     * 参数初始化
     */
    public void init() {
        optionItemDtoList = new ArrayList<>();
        optionUsedAmount = BigDecimal.ZERO;
        optionUnusedAmount = BigDecimal.ZERO;
    }

    /**
     * 活动回款总金额 -可选
     */
    private BigDecimal optionTotalAmount;

    /**
     * 活动已用总金额 -可选
     */
    private BigDecimal optionUsedAmount;

    /**
     * 活动未清总金额 -可选
     */
    private BigDecimal optionUnusedAmount;


    /**
     * 可选部分实体集合
     */
    private List<BalanceReportOptionItemDto> optionItemDtoList;

}

