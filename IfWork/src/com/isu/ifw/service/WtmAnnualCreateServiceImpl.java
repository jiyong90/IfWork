package com.isu.ifw.service;

import com.isu.ifw.entity.WtmAnnualCreate;
import com.isu.ifw.entity.WtmTaaCode;
import com.isu.ifw.mapper.WtmAnnualCreateMapper;
import com.isu.ifw.repository.WtmAnnualCreateRepository;
import com.isu.ifw.repository.WtmTaaCodeRepository;
import com.isu.ifw.vo.WtmAnnualCreateVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 빗썸 연차기준관리
 */
@Service
public class WtmAnnualCreateServiceImpl implements WtmAnnualCreateService{

	private final Logger logger = LoggerFactory.getLogger("ifwFileLog");

	@Resource
	WtmAnnualCreateRepository annualCreateReposi;

	@Autowired
	WtmAnnualCreateMapper annualCreateMapper;

	@Resource
	WtmTaaCodeRepository taaCodeReposi;

	/**
	 * 연차내역관리 조회
	 *
	 * @param paramMap
	 * @param tenantId
	 * @param cd
	 * @param enterCd
	 * @return
	 */
	public List<WtmAnnualCreateVo> getList(Long tenantId, String enterCd, String sYmd, String searchKeyword, String searchType) {

		List<WtmAnnualCreateVo> list = new ArrayList<WtmAnnualCreateVo>();
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("tenantId",tenantId);
			paramMap.put("enterCd",enterCd);
			paramMap.put("sYmd",sYmd);
			paramMap.put("searchKeyword",searchKeyword);
			paramMap.put("searchType",searchType);


			list = annualCreateMapper.getAnnualCreateList(paramMap);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public WtmAnnualCreateVo getByUserId(Long tenantId, String enterCd, String sYmd, String sabun, String taaTypeCd) {

		WtmAnnualCreateVo createVo = new WtmAnnualCreateVo();
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("tenantId",tenantId);
			paramMap.put("enterCd",enterCd);
			paramMap.put("sYmd",sYmd);
			paramMap.put("sabun",sabun);
			paramMap.put("taaTypeCd",taaTypeCd);

			createVo = annualCreateMapper.getAnnualUsedInfo(paramMap);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return createVo;
	}

	public List<Map<String, Object>> getTaaTypeCd(Long tenantId, String enterCd) throws Exception {
		List<String> taaList = new ArrayList<String>();
		taaList.add("D");
		taaList.add("A");
		taaList.add("P");


		List<WtmTaaCode> list = taaCodeReposi.findByTenantIdAndEnterCdAndRequestTypeCdAndWorkYn(tenantId, enterCd, taaList, "", "Y");

		List<Map<String, Object>> codeList = new ArrayList();
		for (WtmTaaCode taaCode : list) {
			codeList.add(convertWtmTaaCodetoMap(taaCode));
		}
		return codeList;

	}

	/**
	 * WtmTaaCode Vo -> Map
	 * @param taaCode
	 * @return
	 * @throws Exception
	 */
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


	/**
	 * 등록
	 * @param tenantId
	 * @param enterCd
	 * @param userId
	 * @param sabun
	 * @param yy
	 * @param taaTypeCd
	 * @param symd
	 * @param eymd
	 * @param createCnt
	 * @param note
	 */
	public void save(Long tenantId, String enterCd, String userId, String sabun, String yy, String taaTypeCd, String symd, String eymd, Integer createCnt, String note) {




	}

	public void save(Long tenantId, String enterCd, String userId, Map<String, Object> convertMap) {



		try {
			if(convertMap.containsKey("insertRows") && ((List)convertMap.get("insertRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("insertRows");

				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> l : iList) {
						WtmAnnualCreate wtmAnnualCreate = new WtmAnnualCreate();
						wtmAnnualCreate.setTenantId(tenantId);
						wtmAnnualCreate.setEnterCd(enterCd);
						wtmAnnualCreate.setSabun(l.get("sabun").toString());
						wtmAnnualCreate.setYy(l.get("yy").toString());
						wtmAnnualCreate.setSymd(l.get("symd").toString());
						wtmAnnualCreate.setEymd(l.get("eymd").toString());
						wtmAnnualCreate.setCreateCnt(Float.valueOf(l.get("createCnt").toString()));
						wtmAnnualCreate.setUpdateId(userId);
						wtmAnnualCreate.setTaaTypeCd(l.get("taaTypeCd").toString());
						wtmAnnualCreate.setNote(l.get("note").toString());

						annualCreateReposi.save(wtmAnnualCreate);

					}
				}

			}
			if(convertMap.containsKey("updateRows") && ((List)convertMap.get("updateRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("updateRows");
				List<Map<String, Object>> saveList = new ArrayList();	// 추가용
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> l : iList) {
						WtmAnnualCreate wtmAnnualCreate = new WtmAnnualCreate();
						wtmAnnualCreate.setAnnCreateId(Long.valueOf(l.get("annCreateId").toString()));
						wtmAnnualCreate.setTenantId(tenantId);
						wtmAnnualCreate.setEnterCd(enterCd);
						wtmAnnualCreate.setSabun(l.get("sabun").toString());
						wtmAnnualCreate.setYy(l.get("yy").toString());
						wtmAnnualCreate.setSymd(l.get("symd").toString());
						wtmAnnualCreate.setEymd(l.get("eymd").toString());
						wtmAnnualCreate.setCreateCnt(Float.valueOf(l.get("createCnt").toString()));
						wtmAnnualCreate.setUpdateId(userId);
						wtmAnnualCreate.setTaaTypeCd(l.get("taaTypeCd").toString());
						wtmAnnualCreate.setNote(l.get("note").toString());

						annualCreateReposi.save(wtmAnnualCreate);
					}
				}


			}

			if(convertMap.containsKey("deleteRows") && ((List)convertMap.get("deleteRows")).size() > 0) {
				List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("deleteRows");
				List<Map<String, Object>> saveList = new ArrayList();	// 추가용
				if(iList != null && iList.size() > 0) {
					for(Map<String, Object> l : iList) {
						WtmAnnualCreate wtmAnnualCreate = new WtmAnnualCreate();
						wtmAnnualCreate.setAnnCreateId(Long.valueOf(l.get("annCreateId").toString()));

						annualCreateReposi.delete(wtmAnnualCreate);
					}
				}

			}


		} catch(Exception e) {
			e.printStackTrace();
			logger.debug(e.toString());
		}
	}
}
