package com.yonyou.occ.report.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 小额账余报表实体类
 *
 * @author lsl
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PettyAccountBalanceEntity implements Serializable {

    /**
     * 销售合同明细主键
     */
    @Id
    private String ctDetailId;

    /**
     * 客户编码
     */
    private String customerCode;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 政策（即活动）编码
     */
    private String activityCode;

    /**
     * 政策（即活动）名称
     */
    private String activityName;

    /**
     * 促销方式
     */
    @Transient
    private Integer promWay;

    /**
     * 促销方式名称
     */
    private String promWayName;

    /**
     *     规则描述
     */
    private String ruleDesc;

    /**
     *    规则-方式名称
     */
    private String ruleModeName;

    /**
     *     是否必选
     */
    @Transient
    private Integer isRequire;

    private String whetherRequired;

    /**
     *     活动余额
     */

    private BigDecimal activityBalance;

    /**
     * 未清金额（显示已到边界值的政策余额）
     */
    private BigDecimal outstandingAmount;

    /**
     * 边界值
     */
    private BigDecimal boundaryValue;
}
