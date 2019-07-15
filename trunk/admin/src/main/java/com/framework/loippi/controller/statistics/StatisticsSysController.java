package com.framework.loippi.controller.statistics;

import com.framework.loippi.consts.StatsConsts;
import com.framework.loippi.controller.GenericController;
import com.framework.loippi.service.order.ShopOrderService;
import com.framework.loippi.service.product.ShopGoodsService;
import com.framework.loippi.service.wallet.ShopWalletLogService;
import com.framework.loippi.vo.stats.StatsCountVo;
import java.text.MessageFormat;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 功能： 统计模块入口
 * 类名：StatisticsIndexSysController
 * 日期：2018/4/8  13:59
 * 作者：czl
 * 详细说明：
 * 修改备注:
 * *********************
 * 销售统计管理 /statistics/{type}/index
 * - 销售量排名   /statistics/goods/salenum
 * - 购买量排名   /statistics/member/buynum
 * - 订单统计     /statistics/order/diffStateCount
 * 商家管理
 * - 商家销售统计 /statistics/store/sale
 * 会员统计
 * - 新增会员报表 /statistics/member/addCount
 * 财务统计
 * - 收入统计报表 /statistics/platform/expenditure
 * - 支出统计报表 /statistics/platform/income
 * 促销统计
 * - 秒杀        /statistics/activity/miaosha
 * - 拼团        /statistics/activity/tuangou
 * - 优惠券      /statistics/activity/coupon
 * - 促销        /statistics/activity/promotion
 * 财务流水
 * - 直播商品销售列表
 * *********************
 */
@Controller("adminStatisticsSysController")
public class StatisticsSysController extends GenericController {

    @Autowired
    private ShopOrderService orderService;
    @Autowired
    private ShopGoodsService goodsService;
    @Autowired
    private ShopWalletLogService walletLogService;

    @RequestMapping({"/admin/statistics/{module}"})
    public String toPage(@PathVariable("module") String pagename) {
        return MessageFormat.format("/statisc/{0}/main", pagename);
    }

    @RequestMapping("/admin/stats/listCount")
    @ResponseBody
    public String statsCountByTime(@RequestParam int statsType) {
        List<StatsCountVo> resultList = null;
        switch (statsType) {
            case StatsConsts.STATS_TYPE_ORDER:
                resultList = orderService.listStatsCountVo();
                break;
            case StatsConsts.STATS_TYPE_TRADE_INCOME:
                resultList = walletLogService.listStatsCountVo(StatsConsts.STATS_TYPE_TRADE_INCOME);
                break;
            case StatsConsts.STATS_TYPE_TRADE_EXPENDITURE:
                resultList = walletLogService.listStatsCountVo(StatsConsts.STATS_TYPE_TRADE_EXPENDITURE);
                break;
//            case StatsConsts.STATS_TYPE_MEMBER:
//                resultList = memberService.listStatsCountVo();
//                break;
            case StatsConsts.STATS_TYPE_GOODS:
                resultList = goodsService.listStatsCountVo();
                break;
            default:
                showErrorJson("统计类型错误");
                return json;
        }

        showSuccessJson(resultList);
        return json;
    }

}


