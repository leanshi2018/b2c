package com.framework.loippi.controller.trade;

import net.sf.json.JSONObject;

import java.io.BufferedOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import com.framework.loippi.controller.GenericController;
import com.framework.loippi.dto.OrderSplitDetail;
import com.framework.loippi.dto.ShippingDto;
import com.framework.loippi.dto.ShopExchangeOrderExcel;
import com.framework.loippi.dto.ShopOrderExcel;
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
import com.framework.loippi.entity.ware.RdWarehouse;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.result.sys.OrderView;
import com.framework.loippi.service.KuaidiService;
import com.framework.loippi.service.RedisService;
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
import com.framework.loippi.service.ware.RdWarehouseService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.GoodsUtils;
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
                                                    inventoryWarningService.updateInventoryByWareCodeAndSpecId("20192514", goodsSpecId, quantity);
                                                }*/

                                                List<ShopOrderGoods> shopOrderGoodsList = new ArrayList<>();
                                                List<ShopOrderGoods> orderGoodsList = (List<ShopOrderGoods>) resMap.get("orderGoods");
                                                List<ShopOrderGoods> shopOrderGoods = updateOrderGoods(shopOrderGoodsList, orderGoodsList, trackingNo);//需要修改订单商品信息
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
                    }else {
                        ShopOrder shopOrder1 = new ShopOrder();
                        shopOrder1.setId(shopOrder.getId());
                        shopOrder1.setOrderState(30);
                        orderService.update(shopOrder1);
                    }
                }
            }
        }

        if (typeExperss.equals("0")){//全部发货
            System.out.println("进来了全部发货");
            List<ShopOrder> orderList = orderService.findStatu20();//所有代发货订单
            for (ShopOrder shopOrder : orderList) {
                if (shopOrder.getLogisticType()==1){
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
                                        inventoryWarningService.updateInventoryByWareCodeAndSpecId("20192514", goodsSpecId, quantity);
                                    }*/

                                    List<ShopOrderGoods> shopOrderGoodsList = new ArrayList<>();
                                    List<ShopOrderGoods> orderGoodsList = (List<ShopOrderGoods>) resMap.get("orderGoods");
                                    List<ShopOrderGoods> shopOrderGoods = updateOrderGoods(shopOrderGoodsList, orderGoodsList, trackingNo);//需要修改订单商品信息
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
                }else {//自提
                    ShopOrder shopOrder1 = new ShopOrder();
                    shopOrder1.setId(shopOrder.getId());
                    shopOrder1.setOrderState(30);
                    orderService.update(shopOrder1);
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
    public List<ShopOrderGoods> updateOrderGoods(List<ShopOrderGoods> shopOrderGoodsNullList,List<ShopOrderGoods> orderGoodsList,String trackingNo) {
        for (ShopOrderGoods orderGoods : orderGoodsList) {
            ShopCommonExpress express = commonExpressService.find(44l);

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

            RdWarehouse warehouse = rdWarehouseService.findByCode("20192514");
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

            inventoryWarningService.updateInventoryByWareCodeAndSpecId("20192514", orderGoods.getSpecId(), orderGoods.getGoodsNum());

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
    public Map<String,Object> orderShip(Long id) {
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

                    Map<String,Object> map1 = new HashMap<>();
                    map1.put("goodId",goodsId);
                    map1.put("combineGoodsId",goodsId1);
                    ShopGoodsGoods goodsGoods = shopGoodsGoodsService.findGoodsGoods(map1);
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
            productListss.add(product);
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
    @ResponseBody
    public String mentionAddressAll(@RequestParam(required = false, value = "pageNo", defaultValue = "1") Integer pageNo,
                                    String provinceCode,String phone,ModelMap model) {
        Pageable pageable = new Pageable(pageNo, 20);
        pageable.setParameter(Paramap.create().put("aid", 0l).put("addProvinceCode",provinceCode).put("phone",phone));
        pageable.setOrderProperty("create_time");
        pageable.setOrderDirection(Order.Direction.DESC);
        List<RdMmAddInfo> lists = rdMmAddInfoService.findMentionAddrList();
        if (lists.size()==0){
            showErrorJson("自提地址为空");
        }
        model.addAttribute("page", rdMmAddInfoService.findMentionAddrListByPage(pageable));
        showSuccessJson(lists);
        return json;
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

        if (shopOrder.getOrderState().longValue()==10||shopOrder.getOrderState().longValue()==20||shopOrder.getOrderState().longValue()==30){
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
                shopOrder.setOrderState(OrderState.ORDER_STATE_NOT_RECEIVING);
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
        orderService.updateCancelOrder(id, Constants.OPERATOR_ADMINISTRATOR, principal.getId(), PaymentTallyState.PAYMENTTALLY_TREM_MB,message,user.getUsername());
        addMessage(model, "取消成功");
        model.addAttribute("noAuto", true);
        model.addAttribute("referer", request.getHeader("Referer"));
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

        ShopCommonExpress eCode = commonExpressService.find("eCode", expressCode);
        String kuaiInfo = kuaidiService.query(expressCode, shippingCode);
        List<ShopOrderLogistics> shopOrderGoodslist=shopOrderLogisticsService.findList("orderId",id);
        List<ShippingDto> shippingDtoList=ShippingDto.buildList(shopOrderGoodslist,shippingCode);
        Map mapType = JSON.parseObject(kuaiInfo, Map.class);
        model.addAttribute("kuaidi", mapType);
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
    @RequestMapping(value = {"/admin/order/getOrderSplitDetail"})
    public String getOrderSplitDetail(Long orderId, ModelMap model, HttpServletRequest request) {

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
                detail.setCutGetId(Optional.ofNullable(order.getCutGetId()).orElse(""));

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
                model.addAttribute("msg", "分账失败:"+Optional.ofNullable(order.getCutFailInfo()).orElse(""));
                model.addAttribute("referer", request.getHeader("Referer"));
                return Constants.MSG_URL;
            }else if (cutStatus==4){
                model.addAttribute("msg", "分账进行中");
                model.addAttribute("referer", request.getHeader("Referer"));
                return Constants.MSG_URL;
            }else {
                model.addAttribute("msg", "未分账");
                model.addAttribute("referer", request.getHeader("Referer"));
                return Constants.MSG_URL;
            }
        }

        return "/trade/shop_order/splitting_details";
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
