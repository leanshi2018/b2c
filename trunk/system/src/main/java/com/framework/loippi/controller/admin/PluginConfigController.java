package com.framework.loippi.controller.admin;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.framework.loippi.controller.GenericController;
import com.framework.loippi.service.TUserSettingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.framework.loippi.entity.TSystemPluginConfig;
import com.framework.loippi.service.TSystemPluginConfigService;
import com.framework.loippi.support.Message;
import com.framework.loippi.support.Pageable;

import java.util.Map;

/**
 * Controller - 插件配置
 *
 * @author zijing
 * @version 2.0
 */
@Controller("adminTSystemPluginConfigController")
@RequestMapping({"/admin/tsystem_plugin_config"})
public class PluginConfigController extends GenericController {

    @Resource
    private TSystemPluginConfigService tSystemPluginConfigService;
    @Resource
    private TUserSettingService tUserSettingService;

    /**
     * 跳转添加页面
     *
     * @param model
     * @return
     */
    @RequestMapping(value = {"/add"}, method = {RequestMethod.GET})
    public String add(ModelMap model) {
        return "/common/tsystem_plugin_config/add";
    }

    /**
     * 保存
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(TSystemPluginConfig tSystemPluginConfig, RedirectAttributes redirectAttributes) {
        tSystemPluginConfigService.save(tSystemPluginConfig);
        addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
        return "redirect:list.jhtml";
    }

    /**
     * 编辑
     */
    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String edit(Long id, ModelMap model) {
        TSystemPluginConfig tSystemPluginConfig = tSystemPluginConfigService.find(id);
        model.addAttribute("tSystemPluginConfig", tSystemPluginConfig);
        return "/common/tsystem_plugin_config/" + tSystemPluginConfig.getPluginId() + "/edit";
    }


    /**
     * 详情
     */
    @RequestMapping(value = "/view", method = RequestMethod.GET)
    public String view(Long id, ModelMap model) {
        TSystemPluginConfig tSystemPluginConfig = tSystemPluginConfigService.find(id);
        model.addAttribute("tSystemPluginConfig", tSystemPluginConfig);
        return "/common/tsystem_plugin_config/view";
    }


    /**
     * 更新
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String update(TSystemPluginConfig tSystemPluginConfig, RedirectAttributes redirectAttributes) {
        tUserSettingService.save(tSystemPluginConfig.getPluginId() + "Enable", tSystemPluginConfig.getIsEnabled() + "");
        tSystemPluginConfigService.update(tSystemPluginConfig);
        addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
        if (tSystemPluginConfig.getType() == 1) {
            return "redirect:listFile.jhtml";
        } else {
            return "redirect:listPay.jhtml";
        }
    }

    /**
     * 列表查询
     *
     * @param pageable
     * @param model
     * @return
     */
    @RequestMapping(value = {"/listPay"})
    public String listPay(Pageable pageable, HttpServletRequest request, ModelMap model) {
        processQueryConditions(pageable, request);
        Map<String, Object> params = (Map<String, Object>) pageable.getParameter();
        params.put("type", 2);
        pageable.setParameter(params);
        model.addAttribute("page", this.tSystemPluginConfigService.findByPage(pageable));
        model.addAttribute("paramter", pageable.getParameter());
        return "/common/tsystem_plugin_config/list";
    }

    /**
     * 存储列表查询
     *
     * @param pageable
     * @param model
     * @return
     */
    @RequestMapping(value = {"/listFile"})
    public String listFile(Pageable pageable, HttpServletRequest request, ModelMap model) {
        processQueryConditions(pageable, request);
        Map<String, Object> params = (Map<String, Object>) pageable.getParameter();
        params.put("type", 1);
        pageable.setParameter(params);
        model.addAttribute("page", this.tSystemPluginConfigService.findByPage(pageable));
        model.addAttribute("paramter", pageable.getParameter());
        return "/common/tsystem_plugin_config/list";
    }

    /**
     * 删除操作
     *
     * @param ids
     * @return
     */
    @RequestMapping(value = {"/delete"}, method = {RequestMethod.POST})
    public
    @ResponseBody
    Message delete(Long[] ids) {
        this.tSystemPluginConfigService.deleteAll(ids);
        return SUCCESS_MESSAGE;
    }
}
