package com.framework.loippi.result.sys;

import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsSpec;
import com.framework.loippi.mybatis.ext.annotation.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by longbh on 2019/1/4.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsStocksListView {
    /**
     * 商品规格索引id
     */
    private Long specId;
    /**
     * 商品索引id
     */
    private Long goodsId;

    /**
     * 商品名称
     */
    private String goodsName;
    /**
     * 商品类型 1-普通2-积分3-组合
     */
    private Integer goodsType;
    /**
     * 商品分类id
     */
    private Long gcId;

    /**
     * 商品分类名称
     */
    private String gcName;

    /**
     * 商品父类分类名称
     */
    private String gcParentName;

    /**
     * 商品品牌id
     */
    private Long brandId;

    /**  */
    private String brandName;
    /**
     * 商品上架1上架0下架
     */
    private Integer goodsShow;
    /**
     * 销售对象主键
     */
    private String salePopulationIds;
    /**
     * 销售对象名称
     */
    private String salePopulationName;


    /**
     * 规格名称
     */
    private String specName;

    /**
     * 规格商品库存
     */
    private Integer specGoodsStorage;

    /**
     * 售出数量
     */
    private Integer specSalenum;


    /**
     * 规格商品编号SKU
     */
    private String specGoodsSerial;

    /**
     * 商品规格序列化
     */
    private String specGoodsSpec;

    /**
     * 是否开启规格,1:开启，0:关闭 3修改后不用的
     */
    private Integer specIsopen;

    /**
     * 保质期
     */
    private Integer shelfLife;

    /** 更新时间 */
    private java.util.Date updateTime;

    public static List<GoodsStocksListView> of(ShopGoodsSpec shopGoodsSpec) {
        List<GoodsStocksListView> goodsStocksListDtoList=new ArrayList<>();

        return goodsStocksListDtoList;
    }

}
