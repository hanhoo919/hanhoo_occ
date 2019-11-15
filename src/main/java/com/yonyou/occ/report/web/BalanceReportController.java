package com.yonyou.occ.report.web;

import com.yonyou.occ.report.service.BalanceReportService;
import com.yonyou.occ.report.service.dto.BalanceReportDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 代理商活动账余报表 controller
 *
 * @author baobq
 * @create 2019/8/19
 * @since 1.0.0
 */
@RestController
@RequestMapping("/report/balanceReport")
public class BalanceReportController extends BaseController {

    @Autowired
    private BalanceReportService balanceReportService;

    /**
     * 页面查询
     *
     * @param request
     * @return
     */
    @GetMapping("/findAll")
    public List<BalanceReportDto> findAll(HttpServletRequest request) {
        Map<String, Object> searchParams = buildSearchParams(request);
        List<BalanceReportDto> resultList = balanceReportService.balanceReport(searchParams);
        return resultList;

    }

    /**
     * 页面查询 - 门户
     *
     * @param request
     * @return
     */
    @GetMapping("/findAllForShop")
    public List<BalanceReportDto> findAllForShop(HttpServletRequest request) {
        Map<String, Object> searchParams = buildSearchParams(request);
        List<BalanceReportDto> resultList = balanceReportService.balanceReportForShop(searchParams);
        return resultList;
    }

    /**
     * 账余报表数据导出
     *
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/exportExcelData")
    public Map<String, String> exportExcelData(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> searchParams = buildSearchParams(request);
        return balanceReportService.exportExcelData(searchParams, response);
    }

}

