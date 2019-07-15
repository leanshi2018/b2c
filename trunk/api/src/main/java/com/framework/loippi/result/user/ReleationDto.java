package com.framework.loippi.result.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Created by longbh on 2017/8/9.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReleationDto {

    private Long uid;
    private String name;
    private String avatar;
    private Boolean isFriends = false;

//    public static ReleationDto of(ShopMember shopMember, String prefix, Set<Long> releations) {
//        ReleationDto releationDto = new ReleationDto();
//        releationDto.setUid(shopMember.getId());
//        releationDto.setName(shopMember.getMemberAvatar());
//        releationDto.setAvatar(prefix + shopMember.getMemberAvatar());
//        if (releations.contains(shopMember.getId())) {
//            releationDto.setIsFriends(true);
//        }
//        return releationDto;
//    }

}
