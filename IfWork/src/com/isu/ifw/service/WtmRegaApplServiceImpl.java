package com.isu.ifw.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isu.ifw.entity.*;
import com.isu.ifw.mapper.WtmValidatorMapper;
import com.isu.ifw.repository.*;
import com.isu.ifw.util.WtmUtil;
import com.isu.ifw.vo.ReturnParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("WtmRegaApplService")
public class WtmRegaApplServiceImpl implements WtmApplService {

	private static final Logger logger = LoggerFactory.getLogger("ifwFileLog");

	@Autowired
	WtmApplRepository wtmApplRepo;

	@Autowired
	private WtmTaaApplRepository wtmTaaApplRepo;

	@Autowired
	private WtmTaaCodeRepository wtmTaaCodeRepo;

	@Autowired
	WtmTaaCodeRepository taaCodeRepo;

	@Autowired
	private WtmWorkDayResultRepository dayResultRepo;

	@Autowired
	WtmValidatorMapper validatorMapper;

	@Autowired
	private WtmTaaApplDetRepository wtmTaaApplDetRepo;

	@Autowired
	private WtmWorkCalendarRepository workCalendarRepo;

	@Autowired
	WtmPropertieRepository propertieRepo;

	@Autowired
	WtmFlexibleStdMgrRepository flexStdMgrRepo;

	@Autowired
	WtmCalcServiceImpl calcService;

