package com.framework.loippi.job;

import com.framework.loippi.entity.coupon.Coupon;
import com.framework.loippi.service.coupon.CouponService;
import com.framework.loippi.utils.Paramap;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
@EnableScheduling
@Lazy(false)
public class CouponRecycleJob {
    @Resource
    private CouponService couponService;

    @Scheduled(cron = "0 0 0 10 11 ? 2019")//2019年十一月十日凌晨执行
    public void recycleCoupon() {
        List<Coupon> list = couponService.findOverdueCoupon(Paramap.create().put("status", 2).put("searchUseTime", new Date()));//查询出上架状态且当前时间超过使用时间的优惠券
        if(list!=null&&list.size()>0){
            for (Coupon coupon : list) {//遍历已经过期的优惠券集合，对优惠券进行状态修改（过期处理）以及针对于购买类型优惠券的退款

            }
        }
    }
}
