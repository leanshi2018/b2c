package com.framework.loippi.result.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.framework.loippi.entity.user.RdMmAddInfo;
import com.framework.loippi.entity.ware.RdWarehouse;

/**
 * Result - 收货地址-列表
 *
 * @author Loippi team
 * @version 2.0
 * @description 收货地址-列表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAddrsListResult {

    /**
     * 收货人
     */
    private String name;

    /**
     * 电话
     */
    private String mobile;

    /**
     * 地区id
     */
    private Long areaId;

    /**
     * 地区
     */
    private String area;

    /**
     * 详细地址
     */
    private String addr;

    /**
     * 地址id
     */
    private Integer addrId;

    /** 是否为默认地址1:是0:否 */
    private Integer isDefAddr;

    public static List<UserAddrsListResult> build(List<RdMmAddInfo> addrList) {
        if (CollectionUtils.isEmpty(addrList)) {
            return Collections.emptyList();
        }
        List<UserAddrsListResult> results = new ArrayList<UserAddrsListResult>();
        for (RdMmAddInfo addr : addrList) {
            Optional<RdMmAddInfo> optional = Optional.ofNullable(addr);
            UserAddrsListResult result = new UserAddrsListResult();
            result.setName(optional.map(RdMmAddInfo::getConsigneeName).orElse(""));
            result.setAddr(optional.map(RdMmAddInfo::getAddDetial).orElse(""));
            result.setMobile(optional.map(RdMmAddInfo::getMobile).orElse(""));
//            result.setAreaId(optional.map(RdMmAddInfo::getAreaId).orElse(0L));
            result.setAddrId(optional.map(RdMmAddInfo::getAid).orElse(-1));
            result.setIsDefAddr(optional.map(RdMmAddInfo::getDefaultadd).orElse(0));
            result.setArea(optional.map(RdMmAddInfo::getAddProvinceCode).orElse("")+optional.map(RdMmAddInfo::getAddCityCode).orElse("")+optional.map(RdMmAddInfo::getAddCountryCode).orElse(""));
            results.add(result);
        }
        return results;
    }

    public static List<UserAddrsListResult> build1(List<RdMmAddInfo> addrList,List<RdWarehouse> houseList) {
        if (CollectionUtils.isEmpty(addrList)) {
            return Collections.emptyList();
        }

        List<UserAddrsListResult> results = new ArrayList<UserAddrsListResult>();
        if (houseList.size()>0){//有停用的
            Set<Integer> setList = new HashSet<Integer>();
            for (RdWarehouse warehouse : houseList) {
                setList.add(warehouse.getMentionId());
            }

            for (RdMmAddInfo addr : addrList) {
                if (setList.contains(addr.getAid())==false){
                    Optional<RdMmAddInfo> optional = Optional.ofNullable(addr);
                    UserAddrsListResult result = new UserAddrsListResult();
                    result.setName(optional.map(RdMmAddInfo::getConsigneeName).orElse(""));
                    result.setAddr(optional.map(RdMmAddInfo::getAddDetial).orElse(""));
                    result.setMobile(optional.map(RdMmAddInfo::getMobile).orElse(""));
                    result.setAddrId(optional.map(RdMmAddInfo::getAid).orElse(-1));
                    result.setIsDefAddr(optional.map(RdMmAddInfo::getDefaultadd).orElse(0));
                    result.setArea(optional.map(RdMmAddInfo::getAddProvinceCode).orElse("")+optional.map(RdMmAddInfo::getAddCityCode).orElse("")+optional.map(RdMmAddInfo::getAddCountryCode).orElse(""));
                    results.add(result);
                }
            }

        }else {
            for (RdMmAddInfo addr : addrList) {
                Optional<RdMmAddInfo> optional = Optional.ofNullable(addr);
                UserAddrsListResult result = new UserAddrsListResult();
                result.setName(optional.map(RdMmAddInfo::getConsigneeName).orElse(""));
                result.setAddr(optional.map(RdMmAddInfo::getAddDetial).orElse(""));
                result.setMobile(optional.map(RdMmAddInfo::getMobile).orElse(""));
                result.setAddrId(optional.map(RdMmAddInfo::getAid).orElse(-1));
                result.setIsDefAddr(optional.map(RdMmAddInfo::getDefaultadd).orElse(0));
                result.setArea(optional.map(RdMmAddInfo::getAddProvinceCode).orElse("")+optional.map(RdMmAddInfo::getAddCityCode).orElse("")+optional.map(RdMmAddInfo::getAddCountryCode).orElse(""));
                results.add(result);
            }
        }

        return results;
    }
}
