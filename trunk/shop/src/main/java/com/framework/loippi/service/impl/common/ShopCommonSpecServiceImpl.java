package com.framework.loippi.service.impl.common;

import com.framework.loippi.dao.common.ShopCommonSpecValueDao;
import com.framework.loippi.entity.common.ShopCommonSpecValue;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.utils.JacksonUtil;
import com.framework.loippi.vo.goods.SpecVo;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.common.ShopCommonSpecDao;
import com.framework.loippi.entity.common.ShopCommonSpec;
import com.framework.loippi.service.common.ShopCommonSpecService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SERVICE - ShopCommonSpec(商品规格模板表)
 *
 * @author zijing
 * @version 2.0
 */
@Service
public class ShopCommonSpecServiceImpl extends GenericServiceImpl<ShopCommonSpec, Long> implements ShopCommonSpecService {

    @Autowired
    private ShopCommonSpecDao shopCommonSpecDao;
    @Autowired
    private TwiterIdService twiterIdService;
    @Autowired
    private ShopCommonSpecValueDao shopCommonSpecValueDao;

    @Autowired
    public void setGenericDao() {
        super.setGenericDao(shopCommonSpecDao);
    }

    //保存规格信息
    public void save(ShopCommonSpec spec, String specValues) {
        spec.setId(twiterIdService.getTwiterId());
        shopCommonSpecDao.insert(spec);
        //得到sp_id
        Long spId = spec.getId();
        List<ShopCommonSpecValue> list = JacksonUtil.convertList(specValues, ShopCommonSpecValue.class);
        //循环存入shop_spec_value表中
        for (int i = 0; i < list.size(); i++) {
            //得到规格值实体
            ShopCommonSpecValue specValue = (ShopCommonSpecValue) list.get(i);
            Long spValueId = specValue.getId();
            String spValueIsDelete = specValue.getIsDelete();
            if ("true".equals(spValueIsDelete)) {
                shopCommonSpecValueDao.delete(spValueId);
            } else {
                if (spValueId == null || spValueId == 0) {
                    //设置spId
                    specValue.setSpId(spId);
                    //存储
                    specValue.setId(twiterIdService.getTwiterId());
                    shopCommonSpecValueDao.insert(specValue);
                } else {
                    shopCommonSpecValueDao.update(specValue);
                }
            }
        }
    }

    //修改规格
    public void update(ShopCommonSpec spec, String specValues) {
        shopCommonSpecDao.update(spec);
        //得到sp_id
        Long spId = spec.getId();
        //删除spId下面所有的规格值
        //specValueDao.deleteBySpId(spId);
        //存入规格值
        //首先得到规格值得list
        List<ShopCommonSpecValue> list = JacksonUtil.convertList(specValues, ShopCommonSpecValue.class);
        //循环存入shop_spec_value表中
        this.updateOrSaveSpecValues(spId, list);
    }

    @Override
    public List<SpecVo> findListByType2(Long typeId) {
        return shopCommonSpecDao.findListByType2(typeId);
    }

    /**
     * 规格修改或保存的私有方法
     *
     * @param spId
     * @param list
     */
    private void updateOrSaveSpecValues(Long spId, List<ShopCommonSpecValue> list) {
        Map<String, Object> params = new HashMap<>();
        params.put("spId", spId);
        List<ShopCommonSpecValue> values = shopCommonSpecValueDao.findByParams(params);
        List<Long> spValueIds = new ArrayList<Long>();
        for (ShopCommonSpecValue spValue : values) {
            spValueIds.add(spValue.getId());
        }
        for (int j = 0; j < list.size(); j++) {
            ShopCommonSpecValue specValue = (ShopCommonSpecValue) list.get(j);
            //设置spId
            specValue.setSpId(spId);
            Long specValueId = specValue.getId();
            if (specValueId != null) {
                spValueIds.remove(specValueId);
                shopCommonSpecValueDao.update(specValue);
            } else {
                specValue.setId(twiterIdService.getTwiterId());
                shopCommonSpecValueDao.insert(specValue);
            }
        }
        for (Long id : spValueIds) {
            shopCommonSpecValueDao.delete(id);
        }
    }

}
