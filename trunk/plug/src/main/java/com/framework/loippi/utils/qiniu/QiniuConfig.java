package com.framework.loippi.utils.qiniu;

import java.util.Map;
import java.util.UUID;

import com.qiniu.util.Auth;

/**
 * Created by longbh on 2019/1/8.
 */
public class QiniuConfig {

    public static String APP_ID = "PCxLWek_quRVfBUKAG7f6KmuIrtTNA3VVesFdent";

    public static String APP_SECRET = "Nh8QdkHTzkq0WdqCc6S1Me4pGS1mCChqVSNMVakZ";

    public static String bucket = "member";

    public static String qiniuLink = "http://rdnmall.com/";

    Auth auth = Auth.create(APP_ID, APP_SECRET);
    
    public static void initPayConfig(Map<String, Object> dto) {
        QiniuConfig.APP_ID = dto.get("appId") + "";
        QiniuConfig.bucket = dto.get("bucket") + "";
        QiniuConfig.APP_SECRET = dto.get("appSecret") + "";
        QiniuConfig.qiniuLink = dto.get("qiniuLink") + "";
    }

    public static void getFileName() {
    }

    /**
     * 获取凭证
     * @param bucketName 空间名称
     * @param key 如果需要覆盖上传则设置此参数
     * @return
     */
    public String getUpToken(String bucketName,String key) {
        return auth.uploadToken(bucketName);
    }

    /**
     * 返回uuid命名的文件名称
     *
     * @param fileName
     * @return
     */
    public static String getFileName(String fileName) {
        String prefix = UUID.randomUUID().toString().replaceAll("-", "");
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        return prefix + suffix;
    }

}
