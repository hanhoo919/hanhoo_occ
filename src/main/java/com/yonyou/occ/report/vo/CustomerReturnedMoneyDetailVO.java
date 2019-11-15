package com.yonyou.occ.report.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelIgnore;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CustomerReturnedMoneyDetailVO {
    @ExcelIgnore
    private String activityId;

    //客户分类（即渠道）
    @Excel(name = "渠道", height = 11, width = 15)
    private String customerCategoryName;

    //区域
    @Excel(name = "区域", height = 11, width = 15)
    private String marketAreaName;

    //客户编码（即代理商编码）
    @Excel(name = "代理商编码", height = 11, width = 15)
    private String customerCode;

    //客户名称（即代理商名称）
    @Excel(name = "代理商名称", height = 11, width = 25)
    private String customerName;

    //收款（认领、分解）日期
    @Excel(name = "分解（认领）日期", exportFormat = "yyyy-MM-dd",height = 11, width = 15)
    private Date gatheringDate;

    //活动编码
    @Excel(name = "政策编码", height = 11, width = 15)
    private String activityCode;

    //活动名称
    @Excel(name = "政策名称", height = 11, width = 55)
    private String activityName;

    //认领单编号
    @ExcelIgnore
    private String claimCode;

    //本次认领组数
    @ExcelIgnore
    private String claimGroup;

    //本次认领金额
    @Excel(name = "认领金额", numFormat="0.00", height = 11, width = 15)
    private BigDecimal claimAmount;

    //认领收款余额
    @ExcelIgnore
    private BigDecimal claimBalance;

    //是否计任务总额
    @Excel(name = "是否计任务", numFormat="0.00", height = 11, width = 15)
    private BigDecimal calTaskAmount;

    //是否计返点总额
    @Excel(name = "是否计返点", numFormat="0.00", height = 11, width = 15)
    private BigDecimal calReturnAmount;

    //备注
    @Excel(name = "备注", height = 11, width = 15)
    private String remark;
}
