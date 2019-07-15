package com.framework.loippi.controller.common;

import com.framework.loippi.consts.Constants;
import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.User;
import com.framework.loippi.entity.common.ShopCommonFeedback;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.service.UserService;
import com.framework.loippi.service.common.ShopCommonFeedbackService;
import com.framework.loippi.support.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Controller - 系统反馈
 *
 * @author zijing
 * @version 2.0
 */
@Controller("adminShopCommonFeedbackController")
@RequestMapping({"/admin/shop_common_feedback"})
public class ShopCommonFeedbackController extends GenericController {

    @Resource
    private ShopCommonFeedbackService shopCommonFeedbackService;
    @Resource
    private UserService userService;

    /**
     * 跳转添加页面
     *
     * @param model
     * @return
     */
    @RequestMapping(value = {"/add"}, method = {RequestMethod.GET})
    public String add(ModelMap model) {
        return "/common/shop_common_feedback/add";
    }

    /**
     * 保存
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(ShopCommonFeedback shopCommonFeedback, RedirectAttributes redirectAttributes) {
        shopCommonFeedbackService.save(shopCommonFeedback);
        addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
        return "redirect:list.jhtml";
    }

    /**
     * 编辑
     */
    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String edit(Long id, ModelMap model) {
        ShopCommonFeedback shopCommonFeedback = shopCommonFeedbackService.find(id);
        model.addAttribute("shopCommonFeedback", shopCommonFeedback);
        return "/common/feedback/edit";
    }

    /**
     * 详情
     */
    @RequestMapping(value = "/view", method = RequestMethod.GET)
    public String view(Long id, ModelMap model) {
        ShopCommonFeedback shopCommonFeedback = shopCommonFeedbackService.find(id);
        model.addAttribute("shopCommonFeedback", shopCommonFeedback);
        return "/common/feedback/view";
    }


    /**
     * 更新
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String update(ShopCommonFeedback shopCommonFeedback, RedirectAttributes redirectAttributes) {
        shopCommonFeedbackService.update(shopCommonFeedback);
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
        model.addAttribute("page", this.shopCommonFeedbackService.findByPage(pageable));
        model.addAttribute("paramter", pageable.getParameter());
        return "/common/feedback/listFeedback";
    }

    /**
     * 标记已解决
     *
     * @param id
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/markFeedback")
    public String markFeedback(@RequestParam Long id, String message, HttpServletRequest request, ModelMap model) {
        User user = userService.getCurrent();
        String referer = request.getHeader("Referer");

        ShopCommonFeedback br = shopCommonFeedbackService.find(id);
        br.setOpTime(new Date());
        br.setManageId(user.getId());
        br.setStatus(1);
        br.setReplyContent(message);
        shopCommonFeedbackService.update(br);

        model.addAttribute("referer", referer);
        model.addAttribute("msg", "修改成功");
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
        this.shopCommonFeedbackService.deleteAll(ids);
        model.addAttribute("referer", request.getHeader("Referer"));
        model.addAttribute("msg", "删除成功");
        return Constants.MSG_URL;
    }
}
