package com.isu.ifw.service; 

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isu.ifw.entity.WtmBaseWorkMgr;
import com.isu.ifw.entity.WtmDayMgr;
import com.isu.ifw.entity.WtmEmpHis;
import com.isu.ifw.entity.WtmFlexibleEmp;
import com.isu.ifw.entity.WtmFlexibleStdMgr;
import com.isu.ifw.entity.WtmHolidayMgr;
import com.isu.ifw.entity.WtmTaaAppl;
import com.isu.ifw.entity.WtmTaaApplDet;
import com.isu.ifw.entity.WtmTaaCode;
import com.isu.ifw.entity.WtmTimeCdMgr;
import com.isu.ifw.entity.WtmWorkCalendar;
import com.isu.ifw.entity.WtmWorkDayResult;
import com.isu.ifw.entity.WtmWorkPattDet;
import com.isu.ifw.entity.WtmWorkteamEmp;
import com.isu.ifw.entity.WtmWorkteamMgr;
import com.isu.ifw.mapper.WtmFlexibleApplMapper;
import com.isu.ifw.mapper.WtmFlexibleApplyMgrMapper;
import com.isu.ifw.mapper.WtmFlexibleEmpMapper;
import com.isu.ifw.repository.WtmBaseWorkMgrRepository;
import com.isu.ifw.repository.WtmDayMgrRepository;
import com.isu.ifw.repository.WtmEmpHisRepository;
import com.isu.ifw.repository.WtmFlexibleApplyDetRepository;
import com.isu.ifw.repository.WtmFlexibleApplyMgrRepository;
import com.isu.ifw.repository.WtmFlexibleEmpRepository;
import com.isu.ifw.repository.WtmFlexibleStdMgrRepository;
import com.isu.ifw.repository.WtmHolidayMgrRepository;
import com.isu.ifw.repository.WtmPropertieRepository;
import com.isu.ifw.repository.WtmRuleRepository;
import com.isu.ifw.repository.WtmTaaApplDetRepository;
import com.isu.ifw.repository.WtmTaaApplRepository;
import com.isu.ifw.repository.WtmTaaCodeRepository;
import com.isu.ifw.repository.WtmTimeCdMgrRepository;
import com.isu.ifw.repository.WtmWorkCalendarRepository;
import com.isu.ifw.repository.WtmWorkDayResultRepository;
import com.isu.ifw.repository.WtmWorkPattDetRepository;
import com.isu.ifw.repository.WtmWorkteamEmpRepository;
import com.isu.ifw.repository.WtmWorkteamMgrRepository;

@Service
public class WtmFlexibleEmpResetServiceImpl implements WtmFlexibleEmpResetService{
	
	private final Logger logger = LoggerFactory.getLogger("ifwFileLog");
	
	@Resource
	WtmFlexibleApplyMgrRepository flexibleApplyRepository;
	
	@Autowired
	WtmFlexibleStdMgrRepository flexStdMgrRepo;
	
	@Autowired
	WtmFlexibleEmpRepository wtmFlexibleEmpRepo;
//	@Resource
//	WtmFlexibleApplyGrpRepository flexibleApplyGrpRepository;
//	@Resource
//	WtmFlexibleApplyEmpRepository flexibleApplyEmpRepository;
	
	@Autowired
	WtmFlexibleApplyMgrMapper wtmFlexibleApplyMgrMapper;
	
	@Autowired
	WtmFlexibleEmpMapper wtmFlexEmpMapper;
	
	@Autowired
	WtmFlexibleApplMapper flexApplMapper;
	
	@Autowired
	@Qualifier("wtmFlexibleApplService")
	WtmApplService wtmApplService;
	
	@Autowired
	WtmWorkPattDetRepository workPattDetRepo;
	
	@Autowired
	WtmFlexibleApplyDetRepository flexibleApplyDetRepo;
	
	@Autowired
	WtmFlexibleEmpService flexibleEmpService;
	
	@Autowired
	WtmWorkCalendarRepository workCalendarRepo;
	
	@Autowired
	WtmWorkDayResultRepository wtmWorkDayResultRepo;

	@Autowired
	WtmPropertieRepository propertieRepo;
	
	@Autowired WtmRuleRepository ruleRepo;

	@Autowired private WtmEmpHisRepository empHisRepo;

	@Autowired private WtmBaseWorkMgrRepository baseWorkMgrRepo;
	@Autowired private WtmWorkteamEmpRepository workteamEmpRepo;
	@Autowired private WtmWorkteamMgrRepository workteamMgrRepo;
	@Autowired private WtmTimeCdMgrRepository timeCdMgrRepo;
	@Autowired private WtmDayMgrRepository dayMgrRepo;
	@Autowired private WtmHolidayMgrRepository holidayMgrRepo;
	@Autowired private WtmCalcService calcService;
	
