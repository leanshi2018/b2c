package com.framework.loippi.service.impl.common;

import com.framework.loippi.service.impl.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.common.ShopCommonDocumentDao;
import com.framework.loippi.entity.common.ShopCommonDocument;
import com.framework.loippi.service.common.ShopCommonDocumentService;

import java.util.List;

/**
 * SERVICE - ShopCommonDocument(系统文章表)
 *
 * @author zijing
 * @version 2.0
 */
@Service
public class ShopCommonDocumentServiceImpl extends GenericServiceImpl<ShopCommonDocument, Long> implements ShopCommonDocumentService {

    @Autowired
    private ShopCommonDocumentDao shopCommonDocumentDao;


    @Autowired
    public void setGenericDao() {
        super.setGenericDao(shopCommonDocumentDao);
    }

    @Override
    public List<ShopCommonDocument> findDocumentListByDocType(String docType) {
        return shopCommonDocumentDao.findDocumentListByDocType(docType);
    }
}
