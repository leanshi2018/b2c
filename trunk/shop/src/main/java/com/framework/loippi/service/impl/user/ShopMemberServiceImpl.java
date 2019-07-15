//package com.framework.loippi.service.impl.user;
//
//import com.framework.loippi.consts.UpdateMemberInfoStatus;
//import com.framework.loippi.dao.user.ShopMemberDao;
//import com.framework.loippi.entity.PayCommon;
//import com.framework.loippi.entity.user.RdMmAccountInfo;
//import com.framework.loippi.entity.user.RdMmBasicInfo;
//import com.framework.loippi.entity.user.ShopMember;
//import com.framework.loippi.entity.user.ShopMemberRegisterNum;
//import com.framework.loippi.mybatis.paginator.domain.PageList;
//import com.framework.loippi.service.TwiterIdService;
//import com.framework.loippi.service.impl.GenericServiceImpl;
//import com.framework.loippi.service.user.RdMmAccountInfoService;
//import com.framework.loippi.service.user.RdMmBasicInfoService;
//import com.framework.loippi.service.user.RdMmEditService;
//import com.framework.loippi.service.user.ShopMemberService;
//import com.framework.loippi.support.Page;
//import com.framework.loippi.support.Pageable;
//import com.framework.loippi.utils.Paramap;
//import com.framework.loippi.vo.stats.StatsCountVo;
//import com.framework.loippi.vo.user.UserInfoDto;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.Resource;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * 项目名称：leimingtech-admin
// * 类名称：MemberServiceImpl
// * 类描述：service实现类
// * 修改备注：
// */
//@Service
//public class ShopMemberServiceImpl extends GenericServiceImpl<ShopMember, Long> implements ShopMemberService {
//
//    @Autowired
//    private ShopMemberDao memberDao;
//    @Resource
//    private RdMmAccountInfoService rdMmAccountInfoService;
//    @Resource
//    private RdMmBasicInfoService rdMmBasicInfoService;
//    @Resource
//    private RdMmEditService rdMmEditService;
//    /**
//     * 登录时验证用户是否存在
//     *
//     * @param member
//     * @return
//     */
//    @Override
//    public ShopMember findMemberExist(ShopMember member) {
//        return memberDao.findMemberExist(member);
//    }
//
//    /**
//     * 注册时验证用户是否存在
//     *
//     * @param member
//     * @return
//     */
//    @Override
//    public int findMemberExistCount(ShopMember member) {
//        return memberDao.findMemberExistCount(member);
//    }
//
//
//    public Page<ShopMember> findMemberListIsLike(Pageable pageable) {
//        PageList<ShopMember> result = memberDao.findMemberListIsLike(pageable.getParameter(), pageable.getPageBounds());
//        return new Page<ShopMember>(result, result.getPaginator().getTotalCount(), pageable);
//    }
//
//    @Override
//    public List<UserInfoDto> listByHids(Long uid, String[] hids) {
//        return memberDao.listByHids(uid, hids);
//    }
//
//    public List<ShopMemberRegisterNum> statisticsRegisterNum(ShopMemberRegisterNum shopMemberRegisterNum) {
//        return memberDao.statisticsRegisterNum(shopMemberRegisterNum);
//    }
//
//    @Override
//    public void unbindThirdparty(Integer type, Long userId) {
//        memberDao.unbindThirdparty(type, userId);
//    }
//
//    @Override
//    public List<StatsCountVo> listStatsCountVo() {
//        return memberDao.listStatsCountVo();
//    }
//
//    //批量更新
//    public void updateBatch(List<ShopMember> memberList) {
//        memberDao.updateBatch(memberList);
//    }
//
//    //批量
//    public List<ShopMember> findShopMember(List<Long> memberIds) {
//        if (memberIds == null || memberIds.size() == 0) {
//            return new ArrayList<>();
//        }
//        Map<String, Object> qyMap = new HashMap<>();
//        qyMap.put("memberIds", memberIds);
//        List<ShopMember> list = memberDao.selectMemberByParam(qyMap);
//        return list;
//    }
//
//    //转map
//    public Map<Long, ShopMember> findShopMemberMap(List<Long> memberIds) {
//        if (memberIds == null || memberIds.size() == 0) {
//            return new HashMap<>();
//        }
//        Map<String, Object> qyMap = new HashMap<>();
//        qyMap.put("memberIds", memberIds);
//        List<ShopMember> list = memberDao.selectMemberByParam(qyMap);
//        Map<Long, ShopMember> shopMemberMap = new HashMap<>();
//        for (ShopMember item : list) {
//            shopMemberMap.put(item.getId(), item);
//        }
//        return shopMemberMap;
//    }
//
//    @Override
//    public void updateMember(ShopMember member, Integer type) {
//        // TODO: 2018/12/10
////        RdMmBasicInfo rdMmBasicInfo=rdMmBasicInfoService.find("mmCode",member.getMemberInvitationCode());
////        if (type==UpdateMemberInfoStatus.UPDATE_AVATAR){
//////            rdMmBasicInfo.se(member.getMemberName());
////        }else if (type==UpdateMemberInfoStatus.UPDATE_NICKNAME){
////            rdMmBasicInfo.setMmNickName(member.getMemberName());
////        }else if (type==UpdateMemberInfoStatus.UPDATE_SEX){
////                  rdMmBasicInfo.setGender(member.getMemberSex());
////        }else if (type==UpdateMemberInfoStatus.UPDATE_BIRTHDAY){
////            rdMmBasicInfo.setBirthdate(member.getMemberBirthday());
////        }else if (type==UpdateMemberInfoStatus.UPDATE_MOBILE){
////            rdMmBasicInfo.setMobile(member.getMemberMobile());
////        }else if (type==UpdateMemberInfoStatus.UPDATE_PASSWORD){
//////            rdMmBasicInfo.set
////        }else if (type==UpdateMemberInfoStatus.UPDATE_PAYMENTPASSWD){
////
////        }else
// if (type==UpdateMemberInfoStatus.SET_PAYMENTPASSWD){
//       RdMmAccountInfo rdMmAccountInfo=rdMmAccountInfoService.find("mmCode",member.getMemberInvitationCode());
//       if (rdMmAccountInfo.getBonusStatus()==null || rdMmAccountInfo.getBonusStatus()==0){
//           rdMmAccountInfo.setBonusStatus(1);
//           rdMmAccountInfo.setRedemptionStatus(1);
//           rdMmAccountInfo.setWalletStatus(1);
//           rdMmAccountInfoService.update(rdMmAccountInfo);
//       }
//        }
//        memberDao.update(member);
//    }
//
//    @Override
//    public int sumShare() {
//        return memberDao.sumShare();
//    }
//
//}
