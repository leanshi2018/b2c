package com.framework.loippi.controller.coupon;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.framework.loippi.entity.coupon.CouponTransLog;
import com.framework.loippi.service.coupon.CouponTransLogService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.framework.loippi.consts.Constants;
import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.Principal;
import com.framework.loippi.entity.coupon.Coupon;
import com.framework.loippi.entity.coupon.CouponPayDetail;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.result.common.coupon.CouponUserLogResult;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.coupon.CouponDetailService;
import com.framework.loippi.service.coupon.CouponPayDetailService;
import com.framework.loippi.service.coupon.CouponService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.StringUtil;

@Controller("shopCouponController")
@RequestMapping("/admin/plarformShopCoupon")
public class ShopCouponController extends GenericController {

    @Resource
    private CouponService couponService;
    @Resource
    private CouponPayDetailService couponPayDetailService;
    @Resource
    private CouponDetailService couponDetailService;
    @Resource
    private CouponTransLogService couponTransLogService;
    @Resource
    private TwiterIdService twiterIdService;

    /**
     * 优惠券列表
     * @param model
     * @param pageNo
     * @param pageSize
     * @param coupon
     * @param sendTimeStr
     * @param useTimeStr
     * @return
     */
    @RequestMapping(value = "/queryCouponConditions",method = RequestMethod.POST)
    public String queryCouponConditions(ModelMap model,
                       @RequestParam(required = false, value = "pageNo", defaultValue = "1") int pageNo,
                       @RequestParam(required = false, value = "pageSize", defaultValue = "20") int pageSize,
                       @ModelAttribute Coupon coupon,
                       @RequestParam(required = false, value = "sendTimeStr") String sendTimeStr,
                       @RequestParam(required = false, value = "useTimeStr") String useTimeStr) {
        Pageable pager = new Pageable();
        pager.setPageSize(pageSize);
        pager.setPageNumber(pageNo);
        pager.setOrderProperty("create_time");
        pager.setOrderDirection(Order.Direction.DESC);
        if (!StringUtil.isEmpty(sendTimeStr)) {
            coupon.setSearchSendTime(sendTimeStr);
        }
        if (!StringUtil.isEmpty(useTimeStr)) {
            coupon.setSearchUseTime(useTimeStr);
        }
        pager.setParameter(coupon);
        Page<Coupon> couponList = couponService.findByPage(pager);
        model.addAttribute("page", couponList);
        return "";//TODO 优惠券展示freemaker页面
    }

