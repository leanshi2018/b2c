package com.framework.loippi.utils;

import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsSpec;

import com.framework.loippi.vo.goods.GoodsSpecVo;

import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.Map.Entry;


import lombok.extern.slf4j.Slf4j;

/**
 * @author cgl
 * @date 2015年06月29日14:50:01
 * @discription 此工具类, 是用来将goods实体类中的spec, attr等字段, 将其字符串序列化成指定的实体类
 */
@Slf4j
public class GoodsUtils {

    /**
     * 获取到商品的详细sku情况
     * 参数:
     * 1. specGoods实体
     * 2. goods实体
     * 返回:
     * 返回的是一个GoodsSpec实体类,在这个实体类中
     * sepcMap这个参数 键为:当前商品规格的规格名称. 值为:当前规格的规格值名称,如果为空,则说明该商品没有规格
     * colImg这个参数是,当前商品所属的规格中的颜色这个规格,的自定义图片,如果为空则说明没有自定义图片
     */
    public static ShopGoodsSpec getSepcMapAndColImgToGoodsSpec(ShopGoods goods, ShopGoodsSpec goodsSpec) {
        if (goodsSpec.getSpecName() != null && !goodsSpec.getSpecName().trim().equals("")) {
            //准备map
            Map<String, String> map = new HashMap<String, String>();
            String sepcNameAndValueStr = goods.getGoodsSpec();
            String specValueStr = goodsSpec.getSpecGoodsSpec();
            Map<String, String> sepcNameMap = JacksonUtil.readJsonToMap(goods.getSpecName());
            Map<String, String> sepcNameAndValueMap = JacksonUtil.readJsonToMap(sepcNameAndValueStr);
            Map<String, String> specValueNameMap = JacksonUtil.readJsonToMap(specValueStr);
            Iterator<Entry<String, String>> sepcNameAndValueEntry = sepcNameAndValueMap.entrySet().iterator();
            while (sepcNameAndValueEntry.hasNext()) {
                Entry<String, String> e = sepcNameAndValueEntry.next();
                String specId = e.getKey();
                String specValueMapStr = e.getValue();
                Map<String, String> specValueMap = JacksonUtil.readJsonToMap(specValueMapStr);
                Iterator<String> specValueIdIt = specValueMap.keySet().iterator();
                while (specValueIdIt.hasNext()) {
                    String specValueId = specValueIdIt.next();
                    String specValueName = specValueMap.get(specValueId);
                    if (specValueNameMap.containsKey(specValueId)) {
                        map.put(sepcNameMap.get(specId), specValueName);
                    }
                }

            }
            goodsSpec.setSepcMap(map);
        }

        return goodsSpec;
    }

