package com.framework.loippi.entity.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Table;

/**
 * @author :ldq
 * @date:2019/12/26
 * @description:dubbo com.framework.loippi.entity.common
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shop_home_picture")
public class ShopHomePicture  implements GenericEntity{

	private static final long serialVersionUID = 5081846432919091193L;

	/**
	 * 活动id
	 */
	private Long id;

	/**
	 * 图片名称
	 */
	private String pictureName;

	/**
	 * 图片路径
	 */
	private String pictureUrl;

	/**
	 * 图片路径需要参数json
	 */
	private String pictureJson;

	/**
	 * 排序
	 */
	private Integer pSort;

	/**
	 * 活动连接
	 */
	private String activityUrl;
	/**
	 * 审核状态
	 * 0 禁用 1开启
	 */
	private Integer auditStatus;
	/**
	 * 图片类型： 0：首页轮播图；1:首页广告位图
	 */
	private Integer pictureType;
}
