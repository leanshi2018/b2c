package com.framework.loippi.controller.common;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.framework.loippi.controller.BaseController;
import com.framework.loippi.entity.common.ShopCommonExpress;
import com.framework.loippi.entity.order.ShopOrder;
import com.framework.loippi.entity.order.ShopOrderAddress;
import com.framework.loippi.entity.order.ShopOrderLogistics;
import com.framework.loippi.result.order.ShippingResult;
import com.framework.loippi.service.KuaidiService;
import com.framework.loippi.service.common.ShopCommonExpressService;
import com.framework.loippi.service.order.ShopOrderAddressService;
import com.framework.loippi.service.order.ShopOrderGoodsService;
import com.framework.loippi.service.order.ShopOrderLogisticsService;
import com.framework.loippi.service.order.ShopOrderService;
import com.framework.loippi.utils.ApiUtils;
import com.framework.loippi.utils.JacksonUtil;
import com.framework.loippi.utils.Xerror;

/**
 * API - 快递接口
 *
 * @author CZL
 * @version 1.0
 */
@Controller("apiKauidiController")
@Slf4j
public class KuaidiAPIController extends BaseController {

    @Autowired
    private ShopCommonExpressService commonExpressService;
    @Autowired
    private KuaidiService kuaidiService;
    @Resource
    private ShopOrderGoodsService shopOrderGoodsService;
    @Resource
    private ShopOrderService shopOrderService;
    @Resource
    private ShopOrderAddressService shopOrderAddressService;
    @Resource
    private ShopOrderLogisticsService shopOrderLogisticsService;

    /**
     * 查看物流
     *
     * @param expressCode 要查询的快递公司代码
     * @param shippingCode 要查询的快递单号
     */
    @RequestMapping("/api/kuaidi/shipping.json")
    @ResponseBody
    public String queryShipping(
        @RequestParam("shippingCode") String shippingCode,
        @RequestParam("expressCode") String expressCode,
        @RequestParam("orderId") Long orderId) {
        if (StringUtils.isBlank(shippingCode) || StringUtils.isBlank(expressCode) || orderId==null) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        ShopOrder shopOrder=shopOrderService.find(orderId);
        if (shopOrder==null){
            return ApiUtils.error("订单不存在");
        }
        ShopOrderAddress shopOrderAddress=shopOrderAddressService.find(shopOrder.getAddressId());
        ShopCommonExpress eCode = commonExpressService.find("eCode", expressCode);
        String kuaiInfo = kuaidiService.query(expressCode, shippingCode);
        Map mapType = JacksonUtil.convertMap(kuaiInfo);
        if (StringUtils.isBlank(kuaiInfo)) {
            return ApiUtils.error();
        }
        List<ShopOrderLogistics> shopOrderGoodslist=shopOrderLogisticsService.findList("orderId",orderId);
        List<ShippingResult> shippingResultList=ShippingResult.buildList(shopOrderGoodslist,shippingCode);
        Map<String, Object> map = new HashMap<>();
        map.put("orderSn", shopOrder.getOrderSn());
        map.put("orderId", orderId);
        map.put("status", shopOrder.getOrderState());
        map.put("shippingCode", shippingCode);
        map.put("receiverName", shopOrderAddress.getTrueName());
        map.put("receiverMobile", shopOrderAddress.getMobPhone());
        map.put("receiverAddress", shopOrderAddress.getAreaInfo()+shopOrderAddress.getAddress());
        map.put("shippingResultList", shippingResultList);
        map.put("express", Optional.ofNullable(eCode).map(ShopCommonExpress::getEName).orElse(""));
        map.put("detail", mapType);
        return ApiUtils.success(map);
    }

}
