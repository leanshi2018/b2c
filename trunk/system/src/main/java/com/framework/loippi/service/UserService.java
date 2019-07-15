package com.framework.loippi.service;

import com.framework.loippi.entity.Principal;
import com.framework.loippi.entity.User;

/**
 * SERVICE - USER
 *
 * @author Loippi Team
 * @version 1.0
 */
public interface UserService extends GenericService<User, Long> {

    /**
     * 获取当前登录管理员
     *
     * @return 当前登录管理员, 若不存在则返回null
     */
    User getCurrent();

    /**
     * 后去登录信息
     *
     * @return
     */
    Principal getPrincipal();

    /**
     * 获取当前登录用户名
     *
     * @return 当前登录用户名, 若不存在则返回null
     */
    String getCurrentUsername();


    /**
     * 检查用户名是否存在
     */
    boolean usernameExists(String usernames);

}
