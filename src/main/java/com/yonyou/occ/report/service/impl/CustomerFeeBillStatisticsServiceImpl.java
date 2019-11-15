package com.yonyou.occ.report.service.impl;

import com.yonyou.occ.b2b.client.PrivilegeClient;
import com.yonyou.occ.report.entity.CustomerFeeBillStatisticsEntity;
import com.yonyou.occ.report.service.CustomerFeeBillStatisticsService;
import com.yonyou.occ.report.service.dto.CustomerFeeBillStatisticsDto;
import com.yonyou.occ.report.utils.BeanConverterUtils;
import com.yonyou.occ.report.vo.CustomerCostBillStatisticVO;
import com.yonyou.ocm.common.exception.BusinessException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 客户费用单统计实现类
 * @author lsl
 */
@Service
public class CustomerFeeBillStatisticsServiceImpl implements CustomerFeeBillStatisticsService {
    
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PrivilegeClient privilegeClient;

    /**
     * 获取客户费用相关信息——门户
     *
     * @param searchParams
     * @return
     */
    @Override
    public List<CustomerFeeBillStatisticsDto> queryAllCustomerCostForPortal(Map<String, Object> searchParams) {
        //获取当前登录用户id
//        String userId = CommonUtils.getCurrentUserId();
//        //获取当前用户绑定的渠道商id
//        String channelId = privilegeClient.getChannelCustomerIdByUserId(userId);
//        if (StringUtils.isEmpty(channelId)) {
//            throw new BusinessException("查询不到当前用户对应的渠道商，请联系管理员");
//        }
//        searchParams.put("channelId", channelId);

        List<CustomerFeeBillStatisticsDto> dtoLists = getDtoLists(searchParams);

        return dtoLists;
    }

    /**
     * 获取客户费用相关信息——中台
     *
     * @param searchParams
     * @return
     */
    @Override
    public List<CustomerFeeBillStatisticsDto> queryAllCustomerCostForMiddleground(Map<String, Object> searchParams) {
        List<CustomerFeeBillStatisticsDto> dtoLists = getDtoLists(searchParams);
        return dtoLists;
    }

    /**
     * 数据导出
     *
     * @param searchParams
     * @param response
     * @return
     */
    @Override
    public List<CustomerCostBillStatisticVO> exportExcelData(Map<String, Object> searchParams, HttpServletResponse response) {
        StringBuffer querySQL = new StringBuffer("");
        createSQL(querySQL, searchParams);
        Query query = this.entityManager.createNativeQuery(querySQL.toString(), CustomerFeeBillStatisticsEntity.class);
        List<CustomerFeeBillStatisticsEntity> entityLists = query.getResultList();

        String beginOutDate = searchParams.get("beginOutDate").toString();
        String endOutDate = searchParams.get("endOutDate").toString();
        if(!StringUtils.isEmpty(entityLists) && entityLists.size() > 0){
            List<CustomerCostBillStatisticVO> customerFeeBillStatisticsVOs = new ArrayList<>();

            for(CustomerFeeBillStatisticsEntity entity : entityLists){
                CustomerCostBillStatisticVO feeBillStatisticsVO = BeanConverterUtils.copyProperties(entity, CustomerCostBillStatisticVO.class);
                String customerId = feeBillStatisticsVO.getCustomerId();
                String castTypeId = feeBillStatisticsVO.getCastTypeId();
                //期初余额计算
                BigDecimal openingBalance = calculateOpeningBalance(customerId, castTypeId, beginOutDate, endOutDate);
                feeBillStatisticsVO.setOpeningBalance(openingBalance);

                //本期增加金额
                BigDecimal currentIncreases = feeBillStatisticsVO.getCurrentIncreases();

                //本期减少金额计算
                BigDecimal totalOutAmount = calculateTotalOutAmount(customerId, castTypeId, beginOutDate, endOutDate);
                feeBillStatisticsVO.setTotalOutAmount(totalOutAmount);

                //计算本期余额  本期余额=期初余额+本期增加金额-本期减少金额
                BigDecimal currentBalances = openingBalance.add(currentIncreases).subtract(totalOutAmount);
                feeBillStatisticsVO.setCurrentBalance(currentBalances);

                customerFeeBillStatisticsVOs.add(feeBillStatisticsVO);
            }
            return customerFeeBillStatisticsVOs;
        }

        return null;
    }

