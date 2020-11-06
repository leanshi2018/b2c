package com.framework.loippi.service.impl;

import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.framework.loippi.entity.CancelOrderReq;
import com.framework.loippi.entity.CreateExpressOrderJsonDTO;
import com.framework.loippi.entity.SearchRouteReq;
import com.framework.loippi.enus.ShunFengOperation;
import com.framework.loippi.enus.ShunFengServieCodeEnum;
import com.framework.loippi.service.ShunFengJsonExpressService;
import com.framework.loippi.utils.shunfeng.HttpClientUtil;
import com.framework.loippi.utils.shunfeng.VerifyCodeUtil;

/**
 * @author :ldq
 * @date:2020/11/3
 * @description:dubbo com.framework.loippi.service.impl
 */
@Service
public class ShunFengJsonExpressServiceImpl implements ShunFengJsonExpressService {



	private static Logger logger = LoggerFactory.getLogger(ShunFengJsonExpressServiceImpl.class);
	/**
	 * jackson的json转化类
	 */

	// 测试环境请求url
	private static final String CALL_URL_BOX = "https://fyp-sit.sf-express.com/";
	// 正式环境请求url
	private static final String CALL_URL_PROD = "https://fyp-sit.sf-express.com/";
	//
	private static final String APP_CODE = "m40itwu80";
	//
	private static final String APP_KEY = "zkiGVxKk5pOKxHonqlUZS4QTtCEtJPASJ6eit27p1jognENLPOXYjIyS4bI3NgQNlXWH9ZReRQeRFoQl+MuVhw==";
	// 顺丰月结卡号
	private static final String CUST_ID = "7551234567";


	/**
	 * 顺丰接口操作集合
	 *
	 * @param requestContent    请求内容
	 * @param shunFengOperation 操作类型
	 * @return
	 */
	@Override
	public String shunFengOperationProcessor(Object requestContent, ShunFengOperation shunFengOperation) {
		// 请求数据
		String requestJson = "";
		// 请求服务的方法
		String servieCode = "";
		switch (shunFengOperation) {
			case CRETE_ORDER: //创建订单
				CreateExpressOrderJsonDTO createExpressOrderReq = (CreateExpressOrderJsonDTO) requestContent;
				requestJson = getOrderServiceRequestJson(createExpressOrderReq);
				servieCode = ShunFengServieCodeEnum.EXP_RECE_CREATE_ORDER;
				break;
			case CANCEL_ORDER: //取消订单
				String orderNo = (String) requestContent;
				requestJson = getOrderCancelServiceRequestJson(orderNo);
				servieCode = ShunFengServieCodeEnum.EXP_RECE_UPDATE_ORDER;
				break;
			case ROUTE_SEARCH: //路由查询
				String mailNo = (String) requestContent;
				requestJson = getRouteServiceRequestJson(mailNo);
				servieCode = ShunFengServieCodeEnum.EXP_RECE_SEARCH_ROUTES;
				break;
			default:
				break;
		}

		Map<String, String> params = new HashMap<>(8);
		String timeStamp = String.valueOf(System.currentTimeMillis());
		// 合作伙伴编码（即顾客编码）
		//params.put("partnerID", CLIENT_CODE);
		//params.put("requestID", UUID.randomUUID().toString().replace("-", ""));
		// 接口服务代码（到API接口详情查看具体服务代码）
		//params.put("serviceCode", servieCode.name());
		// 时间戳
		params.put("timestamp", timeStamp);
		// 业务数据报文
		params.put("msgData", requestJson);
		// 数字签名  加密串规则 msgData（业务报文）+ timestamp+checkWord(客户校验码)
		String accessToken = "";
		try {
			//登录授权获得accessToken
			Map<String, String> map = new HashMap<>(8);
			map.put("appCode",APP_CODE);
			map.put("sign",VerifyCodeUtil.encryptToMd5(APP_CODE + APP_KEY + timeStamp));
			map.put("timestamp",timeStamp);
			JSONObject jsonObject = JSONObject.fromObject(map);
			String str = HttpClientUtil.acquireDataLogin(CALL_URL_BOX, ShunFengServieCodeEnum.EXP_RECE_LOGIN_ORDER, "", jsonObject.toString());
			System.out.println("登录授权str="+str);
			Map mapType = JSON.parseObject(str,Map.class);
			accessToken = mapType.get("data").toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (accessToken==null){
			throw new NullPointerException("登录授权发生错误，accessToken为null");
		}
		String result = null;
		try {
			result = HttpClientUtil.acquireData(CALL_URL_BOX,servieCode,accessToken,requestJson);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("请求报文：{}", params.get("msgData"));
		logger.info("响应报文：{}", result);
		return result;
	}

	/**
	 * 获取下单请求体
	 *
	 * @param req
	 * @return
	 */
	private String getOrderServiceRequestJson(CreateExpressOrderJsonDTO req) {

		// 月结卡号
		req.setExpressType(2);
		req.setIsDoCall(0);
		req.setMonthlyCust(CUST_ID);
		req.setPackageNumber(1);
		req.setPayMethod(2);
		req.setBspType(1);
		req.setReturnType("其他");
		req.setDoOrderNow("1");
		// 寄件基本信息
		req.setProductName("白酒");
		// 寄件人信息
		req.setJcompany("广东乐安士电子商务有限公司");
		req.setJcontact("蜗米商城");
		req.setJmobile("13378412484");
		req.setJprovince("贵州省");
		req.setJcity("遵义市");
		req.setJcounty("仁怀市");
		req.setJaddress("贵州省遵义市仁怀市国威路顺丰快运网点");


		return objectToJson(req);
	}

	/**
	 * 根据快递号查询快递记录
	 *
	 * @param mailNo 快递运单号
	 * @return
	 */
	private String getRouteServiceRequestJson(String mailNo) {
		SearchRouteReq searchRouteReq = new SearchRouteReq();
		// 根据快递号查询
		searchRouteReq.setTrackingType("1");
		searchRouteReq.setTrackingNumber(mailNo);
		return objectToJson(searchRouteReq);
	}

	/**
	 * 取消顺丰订单请求体
	 *
	 * @param orderNo 订单号
	 * @return
	 */
	private String getOrderCancelServiceRequestJson(String orderNo) {
		CancelOrderReq req = new CancelOrderReq();
		req.setOrderId(orderNo);
		return objectToJson(req);
	}

	/**
	 * object转为json
	 *
	 * @param obj 实体类
	 * @return java.lang.String
	 * @author Li Yulong
	 * @date 2020-08-12 21:38
	 */
	private String objectToJson(Object obj) {
		try {
			//com.alibaba.fastjson.JSONObject res = JSON.parseObject(obj.toString());
			String res = JSON.toJSONString(obj);
			return res.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

}
