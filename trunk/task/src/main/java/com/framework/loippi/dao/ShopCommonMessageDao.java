package com.framework.loippi.dao;

import com.framework.loippi.entity.ShopCommonMessage;
import com.framework.loippi.mybatis.dao.GenericDao;
import com.framework.loippi.mybatis.paginator.domain.PageBounds;
import com.framework.loippi.mybatis.paginator.domain.PageList;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * DAO - ShopCommonMessage()
 * 
 * @author zijing
 * @version 2.0
 */
public interface ShopCommonMessageDao extends GenericDao<ShopCommonMessage, Long> {

    PageList<ShopCommonMessage> findMessagePage(Object var1, PageBounds var2);

    Long countMessage(@Param("uid") Long uid, @Param("bizType") Integer bizType);

    /**
     * 删除推送消息
     * @param var1
     * @return
     */
    Long deleteAllMemberMessage(Map<String, Object> var1);

}
