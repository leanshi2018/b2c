package com.framework.loippi.controller.gift;

import java.util.Calendar;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.framework.loippi.consts.Constants;
import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.Principal;
import com.framework.loippi.entity.gift.ShopGiftActivity;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.service.gift.ShopGiftActivityService;
import com.framework.loippi.service.gift.ShopGiftGoodsService;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.DateConverter;
import com.framework.loippi.utils.StringUtil;

@Controller("ShopGiftController")
@RequestMapping("/admin/gift")
public class ShopGiftController extends GenericController {

	@Resource
	private ShopGiftActivityService shopGiftActivityService;
	@Resource
	private ShopGiftGoodsService shopGiftGoodsService;


	/**
	 * 赠品活动列表
	 * @param request
	 * @param pageable
	 * @param model
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/findGiftList")
	public String findGiftList(HttpServletRequest request, Pageable pageable, ModelMap model, @ModelAttribute ShopGiftActivity param) {
		pageable.setParameter(param);
		pageable.setOrderProperty("creation_time");
		pageable.setOrderDirection(Order.Direction.DESC);
		model.addAttribute("page", shopGiftActivityService.findByPage(pageable));
		return "/common/buyFree/index";
	}

	/**
	 * 添加或编辑赠品活动
	 * @param request
	 * @param shopGiftActivity
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/saveOrEditGiftActivity",method = RequestMethod.POST)
	public String saveOrEditGiftActivity(HttpServletRequest request, @ModelAttribute ShopGiftActivity shopGiftActivity, ModelMap model ){

		if(StringUtil.isEmpty(shopGiftActivity.getActivityName())){
			model.addAttribute("msg", "活动名称不可以为空");
			return Constants.MSG_URL;
		}
		if(StringUtil.isEmpty(shopGiftActivity.getStartTimeS())||StringUtil.isEmpty(shopGiftActivity.getEndTimeS())){
			model.addAttribute("msg", "请选择活动开始时间或者结束时间");
			return Constants.MSG_URL;
		}
		if(shopGiftActivity.getPpv1()==null||shopGiftActivity.getSpecIdList1().size()==0){
			model.addAttribute("msg", "活动规则1不可以为空");
			return Constants.MSG_URL;
		}
		if (shopGiftActivity.getPpv1()!=null&&shopGiftActivity.getPpv2()!=null&&shopGiftActivity.getPpv1().compareTo(shopGiftActivity.getPpv2())!=-1){
			model.addAttribute("msg", "活动规则1不可以大于等于活动规则2mi值");
			return Constants.MSG_URL;
		}
		if(shopGiftActivity.getPpv2()!=null&&shopGiftActivity.getSpecIdList2().size()==0){
			model.addAttribute("msg", "活动规则2商品不可以为空");
			return Constants.MSG_URL;
		}
		if (shopGiftActivity.getPpv2()!=null&&shopGiftActivity.getPpv3()!=null&&shopGiftActivity.getPpv2().compareTo(shopGiftActivity.getPpv3())!=-1){
			model.addAttribute("msg", "活动规则2不可以大于等于活动规则3mi值");
			return Constants.MSG_URL;
		}
		if(shopGiftActivity.getPpv3()!=null&&shopGiftActivity.getSpecIdList3().size()==0){
			model.addAttribute("msg", "活动规则3商品不可以为空");
			return Constants.MSG_URL;
		}

		System.out.println("all="+shopGiftActivity);

		Subject subject = SecurityUtils.getSubject();
		if(subject!=null){
			Principal principal = (Principal) subject.getPrincipal();
			if (principal != null && principal.getId() != null) {
				Long id = principal.getId();
				String username = principal.getUsername();

				Calendar calendar = Calendar.getInstance();
				DateConverter converter = new DateConverter();
				if (!"".equals(shopGiftActivity.getStartTimeS())){
					calendar.setTime(converter.convert(shopGiftActivity.getStartTimeS()));
					calendar.set(Calendar.HOUR_OF_DAY,23);
					calendar.set(Calendar.MINUTE,59);
					calendar.set(Calendar.SECOND,59);
					calendar.set(Calendar.MILLISECOND,0);
					shopGiftActivity.setStartTime(calendar.getTime());
				}
				if (!"".equals(shopGiftActivity.getEndTimeS())){
					calendar.setTime(converter.convert(shopGiftActivity.getEndTimeS()));
					calendar.set(Calendar.HOUR_OF_DAY,23);
					calendar.set(Calendar.MINUTE,59);
					calendar.set(Calendar.SECOND,59);
					calendar.set(Calendar.MILLISECOND,0);
					shopGiftActivity.setEndTime(calendar.getTime());
				}
				Map<String, String> map =shopGiftActivityService.saveOrEditGift(shopGiftActivity,id,username);
				if (map == null || StringUtil.isEmpty(map.get("code"))) {
					model.addAttribute("msg", "保存活动信息失败！");
					return Constants.MSG_URL;
				}

				String code = map.get("code");
				if (StringUtil.isEmpty(code) || code.equals("0")) {
					String errorMsg = map.get("msg");
					model.addAttribute("msg", errorMsg);
					return Constants.MSG_URL;
				}
				return "redirect:findGiftList.jhtml";
			}
		}
		model.addAttribute("msg", "请登录后再进行赠品活动相关操作");
		return Constants.MSG_URL;
	}

	/**
	 * 赠品活动详情
	 * @param request
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/findGiftInfo")
	public String findGiftInfo(HttpServletRequest request, ModelMap model, @RequestParam(required = false, value = "id") Long id) {

		if (id==null){
			return "/common/buyFree/edit";
		}
		model.addAttribute("page",shopGiftActivityService.findById(id));
		return "/common/buyFree/edit";
	}

	/**
	 * 上下架
	 * @param request
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/updateGiftEState")
	public String updateGiftEState(HttpServletRequest request, ModelMap model, @RequestParam(required = false, value = "id") Long id) {

		if (id==null){
			model.addAttribute("msg", "未找到该活动");
			return Constants.MSG_URL;
		}

		ShopGiftActivity activity = shopGiftActivityService.find(id);
		if (activity==null){
			model.addAttribute("msg", "未找到该活动");
			return Constants.MSG_URL;
		}

		if (activity.getEState()==0){//原状态：上
			activity.setEState(1);//下
			shopGiftActivityService.update(activity);
		}else {//原状态：下
			//其他活动下
			shopGiftActivityService.updateByEState(1);

			activity.setEState(0);//上
			shopGiftActivityService.update(activity);
		}

		return "redirect:findGiftList.jhtml";
	}

}