    /**
     * 获取客户信息
     * @param searchParams
     * @return
     */
    private List<CustomerFeeBillStatisticsDto> getDtoLists(Map<String, Object> searchParams) {
        StringBuffer querySQL = new StringBuffer("");
        createSQL(querySQL, searchParams);
        Query query = this.entityManager.createNativeQuery(querySQL.toString(), CustomerFeeBillStatisticsEntity.class);
        List<CustomerFeeBillStatisticsEntity> entityLists = query.getResultList();

        String beginOutDate = searchParams.get("beginOutDate").toString();
        String endOutDate = searchParams.get("endOutDate").toString();
        if(!StringUtils.isEmpty(entityLists) && entityLists.size() > 0){
            List<CustomerFeeBillStatisticsDto> customerFeeBillStatisticsDtos = new ArrayList<>();

            for(CustomerFeeBillStatisticsEntity entity : entityLists){
                CustomerFeeBillStatisticsDto feeBillStatisticsDto = BeanConverterUtils.copyProperties(entity, CustomerFeeBillStatisticsDto.class);
                String customerId = feeBillStatisticsDto.getCustomerId();
                String castTypeId = feeBillStatisticsDto.getCastTypeId();
                //期初余额计算
                BigDecimal openingBalance = calculateOpeningBalance(customerId, castTypeId, beginOutDate, endOutDate);
                feeBillStatisticsDto.setOpeningBalance(openingBalance);

                //本期增加金额
                BigDecimal currentIncreases = feeBillStatisticsDto.getCurrentIncreases();

                //本期减少金额计算
                BigDecimal totalOutAmount = calculateTotalOutAmount(customerId, castTypeId, beginOutDate, endOutDate);
                feeBillStatisticsDto.setTotalOutAmount(totalOutAmount);

                //计算本期余额  本期余额=期初余额+本期增加金额-本期减少金额
                BigDecimal currentBalances = openingBalance.add(currentIncreases).subtract(totalOutAmount);
                feeBillStatisticsDto.setCurrentBalance(currentBalances);

                customerFeeBillStatisticsDtos.add(feeBillStatisticsDto);
            }
            return customerFeeBillStatisticsDtos;
        }

        return null;
    }

