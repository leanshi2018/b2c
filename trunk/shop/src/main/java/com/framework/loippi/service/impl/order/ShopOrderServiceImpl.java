package com.framework.loippi.service.impl.order;



import com.framework.loippi.entity.coupon.Coupon;
import com.framework.loippi.entity.coupon.CouponDetail;
import com.framework.loippi.entity.coupon.CouponPayDetail;
import com.framework.loippi.entity.coupon.CouponUser;
import com.framework.loippi.service.coupon.CouponDetailService;
import com.framework.loippi.service.coupon.CouponPayDetailService;
import com.framework.loippi.service.coupon.CouponService;
import com.framework.loippi.service.coupon.CouponUserService;
import com.framework.loippi.service.user.*;
import com.framework.loippi.service.wechat.WechatRefundService;
import com.framework.loippi.utils.*;
import com.framework.loippi.utils.validator.DateUtils;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.consts.Constants;
import com.framework.loippi.consts.IntegrationNameConsts;
import com.framework.loippi.consts.NewVipConstant;
import com.framework.loippi.consts.OrderState;
import com.framework.loippi.consts.PaymentTallyState;
import com.framework.loippi.consts.ShopOrderDiscountTypeConsts;
import com.framework.loippi.controller.AppConstants;
import com.framework.loippi.controller.StateResult;
import com.framework.loippi.dao.ShopCommonMessageDao;
import com.framework.loippi.dao.ShopMemberMessageDao;
import com.framework.loippi.dao.common.ShopCommonExpressDao;
import com.framework.loippi.dao.order.ShopOrderAddressDao;
import com.framework.loippi.dao.order.ShopOrderDao;
import com.framework.loippi.dao.order.ShopOrderDiscountTypeDao;
import com.framework.loippi.dao.order.ShopOrderGoodsDao;
import com.framework.loippi.dao.order.ShopOrderLogDao;
import com.framework.loippi.dao.order.ShopOrderLogisticsDao;
import com.framework.loippi.dao.order.ShopOrderPayDao;
import com.framework.loippi.dao.point.ShopPointsLogDao;
import com.framework.loippi.dao.product.ShopGoodsDao;
import com.framework.loippi.dao.product.ShopGoodsSpecDao;
import com.framework.loippi.dao.trade.ShopRefundReturnDao;
import com.framework.loippi.dao.trade.ShopReturnLogDao;
import com.framework.loippi.dao.trade.ShopReturnOrderGoodsDao;
import com.framework.loippi.dao.user.RdGoodsAdjustmentDao;
import com.framework.loippi.dao.user.RdMmRelationDao;
import com.framework.loippi.dao.user.RdSysPeriodDao;
import com.framework.loippi.dao.user.ShopMemberPaymentTallyDao;
import com.framework.loippi.dao.ware.RdInventoryWarningDao;
import com.framework.loippi.dao.ware.RdWareAdjustDao;
import com.framework.loippi.entity.AliPayRefund;
import com.framework.loippi.entity.PayCommon;
import com.framework.loippi.entity.ShopCommonMessage;
import com.framework.loippi.entity.ShopMemberMessage;
import com.framework.loippi.entity.TSystemPluginConfig;
import com.framework.loippi.entity.WeiRefund;
import com.framework.loippi.entity.activity.ShopActivityGoodsSpec;
import com.framework.loippi.entity.common.ShopCommonArea;
import com.framework.loippi.entity.common.ShopCommonExpress;
import com.framework.loippi.entity.integration.RdMmIntegralRule;
import com.framework.loippi.entity.order.ShopOrder;
import com.framework.loippi.entity.order.ShopOrderAddress;
import com.framework.loippi.entity.order.ShopOrderDiscountType;
import com.framework.loippi.entity.order.ShopOrderGoods;
import com.framework.loippi.entity.order.ShopOrderLog;
import com.framework.loippi.entity.order.ShopOrderLogistics;
import com.framework.loippi.entity.order.ShopOrderPay;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsSpec;
import com.framework.loippi.entity.trade.ShopRefundReturn;
import com.framework.loippi.entity.trade.ShopReturnLog;
import com.framework.loippi.entity.trade.ShopReturnOrderGoods;
import com.framework.loippi.entity.user.RdGoodsAdjustment;
import com.framework.loippi.entity.user.RdMmAccountInfo;
import com.framework.loippi.entity.user.RdMmAccountLog;
import com.framework.loippi.entity.user.RdMmAddInfo;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.entity.user.RdRanks;
import com.framework.loippi.entity.user.RetailProfit;
import com.framework.loippi.entity.user.ShopMemberPaymentTally;
import com.framework.loippi.entity.ware.RdInventoryWarning;
import com.framework.loippi.entity.ware.RdWareAdjust;
import com.framework.loippi.enus.ActivityTypeEnus;
import com.framework.loippi.enus.RefundReturnState;
import com.framework.loippi.mybatis.paginator.domain.PageList;
import com.framework.loippi.pojo.activity.ShopGoodSpec;
import com.framework.loippi.pojo.cart.CartInfo;
import com.framework.loippi.pojo.cart.CartVo;
import com.framework.loippi.pojo.order.CartOrderVo;
import com.framework.loippi.pojo.order.OrderSettlement;
import com.framework.loippi.pojo.order.OrderVo;
import com.framework.loippi.result.sys.OrderView;
import com.framework.loippi.service.RedisService;
import com.framework.loippi.service.TSystemPluginConfigService;
import com.framework.loippi.service.TUserSettingService;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.activity.ShopActivityGoodsSpecService;
import com.framework.loippi.service.activity.ShopActivityService;
import com.framework.loippi.service.alipay.AlipayRefundService;
import com.framework.loippi.service.common.ProductService;
import com.framework.loippi.service.common.ShopCommonAreaService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.integration.RdMmIntegralRuleService;
import com.framework.loippi.service.order.ShopOrderGoodsService;
import com.framework.loippi.service.order.ShopOrderService;
import com.framework.loippi.service.product.ShopCartService;
import com.framework.loippi.service.product.ShopGoodsFreightRuleService;
import com.framework.loippi.service.product.ShopGoodsFreightService;
import com.framework.loippi.service.product.ShopGoodsService;
import com.framework.loippi.service.product.ShopGoodsSpecService;
import com.framework.loippi.service.wallet.ShopWalletLogService;
import com.framework.loippi.service.wechat.WechatMobileRefundService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.web.servlet.support.RequestContext;

/**
 * SERVICE - ShopOrder(订单表)
 *
 * @author zijing
 * @version 2.0
 */
@Service
@Slf4j
public class ShopOrderServiceImpl extends GenericServiceImpl<ShopOrder, Long> implements ShopOrderService {
    @Autowired
    private RdSysPeriodService rdSysPeriodService;
    @Resource
    private CouponPayDetailService couponPayDetailService;
    @Autowired
    private ShopOrderDao orderDao;
    @Autowired
    private TwiterIdService twiterIdService;
    @Autowired
    private RdMmBasicInfoService rdMmBasicInfoService;
    @Autowired
    private RdMmAddInfoService rdMmAddInfoService;
    @Autowired
    private ShopOrderAddressDao orderAddressDao;
    @Autowired
    private ShopCartService cartService;
    @Autowired
    private ShopOrderPayDao orderPayDao;
    @Autowired
    private ShopOrderGoodsDao orderGoodsDao;
    @Autowired
    private ShopCommonExpressDao shopCommonExpressDao;
    @Autowired
    private ShopOrderLogDao orderLogDao;
    @Autowired
    private ProductService productService;
    @Autowired
    private ShopGoodsDao goodsDao;
    @Autowired
    private ShopGoodsSpecDao goodsSpecDao;
    @Autowired
    private WechatMobileRefundService wechatMobileRefundService;
    @Autowired
    private AlipayRefundService alipayRefundService;
    @Autowired
    private ShopMemberPaymentTallyDao paymentTallyDao;
    @Autowired
    private TSystemPluginConfigService tSystemPluginConfigService;
    @Autowired
    private ShopRefundReturnDao refundReturnDao;
    @Autowired
    private ShopReturnLogDao returnLogDao;
    @Autowired
    private ShopWalletLogService walletLogService;
    @Autowired
    private ShopPointsLogDao pointsLogDao;
    @Autowired
    private ShopActivityService activityService;
    @Autowired
    private TUserSettingService settingService;
    @Autowired
    private ShopOrderAddressDao shopOrderAddressDao;
    @Autowired
    private ShopOrderGoodsService shopOrderGoodsService;
    @Autowired
    private ShopCommonMessageDao shopCommonMessageDao;
    @Resource
    private ShopMemberMessageDao shopMemberMessageDao;
    @Autowired
    private ShopReturnOrderGoodsDao shopReturnOrderGoodsDao;
    @Resource
    private RdMmAccountInfoService rdMmAccountInfoService;
    @Resource
    private RdMmAccountLogService rdMmAccountLogService;
    @Resource
    private RedisService redisService;
    @Resource
    private ShopOrderDiscountTypeDao shopOrderDiscountTypeDao;
    @Resource
    private ShopGoodsFreightRuleService shopGoodsFreightRuleService;
    @Resource
    private ShopGoodsFreightService shopGoodsFreightService;
    @Resource
    private RdMmRelationService rdMmRelationService;
    @Resource
    private RdMmRelationDao rdMmRelationDao;
    @Resource
    private RdRanksService rdRanksService;
    @Resource
    private ShopActivityGoodsSpecService shopActivityGoodsSpecService;
    @Resource
    private ShopCommonAreaService areaService;
    @Resource
    private RdSysPeriodDao rdSysPeriodDao;
    @Resource
    private RdWareAdjustDao rdWareAdjustDao;
    @Resource
    private RdGoodsAdjustmentDao rdGoodsAdjustmentDao;
    @Resource
    private RdInventoryWarningDao rdInventoryWarningDao;
    @Resource
    private ShopOrderLogisticsDao shopOrderLogisticsDao;
    @Resource
    private RdMmIntegralRuleService rdMmIntegralRuleService;
    @Resource
    private RetailProfitService retailProfitService;
    @Resource
    private ShopGoodsService shopGoodsService;
    @Resource
    private ShopGoodsSpecService shopGoodsSpecService;
    @Resource
    private CouponService couponService;
    @Resource
    private CouponUserService couponUserService;
    @Resource
    private CouponDetailService couponDetailService;
    @Resource
    private WechatRefundService wechatRefundService;
    @Autowired
    public void setGenericDao() {
        super.setGenericDao(orderDao);
    }

    /*********************
     * 订单发货
     *********************/
    @Override
    public void updateDeliveryOrder(Long orderId, Long storeId, Long shippingExpressId, String shippingCode,
        String deliverExplain, String ShopOrderGoodsIds, String ShopOrderGoodsNums,
        String wareCodes, String wareNames, String adminName, Integer type) {
        // todo 团购订单未成团 不能发货
        // todo 同时操作
        ShopOrder order = orderDao.find(orderId);
        if (!order.getStoreId().equals(storeId)) {
            throw new RuntimeException("不属于该商家订单不予许发货");
        }

        if (order.getOrderState() != OrderState.ORDER_STATE_UNFILLED) {
            throw new RuntimeException("订单状态错误不予许发货");
        }
        ShopCommonExpress express = new ShopCommonExpress();
        if (type == 1) {
            express = shopCommonExpressDao.find(shippingExpressId);
            if (express == null) {
                throw new RuntimeException("系统不支持此快递, 请到配送方式配置");
            }
        }

        //订单商品表id集合
        if (ShopOrderGoodsIds.endsWith(",")) {
            ShopOrderGoodsIds = ShopOrderGoodsIds.substring(0, ShopOrderGoodsIds.length() - 1);
        }
        String[] ShopOrderGoodsIdSort = ShopOrderGoodsIds.split(",");
        List<ShopOrderGoods> goodsList = shopOrderGoodsService
            .findList(Paramap.create().put("Ids", ShopOrderGoodsIdSort));
        //订单商品数量集合
        if (ShopOrderGoodsNums.endsWith(",")) {
            ShopOrderGoodsNums = ShopOrderGoodsNums.substring(0, ShopOrderGoodsNums.length() - 1);
        }
        String[] ShopOrderGoodsNumSort = ShopOrderGoodsNums.split(",");
        //发货仓库id集合
        if (wareCodes.endsWith(",")) {
            wareCodes = wareCodes.substring(0, wareCodes.length() - 1);
        }
        //发货仓库名称集合
        String[] wareCodeSort = wareCodes.split(",");
        if (wareNames.endsWith(",")) {
            wareNames = wareNames.substring(0, wareNames.length() - 1);
        }
        String[] wareNameSort = wareNames.split(",");

        List<ShopOrderGoods> shopOrderGoodsList = new ArrayList<>();
        for (int i = 0; i < ShopOrderGoodsIdSort.length; i++) {
            ShopOrderGoods shopOrderGoods = new ShopOrderGoods();
            shopOrderGoods.setShippingExpressCode(Optional.ofNullable(express.getECode()).orElse(""));
            shopOrderGoods.setShippingExpressId(Optional.ofNullable(express.getId()).orElse(-1L));
            shopOrderGoods.setShippingExpressName(Optional.ofNullable(express.getEName()).orElse(""));
            shopOrderGoods.setShippingCode(Optional.ofNullable(shippingCode).orElse("-1"));
            shopOrderGoods.setShippingGoodsNum(Integer.parseInt(ShopOrderGoodsNumSort[i]));
            shopOrderGoods.setId(Long.parseLong(ShopOrderGoodsIdSort[i]));
            shopOrderGoodsList.add(shopOrderGoods);
            //新增发货单
            RdWareAdjust rdWareAdjust = new RdWareAdjust();
            rdWareAdjust.setWareCode(wareCodeSort[i]);
            rdWareAdjust.setWareName(wareNameSort[i]);
            rdWareAdjust.setAdjustType("SOT");
            rdWareAdjust.setStatus(3);
            rdWareAdjust.setAutohrizeBy(adminName);
            rdWareAdjust.setAutohrizeTime(new Date());
            rdWareAdjust.setAutohrizeDesc("订单发货");
            rdWareAdjustDao.insert(rdWareAdjust);
            ShopGoodsSpec shopGoodsSpec = goodsSpecDao.find(goodsList.get(i).getSpecId());
            ShopGoods shopGoods = goodsDao.find(goodsList.get(i).getGoodsId());
            //新增的发货商品详情
            RdGoodsAdjustment rdGoodsAdjustment = new RdGoodsAdjustment();
            rdGoodsAdjustment.setWid(rdWareAdjust.getWid());
            rdGoodsAdjustment.setSpecificationId(shopGoodsSpec.getId());
            rdGoodsAdjustment.setGoodId(shopGoodsSpec.getGoodsId());
            rdGoodsAdjustment.setGoodsName(shopGoods.getGoodsName());
            rdGoodsAdjustment.setSpecName(shopGoodsSpec.getSpecName());
            rdGoodsAdjustment.setGoodsSpec(shopGoodsSpec.getSpecGoodsSpec());
            rdGoodsAdjustment.setStockNow(shopGoodsSpec.getSpecGoodsStorage().longValue());
            rdGoodsAdjustment.setStockInto(Long.parseLong(ShopOrderGoodsNumSort[i]));
            rdGoodsAdjustment.setCreateTime(new Date());
            rdGoodsAdjustment.setWareCode(wareCodeSort[i]);
            rdGoodsAdjustment.setSign(1);
            rdGoodsAdjustment.setAutohrizeTime(new Date());
            rdGoodsAdjustment.setStatus(1L);
            rdGoodsAdjustmentDao.insert(rdGoodsAdjustment);
            //仓库库存修改
            RdInventoryWarning rdInventoryWarning = new RdInventoryWarning();
            rdInventoryWarning.setWareCode(wareCodeSort[i]);
            rdInventoryWarning.setSpecificationId(shopGoodsSpec.getId());
            rdInventoryWarning.setInventory(Integer.parseInt(ShopOrderGoodsNumSort[i]));
            rdInventoryWarningDao.updateInventory(rdInventoryWarning);
            //新增订单商品物流信息表
            ShopOrderLogistics shopOrderLogistics = new ShopOrderLogistics();
            shopOrderLogistics.setGoodsId(shopGoodsSpec.getGoodsId());
            shopOrderLogistics.setGoodsImage(shopGoods.getGoodsImage());
            shopOrderLogistics.setGoodsName(shopGoods.getGoodsName());
            shopOrderLogistics.setGoodsType(shopGoods.getGoodsType());
            shopOrderLogistics.setOrderId(orderId);
            shopOrderLogistics.setSpecId(shopGoodsSpec.getId());
            shopOrderLogistics.setSpecInfo(goodsList.get(i).getSpecInfo());
            shopOrderLogistics.setGoodsNum(Integer.parseInt(ShopOrderGoodsNumSort[i]));
            shopOrderLogistics.setPpv(shopGoodsSpec.getPpv());
            shopOrderLogistics.setPrice(shopGoodsSpec.getSpecRetailPrice());
            shopOrderLogistics.setShippingExpressCode(Optional.ofNullable(express.getECode()).orElse(""));
            shopOrderLogistics.setShippingExpressId(Optional.ofNullable(express.getId()).orElse(-1L));
            shopOrderLogistics.setShippingCode(Optional.ofNullable(shippingCode).orElse("-1"));
            shopOrderLogistics.setId(twiterIdService.getTwiterId());
            shopOrderLogisticsDao.insert(shopOrderLogistics);
        }

        shopOrderGoodsService.updateBatchForShipmentNum(shopOrderGoodsList);

        //更新零售利润记录预计发放时间
        if(order.getOrderType().equals(1)){//如果是零售订单，查询出零售利润记录
            RetailProfit retailProfit = retailProfitService.find("orderId",order.getId());
            if(retailProfit!=null){
                if(retailProfit.getExpectTime()==null){
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.add(Calendar.DATE, 10);
                    retailProfit.setExpectTime(calendar.getTime());
                    String periodCode = rdSysPeriodDao.getSysPeriodService(retailProfit.getExpectTime());
                    if(periodCode!=null){
                        retailProfit.setExpectPeriod(periodCode);
                    }
                    retailProfitService.update(retailProfit);
                }
            }
        }




        // 更新订单
        ShopOrder newOrder = new ShopOrder();
        newOrder.setId(order.getId());
        if (order.getShippingCode() == null) {
            // 更新
            newOrder.setShippingTime(new Date());
            newOrder.setShippingExpressCode(Optional.ofNullable(express.getECode()).orElse(""));
            newOrder.setShippingExpressId(Optional.ofNullable(express.getId()).orElse(-1L));
            newOrder.setShippingCode(shippingCode);
            newOrder.setShippingName(Optional.ofNullable(express.getEName()).orElse(""));
            orderDao.update(newOrder);

        }
        List<ShopOrderGoods> orderGoodsList = shopOrderGoodsService.findList(Paramap.create().put("orderId", orderId));
        Boolean flag = true;
        for (ShopOrderGoods item : orderGoodsList) {
            if (item.getGoodsNum() > Optional.ofNullable(item.getShippingGoodsNum()).orElse(0)) {
                flag = false;
            }
        }
        if (flag) {
//             if (type==1){
            newOrder.setOrderState(OrderState.ORDER_STATE_NOT_RECEIVING);
            newOrder.setPrevOrderState(OrderState.ORDER_STATE_UNFILLED);
            newOrder.setPrevLockState(OrderState.ORDER_LOCK_STATE_NO);
            updateByIdOrderStateLockState(newOrder, OrderState.ORDER_OPERATE_DELIVERY);
        }
//             }
//             else{
//                 newOrder.setOrderState(OrderState.ORDER_STATE_FINISH);
//                 newOrder.setPrevOrderState(OrderState.ORDER_STATE_UNFILLED);
//                 newOrder.setPrevLockState(OrderState.ORDER_LOCK_STATE_NO);
//                 updateByIdOrderStateLockState(newOrder, OrderState.ORDER_OPERATE_DELIVERY);
//             }
        //进行用户订单通知
        ShopCommonMessage message = new ShopCommonMessage();
        message.setSendUid(order.getBuyerId() + "");
        message.setType(1);
        message.setOnLine(1);
        message.setIsTop(1);
        message.setCreateTime(new Date());
        message.setBizType(3);
        message.setBizId(orderId);
        message.setTitle(" 订单编号：" + order.getOrderSn());
        StringBuffer shareUrl = new StringBuffer();
        shareUrl.append("<ol class='list-paddingleft-2' style='list-style-type: decimal;'>");
        shareUrl.append("<li><p>卖家已发货</p></li>");
        shareUrl.append("<li><p>物流单号：" + shippingCode + "</p></li>");
        shareUrl.append("<li><p>请耐心等候物流</p></li>");
        message.setContent(shareUrl.toString());
        Long msgId = twiterIdService.getTwiterId();
        message.setId(msgId);
        shopCommonMessageDao.insert(message);
        ShopMemberMessage shopMemberMessage = new ShopMemberMessage();
        shopMemberMessage.setBizType(3);
        shopMemberMessage.setCreateTime(new Date());
        shopMemberMessage.setId(twiterIdService.getTwiterId());
        shopMemberMessage.setIsRead(0);//TODO 2019-08-26修改为默认创建为未读信息
        shopMemberMessage.setMsgId(msgId);
        shopMemberMessage.setUid(order.getBuyerId());
        shopMemberMessageDao.insert(shopMemberMessage);
        //订单日志
        ShopOrderLog orderLog = new ShopOrderLog();
        orderLog.setId(twiterIdService.getTwiterId());
        orderLog.setOrderId(order.getId());
        orderLog.setOperator(order.getStoreName());
        orderLog.setCreateTime(new Date());
        if (type == 1) {
            orderLog.setOrderState(OrderState.ORDER_STATE_NOT_RECEIVING + "");
            orderLog.setChangeState(OrderState.ORDER_STATE_FINISH + "");
        } else {
            orderLog.setOrderState(OrderState.ORDER_STATE_FINISH + "");
            orderLog.setChangeState(OrderState.ORDER_STATE_FINISH + "");
        }
        if (type == 1) {
            orderLog.setStateInfo("订单已发货");
        } else {
            orderLog.setStateInfo("订单已自提");
        }
        //保存订单日志
        orderLogDao.insert(orderLog);

    }

    @Override
    public void updateCancelOrder(long orderId, int opType, long memberId, int paytrem, String message,String opName) {
        //通过订单编号查询订单
        ShopOrderVo order=new ShopOrderVo();
        try {
             order = findWithAddrAndGoods(orderId);
        }catch (Exception e){
            if ("系统定时取消订单".equals(message) && e.getMessage().indexOf("存在已下架商品")!=-1){
                order.setOrderState(OrderState.ORDER_STATE_CANCLE); //订单状态
                orderDao.update(order);
            }
        }

        if (opType == Constants.OPERATOR_MEMBER && memberId != order.getBuyerId()) {
            throw new RuntimeException("非法操作");
        }

//        if (opType != Constants.OPERATOR_MEMBER && order.getLockState() == OrderState.ORDER_LOCK_STATE_YES) {
//            throw new RuntimeException("订单已锁定 不允许操作！");
//        }

        if (order.getOrderState() != OrderState.ORDER_STATE_NO_PATMENT
            && order.getOrderState() != OrderState.ORDER_STATE_UNFILLED) {
            throw new RuntimeException("订单状态错误！");
        }
        if(order.getOrderState() == OrderState.ORDER_STATE_UNFILLED){
            RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", order.getBuyerId() + "");
            if(rdMmRelation!=null){
                BigDecimal money = Optional.ofNullable(rdMmRelation.getARetail()).orElse(BigDecimal.ZERO);//获得累计零售购买额
                BigDecimal aTotal = Optional.ofNullable(rdMmRelation.getATotal()).orElse(BigDecimal.ZERO);//获得累计购买额
                BigDecimal orderMoney = Optional.ofNullable(order.getOrderAmount()).orElse(BigDecimal.ZERO)
                        .add(Optional.ofNullable(order.getPointRmbNum()).orElse(BigDecimal.ZERO)
                                .add(Optional.ofNullable(order.getCouponDiscount()).orElse(BigDecimal.ZERO))
                                .subtract(Optional.ofNullable(order.getShippingFee()).orElse(BigDecimal.ZERO)));
                BigDecimal vipMoney = BigDecimal.valueOf(NewVipConstant.NEW_VIP_CONDITIONS_TOTAL);
                BigDecimal ppv = Optional.ofNullable(rdMmRelation.getAPpv()).orElse(BigDecimal.ZERO);
                BigDecimal orderPpv = Optional.ofNullable(order.getPpv()).orElse(BigDecimal.ZERO);
                //BigDecimal agencyPpv = BigDecimal.valueOf(NewVipConstant.NEW_AGENCY_CONDITIONS_TOTAL);
                if(order.getOrderType()==1){
                    rdMmRelation.setARetail(money.subtract(orderMoney));
                }
                //之前少于升级vip的价位 加上这个订单大于或者等于升级vip的价位
                if (order.getOrderType()==1&&money.compareTo(vipMoney) != -1 && (money.subtract(orderMoney)).compareTo(vipMoney) == -1&&rdMmRelation.getNOFlag()==1) {
                    rdMmRelation.setAPpv(ppv.subtract(orderPpv));
                    rdMmRelation.setATotal(aTotal.subtract(orderMoney));
                    rdMmRelation.setRank(0);
                    rdMmRelationDao.update(rdMmRelation);
                    //进行用户降级通知
                    ShopCommonMessage shopCommonMessage=new ShopCommonMessage();
                    shopCommonMessage.setSendUid(rdMmRelation.getMmCode());
                    shopCommonMessage.setType(1);
                    shopCommonMessage.setOnLine(1);
                    shopCommonMessage.setCreateTime(new Date());
                    shopCommonMessage.setBizType(2);
                    shopCommonMessage.setIsTop(1);
                    shopCommonMessage.setCreateTime(new Date());
                    shopCommonMessage.setTitle("很遗憾，等级降了");
                    shopCommonMessage.setContent("您已从VIP会员变成普通会员,多多购物可提升等级哦");
                    Long msgId = twiterIdService.getTwiterId();
                    shopCommonMessage.setId(msgId);
                    shopCommonMessageDao.insert(shopCommonMessage);
                    ShopMemberMessage shopMemberMessage=new ShopMemberMessage();
                    shopMemberMessage.setBizType(2);
                    shopMemberMessage.setCreateTime(new Date());
                    shopMemberMessage.setId(twiterIdService.getTwiterId());
                    shopMemberMessage.setIsRead(0);
                    shopMemberMessage.setMsgId(msgId);
                    shopMemberMessage.setUid(Long.parseLong(rdMmRelation.getMmCode()));
                    shopMemberMessageDao.insert(shopMemberMessage);
                }else {
                    rdMmRelation.setAPpv(ppv.subtract(orderPpv));
                    rdMmRelation.setATotal(aTotal.subtract(orderMoney));
                    rdMmRelationDao.update(rdMmRelation);
                }
            }
        }

        // 拼团过程和拼团成功不允许取消订单；
//        if (order.getOrderType() == OrderState.ORDER_TYPE_GROUP) {
//            throw new StateResult(AppConstants.ORDER_CANCEL_FAIL, "团购订单不能取消");
//        }
        order.setIsModify(1);
        order.setCancelCause(message); // 取消原因
        order.setOrderState(OrderState.ORDER_STATE_CANCLE); //订单状态
        order.setCancelTime(new Date());
        if(order.getOrderType().equals(1)){
            RetailProfit retailProfit = retailProfitService.find("orderId",order.getId());
            if(retailProfit!=null){
                retailProfit.setState(-1);
                retailProfit.setRemark("待发货订单取消");
                retailProfitService.update(retailProfit);
            }
        }
        //order.settime Date());
        // 修改商品库存和销量
        for (ShopOrderGoods orderGoods : order.getShopOrderGoods()) {
            ShopGoodsSpec goodsSpec = new ShopGoodsSpec();
            goodsSpec.setId(orderGoods.getSpecId());
            goodsSpec.setGoodsId(orderGoods.getGoodsId());
            goodsSpec.setSpecSalenum(-orderGoods.getGoodsNum().intValue());
            ShopGoods goods = goodsDao.find(orderGoods.getGoodsId());
            productService.updateStorage(goodsSpec, goods);
            if (orderGoods.getActivityId() != null) {
                List<ShopActivityGoodsSpec> shopActivityGoodsSpecList = shopActivityGoodsSpecService.findList(
                    Paramap.create().put("activityId", orderGoods.getActivityId())
                        .put("specId", orderGoods.getSpecId()));
                if (shopActivityGoodsSpecList != null && shopActivityGoodsSpecList.size() > 0) {
                    ShopActivityGoodsSpec shopActivityGoodsSpec = shopActivityGoodsSpecList.get(0);
                    shopActivityGoodsSpec
                        .setActivityStock(shopActivityGoodsSpec.getActivityStock() + orderGoods.getGoodsNum());
                    shopActivityGoodsSpecService.update(shopActivityGoodsSpec);
                }
            }

        }
        // 修改订单状态
        // 前端取消订单不需要依据订单锁定状态
        // 后台取消订单需要依据订单锁定状态
        orderDao.update(order);
//        if (opType == Constants.OPERATOR_MEMBER) {
//
//        } else {
//            updateByIdOrderStateLockState(order, OrderState.ORDER_OPERATE_CANCEL);
//        }

        // 订单日志
        ShopOrderLog orderLog = new ShopOrderLog();
        orderLog.setOrderState(OrderState.ORDER_STATE_CANCLE + "");
        orderLog.setId(twiterIdService.getTwiterId());
        if (order.getPaymentState() == 1) {
            orderLog.setChangeState("订单取消成功, 等待退款");
        } else {
            orderLog.setChangeState("取消订单成功");
        }
        orderLog.setStateInfo("取消订单");
        orderLog.setOrderId(order.getId());
        //判断操作人
        switch (opType) {
            case Constants.OPERATOR_MEMBER:
                orderLog.setOperator(order.getBuyerName());
                break;
            case Constants.OPERATOR_SELLER:
            case Constants.OPERATOR_ADMINISTRATOR:
                orderLog.setOperator("管理员:"+opName);
                break;
            case Constants.OPERATOR_TIME_TASK:
                orderLog.setOperator("系统定时取消");
                break;
            default:
                throw new RuntimeException("未知用户类型取消订单");
        }
        orderLog.setCreateTime(new Date());
        //保存订单日志
        orderLogDao.insert(orderLog);

        // 订单退款
        if (order.getPaymentState() == 1) {
            // 余额支付
            if ("balancePaymentPlugin".equals(order.getPaymentCode())) {
                returnBalance(order.getBuyerId(), order);
            } else if ("pointsPaymentPlugin".equals(order.getPaymentCode()) || "cashOnDeliveryPlugin"
                .equals(order.getPaymentCode()) || "replacementPaymentPlugin".equals(order.getPaymentCode())) {
                //积分全额抵消 货到付款 换购订单 replacementPaymentPlugin
                returnPoint(order.getBuyerId(), order,orderLog.getOperator());
            } else {
                addPaymentTally(order, paytrem);
                // 第三方退款 退款失败抛异常
                refund(order);
                //退回积分
                returnPoint(order.getBuyerId(), order,orderLog.getOperator());
            }
        }
        if(order.getPaymentState()==0&&null!=order.getUsePointNum()&&order.getUsePointNum()>0){
            //退回积分
            returnPoint(order.getBuyerId(), order,orderLog.getOperator());
        }
        //取消订单，如果订单有使用优惠券，退还优惠券
        List<CouponDetail> couponDetails = couponDetailService.findList("useOrderId", orderId);
        if(couponDetails!=null&&couponDetails.size()>0){//查找出使用在当前取消订单中的优惠券，退还
            for (CouponDetail couponDetail : couponDetails) {
                //可能在订单取消时，当前订单选取的优惠券已过期，判断是退换优惠券还是过期优惠券
                Coupon coupon = couponService.find(couponDetail.getCouponId());
                if(coupon!=null&&coupon.getStatus()==4){
                    //回收优惠券
                    if(coupon.getUseMoneyFlag()==1){//退款回收 TODO
                        couponDetail.setUseState(3);
                        couponDetail.setUseTime(null);
                        couponDetail.setUseOrderId(null);
                        couponDetail.setUseOrderPayStatus(null);
                        couponDetail.setRefundState(2);
                        couponDetail.setRefundSum(coupon.getCouponPrice());
                        couponDetailService.update(couponDetail);
                        //退款
                        returnCoupon(coupon,couponDetail,opName);
                    }else {//回收
                        couponDetail.setUseState(3);
                        couponDetail.setUseTime(null);
                        couponDetail.setUseOrderId(null);
                        couponDetail.setUseOrderPayStatus(null);
                        couponDetail.setRefundState(0);
                        couponDetail.setRefundSum(BigDecimal.ZERO);
                        couponDetailService.update(couponDetail);
                    }
                }else if(coupon!=null&&coupon.getStatus()==2){
                    //退还优惠券
                    couponDetail.setUseState(2);//修改为未使用
                    couponDetail.setUseTime(null);
                    couponDetail.setUseOrderId(null);
                    couponDetail.setUseOrderPayStatus(null);
                    couponDetailService.update(couponDetail);
                }
                //修改couponUser
                List<CouponUser> couponUsers = couponUserService.findList(Paramap.create().put("couponId",couponDetail.getCouponId()).put("mCode",memberId));
                if(couponUsers==null||couponUsers.size()==0){
                    throw new RuntimeException("优惠券拥有记录异常");
                }
                CouponUser couponUser = couponUsers.get(0);
                couponUser.setUseNum(couponUser.getUseNum()-1);
                couponUser.setOwnNum(couponUser.getOwnNum()+1);
                couponUserService.update(couponUser);
            }
        }
    }

