package com.framework.loippi.service.impl.user;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.framework.loippi.consts.UpdateMemberInfoStatus;
import com.framework.loippi.dao.user.RdMmAccountInfoDao;
import com.framework.loippi.dao.user.RdMmBasicInfoDao;
import com.framework.loippi.dao.user.RdMmEditDao;
import com.framework.loippi.dao.user.RdMmRelationDao;
import com.framework.loippi.dao.user.RdRaBindingDao;
import com.framework.loippi.dao.user.RdRanksDao;
import com.framework.loippi.dao.user.RdSysPeriodDao;
import com.framework.loippi.entity.user.OldSysRelationship;
import com.framework.loippi.entity.user.RaMember;
import com.framework.loippi.entity.user.RdMmAccountInfo;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdMmEdit;
import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.entity.user.RdRanks;
import com.framework.loippi.service.RedisService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.user.OldSysRelationshipService;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.utils.Paramap;


/**
 * SERVICE - RdMmBasicInfo(会员基础信息)
 *
 * @author zijing
 * @version 2.0
 */
@Service
@Transactional
public class RdMmBasicInfoServiceImpl extends GenericServiceImpl<RdMmBasicInfo, Long> implements RdMmBasicInfoService {

    @Autowired
    private RdMmBasicInfoDao rdMmBasicInfoDao;
    @Autowired
    private RdSysPeriodDao rdSysPeriodDao;
    @Autowired
    private RdMmRelationDao rdMmRelationDao;
    @Autowired
    private RdMmAccountInfoDao rdMmAccountInfoDao;
    @Autowired
    private RdMmEditDao rdMmEditDao;
    @Autowired
    private RdRanksDao rdRanksDao;
    @Autowired
    private OldSysRelationshipService oldSysRelationshipService;
    @Resource
    private RdRaBindingDao rdRaBindingDao;
    @Resource
    private RedisService redisService;


    @Autowired
    public void setGenericDao() {
        super.setGenericDao(rdMmBasicInfoDao);
    }

