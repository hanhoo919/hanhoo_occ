package com.yonyou.occ.report.service.handler;

import com.yonyou.occ.report.entity.BalanceReport;
import com.yonyou.occ.report.service.dto.BalanceReportDto;
import com.yonyou.occ.report.service.dto.BalanceReportOptionItemDto;
import com.yonyou.occ.report.service.dto.BalanceReportRequireItemDto;
import com.yonyou.ocm.common.utils.DateUtil;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.*;

/**
 * 代理商账余报表 查询数据 处理类
 */
@Component
public class BalanceReportSearchHandler {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * @param searchParams
     * @return
     */
    public List<BalanceReportDto> doSearch(Map<String, Object> searchParams) {
        StringBuffer querySql = new StringBuffer("");
        //创建sql
        createSql(querySql, searchParams);

        //执行sql 获取结果集
        Query query = this.entityManager.createNativeQuery(querySql.toString(), BalanceReport.class);
        List<BalanceReport> balanceReportList = query.getResultList();

        //处理结果集，获取集合
        Map<String, BalanceReportDto> balanceReportDtoMap = processResult(balanceReportList);

        //整理成list
        List<BalanceReportDto> balanceReportDtoList = new ArrayList<>(balanceReportDtoMap.size());
        for (String key : balanceReportDtoMap.keySet()) {
            balanceReportDtoList.add(balanceReportDtoMap.get(key));
        }
        return balanceReportDtoList;
    }

    /**
     * 拼接sql语句
     *
     * @param querySql
     */
    private void createSql(StringBuffer querySql, Map<String, Object> searchParams) {
        querySql.append(" select ");
        //拼接查询的字段
        querySql.append(" pcs.id ct_id, pcsd.id detail_id, bc.code cust_code, bc.name cust_name, bad.name province_name, ");
        querySql.append(" bma.name market_name,pa.name activity_name,pa.code activity_code, an.name node_name, ");
        querySql.append(" pa.start_date, pa.end_date, pae.require_total_amount, pcs.total_claim_group, ");
        querySql.append(" pcs.total_require_repay, pas.pro_code,pas.rule_desc, pas.rebate, pcsd.totalrow_order_amount, ");
        querySql.append(" pcs.total_option_repay, pcsd.totalrow_ct_amount, pas.row_num,pas.pro_type, pas.amount ,pas.count,");
        querySql.append(" pcsd.totalrow_order_num,pas.rule_type,pcsd.totalrow_ct_num ,pcsd.is_require,pas.sale_price ");

        querySql.append(" from prom_ct_sale pcs ");
        //拼接连表
        querySql.append(" left join prom_ct_sale_detail pcsd on pcs.id = pcsd.prom_ct_id ");
        querySql.append(" left join prom_activity_singlerulelist pas on pcsd.single_activitylist_id = pas.id ");
        querySql.append(" left join prom_activity pa on pa.id = pcs.activity_id ");
        querySql.append(" left join prom_activity_ext pae on pa.id = pae.id ");
        querySql.append(" left join activity_node an on an.id = pa.active_node ");
        querySql.append(" left join base_customer bc on pcs.customer_id = bc.id ");
        querySql.append(" left join base_administrative_division bad on bc.province_id = bad.id ");
        querySql.append(" left join base_market_area bma on bc.market_area_id = bma.id ");

        //拼接查询条件
        //拼接默认查询条件
        querySql.append(" where pcs.dr=0 ");

        //拼接时间段
        querySql.append(" and  pa.start_date between to_date( '");
        querySql.append(DateUtil.from_Long_to_String(DateUtil.yyMMdd_HHmmss, Long.parseLong(searchParams.get("startDate").toString())));
        querySql.append("' , 'yyyy-mm-dd hh24:mi:ss') and  to_date(  '");
        querySql.append(DateUtil.from_Long_to_String(DateUtil.yyMMdd_HHmmss, Long.parseLong(searchParams.get("endDate").toString())));
        querySql.append("' , 'yyyy-mm-dd hh24:mi:ss') ");

        //拼接客户id
        if (searchParams.containsKey("customerId")) {
            querySql.append(" and bc.id = '");
            querySql.append(searchParams.get("customerId"));
            querySql.append("'");
        }


    }

