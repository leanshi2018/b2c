package com.framework.loippi.controller.trade;

import com.framework.loippi.consts.Constants;
import javax.annotation.Resource;

import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.common.ShopCommonArea;
import com.framework.loippi.entity.product.ShopGoodsFreight;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.common.ShopCommonAreaService;
import com.framework.loippi.service.product.ShopGoodsFreightService;
import com.framework.loippi.utils.Paramap;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.framework.loippi.support.Message;
import com.framework.loippi.support.Pageable;

import java.util.Date;
import java.util.List;

/**
 * Controller - 运费
 * 
 * @author dzm
 * @version 2.0
 */
@Controller("adminShopGoodsFreightController")
@RequestMapping({ "/admin/shop_goods_freight" })
public class ShopGoodsFreightController extends GenericController {

	@Resource
	private ShopGoodsFreightService shopGoodsFreightService;
	@Resource
	private ShopCommonAreaService areaService;
	@Autowired
	private TwiterIdService twiterIdService;
	/**
	 * 跳转添加页面
	 * 
	 * @param
	 * @param model
	 * @return
	 */
	@RequestMapping(value = { "/add" }, method = { RequestMethod.GET })
	public String add(ModelMap model) {
		Paramap paramap = Paramap.create().put("isDel", 0).put("areaParentId", 0);
		List<ShopCommonArea> areas = areaService.findList(paramap);
		model.addAttribute("areas", areas);
		return "/trade/shop_goods_freight/add";
	}

	/**
	 * 保存
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(ModelMap model,ShopGoodsFreight shopGoodsFreight, RedirectAttributes redirectAttributes) {
		model.addAttribute("referer","list.jhtml");
		if (shopGoodsFreight.getAreaId()==0){
			model.addAttribute("msg", "未选择地区");
			return Constants.MSG_URL;
		}
		ShopGoodsFreight newShopGoodsFreight = shopGoodsFreightService.find(shopGoodsFreight.getAreaId());
		if (newShopGoodsFreight!=null){
			addFlashMessage(redirectAttributes, ERROR_MESSAGE);
			model.addAttribute("msg", "地区不存在");
			return Constants.MSG_URL;
		}
		shopGoodsFreight.setId(twiterIdService.getTwiterId());
		ShopCommonArea shopCommonArea=areaService.find(shopGoodsFreight.getAreaId());
		List<ShopGoodsFreight> shopGoodsFreightServiceList= shopGoodsFreightService.findList("areaId",shopGoodsFreight.getAreaId());
		if (shopGoodsFreightServiceList!=null && shopGoodsFreightServiceList.size()>0){
			model.addAttribute("msg", "已经设置该地区的运费,不能重复设置");
			return Constants.MSG_URL;
		}
		if (shopGoodsFreight.getAreaId()==0L){
			shopGoodsFreight.setAreaName("默认");
		}else{
			shopGoodsFreight.setAreaName(shopCommonArea.getAreaName());
		}
		shopGoodsFreight.setCreateTime(new Date());
		shopGoodsFreight.setUpdateTime(new Date());
		shopGoodsFreightService.save(shopGoodsFreight);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 编辑
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Long id, ModelMap model) {
		Paramap paramap = Paramap.create().put("isDel", 0).put("areaParentId", 0);
		List<ShopCommonArea> areas = areaService.findList(paramap);
		ShopGoodsFreight shopGoodsFreight = shopGoodsFreightService.find(id);
		model.addAttribute("shopGoodsFreight", shopGoodsFreight);
		model.addAttribute("areas", areas);
		return "/trade/shop_goods_freight/edit";
	}
	
	
	/**
	 * 详情
	 */
	@RequiresPermissions("admin:freight:main")
	@RequestMapping(value = "/view", method = RequestMethod.GET)
	public String view(Long id, ModelMap model) {
		ShopGoodsFreight shopGoodsFreight = shopGoodsFreightService.find(id);
		model.addAttribute("shopGoodsFreight", shopGoodsFreight);
		return "/trade/shop_goods_freight/view";
	}
	

	/**
	 * 更新
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(ShopGoodsFreight shopGoodsFreight, RedirectAttributes redirectAttributes) {
		shopGoodsFreight.setUpdateTime(new Date());
		shopGoodsFreightService.update(shopGoodsFreight);
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
	@RequestMapping("/list")
	public String list(Pageable pageable, ModelMap model,ShopGoodsFreight shopGoodsFreight) {
		pageable.setParameter(shopGoodsFreight);
		model.addAttribute("areaNameareaNameLike", shopGoodsFreight.getAreaNameLike());
		model.addAttribute("page", this.shopGoodsFreightService.findByPage(pageable));
		return "/trade/shop_goods_freight/list";
	}

	/**
	 * 删除操作
	 * 
	 * @param ids
	 * @return
	 */
	@RequestMapping(value = { "/delete" }, method = { RequestMethod.POST })
	public @ResponseBody Message delete(Long[] ids) {
		this.shopGoodsFreightService.deleteAll(ids);
		return SUCCESS_MESSAGE;
	}
}
