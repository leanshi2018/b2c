package com.framework.loippi.controller.product;

import com.framework.loippi.consts.Constants;
import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsBrand;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.service.TUserSettingService;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.product.ShopGoodsBrandService;
import com.framework.loippi.service.product.ShopGoodsService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.PinyinUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.RequestContext;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Controller - 品牌表
 *
 * @author zijing
 * @version 2.0
 */
@Slf4j
@Controller("adminShopGoodsBrandController")
@RequestMapping({"/admin/shop_goods_brand"})
public class ShopGoodsBrandSysController extends GenericController {

    @Autowired
    private ShopGoodsService shopGoodsService;
    @Autowired
    private ShopGoodsBrandService shopGoodsBrandService;
    @Autowired
    private TwiterIdService twiterIdService;

    /**
     * 列表查询
     *
     * @param pageable
     * @param model
     * @return
     */
    @RequestMapping(value = "/list")
    public String list(Pageable pageable, HttpServletRequest request, ModelMap model,
                       ShopGoodsBrand shopGoodsBrand, @RequestParam(required = false, value = "pageNo", defaultValue = "1") Integer pageNo) {
        processQueryConditions(pageable, request);
        pageable.setPageNumber(pageNo);
        pageable.setParameter(shopGoodsBrand);
        pageable.setOrderDirection(Order.Direction.ASC);
        pageable.setOrderProperty("brand_sort");
        model.addAttribute("page", this.shopGoodsBrandService.findByPage(pageable));
        model.addAttribute("paramter", pageable.getParameter());
        return "/goods/brand/list";
    }

    /**
     * 修改特别推荐
     *
     * @param
     */
    @RequestMapping("/recommond")
    public
    @ResponseBody
    boolean updateRecommond(@RequestParam Long id, @RequestParam int value, HttpServletRequest request) {
        ShopGoodsBrand brand = new ShopGoodsBrand();
        brand.setId(id);
        brand.setBrandRecommend(value);
        //敏感词过滤
        if (!beanValidatorForJson(brand)) {
            return false;
        }
        shopGoodsBrandService.update(brand);
        //用户操作日志
        return true;
    }

    /**
     * 跳转
     *
     * @param model
     * @return
     */
    @RequestMapping("/forward")
    public String forward(ModelMap model, @RequestParam Long id) {
        //查询类别列表
        if (id == null || id == 0) {
            return "goods/brand/add";
        } else {
            //查询品牌
            ShopGoodsBrand brand = shopGoodsBrandService.find(id);
            if (brand.getAdvertImage() != null) {
                brand.setAdvertImageList(Lists.newArrayList(brand.getAdvertImage().split(",")));
            } else {
                brand.setAdvertImageList(Lists.newArrayList());
            }
            model.addAttribute("brand", brand);
            return "goods/brand/edit";
        }
    }

    /**
     * 编辑
     *
     * @param brand
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/edit")
    public String edit(@ModelAttribute("brand") ShopGoodsBrand brand, HttpServletRequest request, ModelMap model) {
        //敏感词过滤
        if (!beanValidatorForModel(model, brand)) {
            String referer = request.getHeader("Referer");
            model.addAttribute("referer", referer);
            return Constants.MSG_URL;
        }
        brand.setAdvertImageList(Lists.newArrayList(brand.getAdvertImage().split(",")));
        brand.setPinyin(PinyinUtils.toFirst(brand.getBrandName()));
        shopGoodsBrandService.update(brand);
        return "redirect:list.jhtml";
    }

    /**
     * 修改排序
     *
     * @return
     */
    @RequestMapping("/modifySort")
    public
    @ResponseBody
    Boolean modifySort(@RequestParam Long id, @RequestParam Integer value) {
        ShopGoodsBrand brand = new ShopGoodsBrand();
        brand.setId(id);
        brand.setBrandSort(value);
        shopGoodsBrandService.update(brand);
        return true;
    }

