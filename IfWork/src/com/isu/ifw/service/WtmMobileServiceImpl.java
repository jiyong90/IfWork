package com.isu.ifw.service;

import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.isu.ifw.common.service.TenantConfigManagerService;
import com.isu.ifw.mapper.WtmApplMapper;
import com.isu.ifw.mapper.WtmCalendarMapper;
import com.isu.ifw.mapper.WtmEntryApplMapper;
import com.isu.ifw.mapper.WtmInoutHisMapper;
import com.isu.ifw.mapper.WtmOtApplMapper;
import com.isu.ifw.mapper.WtmWorktimeMapper;
import com.isu.ifw.repository.WtmApplCodeRepository;
import com.isu.ifw.util.MobileUtil;
import com.isu.ifw.vo.WtmApplLineVO;

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
	
	@Autowired
	WtmOtApplMapper wtmOtApplMapper;

	@Autowired
	WtmEntryApplMapper wtmEntryApplMapper;
	
	@Autowired
	TenantConfigManagerService tcms;
		
	@Autowired
	WtmCalendarMapper calendarMapper;

	@Override
	public List<Map<String, Object>> getTermList(Map<String, Object> paramMap) throws Exception  {
		//겸직 하위 조직 조회
		paramMap.put("orgList", empService.getLowLevelOrgList(Long.parseLong(paramMap.get("tenantId").toString()), paramMap.get("enterCd").toString(), paramMap.get("sabun").toString(), paramMap.get("month").toString() + "01"));
		
		return timeMapper.getTermList(paramMap);	
	}
	
	@Override
	public List<Map<String, Object>> getPlan(Map<String, Object> paramMap) throws Exception  {

		return calendarMapper.getEmpWorkDayResult(paramMap);	
	}

	//기간 내에 포함된 부서원 조회
	@Override
	public List<Map<String, Object>> getTeamList(Map<String, Object> paramMap) throws Exception  {
		//겸직 하위 조직 조회
		paramMap.put("orgList", empService.getLowLevelOrgList(Long.parseLong(paramMap.get("tenantId").toString()), paramMap.get("enterCd").toString(), paramMap.get("sabun").toString(), paramMap.get("month").toString() + "01"));

		return timeMapper.getTeamList(paramMap);	
	}

	//특정 기간 타각이력
	@Override
	public List<Map<String, Object>> getTeamDetail(Map<String, Object> paramMap) throws Exception {

		return inoutMapper.getInoutListTerm(paramMap);
	}
	
	//code로 콤보박스 그리기
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
	
	//hr에서 데이터 조회 하기 위한 서비스
	public Map<String, Object> getDataMap(String url, String queryId, Map<String,Object> user) throws Exception {
		Map data = new HashMap();
		RestTemplate restTemplate = new RestTemplate();
		((org.springframework.http.client.SimpleClientHttpRequestFactory)
		        restTemplate.getRequestFactory()).setConnectTimeout(1000*3);
		
		ResponseEntity<Map> responseEntity = null;
		
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url) .queryParam("cmd", queryId) ;
//				.queryParam("enterCd", )
//				.queryParam("sabun", sabun)
		
        for(String key : user.keySet()) {
        	builder.queryParam(key, user.get(key));
        }

        URI uri = builder.build().toUri(); 
	
		responseEntity = restTemplate.getForEntity(uri, Map.class);
		System.out.println("responseEntity  " + responseEntity.getBody());
		logger.debug("getDataMap " + url + ", " + queryId + ", " + user.toString());
		
		if(responseEntity.getStatusCodeValue() != 200) {
			logger.debug("getDataMap " + responseEntity.getStatusCodeValue() + " : " + responseEntity.getBody());
			throw new Exception();
		}
		
		Map<String, Object> result = responseEntity.getBody();
		if(!result.get("Message").equals("")) {
			logger.debug("getDataMap " + result.get("Message"));
			return null;
		}
		data.putAll((Map)result.get("DATA"));
		
		return data;
	}
	
	public List<Map<String, Object>> getApplList(Long tenantId, String enterCd, String sabun, String typeCd, int startPage, int pageCount) throws Exception {
		Map<String, Object> paramMap = new HashMap();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("empNo", sabun);
		paramMap.put("typeCd", typeCd);
		paramMap.put("startPage", startPage);
		paramMap.put("pageCount", pageCount);
		
		List<Map<String, Object>> apprList = null;
		if(typeCd.equals("01"))
			apprList = applMapper.getMobileApprList01(paramMap);
		else if(typeCd.equals("02"))
			apprList = applMapper.getMobileApprList02(paramMap);
		else if(typeCd.equals("03"))
			apprList = applMapper.getMobileApprList03(paramMap);
	
		if(apprList != null && apprList.size() > 0) {
			for(Map<String, Object> appr : apprList) {
				appr.put("rowSeq", "" + startPage++ );
				appr.put("applStatNm", appr.get("applStatusNm"));
				appr.put("applEmpNm", appr.get("empNm"));
				appr.put("applYmd", appr.get("applYmd").toString().substring(0, 4)+"."+appr.get("applYmd").toString().substring(4, 6)+"."+appr.get("applYmd").toString().substring(6, 8));
			}
		}
		return apprList;
	}
	
	public Map<String, Object> getApplDetail(Long tenantId, String enterCd, String sabun, String applKey) throws Exception {
		Map<String, Object> resultMap = new HashMap();
		
		String applCd = applKey.split("@")[1];
		String applId = applKey.split("@")[2];
		String typeCd = applKey.split("@")[3];
		String applSabun = applKey.split("@")[4];
		Map<String, Object> paramMap = new HashMap();
		paramMap.put("applId", applId);
		paramMap.put("sabun", applSabun);
		
		Map<String, Object> data = null;
		if("OT".equals(applCd)) {
			
			List<Map<String, Object>> otDetails = wtmOtApplMapper.otApplDetailByApplId(paramMap);
			if(otDetails.size() == 1) {
				data = otDetails.get(0);
				if(!data.get("subsYmd").equals("-")) {
					data.put("subsYmd", data.get("subsYmd").toString().replace("@", "\n"));
				}
			} else {
				List<Map<String, Object>> items = new ArrayList();
				for(int i = 0; i < otDetails.size(); i++) {
					Map<String, Object> ot = otDetails.get(i);
					Map<String, Object> item = new HashMap();
					item.put("itemType", "text");
					item.put("title", ot.get("empNm") + " " +  ot.get("otStime") + "~" + ot.get("otEtime"));
					item.put("key", i);
					items.add(item);
					data.put(i+"", ot.get("reason") + " " + ot.get("reasonNm") +"\\n" + ot.get("subYn"));
				}
				resultMap.put("items", items);
				resultMap.put("data", data);
				
			}
		} else if("ENTRY_CHG".equals(applCd)) {
		    data = wtmEntryApplMapper.findByApplId(Long.parseLong(applId));
		}
		List<WtmApplLineVO> applLine = applMapper.getWtmApplLineByApplId(Long.parseLong(applId));
		List approvalLines = MobileUtil.makeApprLines(applLine);

		if(typeCd.equals("02")) {
			resultMap.put("useAppBtn", "true");

//			preference: {
//				"useRejectBtn": false
//				"useConfirmBtn": false
//				"useRemoveBtn": false
//				"useExtendBtn": false
//				"rejectBtnLabel": "승인"
//				"confirmBtnLabel": "반려"
//				"removeBtnLabel": "삭제"
//				"extendBtnLabel": "재상신"
//			},
//
//			actions: {
//				"url_reject": "https://"
//			    "url_confirm": "https://"
//			    "url_remove": "https://"
//			    "url_extend": "view://"
//			}
			
			
			Map<String,Object> preference = new HashMap();
			preference.put("rejectBtnLabel", "반려");
			preference.put("useRejectBtn", "true");
			preference.put("confirmBtnLabel", "승인");
			preference.put("useConfirmBtn", "true");
			resultMap.put("preference", preference);
			
			Map<String,Object> actions = new HashMap();
			String url = tcms.getConfigValue(tenantId, "WTMS.URL.EDOC_ACTION", true, "");	
			actions.put("url_reject", url);
			actions.put("url_confirm", url);
			
			resultMap.put("actions", actions);
			
		} else {
			resultMap.put("useAppBtn", "false");
		}
		
		resultMap.put("title", "신청서");
		resultMap.put("data", data);
		resultMap.put("apprLines", approvalLines);

		return resultMap;
	}
}