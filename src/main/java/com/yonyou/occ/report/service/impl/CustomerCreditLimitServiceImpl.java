package com.yonyou.occ.report.service.impl;

import com.yonyou.occ.b2b.client.PrivilegeClient;
import com.yonyou.occ.report.entity.CustomerCreditStatisticsEntity;
import com.yonyou.occ.report.service.CustomerCreditLimitService;
import com.yonyou.occ.report.service.dto.CustomerCreditStatisticsDto;
import com.yonyou.occ.report.utils.BeanConverterUtils;
import com.yonyou.occ.report.vo.CustomerCreditStatisticsVO;
import com.yonyou.ocm.common.exception.BusinessException;
import com.yonyou.ocm.common.utils.CommonUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 客户授信统计实现类
 * @author 梁松流
 */
@Service
public class CustomerCreditLimitServiceImpl implements CustomerCreditLimitService {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PrivilegeClient privilegeClient;

    @Override
    public List<CustomerCreditStatisticsDto> queryCreditLimitForPortal(Map<String, Object> searchParams) {

        //获取当前登录用户id
        String userId = CommonUtils.getCurrentUserId();
        //获取当前用户绑定的渠道商id
        String customerId = privilegeClient.getChannelCustomerIdByUserId(userId);
        if (StringUtils.isEmpty(customerId)) {
            throw new BusinessException("查询不到当前用户对应的渠道商，请联系管理员");
        }
        searchParams.put("customerId", customerId);

        List<CustomerCreditStatisticsEntity> customerCreditStatisticsEntities = getEntityLists(searchParams);
        if(!StringUtils.isEmpty(customerCreditStatisticsEntities) && customerCreditStatisticsEntities.size() > 0){
            List<CustomerCreditStatisticsDto> creditStatisticsDtoList = new ArrayList<>();
            for(CustomerCreditStatisticsEntity entity : customerCreditStatisticsEntities){
                CustomerCreditStatisticsDto dto = BeanConverterUtils.copyProperties(entity, CustomerCreditStatisticsDto.class);
                dto.setCreditBalance(dto.getInitialCreditBalance().add(dto.getCreditLimitAmount().subtract(dto.getClaimAmount())));
                creditStatisticsDtoList.add(dto);
            }
            return creditStatisticsDtoList;
        }
        return null;
    }

    @Override
    public List<CustomerCreditStatisticsDto> queryCreditLimitForMiddleground(Map<String, Object> searchParams) {
        List<CustomerCreditStatisticsEntity> customerCreditStatisticsEntities = getEntityLists(searchParams);
        if(!StringUtils.isEmpty(customerCreditStatisticsEntities) && customerCreditStatisticsEntities.size() > 0){
            List<CustomerCreditStatisticsDto> creditStatisticsDtoList = new ArrayList<>();
            for(CustomerCreditStatisticsEntity entity : customerCreditStatisticsEntities){
                CustomerCreditStatisticsDto dto = BeanConverterUtils.copyProperties(entity, CustomerCreditStatisticsDto.class);
                dto.setCreditBalance(dto.getInitialCreditBalance().add(dto.getCreditLimitAmount().subtract(dto.getClaimAmount())));
                creditStatisticsDtoList.add(dto);
            }
            return creditStatisticsDtoList;
        }
        return null;
    }

    /**
     * 导出查询得到的数据
     * @param searchParams
     * @return
     */
    @Override
    public List<CustomerCreditStatisticsVO> exportData(Map<String, Object> searchParams) {
        List<CustomerCreditStatisticsEntity> customerCreditStatisticsEntities = getEntityLists(searchParams);
        if(!StringUtils.isEmpty(customerCreditStatisticsEntities)){
            List<CustomerCreditStatisticsVO> customerCreditStatisticsVOList = new ArrayList<>();
            for(CustomerCreditStatisticsEntity entity : customerCreditStatisticsEntities){
                CustomerCreditStatisticsVO vo = BeanConverterUtils.copyProperties(entity, CustomerCreditStatisticsVO.class);
                customerCreditStatisticsVOList.add(vo);
            }
            return customerCreditStatisticsVOList;
        }
        return null;
    }

    /**
     * 获取查询结果集
     * @param searchParams
     * @return
     */
    private List<CustomerCreditStatisticsEntity> getEntityLists(Map<String, Object> searchParams) {
        StringBuffer querySql = new StringBuffer("");
        createSql(querySql, searchParams);

        Query query = this.entityManager.createNativeQuery(querySql.toString(), CustomerCreditStatisticsEntity.class);
        List<CustomerCreditStatisticsEntity> customerCreditStatisticsEntities = query.getResultList();
        return customerCreditStatisticsEntities;
    }

