package com.framework.loippi.vo.goods;

import lombok.Data;
import lombok.ToString;

/**
 * Created by lys on 2017/10/17.
 */
@Data
@ToString
public class BrandListVo {
    /** 品牌id */
    private Long brandId;
    /** 次数 */
    private Long  countNum;

    /**
     * 2015年06月29日15:49:59
     * @author cgl
     * 这个实体类不用于存数据
     */
    @Data
    @ToString
    public static class GoodsAttrVo {
        /**
         * 属性id
         */
        private String attrId;

        /**
         * 属性名称
         */
        private String attrName;

        /**
         * 自定义属性值id
         */
        private String attrValueId;

        /**
         * 自定义属性值名称
         */
        private String attrValueName;
    }
}
