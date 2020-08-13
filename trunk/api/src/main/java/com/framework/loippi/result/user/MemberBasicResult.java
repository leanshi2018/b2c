package com.framework.loippi.result.user;

import com.framework.loippi.entity.user.MemberQualification;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdMmRelation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.*;

/**
 * 会员主店次店集合
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors
public class MemberBasicResult {
    private String mmAvatar;//会员头像
    private String mmNickName;//会员昵称
    private Integer rank;//会员级别
    private String rankName;//级别名称
    private BigDecimal monthMi;//当期mi值
    private String mmCode;//会员编号
    private Integer onLineFlag;//是否登录 1：登录状态 2：未登录状态
    private Integer mainFlag;//是否为主店 1：主店 2：次店
    private String loginPwd;//加密后的密码

    public static MemberBasicResult build(RdMmBasicInfo basicInfo, RdMmRelation rdMmRelation, MemberQualification qualification,HashMap<Integer,String> rankMap,String onLineCode){
        MemberBasicResult result = new MemberBasicResult();
        Optional<RdMmBasicInfo> optional = Optional.ofNullable(basicInfo);
        Optional<RdMmRelation> optional2 = Optional.ofNullable(rdMmRelation);
        result.setMmAvatar(optional.map(RdMmBasicInfo::getMmAvatar).orElse(""));
        result.setMmNickName(optional.map(RdMmBasicInfo::getMmNickName).orElse(""));
        result.setRank(optional2.map(RdMmRelation::getRank).orElse(0));
        String rankName = rankMap.get(result.getRank());
        result.setRankName(rankName);
        if(qualification==null){
            result.setMonthMi(BigDecimal.ZERO);
        }else {
            if(qualification.getPpv()!=null){
                result.setMonthMi(qualification.getPpv());
            }else {
                result.setMonthMi(BigDecimal.ZERO);
            }
        }
        result.setMmCode(optional.map(RdMmBasicInfo::getMmCode).orElse(""));
        if(result.getMmCode().equals(onLineCode)){
            result.setOnLineFlag(1);
        }else {
            result.setOnLineFlag(2);
        }
        result.setMainFlag(optional.map(RdMmBasicInfo::getMainFlag).orElse(2));
        result.setLoginPwd(optional2.map(RdMmRelation::getLoginPwd).orElse(""));
        return result;
    }
}
