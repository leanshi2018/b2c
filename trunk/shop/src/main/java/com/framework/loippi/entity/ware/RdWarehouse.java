package com.framework.loippi.entity.ware;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

/**
 * Entity - 仓库记录表
 * 
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RD_WAREHOUSE")
public class RdWarehouse implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/** 仓库代码：yyyyxxxx */
	@Column(name = "WARE_CODE" )
	private String wareCode;
	
	/** 仓库名称 */
	@Column(name = "WARE_NAME" )
	private String wareName;

	/** 仓库类型  0.自营仓库  1.外包仓库  2.自提店 */
	@Column(name = "WARE_TYPE" )
	private String wareType;

	/** 省 */
	@Column(name = "PROVINCE_CODE" )
	private String provinceCode;
	
	/** 市 */
	@Column(name = "CITY_CODE" )
	private String cityCode;
	
	/** 区 */
	@Column(name = "COUNTRY_CODE" )
	private String countryCode;
	
	/** 详细地址 */
	@Column(name = "WARE_DETIAL" )
	private String wareDetial;
	
	/** 创建人 */
	@Column(name = "CREATEBY" )
	private String createby;
	
	/** 创建时间 */
	@Column(name = "CREATETIME" )
	private java.util.Date createtime;
	
	/** 最后修改时间 */
	@Column(name = "UPDATE_TIME_LAST" )
	private java.util.Date updateTimeLast;
	
	/** 仓库状态  0.正常 1停用 */
	@Column(name = "WARE_STATUS" )
	private Integer wareStatus;

	/** 负责人姓名（收件人姓名） */
	@Column(name = "CONSIGNEE_NAME" )
	private String consigneeName;

	/** 会员编号(选择类型为自提店时显示且必填) */
	@Column(name = "MM_CODE" )
	private String mmCode;

	/** 自提地址id */
	@Column(name = "MENTION_ID" )
	private Integer mentionId;

	/** 备注 */
	@Column(name = "WARE_DESC" )
	private String wareDesc;


    //搜索条件
	private String wareNameKey;
	private String wareDetialKey;
	private Long specId;
	private Integer specNum;
	
}
