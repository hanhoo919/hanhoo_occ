package com.yonyou.occ.report.service.impl;

import com.yonyou.occ.report.entity.PettyAccountBalanceEntity;
import com.yonyou.occ.report.service.PettyAccountBalanceService;
import com.yonyou.occ.report.service.dto.PettyAccountBalanceDto;
import com.yonyou.occ.report.utils.BeanConverterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 小额账余报表实现类
 * @author lsl
 */
@Service
public class PettyAccountBalanceServiceImpl implements PettyAccountBalanceService {

    @Autowired
    private EntityManager entityManager;

    /**
     * 根据条件查询小额账余数据
     * @param searchParams
     * @return
     */
    @Override
    public List<PettyAccountBalanceDto> findAllPettyAccountBalance(Map<String, Object> searchParams) {
        List<PettyAccountBalanceEntity> pettyAccountBalanceEntities = getEntityList(searchParams);
        if(!StringUtils.isEmpty(pettyAccountBalanceEntities) && pettyAccountBalanceEntities.size() > 0){
            List<PettyAccountBalanceDto> pettyAccountBalanceDtoList = new ArrayList<>();
            for(PettyAccountBalanceEntity entity : pettyAccountBalanceEntities){
                PettyAccountBalanceDto dto = BeanConverterUtils.copyProperties(entity, PettyAccountBalanceDto.class);
                pettyAccountBalanceDtoList.add(dto);
            }
            return pettyAccountBalanceDtoList;
        }
        return null;
    }

    private List<PettyAccountBalanceEntity> getEntityList(Map<String, Object> searchParams) {
        StringBuffer querySQL = createSQL(searchParams);
        Query query = this.entityManager.createNativeQuery(querySQL.toString(), PettyAccountBalanceEntity.class);
        List<PettyAccountBalanceEntity> lists = query.getResultList();
        return lists;
    }

    private StringBuffer createSQL(Map<String, Object> searchParams) {
        StringBuffer querySQL = new StringBuffer("");
        querySQL.append("select * from prom_activity");
        return querySQL;
    }
}
