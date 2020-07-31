package com.isu.ifw.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.isu.ifw.entity.WtmAppl;
import com.isu.ifw.entity.WtmApplCode;
import com.isu.ifw.entity.WtmApplLine;
import com.isu.ifw.entity.WtmCompAppl;
import com.isu.ifw.entity.WtmCompCanAppl;
import com.isu.ifw.entity.WtmTaaCode;
import com.isu.ifw.entity.WtmWorkDayResult;
import com.isu.ifw.mapper.WtmApplMapper;
import com.isu.ifw.mapper.WtmCompApplMapper;
import com.isu.ifw.mapper.WtmCompCanApplMapper;
import com.isu.ifw.mapper.WtmFlexibleEmpMapper;
import com.isu.ifw.mapper.WtmInterfaceMapper;
import com.isu.ifw.repository.WtmApplCodeRepository;
import com.isu.ifw.repository.WtmApplLineRepository;
import com.isu.ifw.repository.WtmApplRepository;
import com.isu.ifw.repository.WtmCompApplRepository;
import com.isu.ifw.repository.WtmCompCanApplRepository;
import com.isu.ifw.repository.WtmTaaCodeRepository;
import com.isu.ifw.repository.WtmWorkDayResultRepository;
import com.isu.ifw.util.WtmUtil;
import com.isu.ifw.vo.ReturnParam;
import com.isu.ifw.vo.WtmApplLineVO;

@Service("wtmCompCanApplService")
public class WtmCompCanApplServiceImpl implements WtmApplService {

	private final Logger logger = LoggerFactory.getLogger("ifwFileLog");
	
	@Autowired
	WtmCompApplListService wtmCompApplListService;
	
	@Autowired
	@Qualifier("wtmCompCanApplService")
	WtmApplService wtmCompCanApplService;
	
	@Autowired
	WtmApplLineService applLineService;
	
	@Autowired
	WtmApplLineRepository wtmApplLineRepo;
	
	@Autowired
	WtmInboxService inbox;
	
	@Autowired
	WtmCompApplRepository wtmCompApplRepo;
	
	@Autowired
	WtmWorkDayResultRepository wtmWorkDayResultRepo;
	
	@Autowired
	WtmApplRepository wtmApplRepo;
	
	@Autowired
	WtmApplCodeRepository wtmApplCodeRepo;
	
	@Autowired
	WtmCompCanApplRepository wtmCompCanApplRepo;
	
	@Autowired
	WtmCompApplMapper wtmCompApplMapper;
	
	@Autowired
	WtmCompCanApplMapper wtmCompCanApplMapper;
	
	@Autowired
	WtmApplMapper applMapper;
	
	@Autowired
	private WtmTaaCodeRepository wtmTaaCodeRepo;
	
	@Autowired
	WtmInterfaceMapper wtmInterfaceMapper;
	
	@Autowired
	private WtmWorkDayResultRepository dayResultRepo;
	
	@Autowired
	WtmFlexibleEmpMapper wtmFlexibleEmpMapper;
	
	@Autowired
	private WtmFlexibleEmpService WtmFlexibleEmpService;

