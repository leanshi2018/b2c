package com.framework.loippi.controller.trade;

import com.framework.loippi.consts.CartConstant;
import com.framework.loippi.consts.Constants;
import com.framework.loippi.controller.BaseController;
import com.framework.loippi.entity.cart.ShopCartExchange;
import com.framework.loippi.entity.product.ShopGoodsFreightRule;
import com.framework.loippi.entity.user.RdMmAddInfo;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.entity.user.RdRanks;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.param.cart.CartAddParam;
import com.framework.loippi.result.app.cart.CartExchangeCheckOutResult;
import com.framework.loippi.result.app.cart.CartExchangeResult;
import com.framework.loippi.result.app.cart.CartResult;
import com.framework.loippi.result.auths.AuthsLoginResult;
import com.framework.loippi.service.product.ShopCartExchangeService;
import com.framework.loippi.service.product.ShopGoodsFreightRuleService;
import com.framework.loippi.service.product.ShopGoodsService;
import com.framework.loippi.service.user.RdMmAddInfoService;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.service.user.RdMmRelationService;
import com.framework.loippi.service.user.RdRanksService;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.ApiUtils;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.Xerror;
import com.framework.loippi.vo.cart.ShopCartExchangeVo;
import com.framework.loippi.vo.cart.ShopCartVo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 换购商品购物车
 */
@Controller("redemptionCartController")
@Slf4j
public class RedemptionCartController extends BaseController {
    @Resource
    private ShopGoodsService goodsService;
    @Resource
    private ShopCartExchangeService shopCartExchangeService;
    @Resource
    private RdMmRelationService rdMmRelationService;
    @Resource
    private ShopGoodsFreightRuleService shopGoodsFreightRuleService;
    @Resource
    private RdRanksService rdRanksService;
    @Resource
    private RdMmBasicInfoService rdMmBasicInfoService;
    @Resource
    private RdMmAddInfoService rdMmAddInfoService;

    /**
     * 换购商品购物车列表
     */
    @RequestMapping(value = "/api/redemption/cart/list", method = RequestMethod.POST)
    @ResponseBody
    public String list(Pageable pageable, HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        pageable.setParameter(Paramap.create().put("memberId", member.getMmCode()));
        pageable.setOrderDirection(Order.Direction.DESC);
        pageable.setOrderProperty("id");
        List<ShopCartExchangeVo> shopCartExchangeVos = shopCartExchangeService.listWithGoodsAndSpec(pageable);
        RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode",member.getMmCode());
        ShopGoodsFreightRule shopGoodsFreightRule = shopGoodsFreightRuleService.find("memberGradeId",rdMmRelation.getRank());
        List<CartExchangeResult> cartResults = CartExchangeResult.buildList(shopCartExchangeVos,shopGoodsFreightRule.getMinimumOrderAmount());
        return ApiUtils.success(cartResults);
    }

    /**
     * 移出换购商品购物车
     */
    @RequestMapping(value = "/api/redemption/cart/remove", method = RequestMethod.POST)
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
        shopCartExchangeService.deleteAll(ids);
        return ApiUtils.success();
    }

    /**
     * 加入换购商品购物车
     */
    @RequestMapping(value = "/api/redemption/cart/add", method = RequestMethod.POST)
    @ResponseBody
    public String add(@Valid CartAddParam param, BindingResult vResult, HttpServletRequest request) {
        if (vResult.hasErrors()) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", member.getMmCode());
        RdRanks rdRanks = rdRanksService.find("rankId", rdMmRelation.getRank());
        shopCartExchangeService.saveExchangeCart(param.getGoodsId(), member.getMmCode(), rdRanks.getRankId(),
                param.getCount(), param.getSpecId(),
                CartConstant.SAVE_TYPE_ADD_TO_CART);
        return ApiUtils.success();
    }

    /**
     * 更新换购商品购物车
     */
    @RequestMapping(value = "/api/redemption/cart/update", method = RequestMethod.POST)
    @ResponseBody
    public String update(@RequestParam long cartId, @RequestParam int num, HttpServletRequest request) {
        if (num < 1) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        //根据商品规格id查询商品规格
        shopCartExchangeService.updateNum(cartId, num, Long.parseLong(member.getMmCode()));
        return ApiUtils.success();
    }

    /**
     * 批量更新换购商品购物车
     */
    @RequestMapping(value = "/api/redemption/cart/updateBatch", method = RequestMethod.POST)
    @ResponseBody
    public String updateBatch(@RequestParam Map<Long, Integer> cartIdNumMap, HttpServletRequest request) {
        // 检查不为空
        for (Map.Entry<Long, Integer> entry : cartIdNumMap.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) {
                return ApiUtils.error(Xerror.PARAM_INVALID);
            }
        }
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        shopCartExchangeService.updateNumBatch(cartIdNumMap, Long.parseLong(member.getMmCode()));
        return ApiUtils.success();
    }

    /**
     * 换购商品购物车结算
     */
    @RequestMapping(value = "/api/redemption/cart/checkout", method = RequestMethod.POST)
    @ResponseBody
    public String checkout(@RequestParam String cartIds,HttpServletRequest request, Long addressId) {
        if (StringUtils.isBlank(cartIds)) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);

        //订单类型相关
        RdMmBasicInfo rdMmBasicInfo = rdMmBasicInfoService.find("mmCode", member.getMmCode());
        RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", member.getMmCode());
        RdRanks rdRanks = rdRanksService.find("rankId", rdMmRelation.getRank());
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
        Map<String, Object> map = shopCartExchangeService
                .queryTotalPrice(cartIds, member.getMmCode(),addr);
        // 购物车数据
        if (map.get("error").equals("true")) {
            return ApiUtils.error("商品属性发生改变,请重新结算");
        }
        List<ShopCartExchange> cartList = Lists.newArrayList();
        if (StringUtils.isNotEmpty(cartIds) && !"null".equals(cartIds)) {
            String[] cartId = cartIds.split(",");
            if (cartId != null && cartId.length > 0) {
                cartList = shopCartExchangeService.findList("ids", cartId);
            }
        }
        if (CollectionUtils.isEmpty(cartList)) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        CartExchangeCheckOutResult result = CartExchangeCheckOutResult
                .build(map, cartList, addr);
        return ApiUtils.success(result);
    }
}
