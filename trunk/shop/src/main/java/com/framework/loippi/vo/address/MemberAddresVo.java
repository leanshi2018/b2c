package com.framework.loippi.vo.address;

import lombok.Data;

/**
 * 会员地址信息
 * Created by Administrator on 2017/6/30.
 */
@Data
public class MemberAddresVo {
    /**
     * 地区id
     */
    private Long areaId;
    /**
     * 地区名
     */
    private String areaName;
    /**
     * 市区id
     */
    private Long cityId;
    /**
     * 市区名
     */
    private String cityName;
    /**
     * 省级id
     */
    private Long provinceId;
    /**
     * 省级名
     */
    private String provinceName;
}
