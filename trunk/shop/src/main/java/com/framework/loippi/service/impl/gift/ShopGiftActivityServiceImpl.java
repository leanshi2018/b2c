package com.framework.loippi.service.impl.gift;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.framework.loippi.dao.gift.ShopGiftActivityDao;
import com.framework.loippi.dao.gift.ShopGiftGoodsDao;
import com.framework.loippi.dao.product.ShopGoodsDao;
import com.framework.loippi.dao.product.ShopGoodsSpecDao;
import com.framework.loippi.entity.gift.ShopGiftActivity;
import com.framework.loippi.entity.gift.ShopGiftGoods;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsSpec;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.gift.ShopGiftActivityService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.utils.GoodsUtils;

/**
 * @author :ldq
 * @date:2020/12/1
 * @description:dubbo com.framework.loippi.service.impl.gift
 */
@Service
@Transactional
public class ShopGiftActivityServiceImpl extends GenericServiceImpl<ShopGiftActivity, Long> implements ShopGiftActivityService {

	@Resource
	private ShopGiftActivityDao shopGiftActivityDao;
	@Resource
	private ShopGiftGoodsDao shopGiftGoodsDao;
	@Resource
	private TwiterIdService twiterIdService;
	@Resource
	private ShopGoodsSpecDao shopGoodsSpecDao;
	@Resource
	private ShopGoodsDao shopGoodsDao;

	@Autowired
	public void setGenericDao() {
		super.setGenericDao(shopGiftActivityDao);
	}

	@Override
	public List<ShopGiftActivity> findByState(Integer eState) {
		return shopGiftActivityDao.findByState(eState);
	}

	@Override
	public Map<String, String> saveOrEditGift(ShopGiftActivity shopGiftActivity, Long id, String username) {
		Map<String, String> resultMap = new HashMap<>();
		resultMap.put("code", "0");
		String errorMsg = "";
		resultMap.put("msg", errorMsg);

		if(shopGiftActivity.getId()==null){//新建
			shopGiftActivity.setId(twiterIdService.getTwiterId());
			shopGiftActivity.setGiftNum(1);
			if (shopGiftActivity.getEState()==0){
				//下架其他赠品活动
				shopGiftActivityDao.updateByEState(1);
			}
			shopGiftActivity.setCreationBy(username);
			shopGiftActivity.setCreationTime(new Date());
			Long flag = shopGiftActivityDao.insert(shopGiftActivity);

			List<Long> specIdList1 = shopGiftActivity.getSpecIdList1();
			insetGiftGoods(specIdList1,shopGiftActivity.getId(),1);

			if (shopGiftActivity.getSpecIdList2()!=null){
				List<Long> specIdList2 = shopGiftActivity.getSpecIdList2();
				insetGiftGoods(specIdList2,shopGiftActivity.getId(),2);
			}
			if (shopGiftActivity.getSpecIdList3()!=null){
				List<Long> specIdList3 = shopGiftActivity.getSpecIdList3();
				insetGiftGoods(specIdList3,shopGiftActivity.getId(),3);
			}

			if(flag==1){
				resultMap.put("code", "1");
				return resultMap;
			}
		}else {//编辑
			if (shopGiftActivity.getEState()==0){
				//下架其他赠品活动
				shopGiftActivityDao.updateByEState(1);
			}
			shopGiftActivity.setUpdateBy(username);
			shopGiftActivity.setUpdateTime(new Date());
			Long falg = shopGiftActivityDao.update(shopGiftActivity);

			//删除以前的商品
			shopGiftGoodsDao.deleteByGiftId(shopGiftActivity.getId());

			List<Long> specIdList1 = shopGiftActivity.getSpecIdList1();
			insetGiftGoods(specIdList1,shopGiftActivity.getId(),1);

			if (shopGiftActivity.getSpecIdList2()!=null){
				List<Long> specIdList2 = shopGiftActivity.getSpecIdList2();
				insetGiftGoods(specIdList2,shopGiftActivity.getId(),2);
			}
			if (shopGiftActivity.getSpecIdList3()!=null){
				List<Long> specIdList3 = shopGiftActivity.getSpecIdList3();
				insetGiftGoods(specIdList3,shopGiftActivity.getId(),3);
			}
			if(falg==1){
				resultMap.put("code", "1");
				return resultMap;
			}
		}
		return resultMap;
	}

	public void insetGiftGoods(List<Long> specIdList1,Long giftId,Integer flag){
		for (Long specId : specIdList1) {
			ShopGoodsSpec spec = shopGoodsSpecDao.find(specId);
			ShopGoods shopGoods = shopGoodsDao.find(spec.getGoodsId());
			ShopGiftGoods giftGoods = new ShopGiftGoods();
			giftGoods.setId(twiterIdService.getTwiterId());
			giftGoods.setGiftId(giftId);
			giftGoods.setGoodsName(shopGoods.getGoodsName());
			giftGoods.setSpecId(specId);
			GoodsUtils.getSepcMapAndColImgToGoodsSpec(shopGoods, spec);
				String specInfo = "";
				Map<String, String> map = spec.getSepcMap();
				//遍历规格map,取出键值对,拼接specInfo
				if (map != null) {
					Set<String> set = map.keySet();
					for (String str : set) {
						specInfo += str + ":" + map.get(str) + "、";
					}
					if(specInfo.length()==0){
						specInfo = spec.getSpecGoodsSerial();
					}else{
						specInfo = specInfo.substring(0, specInfo.length() - 1);
					}
				}
			giftGoods.setSpecInfo(specInfo);
			giftGoods.setWRule(flag);
			shopGiftGoodsDao.insert(giftGoods);
		}
	}

	@Override
	public ShopGiftActivity findById(Long id) {
		ShopGiftActivity activity = shopGiftActivityDao.find(id);
		Map<String,Object> map = new HashMap<>();
		map.put("giftId",activity.getId());
		map.put("wRule",1);
		activity.setGiftGoodsList1(shopGiftGoodsDao.findByGiftIdAndWRule(map));
		map.put("wRule",2);
		activity.setGiftGoodsList2(shopGiftGoodsDao.findByGiftIdAndWRule(map));
		map.put("wRule",3);
		activity.setGiftGoodsList3(shopGiftGoodsDao.findByGiftIdAndWRule(map));
		return activity;
	}

	@Override
	public void updateByEState(Integer eState) {
		shopGiftActivityDao.updateByEState(eState);
	}
}
