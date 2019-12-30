package com.framework.loippi.result.common.index;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.framework.loippi.entity.common.ShopHomePicture;

/**
 * @author :ldq
 * @date:2019/12/30
 * @description:dubbo com.framework.loippi.result.common.index
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeAndADPictureResult {

	//促销活动
	List<ShopHomePicture> homePictures;

	//品牌精品
	List<ShopHomePicture> adPictures;

}
