package com.framework.loippi.service.order;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.framework.loippi.consts.Constants;
import com.framework.loippi.entity.PayCommon;
import com.framework.loippi.entity.order.ShopOrder;
import com.framework.loippi.entity.order.ShopOrderDiscountType;
import com.framework.loippi.entity.order.ShopOrderGoods;
import com.framework.loippi.entity.order.ShopOrderPay;
import com.framework.loippi.entity.trade.ShopRefundReturn;
import com.framework.loippi.entity.trade.ShopReturnOrderGoods;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.pojo.common.CensusVo;
import com.framework.loippi.result.sys.OrderView;
import com.framework.loippi.service.GenericService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.vo.activity.ActivityStatisticsVo;
import com.framework.loippi.vo.goods.GoodsStatisticsVo;
import com.framework.loippi.vo.order.CountOrderStatusVo;
import com.framework.loippi.vo.order.OrderCountVo;
import com.framework.loippi.vo.order.OrderMemberStatisticsVo;
import com.framework.loippi.vo.order.OrderStatisticsVo;
import com.framework.loippi.vo.order.OrderSumPpv;
import com.framework.loippi.vo.order.ShopOrderVo;
import com.framework.loippi.vo.stats.StatsCountVo;
import com.framework.loippi.vo.store.StoreStatisticsVo;

/**
 * SERVICE - ShopOrder(订单表)
 *
 * @author zijing
 * @version 2.0
 */
public interface ShopOrderService extends GenericService<ShopOrder, Long> {

    /********************* 业务方法 *********************/

    /**
     * 订单分物流发货
     *
     * @param orderId           订单编号
     * @param shippingExpressId 配送公司id
     * @param shippingCode      物流单号
     * @param deliverExplain    发货备注
     */
    void updateDeliveryOrder(Long orderId, Long storeId, Long shippingExpressId, String shippingCode,
                             String deliverExplain,String ShopOrderGoodsIds,String ShopOrderGoodsNums,
                             String wareCodes,String wareNames, String adminName,Integer type);

    /**
     * 取消订单
     *
     * @param operator 1:会员取消 2:商家取消 3:定时取消
     * @param paytrem  1:PC 2:mobile  3:h5
     */
    void updateCancelOrder(long orderId, int operator, long memberId, int paytrem,String message,String opName);

    /**
     * 发货提醒
     */
    void updateRemindDelivery(long orderId, long memberId);

    /**
     * 订单确认收货, 订单完成
     *
     * @see Constants#OPERATOR_SELLER
     * @see Constants#OPERATOR_MEMBER
     * @see Constants#OPERATOR_ADMINISTRATOR
     */
    void updateFinishOrder(long orderId, long memberId, int operatorRule);

    /**
     * 订单退款
     *
     * @param buyerMessage   退款原因
     * @param goodsImageMore 用户上传图片
     * @param applyType      2退货 1退款
     * @param shopOrderGoodsList  商品信息
     */
    Long addOrderRefundReturn(long memberId, String buyerMessage,String goodsImageMore, int applyType,List<ShopOrderGoods> shopOrderGoodsList,String brandName);

    /**
     * 提交订单
     *
     * @param cartIds     多个购物车id
     * @param memberId    用户id
     * @param orderMsgMap 存储买家留言信息,键为店铺id,值为店铺留言
     * @param addressId   收货地址id
     * @param couponIds   优惠券id
     * @param isPp        优惠积分支付
     * @param platform    0 微信端 1app
     * @return 返回OrderPay 订单支付表
     */
    ShopOrderPay addOrderReturnPaySn(String cartIds, String memberId, Map<String, Object> orderMsgMap, Long addressId,
                                     String couponIds, Integer isPp, Integer platform, Long groupBuyActivityId, Long groupOrderId,
                                     ShopOrderDiscountType shopOrderDiscountType, Integer logisticType, Integer paymentType);

    ShopOrderPay addOrderReturnPaySnNew(String cartIds, String mmCode, Map<String, Object> orderMsgMap, Long addressId,
                                        String couponIds, Integer isPp, Integer platformApp, Long groupBuyActivityId, Long groupOrderId,
                                        ShopOrderDiscountType shopOrderDiscountType, Integer logisticType, Integer paymentType, Long giftId, Integer giftNum);

    ShopOrderPay addReplacementOrder(Long goodsId,Integer count,Long specId,Long memberId);
    /**
     * 根据支付单号更改 订单支付方式id和支付方式名称
     *
     * @param paysn     支付单号
     * @param paymentId 支付方式id
     */
    void updateByPaySn(String paysn, Long paymentId);

    /**
     * 支付完成后,修改订单状态
     *
     * @param tradeSn       支付流水号
     * @param paymentBranch 支付分支
     */
    void updateOrderStatePayFinish(String paysn, String tradeSn, String paymentBranch);

    /**
     * 逻辑删除订单
     */
    void updateToDelState(Long orderId, String memberId);

    /**
     * 平台账户余额支付
     */
//    Map<String, Object> payWallet(PayCommon payCommon, String memberId);

    /********************* 下面主页是扩展查询， 没什么逻辑 *********************/
    /**
     * 后台订单列表vo集合
     */
    Page listOrderView(Pageable pageable);

    /**
     * 订单基本信息+订单地址详情+订单商品详情
     */
    ShopOrderVo findWithAddrAndGoods(Long orderId);

    /**
     * 订单基本信息+ 未参加售后商品列表
     */
    List<ShopOrderVo> listWithAbleRefundReturnGoods(Pageable pageable);

    /**
     * 查询订单+订单商品
     */
    Page listWithGoods(Pageable pageable);

