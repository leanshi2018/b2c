package com.framework.loippi.service.impl.common;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.framework.loippi.dao.common.ShopHomePictureDao;
import com.framework.loippi.entity.common.ShopHomePicture;
import com.framework.loippi.service.common.ShopHomePictureService;
import com.framework.loippi.service.impl.GenericServiceImpl;

/**
 * @author :ldq
 * @date:2019/12/27
 * @description:dubbo com.framework.loippi.service.impl.common
 */
@Service
@Slf4j
public class ShopHomePictureServiceImpl extends GenericServiceImpl<ShopHomePicture, Long> implements ShopHomePictureService {

	@Resource
	private ShopHomePictureDao shopHomePictureDao;

	@Override
	public List<ShopHomePicture> findByTypeAndSort(Integer pictureType, Integer pSort) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("pictureType",pictureType);
		map.put("pSort",pSort);
		return shopHomePictureDao.findByTypeAndSort(map);
	}

	@Override
	public List<ShopHomePicture> findListByTypeAndStutus(int pictureType, int auditStatus) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("pictureType",pictureType);
		map.put("auditStatus",auditStatus);
		return shopHomePictureDao.findListByTypeAndStutus(map);
	}
}
