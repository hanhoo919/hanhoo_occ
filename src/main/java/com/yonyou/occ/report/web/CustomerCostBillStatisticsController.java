package com.yonyou.occ.report.web;

import com.yonyou.occ.fee.common.enums.CastTypeEnum;
import com.yonyou.occ.fee.common.enums.CastTypeExtEnum;
import com.yonyou.occ.report.service.CustomerFeeBillStatisticsService;
import com.yonyou.occ.report.service.dto.CustomerFeeBillStatisticsDto;
import com.yonyou.occ.report.utils.EasyPoiUtils;
import com.yonyou.occ.report.vo.CustomerCostBillStatisticVO;
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
 * 客户费用单统计报表controller
 * @author lsl
 */

@RestController
@RequestMapping("/report/customerCost")
public class CustomerCostBillStatisticsController extends  BaseController{
    @Autowired
    private CustomerFeeBillStatisticsService customerFeeBillStatisticsService;

    /**
     * 获取客户费用相关信息——门户
     * @param request
     * @return
     */
    @GetMapping("/findAllCustomerCostBillForPortal")
    public List<CustomerFeeBillStatisticsDto> getAllCustomerCostForPortal(HttpServletRequest request){
        Map<String, Object> searchParams = buildSearchParams(request);
        List<CustomerFeeBillStatisticsDto> customerFeeBillStatisticsDtos = customerFeeBillStatisticsService.queryAllCustomerCostForPortal(searchParams);
        return customerFeeBillStatisticsDtos;
    }

    /**
     * 获取客户费用相关信息——中台
     * @param request
     * @return
     */
    @GetMapping("/findAllCustomerCostBillForMiddleground")
    public List<CustomerFeeBillStatisticsDto> getAllCustomerCostForMiddleground(HttpServletRequest request){
        Map<String, Object> searchParams = buildSearchParams(request);
        List<CustomerFeeBillStatisticsDto> customerFeeBillStatisticsDtos = customerFeeBillStatisticsService.queryAllCustomerCostForMiddleground(searchParams);
        return customerFeeBillStatisticsDtos;
    }

    /**
     * 获取费用类型
     * @param request
     * @return
     */
    @GetMapping("/getCastType")
    public Map<String, String> gainCastType(HttpServletRequest request) {
        CastTypeEnum[] castTypeEnums = CastTypeEnum.values();
        Map<String, String> maps = new HashMap<String, String>();
        for (CastTypeEnum castTypeEnum : castTypeEnums) {
            maps.put(castTypeEnum.getCode(), castTypeEnum.getName());
        }

        CastTypeExtEnum[] castTypeExtEnums = CastTypeExtEnum.values();
        for (CastTypeExtEnum castTypeExtEnum : castTypeExtEnums){
            maps.put(castTypeExtEnum.getCode(), castTypeExtEnum.getName());
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
    @GetMapping("/exportCostData")
    public void exportExcelData(HttpServletRequest request, HttpServletResponse response) throws IOException {
        EasyPoiUtils easyPoiUtils = new EasyPoiUtils();
        String dateStr = DateUtil.from_Date_to_String(DateUtil.yyMMddHHmmss, new Date());
        Map<String, Object> searchParams = buildSearchParams(request);
        List<CustomerCostBillStatisticVO> customerCostBillStatisticVOS = customerFeeBillStatisticsService.exportExcelData(searchParams, response);
        if(StringUtils.isEmpty(customerCostBillStatisticVOS) || customerCostBillStatisticVOS.size() <= 0){
            customerCostBillStatisticVOS = new ArrayList<CustomerCostBillStatisticVO>();
        }
        easyPoiUtils.exportExcel(customerCostBillStatisticVOS, "客户费用单统计表", "客户费用单统计表", CustomerCostBillStatisticVO.class, "客户费用单统计表_" + dateStr + ".xls", true, response);
    }

}
