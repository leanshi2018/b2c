package com.framework.loippi.dto;

import com.framework.loippi.entity.ShopCommonMessage;

import java.util.Map;

/**
 * Created by longbh on 2017/8/19.
 */
public class PushMessage {

    //平台
    private String pushPlat;
    //别名
    private String targetId;
    //目标类型  1-个人  2-用户组  3-全部
    private Integer targetType;
    private Long bizId;

    private Integer bizType;

    private String image;

    private String title;

    private String subject;

    private String content;

    private Long msgId;

    public Integer getTargetType() {
        return targetType;
    }

    public void setTargetType(Integer targetType) {
        this.targetType = targetType;
    }

    public String getPushPlat() {
        return pushPlat;
    }

    public void setPushPlat(String pushPlat) {
        this.pushPlat = pushPlat;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getBizId() {
        return bizId;
    }

    public void setBizId(Long bizId) {
        this.bizId = bizId;
    }

    public Integer getBizType() {
        return bizType;
    }

    public void setBizType(Integer bizType) {
        this.bizType = bizType;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public Long getMsgId() {
        return msgId;
    }

    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }

    public static PushMessage of(ShopCommonMessage shopCommonMessage) {
        PushMessage pushMessage = new PushMessage();
        pushMessage.setSubject(shopCommonMessage.getSubject());
        pushMessage.setImage(shopCommonMessage.getImage());
        pushMessage.setBizId(shopCommonMessage.getBizId());
        pushMessage.setBizType(shopCommonMessage.getBizType());
        pushMessage.setPushPlat("jpush");
        pushMessage.setTargetId(shopCommonMessage.getSendUid());
        pushMessage.setTargetType(shopCommonMessage.getUType());
        pushMessage.setTitle(shopCommonMessage.getTitle());
        pushMessage.setMsgId(shopCommonMessage.getId());
        return pushMessage;
    }
}
