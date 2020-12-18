package com.framework.loippi.entity.gift;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Table;

/**
 * @author :ldq
 * @date:2020/12/1
 * @description:dubbo com.framework.loippi.entity.gift
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shop_gift_activity")
public class ShopGiftActivity implements GenericEntity {
	private static final long serialVersionUID = 5081846432919091193L;
	/**
	 * 主键id索引
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
	/**
	 * 活动名称
	 */
	private String activityName;
	/**
	 * 规则1需MI值
	 */
	private BigDecimal ppv1;
	/**
	 * 规则2需MI值
	 */
	private BigDecimal ppv2;
	/**
	 * 规则3需MI值
	 */
	private BigDecimal ppv3;
	/**
	 * 赠品数量
	 */
	private Integer giftNum;
	/**
	 * 开始时间
	 * */
	private Date startTime;
	/**
	 * 结束时间
	 * */
	private Date endTime;
	/**
	 * 状态 0上架  1下架（仅此一条上架）
	 * */
	private Integer estate;
	/**
	 * 创建人
	 */
	private String creationBy;
	/**
	 * 创建时间
	 */
	private Date creationTime;
	/**
	 * 更新人
	 */
	private String updateBy;
	/**
	 * 更新时间
	 */
	private Date updateTime;

	//前端
	private String startTimeS;//开始时间
	private String endTimeS;//结束时间
	//商品规格Id
	List<String> specIdList1;//规则1商品
	List<String> specIdList2;//规则2商品
	List<String> specIdList3;//规则3商品

	//详情展示
	List<ShopGiftGoods> giftGoodsList1;//规则1商品
	List<ShopGiftGoods> giftGoodsList2;//规则2商品
	List<ShopGiftGoods> giftGoodsList3;//规则3商品

}
