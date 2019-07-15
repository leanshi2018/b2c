package com.framework.loippi.utils.huanxin;

import com.framework.loippi.utils.JacksonUtil;
import io.swagger.client.ApiException;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by longbh on 2019/1/28.
 */
@Slf4j
public class ResponseHandler {

    public Object handle(EasemobAPI easemobAPI) {
        Object result = null;
        try {
            result = easemobAPI.invokeEasemobAPI();
        } catch (ApiException e) {
            if (e.getCode() == 401) {
                log.info("The current token is invalid, re-generating token for you and calling it again");
                TokenUtil.initTokenByProp();
                try {
                    result = easemobAPI.invokeEasemobAPI();
                } catch (ApiException e1) {
                    log.error(e1.getMessage());
                }
                return result;
            }
            if (e.getCode() == 429) {
                log.warn("The api call is too frequent");
            }
            if (e.getCode() >= 500) {
                log.info("The server connection failed and is being reconnected");
                result = retry(easemobAPI);
                if (result != null) {
                    return result;
                }
                System.out.println(e);
                log.error("The server may be faulty. Please try again later");
            }
            Map<String, Object> map = JacksonUtil.convertMap(e.getResponseBody());
            log.error("error_code:{} error_msg:{} error_desc:{}", e.getCode(), e.getMessage(), map.get("error_description"));
        }
        return result;
    }

    public Object retry(EasemobAPI easemobAPI) {
        Object result = null;
        long time = 5;
        for (int i = 0; i < 3; i++) {
            try {
                TimeUnit.SECONDS.sleep(time);
                log.info("Reconnection is in progress..." + i);
                result = easemobAPI.invokeEasemobAPI();
                if (result != null) {
                    return result;
                }
            } catch (ApiException e1) {
                time *= 3;
            } catch (InterruptedException e1) {
                log.error(e1.getMessage());
            }
        }
        return result;
    }

}
