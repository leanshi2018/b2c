package com.framework.loippi.service.impl;

import com.framework.loippi.dao.ShopCommonMessageDao;
import com.framework.loippi.entity.ShopCommonMessage;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.mybatis.paginator.domain.PageList;
import com.framework.loippi.service.ShopCommonMessageService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.Paramap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * SERVICE - ShopCommonMessage()
 *
 * @author zijing
 * @version 2.0
 */
@Service
public class ShopCommonMessageServiceImpl extends GenericServiceImpl<ShopCommonMessage, Long> implements ShopCommonMessageService {

    @Autowired
    private ShopCommonMessageDao shopCommonMessageDao;

    @Autowired
    public void setGenericDao() {
        super.setGenericDao(shopCommonMessageDao);
    }


    @Override
    public Page<ShopCommonMessage> findMessagePage(Pageable pageable) {
        PageList<ShopCommonMessage> shopCommonMessages = shopCommonMessageDao.findMessagePage(pageable.getParameter(), pageable.getPageBounds());
        return new Page<ShopCommonMessage>(shopCommonMessages, shopCommonMessages.getPaginator().getTotalCount(), pageable);
    }

    @Override
    public Long countMessage(Long uid, Integer bizType) {
        return shopCommonMessageDao.countMessage(uid, bizType);
    }

    @Override
    public ShopCommonMessage findLast(Long uid, Integer bizType) {
        Pageable pageable = new Pageable(1, 1);
        pageable.setOrderDirection(Order.Direction.DESC);
        pageable.setOrderProperty("create_time");
        List<ShopCommonMessage> data = findByPage(pageable).getContent();
        return data.size() > 0 ? data.get(0) : null;
    }

    /**
     * 删除推送消息
     * @param ids
     */
    @Override
    public void deleteAllMemberMessage(Long[] ids) {
        shopCommonMessageDao.deleteAllMemberMessage(Paramap.create().put("ids", ids));
    }

}
