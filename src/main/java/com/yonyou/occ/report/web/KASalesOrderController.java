package com.yonyou.occ.report.web;

import com.yonyou.occ.b2b.common.enums.OrderStatus;
import com.yonyou.occ.report.service.KASalesOrderService;
import com.yonyou.occ.report.service.dto.KASalesOrderDto;
import com.yonyou.occ.report.utils.EasyPoiUtils;
import com.yonyou.occ.report.vo.KASalesOrderVO;
import com.yonyou.ocm.common.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @author 梁松流
 * @date 2019-09-06
 * @deprecated KA销售订单——报表预览
 */
@RestController
@RequestMapping("/report/KASalesOrder")
public class KASalesOrderController extends BaseController {

    @Autowired
    private KASalesOrderService kaSalesOrderService;

    /**
     * 门户——查询
     */
    @GetMapping("/findAllOrderForPortal")
    public List<KASalesOrderDto> findAllOrderPortal(HttpServletRequest request) {
        Map<String, Object> searchParams = buildSearchParams(request);
        List<KASalesOrderDto> resultList = kaSalesOrderService.queryAllForOrderForPortal(searchParams);
        return resultList;
    }


    /**
     * 中台——查询
     */
    @GetMapping("/findAllOrderForMiddleground")
    public List<KASalesOrderDto> findAllOrderMiddleground(HttpServletRequest request) {
        Map<String, Object> searchParams = buildSearchParams(request);
        List<KASalesOrderDto> resultList = kaSalesOrderService.queryAllForOrder(searchParams);
        return resultList;
    }


    /**
     * 获取订单状态
     *
     * @return
     */
    @GetMapping("/findAllOrderStatus")
    public Map<String, String> getOrderStatus(HttpServletRequest request) {
        OrderStatus[] orderStatuses = OrderStatus.values();
        Map<String, String> maps = new HashMap<String, String>();
        for (OrderStatus orderStatus : orderStatuses) {
            maps.put(orderStatus.getCode(), orderStatus.getName());
        }
        return maps;
    }

    /**
     * 导出数据
     *
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/exportOrderData")
    public void exportExcelData(HttpServletRequest request, HttpServletResponse response) throws IOException {
        EasyPoiUtils easyPoiUtils = new EasyPoiUtils();
        String dateStr = DateUtil.from_Date_to_String(DateUtil.yyMMddHHmmss, new Date());
        Map<String, Object> searchParams = buildSearchParams(request);
        List<KASalesOrderVO> kaSalesOrderVOList = kaSalesOrderService.exportExcelData(searchParams, response);
        if(StringUtils.isEmpty(kaSalesOrderVOList) || kaSalesOrderVOList.size() <= 0){
            kaSalesOrderVOList = new ArrayList<KASalesOrderVO>();
        }
        easyPoiUtils.exportExcel(kaSalesOrderVOList, "销售订单与交货订单表", "销售订单与交货订单表", KASalesOrderVO.class, "销售订单与交货订单表_" + dateStr + ".xls", true, response);
    }
}
