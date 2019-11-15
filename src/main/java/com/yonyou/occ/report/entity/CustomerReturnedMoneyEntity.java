package com.yonyou.occ.report.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author hanhoo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CustomerReturnedMoneyEntity implements Serializable {
    @Id
    private String billreceipt_id;

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

    //回款日期
    private Date billreceiptTime;

    //回款金额
    private BigDecimal receiptAmount;

    //中台收款单号
    private String receiptCode;

    //备注
    private String remark;
}
