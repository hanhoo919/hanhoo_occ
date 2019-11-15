package com.yonyou.occ.report.service.dto;

import lombok.Data;

import java.util.Date;

/**
 * 账余报表 主实体 dto
 */
@Data
public class BalanceReportDto {

    /**
     * 参数初始化
     */
    public void init() {
        balanceReportOptionDto = new BalanceReportOptionDto();
        balanceReportOptionDto.init();
        balanceReportRequireDto = new BalanceReportRequireDto();
        balanceReportRequireDto.init();

    }

    /**
     * 客户编码
     */
    private String custCode;

    /**
     * 客户名称
     */
    private String custName;

    /**
     * 区域名称
     */
    private String marketAreaName;

    /**
     * 省份
     */
    private String provinceName;

    /**
     * 活动编码
     */
    private String activityCode;

    /**
     * 活动名称
     */
    private String activityName;

    /**
     * 活动类型
     */
    private String activityType;

    /**
     * 活动开始时间
     */
    private Date startDate;

    /**
     * 活动结束时间
     */
    private Date endDate;

    /**
     * 可选实体
     */
    private BalanceReportOptionDto balanceReportOptionDto;

    /**
     * 必选实体
     */
    private BalanceReportRequireDto balanceReportRequireDto;

}

