package com.framework.loippi.result.user;

import com.framework.loippi.entity.user.OldSysRelationship;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdRaBinding;
import com.framework.loippi.mybatis.ext.annotation.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Optional;

/**
 * Result - 用户资料
 *
 * @author Loippi team
 * @version 2.0
 * @description 用户资料
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResult {

    //头像
    private String avatar;
    //昵称
    private String nickname;
    //会员编号
    private String membershipNumber;

    // 手机号码
    private String mobile;

    // 性别(0:女，1:男)
    private Integer sex;

    // 生日
    private String birthday;
    // 是否绑定微信  0未绑定 1已绑定
    private Integer isBindingWeiXin;
    // 是否绑定qq    0未绑定 1已绑定
    private Integer isBindingQQ;
    /**
     * 地区内容
     */
    private String memberAreainfo;
    /**
     * 地址内容
     */
    private String memberAddress;
    //老用户昵称
    private String oldNickname;
    //老用户会员编号
    private String oldMembershipNumber;
    //真实姓名
    private String memberTruename;
    //证件类型
    private String userTypeStr;
    //证件号
    private String memberTrueid;
    // 推送状态 1 可以进行推送 2 不可以进行推送
    private Integer pushStatus;

    public static UserProfileResult build3(RdMmBasicInfo rdMmBasicInfo, OldSysRelationship oldSysRelationship) {
        Optional<RdMmBasicInfo> optional = Optional.ofNullable(rdMmBasicInfo);
        UserProfileResult result = new UserProfileResult();
        result.setAvatar(optional.map(RdMmBasicInfo::getMmAvatar).orElse(""));
        result.setNickname(optional.map(RdMmBasicInfo::getMmNickName).orElse(""));
        String m = rdMmBasicInfo.getMobile();
        if (StringUtils.isNotBlank(m) && m.length() == 11) {
            result.setMobile(m);
        } else {
            result.setMobile("");
        }
        result.setSex(optional.map(RdMmBasicInfo::getGender).orElse(0));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar defaultCalendar = Calendar.getInstance();
        defaultCalendar.set(Calendar.YEAR, 1970);
        defaultCalendar.set(Calendar.MONTH, 0);
        defaultCalendar.set(Calendar.DAY_OF_MONTH, 1);
        result.setBirthday(format.format(
                optional.map(RdMmBasicInfo::getBirthdate).orElse(defaultCalendar.getTime())));
        result.setMembershipNumber(optional.map(RdMmBasicInfo::getMmCode).orElse(""));
        if (oldSysRelationship!=null){
            result.setOldNickname(Optional.ofNullable(oldSysRelationship.getONickname()).orElse(""));
            result.setOldMembershipNumber(Optional.ofNullable(oldSysRelationship.getOMcode()).orElse(""));
        }else{
            result.setOldNickname("");
            result.setOldMembershipNumber("");
        }
        result.setMemberTruename(optional.map(RdMmBasicInfo::getMmName).orElse(""));
        result.setMemberTrueid(optional.map(RdMmBasicInfo::getIdCode).orElse(""));
        result.setMemberAreainfo(optional.map(RdMmBasicInfo::getAddProvinceId).orElse("")+optional.map(RdMmBasicInfo::getAddCityId).orElse("")+optional.map(RdMmBasicInfo::getAddCountryId).orElse(""));
        result.setMemberAddress(optional.map(RdMmBasicInfo::getAddDetial).orElse(""));
        result.setIsBindingQQ(0);
        result.setIsBindingWeiXin(0);
        result.setPushStatus(rdMmBasicInfo.getPushStatus());
        if (!"".equals(optional.map(RdMmBasicInfo::getQqCode).orElse(""))){
            result.setIsBindingQQ(1);
        }
        if (!"".equals(optional.map(RdMmBasicInfo::getWechatCode).orElse(""))){
            result.setIsBindingWeiXin(1);
        }
        if (optional.map(RdMmBasicInfo::getIdType).orElse(0)==1){
            result.setUserTypeStr("身份证");
        }
        if (optional.map(RdMmBasicInfo::getIdType).orElse(0)==2){
            result.setUserTypeStr("护照");
        }
        if (optional.map(RdMmBasicInfo::getIdType).orElse(0)==3){
            result.setUserTypeStr("军官证");
        }
        if (optional.map(RdMmBasicInfo::getIdType).orElse(0)==4){
            result.setUserTypeStr("回乡证");
        }
        if (optional.map(RdMmBasicInfo::getIdType).orElse(0)==0){
            result.setUserTypeStr("");
        }
        return result;
    }


    public static UserProfileResult build2(RdMmBasicInfo rdMmBasicInfo,RdRaBinding rdRaBinding) {
        Optional<RdMmBasicInfo> optional = Optional.ofNullable(rdMmBasicInfo);
        UserProfileResult result = new UserProfileResult();
        result.setAvatar(optional.map(RdMmBasicInfo::getMmAvatar).orElse(""));
        result.setNickname(optional.map(RdMmBasicInfo::getMmNickName).orElse(""));
        String m = rdMmBasicInfo.getMobile();
        if (StringUtils.isNotBlank(m) && m.length() == 11) {
            result.setMobile(m);
        } else {
            result.setMobile("");
        }
        result.setSex(optional.map(RdMmBasicInfo::getGender).orElse(0));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar defaultCalendar = Calendar.getInstance();
        defaultCalendar.set(Calendar.YEAR, 1970);
        defaultCalendar.set(Calendar.MONTH, 0);
        defaultCalendar.set(Calendar.DAY_OF_MONTH, 1);
        result.setBirthday(format.format(
                optional.map(RdMmBasicInfo::getBirthdate).orElse(defaultCalendar.getTime())));
        result.setMembershipNumber(optional.map(RdMmBasicInfo::getMmCode).orElse(""));
        // TODO: 2018/12/26 老用户待补齐
        if (rdRaBinding!=null){
            result.setOldNickname(Optional.ofNullable(rdRaBinding.getRaNickName()).orElse(""));
            result.setOldMembershipNumber(Optional.ofNullable(rdRaBinding.getRaCode()).orElse(""));
        }else{
            result.setOldNickname("");
            result.setOldMembershipNumber("");
        }
        result.setMemberTruename(optional.map(RdMmBasicInfo::getMmName).orElse(""));
        result.setMemberTrueid(optional.map(RdMmBasicInfo::getIdCode).orElse(""));
        result.setMemberAreainfo(optional.map(RdMmBasicInfo::getAddProvinceId).orElse("")+optional.map(RdMmBasicInfo::getAddCityId).orElse("")+optional.map(RdMmBasicInfo::getAddCountryId).orElse(""));
        result.setMemberAddress(optional.map(RdMmBasicInfo::getAddDetial).orElse(""));
        result.setIsBindingQQ(0);
        result.setIsBindingWeiXin(0);
        result.setPushStatus(rdMmBasicInfo.getPushStatus());
        if (!"".equals(optional.map(RdMmBasicInfo::getQqCode).orElse(""))){
            result.setIsBindingQQ(1);
        }
        if (!"".equals(optional.map(RdMmBasicInfo::getWechatCode).orElse(""))){
            result.setIsBindingWeiXin(1);
        }
        if (optional.map(RdMmBasicInfo::getIdType).orElse(0)==1){
            result.setUserTypeStr("身份证");
        }
        if (optional.map(RdMmBasicInfo::getIdType).orElse(0)==2){
            result.setUserTypeStr("护照");
        }
        if (optional.map(RdMmBasicInfo::getIdType).orElse(0)==3){
            result.setUserTypeStr("军官证");
        }
        if (optional.map(RdMmBasicInfo::getIdType).orElse(0)==4){
            result.setUserTypeStr("回乡证");
        }
        if (optional.map(RdMmBasicInfo::getIdType).orElse(0)==0){
            result.setUserTypeStr("");
        }
        return result;
    }


}