    /**
     * 将goods实体类中的goodsSpec字段中的字符串值,序列化成map
     * map结构:
     * 外层map的键为规格id(spId),值为内层map
     * 内层为GoodsSpecVo形的list
     *
     * @return Map<Integer, List<GoodsSpecVo>>
     */
    public static Map<String, List<GoodsSpecVo>> goodsSpecStrToMapList(String goodsSpec) {
        if (goodsSpec == null || goodsSpec.trim().equals("")) {
            return null;
        }
        //准备返回的map(外层)
        Map<String, List<GoodsSpecVo>> returnMap = new LinkedHashMap<String, List<GoodsSpecVo>>();
        HashMap<String, String> map = (HashMap<String, String>) JacksonUtil.readJsonToMap(goodsSpec);
        if (map == null) {
            return null;
        }
        //遍历map
        Set<Map.Entry<String, String>> set = map.entrySet();
        Iterator<Map.Entry<String, String>> it = set.iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entrySpec = it.next();
            //得到key
            String spId = entrySpec.getKey();
            //在map中,嵌套了一个map
            HashMap<String, String> valueMap = (HashMap<String, String>) JacksonUtil.readJsonToMap(entrySpec.getValue());
            Iterator<Map.Entry<String, String>> valueKeyIt = valueMap.entrySet().iterator();
            //准备内层的list
            List<GoodsSpecVo> list = new ArrayList<GoodsSpecVo>();
            //内层循环
            while (valueKeyIt.hasNext()) {
                //准备给GoodsSpecVo实体类
                GoodsSpecVo goodsSpecVo = new GoodsSpecVo();
                Map.Entry<String, String> entry = valueKeyIt.next();
                String spValueId = entry.getKey();
                String spValueName = entry.getValue();
                goodsSpecVo.setSpId(spId);
                goodsSpecVo.setSpValueId(spValueId);
                goodsSpecVo.setSpValueName(spValueName);
                //放入list
                list.add(goodsSpecVo);
            }
            //将内层map 放入外层map
            // returnMap.put("\""+spId+"\"", list);
            returnMap.put(spId, list);
        }
        return returnMap;
    }


    /**
     * 将goodsSpec实体类中的specGoodsSpec字段中的字符串值进行提炼获得以逗号分隔的secValueid字符串
     *
     * @return String
     */
    public static String getThisGoodsAllSpecValueId(String specGoodsSpec) {
//        if (specGoodsSpec == null || specGoodsSpec.trim().equals("")) {
//            return null;
//        }
//        Map<String, String> map = JacksonUtil.convertStrMap(specGoodsSpec);
//        Set<String> set = map.keySet();
//        Set<String> newSet = new TreeSet<String>(new Comparator<String>() {
//            @Override
//            public int compare(String o1, String o2) {
//                return o1.compareTo(o2);
//            }
//        });
//        newSet.addAll(set);
//        String str = "";
//        Iterator<String> it = newSet.iterator();
//        while (it.hasNext()) {
//            str += it.next() + ",";
//        }
//        str = str.substring(0, str.length() - 1);
//        return str;
        if (specGoodsSpec == null || specGoodsSpec.trim().equals("")) {
            return null;
        }
        Map<String, String> map2 = goodsColImgStrToMapByOrder(specGoodsSpec);
        Set<String> set1 = map2.keySet();
        String str1 = "";
        Iterator<String> it1 = set1.iterator();
        while (it1.hasNext()) {
            str1 += it1.next().replace(",","") + ",";
        }
        str1 = str1.substring(0, str1.length() - 1);
        return str1;
    }


    /**
     * 将goodsSpec实体类中的specGoodsSpec字段中的字符串值,序列化成map
     * map结构:
     * 键为goodsValueId也就是图片对应的规格值id
     * 值为对应的自定义规格名称
     *
     * @return Map<String, String>
     */
    public static Map<String, String> specGoodsSpecToMap(String specGoodsSpec) {
        return goodsColImgStrToMap(specGoodsSpec);
    }

    /**
     * 将goods实体类中的goodsColImg字段中的字符串值,序列化成map
     * map结构:
     * 键为goodsValueId也就是图片对应的规格值id
     * 值为对应的用户自定义图片名
     *
     * @return Map<String, String>
     */
    public static Map<String, String> goodsColImgStrToMap(String goodsColImg) {
        if (goodsColImg == null || goodsColImg.trim().equals("")) {
            return null;
        }
        //准备返回的map
        Map<String, String> returnMap = new HashMap<>();
        returnMap = (HashMap<String, String>) JacksonUtil.readJsonToMap(goodsColImg);
        return returnMap;
    }

    /**
     * 将goods实体类中的goodsColImg字段中的字符串值,序列化成map
     * map结构:
     * 键为goodsValueId也就是图片对应的规格值id
     * 值为对应的用户自定义图片名
     * {"6303255209967620096":"小/300G","6303255861607272448":"无理由换新","6303550627364474880":"64G"}
     * 严格按照，传递过来的字符串顺序进行封装
     *
     * @return Map<String, String>
     */
    public static Map<String, String> goodsColImgStrToMapByOrder(String goodsColImg) {
        if (goodsColImg == null || goodsColImg.trim().equals("")) {
            return null;
        }
        //准备返回的map
        Map<String, String> returnMap = new LinkedHashMap<>();
        JSONObject jsonObject = JSONObject.fromObject(goodsColImg); //将字符串{“id”：1}
        Iterator iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            String value = jsonObject.getString(key);
            returnMap.put(key, value);
        }
        return returnMap;
    }

    public static void main(String[] args) {
        String str = "{\"6303255209967620096\":\"小/300G\",\"6303255861607272448\":\"无理由换新\",\"6303550627364474880\":\"64G\"}";
        GoodsUtils.goodsColImgStrToMap(str);
    }

    /**
     * 将goods实体类中的goodsAttr字段中的字符串值,序列化成GoodsAttrVo实体类
     *
     * @return GoodsSpecIndex的list
     */
    public static List<com.framework.loippi.vo.goods.GoodsAttrVo> goodsAttrStrToGoodsAttrVoClass(String goodsAttr) {
        if (null == goodsAttr || StringUtils.isEmpty(goodsAttr)) {
            return null;
        }
        //准备要返回的list
        List<com.framework.loippi.vo.goods.GoodsAttrVo> list = new ArrayList<>();
        HashMap<String, String> map = (HashMap<String, String>) JacksonUtil.readJsonToMap(goodsAttr);
        Set<String> set = map.keySet();
        Iterator<String> it = set.iterator();
        //准备实体类
        com.framework.loippi.vo.goods.GoodsAttrVo goodsAttrVo = null;
        while (it.hasNext()) {
            //得到key
            String attrId = it.next();
            //在map中,嵌套了一个map
            HashMap<String, String> valueMap = (HashMap<String, String>) JacksonUtil.readJsonToMap(map.get(attrId));
            Iterator<Map.Entry<String, String>> valueKeyIt = valueMap.entrySet().iterator();
            goodsAttrVo = new com.framework.loippi.vo.goods.GoodsAttrVo();
            //内层循环
            while (valueKeyIt.hasNext()) {
                //属性id
                goodsAttrVo.setAttrId(attrId);
                Map.Entry<String, String> entryAttr = valueKeyIt.next();
                if (entryAttr.getKey().equals("name")) {
                    //属性名称
                    goodsAttrVo.setAttrName(entryAttr.getValue());
                } else {
                    //属性值id
                    goodsAttrVo.setAttrValueId(entryAttr.getKey());
                    //属性值名称
                    goodsAttrVo.setAttrValueName(entryAttr.getValue());
                }
            }
            list.add(goodsAttrVo);
        }
        return list;
    }

    public static ShopGoods enGoods(ShopGoods goods) {
        goods.setSpecName(Encodes.unescapeHtml(goods.getSpecName()));
        goods.setGoodsSpec(Encodes.unescapeHtml(goods.getGoodsSpec()));
        return goods;
    }
}
