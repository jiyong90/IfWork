package com.isu.ifw.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isu.ifw.common.service.TenantConfigManagerService;
import com.isu.ifw.entity.WtmEmpHis;
import com.isu.ifw.entity.WtmIntfTaaAppl;
import com.isu.ifw.entity.WtmOrgChart;
import com.isu.ifw.entity.WtmPropertie;
import com.isu.ifw.entity.WtmRule;
import com.isu.ifw.mapper.WtmFlexibleEmpMapper;
import com.isu.ifw.mapper.WtmInterfaceMapper;
import com.isu.ifw.mapper.WtmIuerpInterfaceMapper;
import com.isu.ifw.mapper.WtmOrgChartMapper;
import com.isu.ifw.repository.WtmEmpHisRepository;
import com.isu.ifw.repository.WtmFlexibleEmpRepository;
import com.isu.ifw.repository.WtmIntfTaaApplRepository;
import com.isu.ifw.repository.WtmOrgChartRepository;
import com.isu.ifw.repository.WtmPropertieRepository;
import com.isu.ifw.repository.WtmRuleRepository;
import com.isu.ifw.util.WtmUtil;
import com.isu.ifw.vo.ReturnParam;

@Service("wtmIuerpInterfaceService")
public class WtmIuerpInterfaceServiceImpl implements WtmIuerpInterfaceService {
		
	private static final Logger logger = LoggerFactory.getLogger("ifwFileLog");
	
	@Autowired
	@Qualifier("WtmTenantConfigManagerService")
	private TenantConfigManagerService tcms;
	
	@Autowired
	@Qualifier("wtmInterfaceService")
	private WtmInterfaceService wtmInterfaceService;
	
	@Autowired
	WtmInterfaceMapper interfaceMapper;
	
	@Autowired
	WtmIuerpInterfaceMapper iuerpInterfaceMapper;
	
	@Autowired
	WtmFlexibleEmpMapper flexibleEmpMapper;
	
	@Autowired
	WtmFlexibleEmpRepository flexibleEmpRepo;
	
	@Autowired
	@Qualifier("flexibleEmpService")
	private WtmFlexibleEmpService flexibleEmpService;
	
	@Autowired
	WtmPropertieRepository propertieRepo;
	
	@Autowired
	WtmRuleRepository ruleRepo;
	
	@Autowired
	WtmIntfTaaApplRepository intfTaaApplRepo;
	
	@Autowired
	WtmOrgChartMapper orgChartMapper;
	
	@Autowired
	WtmOrgChartRepository orgChartRepo;
	
	@Autowired
	WtmInterfaceMapper wtmInterfaceMapper;
	
	@Autowired private WtmEmpHisRepository wtmEmpHisRepo;
	
	@Transactional
	@Override
	public void applyIntf(Long tenantId, String type) {
		
		String ymdhis = null;
		
		logger.debug("tenantId : " + tenantId);
		System.out.println("tenantId : " + tenantId);
		
		//최종 인터페이스된 날짜(lastDate) 조회
		Map<String, Object> dateMap = getIfLastDate(tenantId, type);
		if(dateMap!=null && dateMap.containsKey("lastDate") && dateMap.get("lastDate")!=null) {
			ymdhis = dateMap.get("lastDate").toString();
		}
		
		logger.debug("ymdhis : " + ymdhis);
		System.out.println("ymdhis : " + ymdhis);
		
		if(ymdhis==null) {
			logger.debug("lastDate null");
			System.out.println("lastDate null");
			return;
		}
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("tenantId", tenantId);
		paramMap.put("ymdhis", ymdhis);
		paramMap.put("ymd", WtmUtil.parseDateStr(WtmUtil.toDate(ymdhis, "yyyyMMddHHmmss"), "yyyyMMdd"));
		paramMap.put("updateId", "INTF");
		
		ReturnParam rp = new ReturnParam();
		if(type.equalsIgnoreCase("CODE")) { //공통코드
			rp = saveWtmCode(paramMap);
		} else if(type.equalsIgnoreCase("HOLIDAY")) { //공휴일
			rp = saveWtmHolidayMgr(paramMap);
		} else if(type.equalsIgnoreCase("GNT")) { //근태코드(더존, request type cd 안가져오기)
			rp = saveWtmTaaCode(paramMap);
		} else if(type.equalsIgnoreCase("GNTETC")) { //근태코드(더존외)
			rp = saveWtmTaaCodeEtc(paramMap);
		} else if(type.equalsIgnoreCase("EMP")) { //직원정보
			rp = saveWtmEmpHis(paramMap);
		} else if(type.equalsIgnoreCase("EMPADDR")) { //직원 연락처
			rp = saveWtmEmpAddr(paramMap);
		} else if(type.equalsIgnoreCase("ORG")) { //조직코드
			rp = saveWtmOrgCode(paramMap);
		} else if(type.equalsIgnoreCase("ORGCONC")) { //겸직정보
			rp = saveWtmOrgConc(paramMap);
		} else if(type.equalsIgnoreCase("TAAAPPL")) { //근태 신청
			rp = saveWtmTaaAppl(paramMap);
		} else if(type.equalsIgnoreCase("ORGETC")) { //조직도(더존외)
			rp = saveWtmOrgCodeEtc(paramMap);
		}
		
		// WTM_IF_HIS 테이블에 결과저장
		Map<String, Object> ifHisMap = new HashMap<String, Object>();
		ifHisMap.put("tenantId", tenantId);
		ifHisMap.put("ifStatus", rp.getStatus());
		ifHisMap.put("ifItem", type);
		ifHisMap.put("ifEndDate", ymdhis);
		ifHisMap.put("updateDate", WtmUtil.parseDateStr(new Date(), "yyyyMMddHHmmss"));
		
		logger.debug(">"+type+"status : " + rp.getStatus());
		System.out.println(">"+type+" status : " + rp.getStatus());
		if(rp!=null && rp.containsKey("message") && rp.get("message")!=null) {
			ifHisMap.put("ifMsg", rp.get("message").toString());
			logger.debug(">"+type+" message : " + rp.get("message").toString());
			System.out.println(">"+type+" message : " + rp.get("message").toString());
		}
			
		interfaceMapper.insertIfHis(ifHisMap);
	}
	
