package com.framework.loippi.result.common.goods;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;

import com.framework.loippi.entity.product.ShopGoodsClass;

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
public class GoodsCategoriesResult {

    /**
     * 分类id
     */
    private Long categoryId;
    /**
     * 名称
     */
    private String categoryName;
    //private Integer goodSize = 0;
    private String cateDesc;
    /**
     * logo
     */
    private String cateLogo;
    /**
     * logo灰色
     */
    private String cateLogoGray;
   // private String clickLogo;
    private List<GoodsCategoriesResult> childCategories = new ArrayList<>();
    //private List<GoodsBrandResult> brandList;

    public static List<GoodsCategoriesResult> build(List<ShopGoodsClass> catList, String prefix) {
        if (CollectionUtils.isEmpty(catList)) {
            return Collections.emptyList();
        }
        List<GoodsCategoriesResult> results = new ArrayList<GoodsCategoriesResult>();
        for (ShopGoodsClass cat : catList) {
            GoodsCategoriesResult result = new GoodsCategoriesResult();
            result.setCategoryId(Optional.ofNullable(  cat.getId()).orElse(-1L));
            result.setCateDesc(Optional.ofNullable( cat.getGcDescription()).orElse(""));
            result.setCategoryName(Optional.ofNullable( cat.getGcName()).orElse(""));
            result.setCateLogo(Optional.ofNullable( prefix + cat.getGcPic()).orElse(""));
            result.setCateLogoGray(Optional.ofNullable( prefix + cat.getGcPicGray()).orElse(""));
//            if (cat.getBrandList() != null && cat.getBrandList().size() > 0) {
//                result.setBrandList(GoodsBrandResult.build(cat.getBrandList(), prefix));
//            }
            results.add(result);
        }
        return results;
    }
}

