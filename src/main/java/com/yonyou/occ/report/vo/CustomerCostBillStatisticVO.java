package com.yonyou.occ.report.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelIgnore;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CustomerCostBillStatisticVO {

    @ExcelIgnore
    private String id;

    @ExcelIgnore
    private String customerId;

    /**
     * 客户分类（即渠道）
     */
    @Excel(name = "渠道", width = 15, height = 15)
    private String customerCategoryName;

    /**
     * 区域
     */
    @Excel(name = "区域", width = 15, height = 15)
    private String marketAreaName;

    /**
     * 省份
     */
    @Excel(name = "省份", width = 15, height = 15)
    private String provinceName;

    /**
     * 客户编码（即代理商编码）
     */
    @Excel(name = "代理商编码", width = 25, height = 15)
    private String customerCode;

    /**
     * 客户名称（即代理商名称）
     */
    @Excel(name = "代理商名称", width = 25, height = 15)
    private String customerName;

    /**
     * 期间
     */
    @Excel(name = "期间", width = 15, height = 15)
    private String period;
    /**
     * 费用类型
     */
    @ExcelIgnore
    private String castTypeId;
    @Excel(name = "费用类型", width = 15, height = 15)
    private String castTypeName;

    /**
     * 期初余额
     */
    @Excel(name = "期初余额", width = 15, height = 15, numFormat = "0.00")
    private BigDecimal openingBalance = BigDecimal.ZERO;

    /**
     * 本期增加金额
     */
    @Excel(name = "本期增加金额", width = 15, height = 15, numFormat = "0.00")
    private BigDecimal currentIncreases = BigDecimal.ZERO;

    /**
     * 本期减少金额
     */
    @Excel(name = "本期减少金额", width = 15, height = 15, numFormat = "0.00")
    private BigDecimal totalOutAmount = BigDecimal.ZERO;

    /**
     * 本期余额
     */
    @Excel(name = "本期余额", width = 15, height = 15, numFormat = "0.00")
    private BigDecimal currentBalance = BigDecimal.ZERO;
}
