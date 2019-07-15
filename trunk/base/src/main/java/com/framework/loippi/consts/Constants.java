package com.framework.loippi.consts;

import java.util.HashMap;
import java.util.Map;

public final class Constants {

    public final static int NUMBER_PER_PAGE = 10;

    public final static String MSG_URL = "/common/common/show_msg";

    /**
     * 评论图片上传路径
     */
    public final static String RETURN_ORDER_UPLOAD_URL = "/upload/img/returnOrder";

    /**
     * 评论图片上传路径
     */
    public final static String EVALUATE_UPLOAD_URL = "/upload/img/evaluate";

    /**
     * 品牌图片上传路径
     */
    public final static String BRAND_UPLOAD_URL = "/upload/img/brand";

    /**
     * 广告图片上传路径
     */
    public final static String ADV_UPLOAD_URL = "/upload/img/adv";


    /**
     * 商品图片上传路径
     */
    public final static String GOODS_UPLOAD_URL = "/upload/img/store/goods";

    /**
     * 优惠券图片上传路径
     */
    public final static String LOGO_UPLOAD_URL = "/upload/img/store/slide";

    /**
     * 文章图片上传路径
     */
    public final static String ARRTICLE_UPLOAD_URL = "/upload/attach/article";

    /**
     * 品牌图片上传路径
     */
    public final static String LOGO_UPLOAD_VALUE = "0";
    public final static String BANNER_UPLOAD_VALUE = "1";

    /**
     * 导航图片上传路径
     */
    public final static String BANNER_UPLOAD_URL = "/upload/img/banner";


    /**
     * 会员相关上传路径
     */
    public final static String MEMBER_UPLOAD_URL = "/upload/img/avatar";
    /**
     * 店铺图片
     */
    public final static String STORE_UPLOAD_URL = "/upload/img/store/store";

    /**
     * 店铺入驻证件上传路径
     */
    public final static String STOREJOININ_UPLOAD_URL = "/upload/img/storejoinin";

    /**
     * 相册图片
     */
    public final static String IMGALBUM_UPLOAD_URL = "/upload/img/imgAlbum/imgAlbum";

    public final static int FILE_SIZE_IMG = 102400000;

    public static final String USER_SESSION_KEY = "user_session_key";

    public static final String ADMIN_SESSION_KEY = "admin_session_key";

    /**
     * 找回密码IP地址
     */
    public static final String FORGET_IP = "http://localhost:8080/leimingtech-front";

    public static final String CART_KEY = "cart_key";

    /**
     * seller日志EXCEL,导出和导入的路径
     */
    public static final String SELLERLOG_PATH = "/upload/attach/sellerlog";

    /**
     * admin日志EXCEL,导出和导入的路径
     */
    public static final String ADMINLOG_PATH = "/upload/attach/adminlog";

    /**
     * 规格图片上传
     */
    public static final String SPECIMAGE_PATH = "/upload/img/spec";

    /**
     * 会员等级图片上传
     */
    public static final String MEMBER_GRADE_PATH = "/upload/img/membergrade";

    /**
     * 店铺内的商品图片,规格图片,一切关于店铺下的图片的根目录.注意:在这目录后面需要加上店铺id
     */
    public static final String STORE_IMG_PATH = "/upload/img/store";

    /**
     * 水印图片路径
     */
    public static final String MASK_PATH = "/upload/img/water";

    /**
     * 商品搜索索引
     */
    public static final String GOODS_SEARCH_INDEX_PATH = "/index/goods";

    /**
     * 文章搜索索引
     */
    public static final String CONTENT_SEARCH_INDEX_PATH = "/index/content";

    /**
     * 京东联盟商品搜索索引
     */
    public static final String UNION_GOODS_SEARCH_INDEX_PATH = "/index/uniongoods";

    /**
     * 店铺搜索索引
     */
    public static final String STORE_SEARCH_INDEX_PATH = "/index/store";

    /**
     * 报表
     * 店铺销售情况
     */
    public static final String REPORT_STORE = "/store";

    /**
     * 报表
     * 商品情况
     */
    public static final String REPORT_GOODS = "/goods";

    /**
     * 报表
     * 结算情况
     */
    public static final String REPORT_BALANCE = "/balance";

    /**
     * 报表
     * 订单
     */
    public static final String REPORT_ORDER = "/order";
    /**
     * 店铺二维码
     */
    public final static String STORE_TWOCODE_URL = "/upload/img/storetwocode";
    /**
     * 后台报表
     */
    public static final String REPORT_ADMIN = "/admin";
    /**
     * 支付logo
     */
    public final static String STORE_PAYMENT_LOGO = "/upload/img/paymentlogo";

    /**
     * 首页静态化模板
     */
    public final static String INDEX_MODEL = "/index/index.ftl";

    /**
     * 商品详细页静态化模板
     */
    public final static String GOODS_DETAIL_MODEL = "/product/product-detail.ftl";

    /**
     * 静态的主页
     */
    public final static String STATIC_INDEX = "";

    /**
     * 文章分类静态模版页面
     */
    public final static String STATIC_ARTICLE_MODEL = "/help/index.ftl";

