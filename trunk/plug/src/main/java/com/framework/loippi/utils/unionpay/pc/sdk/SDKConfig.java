package com.framework.loippi.utils.unionpay.pc.sdk;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.List;
import java.util.Properties;

/**
 * 软件开发工具包 配制
 *
 * @author xuyaowen
 */
public class SDKConfig {

    public static final String FILE_NAME = "acp_sdk.properties";

    /**
     * 前台请求URL.
     */
    private String frontRequestUrl;
    /**
     * 后台请求URL.
     */
    private String backRequestUrl;
    /**
     * 单笔查询
     */
    private String singleQueryUrl;
    /**
     * 批量查询
     */
    private String batchQueryUrl;
    /**
     * 批量交易
     */
    private String batchTransUrl;
    /**
     * 文件传输
     */
    private String fileTransUrl;
    /**
     * 签名证书路径.
     */
    private InputStream signCertPath;
    /**
     * 签名证书密码.
     */
    private String signCertPwd;
    /**
     * 签名证书类型.
     */
    private String signCertType;
    /**
     * 加密公钥证书路径.
     */
    private String encryptCertPath;
    /**
     * 验证签名公钥证书目录.
     */
    private String validateCertDir = "";


    //公钥名称
    private String publiccertNames;
    /**
     * 按照商户代码读取指定签名证书目录.
     */
    private String signCertDir;
    /**
     * 磁道加密证书路径.
     */
    private String encryptTrackCertPath;
    /**
     * 有卡交易.
     */
    private String cardRequestUrl;
    /**
     * app交易
     */
    private String appRequestUrl;
    /**
     * 证书使用模式(单证书/多证书)
     */
    private String singleMode;


    /**
     * 配置文件中的前台URL常量.
     */
    public static final String publiccert = "publiccert.names";
    /**
     * 配置文件中的前台URL常量.
     */
    public static final String SDK_FRONT_URL = "acpsdk.frontTransUrl";
    /**
     * 配置文件中的后台URL常量.
     */
    public static final String SDK_BACK_URL = "acpsdk.backTransUrl";
    /**
     * 配置文件中的单笔交易查询URL常量.
     */
    public static final String SDK_SIGNQ_URL = "acpsdk.singleQueryUrl";
    /**
     * 配置文件中的批量交易查询URL常量.
     */
    public static final String SDK_BATQ_URL = "acpsdk.batchQueryUrl";
    /**
     * 配置文件中的批量交易URL常量.
     */
    public static final String SDK_BATTRANS_URL = "acpsdk.batchTransUrl";
    /**
     * 配置文件中的文件类交易URL常量.
     */
    public static final String SDK_FILETRANS_URL = "acpsdk.fileTransUrl";
    /**
     * 配置文件中的有卡交易URL常量.
     */
    public static final String SDK_CARD_URL = "acpsdk.cardTransUrl";
    /**
     * 配置文件中的app交易URL常量.
     */
    public static final String SDK_APP_URL = "acpsdk.appTransUrl";

    /**
     * 配置文件中签名证书路径常量.
     */
    public static final String SDK_SIGNCERT_PATH = "acpsdk.signCert.path";
    /**
     * 配置文件中签名证书密码常量.
     */
    public static final String SDK_SIGNCERT_PWD = "acpsdk.signCert.pwd";
    /**
     * 配置文件中签名证书类型常量.
     */
    public static final String SDK_SIGNCERT_TYPE = "acpsdk.signCert.type";
    /**
     * 配置文件中密码加密证书路径常量.
     */
    public static final String SDK_ENCRYPTCERT_PATH = "acpsdk.encryptCert.path";
    /**
     * 配置文件中磁道加密证书路径常量.
     */
    public static final String SDK_ENCRYPTTRACKCERT_PATH = "acpsdk.encryptTrackCert.path";
    /**
     * 配置文件中验证签名证书目录常量.
     */
    public static final String SDK_VALIDATECERT_DIR = "acpsdk.validateCert.dir";

    /**
     * 配置文件中是否加密cvn2常量.
     */
    public static final String SDK_CVN_ENC = "acpsdk.cvn2.enc";
    /**
     * 配置文件中是否加密cvn2有效期常量.
     */
    public static final String SDK_DATE_ENC = "acpsdk.date.enc";
    /**
     * 配置文件中是否加密卡号常量.
     */
    public static final String SDK_PAN_ENC = "acpsdk.pan.enc";
    /**
     * 配置文件中证书使用模式
     */
    public static final String SDK_SINGLEMODE = "acpsdk.singleMode";
    /**
     * 操作对象.
     */
    private static SDKConfig config;
    /**
     * 属性文件对象.
     */
    private Properties properties;

