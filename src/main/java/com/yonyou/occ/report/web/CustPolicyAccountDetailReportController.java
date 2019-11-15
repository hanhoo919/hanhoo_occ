package com.yonyou.occ.report.web;

import com.yonyou.occ.b2b.client.CustomerClient;
import com.yonyou.occ.b2b.client.PrivilegeClient;
import com.yonyou.occ.report.service.ICustPolicyAccountDetailReportService;
import com.yonyou.occ.report.service.dto.CustPolicyAccountDetailDto;
import com.yonyou.occ.report.service.handler.CustPolicyAccountDetailReportExcelHandler;
import com.yonyou.ocm.common.exception.BusinessException;
import com.yonyou.ocm.common.utils.CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springside.modules.web.Servlets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 客户政策账余明细表 控制器类
 *
 * @author Davis tang
 * @date 2019/08/27
 */
@RestController
@RequestMapping("/report/cust-policy-account-details")
public class CustPolicyAccountDetailReportController extends BaseController {

    @Autowired
    private ICustPolicyAccountDetailReportService custPolicyAccountDetailReportService;

    @Autowired
    private CustPolicyAccountDetailReportExcelHandler custPolicyAccountDetailReportExcelHandler;

    @Autowired
    private PrivilegeClient privilegeClient;

    @Autowired
    private CustomerClient customerClient;

    /**
     * 根据客户编码或客户名称、活动编码或活动名称、查询开始时间、查询结束时间，获取客户政策账余明细数据集合
     * @param customerCode 客户编码，可为空
     * @param customerName 客户名称，可为空
     * @param activityCode 活动编码，可为空
     * @param activityName 活动名称，可为空
     * @param beginDate 查询开始时间，非空
     * @param endDate 查询结束时间，非空
     * @return
     */
//    @GetMapping
//    public List<CustPolicyAccountDetailDto> getCustPolicyAccountDetails(@RequestParam(value = "customerCode", required = false) String customerCode,
//                                                                                  @RequestParam(value = "customerName", required = false) String customerName,
//                                                                                  @RequestParam(value = "activityCode", required = false) String activityCode,
//                                                                                  @RequestParam(value = "activityName", required = false) String activityName,
//                                                                                  @RequestParam(value = "beginDate") String beginDate,
//                                                                                  @RequestParam(value = "endDate") String endDate) {
//        checkDate(beginDate, endDate);
//        List<CustPolicyAccountDetailDto> list = custPolicyAccountDetailReportService.getCustPolicyAccountDetails(customerCode, customerName, activityCode, activityName,beginDate, endDate);
////        ResponseEntity<CustPolicyAccountDetailDto> responseEntity = ResponseEntity.ok(list);
//        return list;
//    }

    /**
     * 中台报表查询 - 根据请求对象（包含：客户编码或客户名称、活动编码或活动名称、查询开始时间、查询结束时间），获取客户政策账余明细数据集合
     *
     * @param request
     * @return
     */
    @GetMapping("/findAll")
    public List<CustPolicyAccountDetailDto> getCustPolicyAccountDetails(HttpServletRequest request) {
        Map<String, Object> searchParams = buildSearchParams(request);
        String customerCode = (String) searchParams.get("customerCode");
        String customerName = (String) searchParams.get("customerName");
        String activityCode = (String) searchParams.get("activityCode");
        String activityName = (String) searchParams.get("activityName");
        String beginDate = (String) searchParams.get("beginDate");
        String endDate = (String) searchParams.get("endDate");

        List<CustPolicyAccountDetailDto> list = custPolicyAccountDetailReportService.getCustPolicyAccountDetails(customerCode, customerName, activityCode, activityName, beginDate, endDate);
        return list;
    }

    /**
     * 门户报表查询 - 根据请求对象（包含：客户编码或客户名称、活动编码或活动名称、查询开始时间、查询结束时间），获取客户政策账余明细数据集合
     *
     * @param request
     * @return
     */
    @GetMapping("/findAllForPortal")
    public List<CustPolicyAccountDetailDto> getCustPolicyAccountDetailsForPortal(HttpServletRequest request) {
        Map<String, Object> searchParams = buildSearchParams(request);
        String activityCode = (String) searchParams.get("activityCode");
        String activityName = (String) searchParams.get("activityName");
        String beginDate = (String) searchParams.get("beginDate");
        String endDate = (String) searchParams.get("endDate");

        // 获取当前登录用户id
        String userId = CommonUtils.getCurrentUserId();
        // 获取当前用户绑定的渠道商id
        String customerId = privilegeClient.getChannelCustomerIdByUserId(userId);
        if (StringUtils.isBlank(customerId)) {
            throw new BusinessException("查询不到当前用户对应的渠道商id，请联系管理员");
        }
        String customerCode = customerClient.getById(customerId).getBody().getCode();
        if (StringUtils.isBlank(customerCode)) {
            throw new BusinessException("查询不到当前用户对应的渠道商编码，请联系管理员");
        }

        List<CustPolicyAccountDetailDto> list = custPolicyAccountDetailReportService.getCustPolicyAccountDetails(customerCode, null, activityCode, activityName, beginDate, endDate);
        return list;
    }

    @GetMapping("/exportExcelData")
    public Map<String, String> exportExcelData(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> searchParams = buildSearchParams(request);
        return custPolicyAccountDetailReportExcelHandler.exportExcelData(searchParams, response, "客户政策账余明细表");
    }


}
