package com.framework.loippi.entity.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

/**
 * Entity - 商品表
 *
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_GOODS")
public class ShopGoods implements GenericEntity {

    private static final long serialVersionUID = 5081846432919091193L;

    /**
     * 商品索引id
     */
    @Column(id = true, name = "id", updatable = false)
    private Long id;

    /**
     * 商品库id
     */
    @Column(name = "goods_serial")
    private String goodsSerial;

    /**
     * 销售对象id
     */
    @Column(name = "member_ship")
    private Long memberShip;

    /**
     * 商品名称
     */
    @Column(name = "goods_name")
    private String goodsName;

    /**
     * 商品副标题
     */
    @Column(name = "goods_subtitle")
    private String goodsSubtitle;

    /**
     * 商品促销语
     */
    @Column(name = "goods_keywords")
    private String goodsKeywords;

    /**
     * 商品类型 1-普通2-积分3-组合
     */
    @Column(name = "goods_type")
    private Integer goodsType;

    /**
     * 商品分类id
     */
    @Column(name = "gc_id")
    private Long gcId;

    /**
     * 商品分类名称
     */
    @Column(name = "gc_name")
    private String gcName;

    /**
     * 商品品牌id
     */
    @Column(name = "brand_id")
    private Long brandId;

    /**  */
    @Column(name = "brand_name")
    private String brandName;

    /**
     * 类型id
     */
    @Column(name = "type_id")
    private Long typeId;

    /**
     * 商品规格开启状态，1开启，0关闭
     */
    @Column(name = "spec_open")
    private Integer specOpen;

    /**
     * 商品默认对应的规格id
     */
    @Column(name = "spec_id")
    private Long specId;

    /**
     * 规格名称
     */
    @Column(name = "spec_name")
    private String specName;

    /**
     * 商品默认封面图片
     */
    @Column(name = "goods_image")
    private String goodsImage;

    /**
     * 新版本商品默认封面图片
     */
    @Column(name = "goods_image_new")
    private String goodsImageNew;

    /**
     * 商品多图
     */
    @Column(name = "goods_image_more")
    private String goodsImageMore="";

    /**
     * 商品详情视频
     */
    @Column(name = "goods_video")
    private String goodsVideo;

    /**
     * 零售价
     */
    @Column(name = "goods_retail_price")
    private java.math.BigDecimal goodsRetailPrice;

    /**
     * 零售利润
     */
    @Column(name = "goods_retail_profit")
    private java.math.BigDecimal goodsRetailProfit;

    /**
     * 会员价格
     */
    @Column(name = "goods_member_price")
    private java.math.BigDecimal goodsMemberPrice;

    /**
     * 大单价
     */
    @Column(name = "goods_big_price")
    private java.math.BigDecimal goodsBigPrice;

    /**
     * ppv
     */
    @Column(name = "ppv")
    private BigDecimal ppv;

    /**
     * 大单pv
     */
    @Column(name = "big_ppv")
    private BigDecimal bigPpv;

    /**
     * 重量
     */
    @Column(name = "weight")
    private Double weight;
    /**
     * 保质期
     */
    @Column(name = "shelf_life")
    private Integer shelfLife;

    /**
     * 商品上架1上架0下架2定时上架
     */
    @Column(name = "goods_show")
    private Integer goodsShow;

    /**
     * 商品浏览数
     */
    @Column(name = "goods_click")
    private Integer goodsClick;

    /**
     * 商品状态，0开启，1违规下架
     */
    @Column(name = "goods_state")
    private Integer goodsState;

    /**  */
    @Column(name = "create_time")
    private java.util.Date createTime;

    /**
     * 商品详细内容,手机版
     */
    @Column(name = "mobile_body")
    private String mobileBody;

    /**
     * 商品规格
     */
    @Column(name = "goods_spec")
    private String goodsSpec;

    /**  */
    @Column(name = "update_time")
    private java.util.Date updateTime;

    /**
     * 运费模板ID，不使用运费模板值为0
     */
    @Column(name = "transport_id")
    private Long transportId;

    /**
     * 商品违规下架原因
     */
    @Column(name = "goods_close_reason")
    private String goodsCloseReason;

    /**
     * 评论次数
     */
    @Column(name = "commentnum")
    private Integer commentnum;

    /**
     * 售出数量
     */
    @Column(name = "salenum")
    private Integer salenum;

    /**
     * 商品收藏数量
     */
    @Column(name = "goods_collect")
    private Integer goodsCollect;

    /**
     * 是否删除 2:未删除  1:已删除
     */
    @Column(name = "is_del")
    private Integer isDel;

    /**
     * 商品库存数量
     */
    @Column(name = "stock")
    private Long stock;

    /**
     * 排序数字
     */
    @Column(name = "sort")
    private Integer sort;

    /**
     * 所属分类等级   1级  2级 3级
     */
    @Column(name = "gc_levle")
    private Integer gcLevle;

    /**
     * 发布时间
     */
    @Column(name = "release_time")
    private java.util.Date releaseTime;

    /**  */
    @Column(name = "details")
    private String details;

    /**
     * 好评率
     */
    @Column(name = "evaluateRate")
    private Double evaluaterate;

    /**
     * 审核状态:0审核中;1审核通过;2审核不通过
     */
    @Column(name = "state")
    private Integer state;
    /**
     * 销售对象主键
     */
    @Column(name = "sale_population_ids")
    private String salePopulationIds;
    /**
     * 销售对象名称
     */
    @Column(name = "sale_population_name")
    private String salePopulationName;

    /**
     * 成本价
     */
    @Column(name = "cost_price")
    private BigDecimal costPrice;

    /**
     * 欠货列表下架展示  0.隐藏  1.显示
     */
    @Column(name = "off_shelf_show")
    private Integer offShelfShow;

    /**
     * 是否为plus vip商品 0：不是 1：是
     */
    @Column(name = "plus_vip_type")
    private Integer plusVipType;

    /*###################扩展##############*/
    private String shopGoodsSpecs;
    private Integer listGroupGoodsNum;
    //查询评论时的条件
    private String showCommentNum;
    private String isDels;
    /*
    ###################查询字段##############
     */
    //库存
    private Integer goodsTotalStorage;
    private BigDecimal startPrice;
    private BigDecimal endPrice;
    private Object orderAll;
    private Long combinationIndex;
    private List<Long> ids;
    private List<Long> notIds;
    private List<Long> goodsIds;
    private String[] brandIds;
    private String[] classIds;
    private List<Long> sgsIds;
    private String searchStartTime;
    private String searchEndTime;
    private String salePopulation;
    private String keyWordBrandName;
    //规格字段
    private Long goodsSpecId;
    private String specBarCode;
    private String bianma;
    private String specGoodsSerial;
    private Integer isState;
    /**
     * 商品一级分类
     */
    private Long gcParentId;
    /**
     * 需要排序的字段
     */
    private String sortField;

    /**
     * 时间排序,降序desc,升序asc
     */
    private String orderBy;
    private String goodsListKeywords;
    private String gcParentName;
    private String goodsEvaluaterate;
    private String goodsSalenum;
    private List<Long> noBrandIds;
    private String noGoodsType;
    private String joinNum;

    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal lowMi;
    private String noExchange;

    //赠品规则Id
    private Long giftSpecId;
    //赠品规格
    private ShopGoodsSpec shopGoodsSpec;
}
