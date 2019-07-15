package com.framework.loippi.controller.activity;

import com.framework.loippi.consts.GoodsState;
import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.activity.ShopActivity;
import com.framework.loippi.entity.activity.ShopActivityGoods;
import com.framework.loippi.entity.activity.ShopActivityGoodsSpec;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsClass;
import com.framework.loippi.entity.product.ShopGoodsSpec;
import com.framework.loippi.enus.ActivityTypeEnus;
import com.framework.loippi.enus.ShopGroupTypeEnus;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.service.UserService;
import com.framework.loippi.service.activity.ShopActivityGoodsService;
import com.framework.loippi.service.activity.ShopActivityGoodsSpecService;
import com.framework.loippi.service.activity.ShopActivityService;
import com.framework.loippi.service.product.ShopGoodsClassService;
import com.framework.loippi.service.product.ShopGoodsService;
import com.framework.loippi.service.product.ShopGoodsSpecService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.GoodsUtils;
import com.framework.loippi.utils.JacksonUtil;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.StringUtil;
import com.framework.loippi.vo.goods.GoodsSpecVo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 活动报名 Created by longbh on 2018/10/26.
 */
@Controller("adminActivitySignUpController")
@RequestMapping({"/admin/shop_activity_signup"})
public class ActivitySignUpController extends GenericController {

    @Resource
    private ShopActivityService shopActivityService;
    @Autowired
    private ShopActivityGoodsService shopActivityGoodsService;
    @Autowired
    private ShopActivityGoodsSpecService shopActivityGoodsSpecService;
    @Resource
    private UserService userService;
    @Resource
    private ShopGoodsService shopGoodsService;
    @Resource
    private ShopGoodsSpecService shopGoodsSpecService;
    @Resource
    private ShopGoodsClassService shopGoodsClassService;
    @Value("#{properties['admin.server']}")
    private String service;

    /**
     * 待审核列表
     */
    @RequestMapping("/activity/{type}/list")
    @RequiresPermissions("admin:activity:audit")
    public String list(ModelMap model, @RequestParam(required = false, value = "pageNo", defaultValue = "1") int pageNo,
        @ModelAttribute ShopActivity shopActivity,
        @PathVariable(value = "type") String activityType,
        @RequestParam(required = false, defaultValue = "0") Integer auditType,
        Long activityId,
        @RequestParam(required = false, value = "endTimeStr") String endTimeStr,
        @RequestParam(required = false, value = "startTimeStr") String startTimeStr) {
        Pageable pager = new Pageable();
        pager.setPageNumber(pageNo);
        //设置审核状态
        Map<String, Object> params = Paramap.create();
        if (!StringUtil.isEmpty(activityType)) {
            pager.setParameter(params.put("activityType", ActivityTypeEnus.activitTypeMap.get(activityType)));
        }
        if (auditType != 0) {
            params.put("status", auditType);
        }
        if (activityId != null) {
            params.put("activityId", activityId);
        }
        params.put("storeId", 0);

        Page<ShopActivityGoods> page = shopActivityGoodsService.findByPage(pager);
        List<Long> longList = new ArrayList<>();
        List<Long> goodList = new ArrayList<>();
        List<Long> goodIdList = new ArrayList<>();
        for (ShopActivityGoods shopActivityGoods : page.getContent()) {
            longList.add(shopActivityGoods.getActivityId());
            goodList.add(shopActivityGoods.getId());
            goodIdList.add(shopActivityGoods.getObjectId());
        }
        if (longList.size() != 0) {
            //填充商品信息
            Map<Long, ShopGoods> goodsMap = shopGoodsService.findGoodsMap(goodIdList);
            //填充活动信息
            Map<Long, ShopActivity> shopActivityMap = shopActivityService.findMapActivity(longList);
            //规格信息
            Map<Long, List<ShopActivityGoodsSpec>> longListMap = shopActivityGoodsSpecService.findMapSpecList(goodList);
            for (ShopActivityGoods shopActivityGoods : page.getContent()) {
                ShopActivity shopActivityItem = shopActivityMap.get(shopActivityGoods.getActivityId());
                if (shopActivityItem != null) {
                    shopActivityGoods.setActivityName(shopActivityItem.getName());
                    shopActivityGoods.setActivityStatus(shopActivityItem.getActivityStatus());
                    shopActivityGoods.setStartTime(shopActivityItem.getStartTime());
                    shopActivityGoods.setEndTime(shopActivityItem.getEndTime());
                }
                shopActivityGoods.setShopActivityGoodsSpecList(longListMap.get(shopActivityGoods.getId()));
                ShopGoods shopGoods = goodsMap.get(shopActivityGoods.getObjectId());
                if (shopGoods != null) {
                    shopActivityGoods.setGoodsName(shopGoods.getGoodsName());
                }
                //返回信息转换
                Map<String, Object> specNameMap = JacksonUtil.convertMap(shopGoods.getSpecName());
                List<String> keys = new ArrayList<>(specNameMap.keySet());
                shopActivityGoods.setSpecKeyList(keys);
                Map<String, List<GoodsSpecVo>> specMap = GoodsUtils.goodsSpecStrToMapList(shopGoods.getGoodsSpec());
                shopActivityGoods.setSpecNameMap(specNameMap);
                shopActivityGoods.setSpecMap(specMap);
            }
        }

        model.addAttribute("activityType", activityType);
        model.addAttribute("shopActivity", shopActivity);
        model.addAttribute("pager", page);
        model.addAttribute("activityTitle", ActivityTypeEnus.activitMap.get(activityType));
        model.addAttribute("toUrl", service + "admin/shop_activity_signup/activity/" + activityType + "list.jhtml");
        return "/activity/shop_activity_audio/activity_list";
    }

