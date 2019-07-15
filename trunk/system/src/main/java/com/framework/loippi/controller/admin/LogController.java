package com.framework.loippi.controller.admin;

import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.Log;
import com.framework.loippi.service.LogService;
import com.framework.loippi.support.Message;
import com.framework.loippi.support.Pageable;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
/**
 * Controlelr - 日志
 * 
 * @author Loippi Team
 * @version 1.0
 */
@Controller("adminogController")
@RequestMapping("/admin/log")
public class LogController extends GenericController {

	@Resource
	private LogService logService;

	/**
	 * 列表
	 */
	@RequiresPermissions("admin:system:log")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Pageable pageable, ModelMap model,Log log) {
		model.addAttribute("log",log);
		pageable.setParameter(log);
		model.addAttribute("page", logService.findByPage(pageable));
		return "/admin/log/list";
	}

	/**
	 * 查看
	 */
	@RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
	public String view(@PathVariable Long id, ModelMap model) {
		model.addAttribute("log", logService.find(id));
		return "/admin/log/view";
	}

	/**
	 * 删除
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public @ResponseBody
	Message delete(Long[] ids) {
		logService.deleteAll(ids);
		return SUCCESS_MESSAGE;
	}

	/**
	 * 清空
	 */
	@RequestMapping(value = "/clear", method = RequestMethod.POST)
	public @ResponseBody
	Message clear() {
		logService.clear();
		return SUCCESS_MESSAGE;
	}
}
