package com.framework.loippi.controller.product;

import com.framework.loippi.consts.Constants;
import com.framework.loippi.consts.ShopGoodsClassState;
import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.product.ShopGoodsClass;
import com.framework.loippi.entity.product.ShopGoodsType;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.product.ShopGoodsBrandService;
import com.framework.loippi.service.product.ShopGoodsClassService;
import com.framework.loippi.service.product.ShopGoodsService;
import com.framework.loippi.service.product.ShopGoodsTypeService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.Paramap;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.RequestContext;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller - 商品分类表
 *
 * @author zijing
 * @version 2.0
 */
@Slf4j
@Controller("adminShopGoodsClassController")
@RequestMapping({"/admin/shop_goods_class"})
public class ShopGoodsClassSysController extends GenericController {

    @Resource
    private ShopGoodsClassService shopGoodsClassService;
    @Resource
    private ShopGoodsTypeService shopGoodsTypeService;
    @Resource
    private ShopGoodsBrandService shopGoodsBrandService;
    @Resource
    private ShopGoodsService shopGoodsService;
    @Resource
    private TwiterIdService twiterIdService;
    String message = "success";

    /**
     * 添加或修改
     *
     * @param goodsClass
     * @return
     */
    @RequestMapping("edit")
    public String edit(ShopGoodsClass goodsClass, HttpServletRequest request, ModelMap model) throws IOException {
        RequestContext requestContext = new RequestContext(request);
        if (goodsClass.getId() == null || goodsClass.getId() == 0) {
            //新增
            goodsClass.setGcTitle("123");
            goodsClass.setGcKeywords("");
            goodsClass.setExpenScale(new BigDecimal(1));
            goodsClass.setTypeId(twiterIdService.getTwiterId());
            //敏感词过滤
            if (!beanValidatorForModel(model, goodsClass)) {
                return "redirect:list.jhtml";
            }
            String gcName = goodsClass.getGcName();
            long count = shopGoodsClassService.count(Paramap.create().put("gcName", gcName));
            if (count == 0) {
                //生产广告分组
                Long classID = twiterIdService.getTwiterId();
                goodsClass.setId(classID);
                if (goodsClass.getGcParentId() == 0) {
                    goodsClass.setDeep(1);
                } else {
                    ShopGoodsClass shopGoodsClass = shopGoodsClassService.find(goodsClass.getGcParentId());
                    goodsClass.setDeep(shopGoodsClass.getDeep() + 1);
                }
                ShopGoodsType goodsType = shopGoodsTypeService.find(goodsClass.getTypeId());
                if (goodsType != null) {
                    goodsClass.setTypeName(goodsType.getTypeName());
                } else {
                    goodsClass.setTypeName("");
                }
                shopGoodsClassService.save(goodsClass);
                //查找父级id
                if (goodsClass.getGcParentId() == null || goodsClass.getGcParentId() == 0) {
                    goodsClass.setGcIdpath(goodsClass.getId() + ",");
                } else {
                    ShopGoodsClass goodscla = shopGoodsClassService.find(goodsClass.getGcParentId());
                    if (goodscla != null) {
                        goodsClass.setGcIdpath(goodscla.getGcIdpath() + goodsClass.getId() + ",");
                    }
                }
                //将idpath存进数据库
                shopGoodsClassService.update(goodsClass);
                model.addAttribute("msg", requestContext.getMessage("add.success"));
            }
        } else {
            //敏感词过滤
            if (!beanValidatorForModel(model, goodsClass)) {
                return "redirect:list.jhtml";
            }
            ShopGoodsType goodsType = shopGoodsTypeService.find(goodsClass.getTypeId());
            if (goodsType != null) {
                goodsClass.setTypeName(goodsType.getTypeName());
            } else {
                goodsClass.setTypeName("");
            }
            //修改
            shopGoodsClassService.update(goodsClass);
            model.addAttribute("msg", requestContext.getMessage("update.success"));
        }
        return "redirect:list.jhtml";
    }

