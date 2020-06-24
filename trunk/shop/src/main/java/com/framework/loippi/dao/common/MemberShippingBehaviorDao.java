package com.framework.loippi.dao.common;

import com.framework.loippi.entity.common.MemberIndicator;
import com.framework.loippi.entity.common.MemberShippingBehavior;
import com.framework.loippi.mybatis.dao.GenericDao;

import java.util.HashMap;
import java.util.List;

public interface MemberShippingBehaviorDao extends GenericDao<MemberShippingBehavior, Long> {

    List<MemberShippingBehavior> findByTime(HashMap<String, Object> map);
}
