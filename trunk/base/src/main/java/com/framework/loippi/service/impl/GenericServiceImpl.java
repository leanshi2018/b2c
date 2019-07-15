package com.framework.loippi.service.impl;

import com.framework.loippi.mybatis.dao.GenericDao;
import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.paginator.domain.PageList;
import com.framework.loippi.service.GenericService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.Paramap;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Service - 基类实现
 * 
 * @author Loippi Team
 * @version 1.0
 */
@Transactional
public class GenericServiceImpl<T extends GenericEntity, PK extends Serializable> implements GenericService<T, PK> {

	/** GenericDao */
	@Autowired
	private GenericDao<T, PK> genericDao;

	public void setGenericDao(GenericDao<T, PK> genericDao) {
		this.genericDao = genericDao;
	}

	@Transactional
	public Long saveEntity(T param) {
		return genericDao.insertEntity(param);
	}

	@Transactional
	public Long updateEntity(T param) {
		return genericDao.updateEntity(param);
	}

	@Transactional
	public Long deleteEntity(T param) {
		return genericDao.deleteEntity(param);
	}

	@Transactional
	public Long save(T param) {
		return genericDao.insert(param);
	}

	@Transactional
	public Long update(T param) {
		return genericDao.update(param);
	}

	@Transactional
	public Long delete(PK pk) {
		return genericDao.delete(pk);
	}
	
	@Transactional
	public Long deleteAll(Long ... ids) {
		return genericDao.deleteAll(Paramap.create().put("ids", ids));
	}

	@Transactional(readOnly = true)
	public Long count() {
		return genericDao.count();
	}

	@Transactional(readOnly = true)
	public Long count(Map<String, Object> params) {
		return genericDao.count(params);
	}

	@Transactional(readOnly = true)
	public T find(PK pk) {
		return genericDao.find(pk);
	}

	@Transactional(readOnly = true)
	public List<T> findAll() {
		return genericDao.findAll();
	}


	@Transactional(readOnly = true)
	public Page<T> findByPage(Pageable pageable) {
		PageList<T> result =  genericDao.findByPage(pageable.getParameter(), pageable.getPageBounds());
		return new Page<T>(result, result.getPaginator().getTotalCount(), pageable);
	}

	@Transactional(readOnly = true)
	public T find(String propertyName, Object value) {
		List<T> results = genericDao.findByParams(Paramap.create().put(propertyName, value));
		if(CollectionUtils.isNotEmpty(results)){
			return results.get(0);
		}
		return null;
	}

	@Transactional(readOnly = true)
	public List<T> findList(String propertyName, Object value) {
		List<T> results = genericDao.findByParams(Paramap.create().put(propertyName, value));
		return results;
	}

	@Transactional(readOnly = true)
	public List<T> findList(Map<String, Object> params) {
		return genericDao.findByParams(params);
	}

}
