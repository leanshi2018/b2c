package com.framework.loippi.dao.order;

import com.framework.loippi.entity.order.ShopOrder;
import com.framework.loippi.mybatis.dao.GenericDao;
import com.framework.loippi.mybatis.paginator.domain.PageBounds;
import com.framework.loippi.mybatis.paginator.domain.PageList;
import com.framework.loippi.result.sys.OrderView;
import com.framework.loippi.vo.activity.ActivityStatisticsVo;
import com.framework.loippi.vo.goods.GoodsStatisticsVo;
import com.framework.loippi.vo.order.*;
import com.framework.loippi.vo.stats.StatsCountVo;
import com.framework.loippi.vo.store.StoreStatisticsVo;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * DAO - ShopOrder(订单表)
 *
 * @author zijing
 * @version 2.0
 */
public interface ShopOrderDao extends GenericDao<ShopOrder, Long> {

    /**
     * 查询后台订单列表--vo集合
     */
    PageList<OrderView> listOrderView(Object parameter, PageBounds pageBounds);

    /**
     * 范围查找未付款订单
     */
    List<ShopOrder> listNonPaymentOrder(@Param("startIndex") int startIndex, @Param("pageSize") int pageSize);

    /**
     * 根据id集合查询订单和会员信息
     */
    List<OrderMemberVo> listOrderMemberInfoByIds(OrderMemberVo orderMemberVo);

    /**
     * 扩展查询订单收货地址和订单商品
     */
    ShopOrderVo getShopOrderVoWithGoodsAndAddress(Long orderId);

    /**
     * 分页查找订单+商品
     */
    PageList<ShopOrderVo> listShopOrderVoWithGoods(Object parameter, PageBounds pageBounds);

    /**
     * 分页查找订单+商品+收货地址
     */
    PageList<ShopOrderVo> listShopOrderVoWithGoodsAndAddr(Object parameter, PageBounds pageBounds);

    /**
     * 分页查找可发货订单+商品+收货地址
     */
    PageList<ShopOrderVo> listDeliverableWithGoodsAndAddr(Object parameter, PageBounds pageBounds);


    /**
     * 扩展查询商品
     */
    ShopOrderVo getShopOrderVoWithGoods(Long orderId);

    /**
     * 扩展查询能售后商品
     */
    List<ShopOrderVo> listShopOrderVoWithAbleRefundReturnGoods(Object parameter, PageBounds pageBounds);

    Long updateByIdAndOrderStateAndLockState(ShopOrder order);

    /**
     * 订单统计查询
     */
    List<OrderCountVo> listOrderCountVo(Map<String, Object> paramMap);

    PageList<OrderStatisticsVo> listOrderStatisticsVo(Object parameter, PageBounds pageBounds);

    /**
     * 根据订单类型分组统计订单数及总额
     */
    List<OrderCountVo> listGroupPriceAndNumOfOrderType(Map<String, Object> paramMap);

    /**
     * 购买量统计
     */
    PageList<OrderMemberStatisticsVo> listOrderMemberStatisticsVo(Object parameter, PageBounds pageBounds);

    /**
     * 销售量统计
     */
    PageList<GoodsStatisticsVo> listGoodsStatisticsVo(Object parameter, PageBounds pageBounds);

    /**
     * 商家订单成交量统计
     */
    PageList<StoreStatisticsVo> listStoreStatisticsVo(Object parameter, PageBounds pageBounds);

    /**
     * 根据订单类型分组统计订单数及总额
     *
     * @param param 订单状态
     */
    List<ShopOrder> statisticsOrderPriceAndOrderNumByState(OrderView param);

    /**
     * 统计秒杀或促销活动总额和订单总数  日   周  月
     */
    List<ActivityStatisticsVo> statisticsSeckillOrPromotionBystate(ActivityStatisticsVo param);

    /**
     * 统计已秒杀的人数及秒杀购买总额
     */
    ActivityStatisticsVo countSeckillSuccess(ActivityStatisticsVo param);

    /**
     * 查询不同状态订单数量
     */
    List<OrderCountVo> listOrderCountVoGroupByState(Long storeId);

    /**
     * 统计订单-昨日 前日 上周 上上周, 上月, 上上月交易数量
     */
    List<StatsCountVo> listStatsCountVo();

    /**
     * 根据订单状态统计所有订单记录数
     * @param paramMap
     * @return
     */
    List<CountOrderStatusVo> countOrderStatus(Map<String, Object> paramMap);

    /**
     * 查询会员购买情况
     */
    List<OrderCountVo> countMemberOrderNum(Map<String, Object> paramMap);

    /**
     * 订单ppv值
     * @param paramMap
     * @return
     */
    OrderSumPpv sumPpv(Map<String, Object> paramMap);
}
