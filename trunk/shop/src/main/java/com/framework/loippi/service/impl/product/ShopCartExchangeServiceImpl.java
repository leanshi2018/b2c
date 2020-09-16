package com.framework.loippi.service.impl.product;

import com.framework.loippi.consts.CartConstant;
import com.framework.loippi.consts.GoodsState;
import com.framework.loippi.consts.ShopOrderDiscountTypeConsts;
import com.framework.loippi.controller.AppConstants;
import com.framework.loippi.controller.StateResult;
import com.framework.loippi.dao.cart.ShopCartExchangeDao;
import com.framework.loippi.dao.product.ShopGoodsBrandDao;
import com.framework.loippi.entity.cart.ShopCart;
import com.framework.loippi.entity.cart.ShopCartExchange;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsBrand;
import com.framework.loippi.entity.product.ShopGoodsSpec;
import com.framework.loippi.entity.user.RdMmAddInfo;
import com.framework.loippi.entity.user.RdRanks;
import com.framework.loippi.pojo.cart.CartExchangeInfo;
import com.framework.loippi.pojo.cart.CartInfo;
import com.framework.loippi.pojo.cart.CartVo;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.product.*;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.GoodsUtils;
import com.framework.loippi.utils.NumberUtils;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.StringUtil;
import com.framework.loippi.vo.cart.ShopCartExchangeVo;
import com.framework.loippi.vo.cart.ShopCartVo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Service
@Slf4j
@Transactional
public class ShopCartExchangeServiceImpl extends GenericServiceImpl<ShopCartExchange, Long> implements ShopCartExchangeService {
    @Resource
    private ShopCartExchangeDao shopCartExchangeDao;
    @Resource
    private ShopGoodsService goodsService;
    @Resource
    private ShopGoodsSpecService goodsSpecService;
    @Resource
    private TwiterIdService twiterIdService;
    @Resource
    private ShopGoodsBrandDao shopGoodsBrandDao;
    @Resource
    private ShopGoodsFreightService shopGoodsFreightService;
    @Resource
    private ShopGoodsFreightRuleService shopGoodsFreightRuleService;
    /**
     * 查出换购商品购物车列表
     * @param pageable
     * @return
     */
    @Override
    public List<ShopCartExchangeVo> listWithGoodsAndSpec(Pageable pageable) {
        return shopCartExchangeDao.listShopCartExchangeVoWithGoodsAndSpec(pageable.getParameter(), pageable.getPageBounds());
    }

