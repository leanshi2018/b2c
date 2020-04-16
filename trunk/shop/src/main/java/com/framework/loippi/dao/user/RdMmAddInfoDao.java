package com.framework.loippi.dao.user;


import java.util.List;

import com.framework.loippi.entity.user.RdMmAddInfo;
import com.framework.loippi.mybatis.dao.GenericDao;
import com.framework.loippi.mybatis.paginator.domain.PageBounds;
import com.framework.loippi.mybatis.paginator.domain.PageList;

/**
 * DAO - RdMmAddInfo(会员地址表)
 * 
 * @author dzm
 * @version 2.0
 */
public interface RdMmAddInfoDao  extends GenericDao<RdMmAddInfo, Long> {

    void updateMember(RdMmAddInfo shopMemberAddress);


	List<RdMmAddInfo> findMentionAddrList();

	PageList<RdMmAddInfo> findMentionAddrListByPage(Object parameter, PageBounds pageBounds);
}