    @Override
    public void updateRemindDelivery(long orderId, long memberId) {
        ShopOrder targetOrder = orderDao.find(orderId);
        if (!targetOrder.getBuyerId().equals(memberId)) {
            throw new RuntimeException("非法操作");
        }

        if (targetOrder.getOrderState() != OrderState.ORDER_STATE_UNFILLED) {
            throw new IllegalStateException("订单状态错误");
        }
        if (targetOrder.getRemindTime() != null) {
            Long hour = ((new Date().getTime() - targetOrder.getRemindTime().getTime()) % 86400000) / 3600000;
            //提醒时间小于4小时不可以进行提醒
            if (hour < 4) {
                throw new StateResult(AppConstants.HAD_REMIND_DELIVERY, "已提醒, 4小时内不可重复提醒");
            }
        }

        ShopOrder order = new ShopOrder();
        order.setId(orderId);
        order.setIsRemind(1);
        order.setRemindTime(new Date());
        orderDao.update(order);
        //TODO 发送推送任务到后台
        Long twiterId = twiterIdService.getTwiterId();
        ShopCommonMessage commonMsg = new ShopCommonMessage();
        commonMsg.setId(twiterId);
        commonMsg.setTitle("发货提醒");
        commonMsg.setContent("订单" + targetOrder.getOrderSn() + "已催发货");
        commonMsg.setBizId(0l);
        commonMsg.setSubject(orderId + "");
        commonMsg.setBizType(3); //1-消息通知  2-提醒信息  3-订单信息 4-留言信息
        commonMsg.setCreateTime(new Date());
        //发送给平台 默认用户id为0L
        commonMsg.setSendUid(0L + "");
        commonMsg.setType(1); //1-系统消息  2-短信消息
        commonMsg.setUType(1); //用户类型 1-个人  2-分组  3-全部用户
        commonMsg.setOnLine(1); //草稿 1-在线  2-草稿
        commonMsg.setIsTop(2); //1-置顶  2-未置顶
        commonMsg.setIsReply(0);//0未回复 1已回复
        shopCommonMessageDao.insert(commonMsg);

    }

    /**
     *
     * @param cartIds     多个购物车id
     * @param memberId    用户id
     * @param orderMsgMap 存储买家留言信息,键为店铺id,值为店铺留言
     * @param addressId   收货地址id
     * @param couponIds   优惠券id
     * @param isPp        优惠积分支付
     * @param platform    0 微信端 1app
     * @param groupBuyActivityId 团购活动id
     * @param groupOrderId 开团订单id
     * @param shopOrderDiscountType 优惠订单类型
     * @param logisticType 快递1 自提2
     * @param paymentType 1在线支付 2货到付款
     * @return
     */
    @Override
    public ShopOrderPay addOrderReturnPaySn(String cartIds, String memberId, Map<String, Object> orderMsgMap,
        Long addressId, String couponIds, Integer isPp, Integer platform, Long groupBuyActivityId,
        Long groupOrderId, ShopOrderDiscountType shopOrderDiscountType, Integer logisticType, Integer paymentType) {
        // 平台优惠券id
        Long couponId = null;
        // 单个订单id
        Long orderId = null;
        //通过用户id查询用户信息
        RdMmBasicInfo member = rdMmBasicInfoService.find("mmCode", memberId);
        //创建一个新的订单支付编号
        String paySn = "P" + Dateutil.getDateString();
        ShopOrderPay orderPay = new ShopOrderPay();
        orderPay.setId(twiterIdService.getTwiterId());
        orderPay.setPaySn(paySn);
        orderPay.setBuyerId(Long.parseLong(memberId));
        orderPay.setApiPayState("0");//设置支付状态0
        //保存订单支付表
        orderPayDao.insert(orderPay);
        //1快递 2自提
        ShopOrderAddress orderAddress = new ShopOrderAddress();
        RdMmAddInfo address = null;
        if (logisticType == 1) {
            /*********************保存发货地址*********************/
            address = rdMmAddInfoService.find("aid", addressId);
            if (address == null) {
                throw new StateResult(AppConstants.RECEIVED_ADDRESS_NOT_EXIT, "收货地址不能为空");
            }
            orderAddress.setIsDefault(Optional.ofNullable(address.getDefaultadd()).orElse(0).toString());
            orderAddress.setId(twiterIdService.getTwiterId());
            orderAddress.setMemberId(Long.parseLong(address.getMmCode()));
            orderAddress.setTrueName(address.getConsigneeName());
            orderAddress.setAddress(address.getAddDetial());
            orderAddress.setMobPhone(address.getMobile());
            orderAddress
                .setAreaInfo(address.getAddProvinceCode() + address.getAddCityCode() + address.getAddCountryCode());
            if ("".equals(address.getAddCountryCode())){
                ShopCommonArea shopCommonArea = areaService.find("areaName", address.getAddCityCode());
                orderAddress.setAreaId(shopCommonArea.getId());
                orderAddress.setCityId(shopCommonArea.getId());
                orderAddress.setProvinceId(shopCommonArea.getAreaParentId());
                orderAddressDao.insert(orderAddress);
            }else{
                List<ShopCommonArea> shopCommonAreas = areaService.findByAreaName(address.getAddCountryCode());//区
                if (shopCommonAreas.size()>1){
                    ShopCommonArea shopCommonCity = areaService.find("areaName", address.getAddCityCode());//市
                    orderAddress.setCityId(shopCommonCity.getId());
                    orderAddress.setProvinceId(shopCommonCity.getAreaParentId());
                    for (ShopCommonArea shopCommonArea : shopCommonAreas) {
                        if (shopCommonArea.getAreaParentId().longValue()==shopCommonCity.getId().longValue()){
                            orderAddress.setAreaId(shopCommonArea.getId());
                        }
                    }
                    orderAddressDao.insert(orderAddress);
                }else{
                    ShopCommonArea shopCommonArea = shopCommonAreas.get(0);
                    orderAddress.setAreaId(shopCommonArea.getId());
                    //if ()
                    orderAddress.setCityId(shopCommonArea.getAreaParentId());
                    ShopCommonArea shopCommonArea2 = areaService.find(shopCommonArea.getAreaParentId());
                    orderAddress.setProvinceId(shopCommonArea2.getAreaParentId());
                    orderAddressDao.insert(orderAddress);
                }
            }

        }
        if (logisticType == 2) {
            /*********************保存发货地址*********************/
            orderAddress.setMemberId(Long.parseLong(memberId));
            orderAddress.setIsDefault("0");
            orderAddress.setTrueName((String) orderMsgMap.get("userName"));
            orderAddress.setMobPhone((String) orderMsgMap.get("userPhone"));
            orderAddress.setAreaInfo("自提没有保存收货地址");
            orderAddress.setId(twiterIdService.getTwiterId());
            orderAddress.setAreaId(-1L);
            orderAddress.setCityId(-1L);
            orderAddress.setProvinceId(-1L);
            orderAddress.setAddress("");
            orderAddressDao.insert(orderAddress);

        }

        /********************* 购物车计算信息 *********************/
        List<CartInfo> cartInfoList = cartService.queryCartInfoList(cartIds, shopOrderDiscountType, address, memberId);
        if (CollectionUtils.isEmpty(cartInfoList)) {
            throw new RuntimeException("购物车不存在");
        }
        /*********************订单相关金额计算*********************/
        OrderSettlement orderSettlement = getAmount(cartInfoList, couponIds, isPp);
        /*********************保存订单+订单项*********************/
        for (OrderVo orderVo : orderSettlement.getOrderVoList()) {
            ShopOrder order = new ShopOrder();
            //该项目只有自营
            order.setStoreId(0L);
            order.setLogisticType(logisticType);
            order.setStoreName("自营商店");
            order.setId(twiterIdService.getTwiterId());
            orderId = order.getId();
            order.setBrandId(orderVo.getBrandId());
            order.setBrandName(orderVo.getBrandName());
            order.setBuyerId(Long.parseLong(member.getMmCode()));
            order.setBuyerName(member.getMmNickName());
            order.setPredepositAmount(BigDecimal.ZERO);
            //没有支付id 暂时为0L
            order.setPaymentId(0L);
            order.setPaymentCode("");
            order.setShippingExpressId(0L);
            order.setEvalsellerStatus(0L);
            order.setOrderPointscount(0);
            // 发货地址id,暂时写死
            order.setDaddressId(0L);
            order.setAddressId(orderAddress.getId());
            //支付表id
            order.setPayId(orderPay.getId());
            //订单类型
            order.setOrderType(shopOrderDiscountType.getPreferentialType());
            //订单类型id
            order.setShopOrderTypeId(shopOrderDiscountType.getId());
            //若支付完成
            if (orderSettlement.getOrderAmount().doubleValue() == 0) {//购物车集合计算所需支付金额为0 视为已经支付订单
                order.setOrderState(OrderState.ORDER_STATE_UNFILLED);
                order.setPaymentState(OrderState.PAYMENT_STATE_YES); //付款状态
                order.setPaymentTime(new Date());
                String period = rdSysPeriodDao.getSysPeriodService(new Date());
                order.setCreationPeriod(period);

            } //未支付完成
            else if (orderSettlement.getOrderAmount().doubleValue() > 0) {
                order.setOrderState(OrderState.ORDER_STATE_NO_PATMENT);
                order.setPaymentState(OrderState.PAYMENT_STATE_NO);
            } else {
                throw new RuntimeException("订单计算出错");
            }
            SnowFlake snowFlake = new SnowFlake(0, 0);
            if (platform == OrderState.PLATFORM_WECHAT) {
                order.setOrderSn("WX" + snowFlake.nextId());
            } else if (platform == OrderState.PLATFORM_APP) {
                order.setOrderSn("AP" + snowFlake.nextId());
            } else if (platform == OrderState.PLATFORM_PC) {
                order.setOrderSn("PC" + snowFlake.nextId());
            } else {
                throw new IllegalStateException("创建订单平台错误");
            }

            order.setBuyerPhone(member.getMobile());
            order.setIsDel(0);
            order.setPaySn(paySn); //支付表编号
            order.setLockState(OrderState.ORDER_LOCK_STATE_NO); //订单锁定状态:正常
            if (paymentType == 1) {
                order.setPaymentName("在线支付"); //支付方式名称
            } else {
                order.setPaymentName("货到付款"); //支付方式名称
            }

            order.setOrderPlatform(platform);
            order.setEvaluationStatus(0);
            order.setCreateTime(new Date()); //订单生成时间
            order.setBarterState(OrderState.BARTER_STATE_NO);//无退货
            order.setBarterNum(0);//换货数量初始化为0
            order.setOrderMessage(Optional.ofNullable(orderMsgMap.get("orderMessages")).orElse("") + ""); //订单留言
            // TODO 发送队列
            // order.setGiftCoupon(JacksonUtil.toJson(cartVo.getGiftCoupons())); // 赠送优惠卷集合
            /********************* 相关金额赋值 *********************/
            /**********应付统计************/
            // 无其他费用（运费等) 订单总价格=商品总价格
            // 订单总价格
            order.setOrderTotalPrice(orderVo.getOrderAmount());
            //运费
            order.setShippingFee(orderVo.getFreightAmount());
            //优惠金额
            order.setDiscount(orderVo.getCouponAmount());
            // 商品总价格
            order.setGoodsAmount(orderVo.getGoodsAmount());
            //订单pv值
            order.setPpv(orderVo.getPpv());
            //订单运费优惠价格
            order.setShippingPreferentialFee(orderVo.getPreferentialFreightAmount());
            order.setOrderPlatform(2);
            /**********支付统计************/
            // 现金支付
            order.setOrderAmount(orderVo.getOrderAmount());
            // 使用积分抵扣金额
            order.setPointRmbNum(Optional.ofNullable(orderVo.getRewardPointPrice()).orElse(BigDecimal.ZERO));
            // 优惠券金额
//            order.setCouponId(orderVo.getCouponId()); //优惠券id
//            order.setCouponPrice(orderVo.getCouponPrice());
            // 店铺优惠券（代金券）
//            order.setVoucherId(orderVo.getVoucherId());
//            order.setVoucherPrice(orderVo.getVoucherPrice());

            //            // 优惠总金额
            //            order.setDiscount(orderVo.getDiscount());
            //            //促销优惠金额
            //            order.setPromoPrice(orderVo.getPromoPrice());
            orderDao.insertEntity(order);
            // todo 推荐反拥

            /*********************订单项*********************/
            for (CartOrderVo cartOrderVo : orderVo.getCartOrderVoList()) {
                ShopOrderGoods orderGoods = new ShopOrderGoods();
                orderGoods.setId(twiterIdService.getTwiterId());
                orderGoods.setGoodsId(cartOrderVo.getGoodsId());
                orderGoods.setGoodsImage(cartOrderVo.getGoodsImages());
                orderGoods.setGoodsName(cartOrderVo.getGoodsName());
                orderGoods.setGoodsNum(cartOrderVo.getGoodsNum().intValue());
                orderGoods.setSpecId(cartOrderVo.getSpecId());
                orderGoods.setSpecInfo(cartOrderVo.getSpecInfo());
                orderGoods.setBuyerId(order.getBuyerId() + "");
                orderGoods.setOrderId(order.getId());
                orderGoods.setEvaluationStatus(0);
                orderGoods.setGoodsReturnnum(0);
                orderGoods.setGoodsBarternum(0);
                orderGoods.setShippingGoodsNum(0);
                orderGoods.setGoodsType(cartOrderVo.getGoodsType());
                orderGoods.setWeight(cartOrderVo.getWeight());
                // TODO 金额总汇
                //零售价
                orderGoods.setMarketPrice(cartOrderVo.getGoodsRetailPrice());
                //大单价
                orderGoods.setGoodsPrice(cartOrderVo.getGoodsBigPrice());
                //会员价
                orderGoods.setVipPrice(cartOrderVo.getGoodsMemberPrice());
                if (shopOrderDiscountType.getPreferentialType() == ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_MEMBER) {
                    orderGoods.setGoodsPayPrice(cartOrderVo.getGoodsMemberPrice());
                } else if (shopOrderDiscountType.getPreferentialType()
                    == ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_PPV) {
                    orderGoods.setGoodsPayPrice(cartOrderVo.getGoodsBigPrice());
                } else if (shopOrderDiscountType.getPreferentialType()
                    == ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_PREFERENTIAL) {
                    BigDecimal money = cartOrderVo.getGoodsRetailPrice()
                        .subtract(shopOrderDiscountType.getPreferential());
                    if (money.compareTo(new BigDecimal("0")) == -1) {
                        money = new BigDecimal("0");
                    }
                    orderGoods.setGoodsPayPrice(money);
                } else {
                    orderGoods.setGoodsPayPrice(cartOrderVo.getGoodsRetailPrice());
                }
                orderGoods.setPpv(cartOrderVo.getPpv());
                orderGoods.setBigPpv(cartOrderVo.getBigPpv());
                orderGoods.setRewardPointPrice(cartOrderVo.getGoodsRewardPointPrice());
                orderGoods.setActivityId(cartOrderVo.getActivityId());
                orderGoods.setActivityType(cartOrderVo.getActivityType());
                orderGoods.setStoresId(0L);

                //默认0
                orderGoods.setShippingExpressId(0L);
                orderGoodsDao.insert(orderGoods);
                //删除购物数据
                cartService.delete(cartOrderVo.getId());
                /*********************更新商品库存+销售数量*********************/
                ShopGoodsSpec goodsSpec = new ShopGoodsSpec();
                goodsSpec.setId(cartOrderVo.getSpecId());
                goodsSpec.setGoodsId(cartOrderVo.getGoodsId());
                goodsSpec.setSpecSalenum(cartOrderVo.getGoodsNum().intValue());
                ShopGoods goods = goodsDao.find(cartOrderVo.getGoodsId());
                ShopGoodsSpec shopGoodsSpec = goodsSpecDao.find(cartOrderVo.getSpecId());
                if (cartOrderVo.getGoodsNum() > shopGoodsSpec.getSpecGoodsStorage()) {
                    throw new RuntimeException("购买数量大于库存");
                }
                productService.updateStorage(goodsSpec, goods);
                if (cartOrderVo.getActivityId() != null) {
                    List<ShopActivityGoodsSpec> shopActivityGoodsSpecList = shopActivityGoodsSpecService.findList(
                        Paramap.create().put("activityId", cartOrderVo.getActivityId())
                            .put("specId", cartOrderVo.getSpecId()));
                    if (shopActivityGoodsSpecList != null && shopActivityGoodsSpecList.size() > 0) {
                        ShopActivityGoodsSpec shopActivityGoodsSpec = shopActivityGoodsSpecList.get(0);
                        shopActivityGoodsSpec
                            .setActivityStock(shopActivityGoodsSpec.getActivityStock() - cartOrderVo.getGoodsNum());
                        shopActivityGoodsSpecService.update(shopActivityGoodsSpec);
                    }
                }

            }
            /*********************保存日志*********************/
            ShopOrderLog orderLog = new ShopOrderLog();
            orderLog.setId(twiterIdService.getTwiterId());
            orderLog.setOperator(member.getMmCode().toString());
            orderLog.setChangeState(OrderState.ORDER_STATE_UNFILLED + "");
            orderLog.setOrderId(order.getId());
            orderLog.setOrderState(OrderState.ORDER_STATE_NO_PATMENT + "");
            orderLog.setStateInfo("提交订单");
            orderLog.setCreateTime(new Date());
            orderLogDao.insert(orderLog);
        }

        //根据payId查询订单列表
        orderPay.setOrderCreateTime(new Date());
        orderPay.setPayAmount(orderSettlement.getOrderAmount());
        orderPay.setOrderTotalPrice(orderSettlement.getGoodsAmount());
        orderPay.setOrderId(orderId);
        orderPay.setPaymentType(paymentType);
        return orderPay;
    }

