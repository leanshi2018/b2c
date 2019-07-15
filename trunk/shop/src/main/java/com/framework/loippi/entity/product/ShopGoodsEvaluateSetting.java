package com.framework.loippi.entity.product;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Entity - 评价设置
 *
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_GOODS_EVALUATE")
public class ShopGoodsEvaluateSetting implements GenericEntity {

    private static final long serialVersionUID = 5081846432919091193L;

    /**
     * 是否开启评价功能（1为启用，0为禁用）
     */
    private Integer isOpenEvaluate=1;

    /**
     * 是否开启追评功能（1为启用，0为禁用）
     */
    private Integer isOpenAgainEvalute=1;

    /**
     * 发表评论权限（1为所有人，2为仅会员，3为仅购买相应商品会员）
     */
    private Integer evaluateRule=1;

    /**
     * 显示条件（1为审核后显示，0为直接显示）
     */
    private Integer showConflact=1;

    /**
     * 默认评论文字
     */
    private String defaultWolds;

    /**
     * 发布成功提示文字
     */
    private String successTipWolds;

    /**
     * 是否开启评论成功返现（1为启用，0为禁用）
     */
    private Integer isOpenReturnMoney=1;

    /**
     * 返现金额上限
     */
    private BigDecimal returnMoneyUp;

    /**
     * 返现金额下限
     */
    private BigDecimal returnMoneyDown;

}
