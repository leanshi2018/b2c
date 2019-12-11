package com.framework.loippi.result.auths;

import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.utils.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

/**
 * Result - 手机登录
 *
 * @author Loippi team
 * @version 2.0
 * @description 手机登录
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthsLoginResult {

    // 会员编号
    private String mmCode;

    // 会员编号
    private String userCode;

    // sessionid
    private String sessionid;

    // 昵称
    private String nickname;

    //头像
    private String avatar;

    // 账号
    private String account;

    //性别 0-男 1-女
    private Integer sex;

    // 生日
    private String birthday;

    //用户id
    private String userId;

    // 手机
    private String mobile;

    //是否显示ppv 0 不显示 1 显示
    private Integer lookPpv;
    //是否显示vip价格 0 不显示 1 显示
    private Integer lookVip;
    //会员等级id
    private Integer rankId;
    //是否赠送优惠券 0 否  1 是
    private Integer getCouponFlag;

    public static AuthsLoginResult of(RdMmBasicInfo account, AuthsLoginResult session, String preUrl) {
        if (account == null) {
            return null;
        }
        AuthsLoginResult result = new AuthsLoginResult();
        result.setMobile(account.getMobile());
        result.setAccount(account.getMmName());
        result.setAvatar(StringUtil.formatImg(preUrl, account.getMmAvatar()));
        if (account.getBirthdate() != null) {
            result.setBirthday(new SimpleDateFormat("yyyy-MM-dd").format(account.getBirthdate()));
        } else {
            result.setBirthday("");
        }
        result.setNickname(account.getMmNickName());
        result.setSessionid(session.getSessionid());
        result.setLookVip(session.getLookVip());
        result.setLookPpv(session.getLookPpv());
        result.setRankId(session.getRankId());
        result.setSex(account.getGender());
        result.setUserId(account.getMmCode());
        result.setMmCode(account.getMmCode());
        result.setUserCode(account.getMmCode());
        return result;
    }

}

