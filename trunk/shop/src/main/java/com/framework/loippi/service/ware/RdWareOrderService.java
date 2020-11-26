package com.framework.loippi.service.ware;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.framework.loippi.entity.PayCommon;
import com.framework.loippi.entity.order.ShopOrderPay;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.ware.RdWareOrder;
import com.framework.loippi.service.GenericService;

/**
 * @author :ldq
 * @date:2020/6/18
 * @description:dubbo com.framework.loippi.service.ware
 */
public interface RdWareOrderService extends GenericService<RdWareOrder, Long> {
	RdWareOrder findBySn(String orderSn);

	List<RdWareOrder> findByPaySn(String paysn);

	void ProcessingIntegrals(String paysn, BigDecimal i, RdMmBasicInfo shopMember, ShopOrderPay pay, int shoppingPointSr);

	void updateByPaySn(String paysn, Long paymentId);

	Map<String, Object> updateOrderpay(PayCommon payCommon, String mmCode, String s, String paymentCode, String paymentId);

	void updateOrderStatePayFinish(String sn, String batchNo, String plug,String totalFee);

	void updateCancelOrder(long orderId, int opType, String memberId, int paytrem, String message, String opName);
}
