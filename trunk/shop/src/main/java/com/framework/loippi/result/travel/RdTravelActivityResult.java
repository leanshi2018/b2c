package com.framework.loippi.result.travel;

import com.framework.loippi.entity.travel.RdTravelActivity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RdTravelActivityResult extends RdTravelActivity {
    private List<String> imageList=new ArrayList<>();
}
