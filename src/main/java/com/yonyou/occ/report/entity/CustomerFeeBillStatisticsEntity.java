package com.yonyou.occ.report.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.math.BigDecimal;


/**
 * @author lsl
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CustomerFeeBillStatisticsEntity implements Serializable{

    /**
     * 根据客户主键与费用类型id创建唯一主键
     */
	@Id
    private String id;

    /**
     * 客户主键
     */
    private String customerId;

    /**
     * 客户分类（即渠道）
     */
    private String customerCategoryName;

    /**
     * 区域
     */
    private String marketAreaName;

    /**
     * 省份
     */
    private String provinceName;

    /**
     * 客户编码（即代理商编码）
     */
    private String customerCode;

    /**
     * 客户名称（即代理商名称）
     */
    private String customerName;

    /**
     * 期间
     */
    @Transient
    private String period;

    /**
     * 费用类型
     */
    private String castTypeId;

    private String castTypeName;

    /**
     * 本期增加金额
     */
    private BigDecimal currentIncreases = BigDecimal.ZERO;

}
