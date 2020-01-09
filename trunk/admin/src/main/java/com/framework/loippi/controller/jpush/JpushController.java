package com.framework.loippi.controller.jpush;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import cn.jpush.api.schedule.ScheduleResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;
import com.framework.loippi.consts.Constants;
import com.framework.loippi.consts.JpushConstant;
import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.activity.ShopActivity;
import com.framework.loippi.entity.common.ShopCommonArticle;
import com.framework.loippi.entity.coupon.Coupon;
import com.framework.loippi.entity.jpush.Jpush;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.activity.ShopActivityService;
import com.framework.loippi.service.common.ShopCommonArticleService;
import com.framework.loippi.service.coupon.CouponService;
import com.framework.loippi.service.jpush.JpushService;
import com.framework.loippi.service.product.ShopGoodsService;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.StringUtil;

@Controller("JpushController")
@RequestMapping("/admin/jpush")
public class JpushController extends GenericController {
    @Resource
    private TwiterIdService twiterIdService;
    @Resource
    private JpushService jpushService;
    @Resource
    private RdMmBasicInfoService rdMmBasicInfoService;
    @Resource
    private ShopGoodsService shopGoodsService;
    @Resource
    private ShopActivityService shopActivityService;
    @Resource
    private ShopCommonArticleService shopCommonArticleService;
    @Resource
    private CouponService couponService;

