package com.framework.loippi.entity.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

/**
 * @author :ldq
 * @date:2020/5/12
 * @description:dubbo com.framework.loippi.entity.user
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rd_mm_logout_num")
public class RdMmLogOutNum implements GenericEntity {

	@Column(id = true, name = "id")
	private Integer id;

	@Column(name = "logout_num" )
	private Integer logoutNum;
}
