package com.framework.loippi.entity.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

/**
 * Entity - 入库记录表
 * 
 * @author dzm
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RD_GOODS_ADJUSTMENT")
public class RdGoodsAdjustment implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/**  */
	@Column(id = true, name = "id", updatable = false)
	private Long id;
	
	/** 商品id */
	@Column(name = "good_id" )
	private Long goodId;
	
	/** 商品名称 */
	@Column(name = "goods_name" )
	private String goodsName;
	
	/** 规格名称 */
	@Column(name = "spec_name" )
	private String specName;
	
	/** 商品规格 */
	@Column(name = "goods_spec" )
	private String goodsSpec;
	
	/** 现有商品库存数量 */
	@Column(name = "stock_now" )
	private Long stockNow;
	
	/** 入库商品数量 */
	@Column(name = "stock_into" )
	private Long stockInto;

	/** 欠货数量（欠货订单生成  负数为欠货 ） */
	@Column(name = "stock_owe" )
	private Long stockOwe;

	/** 生产时间 */
	@Column(name = "create_time" )
	private java.util.Date createTime;
	
	/** 保质期（天） */
	@Column(name = "quality_time" )
	private Long qualityTime;
	
	/** 过期时间 */
	@Column(name = "shelf_life_time" )
	private java.util.Date shelfLifeTime;
	
	/** 入库仓库代码：yyyyxxxx */
	@Column(name = "ware_code" )
	private String wareCode;
	
	/** 调整单号 */
	@Column(name = "wId" )
	private Integer wid;
	
	/** 状态  有效：1      无效：-1   待审：0 */
	@Column(name = "status" )
	private Long status;

	/** 1.调整单 2.调拨单 */
	@Column(name = "sign" )
	private Integer sign;

	/** 授权时间(审核时间) */
	@Column(name = "autohrize_time" )
	private Date autohrizeTime;

	/** 规格id编号 */
	@Column(name = "specification_id" )
	private Long specificationId;

	/** 规格商品编号 */
	@Column(name = "spec_goods_serial" )
	private String specGoodsSerial;

	/** 预警线 */
	@Column(name = "precautious_line" )
	private Integer precautiousLine;//预警线

	//模糊查询
	private String goodsNameKey;
	private String specNameKey;
	
}
