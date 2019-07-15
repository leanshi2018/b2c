package com.framework.loippi.utils;


import java.util.HashMap;
import java.util.Map;

public class Paramap extends HashMap<String, Object> implements Map<String, Object> {
    private static final long serialVersionUID = 1L;

    private Paramap() {
    }

    public static Paramap create() {
        return new Paramap();
    }

    public Paramap put(String name, Object value) {
        super.put(name, value);
        return this;
    }

    public Paramap putMap(Map<String, Object> maps) {
        super.putAll(maps);
        return this;
    }
}
