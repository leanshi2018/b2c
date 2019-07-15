package com.framework.loippi.result.common.document;

import com.framework.loippi.entity.common.ShopCommonDocument;
import com.framework.loippi.entity.product.ShopGoodsBrand;
import com.framework.loippi.result.common.goods.GoodsBrandResult;
import com.framework.loippi.utils.StringUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 *文章列表     关于我们
 */
@Data
public class DocumentListResult {

    /**
     * 文章标题
     */
    private String title;

    /**
     * 跳转到wap端进行展示
     */
    private String url;

    public static List<DocumentListResult> build(List<ShopCommonDocument> list, String wapServer) {
        List<DocumentListResult> results = new ArrayList<>();
        for (ShopCommonDocument item : list) {
            if(StringUtil.isEmpty(item.getDocTitle())||item.getId()==null){
                continue;
            }
            DocumentListResult result = new DocumentListResult();
            result.setTitle(item.getDocTitle());
            result.setUrl(wapServer+item.getId()+ ".html?type=app");
            results.add(result);
        }
        return results;
    }

}
