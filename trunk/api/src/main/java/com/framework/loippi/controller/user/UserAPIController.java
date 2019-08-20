package com.framework.loippi.controller.user;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.framework.loippi.consts.UpdateMemberInfoStatus;
import com.framework.loippi.controller.BaseController;
import com.framework.loippi.entity.common.ShopApp;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsBrowse;
import com.framework.loippi.entity.user.MemberQualification;
import com.framework.loippi.entity.user.OldSysRelationship;
import com.framework.loippi.entity.user.RdBonusMaster;
import com.framework.loippi.entity.user.RdMmAccountInfo;
import com.framework.loippi.entity.user.RdMmBank;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.entity.user.RdRanks;
import com.framework.loippi.entity.user.RdSysPeriod;
import com.framework.loippi.entity.user.ShopMemberFavorites;
import com.framework.loippi.enus.SocialType;
import com.framework.loippi.enus.UserLoginType;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.param.user.UserAddBankCardsParam;
import com.framework.loippi.result.auths.AuthsLoginResult;
import com.framework.loippi.result.user.BankCardsListResult;
import com.framework.loippi.result.user.PersonCenterResult;
import com.framework.loippi.result.user.SelfPerformanceResult;
import com.framework.loippi.result.user.SubordinateUserInformationResult;
import com.framework.loippi.result.user.UserCollectResult;
import com.framework.loippi.result.user.UserFootprintsResult;
import com.framework.loippi.result.user.UserProfileResult;
import com.framework.loippi.service.RedisService;
import com.framework.loippi.service.common.ShopAppService;
import com.framework.loippi.service.common.ShopCommonAreaService;
import com.framework.loippi.service.order.ShopOrderService;
import com.framework.loippi.service.product.ShopGoodsBrowseService;
import com.framework.loippi.service.product.ShopGoodsEvaluateSensitivityService;
import com.framework.loippi.service.product.ShopGoodsEvaluateService;
import com.framework.loippi.service.product.ShopGoodsService;
import com.framework.loippi.service.user.MemberQualificationService;
import com.framework.loippi.service.user.OldSysRelationshipService;
import com.framework.loippi.service.user.RdBonusMasterService;
import com.framework.loippi.service.user.RdMmAccountInfoService;
import com.framework.loippi.service.user.RdMmBankService;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.service.user.RdMmRelationService;
import com.framework.loippi.service.user.RdRaBindingService;
import com.framework.loippi.service.user.RdRanksService;
import com.framework.loippi.service.user.RdSysPeriodService;
import com.framework.loippi.service.user.RetailProfitService;
import com.framework.loippi.service.user.ShopMemberFavoritesService;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.ApiUtils;
import com.framework.loippi.utils.BankCardUtils;
import com.framework.loippi.utils.Constants;
import com.framework.loippi.utils.Dateutil;
import com.framework.loippi.utils.Digests;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.PostUtil;
import com.framework.loippi.utils.SmsUtil;
import com.framework.loippi.utils.StringUtil;
import com.framework.loippi.utils.Xerror;
import com.framework.loippi.vo.address.MemberAddresVo;
import com.framework.loippi.vo.order.CountOrderStatusVo;
import com.framework.loippi.vo.order.OrderSumPpv;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.ibm.icu.text.SimpleDateFormat;


@Controller
@ResponseBody
@RequestMapping("/api/user")
public class UserAPIController extends BaseController {

    @Resource
    private ShopMemberFavoritesService shopMemberFavoritesService;
    @Resource
    private ShopGoodsService shopGoodsService;
    @Resource
    private RedisService redisService;
    @Resource
    private ShopOrderService shopOrderService;
    @Resource
    private ShopGoodsBrowseService shopGoodsBrowseService;
    @Resource
    private ShopGoodsEvaluateService shopGoodsEvaluateService;
    @Resource
    private RdMmBankService rdMmBankService;
    @Resource
    private ShopCommonAreaService shopCommonAreaService;
    @Resource
    private ShopAppService shopAppService;
    @Resource
    private RdMmBasicInfoService rdMmBasicInfoService;
    @Resource
    private RdMmRelationService rdMmRelationService;
    @Resource
    private RdRanksService rdRanksService;
    @Resource
    private RdMmAccountInfoService rdMmAccountInfoService;
    @Resource
    private OldSysRelationshipService oldSysRelationshipService;
    @Resource
    private RdRaBindingService rdRaBindingService;
    @Resource
    private ShopGoodsEvaluateSensitivityService evaluateSensitivityService;
    @Resource
    private RdSysPeriodService periodService;
    @Resource
    private RdMmBasicInfoService mmBasicInfoService;
    @Resource
    private MemberQualificationService qualificationService;
    @Resource
    private RetailProfitService retailProfitService;
    @Resource
    private RdBonusMasterService rdBonusMasterService;
    @Resource
    private MemberQualificationService memberQualificationService;
    @Value("#{properties['wap.server']}")
    private String wapServer;

    /**
     * 个人中心
     */
    @RequestMapping("/personCenter")
    @ResponseBody
    public String personCenter(HttpServletRequest request) {
        // 获取缓存实体
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        //用户未登录或者登录失效
        if (member == null) {
            PersonCenterResult result = new PersonCenterResult();
            return ApiUtils.success(result);
        }
        RdMmBasicInfo shopMember = rdMmBasicInfoService.find("mmCode", member.getMmCode());
        RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", member.getMmCode());
        RdRanks rdRanks = rdRanksService.find("rankId", rdMmRelation.getRank());
        PersonCenterResult result = PersonCenterResult.build(shopMember, rdRanks);
        //各种状态订单数量信息
        List<CountOrderStatusVo> countOrderStatusVoList = shopOrderService
            .countOrderStatus(Paramap.create().put("buyerId", shopMember.getMmCode()).put("isDel",0));
        //心愿单数量
        Long favoritesNum = shopMemberFavoritesService
            .count(Paramap.create().put("memberId", Long.parseLong(shopMember.getMmCode())));
        result.setFavoritesNum(Integer.parseInt(favoritesNum + ""));
        //足迹数量
        Long browseNum = shopGoodsBrowseService
            .count(Paramap.create().put("browseMemberId", Long.parseLong(shopMember.getMmCode())));
        result.setBrowseNum(Integer.parseInt(browseNum + ""));

        //当周期
        RdSysPeriod sysPeriod = periodService.getPeriodService(new Date());
        String period = "";
        String endDate = "";
        BigDecimal periodMi = new BigDecimal("0.00");//本期已达成MI
        if (sysPeriod!=null){
            period = sysPeriod.getPeriodCode();
            Date eDate = sysPeriod.getEndDate();
            SimpleDateFormat starForm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            endDate = starForm.format(eDate);
            //本期的已达成MI
            periodMi = shopOrderService.countOrderPPVByMCodeAndPeriod(member.getMmCode(), period);
            if (periodMi==null||"".equals(periodMi)){
                periodMi = new BigDecimal("0.00");
            }else {
                periodMi = periodMi.setScale(2);//保留两位小数
            }
        }else{
            period = "业务期未开始";
            endDate = "业务期未开始";
        }


        result.setPeriod(period);
        result.setEndDate(endDate);
        result.setPeriodMi(periodMi);

        result = PersonCenterResult.build2(result, countOrderStatusVoList);
        //是否设置登录密码
        RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", member.getMmCode());
        if (StringUtil.isEmpty(rdMmAccountInfo.getPaymentPwd())) {
            result.setIsPaymentPasswd(0);
        } else {
            result.setIsPaymentPasswd(1);
        }
        return ApiUtils.success(result);
    }

