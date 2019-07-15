package com.framework.loippi.entity.product;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity - 商品评价点赞表
 *
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_GOODS_EVALUATE_LIKE")
public class ShopGoodsEvaluateLike implements GenericEntity {

    private static final long serialVersionUID = 5081846432919091193L;

    /**
     * id
     */
    @Column(id = true, name = "id", updatable = false)
    private Long id;

    /**
     * 商品评价id
     */
    @Column(name = "geval_id")
    private Long gevalId;

    /**
     * 用户id
     */
    @Column(name = "member_id")
    private Long memberId;

    /**
     * 0:取消点赞 1:点赞
     */
    @Column(name = "like_type")
    private Integer likeType;

}
