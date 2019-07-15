package com.framework.loippi.controller.ware;

import java.util.Optional;
import javax.annotation.Resource;

import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.ware.RdWarehouse;
import com.framework.loippi.service.ware.RdWarehouseService;
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
 * Controller - 仓库记录表
 * 
 * @author zijing
 * @version 2.0
 */
@Controller("adminRdWarehouseController")
@RequestMapping({ "/admin/rd_warehouse" })
public class RdWarehouseController extends GenericController {

	@Resource
	private RdWarehouseService rdWarehouseService;

	/**
	 * 跳转添加页面
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = { "/add" }, method = { RequestMethod.GET })
	public String add(ModelMap model) {
		return "/admin/rd_warehouse/add";
	}

	/**
	 * 保存
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(RdWarehouse rdWarehouse, RedirectAttributes redirectAttributes) {
		rdWarehouseService.save(rdWarehouse);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 编辑
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Long id, ModelMap model) {
		RdWarehouse rdWarehouse = rdWarehouseService.find(id);
		model.addAttribute("rdWarehouse", rdWarehouse);
		return "/ware/rd_warehouse/edit";
	}
	
	
	/**
	 * 详情
	 */
	@RequestMapping(value = "/view", method = RequestMethod.GET)
	public String view(Long id, ModelMap model) {
		RdWarehouse rdWarehouse = rdWarehouseService.find(id);
		model.addAttribute("rdWarehouse", rdWarehouse);
		return "/ware/rd_warehouse/view";
	}
	

	/**
	 * 更新
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(RdWarehouse rdWarehouse, RedirectAttributes redirectAttributes) {
		rdWarehouseService.update(rdWarehouse);
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
	@RequestMapping(value = "/list" )
	public String list(Pageable pageable, ModelMap model,RdWarehouse rdWarehouse,Integer index,Long specId) {
		if (Optional.ofNullable(specId).orElse(-1L)!=-1){
			rdWarehouse.setSpecId(specId);
			model.addAttribute("specId", "specId");
		}
		pageable.setParameter(rdWarehouse);
		model.addAttribute("page", this.rdWarehouseService.findByPage(pageable));
		model.addAttribute("rdWarehouse", rdWarehouse);
		model.addAttribute("index", index);
		return "/ware/rd_warehouse/list";
	}

	/**
	 * 删除操作
	 * 
	 * @param ids
	 * @return
	 */
	@RequestMapping(value = { "/delete" }, method = { RequestMethod.POST })
	public @ResponseBody Message delete(Long[] ids) {
		this.rdWarehouseService.deleteAll(ids);
		return SUCCESS_MESSAGE;
	}
}