    @RequestMapping(value = "/binding.json", method = RequestMethod.POST)
    public String binding(HttpServletRequest request, String oMCode, String password) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if(member==null){
            return ApiUtils.error("请登录后再进行老系统会员绑定操作");
        }
        String mmCode = member.getMmCode();
        RdMmRelation mmRelation = rdMmRelationService.find("mmCode", mmCode);
        if(mmRelation==null){
            return ApiUtils.error("当前登录用户信息异常");
        }
        if(mmRelation.getNOFlag()==2){
            return ApiUtils.error("当前登录会员已经是老系统会员");
        }
        if (StringUtils.isEmpty(oMCode)) {
            return ApiUtils.error("老系统会员编号为空");
        }
        if (StringUtils.isEmpty(password)) {
            return ApiUtils.error("老系统会员二级密码为空");
        }
        //1.根据会员提供老系统会员编号查询中间表中该会员是否已经注册
        OldSysRelationship oldSysRelationship = oldSysRelationshipService.find("oMcode", oMCode.trim());
        if(oldSysRelationship==null){
            return ApiUtils.error("将要进行绑定的老系统会员不存在或尚未在新系统同步");
        }
        if(oldSysRelationship.getNYnRegistered()==1){
            return ApiUtils.error("提供的老系统会员编号已经在新系统进行注册，不能进行绑定");
        }
        //2.如果提供老系统会员在中间表存在且未注册，验证需要绑定会员的二级密码(确认是会员本人进行绑定)
        String resultString="";
        try {
            resultString = PostUtil.postRequest(
                    "http://admin.fkcn.com/m/user/verify",
                    oMCode.trim(), password);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiUtils.error("验证老用户发生错误");
        }
        //   0：正确，1老系统会员编号不存在，2密码不正确，-1账号暂时锁定，10分钟后再试
        Map parse = (Map) JSON.parse(resultString);
        Object whetherCorrect = parse.get("whetherCorrect");
        String str = whetherCorrect+"";
        if ("".equals(str.trim())) {
            return ApiUtils.error("验证老用户发生错误");
        } else if ("0".equals(str.trim())) {
            RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", member.getMmCode());
            Integer raSponsorStatus = rdMmRelation.getRaSponsorStatus();
            if(raSponsorStatus==1){//如果需要修改会员在新系统绑定状态为永久状态，
                OldSysRelationship oldSysRelationship1 = oldSysRelationshipService.find("nMcode", rdMmRelation.getSponsorCode());
                if (oldSysRelationship1==null){//说明中间表没有其推荐人，即永久推荐人和需要绑定老系统会员对应不上
                    return ApiUtils.error("新系统会员直接推荐人无法与将要绑定老系统直接推荐人匹配，不予进行老系统会员绑定");
                }
                String oSpcode = oldSysRelationship.getOSpcode();//将要绑定老会员的推荐人
                String oMcode = oldSysRelationship1.getOMcode();//新系统会员目前绑定的永久推荐人
                if(!oSpcode.equals(oMcode)){
                    return ApiUtils.error("新系统会员直接推荐人无法与将要绑定老系统直接推荐人匹配，不予进行老系统会员绑定");
                }
                //3.需要绑定的老系统会员和新系统中间表中直接推荐人是同一个人，可以进行绑定
                try {
                    rdMmRelationService.badingAndUpgrade(rdMmRelation,oldSysRelationship);
                    return ApiUtils.success("老系统会员绑定成功");
                } catch (Exception e) {
                    e.printStackTrace();
                    return ApiUtils.success("网络异常，请稍后重试");
                }
            }
            if(raSponsorStatus==0){//如果需要修改会员在新系统绑定状态为临时状态，需要查询中间表中推荐人是否注册，如果注册，将关系表中推荐人关系进行修改
                OldSysRelationship oldSysRelationship2 = oldSysRelationshipService.find("oMcode", oldSysRelationship.getOSpcode());
                if(oldSysRelationship2.getNYnRegistered()==1){
                    RdMmBasicInfo basicInfo = rdMmBasicInfoService.find("mmCode", oldSysRelationship2.getNMcode());
                    try {
                        rdMmRelationService.badingAndUpgrade2(rdMmRelation,oldSysRelationship,basicInfo);
                        return ApiUtils.success("老系统会员绑定成功");
                    } catch (Exception e) {
                        e.printStackTrace();
                        return ApiUtils.success("网络异常，请稍后重试");
                    }
                }
                try {
                    rdMmRelationService.badingAndUpgrade(rdMmRelation,oldSysRelationship);
                    return ApiUtils.success("老系统会员绑定成功");
                } catch (Exception e) {
                    e.printStackTrace();
                    return ApiUtils.success("网络异常，请稍后重试");
                }
            }
            return ApiUtils.error("会员绑定状态错误");
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
    }

    /**
     * 个人资料
     */
    @RequestMapping(value = "/profile.json", method = RequestMethod.POST)
    public String UserProfile(HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        RdMmBasicInfo rdMmBasicInfo = rdMmBasicInfoService.find("mmCode", member.getMmCode());
        OldSysRelationship oldSysRelationship = oldSysRelationshipService.find("nMcode",member.getMmCode());
        //RdRaBinding rdRaBinding=rdRaBindingService.find("rdCode",member.getMmCode());
        return ApiUtils.success(UserProfileResult.build3(rdMmBasicInfo,oldSysRelationship));
    }

    /**
     * 修改姓名
     */
    @RequestMapping(value = "/updateName.json", method = RequestMethod.POST)
    public String updateName(HttpServletRequest request, String name) {
        if (StringUtil.isEmpty(name)) {
            return ApiUtils.error(Xerror.PARAM_INVALID, "参数无效");
        }
        AuthsLoginResult session = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        RdMmBasicInfo member = rdMmBasicInfoService.find("mmCode", session.getMmCode());
        member.setMmName(name);
        rdMmBasicInfoService.updateMember(member, UpdateMemberInfoStatus.UPDATE_NAME);
        redisService.save(session.getSessionid(), AuthsLoginResult.of(member, session, prefix));
        return ApiUtils.success();
    }

    /**
     * 修改头像
     */
    @RequestMapping(value = "/updateAvator.json", method = RequestMethod.POST)
    public String updateAvator(HttpServletRequest request, String avatar) {
        if (StringUtil.isEmpty(avatar)) {
            return ApiUtils.error(Xerror.PARAM_INVALID, "参数无效");
        }
        AuthsLoginResult session = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        RdMmBasicInfo member = rdMmBasicInfoService.find("mmCode", session.getMmCode());
        member.setMmAvatar(avatar);
        rdMmBasicInfoService.updateMember(member, UpdateMemberInfoStatus.UPDATE_AVATAR);
        redisService.save(session.getSessionid(), AuthsLoginResult.of(member, session, prefix));
        return ApiUtils.success();
    }

