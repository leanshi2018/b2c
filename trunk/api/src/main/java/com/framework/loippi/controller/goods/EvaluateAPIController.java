package com.framework.loippi.controller.goods;

import com.framework.loippi.consts.Constants;
import com.framework.loippi.controller.BaseController;
import com.framework.loippi.entity.order.ShopOrder;
import com.framework.loippi.entity.order.ShopOrderGoods;
import com.framework.loippi.entity.product.ShopGoodsEvaluate;
import com.framework.loippi.entity.product.ShopGoodsEvaluateKeywords;
import com.framework.loippi.param.evaluate.EvaluateGoodsParam;
import com.framework.loippi.result.auths.AuthsLoginResult;
import com.framework.loippi.result.evaluate.EvaluateGoodsResult;
import com.framework.loippi.service.order.ShopOrderGoodsService;
import com.framework.loippi.service.order.ShopOrderService;
import com.framework.loippi.service.product.ShopGoodsEvaluateKeywordsService;
import com.framework.loippi.service.product.ShopGoodsEvaluateLikeService;
import com.framework.loippi.service.product.ShopGoodsEvaluateService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.ApiUtils;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.StringUtil;
import com.framework.loippi.utils.Xerror;
import com.framework.loippi.utils.validator.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;


/**
 * API - 评价模块接口
 *
 * @author CZL
 * @version 1.0
 */

@Controller("apiEvaluateController")
@Slf4j
public class EvaluateAPIController extends BaseController {

    @Resource
    private ShopOrderGoodsService shopOrderGoodsService;
    @Resource
    private ShopGoodsEvaluateService shopGoodsEvaluateService;
    @Resource
    private ShopGoodsEvaluateLikeService shopGoodsLikeService;
    @Resource
    private ShopOrderService shopOrderService;
    @Resource
    private ShopGoodsEvaluateKeywordsService shopGoodsEvaluateKeywordsService;

