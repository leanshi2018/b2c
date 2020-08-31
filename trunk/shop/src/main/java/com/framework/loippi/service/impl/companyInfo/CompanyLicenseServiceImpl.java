package com.framework.loippi.service.impl.companyInfo;

import com.framework.loippi.entity.companyInfo.CompanyLicense;
import com.framework.loippi.service.companyInfo.CompanyLicenseService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CompanyLicenseServiceImpl extends GenericServiceImpl<CompanyLicense, Long> implements CompanyLicenseService {
}
