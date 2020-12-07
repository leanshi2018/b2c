package com.framework.loippi.controller.trade;

import net.sf.json.JSONObject;

import java.io.BufferedOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.RequestContext;

import com.alibaba.fastjson.JSON;
import com.framework.loippi.consts.Constants;
import com.framework.loippi.consts.OrderState;
import com.framework.loippi.consts.PaymentTallyState;
import com.framework.loippi.consts.WareHouseConsts;
import com.framework.loippi.controller.GenericController;
import com.framework.loippi.dto.OrderSplitDetail;
import com.framework.loippi.dto.ShippingDto;
import com.framework.loippi.dto.ShopExchangeOrderExcel;
import com.framework.loippi.dto.ShopOrderExcel;
import com.framework.loippi.entity.CreateExpressOrderJsonDTO;
import com.framework.loippi.entity.Principal;
import com.framework.loippi.entity.User;
import com.framework.loippi.entity.common.ShopCommonArea;
import com.framework.loippi.entity.common.ShopCommonExpress;
import com.framework.loippi.entity.coupon.CouponDetail;
import com.framework.loippi.entity.order.ShopOrder;
import com.framework.loippi.entity.order.ShopOrderAddress;
import com.framework.loippi.entity.order.ShopOrderDiscountType;
import com.framework.loippi.entity.order.ShopOrderGoods;
import com.framework.loippi.entity.order.ShopOrderLog;
import com.framework.loippi.entity.order.ShopOrderLogistics;
import com.framework.loippi.entity.order.ShopOrderSplit;
import com.framework.loippi.entity.order.ShopSpiritOrderInfo;
import com.framework.loippi.entity.product.ShopExpressSpecialGoods;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsGoods;
import com.framework.loippi.entity.product.ShopGoodsPresale;
import com.framework.loippi.entity.product.ShopGoodsSpec;
import com.framework.loippi.entity.user.RdGoodsAdjustment;
import com.framework.loippi.entity.user.RdMmAddInfo;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.entity.user.RdRanks;
import com.framework.loippi.entity.ware.RdWareAdjust;
import com.framework.loippi.entity.ware.RdWareOrder;
import com.framework.loippi.entity.ware.RdWarehouse;
import com.framework.loippi.enus.ShunFengOperation;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.pojo.order.SpiritOrderVo;
import com.framework.loippi.result.sys.OrderView;
import com.framework.loippi.service.KuaidiService;
import com.framework.loippi.service.RedisService;
import com.framework.loippi.service.ShunFengJsonExpressService;
import com.framework.loippi.service.UserService;
import com.framework.loippi.service.common.ShopCommonAreaService;
import com.framework.loippi.service.common.ShopCommonExpressNotAreaService;
import com.framework.loippi.service.common.ShopCommonExpressService;
import com.framework.loippi.service.coupon.CouponDetailService;
import com.framework.loippi.service.order.ShopOrderAddressService;
import com.framework.loippi.service.order.ShopOrderDiscountTypeService;
import com.framework.loippi.service.order.ShopOrderGoodsService;
import com.framework.loippi.service.order.ShopOrderLogService;
import com.framework.loippi.service.order.ShopOrderLogisticsService;
import com.framework.loippi.service.order.ShopOrderService;
import com.framework.loippi.service.order.ShopOrderSplitService;
import com.framework.loippi.service.order.ShopSpiritOrderInfoService;
import com.framework.loippi.service.product.ShopExpressSpecialGoodsService;
import com.framework.loippi.service.product.ShopGoodsGoodsService;
import com.framework.loippi.service.product.ShopGoodsPresaleService;
import com.framework.loippi.service.product.ShopGoodsService;
import com.framework.loippi.service.product.ShopGoodsSpecService;
import com.framework.loippi.service.user.RdGoodsAdjustmentService;
import com.framework.loippi.service.user.RdMmAddInfoService;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.service.user.RdMmRelationService;
import com.framework.loippi.service.user.RdRanksService;
import com.framework.loippi.service.ware.RdInventoryWarningService;
import com.framework.loippi.service.ware.RdWareAdjustService;
import com.framework.loippi.service.ware.RdWareOrderService;
import com.framework.loippi.service.ware.RdWarehouseService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.GoodsUtils;
import com.framework.loippi.utils.JacksonUtil;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.excel.ExportExcelUtils;
import com.framework.loippi.vo.order.ShopOrderVo;
import com.google.common.collect.Lists;

/**
 * Controller - 订单表
 *
 * @author zijing
 * @version 2.0
 */
@Controller("adminShopOrderController")
public class OrderSysController extends GenericController {

    @Resource
    private ShopOrderService orderService;
    @Resource
    private ShopOrderGoodsService shopOrderGoodsService;
    @Resource
    private ShopOrderAddressService orderAddressService;
    @Resource
    private RdRanksService rdRanksService;
    @Resource
    private RdMmAddInfoService rdMmAddInfoService;
    @Resource
    private ShopCommonAreaService areaService;
    @Resource
    private ShopOrderLogService orderLogService;
    @Resource
    private RdMmRelationService rdMmRelationService;
    @Resource
    private ShopGoodsGoodsService shopGoodsGoodsService;
    @Resource
    private ShopCommonExpressService commonExpressService;
    @Resource
    private ShopGoodsService shopGoodsService;
    @Resource
    private RedisService redisService;
    @Autowired
    private ShopGoodsSpecService shopGoodsSpecService;
    @Autowired
    private KuaidiService kuaidiService;
    @Resource
    private ShopOrderDiscountTypeService shopOrderDiscountTypeService;
    @Resource
    private RdMmBasicInfoService rdMmBasicInfoService;
    @Resource
    private ShopOrderLogisticsService shopOrderLogisticsService;
    @Resource
    private UserService userService;
    @Resource
    private RdInventoryWarningService inventoryWarningService;
    @Resource
    private RdWarehouseService rdWarehouseService;
    @Resource
    private RdWareAdjustService rdWareAdjustService;
    @Resource
    private RdGoodsAdjustmentService rdGoodsAdjustmentService;
    @Resource
    private ShopExpressSpecialGoodsService shopExpressSpecialGoodsService;
    @Resource
    private CouponDetailService couponDetailService;
    @Resource
    private ShopCommonExpressNotAreaService shopCommonExpressNotAreaService;
    @Resource
    private ShopGoodsPresaleService shopGoodsPresaleService;
    @Resource
    private ShopSpiritOrderInfoService shopSpiritOrderInfoService;
    @Resource
    private ShopOrderSplitService shopOrderSplitService;
    @Resource
    private ShunFengJsonExpressService shunFengJsonExpressService;
    @Resource
    private RdWareOrderService rdWareOrderService;
    // 订单编辑中
    private static final int ORDER_EDITING = 0;
    // 订单编辑完成
    private static final int ORDER_EDITED = 1;

    /**
     * 列表查询
     */
    @RequiresPermissions("admin:order:main")
    @RequestMapping(value = {"/admin/order/list"})
    public String list(Pageable pageable, ModelMap model, OrderView param) {
        // todo 团购订单的成团后才能显示
        // 自营订单
        param.setStoreId(0L);
        if ("45".equals(param.getOrderState())){
            param.setOrderState("40");
            param.setEvalsellerStatus(0);
        }
        pageable.setParameter(param);
        pageable.setOrderProperty("create_time");
        pageable.setOrderDirection(Order.Direction.DESC);
        model.addAttribute("page", orderService.listOrderView(pageable));
        if (Optional.ofNullable(param.getEvalsellerStatus()).orElse(1)==0 && "40".equals(param.getOrderState())){
            param.setOrderState("45");
        }
        model.addAttribute("param", param);
        return "/trade/shop_order/list";
    }

    @RequestMapping(value = {"/admin/order/otherList"})
    public String otherList(Pageable pageable, ModelMap model, OrderView param,String orderTypeStr ) {
        // 自营订单
        param.setStoreId(0L);
        pageable.setParameter(param);
        pageable.setOrderProperty("create_time");
        pageable.setOrderDirection(Order.Direction.DESC);
        if (!"".equals(Optional.ofNullable(orderTypeStr).orElse(""))){
          //换购订单
            param.setOrderType(5);
            model.addAttribute("type", 1);
        }else{
            model.addAttribute("type", 2);
        }
        if ( !"20".equals(Optional.ofNullable(param.getOrderState()).orElse("20"))){
            //param.setLogisticType(1);
        }
        if ("45".equals(param.getOrderState())){
            param.setOrderState("40");
            param.setEvalsellerStatus(0);
        }
        model.addAttribute("orderTypeStr", orderTypeStr);
        model.addAttribute("page", orderService.listOrderView(pageable));
        if (Optional.ofNullable(param.getEvalsellerStatus()).orElse(1)==0 && "40".equals(param.getOrderState())){
            param.setOrderState("45");
        }
        model.addAttribute("param", param);
        return "trade/shop_order/other_list";
    }