    /**
     * 修改推送状态
     */
    @RequestMapping(value = "/updatePush.json", method = RequestMethod.POST)
    public String updatePush(HttpServletRequest request, Integer pushStatus) {
        if (StringUtil.isEmpty(pushStatus + "")) {
            return ApiUtils.error(Xerror.PARAM_INVALID, "参数无效");
        }
        AuthsLoginResult session = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        RdMmBasicInfo member = rdMmBasicInfoService.find("mmCode", session.getMmCode());
        member.setPushStatus(pushStatus);
        rdMmBasicInfoService.updateMember(member, UpdateMemberInfoStatus.UPDATE_PUSHSTATUS);
        redisService.save(session.getSessionid(), AuthsLoginResult.of(member, session, prefix));
        return ApiUtils.success();
    }


    /**
     * 修改密码时验证当前密码
     */
    @RequestMapping(value = "/validPassword.json", method = RequestMethod.POST)
    public String validPassword(HttpServletRequest request, String password) {
        if (StringUtils.isBlank(password)) {
            return ApiUtils.error(Xerror.PARAM_INVALID, "参数无效");
        }
        AuthsLoginResult session = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", session.getMmCode());
        if (!Digests.validatePassword(password, rdMmRelation.getLoginPwd())) {
            return ApiUtils.error("当前密码输入错误");
        }
        return ApiUtils.success();
    }


    /**
     * 修改昵称
     */
    @RequestMapping(value = "/updateNickname.json", method = RequestMethod.POST)
    public String updateNickname(HttpServletRequest request, String nickName) {
        if (StringUtils.isBlank(nickName)) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        //昵称过滤敏感字
        evaluateSensitivityService.filterWords(nickName);
        AuthsLoginResult session = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        RdMmBasicInfo shopMember = rdMmBasicInfoService.find("mmNickName", nickName);
        if (shopMember != null) {
            return ApiUtils.error("此昵称已存在");
        }
        RdMmBasicInfo member = rdMmBasicInfoService.find("mmCode", session.getMmCode());
        member.setMmNickName(nickName);
        rdMmBasicInfoService.updateMember(member, UpdateMemberInfoStatus.UPDATE_NICKNAME);
        redisService.save(session.getSessionid(), AuthsLoginResult.of(member, session, prefix));
        return ApiUtils.success();
    }

    /**
     * 修改性别
     */
    @RequestMapping(value = "/updateSex.json", method = RequestMethod.POST)
    public String updateSex(HttpServletRequest request, Integer sex) {
        if (sex == null || (sex != 1 && sex != 0)) {
            return ApiUtils.error("性别只能为男或者女");
        }
        AuthsLoginResult session = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        RdMmBasicInfo member = rdMmBasicInfoService.find("mmCode", session.getMmCode());
        member.setGender(sex);
        rdMmBasicInfoService.updateMember(member, UpdateMemberInfoStatus.UPDATE_SEX);
        redisService.save(session.getSessionid(), AuthsLoginResult.of(member, session, prefix));
        return ApiUtils.success();
    }

