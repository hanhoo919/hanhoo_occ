package com.yonyou.occ.report.service;

import com.yonyou.occ.report.service.dto.CustomerReturnedMoneyAndDetailDto;
import com.yonyou.occ.report.vo.CustomerReturnedMoneyDetailVO;
import com.yonyou.occ.report.vo.CustomerReturnedMoneyVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface CustomerReturnedMoneyService {
    /**
     * 客户回款统计——门户
     *
     * @param searchParams
     * @return
     */
    List<CustomerReturnedMoneyAndDetailDto> queryReturnedMoney(Map<String, Object> searchParams);

    /**
     * 客户回款统计——中台
     *
     * @param searchParams
     * @param pageable
     * @return
     */
    Page<CustomerReturnedMoneyAndDetailDto> queryReturnedMoneyByPage(Map<String, Object> searchParams, Pageable pageable);

    /**
     * 导出数据
     *
     * @param searchParams
     * @param response
     * @return
     */
    List<CustomerReturnedMoneyVO> exportExcelData(Map<String, Object> searchParams, HttpServletResponse response);

    /**
     * 客户汇款分解明细查询（门户）
     * @param searchParams
     * @return
     */
    List<CustomerReturnedMoneyAndDetailDto> queryReturnedMoneyDetail(Map<String, Object> searchParams);

    List<CustomerReturnedMoneyDetailVO> exportDetailExcelData(Map<String, Object> searchParams);

    List<CustomerReturnedMoneyAndDetailDto> queryReturnedMoneyForPortal(Map<String, Object> searchParams);

    List<CustomerReturnedMoneyAndDetailDto> queryReturnedMoneyDetailForPortal(Map<String, Object> searchParams);

}
