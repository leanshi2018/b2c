package com.framework.loippi.controller.index;

import com.framework.loippi.controller.BaseController;
import com.framework.loippi.entity.product.ShopGoodsBrand;
import com.framework.loippi.entity.product.ShopGoodsClass;
import com.framework.loippi.result.common.goods.GoodsCategoriesResult;
import com.framework.loippi.result.goods.GoodsBrandDetail;
import com.framework.loippi.result.goods.GoodsBrandResult;
import com.framework.loippi.result.goods.GoodsCategoriesTreeResult;
import com.framework.loippi.service.product.ShopGoodsBrandService;
import com.framework.loippi.service.product.ShopGoodsClassService;
import com.framework.loippi.service.product.ShopGoodsService;
import com.framework.loippi.utils.ApiUtils;
import com.framework.loippi.utils.Paramap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.*;

/**
 * 品牌
 * Created by longbh on 2017/8/12.
 */
@Controller
public class BrandClassController extends BaseController {
    @Autowired
    private ShopGoodsBrandService shopGoodsBrandService;
    @Autowired
    private ShopGoodsClassService shopGoodsClassService;

    /**
     * 通过关键字搜索品牌
     * //查看所有品牌并排序
     */
    @ResponseBody
    @RequestMapping("/api/index/brandsKey")
    public String brandKeywordList(String keyword) {
        List<ShopGoodsBrand> list = shopGoodsBrandService.findList(Paramap.create().put("brandNameLike", keyword));
        return ApiUtils.success(GoodsBrandResult.build(list, prefix));
    }

    /**
     * 品牌详情
     *
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping("/api/index/brandsDetail")
    public String brandsDetail(Long id) {
        ShopGoodsBrand brand = shopGoodsBrandService.find(id);
        if(brand == null){
            return ApiUtils.error("品牌不存在");
        }
        return ApiUtils.success(GoodsBrandDetail.of(brand, prefix));
    }

    /**
     * 树结构分类,一次性返回
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/api/index/categoriesTree.json")
    public String categoryTree() {
        List<GoodsCategoriesTreeResult> allResult = new ArrayList<>();
        //封装分类树
        List<ShopGoodsClass> root = shopGoodsClassService.findByParams2(Paramap.create().put("gcParentId", 0).put("gcShow", 1).put("gcShow", 1));
        if (root != null && root.size() > 0) {
            List<ShopGoodsClass> deepByTwo = shopGoodsClassService.findByParams2(Paramap.create().put("deep", 2).put("gcShow", 1).put("gcShow", 1)); //二级分类
            Map<Long, ShopGoodsClass> classMap = new HashMap<Long, ShopGoodsClass>();
            for (ShopGoodsClass item : root) {
                if (classMap.get(item.getId()) == null) classMap.put(item.getId(), item);
            }
            //把二级分类赋值到列表中
            for (ShopGoodsClass item : deepByTwo) {
                if (classMap.get(item.getGcParentId()) != null) {
                    List<ShopGoodsClass> list = classMap.get(item.getGcParentId()).getClassList();//一级分类的
                    if (list == null) {
                        list = new ArrayList<>();
                        classMap.get(item.getGcParentId()).setClassList(list);
                    }
                    list.add(item);
                }
            }
            List<GoodsCategoriesTreeResult> treeResult = GoodsCategoriesTreeResult.build(root, prefix);
            for (GoodsCategoriesTreeResult item : treeResult) {
                allResult.add(item);
            }
        }
        return ApiUtils.success(allResult);
    }

    /**
     * 首页以及商家商品分类(单层)
     *
     * @param categoryId 对应顶部tab
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/api/index/categories.json", method = RequestMethod.POST)
    public String brandCategory(Long categoryId) {  //父分类id查询品牌
        if (categoryId == null || categoryId == 0) {
            List<ShopGoodsClass> all = shopGoodsClassService.findByParams2(Paramap.create()
                    .put("gcParentId", 0).put("gcShow", 1).put("gcSort", 1));
            return ApiUtils.success(GoodsCategoriesResult.build(all, prefix));
        } else {
            List<ShopGoodsClass> list = shopGoodsClassService.findByParams2(Paramap.create()
                    .put("gcParentId", categoryId).put("gcShow", 1).put("gcSort", 1));
            return ApiUtils.success(GoodsCategoriesResult.build(list, prefix));
        }
    }

}
