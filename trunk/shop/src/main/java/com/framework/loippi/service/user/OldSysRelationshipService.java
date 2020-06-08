package com.framework.loippi.service.user;


import com.framework.loippi.entity.user.OldSysRelationship;
import com.framework.loippi.service.GenericService;

import java.util.List;
import java.util.Map;

/**
 * SERVICE - OldSysRelationship(老用户关系表)
 * 
 * @author dzm
 * @version 2.0
 */
public interface OldSysRelationshipService  extends GenericService<OldSysRelationship, Long> {

    public Map<String,String> findOldSysSpcode(String oSpcode);
}
