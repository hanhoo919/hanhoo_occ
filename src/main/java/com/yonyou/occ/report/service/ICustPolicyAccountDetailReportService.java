package com.yonyou.occ.report.service;

import com.yonyou.occ.report.service.dto.CustPolicyAccountDetailDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * 客户政策账余明细表 取数逻辑接口
 *
 * @author Davis tang
 * @date 2019/08/27
 */
public interface ICustPolicyAccountDetailReportService {

    List<CustPolicyAccountDetailDto> getCustPolicyAccountDetails(String customerCode,
                                                                 String customerName,
                                                                 String activityCode,
                                                                 String activityName,
                                                                 String beginDate,
                                                                 String endDate);
}
