package com.framework.loippi.controller.activity;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.framework.loippi.consts.Constants;
import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.activity.ShopActivity;
import com.framework.loippi.entity.activity.ShopActivityPromotionRule;
import com.framework.loippi.enus.ActivityTypeEnus;
import com.framework.loippi.enus.ShopGroupTypeEnus;
import com.framework.loippi.mybatis.paginator.domain.Order.Direction;
import com.framework.loippi.service.activity.ShopActivityPromotionRuleService;
import com.framework.loippi.service.activity.ShopActivityService;
import com.framework.loippi.service.huanxin.HxService;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.support.Message;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.StringUtil;
import com.framework.loippi.utils.validator.DateUtils;

/**
 * 商品活动主表
 */
@Slf4j
@Controller("shopActivityController")
@RequestMapping("/admin/plarformShopActivity")
public class ShopActivityController extends GenericController {

    @Resource
    private ShopActivityService shopActivityService;
    @Resource
    private ShopActivityPromotionRuleService shopActivityPromotionRuleService;
    @Resource
    private RdMmBasicInfoService rdMmBasicInfoService;
    @Resource
    private HxService hxService;
    @Value("#{properties['admin.server']}")
    private String service;

    /**
     * 促销活动基本信息列表
     *
     * @param shopActivity 查询对象
     * @param pageNo 分页初始码
     * @param activityType 活动类型
     * @param startTimeStr 活动开始时间
     * @param endTimeStr 活动结束时间
     */
    @RequestMapping("/activity/{type}/list")
    @RequiresPermissions("sys:activity:view")
    public String list(ModelMap model,
        @RequestParam(required = false, value = "pageNo", defaultValue = "1") int pageNo,
        @ModelAttribute ShopActivity shopActivity,
        @PathVariable(value = "type") String activityType,
        @RequestParam(required = false, value = "endTimeStr") String endTimeStr,
        @RequestParam(required = false, value = "startTimeStr") String startTimeStr) {
        //参数整理
        Pageable pager = new Pageable();
        pager.setPageNumber(pageNo);
        pager.setOrderProperty("sort");
        pager.setOrderDirection(Direction.ASC);
        if (!StringUtil.isEmpty(startTimeStr)) {
            shopActivity.setSearchStartTime(startTimeStr);
        }
        if (!StringUtil.isEmpty(endTimeStr)) {
            shopActivity.setSearchEndTime(endTimeStr);
        }
        //活动类型
        if (StringUtils.isNotEmpty(activityType)) {
            shopActivity.setActivityType(ActivityTypeEnus.EnumType.getValue(activityType));
        }
        pager.setParameter(shopActivity);
        String url = "";
        //专场促销
        Page<ShopActivity> activityList = shopActivityService.findByPage(pager);
        //校验并处理已结束活动
        Map<String, Object> map = new HashMap<String, Object>();
        List<Long> afterIdsList = new ArrayList<Long>();
        List<Long> beforeIdsList = new ArrayList<Long>();
        for (ShopActivity item : activityList.getContent()) {
            if (item.getStartTime().getTime() > new Date().getTime()) {
                beforeIdsList.add(item.getId());
                item.setActivityStatus(10);
            } else {
                if (item.getEndTime().getTime() < new Date().getTime()) {
                    afterIdsList.add(item.getId());
                    item.setActivityStatus(30);
                }
            }
        }
        if (beforeIdsList.size() > 0) {
            //活动未开始
            map.clear();
            map.put("ids", beforeIdsList);
            map.put("activityStatus", 10);
            shopActivityService.updateActivityBatch(map);
        }
        if (afterIdsList.size() > 0) {
            //活动已结束
            map.clear();
            map.put("ids", afterIdsList);
            map.put("activityStatus", 30);
            shopActivityService.updateActivityBatch(map);
        }
        model.addAttribute("page", activityList);
        //专场活动，跳转url
        url = "/activity/shop_activity/activity_list";

        model.addAttribute("activityType", activityType);
        model.addAttribute("shopActivity", shopActivity);
        model.addAttribute("activityTitle", ActivityTypeEnus.activitMap.get(activityType));
        model.addAttribute("toUrl", service + "admin/plarformShopActivity/activity/" + activityType + "list.jhtml");
        return url;
    }

