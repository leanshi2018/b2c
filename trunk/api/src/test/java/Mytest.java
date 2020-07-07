import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.framework.loippi.utils.ApiUtils;
import com.framework.loippi.utils.Dateutil;
import com.framework.loippi.utils.StringUtil;
import com.framework.loippi.utils.sms.AldayuUtil;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.allinpay.yunst.sdk.YunClient;
import com.allinpay.yunst.sdk.bean.YunConfig;
import com.allinpay.yunst.sdk.bean.YunRequest;
import com.framework.loippi.consts.AllInPayConstant;

public class Mytest {
    public static void main(String[] args) {
        final String serverUrl = "https://fintech.allinpay.com/service/soa"; //测试环境请咨询对接人员
        //final String serverUrl = "http://116.228.64.55:6900/service/soa"; //测试环境请咨询对接人员(通商云门户测试环境地址)
        final String sysId = "2004261243386008175";//商户私钥证书
        final String pwd = "leanshi20180501";//商户私钥密码
        //final String alias = "100000000002";
        final String alias = "2004261243386008175";//商户公钥证书
        //final String path = "D:\\tonglian-yun\\branches\\yuntestjava\\100000000002.pfx";
        //final String path = "C:\\Users\\zhangyu\\sign\\1902271423530473681.pfx";
        final String path = "D:\\formalSign\\2004261243386008175.pfx";
        //final String tlCertPath = "D:\\tonglian-yun\\branches\\yuntestjava\\TLCert-test.cer"; //生产环境请使用生产证书
        //final String tlCertPath = "C:\\Users\\zhangyu\\sign\\TLCert-test.cer"; //生产环境请使用生产证书
        final String tlCertPath = "D:\\formalSign\\TLCert(prod).cer"; //生产环境请使用生产证书
        final String version = "2.0";
        final YunConfig config = new YunConfig(serverUrl, sysId, pwd, alias, version, path,tlCertPath);
        YunClient.configure(config);
        final YunRequest request = new YunRequest("MemberService", "createMember");
        request.put("bizUserId", "201807170002");
        request.put("memberType", 2);
        request.put("source", 2);
        try {
            String s = YunClient.request(request);
            Map maps = (Map)JSON.parse(s);
            String status = maps.get("status").toString();
            String signedValue = maps.get("signedValue").toString();

            System.out.println("status="+status);
            System.out.println("signedValue="+signedValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 分账
     */
    @Test
    public void testCut() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = format.format(calendar.getTime());
        System.out.println(str);
    }
}
