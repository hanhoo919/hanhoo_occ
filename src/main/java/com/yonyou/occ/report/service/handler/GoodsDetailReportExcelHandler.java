package com.yonyou.occ.report.service.handler;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.yonyou.iuap.context.InvocationInfoProxy;
import com.yonyou.occ.report.service.GoodsDetailReportService;
import com.yonyou.occ.report.service.dto.*;
import com.yonyou.ocm.common.enums.ExcelRedisKeyEnums;
import com.yonyou.ocm.common.exception.BusinessException;
import com.yonyou.ocm.common.utils.CacheUtil;
import com.yonyou.ocm.common.utils.file.FileManager;
import com.yonyou.ocm.iuap.utils.InvocationInfoUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

/**
 * 出货余额及政策余额报表 导出功能 处理类
 */
@Slf4j
@Component
public class GoodsDetailReportExcelHandler extends BaseExcelHandler<GoodsDetailDto> {

    @Autowired
    private FileManager fileManager;

    @Autowired
    private GoodsDetailReportService goodsDetailReportService;
    private static String START_DATE_STRING = "startDateString";
    private static String END_DATE_STRING = "endDateString";
    private static String CUST_ID = "custId";

    @Override
    protected List<GoodsDetailDto> getSearchList(Map<String, Object> params) {
        String startDateString = (String) params.get(START_DATE_STRING);
        String endDateString = (String) params.get(END_DATE_STRING);
        String custId = (String) params.get(CUST_ID);
        List<GoodsDetailDto> goodsDetailDtos = goodsDetailReportService.getExportGoodsDetail(startDateString, endDateString, custId);
        return goodsDetailDtos;
    }

    /**
     * 导出excel数据
     */
    @Override
    public void exportExcelData(SXSSFWorkbook wb, List<GoodsDetailDto> goodsDetailDtoLists) throws Exception {
        SXSSFSheet st = wb.createSheet("出货明细及政策余额表");
        List<GoodsDetailOfMonth> goodsDetailOfMonths = null;
        if (goodsDetailDtoLists != null) {
            goodsDetailOfMonths = goodsDetailDtoLists.get(0).getGoodsDetailOfMonths();
        } else {
            throw new BusinessException("未获取到每月出货额数据");
        }
        //设置表头合并单元格
        setTitleStyle(st, goodsDetailOfMonths);
        //设置详细信息到单元格
        setDtailToCell(goodsDetailDtoLists, st);
    }


    /**
     * 设置详细信息到单元格
     *
     * @param goodsDetailDtos
     * @param st
     */
    private void setDtailToCell(List<GoodsDetailDto> goodsDetailDtos, SXSSFSheet st) {
        //序号
        int rowNum = 1;
        //行号下标 从2开始，前两行为标头列
        int indexNum = 2;

        for (GoodsDetailDto goodsDetailDto : goodsDetailDtos) {
            //记录每个
            int borderIndex = 1;

            SXSSFRow detailRow = st.createRow(indexNum);
            //给公用的单元格赋值
            setMainField(rowNum, goodsDetailDto, detailRow);


            indexNum += borderIndex;
            rowNum++;
        }
    }


