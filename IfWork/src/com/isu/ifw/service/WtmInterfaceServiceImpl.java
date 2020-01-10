package com.isu.ifw.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.isu.ifw.mapper.WtmFlexibleEmpMapper;
import com.isu.ifw.mapper.WtmInterfaceMapper;

@Service
public class WtmInterfaceServiceImpl implements WtmInterfaceService {
		
	@Autowired
	WtmInterfaceMapper wtmInterfaceMapper;
	
	@Autowired
	WtmFlexibleEmpMapper wtmFlexibleEmpMapper;
	
	@Autowired
	private WtmFlexibleEmpService WtmFlexibleEmpService;
	
		
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
				
				System.out.println("ifUrl : " + ifUrl);
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
//	        		resultCnt += wtmInterfaceMapper.insertEmpHisTemp(ifMap);
//	        		retMsg = resultCnt + "건 반영완료";
	        	}
	        	
	        	try {
		        	// 추가건이 있으면
		    		//if (ifList.size() > 0) {
		    			//System.out.println("insert size : " + ifList.size());
		    			//resultCnt += wtmInterfaceMapper.insertEmpHisTemp(ifList);
	    			if(resultCnt > 0) {
	        			retMsg = resultCnt + "건 반영완료";
	        			
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
	        		retMsg = "emp set : temp 자료저장 오류";
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
			    	// wtmFlexibleEmpMapper.initWtmFlexibleEmpOfWtmWorkDayResult(setEmpMap);
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
	public void setTaaApplIf(HashMap reqMap) throws Exception {
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
					HashMap<String, Object> reqDayMap = reqMap;
					reqDayMap.put("ymd", ymd);
					reqDayMap.put("oldStatus", oldStatusCd);
					reqDayMap.put("retCode", "");
					reqDayMap.put("retMsg", "");
					reqDayMap.put("timeTypeCd", "");
					reqDayMap.put("taaSetYn", "");
					reqDayMap.put("taaSdate", "");
					reqDayMap.put("taaEdate", "");
					System.out.println("oldStatusCd : " + oldStatusCd);
					System.out.println("statusCd : " + reqDayMap.get("status"));
					wtmInterfaceMapper.setTaaApplDayIf(reqDayMap);
					String retDayCode = reqDayMap.get("retCode").toString();
					if("FAIL".equals(retCode)) {
						// 오류다 ㅠㅠ
						ifHisMap.put("ifStatus", "ERR");
						retMsg = "근태정보 이관중 오류. 오류로그 확인";
						break;
					} else {
						// 오류가 아니면.. 근태시간을 생성체크하자
						String taaSetYn = reqDayMap.get("taaSetYn").toString();
						if("I".equals(taaSetYn)) {
							// 근태생성
							WtmFlexibleEmpService.addWtmDayResultInBaseTimeType(
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
							WtmFlexibleEmpService.removeWtmDayResultInBaseTimeType(
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
					}
				}
				
				ifHisMap.put("ifStatus", "OK");
				retMsg = "근태신청서 처리완료";
			} else {
				ifHisMap.put("ifStatus", "ERR");
				retMsg = "근태정보 이관중 오류. 오류로그 확인";
			}
		} catch(Exception e){
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
		   		} else {
		   			retMsg = "TAA_RESULT get : If 데이터 없음";
		   			ifHisMap.put("ifStatus", "ERR");
		   		}
        	} catch(Exception e) {
        		retMsg = "TAA_RESULT get : If 서버통신 오류";
        		ifHisMap.put("ifStatus", "ERR");
        	}
        	
    	} catch(Exception e) {
    		retMsg = "TAA_RESULT get : 최종갱신일 조회오류";
    		ifHisMap.put("ifStatus", "ERR");
    	}
    	
    	// 조회된 자료가 있으면...
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
    			reqMap.put("ifApplNo", Long.parseLong(getIfList.get(l).get("APPL_SEQ").toString()));
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
									WtmFlexibleEmpService.addWtmDayResultInBaseTimeType(
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
									WtmFlexibleEmpService.removeWtmDayResultInBaseTimeType(
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
					        		WtmFlexibleEmpService.calcApprDayInfo(tenantId, enterCd, ymd, ymd, sabun);
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
					} else {
						ifHisMap.put("ifStatus", "ERR");
						retMsg = "근태정보 이관중 오류. 오류로그 확인";
						System.err.println("**TaaAppl reqErr " + reqMap.get("sabun").toString() + "/" + reqMap.get("sYmd").toString() + "~" + reqMap.get("eYmd").toString() + reqMap.get("retCode").toString() + "/"+ reqMap.get("retMsg").toString());
					}
				} catch(Exception e){
					ifHisMap.put("ifStatus", "ERR");
					retMsg = "TAA_RESULT set : 근태 이관오류";
		            e.printStackTrace();
		        }
    		} // end for
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
        System.out.println("WtmInterfaceServiceImpl setTaaApplBatchIf end");
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
				/*
				l.put("shm", l.get("planSdate").toString().substring(8,12));
				l.put("ehm", l.get("planEdate").toString().substring(8,12));
				
				Map<String, Object> planMinuteMap = WtmFlexibleEmpService.calcMinuteExceptBreaktime(Long.parseLong(l.get("timeCdMgrId").toString()), l, userId);
				l.put("planMinute", (Integer.parseInt(planMinuteMap.get("calcMinute")+"")));
				wtmInterfaceMapper.insertDayResult(l);
				*/
				// wtmInterfaceMapper.updateDayResult2(l);
				
        		String enterCd = l.get("enterCd").toString();
        		String sabun = l.get("sabun").toString();
        		String closeYmd = l.get("ymd").toString();
        		System.out.println("********** sabun : " + sabun + ", ymd : " + closeYmd);
        		WtmFlexibleEmpService.calcApprDayInfo(tenantId, enterCd, closeYmd, closeYmd, sabun);
        		
        		// 문제가 없으면 근무계획시간 합산
				
				l.put("symd", l.get("ymd").toString());
				l.put("eymd", l.get("ymd").toString());
				l.put("pId", userId);
				for ( String key : l.keySet() ) {
	    		    System.out.println("key : " + key +" / value : " + l.get(key));
	    		}
				wtmFlexibleEmpMapper.createWorkTermBySabunAndSymdAndEymd(l);
				
				/*
				// 외근 근무시간 기본근무적용
				SimpleDateFormat dt = new SimpleDateFormat("yyyyMMddHHmmss");
				WtmFlexibleEmpService.addWtmDayResultInBaseTimeType(
						  Long.parseLong(l.get("tenantId").toString())
						, l.get("enterCd").toString()
						, l.get("ymd").toString()
						, l.get("sabun").toString()
						, l.get("timeTypeCd").toString()
						, ""
						, dt.parse(l.get("taaSdate").toString())
						, dt.parse(l.get("taaEdate").toString())
						, null
						, "0"
						, true);
				System.out.println("sabun : " + l.get("sabun").toString());
        		*/
			}
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
    	// ymd = "20191202";
    	getDateMap.put("tenantId", tenantId);
    	getDateMap.put("ymd", ymd);
    	getDateMap.put("closeType", closeType);
    	
    	// DB로 타각 갱신부터 처리한다.
    	wtmInterfaceMapper.setCloseEntryOut(getDateMap);
    	
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
        		WtmFlexibleEmpService.calcApprDayInfo(tenantId, enterCd, closeYmd, closeYmd, sabun);
        		
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

}
