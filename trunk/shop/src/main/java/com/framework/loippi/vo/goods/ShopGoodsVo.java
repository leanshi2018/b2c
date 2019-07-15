package com.framework.loippi.vo.goods;

import com.framework.loippi.entity.product.ShopGoods;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by lys on 2017/10/17.
 */
@Data
@ToString
public class ShopGoodsVo extends ShopGoods {
    //查询条件
    public BigDecimal minPrice;
    public BigDecimal maxPrice;
    public List<Long> classList;
    public List<Long> brandList;
    public List<Long> sgsList;
    //商品ID集合
    private List<Long> idsList;
    public BigDecimal activityPrice;
    public BigDecimal orderPrice;
    public Long activityId;
    public String activityType;
    public Long memberId;
    //商品规格id集合
    private List<Long> specIdsList;


}
