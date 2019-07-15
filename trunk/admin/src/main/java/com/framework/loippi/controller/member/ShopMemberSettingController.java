package com.framework.loippi.controller.member;

import com.framework.loippi.consts.Constants;
import com.framework.loippi.controller.GenericController;
import com.framework.loippi.service.TUserSettingService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;


/**
 * 会员设置
 */
@Controller
@RequestMapping("/member/setting")
public class ShopMemberSettingController extends GenericController {

    @Autowired
    private TUserSettingService tUserSettingService;

    @RequiresPermissions("sys:membersetting:view")
    @RequestMapping("/find")
    public ModelAndView find() {
        ModelAndView modelAndView = new ModelAndView("/member/membersetting/setting");
        String checking = tUserSettingService.read("user_auto_pass") + "";
        if (checking == null || "null".equals(checking) || "".equals(checking)) {
            modelAndView.addObject("checking", "0");
        } else {
            modelAndView.addObject("checking", checking);
        }
        return modelAndView;
    }

    @RequiresPermissions("sys:membersetting:view")
    @RequestMapping("/update")
    public String update(@RequestParam("checking") String checking, Model model, HttpServletRequest request) {
        model.addAttribute("referer", request.getHeader("Referer"));
        tUserSettingService.save("user_auto_pass", checking);
        model.addAttribute("msg", "会员设置成功");
        return Constants.MSG_URL;
    }

}
