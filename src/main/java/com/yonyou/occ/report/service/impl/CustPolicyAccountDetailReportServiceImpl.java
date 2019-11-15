package com.yonyou.occ.report.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yonyou.occ.b2b.common.enums.IsCloseEnum;
import com.yonyou.occ.b2b.entity.OrderItem;
import com.yonyou.occ.prom.enums.ProAndGiftEnum;
import com.yonyou.occ.prom.enums.PromWayExtEnum;
import com.yonyou.occ.report.service.ICustPolicyAccountDetailReportService;
import com.yonyou.occ.report.service.dto.CustPolicyAccountDetailDto;
import com.yonyou.ocm.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;

import javax.persistence.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * 客户政策账余明细表 取数逻辑类
 *
 * @author Davis tang
 * @date 2019/08/27
 */
@Slf4j
@Service
public class CustPolicyAccountDetailReportServiceImpl implements ICustPolicyAccountDetailReportService {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 控制类型2
     */
    private enum ControlType2 {

        AMOUNT(0, "金额"),

        NUM(1, "数量");

        private Integer code;

        private String desc;

        ControlType2(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }

    }

    @Override
    public List<CustPolicyAccountDetailDto> getCustPolicyAccountDetails(String customerCode, String customerName, String activityCode, String activityName, String beginDate, String endDate) {
        checkDate(beginDate, endDate);
        List<CustActivityRuleDetail> custActivityRuleDetails = queryCustActivityRuleDetails(customerCode, customerName, activityCode, activityName, beginDate, endDate);
        List<CustPolicyAccountDetailDto> custPolicyAccountDetailDtos = setCustActivityRuleDetails(custActivityRuleDetails, beginDate, endDate);
        return groupByControlType(sortCustPolicyAccountDetailDtos(custPolicyAccountDetailDtos), beginDate, endDate);
    }

    /**
     * 促销合同明细粒度（未按控制类型汇总）的dto集合设值
     *
     * @param custActivityRuleDetails
     * @param beginDate
     * @param endDate
     * @return
     */
    private List<CustPolicyAccountDetailDto> setCustActivityRuleDetails(List<CustActivityRuleDetail> custActivityRuleDetails, String beginDate, String endDate) {
        List<CustPolicyAccountDetailDto> custPolicyAccountDetailDtos = Lists.newArrayList();
        for (CustActivityRuleDetail custActivityRuleDetail : custActivityRuleDetails) {
            CustPolicyAccountDetailDto custPolicyAccountDetailDto = new CustPolicyAccountDetailDto();
            custPolicyAccountDetailDto.setCustomerCategoryName(StringUtils.trimToEmpty(custActivityRuleDetail.getCustomerCategoryName()));
            custPolicyAccountDetailDto.setMarketAreaName(custActivityRuleDetail.getMarketAreaName());
            custPolicyAccountDetailDto.setCustomerCode(custActivityRuleDetail.getCustomerCode());
            custPolicyAccountDetailDto.setCustomerName(custActivityRuleDetail.getCustomerName());
            custPolicyAccountDetailDto.setActivityValidPeriod(custActivityRuleDetail.getActivityBeginDate() + "~" + custActivityRuleDetail.getActivityEndDate());
            custPolicyAccountDetailDto.setActivityCode(custActivityRuleDetail.getActivityCode());
            custPolicyAccountDetailDto.setActivityName(custActivityRuleDetail.getActivityName());
            custPolicyAccountDetailDto.setControlType1(custActivityRuleDetail.getPromType() == PromWayExtEnum.ONLYBUY.getCode() ? PromWayExtEnum.ONLYBUY.getName() : PromWayExtEnum.ONLYGIFT.getName());
            custPolicyAccountDetailDto.setControlType2(getControlType2(custPolicyAccountDetailDto.getControlType1(), custActivityRuleDetail.getProType(), custActivityRuleDetail.getNum()));
            BigDecimal endingBalance = caculateEndingBalance(endDate, custActivityRuleDetail, custPolicyAccountDetailDto);
            custPolicyAccountDetailDto.setEndingBalance(endingBalance);
            BigDecimal beginingBalance = caculateBeginingBalance(beginDate, endDate, custActivityRuleDetail.getPromSaleDetailId(), custPolicyAccountDetailDto.getControlType1(), custPolicyAccountDetailDto.getControlType2(), endingBalance);
            custPolicyAccountDetailDto.setBeginningBalance(beginingBalance);
            custPolicyAccountDetailDto.setCurrentPeriodGoods(caculateCurrentPeriodGoods(beginDate, endDate, custActivityRuleDetail.getPromSaleDetailId(), custPolicyAccountDetailDto.getControlType1(), custPolicyAccountDetailDto.getControlType2()));
            custPolicyAccountDetailDto.setCurrentSumBalance(caculateCurrentSaleDetailBalance(custActivityRuleDetail, custPolicyAccountDetailDto));
            custPolicyAccountDetailDtos.add(custPolicyAccountDetailDto);
        }
        return custPolicyAccountDetailDtos;
    }

