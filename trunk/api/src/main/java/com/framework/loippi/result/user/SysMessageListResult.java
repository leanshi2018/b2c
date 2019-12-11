package com.framework.loippi.result.user;

import com.framework.loippi.entity.ShopCommonMessage;
import com.framework.loippi.utils.JacksonUtil;
import com.framework.loippi.utils.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysMessageListResult {
    /** 消息id */
    private Long id;
    /** 消息标题 */
    private String title;
    /** 消息内容 */
    private String content;
    /** 创建时间 */
    private Date createTime;
    /**
     * 业务id（对于订单来讲为订单id）
     */
    private Long bizId;
    /**
     * 跳转路径
     */
    private String jumpPath;
    /**
     * 跳转链接
     */
    private String jumpUrl;
    /**
     * 跳转路径解析map
     */
    private Map<String,Object> pathMap=new HashMap<String, Object>();

    public static List<SysMessageListResult> build(List<ShopCommonMessage> messages) {
        ArrayList<SysMessageListResult> list = new ArrayList<>();
        if(messages!=null&&messages.size()>0){
            for (ShopCommonMessage message : messages) {
                SysMessageListResult result = new SysMessageListResult();
                result.setId(message.getId());
                result.setTitle(message.getTitle());
                result.setContent(message.getContent());
                result.setCreateTime(message.getCreateTime());
                result.setBizId(Optional.ofNullable(message.getBizId()).orElse(0L));
                result.setJumpPath(Optional.ofNullable(message.getJumpPath()).orElse(""));
                result.setJumpUrl(Optional.ofNullable(message.getJumpUrl()).orElse(""));
                if(!StringUtil.isEmpty(result.getJumpPath())){
                    Map<String, Object> map = JacksonUtil.convertMap(result.getJumpPath());
                    result.setPathMap(map);
                }
                list.add(result);
            }
        }
        return list;
    }
}