    /**
     *
     * @param cartIds     多个购物车id
     * @param memberId    用户id
     * @param orderMsgMap 存储买家留言信息,键为店铺id,值为店铺留言
     * @param addressId   收货地址id
     * @param couponIds   优惠券id
     * @param isPp        优惠积分支付
     * @param platform    0 微信端 1app
     * @param groupBuyActivityId 团购活动id
     * @param groupOrderId 开团订单id
     * @param shopOrderDiscountType 优惠订单类型
     * @param logisticType 快递1 自提2
     * @param paymentType 1在线支付 2货到付款
     * @param giftNum
     * @return
     */
    @Override
    public ShopOrderPay addOrderReturnPaySnNew(String cartIds, String memberId, Map<String, Object> orderMsgMap,
                                               Long addressId, String couponIds, Integer isPp, Integer platform, Long groupBuyActivityId,
                                               Long groupOrderId, ShopOrderDiscountType shopOrderDiscountType, Integer logisticType, Integer paymentType, Long giftId, Integer giftNum) {
        // 平台优惠券id
        Long couponId = null;
        // 单个订单id
        Long orderId = null;
        //通过用户id查询用户信息
        RdMmBasicInfo member = rdMmBasicInfoService.find("mmCode", memberId);
        //创建一个新的订单支付编号
        String paySn = "P" + Dateutil.getDateString();
        ShopOrderPay orderPay = new ShopOrderPay();
        orderPay.setId(twiterIdService.getTwiterId());
        orderPay.setPaySn(paySn);
        orderPay.setBuyerId(Long.parseLong(memberId));
        orderPay.setApiPayState("0");//设置支付状态0
        //保存订单支付表
        orderPayDao.insert(orderPay);
        //1快递 2自提
        ShopOrderAddress orderAddress = new ShopOrderAddress();
        RdMmAddInfo address = null;
        if (logisticType == 1) {
            /*********************保存发货地址*********************/
            address = rdMmAddInfoService.find("aid", addressId);
            if (address == null) {
                throw new StateResult(AppConstants.RECEIVED_ADDRESS_NOT_EXIT, "收货地址不能为空");
            }
            orderAddress.setIsDefault(Optional.ofNullable(address.getDefaultadd()).orElse(0).toString());
            orderAddress.setId(twiterIdService.getTwiterId());
            orderAddress.setMemberId(Long.parseLong(address.getMmCode()));
            orderAddress.setTrueName(address.getConsigneeName());
            orderAddress.setAddress(address.getAddDetial());
            orderAddress.setMobPhone(address.getMobile());
            orderAddress
                    .setAreaInfo(address.getAddProvinceCode() + address.getAddCityCode() + address.getAddCountryCode());
            if ("".equals(address.getAddCountryCode())){
                ShopCommonArea shopCommonArea = areaService.find("areaName", address.getAddCityCode());
                if (shopCommonArea==null) {
                    throw new RuntimeException("请检查APP是否最新版本，并重新添加地址");
                }
                orderAddress.setAreaId(shopCommonArea.getId());
                orderAddress.setCityId(shopCommonArea.getId());
                orderAddress.setProvinceId(shopCommonArea.getAreaParentId());
                orderAddressDao.insert(orderAddress);
            }else{
                List<ShopCommonArea> shopCommonAreas = areaService.findByAreaName(address.getAddCountryCode());//区
                if (CollectionUtils.isEmpty(shopCommonAreas)) {
                    throw new RuntimeException("请检查APP是否最新版本，并重新添加地址");
                }
                if (shopCommonAreas.size()>1){
                    ShopCommonArea shopCommonCity = areaService.find("areaName", address.getAddCityCode());//市
                    if (shopCommonCity==null) {
                        throw new RuntimeException("请检查APP是否最新版本，并重新添加地址");
                    }
                    orderAddress.setCityId(shopCommonCity.getId());
                    orderAddress.setProvinceId(shopCommonCity.getAreaParentId());
                    for (ShopCommonArea shopCommonArea : shopCommonAreas) {
                        if (shopCommonArea.getAreaParentId().longValue()==shopCommonCity.getId().longValue()){
                            orderAddress.setAreaId(shopCommonArea.getId());
                        }
                    }
                    orderAddressDao.insert(orderAddress);
                }else{
                    ShopCommonArea shopCommonArea = shopCommonAreas.get(0);
                    orderAddress.setAreaId(shopCommonArea.getId());
                    //if ()
                    orderAddress.setCityId(shopCommonArea.getAreaParentId());
                    ShopCommonArea shopCommonArea2 = areaService.find(shopCommonArea.getAreaParentId());
                    if (shopCommonArea2==null) {
                        throw new RuntimeException("请检查APP是否最新版本，并重新添加地址");
                    }
                    orderAddress.setProvinceId(shopCommonArea2.getAreaParentId());
                    orderAddressDao.insert(orderAddress);
                }
            }

        }
        if (logisticType == 2) {
            /*********************保存发货地址*********************/
            orderAddress.setMemberId(Long.parseLong(memberId));
            orderAddress.setIsDefault("0");
            orderAddress.setTrueName((String) orderMsgMap.get("userName"));
            orderAddress.setMobPhone((String) orderMsgMap.get("userPhone"));
            orderAddress.setAreaInfo("自提没有保存收货地址");
            orderAddress.setId(twiterIdService.getTwiterId());
            orderAddress.setAreaId(-1L);
            orderAddress.setCityId(-1L);
            orderAddress.setProvinceId(-1L);
            orderAddress.setAddress("");
            orderAddressDao.insert(orderAddress);

        }

        /********************* 购物车计算信息 *********************/
        List<CartInfo> cartInfoList = cartService.queryCartInfoList(cartIds, shopOrderDiscountType, address, memberId);
        if (CollectionUtils.isEmpty(cartInfoList)) {
            throw new RuntimeException("购物车不存在");
        }
        /*********************订单相关金额计算*********************/
        OrderSettlement orderSettlement = getAmount(cartInfoList, couponIds, isPp);
        /*********************保存订单+订单项*********************/
        for (OrderVo orderVo : orderSettlement.getOrderVoList()) {
            ShopOrder order = new ShopOrder();
            //该项目只有自营
            order.setStoreId(0L);
            order.setLogisticType(logisticType);
            order.setStoreName("自营商店");
            order.setId(twiterIdService.getTwiterId());
            orderId = order.getId();
            order.setBrandId(orderVo.getBrandId());
            order.setBrandName(orderVo.getBrandName());
            order.setBuyerId(Long.parseLong(member.getMmCode()));
            order.setBuyerName(member.getMmNickName());
            order.setPredepositAmount(BigDecimal.ZERO);
            //没有支付id 暂时为0L
            order.setPaymentId(0L);
            order.setPaymentCode("");
            order.setShippingExpressId(0L);
            order.setEvalsellerStatus(0L);
            order.setOrderPointscount(0);
            // 发货地址id,暂时写死
            order.setDaddressId(0L);
            order.setAddressId(orderAddress.getId());
            //支付表id
            order.setPayId(orderPay.getId());
            //订单类型
            order.setOrderType(shopOrderDiscountType.getPreferentialType());
            //订单类型id
            order.setShopOrderTypeId(shopOrderDiscountType.getId());
            //若支付完成
            if (orderSettlement.getOrderAmount().doubleValue() == 0) {//购物车集合计算所需支付金额为0 视为已经支付订单
                order.setOrderState(OrderState.ORDER_STATE_UNFILLED);
                order.setPaymentState(OrderState.PAYMENT_STATE_YES); //付款状态
                order.setPaymentTime(new Date());
                String period = rdSysPeriodDao.getSysPeriodService(new Date());
                order.setCreationPeriod(period);

            } //未支付完成
            else if (orderSettlement.getOrderAmount().doubleValue() > 0) {
                order.setOrderState(OrderState.ORDER_STATE_NO_PATMENT);
                order.setPaymentState(OrderState.PAYMENT_STATE_NO);
            } else {
                throw new RuntimeException("订单计算出错");
            }
            SnowFlake snowFlake = new SnowFlake(0, 0);
            if (platform == OrderState.PLATFORM_WECHAT) {
                order.setOrderSn("WX" + snowFlake.nextId());
            } else if (platform == OrderState.PLATFORM_APP) {
                order.setOrderSn("AP" + snowFlake.nextId());
            } else if (platform == OrderState.PLATFORM_PC) {
                order.setOrderSn("PC" + snowFlake.nextId());
            } else {
                throw new IllegalStateException("创建订单平台错误");
            }

            order.setBuyerPhone(member.getMobile());
            order.setIsDel(0);
            order.setPaySn(paySn); //支付表编号
            order.setLockState(OrderState.ORDER_LOCK_STATE_NO); //订单锁定状态:正常
            if (paymentType == 1) {
                order.setPaymentName("在线支付"); //支付方式名称
            } else {
                order.setPaymentName("货到付款"); //支付方式名称
            }

            order.setOrderPlatform(platform);
            order.setEvaluationStatus(0);
            order.setCreateTime(new Date()); //订单生成时间
            order.setBarterState(OrderState.BARTER_STATE_NO);//无退货
            order.setBarterNum(0);//换货数量初始化为0
            order.setOrderMessage(Optional.ofNullable(orderMsgMap.get("orderMessages")).orElse("") + ""); //订单留言
            // TODO 发送队列
            // order.setGiftCoupon(JacksonUtil.toJson(cartVo.getGiftCoupons())); // 赠送优惠卷集合
            /********************* 相关金额赋值 *********************/
            /**********应付统计************/
            // 无其他费用（运费等) 订单总价格=商品总价格
            // 订单总价格
            order.setOrderTotalPrice(orderVo.getOrderAmount());
            //运费
            order.setShippingFee(orderVo.getFreightAmount());
            //优惠金额
            order.setDiscount(orderVo.getCouponAmount());
            // 商品总价格
            order.setGoodsAmount(orderVo.getGoodsAmount());
            //订单pv值
            order.setPpv(orderVo.getPpv());
            //订单运费优惠价格
            order.setShippingPreferentialFee(orderVo.getPreferentialFreightAmount());
            order.setOrderPlatform(2);
            /**********支付统计************/
            // 现金支付
            order.setOrderAmount(orderVo.getOrderAmount());
            // 使用积分抵扣金额
            order.setPointRmbNum(Optional.ofNullable(orderVo.getRewardPointPrice()).orElse(BigDecimal.ZERO));
            // 优惠券金额
//            order.setCouponId(orderVo.getCouponId()); //优惠券id
//            order.setCouponPrice(orderVo.getCouponPrice());
            // 店铺优惠券（代金券）
//            order.setVoucherId(orderVo.getVoucherId());
//            order.setVoucherPrice(orderVo.getVoucherPrice());

            //            // 优惠总金额
            //            order.setDiscount(orderVo.getDiscount());
            //            //促销优惠金额
            //            order.setPromoPrice(orderVo.getPromoPrice());
            orderDao.insertEntity(order);
            // todo 推荐反拥

            /*********************订单项*********************/
            for (CartOrderVo cartOrderVo : orderVo.getCartOrderVoList()) {
                ShopOrderGoods orderGoods = new ShopOrderGoods();
                orderGoods.setId(twiterIdService.getTwiterId());
                orderGoods.setGoodsId(cartOrderVo.getGoodsId());
                orderGoods.setGoodsImage(cartOrderVo.getGoodsImages());
                orderGoods.setGoodsName(cartOrderVo.getGoodsName());
                orderGoods.setGoodsNum(cartOrderVo.getGoodsNum().intValue());
                orderGoods.setSpecId(cartOrderVo.getSpecId());
                orderGoods.setSpecInfo(cartOrderVo.getSpecInfo());
                orderGoods.setBuyerId(order.getBuyerId() + "");
                orderGoods.setOrderId(order.getId());
                orderGoods.setEvaluationStatus(0);
                orderGoods.setGoodsReturnnum(0);
                orderGoods.setGoodsBarternum(0);
                orderGoods.setShippingGoodsNum(0);
                orderGoods.setGoodsType(cartOrderVo.getGoodsType());
                orderGoods.setWeight(cartOrderVo.getWeight());
                // TODO 金额总汇
                //零售价
                orderGoods.setMarketPrice(cartOrderVo.getGoodsRetailPrice());
                //大单价
                orderGoods.setGoodsPrice(cartOrderVo.getGoodsBigPrice());
                //会员价
                orderGoods.setVipPrice(cartOrderVo.getGoodsMemberPrice());
                if (shopOrderDiscountType.getPreferentialType() == ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_MEMBER) {
                    orderGoods.setGoodsPayPrice(cartOrderVo.getGoodsMemberPrice());
                } else if (shopOrderDiscountType.getPreferentialType()
                        == ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_PPV) {
                    orderGoods.setGoodsPayPrice(cartOrderVo.getGoodsBigPrice());
                } else if (shopOrderDiscountType.getPreferentialType()
                        == ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_PREFERENTIAL) {
                    BigDecimal money = cartOrderVo.getGoodsRetailPrice()
                            .subtract(shopOrderDiscountType.getPreferential());
                    if (money.compareTo(new BigDecimal("0")) == -1) {
                        money = new BigDecimal("0");
                    }
                    orderGoods.setGoodsPayPrice(money);
                } else {
                    orderGoods.setGoodsPayPrice(cartOrderVo.getGoodsRetailPrice());
                }
                orderGoods.setPpv(cartOrderVo.getPpv());
                orderGoods.setBigPpv(cartOrderVo.getBigPpv());
                orderGoods.setRewardPointPrice(cartOrderVo.getGoodsRewardPointPrice());
                orderGoods.setActivityId(cartOrderVo.getActivityId());
                orderGoods.setActivityType(cartOrderVo.getActivityType());
                orderGoods.setStoresId(0L);

                //默认0
                orderGoods.setShippingExpressId(0L);
                orderGoodsDao.insert(orderGoods);
                //删除购物数据
                cartService.delete(cartOrderVo.getId());
                /*********************更新商品库存+销售数量*********************/
                ShopGoodsSpec goodsSpec = new ShopGoodsSpec();
                goodsSpec.setId(cartOrderVo.getSpecId());
                goodsSpec.setGoodsId(cartOrderVo.getGoodsId());
                goodsSpec.setSpecSalenum(cartOrderVo.getGoodsNum().intValue());
                ShopGoods goods = goodsDao.find(cartOrderVo.getGoodsId());
                ShopGoodsSpec shopGoodsSpec = goodsSpecDao.find(cartOrderVo.getSpecId());
                if (cartOrderVo.getGoodsNum() > shopGoodsSpec.getSpecGoodsStorage()) {
                    throw new RuntimeException("购买数量大于库存");
                }
                productService.updateStorage(goodsSpec, goods);
                if (cartOrderVo.getActivityId() != null) {
                    List<ShopActivityGoodsSpec> shopActivityGoodsSpecList = shopActivityGoodsSpecService.findList(
                            Paramap.create().put("activityId", cartOrderVo.getActivityId())
                                    .put("specId", cartOrderVo.getSpecId()));
                    if (shopActivityGoodsSpecList != null && shopActivityGoodsSpecList.size() > 0) {
                        ShopActivityGoodsSpec shopActivityGoodsSpec = shopActivityGoodsSpecList.get(0);
                        shopActivityGoodsSpec
                                .setActivityStock(shopActivityGoodsSpec.getActivityStock() - cartOrderVo.getGoodsNum());
                        shopActivityGoodsSpecService.update(shopActivityGoodsSpec);
                    }
                }

            }

            /*处理2019年双十一活动**********************开始*********************/ //TODO
            if(giftId!=null){
                ShopGoods shopGoods = shopGoodsService.find(giftId);
                if(shopGoods==null){
                    throw new RuntimeException("所选赠品不存在");
                }
                ShopGoodsSpec goodsSpec = shopGoodsSpecService.find("goodsId", giftId);
                if(goodsSpec==null){
                    throw new RuntimeException("所选赠品不存在");
                }
                if(goodsSpec.getSpecGoodsStorage()<giftNum){
                    throw new RuntimeException("所选赠品已赠完，请选择其他类型赠品");
                }
                ShopOrderGoods orderGoods = new ShopOrderGoods();
                orderGoods.setId(twiterIdService.getTwiterId());
                orderGoods.setOrderId(order.getId());
                orderGoods.setGoodsId(giftId);
                orderGoods.setGoodsName(shopGoods.getGoodsName());
                orderGoods.setSpecId(goodsSpec.getId());
                //大单价
                orderGoods.setGoodsPrice(goodsSpec.getSpecBigPrice());
                orderGoods.setGoodsNum(giftNum);
                orderGoods.setGoodsImage(shopGoods.getGoodsImage());
                orderGoods.setGoodsReturnnum(0);
                orderGoods.setGoodsType(shopGoods.getGoodsType());
                orderGoods.setRefundAmount(BigDecimal.ZERO);
                orderGoods.setStoresId(0L);
                orderGoods.setEvaluationStatus(0);
                orderGoods.setGoodsPayPrice(BigDecimal.ZERO);
                orderGoods.setBuyerId(order.getBuyerId() + "");
                orderGoods.setIsPresentation(1);
                orderGoods.setMarketPrice(goodsSpec.getSpecRetailPrice());
                orderGoods.setPpv(goodsSpec.getPpv());
                orderGoods.setBigPpv(goodsSpec.getBigPpv());
                orderGoods.setShippingExpressId(0L);
                orderGoods.setVipPrice(goodsSpec.getSpecMemberPrice());
                orderGoods.setWeight(goodsSpec.getWeight());
                orderGoods.setShippingGoodsNum(0);
                orderGoodsDao.insert(orderGoods);
                ShopGoodsSpec goodsSpec1 = new ShopGoodsSpec();
                goodsSpec1.setId(goodsSpec.getId());
                goodsSpec1.setGoodsId(shopGoods.getId());
                goodsSpec1.setSpecSalenum(giftNum);
                productService.updateStorage(goodsSpec1, shopGoods);
            }
            /*处理2019年双十一活动**********************结束*********************/

            /*********************保存日志*********************/
            ShopOrderLog orderLog = new ShopOrderLog();
            orderLog.setId(twiterIdService.getTwiterId());
            orderLog.setOperator(member.getMmCode().toString());
            orderLog.setChangeState(OrderState.ORDER_STATE_UNFILLED + "");
            orderLog.setOrderId(order.getId());
            orderLog.setOrderState(OrderState.ORDER_STATE_NO_PATMENT + "");
            orderLog.setStateInfo("提交订单");
            orderLog.setCreateTime(new Date());
            orderLogDao.insert(orderLog);
        }

        //根据payId查询订单列表
        orderPay.setOrderCreateTime(new Date());
        orderPay.setPayAmount(orderSettlement.getOrderAmount());
        orderPay.setOrderTotalPrice(orderSettlement.getGoodsAmount());
        orderPay.setOrderId(orderId);
        orderPay.setPaymentType(paymentType);
        return orderPay;
    }

    @Override
    public ShopOrderPay addOrderReturnPaySnNew1(String cartIds, String memberId, Map<String, Object> orderMsgMap, Long addressId,
                                                Long couponId, Integer isPp, Integer platform, Long groupBuyActivityId, Long groupOrderId, ShopOrderDiscountType shopOrderDiscountType, Integer logisticType, Integer paymentType, Long giftId, Integer giftNum) {
        // 平台优惠券id
        // 单个订单id
        Long orderId = null;
        //通过用户id查询用户信息
        RdMmBasicInfo member = rdMmBasicInfoService.find("mmCode", memberId);
        //创建一个新的订单支付编号
        String paySn = "P" + Dateutil.getDateString();
        ShopOrderPay orderPay = new ShopOrderPay();
        orderPay.setId(twiterIdService.getTwiterId());
        orderPay.setPaySn(paySn);
        orderPay.setBuyerId(Long.parseLong(memberId));
        orderPay.setApiPayState("0");//设置支付状态0
        //保存订单支付表
        orderPayDao.insert(orderPay);
        //1快递 2自提
        ShopOrderAddress orderAddress = new ShopOrderAddress();
        RdMmAddInfo address = null;
        if (logisticType == 1) {
            /*********************保存发货地址*********************/
            address = rdMmAddInfoService.find("aid", addressId);
            if (address == null) {
                throw new StateResult(AppConstants.RECEIVED_ADDRESS_NOT_EXIT, "收货地址不能为空");
            }
            orderAddress.setIsDefault(Optional.ofNullable(address.getDefaultadd()).orElse(0).toString());
            orderAddress.setId(twiterIdService.getTwiterId());
            orderAddress.setMemberId(Long.parseLong(address.getMmCode()));
            orderAddress.setTrueName(address.getConsigneeName());
            orderAddress.setAddress(address.getAddDetial());
            orderAddress.setMobPhone(address.getMobile());
            orderAddress
                    .setAreaInfo(address.getAddProvinceCode() + address.getAddCityCode() + address.getAddCountryCode());
            if ("".equals(address.getAddCountryCode())){
                ShopCommonArea shopCommonArea = areaService.find("areaName", address.getAddCityCode());
                if (shopCommonArea==null) {
                    throw new RuntimeException("请检查APP是否最新版本，并重新添加地址");
                }
                orderAddress.setAreaId(shopCommonArea.getId());
                orderAddress.setCityId(shopCommonArea.getId());
                orderAddress.setProvinceId(shopCommonArea.getAreaParentId());
                orderAddressDao.insert(orderAddress);
            }else{
                List<ShopCommonArea> shopCommonAreas = areaService.findByAreaName(address.getAddCountryCode());//区
                if (CollectionUtils.isEmpty(shopCommonAreas)) {
                    throw new RuntimeException("请检查APP是否最新版本，并重新添加地址");
                }
                if (shopCommonAreas.size()>1){
                    ShopCommonArea shopCommonCity = areaService.find("areaName", address.getAddCityCode());//市
                    if (shopCommonCity==null) {
                        throw new RuntimeException("请检查APP是否最新版本，并重新添加地址");
                    }
                    orderAddress.setCityId(shopCommonCity.getId());
                    orderAddress.setProvinceId(shopCommonCity.getAreaParentId());
                    for (ShopCommonArea shopCommonArea : shopCommonAreas) {
                        if (shopCommonArea.getAreaParentId().longValue()==shopCommonCity.getId().longValue()){
                            orderAddress.setAreaId(shopCommonArea.getId());
                        }
                    }
                    orderAddressDao.insert(orderAddress);
                }else{
                    ShopCommonArea shopCommonArea = shopCommonAreas.get(0);
                    orderAddress.setAreaId(shopCommonArea.getId());
                    //if ()
                    orderAddress.setCityId(shopCommonArea.getAreaParentId());
                    ShopCommonArea shopCommonArea2 = areaService.find(shopCommonArea.getAreaParentId());
                    if (shopCommonArea2==null) {
                        throw new RuntimeException("请检查APP是否最新版本，并重新添加地址");
                    }
                    orderAddress.setProvinceId(shopCommonArea2.getAreaParentId());
                    orderAddressDao.insert(orderAddress);
                }
            }

        }
        if (logisticType == 2) {
            /*********************保存发货地址*********************/
            orderAddress.setMemberId(Long.parseLong(memberId));
            orderAddress.setIsDefault("0");
            orderAddress.setTrueName((String) orderMsgMap.get("userName"));
            orderAddress.setMobPhone((String) orderMsgMap.get("userPhone"));
            orderAddress.setAreaInfo("自提没有保存收货地址");
            orderAddress.setId(twiterIdService.getTwiterId());
            orderAddress.setAreaId(-1L);
            orderAddress.setCityId(-1L);
            orderAddress.setProvinceId(-1L);
            orderAddress.setAddress("");
            orderAddressDao.insert(orderAddress);

        }
        List<CartInfo> cartInfoList =new ArrayList<>();
        /********************* 购物车计算信息 *********************/
        CouponDetail couponDetail=null;
        if(couponId!=null){//订单使用优惠券
            List<Coupon> list = couponService.findList(Paramap.create().put("id",couponId).put("status",2));
            if(list==null||list.size()==0){
                throw new RuntimeException("订单支付选择优惠券异常");
            }
            List<CouponUser> list1 = couponUserService.findList(Paramap.create().put("mCode",memberId).put("couponId",couponId));
            if(list1==null||list1.size()==0){
                throw new RuntimeException("您没有当前选择优惠券");
            }
            CouponUser couponUser = list1.get(0);
            if(couponUser.getOwnNum()<=0){
                throw new RuntimeException("您没有当前选择优惠券");
            }
            if(couponUser.getUseAbleNum()!=0&&couponUser.getUseNum()>=couponUser.getUseAbleNum()){
                throw new RuntimeException("您当前选择的优惠券已到达使用数量上限");
            }
            //处理couponUser数据
            couponUser.setOwnNum(couponUser.getOwnNum()-1);//拥有数量减1
            couponUser.setUseNum(couponUser.getUseNum()+1);//使用数量加1
            couponUserService.update(couponUser);
            //处理couponDetail数据
            List<CouponDetail> couponDetails = couponDetailService.findList(Paramap.create().put("couponId",couponId).put("holdId",memberId)
                    .put("useState",2));
            if(couponDetails==null||couponDetails.size()==0){
                throw new RuntimeException("您没有当前选择优惠券");
            }
            couponDetail = couponDetails.get(0);//获得当前订单需要消费的优惠券
            cartInfoList = cartService.queryCartInfoList1(cartIds, shopOrderDiscountType, address, memberId,couponId);
        }else {//订单不使用优惠券
            cartInfoList = cartService.queryCartInfoList(cartIds, shopOrderDiscountType, address, memberId);
        }
        if (CollectionUtils.isEmpty(cartInfoList)) {
            throw new RuntimeException("购物车不存在");
        }
        /*********************订单相关金额计算*********************/
        OrderSettlement orderSettlement = getAmount(cartInfoList, null, isPp);
        CartInfo cartInfo = cartInfoList.get(0);
        /*********************保存订单+订单项*********************/
        //不分单  一次购买只能使用一张优惠券
        Boolean flag=true;
        for (OrderVo orderVo : orderSettlement.getOrderVoList()) {
            ShopOrder order = new ShopOrder();
            //该项目只有自营
            order.setStoreId(0L);
            order.setLogisticType(logisticType);
            order.setStoreName("自营商店");
            order.setId(twiterIdService.getTwiterId());
            orderId = order.getId();
            order.setBrandId(orderVo.getBrandId());
            order.setBrandName(orderVo.getBrandName());
            order.setBuyerId(Long.parseLong(member.getMmCode()));
            order.setBuyerName(member.getMmNickName());
            order.setPredepositAmount(BigDecimal.ZERO);
            //没有支付id 暂时为0L
            order.setPaymentId(0L);
            order.setPaymentCode("");
            order.setShippingExpressId(0L);
            order.setEvalsellerStatus(0L);
            order.setOrderPointscount(0);
            // 发货地址id,暂时写死
            order.setDaddressId(0L);
            order.setAddressId(orderAddress.getId());
            //支付表id
            order.setPayId(orderPay.getId());
            //订单类型
            order.setOrderType(shopOrderDiscountType.getPreferentialType());
            //订单类型id
            order.setShopOrderTypeId(shopOrderDiscountType.getId());
            //若支付完成
            if (orderSettlement.getOrderAmount().doubleValue() == 0) {//购物车集合计算所需支付金额为0 视为已经支付订单
                order.setOrderState(OrderState.ORDER_STATE_UNFILLED);
                order.setPaymentState(OrderState.PAYMENT_STATE_YES); //付款状态
                order.setPaymentTime(new Date());
                String period = rdSysPeriodDao.getSysPeriodService(new Date());
                order.setCreationPeriod(period);

            } //未支付完成
            else if (orderSettlement.getOrderAmount().doubleValue() > 0) {
                order.setOrderState(OrderState.ORDER_STATE_NO_PATMENT);
                order.setPaymentState(OrderState.PAYMENT_STATE_NO);
            } else {
                throw new RuntimeException("订单计算出错");
            }
            SnowFlake snowFlake = new SnowFlake(0, 0);
            if (platform == OrderState.PLATFORM_WECHAT) {
                order.setOrderSn("WX" + snowFlake.nextId());
            } else if (platform == OrderState.PLATFORM_APP) {
                order.setOrderSn("AP" + snowFlake.nextId());
            } else if (platform == OrderState.PLATFORM_PC) {
                order.setOrderSn("PC" + snowFlake.nextId());
            } else {
                throw new IllegalStateException("创建订单平台错误");
            }

            order.setBuyerPhone(member.getMobile());
            order.setIsDel(0);
            order.setPaySn(paySn); //支付表编号
            order.setLockState(OrderState.ORDER_LOCK_STATE_NO); //订单锁定状态:正常
            if (paymentType == 1) {
                order.setPaymentName("在线支付"); //支付方式名称
            } else {
                order.setPaymentName("货到付款"); //支付方式名称
            }

            order.setOrderPlatform(platform);
            order.setEvaluationStatus(0);
            order.setCreateTime(new Date()); //订单生成时间
            order.setBarterState(OrderState.BARTER_STATE_NO);//无退货
            order.setBarterNum(0);//换货数量初始化为0
            order.setOrderMessage(Optional.ofNullable(orderMsgMap.get("orderMessages")).orElse("") + ""); //订单留言
            // TODO 发送队列
            // order.setGiftCoupon(JacksonUtil.toJson(cartVo.getGiftCoupons())); // 赠送优惠卷集合
            /********************* 相关金额赋值 *********************/
            /**********应付统计************/
            // 无其他费用（运费等) 订单总价格=商品总价格
            // 订单总价格
            order.setOrderTotalPrice(orderVo.getOrderAmount());
            //运费
            order.setShippingFee(orderVo.getFreightAmount());
            //优惠金额
            order.setDiscount(orderVo.getCouponAmount());
            // 商品总价格
            order.setGoodsAmount(orderVo.getGoodsAmount());
            //订单pv值
            order.setPpv(orderVo.getPpv());
            //订单运费优惠价格
            order.setShippingPreferentialFee(orderVo.getPreferentialFreightAmount());
            //订单使用优惠券金额
            order.setCouponDiscount(orderVo.getUseCouponAmount());
            order.setOrderPlatform(2);
            /**********支付统计************/
            // 现金支付
            order.setOrderAmount(orderVo.getOrderAmount());
            // 使用积分抵扣金额
            order.setPointRmbNum(Optional.ofNullable(orderVo.getRewardPointPrice()).orElse(BigDecimal.ZERO));
            // 优惠券金额
//            order.setCouponId(orderVo.getCouponId()); //优惠券id
//            order.setCouponPrice(orderVo.getCouponPrice());
            // 店铺优惠券（代金券）
//            order.setVoucherId(orderVo.getVoucherId());
//            order.setVoucherPrice(orderVo.getVoucherPrice());

            //            // 优惠总金额
            //            order.setDiscount(orderVo.getDiscount());
            //            //促销优惠金额
            //            order.setPromoPrice(orderVo.getPromoPrice());
            orderDao.insertEntity(order);
            // todo 推荐反拥
            //扣除用户使用的优惠券
            if(couponDetail!=null&&flag){//使用优惠券
                couponDetail.setUseState(1);//已使用
                couponDetail.setUseTime(new Date());//使用时间
                couponDetail.setUseOrderId(orderId);
                couponDetail.setUseOrderPayStatus(0);//未支付
                couponDetailService.update(couponDetail);
                flag=false;
            }
            /*********************订单项*********************/
            for (CartOrderVo cartOrderVo : orderVo.getCartOrderVoList()) {
                ShopOrderGoods orderGoods = new ShopOrderGoods();
                orderGoods.setId(twiterIdService.getTwiterId());
                orderGoods.setGoodsId(cartOrderVo.getGoodsId());
                orderGoods.setGoodsImage(cartOrderVo.getGoodsImages());
                orderGoods.setGoodsName(cartOrderVo.getGoodsName());
                orderGoods.setGoodsNum(cartOrderVo.getGoodsNum().intValue());
                orderGoods.setSpecId(cartOrderVo.getSpecId());
                orderGoods.setSpecInfo(cartOrderVo.getSpecInfo());
                orderGoods.setBuyerId(order.getBuyerId() + "");
                orderGoods.setOrderId(order.getId());
                orderGoods.setEvaluationStatus(0);
                orderGoods.setGoodsReturnnum(0);
                orderGoods.setGoodsBarternum(0);
                orderGoods.setShippingGoodsNum(0);
                orderGoods.setGoodsType(cartOrderVo.getGoodsType());
                orderGoods.setWeight(cartOrderVo.getWeight());
                // TODO 金额总汇
                //零售价
                orderGoods.setMarketPrice(cartOrderVo.getGoodsRetailPrice());
                //大单价
                orderGoods.setGoodsPrice(cartOrderVo.getGoodsBigPrice());
                //会员价
                orderGoods.setVipPrice(cartOrderVo.getGoodsMemberPrice());
                if (shopOrderDiscountType.getPreferentialType() == ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_MEMBER) {
                    orderGoods.setGoodsPayPrice(cartOrderVo.getGoodsMemberPrice());
                } else if (shopOrderDiscountType.getPreferentialType()
                        == ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_PPV) {
                    orderGoods.setGoodsPayPrice(cartOrderVo.getGoodsBigPrice());
                } else if (shopOrderDiscountType.getPreferentialType()
                        == ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_PREFERENTIAL) {
                    BigDecimal money = cartOrderVo.getGoodsRetailPrice()
                            .subtract(shopOrderDiscountType.getPreferential());
                    if (money.compareTo(new BigDecimal("0")) == -1) {
                        money = new BigDecimal("0");
                    }
                    orderGoods.setGoodsPayPrice(money);
                } else {
                    orderGoods.setGoodsPayPrice(cartOrderVo.getGoodsRetailPrice());
                }
                orderGoods.setPpv(cartOrderVo.getPpv());
                orderGoods.setBigPpv(cartOrderVo.getBigPpv());
                orderGoods.setRewardPointPrice(cartOrderVo.getGoodsRewardPointPrice());
                orderGoods.setActivityId(cartOrderVo.getActivityId());
                orderGoods.setActivityType(cartOrderVo.getActivityType());
                orderGoods.setStoresId(0L);

                //默认0
                orderGoods.setShippingExpressId(0L);
                orderGoodsDao.insert(orderGoods);
                //删除购物数据
                cartService.delete(cartOrderVo.getId());
                /*********************更新商品库存+销售数量*********************/
                ShopGoodsSpec goodsSpec = new ShopGoodsSpec();
                goodsSpec.setId(cartOrderVo.getSpecId());
                goodsSpec.setGoodsId(cartOrderVo.getGoodsId());
                goodsSpec.setSpecSalenum(cartOrderVo.getGoodsNum().intValue());
                ShopGoods goods = goodsDao.find(cartOrderVo.getGoodsId());
                ShopGoodsSpec shopGoodsSpec = goodsSpecDao.find(cartOrderVo.getSpecId());
                if (cartOrderVo.getGoodsNum() > shopGoodsSpec.getSpecGoodsStorage()) {
                    throw new RuntimeException("购买数量大于库存");
                }
                productService.updateStorage(goodsSpec, goods);
                if (cartOrderVo.getActivityId() != null) {
                    List<ShopActivityGoodsSpec> shopActivityGoodsSpecList = shopActivityGoodsSpecService.findList(
                            Paramap.create().put("activityId", cartOrderVo.getActivityId())
                                    .put("specId", cartOrderVo.getSpecId()));
                    if (shopActivityGoodsSpecList != null && shopActivityGoodsSpecList.size() > 0) {
                        ShopActivityGoodsSpec shopActivityGoodsSpec = shopActivityGoodsSpecList.get(0);
                        shopActivityGoodsSpec
                                .setActivityStock(shopActivityGoodsSpec.getActivityStock() - cartOrderVo.getGoodsNum());
                        shopActivityGoodsSpecService.update(shopActivityGoodsSpec);
                    }
                }

            }

            /*处理2019年双十一活动**********************开始*********************/ //TODO
            if(giftId!=null){
                ShopGoods shopGoods = shopGoodsService.find(giftId);
                if(shopGoods==null){
                    throw new RuntimeException("所选赠品不存在");
                }
                ShopGoodsSpec goodsSpec = shopGoodsSpecService.find("goodsId", giftId);
                if(goodsSpec==null){
                    throw new RuntimeException("所选赠品不存在");
                }
                if(goodsSpec.getSpecGoodsStorage()<giftNum){
                    throw new RuntimeException("所选赠品已赠完，请选择其他类型赠品");
                }
                ShopOrderGoods orderGoods = new ShopOrderGoods();
                orderGoods.setId(twiterIdService.getTwiterId());
                orderGoods.setOrderId(order.getId());
                orderGoods.setGoodsId(giftId);
                orderGoods.setGoodsName(shopGoods.getGoodsName());
                orderGoods.setSpecId(goodsSpec.getId());
                //大单价
                orderGoods.setGoodsPrice(goodsSpec.getSpecBigPrice());
                orderGoods.setGoodsNum(giftNum);
                orderGoods.setGoodsImage(shopGoods.getGoodsImage());
                orderGoods.setGoodsReturnnum(0);
                orderGoods.setGoodsType(shopGoods.getGoodsType());
                orderGoods.setRefundAmount(BigDecimal.ZERO);
                orderGoods.setStoresId(0L);
                orderGoods.setEvaluationStatus(0);
                orderGoods.setGoodsPayPrice(BigDecimal.ZERO);
                orderGoods.setBuyerId(order.getBuyerId() + "");
                orderGoods.setIsPresentation(1);
                orderGoods.setMarketPrice(goodsSpec.getSpecRetailPrice());
                orderGoods.setPpv(goodsSpec.getPpv());
                orderGoods.setBigPpv(goodsSpec.getBigPpv());
                orderGoods.setShippingExpressId(0L);
                orderGoods.setVipPrice(goodsSpec.getSpecMemberPrice());
                orderGoods.setWeight(goodsSpec.getWeight());
                orderGoods.setShippingGoodsNum(0);
                orderGoodsDao.insert(orderGoods);
                ShopGoodsSpec goodsSpec1 = new ShopGoodsSpec();
                goodsSpec1.setId(goodsSpec.getId());
                goodsSpec1.setGoodsId(shopGoods.getId());
                goodsSpec1.setSpecSalenum(giftNum);
                productService.updateStorage(goodsSpec1, shopGoods);
            }
            /*处理2019年双十一活动**********************结束*********************/

            /*********************保存日志*********************/
            ShopOrderLog orderLog = new ShopOrderLog();
            orderLog.setId(twiterIdService.getTwiterId());
            orderLog.setOperator(member.getMmCode().toString());
            orderLog.setChangeState(OrderState.ORDER_STATE_UNFILLED + "");
            orderLog.setOrderId(order.getId());
            orderLog.setOrderState(OrderState.ORDER_STATE_NO_PATMENT + "");
            orderLog.setStateInfo("提交订单");
            orderLog.setCreateTime(new Date());
            orderLogDao.insert(orderLog);
        }

        //根据payId查询订单列表
        orderPay.setOrderCreateTime(new Date());
        orderPay.setPayAmount(orderSettlement.getOrderAmount());
        orderPay.setOrderTotalPrice(orderSettlement.getGoodsAmount());
        orderPay.setOrderId(orderId);
        orderPay.setPaymentType(paymentType);
        return orderPay;
    }

