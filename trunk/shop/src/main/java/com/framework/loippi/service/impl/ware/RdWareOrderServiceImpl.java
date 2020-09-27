package com.framework.loippi.service.impl.ware;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.consts.OrderState;
import com.framework.loippi.consts.WareOrderState;
import com.framework.loippi.controller.AppConstants;
import com.framework.loippi.controller.StateResult;
import com.framework.loippi.dao.ShopCommonMessageDao;
import com.framework.loippi.dao.ShopMemberMessageDao;
import com.framework.loippi.dao.order.ShopOrderLogDao;
import com.framework.loippi.dao.order.ShopOrderPayDao;
import com.framework.loippi.dao.user.RdGoodsAdjustmentDao;
import com.framework.loippi.dao.user.RdMmAccountInfoDao;
import com.framework.loippi.dao.user.RdMmAccountLogDao;
import com.framework.loippi.dao.user.RdSysPeriodDao;
import com.framework.loippi.dao.ware.RdInventoryWarningDao;
import com.framework.loippi.dao.ware.RdWareAdjustDao;
import com.framework.loippi.dao.ware.RdWareAllocationDao;
import com.framework.loippi.dao.ware.RdWareOrderDao;
import com.framework.loippi.dao.ware.RdWarehouseDao;
import com.framework.loippi.entity.PayCommon;
import com.framework.loippi.entity.ShopCommonMessage;
import com.framework.loippi.entity.ShopMemberMessage;
import com.framework.loippi.entity.TSystemPluginConfig;
import com.framework.loippi.entity.order.ShopOrderLog;
import com.framework.loippi.entity.order.ShopOrderPay;
import com.framework.loippi.entity.user.RdGoodsAdjustment;
import com.framework.loippi.entity.user.RdMmAccountInfo;
import com.framework.loippi.entity.user.RdMmAccountLog;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.ware.RdInventoryWarning;
import com.framework.loippi.entity.ware.RdWareAdjust;
import com.framework.loippi.entity.ware.RdWareAllocation;
import com.framework.loippi.entity.ware.RdWareOrder;
import com.framework.loippi.entity.ware.RdWarehouse;
import com.framework.loippi.service.TSystemPluginConfigService;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.service.ware.RdWareOrderService;
import com.framework.loippi.utils.Paramap;
import com.google.common.collect.Maps;

/**
 * @author :ldq
 * @date:2020/6/18
 * @description:dubbo com.framework.loippi.service.impl.ware
 */
@Service
public class RdWareOrderServiceImpl extends GenericServiceImpl<RdWareOrder, Long> implements RdWareOrderService {
	@Autowired
	private RdWareOrderDao rdWareOrderDao;
	@Autowired
	private RdMmAccountLogDao rdMmAccountLogDao;
	@Autowired
	private RdMmAccountInfoDao rdMmAccountInfoDao;
	@Autowired
	private ShopMemberMessageDao shopMemberMessageDao;
	@Autowired
	private ShopCommonMessageDao shopCommonMessageDao;
	@Autowired
	private TwiterIdService twiterIdService;
	@Autowired
	private RdSysPeriodDao rdSysPeriodDao;
	@Autowired
	private ShopOrderPayDao orderPayDao;
	@Autowired
	private TSystemPluginConfigService tSystemPluginConfigService;
	@Autowired
	private RdMmBasicInfoService rdMmBasicInfoService;
	@Autowired
	private ShopOrderLogDao orderLogDao;
	@Autowired
	private RdWareAllocationDao rdWareAllocationDao;
	@Autowired
	private RdWareAdjustDao rdWareAdjustDao;
	@Autowired
	private RdWarehouseDao rdWarehouseDao;
	@Autowired
	private RdGoodsAdjustmentDao rdGoodsAdjustmentDao;
	@Autowired
	private RdInventoryWarningDao rdInventoryWarningDao;

