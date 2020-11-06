package com.framework.loippi.enus;

/**
 * 快递公司
 *
 */
public enum ExpressCompanyType {

    shunfeng("顺丰快递");

    ExpressCompanyType(String chName) {
        this.chName = chName;
    }

    /**
     * 中文名称.
     */
    private String chName;

    /**
     * Gets the 中文名称.
     *
     * @return the 中文名称
     */
    public String getChName() {
        return chName;
    }
}
