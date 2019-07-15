package com.framework.loippi.controller.trade;

import javax.annotation.Resource;

import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.product.ShopGoodsFreightRule;
import com.framework.loippi.entity.user.RdRanks;
import com.framework.loippi.service.product.ShopGoodsFreightRuleService;
import com.framework.loippi.service.user.RdRanksService;
import com.framework.loippi.utils.Paramap;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import com.framework.loippi.support.Message;
import com.framework.loippi.support.Pageable;

import java.util.*;

/**
 * Controller - 运费规则
 * 
 * @author dzm
 * @version 2.0
 */
@Controller("adminShopGoodsFreightRuleController")
@RequestMapping({ "/admin/shop_goods_freight_rule" })
public class ShopGoodsFreightRuleController extends GenericController {

	@Resource
	private ShopGoodsFreightRuleService shopGoodsFreightRuleService;
	@Resource
	private RdRanksService rdRanksService;

	/**
	 * 跳转添加页面
	 * 
	 * @param
	 * @param model
	 * @return
	 */
	@RequiresPermissions("admin:freight:main")
	@RequestMapping(value = { "/add" }, method = { RequestMethod.GET })
	public String add(ModelMap model) {
		List<RdRanks> shopMemberGradeList=rdRanksService.findAll();
		List<ShopGoodsFreightRule> shopGoodsFreightRuleList=shopGoodsFreightRuleService.findList("groupBy","group by member_grade_id");
		Map<String,ShopGoodsFreightRule> shopMemberGradeMap=new HashMap<>();
		for (ShopGoodsFreightRule item:shopGoodsFreightRuleList) {
			shopMemberGradeMap.put(item.getMemberGradeId()+"",item);
		}
		List<RdRanks> shopMemberGrade=new ArrayList<>();
		for (RdRanks item:shopMemberGradeList) {
			if (shopMemberGradeMap.get(item.getRankId()+"")==null){
				shopMemberGrade.add(item);
			}
		}
		model.addAttribute("shopMemberGradeList", shopMemberGrade);
		return "/trade/shop_goods_freight_rule/add";
	}

	/**
	 * 保存
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(ShopGoodsFreightRule shopGoodsFreightRule, RedirectAttributes redirectAttributes) {

		if (shopGoodsFreightRule.getMemberGradeId()==null){
			addFlashMessage(redirectAttributes, ERROR_MESSAGE);
			return "redirect:list.jhtml";
		}
		List<ShopGoodsFreightRule> shopGoodsFreightRuleList=shopGoodsFreightRuleService.findList(Paramap.create().put("memberGradeId",shopGoodsFreightRule.getMemberGradeId()));
		if (shopGoodsFreightRuleList.size()>0){
			addFlashMessage(redirectAttributes, ERROR_MESSAGE);
			return "redirect:list.jhtml";
		}
		List<ShopGoodsFreightRule> freightRuleList=new ArrayList<>();
		RdRanks shopMemberGrade=rdRanksService.find("rankId",shopGoodsFreightRule.getMemberGradeId());
		for (ShopGoodsFreightRule item:shopGoodsFreightRule.getShopGoodsFreightRules()) {
			ShopGoodsFreightRule freightRule=new ShopGoodsFreightRule();
			freightRule.setMemberGradeName(shopMemberGrade.getRankName());
			freightRule.setMemberGradeId(shopGoodsFreightRule.getMemberGradeId());
			freightRule.setCreateTime(new Date());
			freightRule.setUpdateTime(new Date());
			freightRule.setMinimumOrderAmount(item.getMinimumOrderAmount());
			freightRule.setMaximumOrderAmount(item.getMaximumOrderAmount());
			freightRule.setPreferential(item.getPreferential());
			freightRule.setId(twiterIdService.getTwiterId());
			freightRuleList.add(freightRule);
		}
		shopGoodsFreightRuleService.insertBatch(freightRuleList);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 编辑
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Long id, ModelMap model) {
		List<ShopGoodsFreightRule> shopGoodsFreightRuleList = shopGoodsFreightRuleService.findList("memberGradeId",id);
		model.addAttribute("shopGoodsFreightRule", shopGoodsFreightRuleList.get(0));
		model.addAttribute("shopGoodsFreightRuleList", shopGoodsFreightRuleList);
		return "/trade/shop_goods_freight_rule/edit";
	}
	
	
	/**
	 * 详情
	 */
	@RequiresPermissions("admin:freight:main")
	@RequestMapping(value = "/view", method = RequestMethod.GET)
	public String view(Long id, ModelMap model) {
		ShopGoodsFreightRule shopGoodsFreightRule = shopGoodsFreightRuleService.find(id);
		model.addAttribute("shopGoodsFreightRule", shopGoodsFreightRule);
		return "/trade/shop_goods_freight_rule/view";
	}
	

	/**
	 * 更新
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(ShopGoodsFreightRule shopGoodsFreightRule, RedirectAttributes redirectAttributes) {
		List<ShopGoodsFreightRule> freightRuleList=new ArrayList<>();
		for (ShopGoodsFreightRule item:shopGoodsFreightRule.getShopGoodsFreightRules()) {
			item.setUpdateTime(new Date());
			freightRuleList.add(item);
		}
		shopGoodsFreightRuleService.updateBatch(freightRuleList);
		freightRuleList.clear();
		RdRanks shopMemberGrade=rdRanksService.find("rankId",shopGoodsFreightRule.getMemberGradeId());
		for (ShopGoodsFreightRule item:shopGoodsFreightRule.getShopGoodsFreightRules()) {
			if (item.getId()==null){
				ShopGoodsFreightRule freightRule=new ShopGoodsFreightRule();
				freightRule.setMemberGradeName(shopMemberGrade.getRankName());
				freightRule.setMemberGradeId(shopGoodsFreightRule.getMemberGradeId());
				freightRule.setCreateTime(new Date());
				freightRule.setUpdateTime(new Date());
				freightRule.setMinimumOrderAmount(item.getMinimumOrderAmount());
				freightRule.setMaximumOrderAmount(item.getMaximumOrderAmount());
				freightRule.setPreferential(item.getPreferential());
				freightRule.setId(twiterIdService.getTwiterId());
				freightRuleList.add(freightRule);
			}
		}
		if (!freightRuleList.isEmpty()){
			shopGoodsFreightRuleService.insertBatch(freightRuleList);
		}
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
	@RequiresPermissions("admin:freight:main")
	@RequestMapping(value = { "/list" }, method = { RequestMethod.GET })
	public String list(Pageable pageable, ModelMap model,ShopGoodsFreightRule shopGoodsFreightRule) {
		pageable.setParameter(shopGoodsFreightRule);
		List<RdRanks> shopMemberGradeList=rdRanksService.findAll();
		model.addAttribute("page", this.shopGoodsFreightRuleService.findByPage(pageable));
		model.addAttribute("shopMemberGradeList", shopMemberGradeList);
		model.addAttribute("shopGoodsFreightRule", shopGoodsFreightRule);
		return "/trade/shop_goods_freight_rule/list";
	}

	/**
	 * 删除操作
	 * 
	 * @param ids
	 * @return
	 */
	@RequestMapping(value = { "/delete" }, method = { RequestMethod.POST })
	public @ResponseBody Message delete(Long[] ids) {
		this.shopGoodsFreightRuleService.deleteAll(ids);
		return SUCCESS_MESSAGE;
	}
}
