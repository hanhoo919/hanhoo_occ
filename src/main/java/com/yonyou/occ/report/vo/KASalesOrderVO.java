package com.yonyou.occ.report.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelIgnore;
import com.yonyou.occ.b2b.common.enums.OrderSource;
import com.yonyou.occ.b2b.common.enums.OrderStatus;
import com.yonyou.ocm.base.enums.BillTypeEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class KASalesOrderVO {
    // 订单主键
    @ExcelIgnore
    private String orderId;

    // 订单字表主键
    @ExcelIgnore
    private String orderItemId;

    // 渠道编码
    @ExcelIgnore
    @Excel(name = "渠道编码", height = 11, width = 15)
    private String channelCode;

    // 渠道名称
    @Excel(name = "渠道", height = 11, width = 15)
    private String channelName;

    // 市场区域编码
    @ExcelIgnore
    @Excel(name = "市场区域编码", height = 11, width = 15)
    private String marketAreaCode;

    // 市场区域名称
    @Excel(name = "市场区域名称", height = 11, width = 15)
    private String marketAreaName;

    // 省份编码
    @ExcelIgnore
    @Excel(name = "省份编码", height = 11, width = 15)
    private String provinceCode;

    // 省份名称
    @Excel(name = "省份名称", height = 11, width = 15)
    private String provinceName;

    // 订单编码
    @Excel(name = "客户订单编码", height = 11, width = 15)
    private String orderCode;

    // 单据类型
    @ExcelIgnore
    private String billType;

    @Excel(name = "单据类型", height = 11, width = 15)
    private String billTypeView;

    // 订单类型
    @ExcelIgnore
    private String orderType;

    @Excel(name = "订单类型", height = 11, width = 15)
    private String orderTypeName;

    // 订单状态
    private String orderStatus;

    @Excel(name = "订单状态", height = 11, width = 15)
    private String orderStatusView;

    // 订单来源
    @ExcelIgnore
    private String orderSource;

    @Excel(name = "订单来源", height = 11, width = 15)
    private String orderSourceView;

    // 订单日期
    @Excel(name = "订单日期", exportFormat = "yyyy-MM-dd", height = 11, width = 15)
    private Date orderDate;

    // 客户/代理商编码
    @Excel(name = "客户/代理商编码", height = 11, width = 15)
    private String customerCode;

    // 客户/代理商名称
    @Excel(name = "客户/代理商名称", height = 11, width = 15)
    private String customerName;


    // 商品编码
    @Excel(name = "物料编码", height = 11, width = 15)
    private String goodsCode;

    // 商品名称
    @Excel(name = "物料名称", height = 11, width = 15)
    private String goodsName;

    // 物料组
    @Excel(name = "物料组", height = 11, width = 15)
    private String materialGroup;

    // 物料分类
    @Excel(name = "物料分类", height = 11, width = 15)
    private String materialClass;

    //品牌
    @Excel(name = "品牌", height = 11, width = 15)
    private String brandName;

    //系列
    @Excel(name = "销售系列", height = 11, width = 15)
    private String saleSeriesName;

    // 总数量
    @ExcelIgnore
    @Excel(name = "总数量", height = 11, width = 15)
    private String totalNum;

    // 原金额（不含促销，不含冲抵）
    @ExcelIgnore
    @Excel(name = "原金额", height = 11, width = 15)
    private BigDecimal totalAmount;

    // 总成交金额
    @ExcelIgnore
    @Excel(name = "总成交金额", height = 11, width = 15)
    private BigDecimal totalDealAmount;

    // 促销金额
    @ExcelIgnore
    @Excel(name = "促销金额", height = 11, width = 15)
    private BigDecimal promAmount;

    // 费用冲抵金额
    @ExcelIgnore
    @Excel(name = "费用冲抵金额", height = 11, width = 15)
    private BigDecimal offsetAmount;

    // 总重量
    @ExcelIgnore
    @Excel(name = "总重量", height = 11, width = 15)
    private BigDecimal totalWeight;

    // 总净重
    @ExcelIgnore
    @Excel(name = "总净重", height = 11, width = 15)
    private BigDecimal totalNetWeight;

    // 总体积
    @ExcelIgnore
    @Excel(name = "总体积", height = 11, width = 15)
    private BigDecimal totalVolume;

    // 主数量（销售数量）
    @ExcelIgnore
    @Excel(name = "主数量（销售数量）", height = 11, width = 15)
    private BigDecimal mainNum;

    // 订单数量
    @Excel(name = "订单数量", height = 11, width = 15)
    private BigDecimal orderNum;

    // 累计发货数量
    @Excel(name = "交货数量", height = 11, width = 15)
    private BigDecimal deliveryNum;

    //剩余数量
    @Excel(name = "剩余数量", height = 11, width = 15)
    private BigDecimal residualNum;

    // 订单原价
    @Excel(name = "单价（元）", height = 11, width = 15)
    private BigDecimal salePrice;

    // 成交价
    @Excel(name = "毛价（元）", height = 11, width = 15)
    private BigDecimal dealPrice;

    // 商品档案上的价格
    @ExcelIgnore
    @Excel(name = "商品档案上的价格", height = 11, width = 15)
    private BigDecimal basePrice;

    // 促销折扣价格
    @ExcelIgnore
    private BigDecimal promPrice;

    // 原金额（不含促销，不含冲抵）
    @ExcelIgnore
    private BigDecimal amount;

    //行折扣（%）
    @Excel(name = "行折扣（%）")
    private BigDecimal lineDiscount;

    //整单折扣（%）
    @Excel(name = "整单折扣（%）")
    private BigDecimal fullOrderDiscount;

    // 成交金额
    @Excel(name = "行总计（元）")
    private BigDecimal dealAmount;

    // 实际退货金额
    @ExcelIgnore
    private BigDecimal returnAmount;

    // 累计入库数量
    @ExcelIgnore
    private BigDecimal stockInNum;

    // 累计出库数量
    @ExcelIgnore
    private BigDecimal stockOutNum;

    // 累计退货数量
    @ExcelIgnore
    private BigDecimal returnNum;

    // 累计退款数量
    @ExcelIgnore
    private BigDecimal refundNum;

    // 累计签收数量
    @ExcelIgnore
    private BigDecimal signNum;

    // 累计补货数量
    @ExcelIgnore
    private BigDecimal replenishNum;

    // 累计协同数量
    @ExcelIgnore
    private BigDecimal coordinateNum;

    //仓库编码
    @Excel(name = "仓库代码", height = 11, width = 15)
    private String warehouseCode;

    //仓库名称
    @Excel(name = "仓库名称", height = 11, width = 15)
    private String warehouseName;

    //创建者
    @Excel(name = "创建者", height = 11, width = 15)
    private String creator;

    //单据备注
    @Excel(name = "单据备注", height = 11, width = 15)
    private String billRemark;

    //行备注
    @Excel(name = "行备注", height = 11, width = 15)
    private String lineRemark;

    //收货人
    @ExcelIgnore
    @Excel(name = "收货人", height = 11, width = 15)
    private String receiver;

    //收货客户
    @Excel(name = "收货方", height = 11, width = 15)
    private String receiveCustomer;

    public String getBillTypeView() {
        for (BillTypeEnum billTypeEnums : BillTypeEnum.values()) {
            if (billTypeEnums.getCode().equals(this.billType)) {
                this.billTypeView = billTypeEnums.getName();
            }
        }
        return billTypeView;
    }

    public void setBillTypeView(String billTypeView) {
        this.billTypeView = billTypeView;
    }

    public String getOrderStatusView() {
        for (OrderStatus orderStatusEnum : OrderStatus.values()) {
            if (orderStatusEnum.getCode().equals(this.orderStatus)) {
                this.orderStatusView = orderStatusEnum.getName();
            }
        }
        return orderStatusView;
    }

    public void setOrderStatusView(String orderStatusView) {
        this.orderStatusView = orderStatusView;
    }

    public String getOrderSourceView() {
        for (OrderSource orderSourceEnum : OrderSource.values()) {
            if (orderSourceEnum.getCode().equals(this.orderSource)) {
                this.orderSourceView = orderSourceEnum.getName();
            }
        }
        return orderSourceView;
    }

    public void setOrderSourceView(String orderSourceView) {
        this.orderSourceView = orderSourceView;
    }

    public KASalesOrderVO() {
    }

    public KASalesOrderVO(String orderId, String orderItemId, String orderType, String orderTypeName, String billType, String billTypeView, String orderCode, Date orderDate, String orderStatus, String orderStatusView, String totalNum, BigDecimal totalAmount, BigDecimal totalDealAmount, BigDecimal promAmount, BigDecimal offsetAmount, BigDecimal totalWeight, BigDecimal totalNetWeight, BigDecimal totalVolume, String orderSource, String orderSourceView, BigDecimal mainNum, BigDecimal orderNum, BigDecimal salePrice, BigDecimal dealPrice, BigDecimal basePrice, BigDecimal promPrice, BigDecimal amount, BigDecimal dealAmount, BigDecimal returnAmount, BigDecimal deliveryNum, BigDecimal residualNum, BigDecimal stockInNum, BigDecimal stockOutNum, BigDecimal returnNum, BigDecimal refundNum, BigDecimal signNum, BigDecimal replenishNum, BigDecimal coordinateNum, String customerCode, String customerName, String channelCode, String channelName, String marketAreaCode, String marketAreaName, String provinceCode, String provinceName, String goodsCode, String goodsName, String materialGroup, String materialClass, String creator, String billRemark, String lineRemark, String brandName, String saleSeriesName, String warehouseCode, String warehouseName, String receiver, String receiveCustomer) {
        this.orderId = orderId;
        this.orderItemId = orderItemId;
        this.orderType = orderType;
        this.orderTypeName = orderTypeName;
        this.billType = billType;
        this.billTypeView = billTypeView;
        this.orderCode = orderCode;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.orderStatusView = orderStatusView;
        this.totalNum = totalNum;
        this.totalAmount = totalAmount;
        this.totalDealAmount = totalDealAmount;
        this.promAmount = promAmount;
        this.offsetAmount = offsetAmount;
        this.totalWeight = totalWeight;
        this.totalNetWeight = totalNetWeight;
        this.totalVolume = totalVolume;
        this.orderSource = orderSource;
        this.orderSourceView = orderSourceView;
        this.mainNum = mainNum;
        this.orderNum = orderNum;
        this.salePrice = salePrice;
        this.dealPrice = dealPrice;
        this.basePrice = basePrice;
        this.promPrice = promPrice;
        this.amount = amount;
        this.dealAmount = dealAmount;
        this.returnAmount = returnAmount;
        this.deliveryNum = deliveryNum;
        this.residualNum = residualNum;
        this.stockInNum = stockInNum;
        this.stockOutNum = stockOutNum;
        this.returnNum = returnNum;
        this.refundNum = refundNum;
        this.signNum = signNum;
        this.replenishNum = replenishNum;
        this.coordinateNum = coordinateNum;
        this.customerCode = customerCode;
        this.customerName = customerName;
        this.channelCode = channelCode;
        this.channelName = channelName;
        this.marketAreaCode = marketAreaCode;
        this.marketAreaName = marketAreaName;
        this.provinceCode = provinceCode;
        this.provinceName = provinceName;
        this.goodsCode = goodsCode;
        this.goodsName = goodsName;
        this.materialGroup = materialGroup;
        this.materialClass = materialClass;
        this.creator = creator;
        this.billRemark = billRemark;
        this.lineRemark = lineRemark;
        this.brandName = brandName;
        this.saleSeriesName = saleSeriesName;
        this.warehouseCode = warehouseCode;
        this.warehouseName = warehouseName;
        this.receiver = receiver;
        this.receiveCustomer = receiveCustomer;
    }
}
