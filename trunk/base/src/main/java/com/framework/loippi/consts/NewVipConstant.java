package com.framework.loippi.consts;

public interface NewVipConstant {
    //达到升级VIP 所需购买金额
    int NEW_VIP_CONDITIONS_TOTAL=360;
    //达到升级代理 所需购买的ppv
    int NEW_AGENCY_CONDITIONS_TOTAL=100;
    //直接推荐人级别为VIP会员奖100
    String FIRST_D1_BONUS="100";
    //直接推荐人为代理会员奖150
    String FIRST_D2_BONUS="150";
    //直接推荐人等级大于等于初级代理店奖200
    String FIRST_RE_D3_BONUS="200";
    //如果直接推荐人为D1间接推荐人为D2,间接推荐人奖励50
    String FIRST_D1_SECOND_D2_BONUS="50";
    //如果直接推荐人为D1间接推荐人为D2，且间接推荐人的推荐人大于等于D3,间接推荐人的推荐人奖励50
    String FIRST_D1_SECOND_D2_THIRD_RE_D3_BONUS="50";
    //如果直接推荐人为D1间接推荐人大于等于D3,间接推荐人奖励100
    String FIRST_D1_SECOND_RE_D3_BONUS="100";
    //如果直接推荐人为D2间接推荐人大于等于D3,间接推荐人奖励50
    String FIRST_D2_SECOND_RE_D3_BONUS="50";
}