    /**
     * 修改生日
     */
    @RequestMapping(value = "/updateBirthday.json", method = RequestMethod.POST)
    public String updateBirthday(HttpServletRequest request, String birthday) {
        if (StringUtils.isBlank(birthday)) {
            return ApiUtils.error(Xerror.PARAM_INVALID, "参数无效");
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date birthdayDate;
        try {
            birthdayDate = format.parse(birthday);
        } catch (Exception e) {
            return ApiUtils.error("格式错误，正确格式：yyyy-MM-dd");
        }
        AuthsLoginResult session = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        RdMmBasicInfo member = rdMmBasicInfoService.find("mmCode", session.getMmCode());
        member.setBirthdate(birthdayDate);
        rdMmBasicInfoService.updateMember(member, UpdateMemberInfoStatus.UPDATE_BIRTHDAY);
        redisService.save(session.getSessionid(), AuthsLoginResult.of(member, session, prefix));
        return ApiUtils.success();
    }

    /**
     * 个人资料中修改地区信息
     */
    @RequestMapping(value = "/updateAreainfo.json", method = RequestMethod.POST)
    public String updateAreainfo(HttpServletRequest request, Long areaId) {
        if (areaId == null) {
            return ApiUtils.error(Xerror.PARAM_INVALID, "参数无效");
        }
        StringBuffer areaInfo = new StringBuffer();
        AuthsLoginResult session = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        RdMmBasicInfo member = rdMmBasicInfoService.find("mmCode", session.getMmCode());
        MemberAddresVo memberAddresVo = shopCommonAreaService.findByAreaId(Paramap.create().put("areaId", areaId));
        if (memberAddresVo != null && memberAddresVo.getAreaId() != null) {
            member.setAddCountryId(memberAddresVo.getAreaName());
            areaInfo.insert(0, memberAddresVo.getAreaName());
        }
        if (memberAddresVo != null && memberAddresVo.getCityId() != null) {
            member.setAddCityId(memberAddresVo.getCityName());
            areaInfo.insert(0, memberAddresVo.getCityName());
        }
        if (memberAddresVo != null && memberAddresVo.getProvinceId() != null) {
            member.setAddProvinceId(memberAddresVo.getProvinceName());
            areaInfo.insert(0, memberAddresVo.getProvinceName());
        }
        member.setAddTownId(areaInfo.toString());
        rdMmBasicInfoService.updateMember(member, UpdateMemberInfoStatus.UPDATE_AREAINFO);
        redisService.save(session.getSessionid(), AuthsLoginResult.of(member, session, prefix));
        return ApiUtils.success();
    }

    /**
     * 个人资料中修改地址信息
     */
    @RequestMapping(value = "/updateAddress.json", method = RequestMethod.POST)
    public String updateAddress(HttpServletRequest request, String addressInfo) {
        if (StringUtils.isBlank(addressInfo)) {
            return ApiUtils.error(Xerror.PARAM_INVALID, "参数无效");
        }
        AuthsLoginResult session = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        RdMmBasicInfo member = rdMmBasicInfoService.find("mmCode", session.getMmCode());
        member.setAddDetial(addressInfo);
        rdMmBasicInfoService.updateMember(member, UpdateMemberInfoStatus.UPDATE_ADDRESS);
        redisService.save(session.getSessionid(), AuthsLoginResult.of(member, session, prefix));
        return ApiUtils.success();
    }

    /**
     * 修改手机号码
     *
     * @param {String} oldMobile 原手机号码
     * @param {String} oldMobileCode 原手机号码验证码
     * @param {String} newMobile 新手机号码
     * @param {String} newMobileCode 新手机号码验证码
     */
    @RequestMapping(value = "/updateMobile.json", method = RequestMethod.POST)
    public String updateMobile(
        HttpServletRequest request,
        @RequestParam String oldMobile,
        @RequestParam String oldMobileCode,
        @RequestParam String newMobile,
        @RequestParam String newMobileCode) {
        if (StringUtils.isBlank(oldMobile) || StringUtils.isBlank(oldMobileCode) || StringUtils.isBlank(newMobile)
            || StringUtils.isBlank(newMobileCode)) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        if (!SmsUtil.validMsg(oldMobile, oldMobileCode, redisService)) {
            return ApiUtils.error("原手机号码验证码不正确或已过期");
        }
        if (!SmsUtil.validMsg(newMobile, newMobileCode, redisService)) {
            return ApiUtils.error("新手机号码验证码不正确或已过期");
        }
        AuthsLoginResult session = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        RdMmBasicInfo member = rdMmBasicInfoService.find("mmCode", session.getMmCode());
        if (!oldMobile.equals(member.getMobile())) {
            return ApiUtils.error("不是预留手机号码");
        }
        int newMobileCount = rdMmBasicInfoService.count(Paramap.create().put("mobile", newMobile)).intValue();
        if (newMobileCount > 0) {
            return ApiUtils.error("新手机号码已被绑定");
        }
        RdMmBasicInfo updateMember = new RdMmBasicInfo();
        updateMember.setMmCode(member.getMmCode());
        updateMember.setMobile(newMobile);
        rdMmBasicInfoService.updateMember(updateMember, UpdateMemberInfoStatus.UPDATE_MOBILE);
        member.setMobile(newMobile);
        redisService.save(session.getSessionid(), AuthsLoginResult.of(member, session, prefix));
        redisService.delete(oldMobile);
        redisService.delete(newMobile);
        return ApiUtils.success();
    }

    /**
     * 进行原手机号码验证
     */
    @RequestMapping(value = "/validMobile.json", method = RequestMethod.POST)
    public String validMobile(HttpServletRequest request, @RequestParam String mobile, @RequestParam String code) {
        if (StringUtils.isBlank(mobile) || StringUtils.isBlank(code)) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        //判断手机验证码是否正确
        if (!SmsUtil.validMsg(mobile, code, redisService)) {
            return ApiUtils.error(Xerror.VALID_CODE_ERROR);
        }
        /**判断是不是预留手机号码*/
        AuthsLoginResult session = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        RdMmBasicInfo member = rdMmBasicInfoService.find("mmCode", session.getMmCode());
        if (!member.getMobile().equals(mobile)) {
            return ApiUtils.error("10004", "不是预留手机号码");
        }
        return ApiUtils.success();
    }

    /**
     * 支付密码验证
     */
    @RequestMapping(value = "/validPaymentPasswd.json", method = RequestMethod.POST)
    public String validPaymentPasswd(HttpServletRequest request, @RequestParam String paypassword) {
        if (StringUtils.isBlank(paypassword)) {
            return ApiUtils.error(Xerror.PARAM_INVALID, "参数无效");
        }
        AuthsLoginResult session = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        RdMmAccountInfo member = rdMmAccountInfoService.find("mmCode", session.getMmCode());
        if (!Digests.validatePassword(paypassword, member.getPaymentPwd())) {
            return ApiUtils.error("支付密码输入错误");
        }
        return ApiUtils.success();
    }

    /**
     * 修改密码
     *
     * @param oldpassword 原密码
     * @param newpassword 新密码
     */
    @RequestMapping(value = "/updatePassword.json", method = RequestMethod.POST)
    public String updatePassword(HttpServletRequest request, String oldpassword, String newpassword) {

        if (StringUtils.isBlank(oldpassword) || StringUtils.isBlank(newpassword)) {
            return ApiUtils.error(Xerror.PARAM_INVALID, "参数无效");
        }
        AuthsLoginResult session = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", session.getMmCode());
        if (!Digests.validatePassword(oldpassword, rdMmRelation.getLoginPwd())) {
            return ApiUtils.error("原密码输入错误");
        }
        rdMmRelation.setLoginPwd(Digests.entryptPassword(newpassword));
        rdMmRelationService.update(rdMmRelation);
        return ApiUtils.success();
    }

    /**
     * 设置支付密码
     *
     * @param {String} mobile 手机号码
     * @param {String} code 手机验证码
     * @param {String} paypassword 支付密码
     */
    @RequestMapping(value = "/setPaymentPasswd", method = RequestMethod.POST)
    public String setPaymentPasswd(@RequestParam String mobile,
        @RequestParam String code,
        @RequestParam String paypassword,
        HttpServletRequest request) {
        if (StringUtils.isBlank(mobile) || StringUtils.isBlank(code) || StringUtils.isBlank(paypassword)) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        //判断手机验证码是否正确
        if (!SmsUtil.valicodeIstrue(mobile, code, redisService)) {
            return ApiUtils.error(Xerror.VALID_CODE_ERROR);
        }
        /**判断是不是预留手机号码*/
        AuthsLoginResult session = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        RdMmBasicInfo member = rdMmBasicInfoService.find("mmCode", session.getMmCode());
        if (!member.getMobile().equals(mobile)) {
            return ApiUtils.error("10004", "不是预留手机号码");
        }
        //为保存密码 故意处理
        member.setMobile(Digests.entryptPassword(paypassword));
        rdMmBasicInfoService.updateMember(member, UpdateMemberInfoStatus.SET_PAYMENTPASSWD);
        return ApiUtils.success();
    }

    /**
     * 修改支付密码
     *
     * @param {String} mobile 手机号码
     * @param {String} code 手机验证码
     * @param {String} paypassword 支付密码
     */
    @RequestMapping(value = "/updatePaymentPasswd", method = RequestMethod.POST)
    public String updatePaymentPasswd(HttpServletRequest request, String oldpassword, String newpassword) {
        if (StringUtils.isBlank(oldpassword) || StringUtils.isBlank(newpassword)) {
            return ApiUtils.error(Xerror.PARAM_INVALID, "参数无效");
        }
        AuthsLoginResult session = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        RdMmAccountInfo member = rdMmAccountInfoService.find("mmCode", session.getMmCode());
        if (!Digests.validatePassword(oldpassword, member.getPaymentPwd())) {
            return ApiUtils.error("原密码输入错误");
        }
        member.setPaymentPwd(Digests.entryptPassword(newpassword));
        rdMmAccountInfoService.update(member);
        return ApiUtils.success();
    }

    /**
     * 是否设置支付密码
     */
    @RequestMapping(value = "/paymentPasswdState", method = RequestMethod.POST)
    public String paymentPasswdState(HttpServletRequest request) {

        AuthsLoginResult session = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        RdMmAccountInfo member = rdMmAccountInfoService.find("mmCode", session.getMmCode());
        if (StringUtil.isEmpty(member.getPaymentPwd())) {
            return ApiUtils.success(Paramap.create().put("state", 0));
        } else {
            return ApiUtils.success(Paramap.create().put("state", 1));
        }
    }

    //银行卡列表
    @RequestMapping(value = "/bankCardsList.json")
    public String bankCardsList(@RequestParam(defaultValue = "1") Integer pageNumber,
        @RequestParam(defaultValue = "10") Integer pageSize,
        HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        Pageable pageable = new Pageable(pageNumber, pageSize);
        pageable.setParameter(Paramap.create().put("mmCode", member.getMmCode()));
        pageable.setOrderProperty("defaultbank");
        pageable.setOrderDirection(Order.Direction.DESC);
        List<RdMmBank> rdMmBankList = rdMmBankService.findByPage(pageable).getContent();
        return ApiUtils.success(BankCardsListResult.build(rdMmBankList));
    }

    //添加银行卡
    @RequestMapping(value = "/addBankCards.json")
    public String addBankCards(@Valid UserAddBankCardsParam param, HttpServletRequest request) {
        System.out.println("进来了添加银行卡");
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if (member == null) {
            return ApiUtils.error("请登录");
        }
        //银行卡的验证
        String result = "";
        try {
            result = BankCardUtils.vaildBankCard(param);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiUtils.error("填写相关信息有误");
        }

        System.out.println(result);//1 验证成功

        if (!result.equals("1")) {
            return ApiUtils.error(result);
        }
        //判断手机验证码是否正确
        if (!SmsUtil.valicodeIstrue(param.getMobile(), param.getCode(), redisService)) {
            return ApiUtils.error(Xerror.VALID_CODE_ERROR);
        }
        RdMmBank rdMmBank = new RdMmBank();
        rdMmBank.setMmCode(member.getMmCode());
        rdMmBank.setAccCode(param.getAccCode());
        rdMmBank.setBankDetail(param.getBankDetail());
        rdMmBank.setMobile(param.getMobile());
        rdMmBank.setAccName(param.getAccName());
        rdMmBank.setAccType("1");
        List<RdMmBank> rdMmBankList = rdMmBankService.findList("mmCode", member.getMmCode());
        if (rdMmBankList.size() == 0) {
            rdMmBank.setDefaultbank(1);
        } else {
            rdMmBank.setDefaultbank(0);
        }
        rdMmBankService.save(rdMmBank);
        return ApiUtils.success();
    }

    //设置默认银行卡
    @RequestMapping(value = "/setdefaultBankCards.json")
    public String addBankCards(HttpServletRequest request, Integer Id) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        rdMmBankService.updateDef(Id, member.getMmCode());
        return ApiUtils.success();
    }

