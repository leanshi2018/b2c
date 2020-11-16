package com.framework.loippi.vo.user;

import com.framework.loippi.entity.user.PlusProfit;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * PlusProfit实体类拓展
 */
@Data
@ToString
@Accessors(chain = true)
public class PlusProfitVo extends PlusProfit {
    /**
     *会员昵称
     */
    private String mmNickName;
    /**
     * 会员头像
     */
    private String mmAvatar;
}
