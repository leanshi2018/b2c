package com.framework.loippi.utils;

import com.framework.loippi.dto.oss.AliyunOss;
import com.framework.loippi.service.TwiterIdService;
import com.google.common.collect.Maps;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by Administrator on 2017/11/24.
 */
public class FileUpload {

    private static TwiterIdService twiterIdService = SpringContextUtil.getBean("twiterIdServiceImpl");

    //上传图片
    public static Map<String, Object> uploadImage(HttpServletRequest request, String... args) {
        Map <String, Object> result = Maps.newLinkedHashMap();
        if (args == null || args.length == 0) {
            result.put("code", "0");
            result.put("msg", "参数无效");
        }
        MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
        for (String name : args) {
            try {
                MultipartFile file = mRequest.getFile(name);
                if (file == null) {
                    result.put("code", "0");
                    result.put("msg", "参数无效");
                }
                String ext = FilenameUtils.getExtension(file.getOriginalFilename());
                if (!"jpg,jpeg,bmp,gif,png".contains(StringUtils.lowerCase(ext))) {
                    result.put("code", "0");
                    result.put("msg", "文件类型错误！支持jpg,jpeg,bmp,gif,png格式");
                } else {
                    String twiterId = twiterIdService.getTwiterId() + "." + ext;
                    String url = AliyunOss.uploadInputStream(twiterId, file.getInputStream());
                    result.put(name + "Url", url);
                    result.put(name + "UrlAbsolute", url);
                    result.put("code", "1");
                }
            } catch (Exception e) {
                e.printStackTrace();
                result.put("code", "0");
                result.put("msg", "上传失败");
            }
        }
        return result;

    }

}