    @Override
    public void addUser(RdMmBasicInfo rdMmBasicInfo, RdMmAccountInfo rdMmAccountInfo, RdMmRelation rdMmRelation,
        Integer registerType) {
        String newMmCode = "";
        if (registerType == 1) {
            String oldMmCode = rdMmBasicInfoDao.getMaxMmCode();
            newMmCode = (Long.parseLong(oldMmCode) + 1) + "";
            if (newMmCode.endsWith("4")) {
                newMmCode = (Long.parseLong(newMmCode) + 1) + "";
            }
        }
        RaMember raMember = null;

        if (registerType == 2) {
            raMember = redisService.get(rdMmRelation.getSponsorCode(), RaMember.class);
            if (raMember == null) {
                throw new RuntimeException("老用户验证信息失效,请重新验证！");
            }
            String oldMmCode = raMember.getMmCode();
            Integer length = oldMmCode.length();
            for (int i = length; i < 9; i++) {
                if (i == 8) {
                    oldMmCode = "1" + oldMmCode;
                } else {
                    oldMmCode = "0" + oldMmCode;
                }
            }
            newMmCode = oldMmCode;
        }
        rdMmBasicInfo.setMmCode(newMmCode);
        rdMmBasicInfo.setPushStatus(1);//默认开启推送
        rdMmAccountInfo.setMmCode(newMmCode);
        rdMmAccountInfo.setPaymentPhone(rdMmBasicInfo.getMobile());
        rdMmRelation.setMmCode(newMmCode);
        rdMmRelation.setAPpv(BigDecimal.ZERO);
        RdMmEdit rdMmEdit = new RdMmEdit();
        rdMmEdit.setMmCode(newMmCode);
        //会员等级相关
        List<RdRanks> rdRanksList = rdRanksDao.findByParams(Paramap.create().put("rankClass", 0));
        if (rdRanksList != null && rdRanksList.size() > 0) {
            rdMmRelation.setRank(rdRanksList.get(0).getRankId());
        }
        //邀请相关 新用户
        if (registerType == 1) {
            List<RdMmBasicInfo> oldRdMmBasicInfoList = rdMmBasicInfoDao
                .findByParams(Paramap.create().put("mmCode", rdMmRelation.getSponsorCode()));
            if (oldRdMmBasicInfoList == null || oldRdMmBasicInfoList.size() != 1) {
                throw new RuntimeException("邀请码不存在！");
            }
            List<RdMmRelation> oldRdMmRelation = rdMmRelationDao
                .findByParams(Paramap.create().put("mmCode", rdMmRelation.getSponsorCode()));
            rdMmRelation.setSponsorName(oldRdMmBasicInfoList.get(0).getMmName());
            rdMmRelation.setRaSponsorStatus(1);
            rdMmRelation.setNOFlag(1);
            rdMmRelation.setRaStatus(0);
            rdMmRelation.setRaShopYn(0);
        }
        //老用户
        if (registerType == 2) {
            OldSysRelationship oldSysRelationship = oldSysRelationshipService.find("oMcode", raMember.getMmCode());
            if (oldSysRelationship.getNYnRegistered()==1) {
                throw new RuntimeException("老用户已注册或者绑定！");
            }
            Map<String, String> map = oldSysRelationshipService.findOldSysSpcode(oldSysRelationship.getOSpcode());
            System.out.println(map);
            String sponsorCode = map.get("mmCode");
            String index = map.get("index");
            rdMmRelation.setSponsorCode(sponsorCode);
            //无限循环的
            if ("-1".equals(sponsorCode)) {
                throw new RuntimeException("用户上级绑定数据出现循环！");
            }
            if (!"101000158".equals(sponsorCode)) {
                List<RdMmBasicInfo> oldRdMmBasicInfoList = rdMmBasicInfoDao
                    .findByParams(Paramap.create().put("mmCode", rdMmRelation.getSponsorCode()));
                rdMmRelation.setSponsorName(oldRdMmBasicInfoList.get(0).getMmName());
            } else {
                rdMmRelation.setSponsorName("美国公司节点1");
            }
            if ("1".equals(index)) {
                rdMmRelation.setRaSponsorStatus(1);
            } else {
                rdMmRelation.setRaSponsorStatus(0);
            }
            //RdRaBinding rdRaBinding = new RdRaBinding();
            //rdRaBinding.setBindingBy(rdMmBasicInfo.getMmNickName());
            //rdRaBinding.setBindingDate(new Date());
            //rdRaBinding.setBindingStatus(1);
            //rdRaBinding.setRaCode(oldSysRelationship.getOMcode());
            ////rdRaBinding.setRaIdCode();
            ////rdRaBinding.setRaIdType();
            ////rdRaBinding.setRaName(raMember.getMmName());
            //rdRaBinding.setRaNickName(oldSysRelationship.getONickname());
            //rdRaBinding.setRaSponsorName(map.get("mmName"));
            //rdRaBinding.setRaStatus(oldSysRelationship.getOStatus());
            //rdRaBinding.setRaSponsorCode(map.get("mmCode"));
            //rdRaBinding.setRdCode(newMmCode);
            //rdRaBindingDao.insert(rdRaBinding);

            //如果老用户开店 直接升级vip会员 后期客户要求取消 只要注册成功就是直接升级vip会员
            //if (oldSysRelationship.getOStatus() == 1) {
/*                rdRanksList = rdRanksDao.findByParams(Paramap.create().put("rankClass", 1));
                if (rdRanksList != null && rdRanksList.size() > 0) {
                    rdMmRelation.setRank(rdRanksList.get(0).getRankId());
                }*/
            rdMmRelation.setRank(3);//如果是老会员注册，直接将等级设置为初级代理店 TODO 2019-07-19修改业务需求
            rdMmRelation.setNOFlag(2);
            rdMmRelation.setRaStatus(1);
            rdMmRelation.setRaShopYn(1);
            //}
            oldSysRelationship.setNYnRegistered(1);
            //oldSysRelationship.setNMcode(newMmCode);
            oldSysRelationship.setUpdateTime(new Date());
            if (oldSysRelationship.getId()!=null){
                oldSysRelationshipService.update(oldSysRelationship);
            }else{
                throw new RuntimeException("用户数据出现问题！");
            }

            redisService.delete(rdMmRelation.getSponsorCode());
        }

        //获取当前时间设置的业务周期
        String period = rdSysPeriodDao.getSysPeriodService(new Date());
        rdMmBasicInfo.setCreationPeriod(period);
        rdMmBasicInfoDao.insert(rdMmBasicInfo);
        rdMmBasicInfo.setMmCode(newMmCode);
        rdMmRelationDao.insert(rdMmRelation);
        rdMmAccountInfoDao.insert(rdMmAccountInfo);
        rdMmEditDao.insert(rdMmEdit);
    }

    /**
     * 登录时验证用户是否存在
     */
    @Override
    public RdMmBasicInfo findMemberExist(RdMmBasicInfo member) {
        return rdMmBasicInfoDao.findMemberExist(member);
    }

