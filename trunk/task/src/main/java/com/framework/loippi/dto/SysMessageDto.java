package com.framework.loippi.dto;

import com.framework.loippi.consts.MessageTypeConsts;
import com.framework.loippi.utils.StringUtil;
import io.netty.util.Constant;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

/**
 *系统消息
 */
@Data
public class SysMessageDto {

    /**
     * 消息标题
     */
    private String title;

    /**
     * 是否可点击 1不可以点击    2可点
     */
    private Integer isClick;

    /**
     *消息创建时间
     */
    private Long createTime;

    /**
     * 跳转链接
     */
    private  String url;

    public static List<SysMessageDto> build(List<UserMessageDto> list,String wapService) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        List<SysMessageDto> resultList = new ArrayList<>();
        for(UserMessageDto item:list){
            if(item.getId()==null||item.getBizType()==null||item.getCreateTime()==null){
                continue;
            }
            SysMessageDto dto = new SysMessageDto();
            if(item.getBizType()!=null
                    &&item.getBizType().intValue()== MessageTypeConsts.MESSAGE_TYPE_ADMIN){
                dto.setUrl(wapService+item.getId());
            }else{
                dto.setUrl("");
            }
            if(StringUtil.isEmpty(item.getTitle())){
               dto.setTitle(item.getContent());
            }else{
                if(item.getBizType()!=null
                        &&item.getBizType().intValue()!= MessageTypeConsts.MESSAGE_TYPE_ADMIN){
                    dto.setTitle( item.getContent());
                }else{
                    dto.setTitle( item.getTitle());
                }
            }
            dto.setCreateTime(Optional.ofNullable(  item.getCreateTime().getTime()).orElse(0l));
            resultList.add(dto);
        }
        return resultList;
    }
}
