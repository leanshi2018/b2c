package com.framework.loippi.result.goods;

import com.framework.loippi.entity.product.ShopGoodsBrand;
import com.framework.loippi.utils.StringUtil;
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
public class GoodsBrandDetail {


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

    //品牌轮播图
    private List<String> brandAdvert;

    //品牌星ji级
    private Integer starts;

    public static GoodsBrandDetail of(ShopGoodsBrand brand, String prefix) {
        GoodsBrandDetail result = new GoodsBrandDetail();
        result.setBrandId(Optional.ofNullable(brand.getId()).orElse(-1L));
        result.setBrandLogo(Optional.ofNullable(prefix + brand.getBrandPic()).orElse(""));
        result.setBrandName(Optional.ofNullable(brand.getBrandName()).orElse(""));
        result.setStarts(Optional.ofNullable(brand.getStars()).orElse(0));
        String advertImage = brand.getAdvertImage();
        List<String> brandAdvert = new ArrayList<>();
        if (!StringUtil.isEmpty(advertImage)) {
            String[] advertArray = advertImage.split(",");
            for (String item : advertArray) {
                brandAdvert.add(item);
            }
        }
        result.setBrandAdvert(brandAdvert);
        return result;
    }
}