    /**
     * 秒杀、团购活动、促销活动
     *
     * @param id 修改对象的id
     * @param activityType 活动类型
     */
    @RequestMapping("/forward/{type}/edit")
    @RequiresPermissions("sys:activity:edit")
    public String edit(Model model, @RequestParam(required = false, value = "id") Long id,
        @PathVariable(value = "type") String activityType, HttpServletRequest request) {
        model.addAttribute("referer", request.getHeader("Referer"));
        ShopActivity shopActivity = null;
        if (id != null) {
            shopActivity = shopActivityService.find(id);
        } else {
            shopActivity = new ShopActivity();
            shopActivity.setStartTime(new Date());
            shopActivity.setEndTime(new Date());
            shopActivity.setActivityType(ActivityTypeEnus.activitTypeMap.get(activityType));
        }
        String url;
        //专场
        Map<String, Object> qyMap = new HashMap<>();
        qyMap.put("type", ShopGroupTypeEnus.EnumType.chuxiaogroup.getValue());
        //促销规则
        ShopActivityPromotionRule shopActivityPromotionRule = new ShopActivityPromotionRule();
        shopActivityPromotionRule.setIsActivityIdNull(1l);
        List<ShopActivityPromotionRule> ruleList = shopActivityPromotionRuleService
            .findShopActivityPromotionRuleList(shopActivityPromotionRule);
        model.addAttribute("ruleList", ruleList);
        url = "/activity/shop_activity/activity_edit";
        model.addAttribute("activityType", activityType);
        model.addAttribute("shopActivity", shopActivity);
        model.addAttribute("edit", 1);
        return url;
    }


    /**
     * 秒杀、团购活动、促销活动
     *
     * @param id 修改对象的id
     * @param activityType 活动类型
     */
    @RequestMapping("/forward/{type}/view")
    @RequiresPermissions("sys:activity:edit")
    public String view(Model model, @RequestParam(required = false, value = "id") Long id,
        @PathVariable(value = "type") String activityType) {
        ShopActivity shopActivity = shopActivityService.find(id);
        ShopActivityPromotionRule shopActivityPromotionRule = new ShopActivityPromotionRule();
        shopActivityPromotionRule.setIsActivityIdNull(1l);
        List<ShopActivityPromotionRule> ruleList = shopActivityPromotionRuleService
            .findShopActivityPromotionRuleList(shopActivityPromotionRule);
        model.addAttribute("ruleList", ruleList);
        model.addAttribute("shopActivity", shopActivity);
        model.addAttribute("activityType", activityType);
        return "/activity/shop_activity/activity_edit";
    }


    /**
     * 促销活动基本信息保存
     *
     * @param shopActivity 活动对象
     * @param activityId 活动对象主键
     * @param activityType 活动类型
     * @param endTimeStr 活动结束时间
     * @param startTimeStr 活动开始时间
     * @return String  跳转路径
     */
    @RequestMapping("/saveActivityBaseInfo/{type}/save")
    @RequiresPermissions("sys:activity:edit")
    public String saveActivityBaseInfo(
        ModelMap model, RedirectAttributes attr,
        @ModelAttribute ShopActivity shopActivity,
        @RequestParam(required = false, value = "activityId") Long activityId,
        @PathVariable(value = "type") String activityType,
        @RequestParam(required = false, value = "ruleType") Integer ruleType,
        @RequestParam(required = false, value = "endTimeStr") String endTimeStr,
        @RequestParam(required = false, value = "startTimeStr") String startTimeStr,
        HttpServletRequest request) {
        model.addAttribute("referer",
            request.getContextPath() + "/admin/plarformShopActivity/activity/" + activityType + "/list.jhtml");
        //验证时间
        if (!StringUtil.isEmpty(startTimeStr) && !StringUtil.isEmpty(endTimeStr)) {
            shopActivity.setStartTime(DateUtils.toDate(startTimeStr, "yyyy-MM-dd HH:mm:ss"));
            shopActivity.setEndTime(DateUtils.toDate(endTimeStr, "yyyy-MM-dd HH:mm:ss"));
        } else {
            model.addAttribute("msg", "活动时间不能为空！");
            return Constants.MSG_URL;
        }
        if (shopActivity.getEndTime().before(shopActivity.getStartTime())) {
            model.addAttribute("msg", "活动的结束时间大于活动开始时间！");
            return Constants.MSG_URL;
        }
        shopActivity.setId(activityId);
        shopActivity.setActivityType(ActivityTypeEnus.activitTypeMap.get(activityType));
        //填充优惠规则
        shopActivity.setPromotionType(ruleType);
        //保存活动
        Map<String, String> map = shopActivityService.handleSaveActivity(shopActivity, activityType,
            Constants.PLATFORM_STORE_ID, "平台自营", true);

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

        attr.addAttribute("activityId", shopActivity.getId());
        if (activityId!=null){
            model.addAttribute("msg", "编辑成功！");
        }else{
            model.addAttribute("msg", "添加成功！");
        }

        return Constants.MSG_URL;

    }