	protected Map<String, Object> getIfLastDate(Long tenantId, String ifType) {
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
			Map<String, Object> result = interfaceMapper.getIfLastDate(paramMap);
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
			
			result = interfaceMapper.getIfNowDate(paramMap);
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
	
	//공통코드
	protected ReturnParam saveWtmCode(Map<String, Object> paramMap) {
		ReturnParam rp = new ReturnParam();
		
		int expireCnt = 0;
		int deleteCnt = 0;
		int updateCnt = 0;
		int insertCnt = 0;
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			//1. expire wtm_code
			//근태 테이블에는 있고, 인터페이스 테이블에는 없는 데이터 종료일 변경
			List<Map<String, Object>> expireList = iuerpInterfaceMapper.getExpireWtmCode(paramMap);
			logger.debug("expireList : "+mapper.writeValueAsString(expireList));
			System.out.println("expireList : "+mapper.writeValueAsString(expireList));
			
			expireCnt = iuerpInterfaceMapper.expireWtmCode(paramMap);
			logger.debug("WtmCode expire "+expireCnt+" end");
			System.out.println("WtmCode expire "+expireCnt+" end");
			
			//2. delete wtm_code
			//신규 시작/종료일(인터페이스 될 데이터)이 기존 시작/종료일을 포함할 경우 기존 데이터 삭제
			//인터페이스 데이터 1900.01.01~2999.12.31
			//기존 데이터 2019.01.01~2019.12.31
			List<Map<String, Object>> deleteList = iuerpInterfaceMapper.getDeleteWtmCode(paramMap);
			logger.debug("deleteList : "+mapper.writeValueAsString(deleteList));
			System.out.println("deleteList : "+mapper.writeValueAsString(deleteList));
			
			deleteCnt = iuerpInterfaceMapper.deleteWtmCode(paramMap);
			logger.debug("WtmCode delete "+deleteCnt+" end");
			System.out.println("WtmCode delete "+deleteCnt+" end");
			
			//인터페이스된 데이터가 중도에 잘려서 오면 종료됐다고 판단하여 주석처리
			//3. insert wtm_code
			//기존 시작/종료일이 신규 시작/종료일(인터페이스 될 데이터)을 포함할 경우 
			//인터페이스 데이터 2019.01.01~2019.12.31
			//기존 데이터 1900.01.01~2999.12.31
			//2020.01.01(인터페이스 종료일+1) ~ 2999.12.31(기존 종료일) 데이터 삽입
			//insertCnt = iuerpInterfaceMapper.insertWtmCodeForBetween(paramMap);
			//logger.debug("WtmCode insert1 "+insertCnt+" end");
			//System.out.println("WtmCode insert1 "+insertCnt+" end");
			
			//4. update wtm_code
			//기존 데이터 시작/종료일 수정
			updateCnt = iuerpInterfaceMapper.updateWtmCode(paramMap);
			logger.debug("WtmCode update "+updateCnt+" end");
			System.out.println("WtmCode update "+updateCnt+" end");
			
			//5. insert wtm_code
			//기존 시작/종료일과 신규 시작/종료일이 다른 데이터 삽입
			insertCnt += iuerpInterfaceMapper.insertWtmCode(paramMap);
			logger.debug("WtmCode insert "+insertCnt+" end");
			System.out.println("WtmCode insert "+insertCnt+" end");
			
			
			
		} catch(Exception e) {
			e.printStackTrace();
			rp.setFail("WtmCode 데이터 이관 오류");
			return rp;
		}
		
		int applyCnt = expireCnt+deleteCnt+updateCnt+insertCnt;
		if(applyCnt!=0)
			rp.setSuccess(applyCnt+"건(expire:"+expireCnt+",delete:"+deleteCnt+",update:"+updateCnt+",insert:"+insertCnt+") 반영완료");
		else
			rp.setSuccess("반영완료");
		
		return rp;
	}
	
	//공휴일
	protected ReturnParam saveWtmHolidayMgr(Map<String, Object> paramMap) {
		ReturnParam rp = new ReturnParam();
		
		int deleteCnt = 0;
		int updateCnt = 0;
		int insertCnt = 0;
		
		try {
			deleteCnt = iuerpInterfaceMapper.expireWtmHolidayMgr(paramMap);
			logger.debug("WtmHolidayMgr delete "+deleteCnt+" end");
			System.out.println("WtmHolidayMgr delete "+deleteCnt+" end");
			
			updateCnt = iuerpInterfaceMapper.updateWtmHolidayMgr(paramMap);
			logger.debug("WtmHolidayMgr update "+updateCnt+" end");
			System.out.println("WtmHolidayMgr update "+updateCnt+" end");
			
			insertCnt = iuerpInterfaceMapper.insertWtmHolidayMgr(paramMap);
			logger.debug("WtmHolidayMgr insert "+insertCnt+" end");
			System.out.println("WtmHolidayMgr insert "+insertCnt+" end");
		} catch(Exception e) {
			e.printStackTrace();
			rp.setFail("WtmHolidayMgr 데이터 이관 오류");
			return rp;
		}
		
		int applyCnt = deleteCnt+updateCnt+insertCnt;
		if(applyCnt!=0)
			rp.setSuccess(applyCnt+"건(delete:"+deleteCnt+",update:"+updateCnt+",insert:"+insertCnt+") 반영완료");
		else
			rp.setSuccess("반영완료");
		
		return rp;
	}
	
