package com.framework.loippi.entity.user;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Entity - 提现特权会员表
 *
 * @author zc
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rd_mm_privilege")
public class MemberPrivilege  implements GenericEntity {

    private static final long serialVersionUID = 5081846432919091193L;

    @Column(name = "id" )
    private long id;//主键id

    /** 会员编号 */
    @Column(name = "M_CODE" )
    private String mmCode;

    /**
     * 最新提现时间
     */
    @Column(name = "LAST_WITHDRAWAL_TIME")
    private Date lastWithdrawalTime;
}
