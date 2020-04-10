package com.framework.loippi.entity.walet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

/**
 * @author :ldq
 * @date:2020/4/9
 * @description:dubbo com.framework.loippi.entity.walet
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rd_biz_pay")
public class RdBizPay implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	@Column(id = true, name = "id")
	private Long id;

	@Column(name = "pay_sn" )
	private String paySn;

	@Column(name = "biz_pay_sn" )
	private String bizPaySn;

	@Column(name = "invalid_status" )
	private Integer invalidStatus;
}