    /**
     * 拼接sql语句
     *
     * @param querySql
     * @param searchParams
     */
    private void createSql(StringBuffer querySql, Map<String, Object> searchParams) {
        querySql.append(" SELECT ");
        querySql.append(" tb.credit_limit_id, tb.channel_name, tb.market_area_name, tb.customer_code, ");
        querySql.append(" tb.customer_name, tb.activity_start_date, tb.activity_code, ");
        querySql.append(" tb.activity_name, tb.credit_start_date, tb.credit_end_date,");
        querySql.append(" sum(tb.credit_balance) AS initial_credit_balance, sum(tb.credit_limit_amount) AS credit_limit_amount,");
        querySql.append(" sum(tb.claim_amount) AS claim_amount, li.creation_time ");
        querySql.append(" FROM (");
        querySql.append(" SELECT ");
        querySql.append(" tb7.id  AS credit_limit_id, tb9.name AS channel_name, tb10.name AS market_area_name,");
        querySql.append(" tb8.code AS customer_code, tb8.name AS customer_name, tb1.start_date AS activity_start_date,");
        querySql.append(" tb1.code AS activity_code, tb1.name AS activity_name, tb7.start_date AS credit_start_date,");
        querySql.append(" tb7.end_date AS credit_end_date, sum(tb4.claim_amount) AS claim_amount, sum(tb7.credit_limit) AS credit_limit_amount,");
        querySql.append(" to_number('0') AS credit_balance, count(tb7.creation_time) AS creation_time");
        querySql.append(" FROM ");
        querySql.append(" prom_activity tb1");
        querySql.append(" INNER JOIN prom_activity_ext tb2 ON tb1.id = tb2.id");
        querySql.append(" INNER JOIN prom_ct_sale tb3 ON tb1.id = tb3.activity_id");
        querySql.append(" INNER JOIN prom_claim tb4 ON tb3.id = tb4.prom_ct_id");
        querySql.append(" INNER JOIN prom_claim_detail tb5 ON tb4.id = tb5.prom_claim_id");
        querySql.append(" INNER JOIN cr_credit_limit_ext tb6 ON tb1.id = tb6.activity_id");
        querySql.append(" INNER JOIN cr_credit_limit tb7 ON tb6.id = tb7.id");
        querySql.append(" INNER JOIN cr_credit_ctrl_strategy str ON tb7.credit_ctrl_strategy_id = str.id");
        querySql.append(" LEFT JOIN base_customer tb8 ON tb7.customer_id = tb8.id");
        querySql.append(" LEFT JOIN base_customer_category tb9 ON tb8.customer_category_id = tb9.id");
        querySql.append(" LEFT JOIN base_market_area tb10 ON tb8.market_area_id = tb10.id");
        querySql.append(" WHERE ");
        querySql.append(" nvl(tb1.dr, 0) = 0 AND nvl(tb2.dr, 0) = 0 AND nvl(tb3.dr, 0) = 0");
        querySql.append(" AND nvl(tb4.dr, 0) = 0 AND nvl(tb5.dr, 0) = 0 AND nvl(tb7.dr, 0) = 0");
        querySql.append(" AND nvl(tb8.dr, 0) = 0 AND nvl(tb9.dr, 0) = 0 AND nvl(tb10.dr, 0) = 0");
        querySql.append(" AND tb2.credit_mode <> 0 AND tb2.credit_mode IS NOT NULL AND tb1.state = 3 AND tb1.activity_status <> 0 ");

        if(!StringUtils.isEmpty(searchParams)){
            //渠道
            if (!StringUtils.isEmpty(searchParams.get("channelName"))) {
                querySql.append(" and tb9.name = '");
                querySql.append(searchParams.get("channelName"));
                querySql.append("'");
            }
            //市场区域
            if (!StringUtils.isEmpty(searchParams.get("marketAreaName"))) {
                querySql.append(" and tb10.name = '");
                querySql.append(searchParams.get("marketAreaName"));
                querySql.append("'");
            }

            //客户Id
            if (!StringUtils.isEmpty(searchParams.get("customerId"))) {
                querySql.append(" and tb8.id = '");
                querySql.append(searchParams.get("customerId"));
                querySql.append("'");
            }

            //客户编码
            if (!StringUtils.isEmpty(searchParams.get("customerCode"))) {
                querySql.append(" and tb8.code = '");
                querySql.append(searchParams.get("customerCode"));
                querySql.append("'");
            }

            //客户名称
            if (!StringUtils.isEmpty(searchParams.get("customerName"))) {
                querySql.append(" and tb8.name = '");
                querySql.append(searchParams.get("customerName"));
                querySql.append("'");
            }

            //活动编码
            if (!StringUtils.isEmpty(searchParams.get("activityCode"))) {
                querySql.append(" and tb1.code = '");
                querySql.append(searchParams.get("activityCode"));
                querySql.append("'");
            }
            //活动名称
            if (!StringUtils.isEmpty(searchParams.get("activityName"))) {
                querySql.append(" and tb1.name = '");
                querySql.append(searchParams.get("activityName"));
                querySql.append("'");
            }

            //活动有效开始日期 活动有效结束日期
            if(!StringUtils.isEmpty(searchParams.get("activityBeginDate")) && !StringUtils.isEmpty(searchParams.get("activityEndDate"))){
                querySql.append(" AND (tb1.start_date BETWEEN to_date('");
                querySql.append(searchParams.get("activityBeginDate"));
                querySql.append("', 'yyyy-mm-dd HH24:mi:ss')");

                querySql.append(" AND to_date('");
                querySql.append(searchParams.get("activityEndDate"));
                querySql.append("', 'yyyy-mm-dd HH24:mi:ss')");
                querySql.append(")");
            }else{
                checkDate(searchParams.get("activityBeginDate") + "", searchParams.get("activityEndDate") + "");
            }

            //活动有效结束日期
//            if(!StringUtils.isEmpty(searchParams.get("activityEndDate"))){
//                querySql.append(" AND to_date('");
//                querySql.append(searchParams.get("activityEndDate"));
//                querySql.append("', 'yyyy-mm-dd hh24:mi:ss'))");
//                querySql.append(")");
//            }
        }
        querySql.append(" GROUP BY ");
        querySql.append(" tb7.id, tb9.name, tb10.name, tb8.code, tb8.name,");
        querySql.append(" tb1.start_date, tb1.code, tb1.name, tb7.start_date, tb7.end_date ");

        querySql.append(" ");
        querySql.append(" \t\n UNION \t\n");
        querySql.append(" ");

        querySql.append(" SELECT ");
        querySql.append(" tb7.id AS credit_limit_id, tb9.name AS channel_name, tb10.name AS market_area_name,");
        querySql.append(" tb8.code AS customer_code, tb8.name AS customer_name, tb1.start_date AS activity_start_date,");
        querySql.append(" tb1.code AS activity_code, tb1.name AS activity_name, tb7.start_date AS credit_start_date,");
        querySql.append(" tb7.end_date AS credit_end_date, to_number('0') AS claim_amount, to_number('0') AS credit_limit_amount,");
        querySql.append(" sum(tb7.credit_limit) AS credit_balance, count(tb7.creation_time) AS creation_time");
        querySql.append(" FROM ");
        querySql.append(" prom_activity tb1 ");
        querySql.append(" INNER JOIN prom_activity_ext tb2 ON tb1.id = tb2.id");
        querySql.append(" INNER JOIN prom_ct_sale tb3 ON tb1.id = tb3.activity_id");
        querySql.append(" INNER JOIN prom_claim tb4 ON tb3.id = tb4.prom_ct_id");
        querySql.append(" INNER JOIN prom_claim_detail tb5 ON tb4.id = tb5.prom_claim_id");
        querySql.append(" INNER JOIN cr_credit_limit_ext tb6 ON tb1.id = tb6.activity_id");
        querySql.append(" INNER JOIN cr_credit_limit tb7 ON tb6.id = tb7.id");
        querySql.append(" INNER JOIN cr_credit_ctrl_strategy str ON tb7.credit_ctrl_strategy_id = str.id");
        querySql.append(" LEFT JOIN base_customer tb8 ON tb7.customer_id = tb8.id");
        querySql.append(" LEFT JOIN base_customer_category tb9 ON tb8.customer_category_id = tb9.id");
        querySql.append(" LEFT JOIN base_market_area tb10 ON tb8.market_area_id = tb10.id");
        querySql.append(" WHERE ");
        querySql.append(" nvl(tb1.dr, 0) = 0 AND nvl(tb2.dr, 0) = 0 AND nvl(tb3.dr, 0) = 0");
        querySql.append(" AND nvl(tb4.dr, 0) = 0 AND nvl(tb5.dr, 0) = 0 AND nvl(tb7.dr, 0) = 0");
        querySql.append(" AND nvl(tb8.dr, 0) = 0 AND nvl(tb9.dr, 0) = 0 AND nvl(tb10.dr, 0) = 0");
        querySql.append(" AND tb2.credit_mode <> 0 AND tb2.credit_mode IS NOT NULL AND tb1.state = 3 AND tb1.activity_status <> 0");
        if(!StringUtils.isEmpty(searchParams)){
            //渠道
            if (!StringUtils.isEmpty(searchParams.get("channelName"))) {
                querySql.append(" and tb9.name = '");
                querySql.append(searchParams.get("channelName"));
                querySql.append("'");
            }
            //市场区域
            if (!StringUtils.isEmpty(searchParams.get("marketAreaName"))) {
                querySql.append(" and tb10.name = '");
                querySql.append(searchParams.get("marketAreaName"));
                querySql.append("'");
            }

            //客户Id
            if (!StringUtils.isEmpty(searchParams.get("customerId"))) {
                querySql.append(" and tb8.id = '");
                querySql.append(searchParams.get("customerId"));
                querySql.append("'");
            }

            //客户编码
            if (!StringUtils.isEmpty(searchParams.get("customerCode"))) {
                querySql.append(" and tb8.code = '");
                querySql.append(searchParams.get("customerCode"));
                querySql.append("'");
            }

            //客户名称
            if (!StringUtils.isEmpty(searchParams.get("customerName"))) {
                querySql.append(" and tb8.name = '");
                querySql.append(searchParams.get("customerName"));
                querySql.append("'");
            }

            //活动编码
            if (!StringUtils.isEmpty(searchParams.get("activityCode"))) {
                querySql.append(" and tb1.code = '");
                querySql.append(searchParams.get("activityCode"));
                querySql.append("'");
            }

            //活动名称
            if (!StringUtils.isEmpty(searchParams.get("activityName"))) {
                querySql.append(" and tb1.name = '");
                querySql.append(searchParams.get("activityName"));
                querySql.append("'");
            }

            //授信开始有效期
            if(!StringUtils.isEmpty(searchParams.get("activityBeginDate"))){
                querySql.append(" and tb1.start_date < to_date('");
                querySql.append(searchParams.get("activityBeginDate"));
                querySql.append("', 'yyyy-mm-dd HH24:mi:ss')");
            }
        }

        querySql.append(" GROUP BY ");
        querySql.append(" tb7.id, tb9.name, tb10.name, tb8.code, tb8.name, tb1.start_date,");
        querySql.append(" tb1.code, tb1.name, tb7.start_date, tb7.end_date");
        querySql.append(" ) tb \t");
        querySql.append(" INNER JOIN cr_credit_limit li ON tb.credit_limit_id = li.id ");
        querySql.append(" WHERE ");
        querySql.append(" nvl(li.dr, 0) = 0 ");
        querySql.append(" GROUP BY ");
        querySql.append(" tb.credit_limit_id, tb.channel_name,tb.market_area_name,");
        querySql.append(" tb.customer_code, tb.customer_name, tb.activity_start_date,");
        querySql.append(" tb.activity_code, tb.activity_name, tb.credit_start_date,");
        querySql.append(" tb.credit_end_date, li.creation_time");
        querySql.append(" ORDER BY tb.credit_limit_id");
    }

