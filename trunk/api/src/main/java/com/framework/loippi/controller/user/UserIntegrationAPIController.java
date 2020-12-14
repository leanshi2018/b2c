package com.framework.loippi.controller.user;


import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.framework.loippi.consts.IntegrationNameConsts;
import com.framework.loippi.controller.BaseController;
import com.framework.loippi.dao.user.RdSysPeriodDao;
import com.framework.loippi.entity.ShopCommonMessage;
import com.framework.loippi.entity.ShopMemberMessage;
import com.framework.loippi.entity.companyInfo.CompanyLicense;
import com.framework.loippi.entity.integration.RdMmIntegralRule;
import com.framework.loippi.entity.user.MemberQualification;
import com.framework.loippi.entity.user.RdMmAccountInfo;
import com.framework.loippi.entity.user.RdMmAccountLog;
import com.framework.loippi.entity.user.RdMmBank;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.entity.user.RdMmRemark;
import com.framework.loippi.entity.user.RdRanks;
import com.framework.loippi.entity.user.RdSysPeriod;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.result.auths.AuthsLoginResult;
import com.framework.loippi.result.integral.BopTransMemResult;
import com.framework.loippi.result.user.IntegrationBuildResult;
import com.framework.loippi.result.user.IntegrationDetailResult;
import com.framework.loippi.result.user.IntegrationListResult;
import com.framework.loippi.result.user.IntegrationMemberListResult;
import com.framework.loippi.result.user.UserIntegrationListResult;
import com.framework.loippi.service.companyInfo.CompanyLicenseService;
import com.framework.loippi.service.integration.RdMmIntegralRuleService;
import com.framework.loippi.service.order.ShopOrderService;
import com.framework.loippi.service.user.MemberQualificationService;
import com.framework.loippi.service.user.RdMmAccountInfoService;
import com.framework.loippi.service.user.RdMmAccountLogService;
import com.framework.loippi.service.user.RdMmBankService;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.service.user.RdMmRelationService;
import com.framework.loippi.service.user.RdMmRemarkService;
import com.framework.loippi.service.user.RdNewVipDetailService;
import com.framework.loippi.service.user.RdRanksService;
import com.framework.loippi.service.user.RetailProfitService;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.ApiUtils;
import com.framework.loippi.utils.Constants;
import com.framework.loippi.utils.Digests;
import com.framework.loippi.utils.HttpUtils;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.StringUtil;
import com.framework.loippi.utils.Xerror;
import com.framework.loippi.utils.XmlUtils;
import com.framework.loippi.utils.wechat.mobile.config.WXpayConfig;
import com.framework.loippi.utils.wechat.mobile.util.CollectionUtil;
import com.framework.loippi.utils.wechat.mobile.util.JsonResult;
import com.framework.loippi.utils.wechat.mobile.util.ResponseData;
import com.framework.loippi.utils.wechat.mobile.util.SerializerFeatureUtil;
import com.framework.loippi.utils.wechat.mobile.util.WeixinUtils;

/**
 * 积分 Created by Administrator on 2017/11/23.
 */
@Controller
@ResponseBody
@RequestMapping("/api/integration")
public class UserIntegrationAPIController extends BaseController {
    @Resource
    RdMmAccountLogService rdMmAccountLogService;
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
    @Resource
    private MemberQualificationService memberQualificationService;
    @Resource
    private RetailProfitService retailProfitService;
    @Resource
    private RdSysPeriodDao sysPeriodDao;
    @Resource
    private ShopOrderService shopOrderService;
    @Resource
    private CompanyLicenseService companyLicenseService;
    @Resource
    private RdMmRemarkService rdMmRemarkService;
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

