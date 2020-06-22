package com.framework.loippi.controller.selfMention;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.framework.loippi.consts.Constants;
import com.framework.loippi.controller.BaseController;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.user.RdSysPeriod;
import com.framework.loippi.entity.ware.RdInventoryWarning;
import com.framework.loippi.entity.ware.RdWareAllocation;
import com.framework.loippi.entity.ware.RdWareOrder;
import com.framework.loippi.entity.ware.RdWarehouse;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.result.auths.AuthsLoginResult;
import com.framework.loippi.service.order.ShopOrderService;
import com.framework.loippi.service.product.ShopGoodsService;
import com.framework.loippi.service.product.ShopGoodsSpecService;
import com.framework.loippi.service.user.RdSysPeriodService;
import com.framework.loippi.service.ware.RdInventoryWarningService;
import com.framework.loippi.service.ware.RdWareAllocationService;
import com.framework.loippi.service.ware.RdWarehouseService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.ApiUtils;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.Xerror;
import com.framework.loippi.vo.store.MentionWareGoodsVo;

@Controller("selfMentionController")
@Slf4j
public class SelfMentionController extends BaseController {

    @Resource
    private RdInventoryWarningService rdInventoryWarningService;
    @Resource
    private ShopGoodsService shopGoodsService;
    @Resource
    private ShopGoodsSpecService shopGoodsSpecService;
    @Resource
    private ShopOrderService orderService;
    @Resource
    private RdWarehouseService rdWarehouseService;
    @Resource
    private RdWareAllocationService rdWareAllocationService;
    @Resource
    private RdSysPeriodService rdSysPeriodService;

    /**
     * 点击进入我的小店
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/mention/center.json")
    @ResponseBody
    public String inCenter(HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if(member==null){
            return ApiUtils.error("当前用户尚未登录");
        }
        String mmCode = member.getMmCode();//店主会员编号
        return "";
    }

    /**
     * 商品列表
     */
    @RequestMapping(value = "/api/mention/goodsList", method = RequestMethod.POST)
    @ResponseBody
    public String goodsList(HttpServletRequest request, Pageable pager) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if(member==null){
            return ApiUtils.error("当前用户尚未登录");
        }

        String mmCode = member.getMmCode();
        RdWarehouse warehouse = rdWarehouseService.findByMmCode(mmCode);
        if (warehouse == null ) {
            return ApiUtils.error("该会员没有自提仓库");
        }
        String wareCode = warehouse.getWareCode();

