package com.framework.loippi.controller.common;

import com.framework.loippi.consts.Constants;
import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.User;
import com.framework.loippi.entity.common.ShopApp;
import com.framework.loippi.enus.ActivityTypeEnus;
import com.framework.loippi.service.QiniuService;
import com.framework.loippi.service.TUserSettingService;
import com.framework.loippi.service.UserService;
import com.framework.loippi.service.common.ShopAppService;
import com.framework.loippi.utils.StringUtil;
import com.framework.loippi.utils.file.FileUtils;
import com.google.common.collect.Maps;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import static com.github.abel533.echarts.code.SeriesType.map;

/**
 * Controller - 应用版本
 *
 * @author Mounate Yan。
 * @version 1.0
 */
@Controller("adminShopAppController")
@RequestMapping("/admin/app")
public class ShopAppController extends GenericController {

    @Resource
    private ShopAppService shopAppService;
    @Resource
    private UserService userService;
    @Resource
    private TUserSettingService tUserSettingService;
    @Resource
    private QiniuService qiniuService;

    /**
     * 编辑
     */
    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String edit(Long id, ModelMap model) {
        model.addAttribute("app", shopAppService.find(id));
        return "/admin/app/edit";
    }

    /**
     * 更新
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String update(ShopApp app,  MultipartFile file, HttpServletRequest request, ModelMap model) {
        ShopApp persistent = shopAppService.find(app.getId());
        if (persistent == null) return ERROR_VIEW;
        if (app.getId()==0){
            String link = qiniuService.upload(file);
            persistent.setUrl(link);
        }else{
            persistent.setUrl(app.getUrl());
        }
        persistent.setVersion(app.getVersion());
        persistent.setContent(app.getContent());
        persistent.setUpdateDate(new Date());
        persistent.setVersionCode(app.getVersionCode());
        User user = userService.getCurrent();
        if (user != null) {
            persistent.setUpdator(user.getId());
        }
        shopAppService.update(persistent);
        model.addAttribute("msg", "更新成功");
        return "redirect:list.jhtml";
    }

    /**
     * 列表
     */
    @RequiresPermissions("admin:app:list")
    @RequestMapping(value = "/list")
    public String list(HttpServletRequest request, ModelMap model) {
        model.addAttribute("apps", shopAppService.findAll());
        return "/admin/app/list";
    }

}
