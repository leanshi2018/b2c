package com.framework.loippi.vo.goods;

import lombok.Data;
import lombok.ToString;


/**
 * 2015年06月29日17:58:33
 * @author cgl
 * 这个实体类不用于存数据
 */
@Data
@ToString
public class GoodsSpecVo {
	/**
	 * 规格id
	 */
	private String spId;
	/**
	 * 规格名称
	 */
	private String spName;
	/**
	 * 规格值id
	 */
	private String spValueId;
	/**
	 * 按层排序的规格值id
	 */
	private String spSortValueId;
	/**
	 * 规格值名称
	 */
	private String spValueName;
	
	/**
	 * 对应的图片
	 */
	private String colImg;
	
	/**
	 * 商品规格是否开启
	 */
	private Integer specIsOpen;
}
