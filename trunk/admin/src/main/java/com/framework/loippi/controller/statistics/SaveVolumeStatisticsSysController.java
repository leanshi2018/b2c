package com.framework.loippi.controller.statistics;

import com.framework.loippi.consts.OrderState;
import com.framework.loippi.controller.GenericController;
import com.framework.loippi.dto.OrderStatisticsSysDto;
import com.framework.loippi.service.order.ShopOrderService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.validator.DateUtils;
import com.framework.loippi.vo.order.OrderCountVo;
import com.framework.loippi.vo.order.OrderStatisticsVo;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Controller
public class SaveVolumeStatisticsSysController extends GenericController {

    @Autowired
    private ShopOrderService orderService;

    @RequestMapping("/admin/volume/chartOrderPrice")
    @ResponseBody
    public JSONObject volumecountHighChart(HttpServletRequest request) {
        Map<String, Object> ordercountmap = getmap(request);//获得相应条件map
        ordercountmap.put("storeId", 0);
        ordercountmap.put("notOrderState", 5);
        List<OrderCountVo> ordercountList = orderService.listOrderbuy(ordercountmap);
        JSONObject data = new JSONObject();
        // 下单年月日集合
        List<String> yyMMddList = new ArrayList<>();
        // 下单状态集合
        List<String> orderStateList = new ArrayList<>();
        for (int i = 0; i < ordercountList.size(); i++) {
            OrderCountVo ocv = ordercountList.get(i);
            String createDate = ocv.getOrderDate();
            if (!yyMMddList.contains(createDate)) {
                yyMMddList.add(createDate);
            }
            // 已取消 待付款...
            String stateInfo = OrderState.orderStatus(ocv.getOrderState());
            if (!orderStateList.contains(stateInfo)) {
                orderStateList.add(stateInfo);
            }
        }

        JSONArray series = new JSONArray();
        JSONObject json = null;
        StringBuffer buffer = null;
        json = new JSONObject();
        json.put("name", "销售额");
        buffer = new StringBuffer();
//        for (int i = 0; i < ordercountList.size(); i++) {
//            String orderState = orderStateList.get(i);
//            if ("待付款".equals(orderState) || "待取消".equals(orderState)){
//                continue;
//            }
        buffer.append("[");
            for (int j = 0; j < yyMMddList.size(); j++) {

                String createDate = yyMMddList.get(j);
                String num = "0";
                for (int k = 0; k < ordercountList.size(); k++) {
                    OrderCountVo ggc = ordercountList.get(k);
                   if(ggc.getOrderState()==10 || ggc.getOrderState()==0){
                       continue;
                   }
                    if (null != createDate  && null != ggc.getOrderDate() && ggc.getOrderDate()
                            .equals(createDate)
                            && createDate.equals(ggc.getOrderDate())) {
                        num = ggc.getOrderPrice();
                        k = ordercountList.size();
                    }

                }
                buffer.append(num);
                buffer.append(",");

            }
        buffer.append("]");
        json.put("data", buffer.toString());
        series.add(json);
//        }
        data.put("xAxis", yyMMddList);
        data.put("series", series);
        data.put("startTime", ordercountmap.get("staTime"));
        data.put("endTime", ordercountmap.get("endTime"));
        return data;
    }


