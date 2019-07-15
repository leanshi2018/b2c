package com.framework.loippi.result.activity.promotion;

import com.framework.loippi.vo.activity.ActivityGoodsItemVO;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

/**
 * 促销活动组  --pc端
 */
@Data
public class ActivityGroupWebResult {

    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 促销活动图片
     */
    private String activityImage;



    /*促销活动图片PC端*/
    private String activityPicturePc;

    /**
     * 促销活动名称
     */
    private String activityName;

    /**
     * 促销所属店铺id
     */
    private Long storeId;

    /**
     * 促销活动对应的商品列表
     */
    private List<ActivityItemResult> activityGoodsResults;


    public static ActivityGroupWebResult build(ActivityGoodsItemVO item){

        Optional<ActivityGoodsItemVO> optItem = Optional.ofNullable(item);
        ActivityGroupWebResult groupResult = new ActivityGroupWebResult();
        groupResult.setActivityId(optItem.map(ActivityGoodsItemVO::getActivityId).orElse(0l));
        groupResult.setActivityImage(optItem.map(ActivityGoodsItemVO::getActivityImage).orElse(""));
        groupResult.setActivityPicturePc(optItem.map(ActivityGoodsItemVO::getActivityPicturePc).orElse(""));
        groupResult.setStoreId(optItem.map(ActivityGoodsItemVO::getStoreId).orElse(0l));
        groupResult.setActivityName(optItem.map(ActivityGoodsItemVO::getActivityName).orElse(""));
        groupResult.setActivityGoodsResults(new ArrayList<>());
        return groupResult;

    }

    public static List<ActivityGroupWebResult> buildList(List<ActivityGoodsItemVO> items) {

        if (CollectionUtils.isEmpty(items)) {
            return Collections.emptyList();
        }

        List<ActivityGroupWebResult> infos = new ArrayList<>();
        Map<Long,ActivityGroupWebResult> activityMap = new HashMap<>();
        for(ActivityGoodsItemVO item:items){
            ActivityGroupWebResult groupResult = activityMap.get(item.getActivityId());
            if(groupResult==null){
                groupResult = ActivityGroupWebResult.build(item);
                activityMap.put(item.getActivityId(),groupResult);
                infos.add(groupResult);
            }
            List<ActivityItemResult> itemResults = groupResult.getActivityGoodsResults();
            if(itemResults==null){
                itemResults = new ArrayList<>();
            }
            ActivityItemResult itemResult = ActivityItemResult.build(item);
            if(itemResult!=null){
                itemResults.add(itemResult);
            }
        }
        return infos;
    }

}
