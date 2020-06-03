package com.framework.loippi.controller.common;

import com.framework.loippi.consts.Constants;
import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.common.MemberIndicator;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.service.common.DailyMemCensusService;
import com.framework.loippi.service.common.MemberIndicatorService;
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
import java.util.ArrayList;
import java.util.Date;

/**
 * 会员指标
 */
@Slf4j
@Controller
@RequestMapping("/admin/memIndicator")
public class MemberIndicatorController extends GenericController {
    @Resource
    private MemberIndicatorService memberIndicatorService;

    /**
     *
     * @param model
     * @param request
     * @param pageable
     * @param defaultQuery 是否进入模块的第一次默认查询 是：1 否：0
     * @param periodCode 查询具体的某一个周期
     * @return
     */
    @RequestMapping("/list.jhtml")
    public String list(Model model, HttpServletRequest request, Pageable pageable, Integer defaultQuery,
                       @RequestParam(required = false, value = "periodCode") String periodCode) {
        MemberIndicator memberIndicator = new MemberIndicator();
        if(defaultQuery!=null&&defaultQuery==1){
            //默认查询当年报表数据
            SimpleDateFormat format = new SimpleDateFormat("yyyy");
            String year = format.format(new Date());
            ArrayList<String> strings = new ArrayList<>();
            for (int i=1;i < 13;i++){
                if(i<10){
                    strings.add(year+"0"+i);
                }else {
                    strings.add(year+i);
                }
            }
            memberIndicator.setPeriodList(strings);
        }else {
            if(periodCode==null||"".equals(periodCode.trim())){
                model.addAttribute("msg", "请传入想要查询的周期");
                return Constants.MSG_URL;
            }
            memberIndicator.setPeriodCode(periodCode);
        }
        memberIndicator.setStatus(1);
        pageable.setParameter(memberIndicator);
        pageable.setOrderProperty("statistical_time");
        pageable.setOrderDirection(Order.Direction.DESC);
        Page<MemberIndicator> page = memberIndicatorService.findByPage(pageable);
        model.addAttribute("page",page);
        return "";//TODO
    }
}