    /**
     * 添加换购商品购物车
     * @param goodsId 商品id
     * @param mmCode 会员编号
     * @param rankId 级别
     * @param count 数量
     * @param specId 规格id
     * @param saveTypeAddToCart 添加类型 立即购买:1   加入购物车:0
     */
    @Override
    public Long saveExchangeCart(Long goodsId, String mmCode, Integer rankId, Integer count, Long specId, int saveTypeAddToCart) {
        //商品信息
        ShopGoods goods = goodsService.find(goodsId);
        //商品规格信息
        ShopGoodsSpec goodsSpec = goodsSpecService.find(specId);
        if (goodsSpec.getSpecIsopen()!=1) {
            throw new RuntimeException("该商品目前不能购买");
        }
        //判断是否有购买权限
        if (!StringUtil.isEmpty(goods.getSalePopulationIds())){
            if (!goods.getSalePopulationIds().contains(","+rankId + ",")){
                throw new RuntimeException(goods.getGoodsName()+"目前您的会员等级不能购买该商品");
            }
        }
        // 能否修改购物车
        canSaveOrUpdateCart(goods, goodsSpec, count);
        GoodsUtils.getSepcMapAndColImgToGoodsSpec(goods, goodsSpec);
        ShopCartExchange cart = new ShopCartExchange();
        cart.setId(twiterIdService.getTwiterId());
        cart.setMemberId(Long.parseLong(mmCode));
        cart.setGoodsId(goodsSpec.getGoodsId());
        cart.setGoodsName(goods.getGoodsName());
        cart.setSpecId(goodsSpec.getId());
        cart.setGoodsType(goods.getGoodsType());
        //设置价格
        cart.setGoodsMemberPrice(goodsSpec.getSpecMemberPrice());
        cart.setGoodsBigPrice(goodsSpec.getSpecBigPrice());
        cart.setGoodsRetailPrice(goodsSpec.getSpecRetailPrice());
        cart.setBigPpv(goodsSpec.getBigPpv());
        cart.setPpv(goodsSpec.getPpv());
        cart.setGoodsState(goods.getGoodsType());
        cart.setBrandId(goods.getBrandId());
        ShopGoodsBrand shopGoodsBrand = shopGoodsBrandDao.find(goods.getBrandId());
        cart.setBrandIcon(shopGoodsBrand.getBrandIcon());
        cart.setBrandName(goods.getBrandName());
        cart.setWeight(goodsSpec.getWeight());
        // 图片信息
        if (goods.getGoodsImage() != null) {
            //存储商品默认图片
            cart.setGoodsImages(goods.getGoodsImage().split(",")[0]);
        } else {
            //若商品没有默认图片存储空字段
            cart.setGoodsImages("");
        }
        // 规格信息--新建一个字段存储新的规格格式
        if (goods.getGoodsType()==3){
            cart.setSpecInfo(goodsSpec.getSpecGoodsSerial());
        }else{
            String specInfo = "";
            Map<String, String> map = goodsSpec.getSepcMap();
            //遍历规格map,取出键值对,拼接specInfo
            if (map != null) {
                Set<String> set = map.keySet();
                for (String str : set) {
                    specInfo += str + ":" + map.get(str) + "、";
                }
                specInfo = specInfo.substring(0, specInfo.length() - 1);
            }
            cart.setSpecInfo(specInfo);
        }
        // 数量信息--判断是否存在相同购物车信息,包括商品id,用户id,和规格id
        Paramap paramap = Paramap.create()
                .put("goodsId", cart.getGoodsId()) // 商品id
                .put("memberId", cart.getMemberId()) // 用户id
                .put("specId", cart.getSpecId()); // 商品规格id
        List<ShopCartExchange> cartList = shopCartExchangeDao.findByParams(paramap);
        //如果存在该商品购物车  就更新 否则就添加
        if (CollectionUtils.isNotEmpty(cartList)) {
            for (ShopCartExchange carts : cartList) {
                Integer num = 0;
                // 判断是否为立即购买
                // 若为立即购买,直接将购物车数量更改为当前购买数量
                if (saveTypeAddToCart == CartConstant.SAVE_TYPE_BUY_NOW) {
                    num = count;
                } else { //若为加入购物车,将原有商品数量加上新加入的商品数量
                    num = carts.getGoodsNum().intValue() + count;
                }
                cart.setGoodsNum(num);
                cart.setId(carts.getId());
                shopCartExchangeDao.update(cart);
            }
        } else {
            // 主播id
            cart.setGoodsNum(count);
            shopCartExchangeDao.insert(cart);
        }
        if (Optional.ofNullable(goodsSpec.getSpecIsopen()).orElse(0)==0){
            goodsSpec.setSpecGoodsStorage(0);
        }
        if (cart.getGoodsNum()> goodsSpec.getSpecGoodsStorage()) {
            throw new RuntimeException("购物车数量大于库存");
        }
        return cart.getId();
    }

    /**
     * 更新换购商品购物车
     * @param cartId 购物车id
     * @param num 规格商品数量
     * @param mmCode 会员编号
     */
    @Override
    public void updateNum(long cartId, int num, long mmCode) {
        ShopCartExchange cart = find(cartId);
        if (cart == null) {
            throw new RuntimeException("不存在购物车");
        }

        if (!cart.getMemberId().equals(mmCode)) {
            throw new RuntimeException("非法操作");
        }

        // 商品规格信息
        ShopGoodsSpec goodsSpec = goodsSpecService.find(cart.getSpecId());
        // 商品信息
        ShopGoods goods = goodsService.find(cart.getGoodsId());
        // 能否修改购物车
        canSaveOrUpdateCart(goods, goodsSpec, num);
        cart.setGoodsNum(num);

        //设置价格
        cart.setGoodsMemberPrice(goodsSpec.getSpecMemberPrice());
        cart.setGoodsBigPrice(goodsSpec.getSpecBigPrice());
        cart.setGoodsRetailPrice(goodsSpec.getSpecRetailPrice());
        cart.setBigPpv(goodsSpec.getBigPpv());
        cart.setPpv(goodsSpec.getPpv());

        cart.setId(cartId);
        shopCartExchangeDao.update(cart);
    }

