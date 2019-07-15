package com.framework.loippi.controller.activity;

import com.framework.loippi.controller.GenericController;
import com.framework.loippi.service.TUserSettingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by longbh on 2017/7/27.
 */
@Controller("adminActivityCommonController")
@RequestMapping({"/admin/shop_activity_common"})
public class ActivityCommonController extends GenericController {

    @Resource
    private TUserSettingService tUserSettingService;

    /**
     * 活动反馈
     */
    @RequestMapping(value = "/setReback", method = RequestMethod.GET)
    public String setReback(Long id, ModelMap model) {
        Map<String, Object> params = new HashMap<>();
        params.put("reback_state", tUserSettingService.read("reback_state"));
        params.put("reback_max", tUserSettingService.read("reback_max"));
        params.put("reback_min", tUserSettingService.read("reback_min"));
        params.put("reback_yongjin", tUserSettingService.read("reback_yongjin"));
        params.put("reback_yongjin_rule", tUserSettingService.read("reback_yongjin_rule"));
        model.addAttribute("setting", params);
        return "/activity/common/setReback";
    }

}
