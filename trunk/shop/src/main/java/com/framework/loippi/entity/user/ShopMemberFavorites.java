package com.framework.loippi.entity.user;

import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Entity - 买家收藏表
 *
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_MEMBER_FAVORITES")
public class ShopMemberFavorites implements GenericEntity {

    private static final long serialVersionUID = 5081846432919091193L;

    /**  */
    @Column(name = "id")
    private Long id;

    /**
     * 收藏ID
     */
    @Column(name = "fav_id")
    private Long favId;

    /**
     * 会员ID
     */
    @Column(name = "member_id")
    private Long memberId;

    /**
     * 收藏类型 1-收藏商品，2-收藏店铺
     */
    @Column(name = "fav_type")
    private Integer favType;

    /**
     * 收藏时间
     */
    @Column(name = "fav_time")
    private Date favTime;
    /**
     * 活动
     */
    @Column(name = "activityId")
    private Long activityId;

    /**
     * 活动规格
     */
    @Column(name = "specId")
    private Long specId;

    /**
     * 活动类型
     */
    @Column(name = "activitType")
    private Integer activitType;
    // 商品
    private ShopGoods goods;
    //收藏商品long集合
    private List<Long> favIds;
    // id集合
    private List<Long> ids;
}
