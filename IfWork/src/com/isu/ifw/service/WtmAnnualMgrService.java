package com.isu.ifw.service;

import com.isu.ifw.entity.WtmAnnualMgr;
import com.isu.ifw.entity.WtmTaaCode;
import com.isu.ifw.vo.WtmCodeVo;

import java.util.List;
import java.util.Map;

public interface WtmAnnualMgrService {

    public List<WtmAnnualMgr> getList(Long tenantId, String enterCd) throws Exception;

    public List<Map<String, Object>> getTaaTypeCd(Long tenantId, String enterCd, String taaTypeCd) throws Exception ;

    public List<Map<String, Object>> getTaaTypeList(Long tenantId, String enterCd, String taaTypeCd) throws Exception;

    public Map<String, Object> convertWtmTaaCodetoMap(WtmTaaCode taaCode) throws Exception;

    public void save(Long tenantId, String enterCd, String userId, Map<String, Object> convertMap) throws Exception;

    public List<Map<String, Object>> getCodeList(Long tenantId, String enterCd) throws Exception ;

    public Map<String, Object> convertWtmCodetoMap(WtmCodeVo codeVo) throws Exception;

    public List<Map<String, Object>> getCodeList2(Long tenantId, String enterCd) throws Exception;

}
