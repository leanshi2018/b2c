package com.framework.loippi.pojo.activity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * @author :ldq
 * @date:2019/12/31
 * @description:dubbo com.framework.loippi.pojo.activity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PictureVio {
	private static final long serialVersionUID = 5081846432919091193L;

	/**
	 * 活动id
	 */
	@JsonSerialize(using = ToStringSerializer.class)
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
	 * 跳转名称
	 */
	private String jumpName;

	/**
	 * 排序
	 */
	private Integer pSort;

	/**
	 * 活动连接
	 */
	private String activityUrl;

	/**
	 * 跳转连接
	 */
	private String jumpInterface;
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
