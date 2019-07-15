package com.framework.loippi.shiro.realm;


import com.framework.loippi.entity.*;
import com.framework.loippi.service.RedisService;
import com.framework.loippi.service.RoleService;
import com.framework.loippi.service.UserService;
import com.framework.loippi.shiro.AuthenticationToken;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import javax.annotation.Resource;

import java.util.Date;


/**
 * 权限认证
 *
 * @author Mounate Yan。
 * @version 1.0
 */
public class AuthenticationRealm extends AuthorizingRealm {

    @Resource
    private UserService userService;

    @Resource
    private RoleService roleService;

    @Resource
    private RedisService redisService;

    /**
     * 获取认证信息
     *
     * @param token 令牌
     * @return 认证信息
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(org.apache.shiro.authc.AuthenticationToken token) {
        AuthenticationToken authenticationToken = (AuthenticationToken) token;
        String username = authenticationToken.getUsername();
        String password = new String(authenticationToken.getPassword());
        String ip = authenticationToken.getHost();
        String captchaId = authenticationToken.getCaptchaId();
        String captcha = authenticationToken.getCaptcha().toLowerCase();
        String check = null;
        try {
            check = redisService.get(captchaId, String.class).toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!(captcha + "").equals(check)) {
            throw new UnsupportedTokenException();
        }
        if (username != null && password != null) {
            User user = null;
            try {
                user = userService.find("username", username);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (user == null) {
                throw new UnknownAccountException();
            }

            if (!user.getIsEnabled()) {
                throw new DisabledAccountException();
            }
            if (!DigestUtils.md5Hex(password).equals(user.getPassword())) {
                int loginFailureCount = user.getLoginFailureCount() + 1;
                if (loginFailureCount >= 5) {
                    user.setIsLocked(true);
                    user.setLockedDate(new Date());
                }
                user.setLoginFailureCount(loginFailureCount);
                userService.update(user);
                throw new IncorrectCredentialsException();
            }
            user.setLoginIp(ip);
            user.setLoginDate(new Date());
            user.setLoginFailureCount(0);
            userService.update(user);
            Role role = roleService.find(user.getRoleId());
            return new SimpleAuthenticationInfo(Principal.of(user.getId(), StringUtils.isEmpty(user.getNickname()) ? username : user.getNickname(),
                    role.getName(), user.getAvatar()), password, getName());
        }
        throw new UnknownAccountException();
    }


    /**
     * 获取授权信息
     *
     * @param principals principals
     * @return 授权信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        Principal principal = (Principal) principals.fromRealm(getName()).iterator().next();
        if (principal != null) {
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            User user = userService.find(principal.getId());
            Role role = roleService.findRoleAndAcls(user.getRoleId());
            for (Acl acl : role.getAuthorities()) {
                if (StringUtils.isNotEmpty(acl.getPermission())) {
                    info.addStringPermission(acl.getPermission());
                }
            }
            return info;
        }
        return null;
    }

}