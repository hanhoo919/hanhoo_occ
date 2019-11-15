package com.yonyou.occ.report.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * 代理商账余报表 查询数据 实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class BalanceReport implements Serializable {

    /**
     * 合同主键
     */

    private String ctId;
    /**
     * 合同明细主键
     */
    @Id
    private String detailId;
    /**
     * 客户编码
     */
    private String custCode;
    /**
     * 客户名称
     */
    private String custName;
    /**
     * 省份
     */
    private String provinceName;
    /**
     * 区域
     */
    private String marketName;
    /**
     * 政策类型（活动节点名称）
     */
    private String activityName;
    /**
     * 活动编码
     */
    private String activityCode;
    /**
     * 活动名称
     */
    private String nodeName;
    /**
     * 开始日期
     */
    private Date startDate;
    /**
     * 结束日期
     */
    private Date endDate;
    /**
     * 必选-总金额
     */
    private BigDecimal requireTotalAmount;
    /**
     * 必选-认领组数
     */
    private BigDecimal totalClaimGroup;
    /**
     * 必选-认领总金额
     */
    private BigDecimal totalRequireRepay;
    /**
     * 规则类型 1：商品
     */
    private String proCode;
    /**
     * 规则描述
     */
    private String ruleDesc;
    /**
     * 折扣
     */
    private BigDecimal rebate;
    /**
     * 下单金额
     */
    private BigDecimal totalrowOrderAmount;
    /**
     * 可选-回款金额
     */
    private BigDecimal totalOptionRepay;
    /**
     * 合同总金额
     */
    private BigDecimal totalrowCtAmount;
    /**
     * 行号
     */
    private String rowNum;
    /**
     * 商品行类别
     */
    private Integer proType;
    /**
     * 活动行金额
     */
    private BigDecimal amount;

    /**
     * 活动行数量
     */
    private BigDecimal count;
    /**
     * 已下单数量
     */
    private BigDecimal totalrowOrderNum;
    /**
     * 规则类型
     */
    private Integer ruleType;
    /**
     * 合同数量
     */
    private BigDecimal totalrowCtNum;
    /**
     * 必选标识
     */
    private Integer isRequire;

    /**
     * 销售价格， 零售价 乘 折扣
     */
    private BigDecimal salePrice;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BalanceReport that = (BalanceReport) o;
        return Objects.equals(ctId, that.ctId) &&
                Objects.equals(detailId, that.detailId) &&
                Objects.equals(custCode, that.custCode) &&
                Objects.equals(custName, that.custName) &&
                Objects.equals(provinceName, that.provinceName) &&
                Objects.equals(marketName, that.marketName) &&
                Objects.equals(activityName, that.activityName) &&
                Objects.equals(activityCode, that.activityCode) &&
                Objects.equals(nodeName, that.nodeName) &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate) &&
                Objects.equals(requireTotalAmount, that.requireTotalAmount) &&
                Objects.equals(totalClaimGroup, that.totalClaimGroup) &&
                Objects.equals(totalRequireRepay, that.totalRequireRepay) &&
                Objects.equals(proCode, that.proCode) &&
                Objects.equals(ruleDesc, that.ruleDesc) &&
                Objects.equals(rebate, that.rebate) &&
                Objects.equals(totalrowOrderAmount, that.totalrowOrderAmount) &&
                Objects.equals(totalOptionRepay, that.totalOptionRepay) &&
                Objects.equals(totalrowCtAmount, that.totalrowCtAmount) &&
                Objects.equals(rowNum, that.rowNum) &&
                Objects.equals(proType, that.proType) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(count, that.count) &&
                Objects.equals(totalrowOrderNum, that.totalrowOrderNum) &&
                Objects.equals(ruleType, that.ruleType) &&
                Objects.equals(totalrowCtNum, that.totalrowCtNum) &&
                Objects.equals(isRequire, that.isRequire) &&
                Objects.equals(salePrice, that.salePrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ctId, detailId, custCode, custName, provinceName, marketName, activityName, activityCode, nodeName, startDate, endDate, requireTotalAmount, totalClaimGroup, totalRequireRepay, proCode, ruleDesc, rebate, totalrowOrderAmount, totalOptionRepay, totalrowCtAmount, rowNum, proType, amount, count, totalrowOrderNum, ruleType, totalrowCtNum, isRequire, salePrice);
    }
}

