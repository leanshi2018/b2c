package com.framework.loippi.controller.user;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.framework.loippi.consts.IntegrationNameConsts;
import com.framework.loippi.controller.BaseController;
import com.framework.loippi.entity.integration.RdMmIntegralRule;
import com.framework.loippi.entity.user.RdMmAccountInfo;
import com.framework.loippi.entity.user.RdMmAccountLog;
import com.framework.loippi.entity.user.RdMmBank;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.entity.user.RdRanks;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.result.auths.AuthsLoginResult;
import com.framework.loippi.result.user.IntegrationBuildResult;
import com.framework.loippi.result.user.IntegrationDetailResult;
import com.framework.loippi.result.user.IntegrationListResult;
import com.framework.loippi.result.user.IntegrationMemberListResult;
import com.framework.loippi.result.user.UserIntegrationListResult;
import com.framework.loippi.service.integration.RdMmIntegralRuleService;
import com.framework.loippi.service.user.RdMmAccountInfoService;
import com.framework.loippi.service.user.RdMmAccountLogService;
import com.framework.loippi.service.user.RdMmBankService;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.service.user.RdMmRelationService;
import com.framework.loippi.service.user.RdNewVipDetailService;
import com.framework.loippi.service.user.RdRanksService;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.ApiUtils;
import com.framework.loippi.utils.Constants;
import com.framework.loippi.utils.Digests;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.StringUtil;
import com.framework.loippi.utils.Xerror;

/**
 * 积分 Created by Administrator on 2017/11/23.
 */
@Controller
@ResponseBody
@RequestMapping("/api/integration")
public class UserIntegrationAPIController extends BaseController {

    @Resource
    RdMmAccountInfoService rdMmAccountInfoService;
    @Resource
    private RdMmIntegralRuleService rdMmIntegralRuleService;
    @Resource
    private RdNewVipDetailService rdNewVipDetailService;
    @Resource
    private RdMmAccountLogService RdMmAccountLogService;
    @Resource
    private RdMmBankService rdMmBankService;
    @Resource
    private RdMmBasicInfoService rdMmBasicInfoService;
    @Resource
    private RdRanksService rdRanksService;
    @Resource
    private RdMmRelationService rdMmRelationService;

    //积分列表
    @RequestMapping(value = "/list.json")
    public String list(HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", member.getMmCode());
        return ApiUtils.success(UserIntegrationListResult.build(rdMmAccountInfo));
    }


    //奖励积分转出
    @RequestMapping(value = "/bop/transferOut.json")
    public String bopTransferOut(HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", member.getMmCode());
        if (rdMmAccountInfo.getBonusStatus() != null && rdMmAccountInfo.getBonusStatus() != 0) {
            return ApiUtils.error("该积分未处于正常状态不能进行转出");
        }
        List<RdMmIntegralRule> rdMmIntegralRuleList = rdMmIntegralRuleService
            .findList(Paramap.create().put("order", "RID desc"));
        RdMmIntegralRule rdMmIntegralRule = new RdMmIntegralRule();
        if (rdMmIntegralRuleList != null && rdMmIntegralRuleList.size() > 0) {
            rdMmIntegralRule = rdMmIntegralRuleList.get(0);
        }
        return ApiUtils.success(Paramap.create()
            .put("bonusBlance", Optional.ofNullable(rdMmAccountInfo.getBonusBlance()).orElse(BigDecimal.valueOf(0)))
            .put("walletBlance", Optional.ofNullable(rdMmAccountInfo.getWalletBlance()).orElse(BigDecimal.valueOf(0)))
            .put("proportion", Optional.ofNullable(rdMmIntegralRule.getBonusPointShopping()).orElse(0) * 0.01)
            .put("bonusPointWdLimit", Optional.ofNullable(rdMmIntegralRule.getBonusPointWdLimit()).orElse(0))
            .put("bonusPointWd", Optional.ofNullable(rdMmIntegralRule.getBonusPointWd()).orElse(0)));
    }