	@Autowired
	public void setGenericDao() {
		super.setGenericDao(rdWareOrderDao);
	}

	@Override
	public RdWareOrder findBySn(String orderSn) {
		return rdWareOrderDao.findBySn(orderSn);
	}

	@Override
	public List<RdWareOrder> findByPaySn(String paySn) {
		return rdWareOrderDao.findByPaySn(paySn);
	}

	@Override
	public void ProcessingIntegrals(String paysn, int integration, RdMmBasicInfo shopMember, ShopOrderPay pay, int shoppingPointSr) {
		//第一步 判断积分是否正确
		if (integration < 0) {
			throw new StateResult(AppConstants.GOODS_STATE_ERRO, "要使用的积分不能小于0");
		}
		//积分
		RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoDao.findAccByMCode(shopMember.getMmCode());

		if (rdMmAccountInfo == null) {
			throw new StateResult(AppConstants.GOODS_STATE_ERRO, "用户积分不正确");
		}
		if (rdMmAccountInfo.getWalletStatus() != 0) {
			throw new StateResult(AppConstants.GOODS_STATE_ERRO, "用户积分未激活或者已冻结 ");
		}

		if (rdMmAccountInfo.getWalletBlance().compareTo(BigDecimal.valueOf(integration)) == -1) {
			throw new StateResult(AppConstants.GOODS_STATE_ERRO, "要使用的积分不能大于拥有积分");
		}

		BigDecimal shoppingPoints = new BigDecimal(integration * shoppingPointSr * 0.01);
		if (shoppingPoints.compareTo(pay.getPayAmount()) == 1) {
			throw new StateResult(AppConstants.GOODS_STATE_ERRO, "要抵现的不能大于订单金额");
		}
		//修改订单价格
		List<RdWareOrder> orderList = rdWareOrderDao.findByPaySn(paysn);
		Long orderId = 0L;
		if (orderList != null && orderList.size() > 0) {
			for (RdWareOrder order : orderList) {
				if (order.getOrderState() != 10) {
					throw new StateResult(AppConstants.GOODS_STATE_ERRO, "订单已支付");
				}
				orderId = order.getId();
				int pointNum = 0;
				pointNum = new BigDecimal(
						(order.getOrderAmount().doubleValue() / pay.getPayAmount().doubleValue()) * (integration))
						.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
				order.setUsePointNum(Optional.ofNullable(order.getUsePointNum()).orElse(0) + pointNum);//设置订单所用积分数量
				order.setPointRmbNum(Optional.ofNullable(order.getPointRmbNum()).orElse(BigDecimal.ZERO)
						.add(new BigDecimal(pointNum * shoppingPointSr * 0.01).setScale(2, BigDecimal.ROUND_HALF_UP)));
				order.setOrderAmount(order.getOrderAmount()
						.subtract(new BigDecimal(pointNum * shoppingPointSr * 0.01).setScale(2, BigDecimal.ROUND_HALF_UP)));
				rdWareOrderDao.update(order);
				ShopCommonMessage shopCommonMessage=new ShopCommonMessage();
				shopCommonMessage.setSendUid(shopMember.getMmCode());
				shopCommonMessage.setType(1);
				shopCommonMessage.setOnLine(1);
				shopCommonMessage.setCreateTime(new Date());
				shopCommonMessage.setBizType(2);
				shopCommonMessage.setIsTop(1);
				shopCommonMessage.setCreateTime(new Date());
				shopCommonMessage.setTitle("积分消费");
				shopCommonMessage.setContent("您因调拨订单支付【订单号"+order.getOrderSn()+"】,扣减购物积分"+integration+",请在购物积分账户查看明细");
				Long msgId1 = twiterIdService.getTwiterId();
				shopCommonMessage.setId(msgId1);
				shopCommonMessageDao.insert(shopCommonMessage);
				ShopMemberMessage shopMemberMessage1=new ShopMemberMessage();
				shopMemberMessage1.setBizType(2);
				shopMemberMessage1.setCreateTime(new Date());
				shopMemberMessage1.setId(twiterIdService.getTwiterId());
				shopMemberMessage1.setIsRead(0);
				shopMemberMessage1.setMsgId(msgId1);
				shopMemberMessage1.setUid(Long.parseLong(shopMember.getMmCode()));
				shopMemberMessageDao.insert(shopMemberMessage1);
			}
		} else {
			throw new StateResult(AppConstants.GOODS_STATE_ERRO, "订单不存在");
		}
		//更新用户购物积分
		RdMmAccountLog rdMmAccountLog = new RdMmAccountLog();
		rdMmAccountLog.setTransTypeCode("OP");
		rdMmAccountLog.setAccType("SWB");
		rdMmAccountLog.setTrSourceType("SWB");
		rdMmAccountLog.setMmCode(shopMember.getMmCode());
		rdMmAccountLog.setMmNickName(shopMember.getMmNickName());
		rdMmAccountLog.setTrMmCode(shopMember.getMmCode());
		rdMmAccountLog.setBlanceBefore(rdMmAccountInfo.getWalletBlance());
		rdMmAccountLog.setAmount(BigDecimal.valueOf(integration));
		rdMmAccountLog.setTransDate(new Date());
		String period = rdSysPeriodDao.getSysPeriodService(new Date());
		rdMmAccountLog.setTransPeriod(period);
		rdMmAccountLog.setTrOrderOid(orderId);
		//无需审核直接成功
		rdMmAccountLog.setStatus(3);
		rdMmAccountLog.setCreationBy(shopMember.getMmNickName());
		rdMmAccountLog.setCreationTime(new Date());
		rdMmAccountLog.setAutohrizeBy(shopMember.getMmNickName());
		rdMmAccountLog.setAutohrizeTime(new Date());
		rdMmAccountInfo.setWalletBlance(rdMmAccountInfo.getWalletBlance().subtract(BigDecimal.valueOf(integration)));
		rdMmAccountLog.setBlanceAfter(rdMmAccountInfo.getWalletBlance());
		rdMmAccountInfoDao.update(rdMmAccountInfo);
		rdMmAccountLogDao.insert(rdMmAccountLog);

	}

