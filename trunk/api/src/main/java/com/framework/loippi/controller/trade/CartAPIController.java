package com.framework.loippi.controller.trade;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.framework.loippi.consts.CartConstant;
import com.framework.loippi.consts.Constants;
import com.framework.loippi.consts.ShopOrderDiscountTypeConsts;
import com.framework.loippi.controller.BaseController;
import com.framework.loippi.entity.cart.ShopCart;
import com.framework.loippi.entity.order.ShopOrder;
import com.framework.loippi.entity.order.ShopOrderDiscountType;
import com.framework.loippi.entity.order.ShopOrderGoods;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.user.RdMmAddInfo;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.entity.user.RdRanks;
import com.framework.loippi.mybatis.paginator.domain.Order.Direction;
import com.framework.loippi.param.cart.CartAddParam;
import com.framework.loippi.result.app.cart.CartCheckOutResult;
import com.framework.loippi.result.app.cart.CartResult;
import com.framework.loippi.result.auths.AuthsLoginResult;
import com.framework.loippi.service.order.ShopOrderDiscountTypeService;
import com.framework.loippi.service.order.ShopOrderService;
import com.framework.loippi.service.product.ShopCartService;
import com.framework.loippi.service.product.ShopGoodsService;
import com.framework.loippi.service.user.RdMmAccountInfoService;
import com.framework.loippi.service.user.RdMmAddInfoService;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.service.user.RdMmRelationService;
import com.framework.loippi.service.user.RdRanksService;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.ApiUtils;
import com.framework.loippi.utils.JacksonUtil;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.Xerror;
import com.framework.loippi.vo.cart.ShopCartVo;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * 功能： api购物车模块接口 类名：CartAPIController 日期：2017/11/20  10:40 作者：czl 详细说明： 修改备注:
 */
@Controller("apiCartController")
@Slf4j
public class CartAPIController extends BaseController {

    @Resource
    private ShopCartService cartService;
    @Resource
    private ShopOrderDiscountTypeService shopOrderDiscountTypeService;
    @Resource
    private ShopOrderService orderService;
    @Resource
    private RdMmAddInfoService rdMmAddInfoService;
    @Resource
    private RdMmBasicInfoService rdMmBasicInfoService;
    @Resource
    private RdMmRelationService rdMmRelationService;
    @Resource
    private RdRanksService rdRanksService;
    @Resource
    private RdMmAccountInfoService rdMmAccountInfoService;
    @Resource
    private ShopGoodsService goodsService;
    /**
     * 购物车列表
     */
    @RequestMapping(value = "/api/cart/list", method = RequestMethod.POST)
    @ResponseBody
    public String list(Pageable pageable, HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        pageable.setParameter(Paramap.create().put("memberId", member.getMmCode()));
        pageable.setOrderDirection(Direction.DESC);
        pageable.setOrderProperty("id");
        List<ShopCartVo> shopCartVos = cartService.listWithGoodsAndSpec(pageable);
        List<CartResult> cartResults = CartResult.buildList(shopCartVos);
        return ApiUtils.success(cartResults);
    }

    /**
     * 移出购物车
     */
    @RequestMapping(value = "/api/cart/remove", method = RequestMethod.POST)
    @ResponseBody
    public String remove(String cartIds, HttpServletRequest request) {
        if (StringUtils.isEmpty(cartIds)) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }

        // cartIds 购物车id字符串, 用逗号隔开
        String[] idsStr = cartIds.split(",");
        Long[] ids = new Long[idsStr.length];
        for (int i = 0; i < idsStr.length; i++) {
            ids[i] = Long.valueOf(idsStr[i]);
        }
        // 删除条数
        cartService.deleteAll(ids);
        return ApiUtils.success();
    }

    /**
     * 加入购物车
     */
    @RequestMapping(value = "/api/cart/add", method = RequestMethod.POST)
    @ResponseBody
    public String add(@Valid CartAddParam param, BindingResult vResult, HttpServletRequest request) {
        if (vResult.hasErrors()) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", member.getMmCode());
        RdRanks rdRanks = rdRanksService.find("rankId", rdMmRelation.getRank());
        cartService.saveCart(param.getGoodsId(), member.getMmCode(), rdRanks.getRankId(),
            param.getCount(), param.getSpecId(),
            CartConstant.SAVE_TYPE_ADD_TO_CART,
            param.getActivityId(), param.getActivityType(), param.getActivityGoodsId(), param.getActivitySkuId());
        return ApiUtils.success();
    }

    /**
     * 更新购物车
     */
    @RequestMapping(value = "/api/cart/update", method = RequestMethod.POST)
    @ResponseBody
    public String update(@RequestParam long cartId, @RequestParam int num, HttpServletRequest request) {
        if (num < 1) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        //根据商品规格id查询商品规格
        cartService.updateNum(cartId, num, Long.parseLong(member.getMmCode()));
        return ApiUtils.success();
    }

    /**
     * 批量更新
     */
    @RequestMapping(value = "/api/cart/updateBatch", method = RequestMethod.POST)
    @ResponseBody
    public String updateBatch(@RequestParam Map<Long, Integer> cartIdNumMap, HttpServletRequest request) {
        // 检查不为空
        for (Entry<Long, Integer> entry : cartIdNumMap.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) {
                return ApiUtils.error(Xerror.PARAM_INVALID);
            }
        }
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        cartService.updateNumBatch(cartIdNumMap, Long.parseLong(member.getMmCode()));
        return ApiUtils.success();
    }


    /**
     * 购物车结算
     */
    @RequestMapping(value = "/api/cart/checkout", method = RequestMethod.POST)
    @ResponseBody
    public String checkout(@RequestParam String cartIds, Long groupBuyActivityId, Long shopOrderTypeId,
        @RequestParam(defaultValue = "1") Integer logisticType,
        HttpServletRequest request, Long addressId) {
        if (StringUtils.isBlank(cartIds)) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);

        //订单类型相关
        RdMmBasicInfo rdMmBasicInfo = rdMmBasicInfoService.find("mmCode", member.getMmCode());
        RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", member.getMmCode());
        RdRanks rdRanks = rdRanksService.find("rankId", rdMmRelation.getRank());
        //查看该会员类型下 所有可选择的订单类型
        Integer type = 1; //默认显示零售价
        List<ShopOrderDiscountType> orderDiscountTypeList = new ArrayList<>();
        if (rdRanks != null && rdRanks.getRankClass() != null && rdRanks.getRankClass() > 0) {
            type = 2;
            orderDiscountTypeList = shopOrderDiscountTypeService.findAll();
        }

        // 获取收货地址
        RdMmAddInfo addr = null;
        if (addressId != null) {
            addr = rdMmAddInfoService.find("aid", addressId);
        } else {
            List<RdMmAddInfo> addrList = rdMmAddInfoService.findList("mmCode", member.getMmCode());
            if (CollectionUtils.isNotEmpty(addrList)) {
                addr = addrList.stream()
                    .filter(item -> item.getDefaultadd() != null && item.getDefaultadd() == 1)
                    .findFirst()
                    .orElse(addrList.get(0));
            }
        }
        if (logisticType == 2) {
            addr = null;
        }
        ShopOrderDiscountType shopOrderDiscountType = null;
        if (shopOrderTypeId != null) {
            shopOrderDiscountType = shopOrderDiscountTypeService.find(shopOrderTypeId);
            if (shopOrderDiscountType != null) {
                type = shopOrderDiscountType.getPreferentialType();
                if (type != ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_MEMBER
                    && type != ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_PPV
                    && type != ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_PREFERENTIAL
                    && type != ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_RETAIL) {
                    type = ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_RETAIL;
                    shopOrderDiscountType.setPreferentialType(type);
                }
            }
        }
        if (shopOrderDiscountType == null) {
            shopOrderDiscountType = new ShopOrderDiscountType();
            shopOrderDiscountType.setId(-1L);
            shopOrderDiscountType.setPreferentialType(type);
        }
        Map<String, Object> map = cartService
            .queryTotalPrice(cartIds, member.getMmCode(), null, groupBuyActivityId, shopOrderDiscountType, addr);
        // 购物车数据
        if (map.get("error").equals("true")) {
            return ApiUtils.error("商品属性发生改变,请重新结算");
        }
        List<ShopCart> cartList = Lists.newArrayList();
        if (StringUtils.isNotEmpty(cartIds) && !"null".equals(cartIds)) {
            String[] cartId = cartIds.split(",");
            if (cartId != null && cartId.length > 0) {
                cartList = cartService.findList("ids", cartId);
            }
        }
        if (CollectionUtils.isEmpty(cartList)) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        CartCheckOutResult result = CartCheckOutResult
            .build(map, cartList, addr, shopOrderTypeId, shopOrderDiscountType);
        if (log.isDebugEnabled()) {
            log.debug(JacksonUtil.toJson(result));
        }

        // TODO: 2018/12/14 自提地址 自提地址 id为-1 表示平台地址
        RdMmAddInfo shopMemberAddress = rdMmAddInfoService.find("aid", -1);
        List<ShopOrderDiscountType> shopOrderDiscountTypeList = new ArrayList<>();
        if (rdRanks != null && rdRanks.getRankClass() != null && rdRanks.getRankClass() > 0) {
            shopOrderDiscountTypeList = shopOrderDiscountTypeService.findList("totalPpv", result.getTotalPpv());
        }
        result = result.build2(result, shopOrderDiscountTypeList, rdRanks, rdMmBasicInfo, shopMemberAddress,
            orderDiscountTypeList);
        return ApiUtils.success(result);
    }

    /**
     * 购物车结算新 TODO
     */
    @RequestMapping(value = "/api/cart/checkoutNew", method = RequestMethod.POST)
    @ResponseBody
    public String checkoutNew(@RequestParam String cartIds, Long groupBuyActivityId, Long shopOrderTypeId,
                           @RequestParam(defaultValue = "1") Integer logisticType,
                           HttpServletRequest request, Long addressId) {
        if (StringUtils.isBlank(cartIds)) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);

        //订单类型相关
        RdMmBasicInfo rdMmBasicInfo = rdMmBasicInfoService.find("mmCode", member.getMmCode());
        RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", member.getMmCode());
        RdRanks rdRanks = rdRanksService.find("rankId", rdMmRelation.getRank());
        //查看该会员类型下 所有可选择的订单类型
        Integer type = 1; //默认显示零售价
        List<ShopOrderDiscountType> orderDiscountTypeList = new ArrayList<>();
        if (rdRanks != null && rdRanks.getRankClass() != null && rdRanks.getRankClass() > 0) {
            type = 2;
            orderDiscountTypeList = shopOrderDiscountTypeService.findAll();
        }

        // 获取收货地址
        RdMmAddInfo addr = null;
        if (addressId != null) {
            addr = rdMmAddInfoService.find("aid", addressId);
        } else {
            List<RdMmAddInfo> addrList = rdMmAddInfoService.findList("mmCode", member.getMmCode());
            if (CollectionUtils.isNotEmpty(addrList)) {
                addr = addrList.stream()
                        .filter(item -> item.getDefaultadd() != null && item.getDefaultadd() == 1)
                        .findFirst()
                        .orElse(addrList.get(0));
            }
        }
        if (logisticType == 2) {
            addr = null;
        }
        ShopOrderDiscountType shopOrderDiscountType = null;
        if (shopOrderTypeId != null) {
            shopOrderDiscountType = shopOrderDiscountTypeService.find(shopOrderTypeId);
            if (shopOrderDiscountType != null) {
                type = shopOrderDiscountType.getPreferentialType();
                if (type != ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_MEMBER
                        && type != ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_PPV
                        && type != ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_PREFERENTIAL
                        && type != ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_RETAIL) {
                    type = ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_RETAIL;
                    shopOrderDiscountType.setPreferentialType(type);
                }
            }
        }
        if (shopOrderDiscountType == null) {
            shopOrderDiscountType = new ShopOrderDiscountType();
            shopOrderDiscountType.setId(-1L);
            shopOrderDiscountType.setPreferentialType(type);
        }
        Map<String, Object> map = cartService
                .queryTotalPrice(cartIds, member.getMmCode(), null, groupBuyActivityId, shopOrderDiscountType, addr);
        // 购物车数据
        if (map.get("error").equals("true")) {
            return ApiUtils.error("商品属性发生改变,请重新结算");
        }
        List<ShopCart> cartList = Lists.newArrayList();
        if (StringUtils.isNotEmpty(cartIds) && !"null".equals(cartIds)) {
            String[] cartId = cartIds.split(",");
            if (cartId != null && cartId.length > 0) {
                cartList = cartService.findList("ids", cartId);
            }
        }
        if (CollectionUtils.isEmpty(cartList)) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        CartCheckOutResult result = CartCheckOutResult
                .build(map, cartList, addr, shopOrderTypeId, shopOrderDiscountType);
        if (log.isDebugEnabled()) {
            log.debug(JacksonUtil.toJson(result));
        }

        // TODO: 2018/12/14 自提地址 自提地址 id为-1 表示平台地址
        RdMmAddInfo shopMemberAddress = rdMmAddInfoService.find("aid", -1);
        List<ShopOrderDiscountType> shopOrderDiscountTypeList = new ArrayList<>();
        if (rdRanks != null && rdRanks.getRankClass() != null && rdRanks.getRankClass() > 0) {
            shopOrderDiscountTypeList = shopOrderDiscountTypeService.findList("totalPpv", result.getTotalPpv());
        }
        result = result.build2(result, shopOrderDiscountTypeList, rdRanks, rdMmBasicInfo, shopMemberAddress,
                orderDiscountTypeList);
        /*********************************************************************************************************************/
        Integer flag=0;
        Integer giftsNum=0;
        ArrayList<ShopGoods> shopGoods = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date startTime = format.parse("2019-11-01 00:00:00");
            Date endTime = format.parse("2019-11-12 23:59:59");
            Date nowTime = new Date();
            boolean b = belongCalendar(nowTime, startTime, endTime);
            if(b){
                if(rdMmRelation.getRank()==0&&result.getNeedToPay().compareTo(new BigDecimal("360"))!=-1){
                    flag=1;
                    giftsNum=result.getNeedToPay().divideToIntegralValue(new BigDecimal("360")).intValue();
                    ShopGoods goods = goodsService.find(6587959889232924672L);//老棉农棉柔巾20*20cm*100抽*3包/提
                    ShopGoods goods1 = goodsService.find(6587960502192705536L);//老棉农棉柔巾（加厚款）20*20cm*60抽*3包/提
                    ShopGoods goods2 = goodsService.find(6587961856550244352L);//老棉农婴儿棉柔巾 10*20cm*100抽*6包/提
                    ShopGoods goods3 = goodsService.find(6573038322627645440L);//OLOMI母婴抑菌倍护洗衣凝珠
                    ShopGoods goods4 = goodsService.find(6552743534695288832L);//OLOMI植萃精华保湿面膜
                    ShopGoods goods5 = goodsService.find(6552746788883795968L);//OLOMI植萃精华修护面膜
                    if (goods!=null){
                        shopGoods.add(goods);
                    }
                    if (goods1!=null){
                        shopGoods.add(goods1);
                    }
                    if (goods2!=null){
                        shopGoods.add(goods2);
                    }
                    if (goods3!=null){
                        shopGoods.add(goods3);
                    }
                    if (goods4!=null){
                        shopGoods.add(goods4);
                    }
                    if (goods5!=null){
                        shopGoods.add(goods5);
                    }
                }else if(rdMmRelation.getRank()>0&&result.getActualTotalPpv().compareTo(new BigDecimal("25"))!=-1&&result.getActualTotalPpv().compareTo(new BigDecimal("50"))==-1){
                    flag=1;
                    giftsNum=result.getActualTotalPpv().divideToIntegralValue(new BigDecimal("25")).intValue();
                    ShopGoods goods = goodsService.find(6587959889232924672L);//老棉农棉柔巾20*20cm*100抽*3包/提
                    ShopGoods goods1 = goodsService.find(6587960502192705536L);//老棉农棉柔巾（加厚款）20*20cm*60抽*3包/提
                    ShopGoods goods2 = goodsService.find(6587961856550244352L);//老棉农婴儿棉柔巾 10*20cm*100抽*6包/提
                    ShopGoods goods3 = goodsService.find(6573038322627645440L);//OLOMI母婴抑菌倍护洗衣凝珠
                    ShopGoods goods4 = goodsService.find(6552743534695288832L);//OLOMI植萃精华保湿面膜
                    ShopGoods goods5 = goodsService.find(6552746788883795968L);//OLOMI植萃精华修护面膜
                    if (goods!=null){
                        shopGoods.add(goods);
                    }
                    if (goods1!=null){
                        shopGoods.add(goods1);
                    }
                    if (goods2!=null){
                        shopGoods.add(goods2);
                    }
                    if (goods3!=null){
                        shopGoods.add(goods3);
                    }
                    if (goods4!=null){
                        shopGoods.add(goods4);
                    }
                    if (goods5!=null){
                        shopGoods.add(goods5);
                    }
                }else if(rdMmRelation.getRank()>0&&result.getActualTotalPpv().compareTo(new BigDecimal("50"))!=-1){
                    flag=1;
                    giftsNum=result.getActualTotalPpv().divideToIntegralValue(new BigDecimal("50")).intValue();
                    ShopGoods goods = goodsService.find(6552806260624855040L);//OLOMI氨基酸温和修护洁面乳
                    ShopGoods goods1 = goodsService.find(6587956972564254720L);//OLOMI橙花精油温和卸妆水
                    ShopGoods goods2 = goodsService.find(6592325180272414720L);//【组合套装】家具清洁套装
                    ShopGoods goods3 = goodsService.find(6573037643838263296L);//OLOMI酵素多效洗衣凝珠
                    if (goods!=null){
                        shopGoods.add(goods);
                    }
                    if (goods1!=null){
                        shopGoods.add(goods1);
                    }
                    if (goods2!=null){
                        shopGoods.add(goods2);
                    }
                    if (goods3!=null){
                        shopGoods.add(goods3);
                    }
                }
            }
            result=result.build3(result,shopGoods,flag,giftsNum);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return ApiUtils.success(result);
    }

    /**
     * 购物车结算新(优惠券使用) TODO
     */
    @RequestMapping(value = "/api/cart/checkoutNew1", method = RequestMethod.POST)
    @ResponseBody
    public String checkoutNew1(@RequestParam String cartIds, Long groupBuyActivityId, Long shopOrderTypeId,
                              @RequestParam(defaultValue = "1") Integer logisticType,
                              @RequestParam(required = false,value = "couponId") Long couponId,
                              HttpServletRequest request, Long addressId) {
        if (StringUtils.isBlank(cartIds)) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);

        //订单类型相关
        RdMmBasicInfo rdMmBasicInfo = rdMmBasicInfoService.find("mmCode", member.getMmCode());
        RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", member.getMmCode());
        RdRanks rdRanks = rdRanksService.find("rankId", rdMmRelation.getRank());
        //查看该会员类型下 所有可选择的订单类型
        Integer type = 1; //默认显示零售价
        List<ShopOrderDiscountType> orderDiscountTypeList = new ArrayList<>();
        if (rdRanks != null && rdRanks.getRankClass() != null && rdRanks.getRankClass() > 0) {
            type = 2;
            orderDiscountTypeList = shopOrderDiscountTypeService.findAll();
        }

        // 获取收货地址
        RdMmAddInfo addr = null;
        if (addressId != null) {
            addr = rdMmAddInfoService.find("aid", addressId);
        } else {
            List<RdMmAddInfo> addrList = rdMmAddInfoService.findList("mmCode", member.getMmCode());
            if (CollectionUtils.isNotEmpty(addrList)) {
                addr = addrList.stream()
                        .filter(item -> item.getDefaultadd() != null && item.getDefaultadd() == 1)
                        .findFirst()
                        .orElse(addrList.get(0));
            }
        }
        if (logisticType == 2) {
            addr = null;
        }
        ShopOrderDiscountType shopOrderDiscountType = null;
        if (shopOrderTypeId != null) {
            shopOrderDiscountType = shopOrderDiscountTypeService.find(shopOrderTypeId);
            if (shopOrderDiscountType != null) {
                type = shopOrderDiscountType.getPreferentialType();
                if (type != ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_MEMBER
                        && type != ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_PPV
                        && type != ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_PREFERENTIAL
                        && type != ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_RETAIL) {
                    type = ShopOrderDiscountTypeConsts.DISCOUNT_TYPE_RETAIL;
                    shopOrderDiscountType.setPreferentialType(type);
                }
            }
        }
        if (shopOrderDiscountType == null) {
            shopOrderDiscountType = new ShopOrderDiscountType();
            shopOrderDiscountType.setId(-1L);
            shopOrderDiscountType.setPreferentialType(type);
        }
        Map<String, Object> map = cartService
                .queryTotalPrice1(cartIds, member.getMmCode(), couponId, groupBuyActivityId, shopOrderDiscountType, addr);
        // 购物车数据
        if (map.get("error").equals("true")) {
            return ApiUtils.error("商品属性发生改变,请重新结算");
        }
        List<ShopCart> cartList = Lists.newArrayList();
        if (StringUtils.isNotEmpty(cartIds) && !"null".equals(cartIds)) {
            String[] cartId = cartIds.split(",");
            if (cartId != null && cartId.length > 0) {
                cartList = cartService.findList("ids", cartId);
            }
        }
        if (CollectionUtils.isEmpty(cartList)) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        CartCheckOutResult result = CartCheckOutResult
                .buildNew(map, cartList, addr, shopOrderTypeId, shopOrderDiscountType);
        if (log.isDebugEnabled()) {
            log.debug(JacksonUtil.toJson(result));
        }

        // TODO: 2018/12/14 自提地址 自提地址 id为-1 表示平台地址
        RdMmAddInfo shopMemberAddress = rdMmAddInfoService.find("aid", -1);
        List<ShopOrderDiscountType> shopOrderDiscountTypeList = new ArrayList<>();
        if (rdRanks != null && rdRanks.getRankClass() != null && rdRanks.getRankClass() > 0) {
            shopOrderDiscountTypeList = shopOrderDiscountTypeService.findList("totalPpv", result.getTotalPpv());
        }
        result = result.build2(result, shopOrderDiscountTypeList, rdRanks, rdMmBasicInfo, shopMemberAddress,
                orderDiscountTypeList);
        //***************************************************************************************************************************
        Integer flag=0;
        Integer giftsNum=0;
        ArrayList<ShopGoods> shopGoods = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date startTime = format.parse("2020-01-13 09:30:00");
            Date endTime = format.parse("2020-01-17 13:59:59");
            /*Date startTime1 = format.parse("2020-01-09 00:00:00");
            Date endTime1 = format.parse("2020-01-17 13:59:59");*/
            Date nowTime = new Date();
            boolean b = belongCalendar(nowTime, startTime, endTime);
            /*boolean c = belongCalendar(nowTime, startTime1, endTime1);*/
            /*if(c){
                if(rdMmRelation.getRank()>0&&result.getActualTotalPpv().compareTo(new BigDecimal("50"))!=-1){//单笔订单满50mi，赠送手提袋一个，利是红包一包（10枚），酵素洗衣凝珠一桶
                    flag=1;
                    ShopGoods goods1 = goodsService.find(6573037643838263296L);//酵素多效洗衣凝珠+利是红包+手提袋
                    if (goods1!=null){
                        shopGoods.add(goods1);
                    }
                    giftsNum=1;
                }else if((rdMmRelation.getRank()==0&&result.getNeedToPay().compareTo(new BigDecimal("360"))!=-1)||
                        (rdMmRelation.getRank()>0&&result.getActualTotalPpv().compareTo(new BigDecimal("25"))!=-1&&result.getActualTotalPpv().compareTo(new BigDecimal("50"))==-1)){
                    flag=1;
                    //单笔订单满360或25mi，赠送手提袋一个，利是红包一包（10枚），母婴洗衣凝珠一包
                    ShopGoods goods1 = goodsService.find(6573038322627645440L);//母婴洗衣凝珠+利是红包+手提袋
                    if (goods1!=null){
                        shopGoods.add(goods1);
                    }
                    giftsNum=1;
                }
            }*/
            if(b){
                flag=1;
                if(rdMmRelation.getRank()>0&&result.getActualTotalPpv().compareTo(new BigDecimal("50"))!=-1){//单笔订单满50mi，赠送手提袋一个，利是红包一包（10枚），酵素洗衣凝珠一桶
                    //ShopGoods goods1 = goodsService.find(6620198081273008128L);//酵素多效洗衣凝珠+利是红包+手提袋
                    //ShopGoods goods1 = goodsService.find(6622327967039098880L);//酵素多效洗衣凝珠+利是红包+手提袋 正式
                    ShopGoods goods1 = goodsService.find(6573037643838263296L);//酵素多效洗衣凝珠
                    if (goods1!=null){
                        shopGoods.add(goods1);
                    }
                    giftsNum=1;
                }else if((rdMmRelation.getRank()==0&&result.getNeedToPay().compareTo(new BigDecimal("360"))!=-1)||
                        (rdMmRelation.getRank()>0&&result.getActualTotalPpv().compareTo(new BigDecimal("25"))!=-1&&result.getActualTotalPpv().compareTo(new BigDecimal("50"))==-1)){
                    //单笔订单满360或25mi，赠送手提袋一个，利是红包一包（10枚），母婴洗衣凝珠一包
                    //ShopGoods goods1 = goodsService.find(6620197579269345280L);//母婴洗衣凝珠+利是红包+手提袋
                    //ShopGoods goods1 = goodsService.find(6622327604944834560L);//母婴洗衣凝珠+利是红包+手提袋 正式
                    ShopGoods goods1 = goodsService.find(6573038322627645440L);//母婴洗衣凝珠+利是红包+手提袋 正式
                    if (goods1!=null){
                        shopGoods.add(goods1);
                    }
                    giftsNum=1;
                }else {//凡有购买产品的订单，即赠送利是红包一包（10枚）
                    //ShopGoods goods1 = goodsService.find(6620193953163513856L);//利是红包一包（10枚）
                    /*ShopGoods goods1 = goodsService.find(6622325662478766080L);//利是红包一包（10枚）
                    if (goods1!=null){
                        shopGoods.add(goods1);
                    }*/
                    giftsNum=1;
                }
            }
            /*if(c){//TODO
                List<Long> goodsIds = (List<Long>) map.get("goodsIds");
                for (Long goodsId : goodsIds) {
                    if(goodsId.equals(6597416766391980032L)){//如果商品中有保湿面膜 则赠品赠送一个杯子
                        flag=1;
                        ShopGoods goods1 = goodsService.find(6587959889232924672L);//利是红包一包（10枚）
                        if (goods1!=null){
                            shopGoods.add(goods1);
                        }
                        giftsNum=1;
                        break;
                    }
                }
            }*/
            result=result.build3(result,shopGoods,flag,giftsNum);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ApiUtils.success(result);
    }

    public static boolean belongCalendar(Date nowTime, Date beginTime, Date endTime) {
        //设置当前时间
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);
        //设置开始时间
        Calendar begin = Calendar.getInstance();
        begin.setTime(beginTime);
        //设置结束时间
        Calendar end = Calendar.getInstance();
        end.setTime(endTime);
        //处于开始时间之后，和结束时间之前的判断
        if (date.after(begin) && date.before(end)) {
         return true;
        } else {
         return false;
        }
    }

    /**
     * 立即购买
     */
    @RequestMapping(value = "/api/cart/buyNow", method = RequestMethod.POST)
    @ResponseBody
    public String buyNow(@Valid CartAddParam param, BindingResult vResult, HttpServletRequest request) {
        if (vResult.hasErrors()) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        Long groupBuyActivityId = param.getActivityId();
        Long cartId = cartService.saveCart(param.getGoodsId(), member.getMmCode(),
            null, param.getCount(),
            param.getSpecId(), 1, param.getActivityId(),
            param.getActivityType(), param.getActivityGoodsId(), param.getActivitySkuId());
        return checkout(cartId.toString(), null, groupBuyActivityId, 1, request, null);
    }

    /**
     * 购物车数量
     */
    @RequestMapping(value = "/api/cart/count", method = RequestMethod.POST)
    @ResponseBody
    public String countOfMemberCart(HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        Long count = cartService.count(Paramap.create().put("memberId", member.getMmCode()));
        return ApiUtils.success(count);
    }

    /**
     * 再次购买
     */

    @RequestMapping(value = "/api/cart/buyAgain", method = RequestMethod.POST)
    @ResponseBody
    public String buyAgain(Long orderId, HttpServletRequest request) {
        try {
            if (orderId == null) {
                ApiUtils.error(Xerror.PARAM_INVALID);
            }
            AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
            ShopOrder order = orderService.findWithOrderGoodsById(orderId);
            if (order == null || order.getShopOrderGoodses() == null || order.getShopOrderGoodses().size() < 1) {
                ApiUtils.error("没有购买记录");
            }
            List<ShopCart> cartList = new ArrayList<>();
            for (ShopOrderGoods item : order.getShopOrderGoodses()) {
                ShopCart cart = new ShopCart();
                cart.setGoodsId(item.getGoodsId());
                cart.setMemberId(Long.parseLong(item.getBuyerId()));
                cart.setGoodsNum(item.getGoodsNum());
                cart.setSpecId(item.getSpecId());
                cart.setActivityId(item.getActivityId());
                cart.setActivityType(item.getActivityType());
                cartList.add(cart);
            }
            RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", member.getMmCode());
            RdRanks rdRanks = rdRanksService.find("rankId", rdMmRelation.getRank());
            List<Long> list = cartService.saveCartList(cartList, member.getMmCode(),rdRanks);
            return checkout(Joiner.on(",").join(list), null, -1L, 1, request, null);
        } catch (Exception e) {
            log.error("再次购买错误", e);
            return ApiUtils.error(e.getMessage());
        }
    }

    /**
     * 再次购买
     */

    @RequestMapping(value = "/api/cart/buyAgainNew", method = RequestMethod.POST)
    @ResponseBody
    public String buyAgainNew(Long orderId, HttpServletRequest request) {
        try {
            if (orderId == null) {
                ApiUtils.error(Xerror.PARAM_INVALID);
            }
            AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
            ShopOrder order = orderService.findWithOrderGoodsById(orderId);
            if (order == null || order.getShopOrderGoodses() == null || order.getShopOrderGoodses().size() < 1) {
                ApiUtils.error("没有购买记录");
            }
            List<ShopCart> cartList = new ArrayList<>();
            for (ShopOrderGoods item : order.getShopOrderGoodses()) {
                ShopCart cart = new ShopCart();
                cart.setGoodsId(item.getGoodsId());
                cart.setMemberId(Long.parseLong(item.getBuyerId()));
                cart.setGoodsNum(item.getGoodsNum());
                cart.setSpecId(item.getSpecId());
                cart.setActivityId(item.getActivityId());
                cart.setActivityType(item.getActivityType());
                if(item.getIsPresentation()==null||item.getIsPresentation()!=1){
                    cartList.add(cart);
                }
            }
            RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", member.getMmCode());
            RdRanks rdRanks = rdRanksService.find("rankId", rdMmRelation.getRank());
            List<Long> list = cartService.saveCartList(cartList, member.getMmCode(),rdRanks);
            return checkoutNew1(Joiner.on(",").join(list), null, -1L, 1,null,request, null);
        } catch (Exception e) {
            log.error("再次购买错误", e);
            return ApiUtils.error(e.getMessage());
        }
    }
}