    //奖励积分转出确认
    @RequestMapping(value = "/bop/transferOut/finish.json")
    public String transferOutFinish(HttpServletRequest request, Integer integration, String paypassword) {
        if (integration == null || "".equals(paypassword)) {
            return ApiUtils.error(Xerror.PARAM_INVALID, "参数无效");
        }
        AuthsLoginResult member = (AuthsLoginResult) request
            .getAttribute(com.framework.loippi.consts.Constants.CURRENT_USER);
        RdMmBasicInfo shopMember = rdMmBasicInfoService.find("mmCode", member.getMmCode());
        RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", member.getMmCode());
        if (integration <= 0) {
            return ApiUtils.error("所转积分不合理");
        }
        if (rdMmAccountInfo.getPaymentPwd() == null) {
            return ApiUtils.error("你还未设置支付密码");
        }
        if (!Digests.validatePassword(paypassword, rdMmAccountInfo.getPaymentPwd())) {
            return ApiUtils.error("支付密码错误");
        }

        if (rdMmAccountInfo.getBonusBlance().compareTo(BigDecimal.valueOf(integration)) == -1) {
            return ApiUtils.error("转出积分大于可转出积分");
        }
        List<RdMmIntegralRule> rdMmIntegralRuleList = rdMmIntegralRuleService
            .findList(Paramap.create().put("order", "RID desc"));
        RdMmIntegralRule rdMmIntegralRule = new RdMmIntegralRule();
        if (rdMmIntegralRuleList != null && rdMmIntegralRuleList.size() > 0) {
            rdMmIntegralRule = rdMmIntegralRuleList.get(0);
        }
        BigDecimal walletBlance = BigDecimal
            .valueOf(integration * Optional.ofNullable(rdMmIntegralRule.getBonusPointShopping()).orElse(0) * 0.01);
        List<RdMmAccountLog> rdMmAccountLogList = new ArrayList<>();
        RdMmAccountLog rdMmAccountLogSP = IntegrationBuildResult
            .bonusSP(shopMember, rdMmAccountInfo, integration, walletBlance);
        RdMmAccountLog rdMmAccountLogBT = IntegrationBuildResult.WalletBT(shopMember, rdMmAccountInfo, walletBlance);
        rdMmAccountLogList.add(rdMmAccountLogSP);
        rdMmAccountLogList.add(rdMmAccountLogBT);
        rdMmAccountInfo.setBonusBlance(rdMmAccountInfo.getBonusBlance().subtract(BigDecimal.valueOf(integration)));
        rdMmAccountInfo.setWalletBlance(rdMmAccountInfo.getWalletBlance().add(walletBlance));
        Integer transNumber = rdMmAccountInfoService
            .saveAccountInfo(rdMmAccountInfo, integration, IntegrationNameConsts.BOP, rdMmAccountLogList, null);
        return ApiUtils.success(Paramap.create().put("transNumber", transNumber).put("transferOutPoints", integration)
            .put("bopIntegration", rdMmAccountInfo.getBonusBlance()).put("transferInPoints", walletBlance)
            .put("shpIntegration", rdMmAccountInfo.getWalletBlance()));
    }

