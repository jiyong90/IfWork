package com.isu.ifw.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.isu.ifw.mapper.WtmApiMapper;

@Service("WtmApiServiceImpl")
public class WtmApiServiceImpl implements WtmApiService{

	@Autowired
	WtmApiMapper apiMapper;
	
	@Override
	public List<Map<String, Object>> getEmpList(String datetime, String updateType, Long tenantId)  throws Exception{
		// TODO Auto-generated method stub
		
		if(datetime == null || "".equals(datetime)) {
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			datetime = format.format(new Date());
		}

		if(updateType == null || "".equals(updateType)) {
			updateType = "I";
		}
		
		
		HashMap<String,Object> paramMap = new HashMap<String,Object>();
		
		paramMap.put("baseYmd", datetime);
		paramMap.put("updateType", updateType);
		paramMap.put("tenantId", tenantId);		
		
		return apiMapper.getEmpList(paramMap);
	}
	
}
