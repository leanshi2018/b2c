package com.framework.loippi.utils.wechat.mobile.util;

import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.framework.loippi.utils.wechat.h5.util.MyX509TrustManager;

public class WeixinUtils {

    public static final String TRANSFERS_PAY = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers"; // 企业付款

    //public static final String APP_ID = ConfigUtil.getProperty("wx.appid");
    public static final String APP_ID = ConfigUtil.getProperty("wx.appid");

    //public static final String MCH_ID = ConfigUtil.getProperty("wx.mchid");
    public static final String MCH_ID = ConfigUtil.getProperty("wx.mchid");

    //public static final String API_SECRET = ConfigUtil.getProperty("wx.api.secret");
    public static final String API_SECRET = ConfigUtil.getProperty("wx.api.secret");

    /**
     * 发起https请求并获取结果
     *
     * @param requestUrl    请求地址
     * @param requestMethod 请求方式（GET、POST）
     * @param outputStr     提交的数据
     * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
     */
    public static String request(String requestUrl, String requestMethod,
                                 String outputStr) {
        StringBuffer buffer = new StringBuffer();
        try {
            // 创建SSLContext对象，并使用我们指定的信任管理器初始化
            TrustManager[] tm = {new MyX509TrustManager()};
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            // 从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();

            URL url = new URL(requestUrl);
            HttpsURLConnection httpUrlConn = (HttpsURLConnection) url
                    .openConnection();
            httpUrlConn.setSSLSocketFactory(ssf);
            httpUrlConn.setDoOutput(true);
            httpUrlConn.setDoInput(true);
            httpUrlConn.setUseCaches(false);
            // 设置请求方式（GET/POST）
            httpUrlConn.setRequestMethod(requestMethod);

            if ("GET".equalsIgnoreCase(requestMethod))
                httpUrlConn.connect();

            // 当有数据需要提交时
            if (null != outputStr) {
                OutputStream outputStream = httpUrlConn.getOutputStream();
                // 注意编码格式，防止中文乱码
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }

            // 将返回的输入流转换成字符串
            InputStream inputStream = httpUrlConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(
                    inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader);

            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            // 释放资源
            inputStream.close();
            inputStream = null;
        } catch (ConnectException ce) {
            ce.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    public static String FormatBizQueryParaMap(HashMap<String, String> paraMap,
                                               boolean urlencode) {
        String buff = "";
        try {
            List<Entry<String, String>> infoIds = new ArrayList<Entry<String, String>>(
                    paraMap.entrySet());

            Collections.sort(infoIds,
                    new Comparator<Entry<String, String>>() {
                        public int compare(Entry<String, String> o1,
                                           Entry<String, String> o2) {
                            return (o1.getKey()).toString().compareTo(
                                    o2.getKey());
                        }
                    });

            for (int i = 0; i < infoIds.size(); i++) {
                Entry<String, String> item = infoIds.get(i);
                if (item.getKey() != "") {

                    String key = item.getKey();
                    String val = item.getValue();
                    if (urlencode) {
                        val = URLEncoder.encode(val, "utf-8");
                    }
                    buff += key.toLowerCase() + "=" + val + "&";
                }
            }
            if (buff.isEmpty() == false) {
                buff = buff.substring(0, buff.length() - 1);
            }
        } catch (Exception e) {
        }
        return buff;
    }

    /**
     * 商户生成的随机字符串 字符串类型，32个字节以下
     *
     * @return
     */
    public static String createNoncestr() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String res = "";
        for (int i = 0; i < 16; i++) {
            Random rd = new Random();
            res += chars.charAt(rd.nextInt(chars.length() - 1));
        }
        return res;
    }

    public static String getTimeStamp() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

    /**
     * MD5签名
     *
     * @param content
     * @param key
     * @return
     */
    public static String sign(String content, String key) {
        return XDigestUtils.md5Hex(content + "&key=" + key).toUpperCase();
    }

    public static String originalString(TreeMap<String, String> treeMap) {
        Set<Entry<String, String>> entry = treeMap.entrySet();
        StringBuffer sb = new StringBuffer();
        for (Entry<String, String> obj : entry) {
            String k = obj.getKey();
            String v = obj.getValue();
            if (v == null && v.equals(""))
                continue;
            sb.append(k + "=" + v + "&");
        }
        return sb.toString();
    }

    public static String createPaySign(TreeMap<String, String> param, String key) {
        String string1 = originalString(param);
        String stringSignTemp = string1 + "key=" + key;
        String paysign = XDigestUtils.md5Hex(stringSignTemp).toUpperCase();
        return paysign;
    }

    /**
     * 将XML转JSON字符串
     *
     * @param xml
     * @return
     * @throws JDOMException
     * @throws IOException
     */
    public static String toJson(String xml) throws JDOMException, IOException {
        JSONObject json = new JSONObject();
        InputStream is = new ByteArrayInputStream(xml.getBytes("utf-8"));
        SAXBuilder sb = new SAXBuilder();
        Document doc = sb.build(is);
        org.jdom.Element root = doc.getRootElement();
        json.put(root.getName(), iterateElement(root));
        return json.getJSONObject("xml").toString();
    }

    public static String getJsonValue(String rescontent, String key) {
        JSONObject jsonObject = new JSONObject();
        String v = null;
        try {
            InputStream is = new ByteArrayInputStream(
                    rescontent.getBytes("utf-8"));
            SAXBuilder sb = new SAXBuilder();
            Document doc = sb.build(is);
            org.jdom.Element root = doc.getRootElement();
            jsonObject.put(root.getName(), iterateElement(root));
            v = jsonObject.getJSONObject("xml").getString(key);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return v;
    }

    /**
     * 迭代
     *
     * @param element
     * @return
     */
    @SuppressWarnings("unchecked")
    private static Map<String, String> iterateElement(org.jdom.Element element) {
        List<Element> jiedian = element.getChildren();
        org.jdom.Element et = null;
        Map<String, String> obj = new HashMap<String, String>();
        for (int i = 0; i < jiedian.size(); i++) {
            et = (org.jdom.Element) jiedian.get(i);
            obj.put(et.getName(), et.getTextTrim());
        }
        return obj;
    }

    public static Map<String, String> doXMLParse(HttpServletRequest request) throws JDOMException, IOException {
        Map<String, String> m = new HashMap<String, String>();
        SAXBuilder builder = new SAXBuilder();
        ServletInputStream in = request.getInputStream();
        Document doc = builder.build(in);
        Element root = doc.getRootElement();
        List<Object> list = root.getChildren();
        Iterator<Object> it = list.iterator();
        while (it.hasNext()) {
            Element e = (Element) it.next();
            String k = e.getName();
            String v = "";
            List children = e.getChildren();
            if (children.isEmpty()) {
                v = e.getTextNormalize();
            } else {
                v = getChildrenText(children);
            }

            m.put(k, v);
        }

        //关闭流
        in.close();
        return m;
    }

    /**
     * 获取子结点的xml
     *
     * @param children
     * @return String
     */
    private static String getChildrenText(List children) {
        StringBuffer sb = new StringBuffer();
        if (!children.isEmpty()) {
            Iterator it = children.iterator();
            while (it.hasNext()) {
                Element e = (Element) it.next();
                String name = e.getName();
                String value = e.getTextNormalize();
                List list = e.getChildren();
                sb.append("<" + name + ">");
                if (!list.isEmpty()) {
                    sb.append(getChildrenText(list));
                }
                sb.append(value);
                sb.append("</" + name + ">");
            }
        }

        return sb.toString();
    }

    /**
     * 发起https请求并获取结果
     *
     * @param requestUrl    请求地址
     * @param requestMethod 请求方式（GET、POST）
     * @param outputStr     提交的数据
     * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
     */
    public static String requestSSL(String requestUrl, String certPath, String pass, String requestMethod,
                                    String outputStr) {
        StringBuffer buffer = new StringBuffer();
        try {
            // 创建SSLContext对象，并使用我们指定的信任管理器初始化

            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            FileInputStream instream = new FileInputStream(new File(certPath));//P12文件目录
            try {
                /**
                 * 此处要改
                 * */
                keyStore.load(instream, pass.toCharArray());//这里写密码..默认是你的MCHID
            } finally {
                instream.close();
            }

            // Trust own CA and all self-signed certs
            TrustManager[] tm = {new MyX509TrustManager()};
            SSLContext sslContext = SSLContext.getInstance("SSL");
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(keyStore, pass.toCharArray());
            sslContext.init(kmf.getKeyManagers(), tm, new java.security.SecureRandom());
            // 从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();

            URL url = new URL(requestUrl);
            HttpsURLConnection httpUrlConn = (HttpsURLConnection) url
                    .openConnection();
            httpUrlConn.setSSLSocketFactory(ssf);
            httpUrlConn.setDoOutput(true);
            httpUrlConn.setDoInput(true);
            httpUrlConn.setUseCaches(false);
            // 设置请求方式（GET/POST）
            httpUrlConn.setRequestMethod(requestMethod);

            if ("GET".equalsIgnoreCase(requestMethod))
                httpUrlConn.connect();

            // 当有数据需要提交时
            if (null != outputStr) {
                OutputStream outputStream = httpUrlConn.getOutputStream();
                // 注意编码格式，防止中文乱码
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }

            // 将返回的输入流转换成字符串
            InputStream inputStream = httpUrlConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(
                    inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader);

            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            // 释放资源
            inputStream.close();
            inputStream = null;
        } catch (ConnectException ce) {
            ce.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    /**
     * 退款单号
     *
     * @return
     */
    public static String getTransferNo() {
        // 自增8位数 00000001
        return "TNO" + DatetimeUtil.formatDate(new Date(), DatetimeUtil.TIME_STAMP_PATTERN) + "00000001";
    }

    /**
     * 获取服务器的ip地址
     *
     * @param request
     * @return
     */
    public static String getLocalIp(HttpServletRequest request) {
        return request.getLocalAddr();
    }

	public static String getSign(Map<String, String> params, String paternerKey) throws UnsupportedEncodingException {
		return MD5Util.getMD5(createSign(params, false) + "&key=" + paternerKey).toUpperCase();
	}

	/**
	 * 构造签名
	 *
	 * @param params
	 * @param encode
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String createSign(Map<String, String> params, boolean encode) throws UnsupportedEncodingException {
		Set<String> keysSet = params.keySet();
		Object[] keys = keysSet.toArray();
		Arrays.sort(keys);
		StringBuffer temp = new StringBuffer();
		boolean first = true;
		for (Object key : keys) {
			if (key == null || StringUtil.isEmpty(params.get(key))) // 参数为空不参与签名
				continue;
			if (first) {
				first = false;
			} else {
				temp.append("&");
			}
			temp.append(key).append("=");
			Object value = params.get(key);
			String valueStr = "";
			if (null != value) {
				valueStr = value.toString();
			}
			if (encode) {
				temp.append(URLEncoder.encode(valueStr, "UTF-8"));
			} else {
				temp.append(valueStr);
			}
		}
		return temp.toString();
	}

}