    //奖励积分提现确认
    @RequestMapping(value = "/bop/cashWithdrawal/finish.json")
    public String bopCashWithdrawal(HttpServletRequest request, int bankCardId, int integration, String paypassword) {
        if ("".equals(paypassword)) {
            return ApiUtils.error(Xerror.PARAM_INVALID, "参数无效");
        }

        if (integration <= 0) {
            return ApiUtils.error("提现积分不合理");
        }


        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        RdMmBasicInfo shopMember = rdMmBasicInfoService.find("mmCode", member.getMmCode());
        RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", member.getMmCode());
        if (rdMmAccountInfo.getPaymentPwd() == null) {
            return ApiUtils.error("你还未设置支付密码");
        }

        if (!Digests.validatePassword(paypassword, rdMmAccountInfo.getPaymentPwd())) {
            return ApiUtils.error("支付密码错误");
        }

        //银行卡信息
        RdMmBank rdMmBank = rdMmBankService.find(Long.parseLong(bankCardId+""));
        if (rdMmBank == null) {
            return ApiUtils.error("不存在该银行卡");
        }

        List<RdMmIntegralRule> rdMmIntegralRuleList = rdMmIntegralRuleService
            .findList(Paramap.create().put("order", "RID desc"));
        RdMmIntegralRule rdMmIntegralRule = new RdMmIntegralRule();
        if (CollectionUtils.isNotEmpty(rdMmIntegralRuleList)) {
            rdMmIntegralRule = rdMmIntegralRuleList.get(0);
        }
        if (rdMmIntegralRule.getBonusPointWdLimit()>integration) {
            return ApiUtils.error("提现积分小于最少提现额度"+rdMmIntegralRule.getBonusPointWdLimit());
        }
        BigDecimal bonusPointWd =BigDecimal.valueOf(Optional.ofNullable(rdMmIntegralRule.getBonusPointWd()).orElse(0)* 0.01);
        List<RdMmAccountLog> rdMmAccountLogList = new ArrayList<>();
        RdMmAccountLog rdMmAccountLog = IntegrationBuildResult.bonusWD(shopMember, rdMmAccountInfo, integration, bonusPointWd, bankCardId);
        rdMmAccountLogList.add(rdMmAccountLog);
        rdMmAccountInfo.setBonusBlance(rdMmAccountInfo.getBonusBlance().subtract(BigDecimal.valueOf(integration)));
        Integer transNumber = rdMmAccountInfoService.saveAccountInfo(rdMmAccountInfo, integration, IntegrationNameConsts.BOP, rdMmAccountLogList, null);
        // TODO: 2018/12/28 待处理
        return ApiUtils.success(Paramap.create().put("bankCardCode",
            "****     ****     ****     " + rdMmBank.getAccCode().substring(rdMmBank.getAccCode().length() - 4))
            .put("transferOutMoney", integration)
            .put("bopIntegration", rdMmAccountInfo.getBonusBlance()));
    }

