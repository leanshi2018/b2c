package com.framework.loippi.service.order;

import com.framework.loippi.consts.Constants;
import com.framework.loippi.entity.PayCommon;
import com.framework.loippi.entity.order.*;
import com.framework.loippi.entity.trade.ShopRefundReturn;
import com.framework.loippi.entity.trade.ShopReturnOrderGoods;
import com.framework.loippi.entity.user.RdMmAddInfo;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.ware.RdWarehouse;
import com.framework.loippi.pojo.common.CensusVo;
import com.framework.loippi.pojo.common.MemberShippingBehaviorVo;
import com.framework.loippi.pojo.selfMention.OrderInfo;
import com.framework.loippi.result.selfMention.SelfMentionOrderStatistics;
import com.framework.loippi.result.sys.OrderView;
import com.framework.loippi.service.GenericService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.vo.activity.ActivityStatisticsVo;
import com.framework.loippi.vo.goods.GoodsStatisticsVo;
import com.framework.loippi.vo.order.*;
import com.framework.loippi.vo.stats.StatsCountVo;
import com.framework.loippi.vo.store.StoreStatisticsVo;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SERVICE - ShopOrderSplit(订单分单表)
 *
 * @author zc
 * @date 2020/11/05
 */
public interface ShopOrderSplitService extends GenericService<ShopOrderSplit, Long> {

}
