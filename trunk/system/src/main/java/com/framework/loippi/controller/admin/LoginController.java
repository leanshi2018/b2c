package com.framework.loippi.controller.admin;

import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.framework.loippi.utils.RandomUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.framework.loippi.entity.Principal;
import com.framework.loippi.service.RSAService;
import com.framework.loippi.support.Message;
import com.framework.loippi.controller.GenericController;

/**
 * Controlelr - 登录
 *
 * @author Loippi Team
 * @version 1.0
 */
@Controller("adminLoginController")
public class LoginController extends GenericController {

    @Resource
    private RSAService rsaService;
    @Value("${system.name}")
    private String systemName;
    @Value("${system.version}")
    private String systemVersion;

    /**
     * 登录
     *
     * @return
     */
    @RequestMapping(name = "/admin/login", method = RequestMethod.GET)
    public String index(HttpServletRequest request, ModelMap model) {
        Subject subject = SecurityUtils.getSubject();
        if (subject != null) {
            Principal principal = (Principal) subject.getPrincipal();
            if (principal != null && principal.getId() != null) {

                return "redirect:/admin/common/main.jhtml";
            }
        }
        String token = RandomUtils.getRandomLetterString(20);
        RSAPublicKey publicKey = rsaService.generateKey(request, token);
        try {
            String modulus = Base64.getEncoder().encodeToString(publicKey.getModulus().toByteArray());
            String exponent = Base64.getEncoder().encodeToString(publicKey.getPublicExponent().toByteArray());
            model.addAttribute("modulus", modulus);
            model.addAttribute("exponent", exponent);
            model.addAttribute("token", token);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String captchaId = UUID.randomUUID().toString();
        model.addAttribute("captchaId", captchaId);

        model.addAttribute("systemName", systemName);
        model.addAttribute("systemVersion", systemVersion);
        return "/admin/login/index";
    }

    @RequestMapping(name = "/admin/login", method = RequestMethod.POST)
    public String submit(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String message = null;
        String loginFailure = (String) request.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
        if (StringUtils.isNotEmpty(loginFailure)) {
            if (loginFailure.equals("org.apache.shiro.authc.pam.UnsupportedTokenException")) {
                message = "admin.captcha.invalid";
            }
            else if (loginFailure.equals("org.apache.shiro.authc.UnknownAccountException")) {
                message = "admin.login.unknownAccount";
            }
            else if (loginFailure.equals("org.apache.shiro.authc.DisabledAccountException")) {
                message = "admin.login.disabledAccount";
            } else if (loginFailure.equals("org.apache.shiro.authc.LockedAccountException")) {
                message = "admin.login.lockedAccount";
            } else if (loginFailure.equals("org.apache.shiro.authc.IncorrectCredentialsException")) {
                message = "admin.login.incorrectCredentials";
            } else if (loginFailure.equals("org.apache.shiro.authc.AuthenticationException")) {
                message = "admin.login.authentication";
            }
            addFlashMessage(redirectAttributes, Message.error(message));

            return "redirect:/admin/login.jhtml";
        }

        return "redirect:/admin/common/main.jhtml";
    }
}
