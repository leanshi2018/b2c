package com.framework.loippi.entity.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

/**
 * Entity - 快递公司
 *
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_COMMON_EXPRESS")
public class ShopCommonExpress implements GenericEntity {

    private static final long serialVersionUID = 5081846432919091193L;

    /**
     * 索引ID
     */
    @Column(id = true, name = "id", updatable = false)
    private Long id;

    /**
     * 公司名称
     */
    @Column(name = "e_name")
    private String eName;

    /**
     * 状态
     */
    @Column(name = "e_state")
    private Integer eState;

    /**
     * 编号
     */
    @Column(name = "e_code")
    private String eCode;

    /**
     * 第三方物流单号
     */
    @Column(name = "e_express_code")
    private String eExpressCode;

    /**
     * 首字母
     */
    @Column(name = "e_letter")
    private String eLetter;

    /**
     * 1常用0不常用
     */
    @Column(name = "e_order")
    private Integer eOrder;

    /**
     * 公司网址
     */
    @Column(name = "e_url")
    private String eUrl;

    /**
     * 删除标记
     */
    @Column(name = "is_del")
    private Integer isDel;

}