	@Override
	public void updateByPaySn(String paysn, Long paymentId) {
		RdWareOrder order = find("paySn", paysn);
		TSystemPluginConfig payment = tSystemPluginConfigService.find(paymentId);
		// 更新
		order.setPaymentCode(payment.getPluginId()); //支付方式名称代码
		order.setPaymentId(payment.getId()); //支付方式id
		order.setPaymentName(payment.getPluginName()); //支付方式名称
//        order.setLockState(OrderState.ORDER_LOCK_STATE_YES);
		order.setPrevOrderState(WareOrderState.ORDER_STATE_NO_PATMENT);
		order.setOrderState(WareOrderState.ORDER_STATE_NO_PATMENT);
		updateByIdOrderStateLockState(order, OrderState.ORDER_OPERATE_PAY);
	}

	@Override
	public Map<String, Object> updateOrderpay(PayCommon payCommon, String memberId, String payName, String paymentCode, String paymentId) {

		RdMmBasicInfo shopMember = rdMmBasicInfoService.find("mmCode", memberId);

		List<RdWareOrder> wareOrderList = findList("paySn", payCommon.getOutTradeNo());
		String orderSn = "";
		if (CollectionUtils.isNotEmpty(wareOrderList)) {
			for (RdWareOrder wareOrder : wareOrderList) {
				if (wareOrder.getOrderState() != 10) {
					throw new StateResult(AppConstants.GOODS_STATE_ERRO, "订单已支付");
				}
				if (wareOrder.getPaymentState() == 0) {
					orderSn += wareOrder.getOrderSn() + ",";
					//新建一个订单日志
					ShopOrderLog orderLog = new ShopOrderLog();
					orderLog.setId(twiterIdService.getTwiterId());
					orderLog.setOrderState(WareOrderState.ORDER_STATE_NO_AUDIT + "");
					orderLog.setChangeState(WareOrderState.ORDER_STATE_UNFILLED + "");
					orderLog.setStateInfo("调拨订单付款完成");
					orderLog.setOrderId(wareOrder.getId());
					orderLog.setOperator(shopMember.getMmCode());
					orderLog.setCreateTime(new Date());
					//保存订单日志
					orderLogDao.insert(orderLog);
					//修改订单状态
					RdWareOrder newRdWareOrder = new RdWareOrder();
					newRdWareOrder.setOrderState(WareOrderState.ORDER_STATE_NO_AUDIT);
					newRdWareOrder.setPaymentState(WareOrderState.PAYMENT_STATE_YES);

					newRdWareOrder.setPaymentTime(new Date());
					String period = rdSysPeriodDao.getSysPeriodService(new Date());
					wareOrder.setCreationPeriod(period);
					rdWareOrderDao.update(wareOrder);
					newRdWareOrder.setTradeSn(payCommon.getOutTradeNo());
					//todo 暂时
					newRdWareOrder.setPaymentName(payName);
					newRdWareOrder.setPaymentCode(paymentCode);
					newRdWareOrder.setPaymentId(Long.valueOf(paymentId));
					// 条件
					newRdWareOrder.setId(wareOrder.getId());
					newRdWareOrder.setPrevOrderState(OrderState.ORDER_STATE_NO_PATMENT);
					updateByIdOrderStateLockState(newRdWareOrder, OrderState.ORDER_OPERATE_PAY);
				}
			}


			Map<String, Object> result = Maps.newConcurrentMap();
			result.put("status", 1);
			result.put("orderSn", orderSn);
			result.put("message", "支付成功");
			return result;

		}
		Map<String, Object> result = Maps.newConcurrentMap();
		result.put("status", 0);
		result.put("message", "支付失败");
		return result;
	}

