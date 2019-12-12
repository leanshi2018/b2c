package com.framework.loippi.controller.common;


import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.framework.loippi.controller.BaseController;
import com.framework.loippi.entity.common.ShopCommonArea;
import com.framework.loippi.entity.common.ShopCommonArticle;
import com.framework.loippi.entity.common.ShopCommonDocument;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.enus.VerifyCodeType;
import com.framework.loippi.result.adv.ArticleInfo;
import com.framework.loippi.result.auths.AuthsLoginResult;
import com.framework.loippi.service.QiniuService;
import com.framework.loippi.service.RedisService;
import com.framework.loippi.service.common.ShopCommonAreaService;
import com.framework.loippi.service.common.ShopCommonArticleService;
import com.framework.loippi.service.common.ShopCommonDocumentService;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.ApiUtils;
import com.framework.loippi.utils.JacksonUtil;
import com.framework.loippi.utils.StringUtil;
import com.framework.loippi.utils.Xerror;
import com.framework.loippi.utils.sms.AldayuUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


/**
 * Controller - 通用
 *
 * @author Loippi Team
 * @version 1.0
 */

@Controller
public class CommonController extends BaseController {

    @Resource
    private ShopCommonAreaService shopCommonAreaService;
    @Resource
    private RdMmBasicInfoService rdMmBasicInfoService;
    @Autowired
    private ShopCommonArticleService shopCommonArticleService;
    @Autowired
    private RedisService redisService;
    //    @Resource
//    private Producer captchaProducer;
    @Autowired
    private ShopCommonDocumentService documentService;
    @Autowired
    private QiniuService qiniuService;

    /**
	 * 注册过的手机才经过这个接口
     * 发送短信验证码： 向第三方发送短信验证码  Integer msgType:0-注册1-忘记密码2-验证手机号3-修改手机号4-pc端登录
     */
    @RequestMapping(value = "/api/common/msg.json", method = {RequestMethod.POST})
    public
    @ResponseBody
    String msg(@RequestParam(value = "mobile") String mobile, @RequestParam(value = "msgType") Integer msgType,
        HttpServletRequest request) {
        if (StringUtils.isEmpty(mobile)) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        if (!StringUtil.isMobilePhone(mobile)) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        if (msgType == null) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }

        // 添加手机号相关业务逻辑判断
        Map<String, Object> params = new HashMap<>();
        params.put("mobile", mobile);
        List<RdMmBasicInfo> account = rdMmBasicInfoService.findList(params);
        if (account.size()==0){
			return ApiUtils.error("不存在该账号");
		}

        if (msgType == VerifyCodeType.REGISTER.code) {
            if (account.size() > 0) {
                return ApiUtils.error("该手机已被注册");
            }
        } else if (msgType == VerifyCodeType.RESETPWD.code) {
            if (account.size() == 0) {
                return ApiUtils.error("不存在该账号");
            }
        } else if (msgType == VerifyCodeType.VERIFY_MOBILE.code) {
            if (account.size() > 0) {
                String sessionId = request.getHeader(com.framework.loippi.consts.Constants.USER_SESSION_ID);
                AuthsLoginResult session = redisService.get(sessionId, AuthsLoginResult.class);
                if (session == null) {
                    return ApiUtils.error("会话失效，请重新登录");
                }
                if (!session.getMobile().equals(mobile)) {
                    return ApiUtils.error("手机号不正确");
                }
            }
        } else if (msgType == VerifyCodeType.RESET_MOBILE.code) {
            if (account.size() > 0) {
                return ApiUtils.error("该手机号已被使用");
            }
        } else if (msgType == VerifyCodeType.BankCards_MOBILE.code) {
            //银行卡预留手机发送验证码
        } else {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }

