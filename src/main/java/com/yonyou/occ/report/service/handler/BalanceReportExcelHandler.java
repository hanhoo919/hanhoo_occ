package com.yonyou.occ.report.service.handler;

import com.yonyou.occ.report.service.dto.BalanceReportDto;
import com.yonyou.occ.report.service.dto.BalanceReportOptionItemDto;
import com.yonyou.occ.report.service.dto.BalanceReportRequireItemDto;
import com.yonyou.ocm.common.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 代理商账余报表 导出功能 处理类
 */
@Slf4j
@Component
public class BalanceReportExcelHandler extends BaseExcelHandler<BalanceReportDto> {

    @Autowired
    private BalanceReportSearchHandler balanceReportSearchHandler;


    @Override
    protected List<BalanceReportDto> getSearchList(Map<String, Object> params) {
        return balanceReportSearchHandler.doSearch(params);
    }

    /**
     * 导出excel数据
     */
    @Override
    protected void exportExcelData(SXSSFWorkbook wb, List<BalanceReportDto> balanceReportDtoList) {
        SXSSFSheet st = wb.createSheet("代理商账余报表");
        //获取表格标题样式对象

        CellStyle headStyle = wb.createCellStyle();
        //设置表头合并单元格
        setTitleStyle(st, headStyle);
        //设置详细信息到单元格
        setDtailToCell(balanceReportDtoList, st);
    }


    /**
     * 设置详细信息到单元格
     *
     * @param balanceReportDtoList
     * @param st
     */
    private void setDtailToCell(List<BalanceReportDto> balanceReportDtoList, SXSSFSheet st) {
        //序号
        int rowNum = 1;
        //行号下标 从2开始，前两行为标头列
        int indexNum = 2;

        for (BalanceReportDto balanceReportDto : balanceReportDtoList) {
            //记录每个
            int borderIndex = 1;
            if (balanceReportDto.getBalanceReportOptionDto().getOptionItemDtoList().size() > balanceReportDto.getBalanceReportRequireDto().getRequireItemDtoList().size()) {
                borderIndex = balanceReportDto.getBalanceReportOptionDto().getOptionItemDtoList().size();
            } else {
                borderIndex = balanceReportDto.getBalanceReportRequireDto().getRequireItemDtoList().size();
            }
            //合并单元格
            mergeCell(st, indexNum, borderIndex);

            SXSSFRow detailRow = st.createRow(indexNum);
            //给公用的单元格赋值
            setMainField(rowNum, balanceReportDto, detailRow);

            //将第0行必选信息和可选信息输入单元格
            if (balanceReportDto.getBalanceReportRequireDto().getRequireItemDtoList().size() > 0) {
                setRequireItemToCell(balanceReportDto.getBalanceReportRequireDto().getRequireItemDtoList().get(0), detailRow);
            }
            if (balanceReportDto.getBalanceReportOptionDto().getOptionItemDtoList().size() > 0) {
                setOptionItemToCell(balanceReportDto.getBalanceReportOptionDto().getOptionItemDtoList().get(0), detailRow);
            }

            //循环将必选信息和可选信息输入单元格，从第1行开始，
            for (int i = 1; i < borderIndex; i++) {
                SXSSFRow row = st.createRow((indexNum + i));
                if (i < balanceReportDto.getBalanceReportRequireDto().getRequireItemDtoList().size()) {
                    setRequireItemToCell(balanceReportDto.getBalanceReportRequireDto().getRequireItemDtoList().get(i), row);

                }
                if (i < balanceReportDto.getBalanceReportOptionDto().getOptionItemDtoList().size()) {
                    setOptionItemToCell(balanceReportDto.getBalanceReportOptionDto().getOptionItemDtoList().get(i), row);

                }
            }

            indexNum = indexNum + borderIndex;
            rowNum++;
        }
    }

    /**
     * 将可选信息录入对应单元格
     *
     * @param optionItemDto
     * @param row
     */
    private void setOptionItemToCell(BalanceReportOptionItemDto optionItemDto, SXSSFRow row) {
        //产品组编码
        SXSSFCell cell29 = row.createCell(29);
        cell29.setCellValue(optionItemDto.getProCode());
        //产品组描述
        SXSSFCell cell30 = row.createCell(30);
        cell30.setCellValue(optionItemDto.getProName());
        //控制类型（数量/金额）
        SXSSFCell cell31 = row.createCell(31);
        cell31.setCellValue(optionItemDto.getControlType());
        //特价未清数量/金额
        SXSSFCell cell32 = row.createCell(32);
        cell32.setCellValue(optionItemDto.getUnUsed().toPlainString());
    }

