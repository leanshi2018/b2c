package com.framework.loippi.template.method;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Administrator on 2017/8/31.
 */
@Component("promationStatus")
public class PromationStatus implements TemplateMethodModel {
    @Override
    public Object exec(List arguments) throws TemplateModelException {
        if (arguments != null && arguments.size() > 1) {
            String arg0 = (String) arguments.get(0);
            String arg1 = (String) arguments.get(1);
            try {
                if (StringUtils.isBlank(arg0) && StringUtils.isBlank(arg1)) {
                    return null;
                }
                Long endTimestamp = 1000 * 60 * 60 * 24 - 1000L;
                if (StringUtils.isNotBlank(arg0) && StringUtils.isNotBlank(arg1)) {
                    Long begin = new SimpleDateFormat("yyyy-MM-dd").parse(arg0).getTime();
                    Long end = new SimpleDateFormat("yyyy-MM-dd").parse(arg1).getTime() + endTimestamp;
                    Long now = System.currentTimeMillis();
                    if (begin < now && now < end) {
                        return "进行中";
                    } else if(now > end) {
                        return "已结束";
                    } else {
                        return "待开始";
                    }
                }
                if (StringUtils.isNotBlank(arg0)) {
                    Long begin = new SimpleDateFormat("yyyy-MM-dd").parse(arg0).getTime();
                    Long now = System.currentTimeMillis();
                    if (begin <= now) {
                        return "进行中";
                    } else {
                        return "待开始";
                    }
                }
                if (StringUtils.isNotBlank(arg1)) {
                    Long end = new SimpleDateFormat("yyyy-MM-dd").parse(arg1).getTime() + endTimestamp;
                    Long now = System.currentTimeMillis();
                    if (end >= now) {
                        return "进行中";
                    } else {
                        return "已结束";
                    }
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}
