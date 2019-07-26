package com.framework.loippi.controller.user;

import com.framework.loippi.utils.*;
import redis.clients.jedis.exceptions.JedisException;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.framework.loippi.controller.BaseController;
import com.framework.loippi.entity.user.OldSysRelationship;
import com.framework.loippi.entity.user.RaMember;
import com.framework.loippi.entity.user.RdMmAccountInfo;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.entity.user.RdRanks;
import com.framework.loippi.param.auths.AuthsRegisterParam;
import com.framework.loippi.param.auths.AuthsResetPasswordParam;
import com.framework.loippi.result.auths.AuthsLoginResult;
import com.framework.loippi.service.RedisService;
import com.framework.loippi.service.product.ShopGoodsEvaluateSensitivityService;
import com.framework.loippi.service.user.OldSysRelationshipService;
import com.framework.loippi.service.user.RdMmAccountInfoService;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.service.user.RdMmRelationService;
import com.framework.loippi.service.user.RdRanksService;

/**
 * Created by Administrator on 2017/6/18.
 */
@Controller
@ResponseBody
@RequestMapping("/api/auths")
public class AuthcAPIController extends BaseController {

    private static final String MEMBER_LOCK_MESSAGE = "该用户被禁用";
    private static final int MEMBER_LOCK_STATE = 0;

    @Resource
    private RedisService redisService;
    @Resource
    private RdRanksService rdRanksService;
    @Resource
    private RdMmAccountInfoService rdMmAccountInfoService;
    @Resource
    private ShopGoodsEvaluateSensitivityService evaluateSensitivityService;
    @Resource
    private RdMmBasicInfoService rdMmBasicInfoService;
    @Resource
    private RdMmRelationService rdMmRelationService;
    @Resource
    private OldSysRelationshipService oldSysRelationshipService;

//    @Resource
//    private HxService hxService;
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public String login(HttpServletRequest request) {
        String mobile = request.getParameter("mobile");
        String password = request.getParameter("password");
        if (mobile == null || "".equals(mobile) || password == null || "".equals(password)) {
            return ApiUtils.error(Xerror.PARAM_INVALID, "参数错误");
        }
        RdMmBasicInfo m = new RdMmBasicInfo();
        m.setMobile(mobile);
        //手机号或者会员编号都可以登录
        RdMmBasicInfo member = rdMmBasicInfoService.findMemberExist(m);
        RdMmRelation rdMmRelation = new RdMmRelation();
        if (member == null) {
            return ApiUtils.error(Xerror.OBJECT_IS_NOT_EXIST, "用户不存在");
        } else {
//            // 检查用户是否被禁用
//            if (member.getMemberState() != null && MEMBER_LOCK_STATE == member.getMemberState().intValue()) {
//                return ApiUtils.error(Xerror.LOGIN_ACCOUNT_DISABLED, MEMBER_LOCK_MESSAGE);
//            }
            rdMmRelation = rdMmRelationService.find("mmCode", member.getMmCode());
            //用户是否被冻结或者未激活
            //1冻结2注销
            if (Optional.ofNullable(rdMmRelation.getMmStatus()).orElse(0) == 1) {
                return ApiUtils.error(Xerror.LOGIN_ACCOUNT_DISABLED, "你的账号已被冻结");
            }
            //1冻结2注销
            if (Optional.ofNullable(rdMmRelation.getMmStatus()).orElse(0) == 2) {
                return ApiUtils.error(Xerror.LOGIN_ACCOUNT_DISABLED, "你的账号已被注销");
            }
            // 验证密码是否正确
            if (!Digests.validatePassword(password, rdMmRelation.getLoginPwd())) {
                return ApiUtils.error(Xerror.LOGIN_PASSWORD_ERROR, "密码错误");
            }
        }
        return ApiUtils.success(handlerLogin(member, request, rdMmRelation));
    }

