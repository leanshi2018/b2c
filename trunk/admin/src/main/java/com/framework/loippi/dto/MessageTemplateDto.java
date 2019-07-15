package com.framework.loippi.dto;

import com.framework.loippi.entity.product.ShopGoodsSpec;
import com.framework.loippi.utils.JacksonUtil;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by longbh on 2019/1/4.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageTemplateDto {

    private String code;
    private String title;
    private String content;
    private String signName;
    private String parameter;

    public static MessageTemplateDto of(ShopGoodsSpec shopGoodsSpec) {
        MessageTemplateDto idNameDto = new MessageTemplateDto();

        return idNameDto;
    }

}
