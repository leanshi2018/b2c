package com.framework.loippi.result.goods;

import com.framework.loippi.vo.goods.SpecGoodsVo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Crease小白 on 2017/6/19.
 */

@Data
public class GoodsSpcResult {


    /**
     * 商品规格索引id
     */

    private String goodsSpecId;

    /**
     * 规格名称
     */

    private String specName;


    /**
     * 规格商品价格
     */

    private BigDecimal specGoodsPrice;


    /**
     * 规格商品库存
     */

    private Integer specGoodsStorage;

    /**
     * 规格商品颜色
     */

    private String specGoodsColor;

    public static List<GoodsSpcResult> build(List<SpecGoodsVo> goodsSpec2, String prefix) {
        List<GoodsSpcResult> results = new ArrayList<>();
        for (SpecGoodsVo goodsVo : goodsSpec2) {
            GoodsSpcResult result = new GoodsSpcResult();
            result.setGoodsSpecId(goodsVo.getGoodsSpecId());
            result.setSpecName(goodsVo.getSpecName());
            result.setSpecGoodsPrice(goodsVo.getSpecGoodsPrice());
            result.setSpecGoodsColor(prefix + goodsVo.getGoodsImage());
            result.setSpecGoodsStorage(goodsVo.getSpecGoodsStorage());
            results.add(result);
        }
        return results;
    }

}

