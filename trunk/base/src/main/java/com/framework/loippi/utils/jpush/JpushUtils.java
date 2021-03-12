package com.framework.loippi.utils.jpush;

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
import com.framework.loippi.consts.JpushConstant;
import com.google.gson.JsonObject;

import java.util.*;

public class JpushUtils {

    /**
     * 发送极光推送
     *
     */
    public static boolean sendJpush(ArrayList<String> strings,String message,HashMap extras) {
        ClientConfig config = ClientConfig.getInstance();
        config.setPushHostName("https://api.jpush.cn");
        JPushClient jpushClient = new JPushClient(JpushConstant.MASTER_SECRET,JpushConstant.APP_KEY, null, config);
        PushPayload.Builder payLoad = PushPayload.newBuilder();
        payLoad.setPlatform(Platform.all());
        payLoad.setAudience(Audience.alias(strings));
        payLoad.setNotification(Notification.newBuilder()
                .setAlert(message)
                .addPlatformNotification(AndroidNotification.newBuilder()
                        .addExtras(extras)
                        .build())
                .addPlatformNotification(IosNotification.newBuilder()
                        .addExtras(extras)
                        .build())
                .build());
        payLoad.setOptions(Options.newBuilder().setApnsProduction(true).build());
        //*****************************************************************************
        /*JsonObject notification_3rd = new JsonObject();
        notification_3rd.addProperty("alert","message");
        JsonObject extras1 = new JsonObject();
        extras1.addProperty("page","goodsdetailspage");
        extras1.addProperty("goodsId","6552743534695288832");
        payLoad.addCustom("notification_3rd",notification_3rd);
        notification_3rd.add("extras",extras1);*/
        //*****************************************************************************
        PushPayload pushPayload = payLoad.build();
        Boolean flag=false;
        try {
            System.out.println("推送开始");
            PushResult result = jpushClient.sendPush(pushPayload);
            System.out.println("推送成功");
            flag=true;
            System.out.println(result);
        } catch (APIConnectionException e) {
            e.printStackTrace();
        } catch (APIRequestException e) {
            e.printStackTrace();
            System.out.println("HTTP Status: " + e.getStatus());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Error Message: " + e.getErrorMessage());
        }
        return flag;
    }
}