    /**
     * 把销售合同明细粒度的数据集按：经销商编码、活动编码、控制类型1、控制类型2进行分类
     */
    private Map<String, List<CustPolicyAccountDetailDto>> sortCustPolicyAccountDetailDtos(List<CustPolicyAccountDetailDto> srcDtos) {
        Map<String, List<CustPolicyAccountDetailDto>> sortMap = Maps.newHashMap();
        for (CustPolicyAccountDetailDto dto : srcDtos) {
            String key = dto.getCustomerCode() + "_" + dto.getActivityCode() + "_" + dto.getControlType1() + "_" + dto.getControlType2();
            if (sortMap.containsKey(key)) {
                sortMap.get(key).add(dto);
            } else {
                List<CustPolicyAccountDetailDto> custPolicyAccountDetailDtos = Lists.newArrayList();
                custPolicyAccountDetailDtos.add(dto);
                sortMap.put(key, custPolicyAccountDetailDtos);
            }
        }
        return sortMap;
    }

    /**
     * 将分类完成的数据进行数据汇总（期初余额、本期发货、期末余额、查询截点余额）
     *
     * @param sortMap
     * @return
     */
    private List<CustPolicyAccountDetailDto> groupByControlType(Map<String, List<CustPolicyAccountDetailDto>> sortMap, String beginDate, String endDate) {
        List<CustPolicyAccountDetailDto> groupedDtos = Lists.newArrayList();
        for (Map.Entry<String, List<CustPolicyAccountDetailDto>> entry : sortMap.entrySet()) {
            List<CustPolicyAccountDetailDto> custPolicyAccountDetailDtos = entry.getValue();
            BigDecimal sumBeginingBalance = BigDecimal.ZERO;
            BigDecimal sumEndingBalance = BigDecimal.ZERO;
            BigDecimal sumCurrentPeriodGoods = BigDecimal.ZERO;
            BigDecimal sumCurrentSumBalance = BigDecimal.ZERO;

            // 数据汇总
            for (CustPolicyAccountDetailDto custPolicyAccountDetailDto : custPolicyAccountDetailDtos) {
                sumBeginingBalance = sumBeginingBalance.add(custPolicyAccountDetailDto.getBeginningBalance());
                sumEndingBalance = sumEndingBalance.add(custPolicyAccountDetailDto.getEndingBalance());
                sumCurrentPeriodGoods = sumCurrentPeriodGoods.add(custPolicyAccountDetailDto.getCurrentPeriodGoods());
                sumCurrentSumBalance = sumCurrentSumBalance.add(custPolicyAccountDetailDto.getCurrentSumBalance());
            }

            CustPolicyAccountDetailDto groupDto = new CustPolicyAccountDetailDto();
            groupDto = custPolicyAccountDetailDtos.get(0);
            groupDto.setCurrentPeriodMoney(caculateCurrentPeriodMoney(beginDate, endDate, groupDto.getActivityCode(), groupDto.getCustomerCode()));
            groupDto.setBeginningBalance(sumBeginingBalance);
            groupDto.setEndingBalance(sumEndingBalance);
            groupDto.setCurrentPeriodGoods(sumCurrentPeriodGoods);
            groupDto.setCurrentSumBalance(sumCurrentSumBalance);
            groupedDtos.add(groupDto);
        }
        return groupedDtos;
    }

