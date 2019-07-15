package com.framework.loippi.entity.common;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Entity - 文章分类表
 *
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_COMMON_ARTICLE_CLASS")
public class ShopCommonArticleClass implements GenericEntity {

    private static final long serialVersionUID = 5081846432919091193L;

    /**
     * 索引ID
     */
    @Column(id = true, name = "id", updatable = false)
    private Long id;

    /**
     * 分类标识码
     */
    @Column(name = "ac_code")
    private String acCode;

    /**
     * 分类名称
     */
    @Column(name = "ac_name")
    private String acName;

    /**
     * 父ID
     */
    @Column(name = "ac_parent_id")
    private Long acParentId;

    /**
     * 排序
     */
    @Column(name = "ac_sort")
    private Integer acSort;

    /**  */
    @Column(name = "is_del")
    private Integer isDel;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 分类状态0:启用;1:停用
     */
    @Column(name = "ac_status")
    private Integer acStatus;

    /**
     * 图片url
     */
    @Column(name = "image_url")
    private String imageUrl;

    /**
     * 描述
     */
    @Column(name = "remarks")
    private String remarks;

    private String acNameLike;
    private String order;
    private String acParentIdStr;
    private Long noId;
    private Integer deep;
    private Long acId;
    private Integer articleType;

}
