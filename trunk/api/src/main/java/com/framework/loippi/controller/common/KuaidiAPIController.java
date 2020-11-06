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


    /*@RequestMapping("/api/kuaidi/aliShipping.json")
    @ResponseBody
    public String queryWlInfo(
        @RequestParam("shippingCode") String shippingCode,
        @RequestParam("expressCode") String expressCode,
        @RequestParam("lId") Long lId) {

        if (StringUtils.isBlank(shippingCode) || StringUtils.isBlank(expressCode) || orderId==null) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }

        String lognumber="";//快递公司代码: 可不填自动识别，填了查询更快
        Logistics logistics =null;
        if (lId!=null){
            //根据物流id查询物流编号
            logistics= orderService.selLogisticsById(lId);
            lognumber=logistics.getshippingCode();
        }
        String host = "http://wuliu.market.alicloudapi.com";
        String path = "/kdi";// kdi
        String method = "GET";
        String appcode = "";     // !填写你自己的AppCode
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "APPCODE " + appcode); //格式为:Authorization:APPCODE 83359fd73fe11248385f570e3c139xxx
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("no",shippingCode );// 这是是物流单号
        querys.put("type",lognumber );// 这是物流编码 非必填 会影响查询速度
        LogisticReturn logisticReturn =null;
        try {
            HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
            String s = EntityUtils.toString(response.getEntity());
            if (s.isEmpty() && s.equals("")){
                return new LogisticReturn();
            }
            JSONObject jsonObject = JSON.parseObject(s);
            //获取到返回的物流信息
            Object result = jsonObject.get("result");
            //将得到的物流信息转换为自定义的对象类
            logisticReturn=JSON.parseObject(result.toString(),LogisticReturn.class);
            //return ApiUtils.success(logisticReturn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiUtils.success(logisticReturn);
    }*/

}