	//근태코드
	protected ReturnParam saveWtmTaaCode(Map<String, Object> paramMap) {
		ReturnParam rp = new ReturnParam();
		
		int deleteCnt = 0;
		int updateCnt = 0;
		int insertCnt = 0;
		
		try {
			deleteCnt = iuerpInterfaceMapper.expireWtmTaaCode(paramMap);
			logger.debug("WtmTaaCode delete "+deleteCnt+" end");
			System.out.println("WtmTaaCode delete "+deleteCnt+" end");
			
			updateCnt = iuerpInterfaceMapper.updateWtmTaaCode(paramMap);
			logger.debug("WtmTaaCode update "+updateCnt+" end");
			System.out.println("WtmTaaCode update "+updateCnt+" end");
			
			insertCnt = iuerpInterfaceMapper.insertWtmTaaCode(paramMap);
			logger.debug("WtmTaaCode insert "+insertCnt+" end");
			System.out.println("WtmTaaCode insert "+insertCnt+" end");
		} catch(Exception e) {
			e.printStackTrace();
			rp.setFail("WtmTaaCode 데이터 이관 오류");
			return rp;
		}
		
		int applyCnt = deleteCnt+updateCnt+insertCnt;
		if(applyCnt!=0)
			rp.setSuccess(applyCnt+"건(delete:"+deleteCnt+",update:"+updateCnt+",insert:"+insertCnt+") 반영완료");
		else
			rp.setSuccess("반영완료");
		
		return rp;
	}
	
	//근태코드
	protected ReturnParam saveWtmTaaCodeEtc(Map<String, Object> paramMap) {
		ReturnParam rp = new ReturnParam();
		
		int deleteCnt = 0;
		int updateCnt = 0;
		int insertCnt = 0;
		
		try {
			deleteCnt = iuerpInterfaceMapper.expireWtmTaaCode(paramMap);
			logger.debug("WtmTaaCode2 delete "+deleteCnt+" end");
			System.out.println("WtmTaaCode2 delete "+deleteCnt+" end");
			
			updateCnt = iuerpInterfaceMapper.updateWtmTaaCode2(paramMap);
			logger.debug("WtmTaaCode2 update "+updateCnt+" end");
			System.out.println("WtmTaaCode2 update "+updateCnt+" end");
			
			insertCnt = iuerpInterfaceMapper.insertWtmTaaCode(paramMap);
			logger.debug("WtmTaaCode2 insert "+insertCnt+" end");
			System.out.println("WtmTaaCode2 insert "+insertCnt+" end");
		} catch(Exception e) {
			e.printStackTrace();
			rp.setFail("WtmTaaCode2 데이터 이관 오류");
			return rp;
		}
		
		int applyCnt = deleteCnt+updateCnt+insertCnt;
		if(applyCnt!=0)
			rp.setSuccess(applyCnt+"건(delete:"+deleteCnt+",update:"+updateCnt+",insert:"+insertCnt+") 반영완료");
		else
			rp.setSuccess("반영완료");
		
		return rp;
	}
	
