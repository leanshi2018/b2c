package com.framework.loippi.service.impl.common;


import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.framework.loippi.dao.common.DailyMemCensusDao;
import com.framework.loippi.dao.user.RdMmBasicInfoDao;
import com.framework.loippi.dao.user.RdMmRelationDao;
import com.framework.loippi.entity.common.DailyMemCensus;
import com.framework.loippi.pojo.common.MemCensusVo;
import com.framework.loippi.pojo.common.RankNumVo;
import com.framework.loippi.service.common.DailyMemCensusService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.utils.Paramap;


/**
 * 日会员信息统计
 */
@Service
@Slf4j
@Transactional
public class DailyMemCensusServiceImpl extends GenericServiceImpl<DailyMemCensus, Long> implements DailyMemCensusService {
    @Resource
    private DailyMemCensusDao dailyMemCensusDao;
    @Resource
    private RdMmBasicInfoDao rdMmBasicInfoDao;
    @Resource
    private RdMmRelationDao rdMmRelationDao;
    /**
     * 获取统计当日app会员信息
     */
    @Override
    public void getDailyMemCensus() {
        Calendar instance = Calendar.getInstance();
        instance.setTime(new Date());
        instance.add(Calendar.DATE,-1);
        Date time = instance.getTime();
        //1获取时间确定reportCode
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -1);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String reportCode = format.format(time);
        HashMap<String, Object> map = new HashMap<>();
        String start=reportCode+" 00:00:00";
        String end=reportCode+" 23:59:59";
        map.put("start",start);
        map.put("end",end);
        //2.查询是否存在reportCode数据
        List<DailyMemCensus> list = dailyMemCensusDao.findByParams(Paramap.create().put("reportCode", reportCode));
        if(list!=null&&list.size()>0){
            System.out.println(reportCode+"日已存在记录");
            return;
        }
        //3.生成当日会员信息统计记录
        DailyMemCensus dailyMemCensus = new DailyMemCensus();
        dailyMemCensus.setReportCode(reportCode);
        dailyMemCensus.setStatisticalTime(new Date());
        Long count = rdMmBasicInfoDao.count();
        dailyMemCensus.setMemTotal(count);
        Long dailyMem=rdMmBasicInfoDao.findNewMem(map);
        dailyMemCensus.setDailyMem(dailyMem);
        map.put("NOFlag",1);
        Long dailyNewMem=rdMmBasicInfoDao.findNewMem(map);
        dailyMemCensus.setDailyNewMem(dailyNewMem);
        map.put("NOFlag",2);
        Long dailyOldMem=rdMmBasicInfoDao.findNewMem(map);
        dailyMemCensus.setDailyOldMem(dailyOldMem);
        Long newMem = rdMmRelationDao.count(Paramap.create().put("nOFlag",1));
        dailyMemCensus.setNewMem(newMem);
        Long oldMem = rdMmRelationDao.count(Paramap.create().put("nOFlag",2));
        dailyMemCensus.setOldMem(oldMem);
        map.put("rank",0);
        MemCensusVo memCensusVo=rdMmRelationDao.getMemAtotal(map);
        if(memCensusVo!=null){
            dailyMemCensus.setPayCommonMem(memCensusVo.getNum());
            dailyMemCensus.setPayCommonUnitPrice(memCensusVo.getAmountTotal().divide(new BigDecimal(Long.toString(memCensusVo.getNum())),2,BigDecimal.ROUND_HALF_UP));
        }else {
            dailyMemCensus.setPayCommonMem(0L);
            dailyMemCensus.setPayCommonUnitPrice(new BigDecimal("0.00"));
        }
        Long noPayCommonMem=rdMmRelationDao.getNoPayCommonMem();
        dailyMemCensus.setNoPayCommonMem(noPayCommonMem);
        dailyMemCensus.setD0Num(0L);
        dailyMemCensus.setD1Num(0L);
        dailyMemCensus.setD2Num(0L);
        dailyMemCensus.setD3Num(0L);
        dailyMemCensus.setD4Num(0L);
        dailyMemCensus.setD5Num(0L);
        dailyMemCensus.setD6Num(0L);
        dailyMemCensus.setD7Num(0L);
        dailyMemCensus.setD8Num(0L);
        dailyMemCensus.setD9Num(0L);
        List<RankNumVo> rankNumVos=rdMmRelationDao.findRankNum();
        if(rankNumVos!=null&&rankNumVos.size()>0){
            for (RankNumVo rankNumVo : rankNumVos) {
                if(rankNumVo.getRank()==0){
                    dailyMemCensus.setD0Num(rankNumVo.getNum());
                }
                if(rankNumVo.getRank()==1){
                    dailyMemCensus.setD1Num(rankNumVo.getNum());
                }
                if(rankNumVo.getRank()==2){
                    dailyMemCensus.setD2Num(rankNumVo.getNum());
                }
                if(rankNumVo.getRank()==3){
                    dailyMemCensus.setD3Num(rankNumVo.getNum());
                }
                if(rankNumVo.getRank()==4){
                    dailyMemCensus.setD4Num(rankNumVo.getNum());
                }
                if(rankNumVo.getRank()==5){
                    dailyMemCensus.setD5Num(rankNumVo.getNum());
                }
                if(rankNumVo.getRank()==6){
                    dailyMemCensus.setD6Num(rankNumVo.getNum());
                }
                if(rankNumVo.getRank()==7){
                    dailyMemCensus.setD7Num(rankNumVo.getNum());
                }
                if(rankNumVo.getRank()==8){
                    dailyMemCensus.setD8Num(rankNumVo.getNum());
                }
                if(rankNumVo.getRank()==9){
                    dailyMemCensus.setD9Num(rankNumVo.getNum());
                }
            }
        }
        //4.写入数据
        dailyMemCensusDao.insert(dailyMemCensus);
    }
}
