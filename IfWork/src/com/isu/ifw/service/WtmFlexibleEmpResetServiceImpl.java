package com.isu.ifw.service; 

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
import com.isu.ifw.entity.WtmFlexibleEmp;
import com.isu.ifw.entity.WtmFlexibleStdMgr;
import com.isu.ifw.entity.WtmHolidayMgr;
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
	WtmFlexibleEmpMapper flexEmpMapper;
	
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
	
	@Transactional
	@Override
	public void P_WTM_FLEXIBLE_EMP_RESET(Long tenantId, String enterCd, String sabun, String year, String userId) throws Exception {
		//년단위로 생성하자
		//기본근무는 년단위로 생성한다. 
		//공휴일 정보 적용 등 필요
		String sYmd = year + "0101"; 
		String eYmd = year + "1231";
		
		String empYmd = empHisRepo.findMaxEmpYmdByTenantIdAndEnterCdAndSabun(tenantId, enterCd, sabun);
		logger.debug("입사일 : " + empYmd);
		
		if(Integer.parseInt(sYmd) < Integer.parseInt(empYmd)) {
			sYmd = empYmd;
		}
		logger.debug("1. 초기화");
		this.initWtmFlexibleEmp(tenantId, enterCd, sabun, sYmd, eYmd, userId);
		logger.debug("1. 초기화 END");

		List<WtmFlexibleEmp> emps = wtmFlexibleEmpRepo.findByTenantIdAndEnterCdAndSabunAndEymdGreaterThanEqualAndSymdLessThanEqual(tenantId, enterCd, sabun, sYmd, eYmd);
		if(emps != null) {
			logger.debug("2. 초기화 한 WTM_FLEXIBLE_EMP를 다시 조회 : " + emps.size());
			for(WtmFlexibleEmp flexEmp : emps) {
				WtmFlexibleStdMgr flexStdMgr = flexStdMgrRepo.findByFlexibleStdMgrId(flexEmp.getFlexibleEmpId());
				List<WtmWorkPattDet> pattDets = workPattDetRepo.findByFlexibleStdMgrId(flexStdMgr.getFlexibleStdMgrId());
				
				logger.debug("flexEmp : " + flexEmp.toString());
				logger.debug("flexStdMgr : " + flexStdMgr.toString());
				
				if( (flexStdMgr.getBaseWorkYn() == null || "".equals(flexStdMgr.getBaseWorkYn()) || "N".equals(flexStdMgr.getBaseWorkYn())) 
					&& flexEmp.getWorkTypeCd().startsWith("SELE")
					|| flexEmp.getWorkTypeCd().equals("DIFF")
						) {
					if(!flexEmp.getWorkTypeCd().equals("DIFF")) {
						logger.debug("선택근무제이다.");
						List<WtmWorkDayResult> delResults = wtmWorkDayResultRepo.findByTenantIdAndEnterCdAndSabunAndYmdBetweenAndApprMinuteIsNull(tenantId, enterCd, sabun, flexEmp.getSymd(), flexEmp.getEymd());
						if(delResults != null && delResults.size() > 0) {
							logger.debug("기본근무가 아닌 근무제 기간엔 마감되지 않은 정보는 초기화한다 : " + delResults.size());
							wtmWorkDayResultRepo.deleteAll(delResults);
						}
					}
					this.P_WTM_WORK_CALENDAR_RESET(flexStdMgr, pattDets, flexEmp.getSabun(), flexEmp.getSymd(), flexEmp.getEymd(), WtmFlexibleEmpResetService.WORK_TYPE_FLEX, null, userId);

					if(flexEmp.getWorkTypeCd().equals("DIFF")) {
						logger.debug("시차출퇴근이다. RESULT RESET GO");
					}
					
				}else {
					logger.debug("기본근무제이다.");
					logger.debug("현재 근무제가 기본근무 일 경우 기본근무 인지 근무조 인지 알수가 없다. 시작일 기준으로 근무조정보가 있는지 확인한다.");
					List<WtmWorkteamEmp> workteams = workteamEmpRepo.findByTenantIdAndEnterCdAndSabunAndEymdGreaterThanEqualAndSymdLessThanEqualOrderBySymdAsc(tenantId, enterCd, sabun, flexEmp.getSymd(), flexEmp.getEymd());
					if(workteams != null && workteams.size() > 0){
						logger.debug("근무조 : " + workteams.size() + " << 1 건이어야 한다.");
						for(WtmWorkteamEmp workteam : workteams) {
							this.P_WTM_WORK_CALENDAR_RESET(flexStdMgr, pattDets, flexEmp.getSabun(), flexEmp.getSymd(), flexEmp.getEymd(), WtmFlexibleEmpResetService.WORK_TYPE_BASE_WORKTEAM, workteam.getWorkteamMgrId(), userId);
						}
					}else {
						this.P_WTM_WORK_CALENDAR_RESET(flexStdMgr, pattDets, flexEmp.getSabun(), flexEmp.getSymd(), flexEmp.getEymd(), WtmFlexibleEmpResetService.WORK_TYPE_BASE, null, userId);
					}
				}
				
				//call P_WTM_WORK_DAY_RESULT_TIME_C
			}
		}
		throw new RuntimeException();
		
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
			logger.debug("dayMap : " + mapper.writeValueAsString(dayMap));
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
					
					wtmFlexibleEmpRepo.save(insEmp);
				}
			}else {
				//근무조가 있거나 유연근무제가 있는 상태에서의 초기화
				Date d1 = ymd.parse(sYmd);
				Date d2 = ymd.parse(eYmd);
				 
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
							logger.debug(chkYmd + " : 유연근무일");
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
		
		
		// 패턴이 없으면 생성하지 않는다. 
		if(pattDets != null && pattDets.size() > 0) {
			SimpleDateFormat ymd = new SimpleDateFormat("yyyyMMdd");
			
			Integer startPattSeq = 0;
			Date sDate = ymd.parse(sYmd);
			Date eDate = ymd.parse(eYmd);
			
			Date mgrSdate = null;
			if(workType.equals(this.WORK_TYPE_BASE_WORKTEAM)) {
				WtmWorkteamMgr workteamMgr = workteamMgrRepo.findByWorkteamMgrId(mgrId);
				logger.debug("근무조 설정 기준일로 패턴 시작일을 계산한다.");
				mgrSdate = ymd.parse(workteamMgr.getSymd());
			}else if(workType.equals(this.WORK_TYPE_BASE)) {
				//mgrId
			}else if(workType.equals(this.WORK_TYPE_FLEX)) {
				mgrSdate = ymd.parse(flexStdMgr.getUseSymd());
				
			}
			logger.debug("mgrSdate :" + mgrSdate);
			logger.debug("sDate :" + sDate);
			if(sDate.compareTo(mgrSdate) == 0) { //같으면 패턴 시작은 1부터 시작한다.
				startPattSeq = 1;
			}else {
				long diff = mgrSdate.getTime() - sDate.getTime();
				long days = diff / (24*60*60*1000) + 1;
				logger.debug("days : " + days);
				startPattSeq = (int) (days % pattDets.size());
				if(startPattSeq == 0) {
					startPattSeq = pattDets.size();
				}
			}
			logger.debug("startPattSeq :" + startPattSeq);
			
			Map<Integer, WtmWorkPattDet> pattDetMap = new HashMap<>();
			Map<Integer, WtmTimeCdMgr> timeCdMgrMap = new HashMap<>();
			for(WtmWorkPattDet pattDet : pattDets) {
				pattDetMap.put(pattDet.getSeq(), pattDet);
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
				
				WtmWorkPattDet det = pattDetMap.get(startPattSeq);
				WtmTimeCdMgr timeCdMgr = timeCdMgrMap.get(startPattSeq); 
				startPattSeq++;
				if(startPattSeq > pattDets.size()) {
					startPattSeq = 1;
				}
				
				String currYmd = ymd.format(chkDate);
				WtmWorkCalendar calendar = workCalendarRepo.findByTenantIdAndEnterCdAndSabunAndYmd(flexStdMgr.getTenantId(), flexStdMgr.getEnterCd(), sabun, currYmd);
				if(calendar == null) {
					calendar = new WtmWorkCalendar();
					calendar.setTenantId(flexStdMgr.getTenantId());
					calendar.setEnterCd(flexStdMgr.getEnterCd());
					calendar.setYmd(currYmd);
					calendar.setSabun(sabun);
					calendar.setUpdateId(userId);
				}
				calendar.setTimeCdMgrId(timeCdMgr.getTimeCdMgrId());
				
				if("Y".equals(flexStdMgr.getHolExceptYn()) && holList.indexOf(sYmd) > -1 ) {
					calendar.setHolidayYn("Y");
				}else {
					//휴일이면
					if(!"".equals(timeCdMgr.getHolYn()) &&  "Y".equals(timeCdMgr.getHolYn())) {
						calendar.setHolidayYn("Y");
					}else {
						calendar.setHolidayYn("N");
					}
				}
				
				workCalendarRepo.save(calendar);
				
				cal.add(Calendar.DATE, 1);
				//기준일이기때문에 다음날에 대한 일자 정보를 담아야한다.
				chkDate = cal.getTime();
			}
		} 
	}

	@Override
	public void P_WTM_WORK_DAY_RESULT_RESET(Long tenantId, String enterCd, String sabun, String sYmd, String eYmd,
			String userId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void P_WTM_WORK_DAY_RESULT_TIME_C(Long tenantId, String enterCd, String sabun, String sYmd, String eYmd,
			String userId) {
		// TODO Auto-generated method stub
		
	}
	
}