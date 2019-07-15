package com.framework.loippi.entity.user;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;


/**
 * 会员等级
 * cgl
 * 2015年08月24日15:40:22
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_MEMBER_GRADE")
public class MemberGrade implements GenericEntity {

    private static final long serialVersionUID = -8426339414555997935L;

    /**
     * 索引id
     */
    @Column(name="id")
    public String gradeId;

    /**
     * 等级名称
     */
    @Length(max = 10, message = "等级名称长度必须在0和10之间")
    @Column(name="grade_name")
    public String gradeName;

    /**
     * 所需积分
     */
    @Column(name="integration")
    public Integer integration;

    /**
     * 等级所对应的图片
     */
    @Length(max = 100, message = "图片路径长度必须在0和100之间")
    @Column(name="grade_img")
    public String gradeImg;

    /**
     * 会员有效期
     */
    @Length(max = 100, message = "会员有效期长度必须在0和10之间")
    @Column(name="deadline")
    public String deadline;

    /**
     * 优惠百分比
     */
    public Integer preferential;

    /**
     * 是否是默认的
     */
    public Integer isDefault;
}