    /**
     * 给公用单元格赋值
     *
     * @param rowNum
     * @param goodsDetailDto
     */
    private void setMainField(int rowNum, GoodsDetailDto goodsDetailDto, SXSSFRow detailRow) {

        //序号
        SXSSFCell cell0 = detailRow.createCell(0);
        cell0.setCellValue(rowNum);
        //客户编码
        SXSSFCell cell1 = detailRow.createCell(1);
        cell1.setCellValue(goodsDetailDto.getCustCode());
        //客户名称
        SXSSFCell cell2 = detailRow.createCell(2);
        cell2.setCellValue(goodsDetailDto.getCustName());
        //区域
        SXSSFCell cell3 = detailRow.createCell(3);
        cell3.setCellValue(goodsDetailDto.getMarketArea());
        //省份
        SXSSFCell cell4 = detailRow.createCell(4);
        cell4.setCellValue(goodsDetailDto.getProvince());
        int column = 4;
        //月份出货额度
        for (GoodsDetailOfMonth goodsDetailOfMonth : goodsDetailDto.getGoodsDetailOfMonths()) {
            //月份出货额度-货款出货
            column++;
            SXSSFCell paymetColumn = detailRow.createCell(column);
            paymetColumn.setCellValue(goodsDetailOfMonth.getPaymentAmount().toString());
            //月份出货额度-零售价出货
            column++;
            SXSSFCell retailAmoutColumn = detailRow.createCell(column);
            retailAmoutColumn.setCellValue(goodsDetailOfMonth.getRetailAmout().toString());
        }
        //出货额合计-货款出货
        column++;
        SXSSFCell cell5 = detailRow.createCell(column);
        cell5.setCellValue(goodsDetailDto.getPaymentAmounTotal().toString());
        //出货额合计-零售价出货
        column++;
        SXSSFCell cell6 = detailRow.createCell(column);
        cell6.setCellValue(goodsDetailDto.getRetailAmoutTotal().toString());
        //当前格项政策余额会中-货款余额
        column++;
        SXSSFCell cell7 = detailRow.createCell(column);
        cell7.setCellValue(goodsDetailDto.getPaymentBalance().toString());
        //当前格项政策余额会中-零售价余额
        column++;
        SXSSFCell cell8 = detailRow.createCell(column);
        cell8.setCellValue(goodsDetailDto.getRetailBalance().toString());
    }

    /**
     * 设置表头合并单元格
     *
     * @param st
     */
    private void setTitleStyle(SXSSFSheet st, List<GoodsDetailOfMonth> goodsDetailOfMonths) {
        String[] commonTitle = {"序号", "客户编码", "客户名称", "区域", "省份"};
        SXSSFRow tableTitleRow = st.createRow(0);
        //设置 共有标题属性
        for (int i = 0; i < commonTitle.length; i++) {
            st.addMergedRegion(new CellRangeAddress(0, 1, i, i));
            SXSSFCell cell = tableTitleRow.createCell(i);
            cell.setCellValue(commonTitle[i]);
        }

        List<String> titles = new ArrayList<>();
        for (int i = 0; i < goodsDetailOfMonths.size(); i++) {
            int firstCol = 4 + i * 2 + 1;
            int lastClo = firstCol + 1;
            st.addMergedRegion(new CellRangeAddress(0, 0, firstCol, lastClo));
            SXSSFCell cell10 = tableTitleRow.createCell(firstCol);
            String date = goodsDetailOfMonths.get(i).getDate();
            String[] yearAndMonth = date.split("-");
            cell10.setCellValue(yearAndMonth[0] + "年" + yearAndMonth[1] + "月");
            titles.add("货款出货");
            titles.add("零售价出货");
        }


        int totalFirstCol = 4 + goodsDetailOfMonths.size() * 2 + 1;
        st.addMergedRegion(new CellRangeAddress(0, 0, totalFirstCol, totalFirstCol + 1));
        SXSSFCell cell26 = tableTitleRow.createCell(totalFirstCol);
        cell26.setCellValue("出货额合计");
        titles.add("货款出货");
        titles.add("零售价出货");
        int balanceFirstCol = 4 + goodsDetailOfMonths.size() * 2 + 2 + 1;
        st.addMergedRegion(new CellRangeAddress(0, 0, balanceFirstCol, balanceFirstCol + 1));
        SXSSFCell cell28 = tableTitleRow.createCell(balanceFirstCol);
        cell28.setCellValue("当前各项政策余额汇总");
        titles.add("货款余额");
        titles.add("零售价余额");
        //设置第二行的标题
        SXSSFRow tableTitleRow2 = st.createRow(1);
        int index = titles.size();
        for (int i = 0; i < index; i++) {
            SXSSFCell cell = tableTitleRow2.createCell(i + 5);
            cell.setCellValue(titles.get(i));
        }
    }

}

