package com.framework.loippi.controller.product;

import com.framework.loippi.consts.Constants;
import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.common.ShopCommonSpec;
import com.framework.loippi.entity.product.ShopGoodsClass;
import com.framework.loippi.entity.product.ShopGoodsType;
import com.framework.loippi.entity.product.ShopGoodsTypeSpec;
import com.framework.loippi.service.common.ShopCommonSpecService;
import com.framework.loippi.service.product.*;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.Paramap;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.RequestContext;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Controller - 商品类型表
 *
 * @author zijing
 * @version 2.0
 */
@Controller("adminShopGoodsTypeController")
@RequestMapping({"/admin/shop_goods_type"})
public class ShopGoodsTypeSysController extends GenericController {

    @Resource
    private ShopGoodsTypeService shopGoodsTypeService;
    @Resource
    private ShopGoodsBrandService shopGoodsBrandService;
    @Resource
    private ShopGoodsSpecIndexService shopGoodsSpecIndexService;
    @Resource
    private ShopCommonSpecService shopCommonSpecService;
    @Resource
    private ShopGoodsTypeSpecService shopGoodsTypeSpecService;
    @Resource
    private ShopGoodsTypeBrandService shopGoodsTypeBrandService;
    @Resource
    private ShopGoodsClassService shopGoodsClassService;
    String message = "success";

    /**
     * 查询列表
     *
     * @param model
     * @return
     */
    @RequestMapping("/list")
    public String list(ModelMap model, HttpServletRequest request, Pageable pager) {
        processQueryConditions(pager, request);
        Page<ShopGoodsType> list = shopGoodsTypeService.findSimpleByPage(pager);
        model.addAttribute("page", list);//总数
        model.addAttribute("paramter",pager.getParameter());
        return "goods/type/list";
    }

    /**
     * 删除
     *
     * @param
     * @return
     */
    @RequestMapping("/delete")
    public String delete(@RequestParam Long[] ids, HttpServletRequest request, ModelMap model) {
        RequestContext requestContext = new RequestContext(request);
        String referer = request.getHeader("Referer");
        if (ids == null && ids.length <= 0) {
            model.addAttribute("referer", referer);
            model.addAttribute("msg", requestContext.getMessage("id不能为空"));
            return Constants.MSG_URL;
        }
        List<ShopGoodsClass> typeIds = shopGoodsClassService.findByParams2(Paramap.create().put("typeIds", ids));

        if (typeIds != null && typeIds.size() > 0) {
            model.addAttribute("referer", referer);
            model.addAttribute("msg", requestContext.getMessage("有分类还在使用该类型"));
            return Constants.MSG_URL;
        }
        for (Long id : ids) {
            shopGoodsTypeService.delete(id);
            shopGoodsSpecIndexService.deleteByTypeId(id);
        }
        shopGoodsTypeSpecService.deleteByTypeIds(ids);
        model.addAttribute("referer", referer);
        model.addAttribute("msg", requestContext.getMessage("delete.success"));
        return Constants.MSG_URL;
    }

    /**
     * @param id
     * @return
     */
    @RequestMapping("/forward")
    public String forward(Long id, ModelMap model, String brandName) {
        model.addAttribute("specList", shopCommonSpecService.findAll());
        Map<String, Object> map = new HashMap<>();
        map.put("brandName", brandName);
        model.addAttribute("brandList", shopGoodsBrandService.findList(map));
        model.addAttribute("brandName", brandName);
        if (id == null || id == 0) {
            return "goods/type/save";
        } else {
            ShopGoodsType types = shopGoodsTypeService.find(id);
            //查询
            //  types.setBrandList(shopGoodsBrandService.getBrandListByTypeId(id));
            //  types.setSpecList(shopCommonSpecService.findList("typeId", id));
            types.setSpecList(shopGoodsTypeSpecService.findByTypes(id));
            types.setBrandList(shopGoodsTypeBrandService.findByTypes(id));
            model.addAttribute("type", types);
            return "goods/type/edit";
        }
    }

    @RequestMapping("/save")
    public String save(ShopGoodsType vo, ModelMap model, HttpServletRequest request) {
        model.addAttribute("referer", "list");
        RequestContext requestContext = new RequestContext(request);
        //敏感词过滤
        if (!beanValidatorForModel(model, vo)) {
            return Constants.MSG_URL;
        }
        if (vo.getId() != null) {
            shopGoodsTypeService.updateGoodsType(vo);
            model.addAttribute("msg", requestContext.getMessage("update.success"));
        } else {
            shopGoodsTypeService.saveGoodsType(vo);
            model.addAttribute("msg", requestContext.getMessage("add.success"));
        }
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
        ShopGoodsType type = new ShopGoodsType();
        type.setId(id);
        type.setTypeSort(value);
        //敏感词过滤
        if (!beanValidatorForJson(type)) {
            return false;
        }
        shopGoodsTypeService.update(type);
        return true;
    }

