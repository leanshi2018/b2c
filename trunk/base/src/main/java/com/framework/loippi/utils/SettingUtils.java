package com.framework.loippi.utils;

import com.framework.loippi.support.Setting;

/**
 * Utils - 系统设置
 *
 * @author Mounate Yan。
 * @version 1.0
 */
@SuppressWarnings("unchecked")
public final class SettingUtils {

    private static Setting setting;

    /**
     * 不可实例化
     */
    private SettingUtils() {
    }

    /**
     * 获取系统设置
     *
     * @return 系统设置
     */
    public static Setting get() {
        if (setting == null) {
            setting = new Setting();
        }
        return setting;
    }

    /**
     * 设置系统设置
     *
     * @param setting 系统设置
     */
    public static void set(Setting setting) {
        SettingUtils.setting = setting;
    }

}