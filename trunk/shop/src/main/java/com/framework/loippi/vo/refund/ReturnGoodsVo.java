package com.framework.loippi.vo.refund;

import com.framework.loippi.entity.order.ShopOrderGoods;
import com.framework.loippi.entity.trade.ShopRefundReturn;
import com.framework.loippi.entity.trade.ShopReturnLog;
import com.framework.loippi.entity.trade.ShopReturnOrderGoods;
import lombok.Data;

import java.util.List;

/**
 * 售后商品详情超类
 *
 * @author liukai
 */
@Data
public class ReturnGoodsVo extends ShopRefundReturn {

    /**
     * 售后商品
     */
    private List<ShopReturnOrderGoods> shopReturnOrderGoodsList;
}