    /**
     * 将必选信息录入对应单元格
     *
     * @param requireItemDto
     * @param row
     */
    private void setRequireItemToCell(BalanceReportRequireItemDto requireItemDto, SXSSFRow row) {
        //产品组编码
        SXSSFCell cell15 = row.createCell(15);
        cell15.setCellValue(requireItemDto.getProCode());
        //产品组描述
        SXSSFCell cell16 = row.createCell(16);
        cell16.setCellValue(requireItemDto.getProName());
        //项目类型
        SXSSFCell cell17 = row.createCell(17);
        cell17.setCellValue(requireItemDto.getProType());
        //控制类型
        SXSSFCell cell18 = row.createCell(18);
        cell18.setCellValue(requireItemDto.getControlType());
        //单组数量/金额
        SXSSFCell cell19 = row.createCell(19);
        cell19.setCellValue(requireItemDto.getUnit().toPlainString());
        //回款组数
        SXSSFCell cell20 = row.createCell(20);
        cell20.setCellValue(requireItemDto.getReturnGroup().toPlainString());
        //行折扣
        SXSSFCell cell21 = row.createCell(21);
        cell21.setCellValue(requireItemDto.getDiscount().toPlainString());
        //合计
        SXSSFCell cell22 = row.createCell(22);
        cell22.setCellValue(requireItemDto.getTotal().toPlainString());
        //已发
        SXSSFCell cell23 = row.createCell(23);
        cell23.setCellValue(requireItemDto.getStockOut().toPlainString());
        //未清代理商总计(回款)
        SXSSFCell cell24 = row.createCell(24);
        cell24.setCellValue(requireItemDto.getTotalUnusedAmount().toPlainString());
        //未清
        SXSSFCell cell25 = row.createCell(25);
        cell25.setCellValue(requireItemDto.getUnUsedAmount().toPlainString());
    }

    /**
     * 给公用单元格赋值
     *
     * @param rowNum
     * @param balanceReportDto
     */
    private void setMainField(int rowNum, BalanceReportDto balanceReportDto, SXSSFRow detailRow) {

        //序号
        SXSSFCell cell0 = detailRow.createCell(0);
        cell0.setCellValue(rowNum);
        //客户代码
        SXSSFCell cell1 = detailRow.createCell(1);
        cell1.setCellValue(balanceReportDto.getCustCode());
        //客户名称
        SXSSFCell cell2 = detailRow.createCell(2);
        cell2.setCellValue(balanceReportDto.getCustName());
        //区域
        SXSSFCell cell3 = detailRow.createCell(3);
        cell3.setCellValue(balanceReportDto.getMarketAreaName());
        //省份
        SXSSFCell cell4 = detailRow.createCell(4);
        cell4.setCellValue(balanceReportDto.getProvinceName());
        //客户政策编码
        SXSSFCell cell5 = detailRow.createCell(5);
        cell5.setCellValue(balanceReportDto.getActivityCode());
        //客户政策名称
        SXSSFCell cell6 = detailRow.createCell(6);

        cell6.setCellValue(balanceReportDto.getActivityName());
        //政策类型
        SXSSFCell cell7 = detailRow.createCell(7);
        cell7.setCellValue(balanceReportDto.getActivityType());
        //活动开始时间
        SXSSFCell cell8 = detailRow.createCell(8);
        cell8.setCellValue(DateUtil.from_Date_to_String(DateUtil.yy_MM_dd, balanceReportDto.getStartDate()));
        //活动结束时间
        SXSSFCell cell9 = detailRow.createCell(9);
        cell9.setCellValue(DateUtil.from_Date_to_String(DateUtil.yy_MM_dd, balanceReportDto.getEndDate()));

        //单位组回款
        SXSSFCell cell10 = detailRow.createCell(10);
        cell10.setCellValue(balanceReportDto.getBalanceReportRequireDto().getRequireUnitAmount().toPlainString());
        //已回款组数
        SXSSFCell cell11 = detailRow.createCell(11);
        cell11.setCellValue(balanceReportDto.getBalanceReportRequireDto().getRequireBackGroup().toPlainString());
        //活动回款总金额
        SXSSFCell cell12 = detailRow.createCell(12);
        cell12.setCellValue(balanceReportDto.getBalanceReportRequireDto().getRequireTotalAmount().toPlainString());
        //活动已用总金额
        SXSSFCell cell13 = detailRow.createCell(13);
        cell13.setCellValue(balanceReportDto.getBalanceReportRequireDto().getRequireUsedAmount().toPlainString());
        //活动未清总金额
        SXSSFCell cell14 = detailRow.createCell(14);
        cell14.setCellValue(balanceReportDto.getBalanceReportRequireDto().getRequireUnusedAmount().toPlainString());

        //特价回款总金额
        SXSSFCell cell26 = detailRow.createCell(26);
        cell26.setCellValue(balanceReportDto.getBalanceReportOptionDto().getOptionTotalAmount().toPlainString());
        //特价已用总金额
        SXSSFCell cell27 = detailRow.createCell(27);
        cell27.setCellValue(balanceReportDto.getBalanceReportOptionDto().getOptionUsedAmount().toPlainString());
        //特价未清总金额
        SXSSFCell cell28 = detailRow.createCell(28);
        cell28.setCellValue(balanceReportDto.getBalanceReportOptionDto().getOptionUnusedAmount().toPlainString());
    }

