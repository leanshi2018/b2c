package com.framework.loippi.entity.product;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity - 商品分类表
 *
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_GOODS_CLASS")
public class ShopGoodsClass implements GenericEntity {

    private static final long serialVersionUID = 5081846432919091193L;

    /**
     * 索引ID
     */
    @Column(id = true, name = "id", updatable = false)
    private Long id;

    /**
     * 分类名称
     */
    @Column(name = "gc_name")
    private String gcName;

    /**
     * 分类图片
     */
    @Column(name = "gc_pic")
    private String gcPic;

    /**
     * 类型id
     */
    @Column(name = "type_id")
    private Long typeId;

    /**
     * 类型名称
     */
    @Column(name = "type_name")
    private String typeName;

    /**
     * 父ID
     */
    @Column(name = "gc_parent_id")
    private Long gcParentId;

    /**
     * 排序
     */
    @Column(name = "gc_sort")
    private Integer gcSort;

    /**
     * 前台显示，0为否，1为是，默认为1
     */
    @Column(name = "gc_show")
    private Integer gcShow ;

    /**
     * 名称
     */
    @Column(name = "gc_title")
    private String gcTitle;

    /**
     * 关键词
     */
    @Column(name = "gc_keywords")
    private String gcKeywords;

    /**
     * 描述
     */
    @Column(name = "gc_description")
    private String gcDescription;

    /**
     * 层级path
     */
    @Column(name = "gc_idpath")
    private String gcIdpath;

    /**
     * 费用比例
     */
    @Column(name = "expen_scale")
    private BigDecimal expenScale;

    /**
     * 是否关联子分类 0否, 1是
     */
    @Column(name = "is_relate")
    private Integer isRelate;

    /**
     * 虚拟商品
     */
    @Column(name = "virtual_goods")
    private Integer virtualGoods;

    /**
     * 关联子分类
     */
    @Column(name = "relation")
    private Integer relation;

    /**
     * 推荐，0为否，1为是，默认为0
     */
    @Column(name = "is_commend")
    private Integer isCommend;
    //广告组Id
    @Column(name = "adv_id")
    private Long advId;
    /**
     * 图片URL
     */
    @Column(name = "picUrl")
    private String picUrl;

    /**
     * 深度   1级  2级  3级
     */
    @Column(name = "deep")
    private Integer deep;

    /*********************添加*********************/
    /**
     * 分类下的品牌
     */
    private List<ShopGoodsBrand> brandList;
    private List<ShopGoodsClass> listByChild;
    private List<ShopGoodsClass> classList;
    private List<Long> classIds;
    private List<Long> classParentIds;
    private String stingid;
    private String stringParentId;
    private Long[] typeIds;
    private int hasChild;
    private String classNameLike;
    //广告位图片
    private String advPic;
    //广告位Url
    private String advUrl;

}
