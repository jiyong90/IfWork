package com.isu.ifw.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.isu.ifw.mapper.WtmApplMapper;
import com.isu.ifw.mapper.WtmCompApplMapper;
import com.isu.ifw.repository.WtmApplCodeRepository;
import com.isu.ifw.repository.WtmApplRepository;
import com.isu.ifw.repository.WtmCompApplRepository;

@Service("wtmCompApplListService")
public class WtmCompApplListServiceImpl implements WtmCompApplListService {
	
	private final Logger logger = LoggerFactory.getLogger("ifwFileLog");

	@Autowired
	private WtmCompApplMapper wtmCompApplMapper;

	@Autowired
	WtmApplRepository wtmApplRepo;
	
	@Autowired
	WtmCompApplRepository wtmCompApplRepo;
	
	@Autowired
	WtmApplCodeRepository wtmApplCodeRepo;
	
	@Autowired
	WtmApplLineService applLineService;
	
	@Autowired
	WtmApplMapper applMapper;
	
	/**
	 * 보상 휴가 신청 내역서 조회
	 */
	@Override
	public List<Map<String, Object>> getApprList(Long tenantId, String enterCd, String empNo, Map<String, Object> paramMap, String userId, String sabun) throws Exception{
		
		logger.debug(">>  WtmCompApplServiceImpl.getApprList Start ");

		List<Map<String, Object>> compApplList = null;
		
		try {
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			paramMap.put("userId", userId);
			paramMap.put("sabun", sabun);
			
			compApplList = wtmCompApplMapper.getApplList(paramMap);
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.debug(e.toString(), e);
		}
		
		logger.debug(">>  WtmCompApplServiceImpl.getApprList End ");
		
		return compApplList;
	}


	@Override
	public Map<String, Object> getPossibleUseTime(Long tenantId, String enterCd, String empNo, Map<String, Object> paramMap, String userId, String sabun) throws Exception {
		logger.debug(">>  WtmCompApplServiceImpl.getPossibleUseTime Start ");

		Map<String, Object> possibleUseTime = null;
		
		try {
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			paramMap.put("userId", userId);
			paramMap.put("sabun", sabun);
			
			possibleUseTime = wtmCompApplMapper.getPossibleUseTime(paramMap);
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.debug(e.toString(), e);
		}
		
		logger.debug(">>  WtmCompApplServiceImpl.getPossibleUseTime End ");
		
		return possibleUseTime;
	}
	
	@Override
	public Map<String, Object> getWorkDay(Long tenantId, String enterCd, String empNo, Map<String, Object> paramMap, String userId, String sabun) throws Exception {
		logger.debug(">>  WtmCompApplServiceImpl.getPossibleUseTime Start ");

		Map<String, Object> workDayMap = null;
		
		try {
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			paramMap.put("userId", userId);
			paramMap.put("sabun", sabun);
			
			workDayMap = wtmCompApplMapper.getWorkDayCalendar(paramMap);
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.debug(e.toString(), e);
		}
		
		logger.debug(">>  WtmCompApplServiceImpl.getPossibleUseTime End ");
		
		return workDayMap;
	}
	
}
