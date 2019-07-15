package com.framework.loippi.vo.goods;

import com.google.common.collect.Lists;

import com.framework.loippi.mybatis.eitity.GenericEntity;

import java.util.List;

import lombok.Data;
import lombok.ToString;

/**
 * shop_goods_class 商品分类
 *
 * @项目名称：leimingtech-entity
 * @类名称：class
 * @类描述：
 * @修改备注：
 */
@Data
@ToString
public class GoodsClassh5 implements GenericEntity {
    /**
     * 主键ID
     */
    private String id;
    /**
     * 地区名称
     */
    private String name;
    /**
     * 当前分类的下级地区集合
     */
    private List<GoodsClassh5> child = Lists.newArrayList();
}
