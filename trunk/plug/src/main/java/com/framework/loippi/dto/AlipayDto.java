package com.framework.loippi.dto;

import lombok.Data;
import lombok.ToString;

/**
 * Created by longbh on 2017/8/1.
 */
@Data
@ToString
public class AlipayDto {


    private String partner;
    private String private_key;
    private String public_key;
    private String sellerId;

}
