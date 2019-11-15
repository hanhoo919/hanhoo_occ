package com.yonyou.occ.report.service;

import com.yonyou.occ.report.service.dto.CustomerCreditStatisticsDto;
import com.yonyou.occ.report.vo.CustomerCreditStatisticsVO;

import java.util.List;
import java.util.Map;

/**
 * 客户授信统计接口类
 * @author 梁松流
 */
public interface CustomerCreditLimitService {
    List<CustomerCreditStatisticsDto> queryCreditLimitForPortal(Map<String, Object> searchParams);

    List<CustomerCreditStatisticsDto> queryCreditLimitForMiddleground(Map<String, Object> searchParams);

    List<CustomerCreditStatisticsVO> exportData(Map<String, Object> searchParams);
}
