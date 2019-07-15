package com.framework.loippi.service.impl.user;

import com.framework.loippi.dao.user.RdMmAddInfoDao;
import com.framework.loippi.entity.user.RdMmAddInfo;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.user.RdMmAddInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * SERVICE - RdMmAddInfo(会员地址表)
 * 
 * @author dzm
 * @version 2.0
 */
@Service
public class RdMmAddInfoServiceImpl extends GenericServiceImpl<RdMmAddInfo, Long> implements RdMmAddInfoService {
	
	@Autowired
	private RdMmAddInfoDao rdMmAddInfoDao;
	
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(rdMmAddInfoDao);
	}



	public int updateDef(Integer addressId, String memberId) {
		int result = 0;
		if (addressId != null) {
			RdMmAddInfo address = new RdMmAddInfo();
			address.setMmCode(memberId);
			address.setDefaultadd(0);
			rdMmAddInfoDao.updateMember(address);
			address.setMmCode(memberId);
			address.setAid(addressId);
			address.setDefaultadd(1);
			rdMmAddInfoDao.update(address);
			result = 1;
		}
		return result;
	}
}
