//
//                       _oo0oo_
//                      o8888888o
//                      88" . "88
//                      (| -_- |)
//                      0\  =  /0
//                    ___/`---'\___
//                  .' \\|     |// '.
//                 / \\|||  :  |||// \
//                / _||||| -:- |||||- \
//               |   | \\\  -  /// |   |
//               | \_|  ''\---/''  |_/ |
//               \  .-\__  '-'  ___/-. /
//             ___'. .'  /--.--\  `. .'___
//          ."" '<  `.___\_<|>_/___.' >' "".
//         | | :  `- \`.;`\ _ /`;.`/ - ` : | |
//         \  \ `_.   \_ __\ /__ _/   .-` /  /
//     =====`-.____`.___ \_____/___.-`___.-'=====
//                       `=---='
//
//                佛祖保佑                  永不宕机
//                心外无法                  法外无心
package com.framework.loippi.controller;

import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.support.DateEditor;
import com.framework.loippi.support.Message;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.template.directive.FlashMessageDirective;
import com.framework.loippi.utils.JacksonUtil;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.ParameterUtils;
import com.framework.loippi.utils.StringUtil;
import com.framework.loippi.utils.validator.BeanValidators;
import com.framework.loippi.utils.web.SpringUtils;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlelr - GENERIC
 *
 * @author Loippi Team
 * @version 1.0
 */
@Slf4j
public class GenericController {

    @Autowired
    protected Validator validator;
    @Autowired
    protected TwiterIdService twiterIdService;

    protected String prefix;

    protected String json;

    protected Integer pageNumber;

    /**
     * 错误视图
     */
    protected static final String ERROR_VIEW = "/admin/common/error";

    /**
     * 错误消息
     */
    protected static final Message ERROR_MESSAGE = Message.error("admin.message.error");

    /**
     * 成功消息
     */
    protected static final Message SUCCESS_MESSAGE = Message.success("admin.message.success");


    /**
     * 获取国际化消息
     *
     * @param code 代码
     * @param args 参数
     * @return 国际化消息
     */
    protected String message(String code, Object... args) {
        return SpringUtils.getMessage(code, args);
    }