    /**
     * 计算本期款项分解	即查询条件时间段中活动的认领金额，四种维度显示内容保持一致
     *
     * @param beginDate
     * @param endDate
     * @param activityCode
     * @param customerCode
     * @return
     */
    private BigDecimal caculateCurrentPeriodMoney(String beginDate, String endDate, String activityCode, String customerCode) {
        if (StringUtils.isBlank(beginDate) || StringUtils.isBlank(endDate) || StringUtils.isBlank(activityCode) || StringUtils.isBlank(customerCode)) {
            throw new BusinessException("必要查询条件为空");
        }
        String sql = "select sum(CLAIM_AMOUNT) from PROM_CLAIM pc\n" +
                "inner join PROM_CT_SALE pcs on pc.prom_ct_id = pcs.id\n" +
                "inner join PROM_ACTIVITY pa on pcs.activity_id = pa.id\n" +
                "inner join BASE_CUSTOMER bc on pcs.customer_id = bc.id\n" +
                "where to_char(pc.creation_time,'YYYYMMDD') between :beginDate and :endDate\n" +
                "and bc.code=:customerCode\n" +
                "and pa.code=:activityCode";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("beginDate", beginDate);
        query.setParameter("endDate", endDate);
        query.setParameter("customerCode", customerCode);
        query.setParameter("activityCode", activityCode);
        BigDecimal result = (BigDecimal) query.getSingleResult();
        return result == null ? BigDecimal.ZERO : result;
    }

    /**
     * 判断控制类型2取值逻辑：仅买，指定SKU是数量，不指定SKU是金额；仅赠，指定SKU是数量，不指定SKU数量不为空为数量，否则为金额
     *
     * @param controlType1 控制类型1：仅买、仅赠
     * @param proType      商品类别: 1 商品 2 产品 3 组合 4 分类
     * @param num          数量
     * @return
     */
    private String getControlType2(String controlType1, Integer proType, BigDecimal num) {
        String controlType2 = "";
        // 如果是仅买
        if (controlType1.equals(PromWayExtEnum.ONLYBUY.getName())) {
            if (proType.equals(ProAndGiftEnum.COMMODITY.getCode())) {
                controlType2 = ControlType2.NUM.desc;
            } else {
                controlType2 = ControlType2.AMOUNT.desc;
            }
            // 仅赠
        } else {
            // 指定SKU
            if (proType.equals(ProAndGiftEnum.COMMODITY.getCode())) {
                controlType2 = ControlType2.NUM.desc;
                // 不指定SKU
            } else {
                // 使用数量控制
                if (num != null && num.intValue() != 0) {
                    controlType2 = ControlType2.NUM.desc;
                } else {
                    controlType2 = ControlType2.AMOUNT.desc;
                }
            }
        }
        return controlType2;
    }

