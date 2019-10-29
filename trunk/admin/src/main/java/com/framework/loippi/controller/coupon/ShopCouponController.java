package com.framework.loippi.controller.coupon;

import com.framework.loippi.consts.Constants;
import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.Principal;
import com.framework.loippi.entity.coupon.Coupon;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.coupon.CouponService;
import com.framework.loippi.support.Message;
import com.framework.loippi.utils.StringUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Optional;

@Controller("shopCouponController")
@RequestMapping("/admin/plarformShopCoupon")
public class ShopCouponController extends GenericController {

    @Resource
    private CouponService couponService;
    @Resource
    private TwiterIdService twiterIdService;

/*    @RequestMapping(value = "/checkCouponName")
    public Message checkCouponName(HttpServletRequest request, String couponName) {
        if(StringUtil.isEmpty(couponName)){
            return Message.error("优惠券名称不可以为空");
        }
        Coupon coupon = couponService.find("couponName", couponName);
        if(coupon!=null){
            return Message.error("优惠券名称已存在，请确认一个新的优惠券名称");
        }
        return Message.success("优惠券名称可使用");
    }*/

    /**
     * 优惠券创建
     * @param request
     * @param coupon 优惠券对象
     * @param model
     * @param attr
     * @return
     */
    @RequestMapping(value = "/saveCoupon")
    public String saveCoupon(HttpServletRequest request, @ModelAttribute Coupon coupon, ModelMap model, RedirectAttributes attr) {
        if(StringUtil.isEmpty(coupon.getCouponName())){
            model.addAttribute("msg", "优惠券名称不可以为空");
            return Constants.MSG_URL;
        }
        if(coupon.getCouponValue()==null){
            model.addAttribute("msg", "优惠券面值不可以为空");
            return Constants.MSG_URL;
        }
        if(coupon.getReduceType()==null){
            model.addAttribute("msg", "优惠券优惠类型不可以为空");
            return Constants.MSG_URL;
        }
        if(coupon.getUseScope()==null){
            model.addAttribute("msg", "优惠券使用范围不可以为空");
            return Constants.MSG_URL;
        }
        if(coupon.getReceiveType()==null){
            model.addAttribute("msg", "优惠券获取方式不可以为空");
            return Constants.MSG_URL;
        }
        if(coupon.getPersonLimitNum()==null){
            model.addAttribute("msg", "优惠券会员领取数量限制不可以为空");
            return Constants.MSG_URL;
        }
        if(coupon.getTotalLimitNum()==null){
            model.addAttribute("msg", "优惠券总发行数量不可以为空");
            return Constants.MSG_URL;
        }
        initCoupon(coupon);
        Subject subject = SecurityUtils.getSubject();
        if(subject!=null){
            Principal principal = (Principal) subject.getPrincipal();
            if (principal != null && principal.getId() != null) {
                coupon.setCreateId(principal.getId());
                coupon.setCreateName(principal.getUsername());
                coupon.setCreateTime(new Date());
            }
        }
        System.out.println(coupon);
        Long flag = couponService.save(coupon);
        if(flag!=1){
            model.addAttribute("msg", "添加优惠券失败");
            return Constants.MSG_URL;
        }
        return null;
    }
    private void initCoupon(Coupon coupon){
        Long id = twiterIdService.getTwiterId();
        coupon.setId(id);
        /*coupon.setStoreId(Optional.ofNullable(coupon.getStoreId()).orElse(0L));//设置店家id，如果未设置，默认自营商户：0
        coupon.setStoreName(Optional.ofNullable(coupon.getStoreName()).orElse("自营商店"));//设置卖家店铺名称，默认自营商店
        coupon.setBrandId(Optional.ofNullable(coupon.getBrandId()).orElse(6544150183754600448L));//设置品牌id，如果未设置，默认自营品牌：0
        coupon.setBrandName(Optional.ofNullable(coupon.getBrandName()).orElse("OLOMI"));*/
        coupon.setReceivedNum(0L);//设置已发放优惠券数量为0
        coupon.setStatus(1);//设置带审核状态
    }
}
