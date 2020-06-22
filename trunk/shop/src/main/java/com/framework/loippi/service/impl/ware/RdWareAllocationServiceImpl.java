package com.framework.loippi.service.impl.ware;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.product.ShopGoodsDao;
import com.framework.loippi.dao.product.ShopGoodsSpecDao;
import com.framework.loippi.dao.user.RdGoodsAdjustmentDao;
import com.framework.loippi.dao.ware.RdInventoryWarningDao;
import com.framework.loippi.dao.ware.RdWareAdjustDao;
import com.framework.loippi.dao.ware.RdWareAllocationDao;
import com.framework.loippi.dao.ware.RdWareOrderDao;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsSpec;
import com.framework.loippi.entity.user.RdGoodsAdjustment;
import com.framework.loippi.entity.ware.RdInventoryWarning;
import com.framework.loippi.entity.ware.RdWareAdjust;
import com.framework.loippi.entity.ware.RdWareAllocation;
import com.framework.loippi.entity.ware.RdWareOrder;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.ware.RdWareAllocationService;

/**
 * @author :ldq
 * @date:2020/6/18
 * @description:dubbo com.framework.loippi.service.impl.ware
 */
@Service
public class RdWareAllocationServiceImpl extends GenericServiceImpl<RdWareAllocation, Long> implements RdWareAllocationService {

	@Autowired
	private RdWareAllocationDao rdWareAllocationDao;
	@Autowired
	private RdWareAdjustDao rdWareAdjustDao;
	@Autowired
	private RdWareOrderDao rdWareOrderDao;
	@Autowired
	private RdInventoryWarningDao rdInventoryWarningDao;
	@Autowired
	private ShopGoodsSpecDao shopGoodsSpecDao;
	@Autowired
	private ShopGoodsDao shopGoodsDao;
	@Autowired
	private RdGoodsAdjustmentDao rdGoodsAdjustmentDao;


	@Autowired
	public void setGenericDao() {
		super.setGenericDao(rdWareAllocationDao);
	}

