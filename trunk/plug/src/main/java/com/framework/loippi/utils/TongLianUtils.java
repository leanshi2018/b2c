package com.framework.loippi.utils;

import net.sf.json.JSONArray;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.allinpay.yunst.sdk.YunClient;
import com.allinpay.yunst.sdk.bean.YunConfig;
import com.allinpay.yunst.sdk.bean.YunRequest;

/**
 * 通联工具类
 * @author :ldq
 * @date:2020/3/5
 * @description:dubbo com.framework.loippi.utils
 */
public class TongLianUtils {

	public static final String SERVER_URL = "http://116.228.64.55:6900/service/soa";//测试环境请咨询对接人员(通商云门户测试环境地址)
	public static final String SYS_ID = "1902271423530473681";//商户私钥证书
	public static final String PWD = "123456";//商户私钥密码
	public static final String ALIAS = "1902271423530473681";//商户公钥证书
	public static final String PATH = "C:\\Users\\Administrator\\Desktop\\sign\\1902271423530473681.pfx";//pfx证书路径
	//public static final String PATH =  "C:\\Users\\LDQ\\Desktop\\tongliansign\\1902271423530473681.pfx";
	public static final String TL_CERT_PATH = "C:\\Users\\Administrator\\Desktop\\sign\\TLCert-test.cer";//cer证书路径
	//public static final String TL_CERT_PATH = "C:\\Users\\LDQ\\Desktop\\tongliansign\\TLCert-test.cer";
	public static final String VERSION = "2.0";//接口版本
	public static final String BIZ_USER_ID = "201807170002";//商户系统用户标识，商户系统中唯一编号    公司编号

	public static void getRequest() {
		//final String serverUrl = "https://fintech.allinpay.com/service/soa"; //测试环境请咨询对接人员
		final String serverUrl = SERVER_URL; //测试环境请咨询对接人员(通商云门户测试环境地址)
		final String sysId = SYS_ID;//商户私钥证书
		final String pwd = PWD;//商户私钥密码
		//final String alias = "100000000002";
		final String alias = ALIAS;//商户公钥证书
		//final String path = "D:\\tonglian-yun\\branches\\yuntestjava\\100000000002.pfx";
		final String path = PATH;
		//final String tlCertPath = "D:\\tonglian-yun\\branches\\yuntestjava\\TLCert-test.cer"; //生产环境请使用生产证书
		final String tlCertPath = TL_CERT_PATH; //生产环境请使用生产证书
		final String version = VERSION;
		final YunConfig config = new YunConfig(serverUrl, sysId, pwd, alias, version, path,tlCertPath);
		YunClient.configure(config);

	}

