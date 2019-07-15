package com.framework.loippi.result.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 打赏积分
 * Created by Administrator on 2018/1/4.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LevelResult {

    /** 当前等级 */
    private String current;
    /** 当前等级 */
    private String currentVal;
    /** 下一级 */
    private String next;
    /** 距离下一级还需经验或者消费 */
    private String nextVal;
    /** 是否最高级（false为否，true为是） */
    private boolean isTop;
    /** 服务 */
    private String[] permission;
    /** 如何升级 */
    private String[] way;

    public static LevelResult build(String permission, String way, String type, String current,
                                    String next,String nextVal, boolean isTop, String currentVal) {
        LevelResult result = new LevelResult();
        result.setCurrent(current);
        result.setNext(next);
        result.setNextVal(nextVal);
        result.setTop(isTop);
        result.setCurrentVal(currentVal);
        result.setPermission(permission.split("\\r\\n"));
        result.setWay(way.split("\\r\\n"));
        return result;
    }
}
