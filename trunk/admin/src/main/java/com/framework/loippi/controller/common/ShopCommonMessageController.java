package com.framework.loippi.controller.common;

import com.framework.loippi.consts.Constants;
import com.framework.loippi.controller.GenericController;
import com.framework.loippi.dto.MessageTemplateDto;
import com.framework.loippi.entity.ShopCommonMessage;
import com.framework.loippi.entity.ShopMemberMessage;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.service.PushService;
import com.framework.loippi.service.RedisService;
import com.framework.loippi.service.ShopCommonMessageService;
import com.framework.loippi.service.ShopMemberMessageService;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.support.Message;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.JacksonUtil;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.ParameterUtils;
import com.framework.loippi.utils.StringUtil;
import com.framework.loippi.utils.jpush.JpushUtils;
import com.framework.loippi.utils.sms.AldayuUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.RequestContext;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Controller -
 *
 * @author zijing
 * @version 2.0
 */
@Controller("adminShopCommonMessageController")
@RequestMapping({"/admin/shop_common_message"})
public class ShopCommonMessageController extends GenericController {

    @Resource
    private ShopCommonMessageService shopCommonMessageService;
    @Resource
    private ShopMemberMessageService shopMemberMessageService;
    @Resource
    private PushService pushService;
    @Resource
    private RdMmBasicInfoService rdMmBasicInfoService;
    @Resource
    private RedisService redisService;

    @RequestMapping("/list")
    public String list(Model model, HttpServletRequest request,
                       @RequestParam(required = false, value = "div", defaultValue = "") String div,
                       @RequestParam(required = false, value = "pageNo", defaultValue = "1") Integer pageNo) {
        Pageable pager = new Pageable(pageNo, 10);
        Map<String, Object> paramter = ParameterUtils.getParametersMapStartingWith(request, "filter_");
        paramter.put("bizType", 1);
        pager.setParameter(paramter);
        pager.setOrderProperty("create_time");
        pager.setOrderDirection(Order.Direction.DESC);
        Page<ShopCommonMessage> results = shopCommonMessageService.findByPage(pager);//结果集
        model.addAttribute("page", results);
        model.addAttribute("paramter", paramter);
        return "common/shop_common_message/list";
    }

//    @RequestMapping("/index")
//    public String index(Model model, Pageable pageable,ShopCommonMessage shopCommonMessage) {
//        shopCommonMessage.setBizType(3);
//        shopCommonMessage.setSendUid(0+"");
//        pageable.setParameter(shopCommonMessage);
//        pageable.setOrderProperty("create_time");
//        pageable.setOrderDirection(Order.Direction.DESC);
//        Page<ShopCommonMessage> results = shopCommonMessageService.findByPage(pageable);//结果集
//        model.addAttribute("page", results);
//        model.addAttribute("param", shopCommonMessage);
//        return "common/shop_common_message/index";
//    }

    /**
     * 更新发布状态
     *
     * @param
     * @return
     */
    @RequestMapping(value = {"/updateIsReply"}, method = {RequestMethod.GET})
    public String updateIsReply(Long id) {
        ShopCommonMessage shopCommonMessage = new ShopCommonMessage();
        shopCommonMessage.setId(id);
        shopCommonMessage.setIsReply(1);
        shopCommonMessageService.update(shopCommonMessage);
        return "redirect:/admin/common/index.jhtml";
    }

    @RequestMapping("/forward")
    public String forward(Model model, @RequestParam Long id) {
        if (id != 0 && id != null) {
            model.addAttribute("message", shopCommonMessageService.find(id));
            return "common/shop_common_message/edit";
        } else {
            String messageTemplateJson = redisService.get("messageTemplate");
            List<MessageTemplateDto> messageTemplateDtoList = new ArrayList<>();
            if (!StringUtil.isEmpty(messageTemplateJson)) {
                messageTemplateDtoList = JacksonUtil.convertList(messageTemplateJson, MessageTemplateDto.class);
            }
            model.addAttribute("messageTemplateDtoList", messageTemplateDtoList);
            return "common/shop_common_message/save";
        }
    }

    /**
     * 新增短信模板
     *
     * @return
     */
    @RequestMapping("/messageTemplate")
    public String messageTemplate() {
        return "common/shop_common_message/addMessageTemplate";
    }

