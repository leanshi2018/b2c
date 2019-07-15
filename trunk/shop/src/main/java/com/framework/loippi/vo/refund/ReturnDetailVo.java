package com.framework.loippi.vo.refund;

import com.framework.loippi.entity.trade.ShopRefundReturn;
import com.framework.loippi.entity.trade.ShopReturnLog;
import lombok.Data;

import java.util.List;

/**
 * 退货详情超类
 *
 * @author liukai
 */
@Data
public class ReturnDetailVo extends ShopRefundReturn {

    /**
     * 退货日志
     */
    private List<ShopReturnLog> returnLogList;
}
