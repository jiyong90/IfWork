package com.isu.ifw.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isu.ifw.entity.*;
import com.isu.ifw.mapper.WtmApplMapper;
import com.isu.ifw.mapper.WtmFlexibleEmpMapper;
import com.isu.ifw.repository.*;
import com.isu.ifw.util.WtmUtil;
import com.isu.ifw.vo.ReturnParam;
import com.isu.ifw.vo.WtmApplLineVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("wtmTaaCanService")
public class WtmTaaCanApplServiceImpl implements WtmApplService {

	@Autowired
	private WtmApplMapper applMapper;
	@Autowired
	private WtmApplRepository wtmApplRepo;

	@Autowired
	private WtmPropertieRepository wtmPropertieRepo;

	@Autowired
	private WtmApplLineRepository wtmApplLineRepo;

	@Autowired
	private WtmApplCodeRepository wtmApplCodeRepo;

	@Autowired
	private WtmFlexibleEmpService wtmFlexibleEmpService;

	@Autowired
	private WtmTaaCanApplRepository taaCanApplRepo;

	@Autowired
	private WtmFlexibleEmpMapper wtmFlexibleEmpMapper;

	@Autowired
	private WtmInboxService inbox;

	@Autowired
	private WtmEmpHisRepository wtmEmpHisRepo;

	@Autowired
	private WtmApplLineService applLineService;


	@Autowired
	private WtmTaaApplDetRepository taaApplDetRepo;

	@Autowired
	private WtmTaaApplRepository wtmTaaApplRepo;

	@Autowired
	private WtmTaaApplDetRepository wtmTaaApplDetRepo;

	@Autowired
	private WtmTaaCodeRepository taaCodeRepo;

	@Autowired
	private WtmInterfaceService interfaceService;

	@Autowired
	private WtmAnnualUsedService annualUsedService;

	@Autowired
	private WtmCalcService calcService;

	@Autowired
	private WtmAnnualUsedRepository annuRepo;

	@Autowired
	private WtmWorkDayResultRepository wtmWorkDayResultRepo;

	@Autowired
	private WtmFlexibleEmpRepository wtmFlexibleEmpRepo;

	@Autowired
	private WtmWorkCalendarRepository wtmWorkCalendarRepo;

	@Autowired
	private WtmFlexibleStdMgrRepository flexStdMgrRepo;

	@Autowired
	private WtmTimeCdMgrRepository timeCdMgrRepo;

	@Autowired
	private WtmFlexibleEmpService flexibleEmpService;

	@Autowired
	private WtmFlexibleEmpResetService flexibleEmpResetService;

