package com.framework.loippi.vo.goods;


import lombok.Data;
import lombok.ToString;

/**
 * 2015年06月29日15:49:59
 * @author cgl
 * 这个实体类不用于存数据
 */
@Data
@ToString
public class GoodsAttrVo {
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
