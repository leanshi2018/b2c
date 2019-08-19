package com.framework.loippi.result.user;

import lombok.Data;

import com.framework.loippi.entity.user.RdMmAddInfo;

/**
 * Created by Administrator on 2017/6/23.
 */
@Data
public class UserAddrsAddResult {

    private Integer addrId;
    private String addr;
    private String mobile;
    private String name;
    private String area;
    private Integer isDefault;
     //省
    private String province;
     //市
    private String city;
     // 区县
    private String county;

    public static UserAddrsAddResult build(RdMmAddInfo param) {
        UserAddrsAddResult addr = new UserAddrsAddResult();
        addr.setAddrId(param.getAid());
        addr.setAddr(param.getAddDetial());
        addr.setArea(param.getAddProvinceCode()+param.getAddCityCode()+param.getAddCountryCode());
        addr.setName(param.getConsigneeName());
        addr.setMobile(param.getMobile());
        addr.setIsDefault(param.getDefaultadd());
        addr.setProvince(param.getAddProvinceCode());
        addr.setCity(param.getAddCityCode());
        addr.setCounty(param.getAddCountryCode());
        return addr;
    }
}