    /**
     * 使用极光推送像用户推送消息并在成功后存储推送信息到数据库
     * @param request
     * @param model
     * @param attr
     * @param param
     * @return
     */
    @RequestMapping(value = "/sendJpush",method = RequestMethod.POST)
    public String sendJpush(HttpServletRequest request, ModelMap model, RedirectAttributes attr,
                                    @ModelAttribute Jpush param
    ) {
        if (param.getPlatform()==null){
            model.addAttribute("msg", "请选择具体的推送平台");
            return Constants.MSG_URL;
        }
        if (param.getAudience()==null){
            model.addAttribute("msg", "请选择具体的推送目标");
            return Constants.MSG_URL;
        }
        /*if (param.getNotification()==null){
            model.addAttribute("msg", "通知标题不可以为空");
            return Constants.MSG_URL;
        }*/
        if (param.getMessage()==null){
            model.addAttribute("msg", "通知内容不可以为空");
            return Constants.MSG_URL;
        }
        if(param.getJumpLink()==null&&param.getJumpPath()==null){
            model.addAttribute("msg", "跳转链接和跳转路径不可以同时为空");
            return Constants.MSG_URL;
        }
        if(param.getJumpLink()!=null&&!"".equals(param.getJumpLink())&&param.getJumpPath()!=null&&!"".equals(param.getJumpPath())){
            model.addAttribute("msg", "跳转链接和跳转路径不可以同时存在");
            return Constants.MSG_URL;
        }
        if((param.getJumpPath()!=null&&!"".equals(param.getJumpPath()))&&param.getJumpName()==null){
            model.addAttribute("msg", "跳转路径映射名称不可以为空");
            return Constants.MSG_URL;
        }
        if(param.getPushMethod()==null){
            model.addAttribute("msg", "推送方式不可以为空");
            return Constants.MSG_URL;
        }
        ClientConfig config = ClientConfig.getInstance();
        config.setPushHostName("https://api.jpush.cn");
        JPushClient jpushClient = new JPushClient(JpushConstant.MASTER_SECRET,JpushConstant.APP_KEY, null, config);
        PushPayload.Builder payLoad = PushPayload.newBuilder();
        if(param.getPlatform()==0){
            payLoad.setPlatform(Platform.all());
        }else if(param.getPlatform()==1){
            payLoad.setPlatform(Platform.android());
        }else if(param.getPlatform()==2||param.getPlatform()==3){
            payLoad.setPlatform(Platform.ios());
        }else if(param.getPlatform()==4||param.getPlatform()==5){
            payLoad.setPlatform(Platform.android_ios());
        }
        if(param.getAudience().equals("all")){
            payLoad.setAudience(Audience.all());
        }else {
            String[] strings = param.getAudience().split(",");
            payLoad.setAudience(Audience.alias(strings));
        }
        if(param.getJumpPath()!=null&&!"".equals(param.getJumpPath())){
            Map<String, String> map = new HashMap<>();
            map.put("page",param.getJumpPath());
            if(param.getJumpJson()!=null){
                Map map1= (Map) JSON.parse(param.getJumpJson());
                for (Object o : map1.keySet()) {
                    String str = (String) map1.get(o);
                    map.put(o+"",str);
                }
            }
            System.out.println(map);
            payLoad.setNotification(Notification.newBuilder()
                    .setAlert(param.getMessage())
                    .addPlatformNotification(AndroidNotification.newBuilder()
                            .addExtras(map)
                            .build())
                    .addPlatformNotification(IosNotification.newBuilder()
                            .addExtras(map)
                            .build())
                    .build());
        }else if(param.getJumpLink()!=null&&!"".equals(param.getJumpLink())){
            payLoad.setNotification(Notification.newBuilder()
                    .setAlert(param.getMessage())
                    .addPlatformNotification(AndroidNotification.newBuilder()
                            .addExtra("url",param.getJumpLink())
                            .build())
                    .addPlatformNotification(IosNotification.newBuilder()
                            .addExtra("url",param.getJumpLink())
                            .build())
                    .build());
        }
        if(param.getPlatform()==2||param.getPlatform()==5){
            payLoad.setOptions(Options.newBuilder().setApnsProduction(true).build());
        }
        PushPayload pushPayload = payLoad.build();
        Boolean flag=false;
        if(param.getPushTime()!=null){
            if(param.getPushTime().getTime()<=new Date().getTime()){
                model.addAttribute("msg", "推送时间不可小于当前系统时间");
                return Constants.MSG_URL;
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timeStr = format.format(param.getPushTime());
            try {
                ScheduleResult result = jpushClient.createSingleSchedule("singleSchedule",timeStr,pushPayload);
                flag=true;
                System.out.println(result);
            } catch (APIConnectionException e) {
                e.printStackTrace();
                model.addAttribute("msg", "极光推送连接异常");
                return Constants.MSG_URL;
            } catch (APIRequestException e) {
                e.printStackTrace();
                System.out.println("HTTP Status: " + e.getStatus());
                System.out.println("Error Code: " + e.getErrorCode());
                System.out.println("Error Message: " + e.getErrorMessage());
                model.addAttribute("msg", e.getErrorMessage());
                return Constants.MSG_URL;
            }
        }else {
            try {
                PushResult result = jpushClient.sendPush(pushPayload);
                flag=true;
                System.out.println(result);
            } catch (APIConnectionException e) {
                e.printStackTrace();
                model.addAttribute("msg", "极光推送连接异常");
                return Constants.MSG_URL;
            } catch (APIRequestException e) {
                e.printStackTrace();
                System.out.println("HTTP Status: " + e.getStatus());
                System.out.println("Error Code: " + e.getErrorCode());
                System.out.println("Error Message: " + e.getErrorMessage());
                model.addAttribute("msg", e.getErrorMessage());
                return Constants.MSG_URL;
            }
        }
        if(flag){//如果推送成功，保存推送
            if(param.getPushTime()==null){
                param.setPushTime(new Date());
            }
            param.setId(twiterIdService.getTwiterId());
            param.setStatus(1);
            jpushService.save(param);
        }
        return "redirect:list.jhtml";//TODO 发送成功重定向到极光推送管理页面
    }

    /**
     * 查询历史推送信息
     *
     */
    @RequestMapping("/list")
    public String list(ModelMap model,
                       @RequestParam(required = false, value = "pageNo", defaultValue = "1") int pageNo,
                       @RequestParam(required = false, value = "pageSize", defaultValue = "20") int pageSize,
                       @ModelAttribute Jpush jpush) {
        //参数整理
        Pageable pager = new Pageable();
        pager.setPageNumber(pageNo);
        pager.setPageSize(pageSize);
        pager.setOrderProperty("push_time");
        pager.setOrderDirection(Order.Direction.DESC);
        pager.setParameter(jpush);
        Page<Jpush> page = jpushService.findByPage(pager);
        model.addAttribute("jpushList", page);
        return "/common/push_management/index";
    }

    /**
     * 跳转到添加推送页面
     *
     */
    @RequestMapping("/add/forword")
    public String forword(ModelMap model) {
        model.addAttribute("jpush",null);
        return "/common/push_management/edit";
    }

    /**
     * 查看推送消息内容
     *
     */
    @RequestMapping("/show")
    public String show(ModelMap model,@RequestParam(required = true,value = "id")Long id) {
        Jpush jpush = jpushService.find(id);
        if(jpush==null){
            model.addAttribute("msg","推送信息不存在");
            return Constants.MSG_URL;
        }
        String audience = jpush.getAudience();
        if(!audience.equals("all")){//如果部分推送，则将数据解析，返还一个会员基础信息集合
            String[] codes = audience.split(",");
            List<String> strings = Arrays.asList(codes);
            List<RdMmBasicInfo> list = rdMmBasicInfoService.findShopMember(strings);
            if(list!=null&&list.size()>0){
                model.addAttribute("basicInfoList",list);
            }else {
                model.addAttribute("basicInfoList",null);
            }
        }else {
            model.addAttribute("basicInfoList",null);
        }
        model.addAttribute("jpush",jpush);
        return "/common/push_management/edit";
    }

    /**
     * 查找会员
     */
    @RequestMapping(value = "/findMember", method = RequestMethod.GET)
    public String findMember(String info, ModelMap model) {
        if(StringUtil.isEmpty(info)){
            model.addAttribute("rdMmBasicInfoList",new ArrayList<RdMmBasicInfo>());
            return "";//TODO
        }
        ArrayList<RdMmBasicInfo> rdMmBasicInfos = new ArrayList<>();
        RdMmBasicInfo rdMmBasicInfo = rdMmBasicInfoService.find("mmCode",info);
        if(rdMmBasicInfo!=null){
            rdMmBasicInfos.add(rdMmBasicInfo);
        }
        RdMmBasicInfo rdMmBasicInfo1 = rdMmBasicInfoService.find("mobile",info);
        if(rdMmBasicInfo1!=null){
            rdMmBasicInfos.add(rdMmBasicInfo1);
        }
        model.addAttribute("rdMmBasicInfoList", rdMmBasicInfos);
        return "/common/select/selectMember";//TODO
    }

    /**
     * 查找商品
     */
    @RequestMapping(value = "/findGoods", method = RequestMethod.POST)
    public String findGoods(String info, ModelMap model,
    @RequestParam(required = false, value = "pageNo", defaultValue = "1") int pageNo,
    @RequestParam(required = false, value = "pageSize", defaultValue = "20") int pageSize) {
        if(StringUtil.isEmpty(info)){
            model.addAttribute("goods",null);
            return "";//TODO
        }
        Pageable pageable = new Pageable();
        pageable.setPageSize(pageSize);
        pageable.setPageNumber(pageNo);
        pageable.setOrderDirection(Order.Direction.DESC);
        pageable.setOrderProperty("salenum");
        ShopGoods goods = new ShopGoods();
        goods.setGoodsShow(1);
        goods.setGoodsState(0);
        goods.setIsDel(2);
        goods.setState(1);
        goods.setGoodsListKeywords(info);
        pageable.setParameter(goods);
        Page<ShopGoods> page = shopGoodsService.findByPage(pageable);
        model.addAttribute("goods",page);
        return "";//TODO
    }

    /**
     * 查找活动
     */
    @RequestMapping(value = "/findActivitys", method = RequestMethod.GET)
    public String findActivitys(String info, ModelMap model,
                            @RequestParam(required = false, value = "pageNo", defaultValue = "1") int pageNo,
                            @RequestParam(required = false, value = "pageSize", defaultValue = "20") int pageSize) {
        /*if(StringUtil.isEmpty(info)){
            model.addAttribute("activitys",null);
            return "/common/select/selectActivitys";//TODO
        }*/
        Pageable pageable = new Pageable();
        pageable.setPageSize(pageSize);
        pageable.setPageNumber(pageNo);
        pageable.setOrderDirection(Order.Direction.DESC);
        pageable.setOrderProperty("create_time");
        ShopActivity shopActivity = new ShopActivity();
        shopActivity.setActivityStatus(20);
        shopActivity.setAuditStatus(1);
        shopActivity.setActivityName(info);
        pageable.setParameter(shopActivity);
        Page<ShopActivity> page = shopActivityService.findByPage(pageable);
        model.addAttribute("activitys",page);
        return "/common/select/selectActivitys";//TODO
    }

    /**
     * 查找文章
     */
    @RequestMapping(value = "/findArticles", method = RequestMethod.GET)
    public String findArticles(String info, ModelMap model,
                                @RequestParam(required = false, value = "pageNo", defaultValue = "1") int pageNo,
                                @RequestParam(required = false, value = "pageSize", defaultValue = "20") int pageSize) {
        /*if(StringUtil.isEmpty(info)){
            model.addAttribute("articles",null);
            return "/common/select/selectArticles";//TODO
        }*/
        Pageable pageable = new Pageable();
        pageable.setPageSize(pageSize);
        pageable.setPageNumber(pageNo);
        pageable.setOrderDirection(Order.Direction.DESC);
        pageable.setOrderProperty("create_time");
        ShopCommonArticle article = new ShopCommonArticle();
        article.setArticleShow(1);
        article.setIsDel(0);
        article.setStatus(1);
        article.setKey(info);
        pageable.setParameter(article);
        Page<ShopCommonArticle> page = shopCommonArticleService.findByPage(pageable);
        model.addAttribute("articles",page);
        return "/common/select/selectArticles";//TODO
    }
    /**
     * 查找优惠券
     */
    @RequestMapping(value = "/findCoupons", method = RequestMethod.POST)
    public String findCoupons(String info, ModelMap model,
                               @RequestParam(required = false, value = "pageNo", defaultValue = "1") int pageNo,
                               @RequestParam(required = false, value = "pageSize", defaultValue = "20") int pageSize) {
        if(StringUtil.isEmpty(info)){
            model.addAttribute("coupons",null);
            return "";//TODO
        }
        Pageable pageable = new Pageable();
        pageable.setPageSize(pageSize);
        pageable.setPageNumber(pageNo);
        pageable.setOrderDirection(Order.Direction.DESC);
        pageable.setOrderProperty("create_time");
        Coupon coupon = new Coupon();
        coupon.setStatus(2);
        coupon.setCouponLikeName(info);
        pageable.setParameter(coupon);
        Page<Coupon> page = couponService.findByPage(pageable);
        model.addAttribute("coupons",page);
        return "";//TODO
    }
}
