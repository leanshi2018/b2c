package com.framework.loippi.utils.quickbill.PC.util;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Static functions to simplifiy common {@link MessageDigest} tasks.  This
 * class is thread safe.
 * 
 * @author 99bill
 *
 */
public class BillUtil {
    /**
     * 将String装换成map
     * @param mapString
     * @return
     */
    public static Map<String,Object> transStringToMap(String mapString,String msg){
        Map<String,Object> map = new HashMap<String,Object>();
        StringTokenizer items;
        for(StringTokenizer entrys = new StringTokenizer(mapString, "&");entrys.hasMoreTokens();
            map.put(items.nextToken(), items.hasMoreTokens() ? ((Object) (items.nextToken())) : null))
            items = new StringTokenizer(entrys.nextToken(), "=");
        map.put("signMsg",msg);
        return map;
    }


}