    /**
     * 处理结果集
     *
     * @param resultList
     */
    private Map<String, BalanceReportDto> processResult(List<BalanceReport> resultList) {
        //将结果集封装成报表对象
        Map<String, BalanceReportDto> balanceReportDtoMap = getStringBalanceReportDtoMap(resultList);
        //处理部分需要计算的字段
        Iterator<Map.Entry<String, BalanceReportDto>> iterator = balanceReportDtoMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, BalanceReportDto> balanceReportDtoEntry = iterator.next();
            //累加获取必选已发金额
            for (BalanceReportRequireItemDto balanceReportRequireItemDto : balanceReportDtoEntry.getValue().getBalanceReportRequireDto().getRequireItemDtoList()) {
                if (3 == balanceReportRequireItemDto.getProTypeCode()) {
                    balanceReportDtoEntry.getValue().getBalanceReportRequireDto().setRequireUsedAmount(balanceReportDtoEntry.getValue().getBalanceReportRequireDto().getRequireUsedAmount().add(balanceReportRequireItemDto.getStockOutAmount()));
                }
            }
            //计算必选未清金额
            balanceReportDtoEntry.getValue().getBalanceReportRequireDto().setRequireUnusedAmount(balanceReportDtoEntry.getValue().getBalanceReportRequireDto().getRequireTotalAmount().subtract(balanceReportDtoEntry.getValue().getBalanceReportRequireDto().getRequireUsedAmount()));

            //累加获取可选已发金额
            for (BalanceReportOptionItemDto balanceReportOptionItemDto : balanceReportDtoEntry.getValue().getBalanceReportOptionDto().getOptionItemDtoList()) {
                if (3 == balanceReportOptionItemDto.getProTypeCode()) {
                    balanceReportDtoEntry.getValue().getBalanceReportOptionDto().setOptionUsedAmount(balanceReportDtoEntry.getValue().getBalanceReportOptionDto().getOptionUsedAmount().add(balanceReportOptionItemDto.getStockOutAmount()));
                }
            }
            //计算可选未清金额
            balanceReportDtoEntry.getValue().getBalanceReportOptionDto().setOptionUnusedAmount(balanceReportDtoEntry.getValue().getBalanceReportOptionDto().getOptionTotalAmount().subtract(balanceReportDtoEntry.getValue().getBalanceReportOptionDto().getOptionUsedAmount()));
        }

