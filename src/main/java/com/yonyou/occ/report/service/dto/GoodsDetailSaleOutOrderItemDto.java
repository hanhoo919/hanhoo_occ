package com.yonyou.occ.report.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 销售出库单明细dto
 *
 * @author mwh
 * @date 2019/08/19
 */
@Getter
@Setter
public class GoodsDetailSaleOutOrderItemDto {
    /**
     * 出库明细 id
     */
    private String id;


    /**
     * 是否赠品
     */
    private Integer isGift;
    /**
     * 实处数量
     */
    private Integer factOutNum;

    /**
     * 出库日期
     */
    private Date outDate;
    /**
     * 销售订单号
     */
    private String firstBillCode;
    /**
     * 销售订单明细号
     */
    private String firstBillBCode;
}