    /**
     * 优惠券创建
     * @param request
     * @param coupon 优惠券对象
     * @param model
     * @param attr
     * @return
     */
    @RequestMapping(value = "/saveCoupon",method = RequestMethod.POST)
    public String saveCoupon(HttpServletRequest request, @ModelAttribute Coupon coupon, ModelMap model, RedirectAttributes attr,
                             @RequestParam(required = false, value = "couponId") Long couponId
    ) {
        if(StringUtil.isEmpty(coupon.getCouponName())){
            model.addAttribute("msg", "优惠券名称不可以为空");
            return Constants.MSG_URL;
        }
        if(coupon.getCouponValue()==null){
            model.addAttribute("msg", "优惠券面值不可以为空");
            return Constants.MSG_URL;
        }
        if(coupon.getWhetherPresent()==null){
            model.addAttribute("msg", "优惠券需注明是否可以赠送");
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
        coupon.setId(couponId);
        Subject subject = SecurityUtils.getSubject();
        if(subject!=null){
            Principal principal = (Principal) subject.getPrincipal();
            if (principal != null && principal.getId() != null) {
                Long id = principal.getId();
                String username = principal.getUsername();
                Map<String, String> map =couponService.saveOrEditCoupon(coupon,id,username);
                if (map == null || StringUtil.isEmpty(map.get("code"))) {
                    model.addAttribute("msg", "保存活动信息失败！");
                    return Constants.MSG_URL;
                }

                String code = map.get("code");
                if (StringUtil.isEmpty(code) || code.equals("0")) {
                    String errorMsg = map.get("msg");
                    model.addAttribute("msg", errorMsg);
                    return Constants.MSG_URL;
                }
                //model.addAttribute("msg", "成功");
                return "redirect:coupon/list.jhtml";
            }
        }
        model.addAttribute("msg", "请登录后再进行优惠券相关操作");
        return Constants.MSG_URL;
    }

    /**
     * 优惠券信息查询
     * @param request
     * @param model
     * @param attr
     * @param couponId 优惠券id
     * @return
     */
    @RequestMapping(value = "/viewCouponContent",method = RequestMethod.POST)
    public String viewCouponContent(HttpServletRequest request,  ModelMap model, RedirectAttributes attr,
                             @RequestParam(required = true, value = "couponId") Long couponId
    ) {
        if(couponId==null){
            model.addAttribute("msg", "请传入查询当前优惠券id");
            return Constants.MSG_URL;
        }
        Coupon coupon = couponService.find(couponId);
        if(coupon==null){
            model.addAttribute("msg", "当前id对应优惠券不存在");
            return Constants.MSG_URL;
        }
        model.addAttribute("coupon",coupon);
        return "activity/shop_activity/coupon_edit";//TODO 跳转到展示优惠券详情页面
    }

    /**
     * 根据优惠券id对优惠券进行审核
     * @param request
     * @param model
     * @param attr
     * @param couponId 优惠券id
     * @return
     */
    @RequestMapping(value = "/auditCoupon",method = RequestMethod.POST)
    public String auditCoupon(HttpServletRequest request,  ModelMap model, RedirectAttributes attr,
                                    @RequestParam(required = true, value = "couponId") Long couponId,
                                    @RequestParam(required = true, value = "targetStatus") Integer targetStatus
    ) {
        if(couponId==null){
            model.addAttribute("msg", "请传入查询当前优惠券id");
            return Constants.MSG_URL;
        }
        if(targetStatus!=2&&targetStatus!=3){
            model.addAttribute("msg", "是否审核通过状态码不正确");
            return Constants.MSG_URL;
        }
        Coupon coupon = couponService.find(couponId);
        if(coupon==null){
            model.addAttribute("msg", "当前id对应优惠券不存在");
            return Constants.MSG_URL;
        }
        Integer status = coupon.getStatus();
        if(status!=1){
            model.addAttribute("msg", "当前优惠券不处于待审核状态");
            return Constants.MSG_URL;
        }
        Principal user = (Principal) SecurityUtils.getSubject().getPrincipal();
        if(user==null){
            model.addAttribute("msg", "请登录后进行审核操作");
            return Constants.MSG_URL;
        }
        try {
            couponService.updateCouponState(coupon,targetStatus,user.getId(),user.getUsername());
            if(targetStatus==2){
                model.addAttribute("msg", "审核成功");
            }
            if(targetStatus==3){
                model.addAttribute("msg", "审核失败");
            }
            return "redirect:coupon/list.jhtml";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("msg", "网络异常，请稍后重试");
            return Constants.MSG_URL;
        }
    }

    /**
     * 优惠券订单列表
     * @param request
     * @param pageable
     * @param model
     * @param
     * @return
     */
    @RequestMapping(value = "/Coupon/findCouponPayDetailList")
    public String findCouponPayDetailList(HttpServletRequest request,Pageable pageable,ModelMap model,@ModelAttribute CouponPayDetail param) {
        pageable.setParameter(param);
        pageable.setOrderProperty("create_time");
        pageable.setOrderDirection(Order.Direction.DESC);
        model.addAttribute("page", couponPayDetailService.findByPage(pageable));
        return "/activity/shop_activity/couponbuy_list";
    }

    /**
     * 优惠券领取使用明细
     * @param request
     * @param pageable
     * @param model
     * @param param
     * @return
     */
    @RequestMapping(value = "/Coupon/findCouponUserLogList")
    public String findCouponUseLogList(HttpServletRequest request,Pageable pageable,ModelMap model,@ModelAttribute CouponUserLogResult param) {
        pageable.setParameter(param);
        pageable.setOrderProperty("receive_time");
        pageable.setOrderDirection(Order.Direction.DESC);
        model.addAttribute("page", couponDetailService.findLogResultByPage(pageable));
        return "/activity/shop_activity/couponuse_list";
    }

    /**
     * 优惠券基本信息列表
     *
     */
    @RequestMapping("/coupon/list")
    public String list(ModelMap model,
                       @RequestParam(required = false, value = "pageNo", defaultValue = "1") int pageNo,
                       @RequestParam(required = false, value = "pageSize", defaultValue = "20") int pageSize,
                       @ModelAttribute Coupon coupon) {
        //参数整理
        Pageable pager = new Pageable();
        pager.setPageNumber(pageNo);
        pager.setPageSize(pageSize);
        pager.setOrderProperty("create_time");
        pager.setOrderDirection(Order.Direction.DESC);
        pager.setParameter(coupon);
        Page<Coupon> page = couponService.findByPage(pager);
        model.addAttribute("couponList", page);
        return "/activity/shop_activity/coupon_list";
    }

    /**
     * 优惠券转账日志查询
     *
     */
    @RequestMapping("/coupon/translog")
    public String translogSearch(ModelMap model,
                       @RequestParam(required = false, value = "pageNo", defaultValue = "1") int pageNo,
                       @RequestParam(required = false, value = "pageSize", defaultValue = "20") int pageSize,
                       @ModelAttribute CouponTransLog couponTransLog) {
        //参数整理
        Pageable pager = new Pageable();
        pager.setPageNumber(pageNo);
        pager.setPageSize(pageSize);
        pager.setOrderProperty("trans_time");
        pager.setOrderDirection(Order.Direction.DESC);
        pager.setParameter(couponTransLog);
        Page<CouponTransLog> page = couponTransLogService.findByPage(pager);
        model.addAttribute("couponTransLogList", page);
        return "";//TODO
    }

    /**
     * 前往新增优惠券页面
     * @param model
     * @param request
     * @return
     */
    @RequestMapping("/forward/edit")
    public String edit(Model model, HttpServletRequest request) {

        return "activity/shop_activity/coupon_edit";
    }
}
