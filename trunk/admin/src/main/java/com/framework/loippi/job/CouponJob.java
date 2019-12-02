package com.framework.loippi.job;


import com.framework.loippi.dao.coupon.CouponDao;
import com.framework.loippi.dao.coupon.CouponDetailDao;
import com.framework.loippi.dao.coupon.CouponPayDetailDao;
import com.framework.loippi.dao.coupon.CouponUserDao;
import com.framework.loippi.entity.AliPayRefund;
import com.framework.loippi.entity.TSystemPluginConfig;
import com.framework.loippi.entity.WeiRefund;
import com.framework.loippi.entity.coupon.Coupon;
import com.framework.loippi.entity.coupon.CouponDetail;
import com.framework.loippi.entity.coupon.CouponPayDetail;
import com.framework.loippi.entity.coupon.CouponUser;
import com.framework.loippi.entity.user.RdMmAccountInfo;
import com.framework.loippi.entity.user.RdMmAccountLog;
import com.framework.loippi.service.TSystemPluginConfigService;
import com.framework.loippi.service.alipay.AlipayRefundService;
import com.framework.loippi.service.coupon.CouponDetailService;
import com.framework.loippi.service.coupon.CouponPayDetailService;
import com.framework.loippi.service.user.RdMmAccountInfoService;
import com.framework.loippi.service.user.RdMmAccountLogService;
import com.framework.loippi.service.user.RdSysPeriodService;
import com.framework.loippi.service.wechat.WechatMobileRefundService;
import com.framework.loippi.service.wechat.WechatRefundService;
import com.framework.loippi.utils.NumberUtils;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.validator.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 优惠券定时任务
 */
@Service
@EnableScheduling
@Lazy(false)
public class CouponJob {
    @Resource
    private TSystemPluginConfigService tSystemPluginConfigService;
    @Resource
    private CouponDao couponDao;
    @Resource
    private CouponUserDao couponUserDao;
    @Resource
    private CouponDetailDao couponDetailDao;
    @Resource
    private CouponPayDetailDao couponPayDetailDao;
    @Resource
    private AlipayRefundService alipayRefundService;
    @Resource
    private CouponDetailService couponDetailService;
    @Resource
    private CouponPayDetailService couponPayDetailService;
    @Resource
    private WechatMobileRefundService wechatMobileRefundService;
    @Resource
    private WechatRefundService wechatRefundService;
    @Resource
    private RdMmAccountInfoService rdMmAccountInfoService;
    @Resource
    private RdSysPeriodService rdSysPeriodService;
    @Resource
    private RdMmAccountLogService rdMmAccountLogService;
    private static final Logger log = LoggerFactory.getLogger(ShopOrderJob.class);

