package com.framework.loippi.controller.trade;


import com.framework.loippi.consts.OrderState;
import com.framework.loippi.controller.GenericController;
import com.framework.loippi.dto.PrintDetails;
import com.framework.loippi.entity.Principal;
import com.framework.loippi.entity.common.ShopCommonExpress;
import com.framework.loippi.entity.order.ShopOrder;
import com.framework.loippi.entity.order.ShopOrderGoods;
import com.framework.loippi.entity.order.ShopOrderLogistics;
import com.framework.loippi.mybatis.paginator.domain.Order.Direction;
import com.framework.loippi.service.common.ShopCommonExpressService;
import com.framework.loippi.service.order.ShopOrderLogisticsService;
import com.framework.loippi.service.order.ShopOrderService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.StringUtil;
import java.util.List;
import javax.annotation.Resource;

import com.framework.loippi.vo.order.ShopOrderVo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 功能： 订单发货
 * 类名：ShipmentSysController
 * 日期：2017/12/15  10:46
 * 作者：czl
 * 详细说明：
 * 修改备注:
 */
@Controller("adminShipmentController")
public class ShipmentSysController extends GenericController {

    @Resource
    private ShopOrderService orderService;
    @Resource
    private ShopCommonExpressService expressService;
    @Resource
    private ShopOrderLogisticsService shopOrderLogisticsService;

    /**
     * 发货列表 -- 已发货
     * 未发货 收到货物订单
     */
    @RequiresPermissions("admin:shipment:main")
    @RequestMapping(value = {"/admin/shipment/list"}, method = RequestMethod.GET)
    public String goodsShipments(Integer orderState, String orderSnKeyWord, Pageable pageable, ModelMap model, @RequestParam(defaultValue = "1") Integer pageNo) {
        orderState = orderState == null ? OrderState.ORDER_STATE_UNFILLED : orderState;
        Paramap paramap = Paramap.create().put("orderState", orderState).put("orderSnKeyWord", orderSnKeyWord)
                .put("storeId", 0L);
        pageable.setPageNumber(pageNo);
        pageable.setParameter(paramap);
        pageable.setOrderProperty("create_time");
        pageable.setOrderDirection(Direction.DESC);
        Page page = orderService.findDeliverableWithGoodsAndAddr(pageable);
        model.addAttribute("page", page);
        model.addAttribute("orderState", orderState);
        model.addAttribute("orderSnKeyWord", orderSnKeyWord);
        return "/trade/shop_order/shipments_list";
    }

    /**
     * 发货详细
     */
    @RequiresPermissions("admin:shipment:main")
    @RequestMapping("/admin/shipment/view")
    public String shipmentDetail(@RequestParam long orderId, ModelMap model,ShopOrder oldorder,Integer index) {
        ShopOrder order = orderService.findWithAddrAndGoods(orderId);
        String ShopOrderGoodsIds="";
        String ShopOrderGoodsNums="";
        String wareCodes="";
        String wareNames="";
            ((ShopOrderVo) order).setShopOrderGoods(oldorder.getShopOrderGoodses());
            for (ShopOrderGoods item:((ShopOrderVo) order).getShopOrderGoods()){
                if (item!=null){
                    Integer flag=((ShopOrderVo) order).getShopOrderGoods().indexOf(item);
                    if (item.getGoodsNum()>0 && (index==-1 || flag==index)){
                        ShopOrderGoodsIds+=item.getId()+",";
                        ShopOrderGoodsNums+=item.getGoodsNum()+",";
                        wareCodes+=item.getWareCode()+",";
                        wareNames+=item.getWareName()+",";
                    }
                }

            }
            model.addAttribute("ShopOrderGoodsIds", ShopOrderGoodsIds);
        model.addAttribute("ShopOrderGoodsNums", ShopOrderGoodsNums);
        model.addAttribute("wareCodes", wareCodes);
        model.addAttribute("wareNames", wareNames);
        List<ShopCommonExpress> expressList = expressService
                .findList(Paramap.create().put("eState", 1).put("isDel", 0));
        model.addAttribute("expressList", expressList);
        model.addAttribute("order", order);
        return "/trade/shop_order/shipment_detail";
    }