	@Autowired private WtmTaaApplRepository taaApplRepo;
	@Autowired private WtmTaaApplDetRepository taaApplDetRepo;
	@Autowired private WtmTaaCodeRepository taaCodeRepo;
	@Transactional
	@Override
	public void P_WTM_FLEXIBLE_EMP_RESET(Long tenantId, String enterCd, String sabun, String sYmd, String eYmd, String userId) throws Exception {
		//년단위로 생성하자
		//기본근무는 년단위로 생성한다. 
		//공휴일 정보 적용 등 필요
		//sYmd = "2020" + "0101"; 
		//eYmd = "2020" + "1231";
		
		String empYmd = empHisRepo.findMaxEmpYmdByTenantIdAndEnterCdAndSabun(tenantId, enterCd, sabun);
		logger.debug("입사일 : " + empYmd);
		
		if(Integer.parseInt(sYmd) < Integer.parseInt(empYmd)) {
			sYmd = empYmd;
		}
		logger.debug("1. 초기화"); 
		this.initWtmFlexibleEmp(tenantId, enterCd, sabun, sYmd, eYmd, userId);
		logger.debug("1. 초기화 END");
		wtmFlexibleEmpRepo.flush();
		
		List<WtmFlexibleEmp> emps = wtmFlexibleEmpRepo.findByTenantIdAndEnterCdAndSabunAndEymdGreaterThanEqualAndSymdLessThanEqual(tenantId, enterCd, sabun, sYmd, eYmd);
		if(emps != null) {
			logger.debug("2. 초기화 한 WTM_FLEXIBLE_EMP를 다시 조회 : " + emps.size());
			for(WtmFlexibleEmp flexEmp : emps) {
				//계산 구간을 정하자. 유연근무제는 근무제 전체 기간을 체크하지만 기본근무제들은 재생성하려는 구간만 돌자
				String loopSymd = flexEmp.getSymd();
				String loopEymd = flexEmp.getEymd();
				logger.debug("flexEmp : " + flexEmp);
				WtmFlexibleStdMgr flexStdMgr = flexStdMgrRepo.findByFlexibleStdMgrId(flexEmp.getFlexibleStdMgrId());
				logger.debug("flexStdMgr : " + flexStdMgr);
				List<WtmWorkPattDet> pattDets = workPattDetRepo.findByFlexibleStdMgrId(flexStdMgr.getFlexibleStdMgrId());
				logger.debug("pattDets : " + pattDets);
				logger.debug("flexEmp : " + flexEmp.toString());
				logger.debug("flexStdMgr : " + flexStdMgr.toString());
				
				if( (flexStdMgr.getBaseWorkYn() == null || "".equals(flexStdMgr.getBaseWorkYn()) || "N".equals(flexStdMgr.getBaseWorkYn())) 
					&& flexEmp.getWorkTypeCd().startsWith("SELE")
					|| flexEmp.getWorkTypeCd().equals("DIFF")
						) {
					if(!flexEmp.getWorkTypeCd().equals("DIFF")) {
						logger.debug("선택근무제이다.");

					}
					this.P_WTM_WORK_CALENDAR_RESET(flexStdMgr, pattDets, flexEmp.getSabun(), loopSymd, loopEymd, WtmFlexibleEmpResetService.WORK_TYPE_FLEX, null, userId);

					if(flexEmp.getWorkTypeCd().equals("DIFF")) {
						logger.debug("시차출퇴근이다. RESULT RESET GO");
						/*
						List<WtmWorkDayResult> delResults = wtmWorkDayResultRepo.findByTenantIdAndEnterCdAndSabunAndYmdBetweenAndApprMinuteIsNull(tenantId, enterCd, sabun, loopSymd, loopEymd);
						if(delResults != null && delResults.size() > 0) {
							logger.debug("기본근무가 아닌 근무제 기간엔 마감되지 않은 정보는 초기화한다 : " + delResults.size());
							wtmWorkDayResultRepo.deleteAll(delResults);
						}
						*/

						// { call P_WTM_WORK_DAY_RESULT_RESET(#{tenantId}, #{enterCd}, #{pKey}, #{flexibleEmpId}, #{sYmd}, #{eYmd}, #{holExceptYn}, #{maxPattSeq}, #{pType}, #{userId}) }
						flexibleEmpService.createWtmWorkDayResultAsCalendar(flexEmp);
					}
					
				}else {
					logger.debug("기본근무제이다.");
					logger.debug("현재 근무제가 기본근무 일 경우 기본근무 인지 근무조 인지 알수가 없다. 시작일 기준으로 근무조정보가 있는지 확인한다.");
					if(Integer.parseInt(sYmd) > Integer.parseInt(flexEmp.getSymd()) && Integer.parseInt(sYmd) < Integer.parseInt(flexEmp.getEymd())) {
						logger.debug("시작일 체크 : " + flexEmp.getSymd() + " ~ " + flexEmp.getEymd() + " >> " + sYmd + " 로 시작일을 변경 한다. ");
						loopSymd = sYmd;
					}
					if(Integer.parseInt(eYmd) > Integer.parseInt(flexEmp.getSymd()) && Integer.parseInt(eYmd) < Integer.parseInt(flexEmp.getEymd())) {
						logger.debug("종료일 체크 : " + flexEmp.getSymd() + " ~ " + flexEmp.getEymd() + " >> " + eYmd + " 로 종료일을 변경 한다. ");
						loopEymd = eYmd;
					}
					List<WtmWorkteamEmp> workteams = workteamEmpRepo.findByTenantIdAndEnterCdAndSabunAndEymdGreaterThanEqualAndSymdLessThanEqualOrderBySymdAsc(tenantId, enterCd, sabun, loopSymd, loopEymd);
					if(workteams != null && workteams.size() > 0){
						logger.debug("근무조 : " + workteams.size() + " << 1 건이어야 한다.");
						for(WtmWorkteamEmp workteam : workteams) {
							this.P_WTM_WORK_CALENDAR_RESET(flexStdMgr, pattDets, flexEmp.getSabun(), loopSymd, loopEymd, WtmFlexibleEmpResetService.WORK_TYPE_BASE_WORKTEAM, workteam.getWorkteamMgrId(), userId);
						}
					}else {
						this.P_WTM_WORK_CALENDAR_RESET(flexStdMgr, pattDets, flexEmp.getSabun(), loopSymd, loopEymd, WtmFlexibleEmpResetService.WORK_TYPE_BASE, null, userId);
					}
				}
				
				if(flexEmp.getWorkTypeCd().startsWith("SELE")) {
					Map<String, Integer> calcMinute = calcService.calcFlexibleMinuteByTypeForWorkTypeFlex(flexEmp);
					flexEmp.setWorkMinute(calcMinute.get("workMinute"));
					flexEmp.setOtMinute(calcMinute.get("otMinute"));
					
					flexEmp= wtmFlexibleEmpRepo.save(flexEmp);
					logger.debug("update flexEmp : " + flexEmp);
					
					//flexibleEmpService.createWtmWorkDayResultAsCalendar(flexEmp);
					
				}
			}
		}
		//throw new RuntimeException();
	}

