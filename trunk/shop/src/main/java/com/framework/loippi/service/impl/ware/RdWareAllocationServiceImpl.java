package com.framework.loippi.service.impl.ware;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.framework.loippi.consts.WareOrderState;
import com.framework.loippi.dao.ShopCommonMessageDao;
import com.framework.loippi.dao.ShopMemberMessageDao;
import com.framework.loippi.dao.order.ShopOrderPayDao;
import com.framework.loippi.dao.product.ShopGoodsDao;
import com.framework.loippi.dao.product.ShopGoodsSpecDao;
import com.framework.loippi.dao.user.RdGoodsAdjustmentDao;
import com.framework.loippi.dao.user.RdMmAccountInfoDao;
import com.framework.loippi.dao.user.RdMmAccountLogDao;
import com.framework.loippi.dao.user.RdSysPeriodDao;
import com.framework.loippi.dao.ware.RdInventoryWarningDao;
import com.framework.loippi.dao.ware.RdWareAdjustDao;
import com.framework.loippi.dao.ware.RdWareAllocationDao;
import com.framework.loippi.dao.ware.RdWareOrderDao;
import com.framework.loippi.entity.order.ShopOrderPay;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsSpec;
import com.framework.loippi.entity.user.RdGoodsAdjustment;
import com.framework.loippi.entity.user.RdSysPeriod;
import com.framework.loippi.entity.ware.RdInventoryWarning;
import com.framework.loippi.entity.ware.RdWareAdjust;
import com.framework.loippi.entity.ware.RdWareAllocation;
import com.framework.loippi.entity.ware.RdWareOrder;
import com.framework.loippi.entity.ware.RdWarehouse;
import com.framework.loippi.result.selfMention.SelfOrderSubmitResult;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.ware.RdWareAllocationService;
import com.framework.loippi.utils.Dateutil;
import com.framework.loippi.utils.GoodsUtils;
import com.framework.loippi.vo.store.MentionSubmitGoodsVo;

