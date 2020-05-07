package com.framework.loippi.entity.common;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 会员等级升级规则解释
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rd_ranks_explains")
public class RankExplain implements GenericEntity {
    private static final long serialVersionUID = 5081846432919091193L;

    /** 主键id */
    @Column(name = "id" )
    private Long id;

    /** 等级 */
    @Column(name = "rank" )
    private Integer rank;

    /** 升级规则解释 */
    @Column(name = "message" )
    private String message;
}
