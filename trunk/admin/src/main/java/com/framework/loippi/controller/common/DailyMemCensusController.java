package com.framework.loippi.controller.common;

import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.common.DailyMemCensus;
import com.framework.loippi.entity.common.ShopCommonArticleClass;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.service.common.DailyMemCensusService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 每日用户数据统计
 */
@Slf4j
@Controller
@RequestMapping("/admin/memCensus")
public class DailyMemCensusController extends GenericController {
    @Resource
    private DailyMemCensusService dailyMemCensusService;

    /**
     *
     * @param model
     * @param request
     * @param pageable
     * @param defaultQuery 是否进入模块的第一次默认查询 是：1 否：0
     * @param timeLeft 查询时间左边限 yyyy-MM-dd
     * @param timeRight 查询时间右边限 yyyy-MM-dd
     * @return
     */
    @RequestMapping("/list.jhtml")
    public String list(Model model, HttpServletRequest request, Pageable pageable,Integer defaultQuery,
                       @RequestParam(required = false, value = "timeLeft") String timeLeft,@RequestParam(required = false, value = "timeRight") String timeRight) {
        DailyMemCensus dailyMemCensus = new DailyMemCensus();
        if(defaultQuery!=null&&defaultQuery==1){
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String timeR = format.format(new Date());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE, -6);
            String timeL = format.format(calendar.getTime());
            dailyMemCensus.setTimeLeft(timeL+" 00:00:00");
            dailyMemCensus.setTimeRight(timeR+" 23:59:59");
        }else {
            if(timeLeft!=null&&!"".equals(timeLeft.trim())){
                timeLeft=timeLeft+" 00:00:00";
                dailyMemCensus.setTimeLeft(timeLeft);
            }
            if(timeRight!=null&&!"".equals(timeRight.trim())){
                timeRight=timeRight+" 23:59:59";
                dailyMemCensus.setTimeRight(timeRight);
            }
        }
        pageable.setParameter(dailyMemCensus);
        pageable.setOrderProperty("statistical_time");
        pageable.setOrderDirection(Order.Direction.DESC);
        Page<DailyMemCensus> page = dailyMemCensusService.findByPage(pageable);
        model.addAttribute("page",page);
        return "";//TODO 分页查询每日用户数据统计数据
    }
}
