package com.isu.ifw.service;

import com.isu.ifw.common.entity.CommUser;
import com.isu.ifw.common.repository.WtmCommUserRepository;
import com.isu.ifw.common.service.TenantConfigManagerService;
import com.isu.ifw.entity.WtmEmpAddr;
import com.isu.ifw.entity.WtmEmpHis;
import com.isu.ifw.entity.WtmOtp;
import com.isu.ifw.entity.WtmPropertie;
import com.isu.ifw.mapper.EncryptionMapper;
import com.isu.ifw.mapper.WtmEmpAddrMapper;
import com.isu.ifw.mapper.WtmEmpHisMapper;
import com.isu.ifw.mapper.WtmIfEmpMsgMapper;
import com.isu.ifw.repository.*;
import com.isu.ifw.util.Sha256;
import com.isu.ifw.util.WtmUtil;
import com.isu.ifw.vo.ReturnParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;


@Service("empMgrService")
public class WtmEmpMgrServiceImpl implements WtmEmpMgrService{
	
	private final Logger logger = LoggerFactory.getLogger("ifwDBLog");
	
	@Resource
	WtmEmpHisRepository empHisRepository;
	
	@Resource
	WtmIfEmpMsgRepository empMsgRepository;
	
	@Autowired
	WtmIfEmpMsgMapper ifEmpMsgMapper;
	
	@Autowired
	WtmEmpHisMapper wtmEmpHisMapper;

	@Autowired
	WtmFlexibleEmpService empService;
	
	@Autowired
	EncryptionMapper encryptionMapper;
	
	@Autowired
	@Qualifier("WtmTenantConfigManagerService")
	TenantConfigManagerService tcms;
	
	@Autowired
	WtmEmpAddrMapper empAddrMapper;
	
	@Autowired
	WtmEmpAddrRepository empAddrRepo;
	
	@Autowired
	WtmOtpRepository otpRepo;
	
	@Autowired
	WtmCommUserRepository commUserRepo;
	
	@Autowired
	WtmPropertieRepository propertieRepo;
	
	@Override
	public List<Map<String, Object>> getEmpHisList(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap) {
		List<Map<String, Object>> empList = new ArrayList();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		
		String sYmd = WtmUtil.parseDateStr(new Date(), "yyyyMMdd");
		if(!paramMap.containsKey("sYmd")) {
			paramMap.put("sYmd", "");
		} else {
			sYmd = paramMap.get("sYmd").toString().replaceAll("[-.]", "");
			paramMap.put("sYmd", sYmd);
		}
		
		List<String> auths = empService.getAuth(tenantId, enterCd, sabun);
		if(auths!=null && !auths.contains("FLEX_SETTING") && auths.contains("FLEX_SUB")) {
			//하위 조직 조회
			paramMap.put("orgList", empService.getLowLevelOrgList(tenantId, enterCd, sabun, sYmd));
		}
		
		empList =  wtmEmpHisMapper.getEmpHisList(paramMap);
		
//		List<WtmEmpHis> list = empHisRepository.findByTenantIdAndEnterCd(tenantId, enterCd, paramMap.containsKey("sYmd")?paramMap.get("sYmd").toString():"", paramMap.get("searchKeyword").toString());
//		
//		for(WtmEmpHis l : list) {
//			Map<String, Object> emp = new HashMap();
//			emp.put("empHisId", l.getEmpHisId());
//			emp.put("sabun", l.getSabun());
//			emp.put("empNm", l.getEmpNm());
//			emp.put("empEngNm", l.getEmpEngNm());
//			emp.put("symd", l.getSymd());
//			emp.put("eymd", l.getEymd());
//			emp.put("statusCd", l.getStatusCd());
//			emp.put("orgCd", l.getOrgCd());
//			emp.put("businessPlaceCd", l.getBusinessPlaceCd());
//			emp.put("dutyCd", l.getDutyCd());
//			emp.put("posCd", l.getPosCd());
//			emp.put("classCd", l.getClassCd());
//			emp.put("jobGroupCd", l.getJobGroupCd());
//			emp.put("jobCd", l.getJobCd());
//			emp.put("payTypeCd", l.getPayTypeCd());
//			emp.put("leaderYn", l.getLeaderYn());
//			emp.put("note", l.getNote());
//			empList.add(emp);
//		}
		return empList;
	}

