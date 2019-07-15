//package com.framework.loippi.controller.member;
//
//import javax.annotation.Resource;
//
//import com.framework.loippi.controller.GenericController;
//import com.framework.loippi.entity.order.ShopOrderDiscountType;
//import com.framework.loippi.entity.user.ShopMemberGrade;
//import com.framework.loippi.service.RedisService;
//import com.framework.loippi.service.TwiterIdService;
//import com.framework.loippi.service.order.ShopOrderDiscountTypeService;
//import com.framework.loippi.service.user.ShopMemberGradeService;
//import org.apache.shiro.authz.annotation.RequiresPermissions;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.ModelMap;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import com.framework.loippi.support.Message;
//import com.framework.loippi.support.Pageable;
//
//import java.util.Date;
//import java.util.List;
//
///**
// * Controller - 会员等级管理
// *
// * @author dzm
// * @version 2.0
// */
//@Controller("adminShopMemberGradeController")
//@RequestMapping({ "/admin/shop_member_grade" })
//public class ShopMemberGradeController extends GenericController {
//
//	@Resource
//	private ShopMemberGradeService shopMemberGradeService;
//	@Resource
//	private ShopOrderDiscountTypeService shopOrderDiscountTypeService;
//	@Autowired
//	private TwiterIdService twiterIdService;
//	@Resource
//	private RedisService redisService;
//	/**
//	 * 跳转添加页面
//	 *
//	 * @param
//	 * @param model
//	 * @return
//	 */
//	@RequestMapping(value = { "/add" }, method = { RequestMethod.GET })
//	public String add(ModelMap model) {
//		List<ShopOrderDiscountType> shopOrderDiscountTypeList=shopOrderDiscountTypeService.findAll();
//		model.addAttribute("shopOrderDiscountTypeList", shopOrderDiscountTypeList);
//		return "/member/shop_member_grade/add";
//	}
//
//	/**
//	 * 保存
//	 */
//	@RequestMapping(value = "/save", method = RequestMethod.POST)
//	public String save(ShopMemberGrade shopMemberGrade, RedirectAttributes redirectAttributes) {
//		shopMemberGrade.setId(twiterIdService.getTwiterId());
//		shopMemberGrade.setCreateTime(new Date());
//		shopMemberGrade.setUpdateTime(new Date());
//		shopMemberGrade.setNumber(0);
//		shopMemberGradeService.save(shopMemberGrade);
//		redisService.delete("shopMemberGradeList");
//		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
//		return "redirect:list.jhtml";
//	}
//
//	/**
//	 * 编辑
//	 */
//	@RequestMapping(value = "/edit", method = RequestMethod.GET)
//	public String edit(Long id, ModelMap model) {
//		List<ShopOrderDiscountType> shopOrderDiscountTypeList=shopOrderDiscountTypeService.findAll();
//		model.addAttribute("shopOrderDiscountTypeList", shopOrderDiscountTypeList);
//		ShopMemberGrade shopMemberGrade = shopMemberGradeService.find(id);
//		model.addAttribute("shopMemberGrade", shopMemberGrade);
//		return "/member/shop_member_grade/edit";
//	}
//
//
//	/**
//	 * 详情
//	 */
//	@RequestMapping(value = "/view", method = RequestMethod.GET)
//	public String view(Long id, ModelMap model) {
//		ShopMemberGrade shopMemberGrade = shopMemberGradeService.find(id);
//		model.addAttribute("shopMemberGrade", shopMemberGrade);
//		return "/member/shop_member_grade/view";
//	}
//
//
//	/**
//	 * 更新
//	 */
//	@RequestMapping(value = "/update", method = RequestMethod.POST)
//	public String update(ShopMemberGrade shopMemberGrade, RedirectAttributes redirectAttributes) {
//		shopMemberGrade.setUpdateTime(new Date());
//		shopMemberGradeService.update(shopMemberGrade);
//		redisService.delete("shopMemberGradeList");
//		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
//		return "redirect:list.jhtml";
//	}
//
//	/**
//	 * 列表查询
//	 *
//	 * @param pageable
//	 * @param model
//	 * @return
//	 */
//	@RequiresPermissions("admin:member:grade")
//	@RequestMapping(value = { "/list" }, method = { RequestMethod.GET })
//	public String list(Pageable pageable, ModelMap model) {
//		model.addAttribute("page", this.shopMemberGradeService.findByPage(pageable));
//		return "/member/shop_member_grade/list";
//	}
//
//	/**
//	 * 删除操作
//	 *
//	 * @param ids
//	 * @return
//	 */
//	@RequestMapping(value = { "/delete" }, method = { RequestMethod.POST })
//	public @ResponseBody Message delete(Long[] ids) {
//		this.shopMemberGradeService.deleteAll(ids);
//		redisService.delete("shopMemberGradeList");
//		return SUCCESS_MESSAGE;
//	}
//}
