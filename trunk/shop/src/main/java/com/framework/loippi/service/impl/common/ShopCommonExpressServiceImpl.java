package com.framework.loippi.service.impl.common;

import com.framework.loippi.dao.common.ShopCommonExpressDao;
import com.framework.loippi.entity.common.ShopCommonExpress;
import com.framework.loippi.service.common.ShopCommonExpressService;
import com.framework.loippi.service.impl.GenericServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * SERVICE - ShopCommonExpress(快递公司)
 *
 * @author zijing
 * @version 2.0
 */
@Service
public class ShopCommonExpressServiceImpl extends GenericServiceImpl<ShopCommonExpress, Long>
        implements ShopCommonExpressService {

    @Autowired
    private ShopCommonExpressDao shopCommonExpressDao;

    @Autowired
    public void setGenericDao() {
        super.setGenericDao(shopCommonExpressDao);
    }

    @Override
    public ShopCommonExpress findByOrderId(Long orderId) {
        return shopCommonExpressDao.findByOrderId(orderId);
    }
}
