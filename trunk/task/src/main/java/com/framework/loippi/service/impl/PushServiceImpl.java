package com.framework.loippi.service.impl;

import com.framework.loippi.dao.ShopMemberMessageDao;
import com.framework.loippi.dto.PushMessage;
import com.framework.loippi.dto.ShopMemberDto;
import com.framework.loippi.entity.ShopMemberMessage;
import com.framework.loippi.queue.EventPublisher;
import com.framework.loippi.queue.EventSubscriber;
import com.framework.loippi.queue.QueueWorker;
import com.framework.loippi.service.PushService;
import com.framework.loippi.service.RedisService;
import com.framework.loippi.service.ShopCommonMessageService;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.utils.JacksonUtil;
import com.framework.loippi.utils.StringUtil;
import com.framework.loippi.utils.jpush.JpushRunnableSecond;
import com.framework.loippi.utils.jpush.JpushUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by longbh on 2017/8/19.
 */
@Slf4j
@Service
public class PushServiceImpl implements PushService, QueueWorker {

    ///--------redis-------------
    private Boolean redisEnabled = true;
    @Value("${redis.host}")
    private String host;
    @Value("${redis.port}")
    private Integer port;
    @Value("${redis.maxIdle}")
    private Integer maxIdle;
    @Value("${redis.maxTotal}")
    private Integer maxTotal;
    @Value("${redis.maxWaitMillis}")
    private Long maxWaitMillis;
    @Value("${redis.timeout}")
    private Integer timeout;
    @Value("${redis.pass}")
    private String password;
    @Autowired
    private RedisService redisService;
    @Autowired
    private ShopMemberMessageDao shopMemberMessageDao;
    @Autowired
    private TwiterIdService twiterIdService;
    @Resource(name = "sqlSessionTemplate")
    private SqlSessionTemplate sqlSessionTemplate;

    private EventPublisher publisher;
    private EventSubscriber subscriber;
    private String queue = "push-message";
    ExecutorService execute = Executors.newFixedThreadPool(10);

    @PostConstruct
    public void init() {
        if (!redisEnabled) {
            log.error("redis is not enabled, please check the 'redis.enabled' option in 'application.conf'");
            return;
        }
        publisher = new EventPublisher(redisService.getRedisUtils());

        subscriber = new EventSubscriber(queue, redisService.getRedisUtils(), this);
        subscriber.start();
    }

    public void sendMessage(String content,String[] sendUidSort) {
        //publisher.publish(JacksonUtil.toJson(message), queue);
        //直接进行极光推送
//            JpushUtils.push2alias(content,sendUidSort,null,0);



    }

    @Override
    public void updateReadMessage(Long id) {
        ShopMemberMessage shopMemberMessage = new ShopMemberMessage();
        shopMemberMessage.setId(id);
        shopMemberMessage.setIsRead(1);
        shopMemberMessageDao.update(shopMemberMessage);
    }

    @Override
    public void updateReadUMessage(Long uid, Integer bizType) {
        shopMemberMessageDao.updateReadMessage(uid, bizType);
    }

    @Override
    public void handle(String key, String data) {

        log.info("key:"+key+",data:"+data);

        PushMessage pushMessage = JacksonUtil.fromJson(data, PushMessage.class);

        //发送到用户
        Map<String, String> extras = new HashMap<>();
        extras.put("bizId", pushMessage.getBizId() + "");
        extras.put("bizType", pushMessage.getBizType() + "");
        if (pushMessage.getTargetType() == 1) {
            String[] alias = new String[]{"userId_" + pushMessage.getTargetId() + ""};
            execute.execute(new JpushRunnableSecond(pushMessage.getTitle(), alias, extras));
            //数据库插入发送记录
            save(new ShopMemberMessage(twiterIdService.getTwiterId(), pushMessage.getMsgId(), pushMessage.getBizType(),
                    2, new Date(), Long.valueOf(pushMessage.getTargetId())));
        } else if (pushMessage.getTargetType() == 2) {
            List<String> shopMemberDtos = JacksonUtil.convertList(pushMessage.getTargetId(),String.class);
            List<ShopMemberMessage> shopMemberMessages = new ArrayList<>();
            if (shopMemberDtos.size() > 0) {
                String[] alias = new String[shopMemberDtos.size()];
                for (int i = 0; i < shopMemberDtos.size(); i++) {
                    if (StringUtil.isEmpty(shopMemberDtos.get(i))) continue;
                    alias[i] = "userId_" + shopMemberDtos.get(i);
                    shopMemberMessages.add(new ShopMemberMessage(twiterIdService.getTwiterId(), pushMessage.getMsgId(),
                            pushMessage.getBizType(), 2, new Date(), Long.valueOf(shopMemberDtos.get(i))));
                }
                execute.execute(new JpushRunnableSecond(pushMessage.getTitle(), alias, extras));
                //数据库插入发送记录
                saveBatch(shopMemberMessages);
            }
        } else if (pushMessage.getTargetType() == 3) {
            Integer page = 1;
            List<ShopMemberDto> shopMemberDtos = null;
            while (shopMemberDtos == null || shopMemberDtos.size() == 1000) {
                shopMemberDtos = shopMemberMessageDao.findAllMember(page);
                if (shopMemberDtos.size() > 0) {//分页批量发送
                    List<ShopMemberMessage> shopMemberMessages = new ArrayList<>();
                    String[] alias = new String[shopMemberDtos.size()];
                    for (int i = 0; i < shopMemberDtos.size(); i++) {
                        alias[i] = "userId_" + shopMemberDtos.get(i).getId() + "";
                        try {
                            shopMemberMessages.add(new ShopMemberMessage(twiterIdService.getTwiterId(), pushMessage.getMsgId(),
                                    pushMessage.getBizType(), 2, new Date(), shopMemberDtos.get(i).getId()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    page++;
                    execute.execute(new JpushRunnableSecond(pushMessage.getTitle(), alias, extras));
                    //数据库插入发送记录
                    saveBatch(shopMemberMessages);
                }
            }
        }
    }

    @PreDestroy
    public void destory() {
        redisService.getRedisUtils().stop();
        subscriber.destroy();
    }

    private void save(ShopMemberMessage pushMessage) {
        shopMemberMessageDao.insert(pushMessage);
    }

    private void saveBatch(List<ShopMemberMessage> shopMemberMessages) {
        SqlSession batchSqlSession = null;
        try {
            //获取sqlsession
            batchSqlSession = sqlSessionTemplate
                    .getSqlSessionFactory()
                    .openSession(ExecutorType.BATCH, false);// 获取批量方式的sqlsession
            batchSqlSession.insert("com.framework.loippi.dao.ShopMemberMessageDao.addTrainRecordBatch", shopMemberMessages);
            batchSqlSession.commit();
            batchSqlSession.clearCache();
        } finally {
            if (batchSqlSession != null) {
                batchSqlSession.close();
            }
        }
    }
}
