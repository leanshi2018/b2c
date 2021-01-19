package com.framework.loippi.controller.common;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.Principal;
import com.framework.loippi.entity.common.ShopCommonExpress;
import com.framework.loippi.entity.product.ShopExpressSpecialGoods;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.result.common.goods.ExpressSpecialGoodsResult;
import com.framework.loippi.service.KuaidiService;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.common.ShopCommonAreaService;
import com.framework.loippi.service.common.ShopCommonExpressNotAreaService;
import com.framework.loippi.service.common.ShopCommonExpressService;
import com.framework.loippi.service.product.ShopExpressSpecialGoodsService;
import com.framework.loippi.service.product.ShopGoodsSpecService;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.Paramap;
import com.google.common.collect.Maps;

/**
 * Controller - 快递公司
 *
 * @author zijing
 * @version 2.0
 */
@Controller("adminShopCommonExpressController")
@RequestMapping({"/admin/shop_common_express"})
public class ShopCommonExpressController extends GenericController {

    @Resource
    private ShopCommonExpressService shopCommonExpressService;
    @Resource
    private KuaidiService kuaidiService;
    @Resource
    private TwiterIdService twiterIdService;
    @Resource
    private ShopGoodsSpecService shopGoodsSpecService;
    @Resource
    private ShopExpressSpecialGoodsService shopExpressSpecialGoodsService;
    @Resource
    private ShopCommonExpressNotAreaService shopCommonExpressNotAreaService;
    @Resource
    private ShopCommonAreaService commonAreaService;

    /**
     * 列表 letter首字母
     */
    @RequestMapping(value = {"/list"}, method = {RequestMethod.GET})
    public String list(@RequestParam(required = false, value = "pageNo", defaultValue = "1") Integer pageNo,
            String letter, Long id, ModelMap model,String eState) {
        Pageable pageable = new Pageable(pageNo, 20);
        pageable.setParameter(Paramap.create().put("eLetter", letter).put("eState",eState));
        model.addAttribute("letter", letter);
        model.addAttribute("eState", eState);
        model.addAttribute("page", this.shopCommonExpressService.findByPage(pageable));
        return "/common/shop_common_express/list";
    }

    /**
     * 跳转添加页面
     */
    @RequestMapping(value = {"/forward"}, method = {RequestMethod.GET})
    public String forward(Long id, ModelMap model) {
        List letters = Arrays.asList('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z');
        if (id != null && !id.equals(0L)) {
            //查询快递公司
            ShopCommonExpress express = this.shopCommonExpressService.find(id);
            model.addAttribute("express", express);
            model.addAttribute("code", express.getECode());
            model.addAttribute("url", express.getEUrl());
            model.addAttribute("let", express.getELetter());
            model.addAttribute("type", "修改");
        } else {
            ShopCommonExpress express = new ShopCommonExpress();
            express.setEState(1);
            express.setEOrder(1);
            model.addAttribute("express", express);
            model.addAttribute("type", "新增");
        }
        model.addAttribute("letters", letters);
        return "/common/shop_common_express/edit";
    }

    /**
     * 更新保存
     */
    @RequestMapping(value = {"/saveOrUpdateExpress"}, method = RequestMethod.POST)
    public String saveOrUpdateExpress(Model model, ShopCommonExpress express) {
        if (express.getId() == null || "".equals(express.getId())) {
            express.setIsDel(0);
            express.setId(twiterIdService.getTwiterId());
            shopCommonExpressService.save(express);
            model.addAttribute("msg", "新增成功！");
        } else {
            shopCommonExpressService.update(express);
            model.addAttribute("msg", "修改成功！");
        }
        return "redirect:list.jhtml";
    }

    /**
     * 不地区管理列表
     */
    @RequestMapping(value = {"/notArealist"})
    public String notArealist(@RequestParam(required = false, value = "pageNo", defaultValue = "1") Integer pageNo,
                              Long expressId, ModelMap model) {
        Pageable pageable = new Pageable(pageNo, 20);
        pageable.setParameter(expressId);
        model.addAttribute("page", shopCommonExpressNotAreaService.findByPage(pageable));
        return "/common/shop_common_express/lessthanarea.jhtml";
    }

    /**
     * 删除不地区
     */
    @RequestMapping(value = {"/delNotArea"})
    public String delNotArea(Long id, ModelMap model) {
        if (id==null){
            model.addAttribute("msg", "不地区id为空！");
            return "redirect:notArealist.jhtml";
        }

        shopCommonExpressNotAreaService.delete(id);
        model.addAttribute("msg", "删除成功！");
        return "redirect:notArealist.jhtml";
    }

    /**
     * 添加不地区
     */
    @RequestMapping(value = {"/addNotArea"})
    public String addNotArea(Long expressId,Long[] areaIds , ModelMap model) {
        if (expressId==null){
            model.addAttribute("msg", "快递id为空！");
            return "redirect:notArealist.jhtml";
        }
        if (areaIds==null || areaIds.length==0){
            model.addAttribute("msg", "地区id为空！");
            return "redirect:notArealist.jhtml";
        }

        ShopCommonExpress express = shopCommonExpressService.find(expressId);
        if (express==null){
            model.addAttribute("msg", "快递为空！");
            return "redirect:notArealist.jhtml";
        }

        shopCommonExpressNotAreaService.addNotArea(express,areaIds);
        model.addAttribute("msg", "添加成功！");
        return "redirect:notArealist.jhtml";
    }


