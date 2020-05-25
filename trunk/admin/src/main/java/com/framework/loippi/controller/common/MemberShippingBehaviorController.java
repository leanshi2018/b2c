package com.framework.loippi.controller.common;

import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.common.DailyOrderCensus;
import com.framework.loippi.entity.common.MemberShippingBehavior;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.service.common.MemberShippingBehaviorService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 新老会员购买行为统计
 */
@Slf4j
@Controller
@RequestMapping("/admin/shippingBehavior")
public class MemberShippingBehaviorController  extends GenericController {
    @Resource
    private MemberShippingBehaviorService memberShippingBehaviorService;

    /**
     *
     * @param model
     * @param request
     * @param pageable
     * @param defaultQuery 是否默认进入查询 1：是 0：否
     * @param timeLeft 查询时间左边限 yyyy-MM-dd
     * @param timeRight 查询时间右边限 yyyy-MM-dd
     * @return
     */
    @RequestMapping("/list.jhtml")
    public String list(Model model, HttpServletRequest request, Pageable pageable, Integer defaultQuery,
                       @RequestParam(required = false, value = "timeLeft") String timeLeft,@RequestParam(required = false, value = "timeRight") String timeRight) {
        MemberShippingBehavior memberShippingBehavior = new MemberShippingBehavior();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if(defaultQuery!=null&&defaultQuery==1){
            String timeR = format.format(new Date());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE, -6);
            String timeL = format.format(calendar.getTime());
            memberShippingBehavior.setTimeLeft(timeL+" 00:00:00");
            memberShippingBehavior.setTimeRight(timeR+" 23:59:59");
        }else {
            Calendar calendar = Calendar.getInstance();
            if(timeLeft!=null&&!"".equals(timeLeft.trim())){
                try {
                    Date left = format.parse(timeLeft);
                    calendar.setTime(left);
                    calendar.add(Calendar.DATE, 1);
                    Date timeL = calendar.getTime();
                    String format1 = format.format(timeL);
                    memberShippingBehavior.setTimeLeft(format1+" 00:00:00");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if(timeRight!=null&&!"".equals(timeRight.trim())){
                try {
                    Date right = format.parse(timeRight);
                    calendar.setTime(right);
                    calendar.add(Calendar.DATE, 1);
                    Date timeR = calendar.getTime();
                    String format2 = format.format(timeR);
                    memberShippingBehavior.setTimeRight(format2+" 23:59:59");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        pageable.setParameter(memberShippingBehavior);
        pageable.setOrderProperty("statistical_time");
        pageable.setOrderDirection(Order.Direction.DESC);
        Page<MemberShippingBehavior> page = memberShippingBehaviorService.findByPage(pageable);
        model.addAttribute("page",page);
        return "";//TODO
    }
}
