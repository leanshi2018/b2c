package com.framework.loippi.dao.user;

import com.framework.loippi.entity.user.PlusProfit;
import com.framework.loippi.entity.user.RetailProfit;
import com.framework.loippi.mybatis.dao.GenericDao;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.vo.user.PlusProfitVo;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public interface PlusProfitDao extends GenericDao<PlusProfit, Long> {

    List<PlusProfit> findTimeMature(String expectTime);

    List<PlusProfit> findListTimeAsc(Paramap paramaps);

    BigDecimal countProfit(Paramap paramaps);

    List<PlusProfitVo> findPlusProfitVo(String mmCode);
}
