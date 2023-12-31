package com.isu.ifw.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isu.ifw.entity.*;
import com.isu.ifw.mapper.WtmApplMapper;
import com.isu.ifw.mapper.WtmTaaApplMapper;
import com.isu.ifw.mapper.WtmValidatorMapper;
import com.isu.ifw.repository.*;
import com.isu.ifw.util.WtmUtil;
import com.isu.ifw.vo.ReturnParam;
import com.isu.ifw.vo.WtmApplLineVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("WtmTaaApplService")
public class WtmTaaApplServiceImpl implements WtmApplService{

	private static final Logger logger = LoggerFactory.getLogger("ifwFileLog");
	
	@Autowired WtmApplRepository wtmApplRepo;
	
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
	
	@Autowired private WtmWorkCalendarRepository workCalendarRepo;
	@Autowired private WtmFlexibleEmpRepository flexibleEmpRepo;

	@Autowired
	WtmPropertieRepository propertieRepo;

	@Autowired
	WtmFlexibleStdMgrRepository flexStdMgrRepo;

	@Autowired
	WtmCalcServiceImpl calcService;
	
	@Autowired private WtmTimeCdMgrRepository timeCdMgrRepo;

	@Autowired
	private WtmTaaApplMapper wtmTaaApplMapper;

	@Autowired
	private WtmApplMapper applMapper;

	@Autowired
	WtmInboxService inbox;

	@Autowired
	WtmApplLineRepository wtmApplLineRepo;

	@Autowired
	WtmApplLineService applLineService;

	@Autowired
	WtmApplCodeRepository wtmApplCodeRepo;

	@Autowired
	WtmInterfaceService interfaceService;

	@Autowired
	WtmAnnualUsedService annualUsedService;

