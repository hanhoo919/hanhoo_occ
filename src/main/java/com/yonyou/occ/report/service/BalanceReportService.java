package com.yonyou.occ.report.service;

import com.yonyou.occ.b2b.client.PrivilegeClient;
import com.yonyou.occ.report.service.dto.BalanceReportDto;
import com.yonyou.occ.report.service.handler.BalanceReportExcelHandler;
import com.yonyou.occ.report.service.handler.BalanceReportSearchHandler;
import com.yonyou.ocm.common.exception.BusinessException;
import com.yonyou.ocm.common.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 代理商账余报表 服务类
 */
@Slf4j
@Service("BalanceReportService")
public class BalanceReportService {

    @Autowired
    private BalanceReportSearchHandler balanceReportSearchHandler;

    @Autowired
    private BalanceReportExcelHandler balanceReportExelHandler;

    @Autowired
    private PrivilegeClient privilegeClient;


    /**
     * 账余报表数据查询
     *
     * @param searchParams
     * @return
     */
    public List<BalanceReportDto> balanceReport(Map<String, Object> searchParams) {
        List<BalanceReportDto> balanceReportDtoList = null;
        //判断是否输入时间段
        checkDate(searchParams);

        balanceReportDtoList = balanceReportSearchHandler.doSearch(searchParams);
        return balanceReportDtoList;
    }


    /**
     * 账余报表数据导出
     */
    public Map<String, String> exportExcelData(Map<String, Object> searchParams, HttpServletResponse response) {
        //判断是否输入时间段
        checkDate(searchParams);
        return balanceReportExelHandler.exportExcelData(searchParams, response, "代理商账余报表");
    }


    /**
     * 账余报表数据查询 - 门户端用
     *
     * @param searchParams
     * @return
     */
    public List<BalanceReportDto> balanceReportForShop(Map<String, Object> searchParams) {
        List<BalanceReportDto> balanceReportDtoList = null;
        //判断是否输入时间段
        checkDate(searchParams);

        //获取当前登录用户id
        String userId = CommonUtils.getCurrentUserId();
        //获取当前用户绑定的渠道商id
        String customerId = privilegeClient.getChannelCustomerIdByUserId(userId);
        if (StringUtils.isBlank(customerId)) {
            throw new BusinessException("查询不到当前用户对应的渠道商，请联系管理员");
        }
        searchParams.put("customerId", customerId);

        balanceReportDtoList = balanceReportSearchHandler.doSearch(searchParams);
        return balanceReportDtoList;
    }

    /**
     * 校验参数日期合法性
     *
     * @param searchParams
     */
    private void checkDate(Map<String, Object> searchParams) {
        if (searchParams.containsKey("startDate") && searchParams.containsKey("endDate")) {
            //判断时间间隔是否超过1年
            Date startDate = new Date(Long.parseLong(searchParams.get("startDate").toString()));
            Date endDate = new Date(Long.parseLong(searchParams.get("endDate").toString()));

            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(startDate);
            startCalendar.add(Calendar.YEAR, 1);
            if (startCalendar.getTime().compareTo(endDate) <= 0) {
                throw new BusinessException("开始时间和结束时间间隔超过一年");
            }

        } else {
            throw new BusinessException("开始时间和结束时间是必输的条件");
        }
    }

}

