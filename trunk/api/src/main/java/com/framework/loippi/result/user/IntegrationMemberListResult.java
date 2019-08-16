package com.framework.loippi.result.user;

import com.framework.loippi.entity.user.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.*;

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
    /**
     * 与推荐人的关系  0：临时绑定  1：永久绑定
     */
    private Integer raSpoStatus;

    /**
     * 会员加入时间
     */
    private Date joinTime;

    /**
     * 当期会员MI值
     */
    private BigDecimal totalPv;

    /**
     * 会员等级
     */
    private Integer gradeId;
    /**
     * 对应于当前手机用户已获得的零售利润
     */
    private BigDecimal retailProfit;

    public static List<IntegrationMemberListResult> build(List<RdMmBasicInfo> shopMemberList,List<RdMmRelation> rdMmRelationList, List<RdRanks> shopMemberGradeList) {
        List<IntegrationMemberListResult> userIntegrationListResultList=new ArrayList<>();
        Map<Integer,String> map=new HashMap<>();
        for (RdRanks item:shopMemberGradeList) {
            map.put(item.getRankId(),item.getRankName());
        }
        if (shopMemberList!=null && shopMemberList.size()>0){
            //Integer flag=0;
            for (RdMmBasicInfo item:shopMemberList) {
                IntegrationMemberListResult integrationMemberListResult=new IntegrationMemberListResult();
                integrationMemberListResult.setMemberMobile(Optional.ofNullable(item.getMobile()).orElse(""));
                integrationMemberListResult.setMemberName(Optional.ofNullable(item.getMmNickName()).orElse(""));
                integrationMemberListResult.setId( Optional.ofNullable(item.getMmCode()).orElse("-1"));
                integrationMemberListResult.setMemberAvatar(Optional.ofNullable(item.getMmAvatar()).orElse(""));
                String mmCode = item.getMmCode();
                for (RdMmRelation rdMmRelation : rdMmRelationList) {
                    if(rdMmRelation.getMmCode().equals(mmCode)){
                        integrationMemberListResult.setGradeName(map.get(rdMmRelation.getRank()));
                        integrationMemberListResult.setRaSpoStatus(rdMmRelation.getRaSponsorStatus());
                        break;
                    }
                    integrationMemberListResult.setGradeName("");
                }
/*                RdMmRelation rdMmRelation=rdMmRelationList.get(flag);
                flag++;
                if (map.get(Optional.ofNullable(rdMmRelation.getRank()).orElse(-1))!=null && !"".equals(map.get(Optional.ofNullable(rdMmRelation.getRank()).orElse(-1)))){
                    integrationMemberListResult.setGradeName(map.get(rdMmRelation.getRank()));
                }else{
                    integrationMemberListResult.setGradeName("");
                }*/
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

    public static List<IntegrationMemberListResult> build3(List<MemberQualification> list, List<RdMmBasicInfo> rdMmBasicInfoList, List<RdMmRelation> rdMmRelationList, List<RdRanks> shopMemberGradeList, Integer sorting,HashMap<String, BigDecimal> hashMap) {
        List<IntegrationMemberListResult> userIntegrationListResultList=new ArrayList<>();
        Map<Integer,String> map=new HashMap<>();
        for (RdRanks item:shopMemberGradeList) {
            map.put(item.getRankId(),item.getRankName());
        }
        for (MemberQualification memberQualification : list) {
            IntegrationMemberListResult memberInfo = new IntegrationMemberListResult();
            memberInfo.setId(memberQualification.getMCode());
            for (RdMmBasicInfo rdMmBasicInfo : rdMmBasicInfoList) {
                if(rdMmBasicInfo.getMmCode().equals(memberQualification.getMCode())){
                    memberInfo.setMemberName(rdMmBasicInfo.getMmNickName());
                    memberInfo.setMemberAvatar(rdMmBasicInfo.getMmAvatar());
                    memberInfo.setMemberMobile(rdMmBasicInfo.getMobile());
                    memberInfo.setJoinTime(rdMmBasicInfo.getCreationDate());
                }
            }
            for (RdMmRelation rdMmRelation : rdMmRelationList) {
                if(rdMmRelation.getMmCode().equals(memberQualification.getMCode())){
                    memberInfo.setRaSpoStatus(rdMmRelation.getRaSponsorStatus());
                }
            }
            memberInfo.setTotalPv(Optional.ofNullable(memberQualification.getPpv()).orElse(BigDecimal.ZERO));
            if((memberQualification.getRankAc()+"")!=null){
                memberInfo.setGradeId(memberQualification.getRankAc());
                if(map.get(memberQualification.getRankAc())!=null){
                    memberInfo.setGradeName(map.get(memberQualification.getRankAc()));
                }else {
                    memberInfo.setGradeName("");
                }
            }else {
                memberInfo.setGradeId(0);
                memberInfo.setGradeName(map.get(0));
            }
            BigDecimal decimal = hashMap.get(memberQualification.getMCode());
            memberInfo.setRetailProfit(decimal);
            userIntegrationListResultList.add(memberInfo);
        }
        //对集合进行排序
        //按照mi值升序
        if(sorting.equals(1)){
            Collections.sort(userIntegrationListResultList, new Comparator<IntegrationMemberListResult>() {
                @Override
                public int compare(IntegrationMemberListResult o1, IntegrationMemberListResult o2) {
                    if(o1.getTotalPv().compareTo(o2.getTotalPv())==1){
                        return 1;
                    }else if(o1.getTotalPv().compareTo(o2.getTotalPv())==0){
                        return 0;
                    }
                    return -1;
                }
            });
        }
        //按照mi值降序
        if(sorting.equals(2)){
            Collections.sort(userIntegrationListResultList, new Comparator<IntegrationMemberListResult>() {
                @Override
                public int compare(IntegrationMemberListResult o1, IntegrationMemberListResult o2) {
                    if(o1.getTotalPv().compareTo(o2.getTotalPv())==1){
                        return -1;
                    }else if(o1.getTotalPv().compareTo(o2.getTotalPv())==0){
                        return 0;
                    }
                    return 1;
                }
            });
        }
        //按照加入时间升序
        if(sorting.equals(3)){
            Collections.sort(userIntegrationListResultList, new Comparator<IntegrationMemberListResult>() {
                @Override
                public int compare(IntegrationMemberListResult o1, IntegrationMemberListResult o2) {
                    if(o1.getJoinTime().compareTo(o2.getJoinTime())==1){
                        return 1;
                    }else if(o1.getJoinTime().compareTo(o2.getJoinTime())==0){
                        return 0;
                    }
                    return -1;
                }
            });
        }
        //按照加入时间降序
        if(sorting.equals(4)){
            Collections.sort(userIntegrationListResultList, new Comparator<IntegrationMemberListResult>() {
                @Override
                public int compare(IntegrationMemberListResult o1, IntegrationMemberListResult o2) {
                    if(o1.getJoinTime().compareTo(o2.getJoinTime())==1){
                        return -1;
                    }else if(o1.getJoinTime().compareTo(o2.getJoinTime())==0){
                        return 0;
                    }
                    return 1;
                }
            });
        }
        //按照级别升序
        if(sorting.equals(5)){
            Collections.sort(userIntegrationListResultList, new Comparator<IntegrationMemberListResult>() {
                @Override
                public int compare(IntegrationMemberListResult o1, IntegrationMemberListResult o2) {
                    if(o1.getGradeId()>o2.getGradeId()){
                        return 1;
                    }else if(o1.getGradeId()==o2.getGradeId()){
                        return 0;
                    }
                    return -1;
                }
            });
        }
        //按照级别降序
        if(sorting.equals(6)){
            Collections.sort(userIntegrationListResultList, new Comparator<IntegrationMemberListResult>() {
                @Override
                public int compare(IntegrationMemberListResult o1, IntegrationMemberListResult o2) {
                    if(o1.getGradeId()>o2.getGradeId()){
                        return -1;
                    }else if(o1.getGradeId()==o2.getGradeId()){
                        return 0;
                    }
                    return 1;
                }
            });
        }
        //按照已发放零售利润升序
        if(sorting.equals(7)){
            Collections.sort(userIntegrationListResultList, new Comparator<IntegrationMemberListResult>() {
                @Override
                public int compare(IntegrationMemberListResult o1, IntegrationMemberListResult o2) {
                    if(o1.getRetailProfit().compareTo(o2.getRetailProfit())==1){
                        return 1;
                    }else if(o1.getRetailProfit().compareTo(o2.getRetailProfit())==0){
                        return 0;
                    }
                    return -1;
                }
            });
        }
        //按照已发放零售利润降序
        if(sorting.equals(8)){
            Collections.sort(userIntegrationListResultList, new Comparator<IntegrationMemberListResult>() {
                @Override
                public int compare(IntegrationMemberListResult o1, IntegrationMemberListResult o2) {
                    if(o1.getRetailProfit().compareTo(o2.getRetailProfit())==1){
                        return -1;
                    }else if(o1.getRetailProfit().compareTo(o2.getRetailProfit())==0){
                        return 0;
                    }
                    return 1;
                }
            });
        }
        return userIntegrationListResultList;
    }

    public static List<IntegrationMemberListResult> build4(List<RdMmBasicInfo> rdMmBasicInfoList, List<RdMmRelation> rdMmRelationList, List<RdRanks> shopMemberGradeList, Integer sorting, HashMap<String, BigDecimal> hashMap) {
        List<IntegrationMemberListResult> userIntegrationListResultList=new ArrayList<>();
        Map<Integer,String> map=new HashMap<>();
        for (RdRanks item:shopMemberGradeList) {
            map.put(item.getRankId(),item.getRankName());
        }
        for (RdMmRelation rdMmRelation : rdMmRelationList) {
            IntegrationMemberListResult memberInfo = new IntegrationMemberListResult();
            memberInfo.setId(rdMmRelation.getMmCode());
            memberInfo.setRaSpoStatus(rdMmRelation.getRaSponsorStatus());
            for (RdMmBasicInfo rdMmBasicInfo : rdMmBasicInfoList) {
                if(rdMmBasicInfo.getMmCode().equals(rdMmRelation.getMmCode())){
                    memberInfo.setMemberName(rdMmBasicInfo.getMmNickName());
                    memberInfo.setMemberAvatar(rdMmBasicInfo.getMmAvatar());
                    memberInfo.setMemberMobile(rdMmBasicInfo.getMobile());
                    memberInfo.setJoinTime(rdMmBasicInfo.getCreationDate());
                }
            }
            if((rdMmRelation.getRank()+"")!=null){
                memberInfo.setGradeId(rdMmRelation.getRank());
                if(map.get(rdMmRelation.getRank())!=null){
                    memberInfo.setGradeName(map.get(rdMmRelation.getRank()));
                }else {
                    memberInfo.setGradeName("");
                }
            }else {
                memberInfo.setGradeId(0);
                memberInfo.setGradeName(map.get(0));
            }
            //memberInfo.setTotalPv(Optional.ofNullable(memberQualification.getPpv()).orElse(BigDecimal.ZERO));
            BigDecimal decimal = hashMap.get(rdMmRelation.getMmCode());
            memberInfo.setRetailProfit(decimal);
            userIntegrationListResultList.add(memberInfo);
            //对集合进行排序


            //按照加入时间升序
            if(sorting.equals(3)){
                Collections.sort(userIntegrationListResultList, new Comparator<IntegrationMemberListResult>() {
                    @Override
                    public int compare(IntegrationMemberListResult o1, IntegrationMemberListResult o2) {
                        if(o1.getJoinTime().compareTo(o2.getJoinTime())==1){
                            return 1;
                        }else if(o1.getJoinTime().compareTo(o2.getJoinTime())==0){
                            return 0;
                        }
                        return -1;
                    }
                });
            }
            //按照加入时间降序
            if(sorting.equals(4)){
                Collections.sort(userIntegrationListResultList, new Comparator<IntegrationMemberListResult>() {
                    @Override
                    public int compare(IntegrationMemberListResult o1, IntegrationMemberListResult o2) {
                        if(o1.getJoinTime().compareTo(o2.getJoinTime())==1){
                            return -1;
                        }else if(o1.getJoinTime().compareTo(o2.getJoinTime())==0){
                            return 0;
                        }
                        return 1;
                    }
                });
            }
            //按照级别升序
            if(sorting.equals(5)){
                Collections.sort(userIntegrationListResultList, new Comparator<IntegrationMemberListResult>() {
                    @Override
                    public int compare(IntegrationMemberListResult o1, IntegrationMemberListResult o2) {
                        if(o1.getGradeId()>o2.getGradeId()){
                            return 1;
                        }else if(o1.getGradeId()==o2.getGradeId()){
                            return 0;
                        }
                        return -1;
                    }
                });
            }
            //按照级别降序
            if(sorting.equals(6)){
                Collections.sort(userIntegrationListResultList, new Comparator<IntegrationMemberListResult>() {
                    @Override
                    public int compare(IntegrationMemberListResult o1, IntegrationMemberListResult o2) {
                        if(o1.getGradeId()>o2.getGradeId()){
                            return -1;
                        }else if(o1.getGradeId()==o2.getGradeId()){
                            return 0;
                        }
                        return 1;
                    }
                });
            }
            //按照已发放零售利润升序
            if(sorting.equals(7)){
                Collections.sort(userIntegrationListResultList, new Comparator<IntegrationMemberListResult>() {
                    @Override
                    public int compare(IntegrationMemberListResult o1, IntegrationMemberListResult o2) {
                        if(o1.getRetailProfit().compareTo(o2.getRetailProfit())==1){
                            return 1;
                        }else if(o1.getRetailProfit().compareTo(o2.getRetailProfit())==0){
                            return 0;
                        }
                        return -1;
                    }
                });
            }
            //按照已发放零售利润降序
            if(sorting.equals(8)){
                Collections.sort(userIntegrationListResultList, new Comparator<IntegrationMemberListResult>() {
                    @Override
                    public int compare(IntegrationMemberListResult o1, IntegrationMemberListResult o2) {
                        if(o1.getRetailProfit().compareTo(o2.getRetailProfit())==1){
                            return -1;
                        }else if(o1.getRetailProfit().compareTo(o2.getRetailProfit())==0){
                            return 0;
                        }
                        return 1;
                    }
                });
            }
        }

        return null;
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
