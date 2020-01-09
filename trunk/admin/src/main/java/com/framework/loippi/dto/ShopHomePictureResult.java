package com.framework.loippi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.framework.loippi.entity.common.ShopHomePicture;

/**
 * @author :ldq
 * @date:2020/1/9
 * @description:dubbo com.framework.loippi.controller.common
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopHomePictureResult {
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
	 * 跳转连接
	 */
	private String jumpInterface;

	/**
	 * 跳转需要参数json
	 */
	private String pictureJson;

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
	 * 审核状态
	 * 0 禁用 1开启
	 */
	private Integer auditStatus;
	/**
	 * 图片类型： 0：首页轮播图；1:首页广告位图
	 */
	private Integer pictureType;

	public static ShopHomePictureResult build(ShopHomePicture shopHomePicture) {
		ShopHomePictureResult result = new ShopHomePictureResult();
		if (shopHomePicture.getId()!=null){
			result.setId(shopHomePicture.getId());
		}
		if (shopHomePicture.getPSort()!=null){
			result.setPSort(shopHomePicture.getPSort());
		}
		if (shopHomePicture.getPictureType()!=null){
			result.setPictureType(shopHomePicture.getPictureType());
		}
		if (shopHomePicture.getAuditStatus()!=null){
			result.setAuditStatus(shopHomePicture.getAuditStatus());
		}
		if (shopHomePicture.getPictureName()==null){
			result.setPictureName("");
		}else {
			result.setPictureName(shopHomePicture.getPictureName());
		}

		if (shopHomePicture.getPictureUrl()==null){
			result.setPictureUrl("");
		}else {
			result.setPictureUrl(shopHomePicture.getPictureUrl());
		}

		if (shopHomePicture.getPictureJson()==null){
			result.setPictureJson("");
		}else {
			result.setPictureJson(shopHomePicture.getPictureJson());
		}

		if (shopHomePicture.getActivityUrl()==null){
			result.setActivityUrl("");
		}else {
			result.setActivityUrl(shopHomePicture.getActivityUrl());
		}

		if (shopHomePicture.getJumpName()==null){
			if (shopHomePicture.getJumpInterface()==null){
				result.setJumpInterface("");
			}else {
				result.setJumpInterface(shopHomePicture.getJumpInterface());
			}
		}else {
			result.setJumpName(shopHomePicture.getJumpName());
		}

		return result;
	}
}
