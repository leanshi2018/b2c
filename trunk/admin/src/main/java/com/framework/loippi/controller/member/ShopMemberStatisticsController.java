package com.framework.loippi.controller.member;

import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.user.ShopMemberRegisterNum;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2017/9/14.
 * 会员统计
 */
@Controller
@RequestMapping("/member/statistics")
public class ShopMemberStatisticsController extends GenericController {

    @Autowired
    private RdMmBasicInfoService rdMmBasicInfoService;

    @RequestMapping("/registerNum")
    public String registerNum(HttpServletRequest request) {
        return "/views/memberstatistics/registerNum";
    }

    @RequestMapping("/ajaxRegisterNum")
    @ResponseBody
    public Map<String, List<Integer>> registerNum(HttpServletRequest request, Model model) {
//        try {
//            String begin = request.getParameter("beforeDate");
//            String end = request.getParameter("afterDate");
//            Date afterDate = new Date();
//            Calendar c = Calendar.getInstance();
//            c.add(Calendar.MONTH,-1);
//            c.add(Calendar.DAY_OF_MONTH, 1);
//            Date beforeDate = c.getTime();
//            if (StringUtils.isNotBlank(end)) {
//                afterDate = new SimpleDateFormat("yyyy-MM-dd").parse(end);
//                if (StringUtils.isBlank(begin)) {
//                    c.setTime(afterDate);
//                    c.add(Calendar.MONTH,-1);
//                    c.add(Calendar.DAY_OF_MONTH, 1);
//                    beforeDate = c.getTime();
//                }
//            }
//            if (StringUtils.isNotBlank(begin)) {
//                beforeDate = new SimpleDateFormat("yyyy-MM-dd").parse(begin);
//            }
//
//            ShopMemberRegisterNum param = new ShopMemberRegisterNum();
//            param.setBeforeDate(beforeDate);
//            param.setAfterDate(afterDate);
//            List<ShopMemberRegisterNum> shopMemberRegisterNums = shopMemberService.statisticsRegisterNum(param);
//            Map<Integer,ShopMemberRegisterNum> registerNumMap = Maps.newLinkedHashMap();
//            for(ShopMemberRegisterNum shopMemberRegisterNum : shopMemberRegisterNums) {
//                Calendar temp = Calendar.getInstance();
//                temp.setTime(shopMemberRegisterNum.getRegisterDate());
//                registerNumMap.put(temp.get(Calendar.DATE),shopMemberRegisterNum);
//            }
//
//            Map<String, List<Integer>> result = Maps.newLinkedHashMap();
//            List<Integer> keys = Lists.newArrayList();
//            List<Integer> values = Lists.newArrayList();
//
//            Calendar beforeCalendar = Calendar.getInstance();
//            beforeCalendar.setTime(beforeDate);
//            Calendar afterCalendar = Calendar.getInstance();
//            afterCalendar.setTime(afterDate);
//            afterCalendar.add(Calendar.DAY_OF_MONTH, +1);
//            while (beforeCalendar.before(afterCalendar)) {
//                int key = beforeCalendar.get(Calendar.DATE);
//                keys.add(key);
//                ShopMemberRegisterNum registerNum = registerNumMap.get(key);
//                if (registerNum == null ) {
//                    values.add(0);
//                } else {
//                    values.add(registerNum.getRegisterNum());
//                }
//                beforeCalendar.add(Calendar.DAY_OF_MONTH,+1);
//            }
//
//            result.put("key",keys);
//            result.put("value",values);
//            return result;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        return null;
    }

}
