package com.yonyou.occ.report.web;

import com.yonyou.occ.report.service.CustomerCreditLimitService;
import com.yonyou.occ.report.service.dto.CustomerCreditStatisticsDto;
import com.yonyou.occ.report.utils.EasyPoiUtils;
import com.yonyou.occ.report.vo.CustomerCreditStatisticsVO;
import com.yonyou.ocm.common.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 客户授信统计
 * @author 梁松流
 */
@RestController
@RequestMapping("/report/credit")
public class CustomerCreditStatisticsController extends BaseController {

    @Autowired
    private CustomerCreditLimitService customerCreditLimitService;

    /**
     * 查询数据--门户
     * @param request
     * @return
     */
    @GetMapping("/findAllCreditForPortal")
    public List<CustomerCreditStatisticsDto> getCustomerCreditForPortal(HttpServletRequest request){
        Map<String, Object> searchParams = buildSearchParams(request);
        List<CustomerCreditStatisticsDto> creditStatisticsDtoLists = customerCreditLimitService.queryCreditLimitForPortal(searchParams);
        return creditStatisticsDtoLists;
    }

    /**
     * 查询数据--中台
     * @param request
     * @return
     */
    @GetMapping("/findAllCreditForMiddleground")
    public List<CustomerCreditStatisticsDto> getCustomerCreditForMiddleground(HttpServletRequest request){
        Map<String, Object> searchParams = buildSearchParams(request);
        List<CustomerCreditStatisticsDto> creditStatisticsDtoLists = customerCreditLimitService.queryCreditLimitForMiddleground(searchParams);
        return creditStatisticsDtoLists;
    }

    /**
     * 导出授信数据
     * @param request
     * @param response
     * @throws IOException
     */
    @GetMapping("/exportCreditData")
    public void exportCreditLimitData(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String dateStr = DateUtil.from_Date_to_String(DateUtil.yyMMddHHmmss, new Date());
        EasyPoiUtils easyPoiUtils = new EasyPoiUtils();
        Map<String, Object> searchParams = buildSearchParams(request);
        List<CustomerCreditStatisticsVO> customerCreditStatisticsVOS = customerCreditLimitService.exportData(searchParams);
        easyPoiUtils.exportExcel(customerCreditStatisticsVOS, "客户授信统计表", "客户授信统计表", CustomerCreditStatisticsVO.class, "客户授信统计表_" + dateStr + ".xls", true, response);
    }
}
