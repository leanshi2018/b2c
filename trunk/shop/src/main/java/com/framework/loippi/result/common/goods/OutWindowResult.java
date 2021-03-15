package com.framework.loippi.result.common.goods;

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

import com.framework.loippi.entity.common.ShopHomePicture;
import com.framework.loippi.pojo.activity.PictureVio;
import com.framework.loippi.utils.JacksonUtil;

/**
 * @author :ldq
 * @date:2021/3/15
 * @description:trunk com.framework.loippi.result.common.goods
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutWindowResult {

	//弹窗商品列表信息
	List<GoodsListResult> goodsList;

	//系统弹窗
	List<PictureVio> sysPictures;

	public static OutWindowResult buildSys(List<ShopHomePicture> sysPictures) {

		OutWindowResult result = new OutWindowResult();
		result.setGoodsList(new ArrayList<>());
		if (sysPictures==null){
			result.setSysPictures(new ArrayList<PictureVio>());
		}else {
			List<PictureVio> list = new ArrayList<PictureVio>();
			for (ShopHomePicture homePicture : sysPictures) {
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
			result.setSysPictures(list);
		}
		return result;
	}

	public static OutWindowResult build(List<ShopHomePicture> sysPictures,List<GoodsListResult> goodsList) {

		OutWindowResult result = new OutWindowResult();
		result.setGoodsList(new ArrayList<>());
		if (sysPictures==null){
			result.setSysPictures(new ArrayList<PictureVio>());
		}else {
			List<PictureVio> list = new ArrayList<PictureVio>();
			for (ShopHomePicture homePicture : sysPictures) {
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
			result.setSysPictures(list);
		}

		result.setGoodsList(goodsList);
		return result;
	}
}