	@Override
	public void updateOrderStatePayFinish(String paysn, String tradeSn, String paymentBranch) {
		// 用于积分计算
		double orderTotalAmount = 0.0;
		String memberId = "";
		List<RdWareOrder> rdWareOrderList = findList(Paramap.create().put("paySn", paysn).put("paymentState", 0));
		if (CollectionUtils.isEmpty(rdWareOrderList)) {
			return;
		}

		for (RdWareOrder rdWareOrder : rdWareOrderList) {
			if (rdWareOrder.getPaymentState() == 0) {//未付款
				memberId = rdWareOrder.getMCode();
				// 新建一个订单日志
				ShopOrderLog orderLog = new ShopOrderLog();
				orderLog.setId(twiterIdService.getTwiterId());
				orderLog.setOrderState(WareOrderState.ORDER_STATE_NO_AUDIT + "");
				orderLog.setChangeState(WareOrderState.ORDER_STATE_UNFILLED + "");
				orderLog.setStateInfo("订单付款完成");
				orderLog.setOrderId(rdWareOrder.getId());
				RdMmBasicInfo basicInfo = rdMmBasicInfoService.findByMCode(memberId);
				orderLog.setOperator(basicInfo.getMmNickName());
				orderLog.setCreateTime(new Date());
				// 保存订单日志
				orderLogDao.insert(orderLog);
				// 修改订单状态
				rdWareOrder.setPaymentState(1);
				rdWareOrder.setPaymentTime(new Date());
				rdWareOrder.setTradeSn(tradeSn);
				rdWareOrder.setOrderState(WareOrderState.ORDER_STATE_NO_AUDIT);
				rdWareOrderDao.update(rdWareOrder);

				orderTotalAmount += rdWareOrder.getOrderAmount().doubleValue();
			}
		}
		// 更新支付表
		ShopOrderPay orderPay = new ShopOrderPay();
		orderPay.setPaySn(paysn);
		orderPay.setApiPayState("1");
		orderPayDao.updateByPaysn(orderPay);

	}

