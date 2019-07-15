package com.framework.loippi.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 身份信息
 *
 * @author Mounate Yan。
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Principal implements Serializable {

    private static final long serialVersionUID = 5798882004228239559L;

    /**
     * ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 角色
     */
    private String rolename;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 商铺id
     */
    private Long storeId;

    /**
     * 店员id
     */
    private Long sellerId;

    private Long time = System.currentTimeMillis();

    private boolean mobileLogin = false;

    /**
     * 平台用户
     *
     * @param id
     * @param username
     * @param avatar
     * @param rolename
     * @return
     */
    public static Principal of(Long id, String username, String rolename, String avatar) {
        Principal principal = new Principal();
        principal.setId(id);
        principal.setUsername(username);
        principal.setRolename(rolename);
        principal.setAvatar(avatar);
        return principal;
    }

    /**
     * 商家用户
     *
     * @param id
     * @param username
     * @param name
     * @param avatar
     * @return
     */
    public static Principal of(Long id, String username, Long sellerId, Long storeId,
                               String avatar, boolean mobileLogin) {
        Principal principal = new Principal();
        principal.setId(id);
        principal.setUsername(username);
        principal.setRolename("商家");
        principal.setStoreId(storeId);
        principal.setSellerId(sellerId);
        principal.setMobileLogin(mobileLogin);
        principal.setAvatar(avatar);
        return principal;
    }

}