    /**
     * 列表查询
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/list")
    public String list(HttpServletRequest request, ModelMap model, ShopGoodsClass shopGoodsClass,
                       @RequestParam(required = false, value = "pageNo", defaultValue = "1") Integer pageNumber) {
        //model.addAttribute("list", this.shopGoodsClassService.findByParams2(Paramap.create().put("gcParentId",0L).put("gcSort",1)));
        //model.addAttribute("classNameLike", shopGoodsClass.getClassNameLike());
        //return "/goods/shop_class/list";
        Pageable pager = new Pageable();
        shopGoodsClass.setGcParentId(ShopGoodsClassState.GC_PARENTID);
        shopGoodsClass.setGcSort(ShopGoodsClassState.GC_SORT);
        pager.setParameter(shopGoodsClass);
        pager.setPageNumber(pageNumber);
        pager.setOrderProperty("gc_sort");
        pager.setOrderDirection(Order.Direction.ASC);
        Page<ShopGoodsClass> byPage = shopGoodsClassService.findByPage(pager);
        model.addAttribute("page", byPage);
        model.addAttribute("classNameLike", shopGoodsClass.getClassNameLike());
        model.addAttribute("gcShow", shopGoodsClass.getGcShow());
        model.addAttribute("typeName", shopGoodsClass.getTypeName());
        return "/goods/shop_class/list";
    }

    /**
     * 获取子节点
     *
     * @param id
     * @param level
     * @return
     */
    @RequestMapping("child")
    public
    @ResponseBody
    List<ShopGoodsClass> child(@RequestParam Long id, @RequestParam int level) {
        //存入deep，配合ajax
        List<ShopGoodsClass> classList = shopGoodsClassService.findByParams2(Paramap.create().put("gcParentId", id).put("gcSort", 1));
        if (classList != null && classList.size() > 0) {
            for (ShopGoodsClass vo : classList) {
                vo.setStingid(vo.getId().toString());
                vo.setStringParentId(vo.getGcParentId().toString());
                vo.setDeep(level);
            }
        }
        return classList;
    }

    @RequestMapping("forward")
    public String forward(@ModelAttribute ShopGoodsClass goodsClass, ModelMap model, @ModelAttribute ShopGoodsType goodsType) {
        //拼装类型和类别
        List<ShopGoodsClass> goodsClasses = shopGoodsClassService.findByParams2(Paramap.create().put("gcParentId", 0L));
        model.addAttribute("typeList", shopGoodsTypeService.findAllSimple());
        if (goodsClass.getId() != 0) {
            ShopGoodsClass gc = shopGoodsClassService.find(goodsClass.getId());
            model.addAttribute("deep", gc.getDeep());
            model.addAttribute("classList", goodsClasses);
            model.addAttribute("gc", gc);
            return "/goods/shop_class/edit";
        } else {
            ShopGoodsClass shopGoodsClass = shopGoodsClassService.find(goodsClass.getGcParentId());
            if (shopGoodsClass != null) {
                Integer deep = shopGoodsClass.getDeep();
                model.addAttribute("deep", deep);
            }
            model.addAttribute("flag", goodsClass.getGcParentId());
            model.addAttribute("classList", goodsClasses);
            return "goods/shop_class/add";
        }
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
        ShopGoodsClass goodsClass = new ShopGoodsClass();
        goodsClass.setId(id);
        goodsClass.setGcSort(value);
        shopGoodsClassService.update(goodsClass);
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
        ShopGoodsClass goodsClass = new ShopGoodsClass();
        goodsClass.setId(id);
        goodsClass.setGcName(value);
        //判断是否有重复名称
        if (shopGoodsClassService.findCount(goodsClass) > 0) {
            return false;
        } else {
            //执行修改操作
            shopGoodsClassService.update(goodsClass);
            return true;
        }
    }

    /**
     * 校验菜单下是否有子菜单
     *
     * @return
     */
    @RequestMapping("/validateparentid")
    public
    @ResponseBody
    Boolean validateparentid(@RequestParam Long id) {
        //校验重复
        Map<String, Object> params = new HashMap<>();
        params.put("gcParentId", id);
        Long goodsclaslist = shopGoodsClassService.count(params);
        if (goodsclaslist > 0) {
            return false;
        } else {
            return true;
        }
    }

