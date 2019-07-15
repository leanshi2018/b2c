package com.framework.loippi.controller.admin;

import com.framework.loippi.consts.Constants;
import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.Acl;
import com.framework.loippi.entity.Principal;
import com.framework.loippi.entity.ShopCommonMessage;
import com.framework.loippi.entity.User;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.service.AclService;
import com.framework.loippi.service.RedisService;
import com.framework.loippi.service.ShopCommonMessageService;
import com.framework.loippi.service.UserService;
import com.framework.loippi.support.Message;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.CacheUtils;
import com.framework.loippi.utils.Digests;
import com.framework.loippi.utils.JacksonUtil;
import com.google.code.kaptcha.Producer;
import com.loippi.core.gen.IFactory;
import com.loippi.core.gen.ITable;
import com.loippi.core.gen.mysql.impl.MysqlFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ServletContextAware;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;


/**
 * Controller - 共用
 *
 * @author Loippi Team
 * @version 1.0
 */
@Controller("adminCommonController")
@RequestMapping("/admin/common")
public class CommonController extends GenericController implements ServletContextAware {

    @Value("${system.name}")
    private String systemName;
    @Value("${system.version}")
    private String systemVersion;
    @Value("${system.description}")
    private String systemDescription;


    @Value("${jdbc.url}")
    private String url;

    @Value("${jdbc.username}")
    private String username;

    @Value("${jdbc.password}")
    private String password;

    @Value("${system.name}")
    private String prefix;

    @Resource
    private AclService aclService;


    @Resource
    private UserService userService;

    @Autowired
    private Producer captchaProducer;
    @Resource
    private ShopCommonMessageService shopCommonMessageService;

    /**
     * servletContext
     */
    private ServletContext servletContext;

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Autowired
    private RedisService redisService;


    /**
     * 主页
     */

    @RequestMapping(value = "/main", method = RequestMethod.GET)
    public String main(HttpServletRequest request, ModelMap model) {
        model.addAttribute("systemName", systemName);
        model.addAttribute("systemVersion", systemVersion);
        model.addAttribute("systemDescription", systemDescription);
        model.addAttribute("javaVersion", System.getProperty("java.version"));
        model.addAttribute("javaHome", System.getProperty("java.home"));
        model.addAttribute("osName", System.getProperty("os.name"));
        model.addAttribute("osArch", System.getProperty("os.arch"));
        model.addAttribute("serverInfo", servletContext.getServerInfo());
        model.addAttribute("servletVersion", servletContext.getMajorVersion() + "." + servletContext.getMinorVersion());
        Subject subject = SecurityUtils.getSubject();
        if (subject != null) {
            Principal principal = (Principal) subject.getPrincipal();
            if (principal != null && principal.getId() != null) {
                model.addAttribute("principal", principal);
            }
        }

        String aclStr = redisService.get(prefix + "shop_acl", String.class);
        if (aclStr == null || "".equals(aclStr) || "true".equals(request.getParameter("refreshAcl"))) {
            List<Acl> acls = aclService.findRoots(1);
            for (Acl acl : acls) {
                List<Acl> childrens = aclService.findChildrens(1, acl.getId());
                acl.getChildren().addAll(childrens);
                for (Acl children : childrens) {
                    children.getChildren().addAll(aclService.findChildrens(1, children.getId()));
                }
            }
            redisService.save(prefix + "shop_acl", JacksonUtil.toJson(acls));
            model.addAttribute("acls", acls);
        } else {
            model.addAttribute("acls", JacksonUtil.convertList(aclStr, Acl.class));
        }

        User user = userService.getCurrent();
        if (user != null && user.getTheme() != null && user.getTheme() == 2) {
            return "/admin/common/main_classic";
        }
        return "/admin/common/main";
    }


    /**
     * 主题设置
     */

    @RequestMapping(value = "/theme", method = RequestMethod.POST)
    public
    @ResponseBody
    Message theme(HttpServletRequest request, Integer theme, ModelMap model) {
        User user = userService.getCurrent();
        user.setTheme(theme);
        userService.update(user);
        return SUCCESS_MESSAGE;
    }


