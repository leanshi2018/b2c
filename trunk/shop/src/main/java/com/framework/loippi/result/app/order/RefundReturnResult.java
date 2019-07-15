package com.framework.loippi.result.app.order;

import com.framework.loippi.consts.RefundReturnState;
import com.framework.loippi.entity.trade.ShopRefundReturn;
import com.framework.loippi.result.app.cart.BaseGoodsResult;
import com.framework.loippi.utils.JacksonUtil;
import com.google.common.collect.Lists;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 退货退款放回app数据
 */
@Data
@Accessors(chain = true)
public class RefundReturnResult extends BaseGoodsResult {

    /**
     * 服务单号
     */
    private String refundSn;

    /**
     *
     */
    private Integer state;

    /**
     * 申请时间
     */
    private Long applyTime;

    /**
     * 审核反馈
     */
    private String feedback;


    public static List<RefundReturnResult> buildList(List<ShopRefundReturn> refundReturnList) {
        if (CollectionUtils.isEmpty(refundReturnList)) {
            return Lists.newArrayList();
        }

        List<RefundReturnResult> resultList = new ArrayList<>();
        for (ShopRefundReturn refundReturn : refundReturnList) {
            Optional<ShopRefundReturn> optRefundReturn = Optional.ofNullable(refundReturn);
            RefundReturnResult result = new RefundReturnResult();
            result.setRefundSn(optRefundReturn.map(ShopRefundReturn::getRefundSn).orElse(""))
                .setState(getRefundState(refundReturn))
                .setApplyTime(optRefundReturn.map(ShopRefundReturn::getCreateTime).orElse(new Date()).getTime());
//                .setGoodsId(optRefundReturn.map(ShopRefundReturn::getGoodsId).orElse(-1L))
//                .setGoodsName(optRefundReturn.map(ShopRefundReturn::getGoodsName).orElse(""))
////                .setGoodsStorePrice(optRefundReturn.map(ShopRefundReturn::getRefundAmount).orElse(BigDecimal.ZERO))
//                .setDefaultImage(optRefundReturn.map(ShopRefundReturn::getGoodsImage).orElse(""))
//                .setQuantity(optRefundReturn.map(ShopRefundReturn::getGoodsNum).orElse(0))
//                .setSpecId(optRefundReturn.map(ShopRefundReturn::getSpecId).orElse(-1L))
//                .setSpecInfo(optRefundReturn.map(ShopRefundReturn::getSpecInfo).orElse(""));
            if (StringUtils.isNotEmpty(refundReturn.getSellerMessage())) {
                Map<String, String> map = JacksonUtil.convertStrMap(refundReturn.getSellerMessage());
                String join = StringUtils.join(map.values(), ",");
                result.setFeedback(Optional.ofNullable(join).orElse(""));
            }

            resultList.add(result);
        }

        return resultList;
    }

    public static int getRefundState(ShopRefundReturn refundReturn) {
        // 待审核
        final int not_process = 1;
        // 退货处理中
        final int in_process = 2;
        // 已拒绝
        final int audit_not_passed = 3;
        // 已通过
        final int audit_passed = 4;
        // 已完成
        final int refund_complete = 5;
        switch (refundReturn.getSellerState()) {
            case RefundReturnState.SELLER_STATE_PENDING_AUDIT:
                return not_process;
            case RefundReturnState.SELLER_STATE_CONFIRM_AUDIT:
                return in_process;
            case RefundReturnState.SELLER_STATE_AGREE:
                return audit_passed;
            case RefundReturnState.SELLER_STATE_DISAGREE:
                return audit_not_passed;
            case RefundReturnState.SELLER_STATE_FINISH:
                return refund_complete;
            default:
                return -1;
        }
    }
}

