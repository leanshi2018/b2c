package com.framework.loippi.vo.activity;

import com.framework.loippi.entity.activity.ShopActivity;
import lombok.Data;

/**
 * 促销活动
 */
@Data
public class ShopActivityZhuanChangVo extends ShopActivity {

    private Integer  isPartZhuanChang;//是否参与了促销活动  0没参与  1参与

}
