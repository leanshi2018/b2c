package com.framework.loippi.controller.user;


import com.framework.loippi.controller.BaseController;
import com.framework.loippi.entity.order.ShopOrder;
import com.framework.loippi.entity.order.ShopOrderGoods;
import com.framework.loippi.entity.product.ShopGoodsEvaluate;
import com.framework.loippi.result.auths.AuthsLoginResult;
import com.framework.loippi.result.evaluate.EvaluateGoodsResult;
import com.framework.loippi.result.evaluate.EvaluationCenterGoodsResult;
import com.framework.loippi.service.order.ShopOrderGoodsService;
import com.framework.loippi.service.order.ShopOrderService;
import com.framework.loippi.service.product.ShopGoodsEvaluateService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.ApiUtils;
import com.framework.loippi.utils.Constants;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.Xerror;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户订单
 * Created by longbh on 2018/10/23.
 */
@Controller
@ResponseBody
@RequestMapping("/api/user")
public class UserEvaluationController extends BaseController {

    @Resource
    private ShopOrderService shopOrderService;
    @Resource
    private ShopOrderGoodsService orderGoodsService;
    @Resource
    private ShopGoodsEvaluateService shopGoodsEvaluateService;

    /**
     * 用户评价历史记录
     *
     * @param pageNumber
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "/evaluate.json", method = RequestMethod.POST)
    public String UserEvaluate(@RequestParam(defaultValue = "1") Integer pageNumber,
                               @RequestParam(defaultValue = "10") Integer pageSize,
                               HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        Pageable pager = new Pageable(pageNumber, pageSize);
        Map<String, Object> params = new HashMap<>();
        params.put("gevalState", 0); // 0正常显示 1禁止显示
        params.put("isDel", 0);
        params.put("gevalFrommemberid", Long.parseLong(member.getMmCode()));
        pager.setParameter(params);
        Page<ShopGoodsEvaluate> result = shopGoodsEvaluateService.findWithGoodsByPage(pager, request.getContextPath());//结果集
        return ApiUtils.success(EvaluateGoodsResult.build(result.getContent()));
    }

    //我的评价中心
    @RequestMapping(value = "/myEvaluate.json", method = RequestMethod.POST)
    public String MyEvaluate(@RequestParam(defaultValue = "1") Integer pageNumber,
                             @RequestParam(defaultValue = "10") Integer pageSize, HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if (member == null) {
            return ApiUtils.error(Xerror.USER_UNLOGIN_JSON_CODE);
        }
        Pageable page = new Pageable(pageNumber, pageSize);
        Map<String, Object> order = new HashMap<>();
        //未评价
        Paramap paramap = Paramap.create();
        Pageable pager = new Pageable();
        paramap.put("buyerId", Long.parseLong(member.getMmCode()));
        paramap.put("isDel", 0);
        // -1 查询所有订单
        paramap.put("orderState", 40);
        //已评价状态
        paramap.put("evaluationStatus", "0");

        List<ShopOrder> orderPage = shopOrderService.findList(paramap);
        List<ShopOrderGoods> allShopOrderGoodsList = new ArrayList<>();
        if (orderPage != null && orderPage.size() > 0) {
            for (ShopOrder shopOrder : orderPage) {
                List<ShopOrderGoods> shopOrderGoodsList = orderGoodsService.findEvalListBy(shopOrder.getId(), 0);//评价状态0
                for (ShopOrderGoods shopOrderGoods : shopOrderGoodsList) {
                    if (shopOrderGoods.getEvaluationStatus() == 0) {
                        allShopOrderGoodsList.add(shopOrderGoods);
                    }
                }
            }
        }
        //已评价
        Pageable pagerEvaluate = new Pageable(pageNumber, pageSize);
        Map<String, Object> params = new HashMap<>();
        params.put("gevalState", 0); // 0正常显示 1禁止显示
        params.put("isDel", 0);
        params.put("gevalFrommemberid", Long.parseLong(member.getMmCode()));
        pagerEvaluate.setParameter(params);
        Page<ShopGoodsEvaluate> result = shopGoodsEvaluateService.findWithGoodsByPage(pagerEvaluate, request.getContextPath());//结果集
        Map map = new HashMap();
        map.put("haveEvaluation", result.getTotal());
        map.put("notEvaluation", allShopOrderGoodsList.size());
        return ApiUtils.success(map);
    }

    //我的评价中的未评价
    @RequestMapping(value = "/myNotEvaluate.json", method = RequestMethod.POST)
    public String myNotEvaluate(@RequestParam(defaultValue = "1") Integer pageNumber,
                                @RequestParam(defaultValue = "10") Integer pageSize, HttpServletRequest request, Integer orderStatus) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        Map<String, Object> resultMap = new HashMap<>();
        Paramap paramap = Paramap.create();
        Pageable pager = new Pageable();
        paramap.put("buyerId", Long.parseLong(member.getMmCode()));
        paramap.put("isDel", 0);
        // -1 查询所有订单
        if (orderStatus != null && orderStatus != -1) {
            // 0:已取消;10:待付款;20:待发货;30:待收货;40:交易完成;50:已提交;60:已确认;
            paramap.put("orderState", orderStatus);
            paramap.put("lockState", 0);
        }
        if (orderStatus != null && orderStatus == 40) {
            //已评价状态
            paramap.put("evaluationStatus", "0");
        }
        List<ShopOrder> orderPage = shopOrderService.findList(paramap);
        List<ShopOrderGoods> allShopOrderGoodsList = new ArrayList<>();
        if (orderPage != null && orderPage.size() > 0) {
            for (ShopOrder shopOrder : orderPage) {
                List<ShopOrderGoods> shopOrderGoodsList = orderGoodsService.findEvalListBy(shopOrder.getId(), 0);//评价状态0
                for (ShopOrderGoods shopOrderGoods : shopOrderGoodsList) {
                    if (shopOrderGoods.getEvaluationStatus() == 0) {
                        allShopOrderGoodsList.add(shopOrderGoods);
                    }
                }
            }
        }
        if (allShopOrderGoodsList == null || allShopOrderGoodsList.size() < 1) {
            resultMap.put("itemCount", allShopOrderGoodsList.size());
            resultMap.put("itemList", EvaluationCenterGoodsResult.buildList(allShopOrderGoodsList));
            return com.framework.loippi.utils.ApiUtils.success(resultMap);
        }

        resultMap.put("itemCount", allShopOrderGoodsList.size());
        resultMap.put("itemList", EvaluationCenterGoodsResult.buildList(allShopOrderGoodsList));
        return ApiUtils.success(resultMap);

    }

    //订单中的未评价
    @RequestMapping(value = "/notEvaluate.json", method = RequestMethod.POST)
    public String notEvaluate(Long orderId) {
        if (orderId == null) {
            return com.framework.loippi.utils.ApiUtils.error("订单不能空");
        }
        Map<String, Object> resultMap = new HashMap<>();
        List<ShopOrderGoods> shopOrderGoodsList = orderGoodsService.findEvalListBy(orderId, 0);//评价状态0
        if (shopOrderGoodsList == null || shopOrderGoodsList.size() < 1) {
            resultMap.put("itemCount", shopOrderGoodsList.size());
            resultMap.put("itemList", EvaluationCenterGoodsResult.buildList(shopOrderGoodsList));
            return com.framework.loippi.utils.ApiUtils.success(resultMap);
        }
        resultMap.put("itemCount", shopOrderGoodsList.size());
        resultMap.put("itemList", EvaluationCenterGoodsResult.buildList(shopOrderGoodsList));
        return ApiUtils.success(resultMap);
    }

}
