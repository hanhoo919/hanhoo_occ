package com.yonyou.occ.report.service.handler;

import com.yonyou.occ.report.service.dto.KASalesOrderDto;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.util.List;
import java.util.Map;

public class KASalesOrderHandler extends BaseExcelHandler<KASalesOrderDto> {
    @Override
    protected List<KASalesOrderDto> getSearchList(Map<String, Object> params) {
        return null;
    }

    @Override
    protected void exportExcelData(SXSSFWorkbook wb, List<KASalesOrderDto> dtoList) throws Exception {

    }
}
