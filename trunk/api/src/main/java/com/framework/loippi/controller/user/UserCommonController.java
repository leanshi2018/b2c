package com.framework.loippi.controller.user;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.framework.loippi.consts.DocumentConsts;
import com.framework.loippi.controller.BaseController;
import com.framework.loippi.entity.common.ShopApp;
import com.framework.loippi.entity.common.ShopCommonDocument;
import com.framework.loippi.entity.common.ShopCommonFeedback;
import com.framework.loippi.entity.user.RdMmAccountInfo;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.ware.RdWarehouse;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.result.auths.AuthsLoginResult;
import com.framework.loippi.result.common.document.DocumentListResult;
import com.framework.loippi.result.feedback.FeedbackDetailResult;
import com.framework.loippi.result.feedback.FeedbackListResult;
import com.framework.loippi.service.RedisService;
import com.framework.loippi.service.TUserSettingService;
import com.framework.loippi.service.common.ShopAppService;
import com.framework.loippi.service.common.ShopCommonDocumentService;
import com.framework.loippi.service.common.ShopCommonFeedbackService;
import com.framework.loippi.service.user.RdBonusPaymentService;
import com.framework.loippi.service.user.RdMmAccountInfoService;
import com.framework.loippi.service.user.RdMmAccountLogService;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.service.user.RdMmEditService;
import com.framework.loippi.service.user.RdMmLogOutNumService;
import com.framework.loippi.service.user.RdMmRelationService;
import com.framework.loippi.service.user.RdMmStatusDetailService;
import com.framework.loippi.service.user.RdReceivableMasterService;
import com.framework.loippi.service.user.RdReceiveableDetailService;
import com.framework.loippi.service.ware.RdWarehouseService;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.ApiUtils;
import com.framework.loippi.utils.Constants;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.SmsUtil;
import com.framework.loippi.utils.Xerror;

/**
 * 个人中心公共操作
 * Created by longbh on 2018/10/23.
 */
@Controller
@ResponseBody
@RequestMapping("/api/user")
public class UserCommonController extends BaseController {

    @Resource
    private ShopCommonFeedbackService shopCommonFeedbackService;
    @Resource
    private TUserSettingService tUserSettingService;
    @Resource
    private ShopCommonDocumentService shopCommonDocumentService;
    @Resource
    private RedisService redisService;
    @Resource
    private ShopAppService shopAppService;
    @Resource
    private RdMmBasicInfoService rdMmBasicInfoService;
    @Resource
    private RdMmRelationService rdMmRelationService;
    @Resource
    private RdMmEditService rdMmEditService;
    @Resource
    private RdMmLogOutNumService rdMmLogOutNumService;
    @Resource
    private RdMmAccountLogService rdMmAccountLogService;
    @Resource
    private RdReceiveableDetailService rdReceiveableDetailService;
    @Resource
    private RdReceivableMasterService rdReceivableMasterService;
    @Resource
    private RdMmStatusDetailService rdMmStatusDetailService;
    @Resource
    private RdBonusPaymentService rdBonusPaymentService;
    @Resource
    private RdMmAccountInfoService rdMmAccountInfoService;
    @Resource
    private RdWarehouseService rdWarehouseService;

