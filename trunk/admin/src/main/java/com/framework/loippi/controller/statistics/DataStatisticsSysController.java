package com.framework.loippi.controller.statistics;

import cn.jiguang.common.utils.StringUtils;
import com.framework.loippi.controller.GenericController;
import com.framework.loippi.service.order.ShopOrderService;
import com.framework.loippi.service.product.ShopGoodsService;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.vo.goods.GoodsStatisticsVo;
import com.framework.loippi.vo.order.OrderCountVo;
import java.util.Optional;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 功能： 商品统计
 * 类名：DataStatisticsSysController
 * 日期：2018/4/10  11:59
 * 作者：czl
 * 详细说明：
 * 修改备注:
 */
@Controller("adminDataStatisticsSysController")
public class DataStatisticsSysController extends GenericController {

    @Autowired
    private ShopOrderService orderService;
    @Autowired
    private ShopGoodsService shopGoodsService;
    @Autowired
    private RdMmBasicInfoService rdMmBasicInfoService;

    @RequestMapping("admin/statistics/data")
    public String data(ModelMap model) {
        Paramap paramMap = Paramap.create().put("notOrderState", "(10 or 0)");
        //销售额统计
        List<OrderCountVo> resultList = orderService.listGroupPriceAndNumOfOrderType(paramMap);
        //会员购买统计
        List<OrderCountVo> memberOrderNum = orderService.countMemberOrderNum(paramMap);
        // 订单总数
        double count = orderService.count();
        //所以商品
        Long goodsNum=shopGoodsService.count();
        //会员总数
        Long memberNum=rdMmBasicInfoService.count();
        //分享总数
        Integer shareNum=Optional.ofNullable(rdMmBasicInfoService.sumShare()).orElse(0);
        //未购买会员数
        Long notBuy=0L;
        //首次购买数
        Long oneBuy=0L;
        //二次以上购买会员数
        Long repeatedlyBuy=0L;
        // 订单总数
        double sum = 0;
        //第一次有订单的时间
        String orderDate="";
        DecimalFormat df   = new DecimalFormat("######0.00");
        if (CollectionUtils.isNotEmpty(resultList)) {
            for (OrderCountVo orderCountVo : resultList) {
                if (sum==0){
                    orderDate=orderCountVo.getOrderDate();
                }
                sum += Double.valueOf(orderCountVo.getOrderPrice());
            }
        }

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date strtodate=new Date();
            try {
                strtodate = formatter.parse(orderDate);
            }catch (Exception e){
                e.printStackTrace();
            }
            long diff = new Date().getTime() - strtodate.getTime();
            long day = diff / (1000 * 24 * 60 * 60);
            double averageOrderSum=sum;
            if (day!=0L){
                averageOrderSum=sum/day;
            }

        if (CollectionUtils.isNotEmpty(memberOrderNum)) {
            for (OrderCountVo orderCountVo : memberOrderNum) {
                if (Integer.parseInt(orderCountVo.getNum())==1){
                    oneBuy++;
                }else if (Integer.parseInt(orderCountVo.getNum())>1){
                    repeatedlyBuy++;
                }
            }
        }
            notBuy=memberNum-oneBuy-repeatedlyBuy;
            model.addAttribute("notBuy", notBuy);
            model.addAttribute("oneBuy", oneBuy);
            model.addAttribute("repeatedlyBuy", repeatedlyBuy);
            model.addAttribute("orderSum", sum);
            model.addAttribute("goodsNum", goodsNum);
            model.addAttribute("memberNum", memberNum);
            model.addAttribute("averageOrderSum", df.format(averageOrderSum));
            model.addAttribute("orderNum", count);
        model.addAttribute("shareNum", shareNum);

        return "/statisc/data/data";
    }
}
