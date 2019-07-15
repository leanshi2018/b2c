package com.framework.loippi.dto;

import com.framework.loippi.consts.MessageTypeConsts;
import com.framework.loippi.utils.StringUtil;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

/**
 *系统消息
 */
@Data
public class SysMessageWebDto {

    /**
     * 消息标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 是否可点击 1不可以点击    2可点
     */
    private Integer isClick;

    /**
     *消息创建时间
     */
    private Date createTime;

    /**
     * 跳转链接
     */
    private  String url;

    public static List<SysMessageWebDto> build(List<UserMessageDto> list, String wapService) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        List<SysMessageWebDto> resultList = new ArrayList<>();
        for(UserMessageDto item:list){
            SysMessageWebDto dto = new SysMessageWebDto();
            if(item.getBizType()!=null
                    &&item.getBizType().intValue()== MessageTypeConsts.MESSAGE_TYPE_ADMIN){
                dto.setUrl(wapService+item.getId());
            }else{
                dto.setUrl("");
            }
            if(StringUtil.isEmpty(item.getTitle())){
               dto.setTitle(item.getContent());
            }else{
                dto.setTitle(item.getTitle());
            }
            dto.setContent(item.getContent());
            dto.setCreateTime(Optional.ofNullable(  item.getCreateTime()).orElse(null));
            resultList.add(dto);
        }
        return resultList;
    }
}
