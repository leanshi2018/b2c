package com.framework.loippi.controller.user;


import com.framework.loippi.consts.Constants;
import com.framework.loippi.consts.MessageTypeConsts;
import com.framework.loippi.controller.BaseController;
import com.framework.loippi.dto.SysMessageDto;
import com.framework.loippi.dto.UserMessageDto;
import com.framework.loippi.entity.ShopCommonMessage;
import com.framework.loippi.entity.user.ShopMemberLeavingMessage;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.result.auths.AuthsLoginResult;
import com.framework.loippi.result.user.LeavingMessageDetailResult;
import com.framework.loippi.result.user.LeavingMessageListResult;
import com.framework.loippi.result.user.MessageListResult;
import com.framework.loippi.result.user.SysMessageListResult;
import com.framework.loippi.service.PushService;
import com.framework.loippi.service.ShopCommonMessageService;
import com.framework.loippi.service.ShopMemberMessageService;
import com.framework.loippi.service.user.ShopMemberLeavingMessageService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.ApiUtils;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.StringUtil;
import com.framework.loippi.utils.Xerror;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by longbh on 2017/8/12.
 */
@Controller
@ResponseBody
@RequestMapping("/api/message")
public class MessageCenterApiController extends BaseController {

    @Resource
    private ShopMemberMessageService shopMemberMessageService;
    @Resource
    private ShopCommonMessageService shopCommonMessageService;
    @Resource
    private ShopMemberLeavingMessageService shopMemberLeavingMessageService;
    @Resource
    private PushService pushService;
    @Value("#{properties['wap.server']}")
    private String wapServer;

    /**
     * 消息通知  首页
     *
     * @param request
     * @return
     */
    @RequestMapping("/messageList")
    public String messageList(HttpServletRequest request) {
        // 获取缓存实体
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        Long umessageType = shopMemberMessageService.countMessage(Long.parseLong(member.getMmCode()), MessageTypeConsts.MESSAGE_TYPE_SYS);  //消息统计
        Long adminMessageType = shopMemberMessageService.countMessage(Long.parseLong(member.getMmCode()), MessageTypeConsts.MESSAGE_TYPE_ADMIN);  //后台消息统计
        Map<String, Object> result = new HashMap<>();
        Long msgCount = umessageType + adminMessageType;
        result.put("messageCount", msgCount);
        List<Integer> bizTypeList = new ArrayList<>();
        bizTypeList.add(MessageTypeConsts.MESSAGE_TYPE_SYS);
        bizTypeList.add(MessageTypeConsts.MESSAGE_TYPE_ADMIN);
        UserMessageDto shopCommonMessage = shopMemberMessageService.findLastByTypes(Long.parseLong(member.getMmCode()), bizTypeList);
        result.put("msgContent", shopCommonMessage != null && shopCommonMessage.getTitle() != null
                ? shopCommonMessage.getTitle() : "");
        result.put("messageTime", shopCommonMessage == null || msgCount != null || msgCount == 0 ? 0 : shopCommonMessage.getCreateTime());
        return ApiUtils.success(result);
    }

//    /**
//     * 消息通知列表内容
//     *
//     * @param request
//     * @return
//     */
//    @RequestMapping("/myMessageList")
//    public String myMessageList(HttpServletRequest request) {
//        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
//        String pageSizeStr = (String) request.getParameter("pageSize");
//        String pageNumberStr = (String) request.getParameter("pageNumber");
//        Integer pageNumber = StringUtil.isEmpty(pageNumberStr) ? 0 : Integer.parseInt(pageNumberStr);
//        Integer pageSize = StringUtil.isEmpty(pageSizeStr) ? 10 : Integer.parseInt(pageSizeStr);
//        Pageable pageable = new Pageable(pageNumber, pageSize);
//        List<Integer> bizTypeList = new ArrayList<>();
//        bizTypeList.add(MessageTypeConsts.MESSAGE_TYPE_SYS);
//        bizTypeList.add(MessageTypeConsts.MESSAGE_TYPE_LIVE);
//        bizTypeList.add(MessageTypeConsts.MESSAGE_TYPE_ADMIN);
//        pageable.setParameter(Paramap.create().put("bizTypeList", bizTypeList).put("uid", member.getUserId()));
//        pageable.setOrderDirection(Order.Direction.ASC);
//        pageable.setOrderProperty("is_top");
//        Page<UserMessageDto> memberMessages = shopMemberMessageService.findMessagePage(pageable);
//        List<UserMessageDto> list = null;
//        if (memberMessages != null) {
//            list = memberMessages.getContent();
//        }
//        StringBuffer url = new StringBuffer();
//        url.append(wapServer).append("/wap/message/messageDetail.html?id=");
//        pushService.updateReadUMessage(member.getUserId(), MessageTypeConsts.MESSAGE_TYPE_SYS);
//        pushService.updateReadUMessage(member.getUserId(), MessageTypeConsts.MESSAGE_TYPE_LIVE);
//        pushService.updateReadUMessage(member.getUserId(), MessageTypeConsts.MESSAGE_TYPE_ADMIN);
//        return ApiUtils.success(SysMessageDto.build(list, url.toString()));
//    }

