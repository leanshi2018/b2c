package com.framework.loippi.result.common.index;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.framework.loippi.entity.common.RdKeyword;
import com.framework.loippi.entity.common.ShopHomePicture;
import com.framework.loippi.pojo.activity.PictureVio;
import com.framework.loippi.result.common.goods.GoodsListResult;
import com.framework.loippi.utils.JacksonUtil;

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
	List<PictureVio> homePictures;

	//品牌精品
	List<PictureVio> adPictures;

	//关键词队列
	List<RdKeyword> keywords;

	//是否有弹窗  0:无  1：有
	private Integer outType;

	//弹窗商品列表信息
	List<GoodsListResult> goodsList;

	public static HomeAndADPictureResult build(List<ShopHomePicture> homePictures, List<ShopHomePicture> adPictures,List<RdKeyword> keywords) {

		HomeAndADPictureResult result = new HomeAndADPictureResult();
		result.setOutType(0);

		if (homePictures==null){
			result.setHomePictures(new ArrayList<PictureVio>());
		}else {
			List<PictureVio> list = new ArrayList<PictureVio>();
			for (ShopHomePicture homePicture : homePictures) {
				PictureVio pictureVio = new PictureVio();
				pictureVio.setId(homePicture.getId());
				pictureVio.setPictureName(homePicture.getPictureName());
				if (homePicture.getPictureUrl()!=null){
					pictureVio.setPictureUrl(homePicture.getPictureUrl());
				}else {
					pictureVio.setPictureUrl("");
				}

				if (homePicture.getJumpName()!=null){
					pictureVio.setJumpName(homePicture.getJumpName());
				}else {
					pictureVio.setJumpName("");
				}
				pictureVio.setPSort(homePicture.getPSort());
				pictureVio.setAuditStatus(homePicture.getAuditStatus());
				pictureVio.setPictureType(homePicture.getPictureType());
				if (homePicture.getJumpInterface()!=null){
					pictureVio.setJumpInterface(homePicture.getJumpInterface());
				}

				Map<String,Object> map = new HashMap<String,Object>();
				if (homePicture.getActivityUrl()!=null){
					map.put("page",homePicture.getActivityUrl());
				}else {
					map.put("page","");
				}

				if (homePicture.getPictureJson()!=null && !"".equals(homePicture.getPictureJson())){
					Map<String, String> jsonMap = JacksonUtil.readJsonToMap(homePicture.getPictureJson());
					Set<String> strings = jsonMap.keySet();
					Iterator<String> iterator = strings.iterator();
					while (iterator.hasNext()){
						String key = iterator.next();
						String value = jsonMap.get(key);
						map.put(key,value);
					}
				}

				JSONObject activityUrlJson = JSONObject.fromObject(map);
				pictureVio.setActivityUrl(activityUrlJson.toString());
				list.add(pictureVio);
			}
			result.setHomePictures(list);
		}

		if (adPictures==null){
			result.setAdPictures(new ArrayList<PictureVio>());
		}else {
			List<PictureVio> list = new ArrayList<PictureVio>();
			for (ShopHomePicture adPicture : adPictures) {
				PictureVio pictureVio = new PictureVio();
				pictureVio.setId(adPicture.getId());
				pictureVio.setPictureName(adPicture.getPictureName());
				if (adPicture.getPictureUrl()!=null){
					pictureVio.setPictureUrl(adPicture.getPictureUrl());
				}else {
					pictureVio.setPictureUrl("");
				}

				if (adPicture.getJumpName()!=null){
					pictureVio.setJumpName(adPicture.getJumpName());
				}else {
					pictureVio.setJumpName("");
				}
				pictureVio.setPSort(adPicture.getPSort());
				pictureVio.setAuditStatus(adPicture.getAuditStatus());
				pictureVio.setPictureType(adPicture.getPictureType());
				if (adPicture.getJumpInterface()!=null){
					pictureVio.setJumpInterface(adPicture.getJumpInterface());
				}

				Map<String,Object> map = new HashMap<String,Object>();
				if (adPicture.getActivityUrl()!=null){
					map.put("page",adPicture.getActivityUrl());
				}else {
					map.put("page","");
				}


				if (adPicture.getPictureJson()!=null && !"".equals(adPicture.getPictureJson())){
					Map<String, String> jsonMap = JacksonUtil.readJsonToMap(adPicture.getPictureJson());
					Set<String> strings = jsonMap.keySet();
					Iterator<String> iterator = strings.iterator();
					while (iterator.hasNext()){
						String key = iterator.next();
						String value = jsonMap.get(key);
						map.put(key,value);
					}
				}

				JSONObject activityUrlJson = JSONObject.fromObject(map);
				pictureVio.setActivityUrl(activityUrlJson.toString());
				list.add(pictureVio);
			}
			result.setAdPictures(list);
		}

		if (keywords!=null&&keywords.size()>0){
			result.setKeywords(keywords);
		}else {
			result.setKeywords(new ArrayList<RdKeyword>());
		}
		return result;
	}

	public static HomeAndADPictureResult build1(List<ShopHomePicture> homePictures, List<ShopHomePicture> adPictures,List<RdKeyword> keywords,List<GoodsListResult> goodsList) {

		HomeAndADPictureResult result = new HomeAndADPictureResult();
		result.setOutType(1);

		if (homePictures==null){
			result.setHomePictures(new ArrayList<PictureVio>());
		}else {
			List<PictureVio> list = new ArrayList<PictureVio>();
			for (ShopHomePicture homePicture : homePictures) {
				PictureVio pictureVio = new PictureVio();
				pictureVio.setId(homePicture.getId());
				pictureVio.setPictureName(homePicture.getPictureName());
				if (homePicture.getPictureUrl()!=null){
					pictureVio.setPictureUrl(homePicture.getPictureUrl());
				}else {
					pictureVio.setPictureUrl("");
				}

				if (homePicture.getJumpName()!=null){
					pictureVio.setJumpName(homePicture.getJumpName());
				}else {
					pictureVio.setJumpName("");
				}
				pictureVio.setPSort(homePicture.getPSort());
				pictureVio.setAuditStatus(homePicture.getAuditStatus());
				pictureVio.setPictureType(homePicture.getPictureType());
				if (homePicture.getJumpInterface()!=null){
					pictureVio.setJumpInterface(homePicture.getJumpInterface());
				}

				Map<String,Object> map = new HashMap<String,Object>();
				if (homePicture.getActivityUrl()!=null){
					map.put("page",homePicture.getActivityUrl());
				}else {
					map.put("page","");
				}

				if (homePicture.getPictureJson()!=null && !"".equals(homePicture.getPictureJson())){
					Map<String, String> jsonMap = JacksonUtil.readJsonToMap(homePicture.getPictureJson());
					Set<String> strings = jsonMap.keySet();
					Iterator<String> iterator = strings.iterator();
					while (iterator.hasNext()){
						String key = iterator.next();
						String value = jsonMap.get(key);
						map.put(key,value);
					}
				}

				JSONObject activityUrlJson = JSONObject.fromObject(map);
				pictureVio.setActivityUrl(activityUrlJson.toString());
				list.add(pictureVio);
			}
			result.setHomePictures(list);
		}

		if (adPictures==null){
			result.setAdPictures(new ArrayList<PictureVio>());
		}else {
			List<PictureVio> list = new ArrayList<PictureVio>();
			for (ShopHomePicture adPicture : adPictures) {
				PictureVio pictureVio = new PictureVio();
				pictureVio.setId(adPicture.getId());
				pictureVio.setPictureName(adPicture.getPictureName());
				if (adPicture.getPictureUrl()!=null){
					pictureVio.setPictureUrl(adPicture.getPictureUrl());
				}else {
					pictureVio.setPictureUrl("");
				}

				if (adPicture.getJumpName()!=null){
					pictureVio.setJumpName(adPicture.getJumpName());
				}else {
					pictureVio.setJumpName("");
				}
				pictureVio.setPSort(adPicture.getPSort());
				pictureVio.setAuditStatus(adPicture.getAuditStatus());
				pictureVio.setPictureType(adPicture.getPictureType());
				if (adPicture.getJumpInterface()!=null){
					pictureVio.setJumpInterface(adPicture.getJumpInterface());
				}

				Map<String,Object> map = new HashMap<String,Object>();
				if (adPicture.getActivityUrl()!=null){
					map.put("page",adPicture.getActivityUrl());
				}else {
					map.put("page","");
				}


				if (adPicture.getPictureJson()!=null && !"".equals(adPicture.getPictureJson())){
					Map<String, String> jsonMap = JacksonUtil.readJsonToMap(adPicture.getPictureJson());
					Set<String> strings = jsonMap.keySet();
					Iterator<String> iterator = strings.iterator();
					while (iterator.hasNext()){
						String key = iterator.next();
						String value = jsonMap.get(key);
						map.put(key,value);
					}
				}

				JSONObject activityUrlJson = JSONObject.fromObject(map);
				pictureVio.setActivityUrl(activityUrlJson.toString());
				list.add(pictureVio);
			}
			result.setAdPictures(list);
		}

		if (keywords!=null&&keywords.size()>0){
			result.setKeywords(keywords);
		}else {
			result.setKeywords(new ArrayList<RdKeyword>());
		}

		result.setGoodsList(goodsList);

		return result;
	}

}
