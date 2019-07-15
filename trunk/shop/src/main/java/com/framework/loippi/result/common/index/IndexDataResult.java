package com.framework.loippi.result.common.index;

import com.framework.loippi.result.activity.promotion.ActivityGroupResult;

import com.framework.loippi.result.common.goods.GoodsListResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by neil on 2017/7/11.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndexDataResult {

    //促销活动
    private List<ActivityGroupResult> activityGroupInfo;

    //品牌精品
    private List<GoodsListResult> goodsListResult;

}
