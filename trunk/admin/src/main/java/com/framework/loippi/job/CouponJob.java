package com.framework.loippi.job;


import com.framework.loippi.dao.coupon.CouponDao;
import com.framework.loippi.dao.coupon.CouponDetailDao;
import com.framework.loippi.dao.coupon.CouponPayDetailDao;
import com.framework.loippi.dao.coupon.CouponUserDao;
import com.framework.loippi.entity.coupon.Coupon;
import com.framework.loippi.entity.coupon.CouponDetail;
import com.framework.loippi.entity.coupon.CouponPayDetail;
import com.framework.loippi.utils.Paramap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 定时任务查询过期优惠券，对优惠券进行回收处理
 */
@Service
@EnableScheduling
@Lazy(false)
@Transactional
public class CouponJob {

    @Resource
    private CouponDao couponDao;
    @Resource
    private CouponUserDao couponUserDao;
    @Resource
    private CouponDetailDao couponDetailDao;
    @Resource
    private CouponPayDetailDao couponPayDetailDao;
    private static final Logger log = LoggerFactory.getLogger(ShopOrderJob.class);

    @Scheduled(cron = "0 5 0 * * ? ")  //每天0点五分运行
    public void recycleCoupon() {
        log.info("#################################################################");
        log.info("#########开始执行，查询过期且并未回收优惠券，进行回收处理 ##########");
        log.info("#################################################################");
        //查询出当前时间下，已经过期但是并未进行回收处理的优惠券
        List<Coupon> coupons = couponDao.findOverdueCoupon(Paramap.create().put("status",2).put("searchUseTime",new Date()));
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

                    }
                }else {
                    continue;
                }
            }
        }
    }
}
