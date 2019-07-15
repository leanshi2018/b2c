package com.framework.loippi.entity.activity;

import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;
import com.framework.loippi.utils.StringUtil;
import com.framework.loippi.vo.goods.GoodsSpecVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Entity - 活动商品规格
 *
 * @author longbh
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_ACTIVITY_GOODS")
public class ShopActivityGoods implements GenericEntity {

    private static final long serialVersionUID = 5081846432919091193L;

    /**
     * 活动商品id
     */
    @Column(id = true, name = "ID", updatable = false)
    private Long id;

    /**
     * 活动id
     */
    @Column(name = "activity_id")
    private Long activityId;

    //活动类型
    @Column(name = "activity_type")
    private Integer activityType;

    /**
     * 对象类型 1-商品
     */
    @Column(name = "OBJECT_TYPE")
    private Integer objectType;

    /**
     * 对象id
     */
    @Column(name = "object_id")
    private Long objectId;

    /**
     * 规格活动价格
     */
    @Column(name = "price")
    private BigDecimal price;

    /**
     * 参与的活动的商品库存数量
     */
    @Column(name = "stock_number")
    private Long stockNumber;

    /**
     * 销售数量
     */
    @Column(name = "sale_number")
    private Integer saleNumber;

    /**
     * 商品主图
     */
    @Column(name = "main_picture")
    private String mainPicture;

    /**
     * 排序字段
     */
    @Column(name = "sort")
    private Integer sort;

    /**
     * 1-待审核 2-已通过  3-已拒绝
     */
    @Column(name = "status")
    private Integer status;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    private Date startTime;
    private Date endTime;
    private Integer activityStatus;
    private String activityName;
    private String goodsName;

    //商品信息
    private String goodsImage;
    //规格
    List<ShopActivityGoodsSpec> shopActivityGoodsSpecList;
    //用于输出后台规格列表
    List<String> specKeyList;
    //规格值
    Map<String, List<GoodsSpecVo>> specMap;
    //规格名称
    Map<String, Object> specNameMap;


    //零售价
    private java.math.BigDecimal goodsRetailPrice;

    //会员价格
    private java.math.BigDecimal goodsMemberPrice;

    //大单价
    private java.math.BigDecimal goodsBigPrice;

    //ppv
    private BigDecimal ppv;

    //大单pv
    private BigDecimal bigPpv;

    public void addShopGoodProperty(ShopGoods goods) {
        //base数量
        goods.setSalenum(saleNumber);
        this.setGoodsName(goods.getGoodsName());
        if (StringUtil.isEmpty(this.getGoodsImage())) {
            this.setGoodsImage(goods.getGoodsImage());
        }
        this.setGoodsRetailPrice(goods.getGoodsRetailPrice());
        this.setGoodsBigPrice(goods.getGoodsBigPrice());
        this.setGoodsMemberPrice(goods.getGoodsMemberPrice());
        this.setBigPpv(goods.getBigPpv());
        this.setPpv(goods.getPpv());
        this.setStockNumber(stockNumber);
    }

}