    /**
     * 合并单元格
     *
     * @param st
     * @param indexNum
     * @param borderIndex
     */
    private void mergeCell(SXSSFSheet st, int indexNum, int borderIndex) {
        //合并单元格
        int detailIndex = indexNum + borderIndex - 1;
        if (indexNum != detailIndex) {
            for (int i = 0; i < 15; i++) {
                st.addMergedRegion(new CellRangeAddress(indexNum, detailIndex, i, i));
            }
            st.addMergedRegion(new CellRangeAddress(indexNum, detailIndex, 26, 26));
            st.addMergedRegion(new CellRangeAddress(indexNum, detailIndex, 27, 27));
            st.addMergedRegion(new CellRangeAddress(indexNum, detailIndex, 28, 28));
        }
    }

    /**
     * 设置表头合并单元格
     *
     * @param st
     */
    private void setTitleStyle(SXSSFSheet st, CellStyle headStyle) {
        //设置内容居中
        headStyle.setAlignment(HorizontalAlignment.CENTER);

        String[] commonTitle = {"序号", "客户代码", "客户名称", "区域", "省份", "客户政策编码", "客户政策名称", "政策类型", "活动开始时间", "活动结束时间"};
        SXSSFRow tableTitleRow = st.createRow(0);
        //设置 共有标题属性
        for (int i = 0; i < commonTitle.length; i++) {
            st.addMergedRegion(new CellRangeAddress(0, 1, i, i));
            SXSSFCell cell = tableTitleRow.createCell(i);
            cell.setCellValue(commonTitle[i]);
        }

        st.addMergedRegion(new CellRangeAddress(0, 0, 10, 25));
        SXSSFCell cell10 = tableTitleRow.createCell(10);
        cell10.setCellValue("必选部分");
        cell10.setCellStyle(headStyle);

        st.addMergedRegion(new CellRangeAddress(0, 0, 26, 32));
        SXSSFCell cell26 = tableTitleRow.createCell(26);
        cell26.setCellValue("可选部分");
        cell26.setCellStyle(headStyle);

        //设置第二行的标题
        SXSSFRow tableTitleRow2 = st.createRow(1);
        String[] titles = {"单位组回款", "已回款组数", "活动回款总金额", "活动已用总金额", "活动未清总金额", "产品组编码", "产品组描述",
                "项目类型", "控制类型", "单组数量/金额", "回款组数", "行折扣", "合计", "已发", "未清代理商总计(回款)", "未清",
                "特价回款总金额", "特价已用总金额", "特价未清总金额", "产品组编码", "产品组描述", "控制类型（数量/金额）", "特价未清数量/金额"};
        for (int i = 0; i < titles.length; i++) {
            SXSSFCell cell = tableTitleRow2.createCell(i + 10);
            cell.setCellValue(titles[i]);
        }
    }

}