    /**
     * 管理渠道商品
     */
    @RequestMapping(value = {"/findExpressSpecialGoods"})
    public String findExpressSpecialGoods(Model model ,Long id) {
        if (id==null){
            model.addAttribute("msg", "物流公司id为空！");
            return "redirect:list.jhtml";
        }
        ShopCommonExpress express = shopCommonExpressService.find(id);
        if (express==null){
            model.addAttribute("msg", "没有该物流公司信息！");
            return "redirect:list.jhtml";
        }
        model.addAttribute("express", express);

        List<ShopExpressSpecialGoods> goods = shopExpressSpecialGoodsService.findByExpressId(id);

        model.addAttribute("specialGoods", goods);

        return "/common/shop_common_channel/addchannel.jhtml";
    }

    /**
     * 添加渠道商品
     */
    @RequestMapping(value = {"/addExpressSpecialGoods"})
    public String addExpressSpecialGoods(Long expressId,Long[] specIds , ModelMap model) {
        //后台管理员
        String adminName="";
        Subject subject = SecurityUtils.getSubject();
        if (subject != null) {
            Principal principal = (Principal) subject.getPrincipal();
            if (principal != null && principal.getId() != null) {
                adminName=principal.getUsername();
            }
        }

        if (expressId==null){
            model.addAttribute("msg", "快递id为空！");
            return "redirect:notArealist.jhtml";
        }
        if (specIds==null || specIds.length==0){
            model.addAttribute("msg", "地区id为空！");
            return "redirect:notArealist.jhtml";
        }

        ShopCommonExpress express = shopCommonExpressService.find(expressId);
        if (express==null){
            model.addAttribute("msg", "快递为空！");
            return "redirect:notArealist.jhtml";
        }

        shopExpressSpecialGoodsService.addExpressSpecialGoods(express,specIds,adminName);
        model.addAttribute("msg", "添加成功！");
        return "redirect:findExpressSpecialGoods.jhtml";
    }

    /**
     * 渠道商品列表
     */
    @RequestMapping(value = {"/expressSpecialGoodsList"})
    public String expressSpecialGoodsList(HttpServletRequest request,Pageable pageable,ModelMap model,@ModelAttribute ExpressSpecialGoodsResult param) {
        pageable.setParameter(param);
        pageable.setOrderProperty("creationTime");
        pageable.setOrderDirection(Order.Direction.DESC);
        model.addAttribute("page", shopExpressSpecialGoodsService.findListResultByPage(pageable));
        return " /common/shop_common_channel/list.jhtml";
    }

    /**
     * 删除渠道商品
     */
    @RequestMapping(value = {"/delExpressSpecialGoods"})
    public String delExpressSpecialGoods(Long id, ModelMap model) {
        if (id==null){
            model.addAttribute("msg", "渠道商品id为空！");
            return "redirect:expressSpecialGoodsList.jhtml";
        }

        shopExpressSpecialGoodsService.delete(id);
        model.addAttribute("msg", "删除成功！");
        return "redirect:expressSpecialGoodsList.jhtml";
    }

    /**
     * 校验表单
     */
    @RequestMapping("/validate")
    public @ResponseBody
    String validateForm(String eName, Long id) {
        //校验重复
        Paramap params = Paramap.create().put("nid", id).put("eName", eName);
        return shopCommonExpressService.count(params) > 0 ? "false" : "true";
    }


    /**
     * 删除物流公司
     * qz
     */
    @RequestMapping(value = "/deleteExpress", method = RequestMethod.POST)
    @ResponseBody
    public Map <String, String> deleteExpress(Long[] ids, HttpServletRequest request) {
        Map <String, String> map = Maps.newHashMap();
        String referer = request.getHeader("Referer");
        try {
            for (Long id : ids) {
                shopCommonExpressService.delete(id);
            }
            map.put("referer", referer);
            map.put("success", "true");
            map.put("msg", "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            map.put("referer", referer);
            map.put("success", "false");
            map.put("msg", "删除失败");
        }
        return map;
    }

    /**
     * 根据快递单号查询快递公司
     */
    @RequestMapping(value = "/autoComNum")
    @ResponseBody
    public String autoComNum(@RequestParam String text) {
        return kuaidiService.queryCom(text);
    }

    /**
     * 快递列表
     * @param request
     * @param pageable
     * @param model
     * @param param
     * @return
     */
    @RequestMapping(value = "/findExpressList")
    public String findExpressList(HttpServletRequest request, Pageable pageable, ModelMap model, @ModelAttribute ShopCommonExpress param) {
        pageable.setParameter(Paramap.create().put("name", param.getEName()).put("eState",1).put("isDel",0));
        pageable.setOrderProperty("e_sort");
        pageable.setOrderDirection(Order.Direction.DESC);
        model.addAttribute("page", shopCommonExpressService.findByPage(pageable));
        return "";
    }
}