	protected void initWtmFlexibleEmp(Long tenantId, String enterCd, String sabun, String sYmd, String eYmd,
			String userId) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		SimpleDateFormat ymd = new SimpleDateFormat("yyyyMMdd");
		
		Date sDate = ymd.parse(sYmd);
		Date eDate = ymd.parse(eYmd);		
		logger.debug("call initWtmFlexibleEmp : " + tenantId + " : " + enterCd + " : " + sabun + " : " + sYmd + " ~ " + eYmd);
		logger.debug("1. FLEXIBLE_EMP에서 BASE근무제 정보를 지운다.");
		
		List<WtmFlexibleEmp> delBaseFlexibleemps = wtmFlexibleEmpRepo.findByTenantIdAndEnterCdAndEymdGreaterThanEqualAndSymdLessThanEqualAndBaseWorkYnIsYAndWorkTypeCdIsBASE(tenantId, enterCd, sabun, sYmd, eYmd);
		if(delBaseFlexibleemps != null && delBaseFlexibleemps.size() > 0) {
			logger.debug("1-1. " + delBaseFlexibleemps.size()+ "건 삭제");
			wtmFlexibleEmpRepo.deleteAll(delBaseFlexibleemps);
		}
		//년단위로 생성하지
		
		logger.debug("2. FLEXIBLE_EMP에 유연근무제는 유지. BASE 근무제를 생성한다. ");
		//WtmBaseWorkMgr baseWorkMgr = baseWorkMgrRepo.findByTenantIdAndEnterCdAndSymd(tenantId, enterCd, sYmd);
		List<WtmFlexibleEmp> emps = wtmFlexibleEmpRepo.findByTenantIdAndEnterCdAndSabunAndEymdGreaterThanEqualAndSymdLessThanEqual(tenantId, enterCd, sabun, sYmd, eYmd);
		
		logger.debug("3. 근무조 정보가 있는지 확인한다. ");
		List<WtmWorkteamEmp> workteamEmps = workteamEmpRepo.findByTenantIdAndEnterCdAndSabunAndEymdGreaterThanEqualAndSymdLessThanEqualOrderBySymdAsc(tenantId, enterCd, sabun, sYmd, eYmd);
		
		List<WtmBaseWorkMgr> baseWorks = baseWorkMgrRepo.findByTenantIdAndEnterCdAndEymdGreaterThanEqualAndSymdLessThanEqualOrderBySymdAsc(tenantId, enterCd, sYmd, eYmd);
		
		/**
		 * 무식하게 돌리자 머리가 안돌아간다..
		 */
		Map<String, Map<String, String>> dayMap = null; 
		logger.debug("baseWorks.size : " + baseWorks.size());
		if(baseWorks != null && baseWorks.size() > 0) {
			dayMap = new HashMap<String, Map<String, String>>();
			for(WtmBaseWorkMgr e : baseWorks) {
				Date d1 = ymd.parse(e.getSymd());
				Date d2 = ymd.parse(e.getEymd());
				
				/*
				if(sDate.compareTo(d1) < 0) {
					d1 = sDate;
				}
				if(eDate.compareTo(d2) > 0) {
					d2 = eDate;
				}
				//생성하려는 기간보다 시작일이 늦을 경우
				if(eDate.compareTo(d1) < 0) {
					continue;
				}
				*/
				
				Calendar cal = Calendar.getInstance();
				while(d1.compareTo(d2) <= 0) {
					Map<String, String> m = new HashMap<String, String>();
					m.put("flexibleStdMgrId", e.getFlexibleStdMgrId()+"");
					m.put("flexibleNm", "");
					m.put("type","B");
					dayMap.put(ymd.format(d1), m);
					cal.setTime(d1);
					cal.add(Calendar.DATE, 1);
					d1 = cal.getTime();
				}
			}
		} 
		boolean hasWorkteam = false;
		boolean hasFlexible = false;
		//근무조 정보를 덮어 쓴다 .
		if(dayMap != null && workteamEmps != null && workteamEmps.size() > 0) {
			hasWorkteam = true;
			for(WtmWorkteamEmp e : workteamEmps) {
				WtmWorkteamMgr mgr = workteamMgrRepo.findByWorkteamMgrId(e.getWorkteamMgrId());
				Long flexibleStdMgrId = mgr.getFlexibleStdMgrId();
				String flexibleNm = mgr.getWorkteamNm();
				
				Date d1 = ymd.parse(e.getSymd());
				Date d2 = ymd.parse(e.getEymd());
				/*
				if(sDate.compareTo(d1) > 0) {
					d1 = sDate;
				}
				if(eDate.compareTo(d2) < 0) {
					d2 = eDate;
				}
				//생성하려는 기간보다 시작일이 늦을 경우
				if(eDate.compareTo(d1) < 0) {
					continue;
				}
				*/
				
				Calendar cal = Calendar.getInstance();
				while(d1.compareTo(d2) <= 0) {
					Map<String, String> m = new HashMap<String, String>();
					m.put("flexibleStdMgrId", flexibleStdMgrId+"");
					m.put("flexibleNm",flexibleNm);
					m.put("type","W");
					dayMap.put(ymd.format(d1), m);
					cal.setTime(d1);
					cal.add(Calendar.DATE, 1);
					d1 = cal.getTime();
				}
				 
			}
		}  

