package com.framework.loippi.controller.wap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.framework.loippi.controller.BaseController;
import com.framework.loippi.dto.oss.AliyunOss;
import com.framework.loippi.entity.common.ShopCommonDocument;
import com.framework.loippi.service.common.ShopCommonDocumentService;

/**
 * Created by longbh on 2017/8/12.
 */
@Controller
@RequestMapping("/wap")
public class WapPageController extends BaseController {

    @Resource
    private ShopCommonDocumentService shopCommonDocumentService;

    /**
     * 分享页
     *
     * @param model
     * @param uid
     * @return
     */
    @RequestMapping(value = "/share/{uid}", method = RequestMethod.GET)
    public String share(ModelMap model, @PathVariable Long uid, Long userId) {
        if (userId != null) {
            uid = userId;
        }
        model.addAttribute("uid", uid);
        return "/wap/user/share";
    }

    /**
     * 常见问题
     *
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "/help", method = RequestMethod.GET)
    public String help(ModelMap model, HttpServletRequest request) {
        return "/wap/user/help";
    }

    @RequestMapping(value = "/about", method = RequestMethod.GET)
    public String about(ModelMap model, HttpServletRequest request) {
        return "/wap/user/about";
    }

    /**
     * 文章详情
     *
     * @param model
     * @param id
     * @return
     */
    @RequestMapping(value = "/aboutInfo", method = RequestMethod.GET)
    public String aboutInfo(ModelMap model, Long id) {
        ShopCommonDocument shopCommonDocument = shopCommonDocumentService.find(id);
        model.addAttribute("document", shopCommonDocument);
        return "/wap/user/aboutInfo";
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public String download(ModelMap model, String code,String downloadUrl) {
        model.addAttribute("code", code);
        model.addAttribute("qrcode", AliyunOss.uploadQRcord2Oss(code,"code"));
        model.addAttribute("downloadUrl", downloadUrl);
        return "/wap/download";
    }

    @RequestMapping(value = "/loginSuccessful", method = RequestMethod.GET)
    public String loginSuccessful(ModelMap model, HttpServletRequest request) {
        return "/wap/loginSuccessful";
    }

    @RequestMapping(value = "/oldLogin", method = RequestMethod.GET)
    public String oldLogin(ModelMap model, HttpServletRequest request) {
        return "/wap/oldLogin";
    }

    @RequestMapping(value = "/oldRegister", method = RequestMethod.GET)
    public String oldRegister(ModelMap model, HttpServletRequest request) {
        return "/wap/oldRegister";
    }

}
