//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.framework.loippi.utils;

import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;
import javax.servlet.ServletRequest;
import org.springframework.util.Assert;

public class ParameterUtils {
    public ParameterUtils() {
    }

    public static Map<String, Object> getParametersMap(ServletRequest request) {
        Assert.notNull(request, "Request must not be null");
        Enumeration paramNames = request.getParameterNames();
        TreeMap params = new TreeMap();

        while(paramNames != null && paramNames.hasMoreElements()) {
            String paramName = (String)paramNames.nextElement();
            String[] values = request.getParameterValues(paramName);
            if(values != null && values.length != 0) {
                if(values.length > 1) {
                    params.put(paramName, values);
                } else {
                    params.put(paramName, values[0] == null?null:values[0].trim());
                }
            }
        }

        return params;
    }

    public static Map<String, Object> getParametersMapStartingWith(ServletRequest request, String prefix) {
        Assert.notNull(request, "Request must not be null");
        Enumeration paramNames = request.getParameterNames();
        TreeMap params = new TreeMap();
        if(prefix == null) {
            prefix = "";
        }

        while(paramNames != null && paramNames.hasMoreElements()) {
            String paramName = (String)paramNames.nextElement();
            if("".equals(prefix) || paramName.startsWith(prefix)) {
                String unprefixed = paramName.substring(prefix.length());
                String[] values = request.getParameterValues(paramName);
                if(values != null && values.length != 0) {
                    if(values.length > 1) {
                        params.put(unprefixed, values);
                    } else {
                        params.put(unprefixed, values[0] == null?null:values[0].trim());
                    }
                }
            }
        }

        return params;
    }
}
