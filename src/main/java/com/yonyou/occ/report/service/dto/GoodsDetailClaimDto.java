package com.yonyou.occ.report.service.dto;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 认领单dto
 *
 * @author mwh
 * @date 2019/08/19
 */

@Getter
@Setter
public class GoodsDetailClaimDto {
    /**
     * 认领组数
     */
    private int claimGroup;
    /**
     * 认领金额
     */
    private BigDecimal claimAmount;

    /**
     * 是否必选
     */
    private int isRequire;

    /**
     * 活动id
     */
    private String activityId;

    /**
     * 客户id
     */
    private String customerId;

}
