package com.framework.loippi.result.feedback;

import com.framework.loippi.entity.common.ShopCommonFeedback;
import com.framework.loippi.entity.order.ShopOrder;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.result.order.ShippingResult;
import com.framework.loippi.support.Page;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by neil on 2017/7/13.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackListResult {

    /**  */
    private Long id;

    /** 标题 */
    private String title;

    /** 创建时间 */
    private java.util.Date createTime;

    /**
     * 1-已处理 2-未处理
     */
    private Integer status;

    public static List<FeedbackListResult> buildList(Page<ShopCommonFeedback> shopCommonFeedbackPage) {
        List<FeedbackListResult> feedbackListResultList=new ArrayList<>();
        if (shopCommonFeedbackPage!=null && shopCommonFeedbackPage.getContent()!=null){
            List<ShopCommonFeedback> shopCommonFeedbackList=shopCommonFeedbackPage.getContent();
            if (shopCommonFeedbackList!=null && shopCommonFeedbackList.size()>0){
                for (ShopCommonFeedback item:shopCommonFeedbackList) {
                    FeedbackListResult feedbackListResult=new FeedbackListResult();
                    feedbackListResult.setId(item.getId());
                    feedbackListResult.setCreateTime(item.getCreateTime());
                    feedbackListResult.setTitle(item.getTitle());
                    feedbackListResult.setStatus(item.getStatus());
                    feedbackListResultList.add(feedbackListResult);
                }
            }
        }
        return feedbackListResultList;

    }
}
