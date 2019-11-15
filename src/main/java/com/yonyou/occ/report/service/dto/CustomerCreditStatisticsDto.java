package com.yonyou.occ.report.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 客户授信统计
 */

@Data
public class CustomerCreditStatisticsDto {
    //活动编码
    private String activityCode;

    //活动名称
    private String activityName;

    //客户编码（即代理商编码）
    private String customerCode;

    //客户名称（即代理商名称）

    private String customerName;

    //客户分类（即渠道）
    private String channelName;

    //区域
    private String marketAreaName;

    //省份
    private String provinceName;

    //活动开始日期（即活动日期）
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date activityStartDate;

    //创建授信日期
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date creationTime;

    //授信有效开始日期
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date creditStartDate;

    //授信有效结束日期
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date creditEndDate;

    //期初授信余额
    private BigDecimal initialCreditBalance = BigDecimal.ZERO;

    //本期授信金额
    private BigDecimal creditLimitAmount = BigDecimal.ZERO;

    //本期授信还款
    private BigDecimal claimAmount = BigDecimal.ZERO;

    //本期授信余额
    private BigDecimal creditBalance = BigDecimal.ZERO;

}
