package com.framework.loippi.controller.activity;

import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.activity.ShopActivityGoods;
import com.framework.loippi.entity.activity.ShopActivity;
import com.framework.loippi.entity.activity.ShopActivityGoodsSpec;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsSpec;
import com.framework.loippi.enus.ActivityTypeEnus;
import com.framework.loippi.service.activity.ShopActivityGoodsService;
import com.framework.loippi.service.activity.ShopActivityGoodsSpecService;
import com.framework.loippi.service.activity.ShopActivityService;
import com.framework.loippi.service.product.ShopGoodsService;
import com.framework.loippi.service.product.ShopGoodsSpecService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.GoodsUtils;
import com.framework.loippi.utils.JacksonUtil;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.StringUtil;
import com.framework.loippi.vo.goods.GoodsSpecVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 活动商品列表
 */
@Slf4j
@Controller
@RequestMapping("/admin/plarformShopActivityObject")
public class ActivityGoodsSysController extends GenericController {


    // 活动对象关联表Service接口
    @Autowired
    private ShopActivityGoodsService shopActivityGoodsService;
    @Autowired
    private ShopActivityService shopActivityService;
    @Autowired
    private ShopActivityGoodsSpecService shopActivityGoodsSpecService;
    @Autowired
    private ShopGoodsService shopGoodsService;
    @Autowired
    private ShopGoodsSpecService shopGoodsSpecService;

    /**
     * 活动对象关联表列表
     */
    @RequestMapping("/{type}/list")
    @RequiresPermissions(value = {"admin:activityautit:xianshiqiang",
        "admin:activityautit:tuangou", "admin:activityautit:zhuanchang", "admin:activityautit:kanjia"
        , "admin:activityautit:fenxiao", "admin:activityautit:rexiao",
        "admin:activityautit:fresh"}, logical = Logical.OR)
    public String list(Model model,
        @PathVariable String type,
        Long activityId,
        Long goodsId,
        String goodsName,
        @RequestParam(required = true, defaultValue = "0") Integer auditType,
        @RequestParam(required = false, value = "pageNo", defaultValue = "1") int pageNo) {
        Pageable pager = new Pageable();
        pager.setPageNumber(pageNo);
        //设置审核状态
        Map<String, Object> params = Paramap.create();
        if (!StringUtil.isEmpty(type)) {
            pager.setParameter(params.put("activityType", ActivityTypeEnus.activitTypeMap.get(type)));
        }
        if (auditType != 0) {
            params.put("status", auditType);
        }
        if (activityId != null) {
            params.put("activityId", activityId);
        }

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
                shopActivityGoods.setActivityName(shopActivityMap.get(shopActivityGoods.getActivityId()).getName());
                shopActivityGoods.setShopActivityGoodsSpecList(longListMap.get(shopActivityGoods.getId()));
                ShopGoods shopGoods = goodsMap.get(shopActivityGoods.getObjectId());
                if (shopGoods != null) {
                    //伪搜索
                    if (goodsId!=null){
                        if (!shopActivityGoods.getId().toString().equals(goodsId+"")){
                            shopActivityGoods.setStatus(3);
                        }
                    }
                    if (goodsName!=null){
                        if (shopGoods.getGoodsName().indexOf(goodsName)==-1){
                            shopActivityGoods.setStatus(3);
                        }
                    }
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
        model.addAttribute("type", type);
        model.addAttribute("goodsId", goodsId);
        model.addAttribute("goodsName", goodsName);
        model.addAttribute("auditType", auditType);
        model.addAttribute("page", page);
        model.addAttribute("activityId", activityId);
        return "activity/shop_activity_goods/shopActivityObjectList";
    }

    /**
     * 跳转至活动对象关联表新增或修改页面
     */
    @RequestMapping("/forward")
    public String add(Model model, @RequestParam(required = false, value = "id", defaultValue = "0") Long id) {
        ShopActivityGoods shopActivityGoods = shopActivityGoodsService.find(id);
        ShopActivity shopActivity = shopActivityService.find(shopActivityGoods.getActivityId());
        ShopGoods shopGoods = shopGoodsService.find(shopActivityGoods.getObjectId());
        List<ShopActivityGoodsSpec> shopActivityGoodsSpecs = shopActivityGoodsSpecService
            .findList(Paramap.create().put("activityGoodsId", shopActivityGoods.getId()));
        List<Long> specIdList = new ArrayList<>();
        for (ShopActivityGoodsSpec shopActivityGoodsSpec : shopActivityGoodsSpecs) {
            specIdList.add(shopActivityGoodsSpec.getSpecId());
        }
        List<ShopGoodsSpec> shopGoodsSpecs;
        if (specIdList.size() > 0) {
            shopGoodsSpecs = shopGoodsSpecService.findList(Paramap.create().put("idList", specIdList));
        } else {
            shopGoodsSpecs = new ArrayList<>();
        }
        model.addAttribute("shopActivity", shopActivity);
        model.addAttribute("shopActivityObject", shopActivityGoods);
        model.addAttribute("shopGoods", shopGoods);
        model.addAttribute("shopGoodsSpecs", shopGoodsSpecs);
        return "/activity/shop_activity_goods/shopActivityObjectDetail";
    }

    /**
     * 变跟审核状态
     */
    @ResponseBody
    @RequestMapping("/updateStatus")
    @RequiresPermissions(value = {"admin:activityautit:xianshiqiang",
        "admin:activityautit:pingtuan", "admin:activityautit:chuxiao", "admin:activityautit:kanjia"
        , "admin:activityautit:fenxiao", "admin:activityautit:rexiao",
        "admin:activityautit:fresh"}, logical = Logical.OR)
    public Map updateStatus(Model model, @RequestParam Long id,
        @RequestParam Integer status,
        HttpServletRequest request) {
        Map map = new HashMap();
        shopActivityGoodsService.updateStatus(id, status);
        map.put("success", true);
        map.put("msg", "删除成功");
        return map;
    }


    /**
     * 活动对象关联表删除
     */

    @ResponseBody
    @RequestMapping("/delete")
    public Map delete(Model model, @RequestParam Long[] ids,
        HttpServletRequest request) {
        Map map = new HashMap();
        shopActivityGoodsService.deleteActivityGoods(ids);
        map.put("success", 1);
        map.put("msg", "删除成功");
        return map;
    }

}