    //奖励积分转出确认
    @RequestMapping(value = "/bop/transferOut/finishNew.json")
    public String transferOutFinishNew(HttpServletRequest request, Double integration, String paypassword) {
        if (integration == null || "".equals(paypassword)) {
            return ApiUtils.error(Xerror.PARAM_INVALID, "参数无效");
        }
        AuthsLoginResult member = (AuthsLoginResult) request
                .getAttribute(com.framework.loippi.consts.Constants.CURRENT_USER);
        RdMmBasicInfo shopMember = rdMmBasicInfoService.find("mmCode", member.getMmCode());
        RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", member.getMmCode());
        String format = new DecimalFormat("#.00").format(integration);
        BigDecimal amount = new BigDecimal(format);
        System.out.println(format);
        if (integration <= 0) {
            return ApiUtils.error("所转积分不合理");
        }
        if (rdMmAccountInfo.getPaymentPwd() == null) {
            return ApiUtils.error("你还未设置支付密码");
        }
        if (!Digests.validatePassword(paypassword, rdMmAccountInfo.getPaymentPwd())) {
            return ApiUtils.error("支付密码错误");
        }

        if (rdMmAccountInfo.getBonusBlance().compareTo(amount) == -1) {
            return ApiUtils.error("转出积分大于可转出积分");
        }
        List<RdMmIntegralRule> rdMmIntegralRuleList = rdMmIntegralRuleService
                .findList(Paramap.create().put("order", "RID desc"));
        RdMmIntegralRule rdMmIntegralRule = new RdMmIntegralRule();
        if (rdMmIntegralRuleList != null && rdMmIntegralRuleList.size() > 0) {
            rdMmIntegralRule = rdMmIntegralRuleList.get(0);
        }
        /*BigDecimal walletBlance = BigDecimal
                .valueOf(integration * Optional.ofNullable(rdMmIntegralRule.getBonusPointShopping()).orElse(0) * 0.01);*/
        BigDecimal walletBlance = amount.multiply(new BigDecimal(rdMmIntegralRule.getBonusPointShopping().toString())).multiply(new BigDecimal("0.01")).setScale(2, BigDecimal.ROUND_HALF_UP);
        List<RdMmAccountLog> rdMmAccountLogList = new ArrayList<>();
        RdMmAccountLog rdMmAccountLogSP = IntegrationBuildResult
                .bonusSPNew(shopMember, rdMmAccountInfo, amount.doubleValue(), walletBlance);
        RdMmAccountLog rdMmAccountLogBT = IntegrationBuildResult.WalletBT(shopMember, rdMmAccountInfo, walletBlance);
        rdMmAccountLogList.add(rdMmAccountLogSP);
        rdMmAccountLogList.add(rdMmAccountLogBT);
        ArrayList<ShopCommonMessage> shopCommonMessages = new ArrayList<>();
        ArrayList<ShopMemberMessage> shopMemberMessages = new ArrayList<>();
        ShopCommonMessage commonMessage1 = new ShopCommonMessage();
        Long msgid = twiterIdService.getTwiterId();
        commonMessage1.setId(msgid);
        commonMessage1.setTitle("积分转换");
        commonMessage1.setBizId(0L);
        commonMessage1.setBizType(2);
        commonMessage1.setContent("您已成功将"+amount+"点奖励积分转换为"+walletBlance+"点购物积分，祝您购物愉快");
        commonMessage1.setCreateTime(new Date());
        commonMessage1.setSendUid(member.getMmCode());
        commonMessage1.setType(1);
        commonMessage1.setOnLine(1);
        commonMessage1.setIsTop(1);
        shopCommonMessages.add(commonMessage1);
        ShopMemberMessage shopMemberMessage = new ShopMemberMessage();
        shopMemberMessage.setId(twiterIdService.getTwiterId());
        shopMemberMessage.setMsgId(msgid);
        shopMemberMessage.setBizType(2);
        shopMemberMessage.setIsRead(0);
        shopMemberMessage.setCreateTime(new Date());
        shopMemberMessage.setUid(Long.parseLong(member.getMmCode()));
        shopMemberMessages.add(shopMemberMessage);
        rdMmAccountInfo.setBonusBlance(rdMmAccountInfo.getBonusBlance().subtract(amount));
        rdMmAccountInfo.setWalletBlance(rdMmAccountInfo.getWalletBlance().add(walletBlance));
        Integer transNumber = rdMmAccountInfoService
                .saveAccountInfoNew(rdMmAccountInfo,amount.doubleValue(),IntegrationNameConsts.BOP, rdMmAccountLogList, null,shopCommonMessages,shopMemberMessages);
        return ApiUtils.success(Paramap.create().put("transNumber", transNumber).put("transferOutPoints", amount.doubleValue())
                .put("bopIntegration", rdMmAccountInfo.getBonusBlance().setScale(2,BigDecimal.ROUND_HALF_UP)).put("transferInPoints", walletBlance.setScale(2,BigDecimal.ROUND_HALF_UP))
                .put("shpIntegration", rdMmAccountInfo.getWalletBlance().setScale(2,BigDecimal.ROUND_HALF_UP)));
    }

    //int bankCardId,
    //奖励积分提现确认
    @RequestMapping(value = "/bop/cashWithdrawal/finish.json")
    public String bopCashWithdrawal(HttpServletRequest request,  Double integration, String paypassword) {
        //TODO
        //return ApiUtils.error("抱歉，手动提现暂停使用，请先使用自动提现");

        if ("".equals(paypassword)) {
            return ApiUtils.error(Xerror.PARAM_INVALID, "参数无效");
        }

        if (integration <= 0) {
            return ApiUtils.error("提现积分不合理");
        }

        BigDecimal i = new BigDecimal(integration).setScale(2, RoundingMode.HALF_UP);
        integration = i.doubleValue();

        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        RdMmBasicInfo shopMember = rdMmBasicInfoService.find("mmCode", member.getMmCode());
        RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", member.getMmCode());
        if (rdMmAccountInfo.getPaymentPwd() == null) {
            return ApiUtils.error("你还未设置支付密码");
        }

        if (!Digests.validatePassword(paypassword, rdMmAccountInfo.getPaymentPwd())) {
            return ApiUtils.error("支付密码错误");
        }

        System.out.println("1="+rdMmAccountInfo.getBonusBlance());
        System.out.println("2="+integration);
        if (rdMmAccountInfo.getBonusBlance().compareTo(new BigDecimal(integration).setScale(2,RoundingMode.HALF_UP))==-1) {
            return ApiUtils.error("积分不足，不可提现");
        }

        List<RdMmAccountLog> list = rdMmAccountLogService.findList(Paramap.create().put("mmCode",member.getMmCode()).put("transTypeCode","WD").put("status",2));
        if(list!=null&&list.size()>0){
            return ApiUtils.error("您已有一笔提现申请待审核，如需要，取消后重新提交");
        }
        List<RdMmBank> mmBanks = rdMmBankService.findList(Paramap.create().put("mmCode",member.getMmCode()).put("inValid",1).put("defaultbank",1));
        //银行卡信息
        if (mmBanks.size()<=0){
            return ApiUtils.error("不存在该银行卡");
        }
        RdMmBank rdMmBank = mmBanks.get(0);
        if (rdMmBank == null) {
            return ApiUtils.error("不存在该银行卡");
        }
        /*if (rdMmBank.getBankSigning()==0){
            if (rdMmBank.getSigningStatus()==1){
                return ApiUtils.error("该银行卡签约正在审核中，审核通过后再提现");
            }
            return ApiUtils.error("该银行卡还未签约或签约失败");
        }*/

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
        RdMmAccountLog rdMmAccountLog = IntegrationBuildResult.bonusWD(shopMember, rdMmAccountInfo, integration, bonusPointWd, mmBanks.get(0));
        //RdMmAccountLog rdMmAccountLog = IntegrationBuildResult.bonusWD(shopMember, rdMmAccountInfo, integration, bonusPointWd);
        rdMmAccountLogList.add(rdMmAccountLog);
        rdMmAccountInfo.setBonusBlance(rdMmAccountInfo.getBonusBlance().subtract(BigDecimal.valueOf(integration)));
        ArrayList<ShopCommonMessage> shopCommonMessages = new ArrayList<>();
        ArrayList<ShopMemberMessage> shopMemberMessages = new ArrayList<>();
        ShopCommonMessage commonMessage1 = new ShopCommonMessage();
        Long msgid = twiterIdService.getTwiterId();
        commonMessage1.setId(msgid);
        commonMessage1.setTitle("积分提现");
        commonMessage1.setBizId(0L);
        commonMessage1.setBizType(2);
        commonMessage1.setContent("您已申请提现扣除"+integration+"点奖励积分，请在奖励积分账户查看明细");
        commonMessage1.setCreateTime(new Date());
        commonMessage1.setSendUid(member.getMmCode());
        commonMessage1.setType(1);
        commonMessage1.setOnLine(1);
        commonMessage1.setIsTop(1);
        shopCommonMessages.add(commonMessage1);
        ShopMemberMessage shopMemberMessage = new ShopMemberMessage();
        shopMemberMessage.setId(twiterIdService.getTwiterId());
        shopMemberMessage.setMsgId(msgid);
        shopMemberMessage.setBizType(2);
        shopMemberMessage.setIsRead(0);
        shopMemberMessage.setCreateTime(new Date());
        shopMemberMessage.setUid(Long.parseLong(member.getMmCode()));
        shopMemberMessages.add(shopMemberMessage);
        Integer transNumber = rdMmAccountInfoService.saveAccountInfoNew(rdMmAccountInfo, integration, IntegrationNameConsts.BOP, rdMmAccountLogList, null, shopCommonMessages, shopMemberMessages);
        // TODO: 2018/12/28 待处理
        /*return ApiUtils.success(Paramap.create().put("bankCardCode",
            "****     ****     ****     " + rdMmBank.getAccCode().substring(rdMmBank.getAccCode().length() - 4))
            .put("transferOutMoney", integration)
            .put("bopIntegration", rdMmAccountInfo.getBonusBlance()));*/
        return ApiUtils.success(Paramap.create()
                .put("presentationFeeNow", rdMmAccountLog.getPresentationFeeNow())
                .put("actualWithdrawals", rdMmAccountLog.getActualWithdrawals())
                .put("transferOutMoney", integration)
                .put("bopIntegration", rdMmAccountInfo.getBonusBlance()));
        //return ApiUtils.error("该功能在升级，请耐心等待！");
    }