    @Override
    public ShopOrderPay addReplacementOrder(Long goodsId, Integer count, Long specId, Long memberId) {
        //商品信息
        ShopGoods goods = goodsDao.find(goodsId);
        if (goods.getGoodsType() != 2) {
            throw new RuntimeException("该商品不是换购商品,无法进行换购");
        }
        //商品规格信息
        ShopGoodsSpec goodsSpec = goodsSpecDao.find(specId);
        if (goodsSpec.getSpecGoodsStorage() < count) {
            throw new RuntimeException("该商品库存不够,无法进行换购");
        }
        GoodsUtils.getSepcMapAndColImgToGoodsSpec(goods, goodsSpec);
        //通过用户id查询用户信息
        RdMmBasicInfo member = rdMmBasicInfoService.find("mmCode", memberId);
        RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", memberId);
        if (!StringUtil.isEmpty(goods.getSalePopulationIds())) {
            if (!goods.getSalePopulationIds().contains("," + rdMmRelation.getRank() + ",")) {
                throw new RuntimeException(goods.getGoodsName() + "目前您的会员等级不能购买该商品");
            }
        }
        //创建一个新的订单支付编号
        String paySn = "P" + Dateutil.getDateString();
        ShopOrderPay orderPay = new ShopOrderPay();
        orderPay.setId(twiterIdService.getTwiterId());
        orderPay.setPaySn(paySn);
        orderPay.setBuyerId(memberId);
        orderPay.setApiPayState("0");
        //保存订单支付表
        orderPayDao.insert(orderPay);
        ShopOrder order = new ShopOrder();
        //该项目只有自营
        order.setStoreId(0L);
        order.setStoreName("自营商店");
        Long orderId = twiterIdService.getTwiterId();
        order.setId(orderId);
        order.setBrandId(goods.getBrandId());
        order.setBrandName(goods.getBrandName());
        order.setBuyerId(memberId);
        order.setBuyerName(member.getMmNickName());
        order.setPredepositAmount(BigDecimal.ZERO);
        //没有支付id 暂时为0L
        order.setPaymentId(-1L);
        order.setPaymentCode("");
        order.setShippingExpressId(0L);
        order.setEvalsellerStatus(0L);
        order.setOrderPointscount(0);
        order.setLogisticType(2);
        // 发货地址id,暂时写死
        order.setDaddressId(0L);
        //收货地址id
        order.setAddressId(-1L);
        //支付表id
        order.setPayId(orderPay.getId());
        //订单类型 换购订单
        order.setOrderType(5);
        //订单类型id
        order.setShopOrderTypeId(-1L);
        //若支付完成
        order.setOrderState(OrderState.ORDER_STATE_NO_PATMENT);
        order.setPaymentState(OrderState.PAYMENT_STATE_NO);
        order.setOrderSn("AP" + Dateutil.getShotDateString() + RandomUtils.getRandomNumberString(4));
        order.setBuyerPhone(member.getMobile());
        order.setIsDel(0);
        order.setPaySn(paySn); //支付表编号
        order.setLockState(OrderState.ORDER_LOCK_STATE_NO); //订单锁定状态:正常
        order.setPaymentName("在线支付"); //支付方式名称
        order.setOrderPlatform(2);
        order.setEvaluationStatus(0);
        order.setCreateTime(new Date()); //订单生成时间
        order.setBarterState(OrderState.BARTER_STATE_NO);//无退货
        order.setBarterNum(0);//换货数量初始化为0
        order.setOrderMessage(""); //订单留言
        String period = rdSysPeriodDao.getSysPeriodService(new Date());
        order.setCreationPeriod(period);
        /********************* 相关金额赋值 *********************/
        /**********应付统计************/
        // 无其他费用（运费等) 订单总价格=商品总价格
        // 订单总价格
        order.setOrderTotalPrice(goodsSpec.getSpecRetailPrice());
        //运费
        order.setShippingFee(BigDecimal.valueOf(0));
        //优惠金额
        order.setDiscount(BigDecimal.valueOf(0));
        // 商品总价格
        order.setGoodsAmount(goodsSpec.getSpecRetailPrice());
        //使用积分
        order.setUsePointNum(goodsSpec.getSpecRetailPrice().intValue());
        //订单pv值
        order.setPpv(BigDecimal.ZERO);
        //订单运费优惠价格
        order.setShippingPreferentialFee(BigDecimal.valueOf(0));
        order.setOrderPlatform(2);
        /**********支付统计************/
        // 现金支付
        order.setOrderAmount(goodsSpec.getSpecRetailPrice().multiply(BigDecimal.valueOf(count)));
        order.setPointRmbNum(goodsSpec.getSpecRetailPrice().multiply(BigDecimal.valueOf(count)));
        orderDao.insertEntity(order);
        ShopOrderGoods orderGoods = new ShopOrderGoods();
        orderGoods.setId(twiterIdService.getTwiterId());
        orderGoods.setGoodsId(goodsId);
        orderGoods.setGoodsImage(goods.getGoodsImage());
        orderGoods.setGoodsName(goods.getGoodsName());
        orderGoods.setGoodsNum(count);
        orderGoods.setMarketPrice(goodsSpec.getSpecRetailPrice());
        orderGoods.setSpecId(goodsSpec.getId());
        // 规格信息--新建一个字段存储新的规格格式
        String specInfo = "";
        Map<String, String> map = goodsSpec.getSepcMap();
        //遍历规格map,取出键值对,拼接specInfo
        if (map != null) {
            Set<String> set = map.keySet();
            for (String str : set) {
                specInfo += str + ":" + map.get(str) + "、";
            }
            specInfo = specInfo.substring(0, specInfo.length() - 1);
        }
        orderGoods.setSpecInfo(specInfo);
        orderGoods.setBuyerId(order.getBuyerId() + "");
        orderGoods.setOrderId(order.getId());
        orderGoods.setEvaluationStatus(0);
        orderGoods.setGoodsReturnnum(0);
        orderGoods.setGoodsBarternum(0);
        orderGoods.setGoodsType(goods.getGoodsType());
        //积分
        orderGoods.setGoodsPayPrice(goodsSpec.getSpecRetailPrice());
        orderGoods.setGoodsPrice(goodsSpec.getSpecRetailPrice());
        orderGoods.setPpv(goodsSpec.getPpv());
        orderGoods.setBigPpv(goodsSpec.getBigPpv());
        orderGoods.setRewardPointPrice(goodsSpec.getSpecRetailPrice());
        orderGoods.setActivityId(-1L);
        orderGoods.setActivityType(0);
        orderGoods.setStoresId(0L);
        //默认0
        orderGoods.setShippingExpressId(0L);
        orderGoodsDao.insert(orderGoods);

        /*********************更新商品库存+销售数量*********************/
//        goodsSpec.setId(specId);
//        goodsSpec.setGoodsId(goodsId);
//        goodsSpec.setSpecSalenum(count);
//        productService.updateStorage(goodsSpec, goods);
        ShopGoodsSpec newGoodsSpec = new ShopGoodsSpec();
        newGoodsSpec.setId(specId);
        newGoodsSpec.setGoodsId(goodsId);
        newGoodsSpec.setSpecSalenum(count);
        if (count > goodsSpec.getSpecGoodsStorage()) {
            throw new RuntimeException("购买数量大于库存");
        }
        productService.updateStorage(newGoodsSpec, goods);
/*********************保存日志*********************/
        ShopOrderLog orderLog = new ShopOrderLog();
        orderLog.setId(twiterIdService.getTwiterId());
        orderLog.setOperator(member.getMmCode());
        orderLog.setChangeState(OrderState.ORDER_STATE_UNFILLED + "");
        orderLog.setOrderId(order.getId());
        orderLog.setOrderState(OrderState.ORDER_STATE_NO_PATMENT + "");
        orderLog.setStateInfo("提交订单");
        orderLog.setCreateTime(new Date());
        orderLogDao.insert(orderLog);
        //根据payId查询订单列表
        orderPay.setOrderCreateTime(new Date());
        orderPay.setPayAmount(goodsSpec.getSpecRetailPrice().multiply(BigDecimal.valueOf(count)));
        orderPay.setOrderTotalPrice(goodsSpec.getSpecRetailPrice().multiply(BigDecimal.valueOf(count)));
        orderPay.setOrderId(orderId);
        orderPay.setPaymentType(2);
        return orderPay;
    }

    @Override
    public Long addExchangeOrderReturnOrderId(List<ShopReturnOrderGoods> shopReturnOrderGoodsList, Long orderId) {
        ShopOrder order = orderDao.find(orderId);
        Long newOrderId = twiterIdService.getTwiterId();
        order.setId(newOrderId);
        SnowFlake snowFlake = new SnowFlake(0, 0);
        order.setOrderSn("PC" + snowFlake.nextId());
        order.setOrderState(OrderState.ORDER_STATE_UNFILLED);
        order.setOrderType(ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_RETRANSMISSION);

        // todo 推荐反拥
        BigDecimal orderAmount = new BigDecimal(0);
        /***********************换货订单修改库存***************************/
/*        for (ShopReturnOrderGoods shopReturnOrderGoods : shopReturnOrderGoodsList) {
            ShopOrderGoods orderGoods = new ShopOrderGoods();
            ShopOrderGoods shopOrderGoods=orderGoodsDao.find(shopReturnOrderGoods.getOrderGoodsId());
        }
        ShopGoodsSpec goodsSpec = new ShopGoodsSpec();
        goodsSpec.setId(cartOrderVo.getSpecId());
        goodsSpec.setGoodsId(cartOrderVo.getGoodsId());
        goodsSpec.setSpecSalenum(cartOrderVo.getGoodsNum().intValue());
        ShopGoods goods = goodsDao.find(cartOrderVo.getGoodsId());
        ShopGoodsSpec shopGoodsSpec = goodsSpecDao.find(cartOrderVo.getSpecId());
        if (cartOrderVo.getGoodsNum() > shopGoodsSpec.getSpecGoodsStorage()) {
            throw new RuntimeException("购买数量大于库存");
        }
        productService.updateStorage(goodsSpec, goods);
        if (cartOrderVo.getActivityId() != null) {
            List<ShopActivityGoodsSpec> shopActivityGoodsSpecList = shopActivityGoodsSpecService.findList(
                    Paramap.create().put("activityId", cartOrderVo.getActivityId())
                            .put("specId", cartOrderVo.getSpecId()));
            if (shopActivityGoodsSpecList != null && shopActivityGoodsSpecList.size() > 0) {
                ShopActivityGoodsSpec shopActivityGoodsSpec = shopActivityGoodsSpecList.get(0);
                shopActivityGoodsSpec
                        .setActivityStock(shopActivityGoodsSpec.getActivityStock() - cartOrderVo.getGoodsNum());
                shopActivityGoodsSpecService.update(shopActivityGoodsSpec);
            }
        }*/
        /**************************************************/
        /*********************订单项*********************/
        for (ShopReturnOrderGoods shopReturnOrderGoods : shopReturnOrderGoodsList) {
            ShopOrderGoods orderGoods = new ShopOrderGoods();
            ShopOrderGoods shopOrderGoods=orderGoodsDao.find(shopReturnOrderGoods.getOrderGoodsId());
            orderGoods.setId(twiterIdService.getTwiterId());
            orderGoods.setGoodsId(shopReturnOrderGoods.getGoodsId());
            orderGoods.setGoodsImage(shopReturnOrderGoods.getGoodsImage());
            orderGoods.setGoodsName(shopReturnOrderGoods.getGoodsName());
            orderGoods.setGoodsNum(shopReturnOrderGoods.getGoodsNum().intValue());

            orderGoods.setMarketPrice(shopReturnOrderGoods.getPrice());
            orderGoods.setSpecId(shopReturnOrderGoods.getSpecId());
            orderGoods.setSpecInfo(shopReturnOrderGoods.getSpecInfo());
            orderGoods.setBuyerId(order.getBuyerId() + "");
            orderGoods.setOrderId(order.getId());
            orderGoods.setEvaluationStatus(0);
            orderGoods.setGoodsReturnnum(0);
            orderGoods.setGoodsBarternum(0);
            orderGoods.setShippingGoodsNum(0);
            orderGoods.setGoodsPayPrice(shopOrderGoods.getGoodsPayPrice());
            orderGoods.setGoodsType(shopReturnOrderGoods.getGoodsType());
            orderGoods.setPpv(shopOrderGoods.getPpv());
            orderGoods.setBigPpv(shopOrderGoods.getBigPpv());
            orderGoods.setWeight(shopOrderGoods.getWeight());
            // TODO 金额总汇
            orderGoods.setVipPrice(shopOrderGoods.getVipPrice());
            //零售价
            orderGoods.setGoodsPrice(shopReturnOrderGoods.getPrice());
            orderGoods.setStoresId(0L);
            //默认0
            orderGoods.setShippingExpressId(0L);
            orderGoodsDao.insert(orderGoods);
            orderAmount = orderAmount.add(
                shopReturnOrderGoods.getPrice().multiply(BigDecimal.valueOf(shopReturnOrderGoods.getGoodsNum()))
                    .setScale(2, BigDecimal.ROUND_HALF_DOWN));
            /*********************更新商品库存+销售数量*********************/
               ShopGoodsSpec goodsSpec = new ShopGoodsSpec();
                goodsSpec.setId(shopReturnOrderGoods.getSpecId());
                goodsSpec.setGoodsId(shopReturnOrderGoods.getGoodsId());
                goodsSpec.setSpecSalenum(shopReturnOrderGoods.getGoodsNum().intValue());
               ShopGoods goods = goodsDao.find(shopReturnOrderGoods.getGoodsId());
                productService.updateStorage(goodsSpec, goods);
        }
        // 商品总价格
        order.setGoodsAmount(orderAmount);
        order.setOrderPlatform(1);
        /**********支付统计************/
        // 现金支付
        order.setOrderAmount(orderAmount);
        order.setOrderTotalPrice(orderAmount);
        BigDecimal zero = new BigDecimal(0);
        order.setDiscount(zero);
        order.setShippingFee(zero);
        order.setPointRmbNum(zero);
        order.setUsePointNum(0);
        order.setShippingPreferentialFee(zero);
        String period = rdSysPeriodDao.getSysPeriodService(new Date());
        order.setCreationPeriod(period);
        order.setRemindTime(null);
        order.setCreateTime(new Date());
        order.setPaymentTime(new Date());
        order.setShippingTime(null);
        order.setFinnshedTime(null);
        order.setEvaluationTime(null);
        order.setBalanceTime(null);
        order.setShippingName("");
        order.setShippingExpressId(0L);
        order.setShippingCode("");
        order.setShippingExpressCode("");
        orderDao.insertEntity(order);

        /*********************保存日志*********************/
        ShopOrderLog orderLog = new ShopOrderLog();
        orderLog.setId(twiterIdService.getTwiterId());
        orderLog.setOperator(order.getBuyerId().toString());
        orderLog.setChangeState(OrderState.ORDER_STATE_NOT_RECEIVING + "");
        orderLog.setOrderId(order.getId());
        orderLog.setOrderState(OrderState.ORDER_STATE_UNFILLED + "");
        orderLog.setStateInfo("后台生成售后订单");
        orderLog.setCreateTime(new Date());
        orderLogDao.insert(orderLog);
        return newOrderId;
    }


    private void refund(ShopOrder order) {
        //  订单已取消 并且以付款
        if (order.getOrderState() == OrderState.ORDER_STATE_CANCLE && order.getPaymentState() == 1) {
            // 未使用到现金支付
            if (order.getOrderAmount().compareTo(BigDecimal.ZERO) > 0) {
                if ("weixinMobilePaymentPlugin".equals(order.getPaymentCode())
                    || "weixinScanPaymentPlugin".equals(order.getPaymentCode())) {//微信开放平台支付
                    WeiRefund weiRefund = new WeiRefund();
                    String bathno = RandomUtils.getRandomNumberStringWithTime(4, "yyyyMMddHHmmss");
                    ShopOrder updateOrder = new ShopOrder();
                    updateOrder.setId(order.getId()); //记录ID
                    updateOrder.setBatchNo(bathno); //退款批次号
                    orderDao.update(updateOrder);//将批次号存入退款表
                    weiRefund.setOutrefundno(bathno);//微信交易号
                    weiRefund.setOuttradeno(order.getPaySn());//订单号
                     weiRefund.setTotalfee((int) ((order.getOrderAmount().doubleValue()) * 100));//单位，整数微信里以分为单位
                     weiRefund.setRefundfee((int) ((order.getOrderAmount().doubleValue()) * 100));
                    //weiRefund.setRefundfee(1);
                    //weiRefund.setTotalfee(1);
                    toweichatrefund(weiRefund, "open_weichatpay");
                } else if ("alipayMobilePaymentPlugin".equals(order.getPaymentCode())) {
                    String bathno = RandomUtils.getRandomNumberStringWithTime(4, "yyyyMMddHHmmss");
                    ShopOrder updateOrder = new ShopOrder();
                    updateOrder.setId(order.getId()); //记录ID
                    updateOrder.setBatchNo(bathno); //退款批次号
                    orderDao.update(updateOrder);//将批次号存入退款表
                    AliPayRefund aliPayRefund = new AliPayRefund();
                    //支付宝交易号 ，退款金额，退款理由
                    aliPayRefund.setRefundAmountNum(1);//退款数量，目前是单笔退款
                    aliPayRefund.setBatchNo(bathno);
                    aliPayRefund.setTradeNo(order.getPaySn());
                     aliPayRefund.setRefundAmount(order.getOrderAmount());
                    //aliPayRefund.setRefundAmount(new BigDecimal(0.01));
                    aliPayRefund.setRRefundReason(order.getCancelCause());
                    // aliPayRefund.setDetaildata(order.getTradeSn(), refundReturn.getRefundAmount().subtract(refundReturn.getPredepositAmount()),
                    // refundReturn.getReasonInfo());
                    aliPayRefund.setDetaildata(order.getTradeSn(), order.getOrderAmount(), order.getCancelCause());
                    toalirefund(aliPayRefund);
                } else {
                    throw new StateResult(5010308, "支付宝没接通好");
                }
            }
        }
    }

    /**
     * 订单取消, 已付款, 额积分（原路返回用户）
     */
    private void returnPoint(Long memberId, ShopOrder order,String opName) {
//        ShopMember member = memberDao.find(memberId);
//        if (member != null) {
        RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", memberId);
        RdMmBasicInfo rdMmBasicInfo=rdMmBasicInfoService.find("mmCode", memberId);
        // 订单使用积分支付
        if (order.getUsePointNum() != null && order.getUsePointNum() != 0) {
            RdMmAccountLog rdMmAccountLog = new RdMmAccountLog();

            BigDecimal available = BigDecimal.valueOf(0);
            BigDecimal availableBefore = BigDecimal.valueOf(0);
            //用户积分 = 原有积分 + 退还积分
            if (order.getOrderType() == 5) {
                rdMmAccountLog.setTransTypeCode("OT");
                rdMmAccountLog.setAccType("SRB");
                rdMmAccountLog.setTrSourceType("");
                availableBefore = rdMmAccountInfo.getWalletBlance();
                available = rdMmAccountInfo.getWalletBlance().add(BigDecimal.valueOf(order.getUsePointNum()));
                rdMmAccountInfo.setRedemptionBlance(available);
            } else {
                rdMmAccountLog.setTransTypeCode("OT");
                rdMmAccountLog.setAccType("SWB");
                rdMmAccountLog.setTrSourceType("OWB");
                availableBefore = rdMmAccountInfo.getWalletBlance();
                available = rdMmAccountInfo.getWalletBlance().add(BigDecimal.valueOf(order.getUsePointNum()));
                rdMmAccountInfo.setWalletBlance(available);
            }
            //用户更新积分
            rdMmAccountLog.setTrOrderOid(order.getId());
            rdMmAccountLog.setMmCode(rdMmBasicInfo.getMmCode());
            rdMmAccountLog.setMmNickName(rdMmBasicInfo.getMmNickName());
            rdMmAccountLog.setTrMmCode(rdMmBasicInfo.getMmCode());
            rdMmAccountLog.setBlanceBefore(availableBefore);
            rdMmAccountLog.setAmount(BigDecimal.valueOf(order.getUsePointNum()));
            rdMmAccountLog.setBlanceAfter(available);
            //无需审核直接成功
            rdMmAccountLog.setStatus(3);
            rdMmAccountLog.setCreationBy(rdMmBasicInfo.getMmNickName());
            rdMmAccountLog.setCreationTime(new Date());
            rdMmAccountLog.setAutohrizeBy(opName);
            rdMmAccountLog.setAutohrizeTime(new Date());
            rdMmAccountLog.setTransDate(new Date());
            rdMmAccountLogService.save(rdMmAccountLog);
            rdMmAccountInfoService.update(rdMmAccountInfo);
        }
//        }
    }

    /**
     * 订单取消, 已付款, 额退款（原路返回）
     */
    private void returnBalance(Long memberId, ShopOrder order) {
//        ShopMember member = memberDao.find(memberId);
//        if (member != null) {
//            // 余额与红包不能同时支付
//            // 订单使用余额支付
//            if (order.getOrderAmount().compareTo(BigDecimal.ZERO) == 1) {
//                //预存款可用金额 = 原有金额 + 退还金额
//                double available = member.getAvailablePredeposit().doubleValue();
//                double rdeAmount = 0D;
//                //判断退款金额是否大于余额支付金额
//                available += order.getOrderAmount().doubleValue();
//                rdeAmount = order.getOrderAmount().doubleValue();
//                member.setAvailablePredeposit(BigDecimal.valueOf(available)); //预存款可用金额
//                memberDao.update(member);
//                //创建一个新的变更日志实体
//                ShopWalletLog predepositLog = new ShopWalletLog();
//                predepositLog.setId(twiterIdService.getTwiterId());
//                predepositLog.setLgMemberId(memberId); //会员编号
//                predepositLog.setLgMemberName(member.getMemberName()); //会员名称
//                predepositLog.setLgType(LgTypeEnum.ORDER_CANCEL.value); //操作类型:退款退回的预存款
//                predepositLog.setLgAvAmount(BigDecimal.valueOf(available)); //可用金额变更0表示未变更
//                predepositLog.setLgFreezeAmount(BigDecimal.valueOf(0)); //冻结金额变更0表示未变更
//                predepositLog.setLgRdeAmount(BigDecimal.valueOf(rdeAmount)); //退款  支出
//                predepositLog.setLgDesc("订单取消退还余额"); //描述
//                predepositLog.setCreateTime(new Date()); //添加时间
//                if (order.getStoreId() != null && order.getStoreId() != 0) {
//                    predepositLog.setStoreId(order.getStoreId());
//                }
//                //保存预存款变更日志表
//                walletLogService.save(predepositLog);
//            }
//        }
    }

    /**
     * 跳到微信退款接口
     */
    private void toweichatrefund(WeiRefund weiRefund, String weitype) {
        Map<String, Object> map = null;
        if ("open_weichatpay".equals(weitype)) {//微信开放平台退款
            map = wechatMobileRefundService.toRefund(weiRefund);
        }

        String msg = "";
        if (map.size() != 0 && "SUCCESS".equals(map.get("result_code"))) {
            log.info("用户微信退款成功");
        } else if (map.size() != 0 && "FAIL".equals(map.get("result_code"))) {
            System.out.println(
                "tsn.order_no" + "：" + weiRefund.getOuttradeno() + "<br/>" + "Micro-channel_number" + "：" + weiRefund
                    .getOutrefundno() + "<br/><span style='color:red;'>" + "Micro-channel_error" + "：" + map
                    .get("err_code_des") + "</span>");
            throw new StateResult(5010301, map.get("err_code_des").toString());
        } else {
            throw new RuntimeException("退款失败");
        }
    }

    /**
     * 跳到支付宝退款接口
     */
    public void toalirefund(AliPayRefund aliPayRefund) {
        String sHtmlText = alipayRefundService.toRefund(aliPayRefund);//构造提交支付宝的表单
        if ("true".equals(sHtmlText)) {
            System.out.println("用户支付宝退款成功！");
        } else {
            System.out.println("退款失败:" + sHtmlText);
            throw new StateResult(5010384, sHtmlText);
        }
    }

    private void addPaymentTally(ShopOrder order, Integer paytrem) {
        //根据支付单号获取订单信息
        ShopMemberPaymentTally paymentTally = new ShopMemberPaymentTally();
        paymentTally.setPaymentCode(order.getPaymentCode());//保存支付类型
        switch (order.getPaymentCode()) {
            case "alipayMobilePaymentPlugin":
                paymentTally.setPaymentName("支付宝手机支付");//支付名称
                break;
            case "weixinMobilePaymentPlugin":
                paymentTally.setPaymentName("微信手机支付");//支付名称
                break;
            case "weixinInternaPaymentPlugin":
                paymentTally.setPaymentName("微信国际支付");//支付名称
                break;
            case "weixinScanPaymentPlugin":
                paymentTally.setPaymentName("微信扫描支付");//支付名称
                break;
            case "alipayInternaPaymentPlugin":
                paymentTally.setPaymentName("支付宝国际支付");//支付名称
                break;
            case "weixinH5PaymentPlugin":
                paymentTally.setPaymentName("微信公众号");//支付名称
                break;
            default:
                throw new RuntimeException("支付类型没有对应的名称");
        }

        paymentTally.setPaymentSn(order.getPaySn());//商城内部交易号
        paymentTally.setTradeSn(order.getBatchNo());
        paymentTally.setPaymentAmount(order.getOrderAmount());// 订单交易金额
        paymentTally.setTradeType(PaymentTallyState.PAYMENTTALLY_RECHARGE_CANCEL_ORDER);
        //支付状态
        paymentTally.setPaymentState(PaymentTallyState.PAYMENTTALLY_STATE_SUCCESS);
        //支付终端类型 1:PC;2:APP;3:h5
        if (paytrem == 1) {
            paymentTally.setPaymentFrom(PaymentTallyState.PAYMENTTALLY_TREM_PC);
        } else if (paytrem == 2) {
            paymentTally.setPaymentFrom(PaymentTallyState.PAYMENTTALLY_TREM_MB);
        } else {
            paymentTally.setPaymentFrom(PaymentTallyState.PAYMENTTALLY_TREM_H5);
        }
        //用户id
        paymentTally.setBuyerId(order.getBuyerId());
        //用户名
        paymentTally.setBuyerName(order.getBuyerName());
        //保存生成时间
        paymentTally.setCreateTime(new Date());
        paymentTally.setId(twiterIdService.getTwiterId());
        //保存流水表记录
        paymentTallyDao.insert(paymentTally);
    }

