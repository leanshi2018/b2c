package com.framework.loippi.service.impl;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.framework.loippi.service.RedisService;
import com.framework.loippi.utils.codec.RSAUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.framework.loippi.service.RSAService;

/**
 * Service - RSA安全
 *
 * @author Mounate Yan。
 * @version 1.0
 */
@Service("rsaServiceImpl")
public class RSAServiceImpl implements RSAService {

    /**
     * "私钥"参数名称
     */
    private static final String PRIVATE_KEY_ATTRIBUTE_NAME = "privateKey";
    @Autowired
    private RedisService redisService;

    @Transactional(readOnly = true)
    public RSAPublicKey generateKey(HttpServletRequest request, String sessionId) {
        Assert.notNull(request);
        KeyPair keyPair = RSAUtils.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        redisService.save(sessionId, Base64.getEncoder().encodeToString(privateKey.getEncoded()));
        return publicKey;
    }

    @Transactional(readOnly = true)
    public void removePrivateKey(HttpServletRequest request) {
        Assert.notNull(request);
        HttpSession session = request.getSession();
        session.removeAttribute(PRIVATE_KEY_ATTRIBUTE_NAME);
    }

    @Transactional(readOnly = true)
    public String decryptParameter(String name, HttpServletRequest request) {
        Assert.notNull(request);
        if (name != null) {
            String token = request.getParameter("token");
            RSAPrivateKey privateKey = (RSAPrivateKey) RSAUtils.generatePrivateKey((String) redisService.get(token,String.class));
            String parameter = request.getParameter(name);
            if (privateKey != null && StringUtils.isNotEmpty(parameter)) {
                return RSAUtils.decrypt(privateKey, parameter);
            }
        }
        return null;
    }

}