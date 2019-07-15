package com.framework.loippi.service.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.framework.loippi.dao.UserDao;
import com.framework.loippi.entity.Principal;
import com.framework.loippi.entity.User;
import com.framework.loippi.service.UserService;
import com.framework.loippi.utils.Paramap;

/**
 * Service - 管理员
 *
 * @author Loippi Team
 * @version 1.0
 */
@Service("userServiceImpl")
public class UserServiceImpl extends GenericServiceImpl<User, Long> implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    public void setGenericDao() {
        super.setGenericDao(userDao);
    }

    @Transactional(readOnly = true)
    public User getCurrent() {
        Subject subject = SecurityUtils.getSubject();
        if (subject != null) {
            Principal principal = (Principal) subject.getPrincipal();
            if (principal != null && principal.getId() != null) {
                return userDao.find(principal.getId());
            }
        }
        return null;
    }

    public Principal getPrincipal() {
        Subject subject = SecurityUtils.getSubject();
        if (subject != null) {
            Principal principal = (Principal) subject.getPrincipal();
            return principal;
        }
        return null;
    }

    @Transactional(readOnly = true)
    public String getCurrentUsername() {
        Subject subject = SecurityUtils.getSubject();
        if (subject != null) {
            Principal principal = (Principal) subject.getPrincipal();
            if (principal != null) {
                return principal.getUsername();
            }
        }
        return null;
    }

    public boolean usernameExists(String usernames) {
        List<User> users = userDao.findByParams(Paramap.create().put("username", usernames));
        return CollectionUtils.isNotEmpty(users);
    }


}
