package com.framework.loippi.controller.statistics;

import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.product.ShopGoodsClass;
import com.framework.loippi.service.order.ShopOrderService;
import com.framework.loippi.service.product.ShopGoodsClassService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.vo.goods.GoodsStatisticsVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.jiguang.common.utils.StringUtils;

import java.util.List;

/**
 * 功能： 商品统计
 * 类名：GoodsStatisticsSysController
 * 日期：2018/4/10  11:59
 * 作者：czl
 * 详细说明：
 * 修改备注:
 */
@Controller("adminGoodsStatisticsSysController")
public class GoodsStatisticsSysController extends GenericController {

    @Autowired
    private ShopOrderService orderService;
    @Autowired
    private ShopGoodsClassService shopGoodsClassService;

    @RequestMapping("admin/statistics/goodsSaleNum")
    public String listGoodsSaleNum( Pageable pageable,
                                   ModelMap model,GoodsStatisticsVo goodsStatisticsVo) {
        pageable.setPageSize(10);
        pageable.setParameter(goodsStatisticsVo);
        Page<GoodsStatisticsVo> page = orderService.listBestSellGoods(pageable);
        List<ShopGoodsClass> shopGoodsClassList=shopGoodsClassService.findAll();
        model.addAttribute("page", page);
        model.addAttribute("goodsStatisticsVo", goodsStatisticsVo);
        model.addAttribute("shopGoodsClassList", shopGoodsClassList);
        return "/statisc/goods/best_sell_goods";
    }
}
