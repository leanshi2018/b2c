package com.framework.loippi.utils.jpush;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import com.framework.loippi.utils.JacksonUtil;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * 极光推送
 */
public class JpushUtilsSecond {
    private static Logger log = Logger.getLogger(JpushUtilsSecond.class);
    public static String masterSecret = "338c3c08861fb9a0a9ba15b3";
    public static String appKey = "165be19ef56a742cd424494c";
    private static JPushClient jpushClient = new JPushClient(masterSecret, appKey);

    public static void main(String[] args) {
        Map<String, String> extras = new HashMap<>();
        extras.put("bizId", "6358612941469650944");
        extras.put("bizType", "1");
        JpushUtilsSecond.push2alias("abcd", new String[]{"userId_6372679392073617408"}, extras, 0);
//        Map<String, String> extras = new HashMap<>();
//        extras.put("bizId", "6361772900080422912");
//        extras.put("bizType", "1");
//        JpushUtilsSecond.push2alias("abcd", new String[]{"userId_6325360598871707648"}, extras, 0);
    }

    /**
     * 推送到所有平台用户
     *
     * @param content
     * @return
     */
    public static boolean push2all(String content) {

        PushPayload payload = PushPayload.alertAll(content);
        try {
            PushResult result = jpushClient.sendPush(payload);
            return result.isResultOK();
        } catch (APIConnectionException e) {
            log.info(e.getMessage());
        } catch (APIRequestException e) {
            log.info(e.getMessage());
        }
        return false;
    }

    public static String push2alias(String content, String[] alias) {
        JPushClient jpushClient = new JPushClient(masterSecret, appKey);
        //消息一小时，会清除
        PushPayload payload = PushPayload.newBuilder().setPlatform(Platform.all())
                .setAudience(Audience.alias(alias))

                .setOptions(Options.newBuilder().setTimeToLive(60 * 60).build())
                .setNotification(Notification.alert(content)).setMessage(Message.content(content)).build();
        try {
            PushResult result = jpushClient.sendPush(payload);
            if (result.getOriginalContent() != null) {
                Map<String, Object> data = JacksonUtil.convertMap(result.getOriginalContent());
                return data.get("msg_id") + "";
            }
            return result.getOriginalContent();
        } catch (APIConnectionException e) {
            log.info(e.getMessage());
        } catch (APIRequestException e) {
            log.info(e.getMessage());
        }
        return null;
    }

