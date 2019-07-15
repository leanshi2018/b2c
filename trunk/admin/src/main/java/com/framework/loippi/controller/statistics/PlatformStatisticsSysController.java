package com.framework.loippi.controller.statistics;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.user.ShopMemberPaymentTally;
import com.framework.loippi.entity.walet.ShopWalletLog;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.service.trade.ShopMemberPaymentTallyService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.vo.activity.ActivityStatisticsVo;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 功能： 平台收入支出统计
 * 类名：PlatformStatisticsSysController
 * 日期：2018/4/10  18:33
 * 作者：zhuosr
 * 作者：czl
 * 详细说明：
 * 修改备注:
 */
@Controller("adminPlatformStatisticsSysController ")
public class PlatformStatisticsSysController extends GenericController {

    @Resource
    private ShopMemberPaymentTallyService paymentTallyService;

    /**
     * 收入统计-列表
     */
    @RequestMapping(value = {"admin/platform/listIncome"})
    public String chartIncome(@RequestParam(defaultValue = "1") Integer pageNo, Model model,
            ShopMemberPaymentTally param) {
        Pageable pageable = new Pageable(pageNo, 20);
        pageable.setParameter(param);
        pageable.setOrderProperty("create_time");
        pageable.setOrderDirection(Order.Direction.DESC);
        param.setPaymentState(1);
        param.setTradeType(10);
        Page page = paymentTallyService.findByPage(pageable);
        model.addAttribute("page", page);
//        model.addAttribute("countAmount", paymentTallyService.countAmount());
        model.addAttribute("state", 10);
        return "/statisc/platform/main";
    }

    /**
     * 收入统计-柱形图
     */
    @RequestMapping("/admin/platform/chartIncome")
    @ResponseBody
    public Map <String, Object> ajaxIncomeStatistics(HttpServletRequest request, Model model) throws ParseException {
        String begin = request.getParameter("beforeDate");
        String state = request.getParameter("state");
        Date afterDate = new Date();//截止时间
        Calendar c = Calendar.getInstance();
        if ("4".equals(state)) {
            //查询月报数据的起始月份
            c.add(Calendar.YEAR, -1);
            c.add(Calendar.MONTH, 1);
        } else {
            c.add(Calendar.MONTH, -1);
            c.add(Calendar.DAY_OF_MONTH, 1);
        }
        Date beforeDate = c.getTime();//开始时间
        if (StringUtils.isNotBlank(begin)) {
            beforeDate = new SimpleDateFormat("yyyy-MM-dd").parse(begin);
        }
        ActivityStatisticsVo param = new ActivityStatisticsVo();
        param.setBeforeDate(beforeDate);
        param.setAfterDate(afterDate);
        List <ActivityStatisticsVo> income = new ArrayList <>();//收入
        if ("4".equals(state)) {
            //查询月报数据
            param.setState("3");
        } else if ("3".equals(state)) {
            //查询周报
            param.setState("2");
        } else {
            //查询日报
            param.setState("1");
        }
        income = paymentTallyService.statisticsIncomesBystate(param);
        Map <Integer, ActivityStatisticsVo> incomeMap = Maps.newLinkedHashMap();
        if ("4".equals(state)) {
            //月报数据
            getMap(income, incomeMap, state);
        } else if ("3".equals(state)) {
            //周报数据
            getMap(income, incomeMap, state);
        } else {
            //日报数据
            getMap(income, incomeMap, state);
        }
        Map <String, Object> result = Maps.newLinkedHashMap();
        List <Integer> keys = Lists.newArrayList();
        List <Object> incomeNum = Lists.newArrayList();//收入总计数
        List <Object> incomeTotalPrice = Lists.newArrayList();//下单金额

        Calendar beforeCalendar = Calendar.getInstance();
        beforeCalendar.setTime(beforeDate);
        Calendar afterCalendar = Calendar.getInstance();
        afterCalendar.setTime(afterDate);
        if ("4".equals(state)) {
            afterCalendar.add(Calendar.MONTH, +1);
        } else {
            afterCalendar.add(Calendar.DAY_OF_MONTH, +1);
        }

        if ("4".equals(state)) {
            //生成月报数据
            getMapResult(beforeCalendar, afterCalendar, incomeMap, null, keys, incomeNum, incomeTotalPrice, null, null, state);
        } else if ("3".equals(state)) {
            //生成周报数据
            getMapResult(beforeCalendar, afterCalendar, incomeMap, null, keys, incomeNum, incomeTotalPrice, null, null, state);
        } else {
            //生成日报数据
            getMapResult(beforeCalendar, afterCalendar, incomeMap, null, keys, incomeNum, incomeTotalPrice, null, null, state);
        }
        result.put("key", keys);
        result.put("incomeTotalPrice", incomeTotalPrice);
        return result;
    }

