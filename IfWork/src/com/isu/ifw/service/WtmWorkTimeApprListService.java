package com.isu.ifw.service;

import java.util.List;
import java.util.Map;

import com.isu.ifw.vo.ReturnParam;

public interface WtmWorkTimeApprListService {

	public List<Map<String, Object>> getWorkTimeApprList(Map<String, Object> paramMap) throws Exception;
}
