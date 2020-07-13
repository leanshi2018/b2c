package com.framework.loippi.controller.travel;

import com.allinpay.yunst.sdk.YunClient;
import com.allinpay.yunst.sdk.bean.YunRequest;
import com.framework.loippi.consts.Constants;
import com.framework.loippi.controller.BaseController;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.param.order.OrderSubmitParam;
import com.framework.loippi.param.travel.TravelMemSubmitParam;
import com.framework.loippi.result.auths.AuthsLoginResult;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.utils.ApiUtils;
import com.framework.loippi.utils.JacksonUtil;
import com.framework.loippi.utils.StringUtil;
import com.framework.loippi.utils.Xerror;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

/**
 * 旅游活动控制层
 *
 * @author zc
 * @version 1.0
 */
@Controller("travelController")
@Slf4j
@ResponseBody
@RequestMapping("/api/travel")
public class TravelController extends BaseController {
    @Resource
    private RdMmBasicInfoService rdMmBasicInfoService;

    /**
     *
     * @param param 旅游活动参团报名人信息
     * @param request
     * @return
     */
    @RequestMapping(value = "/tuxedo.json", method = RequestMethod.POST)
    public String tuxedo(@Valid TravelMemSubmitParam param, HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if(member==null){
            return ApiUtils.error(Xerror.USER_UNLOGIN_JSON_CODE);
        }
        return null;
    }
}