    //删除银行卡
    @RequestMapping(value = "/removeBankCards.json")
    public String removeBankCards(HttpServletRequest request, Integer Id) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if (Id == null) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        RdMmBank rdMmBank = rdMmBankService.find(Long.parseLong(Id + ""));
        if (rdMmBank == null) {
            return ApiUtils.error("不存在的银行卡");
        }
        rdMmBankService.delete(Long.parseLong(Id + ""));
        if (rdMmBank.getDefaultbank() == 1) { //如果删除的是默认的 则寻找一个自动为默认
            List<RdMmBank> rdMmBankList = rdMmBankService.findList("mmCode", member.getMmCode());
            if (rdMmBankList != null && rdMmBankList.size() > 0) {
                RdMmBank mmBank = rdMmBankList.get(0);
                mmBank.setDefaultbank(1);
                rdMmBankService.update(mmBank);
            }
        }
        return ApiUtils.success();
    }

    /**
     * 账号安全
     */
    @RequestMapping(value = "/accountSafe.json", method = RequestMethod.POST)
    public String accountSafe(HttpServletRequest request) {
        AuthsLoginResult session = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        RdMmBasicInfo member = rdMmBasicInfoService.find("mmCode", session.getMmCode());
        String qqOpenid = member.getQqCode();
        String weixinOpenid = member.getWechatCode();
        return ApiUtils.success(Paramap.create().put("qqopenid", StringUtils.isBlank(qqOpenid) ? "" : qqOpenid)
            .put("weixinOpenid", StringUtils.isBlank(weixinOpenid) ? "" : weixinOpenid)
        );
    }

    /**
     * 第三方绑定
     */
    @RequestMapping(value = "/thirdpartyBind.json", method = RequestMethod.POST)
    public String thirdpartyBind(HttpServletRequest request, String openid, Integer type) {
        if (StringUtils.isBlank(openid) || type == null || !(type == UserLoginType.QQ.code
            || type == UserLoginType.WECHAT.code || type == UserLoginType.WEIBO.code)) {
            return ApiUtils.error(Xerror.PARAM_INVALID, "参数无效");
        }
        AuthsLoginResult session = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        RdMmBasicInfo member = rdMmBasicInfoService.find("mmCode", session.getMmCode());
        List<RdMmBasicInfo> list = rdMmBasicInfoService.findList(SocialType.of(type), openid);
        if (list != null && list.size() != 0) {
            return ApiUtils.error(Xerror.OBJECT_IS_EXIST, "已绑定其它账号");
        }
        if (type == UserLoginType.QQ.code) {
            member.setQqCode(openid);
        } else if (type == UserLoginType.WECHAT.code) {
            member.setWechatCode(openid);
        }
        rdMmBasicInfoService.update(member);
        return ApiUtils.success();
    }

