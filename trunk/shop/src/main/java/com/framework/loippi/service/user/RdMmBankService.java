package com.framework.loippi.service.user;

import java.util.List;

import com.framework.loippi.entity.user.RdMmBank;
import com.framework.loippi.service.GenericService;

/**
 * SERVICE - RdMmBank(会员银行卡信息)
 * 
 * @author dzm
 * @version 2.0
 */
public interface RdMmBankService  extends GenericService<RdMmBank, Long> {
    int updateDef(Integer id, String mmCode);

	RdMmBank findByCodeAndAccCode(String mmCode, String accCode);

	void updateBankSigning(Integer oId, String signingImage);

	void updateDefaultbank(Integer oId,Integer defaultbank);

	void deleteById(long oId);

	void updateInValid(Integer oid);

	List<RdMmBank> findBankByIdCardAndName(String idCardCode, String accName);

	void updateBankSigningByOId(Integer bankSigning, Integer oid);
}