    public List<InputStream> getPublicCert() {
        if (SDKUtil.isEmpty(publiccertNames)) {
            LogUtil.writeLog("ERROR: publicCertNames is empty");
            return null;
        }
        String[] publicCertName = publiccertNames.split(",");
        List<InputStream> publicCertList = Lists.newArrayList();
        InputStream in = null;
        for (String publicName : publicCertName) {
            try {
                in = this.getClass().getResourceAsStream("unionpay/unionpayprokey/" + publicName + ".cer");
                if (in == null) continue;
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.writeLog("ERROR:" + publicName + "is not exists");
                continue;
            }
            publicCertList.add(in);
        }
        return publicCertList;
    }

    /**
     * 获取config对象.
     *
     * @return
     */
    public static SDKConfig getConfig() {
        if (null == config) {
            config = new SDKConfig();
        }
        return config;
    }

    /**
     * 从properties文件加载
     *
     * @param rootPath 不包含文件名的目录.
     */
    public void loadPropertiesFromPath(String rootPath) {
        if (StringUtils.isNotBlank(rootPath)) {
            File file = new File(rootPath + File.separator + FILE_NAME);
            InputStream in = null;
            if (file.exists()) {
                try {
                    in = new FileInputStream(file);
                    BufferedReader bf = new BufferedReader(
                            new InputStreamReader(in, "utf-8"));
                    properties = new Properties();
                    properties.load(bf);
                    loadProperties(properties);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (null != in) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                // 由于此时可能还没有完成LOG的加载，因此采用标准输出来打印日志信息
                System.out.println(rootPath + FILE_NAME + "不存在,加载参数失败");
            }
        } else {
            loadPropertiesFromSrc();
        }

    }

    /**
     * 从classpath路径下加载配置参数
     */
    public void loadPropertiesFromSrc() {
        InputStream in = null;
        try {
            // Properties pro = null;
            in = SDKConfig.class.getClassLoader()
                    .getResourceAsStream(FILE_NAME);
            if (null != in) {
                BufferedReader bf = new BufferedReader(new InputStreamReader(
                        in, "utf-8"));
                properties = new Properties();
                try {
                    properties.load(bf);
                } catch (IOException e) {
                    throw e;
                }
            } else {
                LogUtil.writeErrorLog(FILE_NAME + "文件不存在!");
                return;
            }
            loadProperties(properties);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 根据传入的 {@link #(Properties)}对象设置配置参数
     *
     * @param propertiesLoader
     */
    public void loadProperties(Properties propertiesLoader) {
        String value = null;
        value = propertiesLoader.getProperty(SDK_SINGLEMODE);
        if (SDKUtil.isEmpty(value) || SDKConstants.TRUE_STRING.equals(value)) {
            this.singleMode = SDKConstants.TRUE_STRING;
            LogUtil.writeLog("SingleCertMode:[" + this.singleMode + "]");
            // 单证书模式
            value = propertiesLoader.getProperty(SDK_SIGNCERT_PATH);
            if (!SDKUtil.isEmpty(value)) {
                try {
                    this.signCertPath = new FileInputStream(value);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            value = propertiesLoader.getProperty(SDK_SIGNCERT_PWD);
            if (!SDKUtil.isEmpty(value)) {
                this.signCertPwd = value.trim();
            }
            value = propertiesLoader.getProperty(SDK_SIGNCERT_TYPE);
            if (!SDKUtil.isEmpty(value)) {
                this.signCertType = value.trim();
            }
        } else {
            // 多证书模式
            this.singleMode = SDKConstants.FALSE_STRING;
            LogUtil.writeLog("SingleMode:[" + this.singleMode + "]");
        }
        value = propertiesLoader.getProperty(SDK_ENCRYPTCERT_PATH);
        if (!SDKUtil.isEmpty(value)) {
            this.encryptCertPath = value.trim();
        }
        value = propertiesLoader.getProperty(SDK_VALIDATECERT_DIR);
        if (!SDKUtil.isEmpty(value)) {
            this.validateCertDir = value.trim();
        }
        value = propertiesLoader.getProperty(SDK_FRONT_URL);
        if (!SDKUtil.isEmpty(value)) {
            this.frontRequestUrl = value.trim();
        }
        value = propertiesLoader.getProperty(SDK_BACK_URL);
        if (!SDKUtil.isEmpty(value)) {
            this.backRequestUrl = value.trim();
        }
        value = propertiesLoader.getProperty(SDK_BATQ_URL);
        if (!SDKUtil.isEmpty(value)) {
            this.batchQueryUrl = value.trim();
        }
        value = propertiesLoader.getProperty(SDK_BATTRANS_URL);
        if (!SDKUtil.isEmpty(value)) {
            this.batchTransUrl = value.trim();
        }
        value = propertiesLoader.getProperty(SDK_FILETRANS_URL);
        if (!SDKUtil.isEmpty(value)) {
            this.fileTransUrl = value.trim();
        }
        value = propertiesLoader.getProperty(SDK_SIGNQ_URL);
        if (!SDKUtil.isEmpty(value)) {
            this.singleQueryUrl = value.trim();
        }
        value = propertiesLoader.getProperty(SDK_CARD_URL);
        if (!SDKUtil.isEmpty(value)) {
            this.cardRequestUrl = value.trim();
        }
        value = propertiesLoader.getProperty(SDK_APP_URL);
        if (!SDKUtil.isEmpty(value)) {
            this.appRequestUrl = value.trim();
        }
        value = propertiesLoader.getProperty(SDK_ENCRYPTTRACKCERT_PATH);
        if (!SDKUtil.isEmpty(value)) {
            this.encryptTrackCertPath = value.trim();
        }

    }


    public String getFrontRequestUrl() {
        return frontRequestUrl;
    }

    public void setFrontRequestUrl(String frontRequestUrl) {
        this.frontRequestUrl = frontRequestUrl;
    }

    public String getBackRequestUrl() {
        return backRequestUrl;
    }

    public void setBackRequestUrl(String backRequestUrl) {
        this.backRequestUrl = backRequestUrl;
    }

    public InputStream getSignCertPath() {
        return signCertPath;
    }

    public void setSignCertPath(InputStream signCertPath) {
        this.signCertPath = signCertPath;
    }

    public String getSignCertPwd() {
        return signCertPwd;
    }

    public void setSignCertPwd(String signCertPwd) {
        this.signCertPwd = signCertPwd;
    }

    public String getSignCertType() {
        return signCertType;
    }

    public void setSignCertType(String signCertType) {
        this.signCertType = signCertType;
    }

    public String getEncryptCertPath() {
        return encryptCertPath;
    }

    public void setEncryptCertPath(String encryptCertPath) {
        this.encryptCertPath = encryptCertPath;
    }

    public String getValidateCertDir() {
        return validateCertDir;
    }

    public void setValidateCertDir(String validateCertDir) {
        this.validateCertDir = validateCertDir;
    }

    public String getSingleQueryUrl() {
        return singleQueryUrl;
    }

    public void setSingleQueryUrl(String singleQueryUrl) {
        this.singleQueryUrl = singleQueryUrl;
    }

    public String getBatchQueryUrl() {
        return batchQueryUrl;
    }

    public void setBatchQueryUrl(String batchQueryUrl) {
        this.batchQueryUrl = batchQueryUrl;
    }

    public String getBatchTransUrl() {
        return batchTransUrl;
    }

    public void setBatchTransUrl(String batchTransUrl) {
        this.batchTransUrl = batchTransUrl;
    }

    public String getFileTransUrl() {
        return fileTransUrl;
    }

    public void setFileTransUrl(String fileTransUrl) {
        this.fileTransUrl = fileTransUrl;
    }

    public String getSignCertDir() {
        return signCertDir;
    }

    public void setSignCertDir(String signCertDir) {
        this.signCertDir = signCertDir;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public String getCardRequestUrl() {
        return cardRequestUrl;
    }

    public void setCardRequestUrl(String cardRequestUrl) {
        this.cardRequestUrl = cardRequestUrl;
    }

    public String getAppRequestUrl() {
        return appRequestUrl;
    }

    public void setAppRequestUrl(String appRequestUrl) {
        this.appRequestUrl = appRequestUrl;
    }

    public String getEncryptTrackCertPath() {
        return encryptTrackCertPath;
    }

    public void setEncryptTrackCertPath(String encryptTrackCertPath) {
        this.encryptTrackCertPath = encryptTrackCertPath;
    }

    public String getSingleMode() {
        return singleMode;
    }

    public void setSingleMode(String singleMode) {
        this.singleMode = singleMode;
    }

    public SDKConfig() {
        super();
    }

    public String getPubliccertNames() {
        return publiccertNames;
    }

    public void setPubliccertNames(String publiccertNames) {
        this.publiccertNames = publiccertNames;
    }
}
