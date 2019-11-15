package com.yonyou.occ.report.service.handler;

import com.yonyou.ocm.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 报表导出功能抽象类 供需要处理复杂表样使用
 */
@Slf4j
public abstract class BaseExcelHandler<T> {

    /**
     * 数据导出
     *
     * @param params   查询条件
     * @param response
     * @param fileName 导出文件名
     * @return
     */
    public Map<String, String> exportExcelData(Map<String, Object> params, HttpServletResponse response, String fileName) {
        Map<String, String> result = new HashMap<>(2);
        ByteArrayOutputStream outputStream = null;
        ByteArrayInputStream tempIn = null;
        try {

            //查询需要导出的数据
            log.info("========Excel导出，查询数据 ，开始。========");
            List<T> resultList = getSearchList(params);
            log.info("========Excel导出，查询数据，完成。======== ");

            //写入excel
            //采用这种方式占用内存少，速度快
            SXSSFWorkbook wb = new SXSSFWorkbook(new XSSFWorkbook(), -1);
            exportExcelData(wb, resultList);

            outputStream = new ByteArrayOutputStream();
            wb.write(outputStream);
            byte[] bytes = outputStream.toByteArray();
            tempIn = new ByteArrayInputStream(outputStream.toByteArray());
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8") + ".xlsx");
            response.setContentType("application/vnd.ms-excel;charset=gb2312");
            //解决导出的文件打开报错问题
            response.setHeader("Content-Length", String.valueOf(tempIn.available()));
            byte[] buffer = new byte[1024];
            int a;
            while ((a = tempIn.read(buffer)) != -1) {
                response.getOutputStream().write(buffer, 0, a);
            }

        } catch (Exception e) {
            log.error("数据导出异常：{%s}", e.getMessage());
            throw new BusinessException("数据导出异常" + e.getMessage());
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                    if (tempIn != null) {
                        tempIn.close();
                    }
                } catch (IOException e) {
                    log.error("流关闭出错：{%s}", e.getMessage());
                    throw new BusinessException("流关闭异常");
                }
            }
        }
        result.put("status", "success");
        result.put("msg", "Excel导出已完成");

        return result;
    }

    /**
     * 查询结果集   继承类需要重写具体的查询功能 暂不支持分页功能
     *
     * @param params 查询条件
     * @return
     */
    protected abstract List<T> getSearchList(Map<String, Object> params);

    /**
     * 导出excel数据
     * 需要重写具体的导出功能
     */
    protected abstract void exportExcelData(SXSSFWorkbook wb, List<T> dtoList) throws Exception;


}

