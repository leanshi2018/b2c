package com.framework.loippi.result.goods;

import com.framework.loippi.entity.product.ShopGoodsBrand;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Crease小白 on 2017/6/19.
 */

@Setter
@Getter
public class GoodsBrandResult {


    /**
     * 索引
     */
    private Long brandId;

    /**
     * 品牌名称
     */
    private String brandName;


    /**
     * 图片
     */
    private String brandLogo;


    public static List<GoodsBrandResult> build(List<ShopGoodsBrand> list, String prefix) {
        List<GoodsBrandResult> results = new ArrayList<>();
        for (ShopGoodsBrand brand : list) {
            GoodsBrandResult result = new GoodsBrandResult();
            // result.setBrandNum(Optional.ofNullable( brand.getBrandNum()).orElse(0L));
            result.setBrandId(Optional.ofNullable(brand.getId()).orElse(-1L));
            result.setBrandLogo(Optional.ofNullable(prefix + brand.getBrandPic()).orElse(""));
            result.setBrandName(Optional.ofNullable(brand.getBrandName()).orElse(""));
            results.add(result);
        }
        return results;
    }

    public static GoodsBrandResult of(ShopGoodsBrand brand, String prefix) {
        GoodsBrandResult result = new GoodsBrandResult();
        result.setBrandId(Optional.ofNullable(brand.getId()).orElse(-1L));
        result.setBrandLogo(Optional.ofNullable(prefix + brand.getBrandPic()).orElse(""));
        result.setBrandName(Optional.ofNullable(brand.getBrandName()).orElse(""));
        return result;
    }
}

