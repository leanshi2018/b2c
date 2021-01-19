package com.framework.loippi.entity.ware;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author :zc
 * @explain 售后订单商品寄回地址表
 * @date:2021/1/14
 * @description:leanshi_member cn.leanshi.model
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shop_after_sale_address")
public class ShopAfterSaleAddress implements GenericEntity {
    private static final long serialVersionUID = 5081846432919091193L;

    /**
     * 主键自增
     */
    @Column(id = true, name = "id", updatable = false)
    private Long id;

    /**
     * 地址信息概要
     */
    @Column(name = "profile" )
    private String profile;

    /**
     * 省
     */
    @Column(name = "province" )
    private String province;

    /**
     * 市
     */
    @Column(name = "city" )
    private String city;

    /**
     * 区
     */
    @Column(name = "country" )
    private String country;

    /**
     * 地址详情
     */
    @Column(name = "detail" )
    private String detail;

    /**
     * 收件人姓名
     */
    @Column(name = "name" )
    private String name;

    /**
     * 收件人手机号码
     */
    @Column(name = "mobile" )
    private String mobile;
}
