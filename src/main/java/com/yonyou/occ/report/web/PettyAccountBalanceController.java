package com.yonyou.occ.report.web;

import com.yonyou.occ.report.service.PettyAccountBalanceService;
import com.yonyou.occ.report.service.dto.PettyAccountBalanceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 小额账余报表
 * @author lsl
 */
@RestController
@RequestMapping("/report/pettyCashBalance")
public class PettyAccountBalanceController extends BaseController {

    @Autowired
    private PettyAccountBalanceService pettyAccountBalanceService;

    @GetMapping("/findAllPettyAccountBalanceForMiddleground")
    public List<PettyAccountBalanceDto> getPettyAccountBalance(HttpServletRequest request){
        Map<String, Object> searchParams = buildSearchParams(request);
        List<PettyAccountBalanceDto> pettyAccountBalanceDtos = pettyAccountBalanceService.findAllPettyAccountBalance(searchParams);
        return pettyAccountBalanceDtos;
    }
}