	@Override
	public Map<String, Object> getEmpHis(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap) {
		
		Map<String, Object> emp = new HashMap();
		
		List<String> auths = empService.getAuth(tenantId, enterCd, sabun);
		if(auths!=null && !auths.contains("FLEX_SETTING") && auths.contains("FLEX_SUB")) {
			//하위 조직 조회
			paramMap.put("orgList", empService.getLowLevelOrgList(tenantId, enterCd, sabun, WtmUtil.parseDateStr(new Date(), "yyyyMMdd")));
		}
		
		emp = wtmEmpHisMapper.getEmpHis(paramMap);
//		WtmEmpHis l = empHisRepository.findByEmpHisId(Long.parseLong(paramMap.get("empHisId").toString()));
//		emp.put("sabun", l.getSabun());
//		emp.put("empNm", l.getEmpNm());
//		emp.put("empEngNm", l.getEmpEngNm());
//		emp.put("symd", l.getSymd());
//		emp.put("eymd", l.getEymd());
//		emp.put("statusCd", l.getStatusCd());
//		emp.put("orgCd", l.getOrgCd());
//		emp.put("businessPlaceCd", l.getBusinessPlaceCd());
//		emp.put("dutyCd", l.getDutyCd());
//		emp.put("posCd", l.getPosCd());
//		emp.put("classCd", l.getClassCd());
//		emp.put("jobGroupCd", l.getJobGroupCd());
//		emp.put("jobCd", l.getJobCd());
//		emp.put("payTypeCd", l.getPayTypeCd());
//		emp.put("leaderYn", l.getLeaderYn());
//		emp.put("note", l.getNote());
		return emp;
	}
	
	@Override
	public List<Map<String, Object>> getEmpIfMsgList(Long tenantId, String enterCd, Map<String, Object> paramMap) {
		List<Map<String, Object>> empList = new ArrayList();
		
		String ymd = null;
		if(paramMap.get("sYmd")!=null && !"".equals(paramMap.get("sYmd"))) {
			ymd = paramMap.get("sYmd").toString().replaceAll("[-.]", "");
		} else {
			ymd = WtmUtil.parseDateStr(new Date(), "yyyyMMdd");
		}
		
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("ymd", ymd);
		
		/*List<WtmIfEmpMsg> list = empMsgRepository.findByTenantIdAndEnterCd(tenantId, enterCd, s, paramMap.get("searchKeyword").toString());
		
		for(WtmIfEmpMsg l : list) {
			Map<String, Object> emp = new HashMap();
			emp.put("sabun", l.getSabun());
			emp.put("chgYmd", l.getChgYmd());
			emp.put("chgTypeCd", l.getChgTypeCd());
			emp.put("oldValue", l.getOldValue());
			emp.put("newValue", l.getNewValue());
			emp.put("note", l.getNote());
			empList.add(emp);
		}
		return empList; */
		
		return ifEmpMsgMapper.getIfEmpMsg(paramMap);
		
	}
	
