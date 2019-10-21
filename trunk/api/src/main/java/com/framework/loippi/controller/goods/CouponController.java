package com.framework.loippi.controller.goods;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.framework.loippi.controller.BaseController;
import com.framework.loippi.utils.ApiUtils;
import com.framework.loippi.utils.Constants;

/**
 * @author :ldq
 * @date:2019/10/21
 * @description:dubbo com.framework.loippi.controller.goods
 */
@Controller
@ResponseBody
@RequestMapping("/api/coupon")
public class CouponController extends BaseController {

	/**
	 * 优惠券详情
	 * @param request
	 * @param couponId 优惠券id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "couponDetail", method = RequestMethod.POST)
	public String detail(HttpServletRequest request, Long couponId) {
		String sessionId = request.getHeader(Constants.USER_SESSION_ID);
		if (couponId == null) {
			return jsonFail();
		}
		//商品基本详情对象
		/*ShopGoods shopGoods = shopGoodsService.find(goodsId);
		if (shopGoods == null) {
			return jsonFail("商品不存在");
		}
		//评价标签
		ActivityGoodsDetailResult goodsDetailResult = ActivityGoodsDetailResult.build(shopGoods, prefix, wapServer);
		//品牌信息
		loadBrand(goodsDetailResult, shopGoods);
		//规格信息
		loadSpecInfo(goodsDetailResult, shopGoods);
		//商品是否收藏
		loadFavorate(sessionId, goodsDetailResult,activityId);
		goodsDetailResult.setGoodsIs(goodsId);
		//加载品论
		loadEvaluate(goodsDetailResult);
		//加载活动
		activityId = loadSpeckData(goodsDetailResult, activityId,shopGoods);
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("goodsDetailInfo", goodsDetailResult);
		if (activityId != null && activityId!=-1) {
			ShopActivity shopActivity = shopActivityService.find(activityId);
			resultMap.put("activityInfo", ActivityDetailResult.build(shopActivity));
		}*/
		return ApiUtils.success();
	}

}