    @Override
    public void updateMember(RdMmBasicInfo member, Integer type) {
        // TODO: 2018/12/10
        RdMmBasicInfo rdMmBasicInfo = rdMmBasicInfoDao.findByParams(Paramap.create().put("mmCode", member.getMmCode()))
            .get(0);
        RdMmEdit rdMmEdit = new RdMmEdit();
        rdMmEdit.setMmCode(member.getMmCode());
        rdMmEdit.setReviewStatus(3);
        if (type == UpdateMemberInfoStatus.UPDATE_AVATAR) {

        } else if (type == UpdateMemberInfoStatus.UPDATE_NICKNAME) {
            rdMmEdit.setMmNickNameBefore(rdMmBasicInfo.getMmNickName());
            rdMmEdit.setMmNickNameAfter(member.getMmNickName());
            rdMmEdit.setUpdateType(2);
            rdMmEditDao.insert(rdMmEdit);
        } else if (type == UpdateMemberInfoStatus.UPDATE_SEX) {
            rdMmEdit.setGenderBefore(rdMmBasicInfo.getGender());
            rdMmEdit.setGenderAfter(member.getGender());
            rdMmEdit.setUpdateType(0);
            rdMmEditDao.insert(rdMmEdit);
        } else if (type == UpdateMemberInfoStatus.UPDATE_MOBILE) {
            rdMmEdit.setMobileBefore(rdMmBasicInfo.getMobile());
            rdMmEdit.setMobileAfter(member.getMobile());
            rdMmEdit.setUpdateType(0);
            rdMmEditDao.insert(rdMmEdit);
            RdMmAccountInfo rdMmAccountInfo = new RdMmAccountInfo();
            rdMmAccountInfo.setMmCode(member.getMmCode());
            rdMmAccountInfo.setPaymentPhone(member.getMobile());
            rdMmAccountInfoDao.update(rdMmAccountInfo);
        } else if (type == UpdateMemberInfoStatus.UPDATE_AREAINFO) {
            rdMmEdit.setAddProvinceIdBefore(rdMmBasicInfo.getAddProvinceId());
            rdMmEdit.setAddProvinceIdAfter(member.getAddProvinceId());

            rdMmEdit.setAddCityIdBefore(rdMmBasicInfo.getAddCityId());
            rdMmEdit.setAddCityIdAfter(member.getAddCityId());

            rdMmEdit.setAddCountryIdBefore(rdMmBasicInfo.getAddCountryId());
            rdMmEdit.setAddCountryIdAfter(member.getAddCountryId());

            rdMmEdit.setUpdateType(0);
            rdMmEditDao.insert(rdMmEdit);
        } else if (type == UpdateMemberInfoStatus.UPDATE_ADDRESS) {
            rdMmEdit.setAddDetialBefore(rdMmBasicInfo.getAddDetial());
            rdMmEdit.setAddDetialAfter(member.getAddDetial());
            rdMmEdit.setUpdateType(0);
            rdMmEditDao.insert(rdMmEdit);
        } else if (type == UpdateMemberInfoStatus.UPDATE_ADDRESS) {
            rdMmEdit.setAddDetialBefore(rdMmBasicInfo.getAddDetial());
            rdMmEdit.setAddDetialAfter(member.getAddDetial());
            rdMmEdit.setUpdateType(0);
            rdMmEditDao.insert(rdMmEdit);
        } else if (type == UpdateMemberInfoStatus.SET_PAYMENTPASSWD) {
            RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoDao
                .findByParams(Paramap.create().put("mmCode", member.getMmCode())).get(0);
            rdMmAccountInfo.setBonusStatus(0);
            rdMmAccountInfo.setRedemptionStatus(0);
            rdMmAccountInfo.setWalletStatus(0);
            rdMmAccountInfo.setPaymentPwd(member.getMobile());
            member.setMobile(rdMmBasicInfo.getMobile());
            rdMmAccountInfoDao.update(rdMmAccountInfo);
            RdMmRelation rdMmRelation = new RdMmRelation();
            rdMmRelation.setMmCode(member.getMmCode());
            rdMmRelation.setMmPointStatus(1);
            rdMmRelationDao.update(rdMmRelation);

        }else if(type == UpdateMemberInfoStatus.UPDATE_NAME){
            rdMmEdit.setMmNameBefore(rdMmBasicInfo.getMmName());
            rdMmEdit.setMmNameAfter(member.getMmName());
            rdMmEdit.setUpdateType(2);
            rdMmEditDao.insert(rdMmEdit);
        }
        rdMmBasicInfoDao.update(member);
    }

    @Override
    public int sumShare() {
        return rdMmBasicInfoDao.sumShare();
    }

    @Override
    public List<RdMmBasicInfo> findShopMember(List<String> mmCodes) {
        if (mmCodes == null || mmCodes.size() == 0) {
            return new ArrayList<>();
        }
        Map<String, Object> qyMap = new HashMap<>();
        qyMap.put("mmCodes", mmCodes);
        List<RdMmBasicInfo> list = rdMmBasicInfoDao.findByParams(qyMap);
        return list;
    }

    @Override
    public RdMmBasicInfo findByMCode(String mCode) {
        return rdMmBasicInfoDao.findByMCode(mCode);
    }


}