    /**
     * 文章列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/ourIndex", method = RequestMethod.POST)
    public String ourIndex(HttpServletRequest request) {
        List<ShopCommonDocument> list = shopCommonDocumentService.findDocumentListByDocType(DocumentConsts.DOCUMENT_TYPE_OUR);
        StringBuffer url = new StringBuffer();
        url.append(wapServer).append("/wap/user/articleDetail.html?type=app&id=");
        return ApiUtils.success(DocumentListResult.build(list, url.toString()));
    }

    /**
     * 固定文章
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/fixedArticles", method = RequestMethod.POST)
    public String protocol(HttpServletRequest request, String docType) {
        if (StringUtils.isBlank(docType)) {
            return ApiUtils.error(Xerror.PARAM_INVALID, "参数无效");
        }
        List<ShopCommonDocument> list = shopCommonDocumentService.findDocumentListByDocType(docType);
        String content = "";
        StringBuffer url = new StringBuffer();
        String title = "";
        if (list.size() != 0) {
            ShopCommonDocument shopCommonDocument = list.get(0);
            if (StringUtils.isNotBlank(shopCommonDocument.getDocContent())) {
                content = shopCommonDocument.getDocContent();
            }
            if (StringUtils.isNotBlank(shopCommonDocument.getDocTitle())) {
                title = shopCommonDocument.getDocTitle();
            }
            url.append(wapServer).append("/wap/document/" + shopCommonDocument.getDocType() + ".html?type=app");
        }
        return ApiUtils.success(Paramap.create().put("content", content).put("title", title).put("url", url));
    }

    /**
     * 帮助文章列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/helpIndex", method = RequestMethod.POST)
    public String helpIndex(HttpServletRequest request) {
        Map<String, Object> qyMap = new HashMap<>();
        qyMap.put("docType", DocumentConsts.DOCUMENT_TYPE_HELP);
        List<ShopCommonDocument> list = shopCommonDocumentService.findList(qyMap);
        StringBuffer url = new StringBuffer();
        url.append(wapServer).append("/wap/document/detail/");
        return ApiUtils.success(DocumentListResult.build(list, url.toString()));
    }

    /**
     * 意见反馈
     */
    @RequestMapping(value = "/feedback", method = RequestMethod.POST)
    public String feedback(HttpServletRequest request, String content, String title) {
        if (StringUtils.isBlank(content) || StringUtils.isBlank(title)) {
            return ApiUtils.error(Xerror.PARAM_INVALID, "参数无效");
        }
        if(content.length()>200){
            return ApiUtils.error(Xerror.PARAM_INVALID, "意见反馈内容超出长度");
        }
        if(title.length()>100){
            return ApiUtils.error(Xerror.PARAM_INVALID, "意见反馈标题超出长度");
        }
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        Long userId = Long.parseLong(member.getMmCode());
        ShopCommonFeedback shopFeedback = new ShopCommonFeedback();
        shopFeedback.setContent(content);
        shopFeedback.setTitle(title);
        shopFeedback.setId(twiterIdService.getTwiterId());
        shopFeedback.setUid(userId);
        shopFeedback.setCreateTime(new Date());
        shopCommonFeedbackService.save(shopFeedback);
        return ApiUtils.success();
    }