//    /**
//     * 第三方解绑
//     */
//    @RequestMapping(value = "/thirdpartyUnBind.json", method = RequestMethod.POST)
//    public String thirdpartyUnBind(HttpServletRequest request, Integer type) {
//        if (type == null || !(type == UserLoginType.QQ.code
//                || type == UserLoginType.WECHAT.code || type == UserLoginType.WEIBO.code)) {
//            return ApiUtils.error(Xerror.PARAM_INVALID, "参数无效");
//        }
//        AuthsLoginResult session = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
//        shopMemberService.unbindThirdparty(type, session.getUserId());
//        return ApiUtils.success();
//    }

    //我的收藏
    @RequestMapping(value = "/collect.json", method = RequestMethod.POST)
    public String UserCollect(@RequestParam(defaultValue = "1") Integer pageNumber,
        @RequestParam(defaultValue = "10") Integer pageSize, HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        Map<String, Object> params = new HashMap<>();
        params.put("favType", 1);
        params.put("orderBy", 1);
        params.put("memberId", member.getMmCode());
        Pageable pager = new Pageable(pageNumber, pageSize);
        pager.setParameter(params);

        // 查找list
        List<ShopMemberFavorites> storeorgoodsList = shopMemberFavoritesService.findByPage(pager).getContent();
        List<Long> goodsId = new ArrayList<>();
        for (ShopMemberFavorites eveluate : storeorgoodsList) {
            goodsId.add(eveluate.getFavId());
        }
        Map<Long, ShopGoods> shopGoodsMap = shopGoodsService.findGoodsMap(goodsId);
        //Map<String, String> shopGoodsEvaluateMap = shopGoodsEvaluateService
        //    .countCommentRate(goodsId, EvaluateGoodsResult.STANDARD_OF_POSITIVE_COMMENT_SCORE);
        //翻回list
        List<UserCollectResult> results = Lists.newArrayList();
        if (storeorgoodsList != null && storeorgoodsList.size() > 0) {
            for (ShopMemberFavorites favorite : storeorgoodsList) {
                UserCollectResult result = new UserCollectResult();
                Long favId = favorite.getFavId();
                ShopGoods shopGoods = shopGoodsMap.get(favId);
                if (shopGoods == null) {
                    shopMemberFavoritesService.delete(favorite.getId());
                    continue;
                }
                result.build2(shopGoods, result, null, favorite, prefix);
                results.add(result);
            }
        }
        return ApiUtils.success(results);
    }

    // 删除收藏
    @RequestMapping(value = "/collect/remove.json", method = RequestMethod.POST)
    public String UserCollectRemove(HttpServletRequest request, String ids) {
        if (StringUtils.isEmpty(ids)) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        String[] idsArray = ids.split(",");
        Long[] idsLong = new Long[idsArray.length];
        for (int i = 0; i < idsArray.length; i++) {
            idsLong[i] = Long.valueOf(idsArray[i]);
        }
        if (idsLong.length > 0) {
            shopMemberFavoritesService.deleteByIds(member.getMmCode(), idsLong);
        }
        return ApiUtils.success();
    }

    //我的足迹
    @RequestMapping(value = "/footprints.json", method = RequestMethod.POST)
    public String UserFootprints(@RequestParam(defaultValue = "1") Integer pageNumber, String day,
        @RequestParam(defaultValue = "10") Integer pageSize, HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        Map<String, Object> params = new HashMap<>();
        if (!StringUtils.isEmpty(day)) {
            params.put("createTime", Dateutil.parseStrFromToDate(day, "yyyy-MM-dd"));
        }
        params.put("browseMemberId", member.getMmCode());
        int size=pageSize;
        int currentPage=pageNumber;
        PageHelper.startPage(currentPage,size,true);
        /*Pageable pager = new Pageable(pageNumber, pageSize);
        pager.setParameter(params);
        pager.setOrderProperty("create_time");
        pager.setOrderDirection(Order.Direction.DESC);
        List<ShopGoodsBrowse> shopGoodsBrowseList = shopGoodsBrowseService.findByPage(pager).getContent();*/
        List<ShopGoodsBrowse> shopGoodsBrowseList = shopGoodsBrowseService.findFootByIdAndTime(params);
        PageInfo<ShopGoodsBrowse> pageInfo = new PageInfo<>(shopGoodsBrowseList);
        List<ShopGoodsBrowse> list = pageInfo.getList();
        List<Long> goodsIds = new ArrayList<>();
        for (ShopGoodsBrowse itemBrowse : list) {
            goodsIds.add(itemBrowse.getBrowseGoodsId());
        }
        Map<Long, ShopGoods> mapGoods = shopGoodsService.findGoodsMap(goodsIds);
        return ApiUtils.success(UserFootprintsResult.build(list, mapGoods, member));
    }

    //删除我的足迹
    @RequestMapping(value = "/footprints/remove.json", method = RequestMethod.POST)
    public String UserFootprintsRemove(HttpServletRequest request, String day, String ids, String checkAll) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if (!StringUtils.isEmpty(day)) {
            if (day.indexOf(",") > -1) {
                String[] daySort = day.split(",");
                shopGoodsBrowseService.deleteMemberFavorites(
                    Paramap.create().put("days", daySort).put("browseMemberId", member.getMmCode()));
//               shopGoodsBrowseService.deleteMemberFavorites(Paramap.create().put("start", daySort[0]).put("end",daySort[daySort.length-1]).put("browseMemberId", member.getUserId()));
            } else {
                shopGoodsBrowseService
                    .deleteMemberFavorites(Paramap.create().put("day", day).put("browseMemberId", member.getMmCode()));
            }

        } else if (!StringUtils.isEmpty(ids)) {
            String[] idsArray = ids.split(",");
            Long[] idsLong = new Long[idsArray.length];
            for (int i = 0; i < idsArray.length; i++) {
                idsLong[i] = Long.valueOf(idsArray[i]);
            }
            shopGoodsBrowseService
                .deleteMemberFavorites(Paramap.create().put("ids", idsLong).put("browseMemberId", member.getMmCode()));
        } else if (!StringUtils.isEmpty(checkAll)) {
            shopGoodsBrowseService.deleteMemberFavorites(
                Paramap.create().put("checkAll", checkAll).put("browseMemberId", member.getMmCode()));
        }
        return ApiUtils.success();
    }

//    //更新用户配置信息
//    @ResponseBody
//    @RequestMapping("/setting/receiveInform.json")
//    public String receiveInform(@RequestParam(defaultValue = "0") Integer isReceive, HttpServletRequest request) {
//        if ((!isReceive.equals("0") && !isReceive.equals("1"))) {
//            return ApiUtils.error(Xerror.PARAM_INVALID);
//        }
//        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
//        ShopMember userSession = new ShopMember();
//        userSession.setId(member.getUserId());
//        userSession.setIsReceive(isReceive);
//        shopMemberService.update(userSession);
//        redisService.save(request.getHeader(Constants.USER_SESSION_ID), member);
//        return ApiUtils.success(Paramap.create().put("isReceive", isReceive));
//    }

    ///**
    // * 验证老用户
    // *
    // * @param request
    // * @param paypassword 密码
    // * @param account     账号
    // * @return
    // */
    //@RequestMapping(value = "/validOldUserAccount.json", method = RequestMethod.POST)
    //public String validOldUserAccount(HttpServletRequest request, @RequestParam String paypassword, @RequestParam String account) {
    //    if (StringUtils.isBlank(paypassword) || StringUtils.isBlank(account)) {
    //        return ApiUtils.error(Xerror.PARAM_INVALID, "参数无效");
    //    }
    //    // TODO: 2018/12/26  查找老用户信息
    //    return ApiUtils.success(Paramap.create().put("nickname", "假数据").put("membershipNumber", 52112414)
    //            .put("id", 1));
    //}

    /**
     * 绑定老账户
     *
     * @param mmCode 老账户mmCode
     */