	protected ReturnParam saveWtmOrgCode(Map<String, Object> paramMap) {
		ReturnParam rp = new ReturnParam();
		
		int expireCnt = 0;
		int deleteCnt = 0;
		int updateCnt = 0;
		int insertCnt = 0;
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			//1. expire wtm_org_code
			List<Map<String, Object>> expireList = iuerpInterfaceMapper.getExpireWtmOrgCode(paramMap);
			logger.debug("expireList : "+mapper.writeValueAsString(expireList));
			System.out.println("expireList : "+mapper.writeValueAsString(expireList));
			
			expireCnt = iuerpInterfaceMapper.expireWtmOrgCode(paramMap);
			logger.debug("WtmOrgCode expire "+expireCnt+" end");
			System.out.println("WtmOrgCode expire "+expireCnt+" end");
			
			//2. delete wtm_org_code
			List<Map<String, Object>> deleteList = iuerpInterfaceMapper.getDeleteWtmOrgCode(paramMap);
			logger.debug("deleteList : "+mapper.writeValueAsString(deleteList));
			System.out.println("deleteList : "+mapper.writeValueAsString(deleteList));
			
			deleteCnt = iuerpInterfaceMapper.deleteWtmOrgCode(paramMap);
			logger.debug("WtmOrgCode delete "+deleteCnt+" end");
			System.out.println("WtmOrgCode delete "+deleteCnt+" end");
			
			//인터페이스된 데이터가 중도에 잘려서 오면 종료됐다고 판단하여 주석처리
			//3. insert wtm_org_code
			//insertCnt = iuerpInterfaceMapper.insertWtmOrgCodeForBetween(paramMap);
			//logger.debug("WtmOrgCode insert1 "+insertCnt+" end");
			//System.out.println("WtmOrgCode insert1 "+insertCnt+" end");
			
			//4. update wtm_org_code
			updateCnt = iuerpInterfaceMapper.updateWtmOrgCode(paramMap);
			logger.debug("WtmOrgCode update "+updateCnt+" end");
			System.out.println("WtmOrgCode update "+updateCnt+" end");
			
			//5. insert wtm_code
			//기존 시작/종료일과 신규 시작/종료일이 다른 데이터 삽입
			insertCnt += iuerpInterfaceMapper.insertWtmOrgCode(paramMap);
			logger.debug("WtmOrgCode insert "+insertCnt+" end");
			System.out.println("WtmOrgCode insert "+insertCnt+" end");
			
			//chart
			if(insertCnt+updateCnt > 0) {
				Long tenantId = Long.valueOf(paramMap.get("tenantId").toString());
				String companyList = tcms.getConfigValue(tenantId, "WTMS.LOGIN.COMPANY_LIST", true, "");
				String ymd = paramMap.get("ymd").toString();
				
				if(companyList!=null && !"".equals(companyList)) {
					List<Map<String, Object>> enterCds = mapper.readValue(companyList, new ArrayList<Map<String, Object>>().getClass());
					
					if(enterCds!=null && enterCds.size()>0) {
						for(Map<String, Object> m : enterCds) {
							for(String enterCd : m.keySet()) {
								Map<String, Object> cMap = new HashMap<String, Object>();
								cMap.put("tenantId", tenantId);
								cMap.put("enterCd", enterCd);
								cMap.put("ymd", WtmUtil.parseDateStr(WtmUtil.addDate(new Date(), -1) , "yyyyMMdd"));
								cMap.put("symd",  WtmUtil.parseDateStr(new Date(), "yyyyMMdd"));
								cMap.put("eymd", "29991231");
								cMap.put("updateId", "INTF");
								cMap.put("orgChartNm", "조직도");
								int uCnt = orgChartMapper.updateOrgChartEymd(cMap);
								logger.debug("WtmOrgChart update "+uCnt+" end");
								System.out.println("WtmOrgChart update "+uCnt+" end");
								int iCnt = orgChartMapper.insertOrgChart(cMap);
								logger.debug("WtmOrgChart insert "+iCnt+" end");
								System.out.println("WtmOrgChart insert "+iCnt+" end");
							
								
								WtmOrgChart orgChart = orgChartRepo.findByTenantIdAndEnterCdAndBetweenSymdAndEymd(tenantId, enterCd, WtmUtil.parseDateStr(new Date(), "yyyyMMdd"));
								//chart det
								if(orgChart!=null) {
									System.out.println("orgChartId : " + orgChart.getOrgChartId());
									
									Map<String, Object> dMap = new HashMap<String, Object>();
									dMap.put("orgChartId", orgChart.getOrgChartId());
									dMap.put("ymd", WtmUtil.parseDateStr(WtmUtil.addDate(new Date(), -1) , "yyyyMMdd"));
									dMap.put("ymdhis", paramMap.get("ymdhis"));
									dMap.put("updateId", "INTF");
									
									 uCnt = iuerpInterfaceMapper.updateWtmOrgChartDet(dMap);
									 logger.debug("WtmOrgChartDet update "+uCnt+" end");
									 System.out.println("WtmOrgChartDet update "+uCnt+" end");
									 iCnt = iuerpInterfaceMapper.insertWtmOrgChartDet(dMap);
									 logger.debug("WtmOrgChartDet insert "+iCnt+" end");
									 System.out.println("WtmOrgChartDet insert "+iCnt+" end");
								}
							}
						}
					}
				}
			}
			
			
		} catch(Exception e) {
			e.printStackTrace();
			rp.setFail("WtmOrgCode 데이터 이관 오류");
			return rp;
		}
		
		int applyCnt = expireCnt+deleteCnt+updateCnt+insertCnt;
		if(applyCnt!=0)
			rp.setSuccess(applyCnt+"건(expire:"+expireCnt+",delete:"+deleteCnt+",update:"+updateCnt+",insert:"+insertCnt+") 반영완료");
		else
			rp.setSuccess("반영완료");
		
