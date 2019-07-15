package com.framework.loippi.controller.statistics;

import com.framework.loippi.controller.GenericController;
import com.framework.loippi.service.order.ShopOrderService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.vo.order.OrderMemberStatisticsVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.jiguang.common.utils.StringUtils;

/**
 * 功能： 会员统计
 * 类名：MemberStatisticsSysController
 * 日期：2018/4/9  15:11
 * 作者：czl
 * 详细说明：
 * 修改备注:
 */
@Controller("adminMemberStatisticsSysController")
public class MemberStatisticsSysController extends GenericController {

    @Autowired
    private ShopOrderService orderService;

    @RequestMapping("admin/statistics/memberBuyNum")
    public String listMemberBuyNum(String startTime, String endTime, String nickName,String memberId, Pageable pageable,
                                   ModelMap model) {
        Paramap paramap = Paramap.create().put("nickName", nickName).put("memberId",memberId);
        if (StringUtils.isNotEmpty(startTime)) {
            paramap.put("startTime", startTime);
        }

        if (StringUtils.isNotEmpty(endTime)) {
            paramap.put("endTime", endTime);
        }
        pageable.setPageSize(10);
        pageable.setParameter(paramap);
        Page<OrderMemberStatisticsVo> page = orderService.topPurchase(pageable);
        model.addAttribute("page", page);
        return "/statisc/member/chart_buy_num";
    }
}