    /**
     * 消息列表
     *
     * @param request
     * @param bizType 消息类型 1-消息通知  2-提醒信息  3-订单信息 4-留言信息
     * @return
     */
    @RequestMapping("/myMessage")
    public String myMessage(HttpServletRequest request, @RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber,
                            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize, Integer bizType) {
        if (bizType == null) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        Pageable pageable = new Pageable(pageNumber, pageSize);
        pageable.setOrderProperty("is_top,create_time");
        pageable.setOrderDirection(Order.Direction.DESC);
        if (bizType == 1){
            pageable.setParameter(Paramap.create().put("bizType", bizType).put("uids", Long.parseLong(member.getMmCode())).put("type",1));
        }else{
            pageable.setParameter(Paramap.create().put("bizType", bizType).put("uid", Long.parseLong(member.getMmCode())));
        }
        List<UserMessageDto> memberMessageList = null;
        if (bizType == 3) {
            memberMessageList = shopMemberMessageService.findMsgOrderPage(pageable).getContent();
        } else {
            memberMessageList = shopMemberMessageService.findMessagePage(pageable).getContent();
        }
        pushService.updateReadUMessage(Long.parseLong(member.getMmCode()), bizType);
        return ApiUtils.success(MessageListResult.build(memberMessageList));
    }

    /**
     * 消息列表
     *
     * @param request
     * @return
     */
    @RequestMapping("/mySysMessage")
    public String mySysMessage(HttpServletRequest request, @RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber,
                            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        Pageable pageable = new Pageable(pageNumber, pageSize);pageable.setOrderProperty("is_top,create_time");
        pageable.setOrderDirection(Order.Direction.DESC);
        pageable.setOrderProperty("is_top,create_time");
        pageable.setOrderDirection(Order.Direction.DESC);
        ShopCommonMessage message = new ShopCommonMessage();
        message.setBizType(1);
        message.setType(1);
        pageable.setParameter(message);
        Page<ShopCommonMessage> page = shopCommonMessageService.findByPage(pageable);
        List<SysMessageListResult> results = SysMessageListResult.build(page.getContent());
        return ApiUtils.success(results);
    }

    /**
     * 留言消息列表
     *
     * @param request
     * @param
     * @param isReply 待回复 已回复
     * @return
     */
    @RequestMapping("/leavingMessageList")
    public String leavingMessageList(HttpServletRequest request, @RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber,
                                     @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize, @RequestParam(value = "isReply", defaultValue = "0") Integer isReply) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        Pageable pageable = new Pageable(pageNumber, pageSize);
        pageable.setParameter(Paramap.create().put("bizType", 4).put("uid", Long.parseLong(member.getMmCode())).put("isReply", isReply));
        List<UserMessageDto> memberMessageList = shopMemberMessageService.findMessagePage(pageable).getContent();
        pushService.updateReadUMessage(Long.parseLong(member.getMmCode()), 4);
        return ApiUtils.success(LeavingMessageListResult.build(memberMessageList));
    }

    /**
     * 新增留言
     *
     * @param request
     * @param title
     * @param content
     * @param image
     * @return
     */
    @RequestMapping("/addLeavingMessage")
    public String addLeavingMessage(HttpServletRequest request, String title, String content, String image) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if (StringUtils.isBlank(title) || StringUtils.isBlank(content)) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        shopMemberMessageService.saveLeavingMessage(title, content, image, Long.parseLong(member.getMmCode()));
        return ApiUtils.success();
    }

    /**
     * 查看留言消息详情
     *
     * @param request
     * @param id
     * @return
     */
    @RequestMapping("/leavingMessageDetail")
    public String leavingMessageDetail(HttpServletRequest request, Long id) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if (id == null) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        ShopCommonMessage shopCommonMessage = shopCommonMessageService.find(id);
        List<ShopMemberLeavingMessage> shopMemberLeavingMessageList = shopMemberLeavingMessageService.findList(Paramap.create().put("bizId", id)
                .put("uid", Long.parseLong(member.getMmCode())).put("order", "create_time"));
        return ApiUtils.success(LeavingMessageDetailResult.build(shopCommonMessage, shopMemberLeavingMessageList));
    }

    /**
     * 回复留言消息
     *
     * @param request
     * @param id
     * @return
     */
    @RequestMapping("/answerLeavingMessage")
    public String answerLeavingMessage(HttpServletRequest request, Long id, String replyContent) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if (id == null || StringUtils.isBlank(replyContent)) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        shopMemberLeavingMessageService.saveLeavingMessage(replyContent, 1, id, Long.parseLong(member.getMmCode()));
        return ApiUtils.success();
    }

    /**
     * 清空类型消息
     *
     * @param request
     * @param bizType
     * @return
     */
    @RequestMapping("/clearList")
    public String clearList(HttpServletRequest request, Integer bizType) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        shopMemberMessageService.deleteMessage(Long.parseLong(member.getMmCode()), bizType);
        return ApiUtils.success();
    }

}
