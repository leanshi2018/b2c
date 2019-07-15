package com.framework.loippi.controller.admin;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.framework.loippi.entity.User;
import com.framework.loippi.service.UserService;
import com.framework.loippi.controller.GenericController;
/**
 * Controlelr - 个人资料
 * 
 * @author Loippi Team
 * @version 1.0
 */
@Controller("adminProfileController")
@RequestMapping("/admin/profile")
public class ProfileController extends GenericController {

	@Resource
	private UserService userService;

	/**
	 * 编辑
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit( ModelMap model) {
		model.addAttribute("user",userService.getCurrent());
		return "/admin/profile/edit";
	}

	/**
	 * 详情
	 */
	@RequestMapping(value = "/view", method = RequestMethod.GET)
	public String view(ModelMap model) {
		model.addAttribute("user",userService.getCurrent());
		return "/admin/profile/view";
	}

	/**
	 * 更新
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(String avatar ,String nickname, RedirectAttributes redirectAttributes) {
		User user = userService.getCurrent();
		user.setAvatar(avatar);
		user.setNickname(nickname);
		userService.update(user);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:edit.jhtml";
	}


}
