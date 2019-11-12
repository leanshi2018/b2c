package com.framework.loippi.dao.user;

import java.util.List;
import java.util.Map;

import com.framework.loippi.entity.user.RdMmBank;

import com.framework.loippi.mybatis.dao.GenericDao;

/**
 * DAO - RdMmBank(会员银行卡信息)
 * 
 * @author dzm
 * @version 2.0
 */
public interface RdMmBankDao  extends GenericDao<RdMmBank, Long> {
    void updateMember(RdMmBank rdMmBank);

	RdMmBank findByCodeAndAccCode(Map<String, Object> map);

	void updateBankSigning(Map<String, Object> map);

	void updateDefaultbank(Map<String, Object> map);

	void deleteById(long oId);

	void updateInValid(Integer oid);

	List<RdMmBank> findBankByIdCardAndName(Map<String, Object> map);
}
