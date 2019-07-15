package com.framework.loippi.controller.product;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.framework.loippi.consts.Constants;
import com.framework.loippi.controller.GenericController;
import com.framework.loippi.dto.IdNameDto;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsClass;
import com.framework.loippi.entity.product.ShopGoodsGoods;
import com.framework.loippi.entity.product.ShopGoodsSpec;
import com.framework.loippi.entity.user.RdGoodsAdjustment;
import com.framework.loippi.entity.user.RdRanks;
import com.framework.loippi.result.sys.GoodsStocksListView;
import com.framework.loippi.service.RedisService;
import com.framework.loippi.service.product.ShopGoodsClassService;
import com.framework.loippi.service.product.ShopGoodsService;
import com.framework.loippi.service.product.ShopGoodsSpecService;
import com.framework.loippi.service.user.RdGoodsAdjustmentService;
import com.framework.loippi.service.user.RdRanksService;
import com.framework.loippi.support.Page;
import com.framework.loippi.utils.GoodsUtils;
import com.framework.loippi.utils.JacksonUtil;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.vo.goods.GoodsSpecVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.framework.loippi.support.Message;
import com.framework.loippi.support.Pageable;

import java.util.*;

/**
 * Controller - 商品规格表
 * 
 * @author zijing
 * @version 2.0
 */
@Controller("adminShopGoodsStocksController")
@RequestMapping({ "/admin/shop_goods_stocks" })
public class ShopGoodsStocksController extends GenericController {

	@Resource
	private ShopGoodsSpecService shopGoodsSpecService;
	@Resource
	private ShopGoodsService shopGoodsService;
	@Resource
	private ShopGoodsClassService shopGoodsClassService;
    @Resource
    private RedisService redisService;
    @Resource
    private RdRanksService rdRanksService;
	@Resource
	private RdGoodsAdjustmentService rdGoodsAdjustmentService;



	/**
	 * 跳转添加页面
	 * 
	 * @param
	 * @param model
	 * @return
	 */
	@RequestMapping(value = { "/add" }, method = { RequestMethod.GET })
	public String add(ModelMap model) {
		return "/goods_stocks/add";
	}

	/**
	 * 保存
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(ShopGoodsSpec shopGoodsSpec, RedirectAttributes redirectAttributes) {
		shopGoodsSpecService.save(shopGoodsSpec);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 编辑
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Long id, ModelMap model) {
		ShopGoodsSpec shopGoodsSpec = shopGoodsSpecService.find(id);
		model.addAttribute("shopGoodsSpec", shopGoodsSpec);
		return "/goods_stocks/edit";
	}
	
	
	/**
	 * 详情
	 */
	@RequestMapping(value = "/view", method = RequestMethod.GET)
	public String view(Long id, ModelMap model) {
		ShopGoodsSpec shopGoodsSpec = shopGoodsSpecService.find(id);
		model.addAttribute("shopGoodsSpec", shopGoodsSpec);
		return "/goods_stocks/view";
	}
	

	/**
	 * 更新
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(ShopGoodsSpec shopGoodsSpec, RedirectAttributes redirectAttributes) {
		shopGoodsSpecService.update(shopGoodsSpec);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 列表查询
	 * 
	 * @param pageable
	 * @param model
	 * @return
	 */
	@RequestMapping(value ="/list" )
	public String list(Pageable pageable, ModelMap model,@ModelAttribute ShopGoods goodsPlatform,Integer type) {
		pageable.setParameter(goodsPlatform);
        Page<GoodsStocksListView> goodsStocksListViewPage=shopGoodsSpecService.findGoodsStocksInfo(pageable);
		List<GoodsStocksListView> list = goodsStocksListViewPage.getContent();
		Map<String, String> treeMap = new HashMap<>();
		Map<String, String> classNameMap = new HashMap<>();
		List<ShopGoodsClass> goodsClasses = new ArrayList<>();
		List<ShopGoodsClass> firstGoodsClasses = new ArrayList<>();
		List<ShopGoodsClass> shopGoodsClassList = shopGoodsClassService.findAll();
		for (ShopGoodsClass item : shopGoodsClassList) {
			if (Optional.ofNullable(item.getDeep()).orElse(0) == 2) {
				goodsClasses.add(item);
				treeMap.put(item.getId() + "", item.getGcParentId() + "");
			} else {
				if (Optional.ofNullable(item.getGcParentId()).orElse(1L)==0L){
					firstGoodsClasses.add(item);
				}
				classNameMap.put(item.getId() + "", item.getGcName());
			}
		}
		for (GoodsStocksListView item : list) {
			String tree = treeMap.get(item.getGcId() + "");
			if (tree != null && !"".equals(tree)) {
				item.setGcParentName(Optional.ofNullable(classNameMap.get(tree)).orElse(""));
			}
            if (item.getSpecName() != null && !item.getSpecName().trim().equals("")) {
                Map<String, String> specNameMap = JacksonUtil.readJsonToMap(item.getSpecName());
                Map<String, String> specValueMap = JacksonUtil.readJsonToMap(item.getSpecGoodsSpec());
                //获取map集合中的所有键的Set集合
                Set<String> keySpecName = specNameMap.keySet();
                Set<String> keySpecValue = specValueMap.keySet();
                //有了Set集合就可以获取其迭代器，取值
                Iterator<String> itSpecName = keySpecName.iterator();
                Iterator<String> itSpecValue = keySpecValue.iterator();
                StringBuffer info=new StringBuffer();
                item.setSpecName("");
                while (itSpecName.hasNext() && itSpecValue.hasNext())
                {
                    if (item.getSpecName().equals("")){
                    item.setSpecName(specNameMap.get(itSpecName.next())+":"+specValueMap.get(itSpecValue.next()));
                  }else{
                        item.setSpecName(item.getSpecName()+";"+specNameMap.get(itSpecName.next())+":"+specValueMap.get(itSpecValue.next()));
                    }

                }
            }
		}

        List<RdRanks> shopMemberGradeList=(List)getData(list).get("shopMemberGradeList");
        list=(List)getData(list).get("shopGoodsList");
        model.addAttribute("shopMemberGradeList", shopMemberGradeList);
        model.addAttribute("shopGoods", goodsPlatform);
		model.addAttribute("firstGoodsClasses", firstGoodsClasses);
		model.addAttribute("classList", goodsClasses);
		model.addAttribute("page", goodsStocksListViewPage);
		if (Optional.ofNullable(type).orElse(0)==2){
			return "/goods_stocks/goods_stocks_select";
		}else{
			return "/goods_stocks/list";
		}

	}