    //    /**
//     * 首页
//     */
//    @RequestMapping(value = "/index", method = RequestMethod.GET)
//    public String index(ModelMap model, Pageable pageable) {
//        model.addAttribute("systemName", systemName);
//        model.addAttribute("systemVersion", systemVersion);
//        model.addAttribute("systemDescription", systemDescription);
//        model.addAttribute("javaVersion", System.getProperty("java.version"));
//        model.addAttribute("javaHome", System.getProperty("java.home"));
//        model.addAttribute("osName", System.getProperty("os.name"));
//        model.addAttribute("osArch", System.getProperty("os.arch"));
//        model.addAttribute("serverInfo", servletContext.getServerInfo());
//        model.addAttribute("servletVersion", servletContext.getMajorVersion() + "." + servletContext.getMinorVersion());
//        return "/admin/common/index";
//    }
    @RequestMapping("/index")
    public String index(Model model, Pageable pageable, ShopCommonMessage shopCommonMessage) {
        shopCommonMessage.setBizType(3);
        shopCommonMessage.setSendUid(0 + "");
        pageable.setParameter(shopCommonMessage);
        pageable.setOrderProperty("create_time");
        if (shopCommonMessage.getSearchStartTime() != null && !shopCommonMessage.getSearchStartTime().equals("")) {
            shopCommonMessage.setSearchStartTime(shopCommonMessage.getSearchStartTime() + " 00:00:00");
        }
        if (shopCommonMessage.getSearchEndTime() != null && !shopCommonMessage.getSearchEndTime().equals("")) {
            shopCommonMessage.setSearchEndTime(shopCommonMessage.getSearchEndTime() + " 23:59:59");
        }
        pageable.setOrderDirection(Order.Direction.DESC);
        Page<ShopCommonMessage> results = shopCommonMessageService.findByPage(pageable);//结果集
        model.addAttribute("page", results);
        model.addAttribute("param", shopCommonMessage);
        return "common/shop_common_message/index";
    }

    /**
     * 注销
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String execute(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        SecurityUtils.getSubject().logout();
        return "redirect:/admin/login.jhtml?jssionid=" + session.getId();
    }

    @RequestMapping(value = "/password", method = RequestMethod.GET)
    public String password(HttpServletRequest request, ModelMap model) {
        return "/admin/common/password";
    }

    /**
     * 修改密码
     */
    @RequestMapping(value = "/password", method = RequestMethod.POST)
    public String doPassword(String oldPassword, String newPassword, ModelMap model, HttpServletRequest request) {
        User user = userService.getCurrent();
        if (!Digests.validatePassword(oldPassword, user.getPassword())) {
            model.addAttribute("oldPassword", oldPassword);
            model.addAttribute("newPassword", newPassword);
            model.addAttribute("msg", "密码错误");
        } else {
            user.setPassword(Digests.entryptPassword(newPassword));
            userService.update(user);
            model.addAttribute("msg", "修改密码成功");
            return "redirect:/admin/common/logout.jhtml";
        }
        model.addAttribute("referer", "/admin/common/main.jhtml");
        return Constants.MSG_URL;

    }

    /**
     * 数据字典
     */
    @RequestMapping(value = "/dic", method = RequestMethod.GET)
    public String dic(ModelMap model) {
        try {
            IFactory factory = MysqlFactory.getInstance(url, username, password);
            List<ITable> tables = factory.getTables();
            model.addAttribute("tables", tables);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "/admin/common/dic";
    }

    /**
     * 验证码
     */
    @RequestMapping(value = "/captcha", method = RequestMethod.GET)
    public void image(String captchaId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (StringUtils.isEmpty(captchaId)) {
            captchaId = request.getSession().getId();
        }

        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");

        String capText = captchaProducer.createText();// 生成验证码字符串
        try {
            redisService.save(captchaId, capText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        BufferedImage bi = captchaProducer.createImage(capText);// 生成验证码图片
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(bi, "jpg", out);
        try {
            out.flush();
        } finally {
            out.close();
        }
        return;
    }

    /**
     * 错误提示
     */
    @RequestMapping("/error")
    public String error() {
        return ERROR_VIEW;
    }

    /**
     * 错误提示
     */
    @RequestMapping("/error2")
    public String error2() {
        return "/admin/common/error2";
    }

    /**
     * 权限错误
     */
    @RequestMapping("/unauthorized")
    public String unauthorized(HttpServletRequest request,
        HttpServletResponse response) {
        String requestType = request.getHeader("X-Requested-With");
        if (requestType != null
            && requestType.equalsIgnoreCase("XMLHttpRequest")) {
            response.addHeader("loginStatus", "unauthorized");
            try {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        return "/admin/common/unauthorized";
    }

    /**
     * 资源不存在
     */
    @RequestMapping("/resource_not_found")
    public String resourceNotFound() {
        return "/admin/common/resource_not_found";
    }
}