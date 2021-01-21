package com.framework.loippi.interceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.framework.loippi.consts.Constants;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.result.auths.AuthsLoginResult;
import com.framework.loippi.service.RedisService;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.service.user.RdMmRelationService;

public class AuthenticationInterceptor extends HandlerInterceptorAdapter {

    @Resource
    private RedisService redisService;
    @Resource
    private RdMmBasicInfoService rdMmBasicInfoService;
    @Resource
    private RdMmRelationService rdMmRelationService;

    //不需要登录的接口控制
    private Set <Pattern> freeset = new HashSet <>();

    {
        freeset.add(Pattern.compile("/api/auth/*"));
        freeset.add(Pattern.compile("/api/index/*"));
        freeset.add(Pattern.compile("/api/common/*"));
        freeset.add(Pattern.compile("/api/goods/*"));
        freeset.add(Pattern.compile("/api/shopActivity/*"));
        freeset.add(Pattern.compile("/api/article/*"));
        freeset.add(Pattern.compile("/api/user/fixedArticles/*"));
        freeset.add(Pattern.compile("/api/cart/gatherArea/*"));
        freeset.add(Pattern.compile("/api/cart/hotSaleGoods/*"));
    }

    private Set<String> interceptset = new HashSet <>();
    {

        interceptset.add("/api/evaluate/saveReplyEvaluate.json");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String uri = request.getRequestURI();
        // 先檢查是否為不需要簽名的界面或路勁
        for (Pattern pattern : freeset) {
            if (interceptset.contains(uri)) {
                break;
            }
            
            if (pattern.matcher(uri).find()) {
                return true;
            }
        }
        String sessionId = request.getHeader(Constants.USER_SESSION_ID);
        AuthsLoginResult member = null;
        response.setCharacterEncoding("utf8");
        response.setHeader("Content-type", "text/json;charset=UTF-8");
        PrintWriter writer;
        try {
            member = redisService.get(sessionId, AuthsLoginResult.class);
        } catch (Exception e){
            e.printStackTrace();
            writer = response.getWriter();
            writer.write("{\"code\":\"40001\",\"message\":\"用户未登录\"}");
            return false;
        }
        if (member != null) {
            RdMmBasicInfo shopMember = rdMmBasicInfoService.find("mmCode",member.getMmCode());
            if (shopMember == null) {
                redisService.delete(sessionId);
                try {
                    writer = response.getWriter();
                    writer.write("{\"code\":\"40001\",\"message\":\"用户被删除\"}");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
            //一个账号只能在一台设备上登录
            String redisSessionId = redisService.get("user_name"+shopMember.getMmCode(),String.class);
            if(!sessionId.equals(redisSessionId)) {
                writer = response.getWriter();
                writer.write("{\"code\":\"40001\",\"message\":\"用户已在其它设备上登录\"}");
                redisService.delete(sessionId);
                return false;
            }
            RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", member.getMmCode());
            //用户是否被冻结或者未激活
            //1冻结2注销
            if (Optional.ofNullable(rdMmRelation.getMmStatus()).orElse(0) == 1) {
                writer = response.getWriter();
                writer.write("{\"code\":\"40001\",\"message\":\"你的账号已被冻结\"}");
                return false;
            }
            //1冻结2注销
            if (Optional.ofNullable(rdMmRelation.getMmStatus()).orElse(0) == 2) {
                writer = response.getWriter();
                writer.write("{\"code\":\"40001\",\"message\":\"你的账号已被注销\"}");
                return false;
            }
            request.setAttribute(Constants.CURRENT_USER, member);
            return true;
        } else {
            try {
                writer = response.getWriter();
                writer.write("{\"code\":\"40001\",\"message\":\"用户未登录\"}");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
    }
}
