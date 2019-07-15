package com.framework.loippi.service.impl.user;

import com.framework.loippi.dao.user.OldSysRelationshipDao;
import com.framework.loippi.entity.user.OldSysRelationship;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.user.OldSysRelationshipService;
import com.framework.loippi.utils.Paramap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * SERVICE - OldSysRelationship(老用户关系表)
 *
 * @author dzm
 * @version 2.0
 */
@Service
public class OldSysRelationshipServiceImpl extends GenericServiceImpl<OldSysRelationship, Long> implements
    OldSysRelationshipService {

    @Autowired
    private OldSysRelationshipDao oldSysRelationshipDao;


    @Autowired
    public void setGenericDao() {
        super.setGenericDao(oldSysRelationshipDao);
    }

    @Override
    public Map<String, String> findOldSysSpcode(String oSpcode) {
        boolean isSelect = true;
        Map<String, String> map = new HashMap<>();
        String mmCode = "";
        String mmName = "";
        Integer index = 0;
        //Long count = oldSysRelationshipDao.count();
        OldSysRelationship oldSysRelationship = new OldSysRelationship();
        oldSysRelationship.setOSpcode(oSpcode);
        while (isSelect) {
            List<OldSysRelationship> oldSysRelationshipList = oldSysRelationshipDao
                .findByParams(Paramap.create().put("oMcode", oldSysRelationship.getOSpcode()));
            if (oldSysRelationshipList != null && oldSysRelationshipList.size() > 0) {
                oldSysRelationship = oldSysRelationshipList.get(0);
                if (oldSysRelationship.getOSpcode().equals("999999999")) {
                    mmCode = "999999999";
                    mmName =oldSysRelationship.getONickname();
                    isSelect = false;
                }
                if (oldSysRelationship.getNYnRegistered() == 1) {
                    mmCode = oldSysRelationship.getNMcode();
                    mmName =oldSysRelationship.getONickname();
                    isSelect = false;
                }
            }else{
                mmCode = "999999999";
                mmName ="公司";
                isSelect = false;
            }
            index++;
            if (index>=200){
                mmCode = "999999999";
                mmName ="公司";
                isSelect = false;
            }
        }
        map.put("mmCode", mmCode);
        map.put("mmName", mmName);
        map.put("index", index + "");
        return map;
    }
}