	@Override
	public Map<String, Object> getAppl(Long tenantId, String enterCd, String sabun, Long applId, String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> getPrevApplList(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap, String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getLastAppl(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap, String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> getApprList(Long tenantId, String enterCd, String empNo, Map<String, Object> paramMap, String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReturnParam request(Long tenantId, String enterCd, Long applId, String workTypeCd, Map<String, Object> paramMap, String sabun, String userId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReturnParam requestSync(Long tenantId, String enterCd, Map<String, Object> paramMap, String sabun, String userId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReturnParam apply(Long tenantId, String enterCd, Long applId, int apprSeq, Map<String, Object> paramMap, String sabun, String userId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReturnParam reject(Long tenantId, String enterCd, Long applId, int apprSeq, Map<String, Object> paramMap, String sabun, String userId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(Long applId) {
		// TODO Auto-generated method stub

	}

	@Transactional
	@Override
	public ReturnParam imsi(Long tenantId, String enterCd, Long applId, String workTypeCd, Map<String, Object> paramMap, String status, String sabun, String userId) throws Exception {
		ReturnParam rp = new ReturnParam();
		rp.setFail("");
		String           applSabun = sabun;
		WtmAppl          appl      = null;
		List<WtmTaaAppl> taaAppls  = wtmTaaApplRepo.findByApplId(applId);
		SimpleDateFormat ymd       = new SimpleDateFormat("yyyyMMdd");

		String workTimeCode = paramMap.get("workTimeCode").toString();
		String taaNote      = paramMap.get("note").toString();

		List<String> taaDateArr = (List<String>) paramMap.get("taaDateArr");
		List<String> startHmArr = (List<String>) paramMap.get("startHmArr");
		List<String> endHmArr   = (List<String>) paramMap.get("endHmArr");

		try {

			//기신청 데이터
			if (taaAppls == null || taaAppls.size() == 0) {
				//logger.debug("works.size() : " + works.size());
				//if(works != null && works.size() > 0) {
				//신청 또는 승인 완료 건에 대해서만
				if (WtmApplService.APPL_STATUS_IMSI.equals(status) || WtmApplService.APPL_STATUS_APPLY_ING.equals(status) || WtmApplService.APPL_STATUS_APPR.equals(status) || WtmApplService.APPL_STATUS_CANCEL.equals(status)) {
					//appl = wtmApplRepo.findByTenantIdAndEnterCdAndIfApplNo(tenantId, enterCd, ifApplNo);
					if (appl == null) {
						appl = new WtmAppl();
						appl.setTenantId(tenantId);
						appl.setEnterCd(enterCd);
						appl.setIfApplNo(null);
						appl.setApplYmd(ymd.format(new Date()));
					} else {
						//있으면 문제다. 데이터 동기화 작업이 필요. 99일 경우
						//preApplStatus = appl.getApplStatusCd();
					}
					appl.setApplCd(WtmApplService.TIME_TYPE_REGA);
					appl.setApplSabun(applSabun);
					appl.setApplInSabun(applSabun);
					appl.setApplStatusCd(status);
					appl.setUpdateId("TAA_INTF");

					appl = wtmApplRepo.save(appl);

					applId = appl.getApplId();

					//for(Map<String, Object> w : works) {
					//String sabun = w.get("sabun")+"";

					//if(w.containsKey("worksDet") && w.get("worksDet") != null && !"".equals(w.get("worksDet")+"")) {

					WtmTaaAppl taaAppl = new WtmTaaAppl();
					taaAppl.setTenantId(tenantId);
					taaAppl.setEnterCd(enterCd);
					taaAppl.setApplId(appl.getApplId());
					taaAppl.setSabun(sabun);
					taaAppl.setIfApplNo(null);
					taaAppl.setUpdateId(userId);

					taaAppl = wtmTaaApplRepo.save(taaAppl);

					//List<Map<String, Object>> worksDet = (List<Map<String, Object>>) w.get("worksDet");
					//for(Map<String, Object> work : worksDet) {

					if (workTimeCode != "" && taaDateArr.size() > 0 && startHmArr.size() > 0 && endHmArr.size() > 0) {


						for (int i = 0; i < taaDateArr.size(); i++) {

							String taaDate = taaDateArr.get(i).replaceAll("-", "");
							String shm = startHmArr.get(i).replaceAll(":", "");
							String ehm = endHmArr.get(i).replaceAll(":", "");


							String taaCd = paramMap.get("workTimeCode").toString();
							String symd = taaDate;
							String eymd = taaDate;



							WtmTaaCode taaCode = wtmTaaCodeRepo.findByTenantIdAndEnterCdAndTaaCd(tenantId, enterCd, taaCd);
							if (taaCode == null || taaCode.getRequestTypeCd() == null || taaCode.getRequestTypeCd().equals("N")) {
								throw new RuntimeException("근태신청이 불가능한 근태코드입니다. ");
							}

							logger.debug("마감여부 체크 : ");
							List<WtmWorkCalendar> calendars = workCalendarRepo.findByTenantIdAndEnterCdAndSabunAndYmdBetween(tenantId, enterCd, sabun, symd, eymd);
							if (calendars != null && calendars.size() > 0) {
								for (WtmWorkCalendar c : calendars) {
									if ("Y".equals(c.getWorkCloseYn())) {
										throw new RuntimeException("신청 기간 내 마감된 근무일이 존재합니다.");
									}
								}
							} else {
								throw new RuntimeException("캘린더 정보가 없습니다.");
							}

							if (Integer.parseInt(symd) > Integer.parseInt(eymd)) {
								throw new RuntimeException("시작일자가 종료일보다 클 수 없습니다.");
							}


							WtmTaaApplDet taaApplDet = new WtmTaaApplDet();
							taaApplDet.setTaaApplId(taaAppl.getTaaApplId());
							taaApplDet.setTaaCd(workTimeCode);
							taaApplDet.setSymd(taaDate);
							taaApplDet.setEymd(taaDate);
							taaApplDet.setShm(shm);
							taaApplDet.setEhm(ehm);
							taaApplDet.setUpdateId("TAA_INTF");
							taaApplDet.setNote(taaNote);

							wtmTaaApplDetRepo.save(taaApplDet);


						}
					} else {
						rp.setFail("출장/긴급근무 신청데이터가 존재하지 않습니다.");
						throw new RuntimeException("근태정보가 부족합니다.");
					}
					//}
					//}else {
					//	throw new RuntimeException(sabun + " 님의 근무 상세정보가 없습니다.");
					//}
					//}

				}
				//}
			} else {
				//
			}

			rp.put("applId", applId);
			rp.setSuccess("");
		}catch (Exception e){
			rp.setFail("출장/긴급근무 신청시 에러가 발생하였습니다, 신청데이터를 확인해주세요");
			e.printStackTrace();
			throw new RuntimeException("근태정보가 부족합니다.");
		}



		return rp;
	}

	@Override
	public ReturnParam preCheck(Long tenantId, String enterCd, String sabun, String workTypeCd, Map<String, Object> paramMap) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReturnParam validate(Long tenantId, String enterCd, String sabun, String workTypeCd, Map<String, Object> work) throws ParseException {

		ReturnParam rp = new ReturnParam();
		rp.setSuccess("저장이 성공하였습니다.");


		String workTimeCode = work.get("workTimeCode").toString();
		String note         = work.get("note").toString();

		List<String> taaDateArr = (List<String>) work.get("taaDateArr");
		List<String> startHmArr = (List<String>) work.get("startHmArr");
		List<String> endHmArr   = (List<String>) work.get("endHmArr");

		List<HashMap<String, Date>> dateList = new ArrayList<HashMap<String, Date>>();

		SimpleDateFormat inFormat = new SimpleDateFormat("yyyyMMddHHmm");

		boolean isDuplicateDt = false;

		if (taaDateArr.size() > 0) {
			for (int i = 0; i < taaDateArr.size(); i++) {

				String taaDate = taaDateArr.get(i).replaceAll("-", "");
				String startHm = startHmArr.get(i).replaceAll(":", "");
				String endHm   = endHmArr.get(i).replaceAll(":", "");

				Map<String, Object> tmpMap = new HashMap<String, Object>();
				tmpMap.put("startYmd", taaDate);
				tmpMap.put("endYmd", taaDate);
				tmpMap.put("startHm", startHm);
				tmpMap.put("endHm", endHm);
				tmpMap.put("workTimeCode", workTimeCode);
				tmpMap.put("note", note);
				tmpMap.put("requestCd", "");    //  validation 기본 키값

				HashMap<String, Date> dateMap = new HashMap<String, Date>();
				dateMap.put("startDt", inFormat.parse(taaDate+startHm));
				dateMap.put("entDt", inFormat.parse(taaDate+endHm));
				dateList.add(dateMap);

				ReturnParam valiRp = new ReturnParam();
				valiRp = validate2(tenantId, enterCd, sabun, WtmApplService.TIME_TYPE_REGA, tmpMap);

				if (valiRp != null && valiRp.getStatus() != null && "OK".equals(valiRp.getStatus())) {
					rp = valiRp;
				}

			}


			for (int i = 1; i < dateList.size(); i++) {

				HashMap<String, Date> dateMap1 = dateList.get(i);

				Date dtt1 = dateList.get(i - 1).get("startDt");
				Date dtt2 = dateList.get(i - 1).get("entDt");

				if (checkBetween(dtt1, dateMap1.get("startDt"), dateMap1.get("entDt"))) {
					isDuplicateDt = true;
				}
				if (checkBetween(dtt2, dateMap1.get("startDt"), dateMap1.get("entDt"))) {
					isDuplicateDt = true;
				}

				for (int j = i + 1; j < dateList.size(); j++) {

					HashMap<String, Date> dateMap2 = dateList.get(j);

					if (checkBetween(dtt1, dateMap2.get("startDt"), dateMap2.get("entDt"))) {
						isDuplicateDt = true;
					}
					if (checkBetween(dtt2, dateMap2.get("startDt"), dateMap2.get("entDt"))) {
						isDuplicateDt = true;
					}
				}
			}

			if(isDuplicateDt){
				rp.setFail("출장/긴급근무 신청기간이 중복됩니다.");
			}

			
		} else {
			rp.setFail("출장/긴급근무 신청데이터가 존재하지 않습니다.");
		}

		return rp;
	}

	/**
	 * WTM_TAA_APPL_DET 기간 중복 벨리데이션 체크
	 * @param dateToCheck
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public boolean checkBetween(Date dateToCheck, Date startDate, Date endDate) {
		return dateToCheck.compareTo(startDate) >= 0 && dateToCheck.compareTo(endDate) <=0;
	}

	/**
	 * 출장/비상근무 > 신청기간 리스트 Validate
	 *
	 * @param tenantId
	 * @param enterCd
	 * @param sabun
	 * @param workTypeCd
	 * @param work
	 * @return
	 */
	protected ReturnParam validate2(Long tenantId, String enterCd, String sabun, String workTypeCd, Map<String, Object> work) {

		ReturnParam rp = new ReturnParam();


		if (sabun != null && !"".equals(sabun)) {


			if (work.get("startYmd") == null || "".equals(work.get("startYmd")) || work.get("endYmd") == null || "".equals(work.get("endYmd"))) {
				rp.setFail(sabun + "의 일자 정보가 없습니다.");
				return rp;
			}

			if (work.get("workTimeCode") == null || "".equals(work.get("workTimeCode"))) {
				rp.setFail(sabun + "의 근태 코드 정보가 없습니다.");
				return rp;
			}

			String workTimeCode = work.get("workTimeCode").toString();
			String requestCd = work.get("requestCd").toString();

			String symd = work.get("startYmd").toString();
			String eymd = work.get("endYmd").toString();

			if (symd.length() != eymd.length()) {
				rp.setFail("시작일과 종료일의 날짜 포맷이 맞지 않습니다.");
				return rp;
			}

			if (symd.length() != 8) {
				rp.setFail("일자는 8자리 입니다.");
				return rp;
			}

			Date sd = WtmUtil.toDate(symd, "yyyyMMdd");
			Date ed = WtmUtil.toDate(eymd, "yyyyMMdd");

			if (sd.compareTo(ed) > 0) {
				rp.setFail("시작일자가 종료일자보다 큽니다.");
				return rp;
			}

			//  totDays, holDays
			Map<String, Integer> calMap = calcService.calcDayCnt(tenantId, enterCd, symd, eymd);
			logger.debug("calMap.toString() : " + calMap.toString());

			//  발생일수, 사용일수
			Integer totalCnt = calMap.get("totDays");
			Integer usedCnt  = calMap.get("totDays") - calMap.get("holDays");

			String shm = "";
			String ehm = "";
			if (work.get("startHm") != null && !"".equals(work.get("startHm"))) {
				shm = work.get("startHm").toString();
			}
			if (work.get("endHm") != null && !"".equals(work.get("endHm"))) {
				ehm = work.get("endHm").toString();
			}

			String applId = ""; //  IF 소스 참조로 빈값으로 대체함.
			if (work.containsKey("applId")) {
				applId = work.get("applId") + "";
			}

			//신청서 중복 체크
			rp = checkDuplicateTaaAppl(tenantId, enterCd, sabun, workTimeCode, symd, eymd, shm, ehm, applId);

			if (rp.getStatus() != null && !"OK".equals(rp.getStatus())) return rp;


			List<Map<String, Object>> worksDet = new ArrayList<Map<String, Object>>();
			worksDet.add(work);

			//근무시간 체크
			return checkWorktimeTaaAppl(tenantId, enterCd, sabun, worksDet);

		}


		return rp;
	}

	protected ReturnParam checkDuplicateTaaAppl(Long tenantId, String enterCd, String sabun, String workTimeCode, String symd, String eymd, String shm, String ehm, String applId) {
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		paramMap.put("taaCd", workTimeCode);
		paramMap.put("applId", applId);

		paramMap.put("symd", symd);
		paramMap.put("eymd", eymd);

		paramMap.put("shm", shm);
		paramMap.put("ehm", ehm);

		Map<String, Object> m = validatorMapper.checkDuplicateTaaAppl(paramMap);

		if (m != null && m.get("cnt") != null) {
			int cnt = Integer.parseInt(m.get("cnt").toString());
			logger.debug("checkDuplicateTaaAppl cnt : " + cnt);
			if (cnt > 0) {

				rp.setFail(m.get("empNm").toString() + "(" + sabun + ") " + WtmUtil.parseDateStr(WtmUtil.toDate(symd, "yyyyMMdd"), "yyyy-MM-dd") + "~" + WtmUtil
						.parseDateStr(WtmUtil.toDate(eymd, "yyyyMMdd"), "yyyy-MM-dd") + "의 신청중인 또는 이미 적용된 근무정보가 있습니다.");
				return rp;
			}
		}

		return rp;
	}

	protected ReturnParam checkWorktimeTaaAppl(Long tenantId, String enterCd, String sabun, List<Map<String, Object>> appl) {
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");

		// 20200710 전영수과장 근무시간체크 해제요청(이효정메일전달)
		if (tenantId == 22) {
			return rp;
		}

		String sDate = null;
		String eDate = null;
		int    i     = 0;
		for (Map<String, Object> a : appl) {
			String symd = a.get("startYmd").toString();
			String eymd = a.get("endYmd").toString();

			if (i == 0) {
				sDate = symd;
				eDate = eymd;
			} else {
				if (symd.compareTo(sDate) == -1) sDate = symd;
				if (eymd.compareTo(eDate) == 1) eDate = eymd;
			}
			i++;
		}

		Map<String, Object>       paramMap    = null;
		List<Map<String, Object>> applMinutes = new ArrayList<Map<String, Object>>();
		String                    taaWorkYn   = "Y";    // 선근제 체크용
		for (Map<String, Object> a : appl) {
			String workTimeCode = a.get("workTimeCode").toString();
			String symd         = a.get("startYmd").toString();
			String eymd         = a.get("endYmd").toString();
			String shm          = a.get("startHm").toString();
			String ehm          = a.get("endHm").toString();

			WtmTaaCode taaCode = taaCodeRepo.findByTenantIdAndEnterCdAndTaaCd(tenantId, enterCd, workTimeCode);
			if (taaCode == null) {
				rp.setFail("근태 코드 정보가 없습니다.");
				return rp;
			}

			int applMinute = 0;

			if ((shm == null || "".equals(shm)) && (ehm == null || "".equals(ehm))) {
				//간주근무시간
				if (taaCode.getWorkApprHour() != null && !"".equals(taaCode.getWorkApprHour())) {
					applMinute = taaCode.getWorkApprHour() * 60;
				} else {
					//일 기본근무시간
					applMinute = 8 * 60;
					WtmPropertie propertie = propertieRepo.findByTenantIdAndEnterCdAndInfoKey(tenantId, enterCd, "OPTION_DEFAULT_WORKTIME");
					if (propertie != null && propertie.getInfoValue() != null && !"".equals(propertie.getInfoValue()) && !"0".equals(propertie.getInfoValue())) {
						applMinute = Integer.parseInt(propertie.getInfoValue()) * 60;
					}
				}
			} else {

				//시간이 들어오는 경우는 symd == eymd

				Date sd = WtmUtil.toDate(symd + shm, "yyyyMMddHHmm");
				Date ed = WtmUtil.toDate(symd + ehm, "yyyyMMddHHmm");

				applMinute = (int) (ed.getTime() - sd.getTime()) / (60 * 1000);
			}

			System.out.println("applMinute : " + applMinute);

			paramMap = new HashMap<>();
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			paramMap.put("sabun", sabun);
			paramMap.put("symd", symd);
			paramMap.put("eymd", eymd);

			paramMap.put("applMinute", applMinute);

			Date s = WtmUtil.toDate(symd, "yyyyMMdd");
			Date e = WtmUtil.toDate(eymd, "yyyyMMdd");

			do {
				String ymd = WtmUtil.parseDateStr(s, "yyyyMMdd");
				paramMap.put("ymd", ymd);
				// WtmFlexibleEmp flexibleEmp = flexEmpRepo.findByTenantIdAndEnterCdAndSabunAndYmdBetween(tenantId, enterCd, sabun, ymd);
				WtmFlexibleStdMgr flexibleStdMgr = flexStdMgrRepo.findByTenantIdAndEnterCdAndSabunAndYmdBetween(tenantId, enterCd, sabun, ymd);
				taaWorkYn = flexibleStdMgr.getTaaWorkYn();
				WtmWorkCalendar workCalendar = workCalendarRepo.findByTenantIdAndEnterCdAndSabunAndYmd(tenantId, enterCd, sabun, ymd);
				if ("Y".equals(taaCode.getHolInclYn()) || ("N".equals(taaCode.getHolInclYn()) && "N".equals(workCalendar.getHolidayYn()))) {
					// 선근제는 해당없음. 전체구간을 검증할꺼니깐 시간가산만 하자
					if (!"SELE_C".equals(flexibleStdMgr.getWorkTypeCd()) && !"SELE_F".equals(flexibleStdMgr.getWorkTypeCd())) {
						//1.근무 계획 시간보다 근태 신청 시간이 더 큰지 체크
						Map<String, Object> m = validatorMapper.checkApplMinute(paramMap);

						if (m == null) {
							rp.setFail(WtmUtil.parseDateStr(s, "yyyy-MM-dd") + "의 근무 시간 정보가 존재하지 않습니다.");
							return rp;
						}

						if ("N".equals(m.get("isValid").toString())) {
							rp.setFail(m.get("empNm").toString() + "(" + sabun + ") " + WtmUtil.parseDateStr(s, "yyyy-MM-dd") + "의 신청 시간이 근무 시간을 초과할 수 없습니다.");
							return rp;
						}

					}
					Map<String, Object> applMinMap = new HashMap<String, Object>();
					applMinMap.put("ymd", ymd);
					applMinMap.put("applMinute", applMinute);
					applMinutes.add(applMinMap);

					// 날짜 더하기
					Calendar cal = Calendar.getInstance();
					cal.setTime(s);
					cal.add(Calendar.DATE, 1);
					s = cal.getTime();

				} else {
					// 무한루프방지 날짜 더하기
					Calendar cal = Calendar.getInstance();
					cal.setTime(s);
					cal.add(Calendar.DATE, 1);
					s = cal.getTime();
				}

			} while (s.compareTo(e) == 0);

		}

		//2.선근제의 경우에는 해당 선근제 근무 기간 내의 소정근로시간을 넘지 않는지 체크
		paramMap = new HashMap<>();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		paramMap.put("symd", sDate);
		paramMap.put("eymd", eDate);
		paramMap.put("taaWorkYn", taaWorkYn);    // 근무시간가산여부(가산이 아니면 해당기간 기본근무 빼고 계산해야함)
		paramMap.put("applMinutes", applMinutes);
		System.out.println("*********** paramMap : " + paramMap.toString());
		List<Map<String, Object>> results = validatorMapper.checkTotalWorkMinuteForSele(paramMap);

		ObjectMapper mapper = new ObjectMapper();
		try {
			System.out.println("results : " + mapper.writeValueAsString(results));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (results != null && results.size() > 0) {
			for (Map<String, Object> r : results) {
				if (r.get("isValid") != null && "N".equals(r.get("isValid"))) {
					System.out.println("workMinute: " + r.get("workMinute").toString());
					System.out.println("totalWorkMinute: " + r.get("totalWorkMinute").toString());

					Double h = 0d;
					Double m = 0d;

					h = Double.parseDouble(r.get("workMinute").toString()) / 60;
					h = Math.ceil(h * 100) / 100.0;
					m = (h - h.intValue()) * 60;

					rp.setFail("선택근무제 총 근무 시간(" + ((h.intValue() > 0) ? String.format("%02d", h.intValue()) : "00") + "시간" + ((m.intValue() > 0) ? String.format("%02d", m.intValue()) + "분" : "") + ")을 초과할 수 없습니다.");
					return rp;
				}
			}

		}

		return rp;
	}

	@Override
	public void sendPush() {
		// TODO Auto-generated method stub

	}

	@Override
	public ReturnParam saveWtmApplSts(Long tenantId, String enterCd, String sabun, String userId, Map<String, Object> convertMap) {
		// TODO Auto-generated method stub
		return null;
	}

}