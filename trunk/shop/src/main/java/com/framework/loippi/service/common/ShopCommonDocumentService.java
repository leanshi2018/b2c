package com.framework.loippi.service.common;

import com.framework.loippi.entity.common.ShopCommonDocument;
import com.framework.loippi.service.GenericService;

import java.util.List;

/**
 * SERVICE - ShopCommonDocument(系统文章表)
 * 
 * @author zijing
 * @version 2.0
 */
public interface ShopCommonDocumentService  extends GenericService<ShopCommonDocument, Long> {

    public List<ShopCommonDocument> findDocumentListByDocType(String docType);

}
