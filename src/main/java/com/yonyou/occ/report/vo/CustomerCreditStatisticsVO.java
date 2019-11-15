package com.yonyou.occ.report.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelIgnore;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CustomerCreditStatisticsVO {

    //客户分类（即渠道）
    @Excel(name = "渠道", width = 15, height = 11)
    private String channelName;

    //区域
    @Excel(name = "区域", width = 15, height = 11)
    private String marketAreaName;

    //省份
    @ExcelIgnore
    private String provinceName;

    //客户编码（即代理商编码）
    @Excel(name = "代理商编码", width = 15, height = 11)
    private String customerCode;

    //客户名称（即代理商名称）
    @Excel(name = "代理商名称", width = 15, height = 11)
    private String customerName;

    //活动开始日期（即活动日期）
    @Excel(name = "活动日期", format = "yyyy-MM-dd", width = 15, height = 11)
    private Date activityStartDate;

    //活动编码
    @Excel(name = "政策编码", width = 15, height = 11)
    private String activityCode;

    //活动名称
    @Excel(name = "政策名称", width = 15, height = 11)
    private String activityName;

    //创建授信日期
    @Excel(name = "创建授信日期", format = "yyyy-MM-dd HH:mm:ss", width = 15, height = 11)
    private Date creationTime;

    //授信有效开始日期
    @Excel(name = "授信有效开始日期", format = "yyyy-MM-dd HH:mm:ss", width = 15, height = 11)
    private Date creditStartDate;

    //授信有效结束日期
    @Excel(name = "授信有效结束日期", format = "yyyy-MM-dd HH:mm:ss", width = 15, height = 11)
    private Date creditEndDate;

    //期初授信余额
    @Excel(name = "期初授信余额", numFormat="0.00", width = 15, height = 11)
    private BigDecimal initialCreditBalance = BigDecimal.ZERO;

    //本期授信金额
    @Excel(name = "本期授信金额", numFormat="0.00", width = 15, height = 11)
    private BigDecimal creditLimitAmount = BigDecimal.ZERO;

    //本期授信还款
    @Excel(name = "本期授信还款", numFormat="0.00", width = 15, height = 11)
    private BigDecimal claimAmount = BigDecimal.ZERO;

    //本期授信余额
    @Excel(name = "本期授信余额", numFormat="0.00", width = 15, height = 11)
    private BigDecimal creditBalance = BigDecimal.ZERO;
}