    /**
     * 平台是 iOS，推送目标是 "tag1", "tag_all" 的交集，推送内容同时包括通知与消息 - 通知信息是 ALERT，角标数字为 5，通知声音为 "happy"，并且附加字段 from = "JPush"；消息内容是 MSG_CONTENT。通知是 APNs 推送通道的，消息是 JPush 应用内消息通道的。APNs 的推送环境是“生产”（如果不显式设置的话，Library 会默认指定为开发）
     */
    public static String push2alias(String content, String[] alias, Map<String, String> extras, int count) {
        JPushClient jpushClient = new JPushClient(masterSecret, appKey);
        //消息一小时，会清除
        PushPayload payload = PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.alias(alias))
                .setMessage(Message.newBuilder().setMsgContent(content).build())
                //生产 环境设置   .setOptions(Options.newBuilder().setApnsProduction(true).setTimeToLive(60*60).build())
                .setOptions(Options.newBuilder().setTimeToLive(60 * 60).build())
                .setNotification(Notification.newBuilder()
                        .addPlatformNotification(IosNotification.newBuilder()
                                .setAlert(content)
                                .setBadge(count)
                                .setSound("happy.caf")
                                .addExtras(extras)
                                .build())
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setAlert(content)
                                .addExtras(extras)
                                .build())
                        .build())
                .build();
        ;
        try {
            PushResult result = jpushClient.sendPush(payload);
            if (result.getOriginalContent() != null) {
                Map<String, Object> data = JacksonUtil.convertMap(result.getOriginalContent());
                return data.get("msg_id") + "";
            }
            return result.getOriginalContent();
        } catch (APIConnectionException e) {
            log.info(e.getMessage());
        } catch (APIRequestException e) {
            log.info(e.getMessage());
        }
        return null;
    }


    /**
     * 平台是 iOS，推送目标是 "tag1", "tag_all" 的交集，推送内容同时包括通知与消息 - 通知信息是 ALERT，角标数字为 5，通知声音为 "happy"，并且附加字段 from = "JPush"；消息内容是 MSG_CONTENT。通知是 APNs 推送通道的，消息是 JPush 应用内消息通道的。APNs 的推送环境是“生产”（如果不显式设置的话，Library 会默认指定为开发）
     */
    public static String push2alias_ios(String content, String alias, String extraKey, String extraValue, int count) {
        JPushClient jpushClient = new JPushClient(masterSecret, appKey);


        //消息一小时，会清除
        PushPayload payload = PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.alias(alias))
                .setMessage(Message.newBuilder().setMsgContent(content).build())

                //生产 环境设置   .setOptions(Options.newBuilder().setApnsProduction(true).setTimeToLive(60*60).build())
                .setOptions(Options.newBuilder().setTimeToLive(60 * 60).build())
                .setNotification(Notification.newBuilder()
                        .addPlatformNotification(IosNotification.newBuilder()
                                .setAlert(content)
                                .setBadge(count)
                                .setSound("happy.caf")
                                .addExtra(extraKey, extraValue)
                                .build())
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setAlert(content)
                                .addExtra(extraKey, extraValue)
                                .build())
                        .build())
                .build();
        ;
        try {
            PushResult result = jpushClient.sendPush(payload);
            if (result.getOriginalContent() != null) {
                Map<String, Object> data = JacksonUtil.convertMap(result.getOriginalContent());
                return data.get("msg_id") + "";
            }
            return result.getOriginalContent();
        } catch (APIConnectionException e) {
            log.info(e.getMessage());
        } catch (APIRequestException e) {
            log.info(e.getMessage());
        }
        return null;
    }


    /**
     * 平台是 iOS，推送目标是 "tag1", "tag_all" 的交集，推送内容同时包括通知与消息 - 通知信息是 ALERT，角标数字为 5，通知声音为 "happy"，并且附加字段 from = "JPush"；消息内容是 MSG_CONTENT。通知是 APNs 推送通道的，消息是 JPush 应用内消息通道的。APNs 的推送环境是“生产”（如果不显式设置的话，Library 会默认指定为开发）
     */
    public static PushPayload buildPushObject_ios_tagAnd_alertWithExtrasAndMessage(String MSG_CONTENT, String ALERT) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.ios())
                .setAudience(Audience.tag_and("tag1", "tag_all"))
                .setNotification(Notification.newBuilder()
                        .addPlatformNotification(IosNotification.newBuilder()
                                .setAlert(ALERT)
                                .setBadge(5)
                                .setSound("happy.caf")
                                .addExtra("from", "JPush")
                                .build())
                        .build())
                .setMessage(Message.content(MSG_CONTENT))
                .setOptions(Options.newBuilder()
                        .setApnsProduction(true)
                        .build())
                .build();
    }


    /**
     * 极光推送 发送
     *
     * @param content
     * @param tag
     * @return msg_id
     */
    public static String push2tag(String content, String tag) {
        JPushClient jpushClient = new JPushClient(masterSecret, appKey);
        PushPayload payload = PushPayload.newBuilder().setPlatform(Platform.all())
                .setAudience(Audience.tag(tag))
                .setNotification(Notification.alert(content))
                .setMessage(Message.content(content)).build();
        try {
            PushResult result = jpushClient.sendPush(payload);
            if (result.getOriginalContent() != null) {
                Map<String, Object> data = JacksonUtil.convertMap(result.getOriginalContent());
                return data.get("msg_id") + "";
            }
            return result.getOriginalContent();
        } catch (APIConnectionException e) {
            log.info(e.getMessage());
        } catch (APIRequestException e) {
            log.info(e.getMessage());
        }
        return null;
    }

}
