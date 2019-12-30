package com.framework.loippi.dao.common;

import java.util.List;
import java.util.Map;

import com.framework.loippi.entity.common.ShopHomePicture;
import com.framework.loippi.mybatis.dao.GenericDao;

/**
 * @author :ldq
 * @date:2019/12/27
 * @description:dubbo com.framework.loippi.dao.common
 */
public interface ShopHomePictureDao extends GenericDao<ShopHomePicture, Long> {
	List<ShopHomePicture> findByTypeAndSort(Map<String, Object> map);
}