    /**
     * 选择商品
     */
    @RequestMapping("/activity/{type}/selectGoods")
    @RequiresPermissions("admin:activity:audit")
    public String selectGoods(ModelMap model,
        @RequestParam(defaultValue = "1") Integer pageNo,
        Long activityId, String goodsKeywords,
        @PathVariable String type,
        Integer goodsType, String goodSpecIds,
        @ModelAttribute ShopGoods goodsPlatform) {
        Pageable pager = new Pageable();
        pager.setPageNumber(pageNo);
        goodsPlatform.setNoGoodsType("2");
        goodsPlatform.setIsDel(GoodsState.GOODS_NOT_DELETE);
        goodsPlatform.setOrderAll(GoodsState.DEFAULT_ORDER);
        if (goodsType != null) {
            goodsPlatform.setGoodsType(goodsType);
        }

        //shopGoods.setState(GoodsState.GOODS_OPEN_STATE);
        goodsPlatform.setGoodsShow(GoodsState.GOODS_ON_SHOW);
        goodsPlatform.setNotIds(shopActivityGoodsService.getSpecIds(goodSpecIds));
        pager.setParameter(goodsPlatform);
        pager.setOrderProperty("stock");
        pager.setOrderDirection(Order.Direction.DESC);
        Page<ShopGoods> byPage = shopGoodsService.findByPage(pager);
        Map<String, String> treeMap = new HashMap<>();
        Map<String, String> classNameMap = new HashMap<>();
        List<ShopGoods> list = byPage.getContent();
        //商品分类查询及回显
        List<ShopGoodsClass> secondGoodsClasses = new ArrayList<>();
        List<ShopGoodsClass> firstGoodsClasses = new ArrayList<>();
        List<ShopGoodsClass> shopGoodsClassList = shopGoodsClassService.findAll();
        for (ShopGoodsClass item : shopGoodsClassList) {
            if (Optional.ofNullable(item.getDeep()).orElse(0) == 2) {
                secondGoodsClasses.add(item);
                treeMap.put(item.getId() + "", item.getGcParentId() + "");
            } else {
                firstGoodsClasses.add(item);
                classNameMap.put(item.getId() + "", item.getGcName());
            }
        }
        for (ShopGoods item : list) {
            String tree = treeMap.get(item.getGcId() + "");
            if (tree != null && !"".equals(tree)) {
                item.setGcParentName(Optional.ofNullable(classNameMap.get(tree)).orElse(""));
            }
        }
        model.addAttribute("secondGoodsClasses", secondGoodsClasses);
        model.addAttribute("firstGoodsClasses", firstGoodsClasses);
        model.addAttribute("pager", byPage);
        model.addAttribute("activityId", activityId);
        model.addAttribute("type", type);//活动类型
        model.addAttribute("paramter", goodsPlatform);
        model.addAttribute("goodsGcId", goodsPlatform.getGcId());
        model.addAttribute("gcParentId", goodsPlatform.getGcParentId());
        return "/activity/shop_activity_audio/activity_goods_select";
    }

