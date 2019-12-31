package com.framework.loippi.service.impl.common;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.framework.loippi.dao.common.RdKeywordDao;
import com.framework.loippi.entity.common.RdKeyword;
import com.framework.loippi.service.common.RdKeywordService;
import com.framework.loippi.service.impl.GenericServiceImpl;

/**
 * @author :ldq
 * @date:2019/12/31
 * @description:dubbo com.framework.loippi.service.impl.common
 */
@Service
@Slf4j
public class RdKeywordServiceImpl extends GenericServiceImpl<RdKeyword, Long> implements RdKeywordService {

	@Resource
	private RdKeywordDao rdKeywordDao;
}