	@Transactional
	@Override
	public int saveEmpHis(Long tenantId, String enterCd, Map<String, Object> convertMap, String userId) {
		int cnt = 0;
		try {
			boolean hrInterfaceYn = true;
			
			WtmPropertie propertie = propertieRepo.findByTenantIdAndEnterCdAndInfoKey(tenantId, enterCd, "OPTION_HR_INTERFACE_YN");
			if(propertie!=null && "N".equalsIgnoreCase(propertie.getInfoValue())) {
				hrInterfaceYn = false;
			}
			
			String aesKey = tcms.getConfigValue(tenantId, "SECURITY.AES.KEY", true, "");
			
			List<String> sabunList = null;
			if(convertMap.containsKey("mergeRows") && ((List)convertMap.get("mergeRows")).size() > 0) {
				List<Map<String, Object>> mergeList = (List<Map<String, Object>>) convertMap.get("mergeRows");
				
				cnt = mergeList.size();
				if(mergeList != null && cnt > 0) {
					sabunList = new ArrayList<String>();
					
					List<WtmEmpHis> empHisList = new ArrayList<WtmEmpHis>();
					for(Map<String, Object> m : mergeList) {
						WtmEmpHis empHis = new WtmEmpHis();
						String sabun = m.get("sabun").toString();
						String symd = m.get("symd").toString();
						String eymd = m.get("eymd").toString();
						
						List<WtmEmpHis> empList = empHisRepository.findByTenantIdAndEnterCdAndSabunAndBetweenSymdAndEymd(tenantId, enterCd, sabun, symd, eymd);
						if(empList!=null && empList.size()>0) {
							for(WtmEmpHis o : empList) {
								//신청기간내에 시작 종료가 포함되어있을 경우
								if(Integer.parseInt(symd) <= Integer.parseInt(o.getSymd()) && Integer.parseInt(eymd) >= Integer.parseInt(o.getEymd())) {
									empHisRepository.delete(o);
								//신청 시작일과 종료일이 기존 근무정보 내에 있을 경우 
								}else if(Integer.parseInt(symd) > Integer.parseInt(o.getSymd()) && Integer.parseInt(eymd) < Integer.parseInt(o.getEymd())) {
									String ed = o.getEymd();
									
									o.setEymd(WtmUtil.parseDateStr(WtmUtil.addDate(WtmUtil.toDate(symd, ""), -1),null));
									empHisRepository.save(o);
									
									WtmEmpHis newEmpHis = new WtmEmpHis();
									newEmpHis.setTenantId(o.getTenantId());
									newEmpHis.setEnterCd(o.getEnterCd());
									newEmpHis.setSabun(o.getSabun());
									newEmpHis.setEmpNm(o.getEmpNm());
									newEmpHis.setEmpEngNm(o.getEmpEngNm());
									newEmpHis.setSymd(WtmUtil.parseDateStr(WtmUtil.addDate(WtmUtil.toDate(eymd, ""), 1),null));
									newEmpHis.setEymd(ed);
									newEmpHis.setEmpYmd(o.getEmpYmd());
									newEmpHis.setStatusCd(o.getStatusCd());
									newEmpHis.setOrgCd(o.getOrgCd());
									newEmpHis.setBusinessPlaceCd(o.getBusinessPlaceCd());
									newEmpHis.setDutyCd(o.getDutyCd());
									newEmpHis.setPosCd(o.getPosCd());
									newEmpHis.setClassCd(o.getClassCd());
									newEmpHis.setJobGroupCd(o.getJobGroupCd());
									newEmpHis.setJobCd(o.getJobCd());
									newEmpHis.setPayTypeCd(o.getPayTypeCd());
									newEmpHis.setOrgPath(o.getOrgPath());
									newEmpHis.setLeaderYn(o.getLeaderYn());
									newEmpHis.setNote(o.getNote());
									newEmpHis.setEmpId(o.getEmpId());
									newEmpHis.setUpdateId(userId);
									
									empHisRepository.save(newEmpHis);

								//시작일만 포함되어있을 경우 
								}else if(Integer.parseInt(symd) >= Integer.parseInt(o.getSymd()) && Integer.parseInt(eymd) < Integer.parseInt(o.getEymd())) {
									//시작일을 신청종료일 다음날로 업데이트 해주자
									o.setSymd(WtmUtil.parseDateStr(WtmUtil.addDate(WtmUtil.toDate(eymd, ""), 1),null));
									empHisRepository.save(o);
								//종료일만 포함되어있을 경우
								}else if(Integer.parseInt(symd) > Integer.parseInt(o.getSymd()) && Integer.parseInt(eymd) <= Integer.parseInt(o.getEymd())) {
									//종료일을 신청시작일 전날로 업데이트 해주자
									o.setEymd(WtmUtil.parseDateStr(WtmUtil.addDate(WtmUtil.toDate(symd, ""), -1),null));
									empHisRepository.save(o);
									
								}
							}
						}
						
						empHis.setTenantId(tenantId);
						empHis.setEnterCd(enterCd);
						empHis.setSabun(m.get("sabun").toString());
						empHis.setEmpNm(m.get("empNm").toString());
						if(m.get("empEngNm")!=null && !"".equals(m.get("empEngNm")))
							empHis.setEmpEngNm(m.get("empEngNm").toString());
						empHis.setSymd(m.get("symd").toString());
						empHis.setEymd(m.get("eymd").toString());
						if(m.get("empYmd")!=null && !"".equals(m.get("empYmd")))
							empHis.setEmpYmd(m.get("empYmd").toString());
						if(m.get("statusCd")!=null && !"".equals(m.get("statusCd")))
							empHis.setStatusCd(m.get("statusCd").toString());
						if(m.get("orgCd")!=null && !"".equals(m.get("orgCd")))
							empHis.setOrgCd(m.get("orgCd").toString());
						if(m.get("businessPlaceCd")!=null && !"".equals(m.get("businessPlaceCd")))
							empHis.setBusinessPlaceCd(m.get("businessPlaceCd").toString());
						if(m.get("dutyCd")!=null && !"".equals(m.get("dutyCd")))
							empHis.setDutyCd(m.get("dutyCd").toString());
						if(m.get("posCd")!=null && !"".equals(m.get("posCd")))
							empHis.setPosCd(m.get("posCd").toString());
						if(m.get("classCd")!=null && !"".equals(m.get("classCd")))
							empHis.setClassCd(m.get("classCd").toString());
						if(m.get("jobGroupCd")!=null && !"".equals(m.get("jobGroupCd")))
							empHis.setJobGroupCd(m.get("jobGroupCd").toString());
						if(m.get("jobCd")!=null && !"".equals(m.get("jobCd")))
							empHis.setJobCd(m.get("jobCd").toString());
						if(m.get("payTypeCd")!=null && !"".equals(m.get("payTypeCd")))
							empHis.setPayTypeCd(m.get("payTypeCd").toString());
						if(m.get("orgPath")!=null && !"".equals(m.get("orgPath")))
							empHis.setOrgPath(m.get("orgPath").toString());
						if(m.get("leaderYn")!=null && !"".equals(m.get("leaderYn")))
							empHis.setLeaderYn(m.get("leaderYn").toString());
						empHis.setNote(m.get("note").toString());
						
						Map<String, Object> pMap = new HashMap<String, Object>();
						pMap.put("encryptStr", tenantId+""+enterCd+""+m.get("sabun").toString());
						pMap.put("encryptKey", aesKey);
						pMap.put("enterCd", enterCd);
						System.out.println("pMap : " + pMap.toString());
						Map<String, Object> encryptMap = (Map<String, Object>) encryptionMapper.getAesEncrypt(pMap);
						if(encryptMap!=null) {
							empHis.setEmpId(encryptMap.get("encryptStr").toString());
						}
						
						empHis.setUpdateId(userId);
						empHisList.add(empHis);
						
						sabunList.add(sabun);
					}
					
					empHisRepository.saveAll(empHisList);
				}
				
				//comm_user에 넣어줌
				if(!hrInterfaceYn) {
					//wtmEmpHisMapper.insertCommUser(tenantId);
					
					//비밀번호 사번으로 변경
					List<Map<String, Object>> pwList = new ArrayList<Map<String, Object>>();
					if(sabunList!=null && sabunList.size()>0) {
						String encKey = tcms.getConfigValue(tenantId, "SECURITY.SHA.KEY", true, "");
						String shaRepeat = tcms.getConfigValue(tenantId, "SECURITY.SHA.REPEAT", true, "");
						int repeatCount = 1;
						if(shaRepeat!=null && !"".equals(shaRepeat))
							repeatCount = Integer.parseInt(shaRepeat);
						
						for(String sabun : sabunList) {
							Map<String, Object> pwMap = new HashMap<String, Object>();
							String password = Sha256.getHash(sabun, encKey, repeatCount);
							
							Map<String, Object> insMap = new HashMap<>();
							insMap.put("tenantId", tenantId);
							insMap.put("sabun", sabun);
							insMap.put("password", password);
							
							wtmEmpHisMapper.insertCommUser(insMap);
//							
//							pwMap.put("sabun", sabun);
//							pwMap.put("password", password);
//							pwList.add(pwMap);
						}
//						
//						if(pwList.size()>0) {
//							Map<String, Object> pMap = new HashMap<String, Object>();
//							pMap.put("tenantId", tenantId);
//							pMap.put("enterCd", enterCd);
//							pMap.put("aesKey", aesKey);
//							pMap.put("pwList", pwList);
//							wtmEmpHisMapper.updateCommUserPw(pMap);
//						}
						
					}
				}
				
				MDC.put("merge cnt", "" + cnt);
			}
			
			if(convertMap.containsKey("deleteRows") && ((List)convertMap.get("deleteRows")).size() > 0) {
				sabunList = new ArrayList<String>();
				List<Map<String, Object>> deleteList = (List<Map<String, Object>>) convertMap.get("deleteRows");
				List<Long> empHisIds = new ArrayList<Long>();
				if(deleteList != null && deleteList.size() > 0) {
					for(Map<String, Object> d : deleteList) {
						Long empHisId = Long.valueOf(d.get("empHisId").toString());
						empHisIds.add(empHisId);
					}
					
					//comm_user삭제
					if(!hrInterfaceYn) {
						Map<String, Object> pMap = new HashMap<String, Object>();
						pMap.put("aesKey", aesKey);
						pMap.put("empHisIds", empHisIds);
						wtmEmpHisMapper.deleteCommUser(pMap);
					}
					
					empHisRepository.deleteByEmpHisIdsIn(empHisIds);
					
					cnt += empHisIds.size();
				}
				
				MDC.put("delete cnt", "" + empHisIds.size());
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.warn(e.toString(), e);
		} finally {
			logger.debug("saveEmpHis Service End", MDC.get("sessionId"), MDC.get("logId"), MDC.get("type"));
			MDC.clear();
		}
		return cnt;
	}
	
	@Override
	public boolean checkPasswordCertificate(Long tenantId, String enterCd, String userInfo){
		
		String passwordCertificate = tcms.getConfigValue(tenantId, "WTMS.LOGIN.PASSWORD_CERTIFICATE", true, "");
			
		boolean isValid = false;
		
		if("PHONE".equalsIgnoreCase(passwordCertificate)) {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			paramMap.put("handPhone", userInfo);
			Map<String, Object> empAddr = empAddrMapper.findByTenantIdAndEnterCdAndHandPhone(paramMap);
			
			if(empAddr!=null) {
				isValid = true;
			}
			
		} else {
			WtmEmpAddr empAddr = empAddrRepo.findByTenantIdAndEnterCdAndEmail(tenantId, enterCd, userInfo);
			
			if(empAddr!=null) {
				isValid = true;
			}
			
		}

		return isValid;
		
	}
	
	@Override
	public ReturnParam codeCheck(Long tenantId, String enterCd, String otp, String userInfo){
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");
		
		try{
			String passwordCertificate = tcms.getConfigValue(tenantId, "WTMS.LOGIN.PASSWORD_CERTIFICATE", true, "");
			
			Long empAddrId = null;
			if("PHONE".equalsIgnoreCase(passwordCertificate)) {
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("tenantId", tenantId);
				paramMap.put("enterCd", enterCd);
				paramMap.put("handPhone", userInfo);
				Map<String, Object> empAddr = empAddrMapper.findByTenantIdAndEnterCdAndHandPhone(paramMap);
				
				empAddrId = Long.valueOf(empAddr.get("empAddrId").toString());
			} else {
				WtmEmpAddr empAddr = empAddrRepo.findByTenantIdAndEnterCdAndEmail(tenantId, enterCd, userInfo);
				
				empAddrId = empAddr.getEmpAddrId();
			}
			
			List<WtmOtp> result = otpRepo.findByOtpAndResourceIdAndExpireDateGreaterThanEqualOrderByExpireDateDesc(otp, empAddrId, new Date());
			
			if(result==null || result.size()==0) {
				rp.setFail("인증코드가 유효하지 않습니다.");
			} 
			
		}catch(Exception e){
			e.printStackTrace();
			rp.setFail("인증코드가 유효하지 않습니다.");
		} 
		return rp;
	}
	
	@Transactional
	@Override
	public void changePw(Long tenantId, String tsId, String enterCd, Map<String, Object> paramMap){
		String password = paramMap.get("password").toString();
		
		String passwordCertificate = tcms.getConfigValue(tenantId, "WTMS.LOGIN.PASSWORD_CERTIFICATE", true, "");
		
		String sabun = null;
		if("PHONE".equalsIgnoreCase(passwordCertificate)) {
			Map<String, Object> pMap = new HashMap<String, Object>();
			pMap.put("tenantId", tenantId);
			pMap.put("enterCd", enterCd);
			pMap.put("handPhone", paramMap.get("userInfo").toString());
			Map<String, Object> empAddr = empAddrMapper.findByTenantIdAndEnterCdAndHandPhone(pMap);
			
			sabun = empAddr.get("sabun").toString();
		} else {
			WtmEmpAddr empAddr = empAddrRepo.findByTenantIdAndEnterCdAndEmail(tenantId, enterCd, paramMap.get("userInfo").toString());
			
			sabun = empAddr.getSabun();
		}
		
		String encKey = paramMap.get("encKey").toString();
		int repeatCount = Integer.valueOf(paramMap.get("repeatCount").toString());
		
		CommUser user = commUserRepo.findByTenantIdAndEnterCdAndLoginIdAndEncKey(tenantId, enterCd, sabun, encKey);
		
		try {
			password = Sha256.getHash(password, encKey, repeatCount);
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		 
		user.setPassword(password);
		user.setLoginFailureCount(0);
		user.setAccountLockoutYn("N");
		commUserRepo.save(user);
	}
	
}