    @RequestMapping(value = "/deleteid", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, String> deleteid(@RequestParam(value = "typeId") Long typeId,
                                 ModelMap model) {
        Map<String, String> map = Maps.newHashMap();
        if (typeId == null) {
            model.addAttribute("result", "ID为空");
            map.put("result", "ID为空");
            map.put(message, "true");
            return map;
        }
        shopGoodsTypeService.delete(typeId);
        map.put("result", "删除成功");
        map.put(message, "true");
        return map;
    }

    /**
     * 校验菜单下是否有子菜单
     *
     * @return
     */
    @RequestMapping("/validateparentid")
    public
    @ResponseBody
    Boolean validateparentid(@RequestParam String typeId) {
        //校验重复
        List<ShopGoodsType> goodsTypelist = shopGoodsTypeService.findList("", typeId);
        if (goodsTypelist.size() > 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 校验菜单下是否有子菜单
     *
     * @return
     */
    @RequestMapping("/findCountByName")
    public
    @ResponseBody
    Boolean findCountByName(@RequestParam String typeName) {
        //校验重复
        Map<String, Object> params = new HashMap<>();
        params.put("typeName", typeName);
        Long count = shopGoodsTypeService.count(params);
        if (count > 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 开启和关闭自定义规格
     *
     * @param id
     * @param value
     * @return
     */
    @RequestMapping("/editCustomSpec")
    @ResponseBody
    public Map<String, String> editCustomSpec(@RequestParam Long id, @RequestParam Integer value, HttpServletRequest request) {
        Map map = new HashMap();
        try {
            ShopGoodsType goodsType = new ShopGoodsType();
            goodsType.setId(id);
            goodsType.setOpenCustomSpec(value);
            shopGoodsTypeService.update(goodsType);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("result", "操作失败");
            map.put(message, "false");
            return map;
        }
        map.put("result", "操作成功");
        map.put(message, "true");
        return map;
    }

    /**
     * 开启和关闭自定义属性
     *
     * @param id
     * @param value
     * @return
     */
    @RequestMapping("/editCustomAttr")
    @ResponseBody
    public Map<String, String> editCustomAttr(@RequestParam Long id, @RequestParam Integer value, HttpServletRequest request) {
        Map map = new HashMap();
        try {
            ShopGoodsType goodsType = new ShopGoodsType();
            goodsType.setId(id);
            goodsType.setOpenCustomAttr(value);
            shopGoodsTypeService.update(goodsType);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("result", "操作失败");
            map.put(message, "false");
            return map;
        }
        map.put("result", "操作成功");
        map.put(message, "true");
        return map;
    }


    /**
     * 列表
     *
     * @param model
     * @return
     */
    @RequestMapping("/speclist")
    public String speclist(ModelMap model,
                           String spName, Long classId,
                           @RequestParam(required = false, value = "pageNo", defaultValue = "1") Integer pageNo) {
        ShopGoodsClass shopGoodsClass=shopGoodsClassService.find(classId);
        List<ShopGoodsTypeSpec> shopGoodsTypeSpecList=shopGoodsTypeSpecService.findByTypes(Optional.ofNullable(shopGoodsClass.getTypeId()).orElse(0L));
        List<Long> longList=new ArrayList<>();
        for (ShopGoodsTypeSpec item:shopGoodsTypeSpecList) {
            longList.add(item.getSpId());
        }
        Pageable pager = new Pageable(pageNo, 10);

            pager.setParameter(Paramap.create().put("ids",longList));


        Page<ShopCommonSpec> shopCommonSpecPage=new Page<>();
     if(longList!=null && longList.size()>0){
        shopCommonSpecPage=shopCommonSpecService.findByPage(pager);
     }
//        model.addAttribute("page", shopGoodsClassSpecPage);//总数
        model.addAttribute("classId", classId);
        model.addAttribute("page", shopCommonSpecPage);
        model.addAttribute("shopGoodsClass", shopGoodsClass);
        model.addAttribute("typeId", Optional.ofNullable(shopGoodsClass.getTypeId()).orElse(0L));
        return "goods/type/speclist";
    }

    //删除
    @RequestMapping("/deleteTypeId")
    private String deleteTypeId(Long[] ids, Long classId, Integer pageNumber,Long typeId) {

        if (ids[0]==null){
            ids[0]=-1L;
        }
        shopGoodsTypeSpecService.deleteByMap(Paramap.create().put("spIds",ids).put("typeId",typeId));
        return "redirect:speclist.jhtml?classId=" + classId + "&pageNumber=" + pageNumber;
    }
    /**
     * 获取店铺商品列表
     *
     * @param pageNo
     * @return
     */
    @RequestMapping("/select")
    public String select(Model model,ShopCommonSpec shopCommonSpec,
                         @RequestParam(required = false, value = "pageNo", defaultValue = "1") int pageNo) {
        Pageable pager = new Pageable();
        pager.setPageNumber(pageNo);
        pager.setParameter(shopCommonSpec);
        Page<ShopCommonSpec> list = shopCommonSpecService.findByPage(pager);
        model.addAttribute("page", list);
        model.addAttribute("pageNo", pager.getPageNumber());    // 当前页
        model.addAttribute("pageSize", pager.getPageSize());// 每页显示条数
        model.addAttribute("shopCommonSpec", shopCommonSpec);
        return "goods/type/spec_select";
    }

    @RequestMapping("/saveSpec")
    public
    @ResponseBody
    Boolean saveSpec( Long[] ids, @RequestParam Long type) {
        for (int i = 0,length=ids.length; i <length ; i++) {
            Long id=ids[i];
            if (id==null){
                continue;
            }
            long count = shopGoodsTypeSpecService.count(Paramap.create().put("typeId", type).put("spId", id));
            if (count > 0) {

            }else{
                ShopGoodsTypeSpec shopCommonSpec = new ShopGoodsTypeSpec();
                shopCommonSpec.setId(twiterIdService.getTwiterId());
                shopCommonSpec.setTypeId(type);
                shopCommonSpec.setSpId(id);
                shopGoodsTypeSpecService.save(shopCommonSpec);
            }

        }

        return true;
    }
}
