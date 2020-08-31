package com.framework.loippi.controller.companyWithdrawal;

import com.framework.loippi.consts.CompanyWithdrawalConstant;
import com.framework.loippi.consts.Constants;
import com.framework.loippi.controller.BaseController;
import com.framework.loippi.entity.companyInfo.CompanyLicense;
import com.framework.loippi.entity.integration.RdMmIntegralRule;
import com.framework.loippi.entity.user.RdMmAccountInfo;
import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.param.companyWithdrawal.CompanyWithdrawalParm;
import com.framework.loippi.result.auths.AuthsLoginResult;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.companyInfo.CompanyLicenseService;
import com.framework.loippi.service.integration.RdMmIntegralRuleService;
import com.framework.loippi.service.user.RdMmAccountInfoService;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.service.user.RdMmRelationService;
import com.framework.loippi.utils.*;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author zc
 * @date 2020-08-28
 * 商户提现控制类
 */
@Controller
@ResponseBody
@RequestMapping("/api/companyWithdrawal")
public class CompanyWithdrawalController extends BaseController {
    @Resource
    private RdMmBasicInfoService rdMmBasicInfoService;
    @Resource
    private CompanyLicenseService companyLicenseService;
    @Resource
    private TwiterIdService twiterIdService;
    @Resource
    private RdMmRelationService rdMmRelationService;
    @Resource
    private RdMmAccountInfoService rdMmAccountInfoService;
    @Resource
    private RdMmIntegralRuleService rdMmIntegralRuleService;

    /**
     * 判断当前登录用户商户提现认证状态 返回Integer数值 1：待审核 2：已认证 -1：未认证
     * @param request
     * @return
     */
    @RequestMapping(value = "/approveFlag", method = RequestMethod.GET)
    public String approveFlag(HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if(member==null){
            return ApiUtils.error("用户尚未登录");
        }
        String mmCode = member.getMmCode();
        Integer flag=-1;
        Long count = companyLicenseService.count(Paramap.create().put("mCode",mmCode).put("status",2));//已认证
        if(count!=null&&count>0){
            flag=2;
        }else {
            Long count1 = companyLicenseService.count(Paramap.create().put("mCode",mmCode).put("status",1));//待审核
            if(count1!=null&&count1>0){
                flag=1;
            }
        }
        return ApiUtils.success(flag);
    }

    /**
     * 提交申请商户提现需要的资料信息
     * @param param
     * @param vResult
     * @param request
     * @return
     */
    @RequestMapping(value = "/applyFor", method = RequestMethod.POST)
    public String applyFor(@Valid CompanyWithdrawalParm param, BindingResult vResult, HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(com.framework.loippi.consts.Constants.CURRENT_USER);
        if(member==null){
            return ApiUtils.error("用户尚未登录");
        }
        if (vResult.hasErrors()) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        Long count = companyLicenseService.count(Paramap.create().put("mCode", member.getMmCode()).put("noStatus", -1));
        if (count!=null&&count>0){
            return ApiUtils.error("当前用户已认证商户提现或已提交商户认证信息，等待审核");
        }
        CompanyLicense license = new CompanyLicense();
        license.setId(twiterIdService.getTwiterId());
        license.setMCode(member.getMmCode());
        license.setCompanyName(param.getCompanyName());
        license.setCreditCode(param.getCreditCode());
        license.setCompanyAdd(param.getCompanyAdd());
        license.setCompanyPhone(param.getCompanyPhone());
        license.setBankName(param.getBankName());
        license.setBankId(param.getBankId());
        license.setBusinessLicense(param.getBusinessLicense());
        license.setPermissionLicense(param.getPermissionLicense());
        license.setStatus(1);
        license.setCreateTime(new Date());
        Long flag = companyLicenseService.save(license);
        if(flag==0){
            return ApiUtils.error("提交失败");
        }
        return ApiUtils.success("提交成功，请联系客服进行审核");
    }

    /**
     * 前往商户提现，携带奖励积分金额以及商户提现税率
     * @param request
     * @return
     */
    @RequestMapping(value = "/depositForward", method = RequestMethod.POST)
    public String depositForward(HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(com.framework.loippi.consts.Constants.CURRENT_USER);
        if(member==null){
            return ApiUtils.error("用户尚未登录");
        }
        RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode",member.getMmCode());
        if(rdMmRelation==null||rdMmRelation.getMmStatus()==null){
            return ApiUtils.error("会员信息异常");
        }
        if(rdMmRelation.getMmStatus()!=0){
            return ApiUtils.error("会员冻结或注销");
        }
        RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", member.getMmCode());
        if(rdMmAccountInfo==null||rdMmAccountInfo.getBonusStatus()==null){
            return ApiUtils.error("会员积分信息异常");
        }
        if(rdMmAccountInfo.getBonusStatus()!=0){
            return ApiUtils.error("奖励积分冻结或未激活");
        }
        List<RdMmIntegralRule> list = rdMmIntegralRuleService.findAll();
        if (list!=null&&list.size()>0){
            RdMmIntegralRule rdMmIntegralRule = list.get(0);
            Optional<RdMmIntegralRule> optional = Optional.ofNullable(rdMmIntegralRule);
            Paramap result = Paramap.create().put("bonusBlance", rdMmAccountInfo.getBonusBlance()).put("rate", optional.map(RdMmIntegralRule::getBonusPointWd).orElse(0))
                    .put("lowAcc",optional.map(RdMmIntegralRule::getBonusPointWdLimit).orElse(0));
            return ApiUtils.success(result);
        }else {
            Paramap result = Paramap.create().put("bonusBlance", rdMmAccountInfo.getBonusBlance()).put("rate", CompanyWithdrawalConstant.COMPANY_WITHDRAWAL_RATE)
                    .put("lowAcc",CompanyWithdrawalConstant.LOW_ACC);
            return ApiUtils.success(result);
        }
    }

    /**
     * 提交商户提现申请
     * @param request
     * @param amount 提现积分金额
     * @param image 电子发票图片
     * @param pwd 支付密码
     * @return
     */
    @RequestMapping(value = "/depositApply", method = RequestMethod.POST)
    public String depositApply(HttpServletRequest request, BigDecimal amount,String image,String pwd) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(com.framework.loippi.consts.Constants.CURRENT_USER);
        if(member==null){
            return ApiUtils.error("用户尚未登录");
        }
        if(amount==null||amount.compareTo(new BigDecimal("5000.00"))==-1){
            return ApiUtils.error("提现金额小于5000");
        }
        if(StringUtil.isEmpty(image)){
            return ApiUtils.error("电子发票不可为空");
        }
        if(StringUtil.isEmpty(pwd)){
            return ApiUtils.error("请输入支付密码");
        }
        RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", member.getMmCode());
        if(rdMmAccountInfo==null||rdMmAccountInfo.getBonusStatus()==null){
            return ApiUtils.error("会员积分信息异常");
        }
        if(rdMmAccountInfo.getBonusStatus()!=0){
            return ApiUtils.error("奖励积分冻结或未激活");
        }
        if (!Digests.validatePassword(pwd, rdMmAccountInfo.getPaymentPwd())) {
            return ApiUtils.error("支付密码错误");
        }
        if(amount.compareTo(rdMmAccountInfo.getBonusBlance())==1){
            return ApiUtils.error("提现金额大于奖励积分余额");
        }
        //处理积分 生成日志
        Map<String,Object> map=rdMmAccountInfoService.companyDeposit(rdMmAccountInfo,amount,CompanyWithdrawalConstant.COMPANY_WITHDRAWAL_RATE,image);
        return ApiUtils.success(map);
    }
}