    /**
     * 商品详情评价列表
     *
     * @param goodsId
     * @param pageNumber
     * @param request
     * @param keywords
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/api/goods/evaluate.json", method = RequestMethod.POST)
    public String goodsRate(@RequestParam(required = true) Long goodsId,
                            @RequestParam(required = false, defaultValue = "") String keywords,
                            @RequestParam(required = false, value = "pageNumber", defaultValue = "1") Integer pageNumber,
                            HttpServletRequest request) {
        Pageable pager = new Pageable();
        Page<ShopGoodsEvaluate> result;
        pager.setPageNumber(pageNumber);
        pager.setPageSize(20);
        Paramap paramap = Paramap.create();
        paramap.put("gevalState", 0); // 0正常显示 1禁止显示 gevalState
        paramap.put("isDel", 0);
        paramap.put("parentId", 0);
        paramap.put("gevalGoodsid", goodsId);
        if (!StringUtil.isEmpty(keywords)){
            paramap.put("gevalContent", keywords);
        }
        pager.setParameter(paramap);
        result = shopGoodsEvaluateService.findWithGoodsByPage(pager, request.getContextPath());
        Map<String, Object> build = new HashMap<>();
        if (result != null) {
            build.put("evaluateList", EvaluateGoodsResult.build(result.getContent()));

        } else {
            build.put("evaluateList", new ArrayList<>());
        }
        build.put("evaluateCount", shopGoodsEvaluateService.count(Paramap.create().put("gevalGoodsid",goodsId).put("gevalState",0)));
        List<ShopGoodsEvaluateKeywords> shopGoodsEvaluateKeywordsList=shopGoodsEvaluateKeywordsService.findAll();
       List<String> stringList=new ArrayList<>();
        List<ShopGoodsEvaluateKeywords> shopGoodsEvaluateKeywordsList1=new ArrayList<>();
        List<ShopGoodsEvaluateKeywords> shopGoodsEvaluateKeywordsResult=new ArrayList<>();
        for (ShopGoodsEvaluateKeywords item:shopGoodsEvaluateKeywordsList) {
            stringList.add(item.getKeywords());
        }
        if (stringList.size()>0){
            shopGoodsEvaluateKeywordsList1=shopGoodsEvaluateService.countGevalContent(stringList,goodsId);
            for (ShopGoodsEvaluateKeywords item:shopGoodsEvaluateKeywordsList1) {
                if (item.getCountNum()!=0){
                    shopGoodsEvaluateKeywordsResult.add(item);
                }
            }
            build.put("shopGoodsEvaluateKeywords",shopGoodsEvaluateKeywordsResult);
        }
        return ApiUtils.success(build);
    }

    /**
     * 评价商品
     */
    @RequestMapping("/api/evaluate/saveEvaluate.json")
    @ResponseBody
    public String saveEvaluateGoods(@Valid EvaluateGoodsParam evaluateGoodsParam, BindingResult vResult,
                                    HttpServletRequest request) {
        if (vResult.hasErrors()) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        // TODO 字数限制
        // TODO 发表成功后提示文字
        // TODO 好评返现功能
        ShopGoodsEvaluate evaluateGoods = new ShopGoodsEvaluate();
        //根据商品id获得商品信息
        ShopOrderGoods orderGoods = shopOrderGoodsService.find(evaluateGoodsParam.getOrderGoodsId());
        if (orderGoods == null) {
            return ApiUtils.error("您未购买过该商品");
        }
        // 已评价
        if (orderGoods.getEvaluationStatus() != null && orderGoods.getEvaluationStatus() == 1) {
            return ApiUtils.error("已评价");
        }

        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if (!orderGoods.getBuyerId().equals(Long.parseLong(member.getMmCode()))) {
            return ApiUtils.error("不是您购买的商品,不能评价");
        }

        evaluateGoods.setId(twiterIdService.getTwiterId());
        evaluateGoods.setGevalOrderid(orderGoods.getOrderId());
        // 图片
        evaluateGoods.setGevalImage(evaluateGoodsParam.getImg());
        //保存商品id
        evaluateGoods.setGevalGoodsid(orderGoods.getGoodsId());
        //评价分数
        evaluateGoods.setGevalScores(evaluateGoodsParam.getGoodsScore());
        //商品名称
        evaluateGoods.setGevalGoodsname(orderGoods.getGoodsName());
        //评价内容
        evaluateGoods.setGevalContent(evaluateGoodsParam.getComment());
        //评价者名称
        evaluateGoods.setGevalFrommembername(member.getNickname());
        //评价者id
        evaluateGoods.setGevalFrommemberid(Long.parseLong(member.getMmCode()));
        //0表示不是 1表示是匿名评价
        evaluateGoods.setGevalIsanonymous(evaluateGoodsParam.getIsAnonymous());
        //评价信息的状态 0为正常 1为禁止显示
        evaluateGoods.setGevalState(0);
        //订单编号
        evaluateGoods.setGevalOrderno(orderGoods.getOrderId().toString());
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
        String successTipWolds = shopGoodsEvaluateService.saveEvaluate(evaluateGoods);
        return ApiUtils.success(successTipWolds);
    }

    /**
     * 评论回复
     *
     * @param evalGoodsId
     * @param content
     * @param request
     * @return
     */
    @RequestMapping("/api/evaluate/saveReplyEvaluate.json")
    @ResponseBody
    public String saveReplyEvaluate(Long evalGoodsId, String content, String img,
                                    @RequestParam(defaultValue = "1") Integer isAnonymous, HttpServletRequest request) {
        try {
            if (evalGoodsId == null || StringUtils.isBlank(content)) {
                return ApiUtils.error(Xerror.PARAM_INVALID);
            }

            ShopGoodsEvaluate evaluateGoods = shopGoodsEvaluateService.find(evalGoodsId);
            if (evaluateGoods == null) {
                return ApiUtils.error(Xerror.PARAM_INVALID);
            }

            AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
            ShopGoodsEvaluate eval = new ShopGoodsEvaluate();
            eval.setId(twiterIdService.getTwiterId());
            eval.setParentId(evaluateGoods.getId());
            eval.setGevalFrommemberid(Long.parseLong(member.getMmCode()));
            eval.setGevalFrommembername(member.getAccount());
            eval.setCreateTime(new Date());
            eval.setGevalGoodsid(evaluateGoods.getGevalGoodsid());
            eval.setGevalContent(content);
            evaluateGoods.setGevalIsanonymous(isAnonymous);
            // 以下不能为空
            ShopOrder shopOrder = shopOrderService.find(evaluateGoods.getGevalOrderid());
            eval.setGevalState(0);
            eval.setGevalScores(0);
            eval.setGevalGoodsname(evaluateGoods.getGevalGoodsname());
            eval.setGevalOrdergoodsid(evaluateGoods.getGevalOrdergoodsid());
            eval.setGevalOrderno(evaluateGoods.getGevalOrderno());
            eval.setGevalOrderid(evaluateGoods.getGevalOrderid());
            eval.setGevalImage(img);
            shopGoodsEvaluateService.save(eval);
            return ApiUtils.success();
        } catch (Exception e) {
            log.error("用户回复出错", e);
            return ApiUtils.error();
        }
    }

