package com.framework.loippi.service.impl.product;

import com.framework.loippi.consts.Constants;
import com.framework.loippi.consts.OrderState;
import com.framework.loippi.controller.StateResult;
import com.framework.loippi.dao.order.ShopOrderDao;
import com.framework.loippi.dao.order.ShopOrderGoodsDao;
import com.framework.loippi.dao.product.ShopGoodsDao;
import com.framework.loippi.dao.product.ShopGoodsEvaluateDao;
import com.framework.loippi.dao.product.ShopGoodsEvaluateLikeDao;
import com.framework.loippi.entity.order.ShopOrder;
import com.framework.loippi.entity.order.ShopOrderGoods;
import com.framework.loippi.entity.product.*;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.mybatis.paginator.domain.PageList;
import com.framework.loippi.service.TUserSettingService;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.order.ShopOrderGoodsService;
import com.framework.loippi.service.product.ShopGoodsEvaluateSensitivityService;
import com.framework.loippi.service.product.ShopGoodsEvaluateService;
import com.framework.loippi.service.product.ShopGoodsService;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.service.user.RdMmRelationService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.JacksonUtil;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.StringUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * SERVICE - ShopGoodsEvaluate(信誉商品评价表)
 *
 * @author zijing
 * @version 2.0
 */
@Service
public class ShopGoodsEvaluateServiceImpl extends GenericServiceImpl<ShopGoodsEvaluate, Long>
    implements ShopGoodsEvaluateService {

    @Autowired
    private ShopGoodsService shopGoodsService;
    @Autowired
    private ShopGoodsEvaluateDao shopGoodsEvaluateDao;
    @Autowired
    private ShopOrderGoodsDao shopOrderGoodsDao;
    @Autowired
    private ShopGoodsDao shopGoodsDao;
    @Autowired
    private ShopOrderDao shopOrderDao;
    @Autowired
    private ShopGoodsEvaluateLikeDao shopGoodsEvaluateLikeDao;
    @Autowired
    private TUserSettingService tUserSettingService;
    @Autowired
    private ShopGoodsEvaluateSensitivityService evaluateSensitivityService;
    @Autowired
    private TwiterIdService twiterIdService;
    @Autowired
    private RdMmBasicInfoService rdMmBasicInfoService;

    @Autowired
    public void setGenericDao() {
        super.setGenericDao(shopGoodsEvaluateDao);
    }

    @Transactional
    @Override
    public String saveEvaluate(ShopGoodsEvaluate evaluateGoods) {
        ShopOrder order = shopOrderDao.find(evaluateGoods.getGevalOrderid());
        if (order == null || order.getOrderState() != OrderState.ORDER_STATE_FINISH) {
            throw new IllegalStateException("订单状态不允许评价");
        }
        // 敏感词过滤
        evaluateSensitivityService.filterWords(evaluateGoods.getGevalContent());
        // 修改订单项评价状态
        ShopOrderGoods orderGoods = new ShopOrderGoods();
        orderGoods.setEvaluationStatus(1);
        orderGoods.setId(evaluateGoods.getGevalOrdergoodsid());
        shopOrderGoodsDao.update(orderGoods);
        //修改商品评价次数
        ShopGoods goods = shopGoodsDao.find(orderGoods.getGoodsId());
        double evaluateRate = goods.getEvaluaterate();
        if (evaluateGoods.getGevalScores() < 3) {
            evaluateRate = (goods.getEvaluaterate() * goods.getCommentnum()) / (goods.getCommentnum() + 1);
        } else {
            evaluateRate = (goods.getEvaluaterate() * goods.getCommentnum() + 1) / (goods.getCommentnum() + 1);
        }
        goods.setCommentnum(goods.getCommentnum() + 1);
        BigDecimal b = new BigDecimal(evaluateRate);
        goods.setEvaluaterate(b.setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
        shopGoodsDao.updateGoodsCommentNum(goods);
        shopGoodsEvaluateDao.insert(evaluateGoods);
        Paramap paramap = Paramap.create().put("orderId", evaluateGoods.getGevalOrderid()).put("evaluationStatus", 0);
        Long countNotEvaluate = shopOrderGoodsDao.count(paramap);
        if (countNotEvaluate != null && countNotEvaluate.intValue() == 0) {
            ShopOrder shopOrder = new ShopOrder();
            shopOrder.setId(evaluateGoods.getGevalOrderid());
            shopOrder.setEvaluationStatus(1);
            shopOrderDao.update(shopOrder);
        }
        String set = tUserSettingService.read("evaluate_setting") + "";
        ShopGoodsEvaluateSetting setting = JacksonUtil.fromJson(set, ShopGoodsEvaluateSetting.class);
        String resultStr =
            StringUtil.isEmpty(setting.getSuccessTipWolds()) ? "" : setting.getSuccessTipWolds(); //评价完客户端显示文字
        return resultStr;
    }

    @Override
    public String saveBatchEvaluate(String evaluateJson, RdMmBasicInfo member) {
        // TODO 字数限制
        // TODO 发表成功后提示文字
        // TODO 好评返现功能
        String resultStr = "";
        Integer index = 0;
        List<ShopGoodsEvaluate> shopOrderGoodsList = com.alibaba.fastjson.JSONArray
            .parseArray(evaluateJson, ShopGoodsEvaluate.class);
        for (ShopGoodsEvaluate item : shopOrderGoodsList) {

            ShopGoodsEvaluate evaluateGoods = new ShopGoodsEvaluate();
            //根据商品id获得商品信息
            ShopOrderGoods orderGoods = shopOrderGoodsDao.find(item.getGevalOrdergoodsid());
            if (orderGoods == null) {
                throw new StateResult(5110312, "您未购买过该商品");

            }
            // 已评价
            if (orderGoods.getEvaluationStatus() != null && orderGoods.getEvaluationStatus() == 1) {
                throw new StateResult(5110313, "已评价");

            }

            if (!orderGoods.getBuyerId().equals(member.getMmCode() + "")) {
                throw new StateResult(5110314, "不是您购买的商品,不能评价");
            }

            evaluateGoods.setId(twiterIdService.getTwiterId());
            evaluateGoods.setGevalOrderid(orderGoods.getOrderId());
            // 图片
            evaluateGoods.setGevalImage(item.getGevalImage());
            //保存商品id
            evaluateGoods.setGevalGoodsid(orderGoods.getGoodsId());
            //评价分数
            evaluateGoods.setGevalScores(item.getGevalScores());
            //商品名称
            evaluateGoods.setGevalGoodsname(orderGoods.getGoodsName());
            //评价内容
            evaluateGoods.setGevalContent(item.getGevalContent());
            //评价者名称
            evaluateGoods.setGevalFrommembername(member.getMmNickName());
            //评价者id
            evaluateGoods.setGevalFrommemberid(Long.parseLong(member.getMmCode()));
            //0表示不是 1表示是匿名评价
//        evaluateGoods.setGevalIsanonymous(evaluateGoodsParam.getIsAnonymous());
            //评价信息的状态 0为正常 1为禁止显示
            evaluateGoods.setGevalState(0);
            //手机号码
            evaluateGoods.setMobile(member.getMobile());
            //订单编号
            evaluateGoods.setGevalOrderno(orderGoods.getOrderId().toString());
            //ip
            evaluateGoods.setIp(member.getUpdateIp());
            //订单商品表编号
            evaluateGoods.setGevalOrdergoodsid(orderGoods.getId());
            //规格描述
            evaluateGoods.setSpecInfo(orderGoods.getSpecInfo());
            //评价时间
            evaluateGoods.setCreateTime(new Date());
            //保存评价信息
            evaluateGoods.setParentId(0L);
            //浏览次数
            evaluateGoods.setSumView(0L);
            ShopOrder order = shopOrderDao.find(evaluateGoods.getGevalOrderid());
            if (order == null || order.getOrderState() != OrderState.ORDER_STATE_FINISH) {
                throw new IllegalStateException("订单状态不允许评价");
            }
            // 敏感词过滤
            evaluateSensitivityService.filterWords(evaluateGoods.getGevalContent());
            // 修改订单项评价状态
//        ShopOrderGoods orderGoods = new ShopOrderGoods();
            orderGoods.setEvaluationStatus(1);
//        orderGoods.setId(evaluateGoods.getGevalOrdergoodsid());
            shopOrderGoodsDao.update(orderGoods);
            //修改商品评价次数
            ShopGoods goods = shopGoodsDao.find(orderGoods.getGoodsId());
            double evaluateRate = Optional.ofNullable(goods.getEvaluaterate()).orElse(0D);
            if (item.getGevalScores() < 3) {
                evaluateRate = (Optional.ofNullable(goods.getEvaluaterate()).orElse(0D) * Optional
                    .ofNullable(goods.getCommentnum()).orElse(0)) / (
                    Optional.ofNullable(goods.getCommentnum()).orElse(0) + 1);
            } else {
                evaluateRate = (Optional.ofNullable(goods.getEvaluaterate()).orElse(0D) * Optional
                    .ofNullable(goods.getCommentnum()).orElse(0) + 1) / (
                    Optional.ofNullable(goods.getCommentnum()).orElse(0) + 1);
            }
            goods.setCommentnum(Optional.ofNullable(goods.getCommentnum()).orElse(0) + 1);
            BigDecimal b = new BigDecimal(evaluateRate);
            goods.setEvaluaterate(b.setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
            shopGoodsDao.updateGoodsCommentNum(goods);
            shopGoodsEvaluateDao.insert(evaluateGoods);
            //最后一次循环在判断是否全部评价完
            if (index == shopOrderGoodsList.size() - 1) {
                Paramap paramap = Paramap.create().put("orderId", evaluateGoods.getGevalOrderid())
                    .put("evaluationStatus", 0);
                Long countNotEvaluate = shopOrderGoodsDao.count(paramap);
                if (countNotEvaluate != null && countNotEvaluate.intValue() == 0) {
                    ShopOrder shopOrder = new ShopOrder();
                    shopOrder.setId(evaluateGoods.getGevalOrderid());
                    shopOrder.setEvaluationStatus(1);
                    shopOrderDao.update(shopOrder);
                }
            }

            String set = tUserSettingService.read("evaluate_setting") + "";
            ShopGoodsEvaluateSetting setting = JacksonUtil.fromJson(set, ShopGoodsEvaluateSetting.class);
            resultStr +=
                StringUtil.isEmpty(setting.getSuccessTipWolds()) ? "" : setting.getSuccessTipWolds(); //评价完客户端显示文字
            index++;
        }
        return resultStr;
    }

    @Override
    public Page findWithExtends(Pageable pager, Long memberId) {
        PageList<ShopGoodsEvaluate> result = shopGoodsEvaluateDao
            .findWithReplyNumAndLikeCount(pager.getParameter(), pager.getPageBounds());
        result.forEach(item -> item.setAdditionalEval(shopGoodsEvaluateDao.findByParentId(item.getId())));
        return new Page(result, result.getPaginator().getTotalCount(), pager);
    }

    @Override
    public List<ShopGoodsEvaluate> findListWithExtends(Paramap paramap) {
        Pageable pageable = new Pageable(1, 100);
        pageable.setOrderDirection(Order.Direction.DESC);
        pageable.setOrderProperty("create_time");
        pageable.setParameter(paramap);
        PageList<ShopGoodsEvaluate> result = shopGoodsEvaluateDao
            .findWithReplyNumAndLikeCount(pageable.getParameter(), pageable.getPageBounds());
        return CollectionUtils.isEmpty(result) ? Lists.newArrayList() : result;
    }

    /**
     * 管理后台分页列表
     */
    @Override
    public Page<ShopGoodsEvaluate> findWithGoodsByPage(Pageable pageable, String prex) {
        PageList<ShopGoodsEvaluate> shopGoodsEvaluatePage = shopGoodsEvaluateDao
            .findByPage(pageable.getParameter(), pageable.getPageBounds());
        List<Long> longList = new ArrayList<>();
        List<Long> memberList = new ArrayList<>();
        for (ShopGoodsEvaluate item : shopGoodsEvaluatePage) {
            longList.add(item.getGevalGoodsid());
            memberList.add(item.getGevalFrommemberid());
        }
        Map<Long, ShopGoods> shopGoodsMap = shopGoodsService.findGoodsMap(longList);
        Map<String, RdMmBasicInfo> shopMemberMap = new HashMap<>();
        if (memberList.size() > 0) {
            List<RdMmBasicInfo> rdMmBasicInfoList = rdMmBasicInfoService
                .findList(Paramap.create().put("mmCodes", memberList));
            for (RdMmBasicInfo item : rdMmBasicInfoList) {
                shopMemberMap.put(item.getMmCode(), item);
            }
        }

        for (ShopGoodsEvaluate item : shopGoodsEvaluatePage) {
            ShopGoods shopGoods = shopGoodsMap.get(item.getGevalGoodsid());
            if (shopGoods != null) {
                item.setGoodsImage(shopGoods.getGoodsImage());
                item.setGevalGoodsname(shopGoods.getGoodsName());
            }

            RdMmBasicInfo shopMember = shopMemberMap.get(item.getGevalFrommemberid() + "");
            if (shopMember != null) {
                String avatar = shopMember.getMmAvatar();
                if (StringUtil.isEmpty(avatar)) {
                    avatar = prex + "/resources/ygres/img/default_avatar.png";
                }
                item.setMobile(shopMember.getMobile());
                item.setNick(shopMember.getMmNickName());
                item.setGevalFrommemberAvatar(avatar);
            } else {
                item.setGevalFrommemberAvatar(prex + "/resources/ygres/img/default_avatar.png");
            }
        }
        Page<ShopGoodsEvaluate> page = new Page(shopGoodsEvaluatePage,
            shopGoodsEvaluatePage.getPaginator().getTotalCount(), pageable);
        return page;
    }

    //统计商品评论积分
    @Override
    public Long findScore(Map<String, Object> params) {
        return shopGoodsEvaluateDao.findScore(params);
    }

    @Override
    public void saveEvaluateLike(Long evalId, Long memberId) {
        ShopGoodsEvaluateLike goodsLike = new ShopGoodsEvaluateLike();
        goodsLike.setId(twiterIdService.getTwiterId());
        goodsLike.setGevalId(evalId);
        goodsLike.setMemberId(memberId);
        goodsLike.setLikeType(1);
        shopGoodsEvaluateLikeDao.insert(goodsLike);
        //评论数增加
        shopGoodsEvaluateDao.addLikeNum(evalId, 1);
    }

    @Override
    public Map<String, String> countCommentRate(List<Long> goodsList, Integer score) {
        if (goodsList.size() == 0) {
            return new HashMap<>();
        }
        List<ShopGoodsEvaluate> shopGoodsEvaluateList = shopGoodsEvaluateDao
            .countCommentRate(Paramap.create().put("ids", goodsList).put("score", score));
        Map<String, String> ShopGoodsEvaluateMap = new HashMap<>();
        if (shopGoodsEvaluateList != null && shopGoodsEvaluateList.size() > 0) {
            for (ShopGoodsEvaluate item : shopGoodsEvaluateList) {
                if (item != null) {
                    ShopGoodsEvaluateMap.put(item.getGevalGoodsid() + "", item.getGevalContent());
                }
            }
        }

        return ShopGoodsEvaluateMap;
    }

    @Override
    public List<ShopGoodsEvaluateKeywords> countGevalContent(List<String> gevalContents, Long goodsId) {
        List<ShopGoodsEvaluateKeywords> shopGoodsEvaluateKeywordsList = shopGoodsEvaluateDao
            .countGevalContent(Paramap.create().put("gevalContents", gevalContents).put("gevalGoodsid", goodsId).put("gevalState",0));
        return shopGoodsEvaluateKeywordsList;
    }
}
