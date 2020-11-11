package com.framework.loippi.dao.order;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.framework.loippi.pojo.selfMention.OrderInfo;
import com.framework.loippi.result.selfMention.SelfMentionOrderStatistics;
import org.apache.ibatis.annotations.Param;

import com.framework.loippi.entity.order.ShopOrder;
import com.framework.loippi.mybatis.dao.GenericDao;
import com.framework.loippi.mybatis.paginator.domain.PageBounds;
import com.framework.loippi.mybatis.paginator.domain.PageList;
import com.framework.loippi.pojo.common.CensusVo;
import com.framework.loippi.pojo.common.MemIndicatorVo;
import com.framework.loippi.pojo.common.MemberShippingBehaviorVo;
import com.framework.loippi.result.sys.OrderView;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.vo.activity.ActivityStatisticsVo;
import com.framework.loippi.vo.goods.GoodsStatisticsVo;
import com.framework.loippi.vo.order.CountOrderStatusVo;
import com.framework.loippi.vo.order.OrderCountVo;
import com.framework.loippi.vo.order.OrderMemberStatisticsVo;
import com.framework.loippi.vo.order.OrderMemberVo;
import com.framework.loippi.vo.order.OrderStatisticsVo;
import com.framework.loippi.vo.order.OrderSumPpv;
import com.framework.loippi.vo.order.ShopOrderVo;
import com.framework.loippi.vo.stats.StatsCountVo;
import com.framework.loippi.vo.store.StoreStatisticsVo;

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

	BigDecimal countOrderPPVByMCodeAndPeriod(Map<String, Object> map);

	BigDecimal countOrderPPVRefundStateNot0(Map<String, Object> map);

    OrderSumPpv findByPeriod(Paramap periodCode);

    BigDecimal findOrderRetail(Paramap buyerId);

	List<ShopOrder> findStatu20();

	void updateOrderStatus(Map<String, Object> map);

    List<ShopOrder> findNoCutOrder(HashMap<String, Object> map);

	List<ShopOrder> findNoCutOrder1(HashMap<String, Object> map);

	ShopOrder findByBuyPaySn(String paySn);

    BigDecimal getCutTotalByCutId(String mmCode);

    List<ShopOrder> findAutoOrder1(Date time);

    List<ShopOrder> findAutoOrder2(Date time);

    Integer findOrderYesterdayNum(HashMap<String, Object> map);

    Integer findEffectiveOrderYesterdayNum(HashMap<String, Object> map);

    Integer findInvalidOrderYesterdayNum(HashMap<String, Object> map);

    Integer findPlatformOrderYesterdayNum(HashMap<String, Object> map);

    BigDecimal findYesIncomeTotal(HashMap<String, Object> map);

    BigDecimal findYesPointTotal(HashMap<String, Object> map);

    CensusVo findCensusData(HashMap<String, Object> map);

	ShopOrder findByPaySn(String paySn);

    List<MemIndicatorVo> findMemIndicatorVo(String periodCode);

    List<MemIndicatorVo> findMemIndicatorVoByYear(HashMap<String, Object> map);

    List<MemberShippingBehaviorVo> findNewShippingBehavior();

    List<MemberShippingBehaviorVo> findOldShippingBehavior();

	Integer countMentionSales(Map<String, Object> map);

    Integer findDailyCountByMentionId(Integer mentionId);

    Integer findMonthCountByMentionId(Integer mentionId);

    List<OrderInfo> findMonthOrderInfo(Integer mentionId);

    List<ShopOrder> findSelfOrderByPage(HashMap<String, Object> map);

    SelfMentionOrderStatistics statisticsSelfOrderByTime(HashMap<String, Object> map);

    List<ShopOrder> findByTime();

    Integer findLastMonthCountByMentionId(Integer mentionId);

    List<OrderInfo> findLastMonthOrderInfo(Integer mentionId);

    Long getPlusVipOrderNum(Long buyerId);

    BigDecimal plusSaveMoney(Paramap paramap);
}
