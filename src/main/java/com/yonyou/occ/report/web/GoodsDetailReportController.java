package com.yonyou.occ.report.web;

import com.yonyou.occ.report.service.GoodsDetailReportService;
import com.yonyou.occ.report.service.dto.GoodsDetailDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Generated;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


/**
 * 出货明细及政策余额表controller
 */
@RestController
@RequestMapping("/report/goodsDetailReport")
@Generated(value = "com.yonyou.occ.util.codegenerator.CodeGenerator")
public class GoodsDetailReportController extends BaseController {


    @Autowired
    GoodsDetailReportService goodsDetailReportService;

    /**
     * 中台-获取出货明细及政策余额表
     *
     * @param startDateString
     * @param endDateString
     * @param custId
     * @return
     */
    @GetMapping("/getExportGoodsDetailReport")
    public ResponseEntity<List<GoodsDetailDto>> getExportGoodsDetail(HttpServletRequest request) {
        Map<String, Object> searchParams = buildSearchParams(request);
        String startDateString = (String) searchParams.get("startDateString");
        String endDateString = (String) searchParams.get("endDateString");
        String custId = (String) searchParams.get("custId");
        ResponseEntity<List<GoodsDetailDto>> response = new ResponseEntity<List<GoodsDetailDto>>(
                goodsDetailReportService.getExportGoodsDetail(startDateString, endDateString, custId), HttpStatus.OK);
        return response;
    }

    /**
     * 门户-获取出货明细及政策余额表
     *
     * @param startDateString
     * @param endDateString
     * @return
     */
    @GetMapping("/getExportGoodsDetailReportPortal")
    public ResponseEntity<List<GoodsDetailDto>> getExportGoodsDetailReportPortal(HttpServletRequest request) {
        Map<String, Object> searchParams = buildSearchParams(request);
        String startDateString = (String) searchParams.get("startDateString");
        String endDateString = (String) searchParams.get("endDateString");
        ResponseEntity<List<GoodsDetailDto>> response = new ResponseEntity<List<GoodsDetailDto>>(
                goodsDetailReportService.getExportGoodsDetail(startDateString, endDateString), HttpStatus.OK);
        return response;
    }

    /**
     * 出货明细及政策余额表导出
     *
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/exportExcelData")
    public Map<String, String> exportExcelData(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> searchParams = buildSearchParams(request);
        return goodsDetailReportService.exportExcelData(searchParams, response);
    }


}