    /**
     * 校验查询日期合法性
     *
     * @param beginDate
     * @param endDate
     */
    private void checkDate(String beginDate, String endDate) {
        if (org.apache.commons.lang3.StringUtils.isBlank(beginDate) || org.apache.commons.lang3.StringUtils.isBlank(endDate)) {
            throw new BusinessException("政策开始日期和结束日期不能为空");
        }
        if (!isValidDate(beginDate) || !isValidDate(endDate)) {
            throw new BusinessException("查询日期格式不正确，正确格式如：2019-10-10 10:10:10");
        }
        if (Integer.valueOf(beginDate) > Integer.valueOf(endDate)) {
            throw new BusinessException("查询开始日期应小于结束日期");
        }

        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTime dateTime = DateTime.parse(beginDate, dateTimeFormatter);
        DateTime beginDateNextYear = dateTime.plusYears(1);
        String strBeginDateNextYear = beginDateNextYear.toString("yyyy-MM-dd HH:mm:ss");
        if (Integer.valueOf(endDate) > Integer.valueOf(strBeginDateNextYear)) {
            throw new BusinessException(String.format("请控制查询开始日期【%s】与结束日期【%s】的跨度在1年以内", beginDate, endDate));
        }
    }

    /**
     * 日期格式是否合法
     *
     * @param str
     * @return
     */
    private boolean isValidDate(String str) {
        boolean convertSuccess = true;
        // 指定日期格式为四位年/两位月份/两位日期
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            format.setLenient(false);
            format.parse(str);
        } catch (ParseException e) {
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            convertSuccess = false;
        }
        return convertSuccess;
    }
}