    @Scheduled(cron = "0 5 0 * * ? ")  //每天0点五分运行
    //@Scheduled(cron = "0/30 * * * * ? " )  //每隔30秒执行一次
    public void recycleCoupon() {
        log.info("#################################################################");
        log.info("#########开始执行，查询过期且并未回收优惠券，进行回收处理 ##########");
        log.info("#################################################################");
        //查询出当前时间下，已经过期但是并未进行回收处理的优惠券
        Date date = new Date();
        System.out.println(date);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = format.format(date);
        System.out.println(time);
        List<Coupon> coupons = couponDao.findOverdueCoupon(Paramap.create().put("status",2).put("searchUseTime",date));
        System.out.println(coupons);
        System.out.println(coupons.size());
        if(coupons!=null&&coupons.size()>0){
            for (Coupon coupon : coupons) {//获得过期优惠券集合，进行遍历回收处理
                //1.判断优惠券是否使用积分或者金钱购买，如果为购买的优惠券，则需要进行退款操作
                if(coupon.getUseMoneyFlag()==0){//免费
                    //1.1免费领取优惠券只需对所有优惠券进行回收，无需退款
                    //1.1.1修改coupon
                    coupon.setStatus(4);
                    coupon.setUpdateName("系统定时任务过期");
                    coupon.setUpdateTime(new Date());
                    couponDao.update(coupon);
                    //1.1.2清空couponUser表
                    couponUserDao.overdueCouponUserByCouponId(coupon.getId());
                    //1.1.3回收couponDetail
                    couponDetailDao.recycleNoMoney(Paramap.create().put("refundState",0).put("refundSum", BigDecimal.ZERO).put("couponId",coupon.getId()));
                }else if(coupon.getUseMoneyFlag()==1){//付费
                    CouponDetail couponDetail1 = new CouponDetail();
                    couponDetail1.setCouponId(coupon.getId());
                    couponDetail1.setUseState(2);
                    Long num=couponDetailDao.getNoUseNum(couponDetail1);
                    coupon.setRefundNum((int) (Optional.ofNullable(coupon.getRefundNum()).orElse(0)+num));
                    //1.2对未使用的优惠券进行退款 退款路径原路返回
                    //1.2.1修改coupon
                    coupon.setStatus(4);
                    coupon.setUpdateName("系统定时任务过期");
                    coupon.setUpdateTime(new Date());
                    couponDao.update(coupon);
                    //1.2.2查询出当前优惠券对应的优惠券订单
                    List<CouponPayDetail> details = couponPayDetailDao.findByParams(Paramap.create().put("couponId",coupon.getId()).put("paymentState",1)
                    .put("couponOrderState",40));
                    if(details!=null&&details.size()>0){//当前过期优惠券有已完成订单
                        for (CouponPayDetail couponPayDetail : details) {
                            //根据购买编号，查询尚未使用的优惠券明细，统计张数，购买人退款，持有人couponUser拥有数量修改
                            List<CouponDetail> couponDetails = couponDetailDao.findByParams(Paramap.create().put("couponId",coupon.getId())
                            .put("useState",2).put("buyOrderId",couponPayDetail.getId()));
                            if(couponDetails!=null&&couponDetails.size()>0){//当前订单有未使用的优惠券
                                //1.2.2.1 统计数量，将金额退还给领取人
                                int couponNum = couponDetails.size();
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
                                if (paymentCode.equals("alipayMobilePaymentPlugin")) {//支付宝退款
                                    String bathno = DateUtils.getDateStr(new Date(), "yyyyMMddHHmmssSSS") + NumberUtils.getRandomNumber();

                                    AliPayRefund aliPayRefund = new AliPayRefund();
                                    //支付宝交易号 ，退款金额，退款理由
                                    aliPayRefund.setRefundAmountNum(couponNum);//退款数量，目前是单笔退款
                                    aliPayRefund.setBatchNo(bathno);
                                    aliPayRefund.setTradeNo(couponPayDetail.getPaySn());
                                    aliPayRefund.setRefundAmount(coupon.getCouponPrice().multiply(new BigDecimal(couponNum)));
                                    //aliPayRefund.setRefundAmount(new BigDecimal(0.01));
                                    aliPayRefund.setRRefundReason("优惠券订单退款");
                                    aliPayRefund.setDetaildata(couponPayDetail.getTradeSn(),coupon.getCouponPrice(),"优惠券订单退款");
                                    //跳到支付宝退款接口
                                    String sHtmlText = alipayRefundService.toRefund(aliPayRefund);//构造提交支付宝的表单
                                    if ("true".equals(sHtmlText)) {
                                        //保存批次号和修改订单数据
                                        updateCouponDetailList(couponPayDetail,bathno,coupon,couponDetails,couponNum);
                                    }
                                }
                                else if (paymentCode.equals("weixinMobilePaymentPlugin")) {//微信开放平台支付

                                    String bathno = DateUtils.getDateStr(new Date(), "yyyyMMddHHmmssSSS") + NumberUtils.getRandomNumber();

                                    WeiRefund weiRefund = new WeiRefund();
                                    weiRefund.setOutrefundno(bathno);//微信交易号
                                    weiRefund.setOuttradeno(couponPayDetail.getPaySn());//订单号
                                    weiRefund.setTotalfee((int) ((couponPayDetail.getOrderAmount().doubleValue()) * 100));//单位，整数微信里以分为单位
                                    weiRefund.setRefundfee((int) ((coupon.getCouponPrice().doubleValue())* couponNum * 100));
                                    //weiRefund.setRefundfee(1);
                                    //weiRefund.setTotalfee(1);
                                    //跳到微信退款接口
                                    //toweichatrefund();
                                    Map<String, Object> map = wechatMobileRefundService.toRefund(weiRefund);
                                    String msg = "";
                                    if (map.size() != 0 && map.get("result_code").equals("SUCCESS")) {
                                        //保存批次号和修改订单数据
                                        updateCouponDetailList(couponPayDetail,bathno,coupon,couponDetails,couponNum);
                                    }
                                }
                                else if (paymentCode.equals("weixinH5PaymentPlugin")) {//微信公共平台支付
                                    String bathno = DateUtils.getDateStr(new Date(), "yyyyMMddHHmmssSSS") + NumberUtils.getRandomNumber();

                                    WeiRefund weiRefund = new WeiRefund();
                                    weiRefund.setOutrefundno(bathno);//微信交易号
                                    weiRefund.setOuttradeno(couponPayDetail.getPaySn());//订单号
                                    weiRefund.setTotalfee((int) ((couponPayDetail.getOrderAmount().doubleValue()) * 100));//单位，整数微信里以分为单位
                                    weiRefund.setRefundfee((int) ((coupon.getCouponPrice().doubleValue())* couponNum * 100));
                                    //              weiRefund.setRefundfee((int) (0.01 * 100));
                                    //跳到微信退款接口
                                    //backurl = toweichatrefund(weiRefund, id, adminMessage, "mp_weichatpay", model, request);

                                    Map<String, Object> map = wechatRefundService.toRefund(weiRefund);
                                    String msg = "";
                                    if (map.size() != 0 && map.get("result_code").equals("SUCCESS")) {
                                        //保存批次号和修改订单数据
                                        updateCouponDetailList(couponPayDetail,bathno,coupon,couponDetails,couponNum);
                                    }
                                }
                                else if (paymentCode.equals("pointsPaymentPlugin")) {

                                    String bathno = DateUtils.getDateStr(new Date(), "yyyyMMddHHmmssSSS") + NumberUtils.getRandomNumber();
                                    //把积分退还给用户
                                    String mCode = couponPayDetail.getReceiveId();
                                    RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", couponPayDetail.getReceiveId());
                                    if (rdMmAccountInfo!=null){
                                        //更新用户购物积分
                                        RdMmAccountLog rdMmAccountLog = new RdMmAccountLog();
                                        rdMmAccountLog.setTransTypeCode("OT");
                                        rdMmAccountLog.setAccType("");
                                        rdMmAccountLog.setTrSourceType("SWB");
                                        rdMmAccountLog.setMmCode(couponPayDetail.getReceiveId());
                                        rdMmAccountLog.setMmNickName(couponPayDetail.getReceiveNickName());
                                        rdMmAccountLog.setTrMmCode(couponPayDetail.getReceiveId());
                                        rdMmAccountLog.setBlanceBefore(rdMmAccountInfo.getWalletBlance());
                                        //所需积分
                                        BigDecimal pricePoint = (couponPayDetail.getUsePointNum().divide(new BigDecimal(couponPayDetail.getCouponNumber()),0,BigDecimal.ROUND_HALF_UP)).multiply(new BigDecimal(couponNum));
                                        rdMmAccountLog.setAmount(pricePoint);
                                        rdMmAccountLog.setTransDate(new Date());
                                        String period = rdSysPeriodService.getSysPeriodService(new Date());
                                        rdMmAccountLog.setTransPeriod(period);
                                        rdMmAccountLog.setTrOrderOid(couponPayDetail.getId());
                                        //无需审核直接成功
                                        rdMmAccountLog.setStatus(3);
                                        //rdMmAccountLog.setCreationBy(adminName);
                                        rdMmAccountLog.setCreationTime(new Date());
                                        //rdMmAccountLog.setAutohrizeBy(adminName);
                                        rdMmAccountLog.setAutohrizeTime(new Date());
                                        rdMmAccountInfo.setWalletBlance(rdMmAccountInfo.getWalletBlance().add(pricePoint));
                                        rdMmAccountLog.setBlanceAfter(rdMmAccountInfo.getWalletBlance());
                                        rdMmAccountInfoService.update(rdMmAccountInfo);
                                        rdMmAccountLogService.save(rdMmAccountLog);

                                        //保存批次号和修改订单数据
                                        //updateCouponDetailList(couponDetailId,bathno,coupon,couponPayDetail);
                                        for (CouponDetail couponDetail : couponDetails) {
                                            if (couponDetail.getUseState()==2 && couponDetail.getRefundState()==1){
                                                //未使用且未退款
                                                couponDetail.setUseState(3);
                                                couponDetail.setRefundState(2);
                                                couponDetail.setRefundSum(couponPayDetail.getUsePointNum().divide(new BigDecimal(couponPayDetail.getCouponNumber()),0,BigDecimal.ROUND_HALF_UP));
                                                couponDetail.setBatchNo(bathno); //退款批次号
                                                couponDetail.setRefundTime(new Date());
                                                couponDetailService.update(couponDetail);//将批次号存入优惠券表
                                                List<CouponUser> couponUsers = couponUserDao.findByMMCodeAndCouponId(Paramap.create().put("mCode", couponDetail.getHoldId()).put("couponId", couponDetail.getCouponId()));
                                                if(couponUsers!=null&&couponUsers.size()>0){
                                                    CouponUser couponUser = couponUsers.get(0);
                                                    couponUser.setOwnNum(couponUser.getOwnNum()-1);
                                                    couponUserDao.update(couponUser);
                                                }
                                            }
                                        }

                                        if ((couponPayDetail.getRefundCouponNum()+couponNum)==couponPayDetail.getCouponNumber()){
                                            couponPayDetail.setRefundState(2);
                                        }else{
                                            couponPayDetail.setRefundState(1);
                                        }
                                        couponPayDetail.setRefundCouponNum(couponPayDetail.getRefundCouponNum()+couponNum);
                                        couponPayDetail.setBatchNo(bathno);
                                        couponPayDetail.setRefundTime(new Date());
                                        couponPayDetail.setRefundAmount(couponPayDetail.getRefundAmount().add(pricePoint));
                                        couponPayDetailService.update(couponPayDetail);
                                    }
                                }
                            }
                        }
                    }
                }else {
                    continue;
                }
            }
        }
        log.info("###############################################过期结束###############################################");
    }
    public void updateCouponDetailList(CouponPayDetail couponPayDetail, String bathno, Coupon coupon, List<CouponDetail> couponDetailList,Integer refundNum) {

        if ((couponPayDetail.getRefundCouponNum()+refundNum)==couponPayDetail.getCouponNumber()){
            couponPayDetail.setRefundState(2);
        }else{
            couponPayDetail.setRefundState(1);
        }
        couponPayDetail.setRefundCouponNum(couponPayDetail.getRefundCouponNum()+refundNum);
        couponPayDetail.setBatchNo(bathno);
        couponPayDetail.setRefundTime(new Date());
        couponPayDetail.setRefundAmount(couponPayDetail.getRefundAmount().add(coupon.getCouponPrice().multiply(new BigDecimal(refundNum))));
        couponPayDetailService.update(couponPayDetail);

        for (CouponDetail couponDetail : couponDetailList) {
            if (couponDetail.getUseState()==2 && couponDetail.getRefundState()==1){
                //未使用且未退款
                couponDetail.setUseState(3);
                couponDetail.setRefundState(2);
                couponDetail.setRefundSum(coupon.getCouponPrice());
                couponDetail.setBatchNo(bathno); //退款批次号
                couponDetail.setRefundTime(new Date());
                couponDetailService.update(couponDetail);//将批次号存入优惠券表
                List<CouponUser> couponUsers = couponUserDao.findByMMCodeAndCouponId(Paramap.create().put("mCode", couponDetail.getHoldId()).put("couponId", couponDetail.getCouponId()));
                if(couponUsers!=null&&couponUsers.size()>0){
                    CouponUser couponUser = couponUsers.get(0);
                    couponUser.setOwnNum(couponUser.getOwnNum()-1);
                    couponUserDao.update(couponUser);
                }
            }
        }
    }
}
