package com.framework.loippi.service.user;


import java.util.List;

import com.framework.loippi.entity.user.RdMmAddInfo;
import com.framework.loippi.service.GenericService;
import com.framework.loippi.support.Pageable;

/**
 * SERVICE - RdMmAddInfo(会员地址表)
 * 
 * @author dzm
 * @version 2.0
 */
public interface RdMmAddInfoService  extends GenericService<RdMmAddInfo, Long> {

    int updateDef(Integer addressId, String memberId);


	List<RdMmAddInfo> findMentionAddrList();

	Object findMentionAddrListByPage(Pageable pageable);

	List<RdMmAddInfo> findMentionAddrListByPhoneAndCode(String provinceCode, String phone);
}
