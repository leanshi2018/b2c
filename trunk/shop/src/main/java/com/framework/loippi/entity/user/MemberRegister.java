package com.framework.loippi.entity.user;



import com.framework.loippi.mybatis.eitity.GenericEntity;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@ToString
@Data
public class MemberRegister implements GenericEntity {

	/**
	 * 会员id
	 */
	private String memberId;
	
	/**
	 * 会员名称
	 */
	private String memberName;
	
	/**
	 * 注册时间
	 */
	private String finnshedTime;
	
	/**
	 * 注册数量
	 */
	private Integer memberCount;
	
	/**
	 * 时间条件
	 * 按这周查:week
	 * 按这个月查:month
	 * 按今年年查:year
	 */
	private String condition;
	
	/**
	 * 开始时间
	 */
	private Long startTime;
	
	/**
	 * 结束时间
	 */
	private Long endTime;
}
