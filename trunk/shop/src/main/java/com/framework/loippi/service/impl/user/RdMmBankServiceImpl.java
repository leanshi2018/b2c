package com.framework.loippi.service.impl.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.user.RdMmBankDao;
import com.framework.loippi.entity.user.RdMmBank;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.user.RdMmBankService;
/**
 * SERVICE - RdMmBank(会员银行卡信息)
 * 
 * @author dzm
 * @version 2.0
 */
@Service
public class RdMmBankServiceImpl extends GenericServiceImpl<RdMmBank, Long> implements RdMmBankService {
	
	@Autowired
	private RdMmBankDao rdMmBankDao;
	
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(rdMmBankDao);
	}

	@Override
	public int updateDef(Integer id, String mmCode) {
		int result = 0;
		if (id != null) {
			RdMmBank rdMmBank=new RdMmBank();
			rdMmBank.setMmCode(mmCode);
			rdMmBank.setDefaultbank(0);
			rdMmBankDao.updateMember(rdMmBank);
			rdMmBank.setOid(id);
			rdMmBank.setMmCode(mmCode);
			rdMmBank.setDefaultbank(1);
			rdMmBankDao.update(rdMmBank);
			result = 1;
		}
		return result;
	}

	@Override
	public RdMmBank findByCodeAndAccCode(String mmCode, String accCode) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("mmCode",mmCode);
		map.put("accCode",accCode);
		return rdMmBankDao.findByCodeAndAccCode(map);
	}
}
