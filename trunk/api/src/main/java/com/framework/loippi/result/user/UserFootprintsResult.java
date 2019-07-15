package com.framework.loippi.result.user;

import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsBrowse;
import com.framework.loippi.entity.user.ShopMemberFavorites;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.result.auths.AuthsLoginResult;
import com.framework.loippi.utils.Dateutil;
import com.framework.loippi.vo.fav.MemberGoodsFavVo;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Administrator on 2017/6/22.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFootprintsResult {
    /**
     * 浏览时间
     */
    private String day;
    /**
     * 浏览时间
     */
    private String time;
    /**
     * 足迹商品信息
     */
    public List<UserFootprintsData> userFootprintsDataList;

    @Data
    static class UserFootprintsData {
        /**
         * 浏览记录id
         */
        public Long id;
        /**
         * 商品Id
         */
        public Long browseGoodsId;
        /**
         * 商品规格Id
         */
        public Long browseSpecId;
        /**
         * 商品名称
         */
        public String browseGoodsName;

        /**
         * 商品图片
         */
        public String browseGoodsImage;

        /**
         * 商品价格
         */
        public BigDecimal browseGoodsPrice;

        /**
         * 商品vip价格
         */
        public BigDecimal browseGoodsVipPrice;
        /**
         * 是否是套装  0不是 1是 ( 默认不是)
         */
        public Integer isSuit = 0;
        /**
         * 活动id
         */
        public Long activityId;

    }

    public static List<UserFootprintsResult> build(List<ShopGoodsBrowse> shopGoodsBrowseList, Map<Long, ShopGoods> mapGoods, AuthsLoginResult member) {
        List<UserFootprintsResult> userFootprintsResultList = new ArrayList<>();

        if (shopGoodsBrowseList != null && shopGoodsBrowseList.size() > 0) {
            String time="";
            String day = "";
            String dayStr = "";
            for (ShopGoodsBrowse shopGoodsBrowse:shopGoodsBrowseList) {
                UserFootprintsResult userFootprintsResult = new UserFootprintsResult();
                List<UserFootprintsData> userFootprintsDataList = new ArrayList<>();
                day=Dateutil.getFormatDate(shopGoodsBrowse.getCreateTime(),"MM-dd");
                if (!time.equals(day)){
                    time=day;
                    dayStr=Dateutil.getFormatDate(shopGoodsBrowse.getCreateTime(),"yyyy-MM-dd");
                    day = day.replace("-", "月");
                    day = day + "日";
                    userFootprintsResult.setDay(day);
                    userFootprintsResult.setTime(dayStr);
                }else{
                    continue;
                }
                for (int i = 0,length=shopGoodsBrowseList.size(); i <length; i++)  {
                    ShopGoodsBrowse item=shopGoodsBrowseList.get(i);
                    if (time.equals(Dateutil.getFormatDate(item.getCreateTime(),"MM-dd"))){
                        UserFootprintsData userFootprintsData = new UserFootprintsData();
                        userFootprintsData.setBrowseGoodsId(item.getBrowseGoodsId());
                        userFootprintsData.setIsSuit(0);
                        if (mapGoods.get(item.getBrowseGoodsId())!=null){
                            userFootprintsData.setBrowseGoodsImage(Optional.ofNullable(mapGoods.get(item.getBrowseGoodsId()).getGoodsImage()).orElse(""));
                            userFootprintsData.setBrowseGoodsName(Optional.ofNullable(mapGoods.get(item.getBrowseGoodsId()).getGoodsName()).orElse(""));
                            userFootprintsData.setBrowseGoodsPrice(mapGoods.get(item.getBrowseGoodsId()).getGoodsRetailPrice());
                            userFootprintsData.setBrowseGoodsVipPrice(mapGoods.get(item.getBrowseGoodsId()).getGoodsMemberPrice());
                            if (3==mapGoods.get(item.getBrowseGoodsId()).getGoodsType()){
                                userFootprintsData.setIsSuit(1);
                            }
                        }

                        userFootprintsData.setBrowseSpecId(item.getBrowseSpecId());
                        userFootprintsData.setId(item.getId());
                        userFootprintsData.setActivityId(Optional.ofNullable(item.getBrowseActivityId()).orElse(-1L));
                        userFootprintsDataList.add(userFootprintsData);
                        userFootprintsResult.setUserFootprintsDataList(userFootprintsDataList);
                    }
                }
                userFootprintsResultList.add(userFootprintsResult);
            }

        }
        return userFootprintsResultList;
    }


}