    private static String secretkey = "1073f238-1971-4890-bfc4-fd903d90d7eb10294";//秘钥
    private static String customerID = "10294";//客户编号
    private static Long spirit_goods_id = 6714107763837898752l;//白酒商品Id
    //private static Long spirit_goods_id = 6714063202381991936l;//白酒商品Id
    /**
     *  待发货订单连接第三方发货
     * @param request
     * @param response
     * @param ids
     * @param typeExperss 发货类型 1选择发货  0所有发货
     * @throws Exception
     */
    @RequestMapping(value = {"/admin/order/expressOrder"}, method = RequestMethod.POST)
    public void expressOrder(HttpServletRequest request, HttpServletResponse response, Long[] ids, String typeExperss) throws Exception{
       /* Long[] ids = new Long[1];
        ids[0] = 6562977106358177792l;//,Long[] ids
*/
        if (typeExperss.equals("1")){//选择发货
            System.out.println("进来了选择发货");
            if (ids.length>0){
                for (Long id : ids) {
                    ShopOrder shopOrder = orderService.find(id);
                    if (shopOrder.getLogisticType()==1){//快递
                        if (shopOrder.getOrderState()==20) {
                            if (id != null) {
                                List<ShopOrderGoods> orderGoodsLists = shopOrderGoodsService.listByOrderId(id);//订单所有商品
                                int a = 0;//是白酒1
                                int b = 0;//不是白酒1
                                if (orderGoodsLists.size()>0){
                                    for (ShopOrderGoods orderGoods : orderGoodsLists) {
                                        ShopGoods shopGoods = shopGoodsService.find(orderGoods.getGoodsId());
                                        if (shopGoods.getGoodsType()!=3){
                                           //不是组合
                                            if (!orderGoods.getGoodsId().equals(spirit_goods_id)){
                                                b=1;
                                            }
                                            if (orderGoods.getGoodsId().equals(spirit_goods_id)){
                                                a=1;
                                            }
                                        }else {
                                            ShopGoodsSpec goodsSpec = shopGoodsSpecService.find(orderGoods.getSpecId());
                                            Map<String, String> specMap = JacksonUtil.readJsonToMap(goodsSpec.getSpecGoodsSpec());
                                            Map<String, String> goodsMap = JacksonUtil.readJsonToMap(goodsSpec.getSpecName());
                                            Set<String> keySpec = specMap.keySet();
                                            Set<String> keyGoods = goodsMap.keySet();
                                            Iterator<String> itSpec = keySpec.iterator();
                                            Iterator<String> itGoods = keyGoods.iterator();

                                            while (itSpec.hasNext() ) {
                                                String specId1 = itSpec.next();//单品的规格id
                                                ShopGoodsSpec shopGoodsSpec = shopGoodsSpecService.find(new Long(specId1));
                                                //String goodId1 = itGoods.next();//单品的商品id
                                                if (!shopGoodsSpec.getGoodsId().equals(spirit_goods_id)){
                                                    b=1;
                                                }
                                                if (shopGoodsSpec.getGoodsId().equals(spirit_goods_id)){
                                                    a=1;
                                                }
                                            }
                                        }
                                    }
                                }
                                if (a==1&&b==0){//订单中只有白酒
                                    System.out.println("只有白酒");
                                    for (ShopOrderGoods orderGoods : orderGoodsLists) {
                                        ShopSpiritOrderInfo haveInfo = shopSpiritOrderInfoService.findByOrderIdAndSpecId(orderGoods.getOrderId(),orderGoods.getSpecId());
                                        if (haveInfo==null){
                                            ShopSpiritOrderInfo spiritOrderInfo = new ShopSpiritOrderInfo();
                                            spiritOrderInfo.setId(twiterIdService.getTwiterId());
                                            spiritOrderInfo.setOrderId(orderGoods.getOrderId());
                                            spiritOrderInfo.setGoodsId(orderGoods.getGoodsId());
                                            spiritOrderInfo.setSpecId(orderGoods.getSpecId());
                                            spiritOrderInfo.setGoodsNum(orderGoods.getGoodsNum());
                                            spiritOrderInfo.setSubmitState(0);
                                            spiritOrderInfo.setOrderShipState(0);
                                            spiritOrderInfo.setCreateTime(new Date());
                                            spiritOrderInfo.setOrderType(0);
                                            shopSpiritOrderInfoService.save(spiritOrderInfo);
                                        }
                                    }
                                }else {
                                    System.out.println("不只有白酒");
                                    Map<String, Object> resMap = orderShip(id);//发货返回信息
                                    String resultS = (String) resMap.get("res");
                                    if (!"".equals(resultS)) {
                                        if(resultS.substring(0,1).equals("{")){
                                            Map maps = (Map) JSON.parse(resultS);
                                            String success = (String) maps.get("success");//是否成功
                                            String orderSn = (String) maps.get("CsRefNo");//订单编号
                                            if (success.equals("success")) {//发货成功
                                                String trackingNo = (String) maps.get("TrackingNo");//运单号
                                                if (!Character.isDigit(trackingNo.charAt(0))) {//不是数字，就是发货失败，有可能在草稿箱
                                                    String failInfo = (String) maps.get("Info");//失败信息
                                                    System.out.println("failInfo");
                                                    orderService.updateOrderStatus(orderSn, 20, 20, failInfo, "");
                                                } else {
                                                    //TrackingNo第一个字符是数字
                                                    if (!"".equals(trackingNo)) {// 订单状态：待收货 提交状态：已提交 失败原因：""  +运单号
                                                        System.out.println("待收货");
                                                        Integer orderState = 30;
                                                        Integer submitStatus = 10;
                                                        String failInfo = "";
                                                        orderService.updateOrderStatus(orderSn, orderState, submitStatus, failInfo, trackingNo);
                                                    } else {//状订单态：仓库在备货 提交状态：已提交 失败原因：""
                                                        System.out.println("仓库在备货");
                                                        orderService.updateOrderStatus(orderSn, 25, 10, "", "");
                                                    }

                                                    List<Map<String, Object>> products = (List<Map<String, Object>>) resMap.get("Products");//发货的数据
                                                    /*for (Map<String, Object> product : products) {
                                                        String sku = (String) product.get("SKU");//发货商品规格编号
                                                        Integer quantity = Integer.valueOf(product.get("MaterialQuantity").toString());//发货商品数量
                                                        ShopGoodsSpec goodsSpec = shopGoodsSpecService.findByspecGoodsSerial(sku);//商品规格信息
                                                        Long goodsSpecId = goodsSpec.getId();//商品规格id
                                                        inventoryWarningService.updateInventoryByWareCodeAndSpecId(WareHouseConsts.COMPANY_WARE_CODE, goodsSpecId, quantity);
                                                    }*/

                                                    List<ShopOrderGoods> shopOrderGoodsList = new ArrayList<>();
                                                    List<ShopOrderGoods> orderGoodsList = (List<ShopOrderGoods>) resMap.get("orderGoods");
                                                    List<ShopOrderGoods> shopOrderGoods = updateOrderGoods(shopOrderGoodsList, orderGoodsList, trackingNo,44l);//需要修改订单商品信息
                                                    shopOrderGoodsService.updateBatchForShipmentNum(shopOrderGoods);//修改订单商品信息
                                                }

                                            }
                                            if (success.equals("failure")) {//发货失败   提交状态：提交失败 失败原因：failInfo
                                                String failInfo = (String) maps.get("Info");//失败信息
                                                System.out.println("failInfo");
                                                orderService.updateOrderStatus(orderSn, 20, 20, failInfo, "");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }else {
                        /*ShopOrder shopOrder1 = new ShopOrder();
                        shopOrder1.setId(shopOrder.getId());
                        shopOrder1.setOrderState(30);
                        orderService.update(shopOrder1);*/
                    }
                }
            }
        }

        if (typeExperss.equals("0")){//全部发货
            System.out.println("进来了全部发货");
            List<ShopOrder> orderList = orderService.findStatu20();//所有代发货订单
            for (ShopOrder shopOrder : orderList) {
                if (shopOrder.getLogisticType()==1){

                    List<ShopOrderGoods> orderGoodsLists = shopOrderGoodsService.listByOrderId(shopOrder.getId());//订单所有商品
                    int a = 0;//是白酒1
                    int b = 0;//不是白酒1
                    if (orderGoodsLists.size()>0){
                        for (ShopOrderGoods orderGoods : orderGoodsLists) {
                            ShopGoods shopGoods = shopGoodsService.find(orderGoods.getGoodsId());
                            if (shopGoods.getGoodsType()!=3){
                                if (!orderGoods.getGoodsId().equals(spirit_goods_id)){
                                    b=1;
                                }
                                if (orderGoods.getGoodsId().equals(spirit_goods_id)){
                                    a=1;
                                }
                            }else {
                                ShopGoodsSpec goodsSpec = shopGoodsSpecService.find(orderGoods.getSpecId());
                                Map<String, String> specMap = JacksonUtil.readJsonToMap(goodsSpec.getSpecGoodsSpec());
                                Map<String, String> goodsMap = JacksonUtil.readJsonToMap(goodsSpec.getSpecName());
                                Set<String> keySpec = specMap.keySet();
                                Set<String> keyGoods = goodsMap.keySet();
                                Iterator<String> itSpec = keySpec.iterator();
                                Iterator<String> itGoods = keyGoods.iterator();

                                while (itSpec.hasNext() ) {
                                    String specId1 = itSpec.next();//单品的规格id
                                    ShopGoodsSpec shopGoodsSpec = shopGoodsSpecService.find(new Long(specId1));
                                    //String goodId1 = itGoods.next();//单品的商品id
                                    if (!shopGoodsSpec.getGoodsId().equals(spirit_goods_id)){
                                        b=1;
                                    }
                                    if (shopGoodsSpec.getGoodsId().equals(spirit_goods_id)){
                                        a=1;
                                    }
                                }
                            }
                        }
                    }

                    if (a==1&&b==0){//订单中只有白酒
                        System.out.println("只有白酒");
                        for (ShopOrderGoods orderGoods : orderGoodsLists) {
                            ShopSpiritOrderInfo haveInfo = shopSpiritOrderInfoService.findByOrderIdAndSpecId(orderGoods.getOrderId(),orderGoods.getSpecId());
                            if (haveInfo==null){
                                ShopSpiritOrderInfo spiritOrderInfo = new ShopSpiritOrderInfo();
                                spiritOrderInfo.setId(twiterIdService.getTwiterId());
                                spiritOrderInfo.setOrderId(orderGoods.getOrderId());
                                spiritOrderInfo.setGoodsId(orderGoods.getGoodsId());
                                spiritOrderInfo.setSpecId(orderGoods.getSpecId());
                                spiritOrderInfo.setGoodsNum(orderGoods.getGoodsNum());
                                spiritOrderInfo.setSubmitState(0);
                                spiritOrderInfo.setOrderShipState(0);
                                spiritOrderInfo.setCreateTime(new Date());
                                spiritOrderInfo.setOrderType(0);
                                shopSpiritOrderInfoService.save(spiritOrderInfo);
                            }
                        }
                    }else {
                        System.out.println("不只有白酒");
                        Map<String, Object> resMap = orderShip(shopOrder.getId());//发货返回信息
                        String resultS = (String)resMap.get("res");
                        if (!"".equals(resultS)){
                            if(resultS.substring(0,1).equals("{")){
                                Map maps = (Map) JSON.parse(resultS);
                                String success = (String) maps.get("success");//是否成功
                                String orderSn = (String) maps.get("CsRefNo");//订单编号
                                if (success.equals("success")) {//发货成功
                                    String trackingNo = (String) maps.get("TrackingNo");//运单号
                                    if (!Character.isDigit(trackingNo.charAt(0))) {//不是数字，就是发货失败，有可能在草稿箱
                                        String failInfo = (String) maps.get("Info");//失败信息
                                        System.out.println("failInfo");
                                        orderService.updateOrderStatus(orderSn, 20, 20, failInfo, "");
                                    } else {
                                        //TrackingNo第一个字符是数字
                                        if (!"".equals(trackingNo)) {// 订单状态：待收货 提交状态：已提交 失败原因："" +运单号
                                            System.out.println("待收货");
                                            Integer orderState = 30;
                                            Integer submitStatus = 10;
                                            String failInfo = "";
                                            orderService.updateOrderStatus(orderSn, orderState, submitStatus, failInfo, trackingNo);
                                        } else {//状订单态：仓库在备货 提交状态：已提交 失败原因：""
                                            System.out.println("仓库在备货");
                                            orderService.updateOrderStatus(orderSn, 25, 10, "", "");
                                        }
                                        List<Map<String, Object>> products = (List<Map<String, Object>>) resMap.get("Products");//发货的数据
                                        /*for (Map<String, Object> product : products) {
                                            String sku = (String) product.get("SKU");//发货商品规格编号
                                            Integer quantity = Integer.valueOf(product.get("MaterialQuantity").toString());//发货商品数量
                                            ShopGoodsSpec goodsSpec = shopGoodsSpecService.findByspecGoodsSerial(sku);//商品规格信息
                                            Long goodsSpecId = goodsSpec.getId();//商品规格id
                                            inventoryWarningService.updateInventoryByWareCodeAndSpecId(WareHouseConsts.COMPANY_WARE_CODE, goodsSpecId, quantity);
                                        }*/

                                        List<ShopOrderGoods> shopOrderGoodsList = new ArrayList<>();
                                        List<ShopOrderGoods> orderGoodsList = (List<ShopOrderGoods>) resMap.get("orderGoods");
                                        List<ShopOrderGoods> shopOrderGoods = updateOrderGoods(shopOrderGoodsList, orderGoodsList, trackingNo,44l);//需要修改订单商品信息
                                        shopOrderGoodsService.updateBatchForShipmentNum(shopOrderGoods);//修改订单商品信息
                                    }
                                }
                                if (success.equals("failure")) {//发货失败   提交状态：提交失败 失败原因：failInfo
                                    String failInfo = (String) maps.get("Info");//失败信息
                                    System.out.println("failInfo");
                                    orderService.updateOrderStatus(orderSn, 20, 20, failInfo, "");
                                }
                            }
                        }
                    }

                }else {//自提
                    /*ShopOrder shopOrder1 = new ShopOrder();
                    shopOrder1.setId(shopOrder.getId());
                    shopOrder1.setOrderState(30);
                    orderService.update(shopOrder1);*/
                }
            }
        }

        System.out.println("发货完成！");

    }

    /**
     * 需要修改订单商品信息
     * @param orderGoodsList
     * @return
     */
    public List<ShopOrderGoods> updateOrderGoods(List<ShopOrderGoods> shopOrderGoodsNullList,List<ShopOrderGoods> orderGoodsList,String trackingNo,Long expressId) {
        for (ShopOrderGoods orderGoods : orderGoodsList) {
            ShopCommonExpress express = commonExpressService.find(expressId);

            ShopOrderGoods shopOrderGoods = new ShopOrderGoods();
            shopOrderGoods.setShippingExpressCode(Optional.ofNullable(express.getECode()).orElse(""));
            shopOrderGoods.setShippingExpressId(Optional.ofNullable(express.getId()).orElse(-1L));
            shopOrderGoods.setShippingExpressName(Optional.ofNullable(express.getEName()).orElse(""));
            shopOrderGoods.setShippingCode(Optional.ofNullable(trackingNo).orElse("-1"));
            shopOrderGoods.setShippingGoodsNum(orderGoods.getGoodsNum());
            shopOrderGoods.setId(orderGoods.getId());
            shopOrderGoodsNullList.add(shopOrderGoods);

            String adminName="";
            Subject subject = SecurityUtils.getSubject();
            if (subject != null) {
                Principal principal = (Principal) subject.getPrincipal();
                if (principal != null && principal.getId() != null) {
                    adminName=principal.getUsername();
                }
            }

            RdWarehouse warehouse = rdWarehouseService.findByCode(WareHouseConsts.COMPANY_WARE_CODE);
            //新增发货单
            RdWareAdjust rdWareAdjust = new RdWareAdjust();
            rdWareAdjust.setWareCode(warehouse.getWareCode());
            rdWareAdjust.setWareName(warehouse.getWareName());
            rdWareAdjust.setAdjustType("SOT");
            rdWareAdjust.setStatus(3);
            rdWareAdjust.setAutohrizeBy(adminName);
            rdWareAdjust.setAutohrizeTime(new Date());
            rdWareAdjust.setAutohrizeDesc("订单发货");
            rdWareAdjustService.insert(rdWareAdjust);

            ShopGoodsSpec shopGoodsSpec = shopGoodsSpecService.find(orderGoods.getSpecId());
            ShopGoods shopGoods = shopGoodsService.find(orderGoods.getGoodsId());
            //新增的发货商品详情
            RdGoodsAdjustment rdGoodsAdjustment = new RdGoodsAdjustment();
            rdGoodsAdjustment.setWid(rdWareAdjust.getWid());
            rdGoodsAdjustment.setSpecificationId(shopGoodsSpec.getId());
            rdGoodsAdjustment.setGoodId(shopGoodsSpec.getGoodsId());
            rdGoodsAdjustment.setGoodsName(shopGoods.getGoodsName());
            rdGoodsAdjustment.setSpecName(shopGoodsSpec.getSpecName());
            rdGoodsAdjustment.setGoodsSpec(shopGoodsSpec.getSpecGoodsSpec());
            rdGoodsAdjustment.setStockNow(shopGoodsSpec.getSpecGoodsStorage().longValue());
            rdGoodsAdjustment.setStockInto(Long.valueOf(orderGoods.getGoodsNum()));
            rdGoodsAdjustment.setCreateTime(shopGoods.getCreateTime());
            rdGoodsAdjustment.setWareCode(warehouse.getWareCode());
            rdGoodsAdjustment.setSign(1);
            rdGoodsAdjustment.setAutohrizeTime(new Date());
            rdGoodsAdjustment.setStatus(1L);
            rdGoodsAdjustmentService.insert(rdGoodsAdjustment);

            inventoryWarningService.updateInventoryByWareCodeAndSpecId(WareHouseConsts.COMPANY_WARE_CODE, orderGoods.getSpecId(), orderGoods.getGoodsNum());

            //新增订单商品物流信息表
            ShopOrderLogistics shopOrderLogistics = new ShopOrderLogistics();
            shopOrderLogistics.setGoodsId(shopGoodsSpec.getGoodsId());
            shopOrderLogistics.setGoodsImage(shopGoods.getGoodsImage());
            shopOrderLogistics.setGoodsName(shopGoods.getGoodsName());
            shopOrderLogistics.setGoodsType(shopGoods.getGoodsType());
            shopOrderLogistics.setOrderId(orderGoods.getOrderId());
            shopOrderLogistics.setSpecId(shopGoodsSpec.getId());
            shopOrderLogistics.setSpecInfo(orderGoods.getSpecInfo());
            shopOrderLogistics.setGoodsNum(orderGoods.getGoodsNum());
            shopOrderLogistics.setPpv(shopGoodsSpec.getPpv());
            shopOrderLogistics.setPrice(shopGoodsSpec.getSpecRetailPrice());
            shopOrderLogistics.setShippingExpressCode(Optional.ofNullable(express.getECode()).orElse(""));
            shopOrderLogistics.setShippingExpressId(Optional.ofNullable(express.getId()).orElse(-1L));
            shopOrderLogistics.setShippingCode(Optional.ofNullable(trackingNo).orElse("-1"));
            shopOrderLogistics.setId(twiterIdService.getTwiterId());
            shopOrderLogisticsService.insert(shopOrderLogistics);

        }
        return shopOrderGoodsNullList;
    }

    /**
     * 连接第三方发货
     * @param id
     * @return
     */
    public Map<String,Object> orderShip(Long id) throws Exception {
        Map<String,Object> map = new HashMap<String,Object>();//strorderinfo参数
        map.put("Style","1");
        map.put("CustomerID",customerID);
        //map.put("ChannelInfoID","CNZT-B");

        ShopOrder shopOrder = orderService.find(id);
        String orderSn = shopOrder.getOrderSn();//订单编号
        map.put("CsRefNo",orderSn);//订单号
        String buyerName = shopOrder.getBuyerName();//买家名称
        String buyerPhone = shopOrder.getBuyerPhone();//买家手机号码
        Long addressId = shopOrder.getAddressId();
        ShopOrderAddress orderAddress = orderAddressService.find(addressId);//订单地址信息
        String trueName = orderAddress.getTrueName();//收件人姓名
        map.put("ShipToName",trueName);

        String mobPhone = orderAddress.getMobPhone();//收件人电话号码
        map.put("ShipToPhoneNumber",mobPhone);

        Long provincedId = orderAddress.getProvinceId();//省级id
        Long cityId = orderAddress.getCityId();//市级ID
        Long areaId = orderAddress.getAreaId();//地区ID
        String zipCode = orderAddress.getZipCode();//邮编
        ShopCommonArea commonAreaProvinced = areaService.find(provincedId);//省
        ShopCommonArea commonAreaCity = areaService.find(cityId);//市
        ShopCommonArea commonAreaArea = areaService.find(areaId);//地区
        String provinced = commonAreaProvinced.getAreaName();
        String city = commonAreaCity.getAreaName();
        String area = commonAreaArea.getAreaName();
        String address = provinced+city+area+orderAddress.getAddress();//地址//收件人地址行1

        map.put("ShipToCountry",provinced);
        map.put("ShipToState",provinced);
        map.put("ShipToCity",city);
        map.put("ShipToAdress1",address);

        map.put("ShipToAdress2","");
        map.put("ShipToCompanyName","");
        map.put("OrderStatus","3");//订单状态--(草稿=1),(确认=3)
        map.put("TrackingNo","");
        map.put("CusRemark","");
        map.put("CODType","");
        map.put("CODMoney","");
        map.put("IDCardNo","");
        map.put("AgentNo","");
        map.put("BillQty","");
        map.put("WarehouseId","5200");

        List<ShopOrderGoods> orderGoodsList = shopOrderGoodsService.listByOrderId(id);//订单所有商品
        List<Map<String,Object>> productLists = new ArrayList<Map<String,Object>>();//商品list
        List<Long> gIdList = new ArrayList<Long>();
        for (ShopOrderGoods orderGoods : orderGoodsList) {

            int goodsNum = orderGoods.getGoodsNum();//商品数量
            Long specId = orderGoods.getSpecId();//商品规格索引id

            ShopGoodsSpec goodsSpec = shopGoodsSpecService.find(specId);//订单里商品的规格
            String specGoodsSpec = goodsSpec.getSpecGoodsSpec();
            //{"6544521286435999744":"24g"}
            String[] specs = specGoodsSpec.split(",");
            if (specs.length==1){
                Map<String,Object> productMap = new HashMap<String,Object>();//单个商品
                productMap.put("ProducingArea","");
                productMap.put("HSCode","");

                String[] splits = specs[0].split("\"");
                Long goodsId1 = goodsSpec.getGoodsId();//组合商品id
                Long specId1 = Long.valueOf(splits[1]);//商品规格id
                int total = 0;//商品数量
                List<ShopGoodsGoods> goodsGoodsList = shopGoodsGoodsService.findGoodsGoodsByGoodsId(goodsId1);//查看是否组合商品
                if (goodsGoodsList!=null&&goodsGoodsList.size()!=0&&goodsGoodsList.size()==1){//是组合商品

                    ShopGoodsSpec goodsSpec1 = shopGoodsSpecService.find(specId1);//参与组合商品规格数据
                    productMap.put("SKU",goodsSpec1.getSpecGoodsSerial());//物品SKU
                    productMap.put("Price",goodsSpec1.getSpecRetailPrice());//物品价格
                    productMap.put("Weight",goodsSpec1.getWeight());
                    ShopGoods shopGoods = shopGoodsService.find(goodsSpec1.getGoodsId());//参与组合商品信息
                    productMap.put("EnName",shopGoods.getGoodsName());//物品名称
                    productMap.put("CnName",shopGoods.getGoodsName());//物品名称
                    gIdList.add(shopGoods.getId());
                    for (ShopGoodsGoods goodsGoods : goodsGoodsList) {
                        int joinNum = goodsGoods.getJoinNum();//参与组合商品数量
                        total = joinNum*goodsNum;
                    }
                    productMap.put("MaterialQuantity",total);//物品数量

                }

                if (goodsGoodsList==null||goodsGoodsList.size()==0){//不是组合商品
                    total =goodsNum;
                    productMap.put("MaterialQuantity",total);//物品数量
                    productMap.put("SKU",goodsSpec.getSpecGoodsSerial());//物品SKU
                    productMap.put("Price",goodsSpec.getSpecRetailPrice());//物品价格
                    productMap.put("Weight",goodsSpec.getWeight());
                    ShopGoods shopGoods = shopGoodsService.find(goodsSpec.getGoodsId());//参与组合商品信息
                    productMap.put("EnName",shopGoods.getGoodsName());//物品名称
                    productMap.put("CnName",shopGoods.getGoodsName());//物品名称
                    gIdList.add(shopGoods.getId());
                }

                if (productLists.size()==0){
                    productLists.add(productMap);
                }else{
                    int h = 10000;
                    for (int i = 0; i < productLists.size(); i++) {
                        Map<String,Object> pMap = productLists.get(i);
                        if (pMap.get("SKU").equals(productMap.get("SKU"))) {
                            h = i;
                        }
                    }
                    if (h!=10000){//list里面有
                        Map<String,Object> p = productLists.get(h);//里面原来的数据
                        int quantity = (int)p.get("MaterialQuantity");//里面原来商品数量
                        total = quantity +total;//总的数量
                        p.put("MaterialQuantity",total);
                        //移除原来的
                        productLists.remove(h);
                        //添加合并数据
                        productLists.add(p);

                    }else{//list里面还没有
                        productLists.add(productMap);
                    }
                }

            }

            //{"6544521286435999744":"24g","6544522466675396608":"22g","6544414763865083904":"3#船长正红"}
            if (specs.length>1){
                Long goodsId = goodsSpec.getGoodsId();//组合商品id
                for (int j=0;j<specs.length;j++) {
                    Map<String,Object> productMap = new HashMap<String,Object>();
                    productMap.put("ProducingArea","");
                    productMap.put("HSCode","");

                    String[] splits = specs[j].split("\"");
                    Long specId2 = Long.valueOf(splits[1]);
                    ShopGoodsSpec goodsSpec1= shopGoodsSpecService.find(specId2);//参与组合商品规格数据
                    productMap.put("SKU",goodsSpec1.getSpecGoodsSerial());
                    productMap.put("Weight",goodsSpec1.getWeight());//物品SKU
                    productMap.put("Price",goodsSpec1.getSpecRetailPrice());//物品价格

                    Long goodsId1 = goodsSpec1.getGoodsId();//参与组合商品里商品id
                    ShopGoods shopGoods = shopGoodsService.find(goodsId1);//参与组合商品信息
                    productMap.put("EnName",shopGoods.getGoodsName());//物品名称
                    productMap.put("CnName",shopGoods.getGoodsName());//物品名称
                    gIdList.add(shopGoods.getId());
                    Map<String,Object> map1 = new HashMap<>();
                    map1.put("goodId",goodsId);
                    map1.put("combineGoodsId",goodsId1);
                    List<ShopGoodsGoods> goodsGoodsList = shopGoodsGoodsService.findGoodsGoodsList(map1);
                    ShopGoodsGoods goodsGoods = null;
                    System.out.println("sp"+specId2);
                    System.out.println("list"+goodsGoodsList);
                    if (goodsGoodsList.size()>0){
                        if (goodsGoodsList.size()==1){
                            goodsGoods = goodsGoodsList.get(0);
                        }else {
                            for (ShopGoodsGoods shopGoodsGoods : goodsGoodsList) {
                                if (shopGoodsGoods.getGoodsSpec().equals(specId2.toString())){
                                    goodsGoods = shopGoodsGoods;
                                }
                            }
                        }
                        if (goodsGoods==null){
                            throw new Exception("组合数据不全");
                        }
                    }else {
                        throw new Exception("组合数据不全");
                    }
                    int joinNum = goodsGoods.getJoinNum();//组合商品里商品数量
                    int total = joinNum*goodsNum;//总数量
                    productMap.put("MaterialQuantity",total);

                    if (productLists.size()==0){
                        productLists.add(productMap);
                    }else{
                        int h = 10000;
                        for (int i = 0; i < productLists.size(); i++) {
                            Map<String,Object> pMap = productLists.get(i);
                            if (pMap.get("SKU").equals(productMap.get("SKU"))) {
                                h = i;
                            }
                        }
                        if (h!=10000){//list里面有
                            Map<String,Object> p = productLists.get(h);//里面原来的数据
                            int quantity = (int)p.get("MaterialQuantity");//里面原来商品数量
                            total = quantity +total;//总的数量
                            p.put("MaterialQuantity",total);
                            //移除原来的
                            productLists.remove(h);
                            //添加合并数据
                            productLists.add(p);

                        }else{//list里面还没有
                            productLists.add(productMap);
                        }
                    }

                }
            }
        }


        List<ShopGoodsPresale> presaleAll = shopGoodsPresaleService.findAll();

/*********************************************排除洗发水*************************************************/
        /*List<Map<String,Object>> productListss = new ArrayList<Map<String,Object>>();//商品list
        for (Map<String, Object> product : productLists) {
            if (!product.get("SKU").equals("6972190330042")){//不是洗发水
                productListss.add(product);
            }
        }

        map.put("Products",productListss);*/

/**************************************选择快递********************************************************/
        String eExpressCode = "CNZT-B";//第三方物流单号
        //最大级数
        /*Integer macSort = commonExpressService.macSort();
        for (int i=1;i<=macSort;i++){
            //根据级数查快递
            ShopCommonExpress express = commonExpressService.findBySort(i);
            Long expressId = express.getId();
        }*/


        //特殊快递渠道商品
        List<ShopExpressSpecialGoods> specialGoodsList = shopExpressSpecialGoodsService.findByState(0);
        Map<String,String> specialGoodsMap = new HashMap<String,String>();
        for (ShopExpressSpecialGoods specialGoods : specialGoodsList) {
            String specGoodsSerial = specialGoods.getSpecGoodsSerial();
            ShopCommonExpress express = commonExpressService.findById(specialGoods.getExpressId());
            String expressCode = express.getEExpressCode();
            specialGoodsMap.put(specGoodsSerial,expressCode);
        }
/*******************************添加清洁剂瓶盖（6972190330202-1）*************************************/
        List<Map<String,Object>> productListss = new ArrayList<Map<String,Object>>();//商品list
        int cupNum = 0;
        for (Map<String, Object> product : productLists) {

            //剔除白酒
            String sgs = (String)product.get("SKU");
            List<ShopGoodsSpec> specList = shopGoodsSpecService.findListBySpecGoodsSerial(sgs);
            Long gId = 0l;
            Long sId = 0l;
            if (specList.size()>0){
                for (ShopGoodsSpec goodsSpec : specList) {
                    if (gIdList.contains(goodsSpec.getGoodsId())){
                        gId = goodsSpec.getGoodsId();
                        sId = goodsSpec.getId();
                    }
                }
            }

            if (gId.equals(spirit_goods_id)){//是白酒
                System.out.println("是白酒"+gId);
                ShopSpiritOrderInfo haveInfo = shopSpiritOrderInfoService.findByOrderIdAndSpecId(shopOrder.getId(),sId);
                if (haveInfo==null){
                    ShopSpiritOrderInfo spiritOrderInfo = new ShopSpiritOrderInfo();
                    spiritOrderInfo.setId(twiterIdService.getTwiterId());
                    spiritOrderInfo.setOrderId(shopOrder.getId());
                    spiritOrderInfo.setGoodsId(gId);
                    spiritOrderInfo.setSpecId(sId);
                    spiritOrderInfo.setGoodsNum((Integer) product.get("MaterialQuantity"));
                    spiritOrderInfo.setSubmitState(0);
                    spiritOrderInfo.setOrderShipState(1);
                    spiritOrderInfo.setCreateTime(new Date());
                    spiritOrderInfo.setOrderType(0);
                    shopSpiritOrderInfoService.save(spiritOrderInfo);
                }
            }else {
                productListss.add(product);
            }


            if (product.get("SKU").equals("6972190330202")){//是OLOMI橘油多效清洁剂
                Map<String,Object> productMap = new HashMap<String,Object>();//单个商品
                productMap.put("ProducingArea","");
                productMap.put("HSCode","");
                int quantity = (int)product.get("MaterialQuantity");//数量
                productMap.put("MaterialQuantity",quantity);//物品数量
                productMap.put("SKU","6972190330202-1");//物品SKU
                productMap.put("Price",0);//物品价格
                productMap.put("Weight",0);
                productMap.put("EnName","OLOMI橘油多效清洁剂-喷头");//物品名称
                productMap.put("CnName","OLOMI橘油多效清洁剂-喷头");//物品名称

                productListss.add(productMap);
            }

            if (product.get("SKU").equals("6972190330394")){//是OLOMI美容仪
                Map<String,Object> productMap = new HashMap<String,Object>();//单个商品
                productMap.put("ProducingArea","");
                productMap.put("HSCode","");
                int quantity = (int)product.get("MaterialQuantity");//数量
                productMap.put("MaterialQuantity",quantity);//物品数量
                productMap.put("SKU","6972190330394-1");//物品SKU
                productMap.put("Price",0);//物品价格
                productMap.put("Weight",0);
                productMap.put("EnName","OLOMI点阵射频热玛吉美容仪-手袋");//物品名称
                productMap.put("CnName","OLOMI点阵射频热玛吉美容仪-手袋");//物品名称

                productListss.add(productMap);
            }

            /*******************************送杯子*******************************************/
            //TODO
            /*if (product.get("SKU").equals("6942098967916") || product.get("SKU").equals("6942098967909")){//是OLOMI 益生菌固体饮料
                if (cupNum==0){
                    Map<String,Object> productMap = new HashMap<String,Object>();//单个商品
                    productMap.put("ProducingArea","");
                    productMap.put("HSCode","");
                    productMap.put("MaterialQuantity",1);//物品数量
                    productMap.put("SKU","6942098967909-1");//物品SKU
                    productMap.put("Price",0);//物品价格
                    productMap.put("Weight",0);
                    productMap.put("EnName","OLOMI摇摇杯");//物品名称
                    productMap.put("CnName","OLOMI摇摇杯");//物品名称

                    productListss.add(productMap);

                    cupNum =1;
                }
            }*/

            //查看是否是特殊快递渠道商品
            String sku = (String)product.get("SKU");
            if (specialGoodsMap.containsKey(sku)){//存在
                eExpressCode = specialGoodsMap.get(sku);
            }

            /************************************预售商品（有的话跳过该订单不发）*********************************************/
            //TODO
            /*if (presaleAll.size()>0){
                for (ShopGoodsPresale goodsPresale : presaleAll) {
                    if (product.get("SKU").equals(goodsPresale.getSpecGoodsSerial())){
                        Map<String,Object> resultMap = new HashMap<String,Object>();
                        resultMap.put("res","");
                        return resultMap;
                    }
                }
            }*/

        }
        map.put("ChannelInfoID",eExpressCode);

        map.put("Products",productListss);
/**********************************************************************************************/
        //map.put("Products",productLists);
        JSONObject jsonObject = JSONObject.fromObject(map);

        //发货
        String url = "http://119.23.163.12/webservice/APIWebService.asmx";// 提供接口的地址
        String soapaction = "http://tempuri.org/";// 域名，这是在server定义的
        String res = "";
        try {
            Service service = new Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(url);
            call.setOperationName(new QName(soapaction,"AddorUpdateOrders")); // 设置要调用哪个方法
            call.addParameter(new QName(soapaction, "strorderinfo"), // 设置要传递的参数
                    org.apache.axis.encoding.XMLType.XSD_STRING, javax.xml.rpc.ParameterMode.IN);
            call.addParameter(new QName(soapaction,"secretkey"),
                    org.apache.axis.encoding.XMLType.XSD_STRING, javax.xml.rpc.ParameterMode.IN);
            call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING);// （标准的类型）
            call.setUseSOAPAction(true);
            call.setSOAPActionURI(soapaction + "AddorUpdateOrders");
            res = String.valueOf(call.invoke(new Object[] { jsonObject.toString(),secretkey}));// 调用方法并传递参数
            System.out.println(res);
            //{ "success":"success","Info":"订单保存并提交成功!","CsRefNo":"AP353609897201369088","OrderNo":"M102940002962190","TrackingNo":"75165047072640","Enmessage":""}
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap.put("res",res);
        resultMap.put("Products",productLists);
        resultMap.put("orderGoods",orderGoodsList);

        return resultMap;
    }

    /*public Boolean findExpressNotArea(Long expressId,Long areaId){
        ShopCommonExpressNotArea notArea = shopCommonExpressNotAreaService.findByEIdAndAId(expressId,areaId);
        if (notArea!=null){
            return true;
        }else {
            return false;
        }
    }*/

    /**
     * 测试顺丰下单接口
     * @param model
     * @param request
     * @return
     */
    @RequestMapping("/admin/order/sf/createOrder")
    public String sfCreateOrder(ModelMap model,HttpServletRequest request) {
        System.out.println("进");
        CreateExpressOrderJsonDTO createExpressOrderReq = new CreateExpressOrderJsonDTO();
        // 如果提示重复下单，把这个编号变一下
        createExpressOrderReq.setOrderId("AP20200121181653954010");
        createExpressOrderReq.setRemark("酱品会·精品7箱，酱品会·珍品4箱，酱品会·甄藏2箱");
        // 寄件人信息
        /*createExpressOrderReq.setJcompany("深圳市乐安士有限公司");
        createExpressOrderReq.setJcontact("李大宝");
        createExpressOrderReq.setJmobile("18777276920");
        createExpressOrderReq.setJprovince("广东省");
        createExpressOrderReq.setJcity("深圳市");
        createExpressOrderReq.setJcounty("南山区");
        createExpressOrderReq.setJaddress("南山大道南园枫叶大厦");*/
        // 收件人信息
        createExpressOrderReq.setDcompany("个人");
        createExpressOrderReq.setDcontact("滕大宝");
        createExpressOrderReq.setDmobile("18938905541");
        createExpressOrderReq.setDprovince("广东省");
        createExpressOrderReq.setDcity("广州市");
        createExpressOrderReq.setDcounty("海珠区");
        createExpressOrderReq.setDaddress("广东省广州市海珠区宝芝林大厦701室");
        String result = shunFengJsonExpressService.shunFengOperationProcessor(createExpressOrderReq, ShunFengOperation.CRETE_ORDER);
        System.out.println("res==="+result);
        Map<String, Object> maps = (Map) JSON.parse(result);
        for (Map.Entry<String, Object> entry : maps.entrySet()) {
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }

        return Constants.MSG_URL;
    }

    /**
     * 测试顺丰路由查询接口
     * @param model
     * @param request
     * @param trackSn 顺丰运单号
     * @return
     */
    @RequestMapping("/admin/order/sf/searchOrder")
    public String sfSearchOrder(ModelMap model,HttpServletRequest request,@RequestParam(required = true, value = "trackSn")String trackSn) {
        System.out.println("进");
        if (trackSn==null||"".equals(trackSn)){
            model.addAttribute("err","顺丰运单号不能为空");
            return Constants.MSG_URL;
        }

        String result = shunFengJsonExpressService.shunFengOperationProcessor(trackSn, ShunFengOperation.ROUTE_SEARCH);
        System.out.println("res==="+result);
        Map maps = (Map) JSON.parse(result);
        /*
        *{
            "code": 200,
            "success": true,
            "msg": null,
            "data": [
                        {
                        "id": "160456410060719",
                        "orderId": "WB763002011051607331491461",
                        "mainMailno": "SF1020036320192",
                        "acceptTime": "2020-11-05 16:14:20",
                        "acceptAddress": "",
                        "remark": "派送成功",
                        "opcode": "80",
                        "createTime": "2020-11-05 16:15:01"
                        },
                        {
                        "id": "160456409372918",
                        "orderId": "WB763002011051607331491461",
                        "mainMailno": "SF1020036320192",
                        "acceptTime": "2020-11-05 16:14:16",
                        "acceptAddress": "",
                        "remark": "正在派件..",
                        "opcode": "44",
                        "createTime": "2020-11-05 16:14:53"
                        },
                        {
                        "id": "160456408886567",
                        "orderId": "WB763002011051607331491461",
                        "mainMailno": "SF1020036320192",
                        "acceptTime": "2020-11-05 16:13:55",
                        "acceptAddress": "",
                        "remark": "上门收件",
                        "opcode": "50",
                        "createTime": "2020-11-05 16:14:49"
                        }
                ]
            }
        * */
        String data = maps.get("data").toString();

        List<Object> list = JSON.parseArray(data);
        for (Object object : list) {
            Map <String,Object> ret = (Map<String, Object>) object;
            for (Map.Entry<String, Object> entry : ret.entrySet()) {
                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            }
        }

        return Constants.MSG_URL;
    }

    /**
     * 测试顺丰取消订单接口
     * @param model
     * @param request
     * @param orderId  订单编号orderSn
     * @return
     */
    @RequestMapping("/admin/order/sf/cancelOrder")
    public String sfCancelOrder(ModelMap model,HttpServletRequest request,@RequestParam(required = true, value = "orderId")String orderId) {
        System.out.println("进");
        if (orderId==null||"".equals(orderId)){
            model.addAttribute("err","顺丰运单号不能为空");
            return Constants.MSG_URL;
        }
        String result = shunFengJsonExpressService.shunFengOperationProcessor(orderId, ShunFengOperation.CANCEL_ORDER);
        System.out.println("res==="+result);
        Map<String, Object> maps = (Map) JSON.parse(result);
        for (Map.Entry<String, Object> entry : maps.entrySet()) {
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }

        return Constants.MSG_URL;
    }

    /**
     * 所以白酒订单发货
     * @param model
     * @param request
     * @return
     */
    @RequestMapping("/admin/order/sf/sfShipOrder")
    public String sfShipOrder(ModelMap model,HttpServletRequest request) throws Exception {

        //白酒发货
        List<ShopSpiritOrderInfo> shopSpiritOrderAll = shopSpiritOrderInfoService.findNoSubmitOrderAll();
        Map<String, List<SpiritOrderVo>> excelMap = new HashMap<String, List<SpiritOrderVo>>();
        for (ShopSpiritOrderInfo spiritOrderInfo : shopSpiritOrderAll) {

            Long id = spiritOrderInfo.getOrderId();//订单Id
            Long gId = spiritOrderInfo.getGoodsId();
            Long specId = spiritOrderInfo.getSpecId();
            Integer orderType = spiritOrderInfo.getOrderType();

            String orderSn = "";
            Long mCode = 0l;
            String trueName = "";
            String provinced = "";//省
            String city = "";//市
            String area = "";//地区
            String address = "";
            String mobPhone = "";
            if (orderType == null || orderType == 0) {
                ShopOrder shopOrder = orderService.find(id);
                if (shopOrder.getOrderState()==0){
                    shopSpiritOrderInfoService.updateSubmitStateAndMsgByOrderId(2,"订单已取消",shopOrder.getId());
                    continue;
                }
                orderSn = shopOrder.getOrderSn();//订单编号
                mCode = shopOrder.getBuyerId();
                String buyerName = shopOrder.getBuyerName();//买家名称
                String buyerPhone = shopOrder.getBuyerPhone();//买家手机号码
                Long addressId = shopOrder.getAddressId();
                ShopOrderAddress orderAddress = orderAddressService.find(addressId);
                trueName = orderAddress.getTrueName();//收件人姓名
                mobPhone = orderAddress.getMobPhone();//收件人电话号码
                Long provinceId = orderAddress.getProvinceId();//省级id
                Long cityId = orderAddress.getCityId();//市级ID
                Long areaId = orderAddress.getAreaId();//地区ID
                String zipCode = orderAddress.getZipCode();//邮编
                ShopCommonArea areaProvinced = areaService.find(provinceId);
                provinced = areaProvinced.getAreaName();//省
                ShopCommonArea areaCity = areaService.find(cityId);
                city = areaCity.getAreaName();//市
                ShopCommonArea areaArea = areaService.find(areaId);
                area = areaArea.getAreaName();//地区
                address = area + orderAddress.getAddress();//地址
            } else {
                RdWareOrder rdWareOrder = rdWareOrderService.find(id);
                if (rdWareOrder.getOrderState()==0){
                    shopSpiritOrderInfoService.updateSubmitStateAndMsgByOrderId(2,"订单已取消",rdWareOrder.getId());
                    continue;
                }
                orderSn = Optional.ofNullable(rdWareOrder.getOrderSn()).orElse("");
                mCode = Optional.ofNullable(new Long(rdWareOrder.getMCode())).orElse(0l);
                trueName = Optional.ofNullable(rdWareOrder.getConsigneeName()).orElse("");
                provinced = Optional.ofNullable(rdWareOrder.getProvinceCode()).orElse("");
                city = Optional.ofNullable(rdWareOrder.getCityCode()).orElse("");
                area = Optional.ofNullable(rdWareOrder.getCountryCode()).orElse("");
                address = provinced + city + area + Optional.ofNullable(rdWareOrder.getWareDetial()).orElse("");
                mobPhone = Optional.ofNullable(rdWareOrder.getWarePhone()).orElse("");
            }


            ShopGoods shopGoods = shopGoodsService.find(gId);
            ShopGoodsSpec spec = shopGoodsSpecService.find(specId);
            Map<String, String> specMap = JacksonUtil.readJsonToMap(spec.getSpecGoodsSpec());
            if (shopGoods.getGoodsType() != 3) {//非组合
                String specName = "";
                for (Map.Entry<String, String> entry : specMap.entrySet()) {
                    specName = specName + entry.getValue();
                }

                SpiritOrderVo orderExcel = new SpiritOrderVo();

                orderExcel.setSpecGoodsSerial(spec.getSpecGoodsSerial());
                orderExcel.setSpecName(specName);
                orderExcel.setGoodsNum(spiritOrderInfo.getGoodsNum());

                List<SpiritOrderVo> orderExcelList = new ArrayList<SpiritOrderVo>();
                if (!excelMap.containsKey(id.toString())) {
                    orderExcel.setOrderSn(orderSn);
                    orderExcel.setBuyerName(trueName);
                    orderExcel.setProvince(provinced);
                    orderExcel.setCity(city);
                    orderExcel.setArea(area);
                    orderExcel.setAddress(address);
                    orderExcel.setBuyerPhone(mobPhone);
                    orderExcelList.add(orderExcel);

                    if (spiritOrderInfo.getOrderShipState() == 0) {
                        //未发货==订单只有白酒
                        //修改订单信息
                    }

                } else {
                    orderExcelList = excelMap.get(id.toString());
                    SpiritOrderVo orderExcelOld = new SpiritOrderVo();
                    for (SpiritOrderVo spiritOrderExcel : orderExcelList) {
                        if (spiritOrderExcel.getSpecGoodsSerial().equals(spec.getSpecGoodsSerial())) {
                            orderExcelOld = spiritOrderExcel;
                        }
                    }
                    if (orderExcelOld == null) {
                        orderExcelList.add(orderExcel);
                    } else {
                        if (orderExcelOld.getGoodsNum() == null) {
                            orderExcelList.add(orderExcel);
                        } else {
                            Integer goodsNumOld = orderExcelOld.getGoodsNum();
                            orderExcelOld.setGoodsNum(goodsNumOld + orderExcel.getGoodsNum());
                            orderExcelList.add(orderExcelOld);
                        }
                    }
                }
                excelMap.put(id.toString(), orderExcelList);
                //修改shop_spirit_order_info信息
                spiritOrderInfo.setSubmitState(1);
                spiritOrderInfo.setUploadTime(new Date());
                shopSpiritOrderInfoService.update(spiritOrderInfo);
            } else {
                Set<String> keySpec = specMap.keySet();
                Iterator<String> itSpec = keySpec.iterator();
                while (itSpec.hasNext()) {
                    String specId1 = itSpec.next();//单品的规格id
                    ShopGoodsSpec spec1 = shopGoodsSpecService.find(new Long(specId1));
                    ShopGoods shopGoods1 = shopGoodsService.find(spec1.getGoodsId());

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("goodId", shopGoods.getId());
                    map.put("combineGoodsId", shopGoods1.getId());
                    List<ShopGoodsGoods> goodsGoodsList = shopGoodsGoodsService.findGoodsGoodsList(map);
                    ShopGoodsGoods goodsGoods = null;
                    if (goodsGoodsList.size() > 0) {
                        if (goodsGoodsList.size() == 1) {
                            goodsGoods = goodsGoodsList.get(0);
                        } else {
                            for (ShopGoodsGoods shopGoodsGoods : goodsGoodsList) {
                                if (shopGoodsGoods.getGoodsSpec().equals(specId1)) {
                                    goodsGoods = shopGoodsGoods;
                                }
                            }
                        }
                        if (goodsGoods == null) {
                            throw new Exception("组合数据不全");
                        }
                    } else {
                        throw new Exception("组合数据不全");
                    }

                    //参与组合数
                    int joinNum = goodsGoods.getJoinNum();//组合商品里商品数量
                    Integer total = spiritOrderInfo.getGoodsNum() * joinNum;

                    Map<String, String> specMap1 = JacksonUtil.readJsonToMap(spec1.getSpecGoodsSpec());
                    String specName = "";
                    for (Map.Entry<String, String> entry : specMap1.entrySet()) {
                        specName = specName + entry.getValue();
                    }

                    SpiritOrderVo orderExcel = new SpiritOrderVo();

                    orderExcel.setSpecGoodsSerial(spec1.getSpecGoodsSerial());
                    orderExcel.setSpecName(specName);
                    orderExcel.setGoodsNum(total);

                    List<SpiritOrderVo> orderExcelList = new ArrayList<SpiritOrderVo>();
                    if (!excelMap.containsKey(id.toString())) {
                        orderExcel.setOrderSn(orderSn);
                        orderExcel.setBuyerName(trueName);
                        orderExcel.setProvince(provinced);
                        orderExcel.setCity(city);
                        orderExcel.setArea(area);
                        orderExcel.setAddress(address);
                        orderExcel.setBuyerPhone(mobPhone);
                        orderExcelList.add(orderExcel);

                        if (spiritOrderInfo.getOrderShipState() == 0) {
                            //未发货==订单只有白酒
                            //修改订单信息
                        }

                    } else {
                        orderExcelList = excelMap.get(id.toString());
                        SpiritOrderVo orderExcelOld = new SpiritOrderVo();
                        for (SpiritOrderVo spiritOrderExcel : orderExcelList) {
                            if (spiritOrderExcel.getSpecGoodsSerial().equals(spec1.getSpecGoodsSerial())) {
                                orderExcelOld = spiritOrderExcel;
                            }
                        }
                        if (orderExcelOld == null) {
                            orderExcelList.add(orderExcel);
                        } else {
                            if (orderExcelOld.getGoodsNum() == null) {
                                orderExcelList.add(orderExcel);
                            } else {
                                Integer goodsNumOld = orderExcelOld.getGoodsNum();
                                orderExcelOld.setGoodsNum(goodsNumOld + total);
                                orderExcelList.add(orderExcelOld);
                            }
                        }
                    }
                    excelMap.put(id.toString(), orderExcelList);
                }
                //修改shop_spirit_order_info信息
                spiritOrderInfo.setSubmitState(1);
                spiritOrderInfo.setUploadTime(new Date());
                shopSpiritOrderInfoService.update(spiritOrderInfo);
            }
        }

        if (excelMap != null && excelMap.size() > 0) {

            // 遍历
            for (Map.Entry<String, List<SpiritOrderVo>> entry : excelMap.entrySet()) {
                String orderSn = "";
                String dcontact = "";//收件人
                String dprovinced = "";//省
                String dcity = "";//市
                String dcounty = "";//地区
                String daddress = "";
                String dmobile = "";
                String remark = "";//备注 （发货商品类型名字+数量）
                List<SpiritOrderVo> orderExcelList = entry.getValue();
                int num = 0;
                for (SpiritOrderVo a : orderExcelList) {
                    num = num+a.getGoodsNum();
                    if (a.getOrderSn() != null && !"".equals(a.getOrderSn())) {
                        orderSn = a.getOrderSn();
                    }
                    if (a.getBuyerName() != null && !"".equals(a.getBuyerName())) {
                        dcontact = a.getBuyerName();
                    }
                    if (a.getProvince() != null && !"".equals(a.getProvince())) {
                        dprovinced = a.getProvince();
                    }
                    if (a.getCity() != null && !"".equals(a.getCity())) {
                        dcity = a.getCity();
                    }
                    if (a.getArea() != null && !"".equals(a.getArea())) {
                        dcounty = a.getArea();
                    }
                    if (a.getAddress() != null && !"".equals(a.getAddress())) {
                        daddress = a.getAddress();
                    }
                    if (a.getBuyerPhone() != null && !"".equals(a.getBuyerPhone())) {
                        dmobile = a.getBuyerPhone();
                    }
                    if (a.getSpecName() != null && !"".equals(a.getSpecName())) {
                        String[] split = a.getSpecName().split("·");
                        if ("".equals(remark)) {
                            remark = remark + split[1] + a.getGoodsNum().toString() + "箱";
                        } else {
                            remark = remark + "," + split[1] + a.getGoodsNum().toString() + "箱";
                        }
                    }
                }

                CreateExpressOrderJsonDTO createExpressOrderReq = new CreateExpressOrderJsonDTO();
                // 如果提示重复下单，把这个编号变一下
                createExpressOrderReq.setOrderId(orderSn);
                createExpressOrderReq.setRemark(remark);
                createExpressOrderReq.setPackageNumber(num);
                // 收件人信息
                createExpressOrderReq.setDcompany("个人");
                createExpressOrderReq.setDcontact(dcontact);
                createExpressOrderReq.setDmobile(dmobile);
                createExpressOrderReq.setDprovince(dprovinced);
                createExpressOrderReq.setDcity(dcity);
                createExpressOrderReq.setDcounty(dcounty);
                createExpressOrderReq.setDaddress(daddress);
                String result = shunFengJsonExpressService.shunFengOperationProcessor(createExpressOrderReq, ShunFengOperation.CRETE_ORDER);
                System.out.println("res===" + result);
                Map<String, Object> maps = (Map) JSON.parse(result);
                for (Map.Entry<String, Object> entry1 : maps.entrySet()) {
                    System.out.println("Key = " + entry1.getKey() + ", Value = " + entry1.getValue());
                }

                String substring = orderSn.substring(0, 2);
                String success = maps.get("success").toString();
                if (success.equals("true")) {//成功
                    String trackSnList = maps.get("data").toString();//运单号
                    String[] split = trackSnList.split(",");
                    String trackSn = split[0];

                    //修改shop_spirit_order_info 数据  加入运单号
                    List<ShopSpiritOrderInfo> list = null;
                    if (!substring.equals("DH")){
                        ShopOrder order = orderService.findByOrderSn(orderSn);
                        shopSpiritOrderInfoService.updateTrackSnByOrderId(order.getId(), trackSn);
                        list = shopSpiritOrderInfoService.findByOrderId(order.getId());
                    }else {
                        RdWareOrder order = rdWareOrderService.findBySn(orderSn);
                        shopSpiritOrderInfoService.updateTrackSnByOrderId(order.getId(), trackSn);
                        list = shopSpiritOrderInfoService.findByOrderId(order.getId());
                    }
                    Integer flag = 1;
                    Integer orderType = 2;
                    for (ShopSpiritOrderInfo info : list) {
                        Integer shipState = info.getOrderShipState();
                        if (shipState == 0) {
                            flag = 0;
                        }
                        if (info.getOrderType() == null) {
                            orderType = 0;
                        } else {
                            orderType = info.getOrderType();
                        }
                    }
                    if (flag == 0 && orderType == 0) {//订单未发货且是app订单
                        Integer orderState = 30;
                        Integer submitStatus = 10;
                        String failInfo = "";
                        orderService.updateOrderStatus(orderSn, orderState, submitStatus, failInfo, trackSn);

                        List<ShopOrderGoods> orderGoodsList = null;
                        if (!substring.equals("DH")) {
                            ShopOrder order = orderService.findByOrderSn(orderSn);
                            orderGoodsList = shopOrderGoodsService.listByOrderId(order.getId());//订单所有商品
                        }else {
                            RdWareOrder order = rdWareOrderService.findBySn(orderSn);
                            orderGoodsList = shopOrderGoodsService.listByOrderId(order.getId());//订单所有商品
                        }

                        List<ShopOrderGoods> shopOrderGoodsList = new ArrayList<>();
                        List<ShopOrderGoods> shopOrderGoods = updateOrderGoods(shopOrderGoodsList, orderGoodsList, trackSn, 29l);//需要修改订单商品信息
                        shopOrderGoodsService.updateBatchForShipmentNum(shopOrderGoods);//修改订单商品信息
                    } else {
                        if (flag == 1 && orderType == 0) {//订单已发货且是app订单
                            //覆盖订单中的快递编号

                            Map<String, Object> map = new HashMap<>();
                            map.put("orderSn", orderSn);
                            map.put("shippingCode", trackSn);
                            ShopCommonExpress express = commonExpressService.find(29l);
                            map.put("shippingExpressCode", Optional.ofNullable(express.getECode()).orElse(""));
                            map.put("shippingExpressId", Optional.ofNullable(express.getId()).orElse(-1L));
                            map.put("shippingName", Optional.ofNullable(express.getEName()).orElse(""));
                            map.put("shippingTime", new Date());
                            orderService.updateOrderShipping(orderSn, trackSn, 29l);
                        }
                    }

                } else {
                    String msg = maps.get("msg").toString();
                    //修改shop_spirit_order_info
                    if (!substring.equals("DH")) {
                        ShopOrder order = orderService.findByOrderSn(orderSn);
                        shopSpiritOrderInfoService.updateSubmitStateAndMsgByOrderId(0, msg, order.getId());
                    }else {
                        RdWareOrder order = rdWareOrderService.findBySn(orderSn);
                        shopSpiritOrderInfoService.updateSubmitStateAndMsgByOrderId(0, msg, order.getId());
                    }
                }
            }
        }

        System.out.println("白酒发货完成");
        model.addAttribute("msg","发货成功");
        return Constants.MSG_URL;
    }


    /**
     * 详情
     */
    @RequiresPermissions("admin:order:main")
    @RequestMapping(value = "/admin/order/view", method = RequestMethod.GET)
    public String view(@RequestParam long id, ModelMap model,Integer type,Integer orderType,HttpServletRequest request) {
        // 查询包括商品和地址
        ShopOrderVo orderVo=new ShopOrderVo();
        try {
             orderVo = orderService.findWithAddrAndGoods(id);
        }catch (Exception e){
            addMessage(model, e.getMessage());
            model.addAttribute("noAuto", true);
            model.addAttribute("referer", request.getHeader("Referer"));
            model.addAttribute("err", e.getMessage());
            return Constants.MSG_URL;
        }

        // 地区-修改用户地址用
        Paramap paramap = Paramap.create().put("isDel", 0).put("areaParentId", 0);
        List<ShopCommonArea> areas = areaService.findList(paramap);
        RdMmRelation rdMmRelation=rdMmRelationService.find("mmCode",orderVo.getBuyerId());
        RdMmBasicInfo rdMmBasicInfo=rdMmBasicInfoService.find("mmCode",orderVo.getBuyerId());
        if (rdMmRelation.getRank()!=null){
            RdRanks shopMemberGrade=rdRanksService.find("rankId",rdMmRelation.getRank());
            model.addAttribute("shopMemberGrade", shopMemberGrade);
        }
        if (Optional.ofNullable(orderVo.getLogisticType()).orElse(0)==2){
            model.addAttribute("sincelift", "sincelift");
        }

        if (orderVo.getAddress()==null){
            RdMmAddInfo shopMemberAddress=rdMmAddInfoService.find("aid",-1);
            model.addAttribute("shopMemberAddress", shopMemberAddress);
        }else {
            ShopOrderAddress shopOrderAddress = orderVo.getAddress();
            RdMmAddInfo shopMemberAddress=rdMmAddInfoService.find("aid",shopOrderAddress.getMentionId());
            model.addAttribute("shopMemberAddress", shopMemberAddress);
        }
        model.addAttribute("type", type);
        if (orderVo.getOrderType()==5){
            model.addAttribute("orderType", 5);
        }
        if (redisService.get(id+"orderVo")!=null && orderVo.getOrderState()==10){
            String info=orderVo.getDeliverExplain();
            orderVo=redisService.get(id+"orderVo",ShopOrderVo.class);
            orderVo.setDeliverExplain(info);
        }
        if (redisService.get(id+"")==null&& orderVo.getOrderState()==10){
            redisService.save(id+"",orderVo.getShopOrderGoods());
        }else if (redisService.get(id+"")!=null && orderVo.getOrderState()==10){
            orderVo.setShopOrderGoods(redisService.get(id+"",List.class));
            model.addAttribute("info", "info");
            List<ShopOrderDiscountType> shopOrderDiscountTypeList=shopOrderDiscountTypeService.findList("totalPpv",orderVo.getFixedPpv());
            model.addAttribute("shopOrderDiscountTypeList", shopOrderDiscountTypeList);
            model.addAttribute("shopOrderTypeId", orderVo.getShopOrderTypeId());
        }
        CouponDetail couponDetail = couponDetailService.find("useOrderId",orderVo.getId());
        if(couponDetail!=null){
            model.addAttribute("useCouponFlag",true);
            model.addAttribute("couponName",couponDetail.getCouponName());
            model.addAttribute("couponSn",couponDetail.getCouponSn());
            model.addAttribute("couponDiscount", orderVo.getCouponDiscount());
        }else {
            model.addAttribute("useCouponFlag",false);
            model.addAttribute("couponName",null);
            model.addAttribute("couponSn",null);
            model.addAttribute("couponDiscount", BigDecimal.ZERO);
        }
        model.addAttribute("order", orderVo);
        model.addAttribute("areas", areas);
        model.addAttribute("shopMember", rdMmBasicInfo);
        if(orderVo.getSplitFlag()!=null&&orderVo.getSplitFlag()==1){
            List<ShopOrderSplit> list = shopOrderSplitService.findList(Paramap.create().put("orderId",orderVo.getId()).put("buyFlag",2).put("status",1));
            ArrayList<String> strings = new ArrayList<>();
            for (ShopOrderSplit shopOrderSplit : list) {
                strings.add(shopOrderSplit.getMmCode());
            }
            model.addAttribute("splitFlag", 1);
            model.addAttribute("splitCodeList",strings);
        }else {
            model.addAttribute("splitFlag", 0);
            model.addAttribute("splitCodeList", new ArrayList<String>());
        }
        if (Optional.ofNullable(type).orElse(0)==1){

            return "/trade/shop_order/edit";
        }

        return "/trade/shop_order/view";
    }

    /**
     * 查询全部自提地址
     */
    @RequiresPermissions("admin:order:main")
    @RequestMapping(value = "/admin/order/mentionAddressAll", method = RequestMethod.GET)
    public String mentionAddressAll(@RequestParam(required = false, value = "pageNo", defaultValue = "1") Integer pageNo,
                                    @RequestParam(required = false, value = "provinceCode") String provinceCode,
                                    @RequestParam(required = false, value = "phone") String phone,
                                    ModelMap model,HttpServletRequest request) {
        Pageable pageable = new Pageable(pageNo, 20);
        pageable.setParameter(Paramap.create().put("aid", 0l).put("addProvinceCode",provinceCode).put("phone",phone));
        pageable.setOrderDirection(Order.Direction.DESC);
        /*List<RdMmAddInfo> lists = rdMmAddInfoService.findMentionAddrListByPhoneAndCode(provinceCode,phone);
        if (lists.size()==0){
            showErrorJson("自提地址为空");
        }*/
        model.addAttribute("page", rdMmAddInfoService.findMentionAddrListByPage(pageable));
        //showSuccessJson(rdMmAddInfoService.findMentionAddrListByPage(pageable));
        //model.addAttribute("page", lists);
        //showSuccessJson(lists);

        return "/trade/shop_order/address_search";
    }

    /**
     * 修改自提点
     */
    @RequiresPermissions("admin:order:main")
    @RequestMapping("/admin/order/updateOrdermentionAddress")
    @ResponseBody
    public String updateOrdermentionAddress(@RequestParam Long orderId,
                                            @RequestParam Long aId,
                                            HttpServletRequest request) {
        RequestContext requestContext = new RequestContext(request);
        ShopOrder shopOrder = orderService.find(orderId);
        if (shopOrder==null){
            showErrorJson(requestContext.getMessage("delivery_refuse_states_error"));
            return json;
        }

        if (shopOrder.getOrderState().longValue()==10||shopOrder.getOrderState().longValue()==20){
            Long addressId = shopOrder.getAddressId();
            ShopOrderAddress orderAddress = new ShopOrderAddress();
            orderAddress.setId(addressId);
            orderAddress.setMentionId(aId);
            orderAddressService.update(orderAddress);
            //将成功的信号传导前台
            showSuccessJson(requestContext.getMessage("delivery_save"));
            return json;
        }else {
            showErrorJson("该订单状态不在修改范围");
            return json;
        }
    }


    @RequestMapping(value = "/admin/order/otherView", method = RequestMethod.GET)
    public String otherView(@RequestParam long id, ModelMap model,Integer type) {
        // 查询包括商品和地址
        ShopOrderVo orderVo = orderService.findWithAddrAndGoods(id);
//        RdMmBasicInfo rdMmBasicInfo=rdMmBasicInfoService.find("mmCode",orderVo.getBuyerId());
        if (Optional.ofNullable(orderVo.getLogisticType()).orElse(0)==2){
            model.addAttribute("sincelift", "sincelift");
        }
        model.addAttribute("order", orderVo);
//        model.addAttribute("shopMember", rdMmBasicInfo);

        return "trade/shop_order/other_view";
    }

    /**
     * 修改订单发货信息
     */
    @RequiresPermissions("admin:order:main")
    @RequestMapping("/admin/order/updateOrderAddress")
    @ResponseBody
    public String updateOrderAddress(ShopOrderAddress orderAddress, @RequestParam long orderId,
                                     HttpServletRequest request) {
        RequestContext requestContext = new RequestContext(request);
        // 验证提交数据有效性
        if (!beanValidatorForJson(orderAddress)) {
            return json;
        }
        // 未发货可以修改, 发货后不能修改
        int[] canModifyAddressStates = {OrderState.ORDER_STATE_NO_PATMENT, OrderState.ORDER_STATE_UNFILLED};
        // 可以修改收货地址的数量
        Long count = orderService
                .count(Paramap.create().put("id", orderId).put("orderStates", canModifyAddressStates).put("storeId", 0L));
        if (count == 1) {
            ShopOrderVo orderVo=new ShopOrderVo();
            if (redisService.get(orderId+"orderVo")!=null ){
                orderVo=redisService.get(orderId+"orderVo",ShopOrderVo.class);
            }else{
                orderVo = orderService.findWithAddrAndGoods(orderId);
            }
            if (orderVo!=null && orderVo.getOrderState()==10){
                orderVo.setAddress(orderAddress);
                orderVo=orderService.modifyOrderCalculatePrice(orderVo,orderId,orderVo.getShopOrderTypeId());
                redisService.save(orderId+"orderVo",orderVo);
            }
            orderAddressService.update(orderAddress);
            ShopOrder shopOrder=new ShopOrder();
            shopOrder.setId(orderId);
            shopOrder.setIsModify(1);
            orderService.update(shopOrder);
            //将成功的信号传导前台
            showSuccessJson(requestContext.getMessage("delivery_save"));
        } else {
            showErrorJson(requestContext.getMessage("delivery_refuse_states_error"));
        }
        return json;
    }

    /**
     * 修改订单取货方式
     */
    @RequiresPermissions("admin:order:main")
    @RequestMapping("/admin/order/updateLogisticType")
    @ResponseBody
    public String updateLogisticType(Integer logisticType , @RequestParam long orderId,
                                     HttpServletRequest request) {
        RequestContext requestContext = new RequestContext(request);
        // 验证提交数据有效性
        if (logisticType==null) {
            return json;
        }
        // 未发货可以修改, 发货后不能修改
        int[] canModifyAddressStates = {OrderState.ORDER_STATE_NO_PATMENT, OrderState.ORDER_STATE_UNFILLED};
        // 可以修改收货地址的数量
        Long count = orderService
                .count(Paramap.create().put("id", orderId).put("orderStates", canModifyAddressStates).put("storeId", 0L));
        if (count == 1) {
            ShopOrder shopOrder=new ShopOrder();
            shopOrder.setId(orderId);
            shopOrder.setIsModify(1);
            if (logisticType==1){
                logisticType=2;
            }else if (logisticType==2){
                logisticType=1;
            }
            shopOrder.setLogisticType(logisticType);
            ShopOrderVo orderVo=new ShopOrderVo();
            if (redisService.get(orderId+"orderVo")!=null ){
                orderVo=redisService.get(orderId+"orderVo",ShopOrderVo.class);
            }else{
                orderVo = orderService.findWithAddrAndGoods(orderId);
            }
            if (orderVo!=null && orderVo.getOrderState()==10){
                orderVo.setLogisticType(logisticType);
                orderVo=orderService.modifyOrderCalculatePrice(orderVo,orderId,orderVo.getShopOrderTypeId());
                redisService.save(orderId+"orderVo",orderVo);
            }
            orderService.update(shopOrder);
            //将成功的信号传导前台
            showSuccessJson(requestContext.getMessage("delivery_save"));
        } else {
            showErrorJson(requestContext.getMessage("delivery_refuse_states_error"));
        }
        return json;
    }

    /**
     * 修改订单取货方式
     */
    @RequiresPermissions("admin:order:main")
    @RequestMapping("/admin/order/updateLogisticTypeNew")
    @ResponseBody
    public String updateLogisticTypeNew(Integer logisticType , @RequestParam Long orderId,@RequestParam(required = false, value = "aId", defaultValue = "-1l") Long aId,
                                     HttpServletRequest request) {
        RequestContext requestContext = new RequestContext(request);
        // 验证提交数据有效性
        if (logisticType==null) {
            return json;
        }
        // 未发货可以修改, 发货后不能修改
        int[] canModifyAddressStates = {OrderState.ORDER_STATE_NO_PATMENT, OrderState.ORDER_STATE_UNFILLED};
        // 可以修改收货地址的数量
        Long count = orderService
                .count(Paramap.create().put("id", orderId).put("orderStates", canModifyAddressStates).put("storeId", 0L));
        if (count == 1) {
            ShopOrder shopOrder1 = orderService.find(orderId);
            Integer orderState = shopOrder1.getOrderState();

            ShopOrder shopOrder=new ShopOrder();
            shopOrder.setId(orderId);
            shopOrder.setIsModify(1);
            if (logisticType==1){
                logisticType=2;
                //是否已付款
                if (orderState==OrderState.ORDER_STATE_NO_PATMENT){
                    showErrorJson(requestContext.getMessage("delivery_refuse_states_error"));
                    return json;
                }

                //修改订单表地址
                Long addressId = shopOrder1.getAddressId();
                ShopOrderAddress orderAddress = new ShopOrderAddress();
                orderAddress.setId(addressId);
                orderAddress.setAreaId(-1l);
                orderAddress.setCityId(-1l);
                orderAddress.setAreaInfo("自提没有保存收货地址");
                orderAddress.setProvinceId(-1l);
                orderAddress.setMentionId(aId);
                orderAddressService.update(orderAddress);

                //修改订单状态为待收货
                shopOrder.setOrderState(OrderState.ORDER_STATE_UNFILLED);
            }else if (logisticType==2){
                logisticType=1;
            }
            shopOrder.setLogisticType(logisticType);
            ShopOrderVo orderVo=new ShopOrderVo();
            if (redisService.get(orderId+"orderVo")!=null ){
                orderVo=redisService.get(orderId+"orderVo",ShopOrderVo.class);
            }else{
                orderVo = orderService.findWithAddrAndGoods(orderId);
            }
            if (orderVo!=null && orderVo.getOrderState()==10){
                orderVo.setLogisticType(logisticType);
                orderVo=orderService.modifyOrderCalculatePrice(orderVo,orderId,orderVo.getShopOrderTypeId());
                redisService.save(orderId+"orderVo",orderVo);
            }
            orderService.update(shopOrder);

            //将成功的信号传导前台
            showSuccessJson(requestContext.getMessage("delivery_save"));
        } else {
            showErrorJson(requestContext.getMessage("delivery_refuse_states_error"));
        }
        return json;
    }


    /**
     * 查询未发货数量
     */
    @RequiresPermissions("admin:order:main")
    @RequestMapping(value = "/admin/order/countNotShipped", method = RequestMethod.GET)
    @ResponseBody
    public String countNoShipped() {
        Long count = orderService
                .count(Paramap.create().put("orderState", OrderState.ORDER_STATE_UNFILLED).put("storeId", 0L));
        showSuccessJson(count.toString());
        return json;
    }

    /**
     * 取消订单
     */
    @RequiresPermissions("admin:order:main")
    @RequestMapping(value = "/admin/order/cancel", method = RequestMethod.POST)
    public String cancel(@RequestParam long id, ModelMap model, HttpServletRequest request,String message) {
        Principal principal = userService.getPrincipal();
        User user = userService.find(principal.getId());
        ShopOrder order = orderService.find(id);
        /*if (order.getOrderType()==5){
            addMessage(model, "取消失败，换购订单不可取消");
            model.addAttribute("noAuto", true);
            model.addAttribute("referer", request.getHeader("Referer"));
        }else {*/
            orderService.updateCancelOrder(id, Constants.OPERATOR_ADMINISTRATOR, principal.getId(), PaymentTallyState.PAYMENTTALLY_TREM_MB,message,user.getUsername());
            addMessage(model, "取消成功");
            model.addAttribute("noAuto", true);
            model.addAttribute("referer", request.getHeader("Referer"));
        /*}*/
        return Constants.MSG_URL;
    }

    /**
     * 订单完成-待收货状态下, 点击完成, 确认收货, 订单完成
     */
    @RequiresPermissions("admin:order:main")
    @RequestMapping(value = "/admin/order/finish", method = RequestMethod.GET)
    public String finish(@RequestParam long orderId, ModelMap model, HttpServletRequest request) {
        Principal principal = (Principal) SecurityUtils.getSubject().getPrincipal();
        orderService.updateFinishOrder(orderId, principal.getId(), Constants.OPERATOR_ADMINISTRATOR);
        addMessage(model, "操作成功");
        model.addAttribute("noAuto", true);
        model.addAttribute("referer", request.getHeader("Referer"));
        return Constants.MSG_URL;
    }

    /**
     * 跳转修改订单备注页
     */
    @RequiresPermissions("admin:order:main")
    @RequestMapping("/admin/order/updateSellerRemark/forward")
    public String updateSellerRemarkIndex(@RequestParam Long orderId, ModelMap model) {
        ShopOrder order = orderService.find(orderId);
        model.addAttribute("order", order);
        return "/trade/shop_order/update_seller_remark";
    }

    /**
     * 订单备注
     */
    @RequiresPermissions("admin:order:main")
    @RequestMapping(value = {"/admin/order/update"}, method = RequestMethod.POST)
    @ResponseBody
    public String update(ShopOrder order) {
        ShopOrder findOrder = orderService.find(order.getId());
        ShopOrder newOrder = new ShopOrder();
        newOrder.setId(order.getId());
        newOrder.setDeliverExplain(order.getDeliverExplain());
        orderService.update(newOrder);
        //将成功的信号传导前台
        showSuccessJson("修改成功");
        return json;
    }

    /**
     * 订单更新
     */
    @RequiresPermissions("admin:order:main")
    @RequestMapping(value = {"/admin/order/updateAmount"}, method = RequestMethod.POST)
    @ResponseBody
    public String updateOrderAmount(ShopOrder order, @RequestParam int type) {
        if (!beanValidatorForJson(order)) {
            return json;
        }

        if (type != ORDER_EDITED && type != ORDER_EDITING) {
            showSuccessJson("参数错误");
            return json;
        }

        ShopOrder findOrder = orderService.find(order.getId());
        if (!findOrder.getStoreId().equals(0L)) {
            showErrorJson("非法修改");
            return json;
        }

        if (findOrder.getOrderState() != OrderState.ORDER_STATE_NO_PATMENT) {
            showErrorJson("订单已经支付过, 不允许修改");
            return json;
        }

        ShopOrder newOrder = new ShopOrder();
        newOrder.setOrderAmount(order.getOrderAmount());
        if (type == ORDER_EDITED) {
            newOrder.setLockState(OrderState.ORDER_LOCK_STATE_NO);
        }

        if (type == ORDER_EDITING) {
//            newOrder.setLockState(OrderState.ORDER_LOCK_STATE_YES);
        }

        // 条件
        newOrder.setId(order.getId());
        newOrder.setPrevLockState(OrderState.ORDER_LOCK_STATE_NO);
        newOrder.setPrevOrderState(OrderState.ORDER_STATE_NO_PATMENT);
        orderService.updateByIdOrderStateLockState(newOrder, OrderState.ORDER_OPERATE_CHANGE_AMOUNT);
        ShopOrderLog orderLog = new ShopOrderLog();
        orderLog.setId(twiterIdService.getTwiterId());
        orderLog.setOrderId(order.getId());
        Principal principal = (Principal) SecurityUtils.getSubject().getPrincipal();
        orderLog.setOperator(principal.getUsername());
        orderLog.setCreateTime(new Date());
        orderLog.setOrderState(findOrder.getOrderState() + "");
        orderLog.setChangeState(findOrder.getOrderState() + "");
        if (type == ORDER_EDITING) {
            orderLog.setStateInfo("自营商家, 修改订单价格中");
        } else {
            orderLog.setStateInfo("自营商家, 修改订单价格完成");
        }

        orderLogService.save(orderLog);
        //将成功的信号传导前台
        showSuccessJson("修改成功");
        return json;
    }

    /**
     * 查看物流
     *
     * @param expressCode 要查询的快递公司代码
     * @param shippingCode 要查询的快递单号
     */
    @RequestMapping("/admin/kuaidi/shipping")
    public String detail(@RequestParam String shippingCode, @RequestParam String expressCode, ModelMap model,HttpServletRequest request,Long id) {
        ShopOrderVo orderVo = orderService.findWithAddrAndGoods(id);
        RdMmBasicInfo rdMmBasicInfo=rdMmBasicInfoService.find("mmCode",orderVo.getBuyerId());
        if (Optional.ofNullable(orderVo.getLogisticType()).orElse(0)==2){
            model.addAttribute("sincelift", "sincelift");
        }
        model.addAttribute("order", orderVo);
        model.addAttribute("shopMember", rdMmBasicInfo);

        List<ShopCommonExpress> eCodeList = commonExpressService.findList("eCode", expressCode);
        ShopCommonExpress eCode = null;
        if (eCodeList.size()==1){
            eCode = eCodeList.get(0);
        }
        if (eCodeList.size()>1){
            for (ShopCommonExpress express : eCodeList) {
                if (express.getId().equals(44l)){//中通
                    eCode = express;
                }
            }
        }
        String kuaiInfo = kuaidiService.query(eCode.getEAliCode(), shippingCode);
        List<ShopOrderLogistics> shopOrderGoodslist=shopOrderLogisticsService.findList("orderId",id);
        List<ShippingDto> shippingDtoList=ShippingDto.buildList(shopOrderGoodslist,shippingCode);
        Map mapType = JSON.parseObject(kuaiInfo, Map.class);
        Map<String, List<Map<String,String>>> result = (Map<String, List<Map<String,String>>>) mapType.get("result");
        List<Map<String, String>> datainfo = result.get("list");
        model.addAttribute("kuaidi", datainfo);
        model.addAttribute("totalQuantity", shippingDtoList.get(0).totalQuantity);
        model.addAttribute("expressName", eCode.getEName());
        model.addAttribute("shippingCode", shippingCode);
        model.addAttribute("shippingDtoList", shippingDtoList);
        model.addAttribute("id", id);
        return  "trade/shop_order/shipment_view";

    }

    /**
     * 获取组合商品的子商品
     * @return
     */
    @RequestMapping("/admin/order/combineGoods")
    public
    @ResponseBody
    ShopGoodsSpec combineGoods(@RequestParam Long goodsId, @RequestParam Long specId) {

        ShopGoodsSpec shopGoodsSpecs = shopGoodsSpecService.find(specId);
        List<ShopGoodsGoods> shopGoodsGoodsList=shopGoodsGoodsService.findList(Paramap.create().put("goodId",goodsId));
        ShopGoods shopGoods=shopGoodsService.find(goodsId);
        GoodsUtils.getSepcMapAndColImgToGoodsSpec(shopGoods, shopGoodsSpecs);
        List<Long> goodsIds=new ArrayList<>();
        for (ShopGoodsGoods item:shopGoodsGoodsList) {
            goodsIds.add(item.getCombineGoodsId());
        }
        if (goodsIds!=null && goodsIds.size()>0){
           List<ShopGoods> shopGoodsList=shopGoodsService.findList(Paramap.create().put("ids",goodsIds));
           Map<String,String> imageMap=new HashMap<>();
           for (ShopGoods item:shopGoodsList) {
               imageMap.put(item.getGoodsName(),item.getGoodsImage());
            }
            shopGoodsSpecs.setImageMap(imageMap);
        }
        return shopGoodsSpecs;
    }

    /**
     * 订单信息表 导出excel表格
     *
     * @param pages 页码
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/admin/order/list/exportExcel", method = RequestMethod.GET)
    public void exportExcel(Integer pages, Pageable pageable, OrderView param, HttpServletRequest request, HttpServletResponse response,Long[] ids) throws Exception {
        param.setStoreId(0L);
        List<Long> longList=new ArrayList<>();
        if (ids!=null){
            for (Long id:ids) {
                longList.add(id);
            }
        }
        param.setIds(longList);
        pageable.setParameter(param);
        pageable.setOrderProperty("create_time");
        pageable.setOrderDirection(Order.Direction.DESC);
        /*设置页码 查询出需要导出哪一页的内容为Excel*/
        pageable.setPageNumber(pages);
        Page<OrderView> page = orderService.listOrderView(pageable);
        List<OrderView> resultList = page.getContent();
        List<ShopOrderExcel> excels = Lists.newArrayList();
        if (resultList != null && resultList.size() > 0) {

            for (OrderView item : resultList) {
                ShopOrderExcel excel = new ShopOrderExcel();
                BeanUtils.copyProperties(item, excel);
                //订单号
                excel.setId(item.getOrderSn()+"");
                //用户昵称
                excel.setBuyerName(item.getAccountName());
                //会员编号
                excel.setBuyerId(item.getBuyerId()+"");
                //手机号
                excel.setBuyerPhone(item.getBuyerPhone());
                //商品数量
                excel.setGoodsCount(item.getGoodsCount()+"");
                //订单金额
                excel.setGoodsAmount(item.getGoodsAmount());
                //运费
                excel.setShippingFee(item.getShippingFee());
                //订单总PV值
                excel.setPpv(item.getPpv()+"");
                //优惠金额
                excel.setDiscount(item.getDiscount());
                //购物积分支付
                excel.setPointRmbNum(item.getPointRmbNum());
                //实付金额
                excel.setOrderAmount(item.getOrderAmount());
                //收货人姓名
                excel.setReceiverName(item.getReceiverName());
                //收货人手机
                excel.setReceiverPhone(item.getReceiverPhone());
                // 订单类型1 零售订单 2 会员订单 3 pv订单 4 优惠订单 5 换购订单
                int orderType=item.getOrderType();
                if (orderType==2){
                    excel.setOrderTypeName("会员订单");
                }else if (orderType==3){
                    excel.setOrderTypeName("pv订单");
                }else if (orderType==4){
                    excel.setOrderTypeName("优惠订单");
                }else if (orderType==5){
                    excel.setOrderTypeName("换购订单");
                }else {
                    excel.setOrderTypeName("零售订单");
                }
                // 订单状态：0:已取消;5待审核;10:待付款;20:待发货;30:待收货;40:交易完成;50:已提交;60:已确认
                String orderState=item.getOrderState();
                if (orderState.equals("0")){
                    excel.setOrderStateName("已取消");
                }else if (orderState.equals("10")){
                    excel.setOrderStateName("待付款");
                }else if (orderState.equals("20")){
                    excel.setOrderStateName("待发货");
                }else if (orderState.equals("30")){
                    excel.setOrderStateName("待收货");
                }else if (orderState.equals("40")){
                    excel.setOrderStateName("交易完成");
                }
                //业务周期
                excel.setCreationPeriod(item.getCreationPeriod());
                //支付方式
                if ("cashOnDeliveryPlugin".equals(item.getPaymentCode())){
                    excel.setPaymentName("货到付款");
                }else if("replacementPaymentPlugin".equals(item.getPaymentCode())){
                    excel.setPaymentName("-");
                }else {
                    excel.setPaymentName("在线支付");
                }
                //下单时间
                excel.setCreateTime(item.getCreateTime());
                //支付时间
                excel.setCreateTime(item.getPaymentTime());
                //更新时间
                excel.setCreateTime(item.getUpdateTime());
                excels.add(excel);
            }
        }
        if (excels != null && excels.size() > 0) {
            // 定义文件的标头
            String[] headers = {"订单号", "会员编号", "用户昵称", "手机号", "商品数量", "订单金额", "运费", "订单总PV值", "优惠金额", "购物积分支付", "实付金额", "收货人姓名", "收货人手机", "订单类型", "订单状态", "业务周期", "支付方式", "下单时间", "支付时间", "更新时间"};
            Long times = System.currentTimeMillis();//定义时间戳
            HSSFWorkbook wb = ExportExcelUtils.exportCell(excels, null, headers, "");
            response.setContentType("application/x-msdownload");
            response.setHeader("Content-disposition", "attachment; filename=" + times + ".xls");
            BufferedOutputStream bos = null;
            try {
                bos = new BufferedOutputStream(response.getOutputStream());
                wb.write(bos);
                //用户操作日志
                //MqLogUtils.setMqMessage(request, null ,null ,"导出会员信息excel"  , MqLogUtils.SYS_LOG_TYPE);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bos != null) {
                    bos.close();
                }
            }
            return;

        } else {
            request.getRequestDispatcher("/admin/order/list").forward(request, response);
        }
    }

    /**
     * 换购订单信息表 导出excel表格
     *
     * @param pages 页码
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/admin/order/otherList/exportExcel", method = RequestMethod.GET)
    public void exportExcel2(Integer pages, Pageable pageable, OrderView param, HttpServletRequest request, HttpServletResponse response,Long[] ids) throws Exception {
        param.setStoreId(0L);
        List<Long> longList=new ArrayList<>();
        if (ids!=null){
            for (Long id:ids) {
                longList.add(id);
            }
        }
        param.setIds(longList);
        pageable.setParameter(param);
        pageable.setOrderProperty("create_time");
        pageable.setOrderDirection(Order.Direction.DESC);
        param.setOrderType(5);
        /*设置页码 查询出需要导出哪一页的内容为Excel*/
        pageable.setPageNumber(pages);
        Page<OrderView> page = orderService.listOrderView(pageable);
        List<OrderView> resultList = page.getContent();
        List<ShopExchangeOrderExcel> excels = Lists.newArrayList();
        if (resultList != null && resultList.size() > 0) {

            for (OrderView item : resultList) {
                ShopExchangeOrderExcel excel = new ShopExchangeOrderExcel();
                BeanUtils.copyProperties(item, excel);
                //订单号
                excel.setId(item.getId()+"");
                //用户昵称
                excel.setBuyerName(item.getAccountName());
                //会员编号
                excel.setBuyerId(item.getBuyerId()+"");
                //手机号
                excel.setBuyerPhone(item.getBuyerPhone());
                //商品数量
                excel.setGoodsCount(item.getGoodsCount()+"");
                //订单总积分
                excel.setGoodsAmount(item.getGoodsAmount());
                //实付积分
                excel.setOrderAmount(item.getOrderAmount());
                //收货人姓名
                excel.setReceiverName(item.getReceiverName());
                //收货人手机
                excel.setReceiverPhone(item.getReceiverPhone());
                // 订单类型1 零售订单 2 会员订单 3 pv订单 4 优惠订单 5 换购订单
                int orderType=item.getOrderType();
                if (orderType==2){
                    excel.setOrderTypeName("会员订单");
                }else if (orderType==3){
                    excel.setOrderTypeName("pv订单");
                }else if (orderType==4){
                    excel.setOrderTypeName("优惠订单");
                }else if (orderType==5){
                    excel.setOrderTypeName("换购订单");
                }else {
                    excel.setOrderTypeName("零售订单");
                }
                // 订单状态：0:已取消;5待审核;10:待付款;20:待发货;30:待收货;40:交易完成;50:已提交;60:已确认
                String orderState=item.getOrderState();
                if (orderState.equals("0")){
                    excel.setOrderStateName("已取消");
                }else if (orderState.equals("10")){
                    excel.setOrderStateName("待付款");
                }else if (orderState.equals("20")){
                    excel.setOrderStateName("待发货");
                }else if (orderState.equals("30")){
                    excel.setOrderStateName("待收货");
                }else if (orderState.equals("40")){
                    excel.setOrderStateName("交易完成");
                }
                //下单时间
                excel.setCreateTime(item.getCreateTime());
                //支付时间
                excel.setCreateTime(item.getPaymentTime());
                //更新时间
                excel.setCreateTime(item.getUpdateTime());
                excels.add(excel);
            }
        }
        if (excels != null && excels.size() > 0) {
            // 定义文件的标头
            String[] headers = {"订单号", "用户昵称", "会员编号", "手机号", "商品数量", "换购积分", "实付积分", "收货人姓名", "收货人手机", "订单类型", "订单状态", "下单时间", "支付时间", "更新时间"};
            Long times = System.currentTimeMillis();//定义时间戳
            HSSFWorkbook wb = ExportExcelUtils.exportCell(excels, null, headers, "");
            response.setContentType("application/x-msdownload");
            response.setHeader("Content-disposition", "attachment; filename=" + times + ".xls");
            BufferedOutputStream bos = null;
            try {
                bos = new BufferedOutputStream(response.getOutputStream());
                wb.write(bos);
                //用户操作日志
                //MqLogUtils.setMqMessage(request, null ,null ,"导出会员信息excel"  , MqLogUtils.SYS_LOG_TYPE);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bos != null) {
                    bos.close();
                }
            }
            return;

        } else {
            request.getRequestDispatcher("/admin/order/otherList").forward(request, response);
        }
    }

    /**
     *
     * 订单分账查询
     * @param orderId
     * @param model
     * @param request
     * @return
     */
    @RequiresPermissions("admin:order:main")
    @RequestMapping(value = "/admin/order/getOrderSplitDetail")
    public String getOrderSplitDetail(HttpServletRequest request, ModelMap model, @RequestParam(required = true, value = "orderId")Long orderId) {

        ShopOrder order = orderService.find(orderId);
        if (order==null){
            model.addAttribute("msg", "找不到该订单");
            model.addAttribute("referer", request.getHeader("Referer"));
            return Constants.MSG_URL;
        }

        Integer cutStatus = order.getCutStatus();
        if (cutStatus==null){
            model.addAttribute("msg", "未分账");
            model.addAttribute("referer", request.getHeader("Referer"));
            return Constants.MSG_URL;
        }else {
            if(cutStatus==2){
                BigDecimal orderAmount = Optional.ofNullable(order.getOrderAmount()).orElse(BigDecimal.ZERO).setScale(2,BigDecimal.ROUND_HALF_UP);
                BigDecimal cutAmount = Optional.ofNullable(order.getCutAmount()).orElse(BigDecimal.ZERO).setScale(2,BigDecimal.ROUND_HALF_UP);
                BigDecimal firmSplitAmount = orderAmount.subtract(cutAmount).setScale(2,BigDecimal.ROUND_HALF_UP);
                OrderSplitDetail detail = new OrderSplitDetail();
                detail.setId(orderId);
                detail.setOrderSn(order.getOrderSn());
                detail.setOrderAmount(orderAmount);
                detail.setFirmSplitAmount(firmSplitAmount);
                detail.setCutAmount(Optional.ofNullable(order.getCutAmount()).orElse(BigDecimal.ZERO).setScale(2,BigDecimal.ROUND_HALF_UP));
                detail.setCutGetId(Optional.ofNullable(order.getCutGetId()).orElse(""));
                detail.setCutFailInfo("");

                RdMmBasicInfo rdMmBasicInfo=rdMmBasicInfoService.find("mmCode",order.getCutGetId());
                if (rdMmBasicInfo==null){
                    detail.setCutGetName("");
                }else {
                    detail.setCutGetName(Optional.ofNullable(rdMmBasicInfo.getMmName()).orElse(""));
                }

                detail.setCutAcc(Optional.ofNullable(order.getCutAcc()).orElse(BigDecimal.ZERO).setScale(2,BigDecimal.ROUND_HALF_UP));
                model.addAttribute("detail", detail);
            }else if (cutStatus==1){
                model.addAttribute("msg", "不满足分账条件:"+Optional.ofNullable(order.getCutFailInfo()).orElse(""));
                model.addAttribute("referer", request.getHeader("Referer"));
                return Constants.MSG_URL;
            }else if (cutStatus==3){
                BigDecimal orderAmount = Optional.ofNullable(order.getOrderAmount()).orElse(BigDecimal.ZERO).setScale(2,BigDecimal.ROUND_HALF_UP);
                BigDecimal cutAmount = Optional.ofNullable(order.getCutAmount()).orElse(BigDecimal.ZERO).setScale(2,BigDecimal.ROUND_HALF_UP);
                BigDecimal firmSplitAmount = orderAmount.subtract(cutAmount).setScale(2,BigDecimal.ROUND_HALF_UP);
                OrderSplitDetail detail = new OrderSplitDetail();
                detail.setId(orderId);
                detail.setOrderSn(order.getOrderSn());
                detail.setOrderAmount(orderAmount);
                detail.setFirmSplitAmount(firmSplitAmount);
                detail.setCutAmount(Optional.ofNullable(order.getCutAmount()).orElse(BigDecimal.ZERO).setScale(2,BigDecimal.ROUND_HALF_UP));
                detail.setCutGetId(Optional.ofNullable(order.getCutGetId()).orElse(""));
                detail.setCutFailInfo(Optional.ofNullable(order.getCutFailInfo()).orElse(""));

                RdMmBasicInfo rdMmBasicInfo=rdMmBasicInfoService.find("mmCode",order.getCutGetId());
                if (rdMmBasicInfo==null){
                    detail.setCutGetName("");
                }else {
                    detail.setCutGetName(Optional.ofNullable(rdMmBasicInfo.getMmName()).orElse(""));
                }

                detail.setCutAcc(Optional.ofNullable(order.getCutAcc()).orElse(BigDecimal.ZERO).setScale(2,BigDecimal.ROUND_HALF_UP));
                model.addAttribute("detail", detail);

                /*model.addAttribute("msg", "分账失败:"+Optional.ofNullable(order.getCutFailInfo()).orElse(""));
                model.addAttribute("referer", request.getHeader("Referer"));
                return Constants.MSG_URL*/;
            }else if (cutStatus==4){
                model.addAttribute("msg", "分账进行中");
                model.addAttribute("referer", request.getHeader("Referer"));
                return Constants.MSG_URL;
            }else {
                BigDecimal orderAmount = Optional.ofNullable(order.getOrderAmount()).orElse(BigDecimal.ZERO).setScale(2,BigDecimal.ROUND_HALF_UP);
                BigDecimal cutAmount = Optional.ofNullable(order.getCutAmount()).orElse(BigDecimal.ZERO).setScale(2,BigDecimal.ROUND_HALF_UP);
                BigDecimal firmSplitAmount = orderAmount.subtract(cutAmount).setScale(2,BigDecimal.ROUND_HALF_UP);
                OrderSplitDetail detail = new OrderSplitDetail();
                detail.setId(orderId);
                detail.setOrderSn(order.getOrderSn());
                detail.setOrderAmount(orderAmount);
                detail.setFirmSplitAmount(firmSplitAmount);
                detail.setCutAmount(Optional.ofNullable(order.getCutAmount()).orElse(BigDecimal.ZERO).setScale(2,BigDecimal.ROUND_HALF_UP));
                detail.setCutGetId(Optional.ofNullable(order.getCutGetId()).orElse(""));
                detail.setCutFailInfo(Optional.ofNullable(order.getCutFailInfo()).orElse(""));

                RdMmBasicInfo rdMmBasicInfo=rdMmBasicInfoService.find("mmCode",order.getCutGetId());
                if (rdMmBasicInfo==null){
                    detail.setCutGetName("");
                }else {
                    detail.setCutGetName(Optional.ofNullable(rdMmBasicInfo.getMmName()).orElse(""));
                }

                detail.setCutAcc(Optional.ofNullable(order.getCutAcc()).orElse(BigDecimal.ZERO).setScale(2,BigDecimal.ROUND_HALF_UP));
                model.addAttribute("detail", detail);

                /*model.addAttribute("msg", "未分账");
                model.addAttribute("referer", request.getHeader("Referer"));
                return Constants.MSG_URL;*/
            }
        }

        return "trade/shop_order/splitting_details";
    }



    /**
     * 通联接口
     * 订单分账查询
     * @param orderId
     * @param model
     * @param request
     * @return
     */
    /*@RequestMapping(value = {"/admin/order/getOrderSplitDetail1"})
    public String getOrderSplitDetail1(Long orderId, ModelMap model, HttpServletRequest request) {

        ShopOrder order = orderService.find(orderId);
        if (order==null){
            model.addAttribute("msg", "找不到该订单");
            model.addAttribute("referer", request.getHeader("Referer"));
            return Constants.MSG_URL;
        }

        String s = TongLianUtils.getOrderSplitRuleListDetail(order.getOrderSn());
        if(!"".equals(s)) {
            Map maps = (Map) JSON.parse(s);
            String status = maps.get("status").toString();
            String signedValue = maps.get("signedValue").toString();
            if (status.equals("OK")) {
                Map okMap = (Map) JSON.parse(signedValue);
                Long state = (Long)okMap.get("state");//分账状态 0-失败 1-成功
                if (state.longValue()==0){
                    model.addAttribute("msg", "分账状态：失败");
                    model.addAttribute("referer", request.getHeader("Referer"));
                    return Constants.MSG_URL;
                }

                String orderNo = okMap.get("orderNo").toString();//通商云订单号
                String bizOrderNo = okMap.get("bizOrderNo").toString();//商户订单号（支付订单）
                String recieverId = okMap.get("recieverId").toString();//商户系统用户标识，商户系统中唯一编号。 收款方  消费订单和托付订单中的 收款人

                List<Map<String, Object>> splitRuleListDetailList = (List<Map<String, Object>>)okMap.get("splitRuleListDetail");//分账明细
                List<Map<String, Object>> splitList = new ArrayList<Map<String, Object>>();
                for (Map<String, Object> map : splitRuleListDetailList) {
                    String bizUserId = Optional.ofNullable(map.get("bizUserId").toString()).orElse("");//商户系统用户标识，商户系统中唯一编号。 如果是平台，则填#yunBizUserId_B2C#
                    //String accountSetNo = Optional.ofNullable(map.get("accountSetNo").toString()).orElse("");//如果向会员分账，不上送，默认为唯一托管账户集。 如果向平台分账，请填写平台的标准账户集编号 （不支持 100003-准备金额度账户集）
                    Long amount = Optional.ofNullable(Long.valueOf(map.get("amount").toString())).orElse(0l);//金额，单位：分
                    Long fee = Optional.ofNullable(Long.valueOf(map.get("fee").toString())).orElse(0l);//手续费，内扣，单位：分
                    String remark = Optional.ofNullable(map.get("remark").toString()).orElse("");//备注，最长 50 个字符
                    List<Map<String, Object>> splitRuleList = (List<Map<String, Object>>)map.get("splitRuleList");//分账列表
                    Map<String, Object> returnMap = new HashMap<String, Object>();
                    returnMap.put(bizUserId,amount-fee);
                    if (splitRuleList.size()>0){
                        Map<String, Object> splitRuleMap = getSplitRuleList(splitRuleList,bizUserId,returnMap);
                        splitList.add(splitRuleMap);
                    }else{
                        splitList.add(returnMap);
                    }
                }
                model.addAttribute("splitList", splitList);
            }else {
                model.addAttribute("msg", maps.get("message"));
                model.addAttribute("referer", request.getHeader("Referer"));
                return Constants.MSG_URL;
            }
        }else {
            model.addAttribute("msg", "调用通联接口失败");
            model.addAttribute("referer", request.getHeader("Referer"));
            return Constants.MSG_URL;
        }

        return "trade/shop_order/other_list";
    }

    //解析分账列表
    public Map<String, Object> getSplitRuleList(List<Map<String, Object>> splitRuleList,String bizUserId,Map<String, Object> returnMap){
        Map<String, Object> resMap = new HashMap<String, Object>();
        for (Map<String, Object> map : splitRuleList) {
            String bizUserId1 = Optional.ofNullable(map.get("bizUserId").toString()).orElse("");//商户系统用户标识，商户系统中唯一编号。 如果是平台，则填#yunBizUserId_B2C#
            Long amount1 = Optional.ofNullable(Long.valueOf(map.get("amount").toString())).orElse(0l);//金额，单位：分
            Long fee1 = Optional.ofNullable(Long.valueOf(map.get("fee").toString())).orElse(0l);//手续费，内扣，单位：分
            //String remark1 = map.get("remark").toString();//备注，最长 50 个字符
            returnMap.put(bizUserId,Optional.ofNullable(Long.valueOf(returnMap.get(bizUserId).toString())).orElse(0l)-amount1-fee1);
            returnMap.put(bizUserId1,amount1-fee1);
            List<Map<String, Object>> splitRuleList1 = (List<Map<String, Object>>)map.get("splitRuleList");//分账列表
            if (splitRuleList1.size()>0){
                Map<String, Object> splitRuleList2 = getSplitRuleList(splitRuleList1,bizUserId1,returnMap);

                //把splitRuleList2复制回returnMap
                Iterator it = splitRuleList2.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry entry = (Map.Entry) it.next();
                    String key = (String)entry.getKey();
                    returnMap.put(key, splitRuleList2.get(key) != null ? splitRuleList2.get(key) : "");

                }
                //TongLianUtils.mapCopy(splitRuleList2,returnMap);
            }
        }
        return returnMap;
    }*/


}
