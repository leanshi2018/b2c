package com.framework.loippi.result.common.activity;

import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsEvaluateKeywords;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.utils.JacksonUtil;
import com.framework.loippi.utils.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringEscapeUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/*
 * Result - 商品详情   活动使用的商品详情信息
 *
 * @author Loippi team
 * @version 2.0
 * @description 商品详情
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityGoodsDetailResult {


    /**
     * 商品id
     */
    private Long goodsIs;

    /**
     * 默认选中的规则id
     */
    private Long specId;

    private Long classId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品轮播图
     */
    private String listImage;

    /**
     * 零售价
     */
    private java.math.BigDecimal goodsRetailPrice;

    /**
     * 会员价格
     */
    private java.math.BigDecimal goodsMemberPrice;

    /**
     * ppv
     */
    private BigDecimal ppv;
    /**
     * 商品描述
     */
    private String goodsSubtitle;
    /**
     * 商品促销语
     */
    private String goodsKeywords;
    /**
     * 售出数量
     */

    private Integer saleNum;

    /**
     * 商品收藏数量
     */
    private Integer goodsCollect = 0;

    //当前会员是否收藏 0适合收藏,1是未收藏
    private Integer favGoods = 0;


    //商品详情,手机版
    private String mobileBody;

    //活动商品总数量
    private Long stockNumber;

    private List<ActivityGoodsSpesDescItem> specItemList;

    private List<ActivityGoodsSkuInfo> products;

    //评价
    private List<ActivityEvaluateGoodsResult> evaluateList;

    private Long evaluateCount;

    private String shareUrl;

    //默认的商品展示图片
    private String defaultImage;

    //聊天帐号id
    private String chatAccount;
    /**
     * 品牌名称
     */
    private String brandName;
    /**
     * 品牌id
     */
    private Long brandId;
    /**
     * 图片
     */
    private String brandPic;
    /**
     * 星级
     */
    private Integer stars;

    //精品图片
    private Boolean isRecomend = false;
    private String recommendImages;

    private Integer goodsType;
    /**
     * 好评率
     */
    private Double evaluateRate;

    /**
     * 品牌描述
     */
    private String description;

    /**
     * 满减运费金额
     */
    private BigDecimal shippingCouponAmount;

    /**
     * 关键字集合
     */
    List<ShopGoodsEvaluateKeywords> shopGoodsEvaluateKeywordsList=new ArrayList<ShopGoodsEvaluateKeywords>();

    public static ActivityGoodsDetailResult build(ShopGoods item, String prefix, String wapServer) {
        ActivityGoodsDetailResult result = new ActivityGoodsDetailResult();
        Optional<ActivityGoodsDetailResult> opt = Optional.ofNullable(result);
        result.setGoodsIs(Optional.ofNullable(item.getId()).orElse(0L));//商品id
        result.setClassId(Optional.ofNullable(item.getGcId()).orElse(-1L));//分类id
        result.setGoodsName(Optional.ofNullable(item.getGoodsName()).orElse(""));//商品名称
        result.setGoodsKeywords(Optional.ofNullable(item.getGoodsKeywords()).orElse(""));//商品促销语
        result.setGoodsSubtitle(Optional.ofNullable(item.getGoodsSubtitle()).orElse(""));//商品副标题,描述
        result.setGoodsRetailPrice(Optional.ofNullable(item.getGoodsRetailPrice()).orElse(BigDecimal.ZERO));//销售价
        result.setGoodsMemberPrice(Optional.ofNullable(item.getGoodsMemberPrice()).orElse(BigDecimal.ZERO));//销售价
        result.setPpv(Optional.ofNullable(item.getPpv()).orElse(BigDecimal.ZERO));//ppv
        result.setGoodsType(Optional.ofNullable(item.getGoodsType()).orElse(1));
        result.setSpecId(Optional.ofNullable(item.getSpecId()).orElse(-1L));//规格id
        result.setListImage(Optional.ofNullable(prefix + item.getGoodsImageMore()).orElse(""));//轮播图
        result.setDefaultImage(Optional.ofNullable(prefix + item.getGoodsImage()).orElse(""));
        result.setEvaluateRate(Optional.ofNullable(item.getEvaluaterate()).orElse(1D));//好评率
        result.setShippingCouponAmount(new BigDecimal("199.00"));//TODO
        //手机端详情拼接
        String mobileBody = StringEscapeUtils.unescapeHtml4(item.getMobileBody());
        if (!StringUtil.isEmpty(mobileBody)) {
            List<Map> mobileBodyMap = JacksonUtil.convertList(mobileBody, Map.class);
            StringBuilder strBuilder = new StringBuilder("");
            for (Map<String, String> mobileBodyItem : mobileBodyMap) {
                if (mobileBodyItem.get("type").equals("image")) {
                    strBuilder.append("<img src='" + prefix + mobileBodyItem.get("value") + "' width='100%'>");
                }
                if (mobileBodyItem.get("type").equals("text")) {
                    strBuilder.append("<p>" + mobileBodyItem.get("value") + "</p>");
                }
            }
            result.setMobileBody(Optional.ofNullable(prefix + strBuilder.toString()).orElse(""));
        }
        result.setGoodsCollect(Optional.ofNullable(item.getGoodsCollect()).orElse(0));
        result.setSaleNum(Optional.ofNullable(item.getSalenum()).orElse(0));
        result.setStockNumber(Optional.ofNullable(item.getStock()).orElse(0l));
        // 市场价不受活动影响
        StringBuffer shareUrl = new StringBuffer();
        shareUrl.append(wapServer);
        shareUrl.append("/wap/goods/detail/");
        shareUrl.append(item.getId());
        shareUrl.append(".html");
        result.setShareUrl(shareUrl.toString());
        return result;
    }


}
