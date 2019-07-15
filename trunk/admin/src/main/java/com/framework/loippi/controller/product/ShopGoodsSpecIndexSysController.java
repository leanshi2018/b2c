package com.framework.loippi.controller.product;

import javax.annotation.Resource;

import com.framework.loippi.controller.GenericController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.framework.loippi.entity.product.ShopGoodsSpecIndex;
import com.framework.loippi.service.product.ShopGoodsSpecIndexService;
import com.framework.loippi.support.Message;
import com.framework.loippi.support.Pageable;

/**
 * Controller - 商品与规格对应表
 * 
 * @author zijing
 * @version 2.0
 */
@Controller("adminShopGoodsSpecIndexController")
@RequestMapping({ "/admin/shop_goods_spec_index" })
public class ShopGoodsSpecIndexSysController extends GenericController {

	@Resource
	private ShopGoodsSpecIndexService shopGoodsSpecIndexService;

	/**
	 * 跳转添加页面
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = { "/add" }, method = { RequestMethod.GET })
	public String add(ModelMap model) {
		return "/admin/shop_goods_spec_index/add";
	}

	/**
	 * 保存
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(ShopGoodsSpecIndex shopGoodsSpecIndex, RedirectAttributes redirectAttributes) {
		shopGoodsSpecIndexService.save(shopGoodsSpecIndex);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 编辑
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Long id, ModelMap model) {
		ShopGoodsSpecIndex shopGoodsSpecIndex = shopGoodsSpecIndexService.find(id);
		model.addAttribute("shopGoodsSpecIndex", shopGoodsSpecIndex);
		return "/admin/shop_goods_spec_index/edit";
	}
	
	
	/**
	 * 详情
	 */
	@RequestMapping(value = "/view", method = RequestMethod.GET)
	public String view(Long id, ModelMap model) {
		ShopGoodsSpecIndex shopGoodsSpecIndex = shopGoodsSpecIndexService.find(id);
		model.addAttribute("shopGoodsSpecIndex", shopGoodsSpecIndex);
		return "/admin/shop_goods_spec_index/view";
	}
	

	/**
	 * 更新
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(ShopGoodsSpecIndex shopGoodsSpecIndex, RedirectAttributes redirectAttributes) {
		shopGoodsSpecIndexService.update(shopGoodsSpecIndex);
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
		model.addAttribute("page", this.shopGoodsSpecIndexService.findByPage(pageable));
		return "/admin/shop_goods_spec_index/list";
	}

	/**
	 * 删除操作
	 * 
	 * @param ids
	 * @return
	 */
	@RequestMapping(value = { "/delete" }, method = { RequestMethod.POST })
	public @ResponseBody Message delete(Long[] ids) {
		this.shopGoodsSpecIndexService.deleteAll(ids);
		return SUCCESS_MESSAGE;
	}
}