    /**
     * 拼接sql，查询客户信息
     * @param querySQL
     * @param searchParams
     */
    private void createSQL(StringBuffer querySQL, Map<String, Object> searchParams) {
        querySQL.append("SELECT DISTINCT \n  " +
                " (bc.id || fa.cast_type_id) AS id, bc.id AS customer_id, bcc.name AS customer_category_name, bad.full_name AS province_name, area.name AS market_area_name, \n " +
                " bc.code AS customer_code, bc.name AS customer_name, fa.cast_type_id, \n " +
                " (CASE fa.cast_type_id WHEN 'pay01' THEN '冲抵订单' WHEN 'pay02' THEN '货补' WHEN 'pay04' THEN '账扣' " +
                " WHEN 'extpay80' THEN '核销' WHEN 'extpay81' THEN '返利' ELSE '' END ) AS cast_type_name, \n sum( fcbd.mny ) AS current_increases  \n ");
        querySQL.append(" FROM \n  ");
        querySQL.append(" fee_customer_bill fcb \n" +
                " INNER JOIN fee_customer_bill_detail fcbd ON fcb.id = fcbd.bill_id \n" +
                " INNER JOIN fee_account fa ON fcb.account_id = fa.id \n" +
                " INNER JOIN b2b_order_offset_details ood ON fa.id = ood.fee_account \n" +
                " LEFT JOIN base_customer bc ON bc.id = fcb.customer_id \n" +
                " LEFT JOIN base_customer_category bcc ON bc.customer_category_id = bcc.id \n" +
                " LEFT JOIN base_administrative_division bad ON bc.province_id = bad.id AND bad.area_level = 1 \n" +
                " LEFT JOIN base_market_area area ON bc.market_area_id = area.id \n");
        querySQL.append(" WHERE \n");
        querySQL.append("nvl( bc.dr, 0 ) = 0 \n " +
                "AND nvl( bcc.dr, 0 ) = 0 \n " +
                "AND nvl( bad.dr, 0 ) = 0 \n " +
                "AND nvl( area.dr, 0 ) = 0 \n " +
                "AND nvl( fcb.dr, 0 ) = 0 \n " +
                "AND nvl( fcbd.dr, 0 ) = 0 \n " +
                "AND nvl( fa.dr, 0 ) = 0 \n " +
                "AND fcb.state = 3 \n ");
        if(!StringUtils.isEmpty(searchParams)) {

            //期间开始日期 期间结束日期
            if (!StringUtils.isEmpty(searchParams.get("beginOutDate")) && !StringUtils.isEmpty(searchParams.get("endOutDate"))) {
                querySQL.append(" AND (fcb.bill_date BETWEEN to_date('");
                querySQL.append(searchParams.get("beginOutDate"));
                querySQL.append("', 'yyyy-mm-dd HH24:mi:ss') \n ");

                querySQL.append(" AND to_date('");
                querySQL.append(searchParams.get("endOutDate"));
                querySQL.append("', 'yyyy-mm-dd HH24:mi:ss')");
                querySQL.append(") \n ");
            } else {
                checkDate(searchParams.get("beginOutDate") + "", searchParams.get("endOutDate") + "");
            }

            //客户
            if(!StringUtils.isEmpty(searchParams.get("customerId")) && !StringUtils.isEmpty(searchParams.get("channelId"))) {//用于门户的查询
                querySQL.append(" \n and bc.id IN('");
                querySQL.append(searchParams.get("customerId"));
                querySQL.append("'");
                querySQL.append(", '");
                querySQL.append(searchParams.get("channelId"));
                querySQL.append("')");
            }else if (!StringUtils.isEmpty(searchParams.get("customerId"))) {
                querySQL.append(" \n and bc.id = '");
                querySQL.append(searchParams.get("customerId"));
                querySQL.append("'");
            }else if(!StringUtils.isEmpty(searchParams.get("channelId"))){//用于门户的查询
                querySQL.append(" \n and bc.id = '");
                querySQL.append(searchParams.get("channelId"));
                querySQL.append("'");
            }

            //费用类型
            if (!StringUtils.isEmpty(searchParams.get("castTypeId"))) {
                querySQL.append(" and fa.cast_type_id = '");
                querySQL.append(searchParams.get("castTypeId"));
                querySQL.append("' \n ");
            }
            querySQL.append("GROUP BY \n" +
                    " bc.id,\n bc.code,\n bc.name,\n fa.cast_type_id,\n" +
                    " bad.full_name,\n area.name,\n bcc.name ");
        }
    }

    /**
     * 根据条件计算期初余额
     * @param customerId
     * @param castTypeId
     * @return
     */
    private BigDecimal calculateOpeningBalance(String customerId, String castTypeId, String beginOutDate, String endOutDate) {
        if (StringUtils.isEmpty(beginOutDate) || StringUtils.isEmpty(endOutDate)) {
            throw new BusinessException("期间开始日期或结束日期为空");
        }
        StringBuffer querySQL = new StringBuffer("");
        querySQL.append(" SELECT (fcbd.mny - sum(ood.total_offset_amount)) \n");
        querySQL.append(" FROM \n");
        querySQL.append(" base_customer bc \n" +
                " INNER JOIN fee_customer_bill fcb ON bc.id = fcb.customer_id \n" +
                " INNER JOIN fee_customer_bill_detail fcbd ON fcb.id = fcbd.bill_id \n" +
                " INNER JOIN fee_account fa ON fcb.account_id = fa.id \n" +
                " INNER JOIN b2b_order_offset_item item ON fcb.id = item.fee_id and fcbd.id = item.fee_item_id \n" +
                " INNER JOIN b2b_order_offset_details ood ON item.offset_detail_id = ood.id \n");
        querySQL.append(" WHERE \n");
        querySQL.append(" nvl( bc.dr, 0 ) = 0 \n" +
                " AND nvl( fcb.dr, 0 ) = 0 \n" +
                " AND nvl( fcbd.dr, 0 ) = 0 \n" +
                " AND nvl( fa.dr, 0 ) = 0 \n" +
                " AND nvl( item.dr, 0 ) = 0 \n" +
                " AND fcb.state = 3 \n");

        if(!StringUtils.isEmpty(beginOutDate)){
            querySQL.append("\n AND (fcb.bill_date < to_date('");
            querySQL.append(beginOutDate);
            querySQL.append("', 'yyyy-mm-dd HH24:mi:ss'))");
        }

        //客户
        if (!StringUtils.isEmpty(customerId)) {
            querySQL.append(" \n and bc.id = '");
            querySQL.append(customerId);
            querySQL.append("'");
        }

        //费用类型
        if (!StringUtils.isEmpty(castTypeId)) {
            querySQL.append(" \n and fa.cast_type_id = '");
            querySQL.append(castTypeId);
            querySQL.append("'");
        }
        querySQL.append(" \n GROUP BY fcbd.mny ");
        Query query = this.entityManager.createNativeQuery(querySQL.toString());
        List<BigDecimal> result = query.getResultList();
        if(StringUtils.isEmpty(result) || result.size() <= 0){
            return BigDecimal.ZERO;
        }else {
            return result.get(0);
        }
    }