    @RequestMapping("/admin/volume/listOrder")
    public ModelAndView list(
            @RequestParam(required = false, value = "div", defaultValue = "") String div,
            @RequestParam(required = false, value = "pageNo", defaultValue = "") String pageNoStr,
            @RequestParam(required = false, value = "startTime", defaultValue = "") String startTime,
            @RequestParam(required = false, value = "endTime", defaultValue = "") String endTime,
            @RequestParam(required = false, value = "orderState", defaultValue = "") String orderState
    ) throws ParseException {
        ModelAndView model = new ModelAndView("statisc/volume/chart_order_list");
        Pageable pager = new Pageable();
        if (StringUtils.isNotBlank(pageNoStr)) {
            pager.setPageNumber(Integer.parseInt(pageNoStr));
        }
        Long day=1L;

        OrderStatisticsVo ordermVo = new OrderStatisticsVo();
        ordermVo.setStoreId(0L);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        if (StringUtils.isNotBlank(startTime)) {
            Date startT = sdf.parse(startTime);
            ordermVo.setStartTime(startT.getTime());
        }
        if (StringUtils.isNotBlank(startTime)) {
            Date endT = sdf.parse(endTime);
            Calendar todayEnd = Calendar.getInstance();
            todayEnd.setTime(endT);
            todayEnd.set(Calendar.HOUR, 23);
            todayEnd.set(Calendar.MINUTE, 59);
            todayEnd.set(Calendar.SECOND, 59);
            todayEnd.set(Calendar.MILLISECOND, 999);
            ordermVo.setEndTime(todayEnd.getTime().getTime());
           day=  (todayEnd.getTime().getTime()-sdf.parse(startTime).getTime()) / (1000 * 24 * 60 * 60);
        }
        if (StringUtils.isNotBlank(orderState)) {
            ordermVo.setOrderState(Integer.parseInt(orderState));
        }
        ordermVo.setNotOrderType("5");
        pager.setParameter(ordermVo);

        // 页面查询条件订单统计列表
        Page<OrderStatisticsVo> highcharorderList = orderService.listOrderStatistics(pager);
        List <OrderStatisticsSysDto> orderStatisticsSysDtoList=OrderStatisticsSysDto.buildList2(highcharorderList.getContent(),day);
        model.addObject("highcharorderList", orderStatisticsSysDtoList);//
        model.addObject("pageNo", pager.getPageNumber());// 当前页
        model.addObject("pageSize", pager.getPageSize());// 每页显示条数
        model.addObject("recordCount", orderStatisticsSysDtoList.size());// 总数
        model.addObject("pager", pager);
        model.addObject("div", div);// 显示的DIV数据区域
        model.addObject("toUrl", "/admin/volume/listOrder.jhtml");// 跳转URL
        // 转发请求到FTL页面
        return model;
    }

    @RequestMapping("/admin/volume/countOrder")
    public ModelAndView countOrder(Integer orderState) {
        ModelAndView model = new ModelAndView("statisc/volume/chart_type_order");
//        Paramap paramMap = Paramap.create().put("orderState", orderState);
//        List<OrderCountVo> resultList = orderService.listGroupPriceAndNumOfOrderType(paramMap);
//        model.addObject("orderNumList", resultList);
//        if (CollectionUtils.isNotEmpty(resultList)) {
//            // 订单总数
//            double sum = 0;
//            // 订单总额
//            double count = 0;
//            for (OrderCountVo orderCountVo : resultList) {
//                sum += Double.valueOf(orderCountVo.getOrderPrice());
//                count += Double.valueOf(orderCountVo.getNum());
//            }
//
//            model.addObject("orderSum", sum);
//            model.addObject("orderNum", count);
//        }
        return model;
    }


