package com.framework.loippi.controller.trade;

import com.alibaba.fastjson.JSON;
import com.framework.loippi.consts.Constants;
import com.framework.loippi.consts.GoodsState;
import com.framework.loippi.consts.OrderState;
import com.framework.loippi.consts.PaymentTallyState;
import com.framework.loippi.controller.GenericController;
import com.framework.loippi.dto.ShippingDto;
import com.framework.loippi.dto.ShopExchangeOrderExcel;
import com.framework.loippi.dto.ShopOrderExcel;
import com.framework.loippi.entity.Principal;
import com.framework.loippi.entity.activity.ShopActivity;
import com.framework.loippi.entity.activity.ShopActivityGoodsSpec;
import com.framework.loippi.entity.common.ShopCommonArea;
import com.framework.loippi.entity.common.ShopCommonExpress;
import com.framework.loippi.entity.order.*;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsClass;
import com.framework.loippi.entity.product.ShopGoodsSpec;
import com.framework.loippi.entity.user.RdMmAddInfo;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.entity.user.RdRanks;
import com.framework.loippi.enus.ActivityTypeEnus;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.result.sys.OrderView;
import com.framework.loippi.service.KuaidiService;
import com.framework.loippi.service.RedisService;
import com.framework.loippi.service.common.ShopCommonAreaService;
import com.framework.loippi.service.common.ShopCommonExpressService;
import com.framework.loippi.service.order.*;
import com.framework.loippi.service.product.ShopGoodsClassService;
import com.framework.loippi.service.product.ShopGoodsService;
import com.framework.loippi.service.product.ShopGoodsSpecService;
import com.framework.loippi.service.user.RdMmAddInfoService;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.service.user.RdMmRelationService;
import com.framework.loippi.service.user.RdRanksService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.GoodsUtils;
import com.framework.loippi.utils.JacksonUtil;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.excel.ExportExcelUtils;
import com.framework.loippi.vo.goods.GoodsSpecVo;
import com.framework.loippi.vo.order.ShopOrderVo;
import com.google.common.collect.Lists;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.RequestContext;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.util.*;

/**
 * Controller - 订单表
 *
 * @author zijing
 * @version 2.0
 */
@Controller("adminModifyShopOrderController")
public class ModifyOrderSysController extends GenericController {

    @Resource
    private ShopOrderService orderService;
    @Resource
    private ShopOrderGoodsService shopOrderGoodsService;
    @Resource
    private ShopOrderAddressService orderAddressService;
    @Resource
    private RdMmAddInfoService rdMmAddInfoService;
    @Resource
    private RdMmRelationService rdMmRelationService;
    @Resource
    private ShopCommonAreaService areaService;
    @Resource
    private ShopOrderLogService orderLogService;
    @Resource
    private RdMmBasicInfoService rdMmBasicInfoService;
    @Resource
    private RdRanksService rdRanksService;
    @Resource
    private ShopCommonExpressService commonExpressService;
    @Resource
    private ShopGoodsService shopGoodsService;
    @Resource
    private ShopGoodsSpecService shopGoodsSpecService;
    @Resource
    private RedisService redisService;
    @Resource
    private ShopOrderDiscountTypeService shopOrderDiscountTypeService;
    @Resource
    private ShopGoodsClassService shopGoodsClassService;
    /**
     * 选择商品
     *
     * @return
     */
    @RequestMapping("/modify/order/selectGoods")
    public String selectGoods(ModelMap model, @RequestParam(defaultValue = "1") Integer pageNo,
                               Integer goodsType,Long orderId,@ModelAttribute ShopGoods goodsPlatform) {
        Pageable pager = new Pageable();
        pager.setPageNumber(pageNo);
        goodsPlatform.setIsDel(GoodsState.GOODS_NOT_DELETE);
        goodsPlatform.setOrderAll(GoodsState.DEFAULT_ORDER);
        if (goodsType!=null){
            goodsPlatform.setGoodsType(goodsType);
        }
        goodsPlatform.setGoodsShow(GoodsState.GOODS_ON_SHOW);
        pager.setParameter(goodsPlatform);
        pager.setOrderProperty("stock");
        pager.setOrderDirection(Order.Direction.DESC);
        Page<ShopGoods> byPage = shopGoodsService.findByPage(pager);
        //商品分类查询及回显
        Map<String, String> treeMap = new HashMap<>();
        Map<String, String> classNameMap = new HashMap<>();
        List<ShopGoodsClass> goodsClasses = new ArrayList<>();
        List<ShopGoodsClass> firstGoodsClasses = new ArrayList<>();
        List<ShopGoodsClass> shopGoodsClassList = shopGoodsClassService.findAll();
        for (ShopGoodsClass item : shopGoodsClassList) {
            if (Optional.ofNullable(item.getDeep()).orElse(0) == 2) {
                goodsClasses.add(item);
                treeMap.put(item.getId() + "", item.getGcParentId() + "");
            } else {
                if (Optional.ofNullable(item.getGcParentId()).orElse(1L)==0L){
                    firstGoodsClasses.add(item);
                }
                classNameMap.put(item.getId() + "", item.getGcName());
            }
        }
        for (ShopGoods item : byPage.getContent()) {
            String tree = treeMap.get(item.getGcId() + "");
            if (tree != null && !"".equals(tree)) {
                item.setGcParentName(Optional.ofNullable(classNameMap.get(tree)).orElse(""));
            }
        }
        model.addAttribute("classList", goodsClasses);
        model.addAttribute("firstGoodsClasses", firstGoodsClasses);
        model.addAttribute("goodsGcId", goodsPlatform.getGcId());
        model.addAttribute("gcParentId", goodsPlatform.getGcParentId());
        model.addAttribute("goodsListKeywords", goodsPlatform.getGoodsListKeywords());
        model.addAttribute("pager", byPage);
        model.addAttribute("orderId", orderId);
        model.addAttribute("type", 1);
        return "/trade/modify_shop_order/goods_select";
    }

