package com.framework.loippi.controller.allinpay;

import com.allinpay.yunst.sdk.YunClient;
import com.allinpay.yunst.sdk.bean.YunRequest;
import com.framework.loippi.utils.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Controller
@RequestMapping("/admin/allinpayUpload")
public class UploadlicenseController {
    /**
     *
     * @param request
     * @param response
     * @param imageUrl 图片路径
     * @param type 1：上传营业执照  2：组织机构代码证（三证时必传） 3：税务登记证（三证时必传） 4：银行开户证明（非必传，上传《银行开户许可 证》或《基本存款账户信息》等可证明平台银行账号和户名的文件）
     *             5：机构信用代码（非必传） 6：ICP 备案许可（非必传） 7：行业许可证（非必传） 8：身份证正面（人像面）（必传） 9：身份证反面（国徽面）（必传）
     * @param mmCode
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/license/upload.jhtml", method = RequestMethod.POST)
    public String allInPayContractBack(HttpServletRequest request, HttpServletResponse response,String imageUrl,Long type,String mmCode) throws IOException{
        byte[] data = null;
        // 读取图片字节数组
        try {
            InputStream in = new FileInputStream(imageUrl);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        String encode = encoder.encode(Objects.requireNonNull(data));
        //调用上传图片信息
        final YunRequest allInRequest = new YunRequest("MemberService", "idcardCollect");
        allInRequest.put("bizUserId", mmCode);
        allInRequest.put("picType", type);
        allInRequest.put("picture", encode);
        try {
            String s = YunClient.request(allInRequest);
            Map<String, Object> map = JacksonUtil.convertMap(s);
            if(map.get("status").equals("OK")){
                System.out.println("上传成功");
                return "";
            }else if(map.get("status").equals("error")){
                String message = (String) map.get("message");
                System.out.println(message);
                return "";
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
