package com.framework.loippi.entity.product;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * Entity - 品牌表
 *
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_GOODS_BRAND")
public class ShopGoodsBrand implements GenericEntity {

    private static final long serialVersionUID = 5081846432919091193L;

    /**
     * 索引ID
     */
    @Column(id = true, name = "id", updatable = false)
    private Long id;

    /**
     * 品牌名称
     */
    @Column(name = "brand_name")
    private String brandName;

    /**
     * 类别名称
     */
    @Column(name = "brand_class")
    private String brandClass;

    /**
     * 图片
     */
    @Column(name = "brand_pic")
    private String brandPic;

    /**
     * 排序
     */
    @Column(name = "brand_sort")
    private Integer brandSort;

    /**
     * 推荐，0为否，1为是，默认为0
     */
    @Column(name = "brand_recommend")
    private Integer brandRecommend;

    /**
     * 店铺ID
     */
    @Column(name = "store_id")
    private Long storeId;

    /**
     * 品牌申请，0为申请中，1为通过，默认为1，申请功能是会员使用，系统后台默认为1
     */
    @Column(name = "brand_apply")
    private Integer brandApply;

    /**
     * 所属分类id
     */
    @Column(name = "class_id")
    private Long classId;

    /**
     * 是否删除0:未删除;1:已删除
     */
    @Column(name = "is_del")
    private Integer isDel;

    /**
     * 拼音
     */
    @Column(name = "pinyin")
    private String pinyin;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 品牌图片
     */
    @Column(name = "advert_image")
    private String advertImage;

    /**
     * 星级
     */
    @Column(name = "stars")
    private Integer stars;

    /**
     * 描述
     */
    @Column(name = "description")
    private String description;

    /*********************添加*********************/
    /**
     * 品牌在售商品数量
     */
    private Long brandNum = 0l;
    //查询 条件
    private String brandKeywords;
    private String brandNameLike;
    private String classNameLike;
    private Long combinationIndex;

    /**
     * 类型id
     */
    private Long typeId;
    private int brandCount;
    private List<Long> classIds;
    private List<Long> brandIds;
    private List<String> advertImageList;


}
