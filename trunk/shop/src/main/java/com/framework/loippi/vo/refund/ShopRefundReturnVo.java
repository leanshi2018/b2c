package com.framework.loippi.vo.refund;

import com.framework.loippi.entity.trade.ShopRefundReturn;
import com.framework.loippi.entity.trade.ShopReturnLog;
import java.util.List;
import lombok.Data;

/**
 * 退货详情超类
 *
 * @author liukai
 */
@Data
public class ShopRefundReturnVo extends ShopRefundReturn {

    /**
     * 售后日志
     */
    private List<ShopReturnLog> returnLogList;


}