        Paramap paramap = Paramap.create();
        //paramap.put("mmCode", member.getMmCode());
        paramap.put("wareCode", wareCode);
        paramap.put("wareStatus", 0);//0正常  1 停用
        if (wareCode == null ) {
            return ApiUtils.error("仓库代码为空");
        }
        pager.setOrderDirection(Order.Direction.DESC);
        pager.setOrderProperty("create_time");
        pager.setParameter(paramap);
        List<MentionWareGoodsVo> list = new ArrayList<MentionWareGoodsVo>();
        Page<RdInventoryWarning> goodsPage = rdInventoryWarningService.findByPage(pager);
        if (goodsPage.getContent().size()==0){
            return ApiUtils.success(list);//空的对象
        }
        /*RdWarehouse warehouse = rdWarehouseService.findByCode(wareCode);
        if (warehouse == null ) {
            return ApiUtils.error("未找到该仓库");
        }*/
        for (RdInventoryWarning inventoryWarning : goodsPage.getContent()) {
            ShopGoods shopGoods = shopGoodsService.find(Long.valueOf(inventoryWarning.getGoodsCode()));

            MentionWareGoodsVo wareGoodsVo = new MentionWareGoodsVo();
            wareGoodsVo.setWareCode(Optional.ofNullable(inventoryWarning.getWareCode()).orElse(""));
            wareGoodsVo.setGoodsName(Optional.ofNullable(shopGoods.getGoodsName()).orElse(""));
            wareGoodsVo.setGoodsImage(Optional.ofNullable(shopGoods.getGoodsImage()).orElse(""));
            wareGoodsVo.setSpecId(Optional.ofNullable(inventoryWarning.getSpecificationId()).orElse(0l));
            wareGoodsVo.setSpecGoodsSpec(Optional.ofNullable(shopGoods.getGoodsName()).orElse(""));
            wareGoodsVo.setGoodsRetailPrice(Optional.ofNullable(shopGoods.getGoodsRetailPrice()).orElse(BigDecimal.ZERO));
            wareGoodsVo.setGoodsMemberPrice(Optional.ofNullable(shopGoods.getGoodsMemberPrice()).orElse(BigDecimal.ZERO));
            wareGoodsVo.setPpv(Optional.ofNullable(shopGoods.getPpv()).orElse(BigDecimal.ZERO));
            wareGoodsVo.setInventory(Optional.ofNullable(inventoryWarning.getInventory()).orElse(0));
            Integer salesNum = orderService.countMentionSales(warehouse.getMentionId(),inventoryWarning.getSpecificationId());//销量
            if (salesNum==null){
                wareGoodsVo.setSales(0);
            }else {
                wareGoodsVo.setSales(salesNum);
            }
            String ware = inventoryWarning.getWareCode();
            Long specId = inventoryWarning.getSpecificationId();
            Integer pNum = rdInventoryWarningService.findProductInventory(ware,specId);
            wareGoodsVo.setProductInventory(pNum);//单品库存
            list.add(wareGoodsVo);
        }
        return ApiUtils.success(list);
    }

    /**
     * 欠款商品列表
     */
    @RequestMapping(value = "/api/mention/oweGoodsList", method = RequestMethod.POST)
    @ResponseBody
    public String oweGoodsList(HttpServletRequest request, Pageable pager) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if(member==null){
            return ApiUtils.error("当前用户尚未登录");
        }

        String mmCode = member.getMmCode();
        RdWarehouse warehouse = rdWarehouseService.findByMmCode(mmCode);
        if (warehouse == null ) {
            return ApiUtils.error("该会员没有自提仓库");
        }
        String wareCode = warehouse.getWareCode();

        List<RdInventoryWarning> inventoryWarningList = rdInventoryWarningService.findByWareCodeAndOweInven(wareCode);
        if (inventoryWarningList.size()==0){
            return ApiUtils.error("无欠货商品");
        }
        List<MentionWareGoodsVo> list = new ArrayList<MentionWareGoodsVo>();
        for (RdInventoryWarning inventoryWarning : inventoryWarningList) {
            ShopGoods shopGoods = shopGoodsService.find(Long.valueOf(inventoryWarning.getGoodsCode()));

            MentionWareGoodsVo wareGoodsVo = new MentionWareGoodsVo();
            wareGoodsVo.setWareCode(Optional.ofNullable(inventoryWarning.getWareCode()).orElse(""));
            wareGoodsVo.setGoodsName(Optional.ofNullable(shopGoods.getGoodsName()).orElse(""));
            wareGoodsVo.setGoodsImage(Optional.ofNullable(shopGoods.getGoodsImage()).orElse(""));
            wareGoodsVo.setSpecId(Optional.ofNullable(inventoryWarning.getSpecificationId()).orElse(0l));
            wareGoodsVo.setSpecGoodsSpec(Optional.ofNullable(shopGoods.getGoodsName()).orElse(""));
            wareGoodsVo.setGoodsRetailPrice(Optional.ofNullable(shopGoods.getGoodsRetailPrice()).orElse(BigDecimal.ZERO));
            wareGoodsVo.setGoodsMemberPrice(Optional.ofNullable(shopGoods.getGoodsMemberPrice()).orElse(BigDecimal.ZERO));
            wareGoodsVo.setPpv(Optional.ofNullable(shopGoods.getPpv()).orElse(BigDecimal.ZERO));
            wareGoodsVo.setInventory(Optional.ofNullable(inventoryWarning.getInventory()).orElse(0));
            String ware = inventoryWarning.getWareCode();
            Long specId = inventoryWarning.getSpecificationId();
            list.add(wareGoodsVo);
        }
        return ApiUtils.success(list);
    }

    /**
     * 按欠货创建发货单
     */
    @RequestMapping(value = "/api/mention/createOrder", method = RequestMethod.POST)
    @ResponseBody
    public String createOrder(@RequestParam String wareCode, @RequestParam Map<Long, Integer> specIdNumMap, HttpServletRequest request) throws Exception {
        // 检查不为空
        for (Map.Entry<Long, Integer> entry : specIdNumMap.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) {
                return ApiUtils.error(Xerror.PARAM_INVALID);
            }
        }

        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if (wareCode == null ) {
            return ApiUtils.error("仓库代码为空");
        }
        RdWarehouse warehouseIn = rdWarehouseService.findByCode(wareCode);

        RdWareOrder rdWareOrder = new RdWareOrder();
        rdWareOrder.setId(twiterIdService.getTwiterId());
        String orderSn = "DH"+twiterIdService.getTwiterId();
        rdWareOrder.setOrderSn(orderSn);
        rdWareOrder.setStoreId(warehouseIn.getWareCode());
        rdWareOrder.setStoreName(warehouseIn.getWareName());
        rdWareOrder.setMCode(Optional.ofNullable(warehouseIn.getMmCode()).orElse(""));
        rdWareOrder.setConsigneeName(Optional.ofNullable(warehouseIn.getConsigneeName()).orElse(""));
        rdWareOrder.setWarePhone(Optional.ofNullable(warehouseIn.getWarePhone()).orElse(""));
        rdWareOrder.setOrderType(8);
        rdWareOrder.setOrderState(5);//待审
        RdSysPeriod nowPeriod = rdSysPeriodService.getPeriodService(new Date());
        if (nowPeriod==null){
            rdWareOrder.setCreationPeriod("");
        }else {
            rdWareOrder.setCreationPeriod(nowPeriod.getPeriodCode());
        }
        rdWareOrder.setCreateTime(new Date());
        rdWareOrder.setOrderDesc("欠货创建");


        RdWarehouse warehouseOut = rdWarehouseService.findByCode("20192514");//仓库
        //调拨单
        RdWareAllocation wareAllocation = new RdWareAllocation();
        wareAllocation.setWareCodeIn(warehouseIn.getWareCode());
        wareAllocation.setWareNameIn(warehouseIn.getWareName());
        wareAllocation.setWareCodeOut(warehouseOut.getWareCode());
        wareAllocation.setWareNameOut(warehouseOut.getWareName());
        wareAllocation.setAttachAdd("");
        wareAllocation.setStatus(2);
        wareAllocation.setAutohrizeBy("");
        wareAllocation.setAutohrizeDesc("");

        //查找入库仓库为负数的商品
        /*List<RdInventoryWarning> inventoryWarningList = rdInventoryWarningService.findByWareCodeAndOweInven(wareAllocation.getWareCodeIn());
        if (inventoryWarningList.size()==0){
            return ApiUtils.error("无欠货商品");
        }*/

        //创建调拨单和订单
        rdWareAllocationService.addAllocation(rdWareOrder,wareAllocation,specIdNumMap);

        return ApiUtils.success();
    }

}