    /**
     * 修改分类名称
     *
     * @param id
     * @param value
     * @return
     */
    @RequestMapping("/modifyName")
    public
    @ResponseBody
    Boolean modifyname(@RequestParam Long id, @RequestParam String value) {
        ShopGoodsBrand brand = new ShopGoodsBrand();
        brand.setId(id);
        brand.setBrandName(value);
        //敏感词过滤
        if (!beanValidatorForJson(brand)) {
            return false;
        }
        //判断是否有重复名称
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("brandName", value);
        if (shopGoodsBrandService.count(params) > 0) {
            return false;
        } else {
            //执行修改操作
            shopGoodsBrandService.update(brand);
            return true;
        }
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public String delete(@RequestParam Long[] ids, HttpServletRequest request, ModelMap model) {
        if (ids != null && ids.length > 0) {
            Map<String, Object> map = new HashMap<>();
            map.put("brandIds", ids);
            Long list = shopGoodsService.count(map);
            if (list > 0) {
                model.addAttribute("referer", request.getHeader("Referer"));
                model.addAttribute("msg", "删除失败,请先删除该品牌下的所有商品");
                return Constants.MSG_URL;
            }
            shopGoodsBrandService.deleteAll(ids);
        }
        model.addAttribute("referer", request.getHeader("Referer"));
        model.addAttribute("msg", "删除成功");
        //用户操作日志
        return Constants.MSG_URL;
    }

    /**
     * 校验表单
     *
     * @return
     */
    @RequestMapping("/validate")
    public
    @ResponseBody
    String validateForm(@ModelAttribute ShopGoodsBrand brand) {
        //校验重复
        Map<String, Object> goods1 = new HashMap<>();
        goods1.put("brandName", brand.getBrandName());
        if (brand.getId() != null) {
            goods1.put("combinationIndex", brand.getId());
        }
        if (shopGoodsBrandService.count(goods1) > 0) {
            return "false";
        } else {
            return "true";
        }
    }

    /**
     * 待审核列表
     *
     * @param model
     * @param pager
     * @return
     */
    @RequestMapping("/applyList")
    public String applyList(ModelMap model, Pageable pager, HttpServletRequest request) {
        processQueryConditions(pager, request);
        Page<ShopGoodsBrand> results = shopGoodsBrandService.findByPage(pager);
        model.addAttribute("page", results);//总数
        model.addAttribute("paramter", pager.getParameter());
        return "goods/brand/applyList";
    }


    /**
     * 通过品牌
     *
     * @param ids
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/pass")
    public String pass(@RequestParam Long[] ids, HttpServletRequest request, ModelMap model) {
        String referer = request.getHeader("Referer");
        StringBuilder stringBuilder = new StringBuilder();
        for (Long id : ids) {
            ShopGoodsBrand brand = shopGoodsBrandService.find(id);
            brand.setBrandApply(1);
            shopGoodsBrandService.update(brand);
            stringBuilder.append(brand.getBrandName()).append(",");
        }
        model.addAttribute("referer", referer);
        model.addAttribute("msg", "品牌通过");

        //用户操作日志
        return Constants.MSG_URL;
    }

    /**
     * 拒绝品牌
     *
     * @param ids
     * @param request
     * @param model
     * @return
     */

    @RequestMapping("/refuse")
    public String refuse(@RequestParam Long[] ids, HttpServletRequest request, ModelMap model) {
        String referer = request.getHeader("Referer");
        for (Long id : ids) {
            ShopGoodsBrand brand = new ShopGoodsBrand();
            brand.setId(id);
            brand.setBrandApply(2);
            shopGoodsBrandService.update(brand);
        }
        model.addAttribute("referer", referer);
        model.addAttribute("msg", "品牌未通过");
        //用户操作日志
        return "redirect:list.jhtml";
    }

    /**
     * 获取店铺商品列表
     *
     * @param pageNo
     * @return
     */
    @RequestMapping("/select")
    public String listBrand(Model model,
                            @RequestParam(required = false, value = "pageNo", defaultValue = "1") int pageNo) {
        Pageable pager = new Pageable();
        pager.setPageNumber(pageNo);
        model.addAttribute("pager", this.shopGoodsBrandService.findByPage(pager));
        model.addAttribute("pageNo", pager.getPageNumber());    // 当前页
        model.addAttribute("pageSize", pager.getPageSize());// 每页显示条数
        return "goods/brand/brand_select";
    }

    /**
     * 保存
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String saveBran(ShopGoodsBrand brand, ModelMap model, HttpServletRequest request) {
        RequestContext requestContext = new RequestContext(request);
        brand.setBrandApply(0);
        brand.setBrandRecommend(1);
        brand.setCreateTime(new Date());
        brand.setId(twiterIdService.getTwiterId());
        shopGoodsBrandService.save(brand);
        //敏感词过滤
        if (!beanValidatorForModel(model, brand)) {
            String referer = request.getHeader("Referer");
            model.addAttribute("referer", referer);
            return Constants.MSG_URL;
        } else {
            return "redirect:list.jhtml";
        }
    }

}