	@Override
	public Map<String, Object> getAppl(Long tenantId, String enterCd, String sabun, Long applId, String userId) {

		try {
			Map<String, Object> resultMap =new HashMap<String, Object>();
			WtmAppl wtmAppl = wtmApplRepo.findByApplId(applId);
			WtmTaaCanAppl wtmTaaCanAppl = taaCanApplRepo.findByApplId(applId);

			resultMap.put("wtmTaaCanAppl", wtmTaaCanAppl);

			List<WtmTaaAppl> taaApplList = wtmTaaApplRepo.findByApplId(wtmTaaCanAppl.getCanApplId());

			WtmTaaAppl taaAppl = null;
			String taaNm = "";
			if(taaApplList != null && taaApplList.size() > 0) {
				taaAppl = taaApplList.get(0);

			}
			List<WtmTaaApplDet> taaApplDetList = wtmTaaApplDetRepo.findByTaaApplId(taaAppl.getTaaApplId());

			if(taaApplDetList != null && taaApplDetList.size() >0){
				resultMap.put("taaAppl", taaAppl);
				resultMap.put("taaApplDetList", taaApplDetList);
				WtmTaaCode wtmTaaCode = taaCodeRepo.findByTenantIdAndEnterCdAndTaaCd(tenantId,enterCd, taaApplDetList.get(0).getTaaCd());
				taaNm = wtmTaaCode.getTaaNm();
			}
			resultMap.put("taaNm", taaNm);

			if(taaApplDetList!=null && taaApplDetList.size()>0) {

				List<String> sabuns = new ArrayList<String>();

				String ymd = WtmUtil.parseDateStr(new Date(), "yyyyMMdd");

				boolean isRecovery = false;
				List<WtmApplLineVO> applLine = applMapper.getWtmApplLineByApplId(applId);

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
				resultMap.put("applLine", applLine);
			}

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

	/**
	 * 휴가 취스 결재 요청
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
	@Override
	public ReturnParam request(Long tenantId, String enterCd, Long applId, String workTypeCd, Map<String, Object> paramMap,
							   String sabun, String userId) throws Exception {

		ReturnParam rp = imsi(tenantId, enterCd, applId, workTypeCd, paramMap, this.APPL_STATUS_APPLY_ING, sabun, userId);

		//rp.put("flexibleApplId", flexibleAppl.getFlexibleApplId());

		//결재라인 상태값 업데이트
		//WtmApplLine line = wtmApplLineRepo.findByApplIdAndApprSeq(applId, apprSeq);
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
		}
		inbox.setInbox(tenantId, enterCd, apprSabun, applId, workTypeCd, "결재요청 : 휴가취소신청", "", "Y");

		//메일 전송을 위한 파라미터
		rp.put("from", sabun);
		rp.put("to", apprSabun);

		return rp;
	}

	@Transactional
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

		if(applSabuns!=null && applSabuns.size()>0) {
			for(String applSabun : applSabuns) {
				rp = this.validate(tenantId, enterCd, applSabun, "", paramMap);
				//rp = validate(applId);
				if(rp.getStatus().equals("FAIL")) {
					throw new Exception(rp.get("message").toString());
				}
			}
		}

		//결재라인 상태값 업데이트
		//WtmApplLine line = wtmApplLineRepo.findByApplIdAndApprSeq(applId, apprSeq);
		List<WtmApplLine> lines = wtmApplLineRepo.findByApplIdOrderByApprSeqAsc(applId);

		String apprSabun = null;
		//마지막 결재자인지 확인하자
		boolean lastAppr = false;
		if(lines != null && lines.size() > 0) {
			for(WtmApplLine line : lines) {
				if(line.getApprSeq() == apprSeq && line.getApprSabun().equals(sabun)) {
					line.setApprStatusCd(APPR_STATUS_APPLY);
					line.setApprDate(new Date());
					//결재의견
					if(paramMap != null && paramMap.containsKey("apprOpinion")) {
						line.setApprOpinion(paramMap.get("apprOpinion").toString());
						line.setUpdateId(userId);
					}
					line = wtmApplLineRepo.save(line);
					apprSabun = line.getApprSabun();
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
		WtmTaaCanAppl wtmTaaCanAppl = taaCanApplRepo.findByApplId(applId);

		//취소할 결재 조회
		WtmAppl canAppl = wtmApplRepo.findByApplId(wtmTaaCanAppl.getCanApplId());
		List<WtmTaaAppl> taaApplList = wtmTaaApplRepo.findByApplId(wtmTaaCanAppl.getCanApplId());
		List<WtmTaaApplDet> wtmTaaApplDetList = wtmTaaApplDetRepo.findByTaaApplId(taaApplList.get(0).getTaaApplId());

		List<String> pushSabun = new ArrayList();
		if(lastAppr) {

			//변경 전의 상태값을 가지고 있는데 99였다가 44가 될 경우엔 RESULT를 다시 생성해야하기 때문이다.
			//21 > 44 는 괜찮다.
			String preApplStatus = null;
			//취소할 신청서상태값을 가지고 온다.
			preApplStatus = canAppl.getApplStatusCd();
			String status = "44";

			if (!preApplStatus.equals(status)) {
				canAppl.setApplStatusCd(status);
				wtmApplRepo.save(canAppl);
			}

			for(WtmTaaApplDet wtmTaaApplDet : wtmTaaApplDetList) {
				Map<String, Object> work = new HashMap<String, Object>();
				work.put("startYmd", wtmTaaApplDet.getSymd());
				work.put("endYmd", wtmTaaApplDet.getEymd());
				work.put("workTimeCode", wtmTaaApplDet.getTaaCd());

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

				//이건 상태랑 같으면 무시
				if (preApplStatus == null || !preApplStatus.equals(status)) {
					if (status.equals(WtmApplService.APPL_STATUS_APPR)
							|| status.equals(WtmApplService.APPL_STATUS_APPR_REJECT)
							|| status.equals(WtmApplService.APPL_STATUS_CANCEL)) {

						//휴가 신청 내역 삭제
						WtmAnnualUsed annuUsed = annuRepo.findByTenantIdAndEnterCdAndTaaTypeCdAndSabunAndSymdAndEymd(tenantId, enterCd, wtmTaaApplDet.getTaaCd(), canAppl.getApplSabun(), wtmTaaApplDet.getSymd(), wtmTaaApplDet.getEymd());
						if (annuUsed != null)
							annuRepo.delete(annuUsed);


						//  WTM_TAA_CAN_APPL 테이블에 있는 APPL_ID -> WTM_WORK_DAY_RESULT.APPL_ID 다 삭제
						wtmWorkDayResultRepo.deleteByApplId(canAppl.getApplId());

						SimpleDateFormat ymdFt = new SimpleDateFormat("yyyyMMdd");
						String chkYmd = ymdFt.format(new Date());


						Calendar cal1 = Calendar.getInstance();
						Calendar cal2 = Calendar.getInstance();
						try {
							cal1.setTime(ymdFt.parse(wtmTaaApplDet.getSymd()));
							cal2.setTime(ymdFt.parse(wtmTaaApplDet.getEymd()));
						} catch (ParseException e) {
							e.printStackTrace();
						}

						Date d1 = cal1.getTime();
						Date d2 = cal2.getTime();
						while (d1.compareTo(d2) < 1) {

							String d = ymdFt.format(d1);
							WtmFlexibleEmp emp = wtmFlexibleEmpRepo.findByTenantIdAndEnterCdAndSabunAndYmdBetween(tenantId, enterCd, canAppl.getApplSabun(), d);
							WtmWorkCalendar cal = wtmWorkCalendarRepo.findByTenantIdAndEnterCdAndSabunAndYmd(tenantId, enterCd, canAppl.getApplSabun(), d);
							WtmFlexibleStdMgr flexibleStdMgr = flexStdMgrRepo.findByFlexibleStdMgrId(emp.getFlexibleStdMgrId());
							WtmTimeCdMgr timeCdMgr = timeCdMgrRepo.findById(cal.getTimeCdMgrId()).get();
							flexibleEmpResetService.P_WTM_WORK_DAY_RESULT_RESET(cal, flexibleStdMgr, timeCdMgr, "ADM");

							if (Integer.parseInt(chkYmd) > Integer.parseInt(d)) {
								try {

									//마감데이터 재생성
									flexibleEmpService.calcApprDayInfo(tenantId
											, enterCd
											, d
											, d
											, canAppl.getApplSabun());


								} catch (Exception e) {
									e.printStackTrace();
									rp.setFail(e.getMessage());
								}
							}

							calcService.P_WTM_FLEXIBLE_EMP_WORKTERM_C(tenantId, enterCd, canAppl.getApplSabun(), d, d);

							cal1.add(Calendar.DATE, 1);

							d1 = cal1.getTime();
						}
					}

				}

			}

			pushSabun.addAll(applSabuns);
			inbox.setInbox(tenantId, enterCd, pushSabun, applId, "APPLY", "결재완료", "휴가취소 신청서가  승인되었습니다.", "N");

			rp.put("msgType", "APPLY");
		} else {
			pushSabun.add(apprSabun);
			inbox.setInbox(tenantId, enterCd, pushSabun, applId, "APPR", "결재요청 : 휴가취소신청", "", "N");

			rp.put("msgType", "APPR");
		}

		//메일 전송을 위한 파라미터
		rp.put("from", sabun);
		rp.put("to", pushSabun);

		//신청서 메인 상태값 업데이트
		appl.setApplStatusCd((lastAppr)?APPL_STATUS_APPR:APPL_STATUS_APPLY_ING);
		appl.setApplYmd(WtmUtil.parseDateStr(new Date(), null));
		appl.setUpdateId(userId);
		appl = wtmApplRepo.save(appl);

		rp.setSuccess("결재가 완료되었습니다.");
		return rp;
	}

	/**
	 * 결재 승인 반려
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
	public ReturnParam reject(Long tenantId, String enterCd, Long applId, int apprSeq, Map<String, Object> paramMap,
							  String sabun, String userId) throws Exception {
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
	@Transactional
	public ReturnParam imsi(Long tenantId, String enterCd, Long applId, String workTypeCd, Map<String, Object> paramMap,
							String status, String sabun, String userId) throws Exception {

		ReturnParam rp = new ReturnParam();
		rp.setFail("");

		WtmApplCode applCode = getApplInfo(tenantId, enterCd, workTypeCd);

		String applSabun = sabun;
		String note = paramMap.get("reason").toString();
		WtmAppl appl = null;
		List<WtmTaaAppl> taaAppls = wtmTaaApplRepo.findByApplId(applId);
		SimpleDateFormat ymd = new SimpleDateFormat("yyyyMMdd");

		//기신청 데이터
		if (taaAppls != null || taaAppls.size() > 0) {
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
				appl.setApplCd(workTypeCd);
				appl.setApplSabun(applSabun);
				appl.setApplInSabun(applSabun);
				appl.setApplStatusCd(status);
				appl.setUpdateId("WTM_ANNUAL_CAN");

				appl = wtmApplRepo.save(appl);

				applId = appl.getApplId();

				//IF에도 번호를 넣어주자 추후에 승인시 필요하다..
				if(applId != null) {
					appl.setIfApplNo(applId.toString());
					appl = wtmApplRepo.save(appl);
				}

				/**
				 * 취소할 근태 정보 조회수정
				 */
				if(taaAppls != null) {
					for(WtmTaaAppl taaAppl: taaAppls) {
						List<WtmTaaApplDet> taaApplDetList = taaApplDetRepo.findByTaaApplId(taaAppl.getTaaApplId());

						for(WtmTaaApplDet taaApplDet : taaApplDetList) {
							WtmTaaCanAppl taaCanAppl = 	saveWtmTaaCanAppl(tenantId, enterCd, appl.getApplId(), taaAppl.getApplId(), paramMap.get("reason").toString(), sabun, userId);
						}

					}
				}
				applLineService.saveWtmApplLine(tenantId, enterCd, Integer.parseInt(applCode.getApplLevelCd()), applId, workTypeCd, applSabun, userId);

				rp.put("applId", applId);
				rp.setSuccess("결재가 완료되었습니다.");
			} else {
				throw new RuntimeException("근태정보가 부족합니다.");
			}
		}
		return rp;
	}

	protected WtmTaaCanAppl saveWtmTaaCanAppl(Long tenantId, String enterCd, Long applId, Long taaApplId, String reason, String sabun, String userId) {

		WtmTaaCanAppl taaCanAppl = new WtmTaaCanAppl();
		taaCanAppl.setTenantId(tenantId);
		taaCanAppl.setEnterCd(enterCd);
		taaCanAppl.setApplId(applId);
		taaCanAppl.setCanApplId(taaApplId);
		taaCanAppl.setNote(reason);
		taaCanAppl.setUpdateId(userId);

		return taaCanApplRepo.save(taaCanAppl);
	}


	protected WtmApplCode getApplInfo(Long tenantId,String enterCd,String applCd) {
		return wtmApplCodeRepo.findByTenantIdAndEnterCdAndApplCd(tenantId, enterCd, applCd);
	}

	@Override
	public ReturnParam preCheck(Long tenantId, String enterCd, String sabun, String workTypeCd,
								Map<String, Object> paramMap) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReturnParam validate(Long tenantId, String enterCd, String sabun, String workTypeCd,
								Map<String, Object> paramMap) {
		// TODO Auto-generated method stub
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");

		return rp;
	}

	@Override
	public void sendPush() {
		// TODO Auto-generated method stub

	}

	protected WtmAppl saveWtmAppl(Long tenantId, String enterCd, Long applId, String workTypeCd, String applStatusCd, String sabun, String userId) {
		WtmAppl appl = null;
		if(applId != null && !applId.equals("")) {
			appl = wtmApplRepo.findById(applId).get();
		}else {
			appl = new WtmAppl();
		}
		appl.setApplStatusCd(applStatusCd);
		appl.setTenantId(tenantId);
		appl.setEnterCd(enterCd);
		appl.setApplInSabun(sabun);
		appl.setApplSabun(sabun);
		appl.setApplCd(workTypeCd);
		appl.setApplYmd(WtmUtil.parseDateStr(new Date(), null));
		appl.setUpdateId(userId);
		//appl.

		return wtmApplRepo.save(appl);
	}

	@Override
	@Transactional
	public void delete(Long applId) {

	}

	@Override
	public ReturnParam requestSync(Long tenantId, String enterCd, Map<String, Object> paramMap, String sabun,
								   String userId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReturnParam saveWtmApplSts(Long tenantId, String enterCd, String sabun, String userId,
									  Map<String, Object> convertMap) {
		// TODO Auto-generated method stub
		return null;
	}

}
