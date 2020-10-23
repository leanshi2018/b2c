package com.framework.loippi.controller.common;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.framework.loippi.controller.GenericController;
import com.framework.loippi.service.QiniuService;
import com.framework.loippi.utils.JacksonUtil;
import com.framework.loippi.utils.qiniu.QiniuConfig;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;

/**
 * 公共文件上传类
 * Created by longbh on 2017/8/2.
 */
@Slf4j
@Controller("adminShopFileuploadController")
@RequestMapping({"/admin/fileupload"})
public class ShopFileuploadController extends GenericController {

    @Autowired
    private QiniuService qiniuService;

    /**
     * 文件上传
     *
     * @param myfiles
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/uploadImage", produces = "text/html;charset=UTF-8")
    public
    @ResponseBody
    String uploadImage(@RequestParam MultipartFile myfiles) throws Exception {
        //可以在上传文件的同时接收其它参数
        Map<String, Object> map = Maps.newConcurrentMap();
        System.err.println();
        if (myfiles.getSize()!=0){
            String link = qiniuService.upload(myfiles);
            map.put("result", link);
            map.put("success", true);
        }else{
            map.put("result", "");
            map.put("success", false);
        }

        //上传后信息写入json回显
        return JacksonUtil.toJson(map);
    }


    // 构造一个带指定Zone对象的配置类
    Configuration cfg = new Configuration(Zone.zone0());

    // ...其他参数参考类注释
    UploadManager uploadManager = new UploadManager(cfg);
    // 默认不指定key的情况下，以文件内容的hash值作为文件名（上传后的文件名）
    String key = null;

    @RequestMapping(value = "/upload", produces = "text/html;charset=UTF-8")
    public
    @ResponseBody
    String upload(@RequestParam(required = false, value = "myfiles") CommonsMultipartFile myfiles) throws Exception {
        //可以在上传文件的同时接收其它参数
        Map<String, Object> map = Maps.newConcurrentMap();
        if (myfiles.getSize()!=0){
            try {
                //得到上传文件的文件名，并赋值给key作为七牛存储的文件名
                key = myfiles.getOriginalFilename();
                //把文件转化为字节数组
                InputStream is = myfiles.getInputStream();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] b = new byte[1024];
                int len = -1;
                while ((len = is.read(b)) != -1) {
                    bos.write(b, 0, len);
                }
                byte[] uploadBytes = bos.toByteArray();
                //进行七牛的操作，不懂去七牛的sdk上看
                Auth auth = Auth.create(QiniuConfig.APP_ID, QiniuConfig.APP_SECRET);
                String upToken = auth.uploadToken(QiniuConfig.bucket);
                //默认上传接口回复对象
                DefaultPutRet putRet;
                try {
                    //进行上传操作，传入文件的字节数组，文件名，上传空间，得到回复对象
                    Response response = uploadManager.put(uploadBytes, key, upToken);
                    putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                    System.out.println(putRet.key);//key文件名
                    System.out.println(putRet.hash);//hash七牛返回的文件存储的地址，可以使用这个地址加七牛给你提供的前缀访问到这个视频。
                    map.put("result", "http://rdnmall.com/"+putRet.hash);
                    map.put("success", true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            map.put("result", "");
            map.put("success", false);
        }

        //上传后信息写入json回显
        return JacksonUtil.toJson(map);
    }

    /**
     * 富文本编辑器
     *
     * @param uploadify
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/ueditor", produces = "text/html;charset=UTF-8", method = {RequestMethod.POST})
    public
    @ResponseBody
    String ueditUpload(@RequestParam MultipartFile uploadify) throws Exception {
        //可以在上传文件的同时接收其它参数
        Map<String, Object> result = new HashMap<>();
        //云服务器状态设置值
        String link = qiniuService.upload(uploadify);
        result.put("original", uploadify.getOriginalFilename());
        result.put("title", uploadify.getOriginalFilename());
        result.put("url", link);
        result.put("state", "success");
        String json = JacksonUtil.toJson(result);
        return json;
    }

}
