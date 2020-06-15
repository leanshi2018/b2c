package com.framework.loippi.pojo.selfMention;

import lombok.Data;

/**
 * 查询指定仓库商品种类（及商品规则数量）
 */
@Data
public class GoodsType {
    /**
     * 商品编号
     */
    private String goodsCode;

    /**
     * 商品名称
     */
    private String goodsName;
    /**
     * 仓库编号
     */
    private String wareCode;

    /**
     * 仓库名称
     */
    private String wareName;

    /**
     * 商品规格数量
     */
    private Integer specNum;
}
