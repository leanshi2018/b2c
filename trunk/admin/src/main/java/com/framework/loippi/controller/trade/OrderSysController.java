package com.framework.loippi.controller.trade;

import java.io.BufferedOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
import com.framework.loippi.dto.ShippingDto;
import com.framework.loippi.dto.ShopExchangeOrderExcel;
import com.framework.loippi.dto.ShopOrderExcel;
import com.framework.loippi.entity.Principal;
import com.framework.loippi.entity.User;
import com.framework.loippi.entity.common.ShopCommonArea;
import com.framework.loippi.entity.common.ShopCommonExpress;
import com.framework.loippi.entity.order.ShopOrder;
import com.framework.loippi.entity.order.ShopOrderAddress;
import com.framework.loippi.entity.order.ShopOrderDiscountType;
import com.framework.loippi.entity.order.ShopOrderLog;
import com.framework.loippi.entity.order.ShopOrderLogistics;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsGoods;
import com.framework.loippi.entity.product.ShopGoodsSpec;
import com.framework.loippi.entity.user.RdMmAddInfo;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.entity.user.RdRanks;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.result.sys.OrderView;
import com.framework.loippi.service.KuaidiService;
import com.framework.loippi.service.RedisService;
import com.framework.loippi.service.UserService;
import com.framework.loippi.service.common.ShopCommonAreaService;
import com.framework.loippi.service.common.ShopCommonExpressService;
import com.framework.loippi.service.order.ShopOrderAddressService;
import com.framework.loippi.service.order.ShopOrderDiscountTypeService;
import com.framework.loippi.service.order.ShopOrderGoodsService;
import com.framework.loippi.service.order.ShopOrderLogService;
import com.framework.loippi.service.order.ShopOrderLogisticsService;
import com.framework.loippi.service.order.ShopOrderService;
import com.framework.loippi.service.product.ShopGoodsGoodsService;
import com.framework.loippi.service.product.ShopGoodsService;
import com.framework.loippi.service.product.ShopGoodsSpecService;
import com.framework.loippi.service.user.RdMmAddInfoService;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.service.user.RdMmRelationService;
import com.framework.loippi.service.user.RdRanksService;
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
        RdMmAddInfo shopMemberAddress=rdMmAddInfoService.find("aid",-1);
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
        model.addAttribute("order", orderVo);
        model.addAttribute("areas", areas);
        model.addAttribute("shopMember", rdMmBasicInfo);
        model.addAttribute("shopMemberAddress", shopMemberAddress);
        if (Optional.ofNullable(type).orElse(0)==1){

            return "/trade/shop_order/edit";
        }

        return "/trade/shop_order/view";
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

}
