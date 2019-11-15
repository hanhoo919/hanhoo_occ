package com.yonyou.occ.report.web;

import com.yonyou.occ.report.service.CustomerReturnedMoneyService;
import com.yonyou.occ.report.service.dto.CustomerReturnedMoneyAndDetailDto;
import com.yonyou.occ.report.utils.EasyPoiUtils;
import com.yonyou.occ.report.vo.CustomerReturnedMoneyDetailVO;
import com.yonyou.occ.report.vo.CustomerReturnedMoneyVO;
import com.yonyou.ocm.common.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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
 * 客户回款统计
 * 客户回款分解明细
 *
 * @author lsl
 */
@RestController
@RequestMapping("/report/remittance")
public class CustomerReturnedMoneyController extends BaseController {

    @Autowired
    private CustomerReturnedMoneyService customerReturnedMoneyService;

    /**
     * 门户——客户回款统计查询
     *
     * @param request
     * @return
     */
    @GetMapping("/findAllReturnedMoneyForPortal")
    public List<CustomerReturnedMoneyAndDetailDto> findAllReturnedMoneyForPortal(HttpServletRequest request) {
        Map<String, Object> searchParams = buildSearchParams(request);

        List<CustomerReturnedMoneyAndDetailDto> lists = customerReturnedMoneyService.queryReturnedMoneyForPortal(searchParams);
        return lists;
    }

    /**
     * 门户——客户回款分解明细查询
     *
     * @param request
     * @return
     */
    @GetMapping("/findAllReturnedMoneyDetailForPortal")
    public List<CustomerReturnedMoneyAndDetailDto> findAllReturnedMoneyDetailForPortal(HttpServletRequest request) {
        Map<String, Object> searchParams = buildSearchParams(request);
        List<CustomerReturnedMoneyAndDetailDto> lists = customerReturnedMoneyService.queryReturnedMoneyDetailForPortal(searchParams);
        return lists;
    }

    /**
     * 中台——客户回款统计查询
     *
     * @param request
     * @return
     */
    @GetMapping("/findAllReturnedMoneyForMiddleground")
    public List<CustomerReturnedMoneyAndDetailDto> findAllReturnedMoneyForMiddleground(HttpServletRequest request, Pageable pageable) {
        Map<String, Object> searchParams = buildSearchParams(request);
        List<CustomerReturnedMoneyAndDetailDto> lists = customerReturnedMoneyService.queryReturnedMoney(searchParams);
        return lists;
    }

    /**
     * 中台——客户回款分解明细查询
     *
     * @param request
     * @return
     */
    @GetMapping("/findAllReturnedMoneyDetailForMiddleground")
    public List<CustomerReturnedMoneyAndDetailDto> findAllReturnedMoneyDetailForMiddleground(HttpServletRequest request) {
        Map<String, Object> searchParams = buildSearchParams(request);
        List<CustomerReturnedMoneyAndDetailDto> lists = customerReturnedMoneyService.queryReturnedMoneyDetail(searchParams);
        return lists;
    }

    /**
     * 导出客户汇款统计表数据
     *
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/exportData")
    public void exportExcelData(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String dateStr = DateUtil.from_Date_to_String(DateUtil.yyMMddHHmmss, new Date());
        EasyPoiUtils easyPoiUtils = new EasyPoiUtils();
        Map<String, Object> searchParams = buildSearchParams(request);
        List<CustomerReturnedMoneyVO> customerReturnedMoneyVOLists = customerReturnedMoneyService.exportExcelData(searchParams, response);
        easyPoiUtils.exportExcel(customerReturnedMoneyVOLists, "客户回款统计表", "客户回款统计表", CustomerReturnedMoneyVO.class, "客户回款统计_" + dateStr + ".xls", true, response);
    }

    /**
     * 导出客户汇款分解明细表数据
     *
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/exportDetailData")
    public void exportDetailExcelData(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String dateStr = DateUtil.from_Date_to_String(DateUtil.yyMMddHHmmss, new Date());
        EasyPoiUtils easyPoiUtils = new EasyPoiUtils();
        Map<String, Object> searchParams = buildSearchParams(request);
        List<CustomerReturnedMoneyDetailVO> customerReturnedMoneyDetailVOS = customerReturnedMoneyService.exportDetailExcelData(searchParams);
        easyPoiUtils.exportExcel(customerReturnedMoneyDetailVOS, "客户回款分解明细表", "客户回款分解明细表", CustomerReturnedMoneyDetailVO.class, "客户回款分解明细表_" + dateStr + ".xls", true, response);
    }
}
