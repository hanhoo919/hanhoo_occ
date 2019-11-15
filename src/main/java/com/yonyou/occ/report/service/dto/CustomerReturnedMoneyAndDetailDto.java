package com.yonyou.occ.report.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 客户回款及分解明细DTO
 */
@Data
public class CustomerReturnedMoneyAndDetailDto {
    //活动主键
    private String activityId;

    //回款单主键
    private String billreceiptId;

    //活动编码
    private String activityCode;

    //活动名称
    private String activityName;

    //客户编码（即代理商编码）
    private String customerCode;

    //客户名称（即代理商名称）

    private String customerName;

    //客户分类（即渠道）
    private String customerCategoryName;

    //区域
    private String marketAreaName;

    //省份
    private String provinceName;

    //认领单编号
    private String claimCode;

    //本次认领组数
    private String claimGroup;

    //本次认领金额
    private BigDecimal claimAmount;

    //收款（认领、分解）日期
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date gatheringDate;

    //认领收款余额
    private BigDecimal claimBalance;

    //是否计任务总额
    private BigDecimal calTaskAmount;

    //是否计返点总额
    private BigDecimal calReturnAmount;

    //回款日期
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date billreceiptTime;

    //回款金额
    private BigDecimal receiptAmount;

    //中台收款单号
    private String receiptCode;

    //备注
    private String remark;

}
