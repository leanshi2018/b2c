package com.framework.loippi.enus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.xmlbeans.SchemaType;

/**
 * Created by Administrator on 2017/9/23.
 */
@AllArgsConstructor
@NoArgsConstructor
public enum SocialType {
    QQ(1,"qqCode"),WEIXIN(2,"wechatCode"),WEIBO(3,"weiboOpenid");
    private Integer type;
    private String value;
    public static String of(Integer type) {
        switch (type) {
            case 1: return QQ.value;
            case 2:return WEIXIN.value;
            case 3:return WEIBO.value;
        }
        return null;
    }
}