    @Override
    public Long addOrderRefundReturn(long memberId, String buyerMessage, String goodsImageMore, int applyType,
        List<ShopOrderGoods> shopOrderGoodsList, String brandName) {
        ShopRefundReturn refundReturn = new ShopRefundReturn();
        ShopOrder newOrder = new ShopOrder();
        // 退款金额
        BigDecimal refundAmount = new BigDecimal("0");
        // 优惠券抵扣金额
        BigDecimal couponPrice = new BigDecimal("0");
        // 积分抵扣金额
        BigDecimal rewardPointPrice = new BigDecimal("0");
        Integer refundNum = 0;
        Integer oldGoodsNum = 0;
        Long id = twiterIdService.getTwiterId();
        for (ShopOrderGoods item : shopOrderGoodsList) {
            Integer goodsNum = item.getGoodsNum();
            Long orderGoodsId = item.getId();
            ShopOrderGoods orderGoods = orderGoodsDao.find(orderGoodsId);
            ShopOrder order = orderDao.find(orderGoods.getOrderId());
            ShopGoods shopGoods = goodsDao.find(orderGoods.getGoodsId());
            if (orderGoods == null) {
                throw new StateResult(5010312, "查无次购买记录");
            }

            if (!orderGoods.getBuyerId().equals(memberId + "")) {
                throw new StateResult(5010316, "非用户商品,非法提交");
            }

            if (goodsNum > orderGoods.getGoodsNum()) {
                throw new StateResult(501320, "退货数量不能大于购买数量");
            }

            if (order.getOrderState() != OrderState.ORDER_STATE_FINISH) {
                throw new StateResult(5010324, "订单状态不对 不允许退货");
            }

            if (StringUtil.daysBetween(order.getFinnshedTime(), new Date()) >= 7) {
                throw new StateResult(5010329, "超过7天, 无法申请");
            }

            if (orderGoods.getGoodsNum() - (orderGoods.getGoodsReturnnum() + orderGoods.getGoodsBarternum())
                < goodsNum) {
                throw new StateResult(5010320, "申请数量大于还可申请数量");
            }

            // 退货,或者退款 并且只退部分
            if ((applyType == RefundReturnState.TYPE_RETURN || applyType == RefundReturnState.TYPE_REFUND)
                && goodsNum < orderGoods.getGoodsNum()) {
                refundNum += goodsNum;
                // TODO: 2018/12/24 需要重新计算
//            MathContext mc = new MathContext(4, RoundingMode.DOWN);
//            refundAmount = orderGoods.getGoodsPayPrice().multiply(BigDecimal.valueOf(goodsNum))
//                    .divide(BigDecimal.valueOf(orderGoods.getGoodsNum()), mc).setScale(2, BigDecimal.ROUND_DOWN);
//            rewardPointPrice = Optional.ofNullable(orderGoods.getRewardPointPrice()).orElse(BigDecimal.ZERO)
//                    .multiply(BigDecimal.valueOf(goodsNum)).divide(BigDecimal.valueOf(orderGoods.getGoodsNum()), mc)
//                    .setScale(2, BigDecimal.ROUND_DOWN);
                BigDecimal money = order.getGoodsAmount().subtract(order.getDiscount());
                BigDecimal price = orderGoods.getGoodsPayPrice().multiply(BigDecimal.valueOf(refundNum));
                refundAmount = refundAmount.add(order.getOrderAmount().multiply(
                    BigDecimal.valueOf(price.doubleValue() / money.doubleValue()).setScale(2, BigDecimal.ROUND_HALF_UP))
                    .setScale(2, BigDecimal.ROUND_HALF_UP));
            } else if ((applyType == RefundReturnState.TYPE_RETURN || applyType == RefundReturnState.TYPE_REFUND)
                && goodsNum == orderGoods.getGoodsNum()) {
                //退货,或者退款 并且全部退
                refundAmount = refundAmount.add(orderGoods.getGoodsPayPrice());
                refundNum += orderGoods.getGoodsNum();
                rewardPointPrice = rewardPointPrice
                    .add(Optional.ofNullable(orderGoods.getRewardPointPrice()).orElse(BigDecimal.valueOf(0)));
            } else if (applyType == RefundReturnState.TYPE_EXCHANGE) {
                //换货 不涉及退款
                refundNum += orderGoods.getGoodsNum();
            }
            refundReturn.setOrderId(orderGoods.getOrderId()); //订单id
            refundReturn.setOrderSn(order.getOrderSn()); //订单编号
            refundReturn.setStoreId(order.getStoreId()); //店铺ID
            refundReturn.setStoreName(order.getStoreName()); //店铺名称
            refundReturn.setBuyerId(order.getBuyerId()); //买家ID
            refundReturn.setBuyerName(order.getBuyerName()); //买家会员名
            refundReturn.setBuyerMobile(order.getBuyerPhone());//买家手机号
            refundReturn.setOrderGoodsType(order.getOrderType()); //订单商品类型
            refundReturn.setBrandName(brandName);
            newOrder.setId(order.getId());
            //旧的退货数量累计
            //if (applyType == RefundReturnState.TYPE_EXCHANGE) {
            //    oldGoodsNum = orderGoods.getGoodsBarternum() == null ? 0 : orderGoods.getGoodsBarternum();
            //    oldGoodsNum += refundNum;
            //    orderGoods.setGoodsBarternum(oldGoodsNum);
            //} else {
            //    oldGoodsNum = orderGoods.getGoodsReturnnum() == null ? 0 : orderGoods.getGoodsReturnnum();
            //    oldGoodsNum += refundNum;
            //    orderGoods.setGoodsReturnnum(oldGoodsNum);
            //}
            //修改订单商品项
            orderGoodsDao.update(orderGoods);
            ShopReturnOrderGoods shopReturnOrderGoods = new ShopReturnOrderGoods();
            shopReturnOrderGoods.setCreateTime(new Date());
            shopReturnOrderGoods.setGoodsId(orderGoods.getGoodsId());
            shopReturnOrderGoods.setGoodsImage(orderGoods.getGoodsImage());
            shopReturnOrderGoods.setGoodsName(orderGoods.getGoodsName());
            shopReturnOrderGoods.setId(twiterIdService.getTwiterId());
            shopReturnOrderGoods.setGoodsNum(goodsNum);
            shopReturnOrderGoods.setOrderGoodsId(orderGoods.getId());
            shopReturnOrderGoods.setReturnOrderId(id);
            shopReturnOrderGoods.setSpecInfo(orderGoods.getSpecInfo());
            shopReturnOrderGoods.setSpecId(orderGoods.getSpecId());
            shopReturnOrderGoods.setUpdateTime(new Date());
            shopReturnOrderGoods.setPrice(orderGoods.getGoodsPayPrice());
            shopReturnOrderGoods.setGoodsType(shopGoods.getGoodsType());
            if (order.getOrderType() == 3) {
                shopReturnOrderGoods.setPpv(shopGoods.getBigPpv());
            } else {
                shopReturnOrderGoods.setPpv(shopGoods.getPpv());
            }
            //订单总pv为0 表示用户不是会员,将不会获得pv
            if (order.getPpv().compareTo(BigDecimal.ZERO) == 0) {
                shopReturnOrderGoods.setPpv(BigDecimal.ZERO);
            }
            shopReturnOrderGoodsDao.insert(shopReturnOrderGoods);
        }
        refundReturn.setId(id);
        refundReturn.setRefundSn("8" + Dateutil.getDateString()); //申请编号
        refundReturn.setRefundAmount(BigDecimal.valueOf(0)); //退款金额
        if (applyType == RefundReturnState.TYPE_RETURN) {
            refundReturn.setRefundType(RefundReturnState.TYPE_RETURN); //申请类型
            refundReturn.setReturnType(RefundReturnState.RETURN_TYPE_NEED); //退货类型
        } else if (applyType == RefundReturnState.TYPE_REFUND) {
            refundReturn.setRefundType(RefundReturnState.TYPE_REFUND);
            refundReturn.setReturnType(RefundReturnState.RETURN_TYPE_NOT_NEED); //退货类型
        } else if (applyType == RefundReturnState.TYPE_EXCHANGE) {
            refundReturn.setRefundType(RefundReturnState.TYPE_EXCHANGE);
            refundReturn.setReturnType(RefundReturnState.RETURN_TYPE_NOT_NEED); //退货类型
        }
        refundReturn.setSellerState(RefundReturnState.SELLER_STATE_CONFIRM_AUDIT); //卖家处理状态
        refundReturn.setOrderLock(RefundReturnState.ORDER_LOCK_NEED); //订单锁定类型
        refundReturn.setPicInfo(goodsImageMore); //图片
        refundReturn.setBuyerMessage(buyerMessage); //申请原因
        refundReturn.setCreateTime(new Date()); //创建时间
        refundReturn.setRewardPointAmount(rewardPointPrice);
        refundReturn.setRefundState(RefundReturnState.REFUND_STATE_PROCESSING); //申请状态
        refundReturn.setGoodsState(RefundReturnState.GOODS_STATE_DEFAULT); //物流状态
        refundReturn.setGoodsNum(refundNum);
        //保存退货表
        refundReturnDao.insert(refundReturn);
        // 锁定订单
        newOrder.setLockState(OrderState.ORDER_LOCK_STATE_YES);
        newOrder.setXianshiId(-1L);
        //修改订单
        orderDao.update(newOrder);
        /*********************保存退货日志*********************/
        ShopReturnLog returnLog = new ShopReturnLog();
        returnLog.setId(twiterIdService.getTwiterId());
        returnLog.setReturnId(refundReturn.getId()); //退货表id
        returnLog.setReturnState(RefundReturnState.REFUND_STATE_PROCESSING + ""); //退货状态信息
        returnLog.setChangeState(RefundReturnState.REFUND_STATE_PROCESSING + ""); //下一步退货状态信息
        returnLog.setStateInfo("买家的售后服务已申请成功,等待卖家审核"); //退货状态描述
        returnLog.setCreateTime(new Date()); //创建时间
        returnLog.setOperator(refundReturn.getBuyerName()); //操作人
        returnLogDao.insert(returnLog);
        return id;
    }

    @Override
    public void updateFinishOrder(long orderId, long memberId, int operatorRule) {
        //通过订单编号查询订单
        ShopOrder order = orderDao.find(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在或异常");
        }
        if (order.getOrderState() != OrderState.ORDER_STATE_NOT_RECEIVING) {
            throw new RuntimeException("订单状态错误");
        }

        /*********************订单状态修改*********************/
        ShopOrder updateOrder = new ShopOrder();
        updateOrder.setId(order.getId());
        updateOrder.setOrderState(OrderState.ORDER_STATE_FINISH); //订单状态
        updateOrder.setFinnshedTime(new Date());
        orderDao.update(updateOrder);
        //###############################零售利润###################################################
/*        if(order.getOrderType()==1){//如果当前确认收货订单为零售订单，查看零售订单，修改预期发放时间
            RetailProfit retailProfit = retailProfitService.find("orderId", order.getId());
            if(retailProfit==null){
                log.info(order.getOrderSn()+"订单支付未产生零售利润");
            }else {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.DATE, 8);
                retailProfit.setExpectTime(calendar.getTime());
                String periodCode = rdSysPeriodDao.getSysPeriodService(retailProfit.getExpectTime());
                if(periodCode!=null){
                    retailProfit.setExpectPeriod(periodCode);
                }
                retailProfitService.update(retailProfit);
            }
        }*/
        //##########################################################################################
        /*********************订单日志*********************/
        ShopOrderLog orderLog = new ShopOrderLog();
        orderLog.setId(twiterIdService.getTwiterId());
        orderLog.setOrderState(OrderState.ORDER_STATE_FINISH + "");
        orderLog.setChangeState(OrderState.ORDER_STATE_FINISH + "");
        orderLog.setStateInfo("订单已完成");
        orderLog.setOrderId(order.getId());
        orderLog.setOperator(memberId + "");
        orderLog.setCreateTime(new Date());
        //保存订单日志
        orderLogDao.insert(orderLog);
        //进行用户订单通知
        ShopCommonMessage message = new ShopCommonMessage();
        message.setSendUid(order.getBuyerId() + "");
        message.setType(1);
        message.setOnLine(1);
        message.setCreateTime(new Date());
        message.setBizType(3);
        message.setBizId(orderId);
        message.setIsTop(1);
        message.setTitle(" 订单编号：" + order.getOrderSn());
        StringBuffer shareUrl = new StringBuffer();
        shareUrl.append("<ol class='list-paddingleft-2' style='list-style-type: decimal;'>");
        shareUrl.append("<li><p>已签收</p></li>");
        shareUrl.append("<li><p>物流单号：" + order.getShippingCode() + "</p></li>");
        shareUrl.append("<li><p>去评价可获得换购积分哦~</p></li>");
        message.setContent(shareUrl.toString());
        Long msgId = twiterIdService.getTwiterId();
        message.setId(msgId);
        shopCommonMessageDao.insert(message);
        message.setId(msgId);
        ShopMemberMessage shopMemberMessage = new ShopMemberMessage();
        shopMemberMessage.setBizType(3);
        shopMemberMessage.setCreateTime(new Date());
        shopMemberMessage.setId(twiterIdService.getTwiterId());
        shopMemberMessage.setIsRead(0);//TODO 修改2019-08-26
        shopMemberMessage.setMsgId(msgId);
        shopMemberMessage.setUid(order.getBuyerId());
        shopMemberMessageDao.insert(shopMemberMessage);
        //判断是否符合升级条件进行升级
/*        RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", memberId);
        if (rdMmRelation != null) {
            BigDecimal money = Optional.ofNullable(rdMmRelation.getARetail()).orElse(BigDecimal.ZERO);//获得累计零售购买额
            BigDecimal orderMoney = Optional.ofNullable(order.getOrderAmount()).orElse(BigDecimal.ZERO)
                .add(Optional.ofNullable(order.getPointRmbNum()).orElse(BigDecimal.ZERO));
            BigDecimal vipMoney = BigDecimal.valueOf(NewVipConstant.NEW_VIP_CONDITIONS_TOTAL);
            BigDecimal ppv = Optional.ofNullable(rdMmRelation.getAPpv()).orElse(BigDecimal.ZERO);
            BigDecimal orderPpv = Optional.ofNullable(order.getPpv()).orElse(BigDecimal.ZERO);
            BigDecimal agencyPpv = BigDecimal.valueOf(NewVipConstant.NEW_AGENCY_CONDITIONS_TOTAL);
            //之前少于升级vip的价位 加上这个订单大于或者等于升级vip的价位
            if (money.compareTo(vipMoney) == -1 && (money.add(orderMoney)).compareTo(vipMoney) != -1) {
                RdRanks rdRanks = rdRanksService.find("rankClass", 1);
                rdMmRelation.setRank(rdRanks.getRankId());
            }
            if (ppv.compareTo(agencyPpv) == -1 && (ppv.add(orderPpv)).compareTo(agencyPpv) != -1) {
                RdRanks rdRanks = rdRanksService.find("rankClass", 2);
                rdMmRelation.setRank(rdRanks.getRankId());
            }
            ////晋升
            ////    //发送升级的消息队列
            //    try {
            //        Producer producer = new Producer("PromotionVIP");
            //        PromotionVipResult promotionVipResult = new PromotionVipResult();
            //        promotionVipResult.setMmCode(order.getBuyerId() + "");
            //        promotionVipResult.setDate(new Date());
            //       producer.sendMessage(SerializationUtils.serialize(promotionVipResult));
            //    } catch (IOException e) {
            //        e.printStackTrace();
            //    }
            ////}
            rdMmRelation.setARetail(rdMmRelation.getARetail().add(orderMoney));
            rdMmRelation.setAPpv(ppv.add(orderPpv));
            rdMmRelationService.update(rdMmRelation);

        }*/


    }

    @Override
    public void updateByPaySn(String paysn, Long paymentId) {
        ShopOrder order = find("paySn", paysn);
        TSystemPluginConfig payment = tSystemPluginConfigService.find(paymentId);
        // 更新
        order.setPaymentCode(payment.getPluginId()); //支付方式名称代码
        order.setPaymentId(payment.getId()); //支付方式id
        order.setPaymentName(payment.getPluginName()); //支付方式名称
//        order.setLockState(OrderState.ORDER_LOCK_STATE_YES);
        order.setPrevLockState(null);
        order.setPrevOrderState(OrderState.ORDER_STATE_NO_PATMENT);
        order.setOrderState(OrderState.ORDER_STATE_NO_PATMENT);
        updateByIdOrderStateLockState(order, OrderState.ORDER_OPERATE_PAY);
    }

    @Override
    public void updateOrderStatePayFinish(String paysn, String tradeSn, String paymentBranch) {
        // 用于积分计算
        double orderTotalAmount = 0.0;
        Long memberId = null;
        List<ShopOrder> orderList = findList(
            Paramap.create().put("paySn", paysn).put("orderState", OrderState.ORDER_STATE_NO_PATMENT));
        if (CollectionUtils.isEmpty(orderList)) {
            return;
        }

        for (ShopOrder order : orderList) {
            if (order.getPaymentState() == 0) {//未付款
                memberId = order.getBuyerId();
                // 新建一个订单日志
                ShopOrderLog orderLog = new ShopOrderLog();
                orderLog.setId(twiterIdService.getTwiterId());
                orderLog.setOrderState(OrderState.ORDER_STATE_UNFILLED + "");
                orderLog.setChangeState(OrderState.ORDER_STATE_NOT_RECEIVING + "");
                orderLog.setStateInfo("订单付款完成");
                orderLog.setOrderId(order.getId());
                orderLog.setOperator(order.getBuyerName());
                orderLog.setCreateTime(new Date());
                // 保存订单日志
                orderLogDao.insert(orderLog);
                // 修改订单状态
                /*ShopOrder newOrder = new ShopOrder();
                newOrder.setId(order.getId());
                newOrder.setOrderState(OrderState.ORDER_STATE_UNFILLED);
                newOrder.setPaymentState(OrderState.PAYMENT_STATE_YES);
                newOrder.setLockState(OrderState.ORDER_LOCK_STATE_NO);
                newOrder.setPaymentTime(new Date());
                String period = rdSysPeriodDao.getSysPeriodService(new Date());
                order.setCreationPeriod(period);
                newOrder.setTradeSn(tradeSn);
                newOrder.setPaymentBranch(paymentBranch);
                orderDao.update(newOrder);
                */
                order.setOrderState(OrderState.ORDER_STATE_UNFILLED);
                order.setPaymentState(OrderState.PAYMENT_STATE_YES);
                //TODO update by zc 2019-11-14 支付修改优惠券状态
                List<CouponDetail> couponDetailList = couponDetailService.findList(Paramap.create().put("holdId",memberId).put("useState",1)
                        .put("useOrderId",order.getId()).put("useOrderPayStatus",0));
                if(couponDetailList!=null&&couponDetailList.size()>0){
                    for (CouponDetail couponDetail : couponDetailList) {
                        couponDetail.setUseOrderPayStatus(1);
                        couponDetailService.update(couponDetail);
                    }
                }
                //TODO***************************************************************
                if(order.getOrderType()==1){//如果当前确认收货订单为零售订单，则产生零售利润，否则，跳过
                    String buyerId = order.getBuyerId()+"";
                    RetailProfit retailProfit = new RetailProfit();
                    retailProfit.setBuyerId(buyerId);
                    retailProfit.setCreateTime(new Date());
                    RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", buyerId);
                    if(rdMmRelation!=null&&rdMmRelation.getSponsorCode()!=null){
                        retailProfit.setReceiptorId(rdMmRelation.getSponsorCode());
                    }
                    String periodCode = rdSysPeriodDao.getSysPeriodService(new Date());
                    if(periodCode!=null){
                        retailProfit.setCreatePeriod(periodCode);
                    }
                    retailProfit.setOrderId(order.getId());
                    retailProfit.setOrderSn(order.getOrderSn());
                    retailProfit.setState(0);
                    BigDecimal profit=BigDecimal.ZERO;
                    List<ShopOrderGoods> shopOrderGoodsList = shopOrderGoodsService.findList("orderId", order.getId());
                    if(shopOrderGoodsList!=null&&shopOrderGoodsList.size()>0){
                        for (ShopOrderGoods shopOrderGoods : shopOrderGoodsList) {
                            ShopGoods shopGoods = goodsDao.find(shopOrderGoods.getGoodsId());
                            ShopGoodsSpec shopGoodsSpec = goodsSpecDao.find( shopGoods.getSpecId());
                            profit=profit.add(shopGoodsSpec.getSpecRetailProfit().multiply(new BigDecimal(shopOrderGoods.getGoodsNum())));
                        }
                    }
                    retailProfit.setProfits(profit);
                    retailProfitService.save(retailProfit);
                }
                order.setLockState(OrderState.ORDER_LOCK_STATE_NO);
                order.setPaymentTime(new Date());
                String period = rdSysPeriodDao.getSysPeriodService(new Date());//TODO 2019/7/15 15:51 修改by zc
                order.setCreationPeriod(period);
                order.setTradeSn(tradeSn);
                order.setPaymentBranch(paymentBranch);
                orderDao.update(order);
                if(order.getUsePointNum()!=null&&order.getUsePointNum()>0){//如果订单支付使用了积分，则创建使用积分消息
                    ShopCommonMessage message=new ShopCommonMessage();
                    message.setSendUid(order.getBuyerId()+"");
                    message.setType(1);
                    message.setOnLine(1);
                    message.setCreateTime(new Date());
                    message.setBizType(2);
                    message.setIsTop(1);
                    message.setCreateTime(new Date());
                    message.setTitle("积分消费");
                    message.setContent("您因订单支付【订单号"+order.getOrderSn()+"】，扣减购物积分"+order.getUsePointNum()+"，请在购物积分账户查看明细");
                    Long msgId = twiterIdService.getTwiterId();
                    message.setId(msgId);
                    shopCommonMessageDao.insert(message);
                    ShopMemberMessage shopMemberMessage=new ShopMemberMessage();
                    shopMemberMessage.setBizType(2);
                    shopMemberMessage.setCreateTime(new Date());
                    shopMemberMessage.setId(twiterIdService.getTwiterId());
                    shopMemberMessage.setIsRead(0);
                    shopMemberMessage.setMsgId(msgId);
                    shopMemberMessage.setUid(order.getBuyerId());
                    shopMemberMessageDao.insert(shopMemberMessage);
                }
                orderTotalAmount += order.getOrderAmount().doubleValue();
                //Modify by zc 2019-07-18 TODO
                RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", memberId);
                if (rdMmRelation != null) {
                    BigDecimal money = Optional.ofNullable(rdMmRelation.getARetail()).orElse(BigDecimal.ZERO);//获得累计零售购买额
                    BigDecimal aTotal = Optional.ofNullable(rdMmRelation.getATotal()).orElse(BigDecimal.ZERO);//获得累计购买额
                    BigDecimal orderMoney = Optional.ofNullable(order.getOrderAmount()).orElse(BigDecimal.ZERO)
                            .add(Optional.ofNullable(order.getPointRmbNum()).orElse(BigDecimal.ZERO))
                            .add(Optional.ofNullable(order.getCouponDiscount()).orElse(BigDecimal.ZERO))
                            .subtract(Optional.ofNullable(order.getShippingFee()).orElse(BigDecimal.ZERO));//优惠券优惠金额记入订单金额
                    BigDecimal vipMoney = BigDecimal.valueOf(NewVipConstant.NEW_VIP_CONDITIONS_TOTAL);
                    BigDecimal ppv = Optional.ofNullable(rdMmRelation.getAPpv()).orElse(BigDecimal.ZERO);
                    BigDecimal orderPpv = Optional.ofNullable(order.getPpv()).orElse(BigDecimal.ZERO);
                    BigDecimal agencyPpv = BigDecimal.valueOf(NewVipConstant.NEW_AGENCY_CONDITIONS_TOTAL);
                    if(order.getOrderType()==1){
                        rdMmRelation.setARetail(rdMmRelation.getARetail().add(orderMoney));
                    }
                    //之前少于升级vip的价位 加上这个订单大于或者等于升级vip的价位
                    if (order.getOrderType()==1&&money.compareTo(vipMoney) == -1 && (money.add(orderMoney)).compareTo(vipMoney) != -1&&rdMmRelation.getNOFlag()==1) {
                        //进行用户升级通知
                        ShopCommonMessage message=new ShopCommonMessage();
                        message.setSendUid(rdMmRelation.getMmCode());
                        message.setType(1);
                        message.setOnLine(1);
                        message.setCreateTime(new Date());
                        message.setBizType(2);
                        message.setIsTop(1);
                        message.setCreateTime(new Date());
                        message.setTitle("恭喜，升级啦");
                        message.setContent("您已从普通会员升级成VIP会员,祝您购物愉快");
                        Long msgId = twiterIdService.getTwiterId();
                        message.setId(msgId);
                        shopCommonMessageDao.insert(message);
                        ShopMemberMessage shopMemberMessage=new ShopMemberMessage();
                        shopMemberMessage.setBizType(2);
                        shopMemberMessage.setCreateTime(new Date());
                        shopMemberMessage.setId(twiterIdService.getTwiterId());
                        shopMemberMessage.setIsRead(0);
                        shopMemberMessage.setMsgId(msgId);
                        shopMemberMessage.setUid(Long.parseLong(rdMmRelation.getMmCode()));
                        shopMemberMessageDao.insert(shopMemberMessage);
                        RdRanks rdRanks = rdRanksService.find("rankClass", 1);
                        rdMmRelation.setRank(rdRanks.getRankId());
                        //如果会员升级为vip则查询当期会员是否存在没有发放的零售利润奖励
                        List<RetailProfit> list=retailProfitService.findNoGrantByCode(rdMmRelation.getMmCode());
                        if(list!=null&&list.size()>0){
                            for (RetailProfit retailProfit : list) {
                                if(retailProfit.getProfits()!=null&&retailProfit.getProfits().compareTo(BigDecimal.ZERO)!=1){
                                    continue;
                                }
                                RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", retailProfit.getReceiptorId());
                                if(rdMmAccountInfo!=null){
                                    //生成积分日志表
                                    RdMmAccountLog rdMmAccountLog = new RdMmAccountLog();
                                    rdMmAccountLog.setMmCode(retailProfit.getReceiptorId());
                                    RdMmBasicInfo basicInfo = rdMmBasicInfoService.find("mmCode", retailProfit.getReceiptorId());
                                    rdMmAccountLog.setMmNickName(basicInfo.getMmNickName());
                                    rdMmAccountLog.setTransTypeCode("BA");
                                    rdMmAccountLog.setAccType("SBB");
                                    rdMmAccountLog.setTrSourceType("CMP");
                                    rdMmAccountLog.setTrOrderOid(retailProfit.getOrderId());
                                    rdMmAccountLog.setBlanceBefore(rdMmAccountInfo.getBonusBlance());
                                    List<RdMmIntegralRule> rules = rdMmIntegralRuleService.findAll();
                                    BigDecimal amount = retailProfit.getProfits().multiply(new BigDecimal(rules.get(0).getRsCountBonusPoint())).divide(new BigDecimal(100),2);
                                    rdMmAccountLog.setAmount(amount);
                                    rdMmAccountLog.setBlanceAfter(rdMmAccountInfo.getBonusBlance().add(amount));
                                    rdMmAccountLog.setTransDate(new Date());
                                    String periodStr = rdSysPeriodDao.getSysPeriodService(new Date());
                                    if(periodStr!=null){
                                        rdMmAccountLog.setTransPeriod(period);
                                    }
                                    rdMmAccountLog.setTransDesc("零售利润奖励发放");
                                    rdMmAccountLog.setStatus(3);
                                    rdMmAccountLogService.save(rdMmAccountLog);
                                    //修改积分账户
                                    rdMmAccountInfo.setBonusBlance(rdMmAccountInfo.getBonusBlance().add(amount));
                                    rdMmAccountInfoService.update(rdMmAccountInfo);
                                    //修改零售利润
                                    retailProfit.setReceiptorId(rdMmRelation.getSponsorCode());
                                    retailProfit.setActualTime(new Date());
                                    retailProfit.setState(1);
                                    retailProfitService.update(retailProfit);
                                    //设置零售利润积分发放通知
                                    ShopCommonMessage shopCommonMessage=new ShopCommonMessage();
                                    shopCommonMessage.setSendUid(rdMmRelation.getMmCode());
                                    shopCommonMessage.setType(1);
                                    shopCommonMessage.setOnLine(1);
                                    shopCommonMessage.setCreateTime(new Date());
                                    shopCommonMessage.setBizType(2);
                                    shopCommonMessage.setIsTop(1);
                                    shopCommonMessage.setCreateTime(new Date());
                                    shopCommonMessage.setTitle("积分到账通知");
                                    shopCommonMessage.setContent("您从零售订单："+retailProfit.getOrderSn()+"获得"+amount+"点积分，已加入奖励积分账户");
                                    Long msgId1 = twiterIdService.getTwiterId();
                                    shopCommonMessage.setId(msgId1);
                                    shopCommonMessageDao.insert(shopCommonMessage);
                                    ShopMemberMessage shopMemberMessage1=new ShopMemberMessage();
                                    shopMemberMessage1.setBizType(2);
                                    shopMemberMessage1.setCreateTime(new Date());
                                    shopMemberMessage1.setId(twiterIdService.getTwiterId());
                                    shopMemberMessage1.setIsRead(0);
                                    shopMemberMessage1.setMsgId(msgId);
                                    shopMemberMessage1.setUid(Long.parseLong(rdMmRelation.getMmCode()));
                                    shopMemberMessageDao.insert(shopMemberMessage1);
                                }
                            }
                        }
                    }
                    //目前不进行代理会员升级降级处理
                    /*if (ppv.compareTo(agencyPpv) == -1 && (ppv.add(orderPpv)).compareTo(agencyPpv) != -1) {
                        RdRanks rdRanks = rdRanksService.find("rankClass", 2);
                        rdMmRelation.setRank(rdRanks.getRankId());
                    }*/
                    ////晋升
                    ////    //发送升级的消息队列
                    //    try {
                    //        Producer producer = new Producer("PromotionVIP");
                    //        PromotionVipResult promotionVipResult = new PromotionVipResult();
                    //        promotionVipResult.setMmCode(order.getBuyerId() + "");
                    //        promotionVipResult.setDate(new Date());
                    //       producer.sendMessage(SerializationUtils.serialize(promotionVipResult));
                    //    } catch (IOException e) {
                    //        e.printStackTrace();
                    //    }
                    ////}
                    rdMmRelation.setAPpv(ppv.add(orderPpv));
                    rdMmRelation.setATotal(aTotal.add(orderMoney));
                    rdMmRelationService.update(rdMmRelation);
                }
            }
        }
        // 更新支付表
        ShopOrderPay orderPay = new ShopOrderPay();
        orderPay.setPaySn(paysn);
        orderPay.setApiPayState("1");
        orderPayDao.updateByPaysn(orderPay);

        /********************* 添加消费积分 *********************/
//        addMemberConsumePoints(orderTotalAmount, memberId);
    }

    /**
     * 用户获取消费积分
     */
    private void addMemberConsumePoints(double orderTotalAmount, Long memberId) {
//        ShopMember member = memberDao.find(memberId);
//        // 消费积分
//        BigDecimal consPoint = Optional.ofNullable(member.getMemberConsumePoints()).orElse(new BigDecimal("0"));
//        // 消费积分获取= 实际支付金额 * set%
//        String set = String.valueOf(settingService.read("cash_consume_integral_exchange"));
//        if (StringUtil.isNumber(set)) {
//            consPoint = consPoint.add(new BigDecimal(Double.valueOf(set) * orderTotalAmount / 100));
//            //修改用户积分
//            member.setMemberConsumePoints(consPoint);
//            memberDao.update(member);
//            ShopPointsLog shopPointsLog = new ShopPointsLog();
//            shopPointsLog.setId(twiterIdService.getTwiterId());
//            shopPointsLog.setPlMemberid(member.getId());
//            shopPointsLog.setPlMembername(member.getMemberName());
//            shopPointsLog.setPlAdminid(1L);
//            shopPointsLog.setPlAdminname("admin");
//            shopPointsLog.setPlPoints(consPoint);
//            shopPointsLog.setCreateTime(new Date());
//            shopPointsLog.setPlType(PointsLogType.POINTS_TYPE_ORDERPAY); //积分操作类型
//            shopPointsLog.setPlDesc("购物获得");
//            shopPointsLog.setPlStage("商品付款成功,增加会员积分");
//            shopPointsLog.setBizType(PointsLogType.BIZ_TYPE_CONSUME_POINT);
//            //保存会员积分日志表
//            pointsLogDao.insert(shopPointsLog);
//        }
    }


