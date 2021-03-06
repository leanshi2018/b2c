package com.framework.loippi.result.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.framework.loippi.entity.user.MemberQualification;
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
     * 会员昵称
     */
    private String memberName;
    /**
     * 会员真实姓名
     */
    private String trueName;
    /**
     * 会员头像
     */
    private String memberAvatar;
    /**
     * 会员头像颜色 0.灰色 1.橙色 2.红色 3.白色
     */
    private Integer memberAvatarColour;

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
    private BigDecimal ppv;

    /**
     * 会员累计MI值
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

    public static List<IntegrationMemberListResult> build(List<RdMmBasicInfo> shopMemberList,List<RdMmRelation> rdMmRelationList, List<RdRanks> shopMemberGradeList,Map<String,String> remarkMap) {
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
                if (!remarkMap.isEmpty()&&remarkMap.containsKey(item.getMmCode())){
                    integrationMemberListResult.setMemberName(Optional.ofNullable(remarkMap.get(item.getMmCode())).orElse(""));
                }else {
                    integrationMemberListResult.setMemberName(Optional.ofNullable(item.getMmNickName()).orElse(""));
                }

                integrationMemberListResult.setId( Optional.ofNullable(item.getMmCode()).orElse("-1"));
                integrationMemberListResult.setMemberAvatar(Optional.ofNullable(item.getMmAvatar()).orElse(""));
                integrationMemberListResult.setJoinTime(Optional.ofNullable(item.getCreationDate()).orElse(null));
                String mmCode = item.getMmCode();
                Integer mmStatus = 0;
                for (RdMmRelation rdMmRelation : rdMmRelationList) {
                    if(rdMmRelation.getMmCode().equals(mmCode)){
                        if (rdMmRelation.getMmStatus()!=null){
                            mmStatus = rdMmRelation.getMmStatus();
                        }
                        if(rdMmRelation.getRank()==2){
                            integrationMemberListResult.setGradeName(map.get(1));
                        }else {
                            integrationMemberListResult.setGradeName(map.get(rdMmRelation.getRank()));
                        }
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
                if (mmStatus!=2){
                    userIntegrationListResultList.add(integrationMemberListResult);
                }
            }
        }

        return userIntegrationListResultList;
    }
    public static List<IntegrationMemberListResult> build2(List<RdMmBasicInfo> shopMemberList, List<RdRanks> shopMemberGradeList, List<RdMmAccountLog> rdMmAccountLogList,List<RdMmRelation> rdMmRelationList,Map<String,String> remarkMap) {
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

                    if (!remarkMap.isEmpty()&&remarkMap.containsKey(item.getMmCode())){
                        integrationMemberListResult.setMemberName(Optional.ofNullable(remarkMap.get(item.getMmCode())).orElse(""));
                    }else {
                        integrationMemberListResult.setMemberName(Optional.ofNullable(item.getMmNickName()).orElse(""));
                    }
                    integrationMemberListResult.setId( Optional.ofNullable(item.getMmCode()).orElse("-1"));
                    integrationMemberListResult.setMemberAvatar(Optional.ofNullable(item.getMmAvatar()).orElse(""));
                    RdMmRelation rdMmRelation=rdMmRelationList.get(flag);
                    flag++;
                    if (map.get(Optional.ofNullable(rdMmRelation.getRank()).orElse(-1))!=null && !"".equals(map.get(Optional.ofNullable(rdMmRelation.getRank()).orElse(-1)))){
                        if(rdMmRelation.getRank()==2){
                            integrationMemberListResult.setGradeName(map.get(1));
                        }else {
                            integrationMemberListResult.setGradeName(map.get(rdMmRelation.getRank()));
                        }
                    }else{
                        integrationMemberListResult.setGradeName("");
                    }
                    integrationMemberListResult.setRaSpoStatus(Optional.ofNullable(rdMmRelation.getRaSponsorStatus()).orElse(0));
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
                    if(memberQualification.getRankAc()==2){
                        memberInfo.setGradeName(map.get(1));
                    }else {
                        memberInfo.setGradeName(map.get(memberQualification.getRankAc()));
                    }
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
                    memberInfo.setTrueName(Optional.ofNullable(rdMmBasicInfo.getMmName()).orElse(""));
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

        return userIntegrationListResultList;
    }

    public static List<IntegrationMemberListResult> build5(List<RdMmBasicInfo> rdMmBasicInfoList, List<RdMmRelation> rdMmRelationList, List<RdRanks> shopMemberGradeList,
                                                           Integer sorting, HashMap<String, BigDecimal> hashMap,List<MemberQualification> memberQualificationList,Map<String,String> remarkMap) {
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
                    if (!remarkMap.isEmpty()&&remarkMap.containsKey(rdMmBasicInfo.getMmCode())){
                        memberInfo.setMemberName(remarkMap.get(rdMmBasicInfo.getMmCode()));
                    }else {
                        memberInfo.setMemberName(rdMmBasicInfo.getMmNickName());
                    }
                    memberInfo.setMemberAvatar(rdMmBasicInfo.getMmAvatar());
                    memberInfo.setMemberMobile(rdMmBasicInfo.getMobile());
                    memberInfo.setJoinTime(rdMmBasicInfo.getCreationDate());
                    memberInfo.setTrueName(Optional.ofNullable(rdMmBasicInfo.getMmName()).orElse(""));
                }
            }
            /*if((rdMmRelation.getRank()+"")!=null){
                memberInfo.setGradeId(rdMmRelation.getRank());
                if(map.get(rdMmRelation.getRank())!=null){
                    memberInfo.setGradeName(map.get(rdMmRelation.getRank()));
                }else {
                    memberInfo.setGradeName("");
                }
            }else {
                memberInfo.setGradeId(0);
                memberInfo.setGradeName(map.get(0));
            }*/
            if (memberQualificationList.size()>0){
                for (MemberQualification qualification : memberQualificationList) {
                    if(qualification.getMCode().equals(rdMmRelation.getMmCode())){
                        //等级和等级名称
                        if(qualification.getRankP0()!=null){
                            memberInfo.setGradeId(qualification.getRankP0());
                            if (qualification.getRankP0()==0){
                                memberInfo.setGradeName("普通会员");
                            }else if (qualification.getRankP0()==1){
                                memberInfo.setGradeName("VIP会员");
                            }else if (qualification.getRankP0()==2){
                                memberInfo.setGradeName("VIP会员");
                            }else if (qualification.getRankP0()==3){
                                memberInfo.setGradeName("初级代理店");
                            }else if (qualification.getRankP0()==4){
                                memberInfo.setGradeName("一级代理店");
                            }else if (qualification.getRankP0()==5){
                                memberInfo.setGradeName("二级代理店");
                            }else if (qualification.getRankP0()==6){
                                memberInfo.setGradeName("三级代理店");
                            }else if (qualification.getRankP0()==7){
                                memberInfo.setGradeName("旗舰店");
                            }else if (qualification.getRankP0()==8){
                                memberInfo.setGradeName("高级旗舰店");
                            }else if (qualification.getRankP0()==9){
                                memberInfo.setGradeName("超级旗舰店");
                            }else {
                                memberInfo.setGradeName("普通会员");
                            }
                            /*memberInfo.setGradeId(qualification.getRankP0());
                            if(map.get(qualification.getRankP0())!=null){
                                memberInfo.setGradeName(map.get(qualification.getRankP0()));
                            }else {
                                memberInfo.setGradeName("");
                            }*/
                        }else {
                            memberInfo.setGradeId(0);
                            memberInfo.setGradeName("普通会员");
                        }
                        //头像颜色
                        //if (rdMmRelation.getRank()!=0){
                            if (qualification.getPpvqualified()==null || qualification.getHPpvQualified()==null){
                                memberInfo.setMemberAvatarColour(0);
                            }else {
                                if (qualification.getPpvqualified()!=1 && qualification.getHPpvQualified()!=1){
                                    memberInfo.setMemberAvatarColour(0);
                                }else{
                                    memberInfo.setMemberAvatarColour(0);
                                    if (qualification.getPpvqualified()==1){
                                        memberInfo.setMemberAvatarColour(1);
                                    }
                                    if (qualification.getHPpvQualified()==1){
                                        memberInfo.setMemberAvatarColour(2);
                                    }
                                }
                            }
                        //}else {
                            //memberInfo.setMemberAvatarColour(3);
                        //}

                        //当期ppv和累计ppv
                        if (qualification.getPpv()!=null){
                            memberInfo.setPpv(qualification.getPpv());
                        }else {
                            memberInfo.setPpv(new BigDecimal("0.00"));
                        }
                        if (qualification.getAppvFinal()!=null){
                            memberInfo.setTotalPv(qualification.getAppvFinal());
                        }else {
                            memberInfo.setTotalPv(new BigDecimal("0.00"));
                        }

                    }
                }
                //全都没找到的话
                if (memberInfo.getMemberAvatarColour()==null){
                    memberInfo.setGradeId(0);
                    memberInfo.setGradeName("普通会员");
                    memberInfo.setMemberAvatarColour(3);
                    memberInfo.setPpv(new BigDecimal("0.00"));
                    memberInfo.setTotalPv(new BigDecimal("0.00"));
                }
            }else {
                memberInfo.setGradeId(0);
                memberInfo.setGradeName("普通会员");
                memberInfo.setMemberAvatarColour(3);
                memberInfo.setPpv(new BigDecimal("0.00"));
                memberInfo.setTotalPv(new BigDecimal("0.00"));
            }
            //memberInfo.setTotalPv(Optional.ofNullable(memberQualification.getPpv()).orElse(BigDecimal.ZERO));
            BigDecimal decimal = hashMap.get(rdMmRelation.getMmCode());
            memberInfo.setRetailProfit(decimal);
            userIntegrationListResultList.add(memberInfo);
            //对集合进行排序
            if(sorting.equals(1)){
                Collections.sort(userIntegrationListResultList, new Comparator<IntegrationMemberListResult>() {
                    @Override
                    public int compare(IntegrationMemberListResult o1, IntegrationMemberListResult o2) {
                        if(o1.getPpv().compareTo(o2.getPpv())==1){
                            return 1;
                        }else if(o1.getPpv().compareTo(o2.getPpv())==0){
                            return 0;
                        }
                        return -1;
                    }
                });
            }
            if(sorting.equals(2)){
                Collections.sort(userIntegrationListResultList, new Comparator<IntegrationMemberListResult>() {
                    @Override
                    public int compare(IntegrationMemberListResult o1, IntegrationMemberListResult o2) {
                        if(o1.getPpv().compareTo(o2.getPpv())==1){
                            return -1;
                        }else if(o1.getPpv().compareTo(o2.getPpv())==0){
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