	@Override
	public void updateCancelOrder(long orderId, int opType, String memberId, int paytrem, String message, String opName) {

		RdWareOrder wareOrder = rdWareOrderDao.find(orderId);
		String orderSn = wareOrder.getOrderSn();

		BigDecimal refundPoint = BigDecimal.ZERO;
		if (wareOrder.getFlagState()==1){//需要支付
			if (wareOrder.getOrderState() != WareOrderState.ORDER_STATE_NO_PATMENT
					&&wareOrder.getOrderState() != WareOrderState.ORDER_STATE_NO_AUDIT) {
				throw new RuntimeException("订单状态错误！");
			}

			//退积分
			List<RdMmAccountInfo> rdMmAccountInfoList = rdMmAccountInfoDao.findByMCode(memberId);
			RdMmBasicInfo rdMmBasicInfo=rdMmBasicInfoService.find("mmCode", memberId);
			RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoList.get(0);
			if (wareOrder.getUsePointNum() != null && wareOrder.getUsePointNum() != 0) {
				RdMmAccountLog rdMmAccountLog = new RdMmAccountLog();
				rdMmAccountLog.setTransTypeCode("OT");
				rdMmAccountLog.setAccType("SWB");
				rdMmAccountLog.setTrSourceType("OWB");

				BigDecimal availableBefore = rdMmAccountInfo.getWalletBlance();//原购物积分
				BigDecimal available = rdMmAccountInfo.getWalletBlance().add(BigDecimal.valueOf(wareOrder.getUsePointNum())).setScale(2, BigDecimal.ROUND_HALF_UP);//退后购物积分
				refundPoint = refundPoint.add(BigDecimal.valueOf(wareOrder.getUsePointNum())).setScale(2, BigDecimal.ROUND_HALF_UP);
				rdMmAccountInfo.setWalletBlance(available);
				//用户更新积分
				rdMmAccountLog.setTrOrderOid(wareOrder.getId());
				rdMmAccountLog.setMmCode(rdMmBasicInfo.getMmCode());
				rdMmAccountLog.setMmNickName(rdMmBasicInfo.getMmNickName());
				rdMmAccountLog.setTrMmCode(rdMmBasicInfo.getMmCode());
				rdMmAccountLog.setBlanceBefore(availableBefore);
				rdMmAccountLog.setAmount(BigDecimal.valueOf(wareOrder.getUsePointNum()));
				rdMmAccountLog.setBlanceAfter(available);
				//无需审核直接成功
				rdMmAccountLog.setStatus(3);
				rdMmAccountLog.setCreationBy(rdMmBasicInfo.getMmNickName());
				rdMmAccountLog.setCreationTime(new Date());
				rdMmAccountLog.setAutohrizeBy(opName);
				rdMmAccountLog.setAutohrizeTime(new Date());
				rdMmAccountLog.setTransDate(new Date());
				rdMmAccountLog.setTransDesc("调拨订单"+wareOrder.getOrderSn()+"取消返还积分");
				rdMmAccountLogDao.insert(rdMmAccountLog);
				rdMmAccountInfoDao.update(rdMmAccountInfo);

				ShopCommonMessage shopCommonMessage1=new ShopCommonMessage();
				shopCommonMessage1.setSendUid(rdMmBasicInfo.getMmCode());
				shopCommonMessage1.setType(1);
				shopCommonMessage1.setOnLine(1);
				shopCommonMessage1.setCreateTime(new Date());
				shopCommonMessage1.setBizType(2);
				shopCommonMessage1.setIsTop(1);
				shopCommonMessage1.setCreateTime(new Date());
				shopCommonMessage1.setTitle("积分到账");
				shopCommonMessage1.setContent("您取消了订单"+wareOrder.getOrderSn()+",返还"+wareOrder.getUsePointNum()+"点购物积分,请进入购物积分账户查看");
				Long msgId = twiterIdService.getTwiterId();
				shopCommonMessage1.setId(msgId);
				shopCommonMessageDao.insert(shopCommonMessage1);
				ShopMemberMessage shopMemberMessage1=new ShopMemberMessage();
				shopMemberMessage1.setBizType(2);
				shopMemberMessage1.setCreateTime(new Date());
				shopMemberMessage1.setId(twiterIdService.getTwiterId());
				shopMemberMessage1.setIsRead(0);
				shopMemberMessage1.setMsgId(msgId);
				shopMemberMessage1.setUid(Long.parseLong(rdMmBasicInfo.getMmCode()));
				shopMemberMessageDao.insert(shopMemberMessage1);
			}
		}

		if (wareOrder.getFlagState()==0){
			if (wareOrder.getOrderState() != WareOrderState.ORDER_STATE_NO_AUDIT) {
				throw new RuntimeException("订单状态错误！");
			}
		}
		wareOrder.setRefundPoint(refundPoint);
		wareOrder.setOrderDesc(message); // 取消原因
		wareOrder.setOrderState(WareOrderState.ORDER_STATE_CANCLE); //订单状态
		rdWareOrderDao.update(wareOrder);

		//8调拨订单
		RdWareAllocation allocation = rdWareAllocationDao.findBySn(orderSn);
		RdWarehouse ware = rdWarehouseDao.findByCode(allocation.getWareCodeIn());
		Map<String,Object> mapAll = new HashMap<>();
		mapAll.put("wId",allocation.getWId());
		mapAll.put("sign",2);
		List<RdGoodsAdjustment> goodsAdjustments = rdGoodsAdjustmentDao.findByWidAndSign(mapAll);

		//添加入库调整单(调拨单号的出库)
		RdWareAdjust wareAdjustIn = new RdWareAdjust();
		wareAdjustIn.setWareCode(allocation.getWareCodeOut());
		wareAdjustIn.setWareName(allocation.getWareNameOut());
		wareAdjustIn.setAdjustType("OAW");
		wareAdjustIn.setAttachAdd(allocation.getAttachAdd());
		wareAdjustIn.setWareAmount(new BigDecimal("0.00"));
		wareAdjustIn.setStatus(3);
		wareAdjustIn.setAutohrizeBy("取消订单"+orderSn);
		wareAdjustIn.setAutohrizeTime(new Date());
		wareAdjustIn.setAutohrizeDesc("调拨单号"+allocation.getWId()+"取消订单返还入库");
		rdWareAdjustDao.insert(wareAdjustIn);

		for (RdGoodsAdjustment goodsAdjustment : goodsAdjustments) {
			//入库(调拨单号的出库)
			RdGoodsAdjustment adjustmentIN = new RdGoodsAdjustment();
			adjustmentIN.setGoodId(goodsAdjustment.getGoodId());
			adjustmentIN.setGoodsName(goodsAdjustment.getGoodsName());
			adjustmentIN.setSpecificationId(goodsAdjustment.getSpecificationId());
			adjustmentIN.setSpecName(goodsAdjustment.getSpecName());
			adjustmentIN.setGoodsSpec(goodsAdjustment.getGoodsSpec());
			adjustmentIN.setSpecGoodsSerial(goodsAdjustment.getSpecGoodsSerial());
			Map<String,Object> mapIn = new HashMap<>();
			mapIn.put("wareCode",allocation.getWareCodeOut());
			mapIn.put("specificationId",goodsAdjustment.getSpecificationId());
			RdInventoryWarning WarningIn = rdInventoryWarningDao.findInventoryWarningByWareAndSpecId(mapIn);
			Long stockNow = 0l;
			if (WarningIn==null){
				stockNow = 0l;
				//入库库存(调拨单号的出库)
				RdInventoryWarning inventoryWarning = new RdInventoryWarning();
				inventoryWarning.setWareCode(allocation.getWareCodeIn());
				inventoryWarning.setWareName(allocation.getWareNameIn());
				inventoryWarning.setGoodsCode(goodsAdjustment.getGoodId().toString());
				inventoryWarning.setGoodsName(goodsAdjustment.getGoodsName());
				inventoryWarning.setSpecificationId(goodsAdjustment.getSpecificationId());
				inventoryWarning.setSpecifications(goodsAdjustment.getGoodsSpec());
				inventoryWarning.setInventory(goodsAdjustment.getStockInto().intValue());
				inventoryWarning.setPrecautiousLine(0);
				rdInventoryWarningDao.insert(inventoryWarning);

			}else {
				stockNow = new Long(WarningIn.getInventory());
				//入库库存(调拨单号的出库)
				WarningIn.setInventory(stockNow.intValue()+goodsAdjustment.getStockInto().intValue());
				rdInventoryWarningDao.update(WarningIn);
			}
			adjustmentIN.setStockNow(stockNow);
			adjustmentIN.setStockInto(goodsAdjustment.getStockInto());
			adjustmentIN.setCreateTime(new Date());
			adjustmentIN.setQualityTime(goodsAdjustment.getQualityTime());
			adjustmentIN.setShelfLifeTime(goodsAdjustment.getShelfLifeTime());
			adjustmentIN.setPrecautiousLine(goodsAdjustment.getPrecautiousLine());
			adjustmentIN.setWid(wareAdjustIn.getWid());
			adjustmentIN.setSign(1);
			adjustmentIN.setWareCode(allocation.getWareCodeOut());
			adjustmentIN.setAutohrizeTime(new Date());
			adjustmentIN.setStatus(1l);
			rdGoodsAdjustmentDao.insert(adjustmentIN);

		}

		//修改调拨单状态
		allocation.setStatus(-2);
		allocation.setAutohrizeBy("用户取消");
		allocation.setAutohrizeTime(new Date());
		allocation.setAutohrizeDesc("取消订单"+orderSn);
		rdWareAllocationDao.update(allocation);

	}