	/**
	 * 入库记录表
	 */
	@RequestMapping("/stocksRecordList")
	public String stocksRecordList(Pageable pageable, ModelMap model,@ModelAttribute RdGoodsAdjustment rdGoodsAdjustment) {
		pageable.setParameter(rdGoodsAdjustment);
		Page<RdGoodsAdjustment> rdGoodsAdjustmentPage=rdGoodsAdjustmentService.findByPage(pageable);
		List<RdGoodsAdjustment> list = rdGoodsAdjustmentPage.getContent();
		for (RdGoodsAdjustment item : list) {
			if (item.getSpecName() != null && !item.getSpecName().trim().equals("")) {
				Map<String, String> specNameMap = JacksonUtil.readJsonToMap(item.getSpecName());
				Map<String, String> specValueMap = JacksonUtil.readJsonToMap(item.getGoodsSpec());
				//获取map集合中的所有键的Set集合
				Set<String> keySpecName = specNameMap.keySet();
				Set<String> keySpecValue = specValueMap.keySet();
				//有了Set集合就可以获取其迭代器，取值
				Iterator<String> itSpecName = keySpecName.iterator();
				Iterator<String> itSpecValue = keySpecValue.iterator();
				StringBuffer info=new StringBuffer();
				item.setSpecName("");
				while (itSpecName.hasNext() && itSpecValue.hasNext())
				{
					if (item.getSpecName().equals("")){
						item.setSpecName(specNameMap.get(itSpecName.next())+":"+specValueMap.get(itSpecValue.next()));
					}else{
						item.setSpecName(item.getSpecName()+";"+specNameMap.get(itSpecName.next())+":"+specValueMap.get(itSpecValue.next()));
					}

				}
			}
		}
		model.addAttribute("page", rdGoodsAdjustmentPage);
		model.addAttribute("rdGoodsAdjustment", rdGoodsAdjustment);
		return "/goods_stocks/stocks_record_list";
	}

	/**
	 * 删除操作
	 * 
	 * @param ids
	 * @return
	 */
	@RequestMapping(value = { "/delete" }, method = { RequestMethod.POST })
	public @ResponseBody Message delete(Long[] ids) {
		this.shopGoodsSpecService.deleteAll(ids);
		return SUCCESS_MESSAGE;
	}

    /**
     * 开启或者关闭规格
     */
    @RequestMapping("/updateSpecStatus")
    public String updateSpecStatus(Long id, Integer status, ModelMap model, HttpServletRequest request) {
            if (id!=null){
                ShopGoodsSpec shopGoodsSpec=new ShopGoodsSpec();
                shopGoodsSpec.setId(id);
                shopGoodsSpec.setSpecIsopen(status);
                shopGoodsSpecService.update(shopGoodsSpec);
            }
        return "redirect:list.jhtml";
    }

    public Map getData(List<GoodsStocksListView> shopGoodsList) {
        Map<String, Object> map = new HashMap<>();
        List<RdRanks> shopMemberGradeList=null;
        if (redisService.get("shopMemberGradeList")!=null){
            shopMemberGradeList=JacksonUtil.convertList(redisService.get("shopMemberGradeList"), RdRanks.class);
        }else{
            shopMemberGradeList = rdRanksService.findAll();
            redisService.save("shopMemberGradeList",shopMemberGradeList);
        }

        for (GoodsStocksListView item:shopGoodsList) {
            String gradeName="";
            for (RdRanks shopMemberGrade:shopMemberGradeList) {
                if (Optional.ofNullable(item.getSalePopulationIds()).orElse("").indexOf(shopMemberGrade.getRankId()+"")!=-1){
                    gradeName+=shopMemberGrade.getRankName()+",";
                }
            }
            item.setSalePopulationName(gradeName);
        }
        map.put("shopMemberGradeList", shopMemberGradeList);
        map.put("shopGoodsList", shopGoodsList);
        return map;
    }
}