    /**
     * 查询订单+订单商品+订单收货地址
     */
    Page listWithGoodsAndAddr(Pageable pageable);

    /**
     * 查询可发货订单(过滤未成团订单)+订单商品+订单收货地址
     */
    Page findDeliverableWithGoodsAndAddr(Pageable pageable);

    /**
     * 根据订单id和状态更新
     *
     * @throws RuntimeException 更新失败, 抛异常
     * @see com.framework.loippi.consts.OrderState#ORDER_OPERATE_PAY 操作类型
     */
    void updateByIdOrderStateLockState(ShopOrder order, int orderOperateType);

    /**
     * 获取订单统计数据
     */
    List<OrderCountVo> listOrderbuy(Map<String, Object> paramMap);

    /**
     * 分页查询统计信息
     */
    Page<OrderStatisticsVo> listOrderStatistics(Pageable pager);

    /**
     * 根据订单类型分组统计订单数及总额
     */
    List<OrderCountVo> listGroupPriceAndNumOfOrderType(Map<String, Object> paramMap);

    /**
     * 统计购买量
     */
    Page<OrderMemberStatisticsVo> topPurchase(Pageable pageable);

    /**
     * 统计销售量
     */
    Page<GoodsStatisticsVo> listBestSellGoods(Pageable pageable);

    /**
     * 店铺成交订单统计
     */
    Page<StoreStatisticsVo> listBestOrderVolumeStore(Pageable pageable);

    /**
     * 查询不同状态下订单数量
     */
    List<OrderCountVo> listOrderCountVo(long storeId);


    /********************* 统计服务 *********************/
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
    ActivityStatisticsVo countSeckillSuccess(ActivityStatisticsVo as);


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
     * 处理购物积分(使用购物积分进行购物)
     * @param paysn 支付单号
     * @param integration 购物积分
     * @param
     */
    void ProcessingIntegrals(String paysn, Integer integration, RdMmBasicInfo shopMember, ShopOrderPay pay, Integer shoppingPointSr);

    void ProcessingIntegralsCoupon(String paysn, Integer integration, RdMmBasicInfo shopMember, ShopOrderPay pay, Integer shoppingPointSr);

    ShopOrder findWithOrderGoodsById(Long orderId);

    /**
     *
     */
    Map<String, Object> updateOrderpay(PayCommon payCommon, String memberId,String payName,String paymentCode,String paymentId);

    /**
     * 查询会员购买情况
     */
    List<OrderCountVo> countMemberOrderNum(Map<String, Object> paramMap);

    /**
     * 后台生成换货订单
     * @param shopReturnOrderGoodsList
     * @param orderId
     * @return
     */
    Long addExchangeOrderReturnOrderId(List<ShopReturnOrderGoods> shopReturnOrderGoodsList, Long orderId);

    /**
     * 后台生售后进行积分,pv,处理
     * @return
     */
    void addRefundPoint(ShopRefundReturn refundReturn);

    /**
     * 后台修改订单商品信息
     * @return
     */
    void modifyOrderInfo(Long goodsId,String specJson,Long orderGoodsId,Long orderId);
    /**
     * 后台进行订单修改后计算价格
     * @return
     */
    ShopOrderVo modifyOrderCalculatePrice(ShopOrderVo orderVo,Long orderId,Long shopOrderTypeId);

    /**
     * 后台确认进行订单修改
     * @return
     */
    ShopOrderVo modifyOrderSubmit(Long orderId);

    /**
     * 订单ppv值
     * @param paramMap
     * @return
     */
    OrderSumPpv sumPpv(Map<String, Object> paramMap);

    /**
     * 计算当前周期下会员的ppv
     * @param mmCode
     * @param period
     * @return
     */
    BigDecimal countOrderPPVByMCodeAndPeriod(String mmCode, String period);

    /**
     * 查询指定周期订单总额以及ppv
     * @param periodCode
     * @return
     */
    OrderSumPpv findByPeriod(Paramap periodCode);

    BigDecimal findOrderRetail(Paramap buyerId);

	List<ShopOrder> findStatu20();

	void updateOrderStatus(String orderSn, Integer orderState, Integer submitStatus, String failInfo, String trackingNo);

    ShopOrderPay addOrderReturnPaySnNew1(String cartIds, String mmCode, Map<String, Object> orderMsgMap, Long addressId, Long couponId, Integer isPp, Integer platformApp, Long groupBuyActivityId, Long groupOrderId, ShopOrderDiscountType shopOrderDiscountType, Integer logisticType, Integer paymentType, Long giftId, Integer giftNum);

	Map<String, Object> checkSuccessOrNo(String mmCode, String orderId);

	ShopOrder findByBuyPaySn(String bizPaySn);

    BigDecimal getCutTotalByCutId(String mmCode);

    void autoReceipt1(List<ShopOrder> list1);

    void autoReceipt2(List<ShopOrder> list2);

    Integer findOrderYesterdayNum(HashMap<String, Object> map);

    Integer findEffectiveOrderYesterdayNum(HashMap<String, Object> map);

    Integer findInvalidOrderYesterdayNum(HashMap<String, Object> map);

    Integer findPlatformOrderYesterdayNum(HashMap<String, Object> map);

    BigDecimal findYesIncomeTotal(HashMap<String, Object> map);

    BigDecimal findYesPointTotal(HashMap<String, Object> map);

    CensusVo findCensusData(HashMap<String, Object> map);

	ShopOrder findByPaySn(String paySn);



	/*void ProcessingIntegralsNew(String paysn, Double integration, RdMmBasicInfo shopMember, ShopOrderPay pay, int shoppingPointSr);*/
}