    /**
     * 注册和忘记密码时验证验证码
     */
    @RequestMapping(value = "/validMsg", method = RequestMethod.POST)
    public String validMsg(String mobile, String code) {
        if (StringUtils.isBlank(mobile) || StringUtils.isBlank(code)) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        // 短信验证
        if (!valicodeIstrue(mobile, code)) {
            return ApiUtils.error(Xerror.VALID_CODE_ERROR);
        }
        return ApiUtils.success();
    }

    ///**
    // * 测试老会员找上级用的测试接口
    // */
    //@RequestMapping(value = "/aaa", method = RequestMethod.POST)
    //public String aaa(String spcode, String code) {
    //    Map<String, String> map = oldSysRelationshipService.findOldSysSpcode(spcode);
    //    return ApiUtils.success(map);
    //}





    /**
     * 用户注册
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(@Valid AuthsRegisterParam param, BindingResult vResult, HttpServletRequest request) {
        if (vResult.hasErrors()) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        if (param.getRegisterType() == 1 && (param.getInvitCode() == null || "".equals(param.getInvitCode()))) {
            return ApiUtils.error("邀请码不能为空");
        }

        if (!valicodeIstrue(param.getMobile(), param.getCode())) {
            return ApiUtils.error(Xerror.VALID_CODE_ERROR);
        }

        //昵称过滤敏感字
        evaluateSensitivityService.filterWords(param.getName());
        RdMmBasicInfo rdMmBasicInfo = new RdMmBasicInfo();
        rdMmBasicInfo.setMobile(param.getMobile());
        rdMmBasicInfo.setMmNickName(param.getName());
        rdMmBasicInfo.setCreationIp(request.getRemoteAddr());
        List<RdMmBasicInfo> rdMmBasicInfoList = rdMmBasicInfoService
            .findList(Paramap.create().put("verificationMobile", param.getMobile()).put("verificationNickName", param.getName()));
        if (rdMmBasicInfoList != null && rdMmBasicInfoList.size() > 0) {
            return ApiUtils.error(Xerror.OBJECT_IS_EXIST, "号码已注册或者昵称已被占用");
        }
        /**
         * 证件类型 1.身份证2.护照3.军官证4.回乡证
         */
        Integer type = param.getType();
        String openid = param.getOpenid();
        rdMmBasicInfo.setIdType(type);
        rdMmBasicInfo.setCreationSource("1");
        RdMmAccountInfo rdMmAccountInfo = new RdMmAccountInfo();
        RdMmRelation rdMmRelation = new RdMmRelation();
        initRdMmBasicInfo(rdMmBasicInfo, param, rdMmAccountInfo, rdMmRelation);//TODO
        rdMmBasicInfoService.addUser(rdMmBasicInfo, rdMmAccountInfo, rdMmRelation, param.getRegisterType());
        redisService.delete(param.getMobile());
        return ApiUtils.success(handlerLogin(rdMmBasicInfo, request, rdMmRelation));
    }

    /**
     * 忘记密码
     */
    @RequestMapping(value = "/resetPassword.json", method = RequestMethod.POST)
    public String AuthsResetPassword(@Valid AuthsResetPasswordParam param, HttpServletRequest request) {
        if (!SmsUtil.valicodeIstrue(param.getMobile(), param.getCode(), redisService)) {
            return ApiUtils.error(Xerror.VALID_CODE_ERROR);
        }
        RdMmBasicInfo m = new RdMmBasicInfo();
        m.setMobile(param.getMobile());
        //手机号或者会员编号都可以登录
        RdMmBasicInfo member = rdMmBasicInfoService.findMemberExist(m);
        if (member == null) {
            return ApiUtils.error(Xerror.OBJECT_IS_NOT_EXIST, "用户不存在");
        }
        RdMmRelation rdMmRelation = new RdMmRelation();
        rdMmRelation.setMmCode(member.getMmCode());
        rdMmRelation.setLoginPwd(Digests.entryptPassword(param.getPassword()));
        rdMmRelationService.update(rdMmRelation);
        redisService.delete(param.getMobile());
        return ApiUtils.success(handlerLogin(member, request, rdMmRelation));
    }