    /**
     * 促销优惠券活动基本信息保存
     *
     * @param shopActivity 活动对象
     * @param activityId 活动对象主键
     * @param endTimeStr 活动结束时间
     * @param startTimeStr 活动开始时间
     * @return String  跳转路径
     */
    /*@RequestMapping("/saveCouponActivityBaseInfo/save")
    @RequiresPermissions("sys:activity:edit")
    public String saveCouponActivityBaseInfo(
        ModelMap model, RedirectAttributes attr,
        @ModelAttribute ShopActivity shopActivity,
        @RequestParam(required = false, value = "activityId") Long activityId,
        //@RequestParam(required = false, value = "ruleType") Integer ruleType,
        @RequestParam(required = false, value = "endTimeStr") String endTimeStr,
        @RequestParam(required = false, value = "startTimeStr") String startTimeStr,
        HttpServletRequest request) {
        model.addAttribute("referer",
            request.getContextPath() + "/admin/plarformShopActivity/activity/" + "zhuanchang" + "/list.jhtml");
        //验证时间
        if (!StringUtil.isEmpty(startTimeStr) && !StringUtil.isEmpty(endTimeStr)) {
            shopActivity.setStartTime(DateUtils.toDate(startTimeStr, "yyyy-MM-dd HH:mm:ss"));
            shopActivity.setEndTime(DateUtils.toDate(endTimeStr, "yyyy-MM-dd HH:mm:ss"));
        } else {
            model.addAttribute("msg", "活动时间不能为空！");
            return Constants.MSG_URL;
        }
        if (shopActivity.getEndTime().before(shopActivity.getStartTime())) {
            model.addAttribute("msg", "活动的结束时间大于活动开始时间！");
            return Constants.MSG_URL;
        }
        shopActivity.setId(activityId);
        shopActivity.setActivityType(ActivityTypeEnus.activitTypeMap.get("coupon"));
        //填充优惠规则
        //shopActivity.setPromotionType(ruleType);
        //保存活动
        Map<String, String> map = shopActivityService.handleSaveCouponActivity(shopActivity, Constants.PLATFORM_STORE_ID, "平台自营", true);

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

        attr.addAttribute("activityId", shopActivity.getId());
        if (activityId!=null){
            model.addAttribute("msg", "编辑成功！");
        }else{
            model.addAttribute("msg", "添加成功！");
        }

        return Constants.MSG_URL;

    }*/

    /**
     * 促销活动基本信息删除
     *
     * @param ids 活动记录的id
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:activity:edit")
    @ResponseBody
    public Message delete(Model model, @RequestParam String[] ids) {
        for (String id : ids) {
            if (StringUtil.isEmpty(id)) {
                continue;
            }
            Long idNum = Long.parseLong(id);
            //删除活动信息，同时需要删除活动规则信息 /已经开始的活动
            ShopActivity item = shopActivityService.find(idNum);
            //活动状态  10  未开始  20 活动中 30已结束
            if (item == null || item.getActivityStatus() == null) {
                return Message.error("活动无效,无法删除！");
            }
            if (item.getActivityStatus().intValue() == 20) {
                return Message.error("活动正在进行中,无法删除！");
            }
            //删除活动信息，同时需要删除活动规则信息 /已经开始的活动
            shopActivityService.deleteShopActivityById(idNum);
        }
        return SUCCESS_MESSAGE;
    }

    /**
     * 用于手工输入活动ID时校验是否正确
     */
    @RequestMapping("/ajaxActivityIsExit")
    public
    @ResponseBody
    boolean ajaxActivityIsExit(Long activityId) {
        boolean isActivityCoupn = shopActivityService.count(Paramap.create().put("id", activityId)) > 0;
        return isActivityCoupn;
    }


    /**
     * 批量更新活动状态
     */
    @RequestMapping("/updateActitivtyStatus")
    @RequiresPermissions("sys:activity:edit")
    @ResponseBody
    public Message updateActitivtyStatus(Model model, @RequestParam String[] ids, @RequestParam Integer activityStatus,
        HttpServletRequest request) {
        List<Long> idsList = new ArrayList<Long>();
        for (String id : ids) {
            if (!StringUtil.isEmpty(id)) {
                idsList.add(Long.parseLong(id));
            }
        }
        if (idsList.size() > 0) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("ids", idsList);
            map.put("auditStatus", activityStatus);
            shopActivityService.updateActivityBatch(map);
        }
        return SUCCESS_MESSAGE;
    }

    /**
     * 活动结束
     */
    @RequestMapping("/finishActivity")
    @RequiresPermissions("sys:activity:edit")
    @ResponseBody
    public Message finishActivity(Model model, @RequestParam String[] ids, HttpServletRequest request) {
        for (String id : ids) {
            if (StringUtil.isEmpty(id)) {
                continue;
            }
            shopActivityService.stopShopActivityById(Long.parseLong(id));
        }
        return SUCCESS_MESSAGE;
    }

}
