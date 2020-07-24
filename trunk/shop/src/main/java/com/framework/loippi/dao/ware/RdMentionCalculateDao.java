package com.framework.loippi.dao.ware;

import com.framework.loippi.entity.ware.RdMentionCalculate;
import com.framework.loippi.mybatis.dao.GenericDao;

import java.util.ArrayList;

public interface RdMentionCalculateDao extends GenericDao<RdMentionCalculate, Long> {
    void insertList(ArrayList<RdMentionCalculate> calculates);
}
