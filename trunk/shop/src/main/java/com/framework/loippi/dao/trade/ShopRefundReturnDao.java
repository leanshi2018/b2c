package com.framework.loippi.dao.trade;

import com.framework.loippi.entity.trade.ShopRefundReturn;
import com.framework.loippi.mybatis.dao.GenericDao;

import com.framework.loippi.mybatis.paginator.domain.PageBounds;
import com.framework.loippi.mybatis.paginator.domain.PageList;
import com.framework.loippi.vo.order.ShopOrderVo;
import com.framework.loippi.vo.refund.ReturnGoodsVo;
import com.framework.loippi.vo.refund.ShopRefundReturnVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * DAO - ShopRefundReturn(退货退款)
 *
 * @author zj
 * @version 2.0
 */
public interface ShopRefundReturnDao extends GenericDao<ShopRefundReturn, Long> {

    /**
     * 扩展查询 退货 / 退款日志
     *
     * @param refundReturnId 退货退款表id
     * @param findAllLog 查询所有退货/退款日志, 还是指定卖家处理状态下日志
     * @return ShopRefundReturnVo
     */
    ShopRefundReturnVo getShopRefundReturnVoWithLog(@Param("id") Long refundReturnId,
        @Param("findAllLog") boolean findAllLog);

//    PageList<OrderAdminVo> findOrderGoodsAdminVoList(Object parameter, PageBounds pageBounds);

    /**
     * 分页查找订单+商品
     */
    PageList<ReturnGoodsVo> listRefundReturnVoWithGoods(Object parameter, PageBounds pageBounds);

    List<ShopRefundReturn> findByOrderId(long orderId);
}
