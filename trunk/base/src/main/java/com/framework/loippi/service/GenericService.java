package com.framework.loippi.service;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Service - 基类
 * 
 * @author Loippi Team
 * @version 1.0
 */
public interface GenericService<T extends GenericEntity, PK extends Serializable> {

	/**
	 * 保存
	 */
	abstract Long saveEntity(T param);

	/**
	 * 更新
	 */
	abstract Long updateEntity(T param);

	/**
	 * 删除
	 */
	abstract Long deleteEntity(T param);

	/**
	 * 保存
	 */
	abstract Long save(T param);

	/**
	 * 更新
	 */
	abstract Long update(T param);

	/**
	 * 根据主键删除
	 */
	abstract Long delete(PK pk);

	/**
	 * 删除列表
	 */
	abstract Long deleteAll(Long... ids);

	/**
	 * 查询实体对象数量
	 */
	abstract Long count();

	/**
	 * 根据条件查询实体对象数量
	 */
	abstract Long count(Map<String, Object> params);

	/**
	 * 根据主键查询数据
	 */
	abstract T find(PK pk);

	/**
	 * 查询所有数据
	 */
	abstract List<T> findAll();

	/**
	 * 根据条件查询数据
	 */
	abstract T find(String propertyName, Object propertyValue);

	/**
	 * 根据属性名和属性值获取实体对象集合.
	 */
	public List<T> findList(String propertyName, Object propertyValue);

	/**
	 * 根据条件查询数据
	 */
	abstract List<T> findList(Map<String, Object> params);

	/**
	 * 分页查询
	 */
	abstract Page<T> findByPage(Pageable pageable);

}
