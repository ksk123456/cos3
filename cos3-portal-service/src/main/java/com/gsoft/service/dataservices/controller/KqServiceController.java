package com.gsoft.service.dataservices.controller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.service.common.constans.ServiceResultConstant;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "苍穹地图服务", description = "苍穹地图服务数据接口服务")
@RestController
@RequestMapping("/kqservice")
public class KqServiceController {
	@ApiOperation("测试接口")
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public String getWidgetBar(@RequestParam String path) throws JSONException {
		JSONObject jo = new JSONObject();
		
		jo.put("status", ServiceResultConstant.RESULT_RETURN_OK_STATUS);
		JSONObject dataJo = new JSONObject();
		
		JSONArray jsonArr = new JSONArray();
		jsonArr.put("测试成功");
		jsonArr.put("谢谢");
		
		dataJo.put("xAxis", jsonArr);
		jo.put("data", dataJo);		
		return jo.toString();
	}
}
