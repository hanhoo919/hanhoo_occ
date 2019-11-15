package com.yonyou.occ.report.service.dto;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 每月出货额dto
 *
 * @author mwh
 * @date 2019/0819
 */

@Setter
@Getter
public class GoodsDetailOfMonth {
    /**
     * 出货月份
     */
    private String date;
    /**
     * 货款出货
     */
    private BigDecimal paymentAmount;
    /**
     * 零售价出货
     */
    private BigDecimal retailAmout;
}
