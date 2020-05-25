package com.framework.loippi.controller.user;

import redis.clients.jedis.exceptions.JedisException;

import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.framework.loippi.controller.BaseController;
import com.framework.loippi.entity.activity.ActivityGuide;
import com.framework.loippi.entity.common.SceneActivity;
import com.framework.loippi.entity.common.VenueInfo;
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
import com.framework.loippi.service.activity.ActivityGuideService;
import com.framework.loippi.service.common.SceneActivityService;
import com.framework.loippi.service.common.VenueInfoService;
import com.framework.loippi.service.coupon.CouponService;
import com.framework.loippi.service.order.ShopOrderService;
import com.framework.loippi.service.product.ShopGoodsEvaluateSensitivityService;
import com.framework.loippi.service.user.MemberQualificationService;
import com.framework.loippi.service.user.OldSysRelationshipService;
import com.framework.loippi.service.user.RdMmAccountInfoService;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.service.user.RdMmRelationService;
import com.framework.loippi.service.user.RdRanksService;
import com.framework.loippi.service.user.RdSysPeriodService;
import com.framework.loippi.service.user.RetailProfitService;
import com.framework.loippi.utils.ApiUtils;
import com.framework.loippi.utils.Digests;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.PostUtil;
import com.framework.loippi.utils.SmsUtil;
import com.framework.loippi.utils.StringUtil;
import com.framework.loippi.utils.Xerror;
import com.google.code.kaptcha.Producer;

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
    private CouponService couponService;
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
    @Resource
    private MemberQualificationService memberQualificationService;
    @Resource
    private RetailProfitService retailProfitService;
    @Resource
    private ShopOrderService shopOrderService;
    @Resource
    private RdSysPeriodService periodService;
    @Resource
    private SceneActivityService sceneActivityService;
    @Resource
    private VenueInfoService venueInfoService;

    @Resource
    private Producer producer;

    @RequestMapping(value = "/sceneActivity/forword",method = RequestMethod.POST)
    public String sceneActivity(HttpServletRequest request,@RequestParam(value = "mCode",required = true) String mCode,
                                @RequestParam(value = "pwd",required = true)String pwd,HttpServletResponse response,@RequestParam(value = "venueUrl",required = true)String venueUrl) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        if(StringUtil.isEmpty(mCode)){
            return ApiUtils.error(Xerror.PARAM_INVALID, "抱歉!请用蜗米商城扫码领取!");
        }
        if(StringUtil.isEmpty(pwd)){
            return ApiUtils.error(Xerror.PARAM_INVALID, "抱歉!请用蜗米商城扫码领取!");
        }
        if(StringUtil.isEmpty(venueUrl)){
            return ApiUtils.error(Xerror.PARAM_INVALID, "抱歉!请用蜗米商城扫码领取!");
        }
        RdMmBasicInfo basicInfo = rdMmBasicInfoService.find("mmCode",mCode);
        if(basicInfo==null){
            return ApiUtils.error(Xerror.OBJECT_IS_NOT_EXIST, "用户不存在");
        }
        RdMmRelation  rdMmRelation = rdMmRelationService.find("mmCode", mCode);
        if(rdMmRelation==null||rdMmRelation.getLoginPwd()==null){
            return ApiUtils.error("当前账号信息异常");
        }
        if(!rdMmRelation.getLoginPwd().equals(pwd)){
            return ApiUtils.error(Xerror.LOGIN_PASSWORD_ERROR, "抱歉!请用蜗米商城扫码领取!");
        }
        //根据url查找会场信息
        VenueInfo venueInfo = venueInfoService.find("venueUrl",venueUrl);
        if(venueInfo==null){
            return ApiUtils.error("会场信息不存在");
        }
        if(venueInfo.getStartTime()==null||venueInfo.getEndTime()==null){
            return ApiUtils.error("会场活动领取及结束时间异常");
        }
        boolean boo = belongCalendar(new Date(), venueInfo.getStartTime(), venueInfo.getEndTime());
        if(!boo){
            return ApiUtils.error("当前时间不处于该会场正常活动时间内");
        }
        //查找是否领取过礼品
        List<SceneActivity> list = sceneActivityService.findList(Paramap.create().put("mCode", mCode).put("venueNum", venueInfo.getVenueNum()));
        if(list!=null&&list.size()>1){
            return ApiUtils.error("数据异常");
        }else if(list!=null&&list.size()==1){
            return ApiUtils.success(list.get(0));
        }else {
            SceneActivity activity = new SceneActivity();
            activity.setId(twiterIdService.getTwiterId());
            activity.setMCode(mCode);
            activity.setPresentStatus(0);
            activity.setMNickName(basicInfo.getMmNickName());
            activity.setImage(basicInfo.getMmAvatar());
            activity.setVenueId(venueInfo.getId());
            activity.setVenueNum(venueInfo.getVenueNum());
            sceneActivityService.save(activity);
            return ApiUtils.success(activity);
        }
    }

    @RequestMapping(value = "/sceneActivity/get",method = RequestMethod.POST)
    @ResponseBody
    public String getGiftQualification(HttpServletRequest request,@RequestParam(value = "mCode",required = true) String mCode,HttpServletResponse response
            ,@RequestParam(value = "venueNum",required = true)String venueNum) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        if(StringUtil.isEmpty(mCode)){
            return ApiUtils.error(Xerror.PARAM_INVALID, "抱歉!请用蜗米商城扫码领取!");
        }
        if(StringUtil.isEmpty(venueNum)){
            return ApiUtils.error(Xerror.PARAM_INVALID, "抱歉!请用蜗米商城扫码领取!");
        }
        RdMmBasicInfo basicInfo = rdMmBasicInfoService.find("mmCode",mCode);
        if(basicInfo==null){
            return ApiUtils.error(Xerror.OBJECT_IS_NOT_EXIST, "用户不存在");
        }
        VenueInfo venueInfo = venueInfoService.find("venueNum",venueNum);
        if(venueInfo==null){
            return ApiUtils.error("会场信息不存在");
        }
        if(venueInfo.getStartTime()==null||venueInfo.getEndTime()==null){
            return ApiUtils.error("会场活动领取及结束时间异常");
        }
        boolean boo = belongCalendar(new Date(), venueInfo.getStartTime(), venueInfo.getEndTime());
        if(!boo){
            return ApiUtils.error("当前时间不处于该会场正常活动时间内");
        }
        List<SceneActivity> list = sceneActivityService.findList(Paramap.create().put("mCode", mCode).put("venueNum",venueNum));
        if(list==null||list.size()!=1){
            return ApiUtils.error("数据异常");
        }
        SceneActivity sceneActivity = list.get(0);
        if(sceneActivity.getPresentStatus()!=0){
            return ApiUtils.error("请勿重复领取");
        }
        sceneActivity.setPresentStatus(1);
        sceneActivity.setGetime(new Date());
        sceneActivityService.update(sceneActivity);
        return ApiUtils.success(sceneActivity);
    }

    @RequestMapping(value = "/sceneActivity/use",method = RequestMethod.POST)
    @ResponseBody
    public String useGiftQualification(HttpServletRequest request,@RequestParam(value = "mCode",required = true) String mCode,HttpServletResponse response
            ,@RequestParam(value = "venueNum",required = true)String venueNum) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        if(StringUtil.isEmpty(mCode)){
            return ApiUtils.error(Xerror.PARAM_INVALID, "抱歉!请用蜗米商城扫码领取!");
        }
        if(StringUtil.isEmpty(venueNum)){
            return ApiUtils.error(Xerror.PARAM_INVALID, "抱歉!请用蜗米商城扫码领取!");
        }
        RdMmBasicInfo basicInfo = rdMmBasicInfoService.find("mmCode",mCode);
        if(basicInfo==null){
            return ApiUtils.error(Xerror.OBJECT_IS_NOT_EXIST, "用户不存在");
        }
        VenueInfo venueInfo = venueInfoService.find("venueNum",venueNum);
        if(venueInfo==null){
            return ApiUtils.error( "会场信息不存在");
        }
        if(venueInfo.getStartTime()==null||venueInfo.getEndTime()==null){
            return ApiUtils.error("会场活动领取及结束时间异常");
        }
        boolean boo = belongCalendar(new Date(), venueInfo.getStartTime(), venueInfo.getEndTime());
        if(!boo){
            return ApiUtils.error("当前时间不处于该会场正常活动时间内");
        }
        List<SceneActivity> list = sceneActivityService.findList(Paramap.create().put("mCode", mCode).put("venueNum",venueNum));
        if(list==null||list.size()!=1){
            return ApiUtils.error("数据异常");
        }
        SceneActivity sceneActivity = list.get(0);
        if(sceneActivity.getPresentStatus()!=1){
            return ApiUtils.error("该券已兑换");
        }
        sceneActivity.setPresentStatus(2);
        sceneActivity.setUseTime(new Date());
        sceneActivityService.update(sceneActivity);
        return ApiUtils.success(sceneActivity);
    }
    @RequestMapping(value = "/getNickName", method = RequestMethod.POST)
    @ResponseBody
    public String getNickName(HttpServletRequest request,String mmCode) {
        if(mmCode==null||"".equals(mmCode.trim())){
            return ApiUtils.error("请传入正确的会员编号");
        }
        RdMmBasicInfo basicInfo = rdMmBasicInfoService.find("mmCode", mmCode);
        if(basicInfo==null){
            return ApiUtils.error("该会员不存在");
        }
        if(basicInfo.getMmNickName()==null){
            return ApiUtils.error("会员信息异常");
        }
        return ApiUtils.success(basicInfo.getMmNickName());
    }
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
        return ApiUtils.success(handlerLoginNew1(member, request, rdMmRelation));
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
         * 加法验证码
         */
        @RequestMapping("/number")
        public void number(HttpServletRequest request ,HttpServletResponse response ,String mobile) throws Exception {
            response.setHeader("Cache-Control", "no-store, no-cache");
            response.setContentType("image/jpeg");

            try {
                //生成文字验证码
                String text = producer.createText();

                //个位数字相加
                String s1 = text.substring(0, 1);
                String s2 = text.substring(1, 2);
                int count = Integer.valueOf(s1).intValue() + Integer.valueOf(s2).intValue();

                System.out.println("******************************************");
                System.out.println("验证码计算后="+count);
                System.out.println("******************************************");

                //生成图片验证码
                BufferedImage image = producer.createImage(s1 + "+" + s2 + "=?");

                //保存 redis key 自己设置
                redisService.save("A"+mobile, count);

                //redisService.delete("A"+Mobile);

                ServletOutputStream out = response.getOutputStream();
                ImageIO.write(image, "jpg", out);
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }





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
        if(param.getName()!=null){
            evaluateSensitivityService.filterWords(param.getName());
        }
        RdMmBasicInfo rdMmBasicInfo = new RdMmBasicInfo();
        rdMmBasicInfo.setMobile(param.getMobile());
        if(param.getName()!=null){
            rdMmBasicInfo.setMmNickName(param.getName());
        }
        rdMmBasicInfo.setCreationIp(request.getRemoteAddr());
        List<RdMmBasicInfo> verificationMobile = rdMmBasicInfoService.findList(Paramap.create().put("mobile", param.getMobile()));
        if(verificationMobile!=null&&verificationMobile.size()>0){
            return ApiUtils.error(Xerror.OBJECT_IS_EXIST, "手机号码已经注册");
        }
        if(param.getName()!=null){
            List<RdMmBasicInfo> rdMmBasicInfoList = rdMmBasicInfoService
                    .findList(Paramap.create().put("mmNickName", param.getName()));
            if (rdMmBasicInfoList != null && rdMmBasicInfoList.size() > 0) {
                return ApiUtils.error(Xerror.OBJECT_IS_EXIST, "昵称已被占用");
            }
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
        try {
            rdMmBasicInfoService.addUser(rdMmBasicInfo, rdMmAccountInfo, rdMmRelation, param.getRegisterType());
            redisService.delete(param.getMobile());
            /*SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startTime = format.parse("2019-12-01 00:00:00");//TODO 活动时间预留
            Date endTime = format.parse("2019-12-31 23:59:59");
            Integer flag=0;
            if (new Date().getTime()>=startTime.getTime()&&new Date().getTime()<=endTime.getTime()){
                flag=1;
                //给当前注册用户以及当前注册用户推荐人发放优惠券
                couponService.givingCoupon(param.getMobile());
            }*/
            return ApiUtils.success(handlerLoginNew(rdMmBasicInfo, request, rdMmRelation,0));//TODO
        } catch (Exception e) {
            e.printStackTrace();
            redisService.delete(param.getMobile());
            return ApiUtils.error("网络异常，请稍后重试");
        }
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
     * 处理登录
     */
    private AuthsLoginResult handlerLoginNew1(RdMmBasicInfo member, HttpServletRequest request, RdMmRelation rdMmRelation) {
        String sessionId = twiterIdService.getSessionId();
        AuthsLoginResult authsLoginResult = new AuthsLoginResult();
        authsLoginResult.setSessionid(sessionId);
        RdRanks rdRanks = rdRanksService.find("rankId", rdMmRelation.getRank());
        authsLoginResult.setRankId(rdRanks.getRankId());
        authsLoginResult.setLookPpv(0);
        authsLoginResult.setLookVip(0);
        authsLoginResult.setPwd(rdMmRelation.getLoginPwd());
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
     * 处理登录
     */
    private AuthsLoginResult handlerLoginNew(RdMmBasicInfo member, HttpServletRequest request, RdMmRelation rdMmRelation,Integer flag) {
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
        /*authsLoginResult.setGetCouponFlag(flag);
        authsLoginResult.setImage("http://rdnmall.com/FslvpSUoQX8rR9hQF7rqmkMclRoV");
        authsLoginResult.setUrl("https://www.smzdm.com/");
        authsLoginResult.setTitle("注册就送优惠券");
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("page","couponsListpage");
        hashMap.put("couponId","6555008628095455332");
        String json = JacksonUtil.toJson(hashMap);
        authsLoginResult.setPath(json);*/
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
        rdMmBasicInfo.setMmAvatar(StringUtil.formatImg(prefix, "http://rdnmall.com/touxiang.jpg"));//会员头像
        rdMmBasicInfo.setCreationSource("1");
        rdMmBasicInfo.setIdCode(param.getMemberTrueId());
        rdMmBasicInfo.setIdType(param.getType());
        //rdMmBasicInfo.setMmName(param.getMemberTrueName());
        rdMmBasicInfo.setMmName(Optional.ofNullable(param.getName()).orElse(""));
        rdMmBasicInfo.setPushStatus(1);
        rdMmBasicInfo.setAllInPayPhoneStatus(0);
        rdMmBasicInfo.setAllInContractStatus(0);
        rdMmRelation.setARetail(BigDecimal.ZERO);
        rdMmRelation.setLoginPwd(Digests.entryptPassword(param.getPassword()));
        rdMmRelation.setSponsorCode(param.getInvitCode());
        rdMmRelation.setRaBindingDate(new Date());
        rdMmRelation.setIsVip(0);
        rdMmRelation.setMmPointStatus(2);
        rdMmRelation.setMmStatus(0);
        rdMmRelation.setPopupFlag(0);
        rdMmRelation.setLastPayTime(new Date());
        BigDecimal zero = BigDecimal.ZERO;
        rdMmAccountInfo.setBonusStatus(2);
        rdMmAccountInfo.setBonusBlance(zero);
        rdMmAccountInfo.setWalletStatus(2);
        rdMmAccountInfo.setWalletBlance(zero);
        rdMmAccountInfo.setRedemptionStatus(2);
        rdMmAccountInfo.setRedemptionBlance(zero);
        rdMmAccountInfo.setLastWithdrawalTime(new Date());//注册时生成最后一次提现时间为当前注册时间
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
        OldSysRelationship oldSysRelationship = oldSysRelationshipService.find("oMcode", oMCode.trim());
        //用户信息是否同步
        if (oldSysRelationship != null) {
            if (oldSysRelationship.getNYnRegistered() == 1) {
                return ApiUtils.error("老用户已经注册或者绑定");
            }
            try {
                resultString = PostUtil.postRequest(
                        "http://admin.fkcn.com/m/user/verify",
                        oMCode.trim(), password);
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
    @Resource
    private ActivityGuideService activityGuideService;

    @RequestMapping(value = "/getActivityGuide", method = RequestMethod.POST)
    @ResponseBody
    public String getActivityGuide() {
        List<ActivityGuide> list = activityGuideService.findAll();
        if (list!=null&&list.size()>0){
            for (ActivityGuide activityGuide : list) {
                if(activityGuide.getIsUse()!=null&&activityGuide.getIsUse()==1){
                    return ApiUtils.success(activityGuide);
                }
            }
        }
        return ApiUtils.error("当前无正在使用的活动指南");
    }

    public static boolean belongCalendar(Date nowTime, Date beginTime, Date endTime) {
        //设置当前时间
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);
        //设置开始时间
        Calendar begin = Calendar.getInstance();
        begin.setTime(beginTime);
        //设置结束时间
        Calendar end = Calendar.getInstance();
        end.setTime(endTime);
        //处于开始时间之后，和结束时间之前的判断
        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }
}
