package com.framework.loippi.vo.goods;


import com.framework.loippi.mybatis.ext.annotation.Column;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 2015年06月29日15:49:59
 * @author cgl
 * 这个实体类不用于存数据
 */
@Data
@ToString
public class GoodsExcel {
    /**
     * 商品索引id   主键
     */

    private Long id;

    /**
     * 商品名称
     */

    private String goodsName;

    /**
     * 商品主图片  商品默认封面图片
     */

    private String goodsImage;

    /**
     * 条形码
     */
    private String specBarCode;
    /**
     * 商品独立二维码
     */

    private String qrCode;

    /**
     * 商品分类名称
     */

    private String gcName;
    /**
     * 分组名
     */

    private String  groupName;

    /**
     * 商品品牌名称
     */

    private String brandName;
    /**
     * 商品属性  新品  热品   普通
     */

    private String goodsAttrs;
    /**
     * 售出数量
     */

    private Integer salenum;

    /**
     * 市场价
     */

    private BigDecimal goodsMarketPrice;

    /**
     * 商品店铺价格  销售价格
     */

    private BigDecimal goodsStorePrice;

    /**
     * 税率
     */

    private BigDecimal tax;
    /**
     * 折扣率
     */

    private BigDecimal discount;
    /**
     * 返佣比率
     */

    private BigDecimal rebate;
    /**
     * 最大使用红包值
     */

    private BigDecimal redPacketUsedMax;
    /**
     * 排序
     */

    private Integer sort;

    /** 更新时间 */

    private Date updateTime;
    private Long     gcId;;

}
