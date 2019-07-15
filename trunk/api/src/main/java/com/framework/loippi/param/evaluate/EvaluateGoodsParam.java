package com.framework.loippi.param.evaluate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 评论参数
 * Created by longbh on 2018/11/13.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluateGoodsParam {

    //对应订单商品id
    private Long orderGoodsId;
    //图片用逗号分隔
    private String img;
    //内容
    private String comment;
    //评分
    private Integer goodsScore;
    //是否匿名 0表示不是 1表示是匿名评价
    private Integer isAnonymous;

}