//    /**
//     * 这个第三方登录方式是否已绑定过账号
//     */
//    @RequestMapping(value = "/checkBind.json", method = RequestMethod.POST)
//    public String checkBind(Integer type, String openid, HttpServletRequest request) {
//        ShopMember member = null;
//        Map<String, Object> param = Maps.newHashMap();
//        param.put(SocialType.of(type), openid);
//        List<ShopMember> list = shopMemberService.findList(param);
//        if (list != null && list.size() != 0) {
//            member = list.get(0);
//        }
//        if (member != null) {
//            // 检查用户是否被禁用
//            if (member.getMemberState() != null && MEMBER_LOCK_STATE == member.getMemberState().intValue()) {
//                return ApiUtils.error(Xerror.LOGIN_ACCOUNT_DISABLED, MEMBER_LOCK_MESSAGE);
//            }
//            return ApiUtils.success(handlerLogin(member, request));
//        } else {
//            return ApiUtils.error(Xerror.OBJECT_IS_NOT_EXIST);
//        }
//    }

    /**
     * 处理登录
     */
    private AuthsLoginResult handlerLogin(RdMmBasicInfo member, HttpServletRequest request, RdMmRelation rdMmRelation) {
        String sessionId = twiterIdService.getSessionId();
        AuthsLoginResult authsLoginResult = new AuthsLoginResult();
        authsLoginResult.setSessionid(sessionId);
        RdRanks rdRanks = rdRanksService.find("rankId", rdMmRelation.getRank());
        authsLoginResult.setRankId(rdRanks.getRankId());
        authsLoginResult.setLookPpv(0);
        authsLoginResult.setLookVip(0);
        if (rdRanks != null) {
            if (Optional.ofNullable(rdRanks.getRankClass()).orElse(0) > 0) {
                authsLoginResult.setLookPpv(1);
                authsLoginResult.setLookVip(1);
            }
        }
        authsLoginResult=AuthsLoginResult.of(member, authsLoginResult, prefix);
        try {
            redisService.save(sessionId, authsLoginResult);
            redisService.save("user_name" + member.getMmCode(), sessionId);
        } catch (JedisException e) {
            throw new JedisException(e.getMessage());
        }

        return authsLoginResult;
    }

    /**
     * 注册时初始化会员参数
     */
    private void initRdMmBasicInfo(RdMmBasicInfo rdMmBasicInfo, AuthsRegisterParam param,
        RdMmAccountInfo rdMmAccountInfo, RdMmRelation rdMmRelation) {
        rdMmBasicInfo.setGender(0);//默认性别
        rdMmBasicInfo.setCreationDate(new Date());
        rdMmBasicInfo.setMmAvatar(StringUtil.formatImg(prefix, "/upload/img/avatar/01.jpg"));//会员头像
        rdMmBasicInfo.setCreationSource("1");
        rdMmBasicInfo.setIdCode(param.getMemberTrueId());
        rdMmBasicInfo.setIdType(param.getType());
        rdMmBasicInfo.setMmName(param.getMemberTrueName());
        rdMmBasicInfo.setPushStatus(1);
        rdMmRelation.setARetail(BigDecimal.ZERO);
        rdMmRelation.setLoginPwd(Digests.entryptPassword(param.getPassword()));
        rdMmRelation.setSponsorCode(param.getInvitCode());
        rdMmRelation.setRaBindingDate(new Date());
        rdMmRelation.setIsVip(0);
        rdMmRelation.setMmPointStatus(2);
        rdMmRelation.setMmStatus(0);
        BigDecimal zero = BigDecimal.ZERO;
        rdMmAccountInfo.setBonusStatus(2);
        rdMmAccountInfo.setBonusBlance(zero);
        rdMmAccountInfo.setWalletStatus(2);
        rdMmAccountInfo.setWalletBlance(zero);
        rdMmAccountInfo.setRedemptionStatus(2);
        rdMmAccountInfo.setRedemptionBlance(zero);
    }

    /**
     * 判断手机验证码是否正确
     */
    private boolean valicodeIstrue(String mobile, String code) {
        Object obj = redisService.get(mobile, Object.class);
        if (obj == null || !obj.toString().equals(code)) {
            return false;
        }

        return true;
    }


    @RequestMapping(value = "/verifyOldUser", method = RequestMethod.POST)
    public String verifyOldUser(HttpServletRequest request, String oMCode, String password) {
        String resultString = "";
        //验证正确,需要查询老用户信息
        OldSysRelationship oldSysRelationship = oldSysRelationshipService.find("oMcode", oMCode);
        //用户信息是否同步
        if (oldSysRelationship != null) {
            if (oldSysRelationship.getNYnRegistered() == 1) {
                return ApiUtils.error("老用户已经注册或者绑定");
            }
            try {
                resultString = PostUtil.postRequest(
                        "http://admin.fkcn.com/m/user/verify",
                        oMCode, password);
            } catch (Exception e) {
                e.printStackTrace();
                return ApiUtils.error("验证老用户发生错误");
            }
            //0：正确，1老系统会员编号不存在，2密码不正确，-1账号暂时锁定，10分钟后再试
            Map parse = (Map)JSON.parse(resultString);
            Object whetherCorrect = parse.get("whetherCorrect");
            String str = whetherCorrect+"";
            if ("".equals(str.trim())) {
                return ApiUtils.error("验证老用户发生错误");
            } else if ("0".equals(str.trim())) {
                //前期测试数据.后期前端不进行修改,后台进行修改
                String sessionId = twiterIdService.getSessionId();
                RaMember raMember = new RaMember();
                raMember.setMmCode(oldSysRelationship.getOMcode());
                raMember.setMmName(oldSysRelationship.getONickname());
                raMember.setOldSessionId(sessionId);
                //密码屏蔽处理
                raMember.setOPassword("");
                redisService.save(sessionId, raMember);
                return ApiUtils.success(raMember);
            } else if ("1".equals(str.trim())) {
                return ApiUtils.error("老系统会员编号不存在");
            } else if ("2".equals(str.trim())) {
                return ApiUtils.error("密码不正确");
            } else if ("3".equals(str.trim())) {
                return ApiUtils.error("会员不活跃");
            } else if ("-1".equals(str.trim())) {
                return ApiUtils.error("密码错误三次," +
                        "账号暂时锁定，10分钟后再试");
            } else {
                return ApiUtils.error("验证老用户发生错误");
            }

        } else {
            return ApiUtils.error("未找到该用户或该用户信息未同步，请核对信息，稍后再次尝试");
        }


    }

    //@RequestMapping(value = "/selectOldUser", method = RequestMethod.POST)
    //public String selectOldUser(HttpServletRequest request, String oMCode, String password) {
    //    List<RaMember> raMemberList=raMemberService.findList(Paramap.create().put("mmCode",oMCode).put("oPassword",password));
    //    //OldSysRelationship oldSysRelationship=new OldSysRelationship();
    //    RaMember raMember=new RaMember();
    //    if (raMemberList!=null && raMemberList.size()>0){
    //         raMember=raMemberList.get(0);
    //        //oldSysRelationship=oldSysRelationshipService.find("oMcode",raMember.getMmCode());
    //    }else{
    //        return ApiUtils.error("用户不存在");
    //    }
    //    String sessionId = twiterIdService.getSessionId();
    //    raMember.setOldSessionId(sessionId);
    //    //密码屏蔽处理
    //    raMember.setOPassword("");
    //    redisService.save(sessionId, raMember);
    //    return ApiUtils.success(raMember);
    //}
}
