package com.isu.ifw.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isu.ifw.common.entity.CommTenantModule;
import com.isu.ifw.common.repository.CommTenantModuleRepository;
import com.isu.ifw.entity.WtmAppl;
import com.isu.ifw.entity.WtmFlexibleStdMgr;
import com.isu.ifw.entity.WtmIfTaaHis;
import com.isu.ifw.entity.WtmIntfCode;
import com.isu.ifw.entity.WtmIntfEmp;
import com.isu.ifw.entity.WtmIntfEmpAddr;
import com.isu.ifw.entity.WtmIntfGnt;
import com.isu.ifw.entity.WtmIntfHoliday;
import com.isu.ifw.entity.WtmIntfOrg;
import com.isu.ifw.entity.WtmIntfOrgChart;
import com.isu.ifw.entity.WtmIntfOrgConc;
import com.isu.ifw.entity.WtmIntfTaaAppl;
import com.isu.ifw.entity.WtmTaaAppl;
import com.isu.ifw.entity.WtmTaaApplDet;
import com.isu.ifw.entity.WtmTaaCode;
import com.isu.ifw.entity.WtmTimeCdMgr;
import com.isu.ifw.entity.WtmWorkCalendar;
import com.isu.ifw.entity.WtmWorkDayResult;
import com.isu.ifw.mapper.WtmFlexibleEmpMapper;
import com.isu.ifw.mapper.WtmInterfaceMapper;
import com.isu.ifw.repository.WtmApplRepository;
import com.isu.ifw.repository.WtmFlexibleStdMgrRepository;
import com.isu.ifw.repository.WtmIfTaaHisRepository;
import com.isu.ifw.repository.WtmIntfCodeRepository;
import com.isu.ifw.repository.WtmIntfEmpAddrRepository;
import com.isu.ifw.repository.WtmIntfEmpRepository;
import com.isu.ifw.repository.WtmIntfGntRepository;
import com.isu.ifw.repository.WtmIntfHolidayRepository;
import com.isu.ifw.repository.WtmIntfOrgChartRepository;
import com.isu.ifw.repository.WtmIntfOrgConcRepository;
import com.isu.ifw.repository.WtmIntfOrgRepository;
import com.isu.ifw.repository.WtmIntfTaaApplRepository;
import com.isu.ifw.repository.WtmTaaApplDetRepository;
import com.isu.ifw.repository.WtmTaaApplRepository;
import com.isu.ifw.repository.WtmTaaCodeRepository;
import com.isu.ifw.repository.WtmTimeCdMgrRepository;
import com.isu.ifw.repository.WtmWorkCalendarRepository;
import com.isu.ifw.repository.WtmWorkDayResultRepository;
import com.isu.ifw.util.WtmUtil;
import com.isu.ifw.vo.ReturnParam;

@Service("wtmInterfaceService")
public class WtmInterfaceServiceImpl implements WtmInterfaceService {
	private static final Logger logger = LoggerFactory.getLogger("ifwFileLog");
	
	@Autowired
	WtmInterfaceMapper wtmInterfaceMapper;
	
	@Autowired
	WtmFlexibleEmpMapper wtmFlexibleEmpMapper;
	
	@Autowired
	private WtmFlexibleEmpService wtmFlexibleEmpService;
	
	@Autowired
	WtmInoutService inoutService;
	
	@Autowired
	private WtmIntfCodeRepository wtmCodeIntfRepo;
	@Autowired
	private WtmIntfEmpRepository wtmEmpIntfRepo;
	@Autowired
	private WtmIntfEmpAddrRepository wtmEmpAddrIntfRepo;
	@Autowired
	private WtmIntfGntRepository wtmGntIntfRepo;
	@Autowired
	private WtmIntfHolidayRepository wtmHolidayIntfRepo;
	@Autowired
	private WtmIntfOrgRepository wtmOrgIntfRepo;

	@Autowired
	private WtmIntfOrgChartRepository wtmOrgChartIntfRepo;

	@Autowired
	private WtmIntfOrgConcRepository wtmOrgConcIntfRepo;
	@Autowired
	private WtmIntfTaaApplRepository wtmTaaIntfRepo; 

	
	@Autowired
	private WtmTaaApplRepository wtmTaaApplRepo;
	
	@Autowired
	private WtmTaaCodeRepository wtmTaaCodeRepo;
	
	@Autowired
	private WtmWorkDayResultRepository dayResultRepo;
	
	@Autowired
	private WtmTaaApplDetRepository wtmTaaApplDetRepo;
	
	@Autowired
	private WtmIfTaaHisRepository wtmIfTaaHisRepo;

	@Autowired private WtmWorkCalendarRepository workCalendarRepo;
	
	@Autowired
	@Qualifier("WtmTenantModuleRepository")
	CommTenantModuleRepository tenantModuleRepo;
	
	@Autowired
	WtmApplRepository wtmApplRepo;
	
	@Autowired private WtmValidatorService validatorService;
	
	@Autowired private WtmWorkDayResultRepository workDayResultRepo;
	
	@Autowired private WtmFlexibleStdMgrRepository flexStdMgrRepo;
	
	@Autowired private WtmCalcService calcService;
	
	@Autowired private WtmTimeCdMgrRepository timeCdMgrRepo;
	