    /**
     * 查询满足条件的销售合同明细粒度的数据集合
     *
     * @param customerCode
     * @param customerName
     * @param activityCode
     * @param activityName
     * @param beginDate
     * @param endDate
     * @return
     */
    private List<CustActivityRuleDetail> queryCustActivityRuleDetails(String customerCode, String customerName,
                                                                      String activityCode, String activityName,
                                                                      String beginDate, String endDate) {
        /**
         select pcsd.id 促销合同明细主键,bma.name 渠道,bcc.name 区域,bc.code 代理商编码,bc.name 代理商名称,pa.code 活动编码,pa.name 活动名称,pa.start_date 活动开始日期, pa.end_date 活动结束日期,pcsd.prom_way 仅买仅赠,prdh.pro_type 商品类别,prdh.amount 金额,prdh.num 数量
         from PROM_CT_SALE_DETAIL pcsd
         inner join PROM_CT_SALE pcs on pcsd.prom_ct_id = pcs.id
         inner join PROM_ACTIVITY pa on pcs.activity_id = pa.id
         inner join PROM_ACTIVITY_SINGLERULELIST pas on pcsd.single_activitylist_id = pas.id
         inner join PROM_RULE_DETAIL_HANHOO prdh on pas.single_rule_id = prdh.id
         inner join BASE_CUSTOMER bc on pcs.customer_id = bc.id
         inner join BASE_CUSTOMER_CATEGORY bcc on bc.customer_category_id = bcc.id
         left join BASE_MARKET_AREA bma on bc.market_area_id = bma.id
         where pa.start_date >= to_date('20190812','YYYYMMDD') and pa.end_date <= to_date('20190831','YYYYMMDD') order by pa.ts desc
         */
        // 注意查询结果与实体成员变量映射的时候，hibernate是按成员变量命名(M驼峰)进行转换后与sql查询字段进行匹配的，例如：实体变量promSaleDetailId，会被转换为prom_sale_detail_id，因此
        // sql中的字段也应该是prom_sale_detail_id
        StringBuilder sql = new StringBuilder();
        sql.append("select pcsd.id as prom_sale_detail_id,")
                .append("pcsd.totalrow_order_amount as totalrow_order_amount,")
                .append("pcsd.totalrow_ct_amount as totalrow_ct_amount,")
                .append("pcsd.totalrow_ct_num as totalrow_ct_num,")
                .append("pcsd.totalrow_order_num as totalrow_order_num,")
                .append("bma.name as customer_category_name,")
                .append("bcc.name as market_area_name,")
                .append("bc.code as customer_code,")
                .append("bc.name as customer_name,")
                .append("pa.code as activity_code,")
                .append("pa.name as activity_name,")
                .append("pa.start_date as activity_begin_date,")
                .append("pa.end_date as activity_end_date,")
                .append("pcsd.prom_way as prom_type,")
                .append("prdh.pro_type as pro_type,")
                .append("prdh.amount as amount,")
                .append("prdh.num as num \n")
                .append("from PROM_CT_SALE_DETAIL pcsd \n")
                .append("inner join PROM_CT_SALE pcs on pcsd.prom_ct_id = pcs.id\n")
                .append("inner join PROM_ACTIVITY pa on pcs.activity_id = pa.id\n")
                .append("inner join PROM_ACTIVITY_SINGLERULELIST pas on pcsd.single_activitylist_id = pas.id\n")
                .append("inner join PROM_RULE_DETAIL_HANHOO prdh on pas.single_rule_id = prdh.id\n")
                .append("inner join BASE_CUSTOMER bc on pcs.customer_id = bc.id\n")
                .append("inner join BASE_CUSTOMER_CATEGORY bcc on bc.customer_category_id = bcc.id\n")
                .append("left join BASE_MARKET_AREA bma on bc.market_area_id = bma.id\n")
//                .append("where pa.start_date >= to_date(:beginDate,'YYYYMMDD') and pa.end_date <= to_date(:endDate,'YYYYMMDD')")
                .append("where pa.start_date between to_date(:beginDate,'YYYYMMDD') and to_date(:endDate,'YYYYMMDD')");

        if (StringUtils.isNotBlank(customerCode)) {
            sql.append(" and bc.code = :customerCode");
        }
        if (StringUtils.isNotBlank(customerName)) {
            sql.append(" and bc.name = :customerName");
        }
        if (StringUtils.isNotBlank(activityCode)) {
            sql.append(" and pa.code = :activityCode");
        }
        if (StringUtils.isNotBlank(activityName)) {
            sql.append(" and pa.name = :activityName");
        }
        sql.append(" order by pa.ts desc");
        Query query = entityManager.createNativeQuery(sql.toString(), CustActivityRuleDetail.class);
        query.setParameter("beginDate", beginDate);
        query.setParameter("endDate", endDate);
        if (StringUtils.isNotBlank(customerCode)) {
            query.setParameter("customerCode", customerCode);
        }
        if (StringUtils.isNotBlank(customerName)) {
            query.setParameter("customerName", customerName);
        }
        if (StringUtils.isNotBlank(activityCode)) {
            query.setParameter("activityCode", activityCode);
        }
        if (StringUtils.isNotBlank(activityName)) {
            query.setParameter("activityName", activityName);
        }

        List<CustActivityRuleDetail> custActivityRuleDeitals = query.getResultList();
        return custActivityRuleDeitals;
    }

