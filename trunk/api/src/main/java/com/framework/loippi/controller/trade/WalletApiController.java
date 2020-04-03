//package com.framework.loippi.controller.trade;
//
//import com.framework.loippi.consts.PaymentTallyState;
//import com.framework.loippi.controller.AppConstants;
//import com.framework.loippi.controller.BaseController;
//import com.framework.loippi.entity.PayCommon;
//import com.framework.loippi.entity.user.ShopMember;
//import com.framework.loippi.entity.walet.ShopWalletCash;
//import com.framework.loippi.entity.walet.ShopWalletRecharge;
//
//import com.framework.loippi.result.auths.AuthsLoginResult;
//import com.framework.loippi.result.user.WithDrawForm;
//import com.framework.loippi.service.alipay.AlipayMobileService;
//import com.framework.loippi.service.union.UnionpayService;
//import com.framework.loippi.service.wechat.WechatMobileService;
//
//import com.framework.loippi.service.trade.ShopMemberPaymentTallyService;
//import com.framework.loippi.service.user.ShopMemberService;
//import com.framework.loippi.service.wallet.ShopWalletCashService;
//import com.framework.loippi.service.wallet.ShopWalletRechargeService;
//import com.framework.loippi.util.ApiUtils;
//import com.framework.loippi.util.Constants;
//import com.framework.loippi.util.Dateutil;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//import java.math.BigDecimal;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created by longbh on 2017/8/24.
// */
//@Controller("apiWalletApiController")
//@Slf4j
//@RequestMapping("/api/wallet")
//public class WalletApiController extends BaseController {
//
//    @Resource
//    private ShopWalletCashService shopWalletCashService;
//    @Resource
//    private ShopWalletRechargeService shopWalletRechargeService;
//    @Resource
//    private ShopMemberService shopMemberService;
//    @Resource
//    private AlipayMobileService alipayMobileService;
//    @Resource
//    private UnionpayService unionpayService;
//    @Resource
//    private WechatMobileService wechatMobileService;
//    @Resource
//    private ShopMemberPaymentTallyService paymentTallyService;
//
//    /**
//     * 钱包余额个人信息
//     *
//     * @param request
//     * @return
//     */
//    @ResponseBody
//    @RequestMapping(value = "detail", method = RequestMethod.POST)
//    public String detail(HttpServletRequest request) {
//        AuthsLoginResult loginUser = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
//        ShopMember wallet = shopMemberService.find(loginUser.getUserId());
//        return ApiUtils.success(wallet.getAvailablePredeposit());
//    }
//
//    /**
//     * 充值
//     *
//     * @param request
//     * @return
//     */
//    @ResponseBody
//    @RequestMapping(value = "excharges", method = RequestMethod.POST)
//    public String excharge(HttpServletRequest request, @RequestParam("paymentCode") String paymentCode, BigDecimal pdrAmount) {
//        AuthsLoginResult loginUser = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
//        ShopWalletRecharge shopWalletRecharge = new ShopWalletRecharge();
//        shopWalletRecharge.setId(twiterIdService.getTwiterId());
//        shopWalletRecharge.setCreateTime(new Date());
//        shopWalletRecharge.setPdrPaymentCode(paymentCode);
//        shopWalletRecharge.setPdrMemberId(loginUser.getUserId());
//        shopWalletRecharge.setPdrAmount(pdrAmount);
//        shopWalletRecharge.setPdrPaymentState("0");
//        shopWalletRecharge.setPdrMemberName(loginUser.getNickname());
//        String paySn = "R" + Dateutil.getDateString();
//        shopWalletRecharge.setPdrSn(paySn);
//        shopWalletRechargeService.save(shopWalletRecharge);
//        PayCommon payCommon = new PayCommon();
//        payCommon.setOutTradeNo(shopWalletRecharge.getPdrSn());
//        payCommon.setTitle("余额充值");
//        payCommon.setBody(shopWalletRecharge.getPdrSn() + "余额充值");
//        payCommon.setNotifyUrl(server + "api/paynotify/notifyMobile/" + paymentCode + "/" + shopWalletRecharge.getPdrSn() + ".json");
//        payCommon.setReturnUrl(server + "payment/payfront");
//        String sHtmlText = "";
//        Map<String, Object> model = new HashMap<String, Object>();
//        if (paymentCode.equals("alipayMobilePaymentPlugin")) {
//            //保存支付流水记录
//            System.out.println("dd:" + PaymentTallyState.PAYMENTTALLY_TREM_PC);
//            paymentTallyService.savePaymentTally(paymentCode, "支付宝", shopWalletRecharge, PaymentTallyState.PAYMENTTALLY_TREM_MB);
//            //修改订单付款信息
//            sHtmlText = alipayMobileService.toPay(payCommon);
//            model.put("tocodeurl", sHtmlText);
//            return ApiUtils.success(model);
//        } else if (paymentCode.equals("unionpayMobilePaymentPlugin")) {
//            //保存支付流水记录
//            paymentTallyService.savePaymentTally(paymentCode, "银联", shopWalletRecharge, PaymentTallyState.PAYMENTTALLY_TREM_MB);
//            sHtmlText = unionpayService.prePay(payCommon, request);//构造提交银联的表单
//            model.put("tocodeurl", sHtmlText);
//            return ApiUtils.success(model);
//        } else if (paymentCode.equals("weixinMobilePaymentPlugin")) {
//            //保存支付流水记录
//            paymentTallyService.savePaymentTally(paymentCode, "微信支付", shopWalletRecharge, PaymentTallyState.PAYMENTTALLY_TREM_MB);
//            String tocodeurl = wechatMobileService.toPay(payCommon);//微信扫码url
//            model.put("paysn", shopWalletRecharge.getPdrSn());//订单号
//            model.put("tocodeurl", tocodeurl);
//            model.put("payCommon", payCommon);//支付单
//            //model.put("url", "/weiscan/native_weichatscan");
//            return ApiUtils.success(model);
//        }
//        return ApiUtils.error("该支付方式不支持");
//    }
//
//    /**
//     * 提现
//     *
//     * @param request
//     * @param form
//     * @return
//     */
//    @ResponseBody
//    @RequestMapping(value = "withdraws", method = RequestMethod.POST)
//    public String withdraw(HttpServletRequest request, WithDrawForm form) {
//        AuthsLoginResult loginUser = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
//        //检查当前用户是否有提现还未完成
//        Map<String, Object> params = new HashMap<>();
//        params.put("pdcPaymentState", "0");
//        params.put("pdcMemberId", loginUser.getUserId());
//        if (shopWalletCashService.count(params) > 0) {
//            return ApiUtils.error("您有提现尚在审核中,请等待");
//        }
//
//        ShopMember wallet = shopMemberService.find(loginUser.getUserId());
//        if (wallet.getAvailablePredeposit().compareTo(new BigDecimal(form.getAmount())) < 0) {
//            return ApiUtils.error(AppConstants.NOT_SOMUTCH);
//        }
//        String paySn = "D" + Dateutil.getDateString();
//        ShopWalletCash walWithdraw = new ShopWalletCash();
//        walWithdraw.setId(twiterIdService.getTwiterId());
//        walWithdraw.setCreateTime(new Date());
//        walWithdraw.setPdcAmount(new BigDecimal(form.getAmount()).multiply(new BigDecimal(-1)));
//        walWithdraw.setPdcBankName(form.getBank());
//        walWithdraw.setPdcBankUser(form.getAccount());
//        walWithdraw.setPdcBankNo(form.getCardNo());
//        walWithdraw.setPdcMemberId(loginUser.getUserId());
//        walWithdraw.setPdcMemberName(loginUser.getNickname());
//        walWithdraw.setPdcSn(paySn);
//        walWithdraw.setPdcPaymentState("0");
//
//        //马上扣钱
//        shopWalletCashService.saveWallet(walWithdraw, wallet);
//        return ApiUtils.success();
//    }
//
//}
