package com.framework.loippi.dto;

import lombok.Data;
import lombok.ToString;

/**
 * Created by longbh on 2017/7/22.
 */
@Data
@ToString
public class TypeExcel {

    /**
     * 类型名称
     */
    private String typeName;
    /**
     * 属性名称
     */
    private String attributeName;
    /**
     * 属性可选值
     */
    private String attributeValue;
    /**
     * 属性可选值RU
     */
    private String attributeValueRU;

}