    /**
     * 意见反馈列表
     */
    @RequestMapping(value = "/feedbackList", method = RequestMethod.POST)
    public String feedbackList(HttpServletRequest request, Pageable pageable) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        pageable.setParameter(Paramap.create().put("uid", Long.parseLong(member.getMmCode())));
        pageable.setOrderProperty("create_time");
        pageable.setOrderDirection(Order.Direction.DESC);
        return ApiUtils.success(FeedbackListResult.buildList(shopCommonFeedbackService.findByPage(pageable)));
    }

    /**
     * 意见反馈详情
     */
    @RequestMapping(value = "/feedbackDetail", method = RequestMethod.POST)
    public String feedbackDetail(HttpServletRequest request, Long id) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if (id == null) {
            return ApiUtils.error(Xerror.PARAM_INVALID, "参数无效");
        }
        ShopCommonFeedback shopFeedback = shopCommonFeedbackService.find(id);
        if (shopFeedback == null) {
            return ApiUtils.error("反馈不存在");
        }
        return ApiUtils.success(FeedbackDetailResult.buildList(shopFeedback));
    }

    /**
     * 获取平台联系电话
     */
    @RequestMapping(value = "/getPlatformTel", method = RequestMethod.POST)
    public String getPlatformTel() {
        String tel = (String) tUserSettingService.read("platform_tel");
        return ApiUtils.success(Paramap.create().put("tel", tel));
    }

    /**
     * 邀请规则
     *
     * @return
     */
    @RequestMapping(value = "/shareRule", method = RequestMethod.POST)
    public String shareRule(HttpServletRequest request) {
        AuthsLoginResult userSession = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        //下载页链接
        List<ShopApp> shopApps = shopAppService.findAll();
        Map<String, Object> downMap = Paramap.create().put("userCode", Long.parseLong(userSession.getMmCode()));
        for (ShopApp shopApp : shopApps) {
            if (shopApp.getDevice() == 0) {
                downMap.put("android", shopApp.getUrl());
            } else {
                downMap.put("ios", shopApp.getUrl());
            }
        }
        return ApiUtils.success(downMap);
    }

    /**
     * 注销登录
     */
    @RequestMapping("/logout.json")
    public String logout(Long id, HttpServletRequest request) {
        AuthsLoginResult userSession = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        redisService.delete(userSession.getSessionid());
        return ApiUtils.success();
    }

    /**
     * 注销用户(改手机号和昵称和状态)
     */
    @RequestMapping("/logoutMember.json")
    public String logoutMember(String code,String mobile,HttpServletRequest request) {
        if (mobile==null|| "".equals(mobile)){
            return ApiUtils.error("请输入手机号");
        }

        if (code==null||"".equals(code)){
            return ApiUtils.error("请输入短信验证码");
        }

        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        RdMmBasicInfo rdMmBasicInfo = rdMmBasicInfoService.find("mmCode", member.getMmCode());
        //RdMmRelation mmRelation = rdMmRelationService.find("mmCode", member.getMmCode());
        if(rdMmBasicInfo==null||rdMmBasicInfo.getMainFlag()==null){
            return ApiUtils.error("会员信息异常");
        }
        List<RdMmBasicInfo> mmBasicInfos=new ArrayList<>();
        if(rdMmBasicInfo.getMainFlag()==1){
            mmBasicInfos = rdMmBasicInfoService.findList("mobile", rdMmBasicInfo.getMobile());

            //停用自提店
            RdWarehouse rdWarehouse = rdWarehouseService.findByMmCode(rdMmBasicInfo.getMmCode());
            if (rdWarehouse!=null){
                rdWarehouse.setWareStatus(1);
                rdWarehouseService.update(rdWarehouse);
            }

        }
        if (!mobile.equals(rdMmBasicInfo.getMobile())){
            return ApiUtils.error("输入的手机号与该账号绑定的手机号不一致");
        }

        if (!SmsUtil.validMsg(rdMmBasicInfo.getMobile(), code, redisService)) {
            return ApiUtils.error("验证码不正确或已过期");
        }
        rdMmRelationService.cancellation(rdMmBasicInfo);
        if(rdMmBasicInfo.getMainFlag()==2){
            redisService.delete(member.getSessionid());
        }
        if(rdMmBasicInfo.getMainFlag()==1){
            for (RdMmBasicInfo mmBasicInfo : mmBasicInfos) {
                String sessionId = redisService.get("user_name" + mmBasicInfo.getMmCode());
                redisService.delete(sessionId);
            }
        }
        /*if(rdMmBasicInfo.getMainFlag()==1){//主店注销
            List<RdMmBasicInfo> infos = rdMmBasicInfoService.findList("mobile", rdMmBasicInfo.getMobile());
            if(infos!=null&&infos.size()>0){
                for (RdMmBasicInfo info : infos) {
                    if(info.getMainFlag()!=null&&info.getMainFlag()==1){
                        //修改会员状态为注销
                        mmRelation.setMmStatus(2);
                        rdMmRelationService.update(mmRelation);

                        //修改下线推荐人
                        rdMmRelationService.updateRelaSponsorBySponsorCode(rdMmBasicInfo.getMmCode(),mmRelation.getSponsorCode(),mmRelation.getSponsorName());

                        //修改该会员所有待审核的修改信息未驳回
                        rdMmEditService.updateByStatusAndMCode(member.getMmCode());


                        RdMmLogOutNum rdMmLogOutNum = rdMmLogOutNumService.find(1);
                        Integer logoutNum = rdMmLogOutNum.getLogoutNum()+1;
                        rdMmLogOutNum.setLogoutNum(logoutNum);
                        rdMmLogOutNumService.update(rdMmLogOutNum);
                        //手机号码 = 19900000001开始累加
                        StringBuilder phone = new StringBuilder(); //构建空的可变字符串
                        phone.append("199");

                        for (int i=0;i<8-(logoutNum+"").length();i++){
                            phone.append("0");
                        }
                        phone.append(logoutNum+"");

                        //昵称= 已注销1+手机号码开始累加
                        String nickName = "已注销"+logoutNum+"-"+phone;

                        //添加修改信息
                        RdMmEdit rdMmEdit = new RdMmEdit();
                        rdMmEdit.setMmCode(rdMmBasicInfo.getMmCode());
                        rdMmEdit.setMmNickNameBefore(rdMmBasicInfo.getMmNickName());
                        rdMmEdit.setMmNickNameAfter(nickName);
                        rdMmEdit.setMobileBefore(rdMmBasicInfo.getMobile());
                        rdMmEdit.setMobileAfter(phone.toString());
                        rdMmEdit.setUpdateBy("用户");
                        rdMmEdit.setUpdateMemo("用户注销");
                        rdMmEdit.setUpdateType(0);
                        rdMmEdit.setUpdateTime(new Date());
                        rdMmEdit.setReviewMemo("用户注销");
                        rdMmEdit.setReviewStatus(3);
                        rdMmEditService.save(rdMmEdit);

                        //修改基础表
                        rdMmBasicInfo.setMmNickName(nickName);
                        rdMmBasicInfo.setMobile(phone.toString());
                        rdMmBasicInfoService.update(rdMmBasicInfo);

                        //修改欠款明细 rd_receiveable_detail
                        rdReceiveableDetailService.updateNickNameByMCode(nickName,rdMmBasicInfo.getMmCode());

                        //修改会员账户交易日志表
                        rdMmAccountLogService.updateNickNameByMCode(nickName,rdMmBasicInfo.getMmCode());

                        //修改会员欠款主表 rd_receivable_master
                        rdReceivableMasterService.updateNickNameByMCode(nickName,rdMmBasicInfo.getMmCode());

                        //修改会员状态变更明细 rd_mm_status_detail
                        rdMmStatusDetailService.updateNickNameByMCode(nickName,rdMmBasicInfo.getMmCode());

                        //添加会员状态变更明细 rd_mm_status_detail
                        RdMmStatusDetail rdMmStatusDetail = new RdMmStatusDetail();
                        rdMmStatusDetail.setMmCode(rdMmBasicInfo.getMmCode());
                        rdMmStatusDetail.setMmNickName(nickName);
                        rdMmStatusDetail.setStatusType("MM");
                        rdMmStatusDetail.setStatusBefore(mmRelation.getMmStatus());
                        rdMmStatusDetail.setStatusAfter(2);
                        rdMmStatusDetail.setUpdateBy("用户");
                        rdMmStatusDetail.setUpdateTime(new Date());
                        rdMmStatusDetail.setUpdateDesc("用户主动注销");
                        rdMmStatusDetailService.save(rdMmStatusDetail);

                        //修改奖金发放表 rd_bonus_payment
                        rdBonusPaymentService.updateNickNameByMCode(nickName,rdMmBasicInfo.getMmCode());

                        //删除redis记录
                        redisService.delete(member.getSessionid());
                    }else if(info.getMainFlag()!=null&&info.getMainFlag()==2){
                        RdMmRelation mmRelation1 = rdMmRelationService.find("mmCode",info.getMmCode());
                        //修改会员状态为注销
                        mmRelation1.setMmStatus(2);
                        rdMmRelationService.update(mmRelation1);
                        RdMmRelation mmRelation2 = rdMmRelationService.find("mmCode",mmRelation1.getSponsorCode());
                        //修改下线推荐人
                        rdMmRelationService.updateRelaSponsorBySponsorCode(info.getMmCode(),mmRelation2.getSponsorCode(),mmRelation2.getSponsorName());

                        //修改该会员所有待审核的修改信息未驳回
                        rdMmEditService.updateByStatusAndMCode(info.getMmCode());


                        RdMmLogOutNum rdMmLogOutNum = rdMmLogOutNumService.find(1);
                        Integer logoutNum = rdMmLogOutNum.getLogoutNum()+1;
                        rdMmLogOutNum.setLogoutNum(logoutNum);
                        rdMmLogOutNumService.update(rdMmLogOutNum);
                        //手机号码 = 19900000001开始累加
                        StringBuilder phone = new StringBuilder(); //构建空的可变字符串
                        phone.append("199");

                        for (int i=0;i<8-(logoutNum+"").length();i++){
                            phone.append("0");
                        }
                        phone.append(logoutNum+"");

                        //昵称= 已注销1+手机号码开始累加
                        String nickName = "已注销"+logoutNum+"-"+phone;

                        //添加修改信息
                        RdMmEdit rdMmEdit = new RdMmEdit();
                        rdMmEdit.setMmCode(info.getMmCode());
                        rdMmEdit.setMmNickNameBefore(info.getMmNickName());
                        rdMmEdit.setMmNickNameAfter(nickName);
                        rdMmEdit.setMobileBefore(info.getMobile());
                        rdMmEdit.setMobileAfter(phone.toString());
                        rdMmEdit.setUpdateBy("用户");
                        rdMmEdit.setUpdateMemo("用户注销");
                        rdMmEdit.setUpdateType(0);
                        rdMmEdit.setUpdateTime(new Date());
                        rdMmEdit.setReviewMemo("用户注销");
                        rdMmEdit.setReviewStatus(3);
                        rdMmEditService.save(rdMmEdit);

                        //修改基础表
                        info.setMmNickName(nickName);
                        info.setMobile(phone.toString());
                        rdMmBasicInfoService.update(info);

                        //修改欠款明细 rd_receiveable_detail
                        rdReceiveableDetailService.updateNickNameByMCode(nickName,info.getMmCode());

                        //修改会员账户交易日志表
                        rdMmAccountLogService.updateNickNameByMCode(nickName,info.getMmCode());

                        //修改会员欠款主表 rd_receivable_master
                        rdReceivableMasterService.updateNickNameByMCode(nickName,info.getMmCode());

                        //修改会员状态变更明细 rd_mm_status_detail
                        rdMmStatusDetailService.updateNickNameByMCode(nickName,info.getMmCode());

                        //添加会员状态变更明细 rd_mm_status_detail
                        RdMmStatusDetail rdMmStatusDetail = new RdMmStatusDetail();
                        rdMmStatusDetail.setMmCode(info.getMmCode());
                        rdMmStatusDetail.setMmNickName(nickName);
                        rdMmStatusDetail.setStatusType("MM");
                        rdMmStatusDetail.setStatusBefore(mmRelation1.getMmStatus());
                        rdMmStatusDetail.setStatusAfter(2);
                        rdMmStatusDetail.setUpdateBy("用户");
                        rdMmStatusDetail.setUpdateTime(new Date());
                        rdMmStatusDetail.setUpdateDesc("用户主动注销");
                        rdMmStatusDetailService.save(rdMmStatusDetail);

                        //修改奖金发放表 rd_bonus_payment
                        rdBonusPaymentService.updateNickNameByMCode(nickName,info.getMmCode());
                        String sessionId = redisService.get("user_name" + info.getMmCode());
                        if(sessionId!=null){
                            //删除redis记录
                            redisService.delete(member.getSessionid());
                        }
                    }
                }
            }
        }
        if(rdMmBasicInfo.getMainFlag()==2){//次店注销
            //修改会员状态为注销
            mmRelation.setMmStatus(2);
            rdMmRelationService.update(mmRelation);

            //修改下线推荐人
            rdMmRelationService.updateRelaSponsorBySponsorCode(rdMmBasicInfo.getMmCode(),mmRelation.getSponsorCode(),mmRelation.getSponsorName());

            //修改该会员所有待审核的修改信息未驳回
            rdMmEditService.updateByStatusAndMCode(member.getMmCode());


            RdMmLogOutNum rdMmLogOutNum = rdMmLogOutNumService.find(1);
            Integer logoutNum = rdMmLogOutNum.getLogoutNum()+1;
            rdMmLogOutNum.setLogoutNum(logoutNum);
            rdMmLogOutNumService.update(rdMmLogOutNum);
            //手机号码 = 19900000001开始累加
            StringBuilder phone = new StringBuilder(); //构建空的可变字符串
            phone.append("199");

            for (int i=0;i<8-(logoutNum+"").length();i++){
                phone.append("0");
            }
            phone.append(logoutNum+"");

            //昵称= 已注销1+手机号码开始累加
            String nickName = "已注销"+logoutNum+"-"+phone;

            //添加修改信息
            RdMmEdit rdMmEdit = new RdMmEdit();
            rdMmEdit.setMmCode(rdMmBasicInfo.getMmCode());
            rdMmEdit.setMmNickNameBefore(rdMmBasicInfo.getMmNickName());
            rdMmEdit.setMmNickNameAfter(nickName);
            rdMmEdit.setMobileBefore(rdMmBasicInfo.getMobile());
            rdMmEdit.setMobileAfter(phone.toString());
            rdMmEdit.setUpdateBy("用户");
            rdMmEdit.setUpdateMemo("用户注销");
            rdMmEdit.setUpdateType(0);
            rdMmEdit.setUpdateTime(new Date());
            rdMmEdit.setReviewMemo("用户注销");
            rdMmEdit.setReviewStatus(3);
            rdMmEditService.save(rdMmEdit);

            //修改基础表
            rdMmBasicInfo.setMmNickName(nickName);
            rdMmBasicInfo.setMobile(phone.toString());
            rdMmBasicInfoService.update(rdMmBasicInfo);

            //修改欠款明细 rd_receiveable_detail
            rdReceiveableDetailService.updateNickNameByMCode(nickName,rdMmBasicInfo.getMmCode());

            //修改会员账户交易日志表
            rdMmAccountLogService.updateNickNameByMCode(nickName,rdMmBasicInfo.getMmCode());

            //修改会员欠款主表 rd_receivable_master
            rdReceivableMasterService.updateNickNameByMCode(nickName,rdMmBasicInfo.getMmCode());

            //修改会员状态变更明细 rd_mm_status_detail
            rdMmStatusDetailService.updateNickNameByMCode(nickName,rdMmBasicInfo.getMmCode());

            //添加会员状态变更明细 rd_mm_status_detail
            RdMmStatusDetail rdMmStatusDetail = new RdMmStatusDetail();
            rdMmStatusDetail.setMmCode(rdMmBasicInfo.getMmCode());
            rdMmStatusDetail.setMmNickName(nickName);
            rdMmStatusDetail.setStatusType("MM");
            rdMmStatusDetail.setStatusBefore(mmRelation.getMmStatus());
            rdMmStatusDetail.setStatusAfter(2);
            rdMmStatusDetail.setUpdateBy("用户");
            rdMmStatusDetail.setUpdateTime(new Date());
            rdMmStatusDetail.setUpdateDesc("用户主动注销");
            rdMmStatusDetailService.save(rdMmStatusDetail);

            //修改奖金发放表 rd_bonus_payment
            rdBonusPaymentService.updateNickNameByMCode(nickName,rdMmBasicInfo.getMmCode());

            //删除redis记录
            redisService.delete(member.getSessionid());
        }*/
        return ApiUtils.success();
    }

    /**
     * 注销用户(改手机号和昵称和状态)
     */
    @RequestMapping("/logoutMemberNew.json")
    public String logoutMemberNew(String code,String mobile,HttpServletRequest request) {
        if (mobile==null|| "".equals(mobile)){
            return ApiUtils.error("请输入手机号");
        }

        if (code==null||"".equals(code)){
            return ApiUtils.error("请输入短信验证码");
        }

        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        RdMmBasicInfo rdMmBasicInfo = rdMmBasicInfoService.find("mmCode", member.getMmCode());
        //RdMmRelation mmRelation = rdMmRelationService.find("mmCode", member.getMmCode());
        if(rdMmBasicInfo==null||rdMmBasicInfo.getMainFlag()==null){
            return ApiUtils.error("会员信息异常");
        }
        List<RdMmBasicInfo> mmBasicInfos=new ArrayList<>();
        if(rdMmBasicInfo.getMainFlag()==1){
            mmBasicInfos = rdMmBasicInfoService.findList("mobile", rdMmBasicInfo.getMobile());
        }
        if (!mobile.equals(rdMmBasicInfo.getMobile())){
            return ApiUtils.error("输入的手机号与该账号绑定的手机号不一致");
        }

        if (!SmsUtil.validMsg(rdMmBasicInfo.getMobile(), code, redisService)) {
            return ApiUtils.error("验证码不正确或已过期");
        }
        rdMmRelationService.cancellation(rdMmBasicInfo);
        if(rdMmBasicInfo.getMainFlag()==2){
            redisService.delete(member.getSessionid());
        }
        if(rdMmBasicInfo.getMainFlag()==1){
            for (RdMmBasicInfo mmBasicInfo : mmBasicInfos) {
                String sessionId = redisService.get("user_name" + mmBasicInfo.getMmCode());
                redisService.delete(sessionId);
            }
        }
        return ApiUtils.success();
    }

    /**
     * 设置积分账户积分预留线
     */
    @RequestMapping("/update/withdrawalLine.json")
    public String logoutMember(HttpServletRequest request, String withdrawalLine) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if(member==null){
            return ApiUtils.error("当前用户尚未登录");
        }
        if(withdrawalLine==null){
            return ApiUtils.error("请传入积分预留金额");
        }
        RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", member.getMmCode());
        if(rdMmAccountInfo==null){
            return ApiUtils.error("会员积分账户异常");
        }
        rdMmAccountInfo.setWithdrawalLine(new BigDecimal(withdrawalLine));
        Long result = rdMmAccountInfoService.update(rdMmAccountInfo);
        if(result>0){
            return ApiUtils.success();
        }else {
            return ApiUtils.error("网络异常，请稍后重试");
        }
    }
}
