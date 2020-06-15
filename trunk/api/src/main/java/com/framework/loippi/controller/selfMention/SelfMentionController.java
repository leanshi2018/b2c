package com.framework.loippi.controller.selfMention;

import com.framework.loippi.entity.ware.RdWarehouse;
import com.framework.loippi.pojo.selfMention.GoodsType;
import com.framework.loippi.pojo.selfMention.OrderInfo;
import com.framework.loippi.result.selfMention.SelfMentionShopResult;
import com.framework.loippi.service.order.ShopOrderService;
import com.framework.loippi.service.ware.RdWarehouseService;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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
import com.framework.loippi.entity.ware.RdInventoryWarning;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.result.auths.AuthsLoginResult;
import com.framework.loippi.service.product.ShopGoodsService;
import com.framework.loippi.service.product.ShopGoodsSpecService;
import com.framework.loippi.service.ware.RdInventoryWarningService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.ApiUtils;
import com.framework.loippi.utils.Paramap;
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
    private RdWarehouseService rdWarehouseService;
    @Resource
    private ShopOrderService shopOrderService;
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
        RdWarehouse rdWarehouse = rdWarehouseService.find("mmCode",mmCode);
        if(rdWarehouse==null){
            return ApiUtils.error("当前用户尚未开店");
        }
        String wareCode = rdWarehouse.getWareCode();//获取仓库号
        List<GoodsType> list=rdInventoryWarningService.findGoodsTypeByWareCode(wareCode);
        Integer goodsTypeNum=0;//商品种类数量
        if(list!=null&&list.size()>0){
            goodsTypeNum=list.size();
        }
        Integer mentionId = rdWarehouse.getMentionId();
        if(mentionId==null){
            return ApiUtils.error("自提店信息异常");
        }
        Integer dailyNum=shopOrderService.findDailyCountByMentionId(mentionId);
        Integer monthNum=shopOrderService.findMonthCountByMentionId(mentionId);
        List<OrderInfo> orderInfos=shopOrderService.findMonthOrderInfo(mentionId);
        BigDecimal total=BigDecimal.ZERO;
        if(orderInfos!=null&&orderInfos.size()>0){
            for (OrderInfo orderInfo : orderInfos) {
                total=total.add(orderInfo.getOrderAmount()).add(orderInfo.getUsePointNum()).subtract(orderInfo.getRefundAmount()).subtract(orderInfo.getRefundPoint());
            }
        }
        SelfMentionShopResult result=SelfMentionShopResult.build(goodsTypeNum,dailyNum,monthNum,total);
        return ApiUtils.success(result);
    }

    /**
     * 商品列表
     */
    @RequestMapping(value = "/api/mention/goodsList", method = RequestMethod.POST)
    @ResponseBody
    public String goodsList(HttpServletRequest request, String wareCode, Pageable pager) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
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

        for (RdInventoryWarning inventoryWarning : goodsPage.getContent()) {
            MentionWareGoodsVo wareGoodsVo = new MentionWareGoodsVo();
            wareGoodsVo.setWareCode(Optional.ofNullable(inventoryWarning.getWareCode()).orElse(""));
            ShopGoods shopGoods = shopGoodsService.find(Long.valueOf(inventoryWarning.getGoodsCode()));
            wareGoodsVo.setGoodsName(Optional.ofNullable(shopGoods.getGoodsName()).orElse(""));
            wareGoodsVo.setGoodsImage(Optional.ofNullable(shopGoods.getGoodsImage()).orElse(""));
            wareGoodsVo.setSpecGoodsSpec(Optional.ofNullable(shopGoods.getGoodsName()).orElse(""));
            wareGoodsVo.setGoodsRetailPrice(Optional.ofNullable(shopGoods.getGoodsRetailPrice()).orElse(BigDecimal.ZERO));
            wareGoodsVo.setGoodsMemberPrice(Optional.ofNullable(shopGoods.getGoodsMemberPrice()).orElse(BigDecimal.ZERO));
            wareGoodsVo.setPpv(Optional.ofNullable(shopGoods.getPpv()).orElse(BigDecimal.ZERO));
            wareGoodsVo.setInventory(Optional.ofNullable(inventoryWarning.getInventory()).orElse(0));
            //wareGoodsVo.setSales();
            //wareGoodsVo.setProductInventory();
        }

        return ApiUtils.success(list);
        //return ApiUtils.success();


    }

    @RequestMapping(value = "/api/mention/orders.json")
    @ResponseBody
    public String getOrders(HttpServletRequest request,Integer orderState) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if(member==null){
            return ApiUtils.error("当前用户尚未登录");
        }
        String mmCode = member.getMmCode();//店主会员编号
        RdWarehouse rdWarehouse = rdWarehouseService.find("mmCode",mmCode);
        if(rdWarehouse==null){
            return ApiUtils.error("当前用户尚未开店");
        }
        return ApiUtils.success();
    }
}
