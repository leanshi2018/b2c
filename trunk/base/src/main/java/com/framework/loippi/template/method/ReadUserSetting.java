package com.framework.loippi.template.method;

import com.framework.loippi.service.TUserSettingService;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Administrator on 2017/8/31.
 */
@Component("readUserSetting")
public class ReadUserSetting implements TemplateMethodModel {
    @Resource
    private TUserSettingService tUserSettingService;
    @Override
    public Object exec(List arguments) throws TemplateModelException {
        if (arguments != null) {
            String arg0 = (String) arguments.get(0);
            return tUserSettingService.read(arg0);
        }
        return "020-12345678";
    }
}