	@Override
	public Map<String, Object> getAppl(Long tenantId, String enterCd, String sabun, Long applId, String userId) {
		WtmAppl wtmAppl =wtmApplRepo.findByApplId(applId);
		WtmTaaAppl taaAppl =wtmTaaApplRepo.findByApplIdAndSabun(applId, sabun);

		List<WtmTaaApplDet> taaApplDetList = wtmTaaApplDetRepo.findByTaaApplId(taaAppl.getTaaApplId());
		Map<String, Object> resultMap =new HashMap<String, Object>();

		WtmTaaCode wtmTaaCode = taaCodeRepo.findByTenantIdAndEnterCdAndTaaCd(tenantId,enterCd, taaApplDetList.get(0).getTaaCd());

		resultMap.put("wtmTaaCode", wtmTaaCode);


		String taaNm = "";

		Map<String, Object> taaMap =new HashMap<String, Object>();
		List<Map<String, Object>> taaList = new ArrayList<Map<String,Object>>();
		if(taaApplDetList != null && taaApplDetList.size() >0){
			for(WtmTaaApplDet taaApplDet : taaApplDetList) {

				taaMap.put("taaApplDetId", taaApplDet.getTaaApplDetId());
				taaMap.put("taaApplId", taaApplDet.getTaaApplId());
				taaMap.put("taaCd", taaApplDet.getTaaCd());
				taaMap.put("symd", taaApplDet.getSymd());
				taaMap.put("sDate", taaApplDet.getSymd());
				taaMap.put("eymd", taaApplDet.getEymd());
				taaMap.put("eDate", taaApplDet.getEymd());
				taaMap.put("shm", taaApplDet.getShm());
				taaMap.put("ehm", taaApplDet.getEhm());
				taaMap.put("note", taaApplDet.getNote());
				taaMap.put("updateDate", taaApplDet.getUpdateDate());
				taaMap.put("updateId", taaApplDet.getUpdateId());
				taaMap.put("taaMinute", taaApplDet.getTaaMinute());
				WtmTaaCode taaCode = taaCodeRepo.findByTenantIdAndEnterCdAndTaaCd(tenantId, enterCd, taaApplDet.getTaaCd());
				taaMap.put("taaNm", taaCode.getTaaNm());
				taaMap.put("applId", taaAppl.getApplId());
				taaList.add(taaMap);
				taaNm = wtmTaaCode.getTaaNm();
			}
		}

		resultMap.put("taaNm", taaNm);
		resultMap.put("taaAppl", taaAppl);
		resultMap.put("taaApplDetList", taaList);
		resultMap.put("taaApplDets", taaApplDetList);

		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			paramMap.put("sabun", sabun);
			paramMap.put("applId", applId);
			List<Map<String, Object>> taaApplList = wtmTaaApplMapper.getTaaApplList(paramMap);

			ObjectMapper mapper = new ObjectMapper();

			logger.debug("compApplList :::::: " + mapper.writeValueAsString(taaApplList));

			List<WtmApplLineVO> applLine = applMapper.getWtmApplLineByApplId(applId);
			resultMap.put("applLine", applLine);

			if(taaApplList!=null && taaApplList.size()>0) {

				List<String> sabuns = new ArrayList<String>();

				String ymd = WtmUtil.parseDateStr(new Date(), "yyyyMMdd");

				for(Map<String, Object> o : taaApplList) {
					sabuns.add(o.get("sabun").toString());
				}

				boolean isRecovery = false;

				//결재요청중일 때 회수 버튼 보여주기
				if(applLine!=null && applLine.size()>0) {
					int seq = 1;
					int rSeq = 1; // 수신처 seq
					boolean isRecIng = false;
					for(WtmApplLineVO l : applLine) {
						if(APPL_LINE_S.equals(l.getApprTypeCd())) {
							if(seq==1 && APPR_STATUS_REQUEST.equals(l.getApprStatusCd()) && sabuns.indexOf(sabun)!=-1 )
								isRecovery = true;

							seq++;
						}

						if(APPL_LINE_R.equals(l.getApprTypeCd())) {
							if(rSeq==1 && APPR_STATUS_REQUEST.equals(l.getApprStatusCd()))
								isRecIng = true;
							rSeq++;
						}
					}

					//발신결재가 없는 경우
					if(seq==1 && isRecIng) {
						isRecovery = true;
					}
				}

				resultMap.put("recoveryYn", isRecovery);
				resultMap.put("taaApplList", taaApplList);
			}

			logger.debug("taaAppl :::::: " + mapper.writeValueAsString(taaAppl));
			return resultMap;

		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public List<Map<String, Object>> getPrevApplList(Long tenantId, String enterCd, String sabun,
			Map<String, Object> paramMap, String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getLastAppl(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap,
			String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> getApprList(Long tenantId, String enterCd, String empNo,
			Map<String, Object> paramMap, String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReturnParam request(Long tenantId, String enterCd, Long applId, String workTypeCd,
			Map<String, Object> paramMap, String sabun, String userId) throws Exception {
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
			inbox.setInbox(tenantId, enterCd, apprSabun, applId, workTypeCd, "결재요청 : 휴가신청", "휴가신청이 완료되었습니다.", "Y");
			rp.setSuccess("저장이 성공하였습니다.");
		}
		return rp;
	}

	@Override
	public ReturnParam requestSync(Long tenantId, String enterCd, Map<String, Object> paramMap, String sabun,
			String userId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReturnParam apply(Long tenantId, String enterCd, Long applId, int apprSeq, Map<String, Object> paramMap,
			String sabun, String userId) throws Exception {
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

					//  연차사용 저장
					for(WtmTaaApplDet det : taaApplDets){

						String symd = det.getSymd();
						String eymd = det.getEymd();
						String yy   = symd.substring(0,4);

						String annualTaCd = det.getTaaCd();

						//  WTM_TAA_CODE.REQUEST_TYPE_CD 조회
						WtmTaaCode taaCode = wtmTaaCodeRepo.findByTenantIdAndEnterCdAndTaaCd(tenantId, enterCd, annualTaCd);
						String requestCd = taaCode.getRequestTypeCd();

						//  계산 해야함. totDays, holDays
						Map<String, Integer> calMap = calcService.calcDayCnt(tenantId, enterCd, symd, eymd);
						logger.debug("calMap.toString() : " + calMap.toString());

						Double totalCnt         = calMap.get("totDays").doubleValue();
						Double usedCnt          = totalCnt - calMap.get("holDays").doubleValue();

						switch (requestCd) {
							case "A":
							case "P":

								if (usedCnt != 0) {
									usedCnt   = 0.5;
								}

						}
						annualUsedService.save(tenantId, enterCd, sabun, sabun, yy, annualTaCd, symd, eymd, usedCnt, det.getNote());
					}
					wtmApplRepo.save(appl);

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
			inbox.setInbox(tenantId, enterCd, pushSabun, applId, "APPLY", "결재완료", "휴가 신청서가  승인되었습니다.", "N");

			rp.put("msgType", "APPLY");
		} else {
			pushSabun.add(apprSabun);
			inbox.setInbox(tenantId, enterCd, pushSabun, applId, "APPR", "결재요청 : 휴가 신청", "", "N");

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

		inbox.setInbox(tenantId, enterCd, applSabun, applId, "APPLY", "결재완료", "휴가 신청서가 반려되었습니다.", "N");

		//메일 전송을 위한 파라미터
		rp.put("from", sabun);
		rp.put("to", applSabun);

		return rp;
	}

	@Override
	public void delete(Long applId) {
		// TODO Auto-generated method stub
		
	}

	@Transactional
	@Override
	public ReturnParam imsi(Long tenantId, String enterCd, Long applId, String workTypeCd, Map<String, Object> paramMap,
			String status, String sabun, String userId) throws Exception {
		ReturnParam rp = new ReturnParam();
		rp.setFail("");
		String applSabun = sabun;
		String note = paramMap.get("note").toString();
		WtmAppl appl = null;
		List<WtmTaaAppl> taaAppls = wtmTaaApplRepo.findByApplId(applId);
		SimpleDateFormat ymd = new SimpleDateFormat("yyyyMMdd");

		WtmApplCode applCode = getApplInfo(tenantId, enterCd, workTypeCd);

		//기신청 데이터 
		if(taaAppls == null || taaAppls.size() == 0) {
			//logger.debug("works.size() : " + works.size());
			//if(works != null && works.size() > 0) {
				//신청 또는 승인 완료 건에 대해서만
				if(WtmApplService.APPL_STATUS_IMSI.equals(status) || WtmApplService.APPL_STATUS_APPLY_ING.equals(status) || WtmApplService.APPL_STATUS_APPR.equals(status) || WtmApplService.APPL_STATUS_CANCEL.equals(status)) {
					//appl = wtmApplRepo.findByTenantIdAndEnterCdAndIfApplNo(tenantId, enterCd, ifApplNo);
					if(appl == null) {
						appl = new WtmAppl();
						appl.setTenantId(tenantId);
						appl.setEnterCd(enterCd);
						appl.setIfApplNo(null);
						appl.setApplYmd(ymd.format(new Date()));
					} else {
						//있으면 문제다. 데이터 동기화 작업이 필요. 99일 경우
						//preApplStatus = appl.getApplStatusCd();
					}
					appl.setApplCd(WtmApplService.TIME_TYPE_ANNUAL);
					appl.setApplSabun(applSabun);
					appl.setApplInSabun(applSabun);
					appl.setApplStatusCd(status);
					appl.setUpdateId("WTM_ANNUAL");
					
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


							WtmTaaAppl taaAppl = new WtmTaaAppl();
							taaAppl.setTenantId(tenantId);
							taaAppl.setEnterCd(enterCd);
							taaAppl.setApplId(appl.getApplId());
							taaAppl.setSabun(sabun);
							taaAppl.setIfApplNo(applId.toString());
							taaAppl.setUpdateId(userId);
//							taaAppl.setTaaCd(taaCd);
							
							taaAppl = wtmTaaApplRepo.save(taaAppl);
							
							//List<Map<String, Object>> worksDet = (List<Map<String, Object>>) w.get("worksDet");
							//for(Map<String, Object> work : worksDet) {


								if(paramMap.containsKey("startYmdArr")
									&& paramMap.containsKey("endYmdArr")
									&& paramMap.containsKey("taaTypeCdArr")
									) {


									List<String> symdArr          = (List<String>) paramMap.get("startYmdArr");
									List<String> eymdArr          = (List<String>) paramMap.get("endYmdArr");
									List<String> taaTypeCdArr     = (List<String>) paramMap.get("taaTypeCdArr");

									if(symdArr.size() == 0 || eymdArr.size() == 0 || taaTypeCdArr.size() == 0){
										throw new Exception("휴가신청 기간이 존재하지 않습니다");
									}

									for (int i = 0; i < symdArr.size(); i++) {

										String symd = symdArr.get(i).replaceAll("[-.]", "");
										String eymd = eymdArr.get(i).replaceAll("[-.]", "");
										String taaCd = taaTypeCdArr.get(i);


										String shm  = "";
										if (paramMap.containsKey("startHm") && paramMap.get("startHm") != null && !"".equals(paramMap.get("startHm"))) {
											shm = paramMap.get("startHm").toString();
										}
										String ehm = "";
										if (paramMap.containsKey("endHm") && paramMap.get("endHm") != null && !"".equals(paramMap.get("endHm"))) {
											ehm = paramMap.get("endHm").toString();
										}

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
										taaApplDet.setTaaCd(taaCd);
										taaApplDet.setSymd(symd);
										taaApplDet.setEymd(eymd);
										taaApplDet.setShm(shm);
										taaApplDet.setEhm(ehm);
										taaApplDet.setUpdateId("TAA_INTF");
										taaApplDet.setNote(note);

										wtmTaaApplDetRepo.save(taaApplDet);
									}
									applLineService.saveWtmApplLine(tenantId, enterCd, Integer.parseInt(applCode.getApplLevelCd()), applId, workTypeCd, applSabun, userId);
									rp.put("applId", applId);
									rp.setSuccess("신청이 완료되었습니다.");
								}else {
									throw new RuntimeException("근태정보가 부족합니다.");
								} 
							//}
						//}else {
						//	throw new RuntimeException(sabun + " 님의 근무 상세정보가 없습니다.");
						//}
					//}
					
				}
			//}
		}else {

			/*if(paramMap.containsKey("applCd") && paramMap.get("applCd") != null){

				String taaCd = paramMap.get("applCd").toString();
				appl = wtmApplRepo.findByApplId(applId);
				appl.setApplCd(taaCd);
				appl.setApplSabun(applSabun);
				appl.setApplInSabun(applSabun);
				appl.setApplStatusCd(status);
				appl.setUpdateId("TAA_INTF");

				appl = wtmApplRepo.save(appl);

				applId = appl.getApplId();

				rp.put("applId", applId);
				rp.setSuccess("");
			}*/

		}


		return rp;
	}

	@Override
	public ReturnParam preCheck(Long tenantId, String enterCd, String sabun, String workTypeCd,
			Map<String, Object> paramMap) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReturnParam validate(Long tenantId, String enterCd, String sabun, String workTypeCd,
			Map<String, Object> work) {

		ReturnParam rp = new ReturnParam();

		if (sabun != null && !"".equals(sabun)) {

			try {

				List<String> symdArr          = (List<String>) work.get("startYmdArr");
				List<String> eymdArr          = (List<String>) work.get("endYmdArr");
				List<String> requestTypeCdArr = (List<String>) work.get("requestTypeCdArr");
				List<String> taaTypeCdArr     = (List<String>) work.get("taaTypeCdArr");

				if (symdArr.size() == 0 || eymdArr.size() == 0 || requestTypeCdArr.size() == 0) {
					rp.setFail(sabun + "의 일자 정보가 없습니다.");
					throw new Exception();
				}

				if (taaTypeCdArr.size() == 0) {
					rp.setFail(sabun + "의 근태 코드 정보가 없습니다.");
					throw new Exception();
				}



				for (int i = 0; i < symdArr.size(); i++) {

					String symd         = symdArr.get(i).replaceAll("[-.]", "");
					String eymd         = eymdArr.get(i).replaceAll("[-.]", "");
					String requestCd    = requestTypeCdArr.get(i);
					String workTimeCode = taaTypeCdArr.get(i);

					if (symd.length() != eymd.length()) {
						rp.setFail("시작일과 종료일의 날짜 포맷이 맞지 않습니다.");
					}

					if (symd.length() != 8) {
						rp.setFail("일자는 8자리 입니다.");
					}

					Date sd = WtmUtil.toDate(symd, "yyyyMMdd");
					Date ed = WtmUtil.toDate(eymd, "yyyyMMdd");

					if (sd.compareTo(ed) > 0) {
						rp.setFail("시작일자가 종료일자보다 큽니다.");
					}

					//  totDays, holDays
					Map<String, Integer> calMap = calcService.calcDayCnt(tenantId, enterCd, symd, eymd);
					logger.debug("calMap.toString() : " + calMap.toString());

					//  발생일수, 사용일수
					Integer totalCnt = calMap.get("totDays");
					Integer usedCnt  = calMap.get("totDays") - calMap.get("holDays");

					//  오전반차 오후반차 여부 체크, 공휴일인경우에 에러
					switch (requestCd) {
						case "A":
						case "P":
							if (usedCnt == 0) {
								rp.setFail("반차사용일이 공휴일입니다");
							}
							break;
					}

					//  사용일수가 최대신청가능일수보다 큰지 체크
					WtmTaaCode taaCode = taaCodeRepo.findByTenantIdAndEnterCdAndTaaCd(tenantId, enterCd, workTimeCode);

					if (taaCode.getApprMaxDays() != null && !"".equals(taaCode.getApprMaxDays())) {
						if (usedCnt > taaCode.getApprMaxDays()) {
							rp.setFail("사용일수가 최대신청일수 (" + taaCode.getApprMaxDays() + "일) 보다 큽니다.");
							throw new Exception();
						}
					}

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


					SimpleDateFormat yMdHm = new SimpleDateFormat("yyyyMMddHHmm");
					SimpleDateFormat hm    = new SimpleDateFormat("HHmm");
					logger.debug("taaCode.getRequestTypeCd() : " + taaCode.getRequestTypeCd());
					if (taaCode.getRequestTypeCd().equals(WtmTaaCode.REQUEST_TYPE_A) || taaCode.getRequestTypeCd().equals(WtmTaaCode.REQUEST_TYPE_P)) {
						WtmWorkCalendar calendar  = workCalendarRepo.findByTenantIdAndEnterCdAndSabunAndYmd(tenantId, enterCd, sabun, symd);
						WtmFlexibleEmp  emp       = flexibleEmpRepo.findByTenantIdAndEnterCdAndSabunAndYmdBetween(tenantId, enterCd, sabun, calendar.getYmd());
						WtmTimeCdMgr    timeCdMgr = timeCdMgrRepo.findById(calendar.getTimeCdMgrId()).get();

						WtmFlexibleStdMgr flexibleStdMgr = flexStdMgrRepo.findByFlexibleStdMgrId(emp.getFlexibleStdMgrId());
						//List<WtmWorkDayResult> baseResult = dayResultRepo.findByTenantIdAndEnterCdAndSabunAndYmdAndTimeTypeCd(tenantId, enterCd, sabun, symd, WtmApplService.TIME_TYPE_BASE);
						Date sdate = null, edate = null;
						try {
							sdate = yMdHm.parse(calendar.getYmd() + timeCdMgr.getWorkShm());
							edate = yMdHm.parse(calendar.getYmd() + timeCdMgr.getWorkEhm());
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							throw new Exception();
						}
				/*
				if(baseResult != null && baseResult.size() > 0) {
					for(WtmWorkDayResult br : baseResult) {
						if(sdate == null) {
							sdate = br.getPlanSdate();
						}else {
							if(sdate.compareTo(br.getPlanSdate()) > 0) {
								sdate = br.getPlanSdate();
							}
						}
						if(edate == null) {
							edate = br.getPlanEdate();
						}else {
							if(edate.compareTo(br.getPlanEdate()) < 0) {
								edate = br.getPlanEdate();
							}
						}
					}
				}
				*/
						//시분을 만들어서 체크한다.
						if (taaCode.getRequestTypeCd().equals(WtmTaaCode.REQUEST_TYPE_A)) {

							if (timeCdMgr.getBreakTypeCd().equals(WtmApplService.BREAK_TYPE_MGR)) {
								edate = calcService.P_WTM_DATE_ADD_FOR_BREAK_MGR(edate, -240, calendar.getTimeCdMgrId(), flexibleStdMgr.getUnitMinute());
							} else {
								Calendar cal2 = Calendar.getInstance();
								cal2.setTime(edate);
								cal2.add(Calendar.MINUTE, -240);
								edate = cal2.getTime();
							}
						} else if (taaCode.getRequestTypeCd().equals(WtmTaaCode.REQUEST_TYPE_P)) {
							if (timeCdMgr.getBreakTypeCd().equals(WtmApplService.BREAK_TYPE_MGR)) {
								//반차는 근무시간을 변경함
								//sdate = calcService.P_WTM_DATE_ADD_FOR_BREAK_MGR(sdate, 240, calendar.getTimeCdMgrId(), flexibleStdMgr.getUnitMinute());
								sdate = calcService.P_WTM_DATE_ADD_FOR_BREAK_MGR(sdate, 240, calendar.getTimeCdMgrId(), flexibleStdMgr.getUnitMinute());
							} else {
								Calendar cal2 = Calendar.getInstance();
								cal2.setTime(sdate);
								cal2.add(Calendar.MINUTE, 240);
								sdate = cal2.getTime();
							}
						}
						shm = hm.format(sdate);
						ehm = hm.format(edate);
					}

					//신청서 중복 체크 'H'시간단위 신청서는 별도로 체크 한다.
					rp = checkDuplicateTaaAppl(tenantId, enterCd, sabun, workTimeCode, symd, eymd, shm, ehm, applId);

					if (rp.getStatus() != null && !"OK".equals(rp.getStatus())) {
						throw new Exception(rp.get("message").toString());
					}

				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}


		return rp;
	}
	/*
	protected ReturnParam checkDuplicateAppl(Long tenantId, String enterCd, String sabun, String workTimeCode, String symd, String eymd, String shm, String ehm, String applId) throws Exception {
		ReturnParam rp = new ReturnParam();
		SimpleDateFormat ymd = new SimpleDateFormat("yyyyMMdd");
		Date sDate = ymd.parse(symd);
		Date eDate = ymd.parse(eymd);
		Calendar cal = Calendar.getInstance();
		while(sDate.compareTo(eDate) <= 0) {
			
			List<WtmWorkDayResult> results = dayResultRepo.findByTenantIdAndEnterCdAndSabunAndYmd(tenantId, enterCd, sabun, ymd.format(sDate));
			if(results != null && results.size() > 0 ){
				
			}
			
			
			cal.setTime(sDate);
			cal.add(Calendar.DATE, 1);
			sDate = cal.getTime();
		}
		cal.setTime(ymd.parse(symd));
		List<WtmWorkDayResult> results = dayResultRepo.findByTenantIdAndEnterCdAndSabunAndYmdBetweenOrderByYmdAsc(tenantId, enterCd, sabun, symd, eymd);
		if(results != null && results.size() > 0 ){
			
		}else {
			rp.setSuccess("");
		}
		return rp;
	}
	*/
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

		ObjectMapper mapper = new ObjectMapper();
		try {
			logger.debug("paramMap : " + mapper.writeValueAsString(paramMap));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<String, Object> m = validatorMapper.checkDuplicateTaaAppl(paramMap);

		if(m!=null && m.get("cnt")!=null) {
			int cnt = Integer.parseInt(m.get("cnt").toString());
			logger.debug("checkDuplicateTaaAppl cnt : " + cnt);
			if(cnt > 0) {

				rp.setFail(m.get("empNm").toString()+"("+sabun+") " + WtmUtil.parseDateStr(WtmUtil.toDate(symd,"yyyyMMdd"), "yyyy-MM-dd") + "~" +  WtmUtil.parseDateStr(WtmUtil.toDate(eymd,"yyyyMMdd"), "yyyy-MM-dd") +"의 신청중인 또는 이미 적용된 근무정보가 있습니다.");
				return rp;
			}
		}

		return rp;
	}

	protected ReturnParam checkWorktimeTaaAppl(Long tenantId, String enterCd, String sabun, List<Map<String, Object>> appl) {
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");

		// 20200710 전영수과장 근무시간체크 해제요청(이효정메일전달)
		if(tenantId == 22) {
			return rp;
		}

		String sDate = null;
		String eDate = null;
		int i = 0;
		for(Map<String, Object> a : appl) {
			String symd = a.get("startYmd").toString();
			String eymd = a.get("endYmd").toString();

			if(i==0) {
				sDate = symd;
				eDate = eymd;
			} else {
				if(symd.compareTo(sDate) == -1)
					sDate = symd;
				if(eymd.compareTo(eDate) == 1)
					eDate = eymd;
			}
			i++;
		}

		Map<String, Object> paramMap = null;
		List<Map<String, Object>> applMinutes = new ArrayList<Map<String, Object>>();
		String taaWorkYn = "Y";	// 선근제 체크용
		for(Map<String, Object> a : appl) {
			String workTimeCode = a.get("workTimeCode").toString();
			String symd = a.get("startYmd").toString();
			String eymd = a.get("endYmd").toString();
			String shm = a.get("startHm").toString();
			String ehm = a.get("endHm").toString();

			WtmTaaCode taaCode = taaCodeRepo.findByTenantIdAndEnterCdAndTaaCd(tenantId, enterCd, workTimeCode);
			if(taaCode==null) {
				rp.setFail("근태 코드 정보가 없습니다.");
				return rp;
			}

			int applMinute = 0;

			if((shm==null || "".equals(shm)) && (ehm==null || "".equals(ehm))) {
				//간주근무시간
				if(taaCode.getWorkApprHour()!=null && !"".equals(taaCode.getWorkApprHour())) {
					applMinute = taaCode.getWorkApprHour() * 60;
				} else {
					//일 기본근무시간
					applMinute = 8 * 60;
					WtmPropertie propertie = propertieRepo.findByTenantIdAndEnterCdAndInfoKey(tenantId, enterCd, "OPTION_DEFAULT_WORKTIME");
					if(propertie!=null && propertie.getInfoValue()!=null && !"".equals(propertie.getInfoValue()) && !"0".equals(propertie.getInfoValue())) {
						applMinute = Integer.parseInt(propertie.getInfoValue()) * 60;
					}
				}
			} else {

				//시간이 들어오는 경우는 symd == eymd

				Date sd = WtmUtil.toDate(symd + shm, "yyyyMMddHHmm");
				Date ed = WtmUtil.toDate(symd + ehm, "yyyyMMddHHmm");

				applMinute = (int)(ed.getTime() - sd.getTime()) / (60*1000);
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
				if("Y".equals(taaCode.getHolInclYn()) || ( "N".equals(taaCode.getHolInclYn()) && "N".equals(workCalendar.getHolidayYn()) )) {
					// 선근제는 해당없음. 전체구간을 검증할꺼니깐 시간가산만 하자
					if(!"SELE_C".equals(flexibleStdMgr.getWorkTypeCd()) && !"SELE_F".equals(flexibleStdMgr.getWorkTypeCd())){
						//1.근무 계획 시간보다 근태 신청 시간이 더 큰지 체크
						Map<String, Object> m = validatorMapper.checkApplMinute(paramMap);

						if(m==null) {
							rp.setFail( WtmUtil.parseDateStr(s, "yyyy-MM-dd") +"의 근무 시간 정보가 존재하지 않습니다.");
							return rp;
						}

						if("N".equals(m.get("isValid").toString())) {
							rp.setFail(m.get("empNm").toString()+"("+sabun+") "+ WtmUtil.parseDateStr(s, "yyyy-MM-dd") +"의 신청 시간이 근무 시간을 초과할 수 없습니다.");
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

			} while(s.compareTo(e) == 0);

		}

		//2.선근제의 경우에는 해당 선근제 근무 기간 내의 기본근로시간을 넘지 않는지 체크
		paramMap = new HashMap<>();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		paramMap.put("symd", sDate);
		paramMap.put("eymd", eDate);
		paramMap.put("taaWorkYn", taaWorkYn);	// 근무시간가산여부(가산이 아니면 해당기간 기본근무 빼고 계산해야함)
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

		if(results!=null && results.size()>0) {
			for(Map<String, Object> r : results) {
				if(r.get("isValid")!=null && "N".equals(r.get("isValid"))) {
					System.out.println("workMinute: " + r.get("workMinute").toString());
					System.out.println("totalWorkMinute: " + r.get("totalWorkMinute").toString());

					Double h = 0d;
					Double m = 0d;

					h = Double.parseDouble(r.get("workMinute").toString())/60;
					h = Math.ceil(h*100)/100.0;
					m = (h - h.intValue()) * 60;

					rp.setFail("선택근무제 총 근무 시간("+((h.intValue()>0)?String.format("%02d", h.intValue()):"00")+"시간"+((m.intValue()>0)?String.format("%02d", m.intValue())+"분":"")+")을 초과할 수 없습니다.");
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
	public ReturnParam saveWtmApplSts(Long tenantId, String enterCd, String sabun, String userId,
			Map<String, Object> convertMap) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> getApprovalApplList(Long tenantId, String enterCd, String empNo, Map<String, Object> paramMap, String userId) {
		return null;
	}

	protected WtmApplCode getApplInfo(Long tenantId,String enterCd,String applCd) {
		return wtmApplCodeRepo.findByTenantIdAndEnterCdAndApplCd(tenantId, enterCd, applCd);
	}

}
