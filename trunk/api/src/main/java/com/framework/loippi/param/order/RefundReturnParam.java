package com.framework.loippi.param.order;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * Created by Administrator on 2017/12/12.
 */
@Data
public class RefundReturnParam {

//    @NotNull
//    private Long orderGoodsId;

    @NotNull
    private Long orderId;
    @NotNull
    private String brandName;

    /**
     * 退货/退款/换货
     *
     * @see com.framework.loippi.consts.RefundReturnState#TYPE_REFUND
     */
    @Max(3)
    @Min(1)
    @NotNull
    private Integer type;

    @Length(max = 255, message = "长度必须介于0和255之间")
    private String buyerMessage;

//    @Min(1)
//    private Integer goodsNum;

    /**
     * 图片
     */
    private String goodsImageMore;


}
