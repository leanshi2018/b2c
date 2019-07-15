package com.framework.loippi.controller.product;

import javax.annotation.Resource;

import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.product.ShopGoodsEvaluateKeywords;
import com.framework.loippi.service.product.ShopGoodsEvaluateKeywordsService;
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
 * Controller - 评价关键字表
 * 
 * @author dzm
 * @version 2.0
 */
@Controller("adminShopGoodsEvaluateKeywordsController")
@RequestMapping({ "/admin/shop_goods_evaluate_keywords" })
public class ShopGoodsEvaluateKeywordsController extends GenericController {

	@Resource
	private ShopGoodsEvaluateKeywordsService shopGoodsEvaluateKeywordsService;

	/**
	 * 跳转添加页面
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = { "/add" }, method = { RequestMethod.GET })
	public String add(ModelMap model) {
		return "/views/trade/shop_goods_evaluate_keywords/add";
	}

	/**
	 * 保存
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(ShopGoodsEvaluateKeywords shopGoodsEvaluateKeywords, RedirectAttributes redirectAttributes) {
		shopGoodsEvaluateKeywordsService.save(shopGoodsEvaluateKeywords);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 编辑
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Long id, ModelMap model) {
		ShopGoodsEvaluateKeywords shopGoodsEvaluateKeywords = shopGoodsEvaluateKeywordsService.find(id);
		model.addAttribute("shopGoodsEvaluateKeywords", shopGoodsEvaluateKeywords);
		return "/views/trade/shop_goods_evaluate_keywords/edit";
	}
	
	
	/**
	 * 详情
	 */
	@RequestMapping(value = "/view", method = RequestMethod.GET)
	public String view(Long id, ModelMap model) {
		ShopGoodsEvaluateKeywords shopGoodsEvaluateKeywords = shopGoodsEvaluateKeywordsService.find(id);
		model.addAttribute("shopGoodsEvaluateKeywords", shopGoodsEvaluateKeywords);
		return "/views/trade/shop_goods_evaluate_keywords/view";
	}
	

	/**
	 * 更新
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(ShopGoodsEvaluateKeywords shopGoodsEvaluateKeywords, RedirectAttributes redirectAttributes) {
		shopGoodsEvaluateKeywordsService.update(shopGoodsEvaluateKeywords);
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
	@RequestMapping("/list")
	public String list(Pageable pageable, ModelMap model,ShopGoodsEvaluateKeywords shopGoodsEvaluateKeywords) {
		pageable.setParameter(shopGoodsEvaluateKeywords);
		model.addAttribute("page", this.shopGoodsEvaluateKeywordsService.findByPage(pageable));
		model.addAttribute("shopGoodsEvaluateKeywords", shopGoodsEvaluateKeywords);
		return "/views/trade/shop_goods_evaluate_keywords/list";
	}

	/**
	 * 删除操作
	 * 
	 * @param ids
	 * @return
	 */
	@RequestMapping(value = { "/delete" }, method = { RequestMethod.POST })
	public @ResponseBody Message delete(Long[] ids) {
		this.shopGoodsEvaluateKeywordsService.deleteAll(ids);
		return SUCCESS_MESSAGE;
	}
}
