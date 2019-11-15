package com.yonyou.occ.report.service;

import com.yonyou.occ.report.service.dto.CustomerFeeBillStatisticsDto;
import com.yonyou.occ.report.vo.CustomerCostBillStatisticVO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 客户费用单统计接口
 * @author lsl
 */
public interface CustomerFeeBillStatisticsService {

    /**
     * 获取客户费用相关信息——门户
     * @param searchParams
     * @return
     */
    List<CustomerFeeBillStatisticsDto> queryAllCustomerCostForPortal(Map<String, Object> searchParams);

    /**
     * 获取客户费用相关信息——中台
     * @param searchParams
     * @return
     */
    List<CustomerFeeBillStatisticsDto> queryAllCustomerCostForMiddleground(Map<String, Object> searchParams);

    /**
     * 数据导出
     * @param searchParams
     * @param response
     * @return
     */
    List<CustomerCostBillStatisticVO> exportExcelData(Map<String, Object> searchParams, HttpServletResponse response);
}
