package com.framework.loippi.result.common.goods;

import com.framework.loippi.entity.product.ShopGoodsBrand;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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
     * 类别名称
     */
    private String brandClass;


    /**
     * 图片
     */
    private String brandLogo;


    /**
     * 排序
     */
    private Integer brandSort;


    /**
     * 推荐，0为否，1为是，默认为0
     */
    private Integer brandRecommend;


    /**
     * 所属分类id
     */
    private Long classId;

    private String className;

    /**
     * 品牌在售商品数量
     */
    private Long brandNum;

    public static List<GoodsBrandResult> build(List<ShopGoodsBrand> list, String prefix) {
        List<GoodsBrandResult> results = new ArrayList<>();
        for (ShopGoodsBrand brand : list) {
            GoodsBrandResult result = new GoodsBrandResult();
            result.setClassId(brand.getClassId());
            result.setBrandNum(brand.getBrandNum());
            result.setClassName(brand.getBrandName());
            result.setBrandId(brand.getId());
            result.setBrandLogo(prefix + brand.getBrandPic());
            result.setBrandName(brand.getBrandName());
            results.add(result);
        }
        return results;
    }
}