		//마지막으로 유연근무제 정보로 지운다
		if(dayMap != null && emps != null && emps.size() > 0) {
			hasFlexible = true;
			for(WtmFlexibleEmp e : emps) {
				Date d1 = ymd.parse(e.getSymd());
				Date d2 = ymd.parse(e.getEymd());
				
				if(sDate.compareTo(d1) > 0) {
					d1 = sDate;
				}
				if(eDate.compareTo(d2) < 0) {
					d2 = eDate;
				}
				//생성하려는 기간보다 시작일이 늦을 경우
				if(eDate.compareTo(d1) < 0) {
					continue;
				}
				Calendar cal = Calendar.getInstance();
				while(d1.compareTo(d2) <= 0) {
					dayMap.remove(ymd.format(d1));
					cal.setTime(d1);
					cal.add(Calendar.DATE, 1);
					d1 = cal.getTime();
				}
			}
		} 
		//기본근무 정보는 필수다.
		if(dayMap != null) {
			//logger.debug("dayMap : " + mapper.writeValueAsString(dayMap));
			logger.debug("hasWorkteam : " + hasWorkteam);
			logger.debug("hasFlexible : " + hasFlexible);
			//신규입사일 경우 
			if(!hasWorkteam && !hasFlexible) {
				for(WtmBaseWorkMgr e : baseWorks) {
					Date d1 = ymd.parse(e.getSymd());
					Date d2 = ymd.parse(e.getEymd());
					
					if(sDate.compareTo(d1) > 0) {
						d1 = sDate;
					}
					if(eDate.compareTo(d2) < 0) {
						d2 = eDate;
					}
					//생성하려는 기간보다 시작일이 늦을 경우
					if(eDate.compareTo(d1) < 0) {
						continue;
					}
					WtmFlexibleEmp insEmp = new WtmFlexibleEmp();
					insEmp.setTenantId(tenantId);
					insEmp.setEnterCd(enterCd);
					insEmp.setSabun(sabun);
					insEmp.setSymd(ymd.format(d1));
					insEmp.setEymd(ymd.format(d2));
					insEmp.setFlexibleNm("");
					insEmp.setFlexibleStdMgrId(e.getFlexibleStdMgrId());
					insEmp.setWorkTypeCd("BASE");
					insEmp.setUpdateId(userId);
					logger.debug("insEmp: " + insEmp);
					wtmFlexibleEmpRepo.save(insEmp);
				}
			}else {
				//근무조가 있거나 유연근무제가 있는 상태에서의 초기화
				Date d1 = ymd.parse(sYmd);
				Date d2 = ymd.parse(eYmd);
				logger.debug("### d1 : "+ d1);
				logger.debug("### d2 : "+ d2);
				Calendar cal = Calendar.getInstance();
				String tmp1 = null, tmp2 = null;
				String s = null, e = null;
				
				boolean isCreate = false;
				while(d1.compareTo(d2) <= 0) {
					Map<String, String> m = null;
					String chkYmd = ymd.format(d1);
					if(dayMap.containsKey(chkYmd)) {
						m= dayMap.get(chkYmd);
						if(s == null) {
							logger.debug("::start : " + chkYmd);
							s = chkYmd;
							tmp1 = m.get("flexibleStdMgrId");
							tmp2 = m.get("type");
						}else {
							//이전 정보가 같을 경우 같은 근무제로 판단.
							if(!tmp1.equals(m.get("flexibleStdMgrId")) || 
									!tmp2.equals(m.get("type"))) {
								isCreate = true;
							}
						}
					}else {
						if(s != null) {
							isCreate = true;
						}else {
							logger.debug(chkYmd + " : 유연근무일");
						}
					}
					if(isCreate) {
						WtmFlexibleEmp insEmp = new WtmFlexibleEmp();
						insEmp.setTenantId(tenantId);
						insEmp.setEnterCd(enterCd);
						insEmp.setSabun(sabun);
						insEmp.setSymd(s);
						insEmp.setEymd(e);
						insEmp.setFlexibleNm("");
						insEmp.setFlexibleStdMgrId(Long.parseLong(tmp1));
						insEmp.setWorkTypeCd("BASE");
						insEmp.setUpdateId(userId);
						
						wtmFlexibleEmpRepo.save(insEmp);
						logger.debug("create WTM_FLEXIBLE_EMP : " + insEmp.toString());
						
						//이전일까지 생성했으니 새로 담는다.
						if(m == null) {
							s = null;
							e = null;
							tmp1 = null;
							tmp2 = null;
							logger.debug(chkYmd + " : 유연근무일2");
						}else {
							s = chkYmd; 
							tmp1 = m.get("flexibleStdMgrId");
							tmp2 = m.get("type");
						}
						isCreate = false;
					} 
					
					if(s != null) {
						e = chkYmd;
					}else {
						e = null;
					}
					
					cal.setTime(d1);
					cal.add(Calendar.DATE, 1);
					d1 = cal.getTime();
					
					if(d1.compareTo(d2) > 0) {
						logger.debug("last ymd : " + chkYmd);
						logger.debug("last s : " + s);
						logger.debug("last e : " + e);
						if(s != null && e != null) {
							//마지막
							WtmFlexibleEmp insEmp = new WtmFlexibleEmp();
							insEmp.setTenantId(tenantId);
							insEmp.setEnterCd(enterCd);
							insEmp.setSabun(sabun);
							insEmp.setSymd(s);
							insEmp.setEymd(e);
							insEmp.setFlexibleNm("");
							insEmp.setFlexibleStdMgrId(Long.parseLong(tmp1));
							insEmp.setWorkTypeCd("BASE");
							insEmp.setUpdateId(userId);
							
							wtmFlexibleEmpRepo.save(insEmp);
							logger.debug("create last WTM_FLEXIBLE_EMP : " + insEmp.toString());
						}
					}
				}
			}
		}
		
	}
	
	@Override
	public void P_WTM_WORK_CALENDAR_RESET(WtmFlexibleStdMgr flexStdMgr, List<WtmWorkPattDet> pattDets, String sabun, String sYmd, String eYmd, String workType, Long mgrId, String userId)  throws Exception {
		logger.debug("call P_WTM_WORK_CALENDAR_RESET : " + workType);
		SimpleDateFormat ymdhm = new SimpleDateFormat("yyyyMMddHHmm");
		SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
		
		// 패턴이 없으면 생성하지 않는다. 
		if(pattDets != null && pattDets.size() > 0) {
			SimpleDateFormat ymd = new SimpleDateFormat("yyyyMMdd");
			
			Integer startPattSeq = 1;
			Date sDate = ymd.parse(sYmd);
			Date eDate = ymd.parse(eYmd);
			
			Date mgrSdate = null;
			if(workType.equals(this.WORK_TYPE_BASE_WORKTEAM)) {
				WtmWorkteamMgr workteamMgr = workteamMgrRepo.findByWorkteamMgrId(mgrId);
				logger.debug("근무조 설정 기준일로 패턴 시작일을 계산한다.");
				mgrSdate = ymd.parse(workteamMgr.getSymd());
			}else if(workType.equals(this.WORK_TYPE_BASE) || workType.equals(this.WORK_TYPE_DIFF)) {
				WtmEmpHis empHis = empHisRepo.findByTenantIdAndEnterCdAndSabunAndYmd(flexStdMgr.getTenantId(), flexStdMgr.getEnterCd(), sabun, sYmd);
				empHis.getBusinessPlaceCd();
				WtmBaseWorkMgr baseWorkMgr = baseWorkMgrRepo.findByTenantIdAndEnterCdAndFlexibleStdMgrIdAndYmdAndBusinessPlaceCd(flexStdMgr.getTenantId(), flexStdMgr.getEnterCd(), flexStdMgr.getFlexibleStdMgrId(), sYmd, empHis.getBusinessPlaceCd());
				mgrSdate = ymd.parse(baseWorkMgr.getSymd());
				//mgrId
			}else if(workType.equals(this.WORK_TYPE_FLEX)) {
				mgrSdate = ymd.parse(flexStdMgr.getUseSymd());
				
			}
			logger.debug("mgrSdate :" + mgrSdate);
			logger.debug("sDate :" + sDate);
			if(sDate.compareTo(mgrSdate) == 0) { //같으면 패턴 시작은 1부터 시작한다.
				startPattSeq = 1;
			}else {
				long diff = sDate.getTime() - mgrSdate.getTime();
				long days = diff / (24*60*60*1000) + 1;
				logger.debug("days : " + days);
				startPattSeq = (int) (days % pattDets.size());
				if(startPattSeq == 0) {
					startPattSeq = pattDets.size();
				}
			}
			logger.debug("startPattSeq :" + startPattSeq);
			
			//Map<Integer, WtmWorkPattDet> pattDetMap = new HashMap<>();
			Map<Integer, WtmTimeCdMgr> timeCdMgrMap = new HashMap<>();
			for(WtmWorkPattDet pattDet : pattDets) {
				//pattDetMap.put(pattDet.getSeq(), pattDet);
				WtmTimeCdMgr timeCdMgr = timeCdMgrRepo.findById(pattDet.getTimeCdMgrId()).get();
				timeCdMgrMap.put(pattDet.getSeq(), timeCdMgr);
			}
			
			List<WtmDayMgr> dayMgrs = dayMgrRepo.findBySunYmdBetween(sYmd, eYmd);
			List<WtmHolidayMgr> hols = holidayMgrRepo.findByTenantIdAndEnterCdAndHolidayYmdBetween(flexStdMgr.getTenantId(), flexStdMgr.getEnterCd(), sYmd, eYmd);
			List<String> holList = new ArrayList<String>();
			if(dayMgrs != null && dayMgrs.size() > 0) {
				for(WtmDayMgr d : dayMgrs) {
					if(!"".equals(d.getHolidayYn()) && "Y".equals(d.getHolidayYn())) {
						holList.add(d.getSunYmd());
					}
				}
			}
			if(hols != null && hols.size() > 0) {
				for(WtmHolidayMgr d : hols) {
					holList.add(d.getId().getHolidayYmd());
				}
			}
			logger.debug("휴일 정보 : " + holList);
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(sDate);
			
			Date chkDate = sDate;
			//List<WtmFlexibleApplDet> saveDetList = new ArrayList<>();
			//logger.debug("chkDate.compareTo(applEDate) : " + chkDate.compareTo(applEDate));
			//ObjectMapper mapper = new ObjectMapper();
			while(chkDate.compareTo(eDate) <= 0) {
				cal.setTime(chkDate);
				
				//WtmWorkPattDet det = pattDetMap.get(startPattSeq);
				WtmTimeCdMgr timeCdMgr = timeCdMgrMap.get(startPattSeq); 
				startPattSeq++;
				if(startPattSeq > pattDets.size()) {
					startPattSeq = 1;
				}
				
				String currYmd = ymd.format(chkDate);
				
				logger.debug("currYmd : " + currYmd);
				
				WtmWorkCalendar calendar = workCalendarRepo.findByTenantIdAndEnterCdAndSabunAndYmd(flexStdMgr.getTenantId(), flexStdMgr.getEnterCd(), sabun, currYmd);
				if(calendar == null) {
					calendar = new WtmWorkCalendar();
					calendar.setTenantId(flexStdMgr.getTenantId());
					calendar.setEnterCd(flexStdMgr.getEnterCd());
					calendar.setYmd(currYmd);
					calendar.setSabun(sabun);
					
				}
				calendar.setTimeCdMgrId(timeCdMgr.getTimeCdMgrId());
				
				if("Y".equals(flexStdMgr.getHolExceptYn()) && holList.indexOf(currYmd) > -1 ) {
					calendar.setHolidayYn("Y");
				}else {
					//휴일이면
					if(!"".equals(timeCdMgr.getHolYn()) &&  "Y".equals(timeCdMgr.getHolYn())) {
						calendar.setHolidayYn("Y");
					}else {
						calendar.setHolidayYn("N");
					}
				}
				
				calendar.setUpdateId(userId);
				
				calendar = workCalendarRepo.save(calendar);
				logger.debug("calendar save : " + calendar);
				
				if(!workType.equals(this.WORK_TYPE_FLEX)) {
					int breakMinute = 0;
					if(calendar.getHolidayYn().equals("N")) {
						if(timeCdMgr.getWorkShm() != null && timeCdMgr.getWorkEhm() != null
								&& !"".equals(timeCdMgr.getWorkShm()) && !"".equals(timeCdMgr.getWorkEhm())) {

							String shm = timeCdMgr.getWorkShm();
							String ehm = timeCdMgr.getWorkEhm();

							logger.debug("shm : " + shm);
							logger.debug("ehm : " + ehm);
							String currEYmd = currYmd;
							Date planSdate = null, planEdate = null;
							try {
								planSdate = ymdhm.parse(currYmd+shm);
								//종료시분이 시작시분보다 작으면 기준일을 다음날로 본다. 
								if(Integer.parseInt(shm) > Integer.parseInt(ehm)) {
									cal.add(Calendar.DATE, 1);
									currEYmd = ymd.format(cal.getTime());
									//기준일이기때문에 다음날에 대한 일자 정보를 담아야한다.
								}
								planEdate = ymdhm.parse(currEYmd+ehm);
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							/*
							WtmFlexibleApplDet applDet = new WtmFlexibleApplDet();
							
							applDet.setFlexibleApplId(flexibleAppl.getFlexibleApplId());
							applDet.setTimeCdMgrId(det.getTimeCdMgrId());
							applDet.setHolidayYn(det.getHolidayYn());
							applDet.setYmd(sYmd);
							applDet.setPlanSdate(sd);
							applDet.setPlanEdate(ed);
							*/
							int planMinute = 0;
							Map<String, Object> resMap = calcService.calcApprMinute(planSdate, planEdate, timeCdMgr.getBreakTypeCd(), timeCdMgr.getTimeCdMgrId(), flexStdMgr.getUnitMinute());
							if(resMap.containsKey("apprMinute")) {
								planMinute = Integer.parseInt(resMap.get("apprMinute")+"");
								breakMinute = Integer.parseInt(resMap.get("breakMinute")+"");
							}
							logger.debug("planSdate : " + planSdate);
							logger.debug("planEdate : " + planEdate);
							logger.debug("planMinute : " + planMinute);
							//List<WtmWorkDayResult> results = wtmWorkDayResultRepo.findByTenantIdAndEnterCdAndSabunAndYmdAndTimeTypeCd(flexStdMgr.getTenantId(), flexStdMgr.getEnterCd(), sabun, currYmd, WtmApplService.TIME_TYPE_BASE);
							List<String> timeTypeCd = new ArrayList<String>();
							timeTypeCd.add(WtmApplService.TIME_TYPE_BASE);
							//timeTypeCd.add(WtmApplService.TIME_TYPE_FIXOT);
							
							List<WtmWorkDayResult> results = wtmWorkDayResultRepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdInAndYmdBetweenOrderByPlanSdateAsc(flexStdMgr.getTenantId(), flexStdMgr.getEnterCd(), sabun, timeTypeCd, currYmd, currYmd);
							if(results != null && results.size() > 0) {
								//wtmWorkDayResultRepo.deleteAll(results);
								boolean isFirst = true;
								for(WtmWorkDayResult r : results) {
									//1개 이상이면 1개는 원데이터로 다른 1개는 지워야한다.
									if(isFirst) {
										r.setPlanSdate(planSdate);
										r.setPlanEdate(planEdate);
										r.setPlanMinute(planMinute);
										r.setUpdateId(userId);
										r = wtmWorkDayResultRepo.save(r);
										logger.debug("**************");
										isFirst = false;
									}else {
										wtmWorkDayResultRepo.delete(r);
									}
								}
							}else {
								WtmWorkDayResult r = new WtmWorkDayResult();
								r.setTenantId(flexStdMgr.getTenantId());
								r.setEnterCd(flexStdMgr.getEnterCd());
								r.setSabun(sabun);
								r.setYmd(currYmd);
								r.setTimeTypeCd("BASE");
								r.setPlanSdate(planSdate);
								r.setPlanEdate(planEdate);
								r.setPlanMinute(planMinute);
								r.setUpdateId(userId);
								wtmWorkDayResultRepo.save(r);
							}
							
							

							if(timeCdMgr.getBreakTypeCd().equals(WtmApplService.BREAK_TYPE_TIME)) {
								String taaInfoCd = "BREAK";
								Integer createMinute = breakMinute;
								if(createMinute != null && createMinute > 0) {
									calcService.createWorkDayResultForBreakTime(flexStdMgr.getTenantId(), flexStdMgr.getEnterCd(), sabun, calendar.getYmd(), taaInfoCd, "PLAN", createMinute, "createWorkDayResultForBreakTime");
								}
							}

							
							List<String> timeTypeCds = new ArrayList<String>();
							timeTypeCds.add(WtmApplService.TIME_TYPE_FIXOT);
							List<WtmWorkDayResult> delFixResults = wtmWorkDayResultRepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdInAndYmdBetweenOrderByPlanSdateAsc(flexStdMgr.getTenantId(), flexStdMgr.getEnterCd(), sabun, timeTypeCds, currYmd, currYmd);
							if(delFixResults != null && delFixResults.size() > 0) {
								logger.debug("fixOt를 삭제한다 : " + delFixResults.size() );
								wtmWorkDayResultRepo.deleteAll(delFixResults);
							}
							
							if(flexStdMgr.getFixotUseType().equals("DAY")) {
								Date calcEdate = null;
								Integer fixOtMinute = 0;
								if(timeCdMgr.getBreakTypeCd().equals(WtmApplService.BREAK_TYPE_MGR)) {
									
									calcEdate = calcService.P_WTM_DATE_ADD_FOR_BREAK_MGR(planEdate, flexStdMgr.getFixotUseLimit(), timeCdMgr.getTimeCdMgrId(), flexStdMgr.getUnitMinute());

									if(flexStdMgr.getWorkShm() != null && !"".equals(flexStdMgr.getWorkShm())
											&& flexStdMgr.getWorkEhm() != null && !"".equals(flexStdMgr.getWorkEhm())
											) {
										try {
											Date limitSdate = ymdhm.parse(currYmd+flexStdMgr.getWorkShm());
											Date limitEdate = ymdhm.parse(currYmd+flexStdMgr.getWorkEhm());
										
											if(limitSdate.compareTo(limitEdate) > 0) {
												logger.debug("제한시간 셋팅이 종료시간 보다 시작시간이 늦을 경우 종료시간을 1일 더해서 다음날로 만든다. sHm : " + flexStdMgr.getWorkShm() + " eHm : " + flexStdMgr.getWorkEhm());
												Calendar cal1 = Calendar.getInstance();
												cal1.setTime(limitEdate);
												cal1.add(Calendar.DATE, 1);
												limitEdate = cal1.getTime();
											} 
							 
											if(calcEdate.compareTo(limitEdate) > 0) {
												logger.debug("종료일 근무 제한 시간 적용. eDate : " + calcEdate + " limitEdate : " + limitEdate);
												calcEdate = limitEdate;
											}
	
										} catch (ParseException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
									fixOtMinute = calcService.WtmCalcMinute(sdf.format(planEdate), sdf.format(calcEdate), null, null, flexStdMgr.getUnitMinute()) -  calcService.getBreakMinuteIfBreakTimeMGR(planEdate, calcEdate, timeCdMgr.getTimeCdMgrId(), flexStdMgr.getUnitMinute());
									logger.debug("fixOtMinute = " +  fixOtMinute);
								}else if(timeCdMgr.getBreakTypeCd().equals(WtmApplService.BREAK_TYPE_TIME)) {
									Calendar cal1 = Calendar.getInstance();
									cal1.setTime(planEdate);
									cal1.add(Calendar.MINUTE, flexStdMgr.getFixotUseLimit());
									calcEdate = cal.getTime();
									fixOtMinute = flexStdMgr.getFixotUseLimit();
								}
								
								WtmWorkDayResult fixRes = new WtmWorkDayResult();
								fixRes.setTenantId(flexStdMgr.getTenantId());
								fixRes.setEnterCd(flexStdMgr.getEnterCd());
								fixRes.setYmd(currYmd);
								fixRes.setSabun(sabun);
								fixRes.setTimeTypeCd(WtmApplService.TIME_TYPE_FIXOT);
								fixRes.setPlanSdate(planEdate);
								fixRes.setPlanEdate(calcEdate);
								fixRes.setPlanMinute(fixOtMinute);
								fixRes.setUpdateId(userId);
								wtmWorkDayResultRepo.save(fixRes);
							}
							
						}
					}else {
						// 휴일BASE 정보 삭제
						List<WtmWorkDayResult> results = wtmWorkDayResultRepo.findByTenantIdAndEnterCdAndSabunAndYmdBetweenAndApprMinuteIsNull(flexStdMgr.getTenantId(), flexStdMgr.getEnterCd(), sabun, currYmd, currYmd);
						wtmWorkDayResultRepo.deleteAll(results);
					}
					
					
				}
				
				
				
				cal.add(Calendar.DATE, 1);
				//기준일이기때문에 다음날에 대한 일자 정보를 담아야한다.
				chkDate = cal.getTime();
			}
		} 
	}

	@Override
	@Transactional
	public void P_WTM_WORK_DAY_RESULT_RESET(WtmWorkCalendar calendar, WtmFlexibleStdMgr flexStdMgr, WtmTimeCdMgr timeCdMgr, String userId)  throws Exception {
		
		//SimpleDateFormat ymd = new SimpleDateFormat("yyyyMMdd");
		//신청서 아이디가 있는 데이터를 제외하고 삭제한다 .
		List<WtmWorkDayResult> delResult = wtmWorkDayResultRepo.findByTenantIdAndEnterCdAndYmdAndSabunAndApplIdIsNull(calendar.getTenantId(), calendar.getEnterCd(), calendar.getYmd(), calendar.getSabun());
		if(delResult != null && delResult.size() > 0) {
			wtmWorkDayResultRepo.deleteAll(delResult);
		}
		boolean isCreateBase = true;
		boolean hasTaa = false;
		List<WtmWorkDayResult> applResults = wtmWorkDayResultRepo.findByTenantIdAndEnterCdAndSabunAndYmd(calendar.getTenantId(), calendar.getEnterCd(), calendar.getSabun(), calendar.getYmd());
		List<WtmWorkDayResult> taaResults = null;
		logger.debug("### applResults : " + applResults.size());
		if(applResults != null && applResults.size() > 0) {
			for(WtmWorkDayResult r : applResults) {
				r.setApprSdate(null);
				r.setApprEdate(null);
				r.setApprMinute(null);
				r.setUpdateId("");
				wtmWorkDayResultRepo.save(r);
				
				WtmTaaAppl taaAppl = taaApplRepo.findByApplIdAndSabun(r.getApplId(), calendar.getSabun());
				if(taaAppl == null)
					continue;
				List<WtmTaaApplDet> taaApplDets = taaApplDetRepo.findByTaaApplId(taaAppl.getTaaApplId());
				//근태나 출장이 아닐수 있다.
				logger.debug("### taaApplDets : " + taaApplDets.size() );
				if(taaApplDets != null && taaApplDets.size() > 0) {
					for(WtmTaaApplDet det : taaApplDets) {
						logger.debug("### if : " + (Integer.parseInt(det.getSymd()) <= Integer.parseInt(calendar.getYmd()) && Integer.parseInt(det.getEymd()) >= Integer.parseInt(calendar.getYmd()) ) );
						if(Integer.parseInt(det.getSymd()) <= Integer.parseInt(calendar.getYmd()) && Integer.parseInt(det.getEymd()) >= Integer.parseInt(calendar.getYmd()) ) {
							
							logger.debug("### det.getTaaCd() : " + det.getTaaCd());
							
							WtmTaaCode taaCode = taaCodeRepo.findByTenantIdAndEnterCdAndTaaCd(calendar.getTenantId(), calendar.getEnterCd(), det.getTaaCd());
							if(taaCode.getRequestTypeCd().equals(WtmTaaCode.REQUEST_TYPE_D)) {
								//신청서가 있는 데이터가 D일 경우 기본근무 정보를 생성하지 않는다.
								logger.debug("### 일단위 근태 또는 출장이 있다. : " + taaCode.getTaaNm() + " : " +  r.getPlanSdate() + " ~ " + r.getPlanEdate());
								isCreateBase = false;
							}else {
								if(taaResults == null)
									taaResults = new ArrayList<WtmWorkDayResult>();
								
								taaResults.add(r);		
								logger.debug("### 시간단위의 근태 또는 출장이 있다. : " + taaCode.getTaaNm() + " : " +  r.getPlanSdate() + " ~ " + r.getPlanEdate());
								hasTaa = true;
							}
						}
					}
				}
			}
		} 
		//기본근무시간 기준으로 기본근무를 생성한다. 최초 개인이 입력한 계획시간을 알수 없다.  
		if(isCreateBase) {
			flexibleEmpService.createResultByCalendar(calendar, flexStdMgr, timeCdMgr);
			//근무시간표 기준으로 기본근무 생성 후 근태 또는 출장 등의 데이터에 맞게 기본근무 시간을 자른다.
			if(hasTaa && taaResults != null) {
				for(WtmWorkDayResult taaR : taaResults) {
					Date calcSdate = calcService.WorkTimeCalcApprDate(taaR.getPlanSdate(), taaR.getPlanSdate(), flexStdMgr.getUnitMinute(), "S");
					Date calcEdate = calcService.WorkTimeCalcApprDate(taaR.getPlanEdate(), taaR.getPlanEdate(), flexStdMgr.getUnitMinute(), "E");
					//gobackResult.setApprSdate(gobackResult.getPlanSdate());
					//gobackResult.setApprEdate(gobackResult.getPlanEdate());
					flexibleEmpService.addWtmDayResultInBaseTimeType(taaR.getTenantId()
																	 , taaR.getEnterCd()
																	 , taaR.getYmd()
																	 , taaR.getSabun()
																	 , taaR.getTimeTypeCd()
																	 , ""
																	 , calcSdate
																	 , calcEdate
																	 , null
																	 , "taa reset timeblock"
																	 , false);
				}
			}
		}
	}
}