    //取消奖励积分提现申请
    @RequestMapping(value = "/bop/CancellWD.json")
    public String bopCancellWD(HttpServletRequest request, Integer transNumber) {

        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        RdMmAccountLog accountLog = RdMmAccountLogService.findByTransNumber(transNumber);
        if (accountLog==null){
            return ApiUtils.error("该提现申请不存在");
        }
        BigDecimal amount = accountLog.getAmount();//提现金额
        String mmCode = accountLog.getMmCode();
        RdMmAccountInfo accountInfo = rdMmAccountInfoService.find("mmCode", mmCode);
        BigDecimal bonusBlance = accountInfo.getBonusBlance();//奖励账户余额
        //取消提现申请
        int i = RdMmAccountLogService.updateCancellWD(transNumber);
        if (i==1){
            bonusBlance = bonusBlance.add(amount);
            //修改奖励积分余额
            rdMmAccountInfoService.updateAddBonusBlance(mmCode,bonusBlance);
            return ApiUtils.success();
        }else{
            return ApiUtils.error("取消失败");
        }
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

        //备注名
        Map<String,String> remarkMap = new HashMap<String,String>();
        List<RdMmRemark> remarkList = rdMmRemarkService.findByMmCode(member.getMmCode());
        if (remarkList.size()>0){
            for (RdMmRemark remark : remarkList) {
                remarkMap.put(remark.getSpCode(),remark.getRemarkName());
            }
        }

        List<RdRanks> shopMemberGradeList = rdRanksService.findAll();
        List<IntegrationMemberListResult> integrationMemberListResultList = IntegrationMemberListResult
            .build2(rdMmBasicInfoList, shopMemberGradeList, rdMmAccountLogList, rdMmRelationList,remarkMap);
        return ApiUtils.success(Paramap.create()
            .put("walletBlance", Optional.ofNullable(rdMmAccountInfo.getWalletBlance()).orElse(BigDecimal.valueOf(0)))
            .put("memberList", integrationMemberListResultList));
    }

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

        //备注名
        Map<String,String> remarkMap = new HashMap<String,String>();
        List<RdMmRemark> remarkList = rdMmRemarkService.findByMmCode(member.getMmCode());
        if (remarkList.size()>0){
            for (RdMmRemark remark : remarkList) {
                remarkMap.put(remark.getSpCode(),remark.getRemarkName());
            }
        }

