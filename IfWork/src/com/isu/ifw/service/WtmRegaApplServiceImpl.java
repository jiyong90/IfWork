package com.isu.ifw.service;

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

	@Autowired
	WtmFlexibleEmpService wtmFlexibleEmpService;

	@Autowired
	WtmApplLineRepository wtmApplLineRepo;

	@Autowired
	WtmInboxService inbox;

	@Autowired
	WtmInterfaceService interfaceService;

	@Autowired
	WtmApplLineService applLineService;

	@Autowired
	WtmApplCodeRepository wtmApplCodeRepo;

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

	/**
	 * 출장 신청
	 * @param tenantId
	 * @param enterCd
	 * @param applId
	 * @param workTypeCd
	 * @param paramMap
	 * @param sabun
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@Transactional
	@Override
	public ReturnParam request(Long tenantId, String enterCd, Long applId, String workTypeCd, Map<String, Object> paramMap, String sabun, String userId) throws Exception {

		ReturnParam rp = new ReturnParam();
		rp = imsi(tenantId, enterCd, applId, workTypeCd, paramMap, WtmApplService.APPL_STATUS_APPLY_ING, sabun, userId);

		List<String> apprSabun = new ArrayList();
		if(rp!=null && rp.getStatus()!=null && "OK".equals(rp.getStatus())) {

			applId = Long.valueOf(rp.get("applId").toString());
			List<WtmApplLine> lines = wtmApplLineRepo.findByApplIdOrderByApprTypeCdAscApprSeqAsc(applId);

			if(lines != null && lines.size() > 0) {
				for(WtmApplLine line : lines) {

					if(APPL_LINE_I.equals(line.getApprTypeCd())) { //기안
						//첫번째 결재자의 상태만 변경 후 스탑
						line.setApprStatusCd(APPR_STATUS_APPLY);
						line.setApprDate(new Date());
						line = wtmApplLineRepo.save(line);
					} else if(APPL_LINE_S.equals(line.getApprTypeCd()) || APPL_LINE_R.equals(line.getApprTypeCd())) { //결재
						//첫번째 결재자의 상태만 변경 후 스탑
						apprSabun.add(line.getApprSabun());
						line.setApprStatusCd(APPR_STATUS_REQUEST);
						line = wtmApplLineRepo.save(line);
						break;
					}

				}
			}
			inbox.setInbox(tenantId, enterCd, apprSabun, applId, workTypeCd, "결재요청 : 출장신청", "출장신청이 완료되었습니다.", "Y");
			rp.setSuccess("저장이 성공하였습니다.");
		}

		return rp;
	}

	@Override
	public ReturnParam requestSync(Long tenantId, String enterCd, Map<String, Object> paramMap, String sabun, String userId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 결재 승인
	 * @param tenantId
	 * @param enterCd
	 * @param applId
	 * @param apprSeq
	 * @param paramMap
	 * @param sabun
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@Override
	public ReturnParam apply(Long tenantId, String enterCd, Long applId, int apprSeq, Map<String, Object> paramMap, String sabun, String userId) throws Exception {
		ReturnParam rp = new ReturnParam();

		rp.setFail("");
		paramMap.put("applId", applId);

		ObjectMapper mapper = new ObjectMapper();

		//신청 대상자
		List<String> applSabuns = null;
		if(paramMap.get("applSabuns")!=null && !"".equals(paramMap.get("applSabuns"))) {
			applSabuns = mapper.readValue(paramMap.get("applSabuns").toString(), new ArrayList<String>().getClass());
		} else {
			//개인 신청
			applSabuns = new ArrayList<String>();
			applSabuns.add(sabun);
		}

		logger.debug("휴가 승인 대상자 : " + mapper.writeValueAsString(applSabuns));

		//결재라인 상태값 업데이트
		//WtmApplLine line = wtmApplLineRepo.findByApplIdAndApprSeq(applId, apprSeq);
		List<WtmApplLine> lines = wtmApplLineRepo.findByApplIdOrderByApprSeqAsc(applId);

		String apprSabun = null;
		//마지막 결재자인지 확인하자
		boolean lastAppr = false;
		if(lines != null && lines.size() > 0) {
			for(int i = 0; i < lines.size(); i++) {
				WtmApplLine line = lines.get(i);

				if(line.getApprSeq() == apprSeq && line.getApprSabun().equals(sabun)) {
					line.setApprStatusCd(APPR_STATUS_APPLY);
					line.setApprDate(new Date());
					//결재의견
					if(paramMap != null && paramMap.containsKey("apprOpinion")) {
						line.setApprOpinion(paramMap.get("apprOpinion").toString());
						line.setUpdateId(userId);
					}
					apprSabun = line.getApprSabun();
					line = wtmApplLineRepo.save(line);
					lastAppr = true;
				}else {
					if(lastAppr) {
						line.setApprStatusCd(APPR_STATUS_REQUEST);
						line = wtmApplLineRepo.save(line);
						apprSabun = line.getApprSabun();
					}
					lastAppr = false;
				}
			}
		}

		WtmAppl appl = wtmApplRepo.findById(applId).get();

		if(lastAppr) {

			List<WtmTaaApplDet> taaApplDets = wtmTaaApplDetRepo.findByApplId(applId);
			rp.setFail("");
			String status = "99";

			//변경 전의 상태값을 가지고 있는데 99였다가 44가 될 경우엔 RESULT를 다시 생성해야하기 때문이다.
			//21 > 44 는 괜찮다.
			String preApplStatus = null;

			if (appl != null) {
				preApplStatus = appl.getApplStatusCd();
				if (!preApplStatus.equals(status)) {

					appl.setApplStatusCd(status);
					wtmApplRepo.save(appl);

					for (WtmTaaApplDet taaApplDet : taaApplDets) {
						Map<String, Object> work = new HashMap<String, Object>();
						work.put("startYmd", taaApplDet.getSymd());
						work.put("endYmd", taaApplDet.getEymd());
						work.put("workTimeCode", taaApplDet.getTaaCd());

						WtmTaaAppl taaAppl = wtmTaaApplRepo.findById(taaApplDet.getTaaApplId()).get();

						logger.debug("===============================taaResult======================================= ");
						logger.debug("status : " + status);

						List<String> statusList = new ArrayList<String>();
						statusList.add(WtmApplService.APPL_STATUS_APPLY_ING);
						statusList.add(WtmApplService.APPL_STATUS_APPLY_REJECT);
						statusList.add(WtmApplService.APPL_STATUS_APPR);
						statusList.add(WtmApplService.APPL_STATUS_APPR_ING);
						statusList.add(WtmApplService.APPL_STATUS_APPR_REJECT);
						statusList.add(WtmApplService.APPL_STATUS_CANCEL);
						statusList.add(WtmApplService.APPL_STATUS_IMSI);

						if (statusList.indexOf(status) == -1) {
							throw new RuntimeException("지원하지 않은 신청서 상태코드");
						}

						logger.debug("============================== HIS END ");
						logger.debug("============================== preApplStatus : " + preApplStatus);
						logger.debug("============================== status : " + status);
						//이건 상태랑 같으면 무시
						if (preApplStatus == null || !preApplStatus.equals(status)) {

							if (status.equals(WtmApplService.APPL_STATUS_APPR)
									|| status.equals(WtmApplService.APPL_STATUS_APPR_REJECT)
									|| status.equals(WtmApplService.APPL_STATUS_CANCEL)) {

								//List<Map<String, Object>> worksDet = new ArrayList<Map<String,Object>>();

								SimpleDateFormat ymd = new SimpleDateFormat("yyyyMMdd");
								Calendar cal1 = Calendar.getInstance();
								Calendar cal2 = Calendar.getInstance();
								try {
									cal1.setTime(ymd.parse(taaApplDet.getSymd()));
									cal2.setTime(ymd.parse(taaApplDet.getEymd()));
								} catch (ParseException e) {
									e.printStackTrace();
								}

								Date d1 = cal1.getTime();
								Date d2 = cal2.getTime();
								while (d1.compareTo(d2) < 1) {
									logger.debug("cal1 : " + d1);
									logger.debug("cal2 : " + d2);
									String d = ymd.format(d1);
									cal1.add(Calendar.DATE, 1);

									d1 = cal1.getTime();

									interfaceService.resetTaaResult(appl.getTenantId(), appl.getEnterCd(), appl.getApplSabun(), d);

								}
							}
						}
					}
					rp.setSuccess("결재가 완료되었습니다.");
				}
			}

			appl.setApplStatusCd((lastAppr) ? APPL_STATUS_APPR : APPL_STATUS_APPLY_ING);
			appl.setApplYmd(WtmUtil.parseDateStr(new Date(), null));
			appl.setUpdateId(userId);

			appl = wtmApplRepo.save(appl);
		}
		List<String> pushSabun = new ArrayList();
		if(lastAppr) {
			pushSabun.addAll(applSabuns);
			inbox.setInbox(tenantId, enterCd, pushSabun, applId, "APPLY", "결재완료", "출장 신청서가  승인되었습니다.", "N");

			rp.put("msgType", "APPLY");
		} else {
			pushSabun.add(apprSabun);
			inbox.setInbox(tenantId, enterCd, pushSabun, applId, "APPR", "결재요청 : 출장 신청", "", "N");

			rp.put("msgType", "APPR");
		}

		return rp;
	}

	@Override
	public ReturnParam reject(Long tenantId, String enterCd, Long applId, int apprSeq, Map<String, Object> paramMap, String sabun, String userId) throws Exception {
		ReturnParam rp = new ReturnParam();

		if(paramMap == null || !paramMap.containsKey("apprOpinion") && paramMap.get("apprOpinion").equals("")) {
			rp.setFail("사유를 입력하세요.");
			throw new Exception("사유를 입력하세요.");
		}
		List<String> applSabun = new ArrayList();
		applSabun.add(paramMap.get("applSabun").toString());
//		String applSabun = paramMap.get("applSabun").toString();
		String apprOpinion = paramMap.get("apprOpinion").toString();

		List<WtmApplLine> lines = wtmApplLineRepo.findByApplIdOrderByApprSeqAsc(applId);
		if(lines != null && lines.size() > 0) {
			for(WtmApplLine line : lines) {
				if(line.getApprSeq() <= apprSeq) {
					line.setApprStatusCd(APPR_STATUS_REJECT);
					//반려일때는 date가 안들어가도 되는건지?? 확인해보기
					line.setApprDate(new Date());
					if(line.getApprSeq() == apprSeq) {
						line.setApprOpinion(apprOpinion);
					}
				}else {
					line.setApprStatusCd("");
				}
				line.setUpdateId(userId);
				wtmApplLineRepo.save(line);
			}
		}
		WtmAppl appl = wtmApplRepo.findById(applId).get();
		appl.setApplStatusCd(APPL_STATUS_APPLY_REJECT);
		appl.setApplYmd(WtmUtil.parseDateStr(new Date(), null));
		appl.setUpdateId(userId);
		wtmApplRepo.save(appl);

		inbox.setInbox(tenantId, enterCd, applSabun, applId, "APPLY", "결재완료", "출장 신청서가 반려되었습니다.", "N");

		//메일 전송을 위한 파라미터
		rp.put("from", sabun);
		rp.put("to", applSabun);

		return rp;
	}

	@Override
	public void delete(Long applId) {
		// TODO Auto-generated method stub

	}

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

		List<String> taaSdateArr = (List<String>) paramMap.get("taaSdateArr");
		List<String> taaEdateArr = (List<String>) paramMap.get("taaEdateArr");
		List<String> startHmArr = (List<String>) paramMap.get("startHmArr");
		List<String> endHmArr   = (List<String>) paramMap.get("endHmArr");

		WtmApplCode applCode = getApplInfo(tenantId, enterCd, workTypeCd);

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
					appl.setUpdateId("WTM_REGA");

					appl = wtmApplRepo.save(appl);

					applId = appl.getApplId();

					//IF에도 번호를 넣어주자 추후에 승인시 필요하다..
					if(applId != null) {
						appl.setIfApplNo(applId.toString());
						appl = wtmApplRepo.save(appl);
					}


					//for(Map<String, Object> w : works) {
					//String sabun = w.get("sabun")+"";

					//if(w.containsKey("worksDet") && w.get("worksDet") != null && !"".equals(w.get("worksDet")+"")) {

					String taaCd = "";
					if(paramMap.containsKey("workTimeCode") && paramMap.get("workTimeCode") != null ){
						taaCd = paramMap.get("workTimeCode").toString();
					}

					WtmTaaAppl taaAppl = new WtmTaaAppl();
					taaAppl.setTenantId(tenantId);
					taaAppl.setEnterCd(enterCd);
					taaAppl.setApplId(applId);
					taaAppl.setSabun(sabun);
					taaAppl.setIfApplNo(applId.toString());
					taaAppl.setUpdateId(userId);
					taaAppl.setTaaCd(taaCd);

					taaAppl = wtmTaaApplRepo.save(taaAppl);

					//List<Map<String, Object>> worksDet = (List<Map<String, Object>>) w.get("worksDet");
					//for(Map<String, Object> work : worksDet) {

					if (workTimeCode != null && taaSdateArr.size() > 0) {


						for (int i = 0; i < taaSdateArr.size(); i++) {

							String taaSdate = taaSdateArr.get(i).replace(".", "").replace("-", "");
							String taaEdate = taaEdateArr.get(i).replace(".", "").replace("-", "");
//							String shm = startHmArr.get(i).replaceAll(":", "");
//							String ehm = endHmArr.get(i).replaceAll(":", "");



							String symd = taaSdate;
							String eymd = taaEdate;



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
							taaApplDet.setSymd(taaSdate);
							taaApplDet.setEymd(taaEdate);
//							taaApplDet.setShm(shm);
//							taaApplDet.setEhm(ehm);
							taaApplDet.setUpdateId("WTM_REGA");
							taaApplDet.setNote(taaNote);

							wtmTaaApplDetRepo.save(taaApplDet);


						}
						applLineService.saveWtmApplLine(tenantId, enterCd, Integer.parseInt(applCode.getApplLevelCd()), applId, workTypeCd, applSabun, userId);

						rp.put("applId", applId);
						rp.setSuccess("신청이 완료되었습니다.");
					} else {
						rp.setFail("출장/긴급근무 신청데이터가 존재하지 않습니다.");
						throw new RuntimeException("근태정보가 부족합니다.");
					}

				}

			}

			rp.put("applId", applId);
			rp.setSuccess("출장신청이 완료되었습니다.");
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
		rp.setSuccess("");


		String workTimeCode = work.get("workTimeCode").toString();
//		String note         = work.get("note").toString();

		List<String> taaSdateArr = (List<String>) work.get("taaSdateArr");
		List<String> taaEdateArr = (List<String>) work.get("taaEdateArr");

		List<HashMap<String, Date>> dateList = new ArrayList<HashMap<String, Date>>();

		SimpleDateFormat inFormat = new SimpleDateFormat("yyyyMMddHHmm");

		boolean isDuplicateDt = false;

		if (taaSdateArr.size() > 0) {
			for (int i = 0; i < taaSdateArr.size(); i++) {

				String taaSdate = taaSdateArr.get(i).replace(".", "").replace("-", "");
				String taaEdate = taaEdateArr.get(i).replace("-", "").replace(".", "");

				Map<String, Object> tmpMap = new HashMap<String, Object>();
				tmpMap.put("startYmd", taaSdate);
				tmpMap.put("endYmd", taaEdate);
				tmpMap.put("workTimeCode", workTimeCode);
				tmpMap.put("requestCd", "");    //  validation 기본 키값
				tmpMap.put("applId", work.get("applId")); 

				//  check 중복
				Map<String, Object> chkMap = new HashMap<String, Object>();
				chkMap.put("tenantId", tenantId);
				chkMap.put("enterCd", enterCd);
				chkMap.put("sabun", sabun);
				chkMap.put("timeTypeCd", "REGA");
				chkMap.put("startYmd", taaSdate);
				chkMap.put("endYmd", taaEdate);

				logger.debug("chkMap :" + chkMap.toString());
				int chkCnt = validatorMapper.checkDuplicateWorkDayResult(chkMap) ;
				logger.debug("chkCnt :" + chkCnt);
				if(chkCnt > 0){
					rp.setFail("출장 신청기간이 중복됩니다.");
					return rp;
				};

				ReturnParam valiRp = new ReturnParam();
				valiRp = validate2(tenantId, enterCd, sabun, WtmApplService.TIME_TYPE_REGA, tmpMap);

				if (valiRp != null && valiRp.getStatus() != null && !"OK".equals(valiRp.getStatus())) {
					rp = valiRp;
				}

			}

			if (rp != null && rp.getStatus() != null && "OK".equals(rp.getStatus())) {
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

				if (isDuplicateDt) {
					rp.setFail("출장/긴급근무 신청기간이 중복됩니다.");
				}
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
			if (work.containsKey("startHm") && work.get("startHm") != null && !"".equals(work.get("startHm"))) {
				shm = work.get("startHm").toString();
			}
			if (work.containsKey("endHm") && work.get("endHm") != null && !"".equals(work.get("endHm"))) {
				ehm = work.get("endHm").toString();
			}

			//  시작시간이 종료시간보다 작으면 안된다. (단 24:00, 00:00시는 제외)
			if(!"".equals(shm) && !"".equals(ehm)
				&& (!"0000".equals(ehm) && !"2400".equals(ehm))){
				Integer stmInt = Integer.parseInt(shm);
				Integer etmInt = Integer.parseInt(ehm);
				if(stmInt > etmInt) {
					rp.setFail("종료시간이 시작시간보다 작습니다.");
					return rp;
				}

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
			String shm          = "";
			String ehm          = "";

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

				Map<String, Object> reCalc = new HashMap<>();
				reCalc.put("tenentId", tenantId);
				reCalc.put("enterCd", enterCd);
				reCalc.put("sabun", sabun);
				reCalc.put("ymd", symd);
				reCalc.put("shm", shm);
				reCalc.put("ehm", ehm);

				//시간이 들어오는 경우는 symd == eymd
				Map<String, Object> addPlanMinuteMap = wtmFlexibleEmpService.calcMinuteExceptBreaktime(tenantId, enterCd, sabun, reCalc, sabun);

				applMinute = Integer.parseInt(addPlanMinuteMap.get("calcMinute")+"");

//				Date sd = WtmUtil.toDate(symd + shm, "yyyyMMddHHmm");
//				Date ed = WtmUtil.toDate(symd + ehm, "yyyyMMddHHmm");
//
//				applMinute = (int) (ed.getTime() - sd.getTime()) / (60 * 1000);
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

//		//2.선근제의 경우에는 해당 선근제 근무 기간 내의 기본근로시간을 넘지 않는지 체크
//		paramMap = new HashMap<>();
//		paramMap.put("tenantId", tenantId);
//		paramMap.put("enterCd", enterCd);
//		paramMap.put("sabun", sabun);
//		paramMap.put("symd", sDate);
//		paramMap.put("eymd", eDate);
//		paramMap.put("taaWorkYn", taaWorkYn);    // 근무시간가산여부(가산이 아니면 해당기간 기본근무 빼고 계산해야함)
//		paramMap.put("applMinutes", applMinutes);
//		logger.debug("*********** paramMap : " + paramMap.toString());
//		List<Map<String, Object>> results = validatorMapper.checkTotalWorkMinuteForSele(paramMap);
//
//		ObjectMapper mapper = new ObjectMapper();
//		try {
//			logger.debug("results : " + mapper.writeValueAsString(results));
//		} catch (JsonProcessingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

//		if (results != null && results.size() > 0) {
//			for (Map<String, Object> r : results) {
//				if (r.get("isValid") != null && "N".equals(r.get("isValid"))) {
//					logger.debug("workMinute: " + r.get("workMinute").toString());
//					logger.debug("totalWorkMinute: " + r.get("totalWorkMinute").toString());
//
//					Double h = 0d;
//					Double m = 0d;
//
//					h = Double.parseDouble(r.get("workMinute").toString()) / 60;
//					h = Math.ceil(h * 100) / 100.0;
//					m = (h - h.intValue()) * 60;
//
//					rp.setFail("선택근무제 총 근무 시간(" + ((h.intValue() > 0) ? String.format("%02d", h.intValue()) : "00") + "시간" + ((m.intValue() > 0) ? String.format("%02d", m.intValue()) + "분" : "") + ")을 초과할 수 없습니다.");
//					return rp;
//				}
//			}
//
//		}

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

	protected WtmApplCode getApplInfo(Long tenantId,String enterCd,String applCd) {
		return wtmApplCodeRepo.findByTenantIdAndEnterCdAndApplCd(tenantId, enterCd, applCd);
	}

}
