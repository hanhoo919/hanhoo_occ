package com.yonyou.occ.report.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelIgnore;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CustomerReturnedMoneyVO {

    //回款单主键
    @ExcelIgnore
    private String billreceiptId;

    //客户分类（即渠道）
    @Excel(name = "渠道", height = 11, width = 15)
    private String customerCategoryName;

    //区域
    @Excel(name = "区域", height = 11, width = 15)
    private String marketAreaName;

    //省份
    @Excel(name = "省份", height = 11, width = 15)
    private String provinceName;

    //客户编码（即代理商编码）
    @Excel(name = "代理商编码", height = 11, width = 15)
    private String customerCode;

    //客户名称（即代理商名称）
    @Excel(name = "代理商名称", height = 11, width = 15)
    private String customerName;

    //回款日期
    @Excel(name = "回款日期", exportFormat = "yyyy-MM-dd HH:mm:ss", height = 11, width = 15)
    private Date billreceiptTime;

    //回款金额
    @Excel(name = "回款金额", height = 11, width = 15)
    private BigDecimal receiptAmount;

    //中台收款单号
    @Excel(name = "中台收款单号", height = 11, width = 15)
    private String receiptCode;

    //备注
    @Excel(name = "备注", height = 11, width = 15)
    private String remark;
}
