package com.framework.loippi.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by longbh on 2019/1/8.
 */
public interface QiniuService {

    String upload(MultipartFile myFile);

}
