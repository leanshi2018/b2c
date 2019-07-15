package com.framework.loippi.service.impl;

import com.framework.loippi.service.HttpService;
import okhttp3.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by longbh on 2017/1/3.
 */
@Service
public class HttpServiceImpl implements HttpService {

    private OkHttpClient client;

    @PostConstruct
    private void init() {
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public String post(String url, String postBody) {
        MediaType MEDIA_TYPE_TEXT = MediaType.parse("application/json; charset=utf-8");
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MEDIA_TYPE_TEXT, postBody))
                .addHeader("Content-Encoding", "gzip")
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String body = response.body().string();
                return body;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String postForm(String url, Map<String, String> form) {
        FormBody.Builder formBody = new FormBody.Builder();
        Set<String> keys = form.keySet();
        for (String item : keys) {
            formBody.add(item, form.get(item));
        }

        Request request = new Request.Builder()
                .url(url)
                .post(formBody.build())
                .addHeader("Content-Encoding", "gzip")
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String body = response.body().string();
                return body;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
