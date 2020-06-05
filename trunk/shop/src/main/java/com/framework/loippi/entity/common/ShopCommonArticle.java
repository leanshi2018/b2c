package com.framework.loippi.entity.common;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Entity - 文章表
 *
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_COMMON_ARTICLE")
public class ShopCommonArticle implements GenericEntity {

    private static final long serialVersionUID = 5081846432919091193L;

    /**
     * 索引id
     */
    @Column(id = true, name = "id", updatable = false)
    private Long id;

    /**
     * 分类id
     */
    @Column(name = "ac_id")
    private Long acId;

    /**
     * 跳转链接
     */
    @Column(name = "article_url")
    private String articleUrl;

    /**
     * 是否显示，0为否，1为是，默认为1
     */
    @Column(name = "article_show")
    private Integer articleShow;

    /**
     * 排序
     */
    @Column(name = "article_sort")
    private Integer articleSort;

    /**
     * 标题
     */
    @Column(name = "article_title")
    private String articleTitle;

    /**
     * 内容
     */
    @Column(name = "article_content")
    private String articleContent;

    /**
     * 0:未删除;1.已删除
     */
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

    /**  */
    @Column(name = "article_time")
    private Date articleTime;

    @Column(name = "status")
    private Integer status;
    /**
     * 文章图片
     */
    @Column(name = "article_image")
    private String articleImage;

    /**
     * 文章视频
     */
    @Column(name = "article_video")
    private String articleVideo;
    
    /**
     * 文章摘要
     */
    @Column(name = "article_author")
    private String articleAuthor;
    /**
     * 文章是否顶置
     */
    @Column(name = "is_top")
    private Integer isTop;
    /**
     * 文章浏览量
     */
    @Column(name = "page_views")
    private Integer pageViews;

    /**
     * 喜欢数量
     */
    @Column(name = "like_num")
    private Integer likeNum;

    /**
     * 跳转路径类型
     */
    @Column(name = "page_type")
    private String pageType;

    /**
     * 跳转路径信息
     */
    @Column(name = "page_path")
    private String pagePath;

    private String acName;

    private Long[] ids;

    private String key;

}
