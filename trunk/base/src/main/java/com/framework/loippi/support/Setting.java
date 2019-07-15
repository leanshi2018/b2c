package com.framework.loippi.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 系统设置
 *
 * @author Mounate Yan。
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Setting implements Serializable {

    private static final long serialVersionUID = -1478999889661796840L;

    /**
     * 小数位精确方式
     */
    public enum RoundType {

        /**
         * 四舍五入
         */
        roundHalfUp,

        /**
         * 向上取整
         */
        roundUp,

        /**
         * 向下取整
         */
        roundDown
    }


    /**
     * 验证码类型
     */
    public enum CaptchaType {

        /**
         * 后台登录
         */
        adminLogin,

        /**
         * 其它
         */
        other
    }

    /**
     * 账号锁定类型
     */
    public enum AccountLockType {

        /**
         * 管理员
         */
        admin
    }


    /**
     * 缓存名称
     */
    public static final String CACHE_NAME = "setting";

    /**
     * 缓存Key
     */
    public static final Integer CACHE_KEY = 0;

    /**
     * 分隔符
     */
    private static final String SEPARATOR = ",";


    /**
     * 网站名称
     */
    private String siteName;

    /**
     * 网站网址
     */
    private String siteUrl;

    /**
     * logo
     */
    private String logo;

    /**
     * 联系人
     */
    private String contact;

    /**
     * 联系地址
     */
    private String address;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * E-mail
     */
    private String email;

    /**
     * 备案编号
     */
    private String certtext;

    /**
     * 连续登录失败最大次数
     */
    private Integer accountLockCount;

    /**
     * 自动解锁时间
     */
    private Integer accountLockTime;

    /**
     * 上传文件最大限制
     */
    private Integer uploadMaxSize;

    /**
     * 允许上传图片扩展名
     */
    private String uploadImageExtension;

    /**
     * 允许上传Flash扩展名
     */
    private String uploadFlashExtension;

    /**
     * 允许上传媒体扩展名
     */
    private String uploadMediaExtension;

    /**
     * 允许上传文件扩展名
     */
    private String uploadFileExtension;

    /**
     * 图片上传路径
     */
    private String imageUploadPath;

    /**
     * Flash上传路径
     */
    private String flashUploadPath;

    /**
     * 媒体上传路径
     */
    private String mediaUploadPath;

    /**
     * 文件上传路径
     */
    private String fileUploadPath;

    /**
     * 货币符号
     */
    private String currencySign;

    /**
     * 货币单位
     */
    private String currencyUnit;

    /**
     * 发件人邮箱
     */
    private String smtpFromMail;

    /**
     * SMTP服务器地址
     */
    private String smtpHost;

    /**
     * SMTP服务器端口
     */
    private Integer smtpPort;

    /**
     * SMTP用户名
     */
    private String smtpUsername;

    /**
     * SMTP密码
     */
    private String smtpPassword;

    /**
     * Cookie路径
     */
    private String cookiePath;

    /**
     * Cookie作用域
     */
    private String cookieDomain;

    /**
     * url前缀
     */
    private String urlPrefix;

    /**
     * 是否开启开发模式
     */
    private Boolean isDevelopmentEnabled = false;

    /**
     * 等级一最高可贷额度
     */
    private Integer loanLevel1MaxQuota = 0;

    /**
     * 等级二最高可贷额度
     */
    private Integer loanLevel2MaxQuota = 0;

    /**
     * 等级三最高可贷额度
     */
    private Integer loanLevel3MaxQuota = 0;

    /**
     * 等级四最高可贷额度
     */
    private Integer loanLevel4MaxQuota = 0;

    /**
     * 等级五最高可贷额度
     */
    private Integer loanLevel5MaxQuota = 0;

    /**
     * 手续费
     */
    private Double loanPoundage = 0d;

    /**
     * 服务费
     */
    private Double loanServiceFee = 0d;

    /**
     * 日利率
     */
    private Double loanInterestRate = 0d;

    /**
     * 贷款期限（月）
     */
    private Integer loanTerm = 0;

    /**
     * 查询提示语
     */
    private String searchToast = "";

    /**
     * 首页商品数
     */
    private String indexNumber = "";

    /**
     * 设置网站网址
     *
     * @param siteUrl 网站网址
     */
    public void setSiteUrl(String siteUrl) {
        this.siteUrl = StringUtils.removeEnd(siteUrl, "/");
    }


    /**
     * 设置允许上传图片扩展名
     *
     * @param uploadImageExtension 允许上传图片扩展名
     */
    public void setUploadImageExtension(String uploadImageExtension) {
        if (uploadImageExtension != null) {
            uploadImageExtension = uploadImageExtension.replaceAll("[,\\s]*,[,\\s]*", ",").replaceAll("^,|,$", "").toLowerCase();
        }
        this.uploadImageExtension = uploadImageExtension;
    }

    /**
     * 设置允许上传Flash扩展名
     *
     * @param uploadFlashExtension 允许上传Flash扩展名
     */
    public void setUploadFlashExtension(String uploadFlashExtension) {
        if (uploadFlashExtension != null) {
            uploadFlashExtension = uploadFlashExtension.replaceAll("[,\\s]*,[,\\s]*", ",").replaceAll("^,|,$", "").toLowerCase();
        }
        this.uploadFlashExtension = uploadFlashExtension;
    }

    /**
     * 设置允许上传媒体扩展名
     *
     * @param uploadMediaExtension 允许上传媒体扩展名
     */
    public void setUploadMediaExtension(String uploadMediaExtension) {
        if (uploadMediaExtension != null) {
            uploadMediaExtension = uploadMediaExtension.replaceAll("[,\\s]*,[,\\s]*", ",").replaceAll("^,|,$", "").toLowerCase();
        }
        this.uploadMediaExtension = uploadMediaExtension;
    }

    /**
     * 设置允许上传文件扩展名
     *
     * @param uploadFileExtension 允许上传文件扩展名
     */
    public void setUploadFileExtension(String uploadFileExtension) {
        if (uploadFileExtension != null) {
            uploadFileExtension = uploadFileExtension.replaceAll("[,\\s]*,[,\\s]*", ",").replaceAll("^,|,$", "").toLowerCase();
        }
        this.uploadFileExtension = uploadFileExtension;
    }

    /**
     * 设置图片上传路径
     *
     * @param imageUploadPath 图片上传路径
     */
    public void setImageUploadPath(String imageUploadPath) {
        if (imageUploadPath != null) {
            if (!imageUploadPath.startsWith("/")) {
                imageUploadPath = "/" + imageUploadPath;
            }
            if (!imageUploadPath.endsWith("/")) {
                imageUploadPath += "/";
            }
        }
        this.imageUploadPath = imageUploadPath;
    }


    /**
     * 设置Flash上传路径
     *
     * @param flashUploadPath Flash上传路径
     */
    public void setFlashUploadPath(String flashUploadPath) {
        if (flashUploadPath != null) {
            if (!flashUploadPath.startsWith("/")) {
                flashUploadPath = "/" + flashUploadPath;
            }
            if (!flashUploadPath.endsWith("/")) {
                flashUploadPath += "/";
            }
        }
        this.flashUploadPath = flashUploadPath;
    }


    /**
     * 设置媒体上传路径
     *
     * @param mediaUploadPath 媒体上传路径
     */
    public void setMediaUploadPath(String mediaUploadPath) {
        if (mediaUploadPath != null) {
            if (!mediaUploadPath.startsWith("/")) {
                mediaUploadPath = "/" + mediaUploadPath;
            }
            if (!mediaUploadPath.endsWith("/")) {
                mediaUploadPath += "/";
            }
        }
        this.mediaUploadPath = mediaUploadPath;
    }

    /**
     * 设置文件上传路径
     *
     * @param fileUploadPath 文件上传路径
     */
    public void setFileUploadPath(String fileUploadPath) {
        if (fileUploadPath != null) {
            if (!fileUploadPath.startsWith("/")) {
                fileUploadPath = "/" + fileUploadPath;
            }
            if (!fileUploadPath.endsWith("/")) {
                fileUploadPath += "/";
            }
        }
        this.fileUploadPath = fileUploadPath;
    }

    /**
     * 设置Cookie路径
     *
     * @param cookiePath Cookie路径
     */
    public void setCookiePath(String cookiePath) {
        if (cookiePath != null && !cookiePath.endsWith("/")) {
            cookiePath += "/";
        }
        this.cookiePath = cookiePath;
    }

    /**
     * 获取允许上传图片扩展名
     *
     * @return 允许上传图片扩展名
     */
    public String[] getUploadImageExtensions() {
        return StringUtils.split(uploadImageExtension, SEPARATOR);
    }

    /**
     * 获取允许上传Flash扩展名
     *
     * @return 允许上传Flash扩展名
     */
    public String[] getUploadFlashExtensions() {
        return StringUtils.split(uploadFlashExtension, SEPARATOR);
    }

    /**
     * 获取允许上传媒体扩展名
     *
     * @return 允许上传媒体扩展名
     */
    public String[] getUploadMediaExtensions() {
        return StringUtils.split(uploadMediaExtension, SEPARATOR);
    }

    /**
     * 获取允许上传文件扩展名
     *
     * @return 允许上传文件扩展名
     */
    public String[] getUploadFileExtensions() {
        return StringUtils.split(uploadFileExtension, SEPARATOR);
    }

    /**
     * 获取是否开启开发模式
     *
     * @return 是否开启开发模式
     */
    public Boolean getIsDevelopmentEnabled() {
        return isDevelopmentEnabled;
    }

    /**
     * 设置是否开启开发模式
     *
     * @param isDevelopmentEnabled 是否开启开发模式
     */
    public void setIsDevelopmentEnabled(Boolean isDevelopmentEnabled) {
        this.isDevelopmentEnabled = isDevelopmentEnabled;
    }

    private Double alipayOnline;
    private Double alipayOffline;

    private Double weixinpayOnline;
    private Double weixinpayOffline;

    private Double unionpayOnline;
    private Double unionpayOffline;

    private Double chargeRate;
    private Double maxChargeMoney;
    private Double transferAmount;

    private String option1;
    private String option2;

    private String redisServerIp;
    private Integer redisPort;
    private String auth;

    private Double floatMoneyRate;

    private Double evaluationScore;


    private String lotteryBanner;
    private Integer lotteryCount;

    private BigDecimal integralExchangeRate;
    private Integer openGroupCount;
}