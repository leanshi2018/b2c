package com.framework.loippi.controller.common;

import com.framework.loippi.consts.Constants;
import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.common.ShopCommonSpec;
import com.framework.loippi.entity.common.ShopCommonSpecValue;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.mybatis.paginator.domain.Order.Direction;
import com.framework.loippi.service.common.ShopCommonSpecService;
import com.framework.loippi.service.common.ShopCommonSpecValueService;
import com.framework.loippi.service.product.ShopGoodsSpecIndexService;
import com.framework.loippi.service.product.ShopGoodsTypeSpecService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.Paramap;
import net.sf.json.JSONArray;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.RequestContext;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller - 商品规格表
 *
 * @author zijing
 * @version 2.0
 */
@Controller("adminShopCommonSpecController")
@RequestMapping({"/admin/shop_goods_spec"})
public class ShopCommonSpecController extends GenericController {

    @Resource
    private ShopGoodsSpecIndexService shopGoodsSpecIndexService;
    @Resource
    private ShopGoodsTypeSpecService shopGoodsTypeSpecService;
    @Resource
    private ShopCommonSpecService shopCommonSpecService;
    @Resource
    private ShopCommonSpecValueService shopCommonSpecValueService;

    /**
     * 删除规格
     *
     * @param spId
     * @return
     */
    @RequestMapping("/delSpec")
    @ResponseBody
    public String delSpec(@RequestParam(value = "spId") Long spId) {
        try {
            shopCommonSpecService.delete(spId);
            json = "{\"result\":\"1\",\"message\":\"操作成功\"}";
        } catch (Exception e) {
            e.printStackTrace();
            showErrorJson("删除失败");
            return json;
        }
        return json;
    }


    /**
     * 删除规格值前检查该规格值id是否被商品使用
     *
     * @param specValueId
     * @return
     */
    @RequestMapping("/findValueUsed")
    @ResponseBody
    public Map<String, Object> findValueUsed(@RequestParam(value = "specValueId") Long specValueId) {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("spValueId", specValueId);
            Long count = shopGoodsSpecIndexService.count(params);
            if (count > 0) {
                map.put("code", "20");//不可删除
            } else {
                map.put("code", "10");//可删除
            }
            map.put("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("success", false);
            map.put("msg", "系统异常");
        }
        return map;
    }

    /**
     * 规格模版
     *
     * @return model
     */
    @RequestMapping("/selectTemplate")
    @ResponseBody
    public String selectTemplate(@RequestParam(value = "specGroupName") String specGroupName) {
        try {
            specGroupName = specGroupName.trim();
            Map map = new HashMap();
            map.put("storeId", "0");
            map.put("specGroupName", specGroupName);
            List<ShopCommonSpec> specs = shopCommonSpecService.findList(map);
            JSONArray jsonArray = JSONArray.fromObject(specs);
            json = "{\"result\":\"1\",\"message\":\"操作成功\",\"data\":" + jsonArray.toString() + "}";
        } catch (Exception e) {
            e.printStackTrace();
            showErrorJson("删除失败");
            return json;
        }
        return json;
    }

    /**
     * 列表
     *
     * @param model
     * @return
     */
    @RequestMapping("/list")
    public String list(ModelMap model,
                       HttpServletRequest request,
                       @RequestParam(required = false, value = "pageNumber", defaultValue = "1") Integer pageNumber) {
        Pageable pager = new Pageable(pageNumber, 10);
        processQueryConditions(pager, request);
        pager.setOrderProperty("sp_sort");
        pager.setOrderDirection(Direction.ASC);
        Page<ShopCommonSpec> list = shopCommonSpecService.findByPage(pager);
        model.addAttribute("page", list);//总数
        model.addAttribute("paramter", pager.getParameter());
        return "goods/spec/list";
    }

    /**
     * 删除
     *
     * @return
     */
    @RequestMapping("/delete")
    public String delete(@RequestParam Long[] ids, HttpServletRequest request, ModelMap model) {
        RequestContext requestContext = new RequestContext(request);
        String referer = request.getHeader("Referer");
        model.addAttribute("msg", requestContext.getMessage("delete.success"));
        model.addAttribute("referer", referer);

        if ("1".equals(ids)) {
            model.addAttribute("msg", requestContext.getMessage("no.delete"));
            return Constants.MSG_URL;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("spIds", ids);
        Long count = shopGoodsTypeSpecService.count(params);
        if (count > 0) {
            model.addAttribute("msg", requestContext.getMessage("Specification_pink") + count + requestContext.getMessage("Specification_pink1"));
            return Constants.MSG_URL;
        }
        shopCommonSpecService.deleteAll(ids);
        return "redirect:list.jhtml";
    }

    //跳转编辑
    @RequestMapping("/forward")
    public String forward(@RequestParam Long id, ModelMap model) {
        if (id == 0) {
            return "goods/spec/add";
        } else {
            //规格
            ShopCommonSpec goodsSpec = shopCommonSpecService.find(id);
            List<ShopCommonSpecValue> shopCommonSpecValues = shopCommonSpecValueService.findList(Paramap.create().put("spId", id));
            model.addAttribute("goodsSpec", goodsSpec);
            model.addAttribute("specValues", shopCommonSpecValues);
            return "goods/spec/edit";
        }
    }

    /**
     * 保存或修改
     *
     * @param goodsSpec
     * @return
     */
    @RequestMapping("/saveOrUpdate")
    public String saveOrUpdate(@ModelAttribute ShopCommonSpec goodsSpec, HttpServletRequest request,
                               ModelMap model, String specValues) {
        RequestContext requestContext = new RequestContext(request);
        String referer = request.getHeader("Referer");
        model.addAttribute("referer", referer);
        //验证数据有效性
        if (!beanValidatorForModel(model, goodsSpec)) {
            model.addAttribute("msg", requestContext.getMessage("输入信息有误"));
            return Constants.MSG_URL;
        }
        //将前台拼接的json串进行反转
        specValues = StringEscapeUtils.unescapeHtml4(specValues);
        goodsSpec.setSpFormat(0);
        if (goodsSpec.getId() == null) {
            shopCommonSpecService.save(goodsSpec, specValues);
            model.addAttribute("msg", requestContext.getMessage("add.success"));
        } else {
            shopCommonSpecService.update(goodsSpec, specValues);
            model.addAttribute("msg", requestContext.getMessage("update.success"));
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
        ShopCommonSpec spec = new ShopCommonSpec();
        spec.setId(id);
        spec.setSpSort(value);
        shopCommonSpecService.update(spec);
        return true;
    }

    /**
     * 判断规格名字是否重复
     *
     * @param specName 规格名字
     * @return
     */
    @RequestMapping("/checkSpecName")
    public
    @ResponseBody
    boolean checkSpecName(@RequestParam String specName) {
        Map<String, Object> params = new HashMap<>();
        params.put("spName", specName);
        Long count = shopCommonSpecService.count(params);
        boolean flag = true;
        if (count > 0) {
            flag = false;
        }
        return flag;
    }

}
