import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

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
import com.framework.loippi.consts.Constants;
import com.framework.loippi.consts.JpushConstant;
import com.framework.loippi.utils.ApiUtils;
import com.framework.loippi.utils.JacksonUtil;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.allinpay.yunst.sdk.YunClient;
import com.allinpay.yunst.sdk.bean.YunConfig;
import com.allinpay.yunst.sdk.bean.YunRequest;
import com.framework.loippi.consts.AllInPayBillCutConstant;
import com.framework.loippi.consts.AllInPayConstant;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Mytest {
    public static void main(String[] args) {
        final String serverUrl = "https://fintech.allinpay.com/service/soa"; //测试环境请咨询对接人员
        final String sysId = "2004261243386008175";//商户私钥证书
        final String pwd = "leanshi20180501";//商户私钥密码
        final String alias = "2004261243386008175";//商户公钥证书
        final String path = "D:\\formalSign\\2004261243386008175.pfx";
        final String tlCertPath = "D:\\formalSign\\TLCert(prod).cer"; //生产环境请使用生产证书
        final String version = "2.0";
        final YunConfig config = new YunConfig(serverUrl, sysId, pwd, alias, version, path,tlCertPath);
        YunClient.configure(config);
        final YunRequest request = new YunRequest("OrderService", "consumeApply");
        request.put("payerId", "900020102");
        request.put("recieverId", "103355283");
        request.put("bizOrderNo", "123456");
        request.put("amount", 100L);
        request.put("fee", 0L);
        request.put("validateType", 1L);
        request.put("backUrl", AllInPayConstant.TRANSFER_BACKURL);
        request.put("industryCode", "2422");
        request.put("industryName", "其他");
        request.put("source", 2L);
        Map<String, Object> object1 = new LinkedHashMap<>();
        object1.put("amount",100L);
        object1.put("accountSetNo", AllInPayBillCutConstant.ACCOUNT_SET_NO);
        ArrayList<Object> objects = new ArrayList<>();
        objects.add(object1);
        Map<String, Object> payMethods = new LinkedHashMap<>();
        payMethods.put("BALANCE",objects);
        request.put("payMethod", payMethods);
        try {
            String s = YunClient.request(request);
            Map maps = (Map)JSON.parse(s);
            String status = maps.get("status").toString();
            System.out.println("status="+status);
            System.out.println(maps);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 分账
     */
    @Test
    public void testCut() {
        final String serverUrl = "https://fintech.allinpay.com/service/soa"; //测试环境请咨询对接人员
        final String sysId = "2004261243386008175";//商户私钥证书
        final String pwd = "leanshi20180501";//商户私钥密码
        final String alias = "2004261243386008175";//商户公钥证书
        final String path = "D:\\formalSign\\2004261243386008175.pfx";
        final String tlCertPath = "D:\\formalSign\\TLCert(prod).cer"; //生产环境请使用生产证书
        final String version = "2.0";
        final YunConfig config = new YunConfig(serverUrl, sysId, pwd, alias, version, path,tlCertPath);
        YunClient.configure(config);
        final YunRequest request = new YunRequest("OrderService", "pay");
        request.put("bizUserId", "900020102");
        request.put("bizOrderNo", "123456");
        request.put("verificationCode","");
        request.put("consumerIp","192.168.1.88");
        try {
            String s = YunClient.request(request);
            Map maps = (Map)JSON.parse(s);
            String status = maps.get("status").toString();
            System.out.println("status="+status);
            System.out.println(maps);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test() {
        ClientConfig config = ClientConfig.getInstance();
        config.setPushHostName("https://api.jpush.cn");
        JPushClient jpushClient = new JPushClient(JpushConstant.MASTER_SECRET,JpushConstant.APP_KEY, null, config);
        PushPayload.Builder payLoad = PushPayload.newBuilder();
        payLoad.setPlatform(Platform.all());
        //payLoad.setAudience(Audience.all());
        ArrayList<String> strings = new ArrayList<>();
        strings.add("900009045");
        //strings.add("900004041");
        //strings.add("900000011");
        payLoad.setAudience(Audience.alias(strings));
        /*String[] strings = new String[10];
        strings[0] = "900000011";
        payLoad.setAudience(Audience.alias(strings));*/

        //if(param.getJumpPath()!=null&&!"".equals(param.getJumpPath())){
            Map<String, String> map = new HashMap<>();
            map.put("page","goodsdetailspage");
            //map.put("goodsId","{\"goodsId\":\"6552743534695288832\"}");
            map.put("goodsId","6552743534695288832");
            /*if(param.getJumpJson()!=null){
                Map map1= (Map) JSON.parse(param.getJumpJson());*/
/*        HashMap<String, String> map1 = new HashMap<>();
        for (String o : map1.keySet()) {
                    String str =  map1.get(o);
                    map.put(o+"",str);
                }*/
            //}
            System.out.println(map);
            payLoad.setNotification(Notification.newBuilder()
                    .setAlert("逢考必过111")
                    .addPlatformNotification(AndroidNotification.newBuilder()
                            .addExtras(map)
                            .build())
                    .addPlatformNotification(IosNotification.newBuilder()
                            .addExtras(map)
                            .build())
                    .build());

/*            payLoad.setNotification(Notification.newBuilder()
                    //.setAlert(param.getMessage())
                    .setAlert("测试1")
                    .addPlatformNotification(AndroidNotification.newBuilder()
                            .addExtra("url","wwww.baidu.com")
                            //.addExtra("url",param.getJumpLink())
                            .build())
                    .addPlatformNotification(IosNotification.newBuilder()
                            //.addExtra("url",param.getJumpLink())
                            .addExtra("url","wwww.baidu.com")
                            .build())
                    .build());*/
        //}
        //if(param.getPlatform()==2||param.getPlatform()==5){
            //payLoad.setOptions(Options.newBuilder().setApnsProduction(true).build());
            payLoad.setOptions(Options.newBuilder().setApnsProduction(true).build());
        //}
        PushPayload pushPayload = payLoad.build();
        Boolean flag=false;
/*        if(param.getPushTime()!=null){
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
        }else {*/
            try {
                System.out.println("推送开始");
                PushResult result = jpushClient.sendPush(pushPayload);
                System.out.println("推送成功");
                flag=true;
                System.out.println(result);
            } catch (APIConnectionException e) {
                e.printStackTrace();
                //model.addAttribute("msg", "极光推送连接异常");
                //return Constants.MSG_URL;
            } catch (APIRequestException e) {
                e.printStackTrace();
                System.out.println("HTTP Status: " + e.getStatus());
                System.out.println("Error Code: " + e.getErrorCode());
                System.out.println("Error Message: " + e.getErrorMessage());
                //model.addAttribute("msg", e.getErrorMessage());
                //return Constants.MSG_URL;
            }
        //}
    }
}
