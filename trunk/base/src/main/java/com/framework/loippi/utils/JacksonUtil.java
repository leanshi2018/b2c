package com.framework.loippi.utils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * json转换集合类型
 *
 * @author linwh
 */

public class JacksonUtil {

    private static String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

     private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final ObjectMapper mapper = new JacksonObjectMapper();

    @SuppressWarnings("unused")
    private static ObjectMapper getInstance() {
        return mapper;
    }


    /**
     * 获取泛型的Collection Type
     *
     * @param collectionClass 泛型的Collection
     * @param elementClasses  元素类
     * @return JavaType Java类型
     * @since 1.0
     */
    private static JavaType getCollectionType(Class<?> collectionClass,
                                              Class<?>... elementClasses) {
        return mapper.getTypeFactory().constructParametricType(collectionClass,
                elementClasses);
    }

    /**
     * 转换集合类型
     *
     * @param jsonString
     * @param clazz
     * @return
     */
    public static <T> List<T> convertList(String jsonString, Class<T> clazz) {
        if (jsonString != null && !"".equals(jsonString)) {
            JavaType javaType = getCollectionType(ArrayList.class, clazz);
            List<T> list = null;
            try {
                list = mapper.readValue(jsonString, javaType);
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return list;
        }

        return null;
    }

    /**
     * 转换map类型
     *
     * @param jsonString
     * @return
     */
    public static Map<String, Object> convertMap(String jsonString) {
        if (jsonString != null && !"".equals(jsonString)) {
            JavaType javaType = getCollectionType(HashMap.class, String.class,
                    Object.class);
            try {
                return mapper.readValue(jsonString, javaType);
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return new HashMap<>();
    }

    /**
     * 转换map类型
     *
     * @param jsonString
     * @return
     */
    public static Map<String, String> convertStrMap(String jsonString) {
        if (jsonString != null && !"".equals(jsonString)) {
            JavaType javaType = getCollectionType(HashMap.class, String.class,
                    String.class);
            try {
                return mapper.readValue(jsonString, javaType);
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * 对象序列化
     *
     * @param obj 需要序列化的对象
     * @return 返回json字符串
     */
    public static String toJson(Object obj) {
        try {

            mapper.configure(MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS,
                    false);
            mapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, true);
            mapper.setDateFormat(new SimpleDateFormat(DATE_PATTERN));

            return mapper.writeValueAsString(obj);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 将JSON转换成object
     *
     * @param json
     * @param clazz
     * @return
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        try {
            return mapper.readValue(json, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Map<String, String> readJsonToMap(String jsonString) {
//        try {
//            JavaType javaType = getCollectionType(HashMap.class, String.class, String.class);
//            try {
//                return mapper.readValue(jsonString, javaType);
//            } catch (JsonParseException e) {
//                e.printStackTrace();
//            } catch (JsonMappingException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
        try {
            Map<String, String> maps = objectMapper.readValue(jsonString, Map.class);
            //System.out.println(maps);
            return maps;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
