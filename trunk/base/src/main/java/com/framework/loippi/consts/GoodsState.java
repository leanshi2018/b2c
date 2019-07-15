package com.framework.loippi.consts;

/**
 * 商品的各种状态
 * @author cgl
 * 2015年08月06日10:30:05
 */
public class GoodsState {
	/**
	 * 商品推荐:是
	 */
	public final static int GOODS_COMMEND_YES = 1;
	
	/**
	 * 商品推荐:否
	 */
	public final static int GOODS_COMMEND_NO = 0;
	
	/**
	 * 商品上架状态
	 */
	public final static int GOODS_ON_SHOW = 1;
	
	/**
	 * 商品下架状态
	 */
	public final static int GOODS_OFF_SHOW = 2;
	
	/**
	 * 商品状态仓库中
	 */
	public final static int GOODS_STORE_SHOW = 2;
	
	/**
	 * 商品定时商家状态
	 */
	public final static int GOODS_PARPER_SHOW = 3;
	
	/**
	 * 商品状态审核通过
	 */
	public final static int GOODS_OPEN_STATE = 1;
	
	/**
	 * 商品状态关闭(违规下架)
	 */
	public final static int GOODS_CLOSE_STATE = 40;
	
	/**
	 * 商品状态审核未通过
	 */
	public final static int GOODS_APPLY_OFF = 2;
	
	/**
	 * 商品状态待审核
	 */
	public final static int GOODS_APPLY_PREPARE = 0;
	
	/**
	 * 商品所在店铺状态 开启 
	 */
	public final static int GOODS_STORE_OPEN = 0;
	
	/**
	 * 商品所在店铺状态 关闭
	 */
	public final static int GOODS_STORE_CLOSE = 1;
	
	/**
	 * 未删除
	 */
	public final static int GOODS_NOT_DELETE = 2;
	
	/**
	 * 已删除
	 */
	public final static int GOODS_HAS_DELETE = 1;
    /**
     * 分类：1展示
     */
    public final static int GOODSCLASS_SHOW = 1;
    /**
     * 分类：父ID：0
     */
    public final static int GOODSCLASS_GCPARENTID_ZERO = 0;


    /**
     * 平台ID：0L
     */
    public final static Long PLATFORM_STORE_ID = 0L;

    /**
     *默认排序
     */
    public final static int DEFAULT_ORDER = 1;



    /**
     * 默认销量
     */
    public final static int DEFAULT_SALENUM = 0;


    /**
     * 默认平台名字
     */
    public final static String DEFAULT_PLATFORM_NAME ="自营商店";


}
