package com.framework.loippi.service.impl.ware;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.product.ShopGoodsDao;
import com.framework.loippi.dao.product.ShopGoodsGoodsDao;
import com.framework.loippi.dao.product.ShopGoodsSpecDao;
import com.framework.loippi.dao.ware.RdInventoryWarningDao;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsGoods;
import com.framework.loippi.entity.product.ShopGoodsSpec;
import com.framework.loippi.entity.ware.RdInventoryWarning;
import com.framework.loippi.pojo.selfMention.GoodsType;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.ware.RdInventoryWarningService;
import com.framework.loippi.utils.JacksonUtil;


/**
 * SERVICE - RdInventoryWarning(仓库库存表)
 * 
 * @author zijing
 * @version 2.0
 */
@Service
public class RdInventoryWarningServiceImpl extends GenericServiceImpl<RdInventoryWarning, Long> implements RdInventoryWarningService {
	
	@Autowired
	private RdInventoryWarningDao rdInventoryWarningDao;
	@Autowired
	private ShopGoodsDao shopGoodsDao;
	@Autowired
	private ShopGoodsSpecDao shopGoodsSpecDao;
	@Autowired
	private ShopGoodsGoodsDao shopGoodsGoodsDao;

	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(rdInventoryWarningDao);
	}

	@Override
	public void updateInventoryByWareCodeAndSpecId(String wareCode, Long goodsSpecId, Integer quantity) {
		Map<String,Object> map = new HashMap<>();
		map.put("wareCode",wareCode);
		map.put("specificationId",goodsSpecId);
		map.put("inventory",quantity);
		rdInventoryWarningDao.updateInventoryByWareCodeAndSpecId(map);
	}

	@Override
	public Integer findProductInventory(String wareCode, Long speId) {
		List<RdInventoryWarning> inventoryWarnings = rdInventoryWarningDao.findByWareCode(wareCode);
		Map<Long,Integer> inventoryMap = new HashMap<>();
		for (RdInventoryWarning inventoryWarning : inventoryWarnings) {
			Long goodsCode = Long.valueOf(inventoryWarning.getGoodsCode());//商品id
			ShopGoods goods = shopGoodsDao.find(goodsCode);
			if (goods.getGoodsType()==3){//组合商品
				ShopGoodsSpec spec = shopGoodsSpecDao.find(inventoryWarning.getSpecificationId());
				Map<String, String> specMap = JacksonUtil.readJsonToMap(spec.getSpecGoodsSpec());
				Map<String, String> goodsMap = JacksonUtil.readJsonToMap(spec.getSpecName());
				Set<String> keySpec = specMap.keySet();
				Set<String> keyGoods = goodsMap.keySet();
				Iterator<String> itSpec = keySpec.iterator();
				Iterator<String> itGoods = keyGoods.iterator();
				while (itSpec.hasNext() && itGoods.hasNext()) {
					Long specId = Long.valueOf(itSpec.next());//单品的规格id
					Long goodsId = Long.valueOf(itGoods.next());//单品的商品id
					//拿到组合的具体数量情况
					int joinNum=1;
					Map<String,Object> mapGGs= new HashMap<>();
					mapGGs.put("goodId",inventoryWarning.getGoodsCode());
					mapGGs.put("combineGoodsId",goodsId);
					ShopGoodsGoods shopGoodsGoodsList = shopGoodsGoodsDao.findGoodsGoods(mapGGs);
					if (shopGoodsGoodsList!=null){
						joinNum= Optional.ofNullable(shopGoodsGoodsList.getJoinNum()).orElse(1);
					}

					Integer inventory = inventoryWarning.getInventory()*joinNum;

					if (inventoryMap.containsKey(specId)){//存在
						Integer num = inventoryMap.get(specId);
						Integer total = num + inventory;
						inventoryMap.put(specId,total);
					}else {//不存在
						inventoryMap.put(specId,inventory);
					}

				}
			}else {//普通商品和换购商品(单品)
				if (inventoryMap.containsKey(inventoryWarning.getSpecificationId())){//存在
					Integer num = inventoryMap.get(inventoryWarning.getSpecificationId());
					inventoryMap.put(inventoryWarning.getSpecificationId(),num+inventoryWarning.getInventory());
				}else {//不存在
					inventoryMap.put(inventoryWarning.getSpecificationId(),inventoryWarning.getInventory());
				}
			}
		}

		if (inventoryMap.containsKey(speId)){//存在
			return inventoryMap.get(speId);
		}else {
			return 0;//不存在
		}
	}

	@Override
	public List<RdInventoryWarning> findByWareCodeAndOweInven(String wareCode) {
		return rdInventoryWarningDao.findByWareCodeAndOweInven(wareCode);
	}

	@Override
	public List<GoodsType> findGoodsTypeByWareCode(String wareCode) {
		return rdInventoryWarningDao.findGoodsTypeByWareCode(wareCode);
	}

	@Override
	public List<RdInventoryWarning> findByWareCode(String wareCode) {
		return rdInventoryWarningDao.findByWareCode(wareCode);
	}

	@Override
	public RdInventoryWarning findInventoryWarningByWareAndSpecId(String wareCode, Long specId) {
		Map<String, Object> map = new HashMap<>();
		map.put("wareCode",wareCode);
		map.put("specificationId",specId);
		return rdInventoryWarningDao.findInventoryWarningByWareAndSpecId(map);
	}
}
