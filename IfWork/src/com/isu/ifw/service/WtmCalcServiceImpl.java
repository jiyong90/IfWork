package com.isu.ifw.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isu.ifw.entity.WtmFlexibleStdMgr;
import com.isu.ifw.entity.WtmTimeBreakMgr;
import com.isu.ifw.entity.WtmWorkDayResult;
import com.isu.ifw.mapper.WtmCalcMapper;
import com.isu.ifw.repository.WtmFlexibleStdMgrRepository;
import com.isu.ifw.repository.WtmTimeBreakMgrRepository;
import com.isu.ifw.repository.WtmWorkDayResultRepository;

@Service
public class WtmCalcServiceImpl implements WtmCalcService {

	@Autowired
	private WtmWorkDayResultRepository workDayResultRepo;
	
	@Autowired
	private WtmFlexibleStdMgrRepository flexibleStdMgrRepo;
	
	@Autowired 
	private WtmTimeBreakMgrRepository timebreakMgrRepo;
	
	@Autowired
	private WtmCalcMapper calcMapper;
	
	@Override
	public void P_WTM_WORK_DAY_RESULT_CREATE_N(Long tenantId, String enterCd,  String sabun, String ymd, String userId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("tenantId", tenantId);
		paramMap.put("tenantId", tenantId);
		
		
		Map<String, Object> workMinuteMap = calcMapper.getTotalWorkMinuteAndRealWorkMinute(paramMap);
		if(workMinuteMap != null) {
			// ymd가 속한 근무제의 총 소정근로 시간.
			int workMinute = Integer.parseInt(workMinuteMap.get("workMinute")+"");
			// ymd가 속한 근무제의 인정 소정근로 시간.
			int sumWorkMinute = Integer.parseInt(workMinuteMap.get("sumWorkMinute")+"");
			
			// 소정근로 시간이 이미 다 찼으면 소정근로 시간을 생성하지 않는다. 
			if( workMinute > sumWorkMinute ) {
				Map<String, Object> calendarMap = calcMapper.getCalendarInfoByYmdAndEntryIsNotNullAndisNotHoliday(paramMap);

				ObjectMapper mapper = new ObjectMapper();
				if(calendarMap != null) {
					try {
						System.out.println("calendarMap : " + mapper.writeValueAsString(calendarMap));
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
					
					List<WtmWorkDayResult> dayResult = workDayResultRepo.findByTimeTypeCdAndTenantIdAndEnterCdAndSabunAndYmd(WtmApplService.TIME_TYPE_BASE, tenantId, enterCd, sabun, ymd);
					if(dayResult == null || dayResult.size() == 0) {
						Date entrySdate = (Date) (calendarMap.get("entrySdate"));
						Date entryEdate = (Date) (calendarMap.get("entryEdate"));
						int unitMinute = Integer.parseInt(calendarMap.get("unitMinute")+"");
						/**
						 * P_WTM_WORK_DAY_RESULT_CREATE_T
						 */
						
					}
					
				}
			}else {
				
			}
		}
		
		
		/*
		// 이미 소정근로시간이 다 찼을 경우 남아있는 남아 있는 계획 시간들은 없애햐한다.
		// 이유는 계획시간으로 남아있으면 인정시간으로 계산하기 때문에 없애야한다. 그냥 지울수 없으니 다른 타입으로 백업을 하자.
		if(workMinute > 0 && workMinute == sumWorkMinute) {
			List<WtmWorkDayResult> dayResult = workDayResultRepo.findByTimeTypeCdAndTenantIdAndEnterCdAndSabunAndYmd(WtmApplService.TIME_TYPE_BASE, tenantId, enterCd, sabun, ymd);
			if(dayResult != null) {
				dayResult.setApprSdate(null);
				dayResult.setApprEdate(null);
				dayResult.setApprMinute(null);
				dayResult.setUpdateDate(new Date());
				dayResult.setUpdateId(userId);
				
				workDayResultRepo.save(dayResult);
				
			}
		}
		*/
		
	}

	public void P_WTM_WORK_DAY_RESULT_CREATE_T(Long tenantId, String enterCd, String sabun, String ymd, Long timeCdMgrId, Date entrySdate, Date entryEdate, String workShm, String workEhm, int unitMinute, String timeTypeCd, String breakTypeCd, int limitMinute, int useMinute, String userId) {
		/**
		 * 단위시간 계산을 위해 자른다. 
		 */
		Date sDate = this.WorkTimeCalcApprDate(entrySdate, entryEdate, unitMinute, "S");
		Date eDate = this.WorkTimeCalcApprDate(entrySdate, entryEdate, unitMinute, "E");
		
		SimpleDateFormat yMdHm = new SimpleDateFormat("yyyyMMddHHmm");
		SimpleDateFormat HHmm = new SimpleDateFormat("HHmm");
		
		//근무정보를 가지고 온다. 
		WtmFlexibleStdMgr flexibleStdMgr = flexibleStdMgrRepo.findByTenantIdAndEnterCdAndSabunAndYmdBetween(tenantId, enterCd, sabun, ymd);
		
		//연장근무 시간이 아니면
		if(!timeTypeCd.equals(WtmApplService.TIME_TYPE_OT) && !timeTypeCd.equals(WtmApplService.TIME_TYPE_NIGHT)) {
			// 등록된 시간이 시작 시분보다 종료 시분이 적으면 0시가 넘어간 시간이다. 근무일 다음날을 종료 시간으로 셋팅 하기 위함이다.
			String shm = flexibleStdMgr.getWorkShm();
			String ehm = flexibleStdMgr.getWorkEhm();
			
			if(shm != null && !shm.equals("") && ehm != null && !ehm.equals("")) {
				try {
					Date limitSdate = yMdHm.parse(ymd+String.format("%02d", shm));
					Date limitEdate = yMdHm.parse(ymd+String.format("%02d", ehm));
					//  종료시간이 작을 경우 다음날로 본다. 
					if(Integer.parseInt(shm) > Integer.parseInt(ehm)) {
						Calendar cal = Calendar.getInstance();
						cal.setTime(limitEdate);
						cal.add(Calendar.DATE, 1);
						limitEdate = cal.getTime();
					}
					
					//근무제한시간보다 종료시간이 클 경우 
					if(eDate.compareTo(limitEdate) > 0) {
						//근무제한시간으로 자른다. 
						eDate = limitEdate;
					}
					
					if(sDate.compareTo(limitSdate) < 0 && eDate.compareTo(limitEdate) > -1) {
						sDate = limitSdate;
					}
					
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		
		int apprMinute = 0;
		int breakMinute = 0;
		// P_LIMIT_MINUTE (총소정근로시간) 에서 P_USE_MINUTE (합산 소정 근로시간) 을 뺀 남은 소정 근로 시간에 대해서만 근무 정보를 생성한다.. 
		// 근무시간을 계산 하자
		if(breakTypeCd.equals(WtmApplService.BREAK_TYPE_MGR)) {
			
			if(sDate.compareTo(eDate) < 1) {
				List<WtmTimeBreakMgr> timeBreakMgrs = timebreakMgrRepo.findByTimeCdMgrId(timeCdMgrId);
				int sumBreakMinute = 0;
				for(WtmTimeBreakMgr timeBreakMgr : timeBreakMgrs) {
					sumBreakMinute = sumBreakMinute + this.WtmCalcMinute(timeBreakMgr.getShm(), timeBreakMgr.getEhm(), HHmm.format(sDate), HHmm.format(eDate), unitMinute);
						
				}
			}
		}
		
	}
	
	public int WtmCalcMinute(String shm, String ehm, String limitShm, String limitEhm, Integer unitMinute) {
		if(shm != null && !shm.equals("") && ehm != null && !ehm.equals("") ) {
			if(unitMinute == null || unitMinute.equals("") || unitMinute < 0) {
				unitMinute = 1;
			}
			String strShm = "";
			String strEhm = "";
			int resM = 0;
			//제한된 시간 구간이 없다면 시작 종료 시간으로만 계산한다.
			if(limitShm == null || limitShm.equals("") || limitEhm == null || limitEhm.equals("") ) {
				//종료 시간이 시작시간보다 적으면 24시간을 더해준다. 
				if(Integer.parseInt(shm) > Integer.parseInt(ehm)) {
					strEhm = (Integer.parseInt(ehm) + 2400) + "";
				}else {
					strEhm = ehm;
				}
				strShm = shm;
				
				resM = (Integer.parseInt(strEhm.substring(0, 2)) * 60 + Integer.parseInt(strEhm.substring(2, 4))
						- Integer.parseInt(strShm.substring(0, 2)) * 60 + Integer.parseInt(strShm.substring(2, 4)));
				return resM;
			}else {
				//종료시간이 0시면 2400으로 변경해준다.
				if(ehm.equals("0000")) {
					ehm = "2400";
				}
				
				if(Integer.parseInt(shm) > Integer.parseInt(ehm) ) {
					return 0;
				}
				// 0800 ~ 0900   2300 ~ 0900 일때 휴게시간 1시간이 나와야 할 경우
		        // 제한 시분이 다음날로 넘어갈 경우
				if(Integer.parseInt(limitShm) > Integer.parseInt(limitEhm)) {
					
					// 24시전까지 먼저 체크한다.
					if( (Integer.parseInt(shm) >= Integer.parseInt(limitShm) && Integer.parseInt(shm) <= 2400) 
							|| ( Integer.parseInt(ehm) >= Integer.parseInt(limitEhm) &&  Integer.parseInt(ehm) <= 2400) ) {
						
						if( Integer.parseInt(shm) >= Integer.parseInt(limitShm) && Integer.parseInt(shm) <= 2400) {
							strShm = shm;
						}else {
							strShm = limitShm;
						}
						
						if( Integer.parseInt(ehm) >= Integer.parseInt(limitShm) && Integer.parseInt(ehm) <= 2400) {
							strEhm = ehm;
						}else {
							strEhm = "2400";
						}
						
						resM = (Integer.parseInt(strEhm.substring(0, 2)) * 60 + Integer.parseInt(strEhm.substring(2, 4))
								- Integer.parseInt(strShm.substring(0, 2)) * 60 + Integer.parseInt(strShm.substring(2, 4)));
						return resM;
					}else if( (Integer.parseInt(shm) >= 0 && Integer.parseInt(shm) <= Integer.parseInt(limitEhm))
							|| (Integer.parseInt(ehm) >= 0 && Integer.parseInt(ehm) <= Integer.parseInt(limitEhm))
							) {
						//0시부터체크
						strShm = shm;
						if( Integer.parseInt(ehm) >= 0 && Integer.parseInt(ehm) <= Integer.parseInt(limitEhm) ) {
							strEhm = ehm;
						}else {
							strEhm = limitEhm;
						}
						resM = (Integer.parseInt(strEhm.substring(0, 2)) * 60 + Integer.parseInt(strEhm.substring(2, 4))
								- Integer.parseInt(strShm.substring(0, 2)) * 60 + Integer.parseInt(strShm.substring(2, 4)));
						return resM;
						
					}else {
						return 0;
					}
				}else {
					
					strShm = shm;
					
					if(Integer.parseInt(shm) > Integer.parseInt(ehm)) {
						strEhm = (Integer.parseInt(ehm) + 2400)+"";
					}else {
						strEhm = ehm;
					}
					
					if(Integer.parseInt(limitShm) > Integer.parseInt(limitEhm)) {
						limitEhm = (Integer.parseInt(limitEhm) + 2400)+"";
					}else {
						limitEhm = limitEhm;
					}
					
					if( (Integer.parseInt(shm) >= Integer.parseInt(limitShm) && Integer.parseInt(shm) <= Integer.parseInt(limitEhm))
							|| (Integer.parseInt(ehm) >= Integer.parseInt(limitShm) && Integer.parseInt(ehm) <= Integer.parseInt(limitEhm))
					) {
						if(Integer.parseInt(shm) >= Integer.parseInt(limitShm) && Integer.parseInt(shm) <= Integer.parseInt(limitEhm)) {
							strShm = shm;
						}else {
							strShm = limitShm;
						}
						
						if(Integer.parseInt(ehm) >= Integer.parseInt(limitShm) && Integer.parseInt(ehm) <= Integer.parseInt(limitEhm)) {
							strEhm = ehm;
						}else {
							strEhm = limitEhm;
						}

						resM = (Integer.parseInt(strEhm.substring(0, 2)) * 60 + Integer.parseInt(strEhm.substring(2, 4))
								- Integer.parseInt(strShm.substring(0, 2)) * 60 + Integer.parseInt(strShm.substring(2, 4)));
						return resM;
					}else {
						return 0;
					}
				}
			} 
		}
		return 0;
	}
	
	@Override
	public Date WorkTimeCalcApprDate(Date rDt, Date dt, int unitMinute, String calcType) {

		SimpleDateFormat dH = new SimpleDateFormat("H");
		SimpleDateFormat dM = new SimpleDateFormat("m");
		SimpleDateFormat dYmd = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
		

		//시
		int h = Integer.parseInt(dH.format(rDt));
		//분
		int m = Integer.parseInt(dM.format(rDt));
		
		// 지각일 경우 
		if(calcType.equals("S")) {
			/**
			 * rDt > dt : 1
			 * rDt == dt : 0
			 * rDy < dt : -1
			 */
			
			//타각 시간이 계획시간보다 작을 경우 정상
			if(rDt.compareTo(dt) < 1) {
				return dt;
			}else {
				// 분으로 계산
				//int totMinute = h * m;
				// 지각은 단위시간으로 계산 시 이후 시간으로 해야한다 . 
				// 10분 단위 일 경우 9시 8분일 경우 9시 10분으로 인정되어햔다.
				
				//단위 시간 적용
				int calcM = ((m + unitMinute) - (m + unitMinute)%unitMinute)%60;

				Calendar cal = Calendar.getInstance();
				try {
					cal.setTime(df.parse(dYmd.format(rDt)+String.format("%02d",h)+String.format("%02d",calcM)));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				// 9시 58분의 경우 10시가 되어야 하고
				// 23시 58분의 경우 다음날 0시가 되어야 한다 .
				if(m > calcM) {
					//58 > 0 이라 1시간을 더하자
					cal.add(Calendar.HOUR, 1);
				}
				return cal.getTime();
			}
		} else {
			if(rDt.compareTo(dt) > -1) {
				return dt;
			} else {

				//단위 시간 적용
				int calcM = m - m%unitMinute;
				Calendar cal = Calendar.getInstance();
				try {
					cal.setTime(df.parse(dYmd.format(rDt)+String.format("%02d",h)+String.format("%02d",calcM)));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				return cal.getTime();
			}
		}
	}

}
