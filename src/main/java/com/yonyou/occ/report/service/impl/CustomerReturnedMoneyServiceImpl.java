package com.yonyou.occ.report.service.impl;

import com.yonyou.occ.b2b.client.PrivilegeClient;
import com.yonyou.occ.report.entity.CustomerReturnedMoneyDetailEntity;
import com.yonyou.occ.report.entity.CustomerReturnedMoneyEntity;
import com.yonyou.occ.report.service.CustomerReturnedMoneyService;
import com.yonyou.occ.report.service.dto.CustomerReturnedMoneyAndDetailDto;
import com.yonyou.occ.report.utils.BeanConverterUtils;
import com.yonyou.occ.report.vo.CustomerReturnedMoneyDetailVO;
import com.yonyou.occ.report.vo.CustomerReturnedMoneyVO;
import com.yonyou.ocm.common.exception.BusinessException;
import com.yonyou.ocm.common.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CustomerReturnedMoneyServiceImpl implements CustomerReturnedMoneyService {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PrivilegeClient privilegeClient;

    /**
     * 获取当前用户绑定的渠道商id
     *
     * @param searchParams
     * @return
     */
    private Map<String, Object> getCustomerId(Map<String, Object> searchParams){

        //获取当前登录用户id
        String userId = CommonUtils.getCurrentUserId();
        //获取当前用户绑定的渠道商id
        String customerId = privilegeClient.getChannelCustomerIdByUserId(userId);
        if (StringUtils.isEmpty(customerId)) {
            throw new BusinessException("查询不到当前用户对应的渠道商，请联系管理员");
        }
        searchParams.put("customerId", customerId);

        return searchParams;
    }

    /**
     * 客户回款统计——门户
     *
     * @param searchParams
     * @return
     */
    @Override
    public List<CustomerReturnedMoneyAndDetailDto> queryReturnedMoneyForPortal(Map<String, Object> searchParams) {
        searchParams = getCustomerId(searchParams);
        List<CustomerReturnedMoneyEntity> customerReturnedMoneyEntityLists = getEntityList(searchParams);
        if (!StringUtils.isEmpty(customerReturnedMoneyEntityLists)) {
            List<CustomerReturnedMoneyAndDetailDto> listDtos = new ArrayList<>();
            for (CustomerReturnedMoneyEntity entity : customerReturnedMoneyEntityLists) {
                CustomerReturnedMoneyAndDetailDto dto = new CustomerReturnedMoneyAndDetailDto();
                dto = BeanConverterUtils.copyProperties(entity, CustomerReturnedMoneyAndDetailDto.class);
                listDtos.add(dto);
            }

            return listDtos;
        }
        return null;
    }

    /**
     * 客户回款统计——中台
     *
     * @param searchParams
     * @return
     */
    @Override
    public List<CustomerReturnedMoneyAndDetailDto> queryReturnedMoney(Map<String, Object> searchParams) {
        List<CustomerReturnedMoneyEntity> customerReturnedMoneyEntityLists = getEntityList(searchParams);
        if (!StringUtils.isEmpty(customerReturnedMoneyEntityLists)) {
            List<CustomerReturnedMoneyAndDetailDto> listDtos = new ArrayList<>();
            for (CustomerReturnedMoneyEntity entity : customerReturnedMoneyEntityLists) {
                CustomerReturnedMoneyAndDetailDto dto = new CustomerReturnedMoneyAndDetailDto();
                dto = BeanConverterUtils.copyProperties(entity, CustomerReturnedMoneyAndDetailDto.class);
                listDtos.add(dto);
            }

            return listDtos;
        }
        return null;
    }

    /**
     * 客户回款统计——中台
     *
     * @param searchParams
     * @param pageable
     * @return
     */
    @Override
    public Page<CustomerReturnedMoneyAndDetailDto> queryReturnedMoneyByPage(Map<String, Object> searchParams, Pageable pageable) {
        List<CustomerReturnedMoneyEntity> customerReturnedMoneyEntityLists = getEntityList(searchParams);
        if (!StringUtils.isEmpty(customerReturnedMoneyEntityLists)) {
            List<CustomerReturnedMoneyAndDetailDto> listDtos = new ArrayList<>();
            for (CustomerReturnedMoneyEntity entity : customerReturnedMoneyEntityLists) {
                CustomerReturnedMoneyAndDetailDto dto = new CustomerReturnedMoneyAndDetailDto();
                dto = BeanConverterUtils.copyProperties(entity, CustomerReturnedMoneyAndDetailDto.class);
                listDtos.add(dto);
            }

            return new PageImpl<CustomerReturnedMoneyAndDetailDto>(listDtos, pageable, listDtos.size());
        }
        return null;
    }

    /**
     * 导出数据
     *
     * @param searchParams
     * @param response
     * @return
     */
    @Override
    public List<CustomerReturnedMoneyVO> exportExcelData(Map<String, Object> searchParams, HttpServletResponse response) {
        List<CustomerReturnedMoneyEntity> customerReturnedMoneyEntityLists = getEntityList(searchParams);
        if (!StringUtils.isEmpty(customerReturnedMoneyEntityLists)) {
            List<CustomerReturnedMoneyVO> listVOs = new ArrayList<>();
            for (CustomerReturnedMoneyEntity entity : customerReturnedMoneyEntityLists) {
                CustomerReturnedMoneyVO vo = new CustomerReturnedMoneyVO();
                vo = BeanConverterUtils.copyProperties(entity, CustomerReturnedMoneyVO.class);
                listVOs.add(vo);
            }

            return listVOs;
        }
        return null;
    }

    /**
     * 客户汇款分解明细查询--门户
     * @param searchParams
     * @return
     */
    @Override
    public List<CustomerReturnedMoneyAndDetailDto> queryReturnedMoneyDetailForPortal(Map<String, Object> searchParams) {
        searchParams = getCustomerId(searchParams);
        List<CustomerReturnedMoneyDetailEntity> customerReturnedMoneyDetailEntities = getEntityDetailList(searchParams);
        if (!StringUtils.isEmpty(customerReturnedMoneyDetailEntities)) {
            List<CustomerReturnedMoneyAndDetailDto> listDtos = new ArrayList<>();
            for (CustomerReturnedMoneyDetailEntity entity : customerReturnedMoneyDetailEntities) {
                CustomerReturnedMoneyAndDetailDto dto = new CustomerReturnedMoneyAndDetailDto();
                dto = BeanConverterUtils.copyProperties(entity, CustomerReturnedMoneyAndDetailDto.class);
                listDtos.add(dto);
            }

            return listDtos;
        }
        return null;
    }

    /**
     * 客户汇款分解明细查询--中台
     * @param searchParams
     * @return
     */
    @Override
    public List<CustomerReturnedMoneyAndDetailDto> queryReturnedMoneyDetail(Map<String, Object> searchParams) {
        List<CustomerReturnedMoneyDetailEntity> customerReturnedMoneyDetailEntities = getEntityDetailList(searchParams);
        if (!StringUtils.isEmpty(customerReturnedMoneyDetailEntities)) {
            List<CustomerReturnedMoneyAndDetailDto> listDtos = new ArrayList<>();
            for (CustomerReturnedMoneyDetailEntity entity : customerReturnedMoneyDetailEntities) {
                CustomerReturnedMoneyAndDetailDto dto = new CustomerReturnedMoneyAndDetailDto();
                dto = BeanConverterUtils.copyProperties(entity, CustomerReturnedMoneyAndDetailDto.class);
                listDtos.add(dto);
            }

            return listDtos;
        }
        return null;
    }

    @Override
    public List<CustomerReturnedMoneyDetailVO> exportDetailExcelData(Map<String, Object> searchParams) {
        List<CustomerReturnedMoneyDetailEntity> customerReturnedMoneyEntityLists = getEntityDetailList(searchParams);
        if (!StringUtils.isEmpty(customerReturnedMoneyEntityLists)) {
            List<CustomerReturnedMoneyDetailVO> listVOs = new ArrayList<>();
            for (CustomerReturnedMoneyDetailEntity entity : customerReturnedMoneyEntityLists) {
                CustomerReturnedMoneyDetailVO vo = new CustomerReturnedMoneyDetailVO();
                vo = BeanConverterUtils.copyProperties(entity, CustomerReturnedMoneyDetailVO.class);
                listVOs.add(vo);
            }

            return listVOs;
        }
        return null;
    }

    /**
     * 根据参数获取客户汇款统计实体数据
     *
     * @param searchParams
     * @return
     */
    private List<CustomerReturnedMoneyEntity> getEntityList(Map<String, Object> searchParams) {
        StringBuffer querySql = null;

        querySql = new StringBuffer("");
        createCustomerReturnedMoneySql(querySql, searchParams);//拼接sql语句

        //执行sql 获取结果集
        Query query = this.entityManager.createNativeQuery(querySql.toString(), CustomerReturnedMoneyEntity.class);
        List<CustomerReturnedMoneyEntity> customerReturnedMoneyLists = query.getResultList();
        return customerReturnedMoneyLists;

    }

    /**
     * 根据参数获取客户汇款分解明细实体数据
     *
     * @param searchParams
     * @return
     */
    private List<CustomerReturnedMoneyDetailEntity> getEntityDetailList(Map<String, Object> searchParams) {
        StringBuffer querySql = new StringBuffer("");
        createCustomerReturnedMoneyDetailsSql(querySql, searchParams);//拼接sql语句

        //执行sql 获取结果集
        Query query = this.entityManager.createNativeQuery(querySql.toString(), CustomerReturnedMoneyDetailEntity.class);
        List<CustomerReturnedMoneyDetailEntity> customerReturnedMoneyDetailEntities = query.getResultList();
        return customerReturnedMoneyDetailEntities;

    }

    /**
     * 客户回款统计
     * 拼接sql语句
     *
     * @param querySql
     */
    private void createCustomerReturnedMoneySql(StringBuffer querySql, Map<String, Object> searchParams) {

        querySql.append(" SELECT DISTINCT");
        //拼接查询的字段
        querySql.append(" tb10.id AS billreceipt_id, tb5.code AS customer_code, tb5.name AS customer_name, ");
        querySql.append(" tb6.name AS customer_category_name, tb7.name AS market_area_name, ");
        querySql.append(" tb10.code AS receipt_code, tb10.money AS receipt_amount, ");
        querySql.append(" tb10.billreceipt_time, tb10.note AS remark, tb11.full_name AS province_name ");
        querySql.append(" FROM ");
        //拼接连表
        querySql.append(" prom_activity tb1 INNER JOIN prom_activity_ext tb2 ON tb1.id = tb2.id ");
        querySql.append(" INNER JOIN prom_activity_singlerulelist tb3 ON tb3.activity_id = tb1.id ");
        querySql.append(" INNER JOIN prom_ct_sale tb4 ON tb1.id = tb4.activity_id ");
        querySql.append(" INNER JOIN base_customer tb5 ON tb4.customer_id = tb5.id ");
        querySql.append(" LEFT JOIN base_customer_category tb6 ON tb5.customer_category_id = tb6.id ");
        querySql.append(" LEFT JOIN base_market_area tb7 ON tb5.market_area_id = tb7.id ");
        querySql.append(" LEFT JOIN base_administrative_division tb11 ON tb5.province_id = tb11.id AND tb11.area_level = 1 ");
        querySql.append(" INNER JOIN prom_claim tb8 ON tb4.id = tb8.prom_ct_id ");
        querySql.append(" INNER JOIN prom_claim_detail tb9 ON tb8.id = tb9.prom_claim_id ");
        querySql.append(" INNER JOIN settlement_billreceipt tb10 ON tb10.id = tb9.gathering_id ");

        //拼接查询条件
        //拼接默认查询条件
        querySql.append(" WHERE ");
        querySql.append("nvl(tb1.dr, 0) = 0 and nvl(tb2.dr, 0) = 0 ");
        querySql.append("and nvl(tb3.dr, 0) = 0 and nvl(tb4.dr, 0) = 0 ");
        querySql.append("and nvl(tb5.dr, 0) = 0 and nvl(tb6.dr, 0) = 0 ");
        querySql.append("and nvl(tb7.dr, 0) = 0 and nvl(tb9.dr, 0) = 0 ");
        querySql.append("and nvl(tb10.dr, 0) = 0 and nvl(tb11.dr, 0) = 0 ");

        //拼接查询条件
        if (!StringUtils.isEmpty(searchParams)) {

            //渠道
            if (!StringUtils.isEmpty(searchParams.get("channelName"))) {
                querySql.append(" and tb6.name = '");
                querySql.append(searchParams.get("channelName"));
                querySql.append("'");
            }
            //市场区域
            if (!StringUtils.isEmpty(searchParams.get("marketAreaName"))) {
                querySql.append(" and tb7.name = '");
                querySql.append(searchParams.get("marketAreaName"));
                querySql.append("'");
            }
            //省份
            if (!StringUtils.isEmpty(searchParams.get("provinceName"))) {
                querySql.append(" and tb11.full_name = '");
                querySql.append(searchParams.get("provinceName"));
                querySql.append("'");
            }

            //客户Id
            if (!StringUtils.isEmpty(searchParams.get("customerId"))) {
                querySql.append(" and tb5.id = '");
                querySql.append(searchParams.get("customerId"));
                querySql.append("'");
            }

            //客户编码
            if (!StringUtils.isEmpty(searchParams.get("customerCode"))) {
                querySql.append(" and tb5.code = '");
                querySql.append(searchParams.get("customerCode"));
                querySql.append("'");
            }

            //客户名称
            if (!StringUtils.isEmpty(searchParams.get("customerName"))) {
                querySql.append(" and tb5.name = '");
                querySql.append(searchParams.get("customerName"));
                querySql.append("'");
            }

            //回款日期
            if (!StringUtils.isEmpty(searchParams.get("billreceiptTime"))) {
                querySql.append(" and tb10.billreceipt_time = to_date('");
                querySql.append(searchParams.get("billreceiptTime"));
                querySql.append("', 'yyyy-mm-dd HH24:mi:ss')");
            }

            //中台收款单号
            if (!StringUtils.isEmpty(searchParams.get("receiptCode"))) {
                querySql.append(" and tb10.code = '");
                querySql.append(searchParams.get("receiptCode"));
                querySql.append("'");
            }
            //分组
            querySql.append("GROUP BY\n" +
                    " tb10.id, tb5.code, tb5.name, tb6.name, tb7.name, tb10.note,\n" +
                    " tb10.code, tb10.money, tb10.billreceipt_time,\ttb11.full_name");
        }
    }

    /**
     * 客户回款分解明细统计
     */
    private void createCustomerReturnedMoneyDetailsSql(StringBuffer querySql, Map<String, Object> searchParams) {
        querySql.append("SELECT DISTINCT ");
        querySql.append(" tb1.id AS activity_id, " +
                " tb1.code AS activity_code, tb1.name AS activity_name, " +
                " tb5.code AS customer_code, tb5.name AS customer_name, " +
                " tb6.name AS customer_category_name, tb7.name AS market_area_name, " +
                " tb8.claim_amount, tb9.gathering_date, " +
                " (CASE tb3.is_cal_task WHEN 1 THEN sum(tb3.amount) ELSE 0 END ) AS cal_task_amount, " +
                " (CASE tb3.is_cal_return WHEN 1 THEN sum(tb3.amount) ELSE 0 END ) AS cal_return_amount, '' AS remark ");
        querySql.append(" FROM ");
        querySql.append(" prom_activity tb1 INNER JOIN prom_activity_ext tb2 ON tb1.id = tb2.id " +
                " INNER JOIN prom_activity_singlerulelist tb3 ON tb3.activity_id = tb1.id " +
                " INNER JOIN prom_ct_sale tb4 ON tb1.id = tb4.activity_id " +
                " INNER JOIN base_customer tb5 ON tb4.customer_id = tb5.id " +
                " LEFT JOIN base_customer_category tb6 ON tb5.customer_category_id = tb6.id " +
                " LEFT JOIN base_market_area tb7 ON tb5.market_area_id = tb7.id " +
                " INNER JOIN prom_claim tb8 ON tb4.id = tb8.prom_ct_id " +
                " INNER JOIN prom_claim_detail tb9 ON tb8.id = tb9.prom_claim_id ");
        querySql.append(" WHERE ");
        querySql.append(" nvl(tb1.dr, 0) = 0 AND nvl(tb2.dr, 0) = 0 AND nvl(tb3.dr, 0) = 0 " +
                " AND nvl(tb4.dr, 0) = 0 AND nvl(tb5.dr, 0) = 0 AND nvl(tb6.dr, 0) = 0 " +
                " AND nvl(tb7.dr, 0) = 0 AND nvl(tb8.dr, 0) = 0 AND nvl(tb9.dr, 0) = 0 ");

        if(!StringUtils.isEmpty(searchParams)){
            //渠道
            if (!StringUtils.isEmpty(searchParams.get("channelName"))) {
                querySql.append(" and tb6.name = '");
                querySql.append(searchParams.get("channelName"));
                querySql.append("'");
            }
            //市场区域
            if (!StringUtils.isEmpty(searchParams.get("marketAreaName"))) {
                querySql.append(" and tb7.name = '");
                querySql.append(searchParams.get("marketAreaName"));
                querySql.append("'");
            }

            //客户Id
            if (!StringUtils.isEmpty(searchParams.get("customerId"))) {
                querySql.append(" and tb5.id = '");
                querySql.append(searchParams.get("customerId"));
                querySql.append("'");
            }

            //客户编码
            if (!StringUtils.isEmpty(searchParams.get("customerCode"))) {
                querySql.append(" and tb5.code = '");
                querySql.append(searchParams.get("customerCode"));
                querySql.append("'");
            }

            //客户名称
            if (!StringUtils.isEmpty(searchParams.get("customerName"))) {
                querySql.append(" and tb5.name = '");
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
        }

        //分组
        querySql.append(" GROUP BY " +
                " tb1.id, tb1.code, tb1.name, tb3.is_cal_task, tb3.is_cal_return, " +
                " tb5.code, tb5.name, tb6.name, tb7.name, tb8.claim_amount, tb9.gathering_date ");
    }
}
