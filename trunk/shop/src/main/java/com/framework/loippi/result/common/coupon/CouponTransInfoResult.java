package com.framework.loippi.result.common.coupon;

import com.framework.loippi.entity.activity.ShopActivity;
import com.framework.loippi.entity.coupon.Coupon;
import com.framework.loippi.entity.coupon.CouponTransLog;
import com.framework.loippi.entity.coupon.CouponUser;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.entity.user.RdRanks;
import com.framework.loippi.result.activity.ActivityDetailResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponTransInfoResult {
    //优惠券id
    private Long couponId;
    //优惠券名称
    private String couponName;
    //当前登录用户拥有指定优惠券个数
    private Integer couponNum;
    //优惠券图片
    private String image;

    private List<CouponTransHistory> couponTransHistories;

    public static CouponTransInfoResult build(Coupon coupon, CouponUser couponUser, List<RdMmBasicInfo> rdMmBasicInfoList, List<RdMmRelation> rdMmRelationList, List<RdRanks> shopMemberGradeList) {
        CouponTransInfoResult result = new CouponTransInfoResult();
        result.setCouponId(coupon.getId());
        result.setCouponName(Optional.ofNullable(coupon.getCouponName()).orElse(""));
        result.setImage(Optional.ofNullable(coupon.getImage()).orElse(""));
        result.setCouponNum(Optional.ofNullable(couponUser.getOwnNum()).orElse(0));
        Map<Integer,String> map=new HashMap<>();
        for (RdRanks item:shopMemberGradeList) {
            map.put(item.getRankId(),item.getRankName());
        }
        ArrayList<CouponTransHistory> couponTransHistories = new ArrayList<>();
        if(rdMmRelationList!=null&&rdMmRelationList.size()>0){
            for (RdMmBasicInfo rdMmBasicInfo : rdMmBasicInfoList) {
                CouponTransHistory history = new CouponTransHistory();
                history.setMCode(Optional.ofNullable(rdMmBasicInfo.getMmCode()).orElse(""));
                history.setImage(Optional.ofNullable(rdMmBasicInfo.getMmAvatar()).orElse(""));
                history.setMobile(Optional.ofNullable(rdMmBasicInfo.getMobile()).orElse(""));
                history.setNickName(Optional.ofNullable(rdMmBasicInfo.getMmNickName()).orElse(""));
                for (RdMmRelation rdMmRelation : rdMmRelationList) {
                    if (rdMmRelation.getMmCode().equals(rdMmBasicInfo.getMmCode())){
                        String gradeName = map.get(rdMmRelation.getRank());
                        history.setGradeName(gradeName);
                        break;
                    }
                }
                couponTransHistories.add(history);
            }
            result.setCouponTransHistories(couponTransHistories);
        }
        return result;
    }
}
