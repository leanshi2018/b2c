import com.allinpay.yunst.sdk.YunClient;
import com.allinpay.yunst.sdk.bean.YunConfig;
import com.allinpay.yunst.sdk.bean.YunRequest;

public class Mytest {
    public static void main(String[] args) {
        //final String serverUrl = "https://fintech.allinpay.com/service/soa"; //测试环境请咨询对接人员
        final String serverUrl = "http://116.228.64.55:6900/service/soa"; //测试环境请咨询对接人员
        final String sysId = "1902271423530473681";
        final String pwd = "123456";
        //final String alias = "100000000002";
        final String alias = "1902271423530473681";
        //final String path = "D:\\tonglian-yun\\branches\\yuntestjava\\100000000002.pfx";
        final String path = "C:\\Users\\zhangyu\\sign\\1902271423530473681.pfx";
        //final String tlCertPath = "D:\\tonglian-yun\\branches\\yuntestjava\\TLCert-test.cer"; //生产环境请使用生产证书
        final String tlCertPath = "C:\\Users\\zhangyu\\sign\\TLCert-test.cer"; //生产环境请使用生产证书
        final String version = "2.0";
        final YunConfig config = new YunConfig(serverUrl, sysId, pwd, alias, version, path,tlCertPath);
        YunClient.configure(config);
        final YunRequest request = new YunRequest("MemberService", "createMember");
        request.put("bizUserId", "201807170002");
        request.put("memberType", 2);
        request.put("source", 2);
        try {
            String s = YunClient.request(request);
            System.out.println(s.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
