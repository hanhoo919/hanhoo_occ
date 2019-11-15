package com.yonyou.occ.report.service;

import com.yonyou.occ.report.service.dto.PettyAccountBalanceDto;

import java.util.List;
import java.util.Map;

public interface PettyAccountBalanceService {
    List<PettyAccountBalanceDto> findAllPettyAccountBalance(Map<String, Object> searchParams);
}
