package com.framework.loippi.vo;

import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.mybatis.eitity.GenericEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ObjectUnionVO implements GenericEntity {

    private String objectType;

    private Long objectId;

    private BigDecimal price;

    /**
     * 活动id
     * 开始字段属性-----------------------开始
     */
    private Long activityId;

    /**
     * 活动名称
     */
    private String activityName;

    /**
     * 当前活动时间
     */
    private Date activityTime;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 限制店铺
     */
    private Integer limitStore;

    /**
     * 店铺id
     */
    private Long storeId;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 审核状态
     */
    private Integer auditStatus;

    /**
     * 活动状态
     */
    private Integer activityStatus;
    /**
     * 活动图片
     */
    private String activityPicture;

    /**
     * 活动图片  --pc端
     */
    private String activityPicturePc;

    /**
     * 活动类型
     */
    private Integer activityType;

    /**
     * 规格id
     */
    private Long goodsSpecId;
    /**
     * 商品规格json序列化
     */
    private String specGoodsSpec;

    /**
     * 规格对应的价格
     */
    private BigDecimal specGoodsPrice;

    /**
     * 市场价格
     */
    private BigDecimal specMarketPrice;
    /**
     * 商品图片
     */
    private String goodsImage;
    /**
     * 商品名称
     */
    private String goodsName;
    /**
     * 商品id
     */
    private Long goodsId;
    /**
     * 商品价格
     */
    private BigDecimal goodsStorePrice;

    /**
     * 卖出数量
     */
    private Integer salenum;


    /**
     * 预销售的商品数量
     */
    private Integer factSaleNumber;

    /**
     * 已购买数量
     */
    private Integer alreadyBuyNumber;

    /**
     * 限购数量
     */
    private Integer restrictionNum;

    /**
     * 活动分类
     * 团购／限时抢购等
     */
    private String activityClass;

    /**
     * 参与的活动的商品库存数量
     */
    private Integer stockNumber;

    /**
     * 商品所属分组
     */
    private Long groupId;

    /**
     * 商品所属分组
     */
//	private Long groupIdQuery;

    /**
     * 限制人数
     */
    private Integer menNumber;

    /**
     * 商品库存
     */
    private Integer specGoodsStorage;

    /**
     * 排除的活动id
     */
    private Long excludeActivityId;

    /**
     * 当天活动开始时间
     */
    private Date todayStartTime;

    /**
     * 是否被关注提醒  1已关注提醒   2未关注提醒
     */
    private Long remindMsgId;

    /**
     * 规格主图
     */
    private String mainPicture;

    /**
     * 规格列表图
     */
    private String listPicture;

    /**
     * 是否APP端显示  1显示  0关闭
     */
    private Integer isAppShow;
    /**
     * 是否微信端显示  1显示 0关闭
     */
    private Integer isWeixinShow;

    private Integer isRecommend;

    /**
     * 已成功团购的数目
     */
    private Integer groupNum;

    /**
     * 商品分类id
     */
    private Long screeningsId;

    /**
     * 活动销售数
     */
    private Integer saleNumber;


    public void addShopGoodProperty(ShopGoods goods) {
        //base数量
        int sale = goods.getSalenum() != null ? goods.getSalenum().intValue() : 0;
        sale += this.getAlreadyBuyNumber().intValue();
        goods.setSalenum(sale);
        int tockNum = this.getStockNumber() != null ? this.getStockNumber().intValue() : 0;
        tockNum += this.getAlreadyBuyNumber().intValue();
        this.setStockNumber(tockNum);
    }


}
