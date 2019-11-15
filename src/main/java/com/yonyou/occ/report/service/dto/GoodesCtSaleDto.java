package com.yonyou.occ.report.service.dto;


import lombok.Getter;
import lombok.Setter;

/**
 * 合同dto
 *
 * @author mwh
 * @date 2019/08/19
 */


@Getter
@Setter
public class GoodesCtSaleDto {
    /**
     * 合同
     */
    private String saleId;
    /**
     * 活动id
     */
    private String activityId;
    /**
     * 客户id
     */
    private String custId;
}