    /**
     * 批量更新换购商品购物车
     * @param cartIdNumMap 购物车id以及对应数量
     * @param mmCode 会员编号
     */
    @Override
    public void updateNumBatch(Map<Long, Integer> cartIdNumMap, long mmCode) {
        // 批量查询购物车
        List<ShopCartExchangeVo> carts = shopCartExchangeDao.listShopCartExchangeVoWithGoodsAndSpecByIds((Long[]) cartIdNumMap.keySet().toArray());
        if (carts.size() != cartIdNumMap.size()) {
            throw new RuntimeException("部分购物车不存在");
        }
        for (ShopCartExchangeVo cart : carts) {
            // 用户校验
            if (!cart.getMemberId().equals(mmCode)) {
                throw new RuntimeException("非法操作");
            }
            // 商品是否存在、是否下架、规格是否变动、库存是否足够判断
            canSaveOrUpdateCart(cart.getGoods(), cart.getGoodsSpec(), cartIdNumMap.get(cart.getId()));
            // 更新数量
            ShopCartExchange updateCart = new ShopCartExchange();
            updateCart.setId(cart.getId());
            // 后台商品可能变更, 重新更新
            cart.setGoodsMemberPrice(cart.getGoodsSpec().getSpecMemberPrice());
            cart.setGoodsBigPrice(cart.getGoodsSpec().getSpecBigPrice());
            cart.setGoodsRetailPrice(cart.getGoodsSpec().getSpecRetailPrice());
            cart.setBigPpv(cart.getGoodsSpec().getBigPpv());
            cart.setPpv(cart.getGoodsSpec().getPpv());

            updateCart.setGoodsNum(cartIdNumMap.get(cart.getId()));
            shopCartExchangeDao.update(updateCart);
        }
    }

    /**
     * 提交换购商品购物车，获得选中商品价格及运费信息
     * @param cartIds 购物车id集合
     * @param mmCode 会员编号
     * @param addr 地址
     * @return
     */
    @Override
    public Map<String, Object> queryTotalPrice(String cartIds, String mmCode, RdMmAddInfo addr) {
        Map<String, Object> map = new HashMap<>();
        map.put("error", "false");
        //通过多个购物车id查询购物车数据
        List<ShopCartExchange> cartList = Lists.newArrayList();
        if (StringUtils.isNotEmpty(cartIds) && !"null".equals(cartIds)) {
            String[] cartId = cartIds.split(",");
            if (cartId != null && cartId.length > 0) {
                cartList = shopCartExchangeDao.findByParams(Paramap.create().put("ids", cartId));
            }
        }
        List<Long> goodsIds = Lists.newArrayList();
        List<Long> specIds = Lists.newArrayList();
        for (ShopCartExchange shopCart : cartList) {
            goodsIds.add(shopCart.getGoodsId());
            specIds.add(shopCart.getSpecId());
        }
        // 待验证规格
        Map<Long, ShopGoods> goodsMap = goodsService.findGoodsMap(goodsIds);
        Map<Long, ShopGoodsSpec> specList = goodsSpecService.findMapSpec(specIds);
        for (ShopCartExchange shopCart : cartList) {
            ShopGoods targetGoods = goodsMap.get(shopCart.getGoodsId());
            // 商品审核通过； 商品已上架; 商品未删除
            boolean isShow = targetGoods.getState() == GoodsState.GOODS_OPEN_STATE &&
                    targetGoods.getGoodsShow() != null &&
                    targetGoods.getGoodsShow() == GoodsState.GOODS_ON_SHOW &&
                    targetGoods.getIsDel() != null &&
                    targetGoods.getIsDel() == GoodsState.GOODS_NOT_DELETE;
            if (!isShow) {
                map.put("error", "true");
                map.put("code","10001");
                map.put("message", targetGoods.getGoodsName() + "商品已下架");
                return map;
            }
            ShopGoodsSpec targetSpec = specList.get(shopCart.getSpecId());
            shopCart.setGoodsRetailPrice(targetSpec.getSpecRetailPrice());
            shopCart.setGoodsBigPrice(targetSpec.getSpecBigPrice());
            shopCart.setGoodsMemberPrice(targetSpec.getSpecMemberPrice());
            shopCart.setPpv(targetSpec.getPpv());
            shopCart.setBigPpv(targetSpec.getBigPpv());
            shopCart.setWeight(targetSpec.getWeight());
        }
        CartExchangeInfo info = getCartExchangeInfo(cartList,addr,mmCode);
        map.put("totalGoodsPrice",info.getGoodsTotalPrice());
        map.put("freightAmount",info.getFreightAmount());
        map.put("preferentialFreightAmount",info.getPreferentialFreightAmount());
        map.put("totalAmount",info.getTotalPrice());
        return map;
    }

