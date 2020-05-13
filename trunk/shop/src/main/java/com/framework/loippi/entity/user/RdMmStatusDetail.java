package com.framework.loippi.entity.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Table;

/**
 * @author :ldq
 * @date:2020/5/13
 * @description:dubbo com.framework.loippi.entity.user
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rd_mm_status_detail")
public class RdMmStatusDetail implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/** id */
	private Integer sId;
	/** 会员编号 */
	private String mmCode;
	/** 昵称 */
	private String mmNickName;
	/** 状态类型： 会员状态：MM  会员积分状态：MR */
	private String statusType;
	/** 修改前状态 */
	private Integer statusBefore;
	/** 修改后状态 */
	private Integer statusAfter;
	/** 操作人 */
	private String updateBy;
	/** 操作时间 */
	private Date updateTime;
	/** 更改备注 */
	private String updateDesc;
}
