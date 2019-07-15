package com.framework.loippi.vo.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by longbh on 2017/8/23.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDto {

    private Long id;
    private String hid;
    private String name;
    private String avatar;

}
