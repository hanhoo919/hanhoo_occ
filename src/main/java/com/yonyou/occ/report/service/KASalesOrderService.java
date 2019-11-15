package com.yonyou.occ.report.service;

import com.yonyou.occ.report.service.dto.KASalesOrderDto;
import com.yonyou.occ.report.vo.KASalesOrderVO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface KASalesOrderService {

    /**
     * 查询所有的销售订单
     */
    List<KASalesOrderDto> queryAllForOrder(Map<String, Object> searchParams);

    /**
     * 导出数据
     *
     * @param searchParams
     * @param response
     * @return
     */
    List<KASalesOrderVO> exportExcelData(Map<String, Object> searchParams, HttpServletResponse response);

    List<KASalesOrderDto> queryAllForOrderForPortal(Map<String, Object> searchParams);
}
