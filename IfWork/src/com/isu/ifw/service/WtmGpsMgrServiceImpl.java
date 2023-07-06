package com.isu.ifw.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.isu.ifw.mapper.WtmGpsMgrMapper;
import com.isu.ifw.repository.WtmFlexibleStdMgrRepository;
import com.isu.ifw.util.WtmUtil; 


@Service
public class WtmGpsMgrServiceImpl implements WtmGpsMgrService{
	
	private final Logger logger = LoggerFactory.getLogger("ifwDBLog");
	
	@Resource
	WtmFlexibleStdMgrRepository flexibleStdMgrRepository; //20200709 안흥규 근무제 적용 관리
	
	@Autowired
	WtmGpsMgrMapper wtmGpsMapper;
	@Autowired
	WtmFlexibleEmpService empService;
	@Override
	public List<Map<String, Object>> getGpsList(Long tenantId, String enterCd, Map<String, Object> paramMap) throws Exception {
		List<Map<String, Object>> gpsMgrList = new ArrayList();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		String sabun = paramMap.get("sabun").toString();
		String ymd = WtmUtil.parseDateStr(new Date(), "yyyyMMdd");
		
		if(paramMap.get("ymd")!=null && !"".equals("ymd")) {
			ymd = paramMap.get("ymd").toString().replaceAll("[-.]", "");
			paramMap.put("ymd", ymd);
		}
		
		List<Map<String, Object>> results = wtmGpsMapper.getGpsList(paramMap);
		if(results!=null && results.size()>0) {
			for(Map<String, Object> r : results) {
				if(r.get("gpsMgrId")!=null && !"".equals(r.get("gpsMgrId"))) {
					Map<String, Object> rtList = new HashMap();
					rtList.put("gpsMgrId", r.get("gpsMgrId").toString());
					rtList.put("tenantId", r.get("tenantId").toString());
					rtList.put("enterCd", r.get("enterCd").toString());
					rtList.put("sabun", r.get("sabun").toString());
					rtList.put("empNm", r.get("empNm").toString());
					rtList.put("orgNm", r.get("orgNm").toString());
					rtList.put("latCd", r.get("latCd").toString());
					rtList.put("lotCd", r.get("lotCd").toString());
					rtList.put("inoutType", r.get("inoutType").toString());
					rtList.put("inoutDate", r.get("inoutDate").toString());
					rtList.put("flexibleStdMgrId", r.get("flexibleStdMgrId").toString());
					gpsMgrList.add(rtList);
				}
			}
		}
		
		return gpsMgrList;
	}
	
	
}