    /**
     * 最新文章静态模版页面
     */
    public final static String STATIC_CONTENT_MODEL = "/help/content.ftl";

    /**
     * 静态的商品页面
     */
    public final static String STATIC_GOODS_DETAIL = "/goods";

    /**
     * 文章详情页面
     */
    public final static String STATIC_ARTICLE_DETAIL = "/article";
    /**
     * 文章分类页面
     */
    public final static String STATIC_CONTENT_DETAIL = "/content";

    /**
     * 联盟商品分类图标存放路径
     */
    public final static String UNION_GOODSCLASS_UPLOAD_URL = "/upload/img/union/goodsclass";

    /**
     * 敏感词上传路径
     */
    public final static String SENSITIVE_UPLOAD_URL = "/sensitive";

    /**
     * 平台自营店铺id
     */
    public final static Long PLATFORM_STORE_ID = 0l;

    /**
     * 平台自营店铺id
     */
    public final static String PLATFORM_STORE_NAME = "平台自营店铺";

    /**
     * 积分购物车 key
     */

    public final static String POINTS_CART_KEY = "points_cart_key";

    /**
     * 店铺商品excel
     */
    public final static String STORE_goodsexcel_URL = "/upload/img/goodsexcel/";

    /**
     * 订单excel
     */
    public final static String STORE_orderexcel_URL = "/upload/img/orderexcel/";

    /**
     * 订单销量excel
     */
    public final static String STORE_ordercountexcel_URL = "/upload/img/ordercountexcel/";

    /**
     * 结算账单excel导出路劲
     */
    public final static String STORE_BILL_URL = "/upload/billexcel/";

    /**
     * 收款单excel导出路劲
     */
    public final static String ORDER_EXCEL_FINISHED_URL = "/upload/order/finished/";

    /**
     * 退款单excel导出路径
     */
    public final static String ORDER_EXCEL_REFUND_URL = "/upload/order/refund/";

    /**
     * 发货单excel导出路径
     */
    public final static String ORDER_EXCEL_SHIPPED_URL = "/upload/order/shipped/";


    /**
     * 店铺excel导出路径
     */
    public static final String STORE_EXCEL_URL = "/storeExcel";

    /**
     * 商品列表excel导出路径
     */
    public static final String GOODS_COMMON_EXCEL_URL = "/goodsExcel";

    /**
     * 入驻店铺excel导出路径
     */
    public static final String STORE_JOININ_EXCEL_URL = "/storeJoininExcel";

    public static Map<String, String> shippingNameMap = new HashMap<String, String>();

    static {
        shippingNameMap.put("zt", "自提");
        shippingNameMap.put("py", "平邮");
        shippingNameMap.put("kd", "快递");
        shippingNameMap.put("es", "EMS");
    }

    /**
     * 平台LOGO
     */
    public final static String SITE_LOGO_URL = "/upload/logo";

    /**
     * 平台文章标识
     */
    public final static String platFormMark = "plat";
    public final static String platFormName = "系统平台";
    /**
     * 文章状态
     */
    public final static Map<String, String> articleContans = new HashMap<String, String>();

    static {
        articleContans.put("draft", "10");//草稿状态
        articleContans.put("approval", "20");//待审核状态
        articleContans.put("release", "30");//已发布状态
        articleContans.put("Offline", "40");//下线状态
        articleContans.put("false", "50");//未通过审核
    }

    public static final String X_AUTH_MODE = "client_auth";
    public static final String UPLOAD_MODE = "pic";
    public static final String JPUSH_USERID = "userId_";
    public static final String USER_SESSION_ID = "sessionId";
    public static final int USER_SESSION_TIMEOUT = 60 * 60 * 24 * 7;
    public static final String CURRENT_USER = "currentUser";
//    public static final String JPUSH_LIVE_ROOMID_ = "JPUSH_live_roomId_";
    /**
     * 发送主播红包
     **/
    public static final String JPUSH_live_anchorId_ = "live_anchorId_";
    public static final String JPUSH_LIVE_ROOMID_ = "live_roomId_";
    /**
     * 发送群红包
     **/
    public static final String JPUSH_live_roomId_ = "live_roomId_";

    /**
     * 红包
     **/
    public static final String hongbaolist = "_hongbaolist";

    /**
     * 已领红包人
     **/
    public static final String hongBaoConsumedList = "_hongBaoConsumedList";

    /**
     * 过滤已领红包人
     **/
    public static final String hongBaoConsumedMap = "_hongBaoConsumedMap";


    /**
     * 红包已经领完了
     **/
    public static final String hongBaoOut = "_hongBaoOut";

    /**
     * 可以领红包人
     **/
    public static final String userMap = "_hongBaoUserMap";

    /**
     * 会员操作
     */
    public static final int OPERATOR_MEMBER = 1;

    /**
     * 管理员操作
     */
    public static final int OPERATOR_ADMINISTRATOR = 2;

    /**
     * 商家操作
     */
    public static final int OPERATOR_SELLER = 3;

    /**
     * 系统定时任务
     */
    public static final int OPERATOR_TIME_TASK = 4;

    public final static Long PLATFORM_STORE_ID_MUMBER = new Long(0);
    
}
