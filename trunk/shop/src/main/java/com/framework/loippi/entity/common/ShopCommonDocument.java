package com.framework.loippi.entity.common;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Entity - 系统文章表
 *
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_COMMON_DOCUMENT")
public class ShopCommonDocument implements GenericEntity {

    private static final long serialVersionUID = 5081846432919091193L;

    /**
     * id
     */
    @Column(id = true, name = "id", updatable = false)
    private Long id;

    /**
     * 调用标识码
     */
    @Column(name = "doc_code")
    private String docCode;

    /**
     * 文章类型
     */
    @Column(name = "doc_type")
    private String docType;

    /**
     * 标题
     */
    @Column(name = "doc_title")
    private String docTitle;

    /**
     * 内容
     */
    @Column(name = "doc_content")
    private String docContent;

    /**
     * 添加时间/修改时间
     */
    @Column(name = "doc_time")
    private Date docTime;

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
     * 0:未删除;1.已删除
     */
    @Column(name = "is_del")
    private Integer isDel;

    /**
     * 文章浏览量
     */
    @Column(name = "page_views")
    private Integer pageViews;
    //查询
    private String idLike;
    private String nameLike;
    private String searchStartTime;
    private String searchEndTime;
    private String notIndocType;


}