    /**
     * 发货确认
     *
     * @param orderId           订单编号
     * @param deliverExplain    备注
     * @param shippingExpressId 快递公司id
     * @param shippingCode      快递单号
     */
    @RequiresPermissions("admin:shipment:main")
    @RequestMapping(value = "/admin/shipment/save", method = RequestMethod.POST)
    @ResponseBody
    public String shipmentSave(@RequestParam long orderId, @RequestParam long shippingExpressId
            , @RequestParam String shippingCode, String deliverExplain,String ShopOrderGoodsIds,String ShopOrderGoodsNums
                , String wareCodes ,String wareNames) {
        if (StringUtil.isEmpty(shippingCode)) {
            showErrorJson("物流单号不能未空");
        } else {
            try {
                String adminName="";
                Subject subject = SecurityUtils.getSubject();
                if (subject != null) {
                    Principal principal = (Principal) subject.getPrincipal();
                    if (principal != null && principal.getId() != null) {
                        adminName=principal.getUsername();
                    }
                }
                orderService.updateDeliveryOrder(orderId, 0L, shippingExpressId, shippingCode, deliverExplain,ShopOrderGoodsIds,ShopOrderGoodsNums,wareCodes,wareNames, adminName,1);
            }catch (Exception e){
                e.printStackTrace();
                System.err.println(e.getMessage());
                showSuccessJson(e.getMessage());
                return json;
            }

            showSuccessJson("发货成功");
        }
        return json;
    }

    @RequestMapping(value = "/admin/logisticType/save", method = RequestMethod.POST)
    public String logisticTypeSave(@RequestParam long orderId,ShopOrder oldorder,Integer index,String oldorders) {
        ShopOrder order = orderService.findWithAddrAndGoods(orderId);
        String ShopOrderGoodsIds="";
        String ShopOrderGoodsNums="";
        String wareCodes="";
        String wareNames="";
        try {
            ((ShopOrderVo) order).setShopOrderGoods(oldorder.getShopOrderGoodses());
            for (ShopOrderGoods item:((ShopOrderVo) order).getShopOrderGoods()){
                Integer flag=((ShopOrderVo) order).getShopOrderGoods().indexOf(item);
                if (item.getGoodsNum()>0 && (index==-1 || flag==index)){
                    ShopOrderGoodsIds+=item.getId()+",";
                    ShopOrderGoodsNums+=item.getGoodsNum()+",";
                    wareCodes+=item.getWareCode()+",";
                    wareNames+=item.getWareName()+",";
                }

            }
        String adminName="";
        Subject subject = SecurityUtils.getSubject();
        if (subject != null) {
            Principal principal = (Principal) subject.getPrincipal();
            if (principal != null && principal.getId() != null) {
                adminName=principal.getUsername();
            }
        }
            orderService.updateDeliveryOrder(orderId, 0L, 0L, null, null,ShopOrderGoodsIds,ShopOrderGoodsNums,wareCodes,wareNames,adminName,2);
        }catch (Exception e){
            return "redirect:/admin/order/otherView.jhtml?id="+orderId;
        }
        return "redirect:/admin/order/otherList.jhtml?orderState=20";
    }


    @RequestMapping("/admin/shipment/printDetails")
    public String printDetails(ModelMap model,@RequestParam Long oderId) {
        List<ShopOrderLogistics> shopOrderLogisticsList=shopOrderLogisticsService.findList(Paramap.create().put("orderId",oderId));
        ShopOrderVo orderVo = orderService.findWithAddrAndGoods(oderId);
        PrintDetails printDetails=PrintDetails.build(shopOrderLogisticsList,orderVo);
        model.addAttribute("printDetails", printDetails);
        return "/trade/shop_order/print_details";
    }

}
