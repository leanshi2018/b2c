package com.framework.loippi.service;

import com.framework.loippi.entity.ShopCommonMessage;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;

/**
 * SERVICE - ShopCommonMessage()
 *
 * @author zijing
 * @version 2.0
 */
public interface ShopCommonMessageService extends GenericService <ShopCommonMessage, Long> {

    Page <ShopCommonMessage> findMessagePage(Pageable pageable);

    Long countMessage(Long uid, Integer bizType);

    ShopCommonMessage findLast(Long uid, Integer bizType);

    /**
     * 删除推送消息
     * @param ids
     */
    void deleteAllMemberMessage(Long[] ids);
}
