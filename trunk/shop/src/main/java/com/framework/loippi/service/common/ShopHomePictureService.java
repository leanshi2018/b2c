package com.framework.loippi.service.common;

import java.util.List;

import com.framework.loippi.entity.common.ShopHomePicture;
import com.framework.loippi.service.GenericService;

/**
 * @author :ldq
 * @date:2019/12/27
 * @description:dubbo com.framework.loippi.service.common
 */
public interface ShopHomePictureService extends GenericService<ShopHomePicture, Long> {

	List<ShopHomePicture> findByTypeAndSort(Integer pictureType, Integer pSort);
}
