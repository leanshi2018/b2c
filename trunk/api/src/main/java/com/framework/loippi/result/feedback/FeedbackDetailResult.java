package com.framework.loippi.result.feedback;

import com.framework.loippi.entity.common.ShopCommonFeedback;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.support.Page;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by neil on 2017/7/13.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackDetailResult {

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

    /**
     * 内容
     */
    private String content;

    /**
     * 回复内容
     */
    private String replyContent;

    public static FeedbackDetailResult buildList(ShopCommonFeedback shopCommonFeedback) {
                    FeedbackDetailResult feedbackListResult=new FeedbackDetailResult();
                    feedbackListResult.setId(shopCommonFeedback.getId());
                    feedbackListResult.setCreateTime(shopCommonFeedback.getCreateTime());
                    feedbackListResult.setTitle(shopCommonFeedback.getTitle());
                    feedbackListResult.setStatus(shopCommonFeedback.getStatus());
                    feedbackListResult.setContent(shopCommonFeedback.getContent());
                    feedbackListResult.setReplyContent(Optional.ofNullable(shopCommonFeedback.getReplyContent()).orElse(""));
        return feedbackListResult;

    }
}