    @Override
    public List<Long> saveCartList(List<ShopCartExchange> cartList, String mmCode, RdRanks rdRanks) {
        List<Long> longList = new ArrayList<>();
        for (ShopCartExchange item : cartList) {
            longList.add(saveExchangeCart(item.getGoodsId(), mmCode, rdRanks.getRankId(), item.getGoodsNum(), item.getSpecId(), 1));
        }
        return longList;
    }

    public CartExchangeInfo getCartExchangeInfo(List<ShopCartExchange> cartList, RdMmAddInfo addr, String mmCode) {
        CartExchangeInfo info = new CartExchangeInfo();
        //商品种类数量
        int goodsNum = 0;
        //购物车商品总价
        double goodsTotalPrice = 0;
        //订单商品总重量
        Double totalWeight = 0d;
        //遍历所有购物车数据
        for (ShopCartExchange cart : cartList) {
            goodsNum += cart.getGoodsNum();
            totalWeight += Optional.ofNullable(cart.getWeight()).orElse(0d) * cart.getGoodsNum();
            goodsTotalPrice += (NumberUtils.getBigDecimal(String.valueOf(cart.getGoodsNum()))
                    .multiply(cart.getGoodsRetailPrice())).doubleValue();
        }
        info.setList(cartList);
        info.setBrandId(cartList.get(0).getBrandId());
        info.setBrandName(cartList.get(0).getBrandName());
        info.setGoodsNum(goodsNum);
        info.setGoodsTotalPrice(BigDecimal.valueOf(goodsTotalPrice));
        //运费计算
        if (addr != null) {
            //运费
            BigDecimal freightAmount = shopGoodsFreightService.CalculateFreight(addr.getAddProvinceCode(), totalWeight);
            //运费优惠
            BigDecimal preferentialFreightAmount = shopGoodsFreightRuleService.CalculateFreightDiscount(mmCode, info.getGoodsTotalPrice());
            if (freightAmount.compareTo(new BigDecimal("0")) == 0) {
                info.setFreightAmount(new BigDecimal("0"));
                info.setPreferentialFreightAmount(new BigDecimal("0"));
            }else{
                info.setFreightAmount(freightAmount);
                info.setPreferentialFreightAmount(preferentialFreightAmount);
            }
            //计算加上运费的订单总价
            info.setTotalPrice(info.getGoodsTotalPrice().add(info.getFreightAmount().subtract(info.getPreferentialFreightAmount())));
        } else {
            info.setFreightAmount(new BigDecimal("0"));
            info.setPreferentialFreightAmount(new BigDecimal("0"));
            info.setTotalPrice(info.getGoodsTotalPrice());
        }
        return info;
    }

    /**
     * 商品是否存在、是否下架、规格是否变动、库存是否足够判断
     */
    private void canSaveOrUpdateCart(ShopGoods goods, ShopGoodsSpec goodsSpec, int count) {
        if (goods == null) {
            throw new StateResult(AppConstants.GOODS_NOT_EXIST, "不存在商品");
        }

        if (goods.getState() != 1) {
            throw new StateResult(AppConstants.GOODS_IN_AUDIT, "商品审核未通过");
        }

        if (goods.getIsDel() == 1) {
            throw new StateResult(AppConstants.GOODS_IS_DEL, "商品不存在");
        }

        if (goods.getGoodsShow() != null && goods.getGoodsShow() == 2) {
            throw new StateResult(AppConstants.GOODS_NOT_SHOW, "商品已下架");
        }

        if (goodsSpec == null) {
            throw new StateResult(AppConstants.GOODS_SPEC_NOT_EXISTS, "商品规格变动, 请重新添加购物车");
        }

        if (goodsSpec.getSpecGoodsStorage() < count) {
            throw new StateResult(AppConstants.GOODS_NO_MORE, "库存不足");
        }
    }
}
