package com.framework.loippi.result.goods;

import com.framework.loippi.entity.product.ShopGoodsClass;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result - 商品分类
 *
 * @author Loippi team
 * @version 2.0
 * @description 商品分类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsCategoriesTreeResult {

    //分类id
    private Long categoryId;
    //名称
    private String categoryName;
    //logo
    private String cateLogo;

    // private String clickLogo;
    // private String logoUrl;//广告图的跳转链接
    // private String advPic;
    //子节点
    private List<GoodsCategoriesTreeResult> childCategories = new ArrayList<>();
    // private List<GoodsBrandResult> brandList;

    public static GoodsCategoriesTreeResult of(ShopGoodsClass catList, String prefix) {
        GoodsCategoriesTreeResult result = new GoodsCategoriesTreeResult();
        result.setCategoryId(catList.getId());
        result.setCategoryName(catList.getGcName());
        result.setCateLogo(prefix + catList.getGcPic());
        //result.setLogoUrl(catList.getAdvUrl());
        // result.setAdvPic(catList.getAdvPic());
        //result.setChildCategories(build(catList.getClassList(), prefix));
        return result;
    }

    public static List<GoodsCategoriesTreeResult> build(List<ShopGoodsClass> catList, String prefix) {
        if (CollectionUtils.isEmpty(catList)) {
            return Collections.emptyList();
        }
        List<GoodsCategoriesTreeResult> results = new ArrayList<GoodsCategoriesTreeResult>();
        for (ShopGoodsClass cat : catList) {
            GoodsCategoriesTreeResult result = new GoodsCategoriesTreeResult();
            result.setCategoryId(Optional.ofNullable(cat.getId()).orElse(-1L));
            result.setCategoryName(Optional.ofNullable(cat.getGcName()).orElse(""));
            result.setCateLogo(Optional.ofNullable(cat.getGcPic()).orElse(""));
            result.setChildCategories(build(cat.getClassList(), prefix));
            results.add(result);
        }
        return results;
    }

}