	@Override
	public void addAllocation(RdWareOrder rdWareOrder, RdWareAllocation wareAllocation, Map<Long, Integer> specIdNumMap) throws Exception {
		rdWareOrderDao.insert(rdWareOrder);
		rdWareAllocationDao.insert(wareAllocation);

		//添加出库调整单
		RdWareAdjust wareAdjustOut = new RdWareAdjust();
		wareAdjustOut.setWareCode(wareAllocation.getWareCodeOut());
		wareAdjustOut.setWareName(wareAllocation.getWareNameOut());
		wareAdjustOut.setAdjustType("TOT");
		wareAdjustOut.setAttachAdd(wareAllocation.getAttachAdd());
		wareAdjustOut.setWareAmount(new BigDecimal("0.00"));
		wareAdjustOut.setStatus(3);
		wareAdjustOut.setAutohrizeBy("自提店欠货创建");
		wareAdjustOut.setAutohrizeTime(new Date());
		wareAdjustOut.setAutohrizeDesc("调拨单号"+wareAllocation.getWId()+"预扣商品");
		rdWareAdjustDao.insert(wareAdjustOut);

		//发货商品
		for (Map.Entry<Long, Integer> entry : specIdNumMap.entrySet()) {
			Long specId = entry.getKey();
			Integer num = entry.getValue();

			ShopGoodsSpec goodsSpec = shopGoodsSpecDao.find(specId);
			ShopGoods shopGoods = shopGoodsDao.find(goodsSpec.getGoodsId());
			Map<String, Object> map1 = new HashMap<>();
			map1.put("wareCode",wareAllocation.getWareCodeOut());
			map1.put("specificationId",goodsSpec.getId());
			RdInventoryWarning inventoryWarning = rdInventoryWarningDao.findInventoryWarningByWareAndSpecId(map1);
			Long stockNow = 0l;
			Integer precautiousLine = 0;
			if (inventoryWarning==null){
				throw new NullPointerException("蜗米仓库商品"+goodsSpec.getId()+"数量不足");
			}else {
				if (inventoryWarning.getInventory()==null){
					stockNow = 0l;
				}else {
					stockNow = inventoryWarning.getInventory().longValue();
				}
				if (inventoryWarning.getPrecautiousLine()==null){
					precautiousLine = 0;
				}else {
					precautiousLine = inventoryWarning.getPrecautiousLine();
				}
			}

			//添加商品调整单
			RdGoodsAdjustment adjustment = new RdGoodsAdjustment();
			adjustment.setGoodId(goodsSpec.getGoodsId());
			adjustment.setGoodsName(shopGoods.getGoodsName());
			adjustment.setSpecificationId(goodsSpec.getId());
			adjustment.setSpecName(goodsSpec.getSpecName());
			adjustment.setGoodsSpec(goodsSpec.getSpecGoodsSpec());
			adjustment.setSpecGoodsSerial(goodsSpec.getSpecGoodsSerial());
			adjustment.setStockNow(stockNow);
			adjustment.setStockInto(Long.valueOf(entry.getValue()));
			if(stockNow-Long.valueOf(entry.getValue())<0){
				throw new Exception("蜗米仓库商品库存数量不足");
			}
			adjustment.setCreateTime(shopGoods.getCreateTime());
			if (shopGoods.getShelfLife()==null){
				adjustment.setQualityTime(0l);
				adjustment.setShelfLifeTime(shopGoods.getCreateTime());
			}else {
				adjustment.setQualityTime(shopGoods.getShelfLife().longValue());
				Calendar ca = Calendar.getInstance();//得到一个Calendar的实例
				ca.setTime(new Date()); //设置时间为当前时间
				ca.add(Calendar.DATE, shopGoods.getShelfLife());//保质天数
				Date date = ca.getTime(); //结果
				adjustment.setShelfLifeTime(date);
			}
			adjustment.setPrecautiousLine(precautiousLine);
			adjustment.setWid(wareAllocation.getWId());
			adjustment.setSign(2);
			adjustment.setWareCode(wareAllocation.getWareCodeOut());//出库仓库
			adjustment.setStatus(0l);

			rdGoodsAdjustmentDao.insert(adjustment);

			//添加出库商品
			RdGoodsAdjustment adjustmentOUT = new RdGoodsAdjustment();
			adjustmentOUT.setGoodId(goodsSpec.getGoodsId());
			adjustmentOUT.setGoodsName(shopGoods.getGoodsName());
			adjustmentOUT.setSpecificationId(goodsSpec.getId());
			adjustmentOUT.setSpecName(goodsSpec.getSpecName());
			adjustmentOUT.setGoodsSpec(goodsSpec.getSpecGoodsSpec());
			adjustmentOUT.setSpecGoodsSerial(goodsSpec.getSpecGoodsSerial());
			adjustmentOUT.setStockNow(stockNow);
			adjustmentOUT.setStockInto(Long.valueOf(entry.getValue()));
			/*if(stockNow-Long.valueOf(entry.getValue())<0){
				throw new Exception("出库商品库存数量小于出库数量");
			}*/
			adjustmentOUT.setCreateTime(shopGoods.getCreateTime());
			if (shopGoods.getShelfLife()==null){
				adjustmentOUT.setQualityTime(0l);
				adjustmentOUT.setShelfLifeTime(shopGoods.getCreateTime());
			}else {
				adjustmentOUT.setQualityTime(shopGoods.getShelfLife().longValue());
				Calendar ca = Calendar.getInstance();//得到一个Calendar的实例
				ca.setTime(new Date()); //设置时间为当前时间
				ca.add(Calendar.DATE, shopGoods.getShelfLife());//保质天数
				Date date = ca.getTime(); //结果
				adjustmentOUT.setShelfLifeTime(date);
			}
			adjustmentOUT.setPrecautiousLine(precautiousLine);
			adjustmentOUT.setWid(wareAdjustOut.getWid());
			adjustmentOUT.setSign(1);
			adjustmentOUT.setWareCode(wareAllocation.getWareCodeOut());
			adjustmentOUT.setAutohrizeTime(new Date());
			adjustmentOUT.setStatus(1l);
			rdGoodsAdjustmentDao.insert(adjustmentOUT);

			//调整仓库
			//出库库存
			/*Map<String,Object> inMap = new HashMap<>();
			inMap.put("wareCode",wareAllocation.getWareCodeOut());//出库仓库
			inMap.put("specificationId",goodsSpec.getId());
			RdInventoryWarning inventory = rdInventoryWarningDao.findByGIAndWare(map1);*/
			if (inventoryWarning==null){
				throw new NullPointerException("出库商品"+goodsSpec.getId()+"为空");
			}else{
				//int inventory1 = inventoryWarning.getInventory()-Integer.valueOf(entry.getValue());
				Map<String,Object> inMap1 = new HashMap<>();
				inMap1.put("wareCode",wareAllocation.getWareCodeOut());
				inMap1.put("specificationId",goodsSpec.getId());
				inMap1.put("inventory",num);
				rdInventoryWarningDao.updateInventoryByWareCodeAndSpecId(inMap1);
			}


		}


	}
}
