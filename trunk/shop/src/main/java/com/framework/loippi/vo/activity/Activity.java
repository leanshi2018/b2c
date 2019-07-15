package com.framework.loippi.vo.activity;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import lombok.Data;
import lombok.ToString;

/**
 * 促销活动基本信息Entity
 *
 * @author JIME
 * @version 2016-09-12
 */
@Data
@ToString
public class Activity implements GenericEntity {


	private Long activityId;

	/**
	 * 活动类型对应的名字
	 */
	private String activityName;

	/**
	 * 活动类型
	 */
	private int activityType;
	
	/**
	 * 活动规则
	 */
	private String limitWhere;
	
	/**
	 * 优惠资源
	 */
	private String source;

	/**
	 * 描述
	 */
	private String info;
	
	/**
	 * 店铺id
	 */
	private Long storeId;
	
	/**
	 * 0选中,1未选择
	 */
	private int checked;
	
}