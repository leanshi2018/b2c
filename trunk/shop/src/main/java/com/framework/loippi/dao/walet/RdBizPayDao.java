package com.framework.loippi.dao.walet;

import java.util.List;
import java.util.Map;

import com.framework.loippi.entity.walet.RdBizPay;
import com.framework.loippi.mybatis.dao.GenericDao;

/**
 * @author :ldq
 * @date:2020/4/9
 * @description:dubbo com.framework.loippi.dao.walet
 */
public interface RdBizPayDao extends GenericDao<RdBizPay, Long> {
	void updateStatus(String paySn);

	List<RdBizPay> findByPaysn(String paySn);

	List<RdBizPay> findByPaysnAndStatus(Map<String, Object> map);
}