    @RequestMapping("/edit")
    public String saveOrUpdate(@ModelAttribute ShopCommonMessage message, String memberId, HttpServletRequest request, Model model, String code) {
        RequestContext requestContext = new RequestContext(request);
        model.addAttribute("referer", request.getContextPath() + "/admin/shop_common_message/list.jhtml");
        Long msgId = null;
        String[] sendUidSort = null;
        boolean flag = true;
        if (message.getId() == null || message.getId() == 0) {
            if (message.getUType() == 2) {
                message.setSendUid("");
            } else if (message.getUType() == 1) {
                sendUidSort = memberId.split(",");
                message.setSendUid(JacksonUtil.toJson(Arrays.asList(memberId.split(","))));
            }
            message.setIsTop(1);
            message.setCreateTime(new Date());
            message.setBizType(1);
            message.setCreateTime(new Date());
            msgId = twiterIdService.getTwiterId();
            message.setId(msgId);
            try {

                message.setId(msgId);
                if (message.getType() == 3) {
                    //发送消息 极光推送
                    if (sendUidSort == null) {
                        List<RdMmBasicInfo> shopMemberList = rdMmBasicInfoService.findList(Paramap.create().put("pushStatus", 1));
                        int length = shopMemberList.size();
                        sendUidSort = new String[length];
                        for (int i = 0; i < length; i++) {
                            sendUidSort[i] = shopMemberList.get(i).getMmCode() + "";
                        }
                        JpushUtils.push2all(message.getContent());
                    }
                    else {
                        JpushUtils.push2alias(message.getContent(), sendUidSort, null, 0);
                    }
                    //JpushUtils.push2alias(message.getContent(), sendUidSort, null, 0);
                    pushService.sendMessage(message.getContent(), sendUidSort);
                } else if (message.getType() == 2) {
                    //短信发送
                    if (StringUtil.isEmpty(code)) {
                        model.addAttribute("msg", "短信模板CODE为空");
                        flag = false;
                    }
                    //获取所有短信模板
                    String messageTemplateJson = redisService.get("messageTemplate");
                    List<MessageTemplateDto> messageTemplateDtoList = new ArrayList<>();
                    MessageTemplateDto messageTemplateDto = null;
                    if (!StringUtil.isEmpty(messageTemplateJson)) {
                        messageTemplateDtoList = JacksonUtil.convertList(messageTemplateJson, MessageTemplateDto.class);
                    }
                    //找到选择的短信模板
                    for (MessageTemplateDto item : messageTemplateDtoList) {
                        if (item.getCode().equals(code)) {
                            messageTemplateDto = item;
                        }
                    }
                    if (messageTemplateDto == null) {
                        model.addAttribute("msg", "短信模板出错");
                        flag = false;
                    }
                    message.setTitle(messageTemplateDto.getTitle());
                    message.setContent(messageTemplateDto.getContent());
                    //进行发送处理
                    try {
                        SendBatchSms(messageTemplateDto, message, sendUidSort);
                        model.addAttribute("msg", requestContext.getMessage("successful_modification"));
                    } catch (Exception ex) {
                        flag = false;
                        model.addAttribute("msg", "消息推送失败");
                        ex.printStackTrace();
                    }
                } else if (message.getType() == 1) {
                    if (!message.getSendUid().equals("")) {
                        List<ShopMemberMessage> shopMemberMessageList = new ArrayList<>();
                        String[] memberIdsArr = memberId.split(",");
                        for (String uid : memberIdsArr) {
                            shopMemberMessageList.add(addMsg(msgId, uid));
                        }

                        if (shopMemberMessageList != null && shopMemberMessageList.size() > 0) {
                            shopMemberMessageService.addTrainRecordBatch(shopMemberMessageList);
                        }
                    }
                }
                model.addAttribute("msg", requestContext.getMessage("Delivery_settings_msg17"));
            } catch (Exception e) {
                flag = false;
                e.printStackTrace();
                model.addAttribute("msg", "消息推送失败");
            }
            if (flag) {
                shopCommonMessageService.save(message);
            }
        } else {
            shopCommonMessageService.update(message);
            model.addAttribute("msg", requestContext.getMessage("successful_modification"));
        }

        return Constants.MSG_URL;
    }


    private ShopMemberMessage addMsg(Long msgId, String uid) {
        ShopMemberMessage shopMemberMessage = new ShopMemberMessage();
        shopMemberMessage.setBizType(1);
        shopMemberMessage.setCreateTime(new Date());
        shopMemberMessage.setId(twiterIdService.getTwiterId());
        shopMemberMessage.setIsRead(2);
        shopMemberMessage.setMsgId(msgId);
        shopMemberMessage.setUid(Long.parseLong(uid));
        return shopMemberMessage;
    }

