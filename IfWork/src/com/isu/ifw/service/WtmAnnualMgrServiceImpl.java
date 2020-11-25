package com.isu.ifw.service;

import com.isu.ifw.entity.WtmAnnualMgr;
import com.isu.ifw.entity.WtmTaaCode;
import com.isu.ifw.mapper.WtmAnnualCreateMapper;
import com.isu.ifw.repository.WtmAnnualMgrRepository;
import com.isu.ifw.repository.WtmTaaCodeRepository;
import com.isu.ifw.vo.WtmCodeVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WtmAnnualMgrServiceImpl implements WtmAnnualMgrService{

    private final Logger logger = LoggerFactory.getLogger("ifwFileLog");

    @Resource
    WtmAnnualMgrRepository wtmAnnualMgrRepo;

    @Resource
    WtmTaaCodeRepository taaCodeReposi;

    @Autowired
    WtmAnnualCreateMapper annualCreateMapper;

    /**
     * 연차기준관리 조회
     *
     * @param tenantId
     * @param enterCd
     * @param paramMap
     * @return
     */
    public List<WtmAnnualMgr> getList(Long tenantId, String enterCd) throws Exception{
        return wtmAnnualMgrRepo.findByTenantIdAndEnterCd(tenantId, enterCd);
    }

    public List<Map<String, Object>> getTaaTypeCd(Long tenantId, String enterCd, String taaTypeCd) throws Exception {
        List<String> taaList = new ArrayList<String>();
        taaList.add("D");
        taaList.add("A");
        taaList.add("P");

        List<WtmTaaCode> list = taaCodeReposi.findByTenantIdAndEnterCdAndRequestTypeCdAndWorkYn(tenantId, enterCd, taaList, taaTypeCd,"N");

        List<Map<String, Object>> codeList = new ArrayList();
        for (WtmTaaCode taaCode : list) {
            codeList.add(convertWtmTaaCodetoMap(taaCode));
        }
        return codeList;

    }


    public List<Map<String, Object>> getTaaTypeList(Long tenantId, String enterCd, String taaTypeCd) throws Exception {
        List<String> taaList = new ArrayList<String>();
        taaList.add("D");
        taaList.add("A");
        taaList.add("P");

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("tenantId", tenantId);
        paramMap.put("enterCd", enterCd);
        List<WtmTaaCode> list = annualCreateMapper.getTaaType(paramMap);

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
            codeMap.put("requestTypeCd", taaCode.getRequestTypeCd());
            codeMap.put("note", taaCode.getNote());
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        }

        return codeMap;
    }

    public void save(Long tenantId, String enterCd, String userId, Map<String, Object> convertMap) {

        try {
            if(convertMap.containsKey("insertRows") && ((List)convertMap.get("insertRows")).size() > 0) {
                List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("insertRows");

                if(iList != null && iList.size() > 0) {
                    for(Map<String, Object> l : iList) {
                        WtmAnnualMgr annualMgr = new WtmAnnualMgr();
                        annualMgr.setTenantId(tenantId);
                        annualMgr.setEnterCd(enterCd);
                        annualMgr.setUpdateId(userId);
                        annualMgr.setTaaTypeCd(l.get("taaTypeCd").toString());
                        annualMgr.setMinusYn(l.get("minusYn").toString());
                        annualMgr.setNote(l.get("note").toString());

                        wtmAnnualMgrRepo.save(annualMgr);
                    }
                }

            }
            if(convertMap.containsKey("updateRows") && ((List)convertMap.get("updateRows")).size() > 0) {
                List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("updateRows");
                List<Map<String, Object>> saveList = new ArrayList();	// 추가용
                if(iList != null && iList.size() > 0) {
                    for(Map<String, Object> l : iList) {
                        WtmAnnualMgr annualMgr = new WtmAnnualMgr();
                        annualMgr.setAnnMgrId(Long.parseLong(l.get("annMgrId").toString()));
                        annualMgr.setTenantId(tenantId);
                        annualMgr.setEnterCd(enterCd);
                        annualMgr.setUpdateId(userId);
                        annualMgr.setTaaTypeCd(l.get("taaTypeCd").toString());
                        annualMgr.setMinusYn(l.get("minusYn").toString());
                        annualMgr.setNote(l.get("note").toString());

                        wtmAnnualMgrRepo.save(annualMgr);
                    }
                }


            }

            if(convertMap.containsKey("deleteRows") && ((List)convertMap.get("deleteRows")).size() > 0) {
                List<Map<String, Object>> iList = (List<Map<String, Object>>) convertMap.get("deleteRows");
                List<Map<String, Object>> saveList = new ArrayList();	// 추가용
                if(iList != null && iList.size() > 0) {
                    for(Map<String, Object> l : iList) {
                        Map<String, Object> saveMap = new HashMap();
                        WtmAnnualMgr annualMgr = new WtmAnnualMgr();
                        annualMgr.setAnnMgrId(Long.parseLong(l.get("annMgrId").toString()));
                        wtmAnnualMgrRepo.delete(annualMgr);
                    }
                }

            }


        } catch(Exception e) {
            e.printStackTrace();
            logger.debug(e.toString());
        }

    }

    public List<Map<String, Object>> getCodeList(Long tenantId, String enterCd) throws Exception {

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("tenantId", tenantId);
        paramMap.put("enterCd", enterCd);

        List<WtmCodeVo> list = annualCreateMapper.getAnnualCreateCodeList(paramMap);

        List<Map<String, Object>> codeList = new ArrayList();
        for (WtmCodeVo codeVo : list) {
            codeList.add(convertWtmCodetoMap(codeVo));
        }
        return codeList;
    }

    public Map<String, Object> convertWtmCodetoMap(WtmCodeVo codeVo) throws Exception {
        Map<String, Object> codeMap = new HashMap();

        try {
            codeMap.put("codeId", codeVo.getCodeId());
            codeMap.put("tenantId", codeVo.getTenantId());
            codeMap.put("enterCd", codeVo.getEnterCd());
            codeMap.put("code", codeVo.getCodeCd());
            codeMap.put("codeNm", codeVo.getCodeNm());
            codeMap.put("codeCd", codeVo.getCodeCd());
            codeMap.put("note", codeVo.getNote());
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        }

        return codeMap;
    }

    public List<Map<String, Object>> getCodeList2(Long tenantId, String enterCd) throws Exception {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("tenantId", tenantId);
        paramMap.put("enterCd", enterCd);

        List<WtmCodeVo> list = annualCreateMapper.getAnnualUseCodeList(paramMap);

        List<Map<String, Object>> codeList = new ArrayList();
        for (WtmCodeVo codeVo : list) {
            codeList.add(convertWtmCodetoMap(codeVo));
        }
        return codeList;
    }
}
