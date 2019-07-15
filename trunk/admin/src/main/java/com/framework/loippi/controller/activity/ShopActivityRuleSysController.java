package com.framework.loippi.controller.activity;

import com.framework.loippi.consts.ActivityStatus;
import com.framework.loippi.consts.Constants;
import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.activity.ShopActivity;
import com.framework.loippi.entity.activity.ShopActivityPromotionRule;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.activity.ShopActivityPromotionRuleService;
import com.framework.loippi.service.activity.ShopActivityService;
import com.framework.loippi.support.Message;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller - 规则表  促销活动(专场)
 *
 * @author zijing
 * @version 2.0
 */
@Controller("adminShopActivityPromotionRuleController")
@RequestMapping({"/admin/shop_activity_rule"})
public class ShopActivityRuleSysController extends GenericController {

    @Resource
    private ShopActivityPromotionRuleService shopActivityPromotionRuleService;
    @Resource
    private ShopActivityService shopActivityService;
    @Resource
    private TwiterIdService twiterIdService;

    @Value("#{properties['admin.server']}")
    private String service;

    /**
     * 保存
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(ShopActivityPromotionRule shopActivityPromotionRule, RedirectAttributes redirectAttributes
        , ModelMap model, HttpServletRequest request, String activityType) {
        model.addAttribute("referer", request.getHeader("Referer"));
        if (!StringUtil.isEmpty(shopActivityPromotionRule.getLimitWhere())) {
            String wStr = shopActivityPromotionRule.getLimitWhere();
            shopActivityPromotionRule.setLimitWhere(wStr.replace(",", ""));
        }
        if (!StringUtil.isEmpty(shopActivityPromotionRule.getCouponSource())) {
            String wStr = shopActivityPromotionRule.getCouponSource();
            shopActivityPromotionRule.setCouponSource(wStr.replace(",", ""));
        }
        try {
            shopActivityPromotionRule.setLimitType(1);
            if (shopActivityPromotionRule.getId() == null) {
                shopActivityPromotionRule.setId(twiterIdService.getTwiterId());
                shopActivityPromotionRuleService.saveShopActivityPromotionRule(shopActivityPromotionRule);
            } else {
                shopActivityPromotionRuleService.updateShopActivityPromotionRule(shopActivityPromotionRule);
            }
            addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            addFlashMessage(redirectAttributes, ERROR_MESSAGE);
        }
        return "redirect:/admin/shop_activity_rule/list.jhtml?activityType=zhuanchang";
    }

    /**
     * 编辑
     */
    @RequestMapping(value = "/edit")
    public String edit(Long id, ModelMap model, String activityType) {
        ShopActivityPromotionRule shopActivityPromotionRule = null;
        if (id != null) {
            shopActivityPromotionRule = shopActivityPromotionRuleService.findById(id);
        } else {
            shopActivityPromotionRule = new ShopActivityPromotionRule();
        }
        model.addAttribute("shopActivityPromotionRule", shopActivityPromotionRule);
        model.addAttribute("activityType", activityType);
        return "/activity/shop_activity_rule/rule_edit";
    }

    /**
     * 更新
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String update(ShopActivityPromotionRule shopActivityPromotionRule,
        RedirectAttributes redirectAttributes, ModelMap model) {
        model.addAttribute("referer", "/admin/shop_activity_rule/rule_list.jhtml");
        if (shopActivityPromotionRule == null || shopActivityPromotionRule.getId() == null) {
            return Constants.MSG_URL;
        } else {

            try {
                if (!StringUtil.isEmpty(shopActivityPromotionRule.getLimitWhere())) {
                    String wStr = shopActivityPromotionRule.getLimitWhere();
                    shopActivityPromotionRule.setLimitWhere(wStr.replace(",", ""));
                }
                if (!StringUtil.isEmpty(shopActivityPromotionRule.getCouponSource())) {
                    String wStr = shopActivityPromotionRule.getCouponSource();
                    shopActivityPromotionRule.setCouponSource(wStr.replace(",", ""));
                }
                shopActivityPromotionRule.setLimitType(1);
                shopActivityPromotionRuleService.updateShopActivityPromotionRule(shopActivityPromotionRule);
                addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                addFlashMessage(redirectAttributes, ERROR_MESSAGE);
            }
        }
        return Constants.MSG_URL;
    }

    /**
     * 列表查询
     */
    @RequestMapping(value = {"/list"})
    public String list(@RequestParam(required = false, value = "pageNo", defaultValue = "1") int pageNo,
        ModelMap model, HttpServletRequest request,
        String activityType, String ruleTitle, Long id) {
        Map<String, Object> paramter = new HashMap<>();
        paramter.put("keyWord", ruleTitle);
        paramter.put("keyWordId", id);
        paramter.put("isActivityIdNull", 1);
        Pageable pager = new Pageable();
        pager.setPageNumber(pageNo);
        pager.setParameter(paramter);
        model.addAttribute("page", this.shopActivityPromotionRuleService.findShopActivityPromotionRulePagerList(pager));
        model.addAttribute("paramter", paramter);
        model.addAttribute("activityType", activityType);
        model.addAttribute("ruleTitle", ruleTitle);
        model.addAttribute("id", id);
        return "/activity/shop_activity_rule/rule_list";
    }

    /**
     * 删除操作
     */
    @RequestMapping(value = {"/delete"}, method = {RequestMethod.POST})
    public
    @ResponseBody
    Message delete(Long[] ids) {
        List<ShopActivity> shopActivityList = shopActivityService
            .findList(Paramap.create().put("promotionRuleIds", ids));
        if (shopActivityList != null && shopActivityList.size() > 0) {
            return ERROR_MESSAGE;
        }
        if (ids != null) {
            for (Long id : ids) {
                this.shopActivityPromotionRuleService.deleteShopActivityPromotionRuleById(id);
            }
        }
        return SUCCESS_MESSAGE;
    }

}