/*    @RequestMapping(value = "/bindOldUserAccount.json", method = RequestMethod.POST)
    public String bindOldUserAccount(HttpServletRequest request, @RequestParam String mmCode) {
        if (StringUtils.isBlank(mmCode)) {
            return ApiUtils.error(Xerror.PARAM_INVALID, "参数无效");
        }
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        // TODO: 2018/12/26  绑定老用户信息
        OldSysRelationship oldSysRelationship=oldSysRelationshipService.find("oMcode",mmCode);
        if (oldSysRelationship.getNYnRegistered()==1){
            return ApiUtils.error("老用户已经注册或者绑定");
        }
        Map<String, String> map = oldSysRelationshipService.findOldSysSpcode(oldSysRelationship.getOSpcode());
        if (map.get("mmCode") != null && !"".equals(map.get("mmCode")) && !"8888".equals(map.get("mmCode"))) {
            //RdRaBinding rdRaBinding = new RdRaBinding();
            //rdRaBinding.setBindingBy(member.getNickname());
            //rdRaBinding.setBindingDate(new Date());
            //rdRaBinding.setBindingStatus(1);
            //rdRaBinding.setRaCode(mmCode);
            //rdRaBinding.setRaNickName(oldSysRelationship.getONickname());
            //rdRaBinding.setRaSponsorName(map.get("mmName"));
            //rdRaBinding.setRaStatus(oldSysRelationship.getOStatus());
            //rdRaBinding.setRaSponsorCode(map.get("mmCode"));
            //rdRaBinding.setRdCode(member.getMmCode());
            //rdRaBindingService.save(rdRaBinding);
            oldSysRelationship.setNYnRegistered(1);
            oldSysRelationship.setNMcode(member.getMmCode());
            oldSysRelationshipService.update(oldSysRelationship);
        } else {
            return ApiUtils.error("网络关系不一致,不予以绑定");
        }
        return ApiUtils.success();
    }*/

    /**
     * 个人主页
     */
    @RequestMapping(value = "/memberHomepage.json", method = RequestMethod.POST)
    public String memberHomepage(HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request
            .getAttribute(com.framework.loippi.consts.Constants.CURRENT_USER);
        RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", member.getMmCode());
        RdRanks rdRanks = rdRanksService.find("rankId", rdMmRelation.getRank());
        String startTime = Dateutil.getEalierOrLaterDate(new Date().getTime(), 10, 1, "yyyy-MM");
        String endTime = Dateutil.getEalierOrLaterDate(new Date().getTime(), 2, 1, "yyyy-MM");
        OrderSumPpv orderSumMonthlyPpv = shopOrderService.sumPpv(
            Paramap.create().put("startTime", startTime).put("endTime", endTime).put("buyerId", member.getMmCode()));
        //OrderSumPpv orderSumAccumulatedPpv=shopOrderService.sumPpv(Paramap.create().put("buyerId",member.getMmCode()));
        BigDecimal monthlyPpv = BigDecimal.ZERO;
        BigDecimal AccumulatedPpv = Optional.ofNullable(rdMmRelation.getAPpv()).orElse(BigDecimal.ZERO);
        if (orderSumMonthlyPpv != null) {
            monthlyPpv = Optional.ofNullable(orderSumMonthlyPpv.getTotalPpv()).orElse(BigDecimal.ZERO);
        }
        if (rdRanks.getRankClass() == 0) {
            monthlyPpv = BigDecimal.ZERO;
            AccumulatedPpv = BigDecimal.ZERO;
        }
        //if (orderSumAccumulatedPpv!=null){
        //    AccumulatedPpv=Optional.ofNullable(orderSumAccumulatedPpv.getTotalPpv()).orElse(BigDecimal.ZERO);
        //}
        if (rdRanks == null) {
            return ApiUtils.success(Paramap.create().put("gradeName", "用户").put("monthlyPpv", monthlyPpv)
                .put("AccumulatedPpv", AccumulatedPpv).put("url", "www.baidu.com").put("docType", "member_upgrade"));
        }
        return ApiUtils.success(Paramap.create().put("gradeName", rdRanks.getRankName()).put("monthlyPpv", monthlyPpv)
            .put("AccumulatedPpv", AccumulatedPpv).put("url", "www.baidu.com").put("docType", "member_upgrade"));
    }

    /**
     * 更新app版本
     */
    @RequestMapping(value = "/updatedVersion.json", method = RequestMethod.POST)
    public String updatedVersion(HttpServletRequest request, int type) {
        List<ShopApp> shopAppList = shopAppService.findList(Paramap.create().put("device", type)
            .put("order", "CREATE_DATE desc"));
        if (shopAppList != null && shopAppList.size() > 0) {
            ShopApp shopApp = shopAppList.get(0);
            return ApiUtils.success(Paramap.create().put("version", shopApp.getVersion())
                .put("url", shopApp.getUrl()).put("isForce", Optional.ofNullable(shopApp.isForce()).orElse(false)));
        } else {
            return ApiUtils.error("该版本为最新版本");
        }
    }

    /**
     * 我的邀请码
     *
     * @param type IOS 1   Android 0
     */
    @RequestMapping(value = "/MyInvitationNumber.json", method = RequestMethod.POST)
    public String MyInvitationNumber(HttpServletRequest request, int type) {
        AuthsLoginResult member = (AuthsLoginResult) request
            .getAttribute(com.framework.loippi.consts.Constants.CURRENT_USER);
        String downloadUrl = "";
        List<ShopApp> shopAppList = shopAppService.findList(Paramap.create().put("device", type)
            .put("order", "CREATE_DATE desc"));
        if (shopAppList != null && shopAppList.size() > 0) {
            ShopApp shopApp = shopAppList.get(0);
            downloadUrl = shopApp.getUrl();
        }
        StringBuffer shareUrl = new StringBuffer();
        shareUrl.append(wapServer);
        shareUrl.append("/wap/download");
        shareUrl.append(".html?code=");
        shareUrl.append(member.getMmCode());
        shareUrl.append("&downloadUrl=");
        shareUrl.append(downloadUrl);
        return ApiUtils.success(
            Paramap.create().put("userCode", member.getMmCode()).put("shareUrl", shareUrl.toString())
                .put("downloadUrl", "https://a.app.qq.com/o/simple.jsp?pkgname=com.ewhale.distribution"));
    }

    /**
     * 增加分享次数
     */
    @RequestMapping(value = "/addShareNumber.json", method = RequestMethod.POST)
    public String addShareNumber(HttpServletRequest request) {
        AuthsLoginResult session = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        RdMmBasicInfo member = rdMmBasicInfoService.find("mmCode", session.getMmCode());
        member.setShareNum(Optional.ofNullable(member.getShareNum()).orElse(0) + 1);
        rdMmBasicInfoService.updateMember(member, UpdateMemberInfoStatus.UPDATE_SHARENUMBER);
        return ApiUtils.success();
    }

    /**
     * 分销用户信息
     *
     * @param memeberId 用户id
     */
    @RequestMapping(value = "/subordinateUserInformation.json", method = RequestMethod.POST)
    public String subordinateUserInformation(HttpServletRequest request, String memeberId) {
        RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", memeberId);
        RdMmBasicInfo rdMmBasicInfo = rdMmBasicInfoService.find("mmCode", memeberId);
        RdRanks shopMemberGrade = null;
        if (Optional.ofNullable(rdMmRelation.getRank()).orElse(-1) != -1) {
            shopMemberGrade = rdRanksService.find("rankId", rdMmRelation.getRank());
        }
        /*String startTime = Dateutil.getEalierOrLaterDate(new Date().getTime(), 10, 1, "yyyy-MM");
        String endTime = Dateutil.getEalierOrLaterDate(new Date().getTime(), 2, 1, "yyyy-MM");
        OrderSumPpv orderSumMonthlyPpv = shopOrderService
            .sumPpv(Paramap.create().put("startTime", startTime).put("endTime", endTime).put("buyerId", memeberId));*/
        OrderSumPpv orderSumAccumulatedPpv = shopOrderService.sumPpv(Paramap.create().put("buyerId", memeberId));
        //查找当前周期订单数据
        String periodCode = periodService.getSysPeriodService(new Date());
        OrderSumPpv periodSumPpv = new OrderSumPpv();
        if(periodCode==null){
            periodSumPpv.setTotalmoney(new BigDecimal("0.00"));
            periodSumPpv.setTotalPpv(new BigDecimal("0.00"));
        }else {
            periodSumPpv=shopOrderService.findByPeriod(Paramap.create().put("buyerId", memeberId).put("creationPeriod",periodCode));
        }
        BigDecimal retail = new BigDecimal("0.00");
        BigDecimal buyerId = shopOrderService.findOrderRetail(Paramap.create().put("buyerId", memeberId));//查询出零售订单总额
        if(buyerId!=null){
            retail = buyerId.add(retail);
        }
        return ApiUtils.success(SubordinateUserInformationResult
            .build2(rdMmBasicInfo, shopMemberGrade, periodSumPpv, orderSumAccumulatedPpv,rdMmRelation.getRaSponsorStatus()));
    }
    /**
     * 分销用户信息
     *
     * @param memeberId 用户id
     */
    @RequestMapping(value = "/subordinateUserInformationNew.json", method = RequestMethod.POST)
    public String subordinateUserInformationNew(HttpServletRequest request, String memeberId,String periodStr) {
        AuthsLoginResult session = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        RdMmBasicInfo member = rdMmBasicInfoService.find("mmCode", session.getMmCode());
        if(StringUtil.isEmpty(memeberId)){
            return ApiUtils.error("请传入需要查询会员的会员编号");
        }
        if(StringUtil.isEmpty(periodStr)){
            return ApiUtils.error("请传入需要查询的周期编号");
        }
        MemberQualification memberQualification=memberQualificationService.findByCodeAndPeriod(Paramap.create().put("mCode",memeberId).put("periodCode",periodStr));
        if(memberQualification==null){
            return ApiUtils.error("当前筛选条件下尚未统计出会员信息");
        }
        RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", memeberId);
        RdMmBasicInfo rdMmBasicInfo = rdMmBasicInfoService.find("mmCode", memeberId);
        RdRanks shopMemberGrade = null;
        if (Optional.ofNullable(rdMmRelation.getRank()).orElse(-1) != -1) {
            shopMemberGrade = rdRanksService.find("rankId", rdMmRelation.getRank());
        }
        OrderSumPpv periodSumPpv = new OrderSumPpv();
        if(periodStr==null){
            RdSysPeriod sysPeriod=periodService.findLastPeriod();
            periodSumPpv=shopOrderService.findByPeriod(Paramap.create().put("buyerId", memeberId).put("creationPeriod",sysPeriod.getPeriodCode()));
        }else {
            periodSumPpv=shopOrderService.findByPeriod(Paramap.create().put("buyerId", memeberId).put("creationPeriod",periodStr));
        }
        BigDecimal retail = new BigDecimal("0.00");
        BigDecimal buyerId = shopOrderService.findOrderRetail(Paramap.create().put("buyerId", memeberId).put("creationPeriod",periodStr));//查询出零售订单总额
        if(buyerId!=null){
            retail = buyerId.add(retail);
        }
        Paramap paramap = Paramap.create();
        //查询已发放零售利润
        BigDecimal pay=BigDecimal.ZERO;
        BigDecimal payD=retailProfitService.findPeriodPay(Paramap.create().put("buyerId", memeberId).put("createPeriod",periodStr));
        if(payD!=null){
            pay=pay.add(payD);
        }
        //查询待发放零售利润
        BigDecimal nopay=BigDecimal.ZERO;
        BigDecimal nopayD=retailProfitService.findPeriodNoPay(Paramap.create().put("buyerId", memeberId).put("createPeriod",periodStr));
        if(nopayD!=null){
            nopay=nopay.add(nopayD);
        }
        boolean flag = member.getMmCode().equals(rdMmRelation.getSponsorCode());
        paramap.put("showFlag",flag);
        SubordinateUserInformationResult subordinateUserInformationResult = SubordinateUserInformationResult
                .build3(memberQualification,rdMmBasicInfo,rdMmRelation,periodSumPpv,shopMemberGrade,retail,pay,nopay);
        paramap.put("data",subordinateUserInformationResult);
        return ApiUtils.success(paramap);
    }

    /**
     * 个人业绩
     * @param request
     * @param periodCode
     * @param mCode
     * @return
     */
    @RequestMapping(value = "/selfPerformance.json", method = RequestMethod.POST)
    public String selfPerformance(HttpServletRequest request, String periodCode, String mCode) {
        if (StringUtils.isEmpty(mCode)){
            return ApiUtils.error("该会员编号为空");
        }
        if (StringUtils.isEmpty(periodCode)){
            RdSysPeriod period = periodService.getPeriodService(new Date());
            if (period!=null){
                periodCode = period.getPeriodCode();
            }else {
                return ApiUtils.error("周期该没有");
            }
        }
        //会员基础信息
        RdMmBasicInfo basicInfo = mmBasicInfoService.findByMCode(mCode);
        if (basicInfo==null){
            return ApiUtils.error("找不到该会员信息！");
        }
        RdSysPeriod period = periodService.findByPeriodCode(periodCode);

        SelfPerformanceResult result = new SelfPerformanceResult();
        MemberQualification qualification = qualificationService.findByMCodeAndPeriodCode(Paramap.create().put("mCode",mCode).put("periodCode",periodCode));
        //当期零售利润
        BigDecimal profits1 = retailProfitService.countProfit(Paramap.create().put("receiptorId",mCode).put("createPeriod",periodCode).put("state",1));
        if (profits1==null){
            profits1 = new BigDecimal("0.00");
        }
        BigDecimal bugMi = shopOrderService.countOrderPPVByMCodeAndPeriod(mCode, periodCode);
        if (bugMi==null){
            bugMi = new BigDecimal("0.00");
        }
        List<String> SysPeriodCode =new ArrayList<String>();
        String prePeriod = period.getPrePeriod();//上一周期
        if (prePeriod==null){
            prePeriod  = "";
        }
        String prePeriod2 = "";//上上一周期
        if (!StringUtils.isEmpty(prePeriod)){//为空
            RdSysPeriod period1 = periodService.findByPeriodCode(prePeriod);
            if (period1!=null){
                prePeriod2 = period1.getPrePeriod();
            }
        }
        SysPeriodCode.add(periodCode);
        if (!"".equals(prePeriod)){
            SysPeriodCode.add(prePeriod);
        }
        if (!"".equals(prePeriod2)){
            SysPeriodCode.add(prePeriod2);
        }

        if (period.getCalStatus()==3){ //发布完
            RdBonusMaster bonusMaster = rdBonusMasterService.findByMCodeAndPeriodCode(Paramap.create().put("mCode",mCode).put("periodCode",periodCode));
            result = SelfPerformanceResult.build1(basicInfo,qualification,profits1,bonusMaster,SysPeriodCode,bugMi);
        }else {
            //当期待发放零售利润
            BigDecimal profits2 = retailProfitService.countProfit(Paramap.create().put("receiptorId",mCode).put("createPeriod",periodCode).put("state",2));
            BigDecimal profits0 = retailProfitService.countProfit(Paramap.create().put("receiptorId",mCode).put("createPeriod",periodCode).put("state",0));
            if (profits2==null){
                profits2 = new BigDecimal("0.00");
            }
            if (profits0==null){
                profits0 = new BigDecimal("0.00");
            }
            profits2 = profits2.add(profits0);
            result = SelfPerformanceResult.build2(basicInfo,qualification,profits1,profits2,SysPeriodCode,bugMi);
        }


        return ApiUtils.success(result);
    }
}
