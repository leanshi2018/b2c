package com.framework.loippi.controller.selfMention;

import com.framework.loippi.consts.Constants;
import com.framework.loippi.controller.BaseController;
import com.framework.loippi.param.order.OrderSubmitParam;
import com.framework.loippi.result.auths.AuthsLoginResult;
import com.framework.loippi.utils.ApiUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller("selfMentionController")
@Slf4j
public class SelfMentionController extends BaseController {

    /**
     * 点击进入我的小店
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/mention/center.json")
    @ResponseBody
    public String inCenter(HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if(member==null){
            return ApiUtils.error("当前用户尚未登录");
        }
        String mmCode = member.getMmCode();//店主会员编号
        return "";
    }
}