    /**
     * 计算期初余额
     *
     * @param beginDate
     * @param endDate
     * @param promSaleDetailId
     * @param controlType1
     * @return
     */
    private BigDecimal caculateBeginingBalance(String beginDate, String endDate, String promSaleDetailId, String controlType1, String controlType2, BigDecimal endingBalance) {
        if (beginDate.equals(endDate)) {
            // 如果查询开始日期与结束日期相等，则期初余额=期末余额，可直接返回期末余额
            return endingBalance;
        }
        BigDecimal happenedBalance = caculateOrderHappendBalance(beginDate, endDate, promSaleDetailId, controlType1, controlType2);
        return happenedBalance.add(endingBalance);
    }

    /**
     * 计算期末余额：
     * 即查询条件中后一个时间点的已认领未下单余额，以查询条件为“20180301-20180630”为例，
     * 查询当天的日期的活动余额加上该活动7月1日到查询当天的销售订单发生额（这里需要根据订单状态进行组合考虑，
     * 对于未关闭的订单，按订单的下单数量进行统计，对于已关闭的订单，按订单的实发数量进行统计，两者之和则为这个订单发生额），
     * 得出6月30日的期末余额值
     * <p>
     * 对于控制类型为：赠品/数量(指定SKU和不指定SKU)，不计算金额，仅计算数量
     *
     * @param endDate
     * @param custActivityRuleDetail
     * @param custPolicyAccountDetailDto
     * @return
     */
    private BigDecimal caculateEndingBalance(String endDate, CustActivityRuleDetail custActivityRuleDetail, CustPolicyAccountDetailDto custPolicyAccountDetailDto) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyyMMdd");
        DateTime dateTime = DateTime.parse(endDate, dateTimeFormatter);
        // 查询结束日期+1天
        DateTime endDateNextDay = dateTime.plusDays(1);
        DateTime nowDate = DateTime.now();
        String strEndDateNextDay = endDateNextDay.toString("yyyyMMdd");
        String strNowDate = nowDate.toString("yyyyMMdd");
        // 查询当天的日期的活动余额
        BigDecimal happenedBalance = caculateCurrentSaleDetailBalance(custActivityRuleDetail, custPolicyAccountDetailDto);
        if (Integer.valueOf(strEndDateNextDay) > Integer.valueOf(strNowDate)) {
            // 查询结束日期+1 大于当前日期时，还不存在销售订单发生时间，不用查询销售订单发生额
            return happenedBalance;
        }
        // 加上查询该活动期末日期（来自查询条件）+1到当天的销售订单发生额
        happenedBalance = happenedBalance.add(caculateOrderHappendBalance(strEndDateNextDay, strNowDate, custActivityRuleDetail.getPromSaleDetailId(), custPolicyAccountDetailDto.getControlType1(), custPolicyAccountDetailDto.getControlType2()));
        return happenedBalance;
    }

    /**
     * 计算当前销售合同明细行余额（已认领未下单金额）
     * 对于控制类型为：赠品/数量(指定SKU和不指定SKU)，不计算金额，仅计算数量
     *
     * @param custActivityRuleDetail
     * @param custPolicyAccountDetailDto
     * @return
     */
    private BigDecimal caculateCurrentSaleDetailBalance(CustActivityRuleDetail custActivityRuleDetail, CustPolicyAccountDetailDto custPolicyAccountDetailDto) {
        // 对于控制类型为：赠品/数量(指定SKU和不指定SKU)，不计算金额，仅计算数量
        if (custPolicyAccountDetailDto.getControlType1().equals(PromWayExtEnum.ONLYGIFT.getName()) && custPolicyAccountDetailDto.getControlType2().equals(ControlType2.NUM.desc)) {
            return BigDecimal.valueOf(custActivityRuleDetail.getTotalrowCtNum() - custActivityRuleDetail.getTotalrowOrderNum());
        }
        return custActivityRuleDetail.getTotalrowCtAmount().subtract(custActivityRuleDetail.getTotalrowOrderAmount());
    }

    /**
     * 计算当前销售合同明细行余额（已认领未下单金额）
     * 对于控制类型为：赠品/数量(指定SKU和不指定SKU)，不计算金额，仅计算数量
     * @param controllType1
     * @param controllType2
     * @param promSaleDetailId
     * @return
     */
