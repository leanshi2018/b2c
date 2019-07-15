package com.framework.loippi.vo.goods;

import java.math.BigDecimal;

import lombok.Data;

/**
 * 功能： 销售量统计
 * 类名：GoodsStatisticsVo
 * 日期：2018/4/10  12:13
 * 作者：czl
 * 详细说明：
 * 修改备注:
 */
@Data
public class GoodsStatisticsVo {

    /**
     * 商品id
     */
    private Long goodsId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 销售数量
     */
    private Integer saleNum;

    /**
     * 销售合计金额
     */
    private BigDecimal saleAmount;
    /**
     * 品牌名称
     */
    private String brandName;
    /**
     * 品牌id
     */
    private Long brandId;
    /**
     * 二级分类名称
     */
    private String gcName;
    /**
     * 二级分类id
     */
    private Long gcId;
    /**
     * 一级分类名称
     */
    private String gcParentName;
    /**
     * 一级分类id
     */
    private Long gcParentId;
    /**
     * 开始时间
     */
    private String startTime;
    /**
     * 结束时间
     */
    private String endTime;

    String goodsIdKeyWord;


}
