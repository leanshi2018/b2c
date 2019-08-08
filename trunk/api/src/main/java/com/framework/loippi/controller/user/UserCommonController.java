package com.framework.loippi.controller.user;

import com.framework.loippi.consts.DocumentConsts;
import com.framework.loippi.controller.BaseController;
import com.framework.loippi.entity.common.ShopApp;
import com.framework.loippi.entity.common.ShopCommonDocument;
import com.framework.loippi.entity.common.ShopCommonFeedback;
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
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.ApiUtils;
import com.framework.loippi.utils.Constants;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.Xerror;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            url.append(wapServer).append("/wap/document/" + shopCommonDocument.getId() + ".html?type=app");
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

}
