package com.framework.loippi.vo.evaluate;

import lombok.Data;
import lombok.ToString;

/**
 * 商品评论api实体
 *
 * @author liukai
 */
@Data
@ToString
public class EvaluateGoodsApiBean {

    /**
     * 评价ID
     */
    private String gevalId;

    /**
     * 评价时间
     */
    private Long gevalAddTime;

    /**
     * 评价人名称
     */
    private String gevalFrommembername;

    /**
     * 评价人头像
     */
    private String gevalFrommemberAvatar;

    /**
     * 晒单图片
     */
    private String gevalImage;

    /**
     * 是不是匿名评价  1表示是匿名评价
     */
    private Integer gevalIsAnonymous;

    /**
     * 信誉评价内容
     */
    private String gevalContent;

    /**
     * 1-5分
     */
    private Integer gevalScore;

    /**
     * 商品规格
     */
    private String specInfo;

    /**
     * 购买时间
     */
    private Long orderAddTime;

}
