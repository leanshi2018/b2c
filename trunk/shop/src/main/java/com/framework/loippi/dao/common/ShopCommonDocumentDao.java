package com.framework.loippi.dao.common;

import com.framework.loippi.entity.common.ShopCommonDocument;
import com.framework.loippi.mybatis.dao.GenericDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * DAO - ShopCommonDocument(系统文章表)
 * 
 * @author zijing
 * @version 2.0
 */
public interface ShopCommonDocumentDao  extends GenericDao<ShopCommonDocument, Long> {

    public List<ShopCommonDocument> findDocumentListByDocType(@Param("docType")String docType);

}
