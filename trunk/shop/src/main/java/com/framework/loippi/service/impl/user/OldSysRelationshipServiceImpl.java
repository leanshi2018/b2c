package com.framework.loippi.service.impl.user;

import com.framework.loippi.dao.user.OldSysRelationshipDao;
import com.framework.loippi.entity.user.OldSysRelationship;
import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.user.OldSysRelationshipService;
import com.framework.loippi.service.user.RdMmRelationService;
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
    private RdMmRelationService rdMmRelationService;


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
        while (isSelect) {
            List<OldSysRelationship> oldSysRelationshipList = oldSysRelationshipDao
                .findByParams(Paramap.create().put("oMcode", oSpcode));
            if (oldSysRelationshipList != null && oldSysRelationshipList.size() > 0) {//如果根据推荐人编号找到了关系表数据
                OldSysRelationship oldSysRelationship = oldSysRelationshipList.get(0);
                if (oldSysRelationship.getNMcode().equals("101000158")) {
                    mmCode = "101000158";
                    mmName =oldSysRelationship.getONickname();
                    isSelect = false;
                }
                else if (oldSysRelationship.getNYnRegistered() == 1) {
                    RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", oldSysRelationship.getNMcode());
                    if(rdMmRelation==null||rdMmRelation.getMmStatus()==null){
                        mmCode = "101000158";
                        mmName ="美国公司节点1";
                        isSelect = false;
                    }else {
                        if(rdMmRelation.getMmStatus()==2){
                            oSpcode=oldSysRelationship.getOSpcode();
                        }else {
                            mmCode = oldSysRelationship.getNMcode();
                            mmName =oldSysRelationship.getONickname();
                            isSelect = false;
                        }
                    }
                }else {
                    oSpcode=oldSysRelationship.getOSpcode();
                }
            }else{
                mmCode = "101000158";
                mmName ="美国公司节点1";
                isSelect = false;
            }
            index++;
            if (index>=200){
                mmCode = "101000158";
                mmName ="美国公司节点1";
                isSelect = false;
            }
        }
        map.put("mmCode", mmCode);
        map.put("mmName", mmName);
        map.put("index", index + "");
        return map;
    }
}