		return rp;
	}
	
	//직원정보
	protected ReturnParam saveWtmEmpHis(Map<String, Object> paramMap) {
		ReturnParam rp = new ReturnParam();
		
		int expireCnt = 0;
		int updateCnt = 0;
		int insertCnt = 0;
		
		try {
			
			ObjectMapper mapper = new ObjectMapper();
			
			//List<Map<String, Object>> expireTargets = iuerpInterfaceMapper.getExpireWtmEmpHis(paramMap);
			//logger.debug("expireTargets : "+ mapper.writeValueAsString(expireTargets));
			//System.out.println("expireTargets : " + mapper.writeValueAsString(expireTargets));
			
			//expireCnt = iuerpInterfaceMapper.expireWtmEmpHis(paramMap);
			//logger.debug("WtmEmpHis expire "+expireCnt+" end");
			//System.out.println("WtmEmpHis expire "+expireCnt+" end");

			String encKey = tcms.getConfigValue(Long.valueOf(paramMap.get("tenantId").toString()), "SECURITY.AES.KEY", true, "");
			paramMap.put("encKey", encKey);
			

			Long tenantId = Long.valueOf(paramMap.get("tenantId").toString());
			
			List<Map<String, Object>> intfEmps = iuerpInterfaceMapper.getWtmIntfEmp(paramMap);
			
			//직원 정보 변경
			//List<Map<String, Object>> updateTargets = iuerpInterfaceMapper.getUpdateWtmEmpHis(paramMap);
			
			logger.debug("intfEmps : "+ mapper.writeValueAsString(intfEmps));
			
			String updateId = paramMap.get("updateId").toString();
			if(intfEmps != null && intfEmps.size() > 0) {
				boolean isNew = false;
				SimpleDateFormat ymd = new SimpleDateFormat("yyyyMMdd");
				String tmpEnterCd = null;
				for(Map<String, Object> intfEmp : intfEmps) {
					isNew = false;
					String enterCd = intfEmp.get("enterCd").toString();
					WtmPropertie propertie = null;
					if(tmpEnterCd == null || !tmpEnterCd.equals(enterCd)) {
						propertie = propertieRepo.findByTenantIdAndEnterCdAndInfoKey(tenantId, enterCd, "OPTION_FLEXIBLE_EMP_EXCEPT_TARGET");
						tmpEnterCd = enterCd;
					}
					
					
					String sabun = intfEmp.get("sabun") != null?intfEmp.get("sabun").toString():"";
					String empNm = intfEmp.get("empNm") != null?intfEmp.get("empNm").toString():"";
					String empEngNm = intfEmp.get("empEngNm") != null?intfEmp.get("empEngNm").toString():"";
					String orgCd = intfEmp.get("orgCd") != null?intfEmp.get("orgCd").toString():"";
					String statusCd = intfEmp.get("statusCd") != null?intfEmp.get("statusCd").toString():""; 
					String locationCd = intfEmp.get("locationCd") != null?intfEmp.get("locationCd").toString():""; 
					String dutyCd = intfEmp.get("dutyCd") != null?intfEmp.get("dutyCd").toString():""; 
					String posCd = intfEmp.get("posCd") != null?intfEmp.get("posCd").toString():""; 
					String classCd = intfEmp.get("classCd") != null?intfEmp.get("classCd").toString():""; 
					String jobGroupCd = intfEmp.get("jobGroupCd") != null?intfEmp.get("jobGroupCd").toString():""; 
					String jobCd = intfEmp.get("jobCd") != null?intfEmp.get("jobCd").toString():""; 
					String payTypeCd = intfEmp.get("payTypeCd") != null?intfEmp.get("payTypeCd").toString():"";
					String leaderYn = intfEmp.get("leaderYn") != null?intfEmp.get("leaderYn").toString():"";
					String symd = intfEmp.get("symd") != null?intfEmp.get("symd").toString():"";
					String eymd = intfEmp.get("eymd") != null?intfEmp.get("eymd").toString():"";
					String empId = intfEmp.get("empId") != null?intfEmp.get("empId").toString():"";
					
					WtmEmpHis empHis = wtmEmpHisRepo.findByTenantIdAndEnterCdAndSabunAndYmd(tenantId, enterCd, sabun, symd);
					if(empHis != null) {
						if(empHis.getSymd().equals(symd)) {
							if(!empHis.getEmpNm().equals(empNm)
									|| !empHis.getEmpEngNm().equals(empEngNm)
									|| !empHis.getStatusCd().equals(statusCd)
									|| !empHis.getOrgCd().equals(orgCd)
									|| !empHis.getDutyCd().equals(dutyCd)
									|| !empHis.getPosCd().equals(posCd)
									|| !empHis.getClassCd().equals(classCd)
									|| !empHis.getJobGroupCd().equals(jobGroupCd)
									|| !empHis.getJobCd().equals(jobCd)
									|| !empHis.getPayTypeCd().equals(payTypeCd)
									|| !empHis.getLeaderYn().equals(leaderYn)
									) {
								empHis.setEmpNm(empNm);
								empHis.setEmpEngNm(empEngNm);
								empHis.setOrgCd(orgCd);
								empHis.setStatusCd(statusCd);
								empHis.setBusinessPlaceCd(locationCd);
								empHis.setDutyCd(dutyCd);
								empHis.setPosCd(posCd);
								empHis.setClassCd(classCd);
								empHis.setJobGroupCd(jobGroupCd);
								empHis.setJobCd(jobCd);
								empHis.setPayTypeCd(payTypeCd);
								empHis.setLeaderYn(leaderYn);
								empHis.setSymd(symd);
								empHis.setEymd(eymd);
								empHis.setEmpId(empId);
								empHis.setUpdateId(updateId);
								
								wtmEmpHisRepo.save(empHis);
								updateCnt++;
							}
							
						}else {
							if(Integer.parseInt(empHis.getSymd()) < Integer.parseInt(symd)) {
								Calendar cal = Calendar.getInstance();
								cal.setTime(new Date(symd));
								cal.add(Calendar.DATE, -1);
								empHis.setEymd(ymd.format(cal.getTime()));
								wtmEmpHisRepo.save(empHis);

								WtmEmpHis newEmp = new WtmEmpHis();
								newEmp.setTenantId(tenantId);
								newEmp.setEnterCd(enterCd);
								newEmp.setSabun(sabun);
								newEmp.setEmpNm(empNm);
								newEmp.setEmpEngNm(empEngNm);
								newEmp.setOrgCd(orgCd);
								newEmp.setStatusCd(statusCd);
								newEmp.setBusinessPlaceCd(locationCd);
								newEmp.setDutyCd(dutyCd);
								newEmp.setPosCd(posCd);
								newEmp.setClassCd(classCd);
								newEmp.setJobGroupCd(jobGroupCd);
								newEmp.setJobCd(jobCd);
								newEmp.setPayTypeCd(payTypeCd);
								newEmp.setLeaderYn(leaderYn);
								newEmp.setSymd(symd);
								newEmp.setEymd(eymd);
								newEmp.setEmpId(empId);
								newEmp.setUpdateId(updateId);
								
								wtmEmpHisRepo.save(newEmp);
								insertCnt++;
							}
						}
					}else {
						WtmEmpHis newEmp = new WtmEmpHis();
						newEmp.setTenantId(tenantId);
						newEmp.setEnterCd(enterCd);
						newEmp.setSabun(sabun);
						newEmp.setEmpNm(empNm);
						newEmp.setEmpEngNm(empEngNm);
						newEmp.setOrgCd(orgCd);
						newEmp.setStatusCd(statusCd);
						newEmp.setBusinessPlaceCd(locationCd);
						newEmp.setDutyCd(dutyCd);
						newEmp.setPosCd(posCd);
						newEmp.setClassCd(classCd);
						newEmp.setJobGroupCd(jobGroupCd);
						newEmp.setJobCd(jobCd);
						newEmp.setPayTypeCd(payTypeCd);
						newEmp.setLeaderYn(leaderYn);
						newEmp.setSymd(symd);
						newEmp.setEymd(eymd);
						newEmp.setEmpId(empId);
						newEmp.setUpdateId(updateId);
						
						wtmEmpHisRepo.save(newEmp);
						insertCnt++;
						isNew = true;
						
					}
					
					if(isNew) {
						String ruleValue = null;
						String ruleType = null;
						if(propertie!=null && propertie.getInfoValue()!=null && !"".equals(propertie.getInfoValue())) {
							WtmRule rule = ruleRepo.findByTenantIdAndEnterCdAndRuleNm(tenantId, enterCd, propertie.getInfoValue());
							if(rule!=null && rule.getRuleValue()!=null && !"".equals(rule.getRuleValue())) {
								ruleType = rule.getRuleType();
								ruleValue = rule.getRuleValue();
							}
								
						}
					
						boolean isNotTarget = false;
						if(ruleValue!=null) 
							isNotTarget = flexibleEmpService.isRuleTarget(tenantId, enterCd, sabun, ruleType, ruleValue);
						
						System.out.println("isNotTarget : " + isNotTarget);
						
					    if(!isNotTarget && !"CA".equals(statusCd) && !"EA".equals(statusCd) && !"RA".equals(statusCd)) {
					    	Map<String, Object> pMap = new HashMap<>();
					    	pMap.put("tenantId", tenantId);
					    	pMap.put("enterCd", enterCd);
					    	pMap.put("sabun", sabun);
					    	pMap.put("symd", symd);
					    	pMap.put("eymd", eymd);
					    	pMap.put("userId", updateId);
					    	pMap.put("pId", updateId);
					    	
					    	flexibleEmpMapper.initWtmFlexibleEmpOfWtmWorkDayResult(pMap);
							flexibleEmpMapper.createWorkTermBySabunAndSymdAndEymd(pMap);
					    }
					}
					
				}
			}
			

			//System.out.println("updateTargets : " + mapper.writeValueAsString(updateTargets));
			/*
			updateCnt = iuerpInterfaceMapper.updateWtmEmpHis(paramMap);
			logger.debug("1.WtmEmpHis update "+updateCnt+" end");
			System.out.println("1.WtmEmpHis update "+updateCnt+" end");
			
			
			//신규 입사자 
			List<Map<String, Object>> insertTargets = iuerpInterfaceMapper.getInsertWtmEmpHis(paramMap);
			logger.debug("insertTargets : "+ mapper.writeValueAsString(insertTargets));
			System.out.println("insertTargets : " + mapper.writeValueAsString(insertTargets));
			
			insertCnt = iuerpInterfaceMapper.insertWtmEmpHis(paramMap);
			logger.debug("2.WtmEmpHis insert "+insertCnt+" end");
			System.out.println("2.WtmEmpHis insert "+insertCnt+" end");
			
			//근무제 제외 대상자를 제외하고 입사자 reset 호출하여 base 생성
			if(insertTargets!=null && insertTargets.size()>0) {
				
				//comm_user 생성
				//iuerpInterfaceMapper.insertCommUser(paramMap);
				//System.out.println("CommUser insert "+insertCnt+" end");
					
				for(Map<String, Object> emp : insertTargets) {
					WtmPropertie propertie = propertieRepo.findByTenantIdAndEnterCdAndInfoKey(Long.valueOf(emp.get("tenantId").toString()), emp.get("enterCd").toString(), "OPTION_FLEXIBLE_EMP_EXCEPT_TARGET");
					
					String ruleValue = null;
					String ruleType = null;
					if(propertie!=null && propertie.getInfoValue()!=null && !"".equals(propertie.getInfoValue())) {
						WtmRule rule = ruleRepo.findByTenantIdAndEnterCdAndRuleNm(Long.valueOf(emp.get("tenantId").toString()), emp.get("enterCd").toString(), propertie.getInfoValue());
						if(rule!=null && rule.getRuleValue()!=null && !"".equals(rule.getRuleValue())) {
							ruleType = rule.getRuleType();
							ruleValue = rule.getRuleValue();
						}
							
					}
				
					boolean isNotTarget = false;
					if(ruleValue!=null) 
						isNotTarget = flexibleEmpService.isRuleTarget(Long.valueOf(emp.get("tenantId").toString()), emp.get("enterCd").toString(), emp.get("sabun").toString(), ruleType, ruleValue);
					
					System.out.println("isNotTarget : " + isNotTarget);
					
					String statusCd = "";
					if(emp.get("statusCd")!=null && !"".equals(emp.get("statusCd"))) {
						statusCd = emp.get("statusCd").toString();
					}
					
				    if(!isNotTarget && !"CA".equals(statusCd) && !"EA".equals(statusCd) && !"RA".equals(statusCd)) {
				    	System.out.println("tenantId : " + Long.valueOf(emp.get("tenantId").toString()) + " / enterCd : " + emp.get("enterCd").toString() + " / sabun : " + emp.get("sabun").toString()  + " / symd : " + emp.get("symd").toString()  + " / eymd : " + emp.get("eymd").toString());
				    	
				    	flexibleEmpMapper.initWtmFlexibleEmpOfWtmWorkDayResult(emp);
						flexibleEmpMapper.createWorkTermBySabunAndSymdAndEymd(emp);
				    }
				    
				}
				logger.debug("3.입사자 reset end");
				System.out.println("3.입사자 reset end");
			}
			
		*/
		} catch(Exception e) {
			e.printStackTrace();
			rp.setFail("WtmEmpHis 데이터 이관 오류");
			return rp;
		}
		
		int applyCnt = updateCnt+insertCnt;
		if(applyCnt!=0)
			rp.setSuccess(applyCnt+"건(update:"+updateCnt+",insert:"+insertCnt+") 반영완료");
		else
			rp.setSuccess("반영완료");
		
		return rp;
	}
	
	//겸직정보
	protected ReturnParam saveWtmOrgConc(Map<String, Object> paramMap) {
		ReturnParam rp = new ReturnParam();
		
		int deleteCnt = 0;
		int updateCnt = 0;
		int insertCnt = 0;
		
		try {
			deleteCnt = iuerpInterfaceMapper.expireWtmOrgConc(paramMap);
			logger.debug("WtmOrgConc delete "+deleteCnt+" end");
			System.out.println("WtmOrgConc delete "+deleteCnt+" end");
			
			updateCnt = iuerpInterfaceMapper.updateWtmOrgConc(paramMap);
			logger.debug("WtmOrgConc update "+updateCnt+" end");
			System.out.println("WtmOrgConc update "+updateCnt+" end");
			
			insertCnt = iuerpInterfaceMapper.insertWtmOrgConc(paramMap);
			logger.debug("WtmOrgConc insert "+insertCnt+" end");
			System.out.println("WtmOrgConc insert "+insertCnt+" end");
		} catch(Exception e) {
			e.printStackTrace();
			rp.setFail("WtmOrgConc 데이터 이관 오류");
			return rp;
		}
		
		int applyCnt = deleteCnt+updateCnt+insertCnt;
		if(applyCnt!=0)
			rp.setSuccess(applyCnt+"건(delete:"+deleteCnt+",update:"+updateCnt+",insert:"+insertCnt+") 반영완료");
		else
			rp.setSuccess("반영완료");
		
		return rp;
	}
	
	protected ReturnParam saveWtmOrgCodeEtc(Map<String, Object> paramMap) {
		ReturnParam rp = new ReturnParam();
		
		int expireCnt = 0;
		int deleteCnt = 0;
		int updateCnt = 0;
		int insertCnt = 0;
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			//1. expire wtm_org_code
			List<Map<String, Object>> expireList = iuerpInterfaceMapper.getExpireWtmOrgCode(paramMap);
			logger.debug("expireList : "+mapper.writeValueAsString(expireList));
			System.out.println("expireList : "+mapper.writeValueAsString(expireList));
			
			expireCnt = iuerpInterfaceMapper.expireWtmOrgCode(paramMap);
			logger.debug("WtmOrgCodeEtc expire "+expireCnt+" end");
			System.out.println("WtmOrgCodeEtc expire "+expireCnt+" end");
			
			//2. delete wtm_org_code
			List<Map<String, Object>> deleteList = iuerpInterfaceMapper.getDeleteWtmOrgCode(paramMap);
			logger.debug("deleteList : "+mapper.writeValueAsString(deleteList));
			System.out.println("deleteList : "+mapper.writeValueAsString(deleteList));
			
			deleteCnt = iuerpInterfaceMapper.deleteWtmOrgCode(paramMap);
			logger.debug("WtmOrgCodeEtc delete "+deleteCnt+" end");
			System.out.println("WtmOrgCodeEtc delete "+deleteCnt+" end");
			
			//인터페이스된 데이터가 중도에 잘려서 오면 종료됐다고 판단하여 주석처리
			//3. insert wtm_org_code
			//insertCnt = iuerpInterfaceMapper.insertWtmOrgCodeForBetween(paramMap);
			//logger.debug("WtmOrgCode insert1 "+insertCnt+" end");
			//System.out.println("WtmOrgCode insert1 "+insertCnt+" end");
			
			//4. update wtm_org_code
			updateCnt = iuerpInterfaceMapper.updateWtmOrgCode(paramMap);
			logger.debug("WtmOrgCodeEtc update "+updateCnt+" end");
			System.out.println("WtmOrgCodeEtc update "+updateCnt+" end");
			
			//5. insert wtm_code
			//기존 시작/종료일과 신규 시작/종료일이 다른 데이터 삽입
			insertCnt += iuerpInterfaceMapper.insertWtmOrgCode(paramMap);
			logger.debug("WtmOrgCodeEtc insert "+insertCnt+" end");
			System.out.println("WtmOrgCodeEtc insert "+insertCnt+" end");
			
			//chart
			if(insertCnt+updateCnt > 0) {
				Long tenantId = Long.valueOf(paramMap.get("tenantId").toString());
				String companyList = tcms.getConfigValue(tenantId, "WTMS.LOGIN.COMPANY_LIST", true, "");
				String ymd = paramMap.get("ymd").toString();
				
				if(companyList!=null && !"".equals(companyList)) {
					List<Map<String, Object>> enterCds = mapper.readValue(companyList, new ArrayList<Map<String, Object>>().getClass());
					
					if(enterCds!=null && enterCds.size()>0) {
						for(Map<String, Object> m : enterCds) {
							for(String enterCd : m.keySet()) {
								Map<String, Object> cMap = new HashMap<String, Object>();
								cMap.put("tenantId", tenantId);
								cMap.put("enterCd", enterCd);
								cMap.put("ymd", WtmUtil.parseDateStr(WtmUtil.addDate(new Date(), -1) , "yyyyMMdd"));
								cMap.put("symd",  WtmUtil.parseDateStr(new Date(), "yyyyMMdd"));
								cMap.put("eymd", "29991231");
								cMap.put("updateId", "INTF");
								cMap.put("orgChartNm", "조직도");
								int uCnt = orgChartMapper.updateOrgChartEymd(cMap);
								logger.debug("WtmOrgChart update "+uCnt+" end");
								System.out.println("WtmOrgChart update "+uCnt+" end");
								int iCnt = orgChartMapper.insertOrgChart(cMap);
								logger.debug("WtmOrgChart insert "+iCnt+" end");
								System.out.println("WtmOrgChart insert "+iCnt+" end");
							
								
								WtmOrgChart orgChart = orgChartRepo.findByTenantIdAndEnterCdAndBetweenSymdAndEymd(tenantId, enterCd, WtmUtil.parseDateStr(new Date(), "yyyyMMdd"));
								//chart det
								if(orgChart!=null) {
									System.out.println("orgChartId : " + orgChart.getOrgChartId());
									
									Map<String, Object> dMap = new HashMap<String, Object>();
									dMap.put("orgChartId", orgChart.getOrgChartId());
									dMap.put("ymd", WtmUtil.parseDateStr(WtmUtil.addDate(new Date(), -1) , "yyyyMMdd"));
									dMap.put("ymdhis", paramMap.get("ymdhis"));
									dMap.put("updateId", "INTF");
									
									 uCnt = iuerpInterfaceMapper.updateWtmOrgChart(dMap);
									 logger.debug("WtmOrgChartDet update "+uCnt+" end");
									 System.out.println("WtmOrgChartDet update "+uCnt+" end");
									 iCnt = iuerpInterfaceMapper.insertWtmOrgChart(dMap);
									 logger.debug("WtmOrgChartDet insert "+iCnt+" end");
									 System.out.println("WtmOrgChartDet insert "+iCnt+" end");
								}
							}
						}
					}
				}
			}
			
			
		} catch(Exception e) {
			e.printStackTrace();
			rp.setFail("WtmOrgCodeEtc 데이터 이관 오류");
			return rp;
		}
		
		int applyCnt = expireCnt+deleteCnt+updateCnt+insertCnt;
		if(applyCnt!=0)
			rp.setSuccess(applyCnt+"건(expire:"+expireCnt+",delete:"+deleteCnt+",update:"+updateCnt+",insert:"+insertCnt+") 반영완료");
		else
			rp.setSuccess("반영완료");
		
		return rp;
	}
	
	//직원연락처
	protected ReturnParam saveWtmEmpAddr(Map<String, Object> paramMap) {
		ReturnParam rp = new ReturnParam();
		
		int deleteCnt = 0;
		int updateCnt = 0;
		int insertCnt = 0;
		
		try {
			deleteCnt = iuerpInterfaceMapper.expireWtmEmpAddr(paramMap);
			logger.debug("WtmEmpAddr delete "+deleteCnt+" end");
			System.out.println("WtmEmpAddr delete "+deleteCnt+" end");
			
			updateCnt = iuerpInterfaceMapper.updateWtmEmpAddr(paramMap);
			logger.debug("WtmEmpAddr update "+updateCnt+" end");
			System.out.println("WtmEmpAddr update "+updateCnt+" end");
			
			insertCnt = iuerpInterfaceMapper.insertWtmEmpAddr(paramMap);
			logger.debug("WtmEmpAddr insert "+insertCnt+" end");
			System.out.println("WtmEmpAddr insert "+insertCnt+" end");
		} catch(Exception e) {
			e.printStackTrace();
			rp.setFail("WtmEmpAddr 데이터 이관 오류");
			return rp;
		}
		
		
		int applyCnt = deleteCnt+updateCnt+insertCnt;
		if(applyCnt!=0)
			rp.setSuccess(applyCnt+"건(delete:"+deleteCnt+",update:"+updateCnt+",insert:"+insertCnt+") 반영완료");
		else
			rp.setSuccess("반영완료");
		
		return rp;
	}
	
	//근태 신청서
	protected ReturnParam saveWtmTaaAppl(Map<String, Object> paramMap) {
		ReturnParam rp = new ReturnParam();
		
		try {
			
			List<WtmIntfTaaAppl> taaAppls = intfTaaApplRepo.findByYyyymmddhhmissGreaterThanAndTenantId(paramMap.get("ymdhis").toString(), Long.valueOf(paramMap.get("tenantId").toString()));
			System.out.println("============================== " + taaAppls.size());
			int total = 0;
			int success = 0;
			if(taaAppls!=null && taaAppls.size()>0) {
				total = taaAppls.size();
				for(WtmIntfTaaAppl a : taaAppls) {
					HashMap<String, Object> taaApplMap = new HashMap<>();
					taaApplMap.put("tenantId", a.getTenantId());
					taaApplMap.put("enterCd", a.getEnterCd());
					taaApplMap.put("sabun", a.getSabun());
					taaApplMap.put("taaCd", a.getGntCd());
					taaApplMap.put("sYmd", a.getSymd());
					taaApplMap.put("eYmd", a.getEymd());
					taaApplMap.put("sHm", a.getShm());
					taaApplMap.put("eHm", a.getEhm());
					taaApplMap.put("status", a.getApplStatusCd());
					taaApplMap.put("ifApplNo", Long.valueOf(a.getApplSeq()));
					
					if(a.getApplSeq()!=null && !"".equals(a.getApplSeq())) {
						rp = wtmInterfaceService.setTaaApplIf(taaApplMap);
						
						if(rp.getStatus()!=null && !"OK".equals(rp.getStatus())) {
							//return rp;
							
							Map<String, Object> dayMap = taaApplMap;
							dayMap.put("symd", taaApplMap.get("sYmd"));
							dayMap.put("eymd", taaApplMap.get("eYmd"));
							dayMap.put("type", taaApplMap.get("TAAAPPL"));
							dayMap.put("msg", rp.get("message"));
							System.out.println("saveWtmTaaAppl FAIL" + dayMap.toString());
							wtmInterfaceMapper.insertErrorLogBatch(dayMap);
						}else {
							success++;
						}
					} 
					System.out.println("============================== " + total + "/" +success);
				}
			}
			
			rp.setSuccess("WtmTaaAppl total : "+total + "succeess : "+ success +" 반영완료");
		} catch(Exception e) {
			e.printStackTrace();
			rp.setFail("WtmTaaAppl 데이터 이관 오류");
			return rp;
		}
		
		return rp;
	}
	
	
}