        String code = RandomStringUtils.random(6, "0123456789");
        try {
            String codeJson = "{\"code\":\"" + code + "\"}";
            AldayuUtil.sendSms(mobile, codeJson, "SMS_165115421", "蜗米商城");
            redisService.save(mobile,code);
            return ApiUtils.success();
        } catch (Exception ex) {
            ex.printStackTrace();
            return ApiUtils.error("发送验证码出错");
        }
        //return ApiUtils.success();
    }

    /**
	 * 未注册过的手机才经过这个接口
     * 注册发送短信验证码： 向第三方发送短信验证码  Integer msgType:0-注册1-忘记密码2-验证手机号3-修改手机号4-pc端登录
     */
    @RequestMapping(value = "/api/common/zcmsg.json", method = {RequestMethod.POST})
    public
    @ResponseBody
    String zcmsg(@RequestParam(value = "mobile") String mobile, @RequestParam(value = "msgType") Integer msgType ,
                 @RequestParam(value = "countNum") String countNum ,HttpServletRequest request) {
        if (StringUtils.isEmpty(mobile)) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        if (!StringUtil.isMobilePhone(mobile)) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        if (msgType == null) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        if (StringUtils.isEmpty(countNum)) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        String redisNum = redisService.get("A" + mobile);
        if (!redisNum.equals(countNum)){
            return ApiUtils.error(Xerror.LOGIN_VALIDCODE_ERROR);
        }
        redisService.delete("A" + mobile);


        // 添加手机号相关业务逻辑判断
        Map<String, Object> params = new HashMap<>();
        params.put("mobile", mobile);
        List<RdMmBasicInfo> account = rdMmBasicInfoService.findList(params);

        if (msgType == VerifyCodeType.REGISTER.code) {
            if (account.size() > 0) {
                return ApiUtils.error("该手机已被注册");
            }
        } else if (msgType == VerifyCodeType.RESETPWD.code) {
            if (account.size() == 0) {
                return ApiUtils.error("不存在该账号");
            }
        } else if (msgType == VerifyCodeType.VERIFY_MOBILE.code) {
            if (account.size() > 0) {
                String sessionId = request.getHeader(com.framework.loippi.consts.Constants.USER_SESSION_ID);
                AuthsLoginResult session = redisService.get(sessionId, AuthsLoginResult.class);
                if (session == null) {
                    return ApiUtils.error("会话失效，请重新登录");
                }
                if (!session.getMobile().equals(mobile)) {
                    return ApiUtils.error("手机号不正确");
                }
            }
        } else if (msgType == VerifyCodeType.RESET_MOBILE.code) {
            if (account.size() > 0) {
                return ApiUtils.error("该手机号已被使用");
            }
        } else if (msgType == VerifyCodeType.BankCards_MOBILE.code) {
            //银行卡预留手机发送验证码
        } else {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }

        String code = RandomStringUtils.random(6, "0123456789");
        try {
            String codeJson = "{\"code\":\"" + code + "\"}";
            AldayuUtil.sendSms(mobile, codeJson, "SMS_165115421", "蜗米商城");
            redisService.save(mobile,code);
            return ApiUtils.success();
        } catch (Exception ex) {
            ex.printStackTrace();
            return ApiUtils.error("发送验证码出错");
        }
        //return ApiUtils.success();
    }

    /**
     * 区域
     */

    @RequestMapping(value = "/api/common/area.json", method = {RequestMethod.POST})
    public
    @ResponseBody
    String area() {
        List<ShopCommonArea> province = shopCommonAreaService.findList("areaParentId", 0);
        List<Map<String, Object>> res_pro = Lists.newArrayList();
        for (ShopCommonArea pro : province) {
            Map<String, Object> proMap = Maps.newLinkedHashMap();
            proMap.put("id", pro.getId());
            proMap.put("pid", pro.getAreaParentId());
            proMap.put("name", pro.getAreaName());
            List<ShopCommonArea> citys = shopCommonAreaService.findList("areaParentId", pro.getId());
            List<Map<String, Object>> res_city = Lists.newArrayList();
            for (ShopCommonArea city : citys) {
                Map<String, Object> cityMap = Maps.newLinkedHashMap();
                cityMap.put("id", city.getId());
                cityMap.put("pid", city.getAreaParentId());
                cityMap.put("name", city.getAreaName());
                List<ShopCommonArea> regions = shopCommonAreaService.findList("areaParentId", city.getId());
                List<Map<String, Object>> res_region = Lists.newArrayList();
                for (ShopCommonArea region : regions) {
                    if (!"市辖区".equals(region.getAreaName())) {
                        Map<String, Object> regionMap = Maps.newLinkedHashMap();
                        regionMap.put("id", region.getId());
                        regionMap.put("pid", region.getAreaParentId());
                        regionMap.put("name", region.getAreaName());
                        res_region.add(regionMap);
                    }
                }
                cityMap.put("child", res_region);
                res_city.add(cityMap);
            }
            proMap.put("child", res_city);
            res_pro.add(proMap);
        }
        Map result = Maps.newLinkedHashMap();
        result.put("area", res_pro);
        String str = JacksonUtil.toJson(result);
        try {
            FileOutputStream out = new FileOutputStream(new File("E:\\area.json"));
            out.write(str.getBytes("utf-8"));
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiUtils.success(res_pro);
    }

    /**
     * 帮助列表
     */
    @RequestMapping(value = "/api/common/listHelp", method = RequestMethod.POST)
    @ResponseBody
    public String listHelp(HttpServletRequest request,
        @RequestParam(defaultValue = "0") Integer pageNumber) {
        Pageable pageable = new Pageable(pageNumber, 20);
        Page<ShopCommonArticle> result = shopCommonArticleService.findByPage(pageable);
        return ApiUtils.success(ArticleInfo.forList(result.getContent()));
    }

//    /**
//     * 验证码
//     */
//    @RequestMapping(value = "/api/common/captcha", method = RequestMethod.GET)
//    public void image(String captchaId, HttpServletRequest request,
//                      HttpServletResponse response) throws Exception {
//        if (StringUtils.isEmpty(captchaId)) {
//            return;
//        }
//        response.setDateHeader("Expires", 0);
//        response.setHeader("Cache-Control", "no-cache, must-revalidate");
//        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
//        response.setHeader("Pragma", "no-cache");
//        response.setContentType("image/jpeg");
//
//        String capText = captchaProducer.createText();// 生成验证码字符串
//        redisService.save(captchaId, capText);
//        System.err.println("capText===" + capText);
//
//        BufferedImage bi = captchaProducer.createImage(capText);// 生成验证码图片
//        ServletOutputStream out = response.getOutputStream();
//        ImageIO.write(bi, "jpg", out);
//        try {
//            out.flush();
//        } finally {
//            out.close();
//        }
//        return;
//    }

    /**
     * 上传图片文件
     */
    @ResponseBody
    @RequestMapping(value = "/api/common/upload", method = RequestMethod.POST)
    public String Upload(HttpServletRequest request) {
        //可以在上传文件的同时接收其它参数
        MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
        MultipartFile myfiles = mRequest.getFile("file");
        Map<String, Object> map = Maps.newConcurrentMap();
        String link = qiniuService.upload(myfiles);
        map.put("result", link);
        return ApiUtils.success(map);
    }

    /**
     * 静态页面
     */
    @RequestMapping(value = "/api/index/document.json", method = RequestMethod.POST)
    @ResponseBody
    public String shareDocument() {
        StringBuffer shareUrl = new StringBuffer();
        shareUrl.append(wapServer);
        shareUrl.append("/wap/setting/aboutInfo.html?type=app&id=");
        String s = shareUrl.toString();
        ShopCommonDocument document;
        Map<String, Object> map = new HashMap<>();
        document = documentService.find("docTitle", "交易协议");
        map.put("trade", s + document.getId());
        document = documentService.find("docTitle", "实名认证");
        map.put("certification", s + document.getId());
        document = documentService.find("docTitle", "推荐好友分享页");
        map.put("friendsShare", s + document.getId());
//        document = documentService.find("docTitle", "常见问题页");
//        map.put("question", s + document.getId());
//        document = documentService.find("docTitle", "关于我们");
//        map.put("aboutOur", s + document.getId());
//        document = documentService.find("docTitle", "商务合作");
//        map.put("businessCooperation", s + document.getId());
//        document = documentService.find("docTitle", "钱包说明");
//        map.put("wallet", s + document.getId());
//        document = documentService.find("docTitle", "优惠券使用说明");
//        map.put("coupon", s + document.getId());
//        document = documentService.find("docTitle", "照片示例");
//        map.put("realNameImg", s + document.getId());
        document = documentService.find("docTitle", "注册协议");
        map.put("register", s + document.getId());
        return ApiUtils.success(map);
    }

    /**
     * 分享APP链接 1为ios ,2为android
     */
    @RequestMapping(value = "/api/index/shareUrl.json", method = RequestMethod.POST)
    @ResponseBody
    public String shareUrl() {
        Map<String, Object> result = new HashMap<>();
        result.put("ios", "https://itunes.apple.com/cn/app/%E7%8E%8B%E8%80%85%E8%8D%A3%E8%80%80/id989673964?mt=8");
        result.put("android", "https://itunes.apple.com/cn/app/%E7%8E%8B%E8%80%85%E8%8D%A3%E8%80%80/id989673964?mt=8");
        return ApiUtils.success(result);
    }

}

