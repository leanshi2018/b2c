package com.framework.loippi.utils.shunfeng;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.framework.loippi.entity.CreateExpressOrderDTO;
import com.framework.loippi.entity.RlsInfoDto;
import com.framework.loippi.entity.ShunFengResponse;
import com.framework.loippi.enus.ExpressCompanyType;


public class XmlHelper {
	/**
	 * 顺丰快递返回xml解析为实体bean
	 * @param content
	 * @return
	 */
	public static ShunFengResponse xmlToBeanForSF(String content) {
        ShunFengResponse response = new ShunFengResponse();
		try {
			SAXReader saxReader = new SAXReader();
			Document docDom4j = saxReader.read(new ByteArrayInputStream(content.getBytes("utf-8")));
			Element root = docDom4j.getRootElement();
            String serviceValue = root.attributeValue("service");

            // 路由推送响应实体
			if ("RoutePushService".equals(serviceValue)) {
				response.setResultFlag(true);
				// 路由推送返回结果
				List<ShunFengResponse.WaybillRoute> waybillRouteArrayList = new ArrayList<>(5);
				Element body = root.element("Body");
				List<Element> waybillRouteElements = body.elements();
				for (Element waybillRouteElement : waybillRouteElements) {
					ShunFengResponse.WaybillRoute waybillRoute = new ShunFengResponse.WaybillRoute();
					String mailno = waybillRouteElement.attributeValue("mailno");
					String opCode = waybillRouteElement.attributeValue("opCode");
					waybillRoute.setMailNo(mailno);
					waybillRoute.setOpCode(opCode);
					waybillRouteArrayList.add(waybillRoute);
				}
				response.setWaybillRouteList(waybillRouteArrayList);
				return response;
			}

            // 非路由推送响应实体
			String head = root.elementTextTrim("Head");
            if ("OK".equals(head)) {
                response.setResultFlag(true);
                if ("OrderService".equals(serviceValue)) {
                    // 下单返回结果处理
                    Element orderResponse = root.element("Body").element("OrderResponse");
                    Element rlsDetail = orderResponse.element("rls_info").element("rls_detail");
                    String mailno = orderResponse.attributeValue("mailno");
                    response.setMailNo(mailno);

                    /* 下单成功返回结果，参数具体含义看实体bean注释 */
                    CreateExpressOrderDTO expressOrderResponse = new CreateExpressOrderDTO();
                    expressOrderResponse.setMailNo(mailno);
                    expressOrderResponse.setDestCode(orderResponse.attributeValue("destcode"));
                    expressOrderResponse.setZipCode(orderResponse.attributeValue("origincode"));
                    List<RlsInfoDto> rlsInfoDtoList = new ArrayList<>(1);
                    RlsInfoDto rlsInfoDto = new RlsInfoDto();
                    rlsInfoDto.setCodingMapping(rlsDetail.attributeValue("codingMapping"));
                    rlsInfoDto.setCodingMappingOut("");
                    rlsInfoDto.setDestRouteLabel(rlsDetail.attributeValue("destRouteLabel"));
                    // 医药标识
                    rlsInfoDto.setPrintIcon("00001000");
                    rlsInfoDto.setProCode(rlsDetail.attributeValue("proCode"));
                    rlsInfoDto.setQrcode(rlsDetail.attributeValue("twoDimensionCode"));
                    rlsInfoDto.setSourceTransferCode(rlsDetail.attributeValue("sourceTransferCode"));
                    rlsInfoDtoList.add(rlsInfoDto);
                    expressOrderResponse.setRlsInfoDtoList(rlsInfoDtoList);
                    response.setOrderResponse(expressOrderResponse);
                } else if("RouteService".equals(serviceValue)) {
                    // 路由查询结果处理
					Element routeResponse = root.element("Body").element("RouteResponse");
					List<ShunFengResponse.DataBean> dataBeanList = new ArrayList<>(10);
					if (null != routeResponse) {
						String mailno = routeResponse.attributeValue("mailno");
						response.setMailNo(mailno);
						response.setCom(ExpressCompanyType.shunfeng);
						List<Element> elements = routeResponse.elements();
						for (Element element : elements) {
							ShunFengResponse.DataBean dataBean = new ShunFengResponse.DataBean();
							dataBean.setTime(element.attributeValue("accept_time"));
							dataBean.setContext(element.attributeValue("remark"));
							dataBeanList.add(dataBean);
						}
						response.setData(dataBeanList);
					}
				}
            } else {
                response.setResultFlag(false);
                String errorMsg = root.elementTextTrim("ERROR");
                response.setErrorMsg(errorMsg);
            }

		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}
}
