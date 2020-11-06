package com.framework.loippi.enus;

/**
 * 请求顺丰服务的类别
 */
public final class ShunFengServieCodeEnum {
    // 登录授权
    public final static String EXP_RECE_LOGIN_ORDER = "/openapi/login/v1/login";
    // 下单
    public final static String EXP_RECE_CREATE_ORDER = "/openapi/bsp/v1/orderService";
    // 路由查询
    public final static String EXP_RECE_SEARCH_ROUTES = "/openapi/bsp/v1/routeService";
    // 订单确认/取消
    public final static String EXP_RECE_UPDATE_ORDER = "/openapi/bsp/v1/cancelService";
}
