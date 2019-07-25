package com.framework.loippi.result.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.framework.loippi.entity.user.RdMmAccountLog;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.entity.user.RdRanks;

/**
 *
 *
 * @author Loippi team
 * @version 2.0
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntegrationMemberListResult {
    /**
     * 会员编号
     */
    private String id;

    /**
     * 会员名称
     */
    private String memberName;
    /**
     * 会员头像
     */
    private String memberAvatar;
    //等级名称
    private String gradeName;
    /**
     * 手机号
     */
    private String memberMobile;

    public static List<IntegrationMemberListResult> build(List<RdMmBasicInfo> shopMemberList,List<RdMmRelation> rdMmRelationList, List<RdRanks> shopMemberGradeList) {
        List<IntegrationMemberListResult> userIntegrationListResultList=new ArrayList<>();
        Map<Integer,String> map=new HashMap<>();
        for (RdRanks item:shopMemberGradeList) {
            map.put(item.getRankId(),item.getRankName());
        }
        if (shopMemberList!=null && shopMemberList.size()>0){
            Integer flag=0;
            for (RdMmBasicInfo item:shopMemberList) {
                IntegrationMemberListResult integrationMemberListResult=new IntegrationMemberListResult();
                integrationMemberListResult.setMemberMobile(Optional.ofNullable(item.getMobile()).orElse(""));
                integrationMemberListResult.setMemberName(Optional.ofNullable(item.getMmNickName()).orElse(""));
                integrationMemberListResult.setId( Optional.ofNullable(item.getMmCode()).orElse("-1"));
                integrationMemberListResult.setMemberAvatar(Optional.ofNullable(item.getMmAvatar()).orElse(""));
                RdMmRelation rdMmRelation=rdMmRelationList.get(flag);
                flag++;
                if (map.get(Optional.ofNullable(rdMmRelation.getRank()).orElse(-1))!=null && !"".equals(map.get(Optional.ofNullable(rdMmRelation.getRank()).orElse(-1)))){
                    integrationMemberListResult.setGradeName(map.get(rdMmRelation.getRank()));
                }else{
                    integrationMemberListResult.setGradeName("");
                }
                userIntegrationListResultList.add(integrationMemberListResult);
            }
        }

        return userIntegrationListResultList;
    }
    public static List<IntegrationMemberListResult> build2(List<RdMmBasicInfo> shopMemberList, List<RdRanks> shopMemberGradeList, List<RdMmAccountLog> rdMmAccountLogList,List<RdMmRelation> rdMmRelationList) {
        List<IntegrationMemberListResult> userIntegrationListResultList=new ArrayList<>();
        Map<Integer,String> map=new HashMap<>();
        Map<String,String> RdMmAccountLogMap=new HashMap<>();
        for (RdRanks item:shopMemberGradeList) {
            map.put(item.getRankId(),item.getRankName());
        }
        for (RdMmAccountLog item:rdMmAccountLogList) {
            RdMmAccountLogMap.put(item.getTrMmCode(),item.getTransTypeCode());
        }
        if (shopMemberList!=null && shopMemberList.size()>0){
            Integer flag=0;
            for (RdMmBasicInfo item:shopMemberList) {
                if (RdMmAccountLogMap.get(item.getMmCode())!=null && !"".equals(RdMmAccountLogMap.get(item.getMmCode()))){
                    IntegrationMemberListResult integrationMemberListResult=new IntegrationMemberListResult();
                    integrationMemberListResult.setMemberMobile(Optional.ofNullable(item.getMobile()).orElse(""));
                    integrationMemberListResult.setMemberName(Optional.ofNullable(item.getMmNickName()).orElse(""));
                    integrationMemberListResult.setId( Optional.ofNullable(item.getMmCode()).orElse("-1"));
                    integrationMemberListResult.setMemberAvatar(Optional.ofNullable(item.getMmAvatar()).orElse(""));
                    RdMmRelation rdMmRelation=rdMmRelationList.get(flag);
                    flag++;
                    if (map.get(Optional.ofNullable(rdMmRelation.getRank()).orElse(-1))!=null && !"".equals(map.get(Optional.ofNullable(rdMmRelation.getRank()).orElse(-1)))){
                        integrationMemberListResult.setGradeName(map.get(rdMmRelation.getRank()));
                    }else{
                        integrationMemberListResult.setGradeName("");
                    }
                    userIntegrationListResultList.add(integrationMemberListResult);
                }
            }
        }

        return userIntegrationListResultList;
    }

//    public static List<IntegrationMemberListResult> build3(List<ShopMember> shopMemberList,List<ShopMemberGrade> shopMemberGradeList) {
//        List<IntegrationMemberListResult> integrationMemberListResultList=new ArrayList<>();
//
//        Map<Long,String> map=new HashMap<>();
//        for (ShopMemberGrade item:shopMemberGradeList) {
//            map.put(item.getId(),item.getGradeName());
//        }
//        if (shopMemberList!=null && shopMemberList.size()>0){
//            for (ShopMember item:shopMemberList) {
//                IntegrationMemberListResult integrationMemberListResult=new IntegrationMemberListResult();
//                integrationMemberListResult.setMemberMobile(Optional.ofNullable(item.getMemberMobile()).orElse(""));
//                integrationMemberListResult.setMemberName(Optional.ofNullable(item.getMemberName()).orElse(""));
//                integrationMemberListResult.setId( Optional.ofNullable(item.getId()).orElse(-1L));
//                integrationMemberListResult.setMemberAvatar(Optional.ofNullable(item.getMemberAvatar()).orElse(""));
//                if (map.get(Optional.ofNullable(item.getMemberGradeid()).orElse(-1L))!=null && !"".equals(map.get(Optional.ofNullable(item.getMemberGradeid()).orElse(-1L)))){
//                    integrationMemberListResult.setGradeName(map.get(item.getMemberGradeid()));
//                }else{
//                    integrationMemberListResult.setGradeName("");
//                }
//                integrationMemberListResultList.add(integrationMemberListResult);
//            }
//        }else{
//            integrationMemberListResult.setMemberMobile("");
//            integrationMemberListResult.setMemberName("");
//            integrationMemberListResult.setId(-1L);
//            integrationMemberListResult.setMemberAvatar("");
//            integrationMemberListResult.setGradeName("");
//            integrationMemberListResultList.add(integrationMemberListResult);
//        }
//
//        return integrationMemberListResultList;
//    }
}
