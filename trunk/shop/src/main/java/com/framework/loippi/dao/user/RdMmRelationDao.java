package com.framework.loippi.dao.user;


import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.mybatis.dao.GenericDao;

import java.util.List;

/**
 * DAO - RdMmRelation(会员关系状态表)
 * 
 * @author dzm
 * @version 2.0
 */
public interface RdMmRelationDao  extends GenericDao<RdMmRelation, Long> {

    List<RdMmRelation> findBySponsorCode(String mmCode);
}