	@Override
	public Map<String, Object> getAppl(Long tenantId, String enterCd, String sabun, Long applId, String userId) {
		
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("applId", applId);
			paramMap.put("sabun", sabun);
			List<Map<String, Object>> compCanApplList = wtmCompCanApplMapper.compCanApplfindByApplId(paramMap);

			ObjectMapper mapper = new ObjectMapper();
			
			logger.debug("compApplList :::::: " + mapper.writeValueAsString(compCanApplList));
			
			Map<String, Object> compCanAppl = null;
			if(compCanApplList!=null && compCanApplList.size()>0) {
				
				List<String> sabuns = new ArrayList<String>();
				
				String ymd = WtmUtil.parseDateStr(new Date(), "yyyyMMdd");
				
				if(compCanAppl==null)
					compCanAppl = compCanApplList.get(0);
				
				for(Map<String, Object> o : compCanApplList) {
					if(sabun.equals(o.get("sabun").toString())) {
						compCanAppl = o;
//						ymd = o.get("ymd").toString();
					}
					sabuns.add(o.get("sabun").toString());
				}
							
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
				
				compCanAppl.put("recoveryYn", isRecovery);
				compCanAppl.put("applLine", applLine);
			}
			logger.debug("compAppl :::::: " + mapper.writeValueAsString(compCanAppl));
			return compCanAppl;
			
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

	@Transactional
	@Override
	public ReturnParam request(Long tenantId, String enterCd, Long applId, String workTypeCd, Map<String, Object> paramMap,
			String sabun, String userId) throws Exception {
		ReturnParam rp = imsi(tenantId, enterCd, applId, workTypeCd, paramMap, this.APPL_STATUS_APPLY_ING, sabun, userId);
		
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
		inbox.setInbox(tenantId, enterCd, apprSabun, applId, "APPR", "결재요청 : 보상휴가취소신청", "", "Y");
		
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
		rp.setSuccess("결재가 완료되었습니다.");
		
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
		
		logger.debug("COMP 승인 대상자 : " + mapper.writeValueAsString(applSabuns));
		
		//결재라인 상태값 업데이트
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
		
		
		//신청서 메인 상태값 업데이트
		WtmAppl appl = wtmApplRepo.findById(applId).get();
		appl.setApplStatusCd((lastAppr)?APPL_STATUS_CANCEL:APPL_STATUS_APPLY_ING);
		appl.setApplYmd(WtmUtil.parseDateStr(new Date(), null));
		appl.setUpdateId(userId);
		
		appl = wtmApplRepo.save(appl);
		
		//최종 승인 완료일때 근무 반영
		if(lastAppr) {
			Map<String, Object> paramAppl = new HashMap<String, Object>();
			Map<String, Object> param = new HashMap<String, Object>();
			paramAppl = (Map<String, Object>) paramMap.get("appl");
			param.put("tenantId", tenantId);
			param.put("enterCd", enterCd);
			param.put("sabun", sabun);
			param.put("applId", applId);
			param.put("userId", userId);
			param.put("compCanApplId", Long.parseLong(paramAppl.get("compCanApplId").toString()));
			param.put("applStatusCd", APPL_STATUS_CANCEL);
			
			wtmCompApplMapper.saveApplRequest(param);
			this.applyStsAfter(tenantId, enterCd, applId, paramMap,  sabun,  userId);
		}
		
		List<String> pushSabun = new ArrayList();
		if(lastAppr) {
			pushSabun.addAll(applSabuns);
			inbox.setInbox(tenantId, enterCd, pushSabun, applId, "APPLY", "결재완료", "보상휴가신청서가  승인되었습니다.", "N");
			
			rp.put("msgType", "APPLY");
		} else {
			pushSabun.add(apprSabun);
			inbox.setInbox(tenantId, enterCd, pushSabun, applId, "APPR", "결재요청 : 보상휴가신청서신청", "", "N");
			
			rp.put("msgType", "APPR");
		}
		
		//메일 전송을 위한 파라미터
		rp.put("from", sabun);
		rp.put("to", pushSabun);
		
		return rp;
	}

	protected  ReturnParam applyStsAfter(Long tenantId, String enterCd, Long applId, Map<String, Object> paramMap, String sabun, String userId) throws NumberFormatException, ParseException {
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("결재가 완료되었습니다.");
		
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("applId", applId);
		paramMap.put("sabun", sabun);
		paramMap.put("userId", userId);
		
		List<Map<String, Object>>  getCompCanApplList = null;
		getCompCanApplList = wtmCompCanApplMapper.getCompCanApplList(paramMap);
		
		for(Map<String, Object> compMap : getCompCanApplList) {
			String nowApplStatusCd = this.APPL_STATUS_CANCEL;
			
			// 4.1 근태 코드별 기준확인
			
			WtmTaaCode taaCode = wtmTaaCodeRepo.findByTenantIdAndEnterCdAndTaaCd(tenantId, enterCd, compMap.get("taaCd").toString());
			if(taaCode == null) {
				rp.setFail("FAIL");
				rp.setMessage("근태코드가 없습니다. 담당자에게 문의 하세요.");
				return rp;
			}
			if("N".equals(taaCode.getRequestTypeCd())) {
				rp.setFail("FAIL");
				rp.setMessage("근태신청이 불가능한 근태코드입니다.");
				return rp;
			}
			// 4.2. 근무옵션 확인
			SimpleDateFormat dt = new SimpleDateFormat("yyyyMMddHHmmss");
			
			Map<String, Object>  getStdMgrMap = null;
			getStdMgrMap = wtmInterfaceMapper.getStdMgrList(compMap);
			String timeTypeCd = "TAA";
			if("Y".equals(taaCode.getWorkYn())) {
				timeTypeCd = "REGA";
			}
			
			if(getStdMgrMap != null && getStdMgrMap.containsKey("unplannedYn") && "Y".equals(getStdMgrMap.get("unplannedYn").toString())) {
				// 4.2.1. 근무계획없음 체크일때 RESULT 갱신은 없음.
				// result 생성해야함
				if("99".equals(nowApplStatusCd)) {
					WtmWorkDayResult dayResult = new WtmWorkDayResult();
					Integer workMinute = 0;
					if(taaCode.getWorkApprHour()!=null) {
						workMinute = Integer.parseInt(taaCode.getWorkApprHour().toString()) * 60;
					}
					if(!"0".equals(compMap.get("workMinute").toString())) {
						workMinute = Integer.parseInt(compMap.get("workMinute").toString());
					}
					dayResult.setTenantId(Long.parseLong(compMap.get("tenantId").toString()));
					dayResult.setEnterCd(compMap.get("enterCd").toString());
					dayResult.setYmd(compMap.get("ymd").toString());
					dayResult.setSabun(compMap.get("sabun").toString());
					dayResult.setApplId(Long.parseLong(compMap.get("applId").toString()));
					dayResult.setTimeTypeCd(timeTypeCd);
					dayResult.setTaaCd(compMap.get("taaCd").toString());
					dayResult.setPlanMinute(workMinute);
					dayResult.setApprMinute(workMinute);
					dayResult.setUpdateId(userId);
					dayResultRepo.save(dayResult);
				} else {
					// delete는 키를 못찾으니깐 쿼리문으로
					wtmInterfaceMapper.deleteResult(compMap);
				}
			} else {
				// 4.2.2 근무계획이 무조건 있어야 함
				// 근태기준이 휴일포함이거나, 휴일포함아니면 근무일일때만 data 생성
				if("Y".equals(taaCode.getHolInclYn()) || ("N".equals(taaCode.getHolInclYn()) && "N".equals(compMap.get("holidayYn")))) {
					if("D".equals(taaCode.getRequestTypeCd()) && "Y".equals(taaCode.getHolInclYn()) && "Y".equals(compMap.get("holidayYn"))) {
						// 휴일포함이면서 휴일이면서 종일근무이면
						WtmWorkDayResult dayResult = new WtmWorkDayResult();
						dayResult.setTenantId(Long.parseLong(compMap.get("tenantId").toString()));
						dayResult.setEnterCd(compMap.get("enterCd").toString());
						dayResult.setYmd(compMap.get("ymd").toString());
						dayResult.setSabun(compMap.get("sabun").toString());
						dayResult.setApplId(Long.parseLong(compMap.get("applId").toString()));
						dayResult.setTimeTypeCd(timeTypeCd);
						dayResult.setTaaCd(compMap.get("taaCd").toString());
						dayResult.setUpdateId("TAAIF");
						dayResultRepo.save(dayResult);
					} else {
						String taaSdate = getStdMgrMap.get("taaSdate").toString();
						String taaEdate = getStdMgrMap.get("taaEdate").toString();
						 
						
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
								rp.setFail("FAIL");
								rp.setMessage("근태 시간계산중 오류가 발생하였습니다.");
								return rp;
							}
							taaSdate = getTimeMap.get("taaSdate").toString();
							taaEdate = getTimeMap.get("taaEdate").toString();
						} else if("P".equals(taaCode.getRequestTypeCd())){	
							Map<String, Object>  getTimeMap = new HashMap();
							getTimeMap.put("tenantId", tenantId);
							getTimeMap.put("enterCd", compMap.get("enterCd").toString());
							getTimeMap.put("sabun", compMap.get("sabun").toString());
							getTimeMap.put("ymd", compMap.get("ymd").toString());
							getTimeMap.put("sDate", taaSdate);
							getTimeMap.put("addMinute", 240);
							getTimeMap.put("retDate", "");
							// System.out.println("**** getTimeMap param : " + getTimeMap);
							Map<String, Object>  setTimeMap = new HashMap();
							setTimeMap = wtmFlexibleEmpMapper.addMinuteWithBreakMGR(getTimeMap);
							String retDate = getTimeMap.get("retDate").toString();
							
							if("".equals(retDate) || retDate.length() != 14) {
								rp.setFail("FAIL");
								rp.setMessage("근태 시간계산중 오류가 발생하였습니다.");
								return rp;
							}
							taaSdate = retDate;
							// taaEdate = getTimeMap.get("taaEdate").toString();
						} 
						
						String chkYmd = WtmUtil.parseDateStr(new Date(), null);
						if("99".equals(nowApplStatusCd)) {
							if ("D".equals(taaCode.getRequestTypeCd()) && "N".equals(getStdMgrMap.get("taaWorkYn"))) {
								// 종일근무이면서 근무가능여부가 N이면 근무계획을 삭제하고 근태만 남겨둬야함.  
								List<String> timeType = new ArrayList<String>();
								timeType.add(WtmApplService.TIME_TYPE_BASE);
								timeType.add(WtmApplService.TIME_TYPE_LLA);
								List<WtmWorkDayResult> base = dayResultRepo.findByTenantIdAndEnterCdAndSabunAndTimeTypeCdInAndYmdBetweenOrderByPlanSdateAsc(tenantId, compMap.get("enterCd").toString(), compMap.get("sabun").toString(), timeType, compMap.get("ymd").toString(), compMap.get("ymd").toString());
								for(WtmWorkDayResult r : base) {
									dayResultRepo.delete(r);
								}
							}
																	
							WtmFlexibleEmpService.addWtmDayResultInBaseTimeType(
									  Long.parseLong(compMap.get("tenantId").toString())
									, compMap.get("enterCd").toString()
									, compMap.get("ymd").toString()
									, compMap.get("sabun").toString()
									, timeTypeCd
									, compMap.get("taaCd").toString()
									, dt.parse(taaSdate)
									, dt.parse(taaEdate)
									, Long.parseLong(compMap.get("applId").toString())
									, "TAAIF");
			        		
			        		// 오늘 이전이면 근무마감을 다시 돌려야함.
							if (Integer.parseInt(chkYmd) > Integer.parseInt(compMap.get("ymd").toString())) {
				        		WtmFlexibleEmpService.calcApprDayInfo(Long.parseLong(compMap.get("tenantId").toString())
				        											 , compMap.get("enterCd").toString()
				        											 , compMap.get("ymd").toString()
				        											 , compMap.get("ymd").toString()
				        											 , compMap.get("sabun").toString());
							}
							
						} else {
							// 취소이면 근태삭제
							WtmFlexibleEmpService.removeWtmDayResultInBaseTimeType(
									Long.parseLong(compMap.get("tenantId").toString())
									, compMap.get("enterCd").toString()
									, compMap.get("ymd").toString()
									, compMap.get("sabun").toString()
									, timeTypeCd
									, compMap.get("taaCd").toString()
									, dt.parse(taaSdate)
									, dt.parse(taaEdate)
									, Long.parseLong(compMap.get("applId").toString())
									, "TAAIF");
							
							// 오늘 이전이면 근무마감을 다시 돌려야함.
							if (Integer.parseInt(chkYmd) > Integer.parseInt(compMap.get("ymd").toString())) {
				        		WtmFlexibleEmpService.calcApprDayInfo(Long.parseLong(compMap.get("tenantId").toString())
				        											 , compMap.get("enterCd").toString()
				        											 , compMap.get("ymd").toString()
				        											 , compMap.get("ymd").toString()
				        											 , compMap.get("sabun").toString());
							}
						}
						
						// 근무시간합산은 재정산한다
		        		HashMap<String, Object> setTermMap = new HashMap();
		        		setTermMap.put("tenantId", tenantId);
		        		setTermMap.put("enterCd", enterCd);
		        		setTermMap.put("sabun", compMap.get("sabun").toString());
		        		setTermMap.put("symd", compMap.get("ymd").toString());
		        		setTermMap.put("eymd", compMap.get("ymd").toString());
		        		setTermMap.put("pId", "TAAIF");
		        		wtmFlexibleEmpMapper.createWorkTermBySabunAndSymdAndEymd(setTermMap);
					}
				} 
			}
		}
		return rp;
	}
	
	@Transactional
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
		
		paramMap.put("applStatusCd", APPL_STATUS_APPLY_REJECT);
		
		inbox.setInbox(tenantId, enterCd, applSabun, applId, "APPLY", "결재완료", "보상휴가신청서가  반려되었습니다.", "N");
		
		//메일 전송을 위한 파라미터
		rp.put("from", sabun);
		rp.put("to", applSabun);
		return null;
	}

	@Transactional
	public ReturnParam imsi(Long tenantId, String enterCd, Long applId, String workTypeCd, Map<String, Object> paramMap,
			String status, String sabun, String userId) throws Exception {

		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");
		
		 
		
		WtmApplCode applCode = getApplInfo(tenantId, enterCd, workTypeCd);
		
		WtmAppl appl = saveWtmAppl(tenantId, enterCd, null, workTypeCd, status, sabun, userId);
		applId = appl.getApplId();
		Long compApplId = Long.parseLong(paramMap.get("compApplId").toString());
		String reason = paramMap.get("reason").toString();
		
		WtmCompCanAppl compAppl = saveWtmCompCanAppl(applId, compApplId, reason, sabun, userId);
		paramMap.put("compAppl", compAppl.getCompCanApplId());
				
		//20.05.13 jyp applId 수정
		applLineService.saveWtmApplLine(tenantId, enterCd, Integer.parseInt(applCode.getApplLevelCd()), appl.getApplId(), workTypeCd, sabun, userId);
		paramMap.put("applId", appl.getApplId());
		rp.put("applId", appl.getApplId());
			
		return rp;
	}

	protected WtmCompCanAppl saveWtmCompCanAppl(Long applId, Long compApplId, String reason, String sabun, String userId) {
		 
	
		WtmCompCanAppl compAppl = new WtmCompCanAppl();
		compAppl.setCompApplId(compApplId);
		compAppl.setApplId(applId);
		compAppl.setSabun(sabun);
		compAppl.setReason(reason);
		compAppl.setUpdateId(userId);
		
		return wtmCompCanApplRepo.save(compAppl);
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
		
		return wtmApplRepo.save(appl);
	}

	@Override
	public void delete(Long applId) {
		// TODO Auto-generated method stub
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