    private OrderSettlement getAmount(List<CartInfo> cartInfoList, String couponIds, Integer isPp) {
        /*********************优惠券使用信息*********************/
        OrderSettlement orderSettlement = new OrderSettlement();
        // 全部订单价格
        BigDecimal totalOrderAmount = new BigDecimal(0);
        // 全部商品价格
        BigDecimal totalGoodsAmount = new BigDecimal(0);
        for (CartInfo cartInfo : cartInfoList) {
            OrderVo orderVo = new OrderVo();
            orderVo.setBrandId(cartInfo.getBrandId());
            orderVo.setBrandName(cartInfo.getBrandName());
            // 商品总价格
            orderVo.setGoodsAmount(cartInfo.getGoodsTotalPrice());
            // 优惠券金额
//            orderVo.setCouponPrice(cartInfo.getCouponPrice());
            //优惠金额
            orderVo.setCouponAmount(cartInfo.getCouponAmount());
            //运费
            orderVo.setFreightAmount(cartInfo.getFreightAmount());
            //运费优惠
            orderVo.setPreferentialFreightAmount(cartInfo.getPreferentialFreightAmount());
            orderVo.setPpv(cartInfo.getPpvNum());
//            orderVo.setCouponId(cartInfo.getCouponId());
            // 代金券金额 店铺优惠券
//            orderVo.setVoucherPrice(cartInfo.getVoucherPrice());
//            orderVo.setVoucherId(cartInfo.getVoucherId());
            // 订单金额
//            BigDecimal orderAmount = cartInfo.getGoodsTotalPrice().subtract(cartInfo.getCouponPrice())
//                    .subtract(cartInfo.getVoucherPrice());
            BigDecimal orderAmount = cartInfo.getActualGoodsTotalPrice();
            orderVo.setOrderAmount(orderAmount);
            orderVo.setUseCouponAmount(cartInfo.getUseCouponAmount());
            //完善订单项信息--
            orderVo.setCartOrderVoList(setCartOrderVoColumn(cartInfo));
            // 所有订单金额
            totalOrderAmount = totalOrderAmount.add(orderAmount);
            totalGoodsAmount = totalGoodsAmount.add(cartInfo.getGoodsTotalPrice());
            orderSettlement.getOrderVoList().add(orderVo);
            // 优惠金额
            //            orderVo.setDiscount(new BigDecimal(cartVo.getProtoGoodsTotalPrice() - cartVo.getGoodsTotalPrice()));
        }

        /********************* 打赏积分支付 *********************/
//        BigDecimal rewardPointPrice = payOfRewardPoint(isPp, member, orderSettlement, totalOrderAmount);
        orderSettlement.setGoodsAmount(totalGoodsAmount);
        orderSettlement.setOrderAmount(totalOrderAmount);
        return orderSettlement;
    }

//    private BigDecimal payOfRewardPoint(Integer isPp, ShopMember member, OrderSettlement orderSettlement,
//                                        BigDecimal totalOrderAmount) {
//        if (isPp == null || isPp != 1) {
//            return BigDecimal.ZERO;
//
//        }
//        Map<String, Object> pointMap = Maps.newConcurrentMap();
//        //cartService.calcPayOfRewardPoint(member, totalOrderAmount, pointMap);
//        // 打赏积分抵扣金额
//        BigDecimal rewardPointPrice = (BigDecimal) pointMap.get("rewardPointPrice");
//        // 使用打赏积分
//        Integer useRewardPoint = (Integer) pointMap.get("rewardPoint");
//        if (rewardPointPrice.compareTo(BigDecimal.ZERO) <= 0) {
//            return BigDecimal.ZERO;
//        }
//        Map<Long, BigDecimal> collect = orderSettlement.getOrderVoList().stream()
//                .collect(Collectors.toMap(OrderVo::getBrandId, OrderVo::getOrderAmount));
//        // TODO 打赏积分抵扣金额-按订单金额比例分隔
////        Map<Long, BigDecimal> storeIdAndPointMap = CalcUtil.divideByRate(rewardPointPrice, collect);
////        orderSettlement.getOrderVoList().forEach(item -> {
////            item.setRewardPointPrice(storeIdAndPointMap.get(item.getStoreId()));
////            item.setOrderAmount(item.getOrderAmount().subtract(item.getRewardPointPrice()));
////            // 订单获取到的打赏积分抵扣金额-按商品价格比例分隔
////            Map<Long, BigDecimal> collect1 = item.getCartOrderVoList().stream()
////                    .collect(Collectors.toMap(CartOrderVo::getId, CartOrderVo::getGoodsCashAmount));
////            Map<Long, BigDecimal> cartIdAndPointMap = CalcUtil.divideByRate(item.getRewardPointPrice(), collect1);
////            item.getCartOrderVoList().forEach(cartOrderVo -> {
////                cartOrderVo.setGoodsRewardPointPrice(cartIdAndPointMap.get(cartOrderVo.getId()));
////                cartOrderVo.setGoodsCashAmount(
////                        cartOrderVo.getGoodsCashAmount().subtract(cartOrderVo.getGoodsRewardPointPrice()));
////            });
////        });
//
//        memberDao.update(member);
//
//        // 保存日志
//        ShopPointsLog shopPointsLog = new ShopPointsLog();
//        shopPointsLog.setId(twiterIdService.getTwiterId());
//        shopPointsLog.setPlMemberid(member.getId());
//        shopPointsLog.setPlMembername(member.getMemberName());
//        shopPointsLog.setPlAdminid(1L);
//        shopPointsLog.setPlAdminname("admin");
//        shopPointsLog.setPlPoints(new BigDecimal(useRewardPoint * -1));
//        shopPointsLog.setCreateTime(new Date());
//        shopPointsLog.setPlType(PointsLogType.POINTS_TYPE_ORDERPAY); //积分操作类型
//        shopPointsLog.setPlDesc("提交订单");
//        shopPointsLog.setPlStage("订单提交成功,减少打赏积分");
//        shopPointsLog.setBizType(PointsLogType.BIZ_TYPE_REWARD_POINT);
//        //保存会员积分日志表
//        pointsLogDao.insert(shopPointsLog);
//        return rewardPointPrice;
//    }

    private List<CartOrderVo> setCartOrderVoColumn(CartInfo cartVo) {
        List<CartOrderVo> cartOrderVoList = Lists.newArrayList();
        // 商品项促销金额平均+送优惠卷获取
        for (CartVo cart : cartVo.getList()) {
            CartOrderVo cartOrderVo = new CartOrderVo();
            // 将cart相同字段复制到cartOrderVo
            BeanUtils.copyProperties(cart, cartOrderVo);
            //TODO 价格错误
            //cartOrderVo.setGoodsPrice(cart.getGoodsStorePrice());
            BigDecimal payAmount = cart.getItemTotalPrice().subtract(cart.getItemPromotionPrice())
                .subtract(cart.getItemCouponPrice()).subtract(cart.getItemVoucherPrice());
            cartOrderVo.setGoodsCouponPrice(cart.getItemCouponPrice());
            cartOrderVo.setGoodsVoucherPrice(cart.getItemVoucherPrice());
            cartOrderVo.setGoodsCashAmount(payAmount);
            cartOrderVo.setActivityId(cart.getActivityId());
            cartOrderVo.setActivityType(cart.getActivityType());
            cartOrderVoList.add(cartOrderVo);
        }

        return cartOrderVoList;
    }

    @Override
    public void updateToDelState(Long orderId, String memberId) {
        ShopOrder order = orderDao.find(orderId);
        if (order == null || !order.getBuyerId().equals(memberId)) {
            throw new RuntimeException("订单不存在或非法删除");
        }

        if (order.getOrderState() != OrderState.ORDER_STATE_FINISH
            && order.getOrderState() != OrderState.ORDER_STATE_CANCLE) {
            throw new RuntimeException("订单状态不能删除");
        }

        if (order.getLockState() == OrderState.ORDER_LOCK_STATE_YES) {
            throw new RuntimeException("订单已锁定不能删除");
        }
        //标记订单删除
        ShopOrder newOrder = new ShopOrder();
        newOrder.setId(order.getId());
        newOrder.setIsDel(1);
        orderDao.update(newOrder);
        //订单日志
        ShopOrderLog orderLog = new ShopOrderLog();
        orderLog.setId(twiterIdService.getTwiterId());
        orderLog.setOrderState(OrderState.ORDER_STATE_FINISH + "");
        orderLog.setChangeState("用户删除订单");
        orderLog.setStateInfo("删除订单");
        orderLog.setOrderId(order.getId());
        orderLog.setOperator(order.getBuyerName());
        orderLog.setCreateTime(new Date());
        orderLogDao.insert(orderLog);
    }

//    @Override
//    public Map<String, Object> payWallet(PayCommon payCommon, String comId) {
//        ShopMember shopMember = memberDao.find(comId);
//        if (shopMember.getAvailablePredeposit() == null) {
//            shopMember.setAvailablePredeposit(new BigDecimal(0));
//        }
//
//        List<ShopOrder> orderList = findList("paySn", payCommon.getOutTradeNo());
//        if (shopMember.getAvailablePredeposit().compareTo(payCommon.getPayAmount()) < 0) {
//            Map<String, Object> result = Maps.newConcurrentMap();
//            result.put("status", -1);
//            result.put("message", "余额不足");
//            return result;
//        } else {
//            //消费余额
//            shopMember.setAvailablePredeposit(shopMember.getAvailablePredeposit().subtract(payCommon.getPayAmount()));
//            memberDao.update(shopMember);
//
//            //扣钱,产生流水
//            //updateOrderStatePayFinish(payCommon.getOutTradeNo(), "", "waletPaymentPlugin");
//            if (CollectionUtils.isNotEmpty(orderList)) {
//                for (ShopOrder order : orderList) {
//                    if (order.getPaymentState() == 0) {
//                        //新建一个订单日志
//                        ShopOrderLog orderLog = new ShopOrderLog();
//                        orderLog.setId(twiterIdService.getTwiterId());
//                        orderLog.setOrderState(OrderState.ORDER_STATE_UNFILLED + "");
//                        orderLog.setChangeState(OrderState.ORDER_STATE_NOT_RECEIVING + "");
//                        orderLog.setStateInfo("订单付款完成");
//                        orderLog.setOrderId(order.getId());
//                        orderLog.setOperator(shopMember.getId() + "");
//                        orderLog.setCreateTime(new Date());
//                        //保存订单日志
//                        orderLogDao.insert(orderLog);
//                        //修改订单状态
//                        ShopOrder newOrder = new ShopOrder();
//                        newOrder.setOrderState(OrderState.ORDER_STATE_UNFILLED);
//                        newOrder.setPaymentState(OrderState.PAYMENT_STATE_YES);
//                        newOrder.setPaymentTime(new Date());
//                        newOrder.setTradeSn(payCommon.getOutTradeNo());
//                        newOrder.setPaymentBranch("waletPaymentPlugin");
//                        //todo 暂时
//                        newOrder.setPaymentCode("balancePaymentPlugin");
//                        newOrder.setPaymentId(8L);
//                        // 条件
//                        newOrder.setId(order.getId());
//                        newOrder.setPrevOrderState(OrderState.ORDER_STATE_NO_PATMENT);
//                        newOrder.setPrevLockState(OrderState.ORDER_LOCK_STATE_NO);
//                        updateByIdOrderStateLockState(newOrder, OrderState.ORDER_OPERATE_PAY);
//                    }
//                    //创建一个新的变更日志实体
//                    ShopWalletLog predepositLog = new ShopWalletLog();
//                    predepositLog.setId(twiterIdService.getTwiterId());
//                    predepositLog.setLgMemberId(shopMember.getId()); //会员编号
//                    predepositLog.setLgMemberName(shopMember.getMemberName()); //会员名称
//                    predepositLog.setLgType(LgTypeEnum.ORDER_PAY.value); //操作类型:下单支付被冻结的预存款
//                    predepositLog.setLgAvAmount(shopMember.getAvailablePredeposit()); //可用金额,变更0表示未变更
//                    predepositLog.setLgFreezeAmount(BigDecimal.valueOf(0)); //冻结金额变更0表示未变更
//                    predepositLog.setLgAddAmount(order.getOrderAmount()); //支出余额等于0
//                    predepositLog.setLgDesc("订单支付使用余额"); //描述
//                    predepositLog.setCreateTime(new Date()); //添加时间
//                    if (order.getStoreId() != null && order.getStoreId() != 0) {
//                        predepositLog.setStoreId(order.getStoreId());
//                    }
//                    //保存预存款变更日志表
//                    walletLogService.save(predepositLog);
//                }
//                // 用户增加消费积分
//                addMemberConsumePoints(payCommon.getPayAmount().doubleValue(), shopMember.getId());
//            }
//            Map<String, Object> result = Maps.newConcurrentMap();
//            result.put("status", 1);
//            result.put("message", "支付成功");
//            return result;
//        }
//    }

    private int getOrderType(List<CartOrderVo> cartList) {
        // 普通类型
        boolean isOrdinary = true;
        // 促销类型
        boolean isPromotion = true;
        for (CartOrderVo cart : cartList) {
            isOrdinary &= cart.getActivityType() == null;
            isPromotion &= cart.getActivityType() != null && ActivityTypeEnus.EnumType.zhuanchang.getValue() == cart
                .getActivityType();
        }

        if (isOrdinary & !isPromotion) {
            return OrderState.ORDER_TYPE_ORDINARY; // 普通订单
        }

        if (isPromotion & !isOrdinary) {
            return OrderState.ORDER_TYPE_PROMOTION;      // 混合订单
        }

        if (!isPromotion & !isOrdinary) {
            return OrderState.ORDER_TYPE_MIX;      // 混合订单
        }

        return OrderState.ORDER_TYPE_ORDINARY;
    }

    /*********************
     * 以下主要扩展查询
     *********************/
    @Override
    public Page listOrderView(Pageable pageable) {
        PageList<OrderView> result = orderDao.listOrderView(pageable.getParameter(), pageable.getPageBounds());
        return new Page<>(result, result.getPaginator().getTotalCount(), pageable);
    }

    //商品详情
    @Override
    public ShopOrderVo findWithAddrAndGoods(Long orderId) {
        ShopOrderVo shopOrderVo = new ShopOrderVo();
        shopOrderVo = orderDao.getShopOrderVoWithGoodsAndAddress(orderId);
        shopOrderVo.setIsStorage(1);
        shopOrderVo.setIsPartial(0);
        BigDecimal fixedPpv=BigDecimal.ZERO;

        if (shopOrderVo != null) {
            shopOrderVo.setAddress(shopOrderAddressDao.find(shopOrderVo.getAddressId()));
            List<ShopOrderGoods> shopOrderGoods = shopOrderGoodsService.findList("orderId", shopOrderVo.getId());
            List<Long> idsLong = new ArrayList<>();
            Map<String, Integer> storageMap = new HashMap<>();
            for (int i = 0; i < shopOrderGoods.size(); i++) {
                idsLong.add(shopOrderGoods.get(i).getSpecId());
            }
            if (idsLong.size() > 0) {
                List<ShopGoodsSpec> shopGoodsSpecList = goodsSpecDao.findByParams(Paramap.create().put("ids", idsLong));
                for (ShopGoodsSpec item : shopGoodsSpecList) {
                    if (item.getSpecIsopen() != 3) {
                        storageMap.put(item.getId() + "", item.getSpecGoodsStorage());
                    }
                }
                for (ShopOrderGoods item : shopOrderGoods) {
                    if (storageMap.get(item.getSpecId() + "") != null) {
                        fixedPpv=fixedPpv.add(item.getPpv());
                        int storage = 0;
                        if (item.getActivityId() != null && item.getActivityId() != -1) {
                            List<ShopActivityGoodsSpec> shopActivityGoodsSpecList = shopActivityGoodsSpecService
                                .findList(Paramap.create().put("activityId", item.getActivityId())
                                    .put("specId", item.getSpecId()));
                            if (shopActivityGoodsSpecList != null && shopActivityGoodsSpecList.size() > 0) {
                                ShopActivityGoodsSpec shopActivityGoodsSpec = shopActivityGoodsSpecList.get(0);
                                storage = shopActivityGoodsSpec.getActivityStock();
                            }
                        } else {
                            storage = storageMap.get(item.getSpecId() + "");
                        }

                        item.setSpecGoodsStorage(storage);
                        if (item.getGoodsNum() > storage) {
                            shopOrderVo.setIsStorage(0);
                        } else {
                            if (item.getShippingCode() == null || "".equals(item.getShippingCode())) {
                                shopOrderVo.setIsPartial(1);
                            }
                        }
                    } else {
                        throw new RuntimeException("存在已下架商品" + item.getGoodsName());
                    }
                }
            }
            shopOrderVo.setFixedPpv(fixedPpv);
            shopOrderVo.setShopOrderGoods(shopOrderGoods);
        } else {
            throw new RuntimeException("不存在订单");
        }
        return shopOrderVo;
    }

    @Override
    public List<ShopOrderVo> listWithAbleRefundReturnGoods(Pageable pageable) {
        return orderDao.listShopOrderVoWithAbleRefundReturnGoods(pageable.getParameter(), pageable.getPageBounds());
    }

    @Override
    public Page listWithGoods(Pageable pageable) {
        PageList<ShopOrderVo> result = orderDao
            .listShopOrderVoWithGoods(pageable.getParameter(), pageable.getPageBounds());
        return new Page<>(result, result.getPaginator().getTotalCount(), pageable);
    }

    @Override
    public Page listWithGoodsAndAddr(Pageable pageable) {
        PageList<ShopOrderVo> result = orderDao
            .listShopOrderVoWithGoodsAndAddr(pageable.getParameter(), pageable.getPageBounds());
        List<Long> ids = new ArrayList<>();
        List<Long> orderIds = new ArrayList<>();
        for (ShopOrderVo item : result) {
            ids.add(item.getAddressId());
            orderIds.add(item.getId());
        }
        //查询地址信息
        Map<Long, ShopOrderAddress> addressMap = new HashMap<>();
        if (ids.size() > 0) {
            List<ShopOrderAddress> shopOrderAddresses = shopOrderAddressDao
                .findByParams(Paramap.create().put("ids", ids));
            for (ShopOrderAddress item : shopOrderAddresses) {
                addressMap.put(item.getId(), item);
            }
        }
        if (orderIds.size() > 0) {
            Map<Long, List<ShopOrderGoods>> map = shopOrderGoodsService.findOrderMap(orderIds);
            for (ShopOrderVo item : result) {
                item.setAddress(addressMap.get(item.getAddressId()));
                item.setShopOrderGoods(map.get(item.getId()));
            }
        } else {
            if (ids.size() > 0) {
                for (ShopOrderVo item : result) {
                    item.setAddress(addressMap.get(item.getAddressId()));
                }
            }
        }
        return new Page<>(result, result.getPaginator().getTotalCount(), pageable);
    }

    //发货订单列表查询
    @Override
    public Page findDeliverableWithGoodsAndAddr(Pageable pageable) {
        PageList<ShopOrderVo> result = orderDao
            .listDeliverableWithGoodsAndAddr(pageable.getParameter(), pageable.getPageBounds());
        List<Long> ids = new ArrayList<>();
        List<Long> orderIds = new ArrayList<>();
        for (ShopOrderVo item : result) {
            ids.add(item.getAddressId());
            orderIds.add(item.getId());
        }
        //查询地址信息
        Map<Long, ShopOrderAddress> addressMap = new HashMap<>();
        List<ShopOrderAddress> shopOrderAddresses = shopOrderAddressDao.findByParams(Paramap.create().put("ids", ids));
        for (ShopOrderAddress item : shopOrderAddresses) {
            addressMap.put(item.getId(), item);
        }
        Map<Long, List<ShopOrderGoods>> map = shopOrderGoodsService.findOrderMap(orderIds);
        for (ShopOrderVo item : result) {
            item.setAddress(addressMap.get(item.getAddressId()));
            item.setShopOrderGoods(map.get(item.getId()));
        }
        return new Page<>(result, result.getPaginator().getTotalCount(), pageable);
    }

    @Override
    public void updateByIdOrderStateLockState(ShopOrder order, int operateType) {
        if (order.getId() == null || order.getPrevOrderState() == null) {
            throw new IllegalArgumentException("参数错误");
        }

        if (operateType != OrderState.ORDER_OPERATE_PAY && order.getPrevLockState() == null) {
            throw new IllegalArgumentException("参数错误");
        }

        Long result = orderDao.updateByIdAndOrderStateAndLockState(order);
        if (result.intValue() != 1) {
            ShopOrder findOrder = orderDao.find(order.getId());
            if (findOrder == null) {
                throw new RuntimeException("不存在订单");
            }
            //换货订单 发货
            if (findOrder.getOrderType() == 6) {
                return;
            }
            String exceptionMsg = null;
            switch (operateType) {
                case OrderState.ORDER_OPERATE_PAY:
                    // 【前端订单发起支付中】 -- 【后台取消订单中】 => 后台取消订单先完成
                    if (!findOrder.getOrderState().equals(order.getPrevOrderState())) {
                        exceptionMsg = "订单已经取消";
                    }
                    // 【后台订单编辑中】 -- 【前端订单发起支付中】 => 后台先完成
                    if (!findOrder.getLockState().equals(order.getPrevLockState())) {
                        exceptionMsg = "订单编辑中, 请重新支付";
                    }
                    break;
                case OrderState.ORDER_OPERATE_CANCEL:
                    // 【前端订单支付中锁定订单】 -- 【后台取消订单中】 =》订单支付中先锁定
                    if (!findOrder.getLockState().equals(order.getPrevLockState())) {
                        exceptionMsg = "订单已经锁定";
                    }
                    // 【订单支付第三方调用完成支付接口】-- 【后台在取消订单中】 =》第三方调用先完成
                    if (!findOrder.getOrderState().equals(order.getPrevOrderState())) {
                        exceptionMsg = "订单已经支付成功";
                    }
                    break;
                // 【后台发货中】 -- 【前端取消订单中】 -- 取消订单先完成
                case OrderState.ORDER_OPERATE_DELIVERY:
                    if (!findOrder.getOrderState().equals(order.getPrevOrderState())) {
                        exceptionMsg = "订单已经取消";
                    }
//                    if (!findOrder.getLockState().equals(order.getPrevLockState())) {
//                        exceptionMsg = "订单已经锁定";
//                    }
                    break;
                // 【后台订单编辑金额】
                case OrderState.ORDER_OPERATE_CHANGE_AMOUNT:
                    if (!findOrder.getLockState().equals(order.getPrevLockState())) {
                        exceptionMsg = "订单已发起支付";
                    }
                    break;
                default:
                    exceptionMsg = "订单更新失败";
                    break;
            }

            throw new StateResult(AppConstants.ORDER_UPDATE_FAIL, exceptionMsg);
        }
    }

    @Override
    public List<OrderCountVo> listOrderbuy(Map<String, Object> paramMap) {
        return orderDao.listOrderCountVo(paramMap);
    }

    @Override
    public Page<OrderStatisticsVo> listOrderStatistics(Pageable pager) {
        PageList<OrderStatisticsVo> result = orderDao
            .listOrderStatisticsVo(pager.getParameter(), pager.getPageBounds());
        return new Page<>(result, result.getPaginator().getTotalCount(), pager);
    }

    @Override
    public List<OrderCountVo> listGroupPriceAndNumOfOrderType(Map<String, Object> paramMap) {
        if (log.isDebugEnabled()) {
            log.debug("执行listGroupPriceAndNumOfOrderType, 参数[{}]", paramMap.toString());
        }
        return orderDao.listGroupPriceAndNumOfOrderType(paramMap);
    }

    @Override
    public Page<OrderMemberStatisticsVo> topPurchase(Pageable pageable) {
        if (log.isDebugEnabled()) {
            log.debug("执行topPurchase, 参数[{}]", JacksonUtil.toJson(pageable));
        }
        PageList<OrderMemberStatisticsVo> result = orderDao
            .listOrderMemberStatisticsVo(pageable.getParameter(), pageable.getPageBounds());
        return new Page<>(result, result.getPaginator().getTotalCount(), pageable);
    }

    @Override
    public Page<GoodsStatisticsVo> listBestSellGoods(Pageable pageable) {
        if (log.isDebugEnabled()) {
            log.debug("执行listBestSellGoods, 参数[{}]", JacksonUtil.toJson(pageable));
        }
        PageList<GoodsStatisticsVo> result = orderDao
            .listGoodsStatisticsVo(pageable.getParameter(), pageable.getPageBounds());
        return new Page<>(result, result.getPaginator().getTotalCount(), pageable);
    }

    @Override
    public Page<StoreStatisticsVo> listBestOrderVolumeStore(Pageable pageable) {
        if (log.isDebugEnabled()) {
            log.debug("执行listBestOrderVolumeStore, 参数[{}]", JacksonUtil.toJson(pageable));
        }
        PageList<StoreStatisticsVo> result = orderDao
            .listStoreStatisticsVo(pageable.getParameter(), pageable.getPageBounds());
        return new Page<>(result, result.getPaginator().getTotalCount(), pageable);
    }

    @Override
    public List<OrderCountVo> listOrderCountVo(long storeId) {
        return orderDao.listOrderCountVoGroupByState(storeId);
    }


    @Override
    public List<ShopOrder> statisticsOrderPriceAndOrderNumByState(OrderView param) {
        return orderDao.statisticsOrderPriceAndOrderNumByState(param);
    }

    @Override
    public List<ActivityStatisticsVo> statisticsSeckillOrPromotionBystate(ActivityStatisticsVo param) {
        return orderDao.statisticsSeckillOrPromotionBystate(param);
    }

    @Override
    public ActivityStatisticsVo countSeckillSuccess(ActivityStatisticsVo param) {
        return orderDao.countSeckillSuccess(param);
    }

    @Override
    public List<StatsCountVo> listStatsCountVo() {
        return orderDao.listStatsCountVo();
    }

    @Override
    public List<CountOrderStatusVo> countOrderStatus(Map<String, Object> paramMap) {
        return orderDao.countOrderStatus(paramMap);
    }

    @Override
    public void ProcessingIntegrals(String paysn, Integer integration, RdMmBasicInfo shopMember, ShopOrderPay pay,
        Integer shoppingPointSr) {//购物积分购物比例
        //第一步 判断积分是否正确
        if (integration < 0) {
            throw new StateResult(AppConstants.GOODS_STATE_ERRO, "要使用的积分不能小于0");
        }
        //积分
        RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", shopMember.getMmCode());

        if (rdMmAccountInfo == null) {
            throw new StateResult(AppConstants.GOODS_STATE_ERRO, "用户积分不正确");
        }
        if (rdMmAccountInfo.getWalletStatus() != 0) {
            throw new StateResult(AppConstants.GOODS_STATE_ERRO, "用户积分未激活或者已冻结 ");
        }

        if (rdMmAccountInfo.getWalletBlance().compareTo(BigDecimal.valueOf(integration)) == -1) {
            throw new StateResult(AppConstants.GOODS_STATE_ERRO, "要使用的积分不能大于拥有积分");
        }

        BigDecimal shoppingPoints = new BigDecimal(integration * shoppingPointSr * 0.01);
        if (shoppingPoints.compareTo(pay.getPayAmount()) == 1) {
            throw new StateResult(AppConstants.GOODS_STATE_ERRO, "要抵现的不能大于订单金额");
        }
        //修改订单价格
        List<ShopOrder> orderList = super.findList("paySn", paysn);
        Long orderId = 0L;
        if (orderList != null && orderList.size() > 0) {
            for (ShopOrder order : orderList) {
                if (order.getOrderState() != 10) {
                    throw new StateResult(AppConstants.GOODS_STATE_ERRO, "订单已支付");
                }
                orderId = order.getId();
                int pointNum = 0;
                pointNum = new BigDecimal(
                    (order.getOrderAmount().doubleValue() / pay.getPayAmount().doubleValue()) * (integration))
                    .setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
                order.setUsePointNum(Optional.ofNullable(order.getUsePointNum()).orElse(0) + pointNum);//设置订单所用积分数量
                order.setPointRmbNum(Optional.ofNullable(order.getPointRmbNum()).orElse(BigDecimal.ZERO)
                    .add(new BigDecimal(pointNum * shoppingPointSr * 0.01).setScale(2, BigDecimal.ROUND_HALF_UP)));
                order.setOrderAmount(order.getOrderAmount()
                    .subtract(new BigDecimal(pointNum * shoppingPointSr * 0.01).setScale(2, BigDecimal.ROUND_HALF_UP)));
                orderDao.update(order);
            }
        } else {
            throw new StateResult(AppConstants.GOODS_STATE_ERRO, "订单不存在");
        }
        //更新用户购物积分
        RdMmAccountLog rdMmAccountLog = new RdMmAccountLog();
        rdMmAccountLog.setTransTypeCode("OP");
        rdMmAccountLog.setAccType("");
        rdMmAccountLog.setTrSourceType("SWB");
        rdMmAccountLog.setMmCode(shopMember.getMmCode());
        rdMmAccountLog.setMmNickName(shopMember.getMmNickName());
        rdMmAccountLog.setTrMmCode(shopMember.getMmCode());
        rdMmAccountLog.setBlanceBefore(rdMmAccountInfo.getWalletBlance());
        rdMmAccountLog.setAmount(BigDecimal.valueOf(integration));
        rdMmAccountLog.setTransDate(new Date());
        String period = rdSysPeriodDao.getSysPeriodService(new Date());
        rdMmAccountLog.setTransPeriod(period);
        rdMmAccountLog.setTrOrderOid(orderId);
        //无需审核直接成功
        rdMmAccountLog.setStatus(3);
        rdMmAccountLog.setCreationBy(shopMember.getMmNickName());
        rdMmAccountLog.setCreationTime(new Date());
        rdMmAccountLog.setAutohrizeBy(shopMember.getMmNickName());
        rdMmAccountLog.setAutohrizeTime(new Date());
        rdMmAccountInfo.setWalletBlance(rdMmAccountInfo.getWalletBlance().subtract(BigDecimal.valueOf(integration)));
        rdMmAccountLog.setBlanceAfter(rdMmAccountInfo.getWalletBlance());
        rdMmAccountInfoService.update(rdMmAccountInfo);
        rdMmAccountLogService.save(rdMmAccountLog);
    }


