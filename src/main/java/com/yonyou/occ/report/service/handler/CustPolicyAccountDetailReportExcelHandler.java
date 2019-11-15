package com.yonyou.occ.report.service.handler;

import com.yonyou.occ.report.service.ICustPolicyAccountDetailReportService;
import com.yonyou.occ.report.service.dto.CustPolicyAccountDetailDto;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class CustPolicyAccountDetailReportExcelHandler extends BaseExcelHandler<CustPolicyAccountDetailDto> {

    @Autowired
    private ICustPolicyAccountDetailReportService custPolicyAccountDetailReportService;

    @Override
    protected List<CustPolicyAccountDetailDto> getSearchList(Map<String, Object> searchParams) {
        String customerCode = (String) searchParams.get("customerCode");
        String customerName = (String) searchParams.get("customerName");
        String activityCode = (String) searchParams.get("activityCode");
        String activityName = (String) searchParams.get("activityName");
        String beginDate = (String) searchParams.get("beginDate");
        String endDate = (String) searchParams.get("endDate");

        List<CustPolicyAccountDetailDto> list = custPolicyAccountDetailReportService.getCustPolicyAccountDetails(customerCode, customerName, activityCode, activityName, beginDate, endDate);
        return list;
    }

    @Override
    protected void exportExcelData(SXSSFWorkbook wb, List<CustPolicyAccountDetailDto> dtoList) throws Exception {
        SXSSFSheet st = wb.createSheet("客户政策账余明细表");
        //获取表格标题样式对象
        CellStyle headStyle = wb.createCellStyle();
        //设置表头合并单元格
        setTitleStyle(st, headStyle);
        //设置详细信息到单元格
        setDetailToCell(dtoList, st);
    }

    /**
     * @param st
     * @param headStyle
     */
    private void setTitleStyle(SXSSFSheet st, CellStyle headStyle) {
        //设置内容居中
        headStyle.setAlignment(HorizontalAlignment.CENTER);
        String[] commonTitle = {"序号", "渠道", "区域", "代理商编号", "代理商名称", "活动有效期", "活动编码", "活动名称", "控制类型1", "控制类型2", "期初余额", "本期款项分解", "本期发货", "期末余额", "查询截点余额"};
        SXSSFRow tableTitleRow = st.createRow(0);
        //设置 共有标题属性
        for (int i = 0; i < commonTitle.length; i++) {
//            st.addMergedRegion(new CellRangeAddress(0, 1, i, i));
            SXSSFCell cell = tableTitleRow.createCell(i);
            cell.setCellValue(commonTitle[i]);
            cell.setCellStyle(headStyle);
        }
    }

    /**
     * 将数据集设置到sheet中
     *
     * @param dtoList
     * @param st
     */
    private void setDetailToCell(List<CustPolicyAccountDetailDto> dtoList, SXSSFSheet st) {
        // 序号
        int rowNum = 1;
        // 行号下标 从2开始，前两行为标头列
        int indexNum = 1;
        for (CustPolicyAccountDetailDto custPolicyAccountDetailDto : dtoList) {
            // 记录每个
            int borderIndex = 1;
            SXSSFRow detailRow = st.createRow(indexNum);
            // 给公用的单元格赋值
            setMainField(rowNum, custPolicyAccountDetailDto, detailRow);
            indexNum += borderIndex;
            rowNum++;
        }
    }

    /**
     * 给每行cell设值
     *
     * @param rowNum
     * @param custPolicyAccountDetailDto
     * @param detailRow
     */
    private void setMainField(int rowNum, CustPolicyAccountDetailDto custPolicyAccountDetailDto, SXSSFRow detailRow) {
        // 序号
        SXSSFCell cell0 = detailRow.createCell(0);
        cell0.setCellValue(rowNum);
        // 渠道
        SXSSFCell cell1 = detailRow.createCell(1);
        cell1.setCellValue(custPolicyAccountDetailDto.getCustomerCategoryName());
        // 区域
        SXSSFCell cell2 = detailRow.createCell(2);
        cell2.setCellValue(custPolicyAccountDetailDto.getMarketAreaName());
        // 代理商编号
        SXSSFCell cell3 = detailRow.createCell(3);
        cell3.setCellValue(custPolicyAccountDetailDto.getCustomerCode());
        // 代理商名称
        SXSSFCell cell4 = detailRow.createCell(4);
        cell4.setCellValue(custPolicyAccountDetailDto.getCustomerName());
        // 活动有效期
        SXSSFCell cell5 = detailRow.createCell(5);
        cell5.setCellValue(custPolicyAccountDetailDto.getActivityValidPeriod());
        // 活动编码
        SXSSFCell cell6 = detailRow.createCell(6);
        cell6.setCellValue(custPolicyAccountDetailDto.getActivityCode());
        // 活动名称
        SXSSFCell cell7 = detailRow.createCell(7);
        cell7.setCellValue(custPolicyAccountDetailDto.getActivityName());
        // 控制类型1
        SXSSFCell cell8 = detailRow.createCell(8);
        cell8.setCellValue(custPolicyAccountDetailDto.getControlType1());
        // 控制类型2
        SXSSFCell cell9 = detailRow.createCell(9);
        cell9.setCellValue(custPolicyAccountDetailDto.getControlType2());
        // 期初余额
        SXSSFCell cell10 = detailRow.createCell(10);
        cell10.setCellValue(custPolicyAccountDetailDto.getBeginningBalance().toString());
        // 本期款项分解
        SXSSFCell cell11 = detailRow.createCell(11);
        cell11.setCellValue(custPolicyAccountDetailDto.getCurrentPeriodMoney().toString());
        // 本期发货
        SXSSFCell cell12 = detailRow.createCell(12);
        cell12.setCellValue(custPolicyAccountDetailDto.getCurrentPeriodGoods().toString());
        // 期末余额
        SXSSFCell cell13 = detailRow.createCell(13);
        cell13.setCellValue(custPolicyAccountDetailDto.getEndingBalance().toString());
        // 查询截点余额
        SXSSFCell cell14 = detailRow.createCell(14);
        cell14.setCellValue(custPolicyAccountDetailDto.getCurrentSumBalance().toString());
    }
}
