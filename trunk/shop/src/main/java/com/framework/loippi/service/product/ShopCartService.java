package com.framework.loippi.service.product;

import com.framework.loippi.entity.cart.ShopCart;
import com.framework.loippi.entity.order.ShopOrderDiscountType;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.user.RdMmAddInfo;

import com.framework.loippi.entity.user.RdRanks;
import com.framework.loippi.pojo.cart.CartInfo;
import com.framework.loippi.service.GenericService;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.vo.cart.ShopCartVo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SERVICE - ShopCart(购物车数据表)
 *
 * @author zijing
 * @version 2.0
 */
public interface ShopCartService extends GenericService<ShopCart, Long> {

    /********************* 业务方法 *********************/
    /**
     * 保存购物车
     *
     * @param goodsId 商品id
     * @param memberId 用户id
     * @param count 商品数量
     * @param goodSpecId 商品规格id
     * @param saveType 加入类型(加入购物车:0/立即购买:1)
     * @param activitType 优惠类型 参照CommonConstants
     * @return 购物车id
     */
    Long saveCart(Long goodsId, String memberId, Integer rankId, Integer count, Long goodSpecId, Integer saveType,
        Long activityId, Integer activitType,Long activityGoodsId,Long activityGoodsSpecId);

    /**
     * 更新购物车数量
     */
    void updateNum(long cartId, int count, long memberId);

    /**
     * 批量更新购物车数量
     */
    void updateNumBatch(Map<Long, Integer> cartIdNumMap, long memberId);

    /**
     * 计算订单最后应付金额
     *
     * @param cartIds 购物车的id
     */
    Map<String, Object> queryTotalPrice(String cartIds, String memberId, String couponIds, Long groupBuyActivityId,
                                        ShopOrderDiscountType shopOrderDiscountType,RdMmAddInfo addr);

    /**
     * 获取购物车结算类
     */
    List<CartInfo> queryCartInfoList(String cartIds,ShopOrderDiscountType shopOrderDiscountType,RdMmAddInfo addr,String memberId);

    /********************* 扩展查询 *********************/

    /**
     * 查询购物车信息+商品信息
     */
    List<ShopCartVo> listWithGoodsAndSpec(Pageable pageable);

    /********************* 其他人添加 *********************/

    /**
     * 批量加入购物车
     * @param cartList
     * @return
     */
    List<Long> saveCartList(List<ShopCart> cartList,String memberId,RdRanks rdRanks);

    Map<String, Object> queryTotalPrice1(String cartIds, String mmCode, Long couponId, Long groupBuyActivityId, ShopOrderDiscountType shopOrderDiscountType, RdMmAddInfo addr);

    List<CartInfo> queryCartInfoList1(String cartIds, ShopOrderDiscountType shopOrderDiscountType, RdMmAddInfo address, String memberId, Long couponId);

    Map<String, Object> queryTotalPrice2(String cartIds, String mmCode, Long couponId, Long groupBuyActivityId, ShopOrderDiscountType shopOrderDiscountType, RdMmAddInfo addr);

    Map<String, Object> queryTotalPriceImmediately(ShopGoods goods, Long specId, Integer count, String mmCode, Long couponId, Long groupBuyActivityId, ShopOrderDiscountType shopOrderDiscountType, RdMmAddInfo addr, Integer rankId, Long activityId
            , Integer activitType,Long activityGoodsId,Long activityGoodsSpecId);

    List<CartInfo> queryCartInfoListImmediately(ArrayList<ShopCart> shopCarts, ShopOrderDiscountType shopOrderDiscountType, RdMmAddInfo address, String mmCode, Long couponId);
}
