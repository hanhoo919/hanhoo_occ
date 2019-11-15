package com.yonyou.occ.report.service.impl;

import com.yonyou.occ.b2b.client.PrivilegeClient;
import com.yonyou.occ.report.entity.KASalesOrderEntity;
import com.yonyou.occ.report.service.KASalesOrderService;
import com.yonyou.occ.report.service.dto.KASalesOrderDto;
import com.yonyou.occ.report.utils.BeanConverterUtils;
import com.yonyou.occ.report.vo.KASalesOrderVO;
import com.yonyou.ocm.common.exception.BusinessException;
import com.yonyou.ocm.common.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class KASalesOrderServiceImpl implements KASalesOrderService {

    @Autowired
    private PrivilegeClient privilegeClient;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 查询所有的销售订单--门户
     */
    @Override
    public List<KASalesOrderDto> queryAllForOrderForPortal(Map<String, Object> searchParams) {
        //获取当前登录用户id
        String userId = CommonUtils.getCurrentUserId();
        //获取当前用户绑定的渠道商id
        String customerId = privilegeClient.getChannelCustomerIdByUserId(userId);
        if (StringUtils.isEmpty(customerId)) {
            throw new BusinessException("查询不到当前用户对应的渠道商，请联系管理员");
        }
        searchParams.put("customerId", customerId);

        List<KASalesOrderEntity> kaSalesOrderLists = getEntityList(searchParams);
        if (!StringUtils.isEmpty(kaSalesOrderLists)) {
            List<KASalesOrderDto> kaSalesOrderDtos = new ArrayList<>();
            for (KASalesOrderEntity kaSalesOrderEntity : kaSalesOrderLists) {
                KASalesOrderDto kaSalesOrderDto = new KASalesOrderDto();
                kaSalesOrderDto = BeanConverterUtils.copyProperties(kaSalesOrderEntity, KASalesOrderDto.class);
                kaSalesOrderDtos.add(kaSalesOrderDto);
            }
            return kaSalesOrderDtos;
        }

        return null;
    }

    /**
     * 查询所有的销售订单--中台
     */
    @Override
    public List<KASalesOrderDto> queryAllForOrder(Map<String, Object> searchParams) {
        List<KASalesOrderEntity> kaSalesOrderLists = getEntityList(searchParams);
        if (!StringUtils.isEmpty(kaSalesOrderLists)) {
            List<KASalesOrderDto> kaSalesOrderDtos = new ArrayList<>();
            for (KASalesOrderEntity kaSalesOrderEntity : kaSalesOrderLists) {
                KASalesOrderDto kaSalesOrderDto = new KASalesOrderDto();
                kaSalesOrderDto = BeanConverterUtils.copyProperties(kaSalesOrderEntity, KASalesOrderDto.class);
                kaSalesOrderDtos.add(kaSalesOrderDto);
            }
            return kaSalesOrderDtos;
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
    public List<KASalesOrderVO> exportExcelData(Map<String, Object> searchParams, HttpServletResponse response) {
        List<KASalesOrderEntity> kaSalesOrderLists = getEntityList(searchParams);
        if (!StringUtils.isEmpty(kaSalesOrderLists) && kaSalesOrderLists.size() > 0) {
            List<KASalesOrderVO> kaSalesOrderVOList = new ArrayList<>();
            for (KASalesOrderEntity kaSalesOrderEntity : kaSalesOrderLists) {
                KASalesOrderVO kaSalesOrderVO = new KASalesOrderVO();
                kaSalesOrderVO = BeanConverterUtils.copyProperties(kaSalesOrderEntity, KASalesOrderVO.class);
                kaSalesOrderVOList.add(kaSalesOrderVO);
            }
            return kaSalesOrderVOList;
        }
        return null;
    }

    /**
     * 根据参数获取实体数据
     *
     * @param searchParams
     * @return
     */
    private List<KASalesOrderEntity> getEntityList(Map<String, Object> searchParams) {
        StringBuffer querySql = new StringBuffer("");
        createSql(querySql, searchParams);//拼接sql语句

        //执行sql 获取结果集
        Query query = this.entityManager.createNativeQuery(querySql.toString(), KASalesOrderEntity.class);
        List<KASalesOrderEntity> kaSalesOrderLists = query.getResultList();
        return kaSalesOrderLists;
    }

    /**
     * 拼接sql语句
     *
     * @param querySql
     */
    private void createSql(StringBuffer querySql, Map<String, Object> searchParams) {

        querySql.append(" SELECT ");
        //拼接查询的字段
        querySql.append(" t1.id AS order_id,t2.id AS order_item_id,t1.order_type,t9.name as order_type_name,t1.bill_type,t1.order_code, ");
        querySql.append(" t1.order_date,t1.order_status,t1.total_num,t1.total_amount,t1.total_deal_amount,t1.prom_amount,t1.offset_amount, ");
        querySql.append(" t1.total_weight,t1.total_net_weight,t1.total_volume,t1.order_source,t1.creator,t1.remark as bill_remark,t2.main_num,t2.order_num, ");
        querySql.append(" t2.sale_price,t2.deal_price,t2.base_price,t2.prom_price,t2.amount,t2.deal_amount,t2.return_amount,t2.delivery_num, ");
        querySql.append(" (t2.order_num - t2.delivery_num) as residual_num,t2.stock_in_num,t2.stock_out_num,t2.return_num,t2.refund_num,t2.sign_num,t2.replenish_num,");
        querySql.append(" t2.coordinate_num,t3.code AS customer_code,t2.remark as line_remark,t3.name AS customer_name,t4.code AS channel_code, ");
        querySql.append(" t4.name AS channel_name, t5.code AS market_area_code,t5.name AS market_area_name,t6.code AS province_code, ");
        querySql.append(" t6.full_name AS province_name, t7.code AS goods_code,t7.name AS goods_name,t8.material_group,t8.material_class, ");
        querySql.append(" t10.name as brand_name,t11.name as sale_series_name,t12.code as warehouse_code,t12.name as warehouse_name,t13.receiver, t14.name as receive_customer ");

        querySql.append(" FROM b2b_order t1 ");
        //拼接连表
        querySql.append(" LEFT JOIN b2b_order_item t2 ON t1.id = t2.order_id ");
        querySql.append(" LEFT JOIN base_customer t3 ON t1.customer_id = t3.id ");
        querySql.append(" LEFT JOIN base_customer_category t4 ON t3.customer_category_id = t4.id ");
        querySql.append(" LEFT JOIN base_market_area t5 ON t1.market_area_id = t5.id ");
        querySql.append(" LEFT JOIN base_administrative_division t6 ON t3.province_id = t6.id AND t6.area_level = 1 ");
        querySql.append(" LEFT JOIN base_goods t7 ON t2.goods_id = t7.id ");
        querySql.append(" LEFT JOIN (SELECT b.id,a.code AS parent_code, a.name AS material_group, b.code,b.name AS material_class FROM base_goods_category a LEFT JOIN base_goods_category b ON a.id = b.PARENT_ID WHERE a.parent_id IS NULL AND a.node_level = 1 AND nvl( a.dr, 0 ) = 0 AND nvl( b.dr, 0 ) = 0 ) t8 ON t7.goods_category_id = t8.id ");
        querySql.append(" LEFT JOIN base_trantype t9 on t1.ORDER_TYPE = t9.id ");
        querySql.append(" LEFT JOIN base_brand t10 on t7.brand_id = t10.id ");
        querySql.append(" LEFT JOIN base_sale_series t11 on t7.sale_series = t11.id ");
        querySql.append(" LEFT JOIN base_warehouse t12 on t2.DELIVERY_WAREHOUSE_ID = t12.id ");
        querySql.append(" LEFT JOIN b2b_order_receive_address t13 on t1.id = t13.order_id ");
        querySql.append(" LEFT JOIN base_customer t14 on t1.receive_customer = t14.id ");

        //拼接查询条件
        //拼接默认查询条件
        querySql.append(" WHERE ");
        querySql.append("nvl(t1.dr, 0) = 0 and nvl(t2.dr, 0) = 0 ");
        querySql.append("and nvl(t3.dr, 0) = 0 and nvl(t4.dr, 0) = 0 ");
        querySql.append("and nvl(t5.dr, 0) = 0 and nvl(t6.dr, 0) = 0 ");
        querySql.append("and nvl(t7.dr, 0) = 0 and nvl(t9.dr, 0) = 0 ");
        querySql.append("and nvl(t10.dr, 0) = 0 and nvl(t11.dr, 0) = 0 ");
        querySql.append("and nvl(t12.dr, 0) = 0 and nvl(t13.dr, 0) = 0 ");

        //拼接查询条件
        if (!StringUtils.isEmpty(searchParams)) {
            //订单开始日期
            if (!StringUtils.isEmpty(searchParams.get("beginOrderDate"))) {
                querySql.append(" and to_char(t1.order_date, 'yyyyMMdd') >= '");
                querySql.append(searchParams.get("beginOrderDate"));
                querySql.append("'");
            }

            //订单结束日期
            if (!StringUtils.isEmpty(searchParams.get("endOrderDate"))) {
                querySql.append(" and to_char(t1.order_date, 'yyyyMMdd') <= '");
                querySql.append(searchParams.get("endOrderDate"));
                querySql.append("'");
            }

            //渠道
            if (!StringUtils.isEmpty(searchParams.get("channelName"))) {
                querySql.append(" and t4.name = '");
                querySql.append(searchParams.get("channelName"));
                querySql.append("'");
            }
            //市场区域
            if (!StringUtils.isEmpty(searchParams.get("marketAreaName"))) {
                querySql.append(" and t5.name = '");
                querySql.append(searchParams.get("marketAreaName"));
                querySql.append("'");
            }

            //省份
            if (!StringUtils.isEmpty(searchParams.get("provinceName"))) {
                querySql.append(" and t6.full_name = '");
                querySql.append(searchParams.get("provinceName"));
                querySql.append("'");
            }

            //客户Id
            if (!StringUtils.isEmpty(searchParams.get("customerId"))) {
                querySql.append(" and t3.id = '");
                querySql.append(searchParams.get("customerId"));
                querySql.append("'");
            }

            //客户编码
            if (!StringUtils.isEmpty(searchParams.get("customerCode"))) {
                querySql.append(" and t3.code = '");
                querySql.append(searchParams.get("customerCode"));
                querySql.append("'");
            }

            //客户名称
            if (!StringUtils.isEmpty(searchParams.get("customerName"))) {
                querySql.append(" and t3.name = '");
                querySql.append(searchParams.get("customerName"));
                querySql.append("'");
            }

            //订单编码
            if (!StringUtils.isEmpty(searchParams.get("orderCode"))) {
                querySql.append(" and t1.order_code = '");
                querySql.append(searchParams.get("orderCode"));
                querySql.append("'");
            }
            //订单状态
            if (!StringUtils.isEmpty(searchParams.get("orderStatus"))) {
                querySql.append(" and t1.order_status = '");
                querySql.append(searchParams.get("orderStatus"));
                querySql.append("'");
            }
        }
    }
}
