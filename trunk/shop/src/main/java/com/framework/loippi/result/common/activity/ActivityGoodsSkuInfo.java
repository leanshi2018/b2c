package com.framework.loippi.result.common.activity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.framework.loippi.entity.activity.ShopActivityGoodsSpec;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsSpec;
import com.framework.loippi.utils.GoodsUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityGoodsSkuInfo {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long skuId;

    //活動商品id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long activityGoodsId;

    //零售价
    private String goodsRetailPrice;

    //会员价格
    private String goodsMemberPrice;

    //ppv
    private BigDecimal ppv;

    //规格信息
    private String specInfo;

    //商品库存
    private Integer stock;

    //规格组合
    private String specValueIDS;

    //活动sku
    @JsonSerialize(using = ToStringSerializer.class)
    private Long activitySkuId;

    //图片
    private String image;

    public static ActivityGoodsSkuInfo of(ShopGoodsSpec sku,ShopGoods shopGoods) {
        ActivityGoodsSkuInfo skuInfo = new ActivityGoodsSkuInfo();
        // 市场价不受活动影响
        skuInfo.setGoodsMemberPrice(sku.getSpecMemberPrice().setScale(2, BigDecimal.ROUND_HALF_DOWN).toString());
        skuInfo.setGoodsRetailPrice(sku.getSpecRetailPrice().setScale(2, BigDecimal.ROUND_HALF_DOWN).toString());
        skuInfo.setPpv(sku.getPpv());
        skuInfo.setSkuId(sku.getId());
        skuInfo.setSpecInfo(sku.getSpecGoodsSpec());
        if (shopGoods.getGoodsType()!=3){
            skuInfo.setSpecValueIDS(GoodsUtils.getThisGoodsAllSpecValueId(sku.getSpecGoodsSpec()));
        }else{
            skuInfo.setSpecValueIDS(sku.getId()+"");
        }

        if (Optional.ofNullable(sku.getSpecIsopen()).orElse(0)==0){
            skuInfo.setStock(0);
        }else{
            skuInfo.setStock(sku.getSpecGoodsStorage());
        }
        skuInfo.setImage(shopGoods.getGoodsImage());
        return skuInfo;
    }

    public static List<ActivityGoodsSkuInfo> forList(List<ShopGoodsSpec> skus,ShopGoods shopGoods) {
        List<ActivityGoodsSkuInfo> list = new ArrayList<ActivityGoodsSkuInfo>();
        for (ShopGoodsSpec sku : skus) {
            list.add(ActivityGoodsSkuInfo.of(sku,shopGoods));
        }
        return list;
    }

    public static ActivityGoodsSkuInfo of(ShopGoodsSpec sku, ShopActivityGoodsSpec shopActivityGoodsSpec,ShopGoods shopGoods) {
        ActivityGoodsSkuInfo skuInfo = new ActivityGoodsSkuInfo();
        // 市场价不受活动影响
        skuInfo.setGoodsMemberPrice(sku.getSpecMemberPrice().setScale(2, BigDecimal.ROUND_HALF_DOWN).toString());
        skuInfo.setGoodsRetailPrice(sku.getSpecRetailPrice().setScale(2, BigDecimal.ROUND_HALF_DOWN).toString());
        skuInfo.setPpv(sku.getPpv());

        skuInfo.setSkuId(sku.getId());
        skuInfo.setSpecInfo(sku.getSpecGoodsSpec());
        if (shopGoods.getGoodsType()!=3){
            skuInfo.setSpecValueIDS(GoodsUtils.getThisGoodsAllSpecValueId(sku.getSpecGoodsSpec()));
        }else{
            skuInfo.setSpecValueIDS(sku.getId()+"");
        }

        if (Optional.ofNullable(sku.getSpecIsopen()).orElse(0)==0){
            skuInfo.setStock(0);
        }else{
            skuInfo.setStock(shopActivityGoodsSpec.getActivityStock());
        }
        skuInfo.setImage(shopGoods.getGoodsImage());
        //活动库存
        if (shopActivityGoodsSpec != null) {
            skuInfo.setActivitySkuId(shopActivityGoodsSpec.getSpecId());
            skuInfo.setActivityGoodsId(shopActivityGoodsSpec.getActivityGoodsId());
            skuInfo.setGoodsRetailPrice(shopActivityGoodsSpec.getActivityPrice().setScale(2, RoundingMode.HALF_DOWN).toString());
        }
        return skuInfo;
    }

    public static List<ActivityGoodsSkuInfo> forList(List<ShopGoodsSpec> skus, Map<Long, ShopActivityGoodsSpec> shopActivityGoodsSpecMap,ShopGoods shopGoods) {
        List<ActivityGoodsSkuInfo> list = new ArrayList<ActivityGoodsSkuInfo>();
        for (ShopGoodsSpec sku : skus) {
            list.add(ActivityGoodsSkuInfo.of(sku, shopActivityGoodsSpecMap.get(sku.getId()), shopGoods));
        }
        return list;
    }

}