    /**
     * 通过条件拼接map
     */
    public Map<String, Object> getmap(HttpServletRequest request) {
        Map<String, Object> ordercountmap = new HashMap<String, Object>();
        String startime = request.getParameter("startime");//开始时间
        String endtime = request.getParameter("endtime");//结束时间
        String paymentState = request.getParameter("paymentState"); //付款状态
        String storeName = request.getParameter("storeName");//店铺名称
        String orderState = request.getParameter("orderState");//订单状态
        String timeScope = request.getParameter("type"); // 日报 周报 月报
        ordercountmap.put("dbName", "mysql");//数据库类型
        if (paymentState != null && !"".equals(paymentState)) {
            ordercountmap.put("balanceState", paymentState);
        }
        if (orderState != null && !"".equals(orderState)) {
            ordercountmap.put("orderState", orderState);
        }
        if (StringUtils.isNotEmpty(startime) && StringUtils.isNotEmpty(endtime)) {
            int month = compareDate(startime, endtime, 1);
            int day = compareDate(startime, endtime, 0);
            if (month <= 1 && day <= 31) {
                ordercountmap.put("timesn", "%Y-%m-%d");
            } else if (month >= 1 && month <= 12) {
                ordercountmap.put("timesn", "%Y-%m");
            } else {
                ordercountmap.put("timesn", "%Y");
            }
            ordercountmap.put("sqltimesn", 10);
            ordercountmap.put("storeName", storeName);
            ordercountmap.put("starttime", DateUtils.strToLong(startime + " 00:00:00"));//开始时间
            ordercountmap.put("endtime", DateUtils.strToLong(endtime + " 23:59:59"));//结束时间
            ordercountmap.put("staTime", startime);//时间回显
            ordercountmap.put("endTime", endtime);//时间回显
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date dateNow = new Date();
            Date dateFrom = null;
            if (StringUtils.isNotBlank(timeScope)) {
                // 获取当天
                if ("aDay".equals(timeScope)) {
                    dateFrom = dateNow;
                }// 获取一周前的时间
                else if ("aWeek".equals(timeScope)) {
                    Calendar cl = Calendar.getInstance();
                    cl.setTime(dateNow);
                    cl.add(Calendar.WEEK_OF_YEAR, -1);
                    dateFrom = cl.getTime();
                } // 获取一个月前时间
                else if ("aMonth".equals(timeScope)) {
                    Calendar cl = Calendar.getInstance();
                    cl.setTime(dateNow);
                    cl.add(Calendar.MONTH, -1);
                    dateFrom = cl.getTime();
                }
            } else {
                dateFrom = dateNow;
            }

            String createTime = sdf.format(dateFrom);
            ordercountmap.put("timesn", "%Y-%m-%d");
            ordercountmap.put("sqltimesn", 10);
            ordercountmap.put("storeName", storeName);
            ordercountmap.put("starttime", DateUtils.strToLong(createTime + " 00:00:00"));//开始时间
            ordercountmap.put("endtime", DateUtils.strToLong(DateUtils.getDate24() + " 23:59:59"));//结束时间
            ordercountmap.put("staTime", createTime);//时间回显
            ordercountmap.put("endTime", DateUtils.getDate24());//时间回显
        }
        return ordercountmap;
    }

    /**
     * @param date1 需要比较的时间 不能为空(null),需要正确的日期格式
     * @param date2 被比较的时间  为空(null)则为当前时间
     * @param stype 返回值类型   0为多少天，1为多少个月，2为多少年
     */
    public static int compareDate(String date1, String date2, int stype) {
        int n = 0;
        String[] u = {"天", "月", "年"};
        String formatStyle = stype == 1 ? "yyyy-MM" : "yyyy-MM-dd";
        date2 = date2 == null ? DateUtils.getDate24() : date2;
        DateFormat df = new SimpleDateFormat(formatStyle);
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        try {
            c1.setTime(df.parse(date1));
            c2.setTime(df.parse(date2));
        } catch (Exception e3) {
            System.out.println("wrong occured");
        }
        //List list = new ArrayList();
        while (!c1.after(c2)) {                     // 循环对比，直到相等，n 就是所要的结果
            //list.add(df.format(c1.getTime()));    // 这里可以把间隔的日期存到数组中 打印出来
            n++;
            if (stype == 1) {
                c1.add(Calendar.MONTH, 1);          // 比较月份，月份+1
            } else {
                c1.add(Calendar.DATE, 1);           // 比较天数，日期+1
            }
        }
        n = n - 1;
        if (stype == 2) {
            n = (int) n / 365;
        }
//        System.out.println(date1+" -- "+date2+" 相差多少"+u[stype]+":"+n);
        return n;
    }

    @RequestMapping("/admin/volume/groupStateCountOrder")
    @ResponseBody
    public String countOrder() {
        List<OrderCountVo> orderCountVoList = orderService.listOrderCountVo(0L);
        showSuccessJson(orderCountVoList);
        return json;
    }

}