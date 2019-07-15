package com.framework.loippi.service.impl.common;

import com.framework.loippi.entity.common.ShopCommonSpecValue;
import com.framework.loippi.service.impl.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.common.ShopCommonSpecValueDao;
import com.framework.loippi.service.common.ShopCommonSpecValueService;

/**
 * SERVICE - ShopCommonSpecValue(商品规格值表)
 *
 * @author zijing
 * @version 2.0
 */
@Service
public class ShopCommonSpecValueServiceImpl extends GenericServiceImpl<ShopCommonSpecValue, Long> implements ShopCommonSpecValueService {

    @Autowired
    private ShopCommonSpecValueDao shopCommonSpecValueDao;


    @Autowired
    public void setGenericDao() {
        super.setGenericDao(shopCommonSpecValueDao);
    }

}