    @Override
    public void ProcessingIntegralsCoupon(String paysn, Integer integration, RdMmBasicInfo shopMember, ShopOrderPay pay,
        Integer shoppingPointSr) {//购物积分购物比例
        //第一步 判断积分是否正确
        if (integration < 0) {
            throw new StateResult(AppConstants.GOODS_STATE_ERRO, "要使用的积分不能小于0");
        }
        //积分
        RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", shopMember.getMmCode());

        if (rdMmAccountInfo == null) {
            throw new StateResult(AppConstants.GOODS_STATE_ERRO, "用户积分不正确");
        }
        if (rdMmAccountInfo.getWalletStatus() != 0) {
            throw new StateResult(AppConstants.GOODS_STATE_ERRO, "用户积分未激活或者已冻结 ");
        }

        if (rdMmAccountInfo.getWalletBlance().compareTo(BigDecimal.valueOf(integration)) == -1) {
            throw new StateResult(AppConstants.GOODS_STATE_ERRO, "要使用的积分不能大于拥有积分");
        }


        BigDecimal shoppingPoints = new BigDecimal(integration * shoppingPointSr * 0.01);
        if (shoppingPoints.compareTo(new BigDecimal("0")) == 1){//使用抵现积分大于0
            if (shoppingPoints.compareTo(pay.getPayAmount()) == 1) {
                throw new StateResult(AppConstants.GOODS_STATE_ERRO, "要抵现的不能大于订单金额");
            }
            if (shoppingPoints.compareTo(pay.getPayAmount()) == -1) {
                throw new StateResult(AppConstants.GOODS_STATE_ERRO, "要抵现的不能小于订单金额");
            }
        }
        //修改订单价格
        List<ShopOrder> orderList = super.findList("paySn", paysn);
        Long orderId = 0L;
        if (orderList != null && orderList.size() > 0) {
            for (ShopOrder order : orderList) {
                if (order.getOrderState() != 10) {
                    throw new StateResult(AppConstants.GOODS_STATE_ERRO, "订单已支付");
                }
                orderId = order.getId();
                int pointNum = 0;
                pointNum = new BigDecimal(
                    (order.getOrderAmount().doubleValue() / pay.getPayAmount().doubleValue()) * (integration))
                    .setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
                order.setUsePointNum(Optional.ofNullable(order.getUsePointNum()).orElse(0) + pointNum);//设置订单所用积分数量
                order.setPointRmbNum(Optional.ofNullable(order.getPointRmbNum()).orElse(BigDecimal.ZERO)
                    .add(new BigDecimal(pointNum * shoppingPointSr * 0.01).setScale(2, BigDecimal.ROUND_HALF_UP)));
                order.setOrderAmount(order.getOrderAmount()
                    .subtract(new BigDecimal(pointNum * shoppingPointSr * 0.01).setScale(2, BigDecimal.ROUND_HALF_UP)));
                orderDao.update(order);
            }
        } else {
            throw new StateResult(AppConstants.GOODS_STATE_ERRO, "订单不存在");
        }
        //更新用户购物积分
        RdMmAccountLog rdMmAccountLog = new RdMmAccountLog();
        rdMmAccountLog.setTransTypeCode("OP");
        rdMmAccountLog.setAccType("");
        rdMmAccountLog.setTrSourceType("SWB");
        rdMmAccountLog.setMmCode(shopMember.getMmCode());
        rdMmAccountLog.setMmNickName(shopMember.getMmNickName());
        rdMmAccountLog.setTrMmCode(shopMember.getMmCode());
        rdMmAccountLog.setBlanceBefore(rdMmAccountInfo.getWalletBlance());
        rdMmAccountLog.setAmount(BigDecimal.valueOf(integration));
        rdMmAccountLog.setTransDate(new Date());
        String period = rdSysPeriodDao.getSysPeriodService(new Date());
        rdMmAccountLog.setTransPeriod(period);
        rdMmAccountLog.setTrOrderOid(orderId);
        //无需审核直接成功
        rdMmAccountLog.setStatus(3);
        rdMmAccountLog.setCreationBy(shopMember.getMmNickName());
        rdMmAccountLog.setCreationTime(new Date());
        rdMmAccountLog.setAutohrizeBy(shopMember.getMmNickName());
        rdMmAccountLog.setAutohrizeTime(new Date());
        rdMmAccountInfo.setWalletBlance(rdMmAccountInfo.getWalletBlance().subtract(BigDecimal.valueOf(integration)));
        rdMmAccountLog.setBlanceAfter(rdMmAccountInfo.getWalletBlance());
        rdMmAccountInfoService.update(rdMmAccountInfo);
        rdMmAccountLogService.save(rdMmAccountLog);
    }
/*    @Override
    public void ProcessingIntegralsNew(String paysn, Double integration, RdMmBasicInfo shopMember, ShopOrderPay pay,
                                    int shoppingPointSr) {//购物积分购物比例
        //第一步 判断积分是否正确
        if (integration < 0) {
            throw new StateResult(AppConstants.GOODS_STATE_ERRO, "要使用的积分不能小于0");
        }
        //积分
        RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", shopMember.getMmCode());

        if (rdMmAccountInfo == null) {
            throw new StateResult(AppConstants.GOODS_STATE_ERRO, "用户积分不正确");
        }
        if (rdMmAccountInfo.getWalletStatus() != 0) {
            throw new StateResult(AppConstants.GOODS_STATE_ERRO, "用户积分未激活或者已冻结 ");
        }

        if (rdMmAccountInfo.getWalletBlance().compareTo(BigDecimal.valueOf(integration)) == -1) {
            throw new StateResult(AppConstants.GOODS_STATE_ERRO, "要使用的积分不能大于拥有积分");
        }

        BigDecimal shoppingPoints = new BigDecimal(integration * shoppingPointSr * 0.01);
        if (shoppingPoints.compareTo(pay.getPayAmount()) == 1) {
            throw new StateResult(AppConstants.GOODS_STATE_ERRO, "要抵现的不能大于订单金额");
        }
        //修改订单价格
        List<ShopOrder> orderList = super.findList("paySn", paysn);
        Long orderId = 0L;
        if (orderList != null && orderList.size() > 0) {
            for (ShopOrder order : orderList) {
                if (order.getOrderState() != 10) {
                    throw new StateResult(AppConstants.GOODS_STATE_ERRO, "订单已支付");
                }
                orderId = order.getId();
                int pointNum = 0;
                pointNum = new BigDecimal(
                        (order.getOrderAmount().doubleValue() / pay.getPayAmount().doubleValue()) * (integration))
                        .setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
                order.setUsePointNum(Optional.ofNullable(order.getUsePointNum()).orElse(0) + pointNum);//设置订单所用积分数量
                order.setPointRmbNum(Optional.ofNullable(order.getPointRmbNum()).orElse(BigDecimal.ZERO)
                        .add(new BigDecimal(pointNum * shoppingPointSr * 0.01).setScale(2, BigDecimal.ROUND_HALF_UP)));
                order.setOrderAmount(order.getOrderAmount()
                        .subtract(new BigDecimal(pointNum * shoppingPointSr * 0.01).setScale(2, BigDecimal.ROUND_HALF_UP)));
                orderDao.update(order);
            }
        } else {
            throw new StateResult(AppConstants.GOODS_STATE_ERRO, "订单不存在");
        }
        //更新用户购物积分
        RdMmAccountLog rdMmAccountLog = new RdMmAccountLog();
        rdMmAccountLog.setTransTypeCode("OP");
        rdMmAccountLog.setAccType("");
        rdMmAccountLog.setTrSourceType("SWB");
        rdMmAccountLog.setMmCode(shopMember.getMmCode());
        rdMmAccountLog.setMmNickName(shopMember.getMmNickName());
        rdMmAccountLog.setTrMmCode(shopMember.getMmCode());
        rdMmAccountLog.setBlanceBefore(rdMmAccountInfo.getWalletBlance());
        rdMmAccountLog.setAmount(BigDecimal.valueOf(integration));
        rdMmAccountLog.setTransDate(new Date());
        String period = rdSysPeriodDao.getSysPeriodService(new Date());
        rdMmAccountLog.setTransPeriod(period);
        rdMmAccountLog.setTrOrderOid(orderId);
        //无需审核直接成功
        rdMmAccountLog.setStatus(3);
        rdMmAccountLog.setCreationBy(shopMember.getMmNickName());
        rdMmAccountLog.setCreationTime(new Date());
        rdMmAccountLog.setAutohrizeBy(shopMember.getMmNickName());
        rdMmAccountLog.setAutohrizeTime(new Date());
        rdMmAccountInfo.setWalletBlance(rdMmAccountInfo.getWalletBlance().subtract(BigDecimal.valueOf(integration)));
        rdMmAccountLog.setBlanceAfter(rdMmAccountInfo.getWalletBlance());
        rdMmAccountInfoService.update(rdMmAccountInfo);
        rdMmAccountLogService.save(rdMmAccountLog);
    }*/

    @Override
    public ShopOrder findWithOrderGoodsById(Long orderId) {
        ShopOrder shopOrder = orderDao.find(orderId);
        if (shopOrder == null) {
            throw new RuntimeException("不存在订单");
        }

        List<ShopOrderGoods> orderGoods = orderGoodsDao
            .findByParams(Paramap.create().put("orderId", orderId));
        shopOrder.setShopOrderGoodses(orderGoods);
        return shopOrder;
    }

    @Override
    public Map<String, Object> updateOrderpay(PayCommon payCommon, String memberId, String payName, String paymentCode,
        String paymentId) {
        RdMmBasicInfo shopMember = rdMmBasicInfoService.find("mmCode", memberId);

        List<ShopOrder> orderList = findList("paySn", payCommon.getOutTradeNo());
        String orderSn = "";
        if (CollectionUtils.isNotEmpty(orderList)) {
            for (ShopOrder order : orderList) {
                if (order.getOrderState() != 10) {
                    throw new StateResult(AppConstants.GOODS_STATE_ERRO, "订单已支付");
                }
                if (order.getPaymentState() == 0) {
                    orderSn += order.getOrderSn() + ",";
                    //新建一个订单日志
                    ShopOrderLog orderLog = new ShopOrderLog();
                    orderLog.setId(twiterIdService.getTwiterId());
                    orderLog.setOrderState(OrderState.ORDER_STATE_UNFILLED + "");
                    orderLog.setChangeState(OrderState.ORDER_STATE_NOT_RECEIVING + "");
                    orderLog.setStateInfo("订单付款完成");
                    orderLog.setOrderId(order.getId());
                    orderLog.setOperator(shopMember.getMmCode());
                    orderLog.setCreateTime(new Date());
                    //保存订单日志
                    orderLogDao.insert(orderLog);
                    //修改订单状态
                    ShopOrder newOrder = new ShopOrder();
                    newOrder.setOrderState(OrderState.ORDER_STATE_UNFILLED);
                    newOrder.setPaymentState(OrderState.PAYMENT_STATE_YES);
                    //TODO update by zc 2019-11-14 支付修改优惠券状态
                    List<CouponDetail> couponDetailList = couponDetailService.findList(Paramap.create().put("holdId",memberId).put("useState",1)
                            .put("useOrderId",order.getId()).put("useOrderPayStatus",0));
                    if(couponDetailList!=null&&couponDetailList.size()>0){
                        for (CouponDetail couponDetail : couponDetailList) {
                            couponDetail.setUseOrderPayStatus(1);
                            couponDetailService.update(couponDetail);
                        }
                    }
                    //TODO***************************************************************
                    if(order.getOrderType()==1){//如果当前确认收货订单为零售订单，则产生零售利润，否则，跳过
                        String buyerId = order.getBuyerId()+"";
                        RetailProfit retailProfit = new RetailProfit();
                        retailProfit.setBuyerId(buyerId);
                        retailProfit.setCreateTime(new Date());
                        RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode",buyerId);
                        if(rdMmRelation!=null&&rdMmRelation.getSponsorCode()!=null){
                            retailProfit.setReceiptorId(rdMmRelation.getSponsorCode());
                        }
                        String per = rdSysPeriodDao.getSysPeriodService(new Date());
                        if(per!=null){
                            retailProfit.setCreatePeriod(per);
                        }
                        retailProfit.setOrderId(order.getId());
                        retailProfit.setOrderSn(order.getOrderSn());
                        retailProfit.setState(0);
                        BigDecimal profit=BigDecimal.ZERO;
                        List<ShopOrderGoods> shopOrderGoodsList = shopOrderGoodsService.findList("orderId", order.getId());
                        if(shopOrderGoodsList!=null&&shopOrderGoodsList.size()>0){
                            for (ShopOrderGoods shopOrderGoods : shopOrderGoodsList) {
                                ShopGoods shopGoods = goodsDao.find(shopOrderGoods.getGoodsId());
                                ShopGoodsSpec shopGoodsSpec = goodsSpecDao.find(shopGoods.getSpecId());
                                profit=profit.add(shopGoodsSpec.getSpecRetailProfit().multiply(new BigDecimal(shopOrderGoods.getGoodsNum())));
                            }
                        }
                        retailProfit.setProfits(profit);
                        retailProfitService.save(retailProfit);
                    }
                    newOrder.setPaymentTime(new Date());
                    String period = rdSysPeriodDao.getSysPeriodService(new Date());
                    order.setCreationPeriod(period);
                    orderDao.update(order);
                    newOrder.setTradeSn(payCommon.getOutTradeNo());
                    newOrder.setPaymentBranch(paymentCode);
                    //todo 暂时
                    newOrder.setPaymentName(payName);
                    newOrder.setPaymentCode(paymentCode);
                    newOrder.setPaymentId(Long.valueOf(paymentId));
                    // 条件
                    newOrder.setId(order.getId());
                    newOrder.setPrevOrderState(OrderState.ORDER_STATE_NO_PATMENT);
                    newOrder.setPrevLockState(OrderState.ORDER_LOCK_STATE_NO);
                    updateByIdOrderStateLockState(newOrder, OrderState.ORDER_OPERATE_PAY);

                    //判断会员等级，并根据升级条件升级 TODO
                    RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", memberId);
                    if (rdMmRelation != null) {//新会员判断累计购买额并影响等级
                        BigDecimal money = Optional.ofNullable(rdMmRelation.getARetail()).orElse(BigDecimal.ZERO);//获得累计零售购买额
                        BigDecimal aTotal = Optional.ofNullable(rdMmRelation.getATotal()).orElse(BigDecimal.ZERO);//获得累计购买额
                        BigDecimal orderMoney = Optional.ofNullable(order.getOrderAmount()).orElse(BigDecimal.ZERO)
                                .add(Optional.ofNullable(order.getPointRmbNum()).orElse(BigDecimal.ZERO)
                                        .add(Optional.ofNullable(order.getCouponDiscount()).orElse(BigDecimal.ZERO))
                                        .subtract(Optional.ofNullable(order.getShippingFee()).orElse(BigDecimal.ZERO)));//优惠券优惠金额记入订单金额
                        BigDecimal vipMoney = BigDecimal.valueOf(NewVipConstant.NEW_VIP_CONDITIONS_TOTAL);
                        BigDecimal ppv = Optional.ofNullable(rdMmRelation.getAPpv()).orElse(BigDecimal.ZERO);
                        BigDecimal orderPpv = Optional.ofNullable(order.getPpv()).orElse(BigDecimal.ZERO);
                        BigDecimal agencyPpv = BigDecimal.valueOf(NewVipConstant.NEW_AGENCY_CONDITIONS_TOTAL);
                        if(order.getOrderType()==1){
                            rdMmRelation.setARetail(rdMmRelation.getARetail().add(orderMoney));
                        }
                        //之前少于升级vip的价位 加上这个订单大于或者等于升级vip的价位
                        if (order.getOrderType()==1&&money.compareTo(vipMoney) == -1 && (money.add(orderMoney)).compareTo(vipMoney) != -1&&rdMmRelation.getNOFlag()==1) {
                            //进行用户升级通知
                            ShopCommonMessage message=new ShopCommonMessage();
                            message.setSendUid(rdMmRelation.getMmCode());
                            message.setType(1);
                            message.setOnLine(1);
                            message.setCreateTime(new Date());
                            message.setBizType(2);
                            message.setIsTop(1);
                            message.setCreateTime(new Date());
                            message.setTitle("恭喜，升级啦");
                            message.setContent("您已从普通会员升级成VIP会员,祝您购物愉快");
                            Long msgId = twiterIdService.getTwiterId();
                            message.setId(msgId);
                            shopCommonMessageDao.insert(message);
                            ShopMemberMessage shopMemberMessage=new ShopMemberMessage();
                            shopMemberMessage.setBizType(2);
                            shopMemberMessage.setCreateTime(new Date());
                            shopMemberMessage.setId(twiterIdService.getTwiterId());
                            shopMemberMessage.setIsRead(0);
                            shopMemberMessage.setMsgId(msgId);
                            shopMemberMessage.setUid(Long.parseLong(rdMmRelation.getMmCode()));
                            shopMemberMessageDao.insert(shopMemberMessage);
                            RdRanks rdRanks = rdRanksService.find("rankClass", 1);
                            rdMmRelation.setRank(rdRanks.getRankId());
                            //如果会员升级为vip则查询当期会员是否存在没有发放的零售利润奖励
                            List<RetailProfit> list=retailProfitService.findNoGrantByCode(rdMmRelation.getMmCode());
                            if(list!=null&&list.size()>0){
                                for (RetailProfit retailProfit : list) {
                                    if(retailProfit.getProfits()!=null&&retailProfit.getProfits().compareTo(BigDecimal.ZERO)!=1){
                                        continue;
                                    }
                                    RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", retailProfit.getReceiptorId());
                                    if(rdMmAccountInfo!=null){
                                        //生成积分日志表
                                        RdMmAccountLog rdMmAccountLog = new RdMmAccountLog();
                                        rdMmAccountLog.setMmCode(retailProfit.getReceiptorId());
                                        RdMmBasicInfo basicInfo = rdMmBasicInfoService.find("mmCode", retailProfit.getReceiptorId());
                                        rdMmAccountLog.setMmNickName(basicInfo.getMmNickName());
                                        rdMmAccountLog.setTransTypeCode("BA");
                                        rdMmAccountLog.setAccType("SBB");
                                        rdMmAccountLog.setTrSourceType("CMP");
                                        rdMmAccountLog.setTrOrderOid(retailProfit.getOrderId());
                                        rdMmAccountLog.setBlanceBefore(rdMmAccountInfo.getBonusBlance());
                                        List<RdMmIntegralRule> rules = rdMmIntegralRuleService.findAll();
                                        BigDecimal amount = retailProfit.getProfits().multiply(new BigDecimal(rules.get(0).getRsCountBonusPoint())).divide(new BigDecimal(100),2);
                                        rdMmAccountLog.setAmount(amount);
                                        rdMmAccountLog.setBlanceAfter(rdMmAccountInfo.getBonusBlance().add(amount));
                                        rdMmAccountLog.setTransDate(new Date());
                                        String periodStr = rdSysPeriodDao.getSysPeriodService(new Date());
                                        if(periodStr!=null){
                                            rdMmAccountLog.setTransPeriod(period);
                                        }
                                        rdMmAccountLog.setTransDesc("零售利润奖励发放");
                                        rdMmAccountLog.setStatus(3);
                                        rdMmAccountLogService.save(rdMmAccountLog);
                                        //修改积分账户
                                        rdMmAccountInfo.setBonusBlance(rdMmAccountInfo.getBonusBlance().add(amount));
                                        rdMmAccountInfoService.update(rdMmAccountInfo);
                                        //修改零售利润
                                        retailProfit.setReceiptorId(rdMmRelation.getMmCode());//TODO
                                        retailProfit.setActualTime(new Date());
                                        String periodCode = rdSysPeriodDao.getSysPeriodService(retailProfit.getActualTime());
                                        if(periodCode!=null){
                                            retailProfit.setActualPeriod(periodCode);
                                        }
                                        retailProfit.setState(1);
                                        retailProfitService.update(retailProfit);
                                        //设置零售利润积分发放通知
                                        ShopCommonMessage shopCommonMessage=new ShopCommonMessage();
                                        shopCommonMessage.setSendUid(rdMmRelation.getMmCode());
                                        shopCommonMessage.setType(1);
                                        shopCommonMessage.setOnLine(1);
                                        shopCommonMessage.setCreateTime(new Date());
                                        shopCommonMessage.setBizType(2);
                                        shopCommonMessage.setIsTop(1);
                                        shopCommonMessage.setCreateTime(new Date());
                                        shopCommonMessage.setTitle("积分到账通知");
                                        shopCommonMessage.setContent("您从零售订单："+retailProfit.getOrderSn()+"获得"+amount+"点积分，已加入奖励积分账户");
                                        Long msgId1 = twiterIdService.getTwiterId();
                                        shopCommonMessage.setId(msgId1);
                                        shopCommonMessageDao.insert(shopCommonMessage);
                                        ShopMemberMessage shopMemberMessage1=new ShopMemberMessage();
                                        shopMemberMessage1.setBizType(2);
                                        shopMemberMessage1.setCreateTime(new Date());
                                        shopMemberMessage1.setId(twiterIdService.getTwiterId());
                                        shopMemberMessage1.setIsRead(0);
                                        shopMemberMessage1.setMsgId(msgId1);
                                        shopMemberMessage1.setUid(Long.parseLong(rdMmRelation.getMmCode()));
                                        shopMemberMessageDao.insert(shopMemberMessage1);
                                    }
                                }
                            }
                        }
                        /*if (ppv.compareTo(agencyPpv) == -1 && (ppv.add(orderPpv)).compareTo(agencyPpv) != -1) {
                            RdRanks rdRanks = rdRanksService.find("rankClass", 2);
                            rdMmRelation.setRank(rdRanks.getRankId());
                        }*/
                        ////晋升
                        ////    //发送升级的消息队列
                        //    try {
                        //        Producer producer = new Producer("PromotionVIP");
                        //        PromotionVipResult promotionVipResult = new PromotionVipResult();
                        //        promotionVipResult.setMmCode(order.getBuyerId() + "");
                        //        promotionVipResult.setDate(new Date());
                        //       producer.sendMessage(SerializationUtils.serialize(promotionVipResult));
                        //    } catch (IOException e) {
                        //        e.printStackTrace();
                        //    }
                        ////}
                        rdMmRelation.setAPpv(ppv.add(orderPpv));
                        rdMmRelation.setATotal(aTotal.add(orderMoney));
                        rdMmRelationService.update(rdMmRelation);
                    }
                }
                //换购订单 扣除换购积分 并生成记录
                if ("10".equals(paymentId)) {
                    //设置换购积分消息通知
                    ShopCommonMessage shopCommonMessage=new ShopCommonMessage();
                    shopCommonMessage.setSendUid(memberId);
                    shopCommonMessage.setType(1);
                    shopCommonMessage.setOnLine(1);
                    shopCommonMessage.setCreateTime(new Date());
                    shopCommonMessage.setBizType(2);
                    shopCommonMessage.setIsTop(1);
                    shopCommonMessage.setCreateTime(new Date());
                    shopCommonMessage.setTitle("积分扣减通知");
                    shopCommonMessage.setContent("您因支付换购订单："+order.getOrderSn()+"订单扣减"+order.getOrderAmount()+"点积分，请在换购积分账户查看明细");
                    Long msgId1 = twiterIdService.getTwiterId();
                    shopCommonMessage.setId(msgId1);
                    shopCommonMessageDao.insert(shopCommonMessage);
                    ShopMemberMessage shopMemberMessage1=new ShopMemberMessage();
                    shopMemberMessage1.setBizType(2);
                    shopMemberMessage1.setCreateTime(new Date());
                    shopMemberMessage1.setId(twiterIdService.getTwiterId());
                    shopMemberMessage1.setIsRead(0);
                    shopMemberMessage1.setMsgId(msgId1);
                    shopMemberMessage1.setUid(Long.parseLong(memberId));
                    shopMemberMessageDao.insert(shopMemberMessage1);
                    RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", shopMember.getMmCode());
                    RdMmAccountLog rdMmAccountLog = new RdMmAccountLog();
                    rdMmAccountLog.setTransTypeCode("EG");
                    rdMmAccountLog.setAccType("");
                    rdMmAccountLog.setTrSourceType("CMP");
                    rdMmAccountLog.setMmCode(shopMember.getMmCode());
                    rdMmAccountLog.setMmNickName(shopMember.getMmNickName());
                    rdMmAccountLog.setTrMmCode(shopMember.getMmCode());
                    rdMmAccountLog.setBlanceBefore(rdMmAccountInfo.getRedemptionBlance());
                    rdMmAccountLog.setAmount(order.getOrderAmount());
                    rdMmAccountLog.setTrOrderOid(order.getId());
                    rdMmAccountLog.setTransDate(new Date());
                    String period = rdSysPeriodDao.getSysPeriodService(new Date());
                    rdMmAccountLog.setTransPeriod(period);
                    //提现需审核初始为申请状态
                    rdMmAccountLog.setStatus(3);
                    rdMmAccountLog.setCreationBy(shopMember.getMmNickName());
                    rdMmAccountLog.setCreationTime(new Date());
                    rdMmAccountInfo
                        .setRedemptionBlance(rdMmAccountInfo.getRedemptionBlance().subtract(order.getOrderAmount()));
                    rdMmAccountLog.setBlanceAfter(rdMmAccountInfo.getRedemptionBlance());
                    List<RdMmAccountLog> rdMmAccountLogList = new ArrayList<>();
                    rdMmAccountLogList.add(rdMmAccountLog);
                    Integer transNumber = rdMmAccountInfoService
                        .saveAccountInfo(rdMmAccountInfo, order.getOrderAmount().intValue(), IntegrationNameConsts.PUI,
                            rdMmAccountLogList, null);
                }

//                // 用户增加消费积分
//                addMemberConsumePoints(payCommon.getPayAmount().doubleValue(), shopMember.getId());
            }
            Map<String, Object> result = Maps.newConcurrentMap();
            result.put("status", 1);
            result.put("orderSn", orderSn);
            result.put("message", "支付成功");
            return result;

        }
        Map<String, Object> result = Maps.newConcurrentMap();
        result.put("status", 0);
        result.put("message", "支付失败");
        return result;
    }

    @Override
    public List<OrderCountVo> countMemberOrderNum(Map<String, Object> paramMap) {
        return orderDao.countMemberOrderNum(paramMap);
    }