    /**
     * 支出统计
     * @param pageNo
     * @param model
     * @return
     */
    @RequestMapping(value = {"admin/statistics/expenditure"})
    public String expenditureStatistics(@RequestParam(defaultValue = "1") Integer pageNo, Model model, ShopWalletLog param){
        return null;
        // param.setIs_lg_add_rde(0);
        // Pageable pageable = new Pageable(pageNo, 20);
        // pageable.setParameter(param);
        // pageable.setOrderProperty("create_time");
        // pageable.setOrderDirection(Order.Direction.DESC);
        // Page<ShopWalletLog> page = shopWalletLogService.findByPage(pageable);
        // model.addAttribute("page", page);
        // //总支出
        // model.addAttribute("countAmount", shopWalletLogService.countAmount());
        // param.setLike(2);
        // //推荐返佣
        // param.setLgType("recommend_rebate");
        // model.addAttribute("recommendRebate", shopWalletLogService.countAmountByLgType(param));
        // //评价返佣
        // param.setLgType("goods_evaluate");
        // model.addAttribute("goodsEvaluate", shopWalletLogService.countAmountByLgType(param));
        // param.setLike(1);
        // //取消订单退款
        // param.setLgType("order_cancel");
        // model.addAttribute("orderCancel", shopWalletLogService.countAmountByLgType(param));
        // //售后订单退款
        // param.setLgType("refund");
        // model.addAttribute("refund", shopWalletLogService.countAmountByLgType(param));
        // model.addAttribute("state",20);
        // return "/views/financialStatistics/financialStatistics";
    }