	/**
	 * 开户
	 * @param bizUserId  商户系统用户标识，商户系统中唯一编号
	 * @param memberType 会员类型
	 * @param source  访问终端类型
	 * @return
	 */
	public static String createMember(String bizUserId, Long memberType, Long source) {
		getRequest();
		final YunRequest request = new YunRequest("MemberService", "createMember");
		request.put("bizUserId", bizUserId);
		request.put("memberType", memberType);
		request.put("source", source);
		try {
			String s = YunClient.request(request);
			return s;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 通联 托管代收（支付）
	 * @param bizOrderNo 商户订单号（支付订单）
	 * @param payerId 商户系统用户标识，商户 系统中唯一编号。 付款人
	 * @param recieverList 收款列表 bizUserId(String) 商户系统用户标识，商户系统中唯一编号。  amount(Long) 金额，单位：分
	 * @param goodsType 商品类型   否
	 * @param bizGoodsNo 商户系统商品编号  否
	 * @param tradeCode 业务码 3001代收 4001代付 否
	 * @param amount 订单金额
	 * @param fee 手续费  否
	 * @param validateType  交易验证方式 0 否
	 * @param frontUrl 前台通知地址 否
	 * @param backUrl 后台通知地址
	 * @param orderExpireDatetime 订单过期时间 否
	 * @param payMethod 支付方式
	 * @param goodsName 商品名称 否
	 * @param goodsDesc 商品描述 否
	 * @param industryCode 行业代码 (1910)
	 * @param industryName 行业名称 (其他)
	 * @param source 访问终端类型
	 * @param summary 摘要 否
	 * @param extendInfo 扩展参数 否
	 */
	public static String agentCollectApply(String bizOrderNo, String payerId, List<Map<String, Object>> recieverList, Long goodsType, String bizGoodsNo, String tradeCode,
										   Long amount, Long fee, Long validateType, String frontUrl, String backUrl, String orderExpireDatetime, Map<String, Object> payMethod,
										   String goodsName, String goodsDesc, String industryCode, String industryName, Long source, String summary, String extendInfo) {
		getRequest();
		final YunRequest request = new YunRequest("OrderService", "agentCollectApply");
		request.put("bizOrderNo", bizOrderNo);
		request.put("payerId", payerId);
		request.put("recieverList", recieverList);
		request.put("goodsType", goodsType);
		//request.put("bizGoodsNo", bizGoodsNo);
		request.put("tradeCode", tradeCode);
		request.put("amount", amount);
		request.put("fee", fee);
		request.put("validateType", validateType);
		//request.put("frontUrl", frontUrl);
		request.put("backUrl", backUrl);
		//request.put("orderExpireDatetime", orderExpireDatetime);
		request.put("payMethod", payMethod);
		//request.put("goodsName", goodsName);
		//request.put("goodsDesc", goodsDesc);
		request.put("industryCode", industryCode);
		request.put("industryName", industryName);
		request.put("source", source);
		//request.put("summary", summary);
		//request.put("extendInfo", extendInfo);
		try {
			String s = YunClient.request(request);
			return s;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 确认支付
	 * @param bizUserId  商户系统用户标识，商户系统中唯一编号
	 * @param bizOrderNo 订单申请的商户订单号 （支付订单）
	 * @param tradeNo  交易编号
	 * @param verificationCode  短信验证码
	 * @param consumerIp  ip 地址  用户公网 IP 用于分控校验 注：不能使用“127.0.0.1” “localhost”
	 * @return
	 */
	public static String confirmPay(String bizUserId, String bizOrderNo, String tradeNo, String verificationCode, String consumerIp) {
		getRequest();
		final YunRequest request = new YunRequest("OrderService", "pay");
		request.put("bizUserId", bizUserId);
		request.put("bizOrderNo", bizOrderNo);
		request.put("tradeNo", tradeNo);
		request.put("verificationCode", verificationCode);
		request.put("consumerIp", consumerIp);
		try {
			String s = YunClient.request(request);
			return s;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 退款申请
	 * @param bizOrderNo 订单申请的商户订单号 （支付订单）
	 * @param oriBizOrderNo  商户原订单号 需要退款的原交易订单号
	 * @param bizUserId  商户系统用户标识，商户系统中唯一编号。 退款收款人。
	 * @param refundType  退款方式 默认 D1 D1：D+1 14:30 向渠道发起退款 D0：D+0 实时向渠道发起退款 否
	 * @param refundList  托管代收订单中的收款人的退款金额  全额退款填  部分退款不填 否
	 * @param backUrl 后台通知地址 如果不填，则不通知。 退款成功时，才会通知。  否
	 * @param amount 本次退款总金额 单位：分
	 * @param couponAmount 代金券退款金额 单位：分 不得超过退款总金额，支持部分退款。 如不填，则默认为 0。 如为 0，则不退代金券 否
	 * @param feeAmount 手续费退款金额 单位：分 不得超过退款总金额。 如不填，则默认为 0。 如为 0，则不退手续费。 否
	 * @param extendInfo  扩展信息 否
	 * @return
	 */
	public static String refundOrder(String bizOrderNo, String oriBizOrderNo, String bizUserId, String refundType, JSONArray refundList,
									 String backUrl, Long amount, Long couponAmount, Long feeAmount,String extendInfo) {
		getRequest();
		final YunRequest request = new YunRequest("OrderService", "refund");
		request.put("bizOrderNo", bizOrderNo);
		request.put("oriBizOrderNo", oriBizOrderNo);
		request.put("bizUserId", bizUserId);
		request.put("refundType", refundType);
		if (refundList.size()>0){
			request.put("refundList", refundList);
		}
		request.put("backUrl", backUrl);
		request.put("amount", amount);
		request.put("couponAmount", couponAmount);
		request.put("feeAmount", feeAmount);
		//request.put("extendInfo", extendInfo);
		try {
			String s = YunClient.request(request);
			return s;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 订单分账明细查询
	 * @param bizOrderNo  商户订单号（支付订单）
	 * @return
	 */
	public static String getOrderSplitRuleListDetail(String bizOrderNo) {
		getRequest();
		final YunRequest request = new YunRequest("OrderService", "getOrderSplitRuleListDetail");
		request.put("bizOrderNo", bizOrderNo);
		try {
			String s = YunClient.request(request);
			return s;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 解绑手机号
	 * @param bizUserId  商户系统用户标识，商户系统中唯一编号
	 * @param phone 手机号码
	 * @param verificationCode  手机验证码
	 * @return
	 */
	public static String unbindPhone(String bizUserId, String phone, String verificationCode) {
		getRequest();
		final YunRequest request = new YunRequest("MemberService", "unbindPhone");
		request.put("bizUserId", bizUserId);
		request.put("phone", phone);
		request.put("verificationCode", verificationCode);
		try {
			String s = YunClient.request(request);
			return s;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 绑定手机号
	 * @param bizUserId  商户系统用户标识，商户系统中唯一编号
	 * @param phone 手机号码
	 * @param verificationCode  手机验证码
	 * @return
	 */
	public static String bindPhone(String bizUserId, String phone, String verificationCode) {
		getRequest();
		final YunRequest request = new YunRequest("MemberService", "bindPhone");
		request.put("bizUserId", bizUserId);
		request.put("phone", phone);
		request.put("verificationCode", verificationCode);
		try {
			String s = YunClient.request(request);
			return s;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 发送短信验证码
	 * @param bizUserId  商户系统用户标识，商户系统中唯一编号
	 * @param phone 手机号码 原手机号
	 * @param verificationCodeType  验证码类型 9-绑定手机  6-解绑手机
	 * @return
	 */
	public static String sendVerificationCode(String bizUserId, String phone, Long verificationCodeType) {
		getRequest();
		final YunRequest request = new YunRequest("MemberService", "sendVerificationCode");
		request.put("bizUserId", bizUserId);
		request.put("phone", phone);
		request.put("verificationCodeType", verificationCodeType);
		try {
			String s = YunClient.request(request);
			return s;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 解锁会员
	 * @param bizUserId  商户系统用户标识，商户系统中唯一编号
	 * @return
	 */
	public static String unlockMember(String bizUserId) {
		getRequest();
		final YunRequest request = new YunRequest("MemberService", "unlockMember");
		request.put("bizUserId", bizUserId);
		try {
			String s = YunClient.request(request);
			return s;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 锁定会员
	 * @param bizUserId  商户系统用户标识，商户系统中唯一编号
	 * @return
	 */
	public static String lockMember(String bizUserId) {
		getRequest();
		final YunRequest request = new YunRequest("MemberService", "lockMember");
		request.put("bizUserId", bizUserId);
		try {
			String s = YunClient.request(request);
			return s;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 冻结金额
	 * @param bizUserId  商户系统用户标识，商户系统中唯一编号
	 * @param bizFreezenNo  商户冻结金额订单号
	 * @param accountSetNo  账户集编号  通商云分配的托管专用账户集的编号
	 * @param amount  冻结金额  单位：分
	 * @return
	 */
	public static String freezeMoney(String bizUserId,String bizFreezenNo,String accountSetNo,Long amount) {
		getRequest();
		final YunRequest request = new YunRequest("OrderService", "freezeMoney");
		request.put("bizUserId", bizUserId);
		request.put("bizFreezenNo", bizFreezenNo);
		request.put("accountSetNo", accountSetNo);
		request.put("amount", amount);
		try {
			String s = YunClient.request(request);
			return s;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 冻结金额
	 * @param bizUserId  商户系统用户标识，商户系统中唯一编号
	 * @param bizFreezenNo  商户冻结金额订单号
	 * @param accountSetNo  账户集编号  通商云分配的托管专用账户集的编号
	 * @param amount  冻结金额  单位：分
	 * @return
	 */
	public static String unfreezeMoney(String bizUserId,String bizFreezenNo,String accountSetNo,Long amount) {
		getRequest();
		final YunRequest request = new YunRequest("OrderService", "unfreezeMoney");
		request.put("bizUserId", bizUserId);
		request.put("bizFreezenNo", bizFreezenNo);
		request.put("accountSetNo", accountSetNo);
		request.put("amount", amount);
		try {
			String s = YunClient.request(request);
			return s;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 获取会员信息
	 * @param bizUserId  商户系统用户标识，商户系统中唯一编号
	 * @return
	 */
	public static String getMemberInfo(String bizUserId) {
		getRequest();
		final YunRequest request = new YunRequest("MemberService", "getMemberInfo");
		request.put("bizUserId", bizUserId);
		try {
			String s = YunClient.request(request);
			return s;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 查询余额
	 * @param bizUserId  商户系统用户标识，商户系统中唯一编号
	 * @param accountSetNo  账户集编号  通商云分配的托管专用账户 集的编号
	 * @return
	 */
	public static String queryBalance(String bizUserId,String accountSetNo) {
		getRequest();
		final YunRequest request = new YunRequest("OrderService", "queryBalance");
		request.put("bizUserId", bizUserId);
		request.put("accountSetNo", accountSetNo);
		try {
			String s = YunClient.request(request);
			return s;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 查询绑定银行卡
	 * @param bizUserId  商户系统用户标识，商户系统中唯一编号
	 * @param cardNo  银行卡号。如为空，则返回用户所有绑定银行卡。  RSA 加密  加密算法：RSA，将加密后的字节组转换为 16 进制字符串（大写）
	 * @return
	 */
	public static String queryBankCard(String bizUserId,String cardNo) {
		getRequest();
		final YunRequest request = new YunRequest("MemberService", "queryBankCard");
		request.put("bizUserId", bizUserId);
		request.put("cardNo", cardNo);
		try {
			String s = YunClient.request(request);
			return s;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 付款方资金代付明细查询(查询会员账户明细)
	 * @param bizUserId  商户系统用户标识，商户系统中唯一编号  收款人， 指定后仅返回指定收款人代付情况  否
	 * @param bizOrderNo  商户订单号  托管代收订单号
	 * @param accountSetNo  收款人的账户集编号。 通商云分配的托管专用账户集合的编号  否
	 * @param dateStart  开始日期 yyyy-MM-dd，托管代付时间   否
	 * @param dateEnd  结束日期  yyyy-MM-dd，托管代付时间   否
	 * @return
	 */
	public static String getPaymentInformationDetail(String bizUserId,String bizOrderNo,String accountSetNo,String dateStart,String dateEnd) {
		getRequest();
		final YunRequest request = new YunRequest("OrderService", "getPaymentInformationDetail");
		request.put("bizUserId", bizUserId);
		request.put("bizOrderNo", bizOrderNo);
		request.put("accountSetNo", accountSetNo);
		request.put("dateStart", dateStart);
		request.put("dateEnd", dateEnd);
		try {
			String s = YunClient.request(request);
			return s;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 复制map对象
	 * @explain 将paramsMap中的键值对全部拷贝到resultMap中；
	 * paramsMap中的内容不会影响到resultMap（深拷贝）
	 * @param paramsMap
	 *     被拷贝对象
	 * @param resultMap
	 *     拷贝后的对象
	 */
	public static void mapCopy(Map paramsMap, Map resultMap) {
		if (resultMap == null) resultMap = new HashMap();
		if (paramsMap == null) return;

		Iterator it = paramsMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Object key = entry.getKey();
			resultMap.put(key, paramsMap.get(key) != null ? paramsMap.get(key) : "");

		}
	}

}