    /**
     * 数据绑定
     *
     * @param binder WebDataBinder
     */
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.registerCustomEditor(Date.class, new DateEditor(true));
    }

    /**
     * 添加瞬时消息
     *
     * @param redirectAttributes RedirectAttributes
     * @param message 消息
     */
    protected void addFlashMessage(RedirectAttributes redirectAttributes, Message message) {
        if (redirectAttributes != null && message != null) {
            redirectAttributes.addFlashAttribute(FlashMessageDirective.FLASH_MESSAGE_ATTRIBUTE_NAME, message);
        }
    }

    protected void processQueryConditions(Pageable pageable, HttpServletRequest request) {
        Map<String, Object> paramter = ParameterUtils.getParametersMapStartingWith(request, "filter_");
        paramter.put("bizType",4);
        pageable.setParameter(paramter);
    }

    /**
     * 服务端参数有效性验证,返回model
     *
     * @param object 验证的实体对象
     * @param groups 验证组
     * @return 验证成功：返回true；严重失败：将错误信息添加到 message 中
     */
    protected boolean beanValidatorForModel(ModelMap model, Object object, Class<?>... groups) {
        try {
            BeanValidators.validateWithException(validator, object, groups);
        } catch (ConstraintViolationException ex) {
            List<String> list = BeanValidators.extractPropertyAndMessageAsList(ex, ": ");
            list.add(0, "数据验证失败：");
            addMessage(model, list.toArray(new String[]{}));
            return false;
        }
        return true;
    }

    /**
     * 添加Model消息
     */
    protected void addMessage(ModelMap model, String... messages) {
        StringBuilder sb = new StringBuilder();
        for (String message : messages) {
            sb.append(message).append(messages.length > 1 ? "<br/>" : "");
        }
        model.addAttribute("msg", sb.toString());
    }

    /**
     * 添加Json消息
     */
    protected void addJsonMessage(String... messages) {
        StringBuilder sb = new StringBuilder();
        for (String message : messages) {
            sb.append(message).append(messages.length > 1 ? "<br/>" : "");
        }
        //this.json = "{\"result\":\"0\",\"message\":\""+sb.toString()+"\"}";
        showErrorJson(sb.toString());
    }

    /**
     * 添加map消息
     */
    protected void addMapMessage(Map<String, String> map, String... messages) {
        StringBuilder sb = new StringBuilder();
        for (String message : messages) {
            sb.append(message).append(messages.length > 1 ? "<br/>" : "");
        }
        map.put("result", "0");
        map.put("message", sb.toString());
    }

    /**
     * 服务端参数有效性验证,返回json
     *
     * @param object 验证的实体对象
     * @param groups 验证组
     * @return 验证成功：返回true；严重失败：将错误信息添加到 message 中
     */
    protected boolean beanValidatorForJson(Object object, Class<?>... groups) {
        try {
            BeanValidators.validateWithException(validator, object, groups);
        } catch (ConstraintViolationException ex) {
            List<String> list = BeanValidators.extractPropertyAndMessageAsList(ex, ": ");
            list.add(0, "数据验证失败：");
            addJsonMessage(list.toArray(new String[]{}));
            return false;
        }
        return true;
    }

    /**
     * 添加Flash消息
     */
    protected void addMessage(RedirectAttributes redirectAttributes, String... messages) {
        StringBuilder sb = new StringBuilder();
        for (String message : messages) {
            sb.append(message).append(messages.length > 1 ? "<br/>" : "");
        }
        redirectAttributes.addFlashAttribute("message", sb.toString());
    }

    /**
     * 返回失败的json串
     */
    protected void showErrorJson(String message) {
        if (StringUtils.isEmpty(message)) {
            this.json = "{\"result\":\"0\"}";
        } else {
            this.json = "{\"result\":\"0\",\"message\":\"" + message + "\"}";
        }
    }


    /**
     * 返回成功的json串
     */
    protected void showSuccessJson(String message) {
        if (StringUtils.isEmpty(message)) {
            this.json = "{\"result\":\"1\"}";
        } else {
            this.json = "{\"result\":\"1\",\"message\":\"" + message + "\"}";
        }
    }

    protected void showSuccessJson(Object obj) {
        Map<String, Object> map = new HashMap();
        map.put("result", "1");
        map.put("message", "操作成功");
        map.put("data", obj);
        this.json = JacksonUtil.toJson(map);
    }


    /**
     * 参数绑定异常
     */
    @ExceptionHandler({BindException.class, ConstraintViolationException.class, ValidationException.class,
        Exception.class})
    public ModelAndView bindException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        log.error("", ex);
        // 编译错误信息
        StringBuilder sb = new StringBuilder("错误信息：");
        if (ex != null) {
            if (ex instanceof BindException) {
                for (ObjectError e : ((BindException) ex).getGlobalErrors()) {
                    sb.append(e.getDefaultMessage() + "(" + e.getObjectName() + ")\n");
                }
                for (FieldError e : ((BindException) ex).getFieldErrors()) {
                    sb.append(e.getDefaultMessage() + "(" + e.getField() + ")\n");
                }
            } else if (ex instanceof ConstraintViolationException) {
                for (ConstraintViolation<?> v : ((ConstraintViolationException) ex).getConstraintViolations()) {
                    sb.append(v.getMessage() + "(" + v.getPropertyPath() + ")\n");
                }
            } else if (ex instanceof StateResult) {
                //sb.append(Exceptions.getStackTraceAsString(ex));
                sb.append(ex.getMessage());
            } else {
                sb.append("未知错误, 请联系管理员！");
            }
        } else {
            sb.append("未知错误.");
        }

        String requestType = request.getHeader("X-Requested-With");
        // 如果为异步请求
        if (requestType != null && requestType.equalsIgnoreCase("XMLHttpRequest")) {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-type", "application/json;charset=UTF-8");
            PrintWriter writer;
            try {
                writer = response.getWriter();
                writer.write("{\"result\":\"0\",\"message\":\"" + sb + "\"}");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            Map<String, Object> m = Maps.newHashMap();
            m.put("msg", StringUtil.toHtml(sb.toString()));
            return new ModelAndView("views/commons/error_400", m);
        }
    }
}
