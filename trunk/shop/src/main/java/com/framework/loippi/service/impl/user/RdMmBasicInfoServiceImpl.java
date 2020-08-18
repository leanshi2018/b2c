package com.framework.loippi.service.impl.user;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
            rdMmBasicInfo.setMmNickName(newMmCode);
            rdMmBasicInfo.setMmName(newMmCode);
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
        rdMmRelation.setATotal(BigDecimal.ZERO);
        /*RdMmEdit rdMmEdit = new RdMmEdit();
        rdMmEdit.setMmCode(newMmCode);*/
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
            //验证原系统用户信息是否已经同步到蜗米超过一年，如果超过一年，不给老系统会员注册
            Date creationTime = oldSysRelationship.getCreationTime();
            if((new Date().getTime()/1000-creationTime.getTime()/1000)>31622400){
                throw new RuntimeException("您的老会员注册已超一年期限，无法使用该号码注册");
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
        //通联接口互通
        //进行通联进行会员数据交互存储
        /*final YunRequest request = new YunRequest("MemberService", "createMember");
        request.put("bizUserId", rdMmBasicInfo.getMmCode());
        request.put("memberType", 3);
        request.put("source", 1);
        try {
            String s = YunClient.request(request);
            Map<String, Object> map = JacksonUtil.convertMap(s);
            if(map.get("status").equals("OK")){
                String jsonStr = (String) map.get("signedValue");
                Map<String, Object> stringObjectMap = JacksonUtil.convertMap(jsonStr);
                String userId = (String) stringObjectMap.get("userId");
                rdMmBasicInfo.setTongLianId(userId);
            }else if(map.get("status").equals("error")){
                String message = (String) map.get("message");
                throw new RuntimeException(message);
            } else {
                throw new RuntimeException("通联支付注册异常");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("通联支付注册异常");
        }*/
        //获取当前时间设置的业务周期
        String period = rdSysPeriodDao.getSysPeriodService(new Date());
        rdMmBasicInfo.setCreationPeriod(period);
        RdMmEdit memberEditReview = new RdMmEdit();
        memberEditReview.setMmCode(rdMmBasicInfo.getMmCode());
        memberEditReview.setMmNameAfter(rdMmBasicInfo.getMmName());
        memberEditReview.setMmNickNameAfter(rdMmBasicInfo.getMmNickName());
        memberEditReview.setMobileAfter(rdMmBasicInfo.getMobile());
        memberEditReview.setUpdateType(0);
        memberEditReview.setUpdateTime(new Date());
        memberEditReview.setReviewStatus(3);
        rdMmBasicInfoDao.insert(rdMmBasicInfo);
        rdMmEditDao.insert(memberEditReview);
        rdMmBasicInfo.setMmCode(newMmCode);
        rdMmRelationDao.insert(rdMmRelation);
        rdMmAccountInfoDao.insert(rdMmAccountInfo);
        //rdMmEditDao.insert(rdMmEdit);
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
            //修改当前会员所有次店手机号
            List<RdMmBasicInfo> members = rdMmBasicInfoDao.findByParams(Paramap.create().put("mobile", rdMmBasicInfo.getMobile()));
            if(members!=null&&members.size()>0){
                for (RdMmBasicInfo basicInfo : members) {
                    basicInfo.setMobile(member.getMobile());
                    rdMmBasicInfoDao.update(basicInfo);
                    RdMmAccountInfo accountInfo = rdMmAccountInfoDao.findAccByMCode(basicInfo.getMmCode());
                    if(accountInfo!=null){
                        accountInfo.setPaymentPhone(member.getMobile());
                        rdMmAccountInfoDao.update(accountInfo);
                    }
                }
            }
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
        } else if (type == UpdateMemberInfoStatus.SET_PAYMENTPASSWD) {//设置支付密码

            List<RdMmBasicInfo> members = rdMmBasicInfoDao.findByParams(Paramap.create().put("mobile", member.getMobile()));
            if(members!=null&&members.size()>0){
                for (RdMmBasicInfo basicInfo : members) {
                    RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoDao
                            .findByParams(Paramap.create().put("mmCode", basicInfo.getMmCode())).get(0);
                    rdMmAccountInfo.setBonusStatus(0);
                    rdMmAccountInfo.setRedemptionStatus(0);
                    rdMmAccountInfo.setWalletStatus(0);
                    rdMmAccountInfo.setPaymentPwd(member.getPhone());
                    rdMmAccountInfoDao.update(rdMmAccountInfo);
                    RdMmRelation rdMmRelation = new RdMmRelation();
                    rdMmRelation.setMmCode(basicInfo.getMmCode());
                    rdMmRelation.setMmPointStatus(1);
                    rdMmRelationDao.update(rdMmRelation);
                }
            }

        }else if(type == UpdateMemberInfoStatus.UPDATE_NAME){
            rdMmEdit.setMmNameBefore(rdMmBasicInfo.getMmName());
            rdMmEdit.setMmNameAfter(member.getMmName());
            rdMmEdit.setUpdateType(2);
            rdMmEditDao.insert(rdMmEdit);
        }
        if(rdMmBasicInfo.getPhone()!=null){
            member.setPhone(rdMmBasicInfo.getPhone());
        }else {
            member.setPhone(null);
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

    @Override
    public List<RdMmBasicInfo> findByKeyWord(Paramap put) {
        return rdMmBasicInfoDao.findByKeyWord(put);
    }

    @Override
    public void updatePhoneStatusAndPhoneByMCode(String phone, Integer allInPayPhoneStatus, String mmCode) {
        Map<String, Object> map = new HashMap<>();
        map.put("mmCode", mmCode);
        map.put("allInPayPhone", phone);
        map.put("allInPayPhoneStatus", allInPayPhoneStatus);
        rdMmBasicInfoDao.updatePhoneStatusAndPhoneByMCode(map);
    }

    @Override
    public void updatePhoneStatusByMCode(Integer allInPayPhoneStatus, String mmCode) {
        Map<String, Object> map = new HashMap<>();
        map.put("mmCode", mmCode);
        map.put("allInPayPhoneStatus", allInPayPhoneStatus);
        rdMmBasicInfoDao.updatePhoneStatusByMCode(map);
    }

    /**
     * 注册次店会员
     * @param mmBasicInfo 主店会员基础信息
     * @param mNickName 次店会员昵称
     * @param mmAvatar
     */
    @Override
    public void addSecondaryUser(RdMmBasicInfo mmBasicInfo, String mNickName, String mmAvatar) {
        //初始化次店会员信息
        List<RdMmRelation> params = rdMmRelationDao.findByParams(Paramap.create().put("mmCode",mmBasicInfo.getMmCode()));
        if(params==null||params.size()==0){
            throw new RuntimeException("主店会员关系表信息异常");
        }
        RdMmRelation relation = params.get(0);
        RdMmAccountInfo accountInfo = rdMmAccountInfoDao.findAccByMCode(mmBasicInfo.getMmCode());
        if(accountInfo==null){
            throw new RuntimeException("主店会员积分表信息异常");
        }
        //1.初始化次店会员基础表信息
        Optional<RdMmBasicInfo> optional = Optional.ofNullable(mmBasicInfo);
        RdMmBasicInfo secondaryUser = new RdMmBasicInfo();
        String oldMmCode = rdMmBasicInfoDao.getMaxMmCode();
        String newMmCode = (Long.parseLong(oldMmCode) + 1) + "";
        if (newMmCode.endsWith("4")) {
            newMmCode = (Long.parseLong(newMmCode) + 1) + "";
        }
        secondaryUser.setMmCode(newMmCode);
        secondaryUser.setMmName(newMmCode);
        secondaryUser.setMmNickName(mNickName);
        secondaryUser.setMmAvatar(mmAvatar);
        secondaryUser.setMobile(mmBasicInfo.getMobile());
        secondaryUser.setIdType(optional.map(RdMmBasicInfo::getIdType).orElse(1));
        secondaryUser.setIdCode(optional.map(RdMmBasicInfo::getIdCode).orElse(""));
        secondaryUser.setGender(optional.map(RdMmBasicInfo::getGender).orElse(0));
        secondaryUser.setCreationDate(new Date());
        String periodCode = rdSysPeriodDao.getSysPeriodService(new Date());
        if(periodCode!=null){
            secondaryUser.setCreationPeriod(periodCode);
        }else {
            secondaryUser.setCreationPeriod(null);
        }
        secondaryUser.setPushStatus(1);
        secondaryUser.setAllInPayPhoneStatus(0);
        secondaryUser.setAllInContractStatus(0);
        secondaryUser.setMainFlag(2);//设置次店
        //进行通联进行会员数据交互存储
/*        final YunRequest request = new YunRequest("MemberService", "createMember");
        request.put("bizUserId", secondaryUser.getMmCode());
        request.put("memberType", 3);
        request.put("source", 1);
        try {
            String s = YunClient.request(request);
            Map<String, Object> map = JacksonUtil.convertMap(s);
            if(map.get("status").equals("OK")){
                String jsonStr = (String) map.get("signedValue");
                Map<String, Object> stringObjectMap = JacksonUtil.convertMap(jsonStr);
                String userId = (String) stringObjectMap.get("userId");
                secondaryUser.setTongLianId(userId);
            }else if(map.get("status").equals("error")){
                String message = (String) map.get("message");
                throw new RuntimeException(message);
            } else {
                throw new RuntimeException("通联支付注册异常");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("通联支付注册异常");
        }*/
        rdMmBasicInfoDao.insert(secondaryUser);
        //2.初始化关系表
        RdMmRelation rdMmRelation = new RdMmRelation();
        rdMmRelation.setMmCode(secondaryUser.getMmCode());
        rdMmRelation.setRank(0);
        rdMmRelation.setARetail(BigDecimal.ZERO);
        rdMmRelation.setAPpv(BigDecimal.ZERO);
        rdMmRelation.setLoginPwd(relation.getLoginPwd());
        rdMmRelation.setMmStatus(0);
        rdMmRelation.setMmPointStatus(relation.getMmPointStatus());
        rdMmRelation.setSponsorCode(mmBasicInfo.getMmCode());
        rdMmRelation.setSponsorName(mmBasicInfo.getMmName());
        rdMmRelation.setRaSponsorStatus(1);
        rdMmRelation.setRaStatus(0);
        rdMmRelation.setRaShopYn(0);
        rdMmRelation.setRaBindingDate(new Date());
        rdMmRelation.setIsVip(0);
        rdMmRelation.setNOFlag(1);
        rdMmRelation.setATotal(BigDecimal.ZERO);
        rdMmRelation.setPopupFlag(0);
        rdMmRelation.setLastPayTime(secondaryUser.getCreationDate());
        rdMmRelationDao.insert(rdMmRelation);
        //3.初始化积分表
        RdMmAccountInfo rdMmAccountInfo = new RdMmAccountInfo();
        rdMmAccountInfo.setMmCode(secondaryUser.getMmCode());
        rdMmAccountInfo.setBonusStatus(accountInfo.getBonusStatus());
        rdMmAccountInfo.setBonusBlance(BigDecimal.ZERO);
        rdMmAccountInfo.setWalletStatus(accountInfo.getWalletStatus());
        rdMmAccountInfo.setWalletBlance(BigDecimal.ZERO);
        rdMmAccountInfo.setRedemptionStatus(accountInfo.getRedemptionStatus());
        rdMmAccountInfo.setRedemptionBlance(BigDecimal.ZERO);
        rdMmAccountInfo.setPaymentPhone(secondaryUser.getMobile());
        if(accountInfo.getPaymentPwd()!=null){
            rdMmAccountInfo.setPaymentPwd(accountInfo.getPaymentPwd());
        }
        rdMmAccountInfo.setLastWithdrawalTime(secondaryUser.getCreationDate());
        rdMmAccountInfo.setAutomaticWithdrawal(0);
        rdMmAccountInfo.setWithdrawalLine(new BigDecimal("500"));
        rdMmAccountInfoDao.insert(rdMmAccountInfo);
        //4.会员修改记录表初始化
        RdMmEdit memberEditReview = new RdMmEdit();
        memberEditReview.setMmCode(secondaryUser.getMmCode());
        memberEditReview.setMmNameAfter(secondaryUser.getMmName());
        memberEditReview.setMmNickNameAfter(secondaryUser.getMmNickName());
        memberEditReview.setMobileAfter(secondaryUser.getMobile());
        memberEditReview.setUpdateType(0);
        memberEditReview.setUpdateTime(new Date());
        memberEditReview.setReviewStatus(3);
        rdMmEditDao.insert(memberEditReview);
    }

}