//    private BigDecimal caculateCurrentSaleDetailBalance(String controllType1, String controllType2, String promSaleDetailId) {
//        String sql = "select * from PROM_CT_SALE_DETAIL where id=:promSaleDetailId";
//        Query query = entityManager.createNativeQuery(sql, CtSaleDetail.class);
//        query.setParameter("promSaleDetailId", promSaleDetailId);
//        List<CtSaleDetail> ctSaleDetails = query.getResultList();
//        if (CollectionUtils.isNotEmpty(ctSaleDetails)) {
//            CtSaleDetail ctSaleDetail = ctSaleDetails.get(0);
//            // 对于控制类型为：赠品/数量(指定SKU和不指定SKU)，不计算金额，仅计算数量
//            if (controllType1.equals(PromWayExtEnum.ONLYGIFT.getName()) && controllType2.equals(ControlType2.NUM.desc)) {
//                return BigDecimal.valueOf(ctSaleDetail.getTotalrowCtNum() - ctSaleDetail.getTotalrowOrderNum());
//            }
//            return ctSaleDetail.getTotalrowCtAmount().subtract(ctSaleDetail.getTotalrowOrderAmount());
//        }
//        return BigDecimal.ZERO;
//    }

    /**
     * 计算本期发货，根据订单累计发货数量来计算（不管订单是否关闭）
     *
     * @param beginDate
     * @param endDate
     * @param promSaleDetailId
     * @param controllType1
     * @param controllType2
     * @return
     */
    private BigDecimal caculateCurrentPeriodGoods(String beginDate, String endDate, String promSaleDetailId, String controllType1, String controllType2) {
        BigDecimal sumBalance = BigDecimal.ZERO;
        // 查询未关闭销售订单
        List<OrderItem> opendOrderItems = getB2bOrderItemByPromSaleDetailId(beginDate, endDate, promSaleDetailId, null);
        sumBalance = sumBalance.add(deliverOrderBalance(opendOrderItems, controllType1, controllType2));
        return sumBalance;
    }

    public static void main(String args[]) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyyMMdd");
        DateTime dateTime = DateTime.parse("20190821", dateTimeFormatter);
        DateTime endDateNextDay = dateTime.plusDays(1);
        System.out.println(dateTime.toString("yyyyMMdd") + "...." + endDateNextDay);

        BigDecimal zero = new BigDecimal(1);
        zero = zero.add(new BigDecimal("9.9"));
        System.out.println(zero);
    }

    /**
     * 计算时间段内销售订单的发生额：
     * 对于未关闭的订单，按订单的下单数量进行统计，对于已关闭的订单，按订单的实发数量进行统计，两者之和则为这个订单发生额
     * <p>
     * 对于控制类型为：赠品/数量(指定SKU和不指定SKU)，不计算金额，仅计算数量
     *
     * @param beginDate
     * @param endDate
     * @param promSaleDetailId
     * @param controlType1
     * @return
     */
    private BigDecimal caculateOrderHappendBalance(String beginDate, String endDate, String promSaleDetailId, String controlType1, String controlType2) {
        BigDecimal sumBalance = BigDecimal.ZERO;
        // 查询未关闭销售订单
        List<OrderItem> opendOrderItems = getB2bOrderItemByPromSaleDetailId(beginDate, endDate, promSaleDetailId, IsCloseEnum.OPEN.getCode());
        sumBalance = sumBalance.add(nonDeliverOrderBalance(opendOrderItems, controlType1, controlType2));

        // 查询已关闭销售订单
        List<OrderItem> closedOrderItems = getB2bOrderItemByPromSaleDetailId(beginDate, endDate, promSaleDetailId, IsCloseEnum.CLOSE.getCode());
        sumBalance = sumBalance.add(deliverOrderBalance(closedOrderItems, controlType1, controlType2));
        return sumBalance;
    }

    /**
     * 按订单订货数计算
     *
     * @param orderItems
     * @param controlType1
     * @param controlType2
     * @return
     */
    private BigDecimal nonDeliverOrderBalance(List<OrderItem> orderItems, String controlType1, String controlType2) {
        BigDecimal sumBalance = BigDecimal.ZERO;
        for (OrderItem orderItem : orderItems) {
            // 订货数量
            BigDecimal orderNum = orderItem.getOrderNum();
            // 对于控制类型为：赠品/数量(指定SKU和不指定SKU)，不计算金额，仅计算数量
            sumBalance = orderItemBalance(controlType1, controlType2, sumBalance, orderItem, orderNum);
        }
        return sumBalance;
    }

    /**
     * 按订单累计发货数计算
     *
     * @param orderItems
     * @param controlType1
     * @param controlType2
     * @return
     */
    private BigDecimal deliverOrderBalance(List<OrderItem> orderItems, String controlType1, String controlType2) {
        BigDecimal sumBalance = BigDecimal.ZERO;
        for (OrderItem orderItem : orderItems) {
            // 累计发货数量
            BigDecimal orderNum = orderItem.getDeliveryNum();
            // 对于控制类型为：赠品/数量(指定SKU和不指定SKU)，不计算金额，仅计算数量
            sumBalance = orderItemBalance(controlType1, controlType2, sumBalance, orderItem, orderNum);
        }
        return sumBalance;
    }

    private BigDecimal orderItemBalance(String controlType1, String controlType2, BigDecimal sumBalance, OrderItem orderItem, BigDecimal orderNum) {
        if (controlType1.equals(PromWayExtEnum.ONLYGIFT.getName()) && controlType2.equals(ControlType2.NUM.desc)) {
            sumBalance = sumBalance.add(orderNum);
        } else {
            BigDecimal price = BigDecimal.ZERO;
            if (controlType1.equals(PromWayExtEnum.ONLYBUY.getName())) {
                // 仅买取成交价
                price = orderItem.getDealPrice();
            } else {
                // 仅赠（成交价为0）取原价
                price = orderItem.getSalePrice();
            }
            sumBalance = sumBalance.add(orderNum.multiply(price));
        }
        return sumBalance;
    }

    /**
     * 根据促销活动合同明细id查询关联的销售订单表体明细
     *
     * @param beginDate        查询开始日期
     * @param endDate          查询结束日期
     * @param promSaleDetailId 销售合同明细id
     * @param closeFlag        订单关闭标识
     * @return
     */
    private List<OrderItem> getB2bOrderItemByPromSaleDetailId(String beginDate, String endDate, String promSaleDetailId, Integer closeFlag) {
        if (StringUtils.isEmpty(beginDate) || StringUtils.isEmpty(endDate)) {
            throw new BusinessException("查询开始日期或结束日期不能为空");
        }
        if (Integer.valueOf(beginDate) > Integer.valueOf(endDate)) {
            throw new BusinessException("查询开始日期应小于等于结束日期");
        }
        if (StringUtils.isEmpty(promSaleDetailId)) {
            throw new BusinessException("销售合同明细id为空");
        }

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select boi.* from b2b_order_item boi\n")
                .append("inner join b2b_order bo on boi.order_id = bo.id\n")
                .append("inner join PROM_CT_SALE_DETAIL pcsd on boi.ext01 = pcsd.id\n")
                .append("where to_char(bo.creation_time,'YYYYMMDD') >= :beginDate\n")
                .append("and to_char(bo.creation_time,'YYYYMMDD') <= :endDate\n")
                .append("and pcsd.id = :promSaleDetailId\n")
                .append("and bo.dr = 0\n");

        if (closeFlag != null) {
            sqlBuilder.append("and bo.is_close = :closeFlag\n");
        }

        sqlBuilder.append("and bo.dr = 0\n")
                .append("order by bo.creation_time");
        Query query = entityManager.createNativeQuery(sqlBuilder.toString(), OrderItem.class);
        query.setParameter("beginDate", beginDate);
        query.setParameter("endDate", endDate);
        query.setParameter("promSaleDetailId", promSaleDetailId);

        if (closeFlag != null) {
            query.setParameter("closeFlag", closeFlag);
        }
        List<OrderItem> orderItems = query.getResultList();
        return orderItems;
    }

    /**
     * 校验查询日期合法性
     *
     * @param beginDate
     * @param endDate
     */
    private void checkDate(String beginDate, String endDate) {
        if (StringUtils.isBlank(beginDate) || StringUtils.isBlank(endDate)) {
            throw new BusinessException("查询日期不能为空");
        }
        if (!isValidDate(beginDate) || !isValidDate(endDate)) {
            throw new BusinessException("查询日期格式不正确，正确格式如：20191010");
        }
        if (Integer.valueOf(beginDate) > Integer.valueOf(endDate)) {
            throw new BusinessException("查询开始日期应小于结束日期");
        }

        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyyMMdd");
        DateTime dateTime = DateTime.parse(beginDate, dateTimeFormatter);
        DateTime beginDateNextYear = dateTime.plusYears(1);
        String strBeginDateNextYear = beginDateNextYear.toString("yyyyMMdd");
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
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
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

    /**
     * 这里不能用内部类，entityManager.createNativeQuery(sql, CustActivityRuleDeital.class)调用时会报：
     * "org.hibernate.InstantiationException: No default constructor for entity:  : com.yonyou.occ.report.service.impl.CustPolicyAccountDetailReportServiceImpl$CustActivityRuleDeital"
     */
//    @NoArgsConstructor
//    @AllArgsConstructor
//    @Data
//    @Entity
//    public class CustActivityRuleDeital {
//
//        /**
//         * 促销合同明细主键
//         */
//        @Id
//        private String promSaleDetailId;
////    @Id
////    private String id;
//
//        /**
//         * 客户分类名称
//         */
//        private String customerCategoryName;
//
//        /**
//         * 市场区域名称
//         */
//        private String marketAreaName;
//
//        /**
//         * 客户编码
//         */
//        private String customerCode;
//
//        /**
//         * 客户名称
//         */
//        private String customerName;
//
//        /**
//         * 活动有效期 - 开始时间
//         */
//        private String activityBeginDate;
//
//        /**
//         * 活动有效期 - 结束时间
//         */
//        private String activityEndDate;
//
//        /**
//         * 活动编码
//         */
//        private String activityCode;
//
//        /**
//         * 活动名称
//         */
//        private String activityName;
//
//        /**
//         * 促销类型，0表示买赠，1表示仅买或者仅赠
//         */
//        private Integer promType;
//
//        /**
//         * 商品类别: 1 商品 2 产品 3 组合 4 分类
//         */
//        private Integer proType;
//
//        /**
//         * 数量
//         */
//        private BigDecimal num;
//
//        /**
//         * 金额
//         */
//        private BigDecimal amount;
//    }

}