        return balanceReportDtoMap;
    }

    /**
     * 将结果集封装成报表对象
     *
     * @param resultList
     * @return
     */
    private Map<String, BalanceReportDto> getStringBalanceReportDtoMap(List<BalanceReport> resultList) {
        Map<String, BalanceReportDto> balanceReportDtoMap = new HashMap<String, BalanceReportDto>();
        for (BalanceReport balanceReport : resultList) {
            //判断合同主键是否已存在，已存在，说明已生成对应的账余主实体dto
            if (!balanceReportDtoMap.containsKey(balanceReport.getCtId())) {
                balanceReportDtoMap.put(balanceReport.getCtId(), getBalanceReportDto(balanceReport));
            }

            if (1 == balanceReport.getIsRequire()) {
                //判断单组必选金额是否为空，不为空且不为0，则生成必选子实体
                balanceReportDtoMap.get(balanceReport.getCtId()).getBalanceReportRequireDto().getRequireItemDtoList().add(getBalanceReportRequireItemDto(balanceReport));
            } else {
                //判断已回款可选金额是否为空，不为空且不为0，则生成可选子实体
                if (balanceReport.getTotalOptionRepay().compareTo(BigDecimal.ZERO) > 0) {
                    balanceReportDtoMap.get(balanceReport.getCtId()).getBalanceReportOptionDto().getOptionItemDtoList().add(getBalanceReportOptionItemDto(balanceReport));
                }
            }
        }
        return balanceReportDtoMap;
    }

    /**
     * 获取 可选子实体
     *
     * @param balanceReport
     * @return
     */
    private BalanceReportOptionItemDto getBalanceReportOptionItemDto(BalanceReport balanceReport) {
        BalanceReportOptionItemDto balanceReportOptionItemDto = new BalanceReportOptionItemDto();
        balanceReportOptionItemDto.setProCode(balanceReport.getProCode());
        balanceReportOptionItemDto.setProName(balanceReport.getRuleDesc().toString().substring(5, balanceReport.getRuleDesc().toString().length()));
        //判断该行是否为指定SKU 不是指定SKU 则需要判断是哪种控制类型
        BigDecimal total = null;
        BigDecimal stockout = null;
        balanceReportOptionItemDto.setProTypeCode(balanceReport.getRuleType());
        if (balanceReport.getProType() != 1 && balanceReport.getTotalrowCtAmount().compareTo(BigDecimal.ZERO) > 0) {
            balanceReportOptionItemDto.setControlType("金额");
            total = balanceReport.getTotalrowCtAmount();
            stockout = balanceReport.getTotalrowOrderAmount();
        } else {
            balanceReportOptionItemDto.setControlType("数量");
            total = balanceReport.getTotalrowCtNum();
            stockout = balanceReport.getTotalrowOrderNum();
        }
        //计算未清金额或数量 “合计”减去“已发”
        balanceReportOptionItemDto.setUnUsed(total.subtract(stockout));
        balanceReportOptionItemDto.setStockOutAmount(balanceReport.getTotalrowOrderAmount());

        return balanceReportOptionItemDto;
    }

    /**
     * 获取 必选子实体
     *
     * @param balanceReport
     * @return
     */
    private BalanceReportRequireItemDto getBalanceReportRequireItemDto(BalanceReport balanceReport) {
        BalanceReportRequireItemDto balanceReportRequireItemDto = new BalanceReportRequireItemDto();
        balanceReportRequireItemDto.setProCode(balanceReport.getProCode());
        balanceReportRequireItemDto.setProName(balanceReport.getRuleDesc().toString().substring(5, balanceReport.getRuleDesc().toString().length()));
        //判断是订购还是赠送 3为订购，4为赠送
        balanceReportRequireItemDto.setProTypeCode(balanceReport.getRuleType());
        if (3 == balanceReportRequireItemDto.getProTypeCode()) {
            balanceReportRequireItemDto.setProType("订购项目");
        } else {
            balanceReportRequireItemDto.setProType("赠送项目");
        }
        //判断该行是否为指定SKU 不是指定SKU 则需要判断是哪种控制类型
        if (balanceReport.getProType() != 1 && balanceReport.getTotalrowCtAmount().compareTo(BigDecimal.ZERO) > 0) {
            balanceReportRequireItemDto.setControlType("金额");
            balanceReportRequireItemDto.setUnit(balanceReport.getAmount());
            balanceReportRequireItemDto.setStockOut(balanceReport.getTotalrowOrderAmount());
        } else {
            balanceReportRequireItemDto.setControlType("数量");
            balanceReportRequireItemDto.setUnit(balanceReport.getCount());
            balanceReportRequireItemDto.setStockOut(balanceReport.getTotalrowOrderNum());
        }
        balanceReportRequireItemDto.setDiscount(balanceReport.getRebate());
        balanceReportRequireItemDto.setReturnGroup(balanceReport.getTotalClaimGroup());
        balanceReportRequireItemDto.setStockOutAmount(balanceReport.getTotalrowOrderAmount());
        //计算合计 “单组数量/金额”乘以“回款组数”
        balanceReportRequireItemDto.setTotal(balanceReportRequireItemDto.getUnit().multiply(balanceReportRequireItemDto.getReturnGroup()));
        //计算未清金额/数量 “合计”减去“已发”
        balanceReportRequireItemDto.setUnUsedAmount(balanceReportRequireItemDto.getTotal().subtract(balanceReportRequireItemDto.getStockOut()));
        //计算未清代理商总计（回款） “未清”乘以“行折扣”
        if (3 == balanceReportRequireItemDto.getProTypeCode()) {
            //判断是金额控制还是数量控制
            if (balanceReport.getProType() != 1 && balanceReport.getTotalrowCtAmount().compareTo(BigDecimal.ZERO) > 0) {
                //金额控制
                balanceReportRequireItemDto.setTotalUnusedAmount(balanceReportRequireItemDto.getUnUsedAmount().multiply(balanceReportRequireItemDto.getDiscount().divide(new BigDecimal(100))));
            } else {
                //数量控制
                balanceReportRequireItemDto.setTotalUnusedAmount(balanceReport.getSalePrice().multiply(balanceReportRequireItemDto.getUnUsedAmount()));
            }
        } else {
            //赠品 为0
            balanceReportRequireItemDto.setTotalUnusedAmount(BigDecimal.ZERO);
        }

        return balanceReportRequireItemDto;
    }

    /**
     * 封装 账余dto
     *
     * @param balanceReport
     * @return
     */
    private BalanceReportDto getBalanceReportDto(BalanceReport balanceReport) {
        BalanceReportDto balanceReportDto = new BalanceReportDto();
        balanceReportDto.init();
        balanceReportDto.setCustCode(balanceReport.getCustCode());
        balanceReportDto.setCustName(balanceReport.getCustName());
        balanceReportDto.setProvinceName(balanceReport.getProvinceName());
        balanceReportDto.setMarketAreaName(balanceReport.getMarketName());
        balanceReportDto.setActivityName(balanceReport.getActivityName());
        balanceReportDto.setActivityCode(balanceReport.getActivityCode());
        balanceReportDto.setActivityType(balanceReport.getNodeName());
        balanceReportDto.setStartDate(balanceReport.getStartDate());
        balanceReportDto.setEndDate(balanceReport.getEndDate());
        balanceReportDto.getBalanceReportRequireDto().setRequireUnitAmount(balanceReport.getRequireTotalAmount());
        balanceReportDto.getBalanceReportRequireDto().setRequireBackGroup(balanceReport.getTotalClaimGroup());
        balanceReportDto.getBalanceReportRequireDto().setRequireTotalAmount(balanceReport.getTotalRequireRepay());
        balanceReportDto.getBalanceReportOptionDto().setOptionTotalAmount(balanceReport.getTotalOptionRepay());
        return balanceReportDto;
    }

}

