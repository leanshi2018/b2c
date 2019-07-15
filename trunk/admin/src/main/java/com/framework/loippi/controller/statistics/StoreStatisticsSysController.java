package com.framework.loippi.controller.statistics;

import com.framework.loippi.controller.GenericController;
import com.framework.loippi.service.order.ShopOrderService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.vo.store.StoreStatisticsVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.jiguang.common.utils.StringUtils;

/**
 * 功能： 店铺统计
 * 类名：StoreStatisticsSysController
 * 日期：2018/4/10  15:49
 * 作者：czl
 * 详细说明：
 * 修改备注:
 */
@Controller("adminStoreStatisticsSysController")
public class StoreStatisticsSysController extends GenericController {
    @Autowired
    private ShopOrderService orderService;

    @RequestMapping("admin/statistics/storeOrderVolume")
    public String listStoreOrderVolume(String startTime, String endTime, String storeKeyWord, Pageable pageable,
            ModelMap model) {
        Paramap paramap = Paramap.create().put("storeKeyWord", storeKeyWord);
        if (StringUtils.isNotEmpty(startTime)) {
            paramap.put("startTime", startTime);
        }

        if (StringUtils.isNotEmpty(endTime)) {
            paramap.put("endTime", endTime);
        }

        pageable.setParameter(paramap);
        Page<StoreStatisticsVo> page = orderService.listBestOrderVolumeStore(pageable);
        model.addAttribute("page", page);
        return "/statisc/store/best_order_volumn";
    }


}
