package com.framework.loippi.vo.store;

import lombok.Data;

import java.util.List;

/**
 * @author :ldq
 * @date:2019/10/11
 * @description:leanshi_member cn.leanshi.model.modelVo
 */
@Data
public class MentionProductVo {

	private Integer pageNum;
	private Integer pageSize;
	private Integer pages;
	private Integer total;
	List<MentionWareGoodsVo> productResults;

}
