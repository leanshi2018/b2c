package com.framework.loippi.dto;

import com.framework.loippi.entity.product.ShopGoodsSpec;
import com.framework.loippi.utils.JacksonUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Created by longbh on 2019/1/4.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdNameDto {

    private Long id;
    private String name;

    public static IdNameDto of(ShopGoodsSpec shopGoodsSpec) {
        IdNameDto idNameDto = new IdNameDto();
        idNameDto.setId(shopGoodsSpec.getId());
        Map<String, String> specValue = JacksonUtil.convertStrMap(shopGoodsSpec.getSpecGoodsSpec());
        StringBuffer name = new StringBuffer();
        for (String itemValue : specValue.values()) {
            name.append(itemValue + ",");
        }
        if (name.length() > 0) {
            name.deleteCharAt(name.length() - 1);
        }
        idNameDto.setName(name.toString());
        return idNameDto;
    }

}
