package com.framework.loippi.entity;

import lombok.Data;

@Data
public class RlsInfoDto implements Cloneable {
    private String waybillNo;
    private String abFlag;
    private String xbFlag;
    private String proCode;
    private String destRouteLabel;
    private String destTeamCode;
    private String codingMapping;
    private String codingMappingOut;
    private String sourceTransferCode;
    private String printIcon;
    private String qrcode;

    @Override
    public Object clone() {
        RlsInfoDto obj = null;
        try {
            obj = (RlsInfoDto) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return obj;
    }
}