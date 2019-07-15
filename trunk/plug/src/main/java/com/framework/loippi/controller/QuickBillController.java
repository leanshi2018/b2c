package com.framework.loippi.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequestMapping("/billpayment")
public class QuickBillController {

    /**
     * 跳转到快钱商户支付结果页面
     *
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/paybillfront")
    public String paybillfront(HttpServletRequest request, Model model) {
        int status = 0;//0失败 ，1 成功
        log.debug("===============payfront================");
        String rebackurl = "";
        String msg = "";
        String billmsg = request.getParameter("msg");
        String orderId = request.getParameter("orderId").trim();
        if (billmsg.equals("success!") && !billmsg.equals("")) {
            if (!orderId.equals("") && orderId.contains("R")) {
                status = 1;
                rebackurl = "/cart/topup_result";
                msg = "恭喜您，充值成功！";
            } else {
                status = 1;
                rebackurl = "/cart/pay_result";
                msg = "恭喜您，支付成功！";
            }
        }
        //这里是你的跳转页面
        model.addAttribute("status", status);
        model.addAttribute("msg", msg);
        return rebackurl;
    }

}
