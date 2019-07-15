package com.framework.loippi.utils;

import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/19.
 */
public class ApiUtils {

    public static final String KEY_CODE = "code";
    public static final String KEY_MSG = "message";
    public static final String KEY_OBJ = "object";
    public static final String KEY_PAGE = "totalPage";
    public static final String DEFAULT_SUCCESS_RESULT_CODE = "1";
    public static final String DEFAULT_FAILURE_RESULT_CODE = "0";
    public static final Map<String, String> MESSAGE = Maps.newHashMap();

    static {
        MESSAGE.put("10000", "非法操作");
        MESSAGE.put("11000", "系统错误");
        MESSAGE.put("1", "操作成功");
        MESSAGE.put("0", "操作失败");
        MESSAGE.put("10003", "参数无效");
        MESSAGE.put("10001", "验证码失效或者有误");
        MESSAGE.put("10002", "此账户不存在");
        MESSAGE.put("20001"," APP暂无更新");
        MESSAGE.put("30001"," 登录密码错误");
        MESSAGE.put("30002"," 该用户被禁用");
        MESSAGE.put("30003"," 验证码错误");
        MESSAGE.put("40001"," 用户未登录代码");
        MESSAGE.put("40002"," 网址URL不存在的代码");
        MESSAGE.put("40003"," 请求方法错误, 需要用POST");
        MESSAGE.put("40004"," 退出登录异常");
        MESSAGE.put("40005"," 登录缓存写入失败");
        MESSAGE.put("50001"," 订单处理失败，请重试");
        MESSAGE.put("10004"," 用户已存在");
        MESSAGE.put("60001"," 库存不足");
        //直播相关错误码7开头
        MESSAGE.put("70001"," 直播结束");
        MESSAGE.put("70002"," 禁止进入直播间");
        MESSAGE.put("70003"," 礼物不存在");
        MESSAGE.put("70004"," 余额不足，请先充值");
        MESSAGE.put("70005"," 积分不足");
        MESSAGE.put("70006"," 已经被禁言");
        MESSAGE.put("70007"," 进入房间密码错误");
        MESSAGE.put("70008"," 不能对自己进行操作!");
        MESSAGE.put("70009"," 不能对自己进行操作!");
        MESSAGE.put("70010"," 你不能拉黑自己");
        MESSAGE.put("70011"," 你不能取消拉黑自己");
        MESSAGE.put("70012"," 没有足够的余额/积分");
        MESSAGE.put("70013"," 礼物不存在");
        MESSAGE.put("70014"," 无权限红包");
        MESSAGE.put("70015"," 该用户已开启陌生人免打扰模式");

    }

    public static String success()
    {
        Map<String, Object> map = new HashMap();
        map.put("code", "1");
        map.put("message", MESSAGE.get("1"));
        return JacksonUtil.toJson(map);
    }

    public static String success(int totalPages, Object object)
    {
        Map<String, Object> map = new HashMap();
        map.put("code", "1");
        map.put("message", MESSAGE.get("1"));
        map.put("object", object);
        map.put("totalPage", Integer.valueOf(totalPages));
        return JacksonUtil.toJson(map);
    }

    public static String success(String code, Object object)
    {
        Map<String, Object> map = new HashMap();
        map.put("code", code);
        map.put("message", MESSAGE.get("1"));
        map.put("object", object);
        return JacksonUtil.toJson(map);
    }

    public static String success(Object object)
    {
        Map<String, Object> map = new HashMap();
        map.put("code", "1");
        map.put("message", MESSAGE.get("1"));
        map.put("object", object);
        return JacksonUtil.toJson(map);
    }

    public static String error()
    {
        Map<String, Object> map = new HashMap();
        map.put("code", "0");
        map.put("message", MESSAGE.get("0"));
        return JacksonUtil.toJson(map);
    }

    public static String error(String code, String message)
    {
        Map<String, Object> map = new HashMap();
        map.put("code", code);
        map.put("message", message);
        return JacksonUtil.toJson(map);
    }
    public static String error(Integer code, String message)
    {
        Map<String, Object> map = new HashMap();
        map.put("code", code);
        map.put("message", message);
        return JacksonUtil.toJson(map);
    }
    public static String error(String message)
    {
        Map<String, Object> map = new HashMap();
        map.put("code", "0");
        map.put("message", message);
        return JacksonUtil.toJson(map);
    }

    public static String error(Integer code)
    {
        Map<String, Object> map = new HashMap();
        map.put("code", code);
        map.put("message", MESSAGE.get(code.toString()));
        return JacksonUtil.toJson(map);
    }

}
