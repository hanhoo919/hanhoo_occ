package com.yonyou.occ.report.service.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 销售订单dto
 *
 * @author mwh
 * @date 2019/08/19
 */


@Getter
@Setter
public class GoodsDetailB2bOrderDto {
    /**
     * 销售订单id
     */
    private String id;
    /**
     * 销售订单类型
     */
    private String orderType;

    /**
     * custId
     */
    private String custId;
}
