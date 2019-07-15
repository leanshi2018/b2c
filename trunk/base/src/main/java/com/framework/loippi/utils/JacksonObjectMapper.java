package com.framework.loippi.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;


public class JacksonObjectMapper extends ObjectMapper {
	public JacksonObjectMapper() {
		
		super();
		
		this.disable(MapperFeature.DEFAULT_VIEW_INCLUSION); //避免IE执行AJAX时,返回JSON出现下载文件
	}
}