        List<IntegrationMemberListResult> integrationMemberListResultList = IntegrationMemberListResult
                .build(rdMmBasicInfoList, rdMmRelationList, shopMemberGradeList,remarkMap);
        return ApiUtils.success(Paramap.create().put("memberList", integrationMemberListResultList));
    }
    private ArrayList<String> findTreePeriod(String periodCode){
        ArrayList<String> strings = new ArrayList<>();
        strings.add(periodCode);
        RdSysPeriod sysPeriod=sysPeriodDao.findByPeriodCode(periodCode);
        if(sysPeriod!=null&&sysPeriod.getPrePeriod()!=null&&!"".equals(sysPeriod.getPrePeriod())){
            strings.add(sysPeriod.getPrePeriod());
            RdSysPeriod sysPeriod1=sysPeriodDao.findByPeriodCode(sysPeriod.getPrePeriod());
            if(sysPeriod1!=null&&sysPeriod1.getPrePeriod()!=null&&!"".equals(sysPeriod1.getPrePeriod())){
                strings.add(sysPeriod1.getPrePeriod());
            }
        }
        return strings;
    }




    /**
     * //购物积分用户列表
     * @param request
     * @param periodCode 周期编号
     * @param sorting 排序种类 1：按mi值升序 2：按mi值降序  3：按加入时间升序 4.按加入时间降序 5.按会员级别升序  6.按会员级别降序 7.按照已发放零售利润升序 8.按照已发放零售利润降序
     * @return
     */
    @RequestMapping(value = "/shp/memberListNew.json")
    public String memberListNew(HttpServletRequest request,
                             @RequestParam(value = "periodCode",required = false) String periodCode,
                             @RequestParam(value = "sorting",required = true)Integer sorting) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        Paramap paramap = Paramap.create();
        ArrayList<String> treePeriod = new ArrayList<String>();
        String code="";
        if(periodCode==null||"".equals(periodCode)){//如果传入周期参数为空，则查询当前周期

            RdSysPeriod sysPeriod = sysPeriodDao.getPeriodService(new Date());
            if(sysPeriod!=null){
                code=sysPeriod.getPeriodCode();
                treePeriod = findTreePeriod(sysPeriod.getPeriodCode());
            }else {//当前时间没有周期，查询最近的一个周期
                RdSysPeriod lastPeriod=sysPeriodDao.findLastPeriod();
                code=lastPeriod.getPeriodCode();
                treePeriod = findTreePeriod(lastPeriod.getPeriodCode());
            }
            paramap.put("periodCodeList",treePeriod);
        }else {
            code=periodCode;
        }
        //List<MemberQualification> list = memberQualificationService.findList(Paramap.create().put("sponsorCode",member.getMmCode()).put("periodCode",periodCode));
        /*HashMap<String, Object> map1 = new HashMap<>();
        map1.put("sponsorCode",member.getMmCode());
        //map1.put("sponsorCode","900000011");
        map1.put("periodCode",code);*/
        //List<MemberQualification> list =memberQualificationService.findBySponsorCodeAndPeriodCode(map1);
        //List<MemberQualification> list = memberQualificationService.findList(Paramap.create().put("sponsorCode","900000011").put("periodCode",periodCode));
        //if(list==null||list.size()==0){
        //   ArrayList<MemberQualification> list1 = new ArrayList<>();
        //  paramap.put("memberList",list1);
        //   return ApiUtils.success(paramap);
        //}
        //List<String> mmCodes = new ArrayList();
        //for (MemberQualification item : list) {
        //    mmCodes.add(item.getMCode());
        //}
        //List<RdMmBasicInfo> rdMmBasicInfoList = new ArrayList<>();
        //List<RdMmRelation> rdMmRelationList = rdMmRelationService.findList("sponsorCode", member.getMmCode());
        List<RdMmRelation> rdMmRelationList = rdMmRelationService.findBySponsorCode( member.getMmCode());
        ArrayList<String> mmCodes = new ArrayList<>();
        if(rdMmRelationList!=null&&rdMmRelationList.size()>0){
            for (RdMmRelation rdMmRelation : rdMmRelationList) {
                mmCodes.add(rdMmRelation.getMmCode());
            }
        }
        List<RdMmBasicInfo> rdMmBasicInfoList = new ArrayList<>();
        if (mmCodes != null && mmCodes.size() > 0) {
            rdMmBasicInfoList = rdMmBasicInfoService.findList("mmCodes", mmCodes);
            //rdMmRelationList = rdMmRelationService.findList("mmCodes", mmCodes);
        }
        //查询从每一个会员获得的零售利润
        HashMap<String, BigDecimal> hashMap = new HashMap<>();
        for (String mmCode : mmCodes) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("buyerId",mmCode);
            map.put("receiptorId",member.getMmCode());
            map.put("createPeriod",code);
            //map.put("state",1);
            BigDecimal result=retailProfitService.findTotalProfit(map);
            if(result!=null){
                hashMap.put(mmCode,result);
            }else {
                hashMap.put(mmCode,BigDecimal.ZERO);
            }
        }
        List<RdRanks> shopMemberGradeList = rdRanksService.findAll();
        List<IntegrationMemberListResult> integrationMemberListResultList = IntegrationMemberListResult
                .build4(rdMmBasicInfoList, rdMmRelationList, shopMemberGradeList,sorting,hashMap);
        paramap.put("memberList", integrationMemberListResultList);
        return ApiUtils.success(paramap);
    }

    /**
     * //购物积分用户列表
     * @param request
     * @param periodCode 周期编号
     * @param sorting 排序种类 1：按mi值升序 2：按mi值降序  3：按加入时间升序 4.按加入时间降序 5.按会员级别升序  6.按会员级别降序 7.按照已发放零售利润升序 8.按照已发放零售利润降序
     * @return
     */
    @RequestMapping(value = "/shp/memberListNew1.json")
    public String memberListNew1(HttpServletRequest request,
                             @RequestParam(value = "periodCode",required = false) String periodCode,
                             @RequestParam(value = "sorting",required = true)Integer sorting) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        Paramap paramap = Paramap.create();
        ArrayList<String> treePeriod = new ArrayList<String>();
        String code="";
        if(periodCode==null||"".equals(periodCode)){//如果传入周期参数为空，则查询当前周期

            RdSysPeriod sysPeriod = sysPeriodDao.getPeriodService(new Date());
            if(sysPeriod!=null){
                code=sysPeriod.getPeriodCode();
                treePeriod = findTreePeriod(sysPeriod.getPeriodCode());
            }else {//当前时间没有周期，查询最近的一个周期
                RdSysPeriod lastPeriod=sysPeriodDao.findLastPeriod();
                code=lastPeriod.getPeriodCode();
                treePeriod = findTreePeriod(lastPeriod.getPeriodCode());
            }
            paramap.put("periodCodeList",treePeriod);
        }else {
            code=periodCode;
        }
        HashMap<String, Object> map1 = new HashMap<>();
        map1.put("sponsorCode",member.getMmCode());
        map1.put("periodCode",code);
        List<MemberQualification> list = memberQualificationService.findBySponsorCodeAndPeriodCode(map1);
        List<MemberQualification> qualificationList = memberQualificationService.findList(Paramap.create().put("mCode",member.getMmCode()).put("periodCode",code));
        if(qualificationList.size()>0){
            MemberQualification qualification = qualificationList.get(0);

            //Integer rank1Number = Optional.ofNullable(qualification.getDdRank1Number()).orElse(0);//累计直邀VIP人数
            Integer rank2Number = Optional.ofNullable(qualification.getDdRank2Number()).orElse(0);//累计直邀代理人数
            Integer ddAcNumber = Optional.ofNullable(qualification.getDdAcNumber()).orElse(0);//复消人数

            //paramap.put("rank1Number",rank1Number);//直邀
            paramap.put("rank2Number",rank2Number);//代理
            paramap.put("ddAcNumber",ddAcNumber);//复消
        }else {
            //paramap.put("rank1Number",0);//直邀
            paramap.put("rank2Number",0);//代理
            paramap.put("ddAcNumber",0);//复消
        }

        Integer rank1Number = rdMmRelationService.findSponCountByMCode(member.getMmCode());
        if (rank1Number==null){
            paramap.put("rank1Number",0);//直邀
        }else {
            paramap.put("rank1Number",rank1Number);//直邀
        }

        /*HashMap<String, Object> map1 = new HashMap<>();
        map1.put("sponsorCode",member.getMmCode());
        //map1.put("sponsorCode","900000011");
        map1.put("periodCode",code);*/
        //List<MemberQualification> list =memberQualificationService.findBySponsorCodeAndPeriodCode(map1);
        //List<MemberQualification> list = memberQualificationService.findList(Paramap.create().put("sponsorCode","900000011").put("periodCode",periodCode));
        //if(list==null||list.size()==0){
        //   ArrayList<MemberQualification> list1 = new ArrayList<>();
        //  paramap.put("memberList",list1);
        //   return ApiUtils.success(paramap);
        //}
        //List<String> mmCodes = new ArrayList();
        //for (MemberQualification item : list) {
        //    mmCodes.add(item.getMCode());
        //}
        //List<RdMmBasicInfo> rdMmBasicInfoList = new ArrayList<>();
        //List<RdMmRelation> rdMmRelationList = rdMmRelationService.findList("sponsorCode", member.getMmCode());
        List<RdMmRelation> rdMmRelationList = rdMmRelationService.findBySponsorCode( member.getMmCode());
        ArrayList<String> mmCodes = new ArrayList<>();
        if(rdMmRelationList!=null&&rdMmRelationList.size()>0){
            for (RdMmRelation rdMmRelation : rdMmRelationList) {
                mmCodes.add(rdMmRelation.getMmCode());
            }
        }
        List<RdMmBasicInfo> rdMmBasicInfoList = new ArrayList<>();
        if (mmCodes != null && mmCodes.size() > 0) {
            rdMmBasicInfoList = rdMmBasicInfoService.findList("mmCodes", mmCodes);
            //rdMmRelationList = rdMmRelationService.findList("mmCodes", mmCodes);
        }
        //查询从每一个会员获得的零售利润
        HashMap<String, BigDecimal> hashMap = new HashMap<>();
        for (String mmCode : mmCodes) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("buyerId",mmCode);
            map.put("receiptorId",member.getMmCode());
            map.put("createPeriod",code);
            //map.put("state",1);
            BigDecimal result=retailProfitService.findTotalProfit(map);
            if(result!=null){
                hashMap.put(mmCode,result);
            }else {
                hashMap.put(mmCode,BigDecimal.ZERO);
            }
        }
        List<RdRanks> shopMemberGradeList = rdRanksService.findAll();

        //备注名
        Map<String,String> remarkMap = new HashMap<String,String>();
        List<RdMmRemark> remarkList = rdMmRemarkService.findByMmCode(member.getMmCode());
        if (remarkList.size()>0){
            for (RdMmRemark remark : remarkList) {
                remarkMap.put(remark.getSpCode(),remark.getRemarkName());
            }
        }

        List<IntegrationMemberListResult> integrationMemberListResultList = IntegrationMemberListResult
                .build5(rdMmBasicInfoList, rdMmRelationList, shopMemberGradeList,sorting,hashMap,list,remarkMap);
        paramap.put("memberList", integrationMemberListResultList);
        return ApiUtils.success(paramap);
    }

    /**
     *
     * @param request
     * @param info
     * @param type 1搜索全部 2搜索自己
     * @return
     */
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
                /*rdMmBasicInfoList = rdMmBasicInfoService
                        .findByKeyWord(Paramap.create().put("mmCodes", mmCodes).put("info", info));*/
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

        //备注名
        Map<String,String> remarkMap = new HashMap<String,String>();
        List<RdMmRemark> remarkList = rdMmRemarkService.findByMmCode(member.getMmCode());
        if (remarkList.size()>0){
            for (RdMmRemark remark : remarkList) {
                remarkMap.put(remark.getSpCode(),remark.getRemarkName());
            }
        }

        List<IntegrationMemberListResult> integrationMemberListResult = IntegrationMemberListResult
            .build(rdMmBasicInfoList, rdMmRelationList, shopMemberGradeList,remarkMap);

        return ApiUtils.success(Paramap.create().put("member", integrationMemberListResult));
    }

    //购物积分转出确认
    @RequestMapping(value = "/shp/transferOut/finish.json")
    public String shpTransferOutFinsh(HttpServletRequest request, Long accentMemberId, Double integration,
        String paypassword, String message) {
        if (accentMemberId == null || integration == null || "".equals(paypassword)) {
            return ApiUtils.error(Xerror.PARAM_INVALID, "参数无效");
        }
        AuthsLoginResult member = (AuthsLoginResult) request
            .getAttribute(com.framework.loippi.consts.Constants.CURRENT_USER);
        RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", member.getMmCode());
        RdMmBasicInfo shopMember = rdMmBasicInfoService.find("mmCode", member.getMmCode());
        if(shopMember.getMmCode().equals(accentMemberId+"")){
            return ApiUtils.error(Xerror.PARAM_INVALID, "转出账户不能是自己");
        }
        BigDecimal decimal = new BigDecimal(integration.toString());
        if(rdMmAccountInfo.getWalletBlance().compareTo(decimal)==-1){
            return ApiUtils.error("转出购物积分金额大于购物积分余额");
        }
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
        ArrayList<ShopCommonMessage> shopCommonMessages = new ArrayList<>();
        ArrayList<ShopMemberMessage> shopMemberMessages = new ArrayList<>();
        //转出者积分账户消息通知生成
        ShopCommonMessage commonMessage1 = new ShopCommonMessage();
        Long msgid = twiterIdService.getTwiterId();
        commonMessage1.setId(msgid);
        commonMessage1.setTitle("积分扣减");
        commonMessage1.setBizId(0L);
        commonMessage1.setBizType(2);
        commonMessage1.setContent("您已成功将"+integration+"点购物积分转给"+accentMemberId+"会员");
        commonMessage1.setCreateTime(new Date());
        commonMessage1.setSendUid(member.getMmCode());
        commonMessage1.setType(1);
        commonMessage1.setOnLine(1);
        commonMessage1.setIsTop(1);
        shopCommonMessages.add(commonMessage1);
        ShopMemberMessage shopMemberMessage = new ShopMemberMessage();
        shopMemberMessage.setId(twiterIdService.getTwiterId());
        shopMemberMessage.setMsgId(msgid);
        shopMemberMessage.setBizType(2);
        shopMemberMessage.setIsRead(0);
        shopMemberMessage.setCreateTime(new Date());
        shopMemberMessage.setUid(Long.parseLong(member.getMmCode()));
        shopMemberMessages.add(shopMemberMessage);
        //接收者积分账户消息通知生成
        ShopCommonMessage commonMessage2 = new ShopCommonMessage();
        Long msgid1 = twiterIdService.getTwiterId();
        commonMessage2.setId(msgid1);
        commonMessage2.setTitle("积分到账");
        commonMessage2.setBizId(0L);
        commonMessage2.setBizType(2);
        commonMessage2.setContent("您已成功收到"+member.getMmCode()+"会员转来的购物积分"+integration+"点，请在购物积分账户查看明细");
        commonMessage2.setCreateTime(new Date());
        commonMessage2.setSendUid(accentMemberId+"");
        commonMessage2.setType(1);
        commonMessage2.setOnLine(1);
        commonMessage2.setIsTop(1);
        shopCommonMessages.add(commonMessage2);
        ShopMemberMessage shopMemberMessage2 = new ShopMemberMessage();
        shopMemberMessage2.setId(twiterIdService.getTwiterId());
        shopMemberMessage2.setMsgId(msgid1);
        shopMemberMessage2.setBizType(2);
        shopMemberMessage2.setIsRead(0);
        shopMemberMessage2.setCreateTime(new Date());
        shopMemberMessage2.setUid(accentMemberId);
        shopMemberMessages.add(shopMemberMessage2);
        Integer transNumber = rdMmAccountInfoService
            .saveAccountInfoNew(rdMmAccountInfo, integration, IntegrationNameConsts.SHP, rdMmAccountLogList,
                accentMmAccountInfo, shopCommonMessages, shopMemberMessages);
        // TODO: 2018/12/28 待实现 
        return ApiUtils.success(Paramap.create().put("transNumber", transNumber).put("transferOutPoints", integration)
            .put("memberName", accentMember.getMmNickName()).put("memberMobile", accentMember.getMobile())
            .put("shpIntegration", rdMmAccountInfo.getWalletBlance().setScale(2,BigDecimal.ROUND_HALF_UP)));
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
            }else if("WD".equals(transTypeCode)){
                map.put("wd", "1");
            }else if ("TT".equals(transTypeCode)){
                map.put("tt", "1");
            }
            else if ("TF".equals(transTypeCode)){
                map.put("tf", "1");
            }
            else {
                map.put("transTypeCode", transTypeCode);
            }
        } else if (type == 2) {//购物积分
            map.put("shp", "1");
        } else if (type == 3) {//换购积分
            map.put("pui", "1");
        } else {
            return ApiUtils.error("积分类型出错");
        }
        if (!StringUtil.isEmpty(time)) {
            map.put("time", time);
        }
        pager.setParameter(map);
        pager.setOrderDirection(Order.Direction.DESC);
        pager.setOrderProperty("TRANS_DATE");
        Long count = rdMmAccountLogService.count(map);
        int pageSize = pager.getPageSize();
        long l = count % pageSize;
        long totalPageNum=0;
        if(l==0){
             totalPageNum = count / pageSize;
        }else {
             totalPageNum = count / pageSize + 1;
        }
        List<RdMmAccountLog> rdMmAccountLogList = RdMmAccountLogService.findByPage(pager).getContent();
        return ApiUtils.success(IntegrationListResult.build(rdMmAccountLogList, type,totalPageNum));
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

    //积分提现绑卡查询
    @RequestMapping(value = "/getBank.json")
    public String getBank(HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if(member==null){
            return ApiUtils.error("用户未登录");
        }
        List<RdMmBank> list = rdMmBankService.findList(Paramap.create().put("mmCode",member.getMmCode()).put("defaultbank",1));
        if(list!=null&&list.size()>0){
            return ApiUtils.success(list.get(0));
        }
        return ApiUtils.error("该用户没有设置默认提现银行卡");
    }

    private static final Logger log = Logger.getLogger(UserIntegrationAPIController.class);

    /**
     * 微信企业向个人支付转账
     * @param request
     * @param response
     * @param openid
     * @param amount
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/bop/transferPay.json",method = RequestMethod.POST)
    public String transferPay(HttpServletRequest request, HttpServletResponse response, String openid,Integer amount) throws UnsupportedEncodingException {

        if (openid==null || "".equals(openid)){
            return ApiUtils.error("微信授权失败");
        }

        if (amount==null){
            return ApiUtils.error("提现金额有误，请输入正确的提现金额");
        }

        Integer Qb = amount*100;

        Map<String, String> restmap = null;
        try {
            Map<String, String> parm = new HashMap<String, String>();
            parm.put("mch_appid", WXpayConfig.APP_ID); //公众账号appid
            parm.put("mchid", WXpayConfig.MCH_ID); //商户号
            parm.put("nonce_str", WeixinUtils.createNoncestr()); //随机字符串
            parm.put("partner_trade_no", WeixinUtils.getTransferNo()); //商户订单号
            parm.put("openid", openid); //用户openid
            parm.put("check_name", "NO_CHECK"); //校验用户姓名选项 OPTION_CHECK
            //parm.put("re_user_name", "安迪"); //check_name设置为FORCE_CHECK或OPTION_CHECK，则必填
            parm.put("amount", Qb.toString()); //转账金额  单位分
            parm.put("desc", "测试转账到个人"); //企业付款描述信息
            parm.put("spbill_create_ip", WeixinUtils.getLocalIp(request)); //服务器Ip地址
            parm.put("sign", WeixinUtils.getSign(parm, WXpayConfig.API_KEY));


            String restxml = HttpUtils.posts(WeixinUtils.TRANSFERS_PAY, XmlUtils.xmlFormat(parm, false));
            restmap = XmlUtils.xmlParse(restxml);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        System.out.println("-------------------------------------------------------");
        System.out.println("-------------------" + restmap + "------------------------------------");
        System.out.println("-------------------------------------------------------");

        if (CollectionUtil.isNotEmpty(restmap) && "SUCCESS".equals(restmap.get("result_code"))) {
            log.info("转账成功：" + restmap.get("err_code") + ":" + restmap.get("err_code_des"));
            Map<String, String> transferMap = new HashMap<>();
            transferMap.put("partner_trade_no", restmap.get("partner_trade_no"));//商户转账订单号
            transferMap.put("payment_no", restmap.get("payment_no")); //微信订单号
            transferMap.put("payment_time", restmap.get("payment_time")); //微信支付成功时间

            return ApiUtils.success(JSON.toJSONString(new JsonResult(1, "转账成功", new ResponseData(null, transferMap)),
                    SerializerFeatureUtil.FEATURES));
            /*WebUtil.response(response,
                    WebUtil.packJsonp(callback,
                            JSON.toJSONString(new JsonResult(1, "转账成功", new ResponseData(null, transferMap)),
                                    SerializerFeatureUtil.FEATURES)));*/
        }else {
            if (CollectionUtil.isNotEmpty(restmap)) {
                log.info("转账失败：" + restmap.get("err_code") + ":" + restmap.get("err_code_des"));
            }
            return ApiUtils.error(JSON
                    .toJSONString(new JsonResult(-1, "转账失败", new ResponseData()), SerializerFeatureUtil.FEATURES));
            /*WebUtil.response(response, WebUtil.packJsonp(callback, JSON
                    .toJSONString(new JsonResult(-1, "转账失败", new ResponseData()), SerializerFeatureUtil.FEATURES)));*/
        }

    }
    //获取奖励积分可互转会员列表
    @RequestMapping(value = "/bop/transferMem/list.json")
    public String bopTransferEach(HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request
                .getAttribute(com.framework.loippi.consts.Constants.CURRENT_USER);
        if(member==null){
            return ApiUtils.error("用户未登录");
        }
        //1.查询当前会员基础信息，判断当前会员是主店还是次店
        RdMmBasicInfo rdMmBasicInfo = rdMmBasicInfoService.findByMCode(member.getMmCode());
        if(rdMmBasicInfo==null||rdMmBasicInfo.getMainFlag()==null){
            return ApiUtils.error("会员基础信息异常");
        }
        ArrayList<RdMmBasicInfo> infos = new ArrayList<>();
        List<RdMmBasicInfo> list=new ArrayList<>();
        //2.筛选除自身以外的其他关联主次店会员信息
        if(rdMmBasicInfo.getMainFlag()==1){
            list=rdMmBasicInfoService.findBranch(rdMmBasicInfo.getMmCode());
        }
        if(rdMmBasicInfo.getMainFlag()==2){
            RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", rdMmBasicInfo.getMmCode());
            RdMmBasicInfo infoMain = rdMmBasicInfoService.findByMCode(rdMmRelation.getSponsorCode());
            infos.add(infoMain);//如果当前登录人为次店，则先将主店添加进集合
            list=rdMmBasicInfoService.findBranch(infoMain.getMmCode());
            Iterator<RdMmBasicInfo> iterator = list.iterator();
            while (iterator.hasNext()){
                RdMmBasicInfo next = iterator.next();
                if(next.getMmCode().equals(rdMmBasicInfo.getMmCode())){
                    iterator.remove();
                }
            }
            /*for (RdMmBasicInfo basicInfo : list) {
                if(basicInfo.getMmCode().equals(rdMmBasicInfo.getMmCode())){
                    list.remove(basicInfo);
                }
            }*/
        }
        infos.addAll(list);
        HashMap<String, RdMmBasicInfo> map = new HashMap<>();
        for (RdMmBasicInfo info : infos) {
            map.put(info.getMmCode(),info);
        }
        //3.搜索关联同商户信息会员信息
        //3.1判断当前登录会员是否存在提交且通过审核的商户信息
        List<CompanyLicense> companyLicenses = companyLicenseService.findList(Paramap.create().put("mCode",rdMmBasicInfo.getMmCode()).put("status",2));
        if(companyLicenses!=null&&companyLicenses.size()>0){
            CompanyLicense companyLicense = companyLicenses.get(0);
            //3.2获取统一社保号，进行匹配
            List<CompanyLicense> licenses = companyLicenseService.findList(Paramap.create().put("creditCode",companyLicense.getCreditCode()).put("status",2));
            if(licenses!=null&&licenses.size()>0){
                for (CompanyLicense licens : licenses) {
                    if(licens.getMCode().equals(companyLicense.getMCode())){
                        continue;
                    }
                    RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode",licens.getMCode());
                    if(rdMmRelation.getMmStatus()!=0){
                        continue;
                    }
                    //判断infos是否存在licens对应的会员，如果不存在，加入infos
                    if (map.get(licens.getMCode())==null){
                        RdMmBasicInfo licenInfo = rdMmBasicInfoService.findByMCode(licens.getMCode());
                        infos.add(licenInfo);
                        map.put(licenInfo.getMmCode(),licenInfo);
                    }
                }
            }
        }
        //4.处理返回信息
        List<RdRanks> ranks = rdRanksService.findAll();
        HashMap<Integer, String> rankMap = new HashMap<>();
        for (RdRanks rank : ranks) {
            rankMap.put(rank.getRankId(),rank.getRankName());
        }
        HashMap<String, RdMmRelation> relationMap= new HashMap<>();
        for (RdMmBasicInfo info : infos) {
            RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode",info.getMmCode());
            relationMap.put(rdMmRelation.getMmCode(),rdMmRelation);

        }
        return ApiUtils.success(BopTransMemResult.build(infos,relationMap,rankMap));
    }

    //进入转给主/分店页面
    @RequestMapping(value = "/bop/transferMem/forward.json")
    public String bopTransferForward(HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request
                .getAttribute(com.framework.loippi.consts.Constants.CURRENT_USER);
        if(member==null){
            return ApiUtils.error("用户未登录");
        }
        RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", member.getMmCode());
        if (rdMmAccountInfo.getBonusStatus() != null && rdMmAccountInfo.getBonusStatus() != 0) {
            return ApiUtils.error("该积分未处于正常状态不能进行转出");
        }
        return ApiUtils.success(rdMmAccountInfo);
    }



    /**
     * 奖励积分转账
     * @param request
     * @param amount 转出积分金额
     * @param acceptCode 接收人会员编号
     * @param pwd 密码
     * @param message 转账留言
     * @return
     */
    @RequestMapping(value = "/bop/transferMem/makeSure.json")
    public String bopTransSure(HttpServletRequest request,String amount,String acceptCode,String pwd,String message) {
        if(StringUtil.isEmpty(amount)){
            return ApiUtils.error("请选择需要转出积分数额");
        }
        if(StringUtil.isEmpty(acceptCode)){
            return ApiUtils.error("请选择需转出会员编号");
        }
        if(StringUtil.isEmpty(pwd)){
            return ApiUtils.error("请输入积分支付密码");
        }
        AuthsLoginResult member = (AuthsLoginResult) request
                .getAttribute(com.framework.loippi.consts.Constants.CURRENT_USER);
        if(member==null){
            return ApiUtils.error("用户未登录");
        }
        RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", member.getMmCode());
        if (rdMmAccountInfo.getBonusStatus() != null && rdMmAccountInfo.getBonusStatus() != 0) {
            return ApiUtils.error("该积分未处于正常状态不能进行转出");
        }
        if (!Digests.validatePassword(pwd,rdMmAccountInfo.getPaymentPwd())) {
            return ApiUtils.error("积分支付密码错误");
        }
        if(rdMmAccountInfo.getMmCode().equals(acceptCode)){
            return ApiUtils.error("奖励积分不可以转给自己");
        }
        RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", acceptCode);
        if(rdMmRelation==null||rdMmRelation.getMmStatus()==null){
            return ApiUtils.error("受赠人会员信息异常");
        }
        if(rdMmRelation.getMmStatus()!=0){
            return ApiUtils.error("受赠人会员处于非正常状态");
        }
        if(!StringUtil.isEmpty(message)&&message.length()>50){
            return ApiUtils.error("交易留言已超过50字限制");
        }
        try {
            BigDecimal total = new BigDecimal(amount);
            if(rdMmAccountInfo.getBonusBlance().compareTo(total)==-1){
                return ApiUtils.error("转出积分大于账户奖励积分余额");
            }
            RdMmAccountInfo acceptAccountInfo = rdMmAccountInfoService.find("mmCode", acceptCode);
            if(acceptAccountInfo==null){
                return ApiUtils.error("接收人积分账户异常");
            }
            HashMap<String,Object> map=rdMmAccountInfoService.bopTransSure(rdMmAccountInfo,acceptAccountInfo,total,pwd,message);
            return ApiUtils.success(map);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiUtils.error("网络异常，请稍后重试");
        }
    }
}

