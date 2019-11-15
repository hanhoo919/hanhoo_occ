package com.yonyou.occ.report.service.dto;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 出货明细及政策余额报表dto
 */
@Getter
@Setter
public class GoodsDetailDto {

    /**
     * 客户id
     */
    private String custId;
    /**
     * 客户名称
     */
    private String custCode;
    /**
     * 客户编码
     */
    private String custName;
    /**
     * 省份
     */
    private String province;
    /**
     * 区域
     */
    private String marketArea;
    /**
     * 每月出货额
     */
    private List<GoodsDetailOfMonth> goodsDetailOfMonths;
    /**
     * 货款出货额合计
     */
    private BigDecimal paymentAmounTotal;
    /**
     * 零售出货额合计
     */
    private BigDecimal retailAmoutTotal;

    /**
     * 货款余额
     */
    private BigDecimal paymentBalance;
    /**
     * 零售余额
     */
    private BigDecimal retailBalance;

}
