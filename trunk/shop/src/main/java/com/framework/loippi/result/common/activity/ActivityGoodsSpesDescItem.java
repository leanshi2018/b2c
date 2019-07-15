package com.framework.loippi.result.common.activity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author chenjx
 */
@Data
public class ActivityGoodsSpesDescItem implements Serializable {

    private static final long serialVersionUID = 2772013076399440551L;

    private String propId;

    private String propName;

    private List<SpesDescItemValue> values;

    @Data
    public static class SpesDescItemValue implements Serializable {

        private static final long serialVersionUID = -6164124326414131493L;

        /**
         * 编号
         */
        private String valueId;

        /**
         * 规格值
         */
        private String value;


    }
}
