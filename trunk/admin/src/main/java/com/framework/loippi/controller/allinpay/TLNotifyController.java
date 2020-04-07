package com.framework.loippi.controller.allinpay;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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
							 @PathVariable String refundSn,//提现订单号
							 @PathVariable String mCode,//会员编号
							 HttpServletResponse response) {
		//request中的param
		String rps = request.getParameter("rps");
		System.out.println("************************");
		System.out.println("退款回调："+rps);
		System.out.println("************************");
		Map<String, Object> map = JacksonUtil.convertMap(rps);
		String status = (String) map.get("status");
		if(status.equals("error")){


		}
		if(status.equals("pending")){

		}
		if(status.equals("OK")){

		}
	}
}
