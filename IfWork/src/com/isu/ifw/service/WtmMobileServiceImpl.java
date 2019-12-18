package com.isu.ifw.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.isu.ifw.mapper.WtmApplMapper;
import com.isu.ifw.mapper.WtmInoutHisMapper;
import com.isu.ifw.mapper.WtmWorktimeMapper;
import com.isu.ifw.repository.WtmApplCodeRepository;

@Service("mobileService")
public class WtmMobileServiceImpl implements WtmMobileService{
	
	private final Logger logger = LoggerFactory.getLogger("ifwFileLog");
	
	@Autowired
	WtmWorktimeMapper timeMapper;
	
	@Autowired
	WtmInoutHisMapper inoutMapper;

	@Autowired
	WtmApplMapper applMapper;
	
	@Autowired
	WtmApplCodeRepository wtmApplCodeRepo;

	@Autowired
	WtmFlexibleEmpService empService;
	
	@Autowired
	WtmCodeService codeService;
	
	//기간 리스트 조회
	@Override
	public List<Map<String, Object>> getTermList(Map<String, Object> paramMap) throws Exception  {
		//겸직 하위 조직 조회
		paramMap.put("orgList", empService.getLowLevelOrgList(Long.parseLong(paramMap.get("tenantId").toString()), paramMap.get("enterCd").toString(), paramMap.get("sabun").toString(), paramMap.get("month").toString() + "01"));
		
		return timeMapper.getTermList(paramMap);	
	}

	//기간 내에 포함된 부서원 조회
	@Override
	public List<Map<String, Object>> getTeamList(Map<String, Object> paramMap) throws Exception  {

		return timeMapper.getTeamList(paramMap);	
	}

	//특정 기간 타각이력
	@Override
	public List<Map<String, Object>> getTeamDetail(Map<String, Object> paramMap) throws Exception {

		return inoutMapper.getInoutListTerm(paramMap);
	}
	
	public Map<String,Object> getCodeList(Long tenantId, String enterCd, String key) throws Exception {
//		Map<String,Map<String,Object>> itemPropertiesMap = new HashMap();
		
		List<Map<String,Object>> itemCollection = new ArrayList();
		List<Map<String,Object>>codeList = codeService.getCodeList(tenantId, enterCd, key);
	
		for(Map<String,Object> row : codeList) {
			String value = (String)row.get("codeCd");
			String text = (String)row.get("codeNm");
			Map<String,Object> item = new HashMap<String,Object>();
			item.put("text", text);
			item.put("value", value);
			itemCollection.add(item);
		}
		Map<String,Object> item = new HashMap();
		item.put("collection", itemCollection);
//		itemPropertiesMap.put("gubun",item);
		return item;
	}

}