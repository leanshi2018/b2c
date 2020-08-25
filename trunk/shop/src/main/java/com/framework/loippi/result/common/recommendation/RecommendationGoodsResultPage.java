package com.framework.loippi.result.common.recommendation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import com.framework.loippi.entity.product.ShopGoodsClass;
import com.framework.loippi.support.Page;

/**
 * @author :ldq
 * @date:2020/8/25
 * @description:dubbo com.framework.loippi.result.common.recommendation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationGoodsResultPage {

	private static final long serialVersionUID = 5081846432919091193L;

	private Integer pageNumber;
	private Integer pageSize;
	private Long total;
	private Integer totalPages;
	private List<RecommendationGoodsResult> goodsResultList;

	public static RecommendationGoodsResultPage build(Page serviceGoodsResult, List<ShopGoodsClass> goodsClassList) {
		List<RecommendationGoodsResult> goodsResultList = new ArrayList<>();
		List<RecommendationGoodsResult> goodsResultContent = serviceGoodsResult.getContent();
		for (RecommendationGoodsResult rgResult : goodsResultContent) {
			Long gcId = rgResult.getGcId();
			for (ShopGoodsClass aClass : goodsClassList) {
				if (aClass.getId().toString().equals(gcId.toString())){
					if (aClass.getGcParentId()!=null&&aClass.getGcParentId()!=0){
						Long gcParentId = aClass.getGcParentId();
						for (ShopGoodsClass gcPClass : goodsClassList) {
							if (gcPClass.getId().toString().equals(gcParentId.toString())){
								rgResult.setGcName(gcPClass.getGcName()+rgResult.getGcName());
							}
						}
					}
				}
			}
			goodsResultList.add(rgResult);
		}

		RecommendationGoodsResultPage page = new RecommendationGoodsResultPage();
		page.setPageNumber(serviceGoodsResult.getPageNumber());
		page.setPageSize(serviceGoodsResult.getPageSize());
		page.setTotal(serviceGoodsResult.getTotal());
		page.setTotalPages(serviceGoodsResult.getTotalPages());
		page.setGoodsResultList(goodsResultList);
		return page;
	}

}
