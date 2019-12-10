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
import cn.jpush.api.push.model.notification.*;
import cn.jpush.api.schedule.ScheduleResult;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.framework.loippi.consts.Constants;
import com.framework.loippi.consts.JpushConstant;
import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.coupon.Coupon;
import com.framework.loippi.entity.coupon.CouponPayDetail;
import com.framework.loippi.entity.jpush.Jpush;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.jpush.JpushService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.Paramap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller("JpushController")
@RequestMapping("/admin/jpush")
public class JpushController extends GenericController {
    @Resource
    private TwiterIdService twiterIdService;
    @Resource
    private JpushService jpushService;

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
}
