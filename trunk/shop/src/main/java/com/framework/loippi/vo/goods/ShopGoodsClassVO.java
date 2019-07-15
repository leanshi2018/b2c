package com.framework.loippi.vo.goods;

import com.framework.loippi.entity.product.ShopGoodsBrand;
import com.framework.loippi.entity.product.ShopGoodsClass;
import com.framework.loippi.mybatis.ext.annotation.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by lys on 2017/8/5.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopGoodsClassVO {
    /**
     * 索引ID
     */
    private String id;

    /**
     * 分类名称
     */
    private String gcName;

    /**
     * 分类图片
     */
    private String gcPic;

    /**
     * 类型id
     */
    private String typeId;

    /**
     * 类型名称
     */
    private String typeName;

    /**
     * 父ID
     */
    private String gcParentId;

    /**
     * 排序
     */
    private Integer gcSort;

    /**
     * 前台显示，0为否，1为是，默认为1
     */
    private Integer gcShow = 1;

    /**
     * 名称
     */
    private String gcTitle;

    /**
     * 关键词
     */
    private String gcKeywords;

    /**
     * 描述
     */
    private String gcDescription;

    /**
     * 层级path
     */
    private String gcIdpath;

    /**
     * 费用比例
     */
    private BigDecimal expenScale;

    /**
     * 是否关联子分类 0否, 1是
     */
    private Integer isRelate;

    /**
     * 虚拟商品
     */
    private Integer virtualGoods;

    /**
     * 关联子分类
     */
    private Integer relation;

    /**
     * 推荐，0为否，1为是，默认为0
     */
    private Integer isCommend;

    /*********************添加*********************/
    /**
     * 分类下的品牌
     */
    private List<ShopGoodsBrand> brandList;

    private List<ShopGoodsClass> classList;

    private Integer deep;

}
