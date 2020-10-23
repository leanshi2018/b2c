import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

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
        byte[] data = null;
        // 读取图片字节数组
        try {
            InputStream in = new FileInputStream("E:\\微信图片_20200427092558.jpg");
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        String encode = encoder.encode(Objects.requireNonNull(data));
        System.out.println(encode);
    }
}