@Service
@Transactional
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
	private TwiterIdService twiterIdService;
	@Autowired
	private RdSysPeriodDao rdSysPeriodDao;
	@Autowired
	private ShopOrderPayDao orderPayDao;
	@Autowired
	private RdMmAccountLogDao rdMmAccountLogDao;
	@Autowired
	private RdMmAccountInfoDao rdMmAccountInfoDao;
	@Autowired
	private ShopMemberMessageDao shopMemberMessageDao;
	@Autowired
	private ShopCommonMessageDao shopCommonMessageDao;


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

	@Override
	public void addAllocationOwe(RdWareOrder rdWareOrder, RdWareAllocation wareAllocation, List<RdInventoryWarning> inventoryWarningList) throws Exception {

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
		for (RdInventoryWarning inventoryWarningIn : inventoryWarningList) {
			Integer inventory = inventoryWarningIn.getInventory();
			Integer num = Math.abs(inventory);

			ShopGoodsSpec goodsSpec = shopGoodsSpecDao.find(inventoryWarningIn.getSpecificationId());
			ShopGoods shopGoods = shopGoodsDao.find(goodsSpec.getGoodsId());
			Map<String, Object> map1 = new HashMap<>();
			map1.put("wareCode",wareAllocation.getWareCodeOut());
			map1.put("specificationId",goodsSpec.getId());
			RdInventoryWarning inventoryWarningOut = rdInventoryWarningDao.findInventoryWarningByWareAndSpecId(map1);

			Long stockNow = 0l;
			Integer precautiousLine = 0;
			if (inventoryWarningOut==null){
				throw new NullPointerException("蜗米仓库商品"+goodsSpec.getId()+"数量不足");
			}else {
				if (inventoryWarningOut.getInventory()==null){
					stockNow = 0l;
				}else {
					stockNow = inventoryWarningOut.getInventory().longValue();
				}
				if (inventoryWarningOut.getPrecautiousLine()==null){
					precautiousLine = 0;
				}else {
					precautiousLine = inventoryWarningOut.getPrecautiousLine();
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
			adjustment.setStockInto(Long.valueOf(num));
			if(stockNow-Long.valueOf(num)<0){
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
			adjustmentOUT.setStockInto(Long.valueOf(num));
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
			if (inventoryWarningOut==null){
				throw new NullPointerException("出库商品"+goodsSpec.getId()+"为空");
			}else{
				Map<String,Object> inMap1 = new HashMap<>();
				inMap1.put("wareCode",wareAllocation.getWareCodeOut());
				inMap1.put("specificationId",goodsSpec.getId());
				inMap1.put("inventory",num);
				rdInventoryWarningDao.updateInventoryByWareCodeAndSpecId(inMap1);
			}
		}
	}

	@Override
	public void addAllocationOweNew(RdWareOrder rdWareOrder, RdWareAllocation wareAllocation, JSONArray array) throws Exception {

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
		for (Object o : array) {

			Long specId = 0l;
			Integer stockInto = 0;
			JSONObject jsonObject = JSON.parseObject(o.toString());
			for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
				System.out.println(entry.getKey() + ":" + entry.getValue());
				if (entry.getKey().equals("id")) {
					specId = Long.valueOf(entry.getValue().toString());
				}
				if (entry.getKey().equals("inventory")) {
					stockInto = Integer.valueOf(entry.getValue().toString());
				}
			}

			//Integer inventory = stockInto;
			Integer num = Math.abs(stockInto);

			ShopGoodsSpec goodsSpec = shopGoodsSpecDao.find(specId);
			ShopGoods shopGoods = shopGoodsDao.find(goodsSpec.getGoodsId());
			Map<String, Object> map1 = new HashMap<>();
			map1.put("wareCode",wareAllocation.getWareCodeOut());
			map1.put("specificationId",goodsSpec.getId());
			RdInventoryWarning inventoryWarningOut = rdInventoryWarningDao.findInventoryWarningByWareAndSpecId(map1);

			Long stockNow = 0l;
			Integer precautiousLine = 0;
			if (inventoryWarningOut==null){
				throw new NullPointerException("蜗米仓库商品"+goodsSpec.getId()+"数量不足");
			}else {
				if (inventoryWarningOut.getInventory()==null){
					stockNow = 0l;
				}else {
					stockNow = inventoryWarningOut.getInventory().longValue();
				}
				if (inventoryWarningOut.getPrecautiousLine()==null){
					precautiousLine = 0;
				}else {
					precautiousLine = inventoryWarningOut.getPrecautiousLine();
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
			adjustment.setStockInto(Long.valueOf(num));
			if(stockNow-Long.valueOf(num)<0){
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
			adjustmentOUT.setStockInto(Long.valueOf(num));
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
			if (inventoryWarningOut==null){
				throw new NullPointerException("出库商品"+goodsSpec.getId()+"为空");
			}else{
				Map<String,Object> inMap1 = new HashMap<>();
				inMap1.put("wareCode",wareAllocation.getWareCodeOut());
				inMap1.put("specificationId",goodsSpec.getId());
				inMap1.put("inventory",num);
				rdInventoryWarningDao.updateInventoryByWareCodeAndSpecId(inMap1);
			}
		}
	}

	@Override
	public SelfOrderSubmitResult addAllocationOwe1(RdWarehouse warehouseIn, RdWarehouse warehouseOut, JSONArray array) throws Exception {

		//创建一个新的订单支付编号
		String paySn = "W" + Dateutil.getDateString();
		ShopOrderPay orderPay = new ShopOrderPay();
		orderPay.setId(twiterIdService.getTwiterId());
		orderPay.setPaySn(paySn);
		orderPay.setBuyerId(Long.parseLong(warehouseIn.getMmCode()));
		orderPay.setApiPayState("0");//设置支付状态0
		//保存订单支付表
		orderPayDao.insert(orderPay);

		RdWareOrder rdWareOrder = new RdWareOrder();
		rdWareOrder.setId(twiterIdService.getTwiterId());
		String orderSn = "DH"+twiterIdService.getTwiterId();
		rdWareOrder.setOrderSn(orderSn);
		rdWareOrder.setStoreId(warehouseIn.getWareCode());
		rdWareOrder.setStoreName(warehouseIn.getWareName());
		rdWareOrder.setMCode(Optional.ofNullable(warehouseIn.getMmCode()).orElse(""));
		rdWareOrder.setConsigneeName(Optional.ofNullable(warehouseIn.getConsigneeName()).orElse(""));
		rdWareOrder.setWarePhone(Optional.ofNullable(warehouseIn.getWarePhone()).orElse(""));
		rdWareOrder.setOrderType(WareOrderState.ORDER_TYPE_ALLOCATION);//调拨订单
		//rdWareOrder.setOrderState(WareOrderState.ORDER_STATE_NO_PATMENT);//待付款
		RdSysPeriod nowPeriod = rdSysPeriodDao.getPeriodService(new Date());
		if (nowPeriod==null){
			rdWareOrder.setCreationPeriod("");
		}else {
			rdWareOrder.setCreationPeriod(nowPeriod.getPeriodCode());
		}
		rdWareOrder.setCreateTime(new Date());
		rdWareOrder.setOrderDesc("欠货创建");
		rdWareOrder.setProvinceCode(Optional.ofNullable(warehouseIn.getProvinceCode()).orElse(""));
		rdWareOrder.setCityCode(Optional.ofNullable(warehouseIn.getCityCode()).orElse(""));
		rdWareOrder.setCountryCode(Optional.ofNullable(warehouseIn.getCountryCode()).orElse(""));
		rdWareOrder.setWareDetial(Optional.ofNullable(warehouseIn.getWareDetial()).orElse(""));
		rdWareOrder.setPaymentId(0l);
		rdWareOrder.setPaymentName("在线支付");
		rdWareOrder.setPaymentState(WareOrderState.PAYMENT_STATE_NO);
		rdWareOrder.setPayId(orderPay.getId());
		rdWareOrder.setPaySn(paySn);


		//调拨单
		RdWareAllocation wareAllocation = new RdWareAllocation();
		wareAllocation.setWareCodeIn(warehouseIn.getWareCode());
		wareAllocation.setWareNameIn(warehouseIn.getWareName());
		wareAllocation.setWareCodeOut(warehouseOut.getWareCode());
		wareAllocation.setWareNameOut(warehouseOut.getWareName());
		wareAllocation.setAttachAdd("");
		wareAllocation.setStatus(2);
		wareAllocation.setWareOrderSn(orderSn);
		wareAllocation.setAutohrizeBy("");
		wareAllocation.setAutohrizeDesc("");
		wareAllocation.setCreateTime(new Date());

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

		//查找入库仓库为负数的商品
		List<Long> specIdList = new ArrayList<Long>();
		Map<Long,Object> warningMap = new HashMap<Long,Object>();
		List<RdInventoryWarning> inventoryWarningList = rdInventoryWarningDao.findByWareCodeAndOweInven(wareAllocation.getWareCodeIn());
		for (RdInventoryWarning warning : inventoryWarningList) {
			specIdList.add(warning.getSpecificationId());
			warningMap.put(warning.getSpecificationId(),warning);
		}

		BigDecimal orderAmount = BigDecimal.ZERO;
		List<MentionSubmitGoodsVo> goodsVoList = new ArrayList<MentionSubmitGoodsVo>();
		//发货商品
		for (Object o : array) {

			Long specId = 0l;
			Integer stockInto = 0;
			JSONObject jsonObject = JSON.parseObject(o.toString());
			for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
				System.out.println(entry.getKey() + ":" + entry.getValue());
				if (entry.getKey().equals("id")) {
					specId = Long.valueOf(entry.getValue().toString());
				}
				if (entry.getKey().equals("inventory")) {
					stockInto = Integer.valueOf(entry.getValue().toString());
				}
			}
			//Integer inventory = stockInto;
			Integer num = Math.abs(stockInto);//进货数量

			ShopGoodsSpec goodsSpec = shopGoodsSpecDao.find(specId);
			ShopGoods shopGoods = shopGoodsDao.find(goodsSpec.getGoodsId());
			GoodsUtils.getSepcMapAndColImgToGoodsSpec(shopGoods, goodsSpec);

			MentionSubmitGoodsVo goodsVo = new MentionSubmitGoodsVo();
			goodsVo.setGoodsName(shopGoods.getGoodsName());
			goodsVo.setGoodsImage(shopGoods.getGoodsImage());
			goodsVo.setSpecId(goodsSpec.getId());
			if (shopGoods.getGoodsType()==3){
				goodsVo.setSpecGoodsSpec(goodsSpec.getSpecGoodsSerial());
			}else{
				String specInfo = "";
				Map<String, String> map = goodsSpec.getSepcMap();
				//遍历规格map,取出键值对,拼接specInfo
				if (map != null) {
					Set<String> set = map.keySet();
					for (String str : set) {
						specInfo += str + ":" + map.get(str) + "、";
					}
					specInfo = specInfo.substring(0, specInfo.length() - 1);
				}
				goodsVo.setSpecGoodsSpec(specInfo);
			}
			goodsVo.setPpv(shopGoods.getPpv());
			goodsVo.setCostPrice(shopGoods.getCostPrice());
			goodsVo.setComeInventory(num);

			if (specIdList.contains(specId)){
				//是欠货商品
				RdInventoryWarning rdInventoryWarning = (RdInventoryWarning)warningMap.get(specId);
				Integer oweInventory = rdInventoryWarning.getInventory();//欠货数量
				Integer oweNum = Math.abs(oweInventory);
				if (num>oweNum){//多进货
					int purchaseNum = num - oweNum;//多进货数量
					BigDecimal scale = shopGoods.getCostPrice().multiply(new BigDecimal(purchaseNum)).setScale(2, BigDecimal.ROUND_HALF_UP);
					orderAmount = orderAmount.add(scale);
				}
				if (num<oweNum){//少进货
					int cNum = oweNum - num;
					BigDecimal scale = shopGoods.getCostPrice().multiply(new BigDecimal(cNum)).setScale(2, BigDecimal.ROUND_HALF_UP);
					orderAmount = orderAmount.subtract(scale);
				}
				goodsVo.setOweInventory(oweNum);

			}else {
				BigDecimal scale = shopGoods.getCostPrice().multiply(new BigDecimal(num)).setScale(2, BigDecimal.ROUND_HALF_UP);
				orderAmount = orderAmount.add(scale);
				goodsVo.setOweInventory(0);
			}
			goodsVoList.add(goodsVo);

			Map<String, Object> map1 = new HashMap<>();
			map1.put("wareCode",wareAllocation.getWareCodeOut());
			map1.put("specificationId",goodsSpec.getId());
			RdInventoryWarning inventoryWarningOut = rdInventoryWarningDao.findInventoryWarningByWareAndSpecId(map1);

			Long stockNow = 0l;
			Integer precautiousLine = 0;
			if (inventoryWarningOut==null){
				throw new NullPointerException("蜗米仓库商品"+goodsSpec.getId()+"数量不足");
			}else {
				if (inventoryWarningOut.getInventory()==null){
					stockNow = 0l;
				}else {
					stockNow = inventoryWarningOut.getInventory().longValue();
				}
				if (inventoryWarningOut.getPrecautiousLine()==null){
					precautiousLine = 0;
				}else {
					precautiousLine = inventoryWarningOut.getPrecautiousLine();
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
			adjustment.setStockInto(Long.valueOf(num));
			if(stockNow-Long.valueOf(num)<0){
				throw new Exception(shopGoods.getGoodsName()+"蜗米仓库商品库存数量不足");
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
			adjustmentOUT.setStockInto(Long.valueOf(num));
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
			if (inventoryWarningOut==null){
				throw new NullPointerException("出库商品"+goodsSpec.getId()+"为空");
			}else{
				Map<String,Object> inMap1 = new HashMap<>();
				inMap1.put("wareCode",wareAllocation.getWareCodeOut());
				inMap1.put("specificationId",goodsSpec.getId());
				inMap1.put("inventory",num);
				rdInventoryWarningDao.updateInventoryByWareCodeAndSpecId(inMap1);
			}
		}

		SelfOrderSubmitResult result = new SelfOrderSubmitResult();

		if (orderAmount.signum()==-1){//负数
			rdWareOrder.setCompensatePoint(orderAmount);//补偿积分
		}

		if (orderAmount.signum()==1){//正数
			rdWareOrder.setOrderState(WareOrderState.ORDER_STATE_NO_PATMENT);//待付款
			rdWareOrder.setFlagState(1);
			result.setFlagState(1);//需要支付
		}else {
			rdWareOrder.setOrderState(WareOrderState.ORDER_STATE_NO_AUDIT);//待审核
			rdWareOrder.setFlagState(0);
			result.setFlagState(0);
		}

		rdWareOrder.setOrderAmount(orderAmount);
		rdWareOrder.setOrderTotalPrice(orderAmount);
		rdWareOrderDao.insert(rdWareOrder);
		RdWareOrder wareOrder = rdWareOrderDao.findBySn(rdWareOrder.getOrderSn());
		System.out.println("w="+wareOrder);
		result.setWareOrder(wareOrder);
		result.setGoodsListVo(goodsVoList);
		return result;
	}

	@Override
	public RdWareAllocation findBySn(String orderSn) {
		return rdWareAllocationDao.findBySn(orderSn);
	}

	@Override
	public List<RdWareAllocation> haveAllocation(String wareCode, int status) {
		Map<String,Object> map = new HashMap<>();
		map.put("wareCodeIn",wareCode);
		map.put("status",status);
		return rdWareAllocationDao.haveAllocation(map);
	}
}
