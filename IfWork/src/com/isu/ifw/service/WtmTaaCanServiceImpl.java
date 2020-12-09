package com.isu.ifw.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.isu.ifw.entity.WtmAppl;
import com.isu.ifw.entity.WtmTaaApplDet;
import com.isu.ifw.entity.WtmTaaCanAppl;
import com.isu.ifw.entity.WtmTaaCode;
import com.isu.ifw.repository.WtmApplRepository;
import com.isu.ifw.repository.WtmTaaApplDetRepository;
import com.isu.ifw.repository.WtmTaaApplRepository;
import com.isu.ifw.repository.WtmTaaCanApplRepository;
import com.isu.ifw.repository.WtmTaaCodeRepository;
import com.isu.ifw.vo.ReturnParam;

@Service("WtmTaaCanApplService")
public class WtmTaaCanServiceImpl implements WtmApplService{

	private static final Logger logger = LoggerFactory.getLogger("ifwFileLog");
	@Autowired
	WtmTaaApplRepository taaApplRepo;
	
	@Autowired
	WtmTaaCanApplRepository taaCanApplRepo;

	@Autowired
	WtmApplRepository wtmApplRepo;

	@Override
	public Map<String, Object> getAppl(Long tenantId, String enterCd, String sabun, Long applId, String userId) {
		return null;
	}

	@Override
	public List<Map<String, Object>> getPrevApplList(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap, String userId) {
		return null;
	}

	@Override
	public Map<String, Object> getLastAppl(Long tenantId, String enterCd, String sabun, Map<String, Object> paramMap, String userId) {
		return null;
	}

	@Override
	public List<Map<String, Object>> getApprList(Long tenantId, String enterCd, String empNo, Map<String, Object> paramMap, String userId) {
		return null;
	}

	@Override
	public ReturnParam request(Long tenantId, String enterCd, Long applId, String workTypeCd, Map<String, Object> paramMap, String sabun, String userId) throws Exception {
		return null;
	}

	@Override
	public ReturnParam apply(Long tenantId, String enterCd, Long applId, int apprSeq, Map<String, Object> paramMap, String sabun, String userId) throws Exception {
		return null;
	}

	@Override
	public ReturnParam requestSync(Long tenantId, String enterCd, Map<String, Object> paramMap, String sabun, String userId) throws Exception {
		return null;
	}


	@Override
	public ReturnParam reject(Long tenantId, String enterCd, Long applId, int apprSeq, Map<String, Object> paramMap, String sabun, String userId) throws Exception {
		return null;
	}

	@Override
	public void delete(Long applId) {

	}

	@Override
	public ReturnParam imsi(Long tenantId, String enterCd, Long applId, String workTypeCd, Map<String, Object> paramMap, String status, String sabun, String userId) throws Exception {
		ReturnParam rp = new ReturnParam();
		rp.setFail("");
		String           applSabun = sabun;

		WtmAppl          appl       = null;
		WtmTaaCanAppl    wtmTaaCanAppl = taaCanApplRepo.findByApplId(applId);
		SimpleDateFormat ymd        = new SimpleDateFormat("yyyyMMdd");
		String applCd = paramMap.get("applCd").toString();
		Long wtmApplId = Long.valueOf(paramMap.get("applId").toString()); 
		
		List<WtmTaaCanAppl> canList = taaCanApplRepo.findByCanApplId(wtmApplId);
		
		if(canList != null && canList.size() > 0) {
			WtmTaaCanAppl canAppl = canList.get(0);
			WtmAppl cAppl = wtmApplRepo.findById(canAppl.getApplId()).get();
			if(cAppl.getApplStatusCd().equals(WtmApplService.APPL_STATUS_APPLY_ING)
					|| cAppl.getApplStatusCd().equals(WtmApplService.APPL_STATUS_APPR_ING)
					) {
				rp.setFail("이미 신청중인 내역이 있습니다.");
				return rp;
			}
			
		}
		
		if(!applCd.equals(WtmApplService.TIME_TYPE_TAA_CAN) && !applCd.equals(WtmApplService.TIME_TYPE_REGA_CAN) ){
			rp.setFail("신청 타입이 옳바르지 않습니다.");
			return rp;
		}

		//기신청 데이터
		if (wtmTaaCanAppl == null) {

			//신청 또는 승인 완료 건에 대해서만
			if (WtmApplService.APPL_STATUS_IMSI.equals(status) || WtmApplService.APPL_STATUS_APPLY_ING.equals(status) || WtmApplService.APPL_STATUS_APPR.equals(status) || WtmApplService.APPL_STATUS_CANCEL.equals(status)) {
				if (appl == null) {
					appl = new WtmAppl();
					appl.setTenantId(tenantId);
					appl.setEnterCd(enterCd);
					appl.setIfApplNo(null);
					appl.setApplYmd(ymd.format(new Date()));
				}


				appl.setApplCd(applCd);
				appl.setApplSabun(applSabun);
				appl.setApplInSabun(applSabun);
				appl.setApplStatusCd(status);
				appl.setUpdateId("TAA_INTF");

				appl = wtmApplRepo.save(appl);

				applId = appl.getApplId();


				WtmTaaCanAppl taaCanAppl = new WtmTaaCanAppl();
				taaCanAppl.setTenantId(tenantId);
				taaCanAppl.setEnterCd(enterCd);
				taaCanAppl.setApplId(applId);
				taaCanAppl.setCanApplId(wtmApplId);
				taaCanAppl.setUpdateId(userId);


				taaCanApplRepo.save(taaCanAppl);


				rp.put("applId", applId);
				rp.setSuccess("");
			} else {
				throw new RuntimeException("결재 신청 상태가 올바르지 않습니다." + status);
			}


		}


		return rp;
	}

	@Override
	public ReturnParam preCheck(Long tenantId, String enterCd, String sabun, String workTypeCd, Map<String, Object> paramMap) {
		return null;
	}

	@Override
	public ReturnParam validate(Long tenantId, String enterCd, String sabun, String workTypeCd, Map<String, Object> paramMap) throws ParseException {
		return null;
	}

	@Override
	public void sendPush() {

	}

	@Override
	public ReturnParam saveWtmApplSts(Long tenantId, String enterCd, String sabun, String userId, Map<String, Object> convertMap) {
		return null;
	}
	
	@Autowired WtmTaaApplDetRepository taaApplDetRepo;
	@Autowired WtmTaaCodeRepository taaCodeRepo;
	
	public WtmTaaCode findTaaCodeByApplId(Long applId) {
		WtmTaaCanAppl canAppl = taaCanApplRepo.findByApplId(applId);
		List<WtmTaaApplDet> taaApplDets = taaApplDetRepo.findByApplId(canAppl.getCanApplId());
		if(taaApplDets != null && taaApplDets.size() > 0) {
			//상위 코드는 1개여야한다.
			WtmTaaApplDet taaApplDet = taaApplDets.get(0);
			
			return taaCodeRepo.findByTenantIdAndEnterCdAndTaaCd(canAppl.getTenantId(), canAppl.getEnterCd(), taaApplDet.getTaaCd());
			
		}
		return null;
	}
	
}