    //购物积分转出
    @RequestMapping(value = "/shp/transferOut.json")
    public String shpTransferOut(HttpServletRequest request, Pageable pageable) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", member.getMmCode());
        if (rdMmAccountInfo.getWalletStatus() != null && rdMmAccountInfo.getWalletStatus() != 0) {
            return ApiUtils.error("该积分不能进行转出");
        }
        pageable.setOrderDirection(Order.Direction.DESC);
        pageable.setOrderProperty("TRANS_DATE");
        pageable.setParameter(Paramap.create().put("transTypeCode", "TT").put("mmCode", member.getMmCode()));
        List<RdMmAccountLog> rdMmAccountLogList = RdMmAccountLogService.findByPage(pageable).getContent();
        //List<RdMmRelation> rdMmRelationList = rdMmRelationService.findList("sponsorCode", member.getMmCode());
        List<String> mmCodes = new ArrayList();
        for (RdMmAccountLog item : rdMmAccountLogList) {
            mmCodes.add(item.getTrMmCode());
        }
        List<RdMmBasicInfo> rdMmBasicInfoList = new ArrayList<>();
        List<RdMmRelation> rdMmRelationList = new ArrayList<>();
        if (mmCodes != null && mmCodes.size() > 0) {
            rdMmBasicInfoList = rdMmBasicInfoService.findList("mmCodes", mmCodes);
            rdMmRelationList = rdMmRelationService.findList("mmCodes", mmCodes);
        }
        List<RdRanks> shopMemberGradeList = rdRanksService.findAll();
        List<IntegrationMemberListResult> integrationMemberListResultList = IntegrationMemberListResult
            .build2(rdMmBasicInfoList, shopMemberGradeList, rdMmAccountLogList, rdMmRelationList);
        return ApiUtils.success(Paramap.create()
            .put("walletBlance", Optional.ofNullable(rdMmAccountInfo.getWalletBlance()).orElse(BigDecimal.valueOf(0)))
            .put("memberList", integrationMemberListResultList));
    }

    //购物积分用户列表
    @RequestMapping(value = "/shp/memberList.json")
    public String memberList(HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        List<RdMmRelation> rdMmRelationList = rdMmRelationService.findList("sponsorCode", member.getMmCode());
        List<String> mmCodes = new ArrayList();
        for (RdMmRelation item : rdMmRelationList) {
            mmCodes.add(item.getMmCode());
        }
        List<RdMmBasicInfo> rdMmBasicInfoList = new ArrayList<>();
        if (mmCodes != null && mmCodes.size() > 0) {
            rdMmBasicInfoList = rdMmBasicInfoService.findList("mmCodes", mmCodes);
        }
        List<RdRanks> shopMemberGradeList = rdRanksService.findAll();
        List<IntegrationMemberListResult> integrationMemberListResultList = IntegrationMemberListResult
            .build(rdMmBasicInfoList, rdMmRelationList, shopMemberGradeList);
        return ApiUtils.success(Paramap.create().put("memberList", integrationMemberListResultList));
    }

    //购物积分搜索用户
    @RequestMapping(value = "/shp/searchMember.json")
    public String shpMemberList(HttpServletRequest request, String info,
        @RequestParam(defaultValue = "1") Integer type) {
        if (StringUtil.isEmpty(info)) {
            return ApiUtils.success(Paramap.create().put("member", new ArrayList<>()));
            //return ApiUtils.error(Xerror.PARAM_INVALID, "参数无效");
        }
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        List<RdMmBasicInfo> rdMmBasicInfoList = new ArrayList<>();
        List<RdMmRelation> rdMmRelationList = new ArrayList<>();
        if (type == 2) {
            rdMmRelationList = rdMmRelationService.findList("sponsorCode", member.getMmCode());
            List<String> mmCodes = new ArrayList();
            for (RdMmRelation item : rdMmRelationList) {
                mmCodes.add(item.getMmCode());
            }
            if (mmCodes != null && mmCodes.size() > 0) {
                rdMmBasicInfoList = rdMmBasicInfoService
                    .findList(Paramap.create().put("mmCodes", mmCodes).put("info", info));
            }

        } else {
            rdMmBasicInfoList = rdMmBasicInfoService.findList(Paramap.create().put("info", info));
            List<String> mmCodes = new ArrayList();
            for (RdMmBasicInfo item : rdMmBasicInfoList) {
                mmCodes.add(item.getMmCode());
            }
            if (mmCodes != null && mmCodes.size() > 0) {
                rdMmRelationList = rdMmRelationService.findList("mmCodes", mmCodes);
            }

        }

        List<RdRanks> shopMemberGradeList = rdRanksService.findAll();

        List<IntegrationMemberListResult> integrationMemberListResult = IntegrationMemberListResult
            .build(rdMmBasicInfoList, rdMmRelationList, shopMemberGradeList);
        return ApiUtils.success(Paramap.create().put("member", integrationMemberListResult));
    }

    //购物积分转出确认
    @RequestMapping(value = "/shp/transferOut/finish.json")
    public String shpTransferOutFinsh(HttpServletRequest request, Long accentMemberId, Integer integration,
        String paypassword, String message) {
        if (accentMemberId == null || integration == null || "".equals(paypassword)) {
            return ApiUtils.error(Xerror.PARAM_INVALID, "参数无效");
        }
        AuthsLoginResult member = (AuthsLoginResult) request
            .getAttribute(com.framework.loippi.consts.Constants.CURRENT_USER);
        RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", member.getMmCode());
        RdMmBasicInfo shopMember = rdMmBasicInfoService.find("mmCode", member.getMmCode());
        if (integration <= 0) {
            return ApiUtils.error("所转积分不合理");
        }
        if (rdMmAccountInfo.getPaymentPwd() == null) {
            return ApiUtils.error("你还未设置支付密码");
        }
        if (!Digests.validatePassword(paypassword, rdMmAccountInfo.getPaymentPwd())) {
            return ApiUtils.error("支付密码错误");
        }
        if (accentMemberId.equals(member.getMmCode())) {
            return ApiUtils.error("不能转给自己");
        }
        //对方信息
        RdMmBasicInfo accentMember = rdMmBasicInfoService.find("mmCode", accentMemberId);

        if (accentMember == null) {
            return ApiUtils.error("对方不存在");
        }
        //对方积分信息
        RdMmAccountInfo accentMmAccountInfo = rdMmAccountInfoService.find("mmCode", accentMember.getMmCode());
        if (accentMmAccountInfo == null) {
            return ApiUtils.error("对方不存在");
        }
        List<RdMmIntegralRule> rdMmIntegralRuleList = rdMmIntegralRuleService
            .findList(Paramap.create().put("order", "RID desc"));
        RdMmIntegralRule rdMmIntegralRule = new RdMmIntegralRule();
        if (rdMmIntegralRuleList != null && rdMmIntegralRuleList.size() > 0) {
            rdMmIntegralRule = rdMmIntegralRuleList.get(0);
        }
        //对方账户添加购物积分
        RdMmAccountLog rdMmAccountLogTF = IntegrationBuildResult
            .WalletTF(accentMember, accentMmAccountInfo, integration, shopMember);
        accentMmAccountInfo.setWalletBlance(accentMmAccountInfo.getWalletBlance().add(BigDecimal.valueOf(integration)));
        rdMmAccountLogTF.setBlanceAfter(accentMmAccountInfo.getWalletBlance());
        //自己账户

        RdMmAccountLog rdMmAccountLogTT = IntegrationBuildResult
            .WalletTT(shopMember, rdMmAccountInfo, integration, accentMember);
        rdMmAccountInfo.setWalletBlance(rdMmAccountInfo.getWalletBlance().subtract(BigDecimal.valueOf(integration)));
        rdMmAccountLogTT.setBlanceAfter(rdMmAccountInfo.getWalletBlance());
        List<RdMmAccountLog> rdMmAccountLogList = new ArrayList<>();
        rdMmAccountLogList.add(rdMmAccountLogTT);
        rdMmAccountLogList.add(rdMmAccountLogTF);
        Integer transNumber = rdMmAccountInfoService
            .saveAccountInfo(rdMmAccountInfo, integration, IntegrationNameConsts.SHP, rdMmAccountLogList,
                accentMmAccountInfo);
        // TODO: 2018/12/28 待实现 
        return ApiUtils.success(Paramap.create().put("transNumber", transNumber).put("transferOutPoints", integration)
            .put("memberName", accentMember.getMmNickName()).put("memberMobile", accentMember.getMobile())
            .put("shpIntegration", rdMmAccountInfo.getWalletBlance()));
    }

    //积分明细列表
    @RequestMapping(value = "/water/list.json")
    public String bopList(HttpServletRequest request, String transTypeCode, String time, Integer type, Pageable pager) {

        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        Map<String, Object> map = new HashMap<>();
        map.put("mmCode", member.getMmCode());
        if (type == 1) {//奖励积分
            if ("-1".equals(transTypeCode)) {
                map.put("bop", "1");
            } else {
                map.put("transTypeCode", transTypeCode);
            }
            if (!StringUtil.isEmpty(time)) {
                map.put("time", time);
            }
        } else if (type == 2) {//购物积分
            map.put("shp", "1");
        } else if (type == 3) {//换购积分
            map.put("pui", "1");
        } else {
            return ApiUtils.error("积分类型出错");
        }
        pager.setParameter(map);
        pager.setOrderDirection(Order.Direction.DESC);
        pager.setOrderProperty("TRANS_DATE");
        List<RdMmAccountLog> rdMmAccountLogList = RdMmAccountLogService.findByPage(pager).getContent();
        return ApiUtils.success(IntegrationListResult.build(rdMmAccountLogList, type));
    }

    //积分明细详情
    @RequestMapping(value = "/water/detail.json")
    public String bopDetail(HttpServletRequest request, Integer transNumber, Integer type) {
        if (transNumber == null || type == null) {
            return ApiUtils.error(Xerror.PARAM_INVALID, "参数无效");
        }
        RdMmAccountLog rdMmAccountLog = RdMmAccountLogService.find("transNumber", transNumber);
        RdMmBasicInfo shopMember = new RdMmBasicInfo();
        if (rdMmAccountLog != null) {
            shopMember = rdMmBasicInfoService.find("mmCode", rdMmAccountLog.getTrMmCode());
        }
        int proportion = 0;
        int tax = 0;
        RdMmIntegralRule rdMmIntegralRule = new RdMmIntegralRule();
        if (type != 3) {
            List<RdMmIntegralRule> rdMmIntegralRuleList = rdMmIntegralRuleService
                .findList(Paramap.create().put("order", "RID desc"));
            if (rdMmIntegralRuleList != null && rdMmIntegralRuleList.size() > 0) {
                rdMmIntegralRule = rdMmIntegralRuleList.get(0);
            }
        }

        return ApiUtils.success(IntegrationDetailResult.build(rdMmAccountLog, shopMember, rdMmIntegralRule, type));
    }


}