	public void updateByIdOrderStateLockState(RdWareOrder order, int operateType) {
		if (order.getId() == null || order.getPrevOrderState() == null) {
			throw new IllegalArgumentException("参数错误");
		}

		if (operateType != OrderState.ORDER_OPERATE_PAY) {
			throw new IllegalArgumentException("参数错误");
		}

		Long result = rdWareOrderDao.updateByIdAndOrderStateAndLockState(order);
		if (result.intValue() != 1) {
			RdWareOrder findOrder = rdWareOrderDao.find(order.getId());
			if (findOrder == null) {
				throw new RuntimeException("不存在调拨订单");
			}
			String exceptionMsg = null;
			switch (operateType) {
				case OrderState.ORDER_OPERATE_PAY:
					// 【前端订单发起支付中】 -- 【后台取消订单中】 => 后台取消订单先完成
					if (!findOrder.getOrderState().equals(order.getPrevOrderState())) {
						exceptionMsg = "订单已经取消";
					}
					break;
				case OrderState.ORDER_OPERATE_CANCEL:
					// 【订单支付第三方调用完成支付接口】-- 【后台在取消订单中】 =》第三方调用先完成
					if (!findOrder.getOrderState().equals(order.getPrevOrderState())) {
						exceptionMsg = "订单已经支付成功";
					}
					break;
				// 【后台发货中】 -- 【前端取消订单中】 -- 取消订单先完成
				case OrderState.ORDER_OPERATE_DELIVERY:
					if (!findOrder.getOrderState().equals(order.getPrevOrderState())) {
						exceptionMsg = "订单已经取消";
					}
					break;
				default:
					exceptionMsg = "订单更新失败";
					break;
			}

			throw new StateResult(AppConstants.ORDER_UPDATE_FAIL, exceptionMsg);
		}
	}

}