    /**
     * 选择规格
     */
    @RequestMapping("/activity/{type}/selectSpec")
    @RequiresPermissions("admin:activity:audit")
    public String selectSpec(ModelMap model, Long activityId, Long goodsId, Long id, @PathVariable String type) {
        List<ShopGoodsSpec> shopGoodsSpecs = shopGoodsSpecService.findListByGoodsId(goodsId);
        for (ShopGoodsSpec shopGoodsSpec : shopGoodsSpecs) {
            shopGoodsSpec.setSpecValueMap(JacksonUtil.convertStrMap(shopGoodsSpec.getSpecGoodsSpec()));
        }
        ShopGoods goods = shopGoodsService.find(goodsId);
        ShopActivity shopActivity = shopActivityService.find(activityId);
        model.addAttribute("shopGoodsSpecs", shopGoodsSpecs);
        model.addAttribute("goods", goods);
        model.addAttribute("activityId", activityId);
        model.addAttribute("activity", shopActivity);
        model.addAttribute("type", type);
        //获取商品的规格
        if (goods.getGoodsSpec() != null && !goods.getGoodsSpec().trim().equals("")) {
            Map<String, Object> specNameMap = JacksonUtil.convertMap(goods.getSpecName());
            List<String> keys = new ArrayList<>(specNameMap.keySet());
            model.addAttribute("specNameList", keys);
            Map<String, List<GoodsSpecVo>> specMap = GoodsUtils.goodsSpecStrToMapList(goods.getGoodsSpec());
            model.addAttribute("specNameMap", specNameMap);
            model.addAttribute("specMap", specMap);
        }
        //获取对应的规格值
        if (id != null) {
            List<ShopActivityGoodsSpec> shopActivityGoodsSpecs = shopActivityGoodsSpecService
                .findList("activityGoodsId", id);
            //转map
            Map<Long, ShopActivityGoodsSpec> shopActivityGoodsSpecMap = new HashMap<>();
            for (ShopActivityGoodsSpec item : shopActivityGoodsSpecs) {
                shopActivityGoodsSpecMap.put(item.getSpecId(), item);
            }
            model.addAttribute("activitySpecMap", shopActivityGoodsSpecMap);
            model.addAttribute("activityGoods", shopActivityGoodsService.find(id));
        }
        model.addAttribute("id", id);
        model.addAttribute("edit", 1);
        return "/activity/shop_activity_audio/activity_spec_select";
    }

