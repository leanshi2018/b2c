package com.framework.loippi.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by longbh on 2017/7/28.
 */
@Controller
@Slf4j
@RequestMapping("/commons")
public class CommonController extends GenericController {

    @RequestMapping(value = {"/show_msg"}, method = {RequestMethod.GET})
    public String msgShow(String msg, ModelMap modelMap) {
        modelMap.addAttribute("msg", msg);
        return "/common/common/show_msg";
    }

}