	@Override
	public Map<String, Object> getIfLastDate(Long tenantId, String ifType) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("getIfLastDate tenantId : " + tenantId + ", ifType : " + ifType);
		String lastDataTime = null;
		String nowDataTime = null;
		Map<String, Object> retMap = new HashMap<>();
		// 2. 건별 data 저장
		try {
			// DATA KEY기준으로 SELECT 
			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("tenantId", tenantId);
			paramMap.put("ifType", ifType);
			Map<String, Object> result = wtmInterfaceMapper.getIfLastDate(paramMap);
//			for ( String key : result.keySet() ) {
//    		    System.out.println("key : " + key +" / value : " + result.get(key));
//    		}
			// System.out.println("getIfLastDate result : " + result.toString());
			if(result != null && result.size() > 0) {
				try {
        			lastDataTime = result.get("lastDate").toString();
				} catch(Exception e){
		            e.printStackTrace();
		        }
			} else {
				// 이관이력이 없으면 그냥 과거부터 쭉쭉 옮기자
				lastDataTime = "19000101000000";
			}
			
			result = wtmInterfaceMapper.getIfNowDate(paramMap);
			// System.out.println("getIfNowDate result : " + result.toString());
			if(result != null && result.size() > 0) {
				try {
					nowDataTime = result.get("ifDate").toString();
				} catch(Exception e){
		            e.printStackTrace();
		        }
			} else {
				nowDataTime = "19000101000000";
			}
			System.out.println("getIfLastDate Ret lastDate : " + lastDataTime + ", nowDate : " + nowDataTime);
			retMap.put("lastDate", lastDataTime);
			retMap.put("nowDate", nowDataTime);
		} catch (Exception e) {
			System.out.println("getIfLastDate Exception!!!!!!!");
            e.printStackTrace();
		}
		return retMap;
	}
	
	@Override
	public HashMap getIfRt(String url) throws Exception {
		// TODO Auto-generated method stub
		HashMap<String, Object> getIfMap = null;
		try {
        	RestTemplate restTemplate = new RestTemplate();
	   		getIfMap = restTemplate.getForObject(url, HashMap.class);
		} catch (Exception e) {
            e.printStackTrace();
		}
		return getIfMap;
	}
	
	@Override
	public String setIfUrl(Long tenantId, String ifaddUrl, String param) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("setIfUrl tenantId : " + tenantId + ", ifaddUrl : " + ifaddUrl + ", param : " + param);
		String ifUrl = "";
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("tenantId", tenantId);
		Map<String, Object> result = wtmInterfaceMapper.getIfUrl(paramMap);
		
		if(result != null && result.size() > 0) {
			try {
				System.out.println("info_data : " + result.get("infoData").toString());
				
				ifUrl = result.get("infoData").toString() + ifaddUrl + param+"&tenantId="+tenantId;
				// System.out.println("ifUrl : " + ifUrl);
			} catch(Exception e){
	            e.printStackTrace();
	        }
		}
		System.out.println("setIfUrl Rtn ifUrl : " + ifUrl);
		return ifUrl;
	}
	
	@Override
	public void getCodeIfResult(Long tenantId) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("WtmInterfaceServiceImpl getCodeIfResult");
        try {
        	// 인터페이스 결과 저장용
        	String retMsg = null;
        	int resultCnt = 0;
        	String ifType = "V_IF_WTM_CODE";
        	Map<String, Object> ifHisMap = new HashMap<>();
        	ifHisMap.put("tenantId", tenantId);
        	ifHisMap.put("ifItem", ifType);
        	
        	// 인터페이스용 변수
        	String lastDataTime = null;
        	String nowDataTime = null;
        	HashMap<String, Object> getDateMap = null;
        	HashMap<String, Object> getIfMap = null;
        	List<Map<String, Object>> getIfList = null;
        	
        	// 최종 자료 if 시간 조회
        	try {
        		getDateMap = (HashMap<String, Object>) getIfLastDate(tenantId, ifType);
        		lastDataTime = getDateMap.get("lastDate").toString();
        		nowDataTime = getDateMap.get("nowDate").toString();
	        	try {
	        		String param = "?lastDataTime="+lastDataTime;
		        	String ifUrl = setIfUrl(tenantId, "/code", param);
			   		getIfMap = getIfRt(ifUrl);
			   		System.out.println("getIfMap.size() : " + getIfMap.size());
			   		if (getIfMap != null && getIfMap.size() > 0) {
			   			
			   			String ifMsg = getIfMap.get("message").toString();
			   			getIfList = (List<Map<String, Object>>) getIfMap.get("ifData");
			   		} else {
			   			retMsg = "Code get : If 데이터 없음";
			   		}
	        	} catch(Exception e) {
	        		retMsg = "Code get : 서버통신 오류";
	        	}
        	} catch(Exception e) {
        		retMsg = "Code get : 최종갱신일 조회오류";
        	}
        	// 조회된 자료가 있으면...
   			if(retMsg == null && getIfList != null && getIfList.size() > 0) {

   	        	
   	        	// String[] hrGrpCode = {"H20010", "H20020", "H20030", "H10050", "H10110", "T10003", "W20010"};
   	        	// String[] wtmGrpCode = {"CLASS_CD", "DUTY_CD", "POS_CD", "JOG_GROUP_CD", "JOB_CD", "PAY_TYPE_CD", "TAA_TYPE_CD", "ORG_TYPE"};
   	        	List<Map<String, Object>> ifList = new ArrayList();
   	        	List<Map<String, Object>> ifUpdateList = new ArrayList();
   	        	for(int i=0; i<getIfList.size(); i++) {
   	        		Map<String, Object> ifMap = new HashMap<>();

   	        		// int j = Arrays.asList(hrGrpCode).indexOf(getIfList.get(i).get("GRCODE_CD"));

   	        		
   	        		ifMap.put("tenantId", tenantId);
   	        		ifMap.put("enterCd", getIfList.get(i).get("ENTER_CD"));
   	        		ifMap.put("grpCodeCd", getIfList.get(i).get("GRCODE_CD"));
   	        		ifMap.put("codeCd", getIfList.get(i).get("CODE"));
   	        		ifMap.put("codeNm", getIfList.get(i).get("CODE_NM"));
   	        		ifMap.put("symd", getIfList.get(i).get("SYMD"));
   	        		ifMap.put("eymd", getIfList.get(i).get("EYMD"));
   	        		ifMap.put("seq", getIfList.get(i).get("SEQ"));
   	        		ifMap.put("note", getIfList.get(i).get("NOTE"));
   	        		try {
   	        			// DATA KEY기준으로 SELECT 
   	        			Map<String, Object> result = wtmInterfaceMapper.getWtmCodeId(ifMap);
   	        			
   	        			if(result != null) {
   	        				try {
   		            			String codeId = result.get("codeId").toString();
   		            			//System.out.println(codeId);
   		            			if(codeId != null && codeId.equals("")) {
   		            				ifMap.put("codeId", codeId);
   		            				ifUpdateList.add(ifMap);
   		            			}
   	        				} catch(Exception e){
   	        					retMsg = "code set : code id 조회시 오류";
   	        		            e.printStackTrace();
   	        		            // 에러걸리면 그냥 아웃시키기
   	        		            break;
   	        		        }
   	        			} else {
   	        				ifList.add(ifMap);
   	        			}
   	    			} catch (Exception e) {
   	    				retMsg = "code set : code 데이터 이관시 오류";
   			            e.printStackTrace();
   			            // 에러걸리면 그냥 아웃시키기
   			            break;
   	    			}
   	        	}
   	        	
   	        	if(retMsg == null || "".equals(retMsg) ) {
   	        		try {
   		        		//수정건이 있으면....
   		        		if (ifUpdateList.size() > 0) {
   		        			//System.out.println("update size : " + ifUpdateList.size());
   		        			resultCnt += wtmInterfaceMapper.updateWtmCode(ifUpdateList);
   		        		}
   		        		// 추가건이 있으면
   		        		if (ifList.size() > 0) {
   		        			//System.out.println("insert size : " + ifList.size());
   		        			resultCnt += wtmInterfaceMapper.insertWtmCode(ifList);
   		        		}
   		        		if(resultCnt > 0) {
   		        			retMsg = resultCnt + "건 반영완료";
   		        		} else {
   		        			retMsg = "갱신자료없음";
   		        		}
   		        		ifHisMap.put("ifStatus", "OK");
   		        		
   		        		// 이력데이터 수정은 건별로
   		        		for(int i=0; i< ifList.size(); i++) {
   		        			Map<String, Object> ifCodeHisMap = new HashMap<>();
   		        			ifCodeHisMap = ifList.get(i);
   		        			try {
   			    				int resultCnt2 = 0 ;
   			    				resultCnt2 = wtmInterfaceMapper.updateWtmCodeHisEymd(ifCodeHisMap);
   			    				resultCnt2 = wtmInterfaceMapper.updateWtmCodeHisSymd(ifCodeHisMap);
   		        			} catch (Exception e) {
   		            			// 이력수정의 오류는 어쩌나?
   		        				retMsg = "code set : 이력수정 중 오류";
   		    		            e.printStackTrace();
   		        			}
   		        		}
   	        		} catch (Exception e) {
   	        			ifHisMap.put("ifStatus", "ERR");
   	        			retMsg = e.getMessage();
   			            e.printStackTrace();
   	    			}
   	        	} else {
   	        		ifHisMap.put("ifStatus", "ERR");
   	        	}
   			} else {
   				retMsg = "갱신자료없음";
   				ifHisMap.put("ifStatus", "OK");
   			}
   			// 3. 처리결과 저장
    		try {
    			ifHisMap.put("ifEndDate", lastDataTime);
    			ifHisMap.put("updateDate", nowDataTime);
    			// WTM_IF_HIS 테이블에 결과저장
    			ifHisMap.put("ifMsg", retMsg);
				wtmInterfaceMapper.insertIfHis(ifHisMap);
			} catch (Exception e) {
				e.printStackTrace();
			}
        } catch(Exception e){
            e.printStackTrace();
        }
	}
	
	@Override
	public void getHolidayIfResult(Long tenantId) throws Exception {
		// TODO Auto-generated method stub
		
		// 인터페이스 결과 저장용
    	String retMsg = null;
    	int resultCnt = 0;
    	String ifType = "V_IF_WTM_HOLIDAY";
    	Map<String, Object> ifHisMap = new HashMap<>();
    	ifHisMap.put("tenantId", tenantId);
    	ifHisMap.put("ifItem", ifType);
    	
    	// 인터페이스용 변수
    	String lastDataTime = null;
    	String nowDataTime = null;
    	HashMap<String, Object> getDateMap = null;
    	HashMap<String, Object> getIfMap = null;
    	List<Map<String, Object>> getIfList = null;
    	
    	// 최종 자료 if 시간 조회
    	try {
    		getDateMap = (HashMap<String, Object>) getIfLastDate(tenantId, ifType);
    		lastDataTime = getDateMap.get("lastDate").toString();
    		nowDataTime = getDateMap.get("nowDate").toString();
        	try {
        		String param = "?lastDataTime="+lastDataTime;
	        	String ifUrl = setIfUrl(tenantId, "/holiday", param); 
	        	getIfMap = getIfRt(ifUrl);
		   		
		   		if (getIfMap != null && getIfMap.size() > 0) {
		   			String ifMsg = getIfMap.get("message").toString();
		   			getIfList = (List<Map<String, Object>>) getIfMap.get("ifData");
		   		} else {
		   			retMsg = "Holiday get : If 데이터 없음";
		   		}
        	} catch(Exception e) {
        		retMsg = "Holiday get : If 서버통신 오류";
        	}
    	} catch(Exception e) {
    		retMsg = "Holiday get : 최종갱신일 조회오류";
    	}
    	// 조회된 자료가 있으면...
		if(retMsg == null && getIfList != null && getIfList.size() > 0) {
	        try {
	        	List<Map<String, Object>> ifList = new ArrayList();
   	        	List<Map<String, Object>> ifUpdateList = new ArrayList();
   	        	for(int i=0; i<getIfList.size(); i++) {
	        		Map<String, Object> ifMap = new HashMap<>();
	        		ifMap.put("tenantId", tenantId);
	        		ifMap.put("enterCd", getIfList.get(i).get("ENTER_CD"));
	        		ifMap.put("locationCd", getIfList.get(i).get("LOCATION_CD"));
	        		ifMap.put("holidayYmd", getIfList.get(i).get("YMD"));
	        		ifMap.put("holidayNm", getIfList.get(i).get("HOLIDAY_NM"));
	        		ifMap.put("sunYn", "Y");
	        		//System.out.println(getIfList.get(i).get("YMD") + "///" + getIfList.get(i).get("FESTIVE_YN"));
	        		ifMap.put("festiveYn", getIfList.get(i).get("FESTIVE_YN"));
	        		ifMap.put("payYn", getIfList.get(i).get("PAY_YN"));
	        		ifList.add(ifMap);
	        	}
	        	if (ifList.size() > 0) {
		        	// 2. data 저장
		    		try {
		    			// key값을 기준으로 mergeinto하자
						resultCnt = wtmInterfaceMapper.insertWtmHoliday(ifList);
						retMsg = resultCnt + "건 반영완료";
						ifHisMap.put("ifStatus", "OK");
					} catch (Exception e) {
						ifHisMap.put("ifStatus", "ERR");
						retMsg = "Holiday set : 데이터 저장 오류";
						e.printStackTrace();
					}
	        	} else {
	        		retMsg = "갱신자료없음";
					ifHisMap.put("ifStatus", "OK");
	        	}
	        } catch(Exception e){
	            e.printStackTrace();
	        }
		} else {
			retMsg = "갱신자료없음";
			ifHisMap.put("ifStatus", "OK");
		}
        // 3. 처리결과 저장
		try {
			// 최종갱신된 일시조회
			ifHisMap.put("updateDate", nowDataTime);
			ifHisMap.put("ifEndDate", lastDataTime);
			// WTM_IF_HIS 테이블에 결과저장
			ifHisMap.put("ifMsg", retMsg);
			wtmInterfaceMapper.insertIfHis(ifHisMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return;
	}
	
	@Override
	public void getTaaCodeIfResult(Long tenantId) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("WtmInterfaceServiceImpl getTaaCodeIfResult");
		// 인터페이스 결과 저장용
    	String retMsg = null;
    	int resultCnt = 0;
    	String ifType = "V_IF_TAA_CODE";
    	Map<String, Object> ifHisMap = new HashMap<>();
    	ifHisMap.put("tenantId", tenantId);
    	ifHisMap.put("ifItem", ifType);
    	
    	// 인터페이스용 변수
    	String lastDataTime = null;
    	String nowDataTime = null;
    	HashMap<String, Object> getDateMap = null;
    	HashMap<String, Object> getIfMap = null;
    	List<Map<String, Object>> getIfList = null;
    	
    	// 최종 자료 if 시간 조회
    	try {
    		getDateMap = (HashMap<String, Object>) getIfLastDate(tenantId, ifType);
    		lastDataTime = getDateMap.get("lastDate").toString();
    		nowDataTime = getDateMap.get("nowDate").toString();
        	try {
        		String param = "?lastDataTime="+lastDataTime;
	        	String ifUrl = setIfUrl(tenantId, "/gntCode", param);  
	        	getIfMap = getIfRt(ifUrl);
		   		
		   		if (getIfMap != null && getIfMap.size() > 0) {
		   			String ifMsg = getIfMap.get("message").toString();
		   			getIfList = (List<Map<String, Object>>) getIfMap.get("ifData");
		   		} else {
		   			retMsg = "TaaCode get : If 데이터 없음";
		   		}
        	} catch(Exception e) {
        		retMsg = "TaaCode get : 서버통신 오류";
        	}
    	} catch(Exception e) {
    		retMsg = "TaaCode get : 최종갱신일 조회오류";
    	}
    	// 조회된 자료가 있으면...
		if(retMsg == null && getIfList != null && getIfList.size() > 0) {
			try {
	        	List<Map<String, Object>> ifList = new ArrayList();
   	        	List<Map<String, Object>> ifUpdateList = new ArrayList();
   	        	for(int i=0; i<getIfList.size(); i++) {
	        		Map<String, Object> ifMap = new HashMap<>();
	        		ifMap.put("tenantId", tenantId);
	        		ifMap.put("enterCd", getIfList.get(i).get("ENTER_CD"));
	        		ifMap.put("taaCd", getIfList.get(i).get("GNT_CD"));
	        		ifMap.put("taaNm", getIfList.get(i).get("GNT_NM"));
	        		ifMap.put("taaTypeCd", getIfList.get(i).get("GNT_GUBUN_CD"));
	        		ifMap.put("holInclYn", getIfList.get(i).get("HOL_INCL_YN"));
	        		ifMap.put("requestTypeCd", getIfList.get(i).get("REQUEST_USE_TYPE"));
	        		ifMap.put("workYn", getIfList.get(i).get("WORK_YN"));
	        		ifMap.put("payYn", getIfList.get(i).get("PAY_YN"));
	        		ifMap.put("note", getIfList.get(i).get("NOTE"));
        		
	        		// 2. 건별 data 저장
	        		try {
	        			// 중복여부 확인해서 insert/update 구분해야함.
	        			// DATA KEY기준으로 SELECT 
	        			Map<String, Object> result = wtmInterfaceMapper.getWtmTaaCodeId(ifMap);
	        			if(result != null) {
	        				try {
	        					String taaCodeId = result.get("taaCodeId").toString();
	        					if(taaCodeId != null && taaCodeId.equals("")) {
		            				ifMap.put("taaCodeId", taaCodeId);
		            				ifUpdateList.add(ifMap);
		            			}
	        				} catch(Exception e){
	        					retMsg = "TaaCode set : taa_code_id 조회오류";
	        		            e.printStackTrace();
	        		            // 에러걸리면 그냥 아웃시키기
	        		            break;
	        		        }
	        			} else {
	        				ifList.add(ifMap);
	        			}
	    			} catch (Exception e) {
	    				retMsg = "TaaCode set : taa_code 검증 오류";
	    				e.printStackTrace();
	    				// 에러걸리면 그냥 아웃시키기
			            break;
	    			}
   	        	}
        	
	        	if(retMsg == null || "".equals(retMsg) ) {
	        		try {
		        		//수정건이 있으면....
		        		if (ifUpdateList.size() > 0) {
		        			//System.out.println("update size : " + ifUpdateList.size());
		        			resultCnt += wtmInterfaceMapper.updateTaaCode(ifUpdateList);
		        		}
		        		// 추가건이 있으면
		        		if (ifList.size() > 0) {
		        			//System.out.println("insert size : " + ifList.size());
		        			resultCnt += wtmInterfaceMapper.insertTaaCode(ifList);
		        		}
		        		if(resultCnt > 0) {
		        			retMsg = resultCnt + "건 반영완료";
		        		} else {
		        			retMsg = "갱신자료없음";
		        		}
		        		ifHisMap.put("ifStatus", "OK");
	        		} catch (Exception e) {
	        			ifHisMap.put("ifStatus", "ERR");
	        			retMsg = "TaaCode set : taa_code 데이터 저장 오류";
			            e.printStackTrace();
	    			}
	        	} else {
	        		ifHisMap.put("ifStatus", "ERR");
	        	}
			} catch (Exception e) {
    			ifHisMap.put("ifStatus", "ERR");
    			retMsg = e.getMessage();
	            e.printStackTrace();
			}
		} else {
			retMsg = "갱신자료없음";
			ifHisMap.put("ifStatus", "OK");
		}
    	// 3. 처리결과 저장
		try {
			// WTM_IF_HIS 테이블에 결과저장
			ifHisMap.put("updateDate", nowDataTime);
   			ifHisMap.put("ifEndDate", lastDataTime);
			ifHisMap.put("ifMsg", retMsg);
			wtmInterfaceMapper.insertIfHis(ifHisMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
        System.out.println("WtmInterfaceServiceImpl getTaaCodeIfResult end");
		return;
	}
	
	@Override
	public void getOrgCodeIfResult(Long tenantId) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("WtmInterfaceServiceImpl getOrgCodeIfResult");
		// 인터페이스 결과 저장용
    	String retMsg = null;
    	int resultCnt = 0;
    	String ifType = "V_IF_ORG_CODE";
    	Map<String, Object> ifHisMap = new HashMap<>();
    	ifHisMap.put("tenantId", tenantId);
    	ifHisMap.put("ifItem", ifType);
    	
    	// 인터페이스용 변수
    	String lastDataTime = null;
    	String nowDataTime = null;
    	HashMap<String, Object> getDateMap = null;
    	HashMap<String, Object> getIfMap = null;
    	List<Map<String, Object>> getIfList = null;
    	
    	// 최종 자료 if 시간 조회
    	try {
    		getDateMap = (HashMap<String, Object>) getIfLastDate(tenantId, ifType);
    		lastDataTime = getDateMap.get("lastDate").toString();
    		nowDataTime = getDateMap.get("nowDate").toString();
        	try {
        		String param = "?lastDataTime="+lastDataTime;
	        	String ifUrl = setIfUrl(tenantId, "/orgCode", param); 
	        	getIfMap = getIfRt(ifUrl);
		   		
		   		if (getIfMap != null && getIfMap.size() > 0) {
		   			String ifMsg = getIfMap.get("message").toString();
		   			if(!"OK".equals(ifMsg)) {
		   				retMsg = "org get : " + ifMsg;
		   			} else {
		   				getIfList = (List<Map<String, Object>>) getIfMap.get("ifData");
		   			}
		   		} else {
		   			retMsg = "org get : If 데이터 없음";
		   		}
        	} catch(Exception e) {
        		retMsg = "org get : 서버통신 오류";
        	}
    	} catch(Exception e) {
    		retMsg = "org get : 최종갱신일 조회오류";
    	}
    	// 조회된 자료가 있으면...
		if(retMsg == null && getIfList != null && getIfList.size() > 0) {
	        try {
	        	List<Map<String, Object>> ifList = new ArrayList();
   	        	for(int i=0; i<getIfList.size(); i++) {
	        		Map<String, Object> ifMap = new HashMap<>();
	        		ifMap.put("tenantId", tenantId);
	        		ifMap.put("enterCd", getIfList.get(i).get("ENTER_CD"));
	        		ifMap.put("orgCd", getIfList.get(i).get("ORG_CD"));
	        		ifMap.put("orgNm", getIfList.get(i).get("ORG_NM"));
	        		ifMap.put("symd", getIfList.get(i).get("SYMD"));
	        		ifMap.put("eymd", getIfList.get(i).get("EYMD"));
	        		ifMap.put("orgType", getIfList.get(i).get("ORG_TYPE"));
	        		ifMap.put("note", getIfList.get(i).get("NOTE"));
	        		ifList.add(ifMap);
   	        	}
   	        	
        		try {
	        		// 추가건이 있으면
	        		if (ifList.size() > 0) {
	        			//System.out.println("insert size : " + ifList.size());
	        			resultCnt += wtmInterfaceMapper.insertWtmOrgCode(ifList);
	        		}
	        		if(resultCnt > 0) {
	        			retMsg = resultCnt + "건 반영완료";
	        		} else {
	        			retMsg = "갱신자료없음";
	        		}
	        		ifHisMap.put("ifStatus", "OK");
	        		
	        		// 이력데이터 수정은 건별로
	        		/*
	        		for(int i=0; i< ifList.size(); i++) {
	        			Map<String, Object> ifCodeHisMap = new HashMap<>();
	        			ifCodeHisMap = ifList.get(i);
	        			try {
		    				int resultCnt2 = 0 ;
		    				resultCnt2 = wtmInterfaceMapper.updateWtmOrgCodeHisEymd(ifCodeHisMap);
		    				resultCnt2 = wtmInterfaceMapper.updateWtmOrgCodeHisSymd(ifCodeHisMap);
	        			} catch (Exception e) {
	            			// 이력수정의 오류는 어쩌나?
	        				retMsg = "org set : 이력 갱신 오류";
	    		            e.printStackTrace();
	        			}
	        		}
	        		*/
        		} catch (Exception e) {
        			ifHisMap.put("ifStatus", "ERR");
        			retMsg = e.getMessage();
		            e.printStackTrace();
    			}
	        	
	        } catch(Exception e){
	        	ifHisMap.put("ifStatus", "ERR");
	            e.printStackTrace();
	        }
		} else {
			retMsg = "갱신자료없음";
			ifHisMap.put("ifStatus", "OK");
		}
        // 3. 처리결과 저장
		try {
			// 최종갱신된 일시조회
			ifHisMap.put("updateDate", nowDataTime);
   			ifHisMap.put("ifEndDate", lastDataTime);
			// WTM_IF_HIS 테이블에 결과저장
			ifHisMap.put("ifMsg", retMsg);
			wtmInterfaceMapper.insertIfHis(ifHisMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}
	
	@Override
	public void getOrgChartIfResult(Long tenantId) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("WtmInterfaceServiceImpl getOrgChartIfResult");
		// 인터페이스 결과 저장용
    	String retMsg = null;
    	int resultCnt = 0;
    	String ifType = "V_IF_ORG_CHART";
    	Map<String, Object> ifHisMap = new HashMap<>();
    	ifHisMap.put("tenantId", tenantId);
    	ifHisMap.put("ifItem", ifType);
    	
    	// 인터페이스용 변수
    	String lastDataTime = null;
    	String nowDataTime = null;
    	HashMap<String, Object> getDateMap = null;
    	HashMap<String, Object> getIfMap = null;
    	HashMap<String, Object> getIfDetMap = null;
    	List<Map<String, Object>> getIfList = null;
    	List<Map<String, Object>> getIfDetList = null;
    	
    	// 최종 자료 if 시간 조회
    	try {
    		getDateMap = (HashMap<String, Object>) getIfLastDate(tenantId, ifType);
    		lastDataTime = getDateMap.get("lastDate").toString();
    		nowDataTime = getDateMap.get("nowDate").toString();
        	try {
        		String param = "?lastDataTime="+lastDataTime;
	        	String ifUrl = setIfUrl(tenantId, "/orgChartMgr", param); 
	        	getIfMap = getIfRt(ifUrl);
		   		
		   		if (getIfMap != null && getIfMap.size() > 0) {
		   			String ifMsg = getIfMap.get("message").toString();
		   			if(!"OK".equals(ifMsg)) {
		   				retMsg = "orgChart get : " + ifMsg;
		   			} else {
		   				getIfList = (List<Map<String, Object>>) getIfMap.get("ifData");
		   			}
		   		} else {
		   			retMsg = "org get : If 데이터 없음";
		   		}
        	} catch(Exception e) {
        		retMsg = "org get : 서버통신 오류";
        	}
    	} catch(Exception e) {
    		retMsg = "org get : 최종갱신일 조회오류";
    	}
    	// 조회된 자료가 있으면...
		if(retMsg == null && getIfList != null && getIfList.size() > 0) {
	        try {
   	        	for(int i=0; i<getIfList.size(); i++) {
	        		Map<String, Object> ifMap = new HashMap<>();
	        		ifMap.put("tenantId", tenantId);
	        		ifMap.put("enterCd", getIfList.get(i).get("ENTER_CD"));
	        		ifMap.put("orgChartNm", getIfList.get(i).get("ORG_CHART_NM"));
	        		ifMap.put("symd", getIfList.get(i).get("SYMD"));
	        		ifMap.put("eymd", getIfList.get(i).get("EYMD"));
	        		ifMap.put("note", getIfList.get(i).get("NOTE"));
	        		Long orgChartId = null;
	        		
	        		Map<String, Object> result = wtmInterfaceMapper.getWtmOgrChartId(ifMap);
        			if(result != null) {
        				try {
        					orgChartId = Long.parseLong(result.get("orgChartId").toString());
        					if(orgChartId != null) {
        						// 기존
	            				ifMap.put("orgChartId", orgChartId);
	            				resultCnt += wtmInterfaceMapper.updateWtmOrgChart(ifMap);
	            			} 
        				} catch(Exception e){
        					retMsg = "ORG_CHART_ID set : ORG_CHART_ID 조회오류";
        		            e.printStackTrace();
        		            // 에러걸리면 그냥 아웃시키기
        		            break;
        		        }
        			} else {
        				// 신규
        				resultCnt += wtmInterfaceMapper.insertWtmOrgChart(ifMap);
        				// 저장후 id 조회
        				Map<String, Object> resultId = wtmInterfaceMapper.getWtmOgrChartId(ifMap);
        				orgChartId = Long.parseLong(resultId.get("orgChartId").toString());
        				System.out.println("insert orgChartId : " + orgChartId);
        				ifMap.put("orgChartId", orgChartId);
        			}
        			
        			if(retMsg == null) {
	        			// chart detail 시작
	        			getIfDetList = null;
	        			try {
	        				String param = "?lastDataTime="+lastDataTime
	   			                 + "&enterCd="+getIfList.get(i).get("ENTER_CD")
	   			                 + "&symd="+getIfList.get(i).get("SYMD");
	        	        	String ifUrl = setIfUrl(tenantId, "/orgChartDet", param);
	        	        	getIfDetMap = getIfRt(ifUrl);
	        		   		
	        		   		if (getIfDetMap != null && getIfDetMap.size() > 0) {
	        		   			String ifMsg = getIfDetMap.get("message").toString();
	        		   			if(!"OK".equals(ifMsg)) {
	        		   				retMsg = "getIfDetMap get : " + ifMsg;
	        		   			} else {
	        		   				getIfDetList = (List<Map<String, Object>>) getIfDetMap.get("ifData");
	        		   			}
	        		   		} else {
	        		   			retMsg = "orgDet get : If 데이터 없음";
	        		   		}
	                	} catch(Exception e) {
	                		retMsg = "orgDet get : 서버통신 오류";
	                	}
	        			System.out.println("getIfDetList.size() : " + getIfDetList.size());
	        			if(retMsg == null && getIfDetList != null && getIfDetList.size() > 0) {
	        				System.out.println("orgchardet insert :c ");
	        				try {
	        					
	        					List<Map<String, Object>> ifList = new ArrayList();
	        	   	        	for(int j=0; j<getIfDetList.size(); j++) {
	        		        		Map<String, Object> ifDetMap = new HashMap<>();
	        		        		ifDetMap.put("orgChartId", orgChartId);
	        		        		ifDetMap.put("orgCd", getIfDetList.get(j).get("ORG_CD"));
	        		        		ifDetMap.put("priorOrgCd", getIfDetList.get(j).get("PRIOR_ORG_CD"));
	        		        		ifDetMap.put("seq", getIfDetList.get(j).get("SEQ"));
	        		        		ifDetMap.put("orgLevel", getIfDetList.get(j).get("ORG_LEVEL"));
	        		        		ifDetMap.put("orderSeq", getIfDetList.get(j).get("ORGER_SEQ"));
	        		        		ifList.add(ifDetMap);
	        	   	        	}
	        	   	        	resultCnt += wtmInterfaceMapper.insertWtmOrgChartDet(ifList);
			            	} catch(Exception e) {
			            		retMsg = "orgDet get : 서버통신 오류";
			            	}
	        			}
        			}
   	        	}
   	        	
        		try {
	        		if(resultCnt > 0) {
	        			retMsg = resultCnt + "건 반영완료";
	        		} else {
	        			retMsg = "갱신자료없음";
	        		}
	        		ifHisMap.put("ifStatus", "OK");
        		} catch (Exception e) {
        			ifHisMap.put("ifStatus", "ERR");
        			retMsg = e.getMessage();
		            e.printStackTrace();
    			}
	        	
	        } catch(Exception e){
	            e.printStackTrace();
	        }
		} else {
			retMsg = "갱신자료없음";
			ifHisMap.put("ifStatus", "OK");
		}
        // 3. 처리결과 저장
		try {
			// 최종갱신된 일시조회
			ifHisMap.put("updateDate", nowDataTime);
   			ifHisMap.put("ifEndDate", lastDataTime);
			// WTM_IF_HIS 테이블에 결과저장
			ifHisMap.put("ifMsg", retMsg);
			wtmInterfaceMapper.insertIfHis(ifHisMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}
		
	@Override
	@Transactional
	@Async("threadPoolTaskExecutor")
	public void getEmpHisIfResult(Long tenantId) throws Exception {
		// TODO Auto-generated method stub
		// 인터페이스 결과 저장용
    	String retMsg = null;
    	int resultCnt = 0;
    	String ifType = "V_IF_WTM_EMPHIS";
    	Map<String, Object> ifHisMap = new HashMap<>();
    	ifHisMap.put("tenantId", tenantId);
    	ifHisMap.put("ifItem", ifType);
    	
    	// 인터페이스용 변수
    	String lastDataTime = null;
    	String nowDataTime = null;
    	HashMap<String, Object> getDateMap = null;
    	HashMap<String, Object> getIfMap = null;
    	List<Map<String, Object>> getIfList = null;
    	
    	// 최종 자료 if 시간 조회
    	try {
    		getDateMap = (HashMap<String, Object>) getIfLastDate(tenantId, ifType);
    		lastDataTime = getDateMap.get("lastDate").toString();
    		nowDataTime = getDateMap.get("nowDate").toString();
        	try {
        		String param = "?lastDataTime="+lastDataTime;
	        	String ifUrl = setIfUrl(tenantId, "/empHis", param); 
	        	getIfMap = getIfRt(ifUrl);
		   		
		   		if (getIfMap != null && getIfMap.size() > 0) {
		   			String ifMsg = getIfMap.get("message").toString();
		   			getIfList = (List<Map<String, Object>>) getIfMap.get("ifData");
		   		} else {
		   			retMsg = "emp get : If 데이터 없음";
		   		}
        	} catch(Exception e) {
        		retMsg = "emp get : If 서버통신 오류";
        	}
    	} catch(Exception e) {
    		retMsg = "emp get : 최종갱신일 조회오류";
    	}
    	// 조회된 자료가 있으면...
    	if(retMsg == null && getIfList != null && getIfList.size() > 0) {
	        try {
	        	
   	        	List<Map<String, Object>> ifUpdateList = new ArrayList();
   	        	
   	        	for(int i=0; i<getIfList.size(); i++) {
   	        		System.out.println("************* i : " + i);
   	        		List<Map<String, Object>> ifList = new ArrayList();
	        		// 사원이력을 임시테이블로 이관 후 프로시저에서 이력을 정리한다.
	        		Map<String, Object> ifMap = new HashMap<>();
	        		ifMap.put("tenantId", tenantId);
	        		ifMap.put("enterCd", getIfList.get(i).get("ENTER_CD"));
	        		ifMap.put("sabun", getIfList.get(i).get("SABUN"));
	        		ifMap.put("empNm", getIfList.get(i).get("EMP_NM"));
	        		ifMap.put("empEngNm", getIfList.get(i).get("EMP_ENG_NM"));
	        		ifMap.put("symd", getIfList.get(i).get("SYMD"));
	        		ifMap.put("eymd", getIfList.get(i).get("EYMD"));
	        		ifMap.put("statusCd", getIfList.get(i).get("STATUS_CD"));
	        		ifMap.put("orgCd", getIfList.get(i).get("ORG_CD"));
	        		ifMap.put("businessPlaceCd", getIfList.get(i).get("LOCATION_CD"));
	        		ifMap.put("dutyCd", getIfList.get(i).get("DUTY_CD"));
	        		ifMap.put("posCd", getIfList.get(i).get("POS_CD"));
	        		ifMap.put("classCd", getIfList.get(i).get("CLASS_CD"));
	        		ifMap.put("jobGroupCd", getIfList.get(i).get("JOB_GROUP_CD"));
	        		ifMap.put("jobCd", getIfList.get(i).get("JOB_CD"));
	        		ifMap.put("payTypeCd", getIfList.get(i).get("PAY_TYPE_CD"));
	        		ifMap.put("leaderYn", getIfList.get(i).get("LEADER_YN"));
	        		ifMap.put("note", getIfList.get(i).get("NOTE"));
	        		
//	    			for ( String key : ifMap.keySet() ) {
//		    		    System.out.println("key : " + key +" / value : " + ifMap.get(key));
//		    		}
	    			
	        		ifList.add(ifMap);
	        		// 건별반영
	        		resultCnt += wtmInterfaceMapper.insertEmpHisTemp(ifList);
	        		
	        		if(resultCnt > 0) {
	        			retMsg = resultCnt + "건 반영완료";
	        		}
//	        		resultCnt += wtmInterfaceMapper.insertEmpHisTemp(ifMap);
//	        		retMsg = resultCnt + "건 반영완료";
	        	}
	        	
	        	try {
		        	// 추가건이 있으면
		    		//if (ifList.size() > 0) {
		    			//System.out.println("insert size : " + ifList.size());
		    			//resultCnt += wtmInterfaceMapper.insertEmpHisTemp(ifList);
	    			if(resultCnt > 0) {
	        			
	        			// temp 저장후 프로시저 호출
		    			HashMap<String, Object> setSpRetMap = new HashMap<>();
		    			setSpRetMap.put("tenantId", tenantId);
		    			setSpRetMap.put("nowDataTime", nowDataTime);
		    			setSpRetMap.put("retCode", "");
		    			wtmInterfaceMapper.setEmpHis(setSpRetMap);
		    			
		    			String retCode = setSpRetMap.get("retCode").toString();
		    			ifHisMap.put("ifStatus", retCode);
		    			if("ERR".equals(retCode)) {
		    				retMsg = "사원정보 이관갱신중 오류 오류로그 확인";
		    			}
		    			
	        		} else {
	        			retMsg = "갱신자료없음";
	        		}
		    		//}
	        	}catch(Exception e) {
	        		ifHisMap.put("ifStatus", "ERR");
	        		retMsg = "setEmpHis DATA 처리중 오류발생";
		            e.printStackTrace();
	        	}
	        }catch(Exception e){
	            e.printStackTrace();
	        }
    	} else {
			ifHisMap.put("ifStatus", "OK");
			retMsg = "갱신자료없음";
		}
    	// 3. 처리결과 저장
		try {
			// 최종갱신된 일시조회
			ifHisMap.put("updateDate", nowDataTime);
   			ifHisMap.put("ifEndDate", lastDataTime);
			// WTM_IF_HIS 테이블에 결과저장
			ifHisMap.put("ifMsg", retMsg);
			wtmInterfaceMapper.insertIfHis(ifHisMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if("OK".equals(ifHisMap.get("ifStatus"))) {
			// 4. 기본근무 생성대상자 조회해서 근무를 생성해주자
			try {
				System.out.println("base flexible emp add ");
				// 근무관리 제외차 체크
				/*
				List<Long> ruleIds = new ArrayList<Long>();
				Map<String, Object> ruleMap = null;
				Long targetRuleId =
				*/ 
				List<Map<String, Object>> getEmpBaseList = null;
				getEmpBaseList = wtmInterfaceMapper.getEmpBaseList(ifHisMap);
				if(getEmpBaseList != null && getEmpBaseList.size() > 0) {
					for(int i=0; i<getEmpBaseList.size(); i++) {
						Map<String, Object> setEmpMap = new HashMap<>();
						setEmpMap = getEmpBaseList.get(i);
						// 파라메터 체크 #{tenantId}, #{enterCd}, #{symd}, #{eymd}, #{sabun} , #{userId}
						System.out.println("tenantId : " + setEmpMap.get("tenantId").toString());
						System.out.println("enterCd : " + setEmpMap.get("enterCd").toString());
						System.out.println("symd : " + setEmpMap.get("symd").toString());
						System.out.println("eymd : " + setEmpMap.get("eymd").toString());
						System.out.println("sabun : " + setEmpMap.get("sabun").toString());
						System.out.println("userId : " + setEmpMap.get("userId").toString());
						setEmpMap.put("pId", setEmpMap.get("userId").toString());
						// 입사자만? 이력정리용 프로시저 호출하기
				    	wtmFlexibleEmpMapper.initWtmFlexibleEmpOfWtmWorkDayResult(setEmpMap);
						wtmFlexibleEmpMapper.createWorkTermBySabunAndSymdAndEymd(setEmpMap);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	    return;
	}
	
	@Override
	public void getOrgConcIfResult(Long tenantId) throws Exception {
		// TODO Auto-generated method stub
		// 인터페이스 결과 저장용
    	String retMsg = null;
    	int resultCnt = 0;
    	String ifType = "V_IF_WTM_ORG_CONC";
    	Map<String, Object> ifHisMap = new HashMap<>();
    	ifHisMap.put("tenantId", tenantId);
    	ifHisMap.put("ifItem", ifType);
    	
    	// 인터페이스용 변수
    	String lastDataTime = null;
    	String nowDataTime = null;
    	HashMap<String, Object> getDateMap = null;
    	HashMap<String, Object> getIfMap = null;
    	List<Map<String, Object>> getIfList = null;
    	
    	// 최종 자료 if 시간 조회
    	try {
    		getDateMap = (HashMap<String, Object>) getIfLastDate(tenantId, ifType);
    		lastDataTime = getDateMap.get("lastDate").toString();
    		nowDataTime = getDateMap.get("nowDate").toString();
        	try {
        		String param = "?lastDataTime="+lastDataTime;
	        	String ifUrl = setIfUrl(tenantId, "/orgConc", param); 
	        	getIfMap = getIfRt(ifUrl);
		   		
		   		if (getIfMap != null && getIfMap.size() > 0) {
		   			String ifMsg = getIfMap.get("message").toString();
		   			getIfList = (List<Map<String, Object>>) getIfMap.get("ifData");
		   		} else {
		   			retMsg = "orgConcurrent : If 데이터 없음";
		   			ifHisMap.put("ifStatus", "OK");
		   		}
        	} catch(Exception e) {
        		retMsg = "orgConcurrent get : If 서버통신 오류";
        		ifHisMap.put("ifStatus", "ERR");
        	}
    	} catch(Exception e) {
    		retMsg = "orgConcurrent get : 최종갱신일 조회오류";
    		ifHisMap.put("ifStatus", "ERR");
    	}
    	// 조회된 자료가 있으면...
    	if(retMsg == null && getIfList != null && getIfList.size() > 0) {
	        try {
   	        	List<Map<String, Object>> ifUpdateList = new ArrayList();
   	        	for(int i=0; i<getIfList.size(); i++) {
   	        		System.out.println("************* i : " + i);
	        		// 사원이력을 임시테이블로 이관 후 프로시저에서 이력을 정리한다.
	        		Map<String, Object> ifMap = new HashMap<>();
	        		ifMap.put("tenantId", tenantId);
	        		ifMap.put("enterCd", getIfList.get(i).get("ENTER_CD"));
	        		ifMap.put("orgCd", getIfList.get(i).get("ORG_CD"));
	        		ifMap.put("sabun", getIfList.get(i).get("SABUN"));
	        		ifMap.put("symd", getIfList.get(i).get("SYMD"));
	        		ifMap.put("eymd", getIfList.get(i).get("EYMD"));
	        		
	    			for ( String key : ifMap.keySet() ) {
		    		    System.out.println("key : " + key +" / value : " + ifMap.get(key));
		    		}
	    			Map<String, Object> ifRetMap = new HashMap<>();
	    			
	    			// 조직장은 WTM_EMP_HIS LEADER_YN값임으로 일단 스킵한다.
	        		// 원소속 조직장확인 및 겸직정보 확인
	    			ifRetMap = wtmInterfaceMapper.getOrgConcChk(ifMap);
	    			
	    			Integer leaderCnt = Integer.parseInt(ifRetMap.get("leaderCnt").toString());
	    			Integer concCnt = Integer.parseInt(ifRetMap.get("concCnt").toString());
	    			Integer concEndCnt = Integer.parseInt(ifRetMap.get("concEndCnt").toString());
	    			// 원소속 조직장이 아니고, 조직장이 등록안되어 있고, 조직장 종료가 아니면
	    			if(leaderCnt == 0 && concCnt == 0 && concEndCnt == 0) {
	    				// 겸직을 등록한다.
	    				wtmInterfaceMapper.insertOrgConc(ifMap);
	    			}
	    			// 원소속 조직장이 아니고, 조직장이 등록안되어 있고, 조직장 종료면
	    			if(leaderCnt == 0 && concCnt == 0 && concEndCnt == 1) {
	    				// 겸직 종료일을 수정한다
	    				wtmInterfaceMapper.updateOrgConcEnd(ifMap);
	    			}
	        	}
   	        	retMsg = getIfList.size() + "건반영";
   	        	ifHisMap.put("ifStatus", "OK");
   	        	
	        }catch(Exception e){
	        	ifHisMap.put("ifStatus", "ERR");
	            e.printStackTrace();
	        }
    	} else {
			ifHisMap.put("ifStatus", "OK");
			retMsg = "갱신자료없음";
		}
    	// 3. 처리결과 저장
		try {
			// 최종갱신된 일시조회
			ifHisMap.put("updateDate", nowDataTime);
   			ifHisMap.put("ifEndDate", lastDataTime);
			// WTM_IF_HIS 테이블에 결과저장
			ifHisMap.put("ifMsg", retMsg);
			wtmInterfaceMapper.insertIfHis(ifHisMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
/*
		if("OK".equals(ifHisMap.get("ifStatus"))) {
			// 조직장 권한 갱신이 필요함
			try {
				System.out.println("orgConcurrent add rule");
				// LEADERYN 이면서 겸직조직기준 권한 DISTINCT 해서 조직장 INSERT
				// 기준에 없는사람 DELETE
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
*/		
	    return;
	}
	
	@Override
	@Transactional
	@Async("threadPoolTaskExecutor")
	public void getEmpHisEtcIfResult(Long tenantId) throws Exception {
		// TODO Auto-generated method stub
		// 인터페이스 결과 저장용
    	String retMsg = null;
    	int resultCnt = 0;
    	String ifType = "V_IF_WTM_EMPHIS";
    	Map<String, Object> ifHisMap = new HashMap<>();
    	ifHisMap.put("tenantId", tenantId);
    	ifHisMap.put("ifItem", ifType);
    	
    	// 인터페이스용 변수
    	String lastDataTime = "20191129000000";
    	String nowDataTime = "20191129000000";

    	ifHisMap.put("updateDate", nowDataTime);
		ifHisMap.put("ifEndDate", lastDataTime);
    	HashMap<String, Object> getDateMap = null;
    	HashMap<String, Object> getIfMap = null;
    	List<Map<String, Object>> getIfList = null;
    	/* 대상자 다 돌렸음
        try {
        	// temp 저장후 프로시저 호출
			HashMap<String, Object> setSpRetMap = new HashMap<>();
			setSpRetMap.put("tenantId", tenantId);
			setSpRetMap.put("nowDataTime", nowDataTime);
			setSpRetMap.put("retCode", "");
			wtmInterfaceMapper.setEmpHis(setSpRetMap);
			
			String retCode = setSpRetMap.get("retCode").toString();
			ifHisMap.put("ifStatus", retCode);
			if("ERR".equals(retCode)) {
				retMsg = "사원정보 이관갱신중 오류 오류로그 확인";
			}
        }catch(Exception e){
            e.printStackTrace();
        }
        */
        // 4. 기본근무 생성대상자 조회해서 근무를 생성해주자
		try {
			System.out.println("base flexible emp add ");
			// 근무관리 제외차 체크
			/*
			List<Long> ruleIds = new ArrayList<Long>();
			Map<String, Object> ruleMap = null;
			Long targetRuleId =
			*/ 
			List<Map<String, Object>> getEmpBaseList = null;
			getEmpBaseList = wtmInterfaceMapper.getEmpBaseEtcList(ifHisMap);
			if(getEmpBaseList != null && getEmpBaseList.size() > 0) {
				for(int i=0; i<getEmpBaseList.size(); i++) {
					Map<String, Object> setEmpMap = new HashMap<>();
					setEmpMap = getEmpBaseList.get(i);
					// 파라메터 체크 #{tenantId}, #{enterCd}, #{symd}, #{eymd}, #{sabun} , #{userId}
					System.out.println("sabun : " + setEmpMap.get("sabun").toString());
					setEmpMap.put("pId", setEmpMap.get("userId").toString());
					// 입사자만? 이력정리용 프로시저 호출하기
			    	wtmFlexibleEmpMapper.initWtmFlexibleEmpOfWtmWorkDayResult(setEmpMap);
					wtmFlexibleEmpMapper.createWorkTermBySabunAndSymdAndEymd(setEmpMap);
				}
			}
			System.out.println("base flexible END");
		} catch (Exception e) {
			e.printStackTrace();
		}
	    return;
	}
	
	@Override
	public void getEmpAddrIfResult(Long tenantId) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("WtmInterfaceServiceImpl getEmpAddrIfResult");
		// 인터페이스 결과 저장용
    	String retMsg = null;
    	int resultCnt = 0;
    	String ifType = "V_IF_EMP_ADDR";
    	Map<String, Object> ifHisMap = new HashMap<>();
    	ifHisMap.put("tenantId", tenantId);
    	ifHisMap.put("ifItem", ifType);
    	
    	// 인터페이스용 변수
    	String lastDataTime = null;
    	String nowDataTime = null;
    	HashMap<String, Object> getDateMap = null;
    	HashMap<String, Object> getIfMap = null;
    	List<Map<String, Object>> getIfList = null;
    	
    	// 최종 자료 if 시간 조회
    	try {
    		getDateMap = (HashMap<String, Object>) getIfLastDate(tenantId, ifType);
    		lastDataTime = getDateMap.get("lastDate").toString();
    		nowDataTime = getDateMap.get("nowDate").toString();
        	try {
        		String param = "?lastDataTime="+lastDataTime;
	        	String ifUrl = setIfUrl(tenantId, "/empAddr", param);  
	        	getIfMap = getIfRt(ifUrl);
		   		
		   		if (getIfMap != null && getIfMap.size() > 0) {
		   			String ifMsg = getIfMap.get("message").toString();
		   			getIfList = (List<Map<String, Object>>) getIfMap.get("ifData");
		   		} else {
		   			retMsg = "empAddr get : If 데이터 없음";
		   		}
        	} catch(Exception e) {
        		retMsg = "empAddr get : 서버통신 오류";
        	}
    	} catch(Exception e) {
    		retMsg = "empAddr get : 최종갱신일 조회오류";
    	}
    	// 조회된 자료가 있으면...
		if(retMsg == null && getIfList != null && getIfList.size() > 0) {
			try {
	        	List<Map<String, Object>> ifList = new ArrayList();
   	        	for(int i=0; i<getIfList.size(); i++) {
	        		Map<String, Object> ifMap = new HashMap<>();
	        		ifMap.put("tenantId", tenantId);
	        		ifMap.put("enterCd", getIfList.get(i).get("ENTER_CD"));
	        		ifMap.put("sabun", getIfList.get(i).get("SABUN"));
	        		ifMap.put("email", getIfList.get(i).get("EMAIL"));
	        		ifMap.put("handPhone", getIfList.get(i).get("HAND_PHONE"));
	        		ifMap.put("note", getIfList.get(i).get("NOTE"));
	        		ifList.add(ifMap);
   	        	}
   	        	if (ifList.size() > 0) {
		        	// 2. data 저장
		    		try {
		    			// key값을 기준으로 mergeinto하자
						resultCnt = wtmInterfaceMapper.insertWtmEmpAddr(ifList);
						retMsg = resultCnt + "건 반영완료";
						ifHisMap.put("ifStatus", "OK");
					} catch (Exception e) {
						ifHisMap.put("ifStatus", "ERR");
						retMsg = "Holiday set : 데이터 저장 오류";
						e.printStackTrace();
					}
	        	} else {
	        		retMsg = "갱신자료없음";
					ifHisMap.put("ifStatus", "OK");
	        	}
        	
			} catch (Exception e) {
    			ifHisMap.put("ifStatus", "ERR");
    			retMsg = e.getMessage();
	            e.printStackTrace();
			}
		} else {
			retMsg = "갱신자료없음";
			ifHisMap.put("ifStatus", "OK");
		}
    	// 3. 처리결과 저장
		try {
			// WTM_IF_HIS 테이블에 결과저장
			ifHisMap.put("updateDate", nowDataTime);
   			ifHisMap.put("ifEndDate", lastDataTime);
			ifHisMap.put("ifMsg", retMsg);
			wtmInterfaceMapper.insertIfHis(ifHisMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
        System.out.println("WtmInterfaceServiceImpl getEmpAddrIfResult end");
		return;
	}
	
	@Override
	public ReturnParam setTaaApplIf(HashMap reqMap) throws Exception {
		ReturnParam rp = new ReturnParam();
		// TODO Auto-generated method stub
		System.out.println("WtmInterfaceServiceImpl setTaaApplIf");
		// 인터페이스 결과 저장용
    	String retMsg = null;
    	int resultCnt = 0;
    	String ifType = "TAA_APPL";
    	Map<String, Object> ifHisMap = new HashMap<>();
    	ifHisMap.put("tenantId", reqMap.get("tenantId"));
    	ifHisMap.put("ifItem", ifType);
    	
    	// 인터페이스용 변수
    	String lastDataTime = null;
    	String nowDataTime = null;
    	HashMap<String, Object> getDateMap = null;
    	HashMap<String, Object> getIfMap = null;
    	List<Map<String, Object>> getIfList = null;
    	
    	// 최종 자료 if 시간 조회
    	try {
    		getDateMap = (HashMap<String, Object>) getIfLastDate((Long) reqMap.get("tenantId"), ifType);
    		lastDataTime = getDateMap.get("nowDate").toString();
    		nowDataTime = getDateMap.get("nowDate").toString();
        	
    	} catch(Exception e) {
    		retMsg = "TAA_RESULT get : 최종갱신일 조회오류";
    		rp.setFail(retMsg);
    		return rp;
    	}
    	
		// 2. 인터페이스 data 처리
		try {
			//String applStatusCd = reqMap.get("status").toString();
			// 호출이되면 근태신청 데이터를 저장하거나 결재상태를 갱신한다
			reqMap.put("retCode", "");
			reqMap.put("retMsg", "");
			reqMap.put("taaApplId", "");
			reqMap.put("applId", "");
			reqMap.put("oldStatus", "");
			wtmInterfaceMapper.setTaaApplIf(reqMap);
			
			String retCode = reqMap.get("retCode").toString();
			System.out.println("CALL setTaaApplIf retCode : " + retCode);
			
			if("FAIL".equals(retCode)) {
				retMsg = reqMap.get("retMsg").toString();
				rp.setFail(retMsg);
	    		return rp;
			}
			
			String oldStatusCd = "";
			if(reqMap.get("oldStatus") != null) { oldStatusCd = reqMap.get("oldStatus").toString();}
			
			
			if("OK".equals(retCode)) {
				//기간 루프
				String sYmd = reqMap.get("sYmd").toString();
				String eYmd = reqMap.get("eYmd").toString();
				
				SimpleDateFormat dt = new SimpleDateFormat("yyyyMMddHHmmss");
				
				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		        Date sDate = formatter.parse(sYmd);
		        Date eDate = formatter.parse(eYmd);
		         
		     // 시간차이를 시간,분,초를 곱한 값으로 나누면 하루 단위가 나옴
		        long diff = eDate.getTime() - sDate.getTime();
		        long diffDays = (diff / (24 * 60 * 60 * 1000)) +1;
				for(int i=0; i<diffDays; i++) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(sDate);
					cal.add(Calendar.DATE, i);
					String ymd = formatter.format(cal.getTime());
					System.out.println("loop ymd : " + ymd);
					Map<String, Object> reqDayMap = reqMap;
					reqDayMap.put("ymd", ymd);
					reqDayMap.put("oldStatus", oldStatusCd);
					reqDayMap.put("retCode", "");
					reqDayMap.put("retMsg", "");
					reqDayMap.put("timeTypeCd", "");
					reqDayMap.put("taaSetYn", "");
					reqDayMap.put("taaSdate", "");
					reqDayMap.put("taaEdate", "");
					System.out.println("SAVE BEFOR : " + reqDayMap.toString());
					//System.out.println("oldStatusCd : " + oldStatusCd);
					//for ( String key : reqDayMap.keySet() ) {
	        		//    System.out.println("key : " + key +" / value : " + reqDayMap.get(key));
	        		//}
					//System.out.println("statusCd : " + reqDayMap.get("status"));
					wtmInterfaceMapper.setTaaApplDayIf(reqDayMap);
					System.out.println("SAVE AFTER : " + reqDayMap.toString());
					String retDayCode = reqDayMap.get("retCode").toString();
					// System.out.println("retMsg : " +  reqDayMap.get("retMsg").toString());
					if("FAIL".equals(retCode)) {
						// 오류다 ㅠㅠ
						ifHisMap.put("ifStatus", "ERR");
						retMsg = "근태정보 이관중 오류. 오류로그 확인";
						System.err.println("**TaaAppl reqDayErr " + reqDayMap.get("sabun").toString() + "/" + reqDayMap.get("sYmd").toString() + "~" + reqDayMap.get("eYmd").toString() + reqDayMap.get("retCode").toString());
						rp.setFail(retMsg);
			    		return rp;
					} else {
						// 오류가 아니면.. 근태시간을 생성체크하자
						String taaSetYn = reqDayMap.get("taaSetYn").toString();
						if("I".equals(taaSetYn)) {
							// 근태생성
							wtmFlexibleEmpService.addWtmDayResultInBaseTimeType(
									  Long.parseLong(reqDayMap.get("tenantId").toString())
									, reqDayMap.get("enterCd").toString()
									, ymd
									, reqDayMap.get("sabun").toString()
									, reqDayMap.get("timeTypeCd").toString()
									, reqDayMap.get("taaCd").toString()
									, dt.parse(reqDayMap.get("taaSdate").toString())
									, dt.parse(reqDayMap.get("taaEdate").toString())
									, Long.parseLong(reqDayMap.get("applId").toString())
									, "0");
						} else if ("D".equals(taaSetYn)) {
							// 근태삭제
							wtmFlexibleEmpService.removeWtmDayResultInBaseTimeType(
									  Long.parseLong(reqDayMap.get("tenantId").toString())
									, reqDayMap.get("enterCd").toString()
									, ymd
									, reqDayMap.get("sabun").toString()
									, reqDayMap.get("timeTypeCd").toString()
									, reqDayMap.get("taaCd").toString()
									, dt.parse(reqDayMap.get("taaSdate").toString())
									, dt.parse(reqDayMap.get("taaEdate").toString())
									, Long.parseLong(reqDayMap.get("applId").toString())
									, "0");
						}
						
						
						String chkYmd = nowDataTime.substring(0, 8);
						Long tenantId = Long.parseLong(reqDayMap.get("tenantId").toString());
						String enterCd = reqDayMap.get("enterCd").toString();
		        		String sabun = reqDayMap.get("sabun").toString();
		        		
		        		// 오늘 이전이면 근무마감을 다시 돌려야함.
						if (Integer.parseInt(chkYmd) > Integer.parseInt(ymd) && ("D".equals(taaSetYn) || "I".equals(taaSetYn))) {
							wtmFlexibleEmpService.calcApprDayInfo(tenantId, enterCd, ymd, ymd, sabun);
						}
						// 근무시간합산은 재정산한다
		        		HashMap<String, Object> setTermMap = new HashMap();
		        		setTermMap.put("tenantId", tenantId);
		        		setTermMap.put("enterCd", enterCd);
		        		setTermMap.put("sabun", sabun);
		        		setTermMap.put("symd", ymd);
		        		setTermMap.put("eymd", ymd);
		        		setTermMap.put("pId", "TAAIF");
		        		wtmFlexibleEmpMapper.createWorkTermBySabunAndSymdAndEymd(setTermMap);
					}
				}
				
				ifHisMap.put("ifStatus", "OK");
				retMsg = "근태신청서 처리완료";
			} else if("END".equals(retCode)) {
				ifHisMap.put("ifStatus", "OK");
				retMsg = reqMap.get("retMsg").toString();
			} else {
				//ifHisMap.put("ifStatus", "ERR");
				//retMsg = "프로시저 생성누락은 사유가 있어서 그래 무시해야함";
				System.err.println("**TaaAppl reqErr " + reqMap.get("sabun").toString() + "/" + reqMap.get("sYmd").toString() + "~" + reqMap.get("eYmd").toString() + reqMap.get("retCode").toString() + "/"+ reqMap.get("retMsg").toString());
				ifHisMap.put("ifStatus", "OK");
			}
		} catch(Exception e){
			retMsg = "TAA_RESULT set : 근태 이관오류";
            e.printStackTrace();
            
            rp.setFail(retMsg);
    		return rp;
        }
		
    	// 3. 처리결과 저장
		try {
			// WTM_IF_HIS 테이블에 결과저장
			ifHisMap.put("updateDate", nowDataTime);
   			ifHisMap.put("ifEndDate", lastDataTime);
			ifHisMap.put("ifMsg", retMsg);
			wtmInterfaceMapper.insertIfHis(ifHisMap);
			
			rp.setSuccess(retMsg);
			
		} catch (Exception e) {
			e.printStackTrace();
            rp.setFail(retMsg);
    		return rp;
		}
        System.out.println("WtmInterfaceServiceImpl setTaaApplIf end");
		return rp;
	}
	
	
	@Override
	public void setTaaApplParam(HashMap reqMap) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("WtmInterfaceServiceImpl setTaaApplParam");
		// 인터페이스 결과 저장용
    	String retMsg = null;
    	int resultCnt = 0;
    	String ifType = "TAA_APPL";
    	Map<String, Object> ifHisMap = new HashMap<>();
    	ifHisMap.put("tenantId", reqMap.get("tenantId"));
    	ifHisMap.put("ifItem", ifType);
    	
    	// 인터페이스용 변수
    	HashMap<String, Object> getDateMap = null;
    	HashMap<String, Object> getIfMap = null;
    	List<Map<String, Object>> getIfList = null;
    	
		// 2. 인터페이스 data 처리
		try {
			//String applStatusCd = reqMap.get("status").toString();
			// 호출이되면 근태신청 데이터를 저장하거나 결재상태를 갱신한다
			getIfList = wtmInterfaceMapper.getTaaApplList(reqMap);
			
			if(getIfList != null || getIfList.size() > 0) {
				for(Map<String, Object> taaMap : getIfList) {
					System.out.println("taaMap : " + taaMap.toString());
					//기간 루프
					String sYmd = taaMap.get("sYmd").toString();
					String eYmd = taaMap.get("eYmd").toString();
					
					SimpleDateFormat dt = new SimpleDateFormat("yyyyMMddHHmmss");
					
					SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			        Date sDate = formatter.parse(sYmd);
			        Date eDate = formatter.parse(eYmd);
			         
			        // 시간차이를 시간,분,초를 곱한 값으로 나누면 하루 단위가 나옴
			        long diff = eDate.getTime() - sDate.getTime();
			        long diffDays = (diff / (24 * 60 * 60 * 1000)) +1;
					for(int i=0; i<diffDays; i++) {
						Calendar cal = Calendar.getInstance();
						cal.setTime(sDate);
						cal.add(Calendar.DATE, i);
						String ymd = formatter.format(cal.getTime());
						HashMap<String, Object> reqDayMap = (HashMap<String, Object>) taaMap;
						reqDayMap.put("ymd", ymd);
						reqDayMap.put("retCode", "");
						reqDayMap.put("retMsg", "");
						reqDayMap.put("timeTypeCd", "");
						reqDayMap.put("taaSetYn", "");
						reqDayMap.put("taaSdate", "");
						reqDayMap.put("taaEdate", "");
						System.out.println("reqDayMap : " + reqDayMap.toString());
						wtmInterfaceMapper.setTaaApplDayIf(reqDayMap);
						String retDayCode = reqDayMap.get("retCode").toString();
						if("FAIL".equals(retDayCode)) {
							// 오류다 ㅠㅠ
							ifHisMap.put("ifStatus", "ERR");
							retMsg = "근태정보 이관중 오류. 오류로그 확인";
							break;
						} else {
							// 오류가 아니면.. 근태시간을 생성체크하자
							String taaSetYn = reqDayMap.get("taaSetYn").toString();
							System.out.println("taaSetYn : " + taaSetYn);
							if("I".equals(taaSetYn)) {
								// 근태생성
								wtmFlexibleEmpService.addWtmDayResultInBaseTimeType(
										  Long.parseLong(reqDayMap.get("tenantId").toString())
										, reqDayMap.get("enterCd").toString()
										, ymd
										, reqDayMap.get("sabun").toString()
										, reqDayMap.get("timeTypeCd").toString()
										, reqDayMap.get("taaCd").toString()
										, dt.parse(reqDayMap.get("taaSdate").toString())
										, dt.parse(reqDayMap.get("taaEdate").toString())
										, Long.parseLong(reqDayMap.get("applId").toString())
										, "0");
								// timeTypeCd = "REGA" & ymd <= 오늘 그러면 타각갱신해야함.
							} else if ("D".equals(taaSetYn)) {
								// 근태삭제
								wtmFlexibleEmpService.removeWtmDayResultInBaseTimeType(
										  Long.parseLong(reqDayMap.get("tenantId").toString())
										, reqDayMap.get("enterCd").toString()
										, ymd
										, reqDayMap.get("sabun").toString()
										, reqDayMap.get("timeTypeCd").toString()
										, reqDayMap.get("taaCd").toString()
										, dt.parse(reqDayMap.get("taaSdate").toString())
										, dt.parse(reqDayMap.get("taaEdate").toString())
										, Long.parseLong(reqDayMap.get("applId").toString())
										, "0");
							}
							
							// String chkYmd = "20200211";
							String chkYmd = WtmUtil.parseDateStr(new Date(), null);
							String enterCd = reqDayMap.get("enterCd").toString();
			        		String sabun = reqDayMap.get("sabun").toString();
			        		
			        		// 오늘 이전이면 근무마감을 다시 돌려야함.
							if (Integer.parseInt(chkYmd) > Integer.parseInt(ymd) && ("D".equals(taaSetYn) || "I".equals(taaSetYn))) {
								wtmFlexibleEmpService.calcApprDayInfo(Long.parseLong(reqMap.get("tenantId").toString()), enterCd, ymd, ymd, sabun);
							}
							// 근무시간합산은 재정산한다
			        		HashMap<String, Object> setTermMap = new HashMap();
			        		setTermMap.put("tenantId", reqMap.get("tenantId"));
			        		setTermMap.put("enterCd", enterCd);
			        		setTermMap.put("sabun", sabun);
			        		setTermMap.put("symd", ymd);
			        		setTermMap.put("eymd", ymd);
			        		setTermMap.put("pId", "TAAIF");
			        		wtmFlexibleEmpMapper.createWorkTermBySabunAndSymdAndEymd(setTermMap);
						}
					}
					
					ifHisMap.put("ifStatus", "OK");
					retMsg = "근태신청서 처리완료";
				}
			}
		} catch(Exception e){
			retMsg = "TAA_RESULT set : 근태 이관오류";
            e.printStackTrace();
        }
		
        System.out.println("WtmInterfaceServiceImpl setTaaApplIf end");
		return;
	}
	
	@Override
	@Transactional
	public Map<String,Object> setTaaApplArrIf(Map reqMap) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("WtmInterfaceServiceImpl setTaaApplArrIf");
		// 인터페이스 결과 저장용
    	String retMsg = "";
    	String status = "OK";
    	
    	
    	int taaCnt = 0;
    	Map<String, Object> retMap = new HashMap<>();
    	
    	// 인터페이스용 변수
    	Long tenantId = null;
    	Long applId = null;
    	Long taaApplId = null;
    	String oldApplStatusCd = "";
    	String nowApplStatusCd = "";
    	WtmAppl appl = new WtmAppl();
    	
    	HashMap<String, Object> getResultMap = null;
    	List<Map<String, Object>> setWorksList = null;
    	List<Map<String, Object>> setWorksDetList = null;
    	HashMap<String, Object> getCheckMap = null;
	    	
    	
    	HashMap<String, Object> getIfMap = null;
    	List<Map<String, Object>> getIfList = null;
    	
		// 1. 인터페이스 data 처리
		
			// 1.1. 필수데이터 체크
	    	if(reqMap == null) {
	    		status = "ERR";
				retMsg = "인터페이스 정보없음";
				throw new Exception(retMsg);
	    		
	    	} else {
	    		if(!reqMap.containsKey("apiKey") && reqMap.get("apiKey").equals("")) {
					retMsg = "인터페이스 pearbranch API 사용 키 정보누락";
					throw new RuntimeException(retMsg);
	    		}
	    		// 테넌트정보 조회
				String apiKey = null;
				apiKey = reqMap.get("apiKey").toString();
				CommTenantModule tm = null;
			    tm = tenantModuleRepo.findByApiKey(apiKey);
			    tenantId = tm.getTenantId();

		        reqMap.put("tenantId", tenantId);
		        
		        if(tenantId == null) {
		        	retMsg = "인터페이스 pearbranch API 사용 키 정보가 유효하지 않습니다";
					throw new RuntimeException(retMsg);
		        }
		        
	    		if(!reqMap.containsKey("secret") && reqMap.get("secret").equals("")) {
					retMsg = "인터페이스 비밀번호 정보누락";
					throw new RuntimeException(retMsg);
	    		}
	    		if(!reqMap.containsKey("enterCd") && reqMap.get("enterCd").equals("")) {
					retMsg = "인터페이스 회사정보 정보누락";
					throw new RuntimeException(retMsg);
	    		}
	    		if(!reqMap.containsKey("applSabun") && reqMap.get("applSabun").equals("")) {
					retMsg = "인터페이스 신청자 정보누락";
					throw new RuntimeException(retMsg);
	    		}
	    		if(!reqMap.containsKey("works") && reqMap.get("works").equals("")) {
					retMsg = "인터페이스 근무정보 정보누락";
					throw new RuntimeException(retMsg);
	    		}
	    		if(!reqMap.containsKey("applNo") && reqMap.get("applNo").equals("")) {
					retMsg = "인터페이스 신청서 구분key 정보누락";
					throw new RuntimeException(retMsg);
	    		}
	    		if(!reqMap.containsKey("status") && reqMap.get("status").equals("")) {
					retMsg = "인터페이스 결재상태 정보누락";
					throw new RuntimeException(retMsg);
	    		}
			}
	    	
	    	nowApplStatusCd = reqMap.get("status").toString();
	    	
	    	if(!nowApplStatusCd.equals(WtmApplService.APPL_STATUS_CANCEL)) {
	    		ReturnParam rp = new ReturnParam();
	    		List<Map<String, Object>> works = (List<Map<String, Object>>)reqMap.get("works");
	    		
	    		rp = validatorService.worktimeValid(tenantId, reqMap.get("enterCd").toString(), reqMap.get("applNo").toString(), works, reqMap.get("applSabun").toString());
	    		//ObjectMapper mm = new ObjectMapper();
	    		logger.debug(rp.getStatus() + " : " + rp.get("message"));
	    		if(rp!=null && rp.getStatus()!=null && !"OK".equals(rp.getStatus())) {
					throw new RuntimeException((String) rp.get("message"));
	    		}
	    		
	    	}
	    	
			// 2. DATA 생성 또는 갱신
			getResultMap = (HashMap<String, Object>) wtmInterfaceMapper.getApplId(reqMap);
			if(getResultMap != null && getResultMap.size() > 0) {
				// 2.1. 이미 등록된 결재건 있음.
				applId = Long.parseLong(getResultMap.get("applId").toString());
				oldApplStatusCd = getResultMap.get("oldApplStatusCd").toString();
				if(!oldApplStatusCd.equals(nowApplStatusCd)) {
					// 2.1.1. 과거상태과 현재상태가 다르면 갱신해야함.
					appl.setApplId(applId);
					appl.setTenantId((Long) reqMap.get("tenantId"));
					appl.setEnterCd(reqMap.get("enterCd").toString());
					appl.setApplCd("TAA");
					appl.setApplSabun(reqMap.get("applSabun").toString());
					appl.setApplInSabun(reqMap.get("applSabun").toString());
					appl.setIfApplNo(reqMap.get("applNo").toString());
					appl.setApplStatusCd(nowApplStatusCd);
					appl.setApplYmd(WtmUtil.parseDateStr(new Date(), null));
					appl.setUpdateId("TAAIF");
					wtmApplRepo.save(appl);
				}
			} else {
				// 2.2. 결재정보가 없음. data를 생성하자
				// 2.2.1. WTM_APPL 생성
				appl.setTenantId((Long) reqMap.get("tenantId"));
				appl.setEnterCd(reqMap.get("enterCd").toString());
				appl.setApplCd("TAA");
				appl.setApplSabun(reqMap.get("applSabun").toString());
				appl.setApplInSabun(reqMap.get("applSabun").toString());
				appl.setIfApplNo(reqMap.get("applNo").toString());
				appl.setApplStatusCd(reqMap.get("status").toString());
				appl.setApplYmd(WtmUtil.parseDateStr(new Date(), null));
				appl.setUpdateId("TAAIF");	
				wtmApplRepo.save(appl);
				applId = appl.getApplId();
				
				setWorksList = (List<Map<String, Object>>) reqMap.get("works");
				// 2.2.2. 대상자정보 루프시작
				for(Map<String, Object> empMap : setWorksList) {
					if(empMap == null || !empMap.containsKey("sabun") && empMap.get("sabun").equals("")) {
						retMsg = "인터페이스  대상자 정보누락";
						throw new RuntimeException(retMsg);
					}
					if(empMap == null || !empMap.containsKey("worksDet") && empMap.get("worksDet").equals("")) {
						retMsg = "인터페이스 대상자 근태정보 정보누락";
						throw new RuntimeException(retMsg);
					}
					
					// 2.2.2.1. 대상자가 근무관리 대상여부 체크
					
					// 2.2.2.2. 근무관리 대상이면 taaCnt 증가하고 WTM_TAA_APPL 생성
					WtmTaaAppl taaAppl = new WtmTaaAppl();  
					taaAppl.setTenantId((Long) reqMap.get("tenantId"));
					taaAppl.setEnterCd(reqMap.get("enterCd").toString());
					taaAppl.setApplId(applId);
					taaAppl.setSabun(empMap.get("sabun").toString());
					taaAppl.setIfApplNo(reqMap.get("applNo").toString());
					taaAppl.setUpdateId("TAAIF");
					wtmTaaApplRepo.save(taaAppl);
					taaApplId = taaAppl.getTaaApplId();

					// 2.2.2.3. 근무기간 루프시작
					setWorksDetList = (List<Map<String, Object>>) empMap.get("worksDet");
					for(Map<String, Object> empDetMap : setWorksDetList) {
						// 2.2.2.3.1. 근무기간 루프시작
						if(empDetMap == null || !empDetMap.containsKey("workTimeCode") && empDetMap.get("workTimeCode").equals("")) {
							retMsg = "인터페이스  대상자 근태코드 정보누락";
							throw new RuntimeException(retMsg);
						}
						if(empDetMap == null || !empDetMap.containsKey("startYmd") && empDetMap.get("startYmd").equals("")) {
							retMsg = "인터페이스  대상자 근태시작일 정보누락";
							throw new RuntimeException(retMsg);
						}
						if(empDetMap == null || !empDetMap.containsKey("endYmd") && empDetMap.get("endYmd").equals("")) {
							retMsg = "인터페이스  대상자 근태종료일 정보누락";
							throw new RuntimeException(retMsg);
						}
						// 3.1. 기간별체크, 마감여부 체크
						getCheckMap = (HashMap<String, Object>) wtmInterfaceMapper.getCloseYnChk(reqMap);
						if(getCheckMap != null) {
							if(Integer.parseInt(getCheckMap.get("dayCnt").toString()) <= 0) {
								retMsg = "근태 신청기간에 오류가 있습니다.";
								throw new RuntimeException(retMsg);
							}
							if(Integer.parseInt(getCheckMap.get("closeCnt").toString()) > 0) {
								retMsg = "근태 신청 기간이 근태마감되었습니다. 담당자에게 문의하세요.";
								throw new RuntimeException(retMsg);
							}
						}
						
						// 3.2. WTM_TAA_APPL_DET 생성(근무상세_worksDet 루프)
						WtmTaaApplDet taaApplDet = new WtmTaaApplDet();
//						System.out.println("taaApplId : " + taaApplId);
						taaApplDet.setTaaApplId(taaApplId);
						taaApplDet.setTaaCd(empDetMap.get("workTimeCode").toString());
						taaApplDet.setSymd(empDetMap.get("startYmd").toString());
						taaApplDet.setEymd(empDetMap.get("endYmd").toString());
						taaApplDet.setShm(empDetMap.get("startHm").toString());
						taaApplDet.setEhm(empDetMap.get("endHm").toString());
						taaApplDet.setUpdateId("TAAIF");
						wtmTaaApplDetRepo.save(taaApplDet);
					}
				}
			}
			// 3.근태저장이 정상으로 종료됨. 근태 갱신여부를 따져보자
			if(("".equals(oldApplStatusCd) && "99".equals(nowApplStatusCd) 												// 최초이관인데 승인완료인경우
				|| !"99".equals(oldApplStatusCd) && "99".equals(nowApplStatusCd) 										// 이전 상태는 승인완료가 아니고 현재상태는 승인완료일때
				|| "99".equals(oldApplStatusCd) && ("22".equals(nowApplStatusCd) || "44".equals(nowApplStatusCd)))		// 이전 상태가 승인완료였다가 현재상태가 반려 또는 취소인경우
			  ) {
				// 3.1 변경정보가 있으니 근무를 갱신
				reqMap.put("applId", applId);
				List<Map<String, Object>>  getTaaList = null;
				getTaaList = wtmInterfaceMapper.getTaaList(reqMap); // 저장한 데이터를 읽어온다
				if(getTaaList == null || getTaaList.size() <= 0) {
					retMsg = "근무기록 대상자가 아닙니다.";
					throw new RuntimeException(retMsg);
				}
				for(Map<String, Object> taaDetMap : getTaaList) {	
					taaDetMap.put("tenantId", tenantId);
					taaDetMap.put("applId", applId);
					// 4.1 근태 코드별 기준확인
					System.out.println("tenantId :: " + tenantId);
					System.out.println("enterCd ::  " + reqMap.get("enterCd").toString());
					System.out.println("taaCd :: " + taaDetMap.get("taaCd").toString() );
					WtmTaaCode taaCode = wtmTaaCodeRepo.findByTenantIdAndEnterCdAndTaaCd(tenantId, reqMap.get("enterCd").toString(), taaDetMap.get("taaCd").toString());
					if(taaCode == null) {
			        	retMsg = "근태코드가 없습니다. 담당자에게 문의 하세요.";
						throw new RuntimeException(retMsg);
					}
					if("N".equals(taaCode.getRequestTypeCd())) {
						retMsg = "근태신청이 불가능한 근태코드입니다.";
						throw new RuntimeException(retMsg);
					}
					// 4.2. 근무옵션 확인
					SimpleDateFormat dt = new SimpleDateFormat("yyyyMMddHHmmss");
					
					Map<String, Object>  getStdMgrMap = null;
					getStdMgrMap = wtmInterfaceMapper.getStdMgrList(taaDetMap);
					String timeTypeCd = "TAA";
					if("Y".equals(taaCode.getWorkYn())) {
						timeTypeCd = "REGA";
					}
					System.out.println("timeTypeCd : " + timeTypeCd);
					if(getStdMgrMap != null && getStdMgrMap.containsKey("unplannedYn") && "Y".equals(getStdMgrMap.get("unplannedYn").toString())) {
						System.out.println("unplannedYn : Y start");
						// 4.2.1. 근무계획없음 체크일때 RESULT 갱신은 없음.
						// result 생성해야함
						if("99".equals(nowApplStatusCd)) {
							WtmWorkDayResult dayResult = new WtmWorkDayResult();
							Integer workMinute = 0;
							if(taaCode.getWorkApprHour()!=null) {
								workMinute = Integer.parseInt(taaCode.getWorkApprHour().toString()) * 60;
							}
							if(!"0".equals(taaDetMap.get("workMinute").toString())) {
								workMinute = Integer.parseInt(taaDetMap.get("workMinute").toString());
							}
							dayResult.setTenantId(tenantId);
							dayResult.setEnterCd(taaDetMap.get("enterCd").toString());
							dayResult.setYmd(taaDetMap.get("ymd").toString());
							dayResult.setSabun(taaDetMap.get("sabun").toString());
							dayResult.setApplId(applId);
							dayResult.setTimeTypeCd(timeTypeCd);
							dayResult.setTaaCd(taaDetMap.get("taaCd").toString());
							dayResult.setPlanMinute(workMinute);
							dayResult.setApprMinute(workMinute);
							dayResult.setUpdateId("TAAIF");
							dayResultRepo.save(dayResult);
						} else {
							// delete는 키를 못찾으니깐 쿼리문으로
							wtmInterfaceMapper.deleteResult(taaDetMap);
						}
					} else {
						System.out.println("unplannedYn : N start");
						// 4.2.2 근무계획이 무조건 있어야 함
						// 근태기준이 휴일포함이거나, 휴일포함아니면 근무일일때만 data 생성
						if("Y".equals(taaCode.getHolInclYn()) || ("N".equals(taaCode.getHolInclYn()) && "N".equals(taaDetMap.get("holidayYn")))) {
							if("D".equals(taaCode.getRequestTypeCd()) && "Y".equals(taaCode.getHolInclYn()) && "Y".equals(taaDetMap.get("holidayYn"))) {
								// 휴일포함이면서 휴일이면서 종일근무이면
								WtmWorkDayResult dayResult = new WtmWorkDayResult();
								dayResult.setTenantId(tenantId);
								dayResult.setEnterCd(taaDetMap.get("enterCd").toString());
								dayResult.setYmd(taaDetMap.get("ymd").toString());
								dayResult.setSabun(taaDetMap.get("sabun").toString());
								dayResult.setApplId(applId);
								dayResult.setTimeTypeCd(timeTypeCd);
								dayResult.setTaaCd(taaDetMap.get("taaCd").toString());
								dayResult.setUpdateId("TAAIF");
								dayResultRepo.save(dayResult);
							} else {
								String taaSdate = getStdMgrMap.get("taaSdate").toString();
								String taaEdate = getStdMgrMap.get("taaEdate").toString();
								
								
								if(!"0".equals(taaDetMap.get("workMinute").toString())) {
									// 근무시간이 왔으면....신청서 근무시간대로 입력해줌
									taaSdate = taaDetMap.get("taaSdate").toString();
									taaEdate = taaDetMap.get("taaEdate").toString();
								} else {
									// 근무시간이 없으면 근태코드별 시간을 조정해야함.
									if("A".equals(taaCode.getRequestTypeCd())){
										// 반차는 근무시간을 변경함
										Map<String, Object>  setTimeMap = null;
										setTimeMap.put("taaSdate",taaSdate);
										setTimeMap.put("taaEdate",taaEdate);
										setTimeMap.put("reqTypeCd",taaCode.getRequestTypeCd());
										Map<String, Object>  getTimeMap = null;
										getTimeMap = wtmInterfaceMapper.getTaaPlanTimeList(setTimeMap);
										if(getTimeMap == null || getTimeMap.size() == 0) {
											retMap.put("status", "ERR");
								        	retMap.put("retMsg", "근태 시간계산중 오류가 발생하였습니다.");
								        	retMsg = "근태 시간계산중 오류가 발생하였습니다.";
											throw new Exception(retMsg);
										}
										taaSdate = getTimeMap.get("taaSdate").toString();
										taaEdate = getTimeMap.get("taaEdate").toString();
									} else if("P".equals(taaCode.getRequestTypeCd())){	
										Map<String, Object>  getTimeMap = new HashMap();
										getTimeMap.put("tenantId", tenantId);
										getTimeMap.put("enterCd", taaDetMap.get("enterCd").toString());
										getTimeMap.put("sabun", taaDetMap.get("sabun").toString());
										getTimeMap.put("ymd", taaDetMap.get("ymd").toString());
										getTimeMap.put("sDate", taaSdate);
										getTimeMap.put("addMinute", 240);
										getTimeMap.put("retDate", "");
										// System.out.println("**** getTimeMap param : " + getTimeMap);
										Map<String, Object>  setTimeMap = new HashMap();
										setTimeMap = wtmFlexibleEmpMapper.addMinuteWithBreakMGR(getTimeMap);
										String retDate = getTimeMap.get("retDate").toString();
										
										if("".equals(retDate) || retDate.length() != 14) {
											System.out.println("**** getTimeMap callend : " + getTimeMap);
											retMap.put("status", "ERR");
								        	retMap.put("retMsg", "근태 시간계산중 오류가 발생하였습니다.");
								        	retMsg = "근태 시간계산중 오류가 발생하였습니다.";
											throw new Exception(retMsg);
										}
										taaSdate = retDate;
										// taaEdate = getTimeMap.get("taaEdate").toString();
									} 
								}
								
								System.out.println("taaSdate : " + taaSdate);
								System.out.println("taaEdate : " + taaEdate);
								String chkYmd = WtmUtil.parseDateStr(new Date(), null);
								if("99".equals(nowApplStatusCd)) {
									// 근태생성
									//(Long tenantId, String enterCd, String ymd, String sabun, String addTimeTypeCd, String addTaaCd,
									//		Date addSdate, Date addEdate, Long applId, String userId, boolean isAdd)
									
									if ("D".equals(taaCode.getRequestTypeCd()) && "N".equals(getStdMgrMap.get("taaWorkYn"))) {
										// 종일근무이면서 근무가능여부가 N이면 근무계획을 삭제하고 근태만 남겨둬야함.  
										List<String> timeType = new ArrayList<String>();
										timeType.add(WtmApplService.TIME_TYPE_BASE);
										timeType.add(WtmApplService.TIME_TYPE_LLA);
										List<WtmWorkDayResult> base = dayResultRepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdInAndYmdBetweenOrderByPlanSdateAsc(tenantId, taaDetMap.get("enterCd").toString(), taaDetMap.get("sabun").toString(), timeType, taaDetMap.get("ymd").toString(), taaDetMap.get("ymd").toString());
										for(WtmWorkDayResult r : base) {
											dayResultRepo.delete(r);
										}
										
										// 인정근무까지 만들어야함.
										WtmWorkDayResult newTaa = new WtmWorkDayResult();
										newTaa.setTenantId(Long.parseLong(taaDetMap.get("tenantId").toString()));
										newTaa.setEnterCd(taaDetMap.get("enterCd").toString());
										newTaa.setYmd(taaDetMap.get("ymd").toString());
										newTaa.setSabun(taaDetMap.get("sabun").toString());
										newTaa.setApplId(applId);
										newTaa.setTimeTypeCd(timeTypeCd);
										newTaa.setTaaCd(taaDetMap.get("taaCd").toString());
										newTaa.setPlanSdate(dt.parse(taaSdate));
										newTaa.setPlanEdate(dt.parse(taaEdate));
										newTaa.setPlanMinute(480);
										newTaa.setApprSdate(dt.parse(taaSdate));
										newTaa.setApprEdate(dt.parse(taaEdate));
										newTaa.setApprMinute(480);
										newTaa.setUpdateDate(new Date());
										newTaa.setUpdateId("taa if");
										dayResultRepo.save(newTaa);
										
									} else {
	//									System.out.println("taaSdate : " + WtmUtil.toDate(taaSdate, "yyyyMMddhhmmss"));
	//									System.out.println("taaEdate : " + WtmUtil.toDate(taaEdate, "yyyyMMddhhmmss"));
																				
										wtmFlexibleEmpService.addWtmDayResultInBaseTimeType(
												  Long.parseLong(taaDetMap.get("tenantId").toString())
												, taaDetMap.get("enterCd").toString()
												, taaDetMap.get("ymd").toString()
												, taaDetMap.get("sabun").toString()
												, timeTypeCd
												, taaDetMap.get("taaCd").toString()
												, dt.parse(taaSdate)
												, dt.parse(taaEdate)
												, Long.parseLong(taaDetMap.get("applId").toString())
												, "TAAIF");
									}
					        		// 오늘 이전이면 근무마감을 다시 돌려야함.
									if (Integer.parseInt(chkYmd) > Integer.parseInt(taaDetMap.get("ymd").toString())) {
										wtmFlexibleEmpService.calcApprDayInfo(Long.parseLong(taaDetMap.get("tenantId").toString())
						        											 , taaDetMap.get("enterCd").toString()
						        											 , taaDetMap.get("ymd").toString()
						        											 , taaDetMap.get("ymd").toString()
						        											 , taaDetMap.get("sabun").toString());
									}
									
								} else {
									// 취소이면 근태삭제
									wtmFlexibleEmpService.removeWtmDayResultInBaseTimeType(
											Long.parseLong(taaDetMap.get("tenantId").toString())
											, taaDetMap.get("enterCd").toString()
											, taaDetMap.get("ymd").toString()
											, taaDetMap.get("sabun").toString()
											, timeTypeCd
											, taaDetMap.get("taaCd").toString()
											, dt.parse(taaSdate)
											, dt.parse(taaEdate)
											, Long.parseLong(taaDetMap.get("applId").toString())
											, "TAAIF");
									
									// 오늘 이전이면 근무마감을 다시 돌려야함.
									if (Integer.parseInt(chkYmd) > Integer.parseInt(taaDetMap.get("ymd").toString())) {
										wtmFlexibleEmpService.calcApprDayInfo(Long.parseLong(taaDetMap.get("tenantId").toString())
						        											 , taaDetMap.get("enterCd").toString()
						        											 , taaDetMap.get("ymd").toString()
						        											 , taaDetMap.get("ymd").toString()
						        											 , taaDetMap.get("sabun").toString());
									}
								}
								
								// 근무시간합산은 재정산한다
				        		HashMap<String, Object> setTermMap = new HashMap();
				        		setTermMap.put("tenantId", reqMap.get("tenantId"));
				        		setTermMap.put("enterCd", taaDetMap.get("enterCd").toString());
				        		setTermMap.put("sabun", taaDetMap.get("sabun").toString());
				        		setTermMap.put("symd", taaDetMap.get("ymd").toString());
				        		setTermMap.put("eymd", taaDetMap.get("ymd").toString());
				        		setTermMap.put("pId", "TAAIF");
				        		wtmFlexibleEmpMapper.createWorkTermBySabunAndSymdAndEymd(setTermMap);
							}
						} 
					}	
				}
			}
		
		retMap.put("status", "OK");
    	retMap.put("retMsg", "");
 		System.out.println("WtmInterfaceServiceImpl WTM_IF_HIS save end");
		return retMap;
	}
	
	
	@Override
	@Transactional
	public void setIfHis(Map<String, Object> reqMap) throws Exception {
		// TODO Auto-generated method stub
		wtmInterfaceMapper.insertIfHis(reqMap);
	}
	
	@Async("threadPoolTaskExecutor")
	@Override
	public void intfTaaAppl(Long tenantId) {
		logger.debug("intfTaaAppl START ==================================== ");
		logger.debug("intfTaaAppl : " + tenantId);
		
		if(this.saveWtmIfTaaHisOnlyTaaApplPpType(tenantId)) {
		//if(true) {
			System.out.println("setTaaApplBatchIfPostProcess");

			List<String> statusList = new ArrayList<String>();
			statusList.add("OK");
//			statusList.add("FAIL");
//			statusList.add("ERR");
			List<WtmIfTaaHis> list = wtmIfTaaHisRepo.findByIfStatusNotIn(statusList); 
			if(list == null || list.size() == 0) {
				logger.debug("setTaaApplBatchIfPostProcess 대상없음 종료");
				return ;
			}
			logger.debug("intfTaaAppl 대상 " + list.size() + " 건");
			Map<String, Map<String, List<WtmIfTaaHis>>> intfMap = new HashMap<String, Map<String, List<WtmIfTaaHis>>>();
			//그룹핑을 한번 하자. 
			for(WtmIfTaaHis data : list) {
				String key = ""+data.getTenantId()+data.getEnterCd()+data.getApplNo();
				Map<String, List<WtmIfTaaHis>> s = null;
				if(!intfMap.containsKey(key)) {
					s = new HashMap<String, List<WtmIfTaaHis>>();
					s.put(data.getSabun(), new ArrayList<WtmIfTaaHis>());
					intfMap.put(key, s);
				}else {
					s = intfMap.get(key);
					if(!s.containsKey(data.getSabun())) {
						s.put(data.getSabun(), new ArrayList<WtmIfTaaHis>());
					}
				}
				
				List<WtmIfTaaHis> hiss = s.get(data.getSabun());
				hiss.add(data);
				s.put(data.getSabun(), hiss);
				intfMap.put(key,s);
			}
			
			ObjectMapper mapper = new ObjectMapper();
			
			try {
				logger.debug("intfMap : " + mapper.writeValueAsString(intfMap));
				
			} catch (JsonProcessingException e1) {
				e1.printStackTrace();
			}
			
			if(intfMap != null && intfMap.size() > 0) {
				//ifApplNo 묶음
				for(String k : intfMap.keySet()) {
					Map<String, List<WtmIfTaaHis>> empMap = intfMap.get(k);
					Long tId = null;
					String enterCd = null;
					String applSabun = null;
					String ifApplNo = null;
					String status = null;
					List<Map<String, Object>> works = new ArrayList<Map<String,Object>>();
					//사원별 데이터 
					for(String empSabun : empMap.keySet()) {
						List<WtmIfTaaHis> taaHis = empMap.get(empSabun);
					
						/*
						"worksDet" : [{ "workTimeCode" : ""
										, "startYmd" : ""  //기준일이 없다.. 시작일을 기준일로  
										, "endYmd" : ""
										, "startHm" : ""
										, "endHm" : ""
									}]
						 */
						List<Map<String, Object>> worksDet = new ArrayList<Map<String,Object>>();
						for(WtmIfTaaHis data : taaHis) {
							//첫데이터를 담는다 기준이 없어서..
							if(tId == null) { tId = data.getTenantId();}
							if(enterCd == null) { enterCd = data.getEnterCd();}
							if(applSabun == null) { applSabun = data.getSabun();}
							if(ifApplNo == null) { ifApplNo = data.getApplNo();}
							if(status == null) { status = data.getStatus();}
							
							SimpleDateFormat ymd = new SimpleDateFormat("yyyyMMdd");
							Calendar cal1 = Calendar.getInstance();
							Calendar cal2 = Calendar.getInstance();
							try {
								cal1.setTime(ymd.parse(data.getStartYmd()));
								cal2.setTime(ymd.parse(data.getEndYmd()));
							} catch (ParseException e) {
								e.printStackTrace();
							}
							
							Date d1 = cal1.getTime();
							Date d2 = cal2.getTime();
							while(d1.compareTo(d2) < 1) {
								logger.debug("cal1 : " + d1);
								logger.debug("cal2 : " + d2);
								
								Map<String, Object> det = new HashMap<>();
								det.put("workTimeCode", data.getWorkTimeCode());
								det.put("startYmd", ymd.format(d1));
								det.put("endYmd", ymd.format(d1));
								det.put("startHm", (data.getStartHm() != null)?data.getStartHm():"");
								det.put("endHm", (data.getEndHm() != null)?data.getEndHm():"");
								
								worksDet.add(det);
								
								cal1.add(Calendar.DATE, 1);
								
								d1 = cal1.getTime();
								
							}
						}
						Map<String, Object> empWork = new HashMap<String, Object>();
						empWork.put("sabun", empSabun);
						empWork.put("worksDet", worksDet);
						works.add(empWork);
					}
					 
					try {
						
						/*
						 works [ 
		  					{ "sabun " : "",
		  					  "worksDet" : [{ "workTimeCode" : ""
											, "startYmd" : ""  //기준일이 없다.. 시작일을 기준일로  
											, "endYmd" : ""
											, "startHm" : ""
											, "endHm" : ""
											}]
							}
						] 
						 */
						//setTaaResult(data);
						
						
						logger.debug("intfTaaAppl tenantId : " + tId);
						logger.debug("intfTaaAppl enterCd : " + enterCd);
						logger.debug("intfTaaAppl applSabun : " + applSabun);
						logger.debug("intfTaaAppl status : " + status);
						logger.debug("intfTaaAppl ifApplNo : " + ifApplNo);
						logger.debug("intfTaaAppl works : " + mapper.writeValueAsString(works));
						
						this.taaResult(tId, enterCd, applSabun, ifApplNo, status, works);
						
						List<WtmIfTaaHis> ifTaaHisList = wtmIfTaaHisRepo.findByTenantIdAndEnterCdAndApplNoAndIfStatusNotIn(tId, enterCd, ifApplNo, "OK");
						for(WtmIfTaaHis h : ifTaaHisList) {
							h.setIfStatus("OK");
						}
						wtmIfTaaHisRepo.saveAll(ifTaaHisList);
			    	} catch (Exception e) {
			    		e.printStackTrace();
			    		List<WtmIfTaaHis> ifTaaHisList = wtmIfTaaHisRepo.findByTenantIdAndEnterCdAndApplNoAndIfStatusNotIn(tId, enterCd, ifApplNo, "OK");
						for(WtmIfTaaHis h : ifTaaHisList) {
				    		h.setIfStatus("FAIL");
				    		h.setIfMsg(e.getMessage());
						}
						wtmIfTaaHisRepo.saveAll(ifTaaHisList);
					}
				}
			}
	    	
			logger.debug("intfTaaAppl end");
	        
		}

		logger.debug("intfTaaAppl END ==================================== ");
	}
	
	/**
	 * 
	 * @param tenantId
	 * @param enterCd
	 * @param sabun
	 * @param ifApplNo
	 * @param status
	 * @param works [ 
	 * 					{ "sabun " : "",
	 * 					  "worksDet" : [{ "workTimeCode" : ""
										, "startYmd" : ""  //기준일이 없다.. 시작일을 기준일로  
										, "endYmd" : ""
										, "startHm" : ""
										, "endHm" : ""
										}]
						}
					]
	 * @throws Exception
	 */
	@Transactional
	@Override
	public void taaResult(Long tenantId, String enterCd, String applSabun, String ifApplNo, String status, List<Map<String, Object>> works) throws Exception {
		logger.debug("===============================taaResult======================================= ");
		logger.debug("tenantId : " + tenantId);
		logger.debug("enterCd : " + enterCd);
		logger.debug("applSabun : " + applSabun);
		logger.debug("ifApplNo : " + ifApplNo);
		logger.debug("status : " + status);
		
		List<String> statusList = new ArrayList<String>();
		statusList.add(WtmApplService.APPL_STATUS_APPLY_ING);
		statusList.add(WtmApplService.APPL_STATUS_APPLY_REJECT);
		statusList.add(WtmApplService.APPL_STATUS_APPR);
		statusList.add(WtmApplService.APPL_STATUS_APPR_ING);
		statusList.add(WtmApplService.APPL_STATUS_APPR_REJECT);
		statusList.add(WtmApplService.APPL_STATUS_CANCEL);
		statusList.add(WtmApplService.APPL_STATUS_IMSI);
		
		if(statusList.indexOf(status) == -1) {
			throw new RuntimeException("지원하지 않은 신청서 상태코드");
		}
		/*
		Long tenantId = data.getTenantId();
		String enterCd = data.getEnterCd();
		String sabun = data.getSabun();
		String taaCd = data.getWorkTimeCode();
		String symd = data.getStartYmd();
		String eymd = data.getEndYmd();
		String shm = (data.getStartHm() != null && !"".equals(data.getStartHm()))?data.getStartHm():"";
		String ehm = (data.getEndHm() != null && !"".equals(data.getEndHm()))?data.getEndHm():"";
		String ifApplNo = data.getApplNo();
		String status = data.getStatus();
		*/
		//변경 전의 상태값을 가지고 있는데 99였다가 44가 될 경우엔 RESULT를 다시 생성해야하기 때문이다. 
		//21 > 44 는 괜찮다. 
		String preApplStatus = null;
		WtmAppl appl = null;
		List<WtmTaaAppl> taaAppls = wtmTaaApplRepo.findByTenantIdAndEnterCdAndIfApplNo(tenantId, enterCd, ifApplNo);
		SimpleDateFormat ymd = new SimpleDateFormat("yyyyMMdd");
		//기신청 데이터 
		if(taaAppls == null || taaAppls.size() == 0) {
			logger.debug("works.size() : " + works.size());
			if(works != null && works.size() > 0) {
				//신청 또는 승인 완료 건에 대해서만
				if(WtmApplService.APPL_STATUS_APPLY_ING.equals(status) || WtmApplService.APPL_STATUS_APPR.equals(status) || WtmApplService.APPL_STATUS_CANCEL.equals(status)) {
					appl = wtmApplRepo.findByTenantIdAndEnterCdAndIfApplNo(tenantId, enterCd, ifApplNo);
					if(appl == null) {
						appl = new WtmAppl();
						appl.setTenantId(tenantId);
						appl.setEnterCd(enterCd);
						appl.setIfApplNo(ifApplNo);
						appl.setApplYmd(ymd.format(new Date()));
					} else {
						//있으면 문제다. 데이터 동기화 작업이 필요. 99일 경우
						preApplStatus = appl.getApplStatusCd();
					}
					appl.setApplCd(WtmApplService.TIME_TYPE_TAA);
					appl.setApplSabun(applSabun);
					appl.setApplInSabun(applSabun);
					appl.setApplStatusCd(status);
					appl.setUpdateId("TAA_INTF");
					
					appl = wtmApplRepo.save(appl);
					
					 
					for(Map<String, Object> w : works) {
						String sabun = w.get("sabun")+"";
						
						if(w.containsKey("worksDet") && w.get("worksDet") != null && !"".equals(w.get("worksDet")+"")) {
							
							WtmTaaAppl taaAppl = new WtmTaaAppl();
							taaAppl.setTenantId(tenantId);
							taaAppl.setEnterCd(enterCd);
							taaAppl.setApplId(appl.getApplId());
							taaAppl.setSabun(sabun);
							taaAppl.setIfApplNo(ifApplNo);
							taaAppl.setUpdateId("TAA_INTF");
							
							taaAppl = wtmTaaApplRepo.save(taaAppl);
							
							List<Map<String, Object>> worksDet = (List<Map<String, Object>>) w.get("worksDet");
							for(Map<String, Object> work : worksDet) {
							
								if(work.containsKey("workTimeCode") && work.containsKey("startYmd") && work.containsKey("endYmd")
										&& work.get("workTimeCode") != null && !"".equals(work.get("workTimeCode"))
										&& work.get("startYmd") != null && !"".equals(work.get("startYmd"))
										&& work.get("endYmd") != null && !"".equals(work.get("endYmd"))
										) {
									String taaCd = work.get("workTimeCode").toString();
									String symd = work.get("startYmd").toString();
									String eymd = work.get("endYmd").toString();
									String shm = "";
									if(work.containsKey("startHm") && work.get("startHm") != null && !"".equals(work.get("startHm"))) {
										shm = work.get("startHm").toString();
									}
									String ehm = "";
									if(work.containsKey("endHm") && work.get("endHm") != null && !"".equals(work.get("endHm"))) {
										ehm = work.get("endHm").toString();
									}
									
									WtmTaaCode taaCode = wtmTaaCodeRepo.findByTenantIdAndEnterCdAndTaaCd(tenantId, enterCd, taaCd);
									if(taaCode == null || taaCode.getRequestTypeCd() == null || taaCode.getRequestTypeCd().equals("N")){
										throw new RuntimeException("근태신청이 불가능한 근태코드입니다. ");
									}
									
									logger.debug("마감여부 체크 : ");
									List<WtmWorkCalendar> calendars = workCalendarRepo.findByTenantIdAndEnterCdAndSabunAndYmdBetween(tenantId, enterCd, sabun, symd, eymd);
									if(calendars != null && calendars.size() > 0) {
										for(WtmWorkCalendar c : calendars) {
											if("Y".equals(c.getWorkCloseYn())) {
												throw new RuntimeException("신청 기간 내 마감된 근무일이 존재합니다.");
											}
										}
									}else {
										throw new RuntimeException("캘린더 정보가 없습니다.");
									}
									
									if(Integer.parseInt(symd) > Integer.parseInt(eymd)) {
										throw new RuntimeException("시작일자가 종료일보다 클 수 없습니다.");
									}
										
									
									WtmTaaApplDet taaApplDet = new WtmTaaApplDet();
									taaApplDet.setTaaApplId(taaAppl.getTaaApplId());
									taaApplDet.setTaaCd(taaCd);
									taaApplDet.setSymd(symd);
									taaApplDet.setEymd(eymd);
									taaApplDet.setShm(shm);
									taaApplDet.setEhm(ehm); 
									taaApplDet.setUpdateId("TAA_INTF");
									
									wtmTaaApplDetRepo.save(taaApplDet);
									
								}else {
									throw new RuntimeException("근태정보가 부족합니다.");
								} 
							}
						}else {
							throw new RuntimeException(sabun + " 님의 근무 상세정보가 없습니다.");
						}
					}
					
				}
			}
		}else {
			//List<String> applCds = new ArrayList<String>();
			//applCds.add(WtmApplService.TIME_TYPE_TAA);
			//applCds.add(WtmApplService.TIME_TYPE_REGA);
			appl = wtmApplRepo.findByTenantIdAndEnterCdAndIfApplNo(tenantId, enterCd, ifApplNo);
			if(appl != null) {
				preApplStatus = appl.getApplStatusCd();
				if(!preApplStatus.equals(status)) {
					/*
					if(preApplStatus.equals(WtmApplService.APPL_STATUS_APPR) 
							&& !status.equals(WtmApplService.APPL_STATUS_APPR_REJECT) 
							&& !status.equals(WtmApplService.APPL_STATUS_CANCEL)) {
						//기존 신청서의 상태가 완료일때 반려 또는 취소건이 아니면 상태 갱신을 할 수 없다 .
						throw new RuntimeException("완료된 신청 건의 상태를 변경 시에는 취소(44) 또는 반려(22)일 때만 가능합니다.");
					}
					*/
					appl.setApplStatusCd(status);
					wtmApplRepo.save(appl);
				}
			}
		}
			
		logger.debug("============================== HIS END ");
		logger.debug("============================== preApplStatus : " + preApplStatus);
		logger.debug("============================== status : " + status);
		//이건 상태랑 같으면 무시
		if(preApplStatus == null || !preApplStatus.equals(status)) {

			if(status.equals(WtmApplService.APPL_STATUS_APPR) 
					|| status.equals(WtmApplService.APPL_STATUS_APPR_REJECT) 
					|| status.equals(WtmApplService.APPL_STATUS_CANCEL)) {
				for(Map<String, Object> w : works) {
					List<Map<String, Object>> worksDet = (List<Map<String, Object>>) w.get("worksDet");
					for(Map<String, Object> work : worksDet) {

						logger.debug("============================== resetTaaResult : " + w.get("sabun")+" : " + work.get("startYmd")+"");
						this.resetTaaResult(tenantId, enterCd, w.get("sabun")+"", work.get("startYmd")+"");
					}
					
				}
			}
		}
		 
	}
	/**
	 * 특정일의 result 정보를 근태신청서 기준으로 재구성한다. 
	 */
	@Transactional
	@Override
	public void resetTaaResult(Long tenantId, String enterCd, String sabun,String ymd) {
		List<WtmTaaApplDet> dets = wtmTaaApplDetRepo.findByMaxApplInfo(tenantId, enterCd, sabun, ymd);
		logger.debug("dets : " + dets);
		if(dets != null && dets.size() > 0) {
			SimpleDateFormat ymdhm = new SimpleDateFormat("yyyyMMddHHmm");
			/*
			WtmFlexibleStdMgr flexibleStdMgr = flexStdMgrRepo.findByTenantIdAndEnterCdAndSabunAndYmdBetween(tenantId, enterCd, sabun, ymd);
			WtmWorkCalendar cal = workCalendarRepo.findByTenantIdAndEnterCdAndYmdAndSabun(tenantId, enterCd, ymd, sabun);
			WtmTimeCdMgr timeCdMgr = timeCdMgrRepo.findById(cal.getTimeCdMgrId()).get();
			*/
			List<String> timeTypeCds = new ArrayList<String>();
			timeTypeCds.add(WtmApplService.TIME_TYPE_REGA);
			timeTypeCds.add(WtmApplService.TIME_TYPE_TAA);
			

			/**
			 * 초기화
			 */

			SimpleDateFormat ymdFt = new SimpleDateFormat("yyyyMMdd");
			
			for(WtmTaaApplDet det : dets) {
				List<WtmWorkDayResult> taaResults = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdInAndYmdBetweenOrderByPlanSdateAsc(tenantId, enterCd, sabun, timeTypeCds, det.getSymd(),  det.getEymd());
				//workDayResultRepo.deleteAll(taaResults);
				logger.debug("taaResults : " + taaResults.size());
				if(taaResults != null && taaResults.size() > 0 ) {
					for(WtmWorkDayResult delResult : taaResults) {
						logger.debug("resetTaaResult remove result : " + delResult.toString());
						if(delResult.getPlanSdate() == null) {
							workDayResultRepo.delete(delResult);
						}else {
							wtmFlexibleEmpService.removeWtmDayResultInBaseTimeType(tenantId, enterCd, delResult.getYmd(), sabun, delResult.getTimeTypeCd(), delResult.getTaaCd(), delResult.getPlanSdate(), delResult.getPlanEdate(), delResult.getApplId(), "remove");
						}
					}
				}
			}
			
			for(WtmTaaApplDet det : dets) {
				Calendar cal1 = Calendar.getInstance();
				Calendar cal2 = Calendar.getInstance();
				try {
					cal1.setTime(ymdFt.parse(det.getSymd()));
					cal2.setTime(ymdFt.parse(det.getEymd()));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				Date d1 = cal1.getTime();
				Date d2 = cal2.getTime();
				while(d1.compareTo(d2) < 1) {
					logger.debug("cal1 : " + d1);
					logger.debug("cal2 : " + d2);
					 
					String d = ymdFt.format(d1);
					System.out.println("sabun : " + sabun);
					System.out.println("d : " + d);
					WtmFlexibleStdMgr flexibleStdMgr = flexStdMgrRepo.findByTenantIdAndEnterCdAndSabunAndYmdBetween(tenantId, enterCd, sabun, d);
					WtmWorkCalendar cal = workCalendarRepo.findByTenantIdAndEnterCdAndYmdAndSabun(tenantId, enterCd, d, sabun);
					WtmTimeCdMgr timeCdMgr = timeCdMgrRepo.findById(cal.getTimeCdMgrId()).get();
					
					
				
					WtmTaaCode taaCode = wtmTaaCodeRepo.findByTenantIdAndEnterCdAndTaaCd(tenantId, enterCd, det.getTaaCd());
					String timeTypeCd = WtmApplService.TIME_TYPE_TAA;
					//간주근무 여부 
					if("Y".equals(taaCode.getWorkYn())){
						timeTypeCd = WtmApplService.TIME_TYPE_REGA;
					}
					WtmTaaAppl taaAppl = wtmTaaApplRepo.findById(det.getTaaApplId()).get();
					WtmAppl appl = wtmApplRepo.findById(taaAppl.getApplId()).get();
					
					//위에서 다 지웠기 때문에 승인건만 적용하면 된다. 
					logger.debug("det appl.getApplStatusCd(): " + appl.getApplStatusCd());
					if(appl.getApplStatusCd().equals(WtmApplService.APPL_STATUS_APPR)) {
						logger.debug("flexibleStdMgr.getUnplannedYn() : " + flexibleStdMgr.getUnplannedYn());
						if("Y".equals(flexibleStdMgr.getUnplannedYn())) {
							WtmWorkDayResult dayResult = new WtmWorkDayResult();
							Integer workMinute = 0;
							if(taaCode.getWorkApprHour()!=null) {
								workMinute = Integer.parseInt(taaCode.getWorkApprHour().toString()) * 60;
							}
							/*
							  CASE WHEN IFNULL(B.SHM, '') != '' AND IFNULL(B.EHM, '') != ''
					 		  THEN timestampdiff(MINUTE, F_WTM_TO_DATE(CONCAT(C.YMD, B.SHM), 'YMDHI'), F_WTM_TO_DATE(CONCAT(C.YMD, B.EHM), 'YMDHI'))
					 		  WHEN IFNULL(B.TAA_MINUTE,0) > 0 THEN B.TAA_MINUTE
					 		  ELSE 0 END AS workMinute				 		  
							*/
							if(det.getShm() != null && !"".equals(det.getShm())
									&& det.getEhm() != null && !"".equals(det.getEhm())
									) {
								workMinute = calcService.WtmCalcMinute(det.getShm(), det.getEhm(), null, null, null);
							}else if(det.getTaaMinute() != null && !"".equals(det.getTaaMinute())) {
								workMinute = Integer.parseInt(det.getTaaMinute());
							}
	
							dayResult.setTenantId(tenantId);
							dayResult.setEnterCd(enterCd);
							dayResult.setYmd(d);
							dayResult.setSabun(sabun);
							dayResult.setApplId(appl.getApplId());
							dayResult.setTimeTypeCd(timeTypeCd);
							dayResult.setTaaCd(det.getTaaCd());
							dayResult.setPlanMinute(workMinute);
							dayResult.setApprMinute(workMinute);
							dayResult.setUpdateId("TAAIF");
							dayResultRepo.save(dayResult);
							
							/*
							if(!"0".equals(taaDetMap.get("workMinute").toString())) {
								workMinute = Integer.parseInt(taaDetMap.get("workMinute").toString());
							}
							*/
						}else {
							logger.debug("taaCode.getHolInclYn() : " + taaCode.getHolInclYn());
							logger.debug("taaCode.getRequestTypeCd() : " + taaCode.getRequestTypeCd());
							logger.debug("cal.getHolidayYn() : " + cal.getHolidayYn());
							logger.debug("taaCode.getHolInclYn() : " + taaCode.getHolInclYn());
							
							//근태코드 기준이 휴일 포함 이거나 휴일포함이 아니면 해당일의 휴일이 아니어야한다. 
							if("Y".equals(taaCode.getHolInclYn())
									|| ( "N".equals(taaCode.getHolInclYn()) && "N".equals(cal.getHolidayYn()))
									) {
								if("D".equals(taaCode.getRequestTypeCd())
										&& "Y".equals(taaCode.getHolInclYn())
										&& "Y".equals(cal.getHolidayYn())
										) {
									//휴일포함이면서 휴일이면서 종일근무이면
									WtmWorkDayResult dayResult = new WtmWorkDayResult();
									dayResult.setTenantId(tenantId);
									dayResult.setEnterCd(enterCd);
									dayResult.setYmd(d);
									dayResult.setSabun(sabun);
									dayResult.setApplId(appl.getApplId());
									dayResult.setTimeTypeCd(timeTypeCd);
									dayResult.setTaaCd(det.getTaaCd());
									dayResult.setUpdateId("TAAIF");
									dayResultRepo.save(dayResult);
								}else {
	
									Date sdate = null;
									Date edate = null;
									
									Integer workMinute = 0;
									if(det.getShm() != null && !"".equals(det.getShm())
											&& det.getEhm() != null && !"".equals(det.getEhm())
											) {
										workMinute = calcService.WtmCalcMinute(det.getShm(), det.getEhm(), null, null, null);
									//}else if(det.getTaaMinute() != null && !"".equals(det.getTaaMinute())) {
									//	workMinute = Integer.parseInt(det.getTaaMinute());
									}
									
									logger.debug("workMinute : " + workMinute);
									if(workMinute > 0) {
										//근무시간이 있으면
										try {
											sdate = ymdhm.parse(d+det.getShm());
											edate = ymdhm.parse(d+det.getEhm());
										} catch (ParseException e) {
											e.printStackTrace();
											sdate = null; edate = null;
										}

										if(sdate.compareTo(edate) > 0) {
											Calendar c = Calendar.getInstance();
											c.setTime(edate);
											c.add(Calendar.DATE, 1);
											edate = c.getTime();
										}
									}else {
										//기본근무시간
										try {
											sdate = ymdhm.parse(d+timeCdMgr.getWorkShm());
											edate = ymdhm.parse(d+timeCdMgr.getWorkEhm());
										} catch (ParseException e) {
											e.printStackTrace();
											sdate = null; edate = null;
										}
										if(sdate.compareTo(edate) > 0) {
											Calendar c = Calendar.getInstance();
											c.setTime(edate);
											c.add(Calendar.DATE, 1);
											edate = c.getTime();
										}
										// 근무시간이 없으면 근태코드별 시간을 조정해야함.
										logger.debug("반차는 근무시간을 변경함 : " + taaCode.getRequestTypeCd());
										if("P".equals(taaCode.getRequestTypeCd())) {
											if(timeCdMgr.getBreakTypeCd().equals(WtmApplService.BREAK_TYPE_MGR)) {
												//반차는 근무시간을 변경함
												sdate = calcService.P_WTM_DATE_ADD_FOR_BREAK_MGR(sdate, 240, cal.getTimeCdMgrId(), flexibleStdMgr.getUnitMinute());
											}else {
												Calendar calendar = Calendar.getInstance();
												calendar.setTime(sdate);
												calendar.add(Calendar.MINUTE, 240);
												sdate = calendar.getTime();
											}
											
											//calcService.getBreakMinuteIfBreakTimeMGR(sDate, eDate, timeCdMgrId, null)
										}else if("A".equals(taaCode.getRequestTypeCd())) {
											if(timeCdMgr.getBreakTypeCd().equals(WtmApplService.BREAK_TYPE_MGR)) {
												edate = calcService.P_WTM_DATE_ADD_FOR_BREAK_MGR(edate, -240, cal.getTimeCdMgrId(), flexibleStdMgr.getUnitMinute());
											}else {
												Calendar calendar = Calendar.getInstance();
												calendar.setTime(edate);
												calendar.add(Calendar.MINUTE, -240);
												edate = calendar.getTime();
											}
										}
									}
									if("D".equals(taaCode.getRequestTypeCd())
											&& (flexibleStdMgr.getTaaWorkYn() == null || "N".equals(flexibleStdMgr.getTaaWorkYn()) || "".equals(flexibleStdMgr.getTaaWorkYn()) )
											) {
										logger.debug("종일근무이면서 근무가능여부가 N이면 근무계획을 삭제하고 근태만 남겨둬야함.");
										List<String> timeType = new ArrayList<String>();
										timeType.add(WtmApplService.TIME_TYPE_BASE);
										timeType.add(WtmApplService.TIME_TYPE_LLA);
										
										List<WtmWorkDayResult> delResults = workDayResultRepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdInAndYmdBetweenOrderByPlanSdateAsc(tenantId, enterCd, sabun, timeType, d, d);
										if(delResults != null && delResults.size() > 0) {
											workDayResultRepo.deleteAll(delResults);
										}
										WtmWorkDayResult newTaa = new WtmWorkDayResult();
										newTaa.setTenantId(tenantId);
										newTaa.setEnterCd(enterCd);
										newTaa.setYmd(d);
										newTaa.setSabun(sabun);
										newTaa.setApplId(appl.getApplId());
										newTaa.setTimeTypeCd(timeTypeCd);
										newTaa.setTaaCd(det.getTaaCd());
										newTaa.setPlanSdate(sdate);
										newTaa.setPlanEdate(edate);
										Map<String, Object> calcMap = calcService.calcApprMinute(sdate, edate, timeCdMgr.getBreakTypeCd(), timeCdMgr.getTimeCdMgrId(), flexibleStdMgr.getUnitMinute());
										int apprMinute = Integer.parseInt(calcMap.get("apprMinute")+"");
										
										newTaa.setPlanMinute(apprMinute);
										newTaa.setApprSdate(sdate);
										newTaa.setApprEdate(edate);
										newTaa.setApprMinute(apprMinute);
										newTaa.setUpdateDate(new Date());
										newTaa.setUpdateId("taa if");
										dayResultRepo.save(newTaa);
										
									}else {
										wtmFlexibleEmpService.addWtmDayResultInBaseTimeType(
												tenantId
												, enterCd
												, d
												, sabun
												, timeTypeCd
												, det.getTaaCd()
												, sdate
												, edate
												, appl.getApplId()
												, "TAAIF");
									}
								}
							}
								
						}
					}

					cal1.add(Calendar.DATE, 1);
					
					d1 = cal1.getTime();
				}
				
			}
			
			String chkYmd = WtmUtil.parseDateStr(new Date(), null);
			

			for(WtmTaaApplDet det : dets) {


				Calendar cal1 = Calendar.getInstance();
				Calendar cal2 = Calendar.getInstance();
				try {
					cal1.setTime(ymdFt.parse(det.getSymd()));
					cal2.setTime(ymdFt.parse(det.getEymd()));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				Date d1 = cal1.getTime();
				Date d2 = cal2.getTime();
				while(d1.compareTo(d2) < 1) {
					logger.debug("cal1 : " + d1);
					logger.debug("cal2 : " + d2);
					 
					String d = ymdFt.format(d1);
					
					// 오늘 이전이면 근무마감을 다시 돌려야함.
					if (Integer.parseInt(chkYmd) > Integer.parseInt(d)) {
						//wtmFlexibleEmpService.resetCalcApprDayInfo(tenantId, enterCd, d, sabun, null);
						wtmFlexibleEmpService.calcApprDayInfo(tenantId
		        											 , enterCd
		        											 , d
		        											 , d
		        											 , sabun);
					}
					
					// 근무시간합산은 재정산한다 
		    		calcService.P_WTM_FLEXIBLE_EMP_WORKTERM_C(tenantId, enterCd, sabun, d, d);
	
					cal1.add(Calendar.DATE, 1);
					
					d1 = cal1.getTime();
				}
			}
		}else {
			throw new RuntimeException("신청정보가 없습니다.");
		}
	}
	@Transactional
	public boolean saveWtmIfTaaHisOnlyTaaApplPpType(Long tenantId) {
		
		boolean isOK = true;
		// 인터페이스 결과 저장용
    	String retMsg = null;
    	int resultCnt = 0;
    	String ifType = "TAA_APPL_PP";
    	Map<String, Object> ifHisMap = new HashMap<>();
    	ifHisMap.put("tenantId", tenantId);
    	ifHisMap.put("ifItem", ifType);
    	ifHisMap.put("ifStatus", "OK");
    	// 인터페이스용 변수
    	String lastDataTime = null;
    	String nowDataTime = null;
    	HashMap<String, Object> getDateMap = null;
    	HashMap<String, Object> getIfMap = null;
    	List<Map<String, Object>> getIfList = null;
    	
    	// 최종 자료 if 시간 조회
    	try {
    		getDateMap = (HashMap<String, Object>) getIfLastDate(tenantId, ifType);
    		lastDataTime = getDateMap.get("lastDate").toString();
    		nowDataTime = getDateMap.get("nowDate").toString();
    		//lastDataTime = "20200201010101";
    		try {
        		String param = "?lastDataTime="+lastDataTime;
	        	String ifUrl = setIfUrl(tenantId, "/taaAppl", param); 
	        	getIfMap = getIfRt(ifUrl);
		   		
		   		if (getIfMap != null && getIfMap.size() > 0) {
		   			String ifMsg = getIfMap.get("message").toString();
		   			getIfList = (List<Map<String, Object>>) getIfMap.get("ifData");
		   	    	if(retMsg == null && getIfList != null && getIfList.size() > 0) {
		   	    		System.out.println("WtmInterfaceServiceImpl tot " + getIfList.size());
		   	    		
		   				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		   				sdf.format(new Date());
		   				String yyyymmddhhmiss= sdf.format(new Date());
		   				
		   	    		for(int l=0; l<getIfList.size(); l++) {
		   	    			
//		   	    			if(!"15003".equals(getIfList.get(l).get("SABUN").toString())){
//		   	    				continue;
//		   	    			}
		   	    			
		   	    			WtmIfTaaHis data = new WtmIfTaaHis();
		   	    			data.setTenantId(tenantId);
		   	    			data.setEnterCd(getIfList.get(l).get("ENTER_CD").toString());
		   	    			data.setSabun(getIfList.get(l).get("SABUN").toString());
		   	    			data.setStartYmd(getIfList.get(l).get("S_YMD").toString());
		   	    			data.setEndYmd(getIfList.get(l).get("E_YMD").toString());
		   	    			data.setWorkTimeCode(getIfList.get(l).get("GNT_CD").toString());
		   	    			data.setIfYmdhis(yyyymmddhhmiss);
		   	    			//data.setIfYmdhis(getIfList.get(l).get("APPL_YMD").toString());
		   	    			if(getIfList.get(l).get("REQ_S_HM") != null) {
		   	    				data.setStartHm(getIfList.get(l).get("REQ_S_HM").toString());
		   	    			} else {
		   	    				data.setStartHm("");
		   	    			}
		   	    			if(getIfList.get(l).get("REQ_E_HM") != null) {
		   	    				data.setEndHm(getIfList.get(l).get("REQ_E_HM").toString());
		   	    			} else {
		   	    				data.setEndHm("");
		   	    			}
		   	    			data.setApplNo(getIfList.get(l).get("APPL_SEQ").toString());
		   	    			data.setStatus(getIfList.get(l).get("APPL_STATUS_CD").toString());
		   	    			data.setIfStatus("");
		   	    			data.setIfMsg("");
		   	    			wtmIfTaaHisRepo.save(data);
		   	    			System.out.println("WtmInterfaceServiceImpl get " + l + " "+ data.toString());
		   	    		}
		   	    	}
		   		} else {
		   			retMsg = "TAA_RESULT get : If 데이터 없음";
		   			ifHisMap.put("ifStatus", "OK");
		   		}
        	} catch(Exception e) {
        		retMsg = "TAA_RESULT get : If 서버통신 오류";
        		ifHisMap.put("ifStatus", "ERR");
        		isOK = false;
        	}
        	
    	} catch(Exception e) {
    		retMsg = "TAA_RESULT get : 최종갱신일 조회오류";
    		ifHisMap.put("ifStatus", "ERR");
    		isOK = false;
    	} finally {
			// WTM_IF_HIS 테이블에 결과저장
			ifHisMap.put("updateDate", nowDataTime);
   			ifHisMap.put("ifEndDate", lastDataTime);
			ifHisMap.put("ifMsg", retMsg);
			wtmInterfaceMapper.insertIfHis(ifHisMap);
		} 
    	
    	return isOK;
	}

	@Override
	@Transactional
	@Async("threadPoolTaskExecutor")
	public void setTaaApplBatchIf(Long tenantId) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("WtmInterfaceServiceImpl setTaaApplBatchIf");
		// 인터페이스 결과 저장용
    	String retMsg = null;
    	int resultCnt = 0;
    	String ifType = "TAA_APPL_PP";
    	Map<String, Object> ifHisMap = new HashMap<>();
    	ifHisMap.put("tenantId", tenantId);
    	ifHisMap.put("ifItem", ifType);
    	ifHisMap.put("ifStatus", "OK");
    	// 인터페이스용 변수
    	String lastDataTime = null;
    	String nowDataTime = null;
    	HashMap<String, Object> getDateMap = null;
    	HashMap<String, Object> getIfMap = null;
    	List<Map<String, Object>> getIfList = null;
    	
    	// 최종 자료 if 시간 조회
    	try {
    		getDateMap = (HashMap<String, Object>) getIfLastDate(tenantId, ifType);
    		lastDataTime = getDateMap.get("lastDate").toString();
    		nowDataTime = getDateMap.get("nowDate").toString();
    		
    		try {
        		String param = "?lastDataTime="+lastDataTime;
	        	String ifUrl = setIfUrl(tenantId, "/taaAppl", param); 
	        	getIfMap = getIfRt(ifUrl);
		   		
		   		if (getIfMap != null && getIfMap.size() > 0) {
		   			String ifMsg = getIfMap.get("message").toString();
		   			getIfList = (List<Map<String, Object>>) getIfMap.get("ifData");
		   	    	if(retMsg == null && getIfList != null && getIfList.size() > 0) {
		   	    		System.out.println("WtmInterfaceServiceImpl tot " + getIfList.size());
		   	    		
		   				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		   				sdf.format(new Date());
		   				String yyyymmddhhmiss= sdf.format(new Date());
		   				
		   	    		for(int l=0; l<getIfList.size(); l++) {
		   	    			WtmIfTaaHis data = new WtmIfTaaHis();
		   	    			data.setTenantId(tenantId);
		   	    			data.setEnterCd(getIfList.get(l).get("ENTER_CD").toString());
		   	    			data.setSabun(getIfList.get(l).get("SABUN").toString());
		   	    			data.setStartYmd(getIfList.get(l).get("S_YMD").toString());
		   	    			data.setEndYmd(getIfList.get(l).get("E_YMD").toString());
		   	    			data.setWorkTimeCode(getIfList.get(l).get("GNT_CD").toString());
		   	    			data.setIfYmdhis(yyyymmddhhmiss);
		   	    			if(getIfList.get(l).get("REQ_S_HM") != null) {
		   	    				data.setStartHm(getIfList.get(l).get("REQ_S_HM").toString());
		   	    			} else {
		   	    				data.setStartHm("");
		   	    			}
		   	    			if(getIfList.get(l).get("REQ_E_HM") != null) {
		   	    				data.setEndHm(getIfList.get(l).get("REQ_E_HM").toString());
		   	    			} else {
		   	    				data.setEndHm("");
		   	    			}
		   	    			data.setApplNo(getIfList.get(l).get("APPL_SEQ").toString());
		   	    			data.setStatus(getIfList.get(l).get("APPL_STATUS_CD").toString());
		   	    			data.setIfStatus("");
		   	    			data.setIfMsg("");
		   	    			wtmIfTaaHisRepo.save(data);
		   	    			System.out.println("WtmInterfaceServiceImpl get " + l + " "+ data.toString());
		   	    		}
		   	    	}
		   		} else {
		   			retMsg = "TAA_RESULT get : If 데이터 없음";
		   			ifHisMap.put("ifStatus", "OK");
		   		}
        	} catch(Exception e) {
        		retMsg = "TAA_RESULT get : If 서버통신 오류";
        		ifHisMap.put("ifStatus", "ERR");
        	}
        	
    	} catch(Exception e) {
    		retMsg = "TAA_RESULT get : 최종갱신일 조회오류";
    		ifHisMap.put("ifStatus", "ERR");
    	} finally {
			// WTM_IF_HIS 테이블에 결과저장
			ifHisMap.put("updateDate", nowDataTime);
   			ifHisMap.put("ifEndDate", lastDataTime);
			ifHisMap.put("ifMsg", retMsg);
			wtmInterfaceMapper.insertIfHis(ifHisMap);
		} 
		
    	//여기서부터다 바꿔
    	/*
    	//조회된 자료가 있으면...
    	if(retMsg == null && getIfList != null && getIfList.size() > 0) {
    		for(int l=0; l<getIfList.size(); l++) {
    			Map<String, Object> reqMap = new HashMap<>();
    			reqMap.put("tenantId", tenantId);
    			reqMap.put("enterCd", getIfList.get(l).get("ENTER_CD").toString());
    			reqMap.put("sabun", getIfList.get(l).get("SABUN").toString());
    			reqMap.put("taaCd", getIfList.get(l).get("GNT_CD").toString());
    			reqMap.put("sYmd", getIfList.get(l).get("S_YMD").toString());
    			reqMap.put("eYmd", getIfList.get(l).get("E_YMD").toString());
    			if(getIfList.get(l).get("REQ_S_HM") != null) {
    				reqMap.put("sHm", getIfList.get(l).get("REQ_S_HM").toString());
    			} else {
    				reqMap.put("sHm", "");
    			}
    			if(getIfList.get(l).get("REQ_E_HM") != null) {
    				reqMap.put("eHm", getIfList.get(l).get("REQ_E_HM").toString());
    			} else {
    				reqMap.put("eHm", "");
    			}
    			reqMap.put("ifApplNo", getIfList.get(l).get("APPL_SEQ").toString());
    			reqMap.put("status", getIfList.get(l).get("APPL_STATUS_CD").toString());
    			
    			
				// 2. 인터페이스 data 처리
				try {
					// 호출이되면 근태신청 데이터를 저장하거나 결재상태를 갱신한다
					reqMap.put("retCode", "");
					reqMap.put("retMsg", "");
					reqMap.put("taaApplId", "");
					reqMap.put("applId", "");
					reqMap.put("oldStatus", "");
					wtmInterfaceMapper.setTaaApplIf(reqMap);
					
					String retCode = reqMap.get("retCode").toString();
					System.out.println("CALL setTaaApplIf retCode : " + retCode);
					String oldStatusCd = "";
					if(reqMap.get("oldStatus") != null) { oldStatusCd = reqMap.get("oldStatus").toString();}
					
					if("OK".equals(retCode)) {
						//기간 루프
						String sYmd = reqMap.get("sYmd").toString();
						String eYmd = reqMap.get("eYmd").toString();
						
						SimpleDateFormat dt = new SimpleDateFormat("yyyyMMddHHmmss");
						
						SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
				        Date sDate = formatter.parse(sYmd);
				        Date eDate = formatter.parse(eYmd);
				         
				        // 시간차이를 시간,분,초를 곱한 값으로 나누면 하루 단위가 나옴
				        long diff = eDate.getTime() - sDate.getTime();
				        long diffDays = (diff / (24 * 60 * 60 * 1000)) +1;
						for(int i=0; i<diffDays; i++) {
							Calendar cal = Calendar.getInstance();
							cal.setTime(sDate);
							cal.add(Calendar.DATE, i);
							String ymd = formatter.format(cal.getTime());
							System.out.println("loop ymd : " + ymd);
							Map<String, Object> reqDayMap = reqMap;
							reqDayMap.put("ymd", ymd);
							reqDayMap.put("oldStatus", oldStatusCd);
							reqDayMap.put("retCode", "");
							reqDayMap.put("retMsg", "");
							reqDayMap.put("timeTypeCd", "");
							reqDayMap.put("taaSetYn", "");
							reqDayMap.put("taaSdate", "");
							reqDayMap.put("taaEdate", "");
							System.out.println("oldStatusCd : " + oldStatusCd);
							for ( String key : reqDayMap.keySet() ) {
			        		    System.out.println("key : " + key +" / value : " + reqDayMap.get(key));
			        		}
							//System.out.println("statusCd : " + reqDayMap.get("status"));
							wtmInterfaceMapper.setTaaApplDayIf(reqDayMap);
							
							String retDayCode = reqDayMap.get("retCode").toString();
							System.out.println("retMsg : " +  reqDayMap.get("retMsg").toString());
							if("FAIL".equals(retCode)) {
								// 오류다 ㅠㅠ
								ifHisMap.put("ifStatus", "ERR");
								retMsg = "근태정보 이관중 오류. 오류로그 확인";
								System.err.println("**TaaAppl reqDayErr " + reqDayMap.get("sabun").toString() + "/" + reqDayMap.get("sYmd").toString() + "~" + reqDayMap.get("eYmd").toString() + reqDayMap.get("retCode").toString());
								break;
							} else {
								// 오류가 아니면.. 근태시간을 생성체크하자
								String taaSetYn = reqDayMap.get("taaSetYn").toString();
								if("I".equals(taaSetYn)) {
									// 근태생성
									wtmFlexibleEmpService.addWtmDayResultInBaseTimeType(
											  Long.parseLong(reqDayMap.get("tenantId").toString())
											, reqDayMap.get("enterCd").toString()
											, ymd
											, reqDayMap.get("sabun").toString()
											, reqDayMap.get("timeTypeCd").toString()
											, reqDayMap.get("taaCd").toString()
											, dt.parse(reqDayMap.get("taaSdate").toString())
											, dt.parse(reqDayMap.get("taaEdate").toString())
											, Long.parseLong(reqDayMap.get("applId").toString())
											, "0");
								} else if ("D".equals(taaSetYn)) {
									// 근태삭제
									wtmFlexibleEmpService.removeWtmDayResultInBaseTimeType(
											  Long.parseLong(reqDayMap.get("tenantId").toString())
											, reqDayMap.get("enterCd").toString()
											, ymd
											, reqDayMap.get("sabun").toString()
											, reqDayMap.get("timeTypeCd").toString()
											, reqDayMap.get("taaCd").toString()
											, dt.parse(reqDayMap.get("taaSdate").toString())
											, dt.parse(reqDayMap.get("taaEdate").toString())
											, Long.parseLong(reqDayMap.get("applId").toString())
											, "0");
								}
								
								
								String chkYmd = nowDataTime.substring(0, 8);
								String enterCd = reqDayMap.get("enterCd").toString();
				        		String sabun = reqDayMap.get("sabun").toString();
				        		
				        		// 오늘 이전이면 근무마감을 다시 돌려야함.
								if (Integer.parseInt(chkYmd) > Integer.parseInt(ymd) && ("D".equals(taaSetYn) || "I".equals(taaSetYn))) {
					        		wtmFlexibleEmpService.calcApprDayInfo(tenantId, enterCd, ymd, ymd, sabun);
								}
								// 근무시간합산은 재정산한다
				        		HashMap<String, Object> setTermMap = new HashMap();
				        		setTermMap.put("tenantId", tenantId);
				        		setTermMap.put("enterCd", enterCd);
				        		setTermMap.put("sabun", sabun);
				        		setTermMap.put("symd", ymd);
				        		setTermMap.put("eymd", ymd);
				        		setTermMap.put("pId", "TAAIF");
				        		wtmFlexibleEmpMapper.createWorkTermBySabunAndSymdAndEymd(setTermMap);
							}
						}
						
						ifHisMap.put("ifStatus", "OK");
						retMsg = "근태신청서 처리완료";
					} else if("END".equals(retCode)) {
						ifHisMap.put("ifStatus", "OK");
						retMsg = reqMap.get("retMsg").toString();
					} else {
						//ifHisMap.put("ifStatus", "ERR");
						//retMsg = "프로시저 생성누락은 사유가 있어서 그래 무시해야함";
						System.err.println("**TaaAppl reqErr " + reqMap.get("sabun").toString() + "/" + reqMap.get("sYmd").toString() + "~" + reqMap.get("eYmd").toString() + reqMap.get("retCode").toString() + "/"+ reqMap.get("retMsg").toString());
						ifHisMap.put("ifStatus", "OK");
					}
				} catch(Exception e){
					ifHisMap.put("ifStatus", "ERR");
					retMsg = "TAA_RESULT set : 근태 이관오류";
		            e.printStackTrace();
		        }
    		} // end for
    	} else {
    		ifHisMap.put("ifStatus", "OK");
    		retMsg = "갱신자료없음";
    	}
		
    	// 3. 처리결과 저장
		try {
			// WTM_IF_HIS 테이블에 결과저장
			ifHisMap.put("updateDate", nowDataTime);
   			ifHisMap.put("ifEndDate", lastDataTime);
			ifHisMap.put("ifMsg", retMsg);
			wtmInterfaceMapper.insertIfHis(ifHisMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
    	
        System.out.println("WtmInterfaceServiceImpl setTaaApplBatchIf end");
		return;
	}
	
	@Transactional
	public void setTaaResult(WtmIfTaaHis data) throws Exception {
//		data.setIfStatus("test");
//		wtmIfTaaHisRepo.save(data);
//		throw new Exception("test");
		
		Map<String, Object> reqMap = new HashMap<>();
		reqMap.put("tenantId", data.getTenantId());
		reqMap.put("enterCd", data.getEnterCd());
		reqMap.put("sabun", data.getSabun());
		reqMap.put("taaCd", data.getWorkTimeCode());
		reqMap.put("sYmd", data.getStartYmd());
		reqMap.put("eYmd", data.getEndYmd());
		if(data.getStartHm() != null && !data.getStartHm().contentEquals("")) {
			reqMap.put("sHm", data.getStartHm());
		} else {
			reqMap.put("sHm", "");
		}
		if(data.getEndHm() != null && !data.getEndHm().contentEquals("")) {
			reqMap.put("eHm", data.getEndHm());
		} else {
			reqMap.put("eHm", "");
		}
		reqMap.put("ifApplNo", data.getApplNo());
		reqMap.put("status", data.getStatus());

		//받아올 데이터
		reqMap.put("retCode", "");
		reqMap.put("retMsg", "");
		reqMap.put("taaApplId", "");
		reqMap.put("applId", "");
		reqMap.put("oldStatus", "");
		wtmInterfaceMapper.setTaaApplIf(reqMap);
		System.out.println("setTaaApplBatchIfPostProcess reqMap " + reqMap.toString());
		
		String retCode = reqMap.get("retCode").toString();
		String oldStatusCd = "";
		if(reqMap.get("oldStatus") != null) { oldStatusCd = reqMap.get("oldStatus").toString();}
		
		if("OK".equals(retCode)) {
			//기간 루프
			String sYmd = reqMap.get("sYmd").toString();
			String eYmd = reqMap.get("eYmd").toString();
						
			SimpleDateFormat dt = new SimpleDateFormat("yyyyMMddHHmmss");
					
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			Date sDate = formatter.parse(sYmd);
			Date eDate = formatter.parse(eYmd);
				         
	        // 시간차이를 시간,분,초를 곱한 값으로 나누면 하루 단위가 나옴
	        long diff = eDate.getTime() - sDate.getTime();
	        long diffDays = (diff / (24 * 60 * 60 * 1000)) +1;
			for(int i=0; i<diffDays; i++) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(sDate);
				cal.add(Calendar.DATE, i);
				String ymd = formatter.format(cal.getTime());
				System.out.println("setTaaApplBatchIfPostProcess loop ymd : " + ymd);
				Map<String, Object> reqDayMap = reqMap;
				reqDayMap.put("ymd", ymd);
				reqDayMap.put("oldStatus", oldStatusCd);
				reqDayMap.put("retCode", "");
				reqDayMap.put("retMsg", "");
				reqDayMap.put("timeTypeCd", "");
				reqDayMap.put("taaSetYn", "");
				reqDayMap.put("taaSdate", "");
				reqDayMap.put("taaEdate", "");
				System.out.println("setTaaApplBatchIfPostProcess oldStatusCd : " + oldStatusCd);
				for ( String key : reqDayMap.keySet() ) {
        		    System.out.println("setTaaApplBatchIfPostProcess key : " + key +" / value : " + reqDayMap.get(key));
        		}
				//System.out.println("statusCd : " + reqDayMap.get("status"));
				wtmInterfaceMapper.setTaaApplDayIf(reqDayMap);
				
				String retDayCode = reqDayMap.get("retCode").toString();
				System.out.println("setTaaApplBatchIfPostProcess retMsg : " +  reqDayMap.get("retMsg").toString());
				if("FAIL".equals(retCode)) {
					// 오류다 ㅠㅠ
					System.err.println("setTaaApplBatchIfPostProcess **TaaAppl reqDayErr " + reqDayMap.get("sabun").toString() + "/" + reqDayMap.get("sYmd").toString() + "~" + reqDayMap.get("eYmd").toString() + reqDayMap.get("retCode").toString());
					throw new Exception("근태정보 이관중 오류. 오류로그 확인");
					//ifHisMap.put("ifStatus", "ERR");
					//retMsg = "근태정보 이관중 오류. 오류로그 확인";
					//break;
				} else {
					// 오류가 아니면.. 근태시간을 생성체크하자
					String taaSetYn = reqDayMap.get("taaSetYn").toString();
					if("I".equals(taaSetYn)) {
						// 근태생성
						wtmFlexibleEmpService.addWtmDayResultInBaseTimeType(
								  Long.parseLong(reqDayMap.get("tenantId").toString())
								, reqDayMap.get("enterCd").toString()
								, ymd
								, reqDayMap.get("sabun").toString()
								, reqDayMap.get("timeTypeCd").toString()
								, reqDayMap.get("taaCd").toString()
								, dt.parse(reqDayMap.get("taaSdate").toString())
								, dt.parse(reqDayMap.get("taaEdate").toString())
								, Long.parseLong(reqDayMap.get("applId").toString())
								, "0");
					} else if ("D".equals(taaSetYn)) {
						// 근태삭제
						wtmFlexibleEmpService.removeWtmDayResultInBaseTimeType(
								  Long.parseLong(reqDayMap.get("tenantId").toString())
								, reqDayMap.get("enterCd").toString()
								, ymd
								, reqDayMap.get("sabun").toString()
								, reqDayMap.get("timeTypeCd").toString()
								, reqDayMap.get("taaCd").toString()
								, dt.parse(reqDayMap.get("taaSdate").toString())
								, dt.parse(reqDayMap.get("taaEdate").toString())
								, Long.parseLong(reqDayMap.get("applId").toString())
								, "0");
					}
								
					String chkYmd = data.getIfYmdhis().substring(0, 8);
					String enterCd = reqDayMap.get("enterCd").toString();
	        		String sabun = reqDayMap.get("sabun").toString();
				        		
	        		// 오늘 이전이면 근무마감을 다시 돌려야함.
					if (Integer.parseInt(chkYmd) > Integer.parseInt(ymd) && ("D".equals(taaSetYn) || "I".equals(taaSetYn))) {
						wtmFlexibleEmpService.calcApprDayInfo(data.getTenantId(), enterCd, ymd, ymd, sabun);
					}
					// 근무시간합산은 재정산한다
	        		HashMap<String, Object> setTermMap = new HashMap();
	        		setTermMap.put("tenantId", data.getTenantId());
	        		setTermMap.put("enterCd", enterCd);
	        		setTermMap.put("sabun", sabun);
	        		setTermMap.put("symd", ymd);
	        		setTermMap.put("eymd", ymd);
	        		setTermMap.put("pId", "TAAIF");
				    wtmFlexibleEmpMapper.createWorkTermBySabunAndSymdAndEymd(setTermMap);
				}
			}
//						
//			ifHisMap.put("ifStatus", "OK");
//			retMsg = "근태신청서 처리완료";
		} else if("END".equals(retCode)) {			
			System.err.println("**TaaAppl reqErr " + reqMap.get("sabun").toString() + "/" + reqMap.get("sYmd").toString() + "~" + reqMap.get("eYmd").toString() + reqMap.get("retCode").toString() + "/"+ reqMap.get("retMsg").toString());

//			ifHisMap.put("ifStatus", "OK");
//			retMsg = reqMap.get("retMsg").toString();
		} else {
			//ifHisMap.put("ifStatus", "ERR");
			//retMsg = "프로시저 생성누락은 사유가 있어서 그래 무시해야함";
			System.err.println("**TaaAppl reqErr " + reqMap.get("sabun").toString() + "/" + reqMap.get("sYmd").toString() + "~" + reqMap.get("eYmd").toString() + reqMap.get("retCode").toString() + "/"+ reqMap.get("retMsg").toString());
			//ifHisMap.put("ifStatus", "OK");
		}
	}
	
	@Override
	public void setTaaApplBatchIfPostProcess(){
		System.out.println("setTaaApplBatchIfPostProcess");
		List<String> status = new ArrayList<String>();
		status.add("OK");
//		status.add("FAIL");
//		status.add("ERR");

//		List<String> enterCds = new ArrayList<String>();
//		enterCds.add("ISU_ABX");http://localhost/ifw/schedule/colseDay?tenantId=98
//		enterCds.add("ISU_AMC");
		List<WtmIfTaaHis> list = wtmIfTaaHisRepo.findByIfStatusNotIn(status);
//		List<WtmIfTaaHis> list = wtmIfTaaHisRepo.findByIfStatusNotInAndEnterCdNotIn(status, enterCds);
//		List<WtmIfTaaHis> list = wtmIfTaaHisRepo.findByTenantIdAndEnterCdAndIfStatusNotInGroupBy();
		if(list == null || list.size() == 0) {
			System.out.println("setTaaApplBatchIfPostProcess 대상없음 종료");
			return ;
		}
		System.out.println("setTaaApplBatchIfPostProcess 대상 " + list.size() + " 건");
		for(WtmIfTaaHis data : list) {
			try {
				setTaaResult(data);
				data.setIfStatus("OK");
	    	} catch (Exception e) {
	    		data.setIfStatus("FAIL");
	    		data.setIfMsg(e.getMessage());
			} finally {
				wtmIfTaaHisRepo.save(data);
			}
		}
    	
        System.out.println("setTaaApplBatchIfPostProcess end");
		return;
	}
	
	@Override
	public void setWorkTimeCloseIf(HashMap reqMap) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("WtmInterfaceServiceImpl setWorkTimeCloseIf");
		// 인터페이스 결과 저장용
    	String retMsg = null;
    	int resultCnt = 0;
    	String ifType = "WORKTIME_CLOSE";
    	Map<String, Object> ifHisMap = new HashMap<>();
    	ifHisMap.put("tenantId", reqMap.get("tenantId"));
    	ifHisMap.put("ifItem", ifType);
    	
    	// 인터페이스용 변수
    	String lastDataTime = null;
    	String nowDataTime = null;
    	HashMap<String, Object> getDateMap = null;
    	HashMap<String, Object> getIfMap = null;
    	List<Map<String, Object>> getIfList = null;
    	
    	// 최종 자료 if 시간 조회
    	try {
    		getDateMap = (HashMap<String, Object>) getIfLastDate((Long) reqMap.get("tenantId"), ifType);
    		lastDataTime = getDateMap.get("nowDate").toString();
    		nowDataTime = getDateMap.get("nowDate").toString();
        	
    	} catch(Exception e) {
    		retMsg = "WORKTIME_CLOSE get : 최종갱신일 조회오류";
    	}
    	
		// 2. 인터페이스 data 처리
		try {
			//String applStatusCd = reqMap.get("status").toString();
			// 호출이되면 근태신청 데이터를 저장하거나 결재상태를 갱신한다
			reqMap.put("retCode", "");
			reqMap.put("retMsg", "");
			wtmInterfaceMapper.setWorkTimeCloseIf(reqMap);
			String retCode = reqMap.get("retCode").toString();
			if("FAIL".equals(retCode)) {
				ifHisMap.put("ifStatus", "ERR");
				retMsg = reqMap.get("retMsg").toString();
			} else {
				ifHisMap.put("ifStatus", "OK");
				retMsg = reqMap.get("ym").toString() + " 근무마감 처리완료";
			}
		} catch(Exception e){
			ifHisMap.put("ifStatus", "ERR");
			retMsg = "TAA_RESULT set : 근태 이관오류";
            e.printStackTrace();
        }
		
    	// 3. 처리결과 저장
		try {
			// WTM_IF_HIS 테이블에 결과저장
			ifHisMap.put("updateDate", nowDataTime);
   			ifHisMap.put("ifEndDate", lastDataTime);
			ifHisMap.put("ifMsg", retMsg);
			wtmInterfaceMapper.insertIfHis(ifHisMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
        System.out.println("WtmInterfaceServiceImpl setTaaApplIf end");
		return;
	}
	
	/*
	 * 근무시간 마감 - JAVA 루프용
	 */
	@Override
	@Transactional
	@Async("threadPoolTaskExecutor")
	public void setCloseWorkIf(HashMap reqMap) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("WtmInterfaceServiceImpl setCloseWorkIf");
		// 인터페이스 결과 저장용
    	String retMsg = "";
    	int resultCnt = 0;
    	String ifType = "WORKTIME_CLOSE";
    	Map<String, Object> ifHisMap = new HashMap<>();
    	ifHisMap.put("tenantId", reqMap.get("tenantId"));
    	ifHisMap.put("ifItem", ifType);
    	
    	// 인터페이스용 변수
    	String lastDataTime = null;
    	String nowDataTime = null;
    	HashMap<String, Object> getDateMap = null;
    	HashMap<String, Object> getIfMap = null;
    	List<Map<String, Object>> getIfList = null;
    	
    	// 최종 자료 if 시간 조회
    	try {
    		getDateMap = (HashMap<String, Object>) getIfLastDate((Long) reqMap.get("tenantId"), ifType);
    		lastDataTime = getDateMap.get("nowDate").toString();
    		nowDataTime = getDateMap.get("nowDate").toString();
    	} catch(Exception e) {
    		retMsg = "WORKTIME_CLOSE get : 최종갱신일 조회오류";
    	}

		try {
			// 대상자를 가져와야함.
			List<Map<String, Object>> empList = null;
			System.out.println("************************* reqMap ** " + reqMap.toString());
			empList = wtmInterfaceMapper.getCloseEmp(reqMap);
			
			if(empList != null && empList.size() > 0) {
				// 일마감처리로 지각조퇴결근, 근무시간계산처리를 완료한다
				for(Map<String, Object> l : empList) {
					// 사원별 기간을 반복해야함
					String sYmd = reqMap.get("sYmd").toString();
					String eYmd = reqMap.get("eYmd").toString();
					
					SimpleDateFormat dt = new SimpleDateFormat("yyyyMMddHHmmss");
					SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			        Date sDate = formatter.parse(sYmd);
			        Date eDate = formatter.parse(eYmd);
			         
			        // 시간차이를 시간,분,초를 곱한 값으로 나누면 하루 단위가 나옴
			        long diff = eDate.getTime() - sDate.getTime();
			        long diffDays = (diff / (24 * 60 * 60 * 1000)) +1;
					for(int i=0; i<diffDays; i++) {
						Calendar cal = Calendar.getInstance();
						cal.setTime(sDate);
						cal.add(Calendar.DATE, i);
						String ymd = formatter.format(cal.getTime());
						
						Map<String, Object> dayMap = reqMap;
						dayMap.put("retCode", "");
						dayMap.put("retMsg", "");
						dayMap.put("sabun", l.get("sabun").toString());
						dayMap.put("ymd", ymd);
						
						// System.out.println("******************************* dayMap");
						// System.out.println(dayMap.toString());
						
						wtmInterfaceMapper.setCloseDay(dayMap);
						String retCode = dayMap.get("retCode").toString();
						if("FAIL".equals(retCode)) {
							ifHisMap.put("ifStatus", "ERR");
							retMsg = dayMap.get("retMsg").toString();
							System.out.println("******************************* SP CALL DAY FAIL" + retMsg);
							break;
						}
					}
					System.out.println("******************************* start month chk" + retMsg);
					if("".equals(retMsg)) {
						// 기간반복이 종료되었으니깐 월마감을 돌려야함...
						Map<String, Object> monMap = reqMap;
						monMap.put("retCode", "");
						monMap.put("retMsg", "");
						monMap.put("sabun", l.get("sabun").toString());
						//System.out.println("******************************* monMap");
						System.out.println(monMap.toString());
						
						wtmInterfaceMapper.setCloseMonth(monMap);
						String retCodeMon = monMap.get("retCode").toString();
						if("FAIL".equals(retCodeMon)) {
							ifHisMap.put("ifStatus", "ERR");
							retMsg = monMap.get("retMsg").toString();
							System.out.println("******************************* SP CALL MONTH FAIL" + retMsg);
							break;
						}
						
						// 월마감에서 근태마감시키고.....
					}
				}
			}
			
			// 마감이 다 돌았는으면 보상휴가생성으로 넘어가자
		} catch(Exception e){
			ifHisMap.put("ifStatus", "ERR");
			retMsg = "근무마감오류";
            e.printStackTrace();
        }
		
    	// 3. 처리결과 저장
		try {
			// WTM_IF_HIS 테이블에 결과저장
			ifHisMap.put("updateDate", nowDataTime);
   			ifHisMap.put("ifEndDate", lastDataTime);
			ifHisMap.put("ifMsg", retMsg);
			wtmInterfaceMapper.insertIfHis(ifHisMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
        System.out.println("WtmInterfaceServiceImpl setTaaApplIf end");
		return;
	}
	
	@Override
	@Transactional
	@Async("threadPoolTaskExecutor")
	public void setCalcDay(Long tenantId) throws Exception {
		List<Map<String, Object>> dataList = new ArrayList();
		String userId = "1";
		dataList = wtmInterfaceMapper.setCalcDay(tenantId);
		// dataList = wtmInterfaceMapper.setCalcDayResult(tenantId);
		
		if(dataList != null && dataList.size() > 0) {
			// 일마감처리로 지각조퇴결근, 근무시간계산처리를 완료한다
			for(Map<String, Object> l : dataList) {
				// RESULT 생성하기
				l.put("tenantId", tenantId);
				
				String enterCd = l.get("enterCd").toString();
        		String sabun = l.get("sabun").toString();
        		String ymd = l.get("ymd").toString();
        		Integer gooutCnt = Integer.parseInt(l.get("gooutCnt").toString());
        		System.out.println("********** sabun : " + sabun + ", ymd : " + ymd + ", gooutCnt : " + gooutCnt);
        		l.put("symd", l.get("ymd").toString());
				l.put("eymd", l.get("ymd").toString());
				l.put("sYmd", l.get("ymd").toString());
				l.put("eYmd", l.get("ymd").toString());
				l.put("pId", userId);
				l.put("userId", userId);
				
				// wtmFlexibleEmpMapper.resetNoPlanWtmWorkDayResultByFlexibleEmpIdWithFixOt(l);
				
        		// 브로제 외출복귀 있으면 create result 호출하고 마감돌려야함.
				/*
        		if(gooutCnt > 0) {
        			// create result 호출
        			wtmFlexibleEmpMapper.resetNoPlanWtmWorkDayResultByFlexibleEmpIdWithFixOt(l);
        			
        			// 외출횟수만큼 근무시간을 짤라야함 외출정보를 조회하자
        			List<Map<String, Object>> goOutList = new ArrayList();
        			goOutList = wtmInterfaceMapper.setCalcDayResult(l);
        			if(goOutList != null && goOutList.size() > 0) {
        				for(Map<String, Object> f : goOutList) {
        					System.out.println("goout send: " + f.toString());
		        			SimpleDateFormat dt = new SimpleDateFormat("yyyyMMddHHmmss");
		    				wtmFlexibleEmpService.addWtmDayResultInBaseTimeType(
		    						  tenantId
		    						, enterCd
		    						, ymd
		    						, sabun
		    						, f.get("timeTypeCd").toString()
		    						, ""
		    						, dt.parse(f.get("planSdate").toString())
		    						, dt.parse(f.get("planEdate").toString())
		    						, null
		    						, "0"
		    						, false);
        				}
        			}
        		}
				*/
				/*
				l.put("shm", l.get("planSdate").toString().substring(8,12));
				l.put("ehm", l.get("planEdate").toString().substring(8,12));
				
				Map<String, Object> planMinuteMap = wtmFlexibleEmpService.calcMinuteExceptBreaktime(Long.parseLong(l.get("timeCdMgrId").toString()), l, userId);
				l.put("planMinute", (Integer.parseInt(planMinuteMap.get("calcMinute")+"")));
				wtmInterfaceMapper.insertDayResult(l);
				*/
				// wtmInterfaceMapper.updateDayResult2(l);
        		
        		// 일마감생성
        		//wtmFlexibleEmpService.calcApprDayInfo(tenantId, enterCd, ymd, ymd, sabun);
        		
        		// 문제가 없으면 근무계획시간 합산
				wtmFlexibleEmpMapper.createWorkTermBySabunAndSymdAndEymd(l);
				System.out.println("********** sabun : " + sabun + ", ymd : " + ymd + " end");
			}
			System.out.println("********** ALL END");
		}
		return;
	}
	
	@Override
	@Transactional
	@Async("threadPoolTaskExecutor")
	public void setCalcDayParam(Long tenantId, String enterCd, String sabun, String sYmd, String eYmd) throws Exception {
		List<Map<String, Object>> dataList = new ArrayList();
		String userId = "1";
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		paramMap.put("sYmd", sYmd);
		paramMap.put("eYmd", eYmd);
		
		dataList = wtmInterfaceMapper.setCalcDayParam(paramMap);
		// dataList = wtmInterfaceMapper.setCalcDayResult(tenantId);
		
		if(dataList != null && dataList.size() > 0) {
			// 일마감처리로 지각조퇴결근, 근무시간계산처리를 완료한다
			for(Map<String, Object> l : dataList) {
				// RESULT 생성하기
				l.put("tenantId", tenantId);
				
        		String ymd = l.get("ymd").toString();
        		Integer gooutCnt = Integer.parseInt(l.get("gooutCnt").toString());
        		System.out.println("********** sabun : " + sabun + ", ymd : " + ymd + ", gooutCnt : " + gooutCnt);
        		l.put("symd", l.get("ymd").toString());
				l.put("eymd", l.get("ymd").toString());
				l.put("sYmd", l.get("ymd").toString());
				l.put("eYmd", l.get("ymd").toString());
				l.put("pId", userId);
				l.put("userId", userId);
				
				// 임시 RESULT 생성
				// wtmFlexibleEmpMapper.resetNoPlanWtmWorkDayResultByFlexibleEmpIdWithFixOt(l);
				
        		// 브로제 외출복귀 있으면 create result 호출하고 마감돌려야함.
        		if(gooutCnt > 0) {
        			// create result 호출
        			System.out.println("goout!!!");
        			wtmFlexibleEmpMapper.resetNoPlanWtmWorkDayResultByFlexibleEmpIdWithFixOt(l);
        			
        			// 외출횟수만큼 근무시간을 짤라야함 외출정보를 조회하자
        			List<Map<String, Object>> goOutList = new ArrayList();
        			goOutList = wtmInterfaceMapper.setCalcDayResult(l);
        			if(goOutList != null && goOutList.size() > 0) {
        				for(Map<String, Object> f : goOutList) {
        					System.out.println("goout send: " + f.toString());
		        			SimpleDateFormat dt = new SimpleDateFormat("yyyyMMddHHmmss");
		        			wtmFlexibleEmpService.addWtmDayResultInBaseTimeType(
		    						  tenantId
		    						, enterCd
		    						, ymd
		    						, sabun
		    						, f.get("timeTypeCd").toString()
		    						, ""
		    						, dt.parse(f.get("planSdate").toString())
		    						, dt.parse(f.get("planEdate").toString())
		    						, null
		    						, "0"
		    						, false);
        				}
        			}
        		}
				/*
				l.put("shm", l.get("planSdate").toString().substring(8,12));
				l.put("ehm", l.get("planEdate").toString().substring(8,12));
				
				Map<String, Object> planMinuteMap = wtmFlexibleEmpService.calcMinuteExceptBreaktime(Long.parseLong(l.get("timeCdMgrId").toString()), l, userId);
				l.put("planMinute", (Integer.parseInt(planMinuteMap.get("calcMinute")+"")));
				wtmInterfaceMapper.insertDayResult(l);
				*/
				// wtmInterfaceMapper.updateDayResult2(l);
        		
        		// 일마감생성
        		wtmFlexibleEmpService.calcApprDayInfo(tenantId, enterCd, ymd, ymd, sabun);        		
        		// 문제가 없으면 근무계획시간 합산
				wtmFlexibleEmpMapper.createWorkTermBySabunAndSymdAndEymd(l);
				System.out.println("********** sabun : " + sabun + ", ymd : " + ymd + " end");
			}
			System.out.println("********** ALL END");
		}
		return;
	}
	
	@Override
	public List<Map<String, Object>> getCalcDayLoopEmp(Long tenantId) {
		List<Map<String, Object>> dataList = new ArrayList();
		
		try {
			dataList = wtmInterfaceMapper.getCalcDayLoopEmp(tenantId);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return dataList;
	}
	
	@Override
	@Transactional
	@Async("threadPoolTaskExecutor")
	public void setCalcDayLoop(Long tenantId, String enterCd, String sabun, String sYmd, String eYmd) throws Exception {
		List<Map<String, Object>> dataList = new ArrayList();
		String userId = "1";
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		paramMap.put("sYmd", sYmd);
		paramMap.put("eYmd", eYmd);
		
		// 일단 result를 지우고
		// wtmInterfaceMapper.deleteCalcDayLoop(paramMap);
		
		dataList = wtmInterfaceMapper.setCalcDayLoop(paramMap);
		// dataList = wtmInterfaceMapper.setCalcDayResult(tenantId);
		
		if(dataList != null && dataList.size() > 0) {
			// 일마감처리로 지각조퇴결근, 근무시간계산처리를 완료한다
			for(Map<String, Object> l : dataList) {
				// RESULT 생성하기
				l.put("tenantId", tenantId);
				
        		String ymd = l.get("ymd").toString();
        		Integer gooutCnt = Integer.parseInt(l.get("gooutCnt").toString());
        		System.out.println("********** sabun : " + sabun + ", ymd : " + ymd + ", gooutCnt : " + gooutCnt);
        		l.put("symd", l.get("ymd").toString());
				l.put("eymd", l.get("ymd").toString());
				l.put("sYmd", l.get("ymd").toString());
				l.put("eYmd", l.get("ymd").toString());
				l.put("pId", userId);
				l.put("userId", userId);
				
        		// 브로제 외출복귀 있으면 create result 호출하고 마감돌려야함.
        		if(gooutCnt > 0) {
        			// create result 호출
        			wtmFlexibleEmpMapper.resetNoPlanWtmWorkDayResultByFlexibleEmpIdWithFixOt(l);
        			
        			// 외출횟수만큼 근무시간을 짤라야함 외출정보를 조회하자
        			List<Map<String, Object>> goOutList = new ArrayList();
        			goOutList = wtmInterfaceMapper.setCalcDayResult(l);
        			if(goOutList != null && goOutList.size() > 0) {
        				for(Map<String, Object> f : goOutList) {
        					System.out.println("goout send: " + f.toString());
		        			SimpleDateFormat dt = new SimpleDateFormat("yyyyMMddHHmmss");
		        			wtmFlexibleEmpService.addWtmDayResultInBaseTimeType(
		    						  tenantId
		    						, enterCd
		    						, ymd
		    						, sabun
		    						, f.get("timeTypeCd").toString()
		    						, ""
		    						, dt.parse(f.get("planSdate").toString())
		    						, dt.parse(f.get("planEdate").toString())
		    						, null
		    						, "0"
		    						, false);
        				}
        			}
        		}
        		
        		// 일마감생성
        		wtmFlexibleEmpService.calcApprDayInfo(tenantId, enterCd, ymd, ymd, sabun);
        		
        		// 문제가 없으면 근무계획시간 합산
				wtmFlexibleEmpMapper.createWorkTermBySabunAndSymdAndEymd(l);
				System.out.println("********** sabun : " + sabun + ", ymd : " + ymd + " end");
			}
			System.out.println("********** ALL END");
		}
		return;
	}
	
	@Override
	public void getDataExp(HashMap reqMap) throws Exception {
		List<Map<String, Object>> tableList = new ArrayList();
		List<Map<String, Object>> colList = new ArrayList();
		List<Map<String, Object>> dataList = new ArrayList();
		
		reqMap.put("tableSchema", "worktimemanagement");
		
		tableList = wtmInterfaceMapper.getExpTableList(reqMap);
		if(tableList != null && tableList.size() > 0) {
			// 테이블 루프
			for(int i=0; i<tableList.size(); i++) {
				String tableName = tableList.get(i).get("tableName").toString();
				System.out.println("-- tableName : " + tableName + " start!");
				reqMap.put("tableName", tableName);
				colList = wtmInterfaceMapper.getExpColList(reqMap);
				
				String InsertStr = "";
				if(colList != null && colList.size() > 0) {
					InsertStr = "INSERT INTO " + tableName + "(";
					for(int j=0; j<colList.size(); j++) {
						if("1".equals(colList.get(j).get("colNo").toString())) {
							InsertStr += colList.get(j).get("colNm").toString();
						} else {
							InsertStr += ", " + colList.get(j).get("colNm").toString(); 
						}
					}
					InsertStr += ")"; 
					// System.out.println("-- collist InsertStr : " + InsertStr);
				} else {
					System.out.println("tableName : " + tableName + "no column");
				}
				dataList = wtmInterfaceMapper.getExpDataList(reqMap);
				if(dataList != null && dataList.size() > 0) {
					String dataStr = "";
					for(int x=0; x<dataList.size(); x++) {
						dataStr = "SELECT ";
						for(int j=0; j<colList.size(); j++) {
							if(j > 0) {dataStr += ", ";}
							
							String colNm = colList.get(j).get("colNm").toString();
							String colType = colList.get(j).get("dataType").toString();
							String dataVal = "";
							if(dataList.get(x).get(colNm) == null || "".equals(dataList.get(x).get(colNm).toString().trim())){
								dataStr += " null ";
							} else {
								if("bigint".equals(colType) || "smallint".equals(colType) || "int".equals(colType)) {
									dataStr += dataList.get(x).get(colNm).toString() + " ";
								} else if ("datetime".equals(colType)) {
									// '2019-09-20 15:48:59.0' 로 넘어오니깐 data 맞추자
									if(dataVal.length() > 19) {dataVal = dataVal.substring(0, 19);}
									
									dataStr += "TO_DATE('" + dataVal + "', 'YYYY-MM-DD HH24:MI:SS') ";
								} else {
									dataStr += "'" + dataList.get(x).get(colNm).toString() + "'";
								}
							}
						}
						
						dataStr += " FROM DUAL;";
						System.out.println(InsertStr + dataStr);
					}
				}
			}
			
		} else {
			System.out.println("nodata !!!!");
		}
		
	}
	
	@Override
	@Transactional
	@Async("threadPoolTaskExecutor")
	public void setCloseDay(Long tenantId) throws Exception {
		
		// 인터페이스용 변수
		String ifType = "dayClose";
    	String nowDataTime = null;
		HashMap<String, Object> getDateMap = null;
		
		getDateMap = (HashMap<String, Object>) getIfLastDate(tenantId, ifType);
    	nowDataTime = getDateMap.get("nowDate").toString();
    		
    	String ymd = nowDataTime.substring(0, 8);
    	System.out.println("********** ymd : " + ymd);
    	// 마감구분 A:자정(당일퇴근자 마감), B:익일4시(익일심야근무 퇴근자 마감)
    	String closeType = "A";
    	if("04".equals(nowDataTime.substring(8, 10))) {
    		closeType = "B";
    	}
    	
    	getDateMap = new HashMap();
    	// ymd = "20200203";
    	getDateMap.put("tenantId", tenantId);
    	getDateMap.put("ymd", ymd);
    	getDateMap.put("closeType", closeType);
    	
    	// DB로 타각 갱신부터 처리한다.
    	// wtmInterfaceMapper.setCloseEntryOut(getDateMap);
    	
    	// 타각갱신이 완료되면, 출퇴근 기록완성자의 근무시간을 갱신해야한다.
		List<Map<String, Object>> closeList = new ArrayList();
		closeList = wtmInterfaceMapper.getWtmCloseDay(getDateMap);
		
		if(closeList != null && closeList.size() > 0) {
			// 일마감처리로 지각조퇴결근, 근무시간계산처리를 완료한다
			for(int i=0; i<closeList.size(); i++) {
        		String enterCd = closeList.get(i).get("enterCd").toString();
        		String sabun = closeList.get(i).get("sabun").toString();
        		String closeYmd = closeList.get(i).get("ymd").toString();
        		System.out.println("********** sabun : " + sabun + ", ymd : " + closeYmd);
        		wtmFlexibleEmpService.calcApprDayInfo(tenantId, enterCd, closeYmd, closeYmd, sabun);
        		
        		HashMap<String, Object> setTermMap = new HashMap();
        		setTermMap.put("tenantId", tenantId);
        		setTermMap.put("enterCd", enterCd);
        		setTermMap.put("sabun", sabun);
        		setTermMap.put("symd", closeYmd);
        		setTermMap.put("eymd", closeYmd);
        		setTermMap.put("pId", "DAYCLOSE");
        		wtmFlexibleEmpMapper.createWorkTermBySabunAndSymdAndEymd(setTermMap);
        		
			}
		}
	}
	
	@Override
	@Transactional
	@Async("threadPoolTaskExecutor")
	public void sendCompCnt(HashMap reqMap) throws Exception {
		Long tenantId = Long.parseLong(reqMap.get("tenantId").toString());
		List<Map<String, Object>> dataList = new ArrayList();
		dataList = wtmInterfaceMapper.getCompList(reqMap);
		Map<String, Object> parmaMap = new HashMap();
		parmaMap.put("tenantId", tenantId);
		
    	// String ifUrl = setIfUrl(tenantId, "/code", rp);
		String ifUrl = "";
		Map<String, Object> result = wtmInterfaceMapper.getIfUrl(parmaMap);
		
		if(result != null && result.size() > 0) {
			try {
				System.out.println("info_data : " + result.get("infoData").toString());
				
				ifUrl = result.get("infoData").toString() + "/workTimeClose";
				System.out.println("ifUrl : " + ifUrl);
				parmaMap.put("compList", dataList);
				HashMap<String, Object> getIfMap = null;
				try {
		        	RestTemplate restTemplate = new RestTemplate();
		        	System.out.println(parmaMap.toString());
			   		getIfMap = (HashMap<String, Object>) restTemplate.postForObject(ifUrl, parmaMap, Map.class);
			   		System.out.println(getIfMap.toString());
				} catch (Exception e) {
		            e.printStackTrace();
				}
				
			} catch(Exception e){
	            e.printStackTrace();
	        }
		}
		return;
	}
	
	

	@Override
	public void saveCodeIntf(Long tenantId, List<Map<String, Object>> dataList) {
		if(dataList != null && dataList.size() > 0 ) {
			List<WtmIntfCode> codes = new ArrayList<WtmIntfCode>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			sdf.format(new Date());
			String yyyymmddhhmiss= sdf.format(new Date());
			
			for(Map<String, Object> m : dataList) {
				WtmIntfCode code = new WtmIntfCode();
				code.setYyyymmddhhmiss(yyyymmddhhmiss);
				code.setTenantId(tenantId);
				code.setEnterCd(m.get("ENTER_CD")+"");
				code.setGrpCodeCd(m.get("GRCODE_CD")+"");
				code.setCodeCd(m.get("CODE")+"");
				code.setCodeNm(m.get("CODE_NM")+"");
				code.setSeq((m.get("SEQ") != null && m.get("SEQ")!="")?Integer.parseInt(m.get("SEQ")+""):null);
				code.setSymd(m.get("SYMD")+"");
				code.setEymd(m.get("EYMD")+"");
				code.setNote(m.get("NOTE")+"");
				codes.add(code);
			} 

			wtmCodeIntfRepo.saveAll(codes); 
				
	   		
		}
	}

	@Override
	public void saveEmpIntf(Long tenantId, List<Map<String, Object>> dataList) {
		if(dataList != null && dataList.size() > 0 ) {
			List<WtmIntfEmp> datas = new ArrayList<WtmIntfEmp>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			sdf.format(new Date());
			String yyyymmddhhmiss= sdf.format(new Date());
			/*
			ENTER_CD, SABUN, EMP_NM, EMP_ENG_NM, SYMD, EYMD, STATUS_CD, ORG_CD, LOCATION_CD
		     , DUTY_CD, POS_CD, CLASS_CD, JOB_GROUP_CD, JOB_CD, PAY_TYPE_CD, LEADER_YN
		     */
			for(Map<String, Object> m : dataList) {
				WtmIntfEmp d = new WtmIntfEmp();
			
				d.setYyyymmddhhmiss(yyyymmddhhmiss);
				d.setTenantId(tenantId);
				d.setEnterCd(m.get("ENTER_CD")+"");
				d.setSabun(m.get("SABUN")+"");
				d.setEmpNm(m.get("EMP_NM")+"");
				d.setEmpEngNm(m.get("EMP_ENG_NM")+"");
				d.setSymd(m.get("SYMD")+"");
				d.setEymd(m.get("EYMD")+"");
				d.setStatusCd(m.get("STATUS_CD")+"");
				d.setOrgCd(m.get("ORG_CD")+"");
				d.setLocationCd(m.get("LOCATION_CD")+"");
				d.setDutyCd(m.get("DUTY_CD")+"");
				d.setPosCd(m.get("POS_CD")+"");
				d.setClassCd(m.get("CLASS_CD")+"");
				d.setJobGroupCd(m.get("JOB_GROUP_CD")+"");
				d.setJobCd(m.get("JOB_CD")+"");
				d.setPayTypeCd(m.get("PAY_TYPE_CD")+"");
				d.setLeaderYn(m.get("LEADER_YN")+"");
				datas.add(d);
			} 
			wtmEmpIntfRepo.saveAll(datas); 
		}
	}

	@Override
	public void saveEmpAddrIntf(Long tenantId, List<Map<String, Object>> dataList) {
		if(dataList != null && dataList.size() > 0 ) {
			List<WtmIntfEmpAddr> datas = new ArrayList<WtmIntfEmpAddr>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			sdf.format(new Date());
			String yyyymmddhhmiss= sdf.format(new Date());
			/* 
		     */
			for(Map<String, Object> m : dataList) {
				WtmIntfEmpAddr d = new WtmIntfEmpAddr();
				//m.get("")+""
				d.setYyyymmddhhmiss(yyyymmddhhmiss);
				d.setTenantId(tenantId);
				d.setEnterCd(m.get("ENTER_CD")+"");
				d.setSabun(m.get("SABUN")+"");
				d.setEmail(m.get("EMAIL")+"");
				d.setPhone(m.get("PHONE")+"");
				d.setNote(m.get("NOTE")+"");

				datas.add(d);
			} 
			wtmEmpAddrIntfRepo.saveAll(datas); 
		}
	}

	@Override
	public void saveGntIntf(Long tenantId, List<Map<String, Object>> dataList) {
		if(dataList != null && dataList.size() > 0 ) {
			List<WtmIntfGnt> datas = new ArrayList<WtmIntfGnt>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			sdf.format(new Date());
			String yyyymmddhhmiss= sdf.format(new Date());
			/*
			ENTER_CD, GNT_CD, GNT_NM, GNT_GUBUN_CD, HOL_INCL_YN, REQ_USE_TYPE, WORK_YN, PAY_YN, NOTE
		     */
			for(Map<String, Object> m : dataList) {
				WtmIntfGnt d = new WtmIntfGnt();
				//m.get("")+""
				d.setYyyymmddhhmiss(yyyymmddhhmiss);
				d.setTenantId(tenantId);
				d.setEnterCd(m.get("ENTER_CD")+"");
				d.setGntCd(m.get("GNT_CD")+"");
				d.setGntNm(m.get("GNT_NM")+"");
				d.setGntGubunCd(m.get("GNT_GUBUN_CD")+"");
				d.setHolInclYn(m.get("HOL_INCL_YN")+"");
				d.setReqUseType(m.get("REQ_USE_TYPE")+"");
				d.setWorkYn(m.get("WORK_YN")+"");
				d.setPayYn(m.get("PAY_YN")+"");
				d.setNote(m.get("NOTE")+"");

				datas.add(d);
			} 
			wtmGntIntfRepo.saveAll(datas); 
		}
		
	}

	@Override
	public void saveHolidayIntf(Long tenantId, List<Map<String, Object>> dataList) {
		if(dataList != null && dataList.size() > 0 ) {
			List<WtmIntfHoliday> datas = new ArrayList<WtmIntfHoliday>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			sdf.format(new Date());
			String yyyymmddhhmiss= sdf.format(new Date());
			/*
			ENTER_CD, LOCATION_CD, YMD, HOLIDAY_NM, FESTIVE_YN, PAY_YN
		     */
			for(Map<String, Object> m : dataList) {
				WtmIntfHoliday d = new WtmIntfHoliday();
				//m.get("")+""
				d.setYyyymmddhhmiss(yyyymmddhhmiss);
				d.setTenantId(tenantId);
				d.setEnterCd(m.get("ENTER_CD")+"");
				d.setLocationCd(m.get("LOCATION_CD")+"");
				d.setYmd(m.get("YMD")+"");
				d.setHolidayNm(m.get("HOLIDAY_NM")+"");
				d.setFestiveYn(m.get("FESTIVE_YN")+"");
				d.setPayYn(m.get("PAY_YN")+"");
				d.setNote(m.get("NOTE")+"");

				datas.add(d);
			} 
			wtmHolidayIntfRepo.saveAll(datas); 
		}
		
	}

	@Override
	public void saveOrgIntf(Long tenantId, List<Map<String, Object>> dataList) {
		if(dataList != null && dataList.size() > 0 ) {
			List<WtmIntfOrg> datas = new ArrayList<WtmIntfOrg>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			sdf.format(new Date());
			String yyyymmddhhmiss= sdf.format(new Date());
			/*
			ENTER_CD, ORG_CD, ORG_NM,  PRIOR_ORG_CD, SEQ, ORG_LEVEL, SYMD, EYMD, ORG_TYPE, NOTE
		     */
			for(Map<String, Object> m : dataList) {
				WtmIntfOrg d = new WtmIntfOrg();
				//m.get("")+""
				d.setYyyymmddhhmiss(yyyymmddhhmiss);
				d.setTenantId(tenantId);
				d.setEnterCd(m.get("ENTER_CD")+"");
				d.setOrgCd(m.get("ORG_CD")+"");
				d.setOrgNm(m.get("ORG_NM")+"");
				d.setPriorOrgCd(m.get("PRIOR_ORG_CD")+"");
				d.setSeq(m.get("SEQ")+"");
				d.setOrgLevel(m.get("ORG_LEVEL")+"");
				d.setSymd(m.get("SYMD")+"");
				d.setEymd(m.get("EYMD")+"");
				d.setOrgType(m.get("ORG_TYPE")+"");
				d.setNote(m.get("NOTE")+"");

				datas.add(d);
			} 
			wtmOrgIntfRepo.saveAll(datas); 
		}
		
	}

	@Override
	public void saveOrgConcIntf(Long tenantId, List<Map<String, Object>> dataList) {
		if(dataList != null && dataList.size() > 0 ) {
			List<WtmIntfOrgConc> datas = new ArrayList<WtmIntfOrgConc>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			sdf.format(new Date());
			String yyyymmddhhmiss= sdf.format(new Date());
			/*
			ENTER_CD, ORG_CD, ORG_NM,  PRIOR_ORG_CD, SEQ, ORG_LEVEL, SYMD, EYMD, ORG_TYPE, NOTE
		     */
			for(Map<String, Object> m : dataList) {
				WtmIntfOrgConc d = new WtmIntfOrgConc();
				//m.get("")+""
				d.setYyyymmddhhmiss(yyyymmddhhmiss);
				d.setTenantId(tenantId);
				d.setEnterCd(m.get("ENTER_CD")+"");
				d.setOrgCd(m.get("ORG_CD")+"");
				d.setSabun(m.get("SABUN")+"");
				d.setSymd(m.get("SYMD")+"");
				d.setEymd(m.get("EYMD")+"");
				d.setNote(m.get("NOTE")+"");

				datas.add(d);
			} 
			wtmOrgConcIntfRepo.saveAll(datas); 
		}
		
	}

	@Override
	public void saveOrgChartIntf(Long tenantId, List<Map<String, Object>> dataList) {
		if(dataList != null && dataList.size() > 0 ) {
			List<WtmIntfOrgChart> datas = new ArrayList<WtmIntfOrgChart>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			sdf.format(new Date());
			String yyyymmddhhmiss= sdf.format(new Date());
			/*
			ENTER_CD, ORG_CD, ORG_NM,  PRIOR_ORG_CD, SEQ, ORG_LEVEL, SYMD, EYMD, ORG_TYPE, NOTE
		     */
			for(Map<String, Object> m : dataList) {
				WtmIntfOrgChart d = new WtmIntfOrgChart();
				//m.get("")+""
				d.setYyyymmddhhmiss(yyyymmddhhmiss);
				d.setTenantId(tenantId);
				d.setEnterCd(m.get("ENTER_CD")+"");
				d.setOrgCd(m.get("ORG_CD")+"");
				d.setOrgNm(m.get("ORG_NM")+"");
				d.setPriorOrgCd(m.get("PRIOR_ORG_CD")+"");
				d.setSeq(m.get("SEQ")+"");
				d.setOrgLevel(m.get("ORG_LEVEL")+"");
				d.setSymd(m.get("SYMD")+"");
				d.setEymd(m.get("EYMD")+"");
				d.setOrgType(m.get("ORG_TYPE")+"");
				d.setNote(m.get("NOTE")+"");
				datas.add(d);
			} 
			
			wtmOrgChartIntfRepo.saveAll(datas); 
		}
		
	}
	
	@Override
	public void saveTaaApplIntf(Long tenantId, List<Map<String, Object>> dataList) {
		if(dataList != null && dataList.size() > 0 ) {
			List<WtmIntfTaaAppl> datas = new ArrayList<WtmIntfTaaAppl>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			sdf.format(new Date());
			String yyyymmddhhmiss= sdf.format(new Date());
			/*
			ENTER_CD, ORG_CD, ORG_NM,  PRIOR_ORG_CD, SEQ, ORG_LEVEL, SYMD, EYMD, ORG_TYPE, NOTE
		     */
			for(Map<String, Object> m : dataList) {
				WtmIntfTaaAppl d = new WtmIntfTaaAppl();
				//m.get("")+""
				d.setYyyymmddhhmiss(yyyymmddhhmiss);
				d.setTenantId(tenantId);
				d.setEnterCd(m.get("ENTER_CD")+"");
				d.setSabun(m.get("SABUN")+"");
				d.setGntCd(m.get("GNT_CD")+"");
				d.setSymd(m.get("SYMD")+"");
				d.setEymd(m.get("EYMD")+"");
				d.setShm(m.get("SHM")+"");
				d.setEhm(m.get("EHM")+"");
				d.setApplSeq(m.get("APPL_SEQ")+"");
				d.setApplStatusCd(m.get("APPL_STATUS_CD")+"");
				d.setNote(m.get("NOTE")+"");

				datas.add(d);
			} 
			wtmTaaIntfRepo.saveAll(datas); 
		}
		
	}
	
	@Override
	public void setTempHj(HashMap reqMap) throws Exception {
		// TODO Auto-generated method stub
		/*
		String ifUrl = "";
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("tenantId", reqMap.get("tenantId"));
		List<Map<String, Object>> dataList = new ArrayList();
		String userId = "1";
		dataList = wtmInterfaceMapper.setCalcDay(Long.parseLong(reqMap.get("tenantId").toString()));
		if(dataList != null && dataList.size() > 0) {
			// 일마감처리로 지각조퇴결근, 근무시간계산처리를 완료한다
			for(Map<String, Object> l : dataList) {
				ifUrl = l.get("callUrl").toString();
				System.out.println("ifUrl : " + ifUrl);
				getIfRt(ifUrl);
			}
		}
		*/
		List<Map<String, Object>> dataList = new ArrayList();
		dataList = wtmInterfaceMapper.setCalcDay(Long.parseLong(reqMap.get("tenantId").toString()));
		if(dataList != null && dataList.size() > 0) {
			// 일마감처리로 지각조퇴결근, 근무시간계산처리를 완료한다
			for(Map<String, Object> l : dataList) {
				try {
					Map<String, Object> paramMap = new HashMap();
					paramMap.put("tenantId", Long.parseLong(l.get("tenantId").toString()));
					paramMap.put("enterCd", l.get("enterCd").toString());
					paramMap.put("sabun", l.get("emp").toString());
					paramMap.put("inoutDate", l.get("time").toString());
			//         paramMap.put("ymd", request.getParameter("ymd"));
					paramMap.put("inoutType", l.get("type").toString());
					paramMap.put("entryType", "INTF");	
			  System.out.println("paramMap : " + paramMap);
					inoutService.updateCalendar(paramMap);
					//퇴근일때만 인정시간 계산
					inoutService.inoutPostProcess(paramMap);
				} catch(Exception e) {
					// e.printStackTrace();
					System.out.println("paramMap : " + e.getMessage());
					continue;
				}
			}
		}
	}


	@Override
	@Transactional
	public ReturnParam setCloseWorkIfN(HashMap reqMap) {
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");

		// TODO Auto-generated method stub
		System.out.println("WtmInterfaceServiceImpl setCloseWorkIf");
		// 인터페이스 결과 저장용
		String retMsg = "";
		int resultCnt = 0;
		String ifType = "WORKTIME_CLOSE";
		Map<String, Object> ifHisMap = new HashMap<>();
		ifHisMap.put("tenantId", reqMap.get("tenantId"));
		ifHisMap.put("ifItem", ifType);

		// 인터페이스용 변수
		String lastDataTime = null;
		String nowDataTime = null;
		HashMap<String, Object> getDateMap = null;
		HashMap<String, Object> getIfMap = null;
		List<Map<String, Object>> getIfList = null;

		// 최종 자료 if 시간 조회
		try {
			getDateMap = (HashMap<String, Object>) getIfLastDate((Long) reqMap.get("tenantId"), ifType);
			lastDataTime = getDateMap.get("nowDate").toString();
			nowDataTime = getDateMap.get("nowDate").toString();
		} catch(Exception e) {
			retMsg = "WORKTIME_CLOSE get : 최종갱신일 조회오류";
			rp.setFail(retMsg);
			return rp;
		}

		try {
			System.out.println("************************* reqMap ** " + reqMap.toString());

			// 사원별 기간을 반복해야함
			String sYmd = reqMap.get("sYmd").toString();
			String eYmd = reqMap.get("eYmd").toString();

			Map<String, Object> dayMap = reqMap;
			dayMap.put("symd", sYmd);
			dayMap.put("eymd", eYmd);
			System.out.println("dayMap" + dayMap.toString());
			//근무 마감된 자료 있는지 조회
			Map<String, Object> workCloseCntMap = wtmInterfaceMapper.isWorkClose(dayMap);
			if(workCloseCntMap!=null && workCloseCntMap.containsKey("workCloseCnt") && workCloseCntMap.get("workCloseCnt")!=null && !"".equals(workCloseCntMap.get("workCloseCnt")) ) {
				int workCloseCnt = Integer.parseInt(workCloseCntMap.get("workCloseCnt").toString());
				if(workCloseCnt>0) {
					ifHisMap.put("ifStatus", "ERR");
					retMsg = "근무마감된 자료가 있습니다. 마감여부를 확인하세요.";

					dayMap.put("msg", retMsg);
					wtmInterfaceMapper.insertErrorLog(dayMap);

					rp.setFail(retMsg);
					return rp;
				}
			}

			//지각조퇴결근 코드 조회
			Map<String, Object> taaCodeMap = wtmInterfaceMapper.getLateAndLeaveAndAbsenceCode(dayMap);
			if(taaCodeMap==null
					|| !taaCodeMap.containsKey("lateCd") || taaCodeMap.get("lateCd")==null || "".equals(taaCodeMap.get("lateCd"))
					|| !taaCodeMap.containsKey("leaveCd") || taaCodeMap.get("leaveCd")==null || "".equals(taaCodeMap.get("leaveCd"))
					|| !taaCodeMap.containsKey("absenceCd") || taaCodeMap.get("absenceCd")==null || "".equals(taaCodeMap.get("absenceCd")) ) {
				ifHisMap.put("ifStatus", "ERR");
				retMsg = "지각/조퇴/결근 코드가 없습니다.";

				dayMap.put("msg", retMsg);
				wtmInterfaceMapper.insertErrorLog(dayMap);

				rp.setFail(retMsg);
				return rp;
			}



			//생성할 자료 삭제
			wtmInterfaceMapper.deleteWorktimeDayClose(dayMap);

			Map<String, Object> emptyWorkTypeCntMap = wtmInterfaceMapper.isWorkType(dayMap);
			if(emptyWorkTypeCntMap!=null && emptyWorkTypeCntMap.containsKey("emptyWorkTypeCnt") && emptyWorkTypeCntMap.get("emptyWorkTypeCnt")!=null && !"".equals(emptyWorkTypeCntMap.get("emptyWorkTypeCnt")) ) {
				int emptyWorkTypeCnt = Integer.parseInt(emptyWorkTypeCntMap.get("emptyWorkTypeCnt").toString());
				if(emptyWorkTypeCnt>0) {
					ifHisMap.put("ifStatus", "ERR");
					retMsg = "근무정보가 없는 대상자가 있습니다.";

					dayMap.put("msg", retMsg);
					wtmInterfaceMapper.insertErrorLog(dayMap);

					rp.setFail(retMsg);
					return rp;
				}
			}

			//WTM_WORKTIME_DAY_CLOSE 기본값 잆력
			wtmInterfaceMapper.insertWorktimeDayClose(dayMap);

			//근무시간 반영
			dayMap.putAll(taaCodeMap);
			wtmInterfaceMapper.updateWorktimeDayClose(dayMap);

			// 기본근무시간대 심야근무시간이 있으면, 심야근무시간 가산
			wtmInterfaceMapper.updateWorktimeNight(dayMap);

			System.out.println("******************************* DAY close end");
			// System.out.println(dayMap.toString());

			Map<String, Object> monMap = reqMap;
			monMap.put("retCode", "");
			monMap.put("retMsg", "");

			wtmInterfaceMapper.monthWorkClose(monMap);
			String retCodeMon = monMap.get("retCode").toString();
			if("FAIL".equals(retCodeMon)) {
				ifHisMap.put("ifStatus", "ERR");
				retMsg = monMap.get("retMsg").toString();

				dayMap.put("msg", retMsg);
				System.out.println(dayMap.toString());
				wtmInterfaceMapper.insertErrorLog(dayMap);
				rp.setFail(retMsg);
				return rp;
			}

			// 마감이 다 돌았는으면 보상휴가생성으로 넘어가자
		} catch(Exception e){
			ifHisMap.put("ifStatus", "ERR");
			retMsg = "근무마감오류";
			e.printStackTrace();

			rp.setFail(retMsg);
			return rp;
		}

		// 3. 처리결과 저장
		try {
			// WTM_IF_HIS 테이블에 결과저장
			ifHisMap.put("updateDate", nowDataTime);
			ifHisMap.put("ifEndDate", lastDataTime);
			ifHisMap.put("ifMsg", retMsg);
			wtmInterfaceMapper.insertIfHis(ifHisMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("WtmInterfaceServiceImpl setTaaApplIf end");
		return rp;
	}

}