    /**
     * 其他用户回复评价列表
     */
    @RequestMapping("/api/evaluate/replyEvaluateList.json")
    @ResponseBody
    public String replyEvaluateList(@RequestParam(required = true) Long evalGoodsId,
                                    @RequestParam(required = false, value = "pageNumber", defaultValue = "1") Integer pageNumber) {
        try {
            if (evalGoodsId == null) {
                return ApiUtils.error(Xerror.PARAM_INVALID);
            }
            //更新浏览数
            ShopGoodsEvaluate shopGoodsEvaluate = shopGoodsEvaluateService.find(evalGoodsId);
            if (shopGoodsEvaluate != null) {
                if (shopGoodsEvaluate.getSumView() == null) {
                    shopGoodsEvaluate.setSumView(0L);
                }
                shopGoodsEvaluate.setSumView(shopGoodsEvaluate.getSumView() + 1);
                shopGoodsEvaluateService.update(shopGoodsEvaluate);
            }
            Pageable pager = new Pageable();
            pager.setPageNumber(pageNumber);
            pager.setPageSize(20);
            ShopGoodsEvaluate condition = new ShopGoodsEvaluate();
            condition.setParentId(evalGoodsId);
            condition.setIsDel(0);
            condition.setGevalOrdergoodsid(null); // 回复内容没有订单商品id--区分是否追评和回复
            pager.setParameter(condition);
            Page<ShopGoodsEvaluate> withExtends = shopGoodsEvaluateService.findWithExtends(pager, null);
            List<ShopGoodsEvaluate> evaluateGoodsList = new ArrayList<>();
            evaluateGoodsList = withExtends.getContent();
            List<Map<String, Object>> result = new ArrayList<>();
            evaluateGoodsList.forEach(item -> {
                Map<String, Object> map = new HashMap<>();
                map.put("isLiked", item.getIsLiked() == null ? 1 : 0);
                map.put("accessed", "浏览10次");
                map.put("evalGoodsId", item.getId());
                map.put("userName", item.getGevalFrommembername());
                map.put("createTime", DateUtils.formatLongToStr(item.getCreateTime().getTime(), "MM月dd日"));
                map.put("likeCount", item.getLikeNum() == null ? 0 : item.getLikeNum());
                map.put("content", item.getGevalContent());
                map.put("evalId", item.getId());
                if (StringUtils.isNotBlank(item.getGevalFrommemberAvatar())) {
                    map.put("avatar", item.getGevalFrommemberAvatar());
                } else {
                    map.put("avatar", "");
                }
                result.add(map);
            });

            return ApiUtils.success(result);
        } catch (Exception e) {
            log.error("其他用户回复评价列表失败", e);
            return ApiUtils.error();
        }
    }


    /**
     * 评论点赞
     *
     * @param evalGoodsId 评论商品id
     * @param request
     * @return
     */
    @RequestMapping("/api/evaluate/clickALike.json")
    @ResponseBody
    public String clickALike(Long evalGoodsId, HttpServletRequest request) {
        if (evalGoodsId == null) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        Paramap paramap = Paramap.create().put("gevalId", evalGoodsId)
                .put("memberId", Long.parseLong(member.getMmCode()));
        long evaluateGoodsLike = shopGoodsLikeService.count(paramap);
        if (evaluateGoodsLike > 0) {
            return ApiUtils.error("已点赞 不能重复");
        }
        shopGoodsEvaluateService.saveEvaluateLike(evalGoodsId, Long.parseLong(member.getMmCode()));
        return ApiUtils.success();
    }

    //根据评价id获取评价
    @ResponseBody
    @RequestMapping(value = "/api/goods/evaluateDetail.json", method = RequestMethod.POST)
    public String goodsRate(@RequestParam(required = true) Long evaluateId, HttpServletRequest request) {
        ShopGoodsEvaluate shopGoodsEvaluate = shopGoodsEvaluateService.find(evaluateId);
        return ApiUtils.success(EvaluateGoodsResult.of(shopGoodsEvaluate));
    }

}