    /**
     * 添加消息模板
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/addMessageTemplate")
    public String addMessageTemplate(MessageTemplateDto messageTemplateDto) {
        String messageTemplateJson = redisService.get("messageTemplate");
        List<MessageTemplateDto> messageTemplateDtoList = new ArrayList<>();
        if (!StringUtil.isEmpty(messageTemplateJson)) {
            messageTemplateDtoList = JacksonUtil.convertList(messageTemplateJson, MessageTemplateDto.class);
        }
        if (messageTemplateDto != null) {
            messageTemplateDtoList.add(messageTemplateDto);
        }
        redisService.save("messageTemplate", messageTemplateDtoList);
        return "redirect:/admin/shop_common_message/list.jhtml";
    }

    /**
     * 删除消息模板
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/deleteMessageTemplate")
    public
    @ResponseBody
    Message deleteMessageTemplate(String code) {
        redisService.delete("messageTemplate");
        String messageTemplateJson = redisService.get("messageTemplate");
        List<MessageTemplateDto> messageTemplateDtoList = new ArrayList<>();
        try {
            if (!StringUtil.isEmpty(messageTemplateJson)) {
                messageTemplateDtoList = JacksonUtil.convertList(messageTemplateJson, MessageTemplateDto.class);
            }
            for (MessageTemplateDto item : messageTemplateDtoList) {
                if (item.getCode().equals(code)) {
                    messageTemplateDtoList.remove(item);
                    redisService.save("messageTemplate", messageTemplateDtoList);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Message.error("删除失败！");
        }
        return SUCCESS_MESSAGE;
    }

    /**
     * 更新发布状态
     *
     * @param
     * @return
     */
    @RequestMapping(value = {"/updateStatus"}, method = {RequestMethod.GET})
    public String updateStatus(Long id, Integer status) {
        ShopCommonMessage shopCommonMessage = new ShopCommonMessage();
        shopCommonMessage.setId(id);
        shopCommonMessage.setIsTop(status);
        shopCommonMessageService.update(shopCommonMessage);
        return "redirect:/admin/shop_common_message/list.jhtml";
    }

    /**
     * 更新发布状态
     *
     * @param
     * @return
     */
    @RequestMapping(value = {"/updateOnline"}, method = {RequestMethod.GET})
    public String updateOnline(Long id, Integer status) {
        ShopCommonMessage shopCommonMessage = new ShopCommonMessage();
        shopCommonMessage.setId(id);
        shopCommonMessage.setOnLine(status);
        shopCommonMessageService.update(shopCommonMessage);
        return "redirect:/admin/shop_common_message/list.jhtml";
    }


    /**
     * 删除APP图片
     *
     * @return
     */
    @RequestMapping("/delete")
    public String delete(@RequestParam Long[] ids, HttpServletRequest request, Model model) {
        String referer = request.getHeader("Referer");
        model.addAttribute("referer", referer);
        if (ids == null || ids.length == 0) {
            model.addAttribute("result", "ID为空");
            model.addAttribute("msg", "删除失败，ID为空");
        } else {
            // 删除消息中心-平台消息
            shopCommonMessageService.deleteAll(ids);
            // 删除推送消息
            // 若不删除推送消息，那么删除了平台消息后用户还是可以在消息中心看到消息
            shopCommonMessageService.deleteAllMemberMessage(ids);
            model.addAttribute("msg", "删除成功");
        }
        return Constants.MSG_URL;
    }

    @RequestMapping("/view")
    public String view(Model model, @RequestParam Long id) {
        ShopCommonMessage message = shopCommonMessageService.find(id);
        if (message != null && !StringUtil.isEmpty(message.getSendUid())) {
            List<String> ids;
            if (message.getUType() == 1) {
                ids = new ArrayList<>();
                if (message.getSendUid() != null) {
                    ids.add(message.getSendUid());
                }
            } else {
                ids = JacksonUtil.convertList(message.getSendUid(), String.class);
            }
            if (ids.size() > 0) {
                List<RdMmBasicInfo> map = rdMmBasicInfoService.findShopMember(ids);
                StringBuffer name = new StringBuffer();
                for (RdMmBasicInfo member : map) {
                    if (!StringUtil.isEmpty(member.getMmNickName())) {
                        name.append(member.getMmNickName()).append(",");
                    } else if (!StringUtil.isEmpty(member.getMmName())) {
                        name.append(member.getMmName()).append(",");
                    }
                }
                model.addAttribute("name", name.toString());
            }
        }
        model.addAttribute("message", message);
        model.addAttribute("typeFileds", message.getUType());
        return "common/shop_common_message/view";
    }

    public void SendBatchSms(MessageTemplateDto messageTemplateDto, ShopCommonMessage message, String[] sendUidSort) {
        if (sendUidSort == null) {
            List<RdMmBasicInfo> shopMemberList = rdMmBasicInfoService.findList(Paramap.create().put("pushStatus", 1));
            int length = shopMemberList.size();
            for (int i = 0; i < length; i++) {
                try {
                    AldayuUtil.sendSms(shopMemberList.get(i).getMobile(), "{\"code\":\"" + (i + 1) + "\"}", messageTemplateDto.getCode(), messageTemplateDto.getSignName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            int length = sendUidSort.length;
            List<RdMmBasicInfo> shopMemberList = rdMmBasicInfoService.findList(Paramap.create().put("mmCodes", sendUidSort));
            for (int i = 0; i < length; i++) {
                try {
                    AldayuUtil.sendSms(shopMemberList.get(i).getMobile(), "{\"code\":\"" + (i + 1) + "\"}", messageTemplateDto.getCode(), messageTemplateDto.getSignName());
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }
    }


}
