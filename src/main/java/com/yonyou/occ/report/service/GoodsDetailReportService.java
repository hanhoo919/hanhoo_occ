package com.yonyou.occ.report.service;

import com.yonyou.occ.b2b.client.PrivilegeClient;
import com.yonyou.occ.report.service.dto.*;
import com.yonyou.occ.report.service.handler.GoodsDetailReportExcelHandler;
import com.yonyou.ocm.common.utils.CommonUtils;
import org.apache.commons.collections4.CollectionUtils;
import com.yonyou.ocm.common.annos.IndustryExt;
import com.yonyou.ocm.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 出货明细及政策余额报表Service
 *
 * @author mwh
 * @date 19/08/15
 */
//TODO   年月-零售价出货和零售价余额只考虑单品和套盒两种分类

/**
 *  1)	查询条件：客户（非必录）和时间段（必录）。
 2)	查询条件控制点：其中客户条件可以是客户编码或者客户名称，时间段为手动通过时间空间选择开始时间和结束时间，控制必须选择年、月，不能选择日，控制时间段跨度不能超过一年。
 3)	活动查询控制：仅统计活动开始时间在所查询时间段区间内的活动
 4)	门户显示：该报表需要在门户显示，查询条件仅有时间段，表样结构与中台显示一致，仅显示当前登录客户的信息。
 */
@Slf4j
@Service
public class GoodsDetailReportService {
    //商品分类为单品的编码
    private final static String SINGLE_PRODUCT_CODE = "0101";
    //商品分类为套盒的编码
    private final static String BOXES_CODE = "0102";
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    PrivilegeClient privilegeClient;

    /**
     * sql 中 一个in条件中的数量
     */
    private final static int IN_LENGTH = 180;
    /**
     * 销售订单类型
     */
    private final static String ORDER_TYPE = "GeneralSale";
    /**
     * 销售订单类型
     */
    private final static int IS_GIFT = 1;

    @Autowired
    GoodsDetailReportExcelHandler goodsDetailReportExcelHandler;

    /**
     * 账余报表数据导出
     */
    public Map<String, String> exportExcelData(Map<String, Object> searchParams, HttpServletResponse response) {
        return goodsDetailReportExcelHandler.exportExcelData(searchParams, response, "出货明细及政策余额表");
    }

    /**
     * 门户-查询 出货明细及政策余额报表 数据（当前登录的客户）
     * @param startDateString 开始时间，精确到月份
     * @param endDateString     结束时间，精确到月份
     * @auth mwh
     * @date 19/18/15
     */
    public List<GoodsDetailDto> getExportGoodsDetail(String startDateString, String endDateString) {
        String userId = CommonUtils.getCurrentUserId();
        String custId = privilegeClient.getChannelCustomerIdByUserId(userId);
        return getExportGoodsDetail(startDateString, endDateString, custId);
    }

    /**
     * 查询 出货明细及政策余额报表 数据
     * @param startDateString 开始时间，精确到月份
     * @param endDateString     结束时间，精确到月份
     * @param custId  客户编码或者名称
     * @auth mwh
     * @date 19/18/15
     */
    public List<GoodsDetailDto> getExportGoodsDetail(String startDateString, String endDateString, String custId) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = null;
        Date endDate = null;
        try {
            //格式化获取开始时间和结束时间
            startDateString = startDateString + "-01";
            startDate = simpleDateFormat.parse(startDateString);
            //startDate = new Date(2019,03,00);
            endDateString = endDateString + "-01";
            endDate = simpleDateFormat.parse(endDateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDate);
            calendar.add(Calendar.MONTH, 1);
            endDate = calendar.getTime();
        } catch (ParseException e) {
            log.error("查询时间格式化异常");
            throw new BusinessException("查询时间格式化异常:" + e.getStackTrace());
        }
        //List<CsExportGoodsDetailDto> exportGoodsDetails = new ArrayList<>();
        //1.获取客户信息 //根据条件查询客户信息，包括客户id,客户编码，客户名称，区域、省份
        List<GoodsDetailDto> csExportGoodsDetailDtos = getCustInfo(custId);

        //2.获取每月的货款出货和零售价出货
        //获取时间范围内所有的销售出库单明细
        List<GoodsDetailSaleOutOrderItemDto> goodsDetailSaleOutOrderItemDtos = getSaleOutOrderItems(startDate, endDate);

        //查询出所有的销售出库单对应的销售订单类型
        Map<String, GoodsDetailB2bOrderDto> orderMap = getOrderTypes(goodsDetailSaleOutOrderItemDtos);
        //查询出所有销售出库单明细对应的成交价
        Map<String, BigDecimal> orderItemDealPrice = getOrdersDealPrice(goodsDetailSaleOutOrderItemDtos);
        //组装每月出货额度，零售额度，出货额度合计，零售额度合计
        buildExportGoodsOfMonths(csExportGoodsDetailDtos, startDateString, endDateString, goodsDetailSaleOutOrderItemDtos, orderMap, orderItemDealPrice);


        //3.获取货款余额和零售价余额

        //查询出时间范围内的所有认领单
        /*List<GoodsDetailClaimDto> goodsDetailClaimDtos =  getClaims(startDate,endDate);

        //查询出销售订单中仅买的金额
        Map<String,BigDecimal> orderOnlyBuyAmount  = getOrderOnlyBuyAmount(startDate,endDate);
        //查询出销售订单中仅赠的金额
        Map<String,BigDecimal> orderOnlyGiftAmount = getOrderOnlyGiftAmount(startDate,endDate);
        //查询客户反认领金额
        Map<String,BigDecimal> custClaimVersaAmount = getCustClaimVersaAmount(startDate,endDate);
        // 组装货款余额
        buildExportGoodsOfPaymentBalance(csExportGoodsDetailDtos, goodsDetailClaimDtos,custClaimVersaAmount,orderOnlyBuyAmount);
        //组装零售余额
        buildExportGoodsOfRetailBalance(csExportGoodsDetailDtos, goodsDetailClaimDtos,orderOnlyGiftAmount,startDate,endDate);*/