    @Override
    public void addRefundPoint(ShopRefundReturn refundReturn) {
        if (refundReturn.getRewardPointAmount().compareTo(BigDecimal.ZERO) == 1) {
            ShopOrder order = orderDao.find(refundReturn.getOrderId());
            RdMmBasicInfo shopMember = rdMmBasicInfoService.find("mmCode", refundReturn.getBuyerId());
            RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", shopMember.getMmCode());
            BigDecimal money = Optional.ofNullable(order.getRefundAmount()).orElse(BigDecimal.valueOf(0));
            BigDecimal ppv = Optional.ofNullable(order.getRefundPpv()).orElse(BigDecimal.ZERO);
            BigDecimal point = Optional.ofNullable(order.getRefundPoint()).orElse(BigDecimal.valueOf(0));
            ShopOrder newShopOrder = new ShopOrder();
            newShopOrder.setId(order.getId());
            newShopOrder.setRefundAmount(money.add(refundReturn.getRefundAmount()));
            newShopOrder.setRefundPpv(ppv.add(refundReturn.getPpv()));
            newShopOrder.setRefundPoint(refundReturn.getRewardPointAmount().add(point));
            RdMmAccountLog rdMmAccountLog = new RdMmAccountLog();
            rdMmAccountLog.setTransTypeCode("OT");
            rdMmAccountLog.setAccType("SWB");
            rdMmAccountLog.setTrSourceType("OWB");
            rdMmAccountLog.setMmCode(shopMember.getMmCode());
            rdMmAccountLog.setMmNickName(shopMember.getMmNickName());
            rdMmAccountLog.setTrMmCode(shopMember.getMmCode());
            rdMmAccountLog.setBlanceBefore(rdMmAccountInfo.getWalletBlance());
            rdMmAccountLog.setAmount(refundReturn.getRewardPointAmount());
            //无需审核直接成功
            rdMmAccountLog.setStatus(3);
            rdMmAccountLog.setCreationBy(shopMember.getMmNickName());
            rdMmAccountLog.setCreationTime(new Date());
            rdMmAccountLog.setAutohrizeBy("后台管理员");
            rdMmAccountLog.setAutohrizeTime(new Date());
            rdMmAccountInfo.setWalletBlance(rdMmAccountInfo.getWalletBlance().add(refundReturn.getRewardPointAmount()));
            rdMmAccountLog.setBlanceAfter(point.add(rdMmAccountInfo.getWalletBlance()));
            rdMmAccountLog.setTransDate(new Date());
            rdMmAccountLogService.save(rdMmAccountLog);
            rdMmAccountInfoService.update(rdMmAccountInfo);
            orderDao.update(newShopOrder);
            BigDecimal totalMoney = Optional.ofNullable(newShopOrder.getOrderAmount()).orElse(BigDecimal.ZERO).
                    add(Optional.ofNullable(newShopOrder.getPointRmbNum()).orElse(BigDecimal.ZERO)).subtract(Optional.ofNullable(order.getShippingFee()).orElse(BigDecimal.ZERO));
            Boolean flag=false;
            if((Optional.ofNullable(newShopOrder.getRefundAmount()).orElse(BigDecimal.ZERO).add(Optional.ofNullable(newShopOrder.getRefundPoint()).orElse(BigDecimal.ZERO))).compareTo(totalMoney)!=-1){
                flag=true;
            }
            //判断是否符合降级条件进行降级
            RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", order.getBuyerId());
            if (rdMmRelation != null) {
                BigDecimal aRetail = rdMmRelation.getARetail();
                BigDecimal aTotal = rdMmRelation.getATotal();
                BigDecimal vipMoney = BigDecimal.valueOf(NewVipConstant.NEW_VIP_CONDITIONS_TOTAL);
                //退积分也要转换为钱 用于分辨用户会员等级升降
                List<RdMmIntegralRule> rdMmIntegralRuleList = rdMmIntegralRuleService
                    .findList(Paramap.create().put("order", "RID desc"));
                RdMmIntegralRule rdMmIntegralRule = new RdMmIntegralRule();
                if (rdMmIntegralRuleList != null && rdMmIntegralRuleList.size() > 0) {
                    rdMmIntegralRule = rdMmIntegralRuleList.get(0);
                }
                int shoppingPointSr = Optional.ofNullable(rdMmIntegralRule.getShoppingPointSr()).orElse(0);
                BigDecimal orderMoney=BigDecimal.ZERO;
                BigDecimal orderMoneyTotal=BigDecimal.ZERO;
                if(flag){
                    orderMoney =orderMoney.add(aRetail).subtract(refundReturn.getRefundAmount().add(new BigDecimal(refundReturn.getRewardPointAmount().doubleValue() * shoppingPointSr * 0.01).setScale(2, BigDecimal.ROUND_HALF_UP)).add(Optional.ofNullable(order.getCouponDiscount()).orElse(BigDecimal.ZERO)));
                    orderMoneyTotal =orderMoneyTotal.add(aTotal).subtract(refundReturn.getRefundAmount().add(new BigDecimal(refundReturn.getRewardPointAmount().doubleValue() * shoppingPointSr * 0.01).setScale(2, BigDecimal.ROUND_HALF_UP)).add(Optional.ofNullable(order.getCouponDiscount()).orElse(BigDecimal.ZERO)));
                }else {
                    orderMoney =orderMoney.add(aRetail).subtract(refundReturn.getRefundAmount().add(new BigDecimal(refundReturn.getRewardPointAmount().doubleValue() * shoppingPointSr * 0.01).setScale(2, BigDecimal.ROUND_HALF_UP)));
                    orderMoneyTotal =orderMoneyTotal.add(aTotal).subtract(refundReturn.getRefundAmount().add(new BigDecimal(refundReturn.getRewardPointAmount().doubleValue() * shoppingPointSr * 0.01).setScale(2, BigDecimal.ROUND_HALF_UP)));
                }
                BigDecimal aPpv = Optional.ofNullable(rdMmRelation.getAPpv()).orElse(BigDecimal.ZERO);
                BigDecimal agencyPpv = BigDecimal.valueOf(NewVipConstant.NEW_AGENCY_CONDITIONS_TOTAL);
                BigDecimal orderPpv = BigDecimal.ZERO;
                /*if (aPpv.compareTo(BigDecimal.ZERO) != 0) {
                    orderPpv = aPpv.subtract(refundReturn.getPpv());
                }*/
                rdMmRelation.setATotal(orderMoneyTotal);
                rdMmRelation.setAPpv(aPpv.subtract(refundReturn.getPpv()));
                if(order.getOrderType()==1){
                    rdMmRelation.setARetail(orderMoney);
                }
                /*//降级到vip会员
                if ((aPpv.compareTo(agencyPpv) == 1||aPpv.compareTo(agencyPpv) == 0) && orderPpv.compareTo(agencyPpv) == -1&&rdMmRelation.getNOFlag()==1) {
                    RdRanks rdRanks = rdRanksService.find("rankClass", 1);
                    rdMmRelation.setRank(rdRanks.getRankId());
                }*/
                //******************************************降级到普通会员****************************************************************** TODO 修改by zc 2019-07-24
                if (order.getOrderType()==1&&aRetail.compareTo(vipMoney)!=-1&&orderMoney.compareTo(vipMoney)==-1&&rdMmRelation.getNOFlag()==1) {//新会员 退款之前累计购买额大于等于360退款之后小于360降级vip
                    rdMmRelation.setRank(0);
                    //进行用户降级通知
                    ShopCommonMessage shopCommonMessage=new ShopCommonMessage();
                    shopCommonMessage.setSendUid(rdMmRelation.getMmCode());
                    shopCommonMessage.setType(1);
                    shopCommonMessage.setOnLine(1);
                    shopCommonMessage.setCreateTime(new Date());
                    shopCommonMessage.setBizType(2);
                    shopCommonMessage.setIsTop(1);
                    shopCommonMessage.setCreateTime(new Date());
                    shopCommonMessage.setTitle("很遗憾，等级降了");
                    shopCommonMessage.setContent("您已从VIP会员变成普通会员,多多购物可提升等级哦");
                    Long msgId = twiterIdService.getTwiterId();
                    shopCommonMessage.setId(msgId);
                    shopCommonMessageDao.insert(shopCommonMessage);
                    ShopMemberMessage shopMemberMessage=new ShopMemberMessage();
                    shopMemberMessage.setBizType(2);
                    shopMemberMessage.setCreateTime(new Date());
                    shopMemberMessage.setId(twiterIdService.getTwiterId());
                    shopMemberMessage.setIsRead(0);
                    shopMemberMessage.setMsgId(msgId);
                    shopMemberMessage.setUid(Long.parseLong(rdMmRelation.getMmCode()));
                    shopMemberMessageDao.insert(shopMemberMessage);
                }
                //************************************************************************************************************
                //之前大于升级vip的价位 加上这个售后金额小于vip的价位
                /*if ((aRetail.compareTo(vipMoney) == 1||aRetail.compareTo(vipMoney) == 0) && orderMoney.compareTo(vipMoney) == -1) {
                    RdRanks rdRanks = rdRanksService.find("rankClass", 0);
                    rdMmRelation.setRank(rdRanks.getRankId());
                }*/
                ////发送升级的消息队列
                //try {
                //    Producer producer = new Producer("DowngradeVIP");
                //    PromotionVipResult promotionVipResult = new PromotionVipResult();
                //    promotionVipResult.setMmCode(order.getBuyerId() + "");
                //    promotionVipResult.setDate(new Date());
                //    producer.sendMessage(SerializationUtils.serialize(promotionVipResult));
                //} catch (IOException e) {
                //    e.printStackTrace();
                //}

                /*if (order.getPpv().compareTo(BigDecimal.ZERO) != 0) {
                    rdMmRelation.setAPpv(orderPpv);
                }*/
                rdMmRelationService.update(rdMmRelation);
            }

        }else{
            ShopOrder order = orderDao.find(refundReturn.getOrderId());
            BigDecimal money = Optional.ofNullable(order.getRefundAmount()).orElse(BigDecimal.valueOf(0));
            BigDecimal ppv = Optional.ofNullable(order.getRefundPpv()).orElse(BigDecimal.ZERO);
            ShopOrder newShopOrder = new ShopOrder();
            newShopOrder.setId(order.getId());
            newShopOrder.setRefundAmount(money.add(refundReturn.getRefundAmount()));
            newShopOrder.setRefundPpv(ppv.add(refundReturn.getPpv()));
            orderDao.update(newShopOrder);
            //判断是否符合降级条件进行降级
            RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", order.getBuyerId());
            if (rdMmRelation != null) {
                BigDecimal aRetail = rdMmRelation.getARetail();
                BigDecimal aTotal = rdMmRelation.getATotal();
                BigDecimal vipMoney = BigDecimal.valueOf(NewVipConstant.NEW_VIP_CONDITIONS_TOTAL);
                BigDecimal orderMoney = aRetail.subtract(refundReturn.getRefundAmount());
                BigDecimal orderMoneyTotal = aTotal.subtract(refundReturn.getRefundAmount());
                BigDecimal aPpv = Optional.ofNullable(rdMmRelation.getAPpv()).orElse(BigDecimal.ZERO);
                BigDecimal agencyPpv = BigDecimal.valueOf(NewVipConstant.NEW_AGENCY_CONDITIONS_TOTAL);
                BigDecimal orderPpv = BigDecimal.ZERO;
                /*if (aPpv.compareTo(BigDecimal.ZERO) != 0) {
                    orderPpv = aPpv.subtract(refundReturn.getPpv());
                }*/
                /*//降级到vip会员
                if ((aPpv.compareTo(agencyPpv) == 1||aPpv.compareTo(agencyPpv) == 0) && orderPpv.compareTo(agencyPpv) == -1&&rdMmRelation.getNOFlag()==1) {
                    RdRanks rdRanks = rdRanksService.find("rankClass", 1);
                    rdMmRelation.setRank(rdRanks.getRankId());
                }*/
                //之前大于升级vip的价位 加上这个售后金额小于vip的价位
                if (order.getOrderType()==1&&aRetail.compareTo(vipMoney) != -1 && orderMoney.compareTo(vipMoney) == -1&&rdMmRelation.getNOFlag()==1) {
                    RdRanks rdRanks = rdRanksService.find("rankClass", 0);
                    rdMmRelation.setRank(rdRanks.getRankId());
                    rdMmRelation.setARetail(orderMoney);
                }
                /*if (order.getPpv().compareTo(BigDecimal.ZERO) != 0) {
                    rdMmRelation.setAPpv(orderPpv);
                }*/
                rdMmRelation.setATotal(orderMoneyTotal);
                rdMmRelation.setAPpv(aPpv.subtract(refundReturn.getPpv()));
                rdMmRelationService.update(rdMmRelation);
            }
        }
    }

    @Override
    public void modifyOrderInfo(Long goodsId, String specJson, Long orderGoodsId, Long orderId) {
        List<ShopGoodSpec> specList = JacksonUtil.convertList(specJson, ShopGoodSpec.class);
        String shopOrderGoodsJson = redisService.get(orderId + "");
        ShopOrder shopOrder = orderDao.find(orderId);
        List<ShopOrderGoods> shopOrderGoodsList = JacksonUtil.convertList(shopOrderGoodsJson, ShopOrderGoods.class);
        ShopOrderGoods shopOrderGoods = new ShopOrderGoods();
        int a = 0;
        for (int i = 0; i < shopOrderGoodsList.size(); i++) {
            if (specList.get(0).getId().equals(shopOrderGoodsList.get(i).getSpecId() + "")) {
                shopOrderGoods = shopOrderGoodsList.get(i);
                a = 1;
            }
        }
        for (ShopGoodSpec shopGoodSpec : specList) {
            ShopGoods goods = goodsDao.find(goodsId);
            ShopGoodsSpec goodsSpec = goodsSpecDao.find(Long.parseLong(shopGoodSpec.getId()));
            shopOrderGoods.setSpecId(goodsSpec.getId());
            GoodsUtils.getSepcMapAndColImgToGoodsSpec(goods, goodsSpec);
            String specInfo = "";
            Map<String, String> map = goodsSpec.getSepcMap();
            //遍历规格map,取出键值对,拼接specInfo
            if (map != null) {
                Set<String> set = map.keySet();
                for (String str : set) {
                    specInfo += str + ":" + map.get(str) + "、";
                }
                specInfo = specInfo.substring(0, specInfo.length() - 1);
            }
            shopOrderGoods.setSpecInfo(specInfo);
            shopOrderGoods.setGoodsPrice(goodsSpec.getSpecBigPrice());
            shopOrderGoods.setMarketPrice(goodsSpec.getSpecRetailPrice());
            shopOrderGoods.setPpv(goodsSpec.getPpv());
            shopOrderGoods.setBigPpv(goodsSpec.getBigPpv());
            shopOrderGoods.setVipPrice(goodsSpec.getSpecMemberPrice());

            shopOrderGoods.setGoodsNum(Integer.parseInt(shopGoodSpec.getStorage()));
            if (a == 0) {
                shopOrderGoods.setId(twiterIdService.getTwiterId());
                shopOrderGoods.setGoodsImage(goods.getGoodsImage());
                shopOrderGoods.setGoodsName(goods.getGoodsName());
                shopOrderGoods.setGoodsId(goods.getId());
                shopOrderGoods.setOrderId(orderId);
                shopOrderGoods.setEvaluationStatus(0);
                shopOrderGoods.setGoodsReturnnum(0);
                shopOrderGoods.setGoodsBarternum(0);
                shopOrderGoods.setStoresId(0L);
                shopOrderGoods.setShippingExpressId(0L);
                shopOrderGoods.setBuyerId(shopOrder.getBuyerId() + "");
                shopOrderGoods.setGoodsType(goods.getGoodsType());
                shopOrderGoods.setShippingGoodsNum(0);
                shopOrderGoodsList.add(shopOrderGoods);
            }
            redisService.save(orderId + "", shopOrderGoodsList);
        }
    }

    @Override
    public ShopOrderVo modifyOrderCalculatePrice(ShopOrderVo orderVo, Long orderId, Long shopOrderTypeId) {
        ShopOrderDiscountType shopOrderDiscountType = shopOrderDiscountTypeDao.find(shopOrderTypeId);
        String shopOrderGoodsJson = redisService.get(orderId + "");
        List<ShopOrderGoods> shopOrderGoodsList = JacksonUtil.convertList(shopOrderGoodsJson, ShopOrderGoods.class);
        BigDecimal goodsAmount = BigDecimal.ZERO;
        BigDecimal orderTotalPrice = BigDecimal.ZERO;
        BigDecimal orderAmount = BigDecimal.ZERO;
        BigDecimal ppv = BigDecimal.ZERO;
        Double totalWeight = 0D;
        for (ShopOrderGoods item : shopOrderGoodsList) {
            goodsAmount = goodsAmount.add(item.getMarketPrice().multiply(new BigDecimal(item.getGoodsNum())));
            if (shopOrderDiscountType == null
                || shopOrderDiscountType.getPreferentialType() == ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_RETAIL) {
                orderTotalPrice = orderTotalPrice
                    .add(item.getMarketPrice().multiply(new BigDecimal(item.getGoodsNum())));
                orderAmount = orderAmount.add(item.getMarketPrice().multiply(new BigDecimal(item.getGoodsNum())));
                orderVo.setOrderType(1);
                item.setGoodsPayPrice(item.getMarketPrice());
            } else if (shopOrderDiscountType.getPreferentialType()
                == ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_MEMBER) {
                orderTotalPrice = orderTotalPrice.add(item.getVipPrice().multiply(new BigDecimal(item.getGoodsNum())));
                orderAmount = orderAmount.add(item.getVipPrice().multiply(new BigDecimal(item.getGoodsNum())));
                orderVo.setOrderType(2);
                item.setGoodsPayPrice(item.getVipPrice());
            } else if (shopOrderDiscountType.getPreferentialType() == ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_PPV) {
                orderTotalPrice = orderTotalPrice
                    .add(item.getGoodsPrice().multiply(new BigDecimal(item.getGoodsNum())));
                orderAmount = orderAmount.add(item.getGoodsPrice().multiply(new BigDecimal(item.getGoodsNum())));
                orderVo.setOrderType(3);
                item.setGoodsPayPrice(item.getGoodsPrice());
            } else if (shopOrderDiscountType.getPreferentialType()
                == ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_PREFERENTIAL) {
                orderTotalPrice = orderTotalPrice.add(
                    item.getMarketPrice().subtract(shopOrderDiscountType.getPreferential())
                        .multiply(new BigDecimal(item.getGoodsNum())));
                orderAmount = orderAmount.add(item.getMarketPrice().subtract(shopOrderDiscountType.getPreferential())
                    .multiply(new BigDecimal(item.getGoodsNum())));
                orderVo.setOrderType(4);
                item.setGoodsPayPrice(item.getMarketPrice().subtract(shopOrderDiscountType.getPreferential()));
            }
            if (shopOrderDiscountType != null
                && shopOrderDiscountType.getPreferentialType() == ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_PPV) {
                ppv = ppv.add(item.getBigPpv().multiply(BigDecimal.valueOf(item.getGoodsNum())));
//                ppv+=item.getBigPpv()*item.getGoodsNum();
            } else {
                ppv = ppv.add(item.getPpv().multiply(BigDecimal.valueOf(item.getGoodsNum())));
//                ppv+=item.getPpv()*item.getGoodsNum();
            }
            totalWeight += Optional.ofNullable(item.getWeight()).orElse(0D) * item.getGoodsNum();
        }
        orderVo.setShopOrderTypeId(shopOrderTypeId);
        if (orderVo.getLogisticType() == 1) {
            //运费
            ShopCommonArea shopCommonArea = areaService.find(orderVo.getAddress().getProvinceId());
            BigDecimal freightAmount = shopGoodsFreightService
                .CalculateFreight(shopCommonArea.getAreaName(), totalWeight);
            //运费优惠
            BigDecimal preferentialFreightAmount = shopGoodsFreightRuleService
                .CalculateFreightDiscount(orderVo.getBuyerId().toString(), goodsAmount);
            if (preferentialFreightAmount.compareTo(new BigDecimal("0")) == 0) {
                orderVo.setShippingFee(freightAmount);
                orderVo.setShippingPreferentialFee(new BigDecimal("0"));
            }
            if (freightAmount.compareTo(new BigDecimal("0")) == 0) {
                orderVo.setShippingFee(BigDecimal.ZERO);
                orderVo.setShippingPreferentialFee(new BigDecimal("0"));
            }
        } else {
            orderVo.setShippingFee(BigDecimal.ZERO);
            orderVo.setShippingPreferentialFee(BigDecimal.ZERO);
        }
        orderVo.setGoodsAmount(goodsAmount);
        orderVo.setDiscount(goodsAmount.subtract(orderAmount));
        orderAmount = orderAmount.add(orderVo.getShippingFee()).subtract(orderVo.getShippingPreferentialFee());
        orderVo.setOrderTotalPrice(orderAmount);
        orderVo.setOrderAmount(orderAmount);
        orderVo.setPpv(ppv);
        redisService.save(orderId + "", shopOrderGoodsList);
        return orderVo;
    }

    @Override
    public ShopOrderVo modifyOrderSubmit(Long orderId) {
        ShopOrderVo orderVo = redisService.get(orderId + "orderVo", ShopOrderVo.class);
        ShopOrder oldShopOrder = orderDao.find(orderId);
        String shopOrderGoodsJson = redisService.get(orderId + "");
        if (shopOrderGoodsJson == null) {
            throw new RuntimeException("订单商品不能为空");
        }
        List<ShopOrderGoods> shopOrderGoodsList = JacksonUtil.convertList(shopOrderGoodsJson, ShopOrderGoods.class);
        ShopOrderGoods shopOrderGoods = new ShopOrderGoods();
        shopOrderGoods.setOrderId(orderId);
        shopOrderGoodsService.deleteByEntity(shopOrderGoods);
        shopOrderGoodsService.insertBatch(shopOrderGoodsList);
        ShopOrder shopOrder = new ShopOrder();
        BeanUtils.copyProperties(orderVo, shopOrder);
        shopOrder.setCreateTime(oldShopOrder.getCreateTime());
        shopOrder.setIsModify(1);
        orderDao.update(shopOrder);
        redisService.delete(orderId + "");
        redisService.delete(orderId + "orderVo");
        return orderVo;
    }

    @Override
    public OrderSumPpv sumPpv(Map<String, Object> paramMap) {
        return orderDao.sumPpv(paramMap);
    }

    @Override
    public BigDecimal countOrderPPVByMCodeAndPeriod(String mmCode, String period) {
        Map<String,Object> map = new HashMap<>();
        map.put("creationPeriod",period);
        map.put("buyerId",mmCode);
        /*BigDecimal pvAll = orderDao.countOrderPPVByMCodeAndPeriod(map);
        BigDecimal pvNot0 = orderDao.countOrderPPVRefundStateNot0(map);
        BigDecimal pv =  pvAll.subtract(pvNot0);*/
        return orderDao.countOrderPPVByMCodeAndPeriod(map);
    }

    @Override
    public OrderSumPpv findByPeriod(Paramap periodCode) {
        return orderDao.findByPeriod(periodCode);
    }

    @Override
    public BigDecimal findOrderRetail(Paramap buyerId) {
        return orderDao.findOrderRetail(buyerId);
    }

    @Override
    public List<ShopOrder> findStatu20() {
        return orderDao.findStatu20();
    }

    @Override
    public void updateOrderStatus(String orderSn, Integer orderState, Integer submitStatus, String failInfo, String trackingNo) {
        Map<String,Object> map = new HashMap<>();
        map.put("orderSn",orderSn);
        map.put("orderState",orderState);
        map.put("submitStatus",submitStatus);
        map.put("failInfo",failInfo);
        map.put("shippingCode",trackingNo);
        ShopCommonExpress express = shopCommonExpressDao.find(44l);
        map.put("shippingExpressCode",Optional.ofNullable(express.getECode()).orElse(""));
        map.put("shippingExpressId",Optional.ofNullable(express.getId()).orElse(-1L));
        map.put("shippingName",Optional.ofNullable(express.getEName()).orElse(""));
        map.put("shippingTime",new Date());
        orderDao.updateOrderStatus(map);
        //根据订单编号查询是否有零售利润  如果有，设置预期发放时间
        RetailProfit retailProfit = retailProfitService.find("orderSn",orderSn);
        if(retailProfit!=null){
            if(retailProfit.getExpectTime()==null){
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.DATE, 10);
                retailProfit.setExpectTime(calendar.getTime());
                String periodCode = rdSysPeriodDao.getSysPeriodService(retailProfit.getExpectTime());
                if(periodCode!=null){
                    retailProfit.setExpectPeriod(periodCode);
                }
                retailProfitService.update(retailProfit);
            }
        }
    }
    public void returnCoupon(Coupon coupon,CouponDetail couponDetail,String opName){
        CouponPayDetail couponPayDetail = couponPayDetailService.find(couponDetail.getBuyOrderId());
        //判断是否积分支付
        String paymentCode = "";
        if (couponPayDetail.getPaymentId()==6){
            paymentCode = "pointsPaymentPlugin";
        }else{
            //不是积分支付
            TSystemPluginConfig pluginConfig = tSystemPluginConfigService.find(couponPayDetail.getPaymentId());
            if (pluginConfig!=null){
                paymentCode = pluginConfig.getPluginId();
            }
        }
        if ("".equals(paymentCode)){
            return;
        }
        if (paymentCode.equals("alipayMobilePaymentPlugin")) {//支付宝退款
            String bathno = DateUtils.getDateStr(new Date(), "yyyyMMddHHmmssSSS") + NumberUtils.getRandomNumber();

            AliPayRefund aliPayRefund = new AliPayRefund();
            //支付宝交易号 ，退款金额，退款理由
            aliPayRefund.setRefundAmountNum(1);//退款数量，目前是单笔退款
            aliPayRefund.setBatchNo(bathno);
            aliPayRefund.setTradeNo(couponPayDetail.getPaySn());
            aliPayRefund.setRefundAmount(coupon.getCouponPrice());
            //aliPayRefund.setRefundAmount(new BigDecimal(0.01));
            aliPayRefund.setRRefundReason("单张优惠券退款");
            aliPayRefund.setDetaildata(couponPayDetail.getTradeSn(),coupon.getCouponPrice(),"单张优惠券退款");

            //跳到支付宝退款接口
            String sHtmlText = alipayRefundService.toRefund(aliPayRefund);//构造提交支付宝的表单
            if ("true".equals(sHtmlText)) {
                //保存批次号和修改订单数据
                updateCoupon(couponDetail,bathno,coupon,couponPayDetail);
            }
        } else if (paymentCode.equals("weixinMobilePaymentPlugin")) {//微信开放平台支付

            String bathno = DateUtils.getDateStr(new Date(), "yyyyMMddHHmmssSSS") + NumberUtils.getRandomNumber();

            WeiRefund weiRefund = new WeiRefund();
            weiRefund.setOutrefundno(bathno);//微信交易号
            weiRefund.setOuttradeno(couponPayDetail.getPaySn());//订单号
            weiRefund.setTotalfee((int) ((couponPayDetail.getOrderAmount().doubleValue()) * 100));//单位，整数微信里以分为单位
            weiRefund.setRefundfee((int) ((coupon.getCouponPrice().doubleValue()) * 100));
            //weiRefund.setRefundfee(1);
            //weiRefund.setTotalfee(1);
            //跳到微信退款接口
            //toweichatrefund();
            Map<String, Object> map = wechatMobileRefundService.toRefund(weiRefund);
            String msg = "";
            if (map.size() != 0 && map.get("result_code").equals("SUCCESS")) {
                //保存批次号和修改订单数据
                updateCoupon(couponDetail,bathno,coupon,couponPayDetail);
            }
        } else if (paymentCode.equals("weixinH5PaymentPlugin")) {//微信公共平台支付
            String bathno = DateUtils.getDateStr(new Date(), "yyyyMMddHHmmssSSS") + NumberUtils.getRandomNumber();

            WeiRefund weiRefund = new WeiRefund();
            weiRefund.setOutrefundno(bathno);//微信交易号
            weiRefund.setOuttradeno(couponPayDetail.getPaySn());//订单号
            weiRefund.setTotalfee((int) ((couponPayDetail.getOrderAmount().doubleValue()) * 100));//单位，整数微信里以分为单位
            weiRefund.setRefundfee((int) ((coupon.getCouponPrice().doubleValue()) * 100));
//              weiRefund.setRefundfee((int) (0.01 * 100));
            //跳到微信退款接口
            //backurl = toweichatrefund(weiRefund, id, adminMessage, "mp_weichatpay", model, request);
            Map<String, Object> map = wechatRefundService.toRefund(weiRefund);
            String msg = "";
            if (map.size() != 0 && map.get("result_code").equals("SUCCESS")) {
                //保存批次号和修改订单数据
                updateCoupon(couponDetail,bathno,coupon,couponPayDetail);
            }
        }else if (paymentCode.equals("pointsPaymentPlugin")) {

            String bathno = DateUtils.getDateStr(new Date(), "yyyyMMddHHmmssSSS") + NumberUtils.getRandomNumber();
            //把积分退还给用户
            String mCode = couponDetail.getReceiveId();
            RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", couponDetail.getReceiveId());
            if (rdMmAccountInfo!=null){
                //更新用户购物积分
                RdMmAccountLog rdMmAccountLog = new RdMmAccountLog();
                rdMmAccountLog.setTransTypeCode("OT");
                rdMmAccountLog.setAccType("");
                rdMmAccountLog.setTrSourceType("SWB");
                rdMmAccountLog.setMmCode(couponDetail.getReceiveId());
                rdMmAccountLog.setMmNickName(couponDetail.getReceiveNickName());
                rdMmAccountLog.setTrMmCode(couponDetail.getReceiveId());
                rdMmAccountLog.setBlanceBefore(rdMmAccountInfo.getWalletBlance());
                //单张所需积分
                BigDecimal pricePoint = couponPayDetail.getUsePointNum().divide(new BigDecimal(couponPayDetail.getCouponNumber()),0,BigDecimal.ROUND_HALF_UP);
                rdMmAccountLog.setAmount(pricePoint);
                rdMmAccountLog.setTransDate(new Date());
                String period = rdSysPeriodService.getSysPeriodService(new Date());
                rdMmAccountLog.setTransPeriod(period);
                rdMmAccountLog.setTrOrderOid(couponDetail.getBuyOrderId());
                //无需审核直接成功
                rdMmAccountLog.setStatus(3);
                rdMmAccountLog.setCreationBy(opName);
                rdMmAccountLog.setCreationTime(new Date());
                rdMmAccountLog.setAutohrizeBy(opName);
                rdMmAccountLog.setAutohrizeTime(new Date());
                rdMmAccountInfo.setWalletBlance(rdMmAccountInfo.getWalletBlance().add(pricePoint));
                rdMmAccountLog.setBlanceAfter(rdMmAccountInfo.getWalletBlance());
                rdMmAccountInfoService.update(rdMmAccountInfo);
                rdMmAccountLogService.save(rdMmAccountLog);

                //保存批次号和修改订单数据
                //updateCoupon(couponDetailId,bathno,coupon,couponPayDetail);
                CouponDetail couponDetail1 = new CouponDetail();
                couponDetail1.setId(couponDetail.getId()); //记录ID
                couponDetail1.setRefundState(2);//0：无需退款（非交易性优惠券）1：未退款 2：已退款
                couponDetail1.setRefundSum(pricePoint);
                couponDetail1.setBatchNo(bathno); //退款批次号
                couponDetail1.setRefundTime(new Date());
                couponDetailService.update(couponDetail1);//将批次号存入优惠券表

                if (couponPayDetail.getRefundCouponNum()+1==couponPayDetail.getCouponNumber()){
                    couponPayDetail.setRefundState(2);
                }else{
                    couponPayDetail.setRefundState(1);
                }
                couponPayDetail.setRefundCouponNum(couponPayDetail.getRefundCouponNum()+1);
                couponPayDetail.setBatchNo(bathno);
                couponPayDetail.setRefundTime(new Date());
                couponPayDetail.setRefundAmount(couponPayDetail.getRefundAmount().add(pricePoint));
                couponPayDetailService.update(couponPayDetail);

                //改rd_coupon_user
                List<CouponUser> couponUsers = couponUserService.findByMMCodeAndCouponId(couponDetail.getHoldId(), couponDetail.getCouponId());
                CouponUser couponUser = couponUsers.get(0);
                couponUser.setOwnNum(couponUser.getOwnNum()-1);
                couponUserService.update(couponUser);
            }
         }
    }

    public void updateCoupon(CouponDetail couponDetail, String bathno, Coupon coupon, CouponPayDetail couponPayDetail) {
            couponDetail.setRefundState(2);//0：无需退款（非交易性优惠券）1：未退款 2：已退款
            couponDetail.setRefundSum(coupon.getCouponPrice());
            couponDetail.setBatchNo(bathno); //退款批次号
            couponDetail.setRefundTime(new Date());
            couponDetailService.update(couponDetail);//将批次号存入优惠券表

            if (couponPayDetail.getRefundCouponNum()+1==couponPayDetail.getCouponNumber()){
                couponPayDetail.setRefundState(2);
            }else{
                couponPayDetail.setRefundState(1);
            }
            couponPayDetail.setRefundCouponNum(couponPayDetail.getRefundCouponNum()+1);
            couponPayDetail.setBatchNo(bathno);
            couponPayDetail.setRefundTime(new Date());
            couponPayDetail.setRefundAmount(couponPayDetail.getRefundAmount().add(coupon.getCouponPrice()));
            couponPayDetailService.update(couponPayDetail);

            //改rd_coupon_user
            List<CouponUser> couponUsers = couponUserService.findByMMCodeAndCouponId(couponDetail.getHoldId(), couponDetail.getCouponId());
            CouponUser couponUser = couponUsers.get(0);
            couponUser.setOwnNum(couponUser.getOwnNum()-1);
            couponUserService.update(couponUser);
        }
}
