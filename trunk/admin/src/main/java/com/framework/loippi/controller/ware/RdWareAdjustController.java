package com.framework.loippi.controller.ware;

import javax.annotation.Resource;

import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.ware.RdWareAdjust;
import com.framework.loippi.service.ware.RdWareAdjustService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.framework.loippi.support.Message;
import com.framework.loippi.support.Pageable;

/**
 * Controller - 发货单表
 * 
 * @author zijing
 * @version 2.0
 */
@Controller("adminRdWareAdjustController")
@RequestMapping({ "/admin/rd_ware_adjust" })
public class RdWareAdjustController extends GenericController {

	@Resource
	private RdWareAdjustService rdWareAdjustService;

	/**
	 * 跳转添加页面
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = { "/add" }, method = { RequestMethod.GET })
	public String add(ModelMap model) {
		return "/admin/rd_ware_adjust/add";
	}

	/**
	 * 保存
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(RdWareAdjust rdWareAdjust, RedirectAttributes redirectAttributes) {
		rdWareAdjustService.save(rdWareAdjust);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 编辑
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Long id, ModelMap model) {
		RdWareAdjust rdWareAdjust = rdWareAdjustService.find(id);
		model.addAttribute("rdWareAdjust", rdWareAdjust);
		return "/ware/rd_ware_adjust/edit";
	}
	
	
	/**
	 * 详情
	 */
	@RequestMapping(value = "/view", method = RequestMethod.GET)
	public String view(Long id, ModelMap model) {
		RdWareAdjust rdWareAdjust = rdWareAdjustService.find(id);
		model.addAttribute("rdWareAdjust", rdWareAdjust);
		return "/ware/rd_ware_adjust/view";
	}
	

	/**
	 * 更新
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(RdWareAdjust rdWareAdjust, RedirectAttributes redirectAttributes) {
		rdWareAdjustService.update(rdWareAdjust);
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
	@RequestMapping(value = { "/list" }, method = { RequestMethod.GET })
	public String list(Pageable pageable, ModelMap model) {
		model.addAttribute("page", this.rdWareAdjustService.findByPage(pageable));
		return "/ware/rd_ware_adjust/list";
	}

	/**
	 * 删除操作
	 * 
	 * @param ids
	 * @return
	 */
	@RequestMapping(value = { "/delete" }, method = { RequestMethod.POST })
	public @ResponseBody Message delete(Long[] ids) {
		this.rdWareAdjustService.deleteAll(ids);
		return SUCCESS_MESSAGE;
	}
}
