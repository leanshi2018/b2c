package com.framework.loippi.service.common;

import java.util.List;

import com.framework.loippi.entity.common.RdKeyword;
import com.framework.loippi.service.GenericService;

/**
 * @author :ldq
 * @date:2019/12/31
 * @description:dubbo com.framework.loippi.service.common
 */
public interface RdKeywordService extends GenericService<RdKeyword, Long> {
	List<RdKeyword> findByAll();
}
