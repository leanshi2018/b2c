package com.framework.loippi.result.user;

import com.framework.loippi.consts.OrderState;
import com.framework.loippi.entity.user.*;
import com.framework.loippi.pojo.activity.PictureVio;
import com.framework.loippi.vo.order.CountOrderStatusVo;
import com.framework.loippi.vo.user.PlusProfitVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Result - plus会员页面
 *
 * @author zc
 * @date 2020/11/09
 * @description plus会员页面
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors
public class PlusCenterResult {

    /***
     * 昵称
     */
    private String nickname;

    /***
     * 头像
     */
    private String avatar;

    /***
     * 是否属于plus vip会员 0:不是plus会员 1：是plus会员
     */
    private Integer plusVip;

    /**
     * 等级
     */
    private Integer rank;

    /**
     * 等级图标
     */
    private String rankIcon;

    //等级名称
    private String gradeName;

    //会员编号
    private String mmCode;

    //已获奖励
    private BigDecimal yetReward;

    //待发放奖励
    private BigDecimal waitReward;

    //累计省钱
    private BigDecimal saveMoney;

    //邀请人数
    private Integer inviteNum;

    //plus vip奖励列表
    public List<PlusProfitVo> plusProfitVos=new ArrayList<PlusProfitVo>();

    public static PlusCenterResult build(RdMmBasicInfo shopMember, RdMmRelation rdMmRelation, RdRanks rdRanks,
                                         RdRanks rdRankVip, BigDecimal yetReward, BigDecimal waitReward, BigDecimal saveMoney, Integer inviteNum, List<PlusProfitVo> profitVos) {
        PlusCenterResult result = new PlusCenterResult();
        Optional<RdMmBasicInfo> optional = Optional.ofNullable(shopMember);
        result.setAvatar(optional.map(RdMmBasicInfo::getMmAvatar).orElse(""));
        result.setNickname(optional.map(RdMmBasicInfo::getMmNickName).orElse(""));
        result.setMmCode(optional.map(RdMmBasicInfo::getMmCode).orElse(""));
        result.setPlusVip(optional.map(RdMmBasicInfo::getPlusVip).orElse(0));
        result.setRank(rdMmRelation.getRank());
        if(rdMmRelation.getRank()==2){
            result.setGradeName(rdRankVip.getRankName());
            result.setRankIcon(rdRankVip.getRankIcon());
        }else {
            result.setGradeName(rdRanks.getRankName());
            result.setRankIcon(rdRanks.getRankIcon());
        }
        result.setYetReward(yetReward);
        result.setWaitReward(waitReward);
        result.setSaveMoney(saveMoney);
        result.setInviteNum(inviteNum);
        if(profitVos!=null&&profitVos.size()>0){
            result.setPlusProfitVos(profitVos);
        }
        return result;
    }
}