        //查询出开始时间在时间范围内的促销活动
        List<String> activityIds = getActivityIds(startDate, endDate);
        //查询出活动所对应的合同
        Map<String, String> saleIdAndCustId = getCtSaleIdAndCustId(activityIds);
        //获取合同的必选认领金额-进去已提货金额
        List<String> ctSaleIds = new ArrayList<>();
        for (Map.Entry<String, String> entry : saleIdAndCustId.entrySet()) {
            ctSaleIds.add(entry.getKey());
        }
        Map<String, BigDecimal> salePaymentBalanceMap = getCtSalePaymentBalance(ctSaleIds);
        buildExportGoodsOfPaymentBalance(csExportGoodsDetailDtos, saleIdAndCustId, salePaymentBalanceMap);
        //获取合同的零售余额
        Map<String, BigDecimal> retailPaymentBalanceMap = getCtSaleRetailBalance(ctSaleIds);
        //组装零售余额
        buildExportGoodsOfRetailBalance(csExportGoodsDetailDtos, saleIdAndCustId, retailPaymentBalanceMap);
        return csExportGoodsDetailDtos;
    }

    /**
     * 组装零售余额余额
     * @param csExportGoodsDetailDtos
     * @param saleIdAndCustId
     * @param retailPaymentBalanceMap
     */
    private void buildExportGoodsOfRetailBalance(List<GoodsDetailDto> csExportGoodsDetailDtos, Map<String, String> saleIdAndCustId, Map<String, BigDecimal> retailPaymentBalanceMap) {
        for (GoodsDetailDto goodsDetailDtos : csExportGoodsDetailDtos) {
            BigDecimal retailBalance = BigDecimal.ZERO;
            for (Map.Entry<String, String> entry : saleIdAndCustId.entrySet()) {
                //是当前客户下的合同
                if (goodsDetailDtos.getCustId().equals(entry.getValue())) {
                    //可能合同明细中没有非指定SKU或者SKU为单品和套盒商品
                    retailBalance = retailBalance.add(retailPaymentBalanceMap.get(entry.getKey()) == null ? BigDecimal.ZERO : retailPaymentBalanceMap.get(entry.getKey()));
                }
            }
            goodsDetailDtos.setRetailBalance(retailBalance);
        }
    }

    /**
     * 组装货款余额
     * @param csExportGoodsDetailDtos
     * @param saleIdAndCustId
     * @param salePaymentBalanceMap
     */
    private void buildExportGoodsOfPaymentBalance(List<GoodsDetailDto> csExportGoodsDetailDtos,
                                                  Map<String, String> saleIdAndCustId, Map<String, BigDecimal> salePaymentBalanceMap) {
        for (GoodsDetailDto goodsDetailDtos : csExportGoodsDetailDtos) {
            BigDecimal paymentBalance = BigDecimal.ZERO;
            for (Map.Entry<String, String> entry : saleIdAndCustId.entrySet()) {
                //是当前客户下的合同
                if (goodsDetailDtos.getCustId().equals(entry.getValue())) {
                    paymentBalance = paymentBalance.add(salePaymentBalanceMap.get(entry.getKey()));
                }
            }
            goodsDetailDtos.setPaymentBalance(paymentBalance);
        }

    }


    /**
     *
     * 组装每月货款出货额度，零售出货额度   货款出货合计、零售出货合计
     * 每个客户每个月份根据取出销售订单类型为普通销售的销售出库单明细，根据明细的数量
     * 计算货款出货和零售出货    其中货款出货 = 累加当前月份当前客户下的出库单明细数量*销售订单明细成交价
     *  其中零售出货 = 累加当前月份当前客户下的出库单明细数量*销售订单明细基础价
     *  货款出货合计 = 当前客户下所有货款出货累加
     */
    private void buildExportGoodsOfMonths(List<GoodsDetailDto> csExportGoodsDetailDtos, String beginDate, String endDate
            , List<GoodsDetailSaleOutOrderItemDto> goodsDetailSaleOutOrderItemDtos, Map<String, GoodsDetailB2bOrderDto> orderMap, Map<String, BigDecimal> orderItemDealPrice) {

        //开始时间和结束时间之间的月份
        List<Date> dates = getMonthBetween(beginDate, endDate);
        SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM");

        //遍历客户
        for (GoodsDetailDto csExportGoodsDetailDto : csExportGoodsDetailDtos) {
            List<GoodsDetailOfMonth> goodsDetailOfMonths = new ArrayList<>();
            BigDecimal paymentAmounTotal = BigDecimal.ZERO;
            BigDecimal retailAmoutTotal = BigDecimal.ZERO;
            //遍历月份
            for (Date date : dates) {
                GoodsDetailOfMonth goodsDetailOfMonth = new GoodsDetailOfMonth();
                //非赠品的当前客户下当月的出库明细
                List<GoodsDetailSaleOutOrderItemDto> notIsGifts = new ArrayList<>();
                //赠品的当前客户下当月的出库明细
                List<GoodsDetailSaleOutOrderItemDto> isGifts = new ArrayList<>();
                for (GoodsDetailSaleOutOrderItemDto goodsDetailSaleOutOrderItemDto : goodsDetailSaleOutOrderItemDtos) {
                    //判断是否是当前客户下当月的销售订单明细
                    boolean result = checkCustAndMonthAndOrderType(csExportGoodsDetailDto.getCustId(), date, orderMap, goodsDetailSaleOutOrderItemDto);
                    if (result) {
                        if (goodsDetailSaleOutOrderItemDto.getIsGift() == IS_GIFT) {
                            isGifts.add(goodsDetailSaleOutOrderItemDto);
                        } else {
                            notIsGifts.add(goodsDetailSaleOutOrderItemDto);
                        }
                    }
                }
                BigDecimal notIsGiftsAmount = BigDecimal.ZERO;
                for (GoodsDetailSaleOutOrderItemDto goodsDetailSaleOutOrderItemDto : notIsGifts) {
                    notIsGiftsAmount = notIsGiftsAmount.add(orderItemDealPrice.get(goodsDetailSaleOutOrderItemDto.getFirstBillBCode()).multiply(new BigDecimal(goodsDetailSaleOutOrderItemDto.getFactOutNum().toString())));
                }
                BigDecimal isGiftsAmount = BigDecimal.ZERO;
                for (GoodsDetailSaleOutOrderItemDto goodsDetailSaleOutOrderItemDto : isGifts) {
                    isGiftsAmount = isGiftsAmount.add(orderItemDealPrice.get(goodsDetailSaleOutOrderItemDto.getFirstBillBCode()).multiply(new BigDecimal(goodsDetailSaleOutOrderItemDto.getFactOutNum().toString())));
                }
                goodsDetailOfMonth.setDate(sm.format(date));
                goodsDetailOfMonth.setPaymentAmount(notIsGiftsAmount);
                goodsDetailOfMonth.setRetailAmout(isGiftsAmount);
                goodsDetailOfMonths.add(goodsDetailOfMonth);
                paymentAmounTotal = paymentAmounTotal.add(notIsGiftsAmount);
                retailAmoutTotal = retailAmoutTotal.add(isGiftsAmount);
            }

            csExportGoodsDetailDto.setGoodsDetailOfMonths(goodsDetailOfMonths);
            csExportGoodsDetailDto.setPaymentAmounTotal(paymentAmounTotal);
            csExportGoodsDetailDto.setRetailAmoutTotal(retailAmoutTotal);
        }
    }


    /**
     * 查询获取客户信息   客户id,客户编码，客户名称，区域、省份
     * @param custId 客户id  可以为空
     * @return csExportGoodsDetailDtos
     * add by mwh
     * 190816
     */
    private List<GoodsDetailDto> getCustInfo(String custId) {
        List<GoodsDetailDto> csExportGoodsDetailDtos = new ArrayList<>();
        StringBuilder jpql = new StringBuilder();
        jpql.append("SELECT c.id,c.code,c.name as cust_name")
                .append(" ,p.name as province_name,a.name as area_name FROM BASE_CUSTOMER c,STOCK_PROVINCE_SHORT p")
                .append(" ,BASE_MARKET_AREA a where c.PROVINCE_ID = p.id(+) and c.MARKET_AREA_ID = a.id(+)");
        if (StringUtils.isNotEmpty(custId)) {
            jpql.append(" and c.id = '").append(custId).append("'");
        }
        Query query = this.entityManager.createNativeQuery(jpql.toString());
        List rows = query.getResultList();
        if (CollectionUtils.isEmpty(rows)) {
            return csExportGoodsDetailDtos;
        }
        for (int i = 0; i < rows.size(); ++i) {
            Object[] row = (Object[]) ((Object[]) rows.get(i));
            GoodsDetailDto exportGoodsDetailDto = new GoodsDetailDto();
            if (row[0] != null && StringUtils.isNotEmpty(row[0].toString())) {
                exportGoodsDetailDto.setCustId(row[0].toString());
            }
            if (row[1] != null && StringUtils.isNotEmpty(row[1].toString())) {
                exportGoodsDetailDto.setCustCode(row[1].toString());
            }
            if (row[2] != null && StringUtils.isNotEmpty(row[2].toString())) {
                exportGoodsDetailDto.setCustName(row[2].toString());
            }
            if (row[3] != null && StringUtils.isNotEmpty(row[3].toString())) {
                exportGoodsDetailDto.setProvince(row[3].toString());
            }
            if (row[4] != null && StringUtils.isNotEmpty(row[4].toString())) {
                exportGoodsDetailDto.setMarketArea(row[4].toString());
            }
            csExportGoodsDetailDtos.add(exportGoodsDetailDto);

        }
        return csExportGoodsDetailDtos;
    }


    /**
     * 查询出所有的在时间范围呢的出库单明细(销售出库单id  id   是否赠品 is_gift, 实际发货数 fact_out_num
     *  出库日期OUT_DATE，销售订单号 FIRST_BILL_CODE  销售订单明细号 FIRST_BILL_CODE
     * 销售订单明细号 FIRST_BILL_BCODE )
     *@param  startDate 出库单明细的开始时间
     *@param  endDate 出库单明细的结束时间
     */
    private List<GoodsDetailSaleOutOrderItemDto> getSaleOutOrderItems(Date startDate, Date endDate) {
        List<GoodsDetailSaleOutOrderItemDto> goodsDetailSaleOutOrderItemDtos = new ArrayList<>();
        //需要查询出当月的明细，因此结束时间月份+1
        Calendar c = Calendar.getInstance();
        c.setTime(endDate);
        c.add(Calendar.MONTH, 1);
        endDate = c.getTime();

        StringBuilder jpql = new StringBuilder();
        SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        jpql.append("select id,IS_GIFT,FACT_OUT_NUM,OUT_DATE,FIRST_BILL_CODE,FIRST_BILL_BCODE from STOCK_SALE_OUT_ORDER_ITEM")
                .append(" where OUT_DATE>to_date('").append(sm.format(startDate)).append("' , 'yyyy-mm-dd hh24:mi:ss')  and OUT_DATE<to_date('").append(sm.format(endDate)).append("' , 'yyyy-mm-dd hh24:mi:ss') ");
        jpql.append("and( IS_GIFT = 0 or ( IS_GIFT =1 and (GOODS_ID in (select id from BASE_GOODS where GOODS_CATEGORY_ID in (SELECT id from BASE_GOODS_CATEGORY where CODE in ('").append(SINGLE_PRODUCT_CODE).append("','").append(BOXES_CODE).append("') )) )))");
        Query query = this.entityManager.createNativeQuery(jpql.toString());
        List rows = query.getResultList();
        if (CollectionUtils.isEmpty(rows)) {
            return goodsDetailSaleOutOrderItemDtos;
        }
        for (int i = 0; i < rows.size(); ++i) {
            Object[] row = (Object[]) ((Object[]) rows.get(i));
            GoodsDetailSaleOutOrderItemDto goodsDetailSaleOutOrderItemDto = new GoodsDetailSaleOutOrderItemDto();
            if (row[0] != null && StringUtils.isNotEmpty(row[0].toString())) {
                goodsDetailSaleOutOrderItemDto.setId(row[0].toString());
            }
            if (row[1] != null && StringUtils.isNotEmpty(row[1].toString())) {
                goodsDetailSaleOutOrderItemDto.setIsGift(((BigDecimal) row[1]).intValue());
            }
            if (row[2] != null && StringUtils.isNotEmpty(row[2].toString())) {
                goodsDetailSaleOutOrderItemDto.setFactOutNum(((BigDecimal) row[2]).intValue());
            }
            if (row[3] != null && StringUtils.isNotEmpty(row[3].toString())) {
                try {
                    goodsDetailSaleOutOrderItemDto.setOutDate(sm.parse(row[3].toString()));
                } catch (ParseException e) {
                    log.error("查询出库单明细时时间转换异常：" + e.getStackTrace());
                    throw new BusinessException("查询出库单明细时时间转换异常");
                }
            }
            if (row[4] != null && StringUtils.isNotEmpty(row[4].toString())) {
                goodsDetailSaleOutOrderItemDto.setFirstBillCode(row[4].toString());
            }
            if (row[5] != null && StringUtils.isNotEmpty(row[5].toString())) {
                goodsDetailSaleOutOrderItemDto.setFirstBillBCode(row[5].toString());
            }
            goodsDetailSaleOutOrderItemDtos.add(goodsDetailSaleOutOrderItemDto);

        }
        return goodsDetailSaleOutOrderItemDtos;
    }


    /**
     * 查询出需要的销售订单id和订单类型ORDER_TYPE
     *@param  goodsDetailSaleOutOrderItemDtos 销售出库单明细集合
     */
    private Map<String, GoodsDetailB2bOrderDto> getOrderTypes(List<GoodsDetailSaleOutOrderItemDto> goodsDetailSaleOutOrderItemDtos) {
        Map<String, GoodsDetailB2bOrderDto> orderTypes = new HashMap<>();
        if (CollectionUtils.isEmpty(goodsDetailSaleOutOrderItemDtos)) {
            return orderTypes;
        }
        StringBuilder jpql = new StringBuilder();
        jpql.append("select id ,order_type, CUSTOMER_ID from B2B_ORDER where id ");
        List<String> orderCodes = new ArrayList<>();
        for (GoodsDetailSaleOutOrderItemDto goodsDetailSaleOutOrderItemDto : goodsDetailSaleOutOrderItemDtos) {
            orderCodes.add(goodsDetailSaleOutOrderItemDto.getFirstBillCode());
        }
        //由于in操作符的长度限制为1000，当需要查询的销售订单大于1000时，需要截取分批处理
        int inLenth = 0;
        if (goodsDetailSaleOutOrderItemDtos.size() % IN_LENGTH == 0) {
            inLenth = goodsDetailSaleOutOrderItemDtos.size() / IN_LENGTH;
        } else {
            inLenth = goodsDetailSaleOutOrderItemDtos.size() / IN_LENGTH + 1;
        }
        List<List<String>> orderorderCodess = new ArrayList<>();
        for (int i = 0; i < inLenth; i++) {
            List<String> codes = orderCodes.subList(i * IN_LENGTH, orderCodes.size() >= (i + 1) * IN_LENGTH ? (i + 1) * IN_LENGTH : orderCodes.size());
            ;
            //如果IN_LENGTH=1000 第一次截取0-999个  第二次截取1000-1999个id
           /* if(orderCodes.size()>=(i+1)*IN_LENGTH){
               codes = orderCodes.subList(i*IN_LENGTH,(i+1)*IN_LENGTH);
            }else {
                codes = orderCodes.subList(i*IN_LENGTH,(i+1)*IN_LENGTH);
            }*/

            orderorderCodess.add(codes);
        }
        //拼接 in 条件
        //形如 in ('SO20190813000005','SO20190813000006') or ORDER_CODE in ('SO20190813000007','SO20190807000001')
        for (int i = 0; i < orderorderCodess.size(); i++) {
            if (i == 0) {
                jpql.append(" in (");
                List<String> codes = orderorderCodess.get(i);
                for (int j = 0; j < codes.size(); j++) {
                    if (j == 0) {
                        jpql.append(" '").append(codes.get(j)).append("' ");
                    } else {
                        jpql.append(" ,'").append(codes.get(j)).append("' ");
                    }
                }
                jpql.append(")");
            } else {
                jpql.append(" or id in (");
                List<String> codes = orderorderCodess.get(i);
                for (int j = 0; j < codes.size(); j++) {
                    if (j == 0) {
                        jpql.append(" '").append(codes.get(j)).append("' ");
                    } else {
                        jpql.append(" ,'").append(codes.get(j)).append("' ");
                    }
                }
                jpql.append(")");
            }
        }


        Query query = this.entityManager.createNativeQuery(jpql.toString());

        List rows = query.getResultList();
        if (CollectionUtils.isEmpty(rows)) {
            return orderTypes;
        }
        for (int i = 0; i < rows.size(); ++i) {
            Object[] row = (Object[]) ((Object[]) rows.get(i));
            GoodsDetailB2bOrderDto goodsDetailB2BOrderDto = new GoodsDetailB2bOrderDto();
            if (row[0] != null && StringUtils.isNotEmpty(row[0].toString())) {
                goodsDetailB2BOrderDto.setId(row[0].toString());
            }
            if (row[1] != null && StringUtils.isNotEmpty(row[1].toString())) {
                goodsDetailB2BOrderDto.setOrderType(row[1].toString());
            }
            if (row[2] != null && StringUtils.isNotEmpty(row[2].toString())) {
                goodsDetailB2BOrderDto.setCustId(row[2].toString());
            }

            orderTypes.put(goodsDetailB2BOrderDto.getId(), goodsDetailB2BOrderDto);


        }
        return orderTypes;
    }

    /**
     * 查询出需要的销售订单明细id和价格 赠品是原价   非赠品是成交价DEAL_PRICE
     *@param  goodsDetailSaleOutOrderItemDtos 销售出库单明细集合
     */
    private Map<String, BigDecimal> getOrdersDealPrice(List<GoodsDetailSaleOutOrderItemDto> goodsDetailSaleOutOrderItemDtos) {
        Map<String, BigDecimal> orderTypes = new HashMap<>();
        if (CollectionUtils.isEmpty(goodsDetailSaleOutOrderItemDtos)) {
            return orderTypes;
        }
        StringBuilder jpql = new StringBuilder();
        jpql.append("select id ,deal_price,SALE_PRICE,IS_GIFT from B2B_ORDER_ITEM where id ");

        List<String> orderItemIds = new ArrayList<>();
        for (GoodsDetailSaleOutOrderItemDto goodsDetailSaleOutOrderItemDto : goodsDetailSaleOutOrderItemDtos) {
            orderItemIds.add(goodsDetailSaleOutOrderItemDto.getFirstBillBCode());
        }
        //由于in操作符的长度限制为1000，当需要查询的销售订单大于1000时，需要截取分批处理
        int inLenth = 0;
        if (goodsDetailSaleOutOrderItemDtos.size() % IN_LENGTH == 0) {
            inLenth = goodsDetailSaleOutOrderItemDtos.size() / IN_LENGTH;
        } else {
            inLenth = goodsDetailSaleOutOrderItemDtos.size() / IN_LENGTH + 1;
        }

        List<List<String>> orderItemIdss = new ArrayList<>();
        for (int i = 0; i < inLenth; i++) {

            //如果IN_LENGTH=1000 第一次截取0-999个  第二次截取1000-1999个id
            List<String> codes = orderItemIds.subList(i * IN_LENGTH, orderItemIds.size() >= (i + 1) * IN_LENGTH ? (i + 1) * IN_LENGTH : orderItemIds.size());
            ;

            orderItemIdss.add(codes);
        }
        //拼接 in 条件
        //形如 in ('SO20190813000005','SO20190813000006') or ORDER_CODE in ('SO20190813000007','SO20190807000001')
        for (int i = 0; i < orderItemIdss.size(); i++) {
            if (i == 0) {
                jpql.append(" in (");
                List<String> codes = orderItemIdss.get(i);
                for (int j = 0; j < codes.size(); j++) {
                    if (j == 0) {
                        jpql.append(" '").append(codes.get(j)).append("' ");
                    } else {
                        jpql.append(" ,'").append(codes.get(j)).append("' ");
                    }
                }
                jpql.append(")");
            } else {
                jpql.append(" or id in (");
                List<String> codes = orderItemIdss.get(i);
                for (int j = 0; j < codes.size(); j++) {
                    if (j == 0) {
                        jpql.append(" '").append(codes.get(j)).append("' ");
                    } else {
                        jpql.append(" ,'").append(codes.get(j)).append("' ");
                    }
                }
                jpql.append(")");
            }
        }


        Query query = this.entityManager.createNativeQuery(jpql.toString());

        List rows = query.getResultList();
        if (CollectionUtils.isEmpty(rows)) {
            return orderTypes;
        }
        for (int i = 0; i < rows.size(); ++i) {
            Object[] row = (Object[]) ((Object[]) rows.get(i));
            String key = null;
            BigDecimal value = null;
            if (row[0] != null && StringUtils.isNotEmpty(row[0].toString())) {
                key = row[0].toString();
            }
            int isGift = 0;
            if (row[3] != null && StringUtils.isNotEmpty(row[3].toString())) {
                isGift = ((BigDecimal) row[3]).intValue();
            }
            //赠品取原价，非赠品取零售价
            if (isGift == 1) {
                if (row[2] != null && StringUtils.isNotEmpty(row[2].toString())) {
                    value = ((BigDecimal) row[2]);
                }
            } else {
                if (row[1] != null && StringUtils.isNotEmpty(row[1].toString())) {
                    value = ((BigDecimal) row[1]);
                }
            }


            orderTypes.put(key, value);


        }
        return orderTypes;
    }

    /**
     * 获取合同信息 <合同id,CtSaleDto>
     * @param saleIds
     * @return
     */
    private Map<String, GoodesCtSaleDto> getSaleActivity(List<String> saleIds) {
        Map<String, GoodesCtSaleDto> ctSaleDtoMap = new HashMap<>();
        if (CollectionUtils.isEmpty(saleIds)) {
            return ctSaleDtoMap;
        }
        StringBuilder jpql = new StringBuilder();
        jpql.append("select id,ACTIVITY_ID,CUSTOMER_ID from PROM_CT_SALE where id  ");


        //由于in操作符的长度限制为1000，当需要查询的销售订单大于1000时，需要截取分批处理
        int inLenth = 0;
        if (saleIds.size() % IN_LENGTH == 0) {
            inLenth = saleIds.size() / IN_LENGTH;
        } else {
            inLenth = saleIds.size() / IN_LENGTH + 1;
        }
        List<List<String>> idss = new ArrayList<>();
        for (int i = 0; i < inLenth; i++) {

            //如果IN_LENGTH=1000 第一次截取0-999个  第二次截取1000-1999个id
            List<String> ids = saleIds.subList(i * IN_LENGTH, saleIds.size() >= (i + 1) * IN_LENGTH ? (i + 1) * IN_LENGTH : saleIds.size());
            ;

            idss.add(ids);
        }
        //拼接 in 条件
        //形如 in ('SO20190813000005','SO20190813000006') or ORDER_CODE in ('SO20190813000007','SO20190807000001')
        for (int i = 0; i < idss.size(); i++) {
            if (i == 0) {
                jpql.append(" in (");
                List<String> codes = idss.get(i);
                for (int j = 0; j < codes.size(); j++) {
                    if (j == 0) {
                        jpql.append(" '").append(codes.get(j)).append("' ");
                    } else {
                        jpql.append(" ,'").append(codes.get(j)).append("' ");
                    }
                }
                jpql.append(")");
            } else {
                jpql.append(" or id in (");
                List<String> codes = idss.get(i);
                for (int j = 0; j < codes.size(); j++) {
                    if (j == 0) {
                        jpql.append(" '").append(codes.get(j)).append("' ");
                    } else {
                        jpql.append(" ,'").append(codes.get(j)).append("' ");
                    }
                }
                jpql.append(")");
            }
        }


        Query query = this.entityManager.createNativeQuery(jpql.toString());

        List rows = query.getResultList();
        if (CollectionUtils.isEmpty(rows)) {
            return ctSaleDtoMap;
        }
        for (int i = 0; i < rows.size(); ++i) {
            GoodesCtSaleDto ctSaleDto = new GoodesCtSaleDto();
            Object[] row = (Object[]) ((Object[]) rows.get(i));
            String key = null;
            String value = null;
            if (row[0] != null && StringUtils.isNotEmpty(row[0].toString())) {
                ctSaleDto.setSaleId(row[0].toString());
                ;
            }
            if (row[1] != null && StringUtils.isNotEmpty(row[1].toString())) {
                ctSaleDto.setActivityId(row[1].toString());
            }

            if (row[2] != null && StringUtils.isNotEmpty(row[2].toString())) {
                ctSaleDto.setCustId(row[2].toString());
            }
            ctSaleDtoMap.put(ctSaleDto.getSaleId(), ctSaleDto);
        }
        return ctSaleDtoMap;
    }


    /**
     * 每组活动仅赠金额
     */
    private Map<String, BigDecimal> getActivityGiftAmount(List<String> activityIds) {
        Map<String, BigDecimal> activityGiftAmount = new HashMap<>();
        if (CollectionUtils.isEmpty(activityIds)) {
            return activityGiftAmount;
        }
        StringBuilder jpql = new StringBuilder();
        jpql.append("select ACTIVITY_ID,sum( nvl(amount,0))  ")
                .append("from PROM_ACTIVITY_SINGLERULELIST ")
                .append(" where RULE_TYPE = 4  and (ACTIVITY_ID ");

        //由于in操作符的长度限制为1000，当需要查询的销售订单大于1000时，需要截取分批处理
        int inLenth = 0;
        if (activityIds.size() % IN_LENGTH == 0) {
            inLenth = activityIds.size() / IN_LENGTH;
        } else {
            inLenth = activityIds.size() / IN_LENGTH + 1;
        }
        List<List<String>> idss = new ArrayList<>();
        for (int i = 0; i < inLenth; i++) {

            //如果IN_LENGTH=1000 第一次截取0-999个  第二次截取1000-1999个id
            List<String> ids = activityIds.subList(i * IN_LENGTH, activityIds.size() >= (i + 1) * IN_LENGTH ? (i + 1) * IN_LENGTH : activityIds.size());
            ;

            idss.add(ids);
        }
        //拼接 in 条件
        //形如 in ('SO20190813000005','SO20190813000006') or ORDER_CODE in ('SO20190813000007','SO20190807000001')
        for (int i = 0; i < idss.size(); i++) {
            if (i == 0) {
                jpql.append(" in (");
                List<String> codes = idss.get(i);
                for (int j = 0; j < codes.size(); j++) {
                    if (j == 0) {
                        jpql.append(" '").append(codes.get(j)).append("' ");
                    } else {
                        jpql.append(" ,'").append(codes.get(j)).append("' ");
                    }
                }
                jpql.append(")");
            } else {
                jpql.append(" or ACTIVITY_ID in (");
                List<String> codes = idss.get(i);
                for (int j = 0; j < codes.size(); j++) {
                    if (j == 0) {
                        jpql.append(" '").append(codes.get(j)).append("' ");
                    } else {
                        jpql.append(" ,'").append(codes.get(j)).append("' ");
                    }
                }
                jpql.append(")");
            }
        }
        jpql.append(")  group by ACTIVITY_ID ");
        Query query = this.entityManager.createNativeQuery(jpql.toString());
        List rows = query.getResultList();
        if (CollectionUtils.isEmpty(rows)) {
            return activityGiftAmount;
        }
        for (int i = 0; i < rows.size(); ++i) {
            Object[] row = (Object[]) ((Object[]) rows.get(i));
            String key = null;
            BigDecimal value = null;
            if (row[0] != null && StringUtils.isNotEmpty(row[0].toString())) {
                key = row[0].toString();
            }
            if (row[1] != null && StringUtils.isNotEmpty(row[1].toString())) {
                value = ((BigDecimal) row[1]);
            }
            activityGiftAmount.put(key, value);
        }
        return activityGiftAmount;
    }

    //获取客户下活动认领的组数  <客户id,<活动id,活动组数>>
    private Map<String, Map<String, Integer>> getCustClaimGroup(List<GoodsDetailClaimDto> goodsDetailClaimDtos) {
        //<客户id,<活动id,活动组数>>
        Map<String, Map<String, Integer>> custActivityGroup = new HashMap<>();
        for (GoodsDetailClaimDto goodsDetailClaimDto : goodsDetailClaimDtos) {
            //map中存在当前客户
            if (custActivityGroup.containsKey(goodsDetailClaimDto.getCustomerId())) {
                //外层map的value <活动，组数>
                Map<String, Integer> activityGroup = custActivityGroup.get(goodsDetailClaimDto.getCustomerId());
                //内层map中存在当前活动
                if (activityGroup.containsKey(goodsDetailClaimDto.getActivityId())) {
                    //当前火哦的那个增加组数
                    activityGroup.put(goodsDetailClaimDto.getActivityId(), activityGroup.get(goodsDetailClaimDto.getActivityId()) + goodsDetailClaimDto.getClaimGroup());
                } else {
                    //直接put当前活动
                    activityGroup.put(goodsDetailClaimDto.getActivityId(), goodsDetailClaimDto.getClaimGroup());
                }

            } else {
                //新增当前客户下的<活动，组数>Map
                HashMap<String, Integer> activityGroup = new HashMap<>();
                activityGroup.put(goodsDetailClaimDto.getActivityId(), goodsDetailClaimDto.getClaimGroup());
                custActivityGroup.put(goodsDetailClaimDto.getCustomerId(), activityGroup);
            }
        }
        return custActivityGroup;
    }


    /**
     * 判断是否是当前客户下当月的销售订单明细
     * @param custId
     * @param date
     * @param orderMap
     * @return
     */
    private boolean checkCustAndMonthAndOrderType(String custId, Date date, Map<String, GoodsDetailB2bOrderDto> orderMap, GoodsDetailSaleOutOrderItemDto goodsDetailSaleOutOrderItemDto) {
        boolean result = false;
        String outOrderItemCustId = orderMap.get(goodsDetailSaleOutOrderItemDto.getFirstBillCode()).getCustId();
        String outOrderItemOrderType = orderMap.get(goodsDetailSaleOutOrderItemDto.getFirstBillCode()).getOrderType();
        Date outOrderItemDate = goodsDetailSaleOutOrderItemDto.getOutDate();
        //当前客户，当前月份，并且单据类型是普通销售，返回true
        if (StringUtils.equals(custId, outOrderItemCustId) && checkDate(date, outOrderItemDate) && StringUtils.equals(ORDER_TYPE, outOrderItemOrderType)) {
            result = true;
        }
        return result;
    }

    private boolean checkDate(Date sourceDate, Date targetDate) {
        int sourceDateInt = getYearMonth(sourceDate);
        int tartgetDateInt = getYearMonth(targetDate);
        if (sourceDateInt == tartgetDateInt) {
            return true;
        } else {
            return false;
        }
    }

    private int getYearMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        //设置时间
        cal.setTime(date);
        //获取年份
        int year = cal.get(Calendar.YEAR);
        //获取月份
        int month = cal.get(Calendar.MONTH);
        //返回年份乘以100加上月份的值，因为月份最多2位数，所以年份乘以100可以获取一个唯一的年月数值
        return year * 100 + month;
    }

    private static List<Date> getMonthBetween(String beginDate, String endDate) {
        ArrayList<Date> result = new ArrayList<Date>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");//格式化为年月

        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();
        max.add(Calendar.MONTH, 1);
        try {
            min.setTime(sdf.parse(beginDate));
        } catch (ParseException e) {
            log.error("获取时间段内的月份时时间格式转换异常：" + e.getStackTrace());
            throw new BusinessException("获取时间段内的月份时时间格式转换异常");
        }

        min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);
        try {
            max.setTime(sdf.parse(endDate));
        } catch (ParseException e) {
            log.error("获取时间段内的月份时时间格式转换异常：" + e.getStackTrace());
            throw new BusinessException("获取时间段内的月份时时间格式转换异常");
        }

        max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);

        Calendar curr = min;
        while (curr.before(max)) {
            result.add(curr.getTime());
            curr.add(Calendar.MONTH, 1);
        }
        min = null;
        max = null;
        curr = null;
        return result;
    }

    /**
     * 查询时间范围内的活动
     * @param startDate
     * @param endDate
     * @return
     */
    private List<String> getActivityIds(Date startDate, Date endDate) {
        List<String> ids = new ArrayList<>();
        StringBuilder jpql = new StringBuilder();
        SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        jpql.append(" SELECT a.id FROM PROM_ACTIVITY_EXT ae,PROM_ACTIVITY a  where  a.id = ae.id and  a.dr != 1 ")

                .append(" and a.START_DATE>to_date('").append(sm.format(startDate)).append("' , 'yyyy-mm-dd hh24:mi:ss')   and a.START_DATE<to_date('").append(sm.format(endDate)).append("' , 'yyyy-mm-dd hh24:mi:ss') ");
        Query query = this.entityManager.createNativeQuery(jpql.toString());
        List rows = query.getResultList();
        if (CollectionUtils.isEmpty(rows)) {
            return ids;
        }
        for (int i = 0; i < rows.size(); ++i) {
            Object row = ((Object) rows.get(i));
            if (row != null && StringUtils.isNotEmpty(row.toString())) {
                ids.add(row.toString());
            }
        }
        return ids;
    }

    /**
     * 查询时间范围内的合同
     * @param activityIds
     * @return saleIdAndCustId Map<合同id,custId>
     */
    private Map<String, String> getCtSaleIdAndCustId(List<String> activityIds) {
        Map<String, String> saleIdAndCustId = new HashMap<>();

        if (CollectionUtils.isEmpty(activityIds)) {
            return saleIdAndCustId;
        }
        StringBuilder jpql = new StringBuilder();
        jpql.append("select id,CUSTOMER_ID from prom_ct_sale where activity_id    ");


        //由于in操作符的长度限制为1000，当需要查询的销售订单大于1000时，需要截取分批处理
        int inLenth = 0;
        if (activityIds.size() % IN_LENGTH == 0) {
            inLenth = activityIds.size() / IN_LENGTH;
        } else {
            inLenth = activityIds.size() / IN_LENGTH + 1;
        }

        List<List<String>> idss = new ArrayList<>();
        for (int i = 0; i < inLenth; i++) {

            //如果IN_LENGTH=1000 第一次截取0-999个  第二次截取1000-1999个id
            List<String> ids = activityIds.subList(i * IN_LENGTH, activityIds.size() >= (i + 1) * IN_LENGTH ? (i + 1) * IN_LENGTH : activityIds.size());
            ;

            idss.add(ids);
        }
        //拼接 in 条件
        //形如 in ('SO20190813000005','SO20190813000006') or ORDER_CODE in ('SO20190813000007','SO20190807000001')
        for (int i = 0; i < idss.size(); i++) {
            if (i == 0) {
                jpql.append(" in (");
                List<String> codes = idss.get(i);
                for (int j = 0; j < codes.size(); j++) {
                    if (j == 0) {
                        jpql.append(" '").append(codes.get(j)).append("' ");
                    } else {
                        jpql.append(" ,'").append(codes.get(j)).append("' ");
                    }
                }
                jpql.append(")");
            } else {
                jpql.append(" or activity_id in (");
                List<String> codes = idss.get(i);
                for (int j = 0; j < codes.size(); j++) {
                    if (j == 0) {
                        jpql.append(" '").append(codes.get(j)).append("' ");
                    } else {
                        jpql.append(" ,'").append(codes.get(j)).append("' ");
                    }
                }
                jpql.append(")");
            }
        }
        Query query = this.entityManager.createNativeQuery(jpql.toString());
        List rows = query.getResultList();
        if (CollectionUtils.isEmpty(rows)) {
            return saleIdAndCustId;
        }
        for (int i = 0; i < rows.size(); ++i) {
            Object[] row = (Object[]) ((Object[]) rows.get(i));
            String key = null;
            String value = null;
            if (row[0] != null && StringUtils.isNotEmpty(row[0].toString())) {
                key = row[0].toString();
            }
            if (row[1] != null && StringUtils.isNotEmpty(row[1].toString())) {
                value = row[1].toString();
            }

            saleIdAndCustId.put(key, value);
        }
        return saleIdAndCustId;
    }

    /**
     * 获取合同货款余额  合同必选认领金额减去合同明细为仅买的已提货金额之和
     * @param ctSaleIds
     * @return
     */
    private Map<String, BigDecimal> getCtSalePaymentBalance(List<String> ctSaleIds) {
        //合同id -  货款余额
        Map<String, BigDecimal> salePaymentBalanceMap = new HashMap<>();
        // 合同 - 必选已认领金额
        Map<String, BigDecimal> saleClaimAmount = getSaleClaimAmount(ctSaleIds);

        // 合同 - 必选已提货金额
        Map<String, BigDecimal> saleOrderAmount = getSaleOrderAmount(ctSaleIds);
        for (Map.Entry<String, BigDecimal> entry : saleClaimAmount.entrySet()) {
            BigDecimal paymentBalance = (entry.getValue() == null ? BigDecimal.ZERO :
                    entry.getValue()).subtract(saleOrderAmount.get(entry.getKey()) == null ? BigDecimal.ZERO : saleOrderAmount.get(entry.getKey()));
            salePaymentBalanceMap.put(entry.getKey(), paymentBalance);
        }
        return salePaymentBalanceMap;
    }

    /**
     * 获取合同零售余额   累计合同明细中（明细总金额（totalrowCtAmount） - 累计行提货金额 （totalrowOrderAmount））
     * 其中合同明细是仅赠，并且 商品类型为非指定SKU或者指定SKU但是商品类型是单品和套盒的明细
     * @param ctSaleIds
     * @return
     */
    private Map<String, BigDecimal> getCtSaleRetailBalance(List<String> ctSaleIds) {
        //合同id -  零售余额
        Map<String, BigDecimal> retailBalance = new HashMap<>();


        if (CollectionUtils.isEmpty(ctSaleIds)) {
            return retailBalance;
        }
        StringBuilder jpql = new StringBuilder();
        jpql.append("select sd.PROM_CT_ID, sum(sd.TOTALROW_CT_AMOUNT - sd.TOTALROW_ORDER_AMOUNT) from PROM_CT_SALE_DETAIL sd where sd.PROM_WAY = 4 and sd.TOTALROW_CT_AMOUNT !=0 and (sd.pro_type!=1 or sd.SINGLE_ACTIVITYLIST_ID in (SELECT id from PROM_ACTIVITY_SINGLERULELIST where SINGLE_RULE_ID in( select id from PROM_RULE_DETAIL_HANHOO  where pro_type = 1 and PRO_ID in (select id from BASE_GOODS where GOODS_CATEGORY_ID in (SELECT id from BASE_GOODS_CATEGORY where CODE in ('").append(SINGLE_PRODUCT_CODE).append("','").append(BOXES_CODE).append("0102'))))))");
        jpql.append(" and ( sd.PROM_CT_ID ");

        //由于in操作符的长度限制为1000，当需要查询的销售订单大于1000时，需要截取分批处理
        int inLenth = 0;
        if (ctSaleIds.size() % IN_LENGTH == 0) {
            inLenth = ctSaleIds.size() / IN_LENGTH;
        } else {
            inLenth = ctSaleIds.size() / IN_LENGTH + 1;
        }

        List<List<String>> idss = new ArrayList<>();
        for (int i = 0; i < inLenth; i++) {

            //如果IN_LENGTH=1000 第一次截取0-999个  第二次截取1000-1999个id
            List<String> ids = ctSaleIds.subList(i * IN_LENGTH, ctSaleIds.size() >= (i + 1) * IN_LENGTH ? (i + 1) * IN_LENGTH : ctSaleIds.size());
            ;

            idss.add(ids);
        }
        //拼接 in 条件
        //形如 in ('SO20190813000005','SO20190813000006') or ORDER_CODE in ('SO20190813000007','SO20190807000001')
        for (int i = 0; i < idss.size(); i++) {
            if (i == 0) {
                jpql.append(" in (");
                List<String> codes = idss.get(i);
                for (int j = 0; j < codes.size(); j++) {
                    if (j == 0) {
                        jpql.append(" '").append(codes.get(j)).append("' ");
                    } else {
                        jpql.append(" ,'").append(codes.get(j)).append("' ");
                    }
                }
                jpql.append(")");
            } else {
                jpql.append(" or sd.PROM_CT_ID in (");
                List<String> codes = idss.get(i);
                for (int j = 0; j < codes.size(); j++) {
                    if (j == 0) {
                        jpql.append(" '").append(codes.get(j)).append("' ");
                    } else {
                        jpql.append(" ,'").append(codes.get(j)).append("' ");
                    }
                }
                jpql.append(")");
            }
        }
        jpql.append(")");
        jpql.append(" group by sd.PROM_CT_ID");
        Query query = this.entityManager.createNativeQuery(jpql.toString());
        List rows = query.getResultList();
        if (CollectionUtils.isEmpty(rows)) {
            return retailBalance;
        }
        for (int i = 0; i < rows.size(); ++i) {
            Object[] row = (Object[]) ((Object[]) rows.get(i));
            String key = null;
            BigDecimal value = null;
            if (row[0] != null && StringUtils.isNotEmpty(row[0].toString())) {
                key = row[0].toString();
            }
            if (row[1] != null && StringUtils.isNotEmpty(row[1].toString())) {
                value = ((BigDecimal) row[1]);
                ;
            }

            retailBalance.put(key, value);
        }
        return retailBalance;
    }

    /**
     * 获取客户仅买的也认领金额
     * @param ctSaleIds
     * @return
     */
    private Map<String, BigDecimal> getSaleClaimAmount(List<String> ctSaleIds) {
        // 合同 - 必选+可选的仅买已认领金额
        Map<String, BigDecimal> saleClaimAmount = new HashMap<>();


        if (CollectionUtils.isEmpty(ctSaleIds)) {
            return saleClaimAmount;
        }
        StringBuilder jpql = new StringBuilder();
        jpql.append("select id,TOTAL_REQUIRE_REPAY+TOTAL_OPTION_REPAY from prom_ct_sale where  id  ");


        //由于in操作符的长度限制为1000，当需要查询的销售订单大于1000时，需要截取分批处理
        int inLenth = 0;
        if (ctSaleIds.size() % IN_LENGTH == 0) {
            inLenth = ctSaleIds.size() / IN_LENGTH;
        } else {
            inLenth = ctSaleIds.size() / IN_LENGTH + 1;
        }

        List<List<String>> idss = new ArrayList<>();
        for (int i = 0; i < inLenth; i++) {

            //如果IN_LENGTH=1000 第一次截取0-999个  第二次截取1000-1999个id
            List<String> ids = ctSaleIds.subList(i * IN_LENGTH, ctSaleIds.size() >= (i + 1) * IN_LENGTH ? (i + 1) * IN_LENGTH : ctSaleIds.size());
            ;

            idss.add(ids);
        }
        //拼接 in 条件
        //形如 in ('SO20190813000005','SO20190813000006') or ORDER_CODE in ('SO20190813000007','SO20190807000001')
        for (int i = 0; i < idss.size(); i++) {
            if (i == 0) {
                jpql.append(" in (");
                List<String> codes = idss.get(i);
                for (int j = 0; j < codes.size(); j++) {
                    if (j == 0) {
                        jpql.append(" '").append(codes.get(j)).append("' ");
                    } else {
                        jpql.append(" ,'").append(codes.get(j)).append("' ");
                    }
                }
                jpql.append(")");
            } else {
                jpql.append(" or id in (");
                List<String> codes = idss.get(i);
                for (int j = 0; j < codes.size(); j++) {
                    if (j == 0) {
                        jpql.append(" '").append(codes.get(j)).append("' ");
                    } else {
                        jpql.append(" ,'").append(codes.get(j)).append("' ");
                    }
                }
                jpql.append(")");
            }
        }
        Query query = this.entityManager.createNativeQuery(jpql.toString());
        List rows = query.getResultList();
        if (CollectionUtils.isEmpty(rows)) {
            return saleClaimAmount;
        }
        for (int i = 0; i < rows.size(); ++i) {
            Object[] row = (Object[]) ((Object[]) rows.get(i));
            String key = null;
            BigDecimal value = null;
            if (row[0] != null && StringUtils.isNotEmpty(row[0].toString())) {
                key = row[0].toString();
            }
            if (row[1] != null && StringUtils.isNotEmpty(row[1].toString())) {
                value = ((BigDecimal) row[1]);
                ;
            }

            saleClaimAmount.put(key, value);
        }
        return saleClaimAmount;
    }

    /**
     * 获取合同仅买的已下单金额
     * @param ctSaleIds
     * @return
     */
    private Map<String, BigDecimal> getSaleOrderAmount(List<String> ctSaleIds) {
        // 合同 - 必选已认领金额
        Map<String, BigDecimal> saleOrderAmount = new HashMap<>();

        if (CollectionUtils.isEmpty(ctSaleIds)) {
            return saleOrderAmount;
        }
        StringBuilder jpql = new StringBuilder();
        jpql.append("select cd.PROM_CT_ID,sum(TOTALROW_ORDER_AMOUNT) from PROM_CT_SALE c,PROM_CT_SALE_DETAIL cd where c.id = cd.PROM_CT_ID and PROM_WAY = 3 and  ( cd.PROM_CT_ID  ");


        //由于in操作符的长度限制为1000，当需要查询的销售订单大于1000时，需要截取分批处理
        int inLenth = 0;
        if (ctSaleIds.size() % IN_LENGTH == 0) {
            inLenth = ctSaleIds.size() / IN_LENGTH;
        } else {
            inLenth = ctSaleIds.size() / IN_LENGTH + 1;
        }

        List<List<String>> idss = new ArrayList<>();
        for (int i = 0; i < inLenth; i++) {

            //如果IN_LENGTH=1000 第一次截取0-999个  第二次截取1000-1999个id
            List<String> ids = ctSaleIds.subList(i * IN_LENGTH, ctSaleIds.size() >= (i + 1) * IN_LENGTH ? (i + 1) * IN_LENGTH : ctSaleIds.size());
            ;

            idss.add(ids);
        }
        //拼接 in 条件
        //形如 in ('SO20190813000005','SO20190813000006') or ORDER_CODE in ('SO20190813000007','SO20190807000001')
        for (int i = 0; i < idss.size(); i++) {
            if (i == 0) {
                jpql.append(" in (");
                List<String> codes = idss.get(i);
                for (int j = 0; j < codes.size(); j++) {
                    if (j == 0) {
                        jpql.append(" '").append(codes.get(j)).append("' ");
                    } else {
                        jpql.append(" ,'").append(codes.get(j)).append("' ");
                    }
                }
                jpql.append(")");
            } else {
                jpql.append(" or cd.PROM_CT_ID in (");
                List<String> codes = idss.get(i);
                for (int j = 0; j < codes.size(); j++) {
                    if (j == 0) {
                        jpql.append(" '").append(codes.get(j)).append("' ");
                    } else {
                        jpql.append(" ,'").append(codes.get(j)).append("' ");
                    }
                }
                jpql.append(")");

            }
        }
        jpql.append(")");
        jpql.append(" group by cd.PROM_CT_ID ");
        Query query = this.entityManager.createNativeQuery(jpql.toString());
        List rows = query.getResultList();
        if (CollectionUtils.isEmpty(rows)) {
            return saleOrderAmount;
        }
        for (int i = 0; i < rows.size(); ++i) {
            Object[] row = (Object[]) ((Object[]) rows.get(i));
            String key = null;
            BigDecimal value = null;
            if (row[0] != null && StringUtils.isNotEmpty(row[0].toString())) {
                key = row[0].toString();
            }
            if (row[1] != null && StringUtils.isNotEmpty(row[1].toString())) {
                value = ((BigDecimal) row[1]);
                ;
            }

            saleOrderAmount.put(key, value);
        }
        return saleOrderAmount;
    }

}
