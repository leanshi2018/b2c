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

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.framework.loippi.consts.IntegrationNameConsts;
import com.framework.loippi.controller.BaseController;
import com.framework.loippi.dao.user.RdSysPeriodDao;
import com.framework.loippi.entity.ShopCommonMessage;
import com.framework.loippi.entity.ShopMemberMessage;
import com.framework.loippi.entity.integration.RdMmIntegralRule;
import com.framework.loippi.entity.user.MemberQualification;
import com.framework.loippi.entity.user.RdMmAccountInfo;
import com.framework.loippi.entity.user.RdMmAccountLog;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.entity.user.RdRanks;
import com.framework.loippi.entity.user.RdSysPeriod;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.result.auths.AuthsLoginResult;
import com.framework.loippi.result.user.IntegrationBuildResult;
import com.framework.loippi.result.user.IntegrationDetailResult;
import com.framework.loippi.result.user.IntegrationListResult;
import com.framework.loippi.result.user.IntegrationMemberListResult;
import com.framework.loippi.result.user.UserIntegrationListResult;
import com.framework.loippi.service.integration.RdMmIntegralRuleService;
import com.framework.loippi.service.user.MemberQualificationService;
import com.framework.loippi.service.user.RdMmAccountInfoService;
import com.framework.loippi.service.user.RdMmAccountLogService;
import com.framework.loippi.service.user.RdMmBankService;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.service.user.RdMmRelationService;
import com.framework.loippi.service.user.RdNewVipDetailService;
import com.framework.loippi.service.user.RdRanksService;
import com.framework.loippi.service.user.RetailProfitService;
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
                .bonusSPNew(shopMember, rdMmAccountInfo, integration, walletBlance);
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
        commonMessage1.setContent("您已成功将"+integration+"点奖励积分转换为"+walletBlance+"点购物积分，祝您购物愉快");
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
        rdMmAccountInfo.setBonusBlance(rdMmAccountInfo.getBonusBlance().subtract(BigDecimal.valueOf(integration)));
        rdMmAccountInfo.setWalletBlance(rdMmAccountInfo.getWalletBlance().add(walletBlance));
        Integer transNumber = rdMmAccountInfoService
                .saveAccountInfoNew(rdMmAccountInfo,integration,IntegrationNameConsts.BOP, rdMmAccountLogList, null,shopCommonMessages,shopMemberMessages);
        return ApiUtils.success(Paramap.create().put("transNumber", transNumber).put("transferOutPoints", integration)
                .put("bopIntegration", rdMmAccountInfo.getBonusBlance().setScale(2,BigDecimal.ROUND_HALF_UP)).put("transferInPoints", walletBlance.setScale(2,BigDecimal.ROUND_HALF_UP))
                .put("shpIntegration", rdMmAccountInfo.getWalletBlance().setScale(2,BigDecimal.ROUND_HALF_UP)));
    }

    //int bankCardId,
    //奖励积分提现确认
    @RequestMapping(value = "/bop/cashWithdrawal/finish.json")
    public String bopCashWithdrawal(HttpServletRequest request,  Double integration, String paypassword) {
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

        if (rdMmAccountInfo.getBonusBlance().compareTo(new BigDecimal(integration))==-1) {
            return ApiUtils.error("积分不足，不可提现");
        }

        List<RdMmAccountLog> list = rdMmAccountLogService.findList(Paramap.create().put("mmCode",member.getMmCode()).put("transTypeCode","WD").put("status",2));
        if(list!=null&&list.size()>0){
            return ApiUtils.error("您已有一笔提现申请待审核，如需要，取消后重新提交");
        }
        //银行卡信息
        /*RdMmBank rdMmBank = rdMmBankService.find(Long.parseLong(bankCardId+""));
        if (rdMmBank == null) {
            return ApiUtils.error("不存在该银行卡");
        }*/
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
        //RdMmAccountLog rdMmAccountLog = IntegrationBuildResult.bonusWD(shopMember, rdMmAccountInfo, integration, bonusPointWd, bankCardId);
        RdMmAccountLog rdMmAccountLog = IntegrationBuildResult.bonusWD(shopMember, rdMmAccountInfo, integration, bonusPointWd);
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
                .put("transferOutMoney", integration)
                .put("bopIntegration", rdMmAccountInfo.getBonusBlance()));
        /*return ApiUtils.error("该功能在升级，请耐心等待！");*/
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
        List<RdRanks> shopMemberGradeList = rdRanksService.findAll();
        List<IntegrationMemberListResult> integrationMemberListResultList = IntegrationMemberListResult
            .build2(rdMmBasicInfoList, shopMemberGradeList, rdMmAccountLogList, rdMmRelationList);
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
        List<IntegrationMemberListResult> integrationMemberListResultList = IntegrationMemberListResult
                .build(rdMmBasicInfoList, rdMmRelationList, shopMemberGradeList);
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
        List<MemberQualification> list = memberQualificationService.findList(Paramap.create().put("sponsorCode",member.getMmCode()).put("periodCode",periodCode));
        List<MemberQualification> qualificationList = memberQualificationService.findList(Paramap.create().put("mCode",member.getMmCode()).put("periodCode",periodCode));
        if(qualificationList.size()>0){
            MemberQualification qualification = qualificationList.get(0);

            Integer rank1Number = Optional.ofNullable(qualification.getDdRank1Number()).orElse(0);//累计直邀VIP人数
            Integer rank2Number = Optional.ofNullable(qualification.getDdRank2Number()).orElse(0);//累计直邀代理人数
            Integer ddAcNumber = Optional.ofNullable(qualification.getDdAcNumber()).orElse(0);//复消人数

            paramap.put("rank1Number",rank1Number);//直邀
            paramap.put("rank2Number",rank2Number);//代理
            paramap.put("ddAcNumber",ddAcNumber);//复消
        }else {
            paramap.put("rank1Number",0);//直邀
            paramap.put("rank2Number",0);//代理
            paramap.put("ddAcNumber",0);//复消
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
        List<IntegrationMemberListResult> integrationMemberListResultList = IntegrationMemberListResult
                .build5(rdMmBasicInfoList, rdMmRelationList, shopMemberGradeList,sorting,hashMap,list);
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

        List<IntegrationMemberListResult> integrationMemberListResult = IntegrationMemberListResult
            .build(rdMmBasicInfoList, rdMmRelationList, shopMemberGradeList);

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

