package com.framework.loippi.controller.common;

import com.framework.loippi.consts.Constants;
import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.ShopCommonMessage;
import com.framework.loippi.entity.User;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.ShopMemberLeavingMessage;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.service.ShopCommonMessageService;
import com.framework.loippi.service.UserService;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.service.user.ShopMemberLeavingMessageService;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.Paramap;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Controller - 留言
 *
 * @author zijing
 * @version 2.0
 */
@Controller("adminShopLeavingMessageController")
@RequestMapping({"/admin/shop_leving_message"})
public class ShopLeavingMessageController extends GenericController {

    @Resource
    private ShopMemberLeavingMessageService shopMemberLeavingMessageService;
    @Resource
    private UserService userService;
    @Resource
    private ShopCommonMessageService shopCommonMessageService;
    @Resource
    private RdMmBasicInfoService rdMmBasicInfoService;

    /**
     * 跳转添加页面
     *
     * @param model
     * @return
     */
    @RequestMapping(value = {"/add"}, method = {RequestMethod.GET})
    public String add(ModelMap model) {
        return "/common/levingmessage/add";
    }

    /**
     * 保存
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(ShopMemberLeavingMessage shopCommonlevingmessage, RedirectAttributes redirectAttributes) {
        shopMemberLeavingMessageService.save(shopCommonlevingmessage);
        addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
        return "redirect:list.jhtml";
    }

    /**
     * 详情
     */
    @RequestMapping(value = "/view", method = RequestMethod.GET)
    public String view(Long id, ModelMap model) {
        ShopCommonMessage shopCommonMessage=shopCommonMessageService.find(id);
        List<ShopMemberLeavingMessage> shopCommonlevingmessage = shopMemberLeavingMessageService.findList(Paramap.create().put("order","create_time").put("bizId",shopCommonMessage.getId()));
        RdMmBasicInfo rdMmBasicInfo=rdMmBasicInfoService.find("mmCode",shopCommonMessage.getSendUid());
            model.addAttribute("shopCommonlevingmessage", shopCommonlevingmessage);

        model.addAttribute("shopMember", rdMmBasicInfo);
        model.addAttribute("shopCommonMessage", shopCommonMessage);
        return "/common/levingmessage/view";
    }


    /**
     * 更新
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String update(ShopMemberLeavingMessage shopCommonlevingmessage, RedirectAttributes redirectAttributes) {
        shopMemberLeavingMessageService.update(shopCommonlevingmessage);
        addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
        return "redirect:list.jhtml";
    }

    /**
     * 列表查询
     *
     * @param pageable
     * @param model
     * @return
     */
    @RequestMapping(value = {"/list"})
    public String list(Pageable pageable, ModelMap model, HttpServletRequest request) {
        processQueryConditions(pageable, request);
        pageable.setOrderProperty("create_time");
        pageable.setOrderDirection(Order.Direction.DESC);

        model.addAttribute("page", this.shopCommonMessageService.findByPage(pageable));
        model.addAttribute("paramter", pageable.getParameter());
        return "/common/levingmessage/listLevingMessage";
    }

    /**
     * 回复
     *
     * @param id
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/markLevingMessage")
    public String markLevingMessage(@RequestParam Long id, String message, HttpServletRequest request, ModelMap model) {
        User user = userService.getCurrent();
        ShopCommonMessage shopCommonMessage=shopCommonMessageService.find(id);
        if (shopCommonMessage.getIsReply()!=1){
            shopCommonMessage.setIsReply(1);
            shopCommonMessageService.update(shopCommonMessage);
        }
        shopMemberLeavingMessageService.saveLeavingMessage(message,0,id,Long.parseLong(shopCommonMessage.getSendUid()));
        String referer = request.getHeader("Referer");
        model.addAttribute("referer", referer);
        model.addAttribute("msg", "回复成功");
        return Constants.MSG_URL;
    }

    /**
     * 删除
     *
     * @param ids
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = {"/delete"}, method = {RequestMethod.POST})
    public String delete(Long[] ids, HttpServletRequest request, ModelMap model) {
        this.shopMemberLeavingMessageService.deleteAll(ids);
        model.addAttribute("referer", request.getHeader("Referer"));
        model.addAttribute("msg","删除成功");
        return Constants.MSG_URL;
    }
}
