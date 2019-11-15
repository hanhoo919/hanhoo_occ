package com.yonyou.occ.report.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
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
public class CustomerReturnedMoneyDetailEntity implements Serializable {
    @Id
    private String activityId;

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

    //认领单编号
    @Transient
    private String claimCode;

    //本次认领组数
    @Transient
    private String claimGroup;

    //本次认领金额
    private BigDecimal claimAmount;

    //收款（认领、分解）日期
    private Date gatheringDate;

    //认领收款余额
    @Transient
    private BigDecimal claimBalance;

    //是否计任务总额
    private BigDecimal calTaskAmount;

    //是否计返点总额
    private BigDecimal calReturnAmount;

    //备注
    private String remark;
}