    @RequestMapping(value = "/deleteid", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, String> deleteid(@RequestParam(value = "classid") Long classid, HttpServletRequest request,
                                 ModelMap model) {
        RequestContext requestContext = new RequestContext(request);
        Map<String, String> map = Maps.newHashMap();
        if (classid == null) {
            model.addAttribute("result", "ID为空");
            map.put("result", "ID为空");
            map.put(message, "true");
            model.addAttribute("referer", request.getHeader("Referer"));
            return map;
        }
        Map<String, Object> params = new HashMap<>();
        if (classid != null) {
            params.put("gcId", classid);
            Long countNum = shopGoodsService.count(params);
            if (countNum != null && countNum > 0) {
                map.put("result", "该分类下还有商品,不能删除");
                map.put(message, "true");
                return map;
            }
            params.clear();
            params.put("classId", classid);
            //countNum = shopGoodsBrandService.count(params);
            //if (countNum != null && countNum > 0) {
            //    map.put("result", "该分类下还有绑定了品牌");
            //    map.put(message, "true");
            //    return map;
            //}
        }

        shopGoodsClassService.delete(classid);//qt
        ShopGoodsClass shopGoodsClass = shopGoodsClassService.find(classid);
        if (shopGoodsClass != null) {
            Long advId = shopGoodsClass.getAdvId();
        }
        map.put("result", requestContext.getMessage("delete.success"));
        map.put(message, "true");
        return map;
    }

    /**
     * 校验表单
     *
     * @return
     */
    @RequestMapping("/validate")
    public
    @ResponseBody
    String validateForm(ShopGoodsClass goodsClass) {
        //校验重复
        ShopGoodsClass gclass = new ShopGoodsClass();
        gclass.setGcParentId(goodsClass.getGcParentId());
        if (!goodsClass.getGcName().equals("")) {
            gclass.setGcName(goodsClass.getGcName().trim());
        } else {
            gclass.setGcName("0");
        }
        if (goodsClass.getId() != null && goodsClass.getId() != 0) {//编辑商品
            ShopGoodsClass prevClass = shopGoodsClassService.find(goodsClass.getId());//初始的class
            if (prevClass.getGcName().equals(goodsClass.getGcName().trim())) {
                return "true";
            } else {
                Map<String, Object> params = new HashMap<>();
                params.put("gcName", goodsClass.getGcName());
                params.put("gcParentId", goodsClass.getGcParentId());
                Long count = shopGoodsClassService.count(params);
                if (count > 0) {
                    return "false";
                } else {
                    return "true";
                }
            }
        } else {//新增商品
            Map<String, Object> params = new HashMap<>();
            params.put("gcName", goodsClass.getGcName());
            params.put("gcParentId", goodsClass.getGcParentId());
            Long count = shopGoodsClassService.count(params);
            if (count > 0) {
                return "false";
            } else {
                return "true";
            }
        }
    }

    /**
     * 获取分类
     *
     * @param gcId
     * @return
     */
    @RequestMapping("/getGoodsClass")
    public
    @ResponseBody
    ShopGoodsClass getGoodsClass(@RequestParam Long gcId) {
        ShopGoodsClass goodsClass = shopGoodsClassService.find(gcId);
        return goodsClass;
    }

    @RequestMapping(value = "/listByfind")
    public String listByfind(HttpServletRequest request, ModelMap model, ShopGoodsClass shopGoodsClass
            , @RequestParam(required = false, value = "pageNo", defaultValue = "1") Integer pageNumber) {
        Pageable pager = new Pageable();
        shopGoodsClass.setGcSort(ShopGoodsClassState.GC_SORT);
        pager.setParameter(shopGoodsClass);
        pager.setPageNumber(pageNumber);
        Page<ShopGoodsClass> byPage = shopGoodsClassService.findByPage(pager);
        model.addAttribute("page", byPage);
        model.addAttribute("classNameLike", shopGoodsClass.getClassNameLike());
        model.addAttribute("gcShow", shopGoodsClass.getGcShow());
        model.addAttribute("changeAction", ShopGoodsClassState.CHANGE_ACTION);
        model.addAttribute("typeName", shopGoodsClass.getTypeName());
        return "/goods/shop_class/list";
    }


    @RequestMapping(value = "/delete")
    public String delete(HttpServletRequest request, ModelMap model, Long[] ids) {
        if (ids != null && ids.length > 0) {
            for (Long id : ids) {
                Map<String, Object> params = new HashMap<>();
                params.put("gcParentId", id);
                Long goodsclaslist = shopGoodsClassService.count(params);
                if (goodsclaslist != null && goodsclaslist > 0) {
                    ShopGoodsClass shopGoodsClass = shopGoodsClassService.find(id);
                    model.addAttribute("msg", shopGoodsClass.getGcName() + "有子类，请删除子类再进行删除操作");
                    model.addAttribute("referer", request.getHeader("Referer"));
                    return Constants.MSG_URL;
                }
            }
            shopGoodsClassService.deleteAll(ids);
        } else {
            model.addAttribute("msg", "id不能为空");
            model.addAttribute("referer", request.getHeader("Referer"));
            return Constants.MSG_URL;
        }
        return "redirect:list.jhtml";
    }

}
