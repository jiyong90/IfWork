package com.isu.ifw.service;

import com.isu.ifw.entity.WtmAnnualMgr;
import com.isu.ifw.entity.WtmTaaCode;
import com.isu.ifw.mapper.WtmValidatorMapper;
import com.isu.ifw.repository.WtmTaaCodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WtmTaaServiceImpl implements WtmTaaService{

	private final Logger logger = LoggerFactory.getLogger("ifwFileLog");

	@Autowired
	WtmTaaCodeRepository taaCodeRepo;

	@Qualifier("WtmTaaApplService")
	@Autowired
	WtmApplService taaApplService;

	@Autowired
	WtmCalcServiceImpl calcService;

	@Autowired
	WtmValidatorMapper validatorMapper;

	
	public List<Map<String, Object>> getWtmTaaCodelist(Long tenantId, String enterCd) throws Exception {

		List<WtmTaaCode> list = taaCodeRepo.findByTenantIdAndEnterCdAndTaaTypeCd(tenantId, enterCd, "20");

		List<Map<String, Object>> codeList = new ArrayList();
		for (WtmTaaCode taaCode : list) {
			codeList.add(convertWtmTaaCodetoMap(taaCode));
		}
		return codeList;
	}

	public Map<String, Object> convertWtmTaaCodetoMap(WtmTaaCode taaCode) throws Exception {


		Map<String, Object> codeMap = new HashMap();

		try {
			codeMap.put("codeId", taaCode.getTaaCodeId());
			codeMap.put("tenantId", taaCode.getTenantId());
			codeMap.put("enterCd", taaCode.getEnterCd());
			codeMap.put("code", taaCode.getTaaCd());
			codeMap.put("codeNm", taaCode.getTaaNm());
			codeMap.put("codeCd", taaCode.getTaaInfoCd());
			codeMap.put("note", taaCode.getNote());
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception();
		}

		return codeMap;
	}

	public WtmAnnualMgr getTaaInfo(Long tenantId, String enterCd, Long applId) {
		return null;

	}
}
