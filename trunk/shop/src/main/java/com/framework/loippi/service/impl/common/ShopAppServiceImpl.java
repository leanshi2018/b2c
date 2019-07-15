package com.framework.loippi.service.impl.common;

import com.framework.loippi.dao.common.ShopAppDao;
import com.framework.loippi.entity.common.ShopApp;
import com.framework.loippi.service.common.ShopAppService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * SERVICE -App
 *
 * @author Loippi Team
 * @version 1.0
 */
@Service
public class ShopAppServiceImpl extends GenericServiceImpl<ShopApp, Long> implements ShopAppService {

    @Autowired
    private ShopAppDao shopAppDao;

    @Autowired
    public void setGenericDao() {
        super.setGenericDao(shopAppDao);
    }
}
