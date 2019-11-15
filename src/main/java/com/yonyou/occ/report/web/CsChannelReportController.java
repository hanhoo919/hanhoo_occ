package com.yonyou.occ.report.web;

import com.yonyou.occ.report.service.dto.CustPolicyAccountDetailDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping("/report")
public class CsChannelReportController {

    /**
     * 根据客户编码或客户名称、活动编码或活动名称、查询开始时间、查询结束时间，获取客户政策账余明细数据集合
     *
     * @param customerCode 客户编码，可为空
     * @param customerName 客户名称，可为空
     * @param activityCode 活动编码，可为空
     * @param activityName 活动名称，可为空
     * @param beginDate    查询开始时间，非空
     * @param endDate      查询结束时间，非空
     * @return
     */
    @GetMapping("cust-policy-account-details")
    public ResponseEntity<CustPolicyAccountDetailDto> getCustPolicyAccountDetails(@RequestParam(value = "customerCode", required = false) String customerCode,
                                                                                  @RequestParam(value = "customerName", required = false) String customerName,
                                                                                  @RequestParam(value = "activityCode", required = false) String activityCode,
                                                                                  @RequestParam(value = "activityName", required = false) String activityName,
                                                                                  @RequestParam(value = "beginDate") String beginDate,
                                                                                  @RequestParam(value = "endDate") String endDate) {
        return null;

    }
}