    /**
     * 参加活动
     */
    @RequestMapping("/joinActivity")
    @RequiresPermissions("admin:activity:audit")
    public String joinActivity(HttpServletRequest request, ModelMap model, Long screeningsId,
        Long activityId, Long goodsId, String activityPicture,
        Long id, String specJson, Integer maxOrderBuy,
        Integer maxUserBuy) {
        ShopActivity shopActivity = shopActivityService.find(activityId);
        shopActivityGoodsService
            .saveGoodSpec(0l, shopActivity, goodsId, id, specJson, maxOrderBuy, maxUserBuy, activityPicture,
                screeningsId);
        model.addAttribute("referer",
            request.getContextPath() + "/seller/shop_activity_signup/activity/" + ActivityTypeEnus.activitTypeEnumMap
                .get(shopActivity.getActivityType()) + "/list.jhtml");
//        return request.getContextPath() + "/admin/plarformShopActivity/activity/" + ActivityTypeEnus.activitTypeEnumMap.get(shopActivity.getActivityType()) + "/list.jhtml";
        return "redirect:/admin/plarformShopActivity/activity/zhuanchang/list.jhtml";
    }

    /**
     * 编辑
     */
    @RequestMapping("/joinDetail")
    @RequiresPermissions("admin:activity:audit")
    public String joinDetail(ModelMap model, Long id) {
        ShopActivityGoods ShopActivityGoods = shopActivityGoodsService.find(id);
        List<ShopGoodsSpec> shopGoodsSpecs = shopGoodsSpecService.findListByGoodsId(ShopActivityGoods.getObjectId());
        for (ShopGoodsSpec shopGoodsSpec : shopGoodsSpecs) {
            shopGoodsSpec.setSpecValueMap(JacksonUtil.convertStrMap(shopGoodsSpec.getSpecGoodsSpec()));
        }
        ShopGoods goods = shopGoodsService.find(ShopActivityGoods.getObjectId());
        ShopActivity shopActivity = shopActivityService.find(ShopActivityGoods.getActivityId());
        model.addAttribute("shopGoodsSpecs", shopGoodsSpecs);
        model.addAttribute("goods", goods);
        model.addAttribute("activityId", ShopActivityGoods.getActivityId());
        model.addAttribute("activity", shopActivity);
        model.addAttribute("type", shopActivity.getActivityType());
        //获取商品的规格
        if (goods.getGoodsSpec() != null && !goods.getGoodsSpec().trim().equals("")) {
            Map<String, Object> specNameMap = JacksonUtil.convertMap(goods.getSpecName());
            List<String> keys = new ArrayList<>(specNameMap.keySet());
            model.addAttribute("specNameList", keys);
            Map<String, List<GoodsSpecVo>> specMap = GoodsUtils.goodsSpecStrToMapList(goods.getGoodsSpec());
            model.addAttribute("specNameMap", specNameMap);
            model.addAttribute("specMap", specMap);
        }
        //获取对应的规格值
        if (id != null) {
            List<ShopActivityGoodsSpec> shopActivityGoodsSpecs = shopActivityGoodsSpecService
                .findList("activityGoodsId", id);
            //转map
            Map<Long, ShopActivityGoodsSpec> shopActivityGoodsSpecMap = new HashMap<>();
            for (ShopActivityGoodsSpec item : shopActivityGoodsSpecs) {
                shopActivityGoodsSpecMap.put(item.getSpecId(), item);
            }
            model.addAttribute("activitySpecMap", shopActivityGoodsSpecMap);
            model.addAttribute("activityGoods", shopActivityGoodsService.find(id));
        }
        model.addAttribute("activityType", ActivityTypeEnus.activitMap.get(shopActivity.getActivityType()));
        model.addAttribute("id", id);
        return "/activity/shop_activity_audio/activity_spec_select";
    }

    @RequestMapping(value = {"/activity/{type}/actSucess"}, method = {RequestMethod.GET})
    public String msgShow(HttpServletRequest request, ModelMap modelMap, @PathVariable String type) {
        modelMap.addAttribute("msg", "报名成功");
        modelMap.addAttribute("referer",
            request.getContextPath() + "/admin/shop_activity_signup/activity/" + type + "/list.jhtml");
        return "/common/common/show_msg";
    }

}
