package com.framework.loippi.entity.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.validator.constraints.Length;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;
import com.framework.loippi.utils.validator.Words;

/**
 * Entity - 订单地址信息表
 *
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_ORDER_ADDRESS")
public class ShopOrderAddress implements GenericEntity {

    private static final long serialVersionUID = 5081846432919091193L;

    /**
     * 地址ID
     */
    @Column(id = true, name = "id", updatable = false)
    private Long id;

    /**
     * 会员ID
     */
    @Column(name = "member_id")
    private Long memberId;

    /**
     * 会员姓名
     */
    @Column(name = "true_name")
    @Length(max = 50, message = "会员姓名长度必须介于0和50之间")
    @Words(field = "会员名称", message = "会员名称包含敏感词")
    private String trueName;

    /**
     * 地区ID
     */
    @Column(name = "area_id")
    private Long areaId;

    /**
     * 市级ID
     */
    @Column(name = "city_id")
    private Long cityId;

    /**
     * 地区内容
     */
    @Column(name = "area_info")
    @Length(max = 255, message = "地区内容长度必须介于0和255之间")
    @Words(field = "地区内容", message = "地区内容包含敏感词")
    private String areaInfo;

    /**
     * 地址
     */
    @Column(name = "address")
    @Length(max = 255, message = "地址长度必须介于0和255之间")
    @Words(field = "地址", message = "地址包含敏感词")
    private String address;

    /**
     * 座机电话
     */
    @Column(name = "tel_phone")
    @Length(max = 20, message = "座机电话长度必须介于0和20之间")
    @Words(field = "座机电话", message = "座机电话包含敏感词")
    private String telPhone;

    /**
     * 手机电话
     */
    @Column(name = "mob_phone")
    @Length(max = 15, message = "手机电话长度必须介于0和15之间")
    @Words(field = "手机电话", message = "手机电话包含敏感词")
    private String mobPhone;

    /**
     * 1默认收货地址
     */
    @Column(name = "is_default")
    private String isDefault;

    /**
     * 省级id
     */
    @Column(name = "province_id")
    private Long provinceId;

    /**
     * 邮编
     */
    @Column(name = "zip_code")
    private String zipCode;

    /**
     * 自提地址id
     */
    @Column(name = "mention_id")
    private Long mentionId;
}
