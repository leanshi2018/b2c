package com.framework.loippi.service.impl.product;

import com.framework.loippi.controller.AppConstants;
import com.framework.loippi.controller.StateResult;
import com.framework.loippi.dao.product.ShopGoodsEvaluateSensitivityDao;
import com.framework.loippi.entity.product.ShopGoodsEvaluateSensitivity;
import com.framework.loippi.service.RedisService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.product.ShopGoodsEvaluateSensitivityService;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.validator.SensitivewordFilter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * SERVICE - ShopGoodsEvaluateSensitivity(评价敏感词)
 *
 * @author zijing
 * @version 2.0
 */
@Service
@Slf4j
public class ShopGoodsEvaluateSensitivityServiceImpl extends
        GenericServiceImpl<ShopGoodsEvaluateSensitivity, Long> implements ShopGoodsEvaluateSensitivityService {

    @Autowired
    private ShopGoodsEvaluateSensitivityDao shopGoodsEvaluateSensitivityDao;
    @Autowired
    private RedisService redisService;

    @Autowired
    public void setGenericDao() {
        super.setGenericDao(shopGoodsEvaluateSensitivityDao);
    }

    @Override
    public void filterWords(String word) {
        log.info("用户评价语[{}]", word);
        JSONObject jedisStringCache = redisService.get("sensitive_word_of_evaluation", JSONObject.class);
        if (jedisStringCache == null) {
            List<ShopGoodsEvaluateSensitivity> filterWord = shopGoodsEvaluateSensitivityDao.findByParams(Paramap.create().put("status",1));
            Map map = new HashMap();
            for (ShopGoodsEvaluateSensitivity sensitiveWord : filterWord) {
                map.put(sensitiveWord.getId().toString(), sensitiveWord.getSensitivity());
            }
            JSONObject json = JSONObject.fromObject(map);
            redisService.save("sensitive_word_of_evaluation", json);
            jedisStringCache=redisService.get("sensitive_word_of_evaluation", JSONObject.class);
        }

        Map map = (Map) jedisStringCache;
        SensitivewordFilter filter = new SensitivewordFilter(map);
        Set<String> set = filter.getSensitiveWord(word, 2);
        log.info("语句中包含敏感词的个数为：[{}]。包含：[{}]", set.size(), set);
        if (CollectionUtils.isNotEmpty(set)) {
            throw new StateResult(AppConstants.EVALUATION_CONTAIN_SENSITIVE_WORD, "包含敏感词" + set);
        }
    }

    /**
     * 是否包含
     * @param word
     * @return
     */
    @Override
    public boolean hasFilterWords(String word) {
        JSONObject jedisStringCache = redisService.get("sensitive_word_of_evaluation", JSONObject.class);
        if (jedisStringCache == null) {
            List<ShopGoodsEvaluateSensitivity> filterWord = shopGoodsEvaluateSensitivityDao.findAll();
            Map map = new HashMap();
            for (ShopGoodsEvaluateSensitivity sensitiveWord : filterWord) {
                map.put(sensitiveWord.getId().toString(), sensitiveWord.getSensitivity());
            }
            JSONObject json = JSONObject.fromObject(map);
            redisService.save("sensitive_word_of_evaluation", json);
        }
        Map map = (Map) jedisStringCache;
        SensitivewordFilter filter = new SensitivewordFilter(map);
        Set<String> set = filter.getSensitiveWord(word, 2);
        if (set.size() > 0) {
            return true;
        }
        return false;
    }
}
