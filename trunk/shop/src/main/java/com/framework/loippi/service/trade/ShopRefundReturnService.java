package com.framework.loippi.service.trade;

import com.framework.loippi.entity.trade.ShopRefundReturn;
import com.framework.loippi.service.GenericService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.vo.refund.ShopRefundReturnVo;

import java.util.List;

/**
 * SERVICE - ShopRefundReturn(退款退货)
 *
 * @author zijing
 * @version 2.0
 */
public interface ShopRefundReturnService extends GenericService<ShopRefundReturn, Long> {

    /********************* 业务方法 *********************/
    /**
     * 审核确认
     *
     * @param refundId 售后id
     * @param sellerMessage 审核留言
     * @param processInfo 处理进度
     */
    void updateAuditConfirm(Long refundId, Long storeId, String sellerMessage, String processInfo);

    /**
     * 审核通过/不通过
     *
     * @param refundId 记录ID
     * @param sellerState 卖家处理状态 【同意/不同意】
     * @param operator 操作人
     */
    void updateAuditPass(Long refundId, Long storeId, Integer sellerState, String operator, String processInfo,
        String sellerMessage);

//    /**
//     * 获取页面所需退款项信息
//     */
//    Page findOrderGoodsAdminVoList(Pageable pageable);
//
//
//    Page<ShopRefundReturn> findWithExtendsByPage(Pageable pager);

    /**
     * 退款退货管理员审核退款
     *  @param refundId 记录ID
     * @param adminMessage 管理员备注
     * @param username
     */
    void updateRefundReturnAudiReturn(Long refundId, String adminMessage, String type, String username);

    /********************* 扩展查询 *********************/
    /**
     * 扩展查询 退货日志
     *
     * @param findAllLog 指定卖家处理状态下日志, 还是全部日志
     */
    ShopRefundReturnVo findWithRefundReturnLog(Long refundId, boolean findAllLog);

    /********************* 其他人添加 *********************/
    /**
     * 查询售后订单+售后订单商品
     */
    Page listWithGoods(Pageable pageable);

    List<ShopRefundReturn> findByOrderId(long orderId);
}