    /**
     * 根据条件计算本期减少金额
     * 如果销售订单关闭，本期减少金额=实发数量*成交价
     * 如果销售订单未关闭，则 本期减少金额=实发数量*成交价 + 未发数量（未发数量=应发数量-实发数量）*成交价的
     * @param customerId
     * @param castTypeId
     * @param beginOutDate
     * @param endOutDate
     * @return
     */
    private BigDecimal calculateTotalOutAmount(String customerId, String castTypeId, String beginOutDate, String endOutDate) {
        StringBuffer querySql = new StringBuffer("");
        querySql.append("SELECT\n" +
                " nvl( sum( ssooi.fact_out_num * boi.deal_price ), 0) AS total_out_amount\n" +
                " FROM\n" +
                " stock_sale_out_order_item ssooi\n" +
                " INNER JOIN b2b_order_item boi ON boi.id = ssooi.first_bill_bcode\n" +
                " INNER JOIN b2b_order bo ON ssooi.first_bill_code = bo.id\n" +
                " INNER JOIN base_customer bc ON bo.customer_id = bc.id\n" +
                " INNER JOIN fee_customer_bill fcb ON bc.id = fcb.customer_id\n" +
                " INNER JOIN fee_account fa ON fcb.account_id = fa.id \n" +
                " WHERE\n" +
                " nvl( bo.dr, 0 ) = 0 \n" +
                " AND nvl( boi.dr, 0 ) = 0 \n" +
                " AND nvl( ssooi.dr, 0 ) = 0 \n" +
                " AND nvl( bc.dr, 0 ) = 0 \n" +
                " AND nvl( fcb.dr, 0 ) = 0 \n" +
                " AND nvl( fa.dr, 0 ) = 0 \n" +
                " AND fcb.state = 3 ");

        if(!StringUtils.isEmpty(beginOutDate) && !StringUtils.isEmpty(endOutDate)){
            querySql.append(" \n AND (ssooi.out_date BETWEEN to_date('");
            querySql.append(beginOutDate);
            querySql.append("', 'yyyy-mm-dd HH24:mi:ss')");

            querySql.append(" AND to_date('");
            querySql.append(endOutDate);
            querySql.append("', 'yyyy-mm-dd HH24:mi:ss')");
            querySql.append(")");
        }else{
            checkDate(beginOutDate, endOutDate);
        }

        //客户
        if (!StringUtils.isEmpty(customerId)) {
            querySql.append(" \n and bc.id = '");
            querySql.append(customerId);
            querySql.append("'");
        }

        //费用类型
        if (!StringUtils.isEmpty(castTypeId)) {
            querySql.append(" \n and fa.cast_type_id = '");
            querySql.append(castTypeId);
            querySql.append("'");
        }
        
        Query query = this.entityManager.createNativeQuery(querySql.toString());
        List<BigDecimal> result = query.getResultList();
        if(StringUtils.isEmpty(result) || result.size() <= 0){
            return BigDecimal.ZERO;
        }else {
            return result.get(0);
        }
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
