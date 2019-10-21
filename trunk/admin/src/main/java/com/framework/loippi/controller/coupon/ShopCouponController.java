package com.framework.loippi.controller.coupon;

import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.coupon.Coupon;
import com.framework.loippi.service.coupon.CouponService;
import com.framework.loippi.support.Message;
import com.framework.loippi.utils.StringUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Controller("shopCouponController")
@RequestMapping("/admin/plarformShopCoupon")
public class ShopCouponController extends GenericController {

    @Resource
    private CouponService couponService;

    @RequestMapping(value = "/checkCouponName")
    public Message checkCouponName(HttpServletRequest request, String couponName) {
        if(StringUtil.isEmpty(couponName)){
            return Message.error("优惠券名称不可以为空");
        }
        Coupon coupon = couponService.find("couponName", couponName);
        if(coupon!=null){
            return Message.error("优惠券名称已存在，请确认一个新的优惠券名称");
        }
        return Message.success("优惠券名称可使用");
    }

    @RequestMapping(value = "/saveCoupon")
    public
    @ResponseBody
    Message saveCoupon(HttpServletRequest request, Coupon coupon) {
        if(StringUtil.isEmpty(coupon.getCouponName())){
            return Message.error("优惠券名称不可以为空");
        }
        if(coupon.getCouponValue()==null){
            return Message.error("优惠券面值不可以为空");
        }
        if(coupon.getReduceType()==null){
            return Message.error("优惠券优惠类型不可以为空");
        }
        if(coupon.getUseScope()==null){
            return Message.error("优惠券使用范围不可以为空");
        }
        if(coupon.getReceiveType()==null){
            return Message.error("优惠券获取方式不可以为空");
        }
        if(coupon.getPersonLimitNum()==null){
            return Message.error("优惠券会员领取数量限制不可以为空");
        }
        if(coupon.getTotalLimitNum()==null){
            return Message.error("优惠券总发行数量不可以为空");
        }
        return null;
    }
}
