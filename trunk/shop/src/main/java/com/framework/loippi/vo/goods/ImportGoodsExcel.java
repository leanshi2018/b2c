package com.framework.loippi.vo.goods;


import com.framework.loippi.mybatis.ext.annotation.Column;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 2015年06月29日15:49:59
 * @author cgl
 * 这个实体类不用于存数据
 */
@Data
@ToString
public class ImportGoodsExcel {
    /**
     * 热卖推荐  1推荐  0不推荐
     */

    private Integer bestSellersCommend;

    /**
     * 商品推荐  （首页推荐）  1推荐 0不推荐
     */

    private String goodsCommend;

    /**
     * 是否开启参与拼团活动   1开启  0关闭
     */

    private Integer fightGroups;

    /**
     * 是否开启参与优惠券  1开启  0关闭
     */

    private Integer coupon;

    /**
     * 是否开启水印   1开启 0关闭
     */

    private Integer waterMark;

    /**
     * 是否开启参与秒杀活动  1开启  0关闭
     */

    private String seckill;

    /**
     * 是否APP端显示  1显示  0关闭
     */

    private String isAppShow;

    /**
     * 是否微信端显示  1显示 0关闭
     */

    private String isWeixinShow;

    /**
     * 是否开启 返佣  1开启 0关闭
     */

    private Integer isRebate;



    /**
     * 商品属性  新品  热品   普通
     */

    private String goodsAttrs;

    /**
     * 商品名称
     */

    private String goodsName;

    /**
     * 商品副标题
     */

    private String goodsSubtitle;

    //计量名称

    private String measureName;

    /**
     * 保质期(过期时间)
     */

    private Integer shelfLifeTime;

    /**
     * 重量
     */

    private Integer weight;

    /**
     * 分类名称
     */
    private String className;
    /**
     * 规格
     */
    private String goodsSpec;

    /**
     * 编码
     */
    private String specGoodsSerial;


    /**
     * 条形码
     */
    @Column(name = "spec_bar_code")
    private String specBarCode;


    /**
     * 商品店铺价格  销售价格
     */
    private Integer goodsStorePrice;

    /**
     * 市场价
     */

    private Integer goodsMarketPrice;

    /**
     * 规格商品库存
     */
    @Column(name = "spec_goods_storage")
    private Integer specGoodsStorage;

    /**
     * 成本价
     */

    private Integer goodsCostPrice;
    /**
     * 最大使用红包值
     */

    private Integer redPacketUsedMax;

    /**
     * 最大使用钱包比率
     */

    private Integer walletUsedMax;

    /**
     * 税率
     */

    private Integer tax;
    /**
     * 税费说明
     */

    private String taxInfo;

    /**
     * 每人限购数
     */

    private Integer purchasemaxnum;

    /**
     * 最少购买数
     */

    private Integer purchaseminnum;

    /**
     * 折扣率
     */

    private Integer discount;

    /**
     * 返佣比率
     */

    private Integer rebate;

    /**
     * 排序
     */

    private Integer sort;

    /**
     * 商品品牌名称
     */

    private String brandName;
    /**
     * 商品品牌名称
     */

    private String brandCountry;

    /**
     * 分组名
     */

    private String  groupName;

    /** 规格参数 */

    private String goodsProperty;
    /**
     * 产品保证说明
     */

    private String explains;


    /**
     * 商品所在地(市)
     */

    private String cityName;

}
