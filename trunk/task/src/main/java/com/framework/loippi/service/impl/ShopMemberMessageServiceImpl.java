package com.framework.loippi.service.impl;

import com.framework.loippi.dao.ShopCommonMessageDao;
import com.framework.loippi.dao.ShopMemberMessageDao;
import com.framework.loippi.dto.UserMessageDto;
import com.framework.loippi.entity.ShopCommonMessage;
import com.framework.loippi.entity.ShopMemberMessage;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.mybatis.paginator.domain.PageList;
import com.framework.loippi.service.ShopMemberMessageService;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.Paramap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * SERVICE - ShopMemberMessage(用户消息)
 *
 * @author zijing
 * @version 2.0
 */
@Service
public class ShopMemberMessageServiceImpl extends GenericServiceImpl<ShopMemberMessage, Long>
        implements ShopMemberMessageService {

    @Autowired
    private ShopMemberMessageDao shopMemberMessageDao;

    @Autowired
    private TwiterIdService twiterIdService;

    @Autowired
    private ShopCommonMessageDao shopCommonMessageDao;

    @Autowired
    public void setGenericDao() {
        super.setGenericDao(shopMemberMessageDao);
    }

    @Override
    public Long countMessage(Long uid, Integer bizType) {
        return shopMemberMessageDao.countMessage(uid, bizType);
    }

    @Override
    public Page<UserMessageDto> findMessagePage(Pageable pageable) {
        PageList<UserMessageDto> result = shopMemberMessageDao.findMessagePage(pageable.getParameter(), pageable.getPageBounds());
        return new Page<UserMessageDto>(result, result.getPaginator().getTotalCount(), pageable);
    }

    @Override
    public Page<UserMessageDto> findMsgOrderPage(Pageable pageable) {
        PageList<UserMessageDto> result = shopMemberMessageDao.findMsgOrderPage(pageable.getParameter(), pageable.getPageBounds());
        return new Page<UserMessageDto>(result, result.getPaginator().getTotalCount(), pageable);
    }

    @Override
    public UserMessageDto findLast(Long uid, Integer bizType) {
        Pageable pageable = new Pageable(1, 1);
        pageable.setParameter(Paramap.create().put("uid", uid).put("bizType", bizType));
        pageable.setOrderDirection(Order.Direction.DESC);
        pageable.setOrderProperty("create_time");
        List<UserMessageDto> data = findMessagePage(pageable).getContent();
        return data.size() > 0 ? data.get(0) : null;
    }

    @Override
    public UserMessageDto findLastByTypes(Long uid, List<Integer> bizTypeList) {
        Pageable pageable = new Pageable(1, 1);
        pageable.setParameter(Paramap.create().put("uid", uid).put("bizTypeList", bizTypeList));
        pageable.setOrderDirection(Order.Direction.DESC);
        pageable.setOrderProperty("create_time");
        List<UserMessageDto> data = findMessagePage(pageable).getContent();
        return data.size() > 0 ? data.get(0) : null;
    }

    @Override
    public void deleteMessage(Long uid, Integer type) {
        shopMemberMessageDao.deleteMessage(uid, type);
    }

    @Override
    public void saveLogisticMsg(Integer type, String title, String subTitle, String img, Long orderId, Long memberId) {
        Long twiterId = twiterIdService.getTwiterId();
        ShopCommonMessage commonMsg = new ShopCommonMessage();
        commonMsg.setId(twiterId);
        commonMsg.setTitle("");
        if (type == 0) {
            commonMsg.setSubject("订单已经支付成功，系统已经提交到仓库审核，请随时查看订单和物流状态");
        } else {
            commonMsg.setSubject("订单已经签收，你购买的青少年成人全脂牛奶已经收货，请查看物流信息和及时给予评价");
        }
        commonMsg.setTitle(title); //澳洲Maxigenes美可卓蓝胖子青少年成人全脂牛奶
        commonMsg.setContent(subTitle); //商品副标题
        commonMsg.setImage(img);
        commonMsg.setBizId(orderId);
        commonMsg.setBizType(3); //消息类型 1-消息通知  2-促销活动  3-交易物流
        commonMsg.setCreateTime(new Date());
        commonMsg.setSendUid(memberId + "");
        commonMsg.setType(1); //1-系统消息  2-短信消息
        commonMsg.setUType(1); //用户类型 1-个人  2-分组  3-全部用户
        commonMsg.setOnLine(1); //草稿 1-在线  2-草稿
        commonMsg.setIsTop(2); //1-置顶  2-未置顶
        shopCommonMessageDao.insert(commonMsg);

        ShopMemberMessage memberMsg = new ShopMemberMessage();
        memberMsg.setId(twiterIdService.getTwiterId());
        memberMsg.setMsgId(twiterId);
        memberMsg.setBizType(3);
        memberMsg.setIsRead(2);
        memberMsg.setCreateTime(new Date());
        memberMsg.setUid(memberId);
        shopMemberMessageDao.insert(memberMsg);
    }

    /**
     * 根据是否已读查询消息
     *
     * @param paramap
     * @return
     */
    @Override
    public List<UserMessageDto> findMessagePageByIsRead(Paramap paramap) {
        List<UserMessageDto> result = shopMemberMessageDao.findMessagePageByIsRead(paramap);
        return result;
    }

    @Override
    public void saveLeavingMessage(String title, String content, String img, Long memberId) {
        Long twiterId = twiterIdService.getTwiterId();
        ShopCommonMessage commonMsg = new ShopCommonMessage();
        commonMsg.setId(twiterId);
        commonMsg.setTitle(title);
        commonMsg.setContent(content);
        commonMsg.setImage(img);
        commonMsg.setBizId(0l);
        commonMsg.setBizType(4); //1-消息通知  2-提醒信息  3-订单信息 4-留言信息
        commonMsg.setCreateTime(new Date());
        commonMsg.setSendUid(memberId + "");
        commonMsg.setType(1); //1-系统消息  2-短信消息
        commonMsg.setUType(1); //用户类型 1-个人  2-分组  3-全部用户
        commonMsg.setOnLine(1); //草稿 1-在线  2-草稿
        commonMsg.setIsTop(2); //1-置顶  2-未置顶
        commonMsg.setIsReply(0);//0未回复 1已回复
        shopCommonMessageDao.insert(commonMsg);
        ShopMemberMessage memberMsg = new ShopMemberMessage();
        memberMsg.setId(twiterIdService.getTwiterId());
        memberMsg.setMsgId(twiterId);
        memberMsg.setBizType(4);
        memberMsg.setIsRead(0);
        memberMsg.setCreateTime(new Date());
        memberMsg.setUid(memberId);
        shopMemberMessageDao.insert(memberMsg);
    }

    @Override
    public void addTrainRecordBatch(List<ShopMemberMessage> shopMemberMessageList) {
        shopMemberMessageDao.addTrainRecordBatch(shopMemberMessageList);
    }

    @Override
    public Integer findMessageRemindNum(long uid) {
        return shopMemberMessageDao.findMessageRemindNum(uid);
    }

    @Override
    public Integer findMessageOrderNum(long uid) {
        return shopMemberMessageDao.findMessageOrderNum(uid);
    }

    @Override
    public Integer findMessageLeaveNum(long uid) {
        return shopMemberMessageDao.findMessageLeaveNum(uid);
    }
}
