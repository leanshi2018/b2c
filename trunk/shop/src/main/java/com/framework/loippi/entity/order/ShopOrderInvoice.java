package com.framework.loippi.entity.order;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity - 买家发票信息表
 * 
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_ORDER_INVOICE")
public class ShopOrderInvoice implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/** 索引id */
	@Column(id = true, name = "id", updatable = false)
	private Long id;
	
	/** 会员ID */
	@Column(name = "member_id" )
	private Long memberId;
	
	/** 订单ID */
	@Column(name = "order_id" )
	private Long orderId;
	
	/** 1普通发票2增值税发票 */
	@Column(name = "inv_state" )
	private String invState;
	
	/** 发票抬头[普通发票] */
	@Column(name = "inv_title" )
	private String invTitle;
	
	/** 发票内容[普通发票] */
	@Column(name = "inv_content" )
	private String invContent;
	
	/** 单位名称 */
	@Column(name = "inv_company" )
	private String invCompany;
	
	/** 纳税人识别号 */
	@Column(name = "inv_code" )
	private String invCode;
	
	/** 注册地址 */
	@Column(name = "inv_reg_addr" )
	private String invRegAddr;
	
	/** 注册电话 */
	@Column(name = "inv_reg_phone" )
	private String invRegPhone;
	
	/** 开户银行 */
	@Column(name = "inv_reg_bname" )
	private String invRegBname;
	
	/** 银行帐户 */
	@Column(name = "inv_reg_baccount" )
	private String invRegBaccount;
	
	/** 收票人姓名 */
	@Column(name = "inv_rec_name" )
	private String invRecName;
	
	/** 收票人手机号 */
	@Column(name = "inv_rec_mobphone" )
	private String invRecMobphone;
	
	/** 收票人省份 */
	@Column(name = "inv_rec_province" )
	private String invRecProvince;
	
	/** 送票地址 */
	@Column(name = "inv_goto_addr" )
	private String invGotoAddr;
	
	/** 是否是默认的 */
	@Column(name = "is_default" )
	private Integer isDefault;
	
}
