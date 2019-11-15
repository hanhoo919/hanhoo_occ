package com.yonyou.occ.report.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 梁松流
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class KASalesOrderEntity implements Serializable {
    // 订单主键
    @Id
    private String orderId;

    // 订单字表主键
    private String orderItemId;

    // 订单类型
    private String orderType;

    private String orderTypeName;

    // 单据类型
    private String billType;

    // 订单编码
    private String orderCode;

    // 订单日期
    private Date orderDate;

    // 订单状态
    private String orderStatus;

    // 总数量
    private String totalNum;

    // 原金额（不含促销，不含冲抵）
    private BigDecimal totalAmount;

    // 总成交金额
    private BigDecimal totalDealAmount;

    // 促销金额
    private BigDecimal promAmount;

    // 费用冲抵金额
    private BigDecimal offsetAmount;

    // 总重量
    private BigDecimal totalWeight;

    // 总净重
    private BigDecimal totalNetWeight;

    // 总体积
    private BigDecimal totalVolume;

    // 订单来源
    private String orderSource;

    // 主数量（销售数量）
    private BigDecimal mainNum;

    // 订单数量
    private BigDecimal orderNum;

    // 订单原价
    private BigDecimal salePrice;

    // 成交价
    private BigDecimal dealPrice;

    // 商品档案上的价格
    private BigDecimal basePrice;

    // 促销折扣价格
    private BigDecimal promPrice;

    // 原金额（不含促销，不含冲抵）
    private BigDecimal amount;

    // 成交金额
    private BigDecimal dealAmount;

    // 实际退货金额
    private BigDecimal returnAmount;

    // 累计发货数量
    private BigDecimal deliveryNum;

    //剩余数量
    private BigDecimal residualNum;

    // 累计入库数量
    private BigDecimal stockInNum;

    // 累计出库数量
    private BigDecimal stockOutNum;

    // 累计退货数量
    private BigDecimal returnNum;

    // 累计退款数量
    private BigDecimal refundNum;

    // 累计签收数量
    private BigDecimal signNum;

    // 累计补货数量
    private BigDecimal replenishNum;

    // 累计协同数量
    private BigDecimal coordinateNum;

    // 客户/代理商编码
    private String customerCode;

    // 客户/代理商名称
    private String customerName;

    // 渠道编码
    private String channelCode;

    // 渠道名称
    private String channelName;

    // 市场区域编码
    private String marketAreaCode;

    // 市场区域名称
    private String marketAreaName;

    // 省份编码
    private String provinceCode;

    // 省份名称
    private String provinceName;

    // 商品编码
    private String goodsCode;

    // 商品名称
    private String goodsName;

    // 物料组
    private String materialGroup;

    // 物料分类
    private String materialClass;

    //创建者
    private String creator;

    //单据备注
    private String billRemark;

    //行备注
    private String lineRemark;

    //品牌
    private String brandName;

    //系列
    private String saleSeriesName;

    //仓库编码
    private String warehouseCode;

    //仓库名称
    private String warehouseName;

    //收货方
    private String receiver;

    //收货客户
    private String receiveCustomer;
}