    /**
     * 选择规格
     *
     * @param goodsId
     * @return
     */
    @RequestMapping("/modify/order/selectSpec")
    public String selectSpec(ModelMap model, Long goodsId, Long orderGoodsId,Long orderId) {
        List<ShopGoodsSpec> shopGoodsSpecs = shopGoodsSpecService.findListByGoodsId(goodsId);
        for (ShopGoodsSpec shopGoodsSpec : shopGoodsSpecs) {
            shopGoodsSpec.setSpecValueMap(JacksonUtil.convertStrMap(shopGoodsSpec.getSpecGoodsSpec()));
        }
        ShopGoods goods = shopGoodsService.find(goodsId);
        model.addAttribute("shopGoodsSpecs", shopGoodsSpecs);
        model.addAttribute("goods", goods);
        model.addAttribute("goodsId", goodsId);
        model.addAttribute("orderGoodsId", orderGoodsId);
        model.addAttribute("orderId", orderId);
        //获取商品的规格
        if (goods.getGoodsSpec() != null && !goods.getGoodsSpec().trim().equals("")) {
            Map<String, Object> specNameMap = JacksonUtil.convertMap(goods.getSpecName());
            List<String> keys = new ArrayList<>(specNameMap.keySet());
            model.addAttribute("specNameList", keys);
            Map<String, List<GoodsSpecVo>> specMap = GoodsUtils.goodsSpecStrToMapList(goods.getGoodsSpec());
            model.addAttribute("specNameMap", specNameMap);
            model.addAttribute("specMap", specMap);
        }
        model.addAttribute("edit", 1);
        return "/trade/modify_shop_order/spec_select";
    }



    /**
     * 修改订单商品规格
     *
     * @return
     */
    @RequestMapping("/modify/order/goodsSpec")
    public String joinActivity(HttpServletRequest request, ModelMap model, Long goodsId,
                               String specJson,Long orderGoodsId,Long orderId,Integer type,Long shopOrderTypeId) {
        //修改订单商品
        if (type==1){
            orderService.modifyOrderInfo( goodsId,  specJson,  orderGoodsId, orderId);
        }
        //删除订单商品
        if (type==2){
            String shopOrderGoodsJson=redisService.get(orderId+"");
            List<ShopOrderGoods> shopOrderGoodsList=JacksonUtil.convertList(shopOrderGoodsJson, ShopOrderGoods.class);
            List<ShopOrderGoods> newShopOrderGoodsList=new ArrayList<>();
            ShopOrderGoods shopOrderGoods = new ShopOrderGoods();
            for (int i = 0; i <shopOrderGoodsList.size() ; i++) {
                if (!shopOrderGoodsList.get(i).getId().equals(orderGoodsId)){
                    newShopOrderGoodsList.add(shopOrderGoodsList.get(i));
                }
            }
            redisService.save(orderId+"",newShopOrderGoodsList);
        }
        // 查询包括商品和地址
        ShopOrderVo orderVo = orderService.findWithAddrAndGoods(orderId);
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
        model.addAttribute("type", 1);
        if (orderVo.getOrderType()==5){
            model.addAttribute("orderType", 5);
        }
        orderVo=orderService.modifyOrderCalculatePrice(orderVo,orderId,shopOrderTypeId);
        redisService.save(orderId+"orderVo",orderVo);
        orderVo.setShopOrderGoods(redisService.get(orderId+"",List.class));
        List<ShopOrderDiscountType> shopOrderDiscountTypeList=shopOrderDiscountTypeService.findList("totalPpv",orderVo.getPpv());
        model.addAttribute("order", orderVo);
        model.addAttribute("areas", areas);
        model.addAttribute("shopMember", rdMmBasicInfo);
        model.addAttribute("shopMemberAddress", shopMemberAddress);
        model.addAttribute("shopOrderDiscountTypeList", shopOrderDiscountTypeList);
        model.addAttribute("info", "info");
        model.addAttribute("shopOrderTypeId", shopOrderTypeId);
        model.addAttribute("ok", "ok");
            return "/trade/shop_order/edit";
    }

    /**
     * 取消订单修改
     *
     * @return
     */
    @RequestMapping("/modify/order/cancel")
    public String cancel(HttpServletRequest request, Long orderId) {
        redisService.delete(orderId+"");
        redisService.delete(orderId+"orderVo");
        return "redirect:/admin/order/view.jhtml?id="+orderId+"&type=1";
    }

    /**
     * 确认订单修改
     *
     * @return
     */
    @RequestMapping("/modify/order/submit")
    public String submit(HttpServletRequest request, Long orderId) {
        orderService.modifyOrderSubmit(orderId);
        return "redirect:/admin/order/list.jhtml";
    }

}