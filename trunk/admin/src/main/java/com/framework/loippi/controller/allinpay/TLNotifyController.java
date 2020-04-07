package com.framework.loippi.controller.allinpay;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.framework.loippi.entity.trade.ShopRefundReturn;
import com.framework.loippi.service.trade.ShopRefundReturnService;
import com.framework.loippi.service.wallet.RdMmWithdrawLogService;
import com.framework.loippi.utils.JacksonUtil;

/**
 * @author :ldq
 * @date:2020/4/3
 * @description:dubbo com.framework.loippi.controller.allinpay
 */
@Slf4j
@Controller
@RequestMapping("/admin/paynotify")
public class TLNotifyController {

	@Resource
	private RdMmWithdrawLogService rdMmWithdrawLogService;
	@Resource
	private ShopRefundReturnService refundReturnService;

	/**
	 * 提现回调
	 * @param request
	 * @param withdrawSn
	 * @param mCode
	 * @param response
	 */
	@RequestMapping({"/withdrawBank/{withdrawSn}/{mCode}.json"})
	public void withdrawBank(HttpServletRequest request,
							 @PathVariable String withdrawSn,//提现订单号
							 @PathVariable String mCode,//会员编号
							 HttpServletResponse response) {
		//request中的param
		String rps = request.getParameter("rps");
		System.out.println("************************");
		System.out.println("提现回调："+rps);
		System.out.println("************************");
		Map<String, Object> map = JacksonUtil.convertMap(rps);
		String status = (String) map.get("status");
		if(status.equals("error")){
			rdMmWithdrawLogService.updateStatusBySnAndMCode(1,withdrawSn,mCode);
		}
		if(status.equals("pending")){
			rdMmWithdrawLogService.updateStatusBySnAndMCode(0,withdrawSn,mCode);
		}
		if(status.equals("OK")){
			rdMmWithdrawLogService.updateStatusBySnAndMCode(0,withdrawSn,mCode);
		}
	}

	/**
	 * 退款回调
	 * @param request
	 * @param refundSn
	 * @param mCode
	 * @param response
	 */
	@RequestMapping({"/refundBank/{refundSn}/{mCode}.json"})
	public void refundBank(HttpServletRequest request,
							 @PathVariable String refundSn,//退款表Id
							 @PathVariable String mCode,//会员编号
							 HttpServletResponse response) {
		//request中的param
		String rps = request.getParameter("rps");
		System.out.println("************************");
		System.out.println("退款回调："+rps);
		System.out.println("************************");
		Map<String, Object> map = JacksonUtil.convertMap(rps);
		String status = (String) map.get("status");

		ShopRefundReturn refundReturn = refundReturnService.find(Long.valueOf(refundSn));
		String adminMsg = Optional.ofNullable(refundReturn.getAdminMessage()).orElse("");

		if(status.equals("error")){
			String message = Optional.ofNullable(map.get("message").toString()).orElse("");
			String oriOrderNo = Optional.ofNullable(map.get("oriOrderNo").toString()).orElse("");// 原通商云订单号
			String msg = adminMsg+","+message;
			refundReturnService.updateTlStatusById(refundSn,0,msg);
		}
		if(status.equals("pending")){
			refundReturnService.updateTlStatusById(refundSn,1,adminMsg);
		}
		if(status.equals("OK")){
			String buyerBizUserId = map.get("buyerBizUserId").toString();//商户系统用户标识，商 户系统中唯一编号。 付款人
			String oriOrderNo = Optional.ofNullable(map.get("oriOrderNo").toString()).orElse("");// 原通商云订单号
			String oriBizOrderNo = Optional.ofNullable(map.get("oriBizOrderNo").toString()).orElse("");//原商户订单号
			Long amount = Optional.ofNullable(Long.valueOf(map.get("amount").toString())).orElse(0l);//订单金额
			refundReturnService.updateTlStatusById(refundSn,2,adminMsg);
		}
	}
}
