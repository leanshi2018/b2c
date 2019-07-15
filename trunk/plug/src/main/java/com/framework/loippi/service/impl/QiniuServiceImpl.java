package com.framework.loippi.service.impl;

import com.framework.loippi.service.QiniuService;
import com.framework.loippi.service.TSystemPluginConfigService;
import com.framework.loippi.utils.JacksonUtil;
import com.framework.loippi.utils.qiniu.QiniuConfig;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * Created by longbh on 2019/1/8.
 */
@Service
public class QiniuServiceImpl implements QiniuService {

    @Resource
    private TSystemPluginConfigService tSystemPluginConfigService;

    public String upload(MultipartFile myFile) {
        QiniuConfig.initPayConfig(tSystemPluginConfigService.readPlug("qiniuFilePlugin"));
        //构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(Zone.huanan());
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = null;
        Auth auth = Auth.create(QiniuConfig.APP_ID, QiniuConfig.APP_SECRET);
        String upToken = auth.uploadToken(QiniuConfig.bucket);
        try {
            Response response = uploadManager.put(myFile.getInputStream(), key, upToken, null, null);
            //解析上传成功的结果
            DefaultPutRet putRet = JacksonUtil.fromJson(response.bodyString(), DefaultPutRet.class);
            return QiniuConfig.qiniuLink + putRet.key;
        } catch (QiniuException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
