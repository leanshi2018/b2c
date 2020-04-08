package com.framework.loippi.controller.allInPay;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.allinpay.yunst.sdk.YunClient;
import com.allinpay.yunst.sdk.bean.YunRequest;
import com.allinpay.yunst.sdk.util.RSAUtil;
import com.framework.loippi.consts.Constants;
import com.framework.loippi.controller.BaseController;
import com.framework.loippi.entity.user.RdMmAccountInfo;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.result.auths.AuthsLoginResult;
import com.framework.loippi.service.user.RdMmAccountInfoService;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.utils.ApiUtils;
import com.framework.loippi.utils.JacksonUtil;
import com.framework.loippi.utils.StringUtil;
import com.framework.loippi.utils.Xerror;


/**
 * 功能： api对接通联支付入口
 */
@Controller("allInPayController")
@ResponseBody
@RequestMapping("/api/allInPay")
@Slf4j
public class AllInPayController extends BaseController {
    @Resource
    private RdMmBasicInfoService rdMmBasicInfoService;
    @Resource
    private RdMmAccountInfoService rdMmAccountInfoService;

    /**
     * 向通联支付发送手机验证码
     * @param type 验证类型 6：解绑手机  9：绑定手机
     * @param request
     * @return
     */
    @RequestMapping(value = "/sendVerificationCode.json", method = RequestMethod.POST)
    public String sendVerificationCode(Long type, HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if(member==null){
            return ApiUtils.error(Xerror.USER_UNLOGIN_JSON_CODE);
        }
        String mmCode = member.getMmCode();
        if(mmCode==null){
            return ApiUtils.error("会员基础信息异常");
        }
        RdMmBasicInfo rdMmBasicInfo = rdMmBasicInfoService.find("mmCode", mmCode);
        if(rdMmBasicInfo==null){
            return ApiUtils.error("会员基础信息异常");
        }
        if (StringUtils.isEmpty(rdMmBasicInfo.getMobile())) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        if (!StringUtil.isMobilePhone(rdMmBasicInfo.getMobile())) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        if(type==null||(type!=6&&type!=9)){
            return ApiUtils.error("请提供正确的手机验证类型");
        }
        final YunRequest allInRequest = new YunRequest("MemberService", "sendVerificationCode");
        allInRequest.put("bizUserId", mmCode);
        allInRequest.put("phone", rdMmBasicInfo.getMobile());
        allInRequest.put("verificationCodeType", type);
        try {
            String s = YunClient.request(allInRequest);
            Map<String, Object> map = JacksonUtil.convertMap(s);
            if(map.get("status").equals("OK")){
                return ApiUtils.success("验证码已发送，请注意查收");
            }else if(map.get("status").equals("error")){
                String message = (String) map.get("message");
                return ApiUtils.error(message);
            } else {
                throw new RuntimeException("通联支付发送手机验证码异常");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiUtils.error("网络异常，请稍后重试");
    }

    /**
     * 绑定手机号
     * @param code 验证码
     * @param request
     * @return
     */
    @RequestMapping(value = "/badingAllInMobile.json", method = RequestMethod.POST)
    public String badingAllInMobile(String code, HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if(member==null){
            return ApiUtils.error(Xerror.USER_UNLOGIN_JSON_CODE);
        }
        String mmCode = member.getMmCode();
        if(mmCode==null){
            return ApiUtils.error("会员基础信息异常");
        }
        RdMmBasicInfo rdMmBasicInfo = rdMmBasicInfoService.find("mmCode",mmCode);
        if(rdMmBasicInfo==null){
            return ApiUtils.error("会员基础信息异常");
        }
        if (StringUtils.isEmpty(rdMmBasicInfo.getMobile())) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        if (!StringUtil.isMobilePhone(rdMmBasicInfo.getMobile())) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        if (StringUtils.isEmpty(code)) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        final YunRequest allInRequest = new YunRequest("MemberService", "bindPhone");
        allInRequest.put("bizUserId", mmCode);
        allInRequest.put("phone", rdMmBasicInfo.getMobile());
        allInRequest.put("verificationCode", code);
        try {
            String s = YunClient.request(allInRequest);
            Map<String, Object> map = JacksonUtil.convertMap(s);
            if(map.get("status").equals("OK")){
                rdMmBasicInfo.setAllInPayPhoneStatus(1);
                rdMmBasicInfoService.update(rdMmBasicInfo);
                return ApiUtils.success("手机号码绑定成功");
            }else if(map.get("status").equals("error")){
                String message = (String) map.get("message");
                return ApiUtils.error(message);
            } else {
                throw new RuntimeException("通联支付发送手机验证码异常");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiUtils.error("网络异常，请稍后重试");
    }

    /**
     * 实名制认证姓名
     * @param name 姓名
     * @param identityNo 身份证号码
     * @param request
     * @return
     */
    @RequestMapping(value = "/setRealName.json", method = RequestMethod.POST)
    public String setRealName(String name,String identityNo, HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        System.out.println(member);
        if(member==null){
            return ApiUtils.error(Xerror.USER_UNLOGIN_JSON_CODE);
        }
        String mmCode = member.getMmCode();
        if(mmCode==null){
            return ApiUtils.error("会员基础信息异常");
        }
        RdMmBasicInfo rdMmBasicInfo = rdMmBasicInfoService.find("mmCode", mmCode);
        if(rdMmBasicInfo==null){
            return ApiUtils.error("会员基础信息异常");
        }
        if (StringUtils.isEmpty(name)) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        if (StringUtils.isEmpty(identityNo)) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        try {
            String encrypt = RSAUtil.encrypt(identityNo);
            final YunRequest allInRequest = new YunRequest("MemberService", "setRealName");
            allInRequest.put("bizUserId", mmCode);
            allInRequest.put("isAuth",true);
            allInRequest.put("name", name);
            allInRequest.put("identityType", 1L);
            allInRequest.put("identityNo", encrypt);
            String s = YunClient.request(allInRequest);
            Map<String, Object> map = JacksonUtil.convertMap(s);
            if(map.get("status").equals("OK")){
                rdMmBasicInfo.setWhetherTrueName(1);
                rdMmBasicInfoService.update(rdMmBasicInfo);
                return ApiUtils.success("实名制认证成功");
            }else if(map.get("status").equals("error")){
                String message = (String) map.get("message");
                return ApiUtils.error(message);
            } else {
                throw new RuntimeException("实名制认证失败，请稍后重试");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiUtils.error("网络异常，请稍后重试");
    }

    /**
     * 关闭通联钱包自动提现
     * @param request
     * @return
     */
    @RequestMapping(value = "/close/signContract.json", method = RequestMethod.GET)
    public String closeSignContract(HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if(member==null){
            return ApiUtils.error(Xerror.USER_UNLOGIN_JSON_CODE);
        }
        String mmCode = member.getMmCode();
        if(mmCode==null){
            return ApiUtils.error("会员基础信息异常");
        }
        RdMmBasicInfo rdMmBasicInfo = rdMmBasicInfoService.find("mmCode", mmCode);
        if(rdMmBasicInfo==null){
            return ApiUtils.error("会员基础信息异常");
        }
        RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", mmCode);
        if(rdMmAccountInfo==null){
            return ApiUtils.error("会员积分账户异常");
        }
        rdMmBasicInfo.setAllInContractStatus(2);
        rdMmAccountInfo.setAutomaticWithdrawal(0);
        rdMmBasicInfoService.update(rdMmBasicInfo);
        rdMmAccountInfoService.update(rdMmAccountInfo);
        return ApiUtils.success("已为您关闭通联钱包自动提现功能");
    }

    /**
     * 申请进行通联网络签约，返回跳转页面链接（携带参数）
     * @param request
     * @return
     */
    @RequestMapping(value = "/signContract.json", method = RequestMethod.POST)
    public String signContract(HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if(member==null){
            return ApiUtils.error(Xerror.USER_UNLOGIN_JSON_CODE);
        }
        String mmCode = member.getMmCode();
        if(mmCode==null){
            return ApiUtils.error("会员基础信息异常");
        }
        RdMmBasicInfo rdMmBasicInfo = rdMmBasicInfoService.find("mmCode", mmCode);
        if(rdMmBasicInfo==null){
            return ApiUtils.error("会员基础信息异常");
        }
        if(rdMmBasicInfo.getAllInContractStatus()==1){
            return ApiUtils.error("当前会员已完成通联钱包自动提现签约");
        }
        if(rdMmBasicInfo.getAllInContractStatus()==2){
            rdMmBasicInfo.setAllInContractStatus(1);
            RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", rdMmBasicInfo.getMmCode());
            rdMmAccountInfo.setAutomaticWithdrawal(1);
            rdMmBasicInfoService.update(rdMmBasicInfo);
            rdMmAccountInfoService.update(rdMmAccountInfo);
            return ApiUtils.success("2","已为您重新开启通联钱包自动提现功能");
        }else {
            try {
                String webParamUrl = "http://116.228.64.55:6900/yungateway/member/signContract.html?";//测试环境签约地址
                //String webParamUrl = "https://fintech.allinpay.com/yungateway/member/signContract.html?";//正式环境签约地址
                final YunRequest allInRequest = new YunRequest("MemberService", "signContract");
                allInRequest.put("bizUserId", mmCode);
                allInRequest.put("jumpUrl","http://www.baidu.com");//TODO 预留签约成功后跳转前端页面
                allInRequest.put("backUrl","http://192.168.1.157:8085/api/allinpay/contract/signBack.json");//后台接收回调地址
                allInRequest.put("source", 1);//访问终端 1：mobile 2：PC
                String res = YunClient.encodeOnce(allInRequest);
                webParamUrl += res;
                return ApiUtils.success("1",webParamUrl);//返回完整签约地址 携带用户信息
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ApiUtils.error("网络异常，请稍后重试");
    }

    /**
     *  绑定银行卡
     * @param request
     * @param cardNo 卡号
     * @param name 姓名
     * @param identityNo 身份证号
     * @return
     */
    @RequestMapping(value = "/bindingBankCard.json", method = RequestMethod.POST)
    public String bindingBankCard(HttpServletRequest request,String cardNo,String name,String identityNo) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if(member==null){
            return ApiUtils.error(Xerror.USER_UNLOGIN_JSON_CODE);
        }
        String mmCode = member.getMmCode();
        if(mmCode==null){
            return ApiUtils.error("会员基础信息异常");
        }
        RdMmBasicInfo rdMmBasicInfo = rdMmBasicInfoService.find("mmCode", mmCode);
        if(rdMmBasicInfo==null){
            return ApiUtils.error("会员基础信息异常");
        }
        if (StringUtils.isEmpty(name)) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        if (StringUtils.isEmpty(identityNo)) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        if (StringUtils.isEmpty(cardNo)) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        try {
            String safeIdentityNo = RSAUtil.encrypt(identityNo.trim());
            String safeCardNo = RSAUtil.encrypt(cardNo.trim());
            final YunRequest allInRequest = new YunRequest("MemberService", "applyBindBankCard");
            allInRequest.put("bizUserId", mmCode);
            allInRequest.put("cardNo",safeCardNo);
            allInRequest.put("name", name.trim());
            allInRequest.put("cardCheck", 1L);
            allInRequest.put("identityType", 1L);
            allInRequest.put("identityNo", safeIdentityNo);
            String s = YunClient.request(allInRequest);
            Map<String, Object> map = JacksonUtil.convertMap(s);
            if(map.get("status").equals("OK")){
                String result = (String) map.get("signedValue");
                System.out.println(result);
                Map<String, Object> resultMap = JacksonUtil.convertMap(result);
                String bizUserId = (String) resultMap.get("bizUserId");
                System.out.println(bizUserId);
                String tranceNum = (String) resultMap.get("tranceNum");
                System.out.println(tranceNum);
                String transDate = (String) resultMap.get("transDate");
                System.out.println(transDate);
                String bankName = (String) resultMap.get("bankName");
                System.out.println(bankName);
                String bankCode = (String) resultMap.get("bankCode");
                System.out.println(bankCode);
                Integer cardType = (Integer) resultMap.get("cardType");
                System.out.println(cardType);
            }else if(map.get("status").equals("error")){
                String message = (String) map.get("message");
                return ApiUtils.error(message);
            } else {
                throw new RuntimeException("实名制认证失败，请稍后重试");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiUtils.error("网络异常，请稍后重试");
    }

}
