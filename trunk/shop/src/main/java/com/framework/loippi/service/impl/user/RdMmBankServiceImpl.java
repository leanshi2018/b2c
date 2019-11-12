package com.framework.loippi.service.impl.user;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

	@Override
	public void updateBankSigning(Integer oId, String signingImage) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("oid",oId);
		map.put("signingImage",signingImage);
		map.put("signingStatus",1);
		map.put("createTime",new Date());
		rdMmBankDao.updateBankSigning(map);
	}

	@Override
	public void updateDefaultbank(Integer oId,Integer defaultbank) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("oid",oId);
		map.put("defaultbank",defaultbank);
		rdMmBankDao.updateDefaultbank(map);
	}

	@Override
	public void deleteById(long oId) {
		rdMmBankDao.deleteById(oId);
	}

	@Override
	public void updateInValid(Integer oid) {
		rdMmBankDao.updateInValid(oid);
	}

	@Override
	public List<RdMmBank> findBankByIdCardAndName(String idCardCode, String accName) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("idCardCode",idCardCode);
		map.put("accName",accName);
		return rdMmBankDao.findBankByIdCardAndName(map);
	}

	@Override
	public void updateBankSigningByOId(Integer bankSigning, Integer oid) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("oId",oid);
		map.put("bankSigning",bankSigning);
		rdMmBankDao.updateBankSigningByOId(map);
	}
}
