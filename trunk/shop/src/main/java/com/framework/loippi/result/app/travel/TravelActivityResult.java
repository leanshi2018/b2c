package com.framework.loippi.result.app.travel;

import com.framework.loippi.entity.travel.RdTravelActivity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * 旅游活动详情
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TravelActivityResult extends RdTravelActivity implements Serializable {
    /**
     * 是否报名 0：未报名 1：已报名
     */
    private Integer signUpFlag;

    /**
     * 旅游活动详情
     */
    private String travelImage;

    public static List<TravelActivityResult> build(List<RdTravelActivity> list, HashMap<Long, Integer> map, String prefix) {
        ArrayList<TravelActivityResult> results = new ArrayList<>();
        for (RdTravelActivity rdTravelActivity : list) {
            TravelActivityResult result = new TravelActivityResult();
            BeanUtils.copyProperties(rdTravelActivity,result);
            Integer flag = map.get(rdTravelActivity.getId());
            result.setSignUpFlag(flag);
            String image = rdTravelActivity.getImage();
            if(image==null){
                result.setTravelImage("");
            }else {
                String[] strings = image.split(",");
                StringBuilder strBuilder = new StringBuilder("");
                for (String string : strings) {
                    strBuilder.append("<img src='" + prefix + string + "' width='100%'>");
                }
                result.setTravelImage(Optional.ofNullable(prefix + strBuilder.toString()).orElse(""));
            }
            results.add(result);
        }
        return results;
    }
}
