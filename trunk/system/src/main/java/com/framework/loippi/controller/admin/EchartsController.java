package com.framework.loippi.controller.admin;

import java.util.Arrays;
import java.util.List;

import com.framework.loippi.controller.GenericController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.github.abel533.echarts.axis.CategoryAxis;
import com.github.abel533.echarts.axis.ValueAxis;
import com.github.abel533.echarts.code.Magic;
import com.github.abel533.echarts.code.MarkType;
import com.github.abel533.echarts.code.SeriesType;
import com.github.abel533.echarts.code.Tool;
import com.github.abel533.echarts.code.Trigger;
import com.github.abel533.echarts.data.Data;
import com.github.abel533.echarts.data.PointData;
import com.github.abel533.echarts.feature.MagicType;
import com.github.abel533.echarts.json.GsonOption;
import com.github.abel533.echarts.series.Bar;
import com.github.abel533.echarts.series.EMap;

/**
 * Controller - 图标示例（Echarts）
 * 
 * @author Loippi Team
 * @version 2.0
 *
 */
@Controller("adminEchartsController")
@RequestMapping("/admin/echarts")
public class EchartsController extends GenericController {
	/**
	 * 首页
	 */
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(ModelMap model) {
		barMethod(model);

		mapMethod(model);

		return "/admin/echarts/index";
	}

	private void mapMethod(ModelMap model) {
		GsonOption option = new GsonOption();
		option.tooltip(Trigger.item).tooltip().show(true).formatter("{b}");
		EMap map = new EMap("Map");
		map.setName("中国");
		map.setType(SeriesType.map);
		map.setMapType("china");
		map.label().normal().show(true);
		map.label().emphasis().show(true);
		Data data = new Data("广东");
		data.selected(true);
		map.data(data);
		option.series(map);
		model.addAttribute("mapOption1", option.toString());
	}

	private void barMethod(ModelMap model) {
		// 地址：http://echarts.baidu.com/doc/example/bar1.html
		GsonOption option = new GsonOption();
		option.title().text("某地区蒸发量和降水量").subtext("纯属虚构");
		option.tooltip().trigger(Trigger.axis);
		option.legend("蒸发量", "降水量");
		option.toolbox().show(true).feature(Tool.mark, Tool.dataView, new MagicType(Magic.line, Magic.bar).show(true),
				Tool.restore, Tool.saveAsImage);
		option.calculable(true);
		option.xAxis(
				new CategoryAxis().data("1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"));
		option.yAxis(new ValueAxis());

		Bar bar = new Bar("蒸发量");
		bar.data(2.0, 4.9, 7.0, 23.2, 25.6, 76.7, 135.6, 162.2, 32.6, 20.0, 6.4, 3.3);
		bar.markPoint().data(new PointData().type(MarkType.max).name("最大值"),
				new PointData().type(MarkType.min).name("最小值"));
		bar.markLine().data(new PointData().type(MarkType.average).name("平均值"));

		Bar bar2 = new Bar("降水量");
		List<Double> list = Arrays.asList(2.6, 5.9, 9.0, 26.4, 28.7, 70.7, 175.6, 182.2, 48.7, 18.8, 6.0, 2.3);
		bar2.setData(list);
		bar2.markPoint().data(new PointData("年最高", 182.2).xAxis(7).yAxis(183).symbolSize(18),
				new PointData("年最低", 2.3).xAxis(11).yAxis(3));
		bar2.markLine().data(new PointData().type(MarkType.average).name("平均值"));
		option.series(bar, bar2);
		model.addAttribute("barOption1", option.toString());
	}
}