    /**
     * 收入统计报表生成
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/ajaxExpenditureStatistics")
    @ResponseBody
    public Map<String, Object> ajaxExpenditureStatistics(HttpServletRequest request, Model model) {
        try {
            // // String begin = request.getParameter("beforeDate");
            // // //state:2:日报   3:周报   4:月报
            // // String state = request.getParameter("state");
            // // //默认为日报
            // // if(state == null || state == ""){
            // //     state = "2";
            // // }
            // // Date afterDate = new Date();//截止时间
            // // Calendar c = Calendar.getInstance();
            // // if("4".equals(state)){
            // //     //查询月报数据的起始月份
            // //     c.add(Calendar.YEAR, -1);
            // //     c.add(Calendar.MONTH,1);
            // // }else {
            // //     c.add(Calendar.MONTH,-1);
            // //     c.add(Calendar.DAY_OF_MONTH, 1);
            // // }
            // // Date beforeDate = c.getTime();//开始时间
            // // if (StringUtils.isNotBlank(begin)) {
            // //     beforeDate = new SimpleDateFormat("yyyy-MM-dd").parse(begin);
            // // }
            // // ActivityStatistics param = new ActivityStatistics();
            // // param.setBeforeDate(beforeDate);
            // // param.setAfterDate(afterDate);
            // // List<ActivityStatistics> totalExpenditure = new ArrayList<>();//收入
            // // List<ActivityStatistics> recommendRebate = new ArrayList<>();//收入
            // // List<ActivityStatistics> redPacket = new ArrayList<>();//收入
            // // if("4".equals(state)){
            // //     //查询月报数据
            // //     param.setState("3");
            // //     totalExpenditure = shopWalletLogService.statisticsExpenditureBystate(param);
            // //     //返佣
            // //     param.setLike(2);
            // //     param.setLgType("recommend_rebate");
            // //     recommendRebate = shopWalletLogService.statisticsExpenditureBystate(param);
            // //     //红包
            // //     param.setLike(1);
            // //     param.setLgType("red_packet");
            // //     redPacket = shopWalletLogService.statisticsExpenditureBystate(param);
            // // }else if("3".equals(state)){
            // //     //查询周报
            // //     param.setState("2");
            // //     totalExpenditure = shopWalletLogService.statisticsExpenditureBystate(param);
            // //     //返佣
            // //     param.setLike(2);
            // //     param.setLgType("recommend_rebate");
            // //     recommendRebate = shopWalletLogService.statisticsExpenditureBystate(param);
            // //     //红包
            // //     param.setLike(1);
            // //     param.setLgType("red_packet");
            // //     redPacket = shopWalletLogService.statisticsExpenditureBystate(param);
            // // }else {
            // //     //查询日报
            // //     param.setState("1");
            // //     totalExpenditure = shopWalletLogService.statisticsExpenditureBystate(param);
            // //     //返佣
            // //     param.setLike(2);
            // //     param.setLgType("recommend_rebate");
            // //     recommendRebate = shopWalletLogService.statisticsExpenditureBystate(param);
            // //     //红包
            // //     param.setLike(1);
            // //     param.setLgType("red_packet");
            // //     redPacket = shopWalletLogService.statisticsExpenditureBystate(param);
            // // }
            // // Map<Integer,ActivityStatistics> totalExpenditureMap = Maps.newLinkedHashMap();
            // // Map<Integer,ActivityStatistics> recommendRebateMap = Maps.newLinkedHashMap();
            // // Map<Integer,ActivityStatistics> redPacketMap = Maps.newLinkedHashMap();
            // // if("4".equals(state)){
            // //     //月报数据
            // //     getMap(totalExpenditure,totalExpenditureMap,state);
            // //     getMap(recommendRebate,recommendRebateMap,state);
            // //     getMap(redPacket,redPacketMap,state);
            // // }else if("3".equals(state)){
            // //     //周报数据
            // //     getMap(totalExpenditure,totalExpenditureMap,state);
            // //     getMap(recommendRebate,recommendRebateMap,state);
            // //     getMap(redPacket,redPacketMap,state);
            // // }else {
            // //     //日报数据
            // //     getMap(totalExpenditure,totalExpenditureMap,state);
            // //     getMap(recommendRebate,recommendRebateMap,state);
            // //     getMap(redPacket,redPacketMap,state);
            // // }
            // // Map<String, Object> result = Maps.newLinkedHashMap();
            // // List<Integer> keys = Lists.newArrayList();
            // // List<Object> totalExpenditurePrice = Lists.newArrayList();//下单金额
            // // List<Object> recommendRebatePrice = Lists.newArrayList();//下单金额
            // // List<Object> redPacketlPrice = Lists.newArrayList();//下单金额
            //
            // Calendar beforeCalendar = Calendar.getInstance();
            // beforeCalendar.setTime(beforeDate);
            // Calendar afterCalendar = Calendar.getInstance();
            // afterCalendar.setTime(afterDate);
            // if("4".equals(state)){
            //     afterCalendar.add(Calendar.MONTH, +1);
            // }else {
            //     afterCalendar.add(Calendar.DAY_OF_MONTH, +1);
            // }
            //
            // if("4".equals(state)) {
            //     //生成月报数据
            //     getMapResultByOne(beforeCalendar,afterCalendar,totalExpenditureMap,recommendRebateMap,redPacketMap,keys,totalExpenditurePrice,recommendRebatePrice,redPacketlPrice,state);
            // }else if("3".equals(state)) {
            //     //生成周报数据
            //     getMapResultByOne(beforeCalendar,afterCalendar,totalExpenditureMap,recommendRebateMap,redPacketMap,keys,totalExpenditurePrice,recommendRebatePrice,redPacketlPrice,state);
            // }else {
            //     //生成日报数据
            //     getMapResultByOne(beforeCalendar,afterCalendar,totalExpenditureMap,recommendRebateMap,redPacketMap,keys,totalExpenditurePrice,recommendRebatePrice,redPacketlPrice,state);
            // }
            // result.put("key",keys);
            // result.put("totalExpenditurePrice",totalExpenditurePrice);
            // result.put("recommendRebatePrice",recommendRebatePrice);
            // result.put("redPacketlPrice",redPacketlPrice);
            // return result;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     *双轴双线  统计+金额
     */
    private void getMapResult(Calendar beforeCalendar,Calendar afterCalendar, Map<Integer,ActivityStatisticsVo> countMap,Map<Integer,ActivityStatisticsVo> successMap,
            List<Integer> keys,List<Object> countNum,List<Object> countTotalPrice,List<Object> successNum,List<Object> successTotalPrice,String state){
        int key;
        while (beforeCalendar.before(afterCalendar)){
            if("4".equals(state)){
                key = beforeCalendar.get(Calendar.MONTH);
                keys.add(key+1);
            }else if("3".equals(state)){
                key = beforeCalendar.get(Calendar.WEEK_OF_YEAR);
                keys.add(key);
            }else {
                key = beforeCalendar.get(Calendar.DATE);
                keys.add(key);
            }

            ActivityStatisticsVo count = null;
            if(countMap != null){
                count = countMap.get(key);
            }
            if(countNum != null && countTotalPrice!=null){
                if (count == null ) {
                    countNum.add(0);
                    countTotalPrice.add(0);
                } else {
                    countNum.add(count.getTotalNum());
                    countTotalPrice.add(count.getTotalPrice());
                }
            }

            ActivityStatisticsVo success = null;
            if(successMap != null){
                success = successMap.get(key);
            }
            if(successNum != null && successTotalPrice != null){
                if (success == null ) {
                    successNum.add(0);
                    successTotalPrice.add(0);
                } else {
                    successNum.add(success.getTotalNum());
                    successTotalPrice.add(success.getTotalPrice());
                }
            }

            if("4".equals(state)){
                beforeCalendar.add(Calendar.MONTH,+1);
            }else if("3".equals(state)){
                beforeCalendar.add(Calendar.WEEK_OF_YEAR,+1);
            }else {
                beforeCalendar.add(Calendar.DAY_OF_MONTH,+1);
            }
        }
    }

    private void getMap(List<ActivityStatisticsVo> list,Map<Integer,ActivityStatisticsVo> map,String state){
        for(ActivityStatisticsVo activityStatistics : list){
            Calendar temp = Calendar.getInstance();
            if("4".equals(state)){
                //月报
                try {
                    temp.setTime(new SimpleDateFormat("yyyy-MM").parse(activityStatistics.getMonthDate().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                map.put(temp.get(Calendar.MONTH),activityStatistics);
            }else if("3".equals(state)){
                //周报
                map.put(activityStatistics.getWeekDate(),activityStatistics);
            }else {
                //日报
                temp.setTime(activityStatistics.getCreateDate());
                map.put(temp.get(Calendar.DATE),activityStatistics);
            }
        }
    }


}
