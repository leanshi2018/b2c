package com.framework.loippi.service.huanxin.impl;

import com.framework.loippi.service.huanxin.HxService;
import com.framework.loippi.utils.huanxin.EasemobAPI;
import com.framework.loippi.utils.huanxin.ResponseHandler;
import com.framework.loippi.utils.huanxin.TokenUtil;
import io.swagger.client.ApiException;
import io.swagger.client.api.UsersApi;
import io.swagger.client.model.RegisterUsers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by longbh on 2019/1/28.
 */
@Service
public class HxServiceImpl implements HxService {

    @Value("#{properties['ORG_NAME']}")
    protected String ORG_NAME;
    @Value("#{properties['APP_NAME']}")
    protected String APP_NAME;

    @Value("#{properties['GRANT_TYPE']}")
    protected String GRANT_TYPE;
    @Value("#{properties['CLIENT_ID']}")
    protected String CLIENT_ID;
    @Value("#{properties['CLIENT_SECRET']}")
    protected String CLIENT_SECRET;

    private UsersApi api = new UsersApi();
    private ResponseHandler responseHandler = new ResponseHandler();

    @PostConstruct
    public void init() {
        TokenUtil.instance(GRANT_TYPE, CLIENT_ID, CLIENT_SECRET, ORG_NAME, APP_NAME);
    }

    @Override
    public Object createNewIMUserSingle(final Object payload) {
        return responseHandler.handle(new EasemobAPI() {
            @Override
            public Object invokeEasemobAPI() throws ApiException {
                return api.orgNameAppNameUsersPost(ORG_NAME, APP_NAME, (RegisterUsers) payload,
                        TokenUtil.getAccessToken());
            }
        });
    }

}
