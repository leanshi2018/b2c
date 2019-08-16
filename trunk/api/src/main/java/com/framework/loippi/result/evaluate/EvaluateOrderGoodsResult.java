package com.framework.loippi.result.evaluate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.framework.loippi.entity.order.ShopOrder;
import com.framework.loippi.entity.order.ShopOrderGoods;
import com.framework.loippi.entity.product.ShopGoodsEvaluate;

/**
 * 用户订单待评价的商品列表返回app结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluateOrderGoodsResult {
      //订单商品表id
    private Long gevalOrdergoodsid;
    //订单商品表id
    private Long gevalGoodsid;
    /**
     * 商品名称
     */
    private String gevalGoodsname;
    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 商品价格
     */
    private BigDecimal gevalGoodsprice;
    /**
     * 商品数量
     */
    private Integer goodsNum;
    /**
     * 商品图片
     */
    private String goodsImage;
    /**
     * 商品规格信息
     */
    private String specInfo;

    /**
     * pv值
     */
    private BigDecimal ppv;
    /**
     * 评价内容
     */
    private ShopGoodsEvaluate shopGoodsEvaluate;
    /**
     * 是否已评价
     */
    private int typeStatus;


    public static List<EvaluateOrderGoodsResult> build(List<ShopOrderGoods> shopOrderGoodsList,ShopOrder shopOrder ,List<ShopGoodsEvaluate> evaluateList) {
        List<EvaluateOrderGoodsResult> results = new ArrayList<>();
        if(shopOrderGoodsList!=null && shopOrderGoodsList.size()>0){
            for (ShopOrderGoods item:shopOrderGoodsList) {
                EvaluateOrderGoodsResult evaluateOrderGoodsResult=new EvaluateOrderGoodsResult();
                evaluateOrderGoodsResult.setGevalOrdergoodsid(item.getId());
                evaluateOrderGoodsResult.setGevalGoodsid(item.getGoodsId());
                evaluateOrderGoodsResult.setGevalGoodsname(item.getGoodsName());
                evaluateOrderGoodsResult.setGoodsImage(item.getGoodsImage());
                evaluateOrderGoodsResult.setGevalGoodsprice(item.getGoodsPayPrice());
                evaluateOrderGoodsResult.setGoodsNum(item.getGoodsNum());
                evaluateOrderGoodsResult.setSpecInfo(item.getSpecInfo());
                evaluateOrderGoodsResult.setBrandName(shopOrder.getBrandName());
                if (shopOrder.getOrderType()==3){
                    evaluateOrderGoodsResult.setPpv(item.getBigPpv());
                }else{
                    evaluateOrderGoodsResult.setPpv(item.getPpv());
                }
                for (ShopGoodsEvaluate goodsEvaluate : evaluateList) {
                    if (goodsEvaluate.getGevalOrdergoodsid().longValue()==item.getId().longValue()){
                        evaluateOrderGoodsResult.setShopGoodsEvaluate(goodsEvaluate);
                        evaluateOrderGoodsResult.setTypeStatus(1);
                    }else {
                        evaluateOrderGoodsResult.setTypeStatus(0);
                    }
                }
                results.add(evaluateOrderGoodsResult);
            }
        }
        return results;
    }

}
