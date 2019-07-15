package com.framework.loippi.controller.common;

import com.framework.loippi.controller.GenericController;
import com.framework.loippi.service.QiniuService;
import com.framework.loippi.utils.JacksonUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

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
