package com.framework.loippi.vo.activity;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 促销活动
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityGoodsItemVO implements GenericEntity {


	/**
	 * 活动id
	 */
	private Long activityId;

	/**
	 * 活动名称
	 */
	private String activityName;

	/**
	 * 活动价格
	 */
	private BigDecimal activityPrice;

	/**
	 * 商品销售价
	 */
	private  BigDecimal goodsPrice;

	/**
	 * 规格id
	 */
	private Long goodsSpecId;

	/**
	 * 商品图片
	 */
	private String goodsImage;
	/**
	 * 商品名称
	 */
	private String goodsName;

	/**
	 * 促销活动图片手机端
	 */
	private String activityImage;


	/*促销活动图片PC端*/
	private String activityPicturePc;

	/**
	 * 促销所属店铺id
	 */
	private Long storeId;

    private Integer sort;

    //商品库存
	private  Integer goodsStock;


}
