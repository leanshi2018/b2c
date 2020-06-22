package com.framework.loippi.entity.ware;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Table;

/**
 * 调拨单
 * @author :ldq
 * @date:2018/11/7
 * @description:leanshi_member cn.leanshi.model
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rd_ware_allocation")
public class RdWareAllocation implements GenericEntity {
	private static final long serialVersionUID = 5081846432919091193L;

	private int wId;//id
	private String wareCodeIn;//入库代码  yyyyxxxx
	private String wareNameIn;//入库名
	private String wareCodeOut;//出库代码  yyyyxxxx
	private String wareNameOut;//出库名
	private String attachAdd;//附件地址
	private int status;//状态 -2：拒绝 -1：已取消 1：新单（保存状态） 2：待审 3：已授权
	private String wareOrderSn;//发货订单编号（为空的时候没有发货单）
	private String autohrizeBy;//授权人
	private Date autohrizeTime;//授权时间
	private String autohrizeDesc;//授权说明（同意或不同意的理由）

}
