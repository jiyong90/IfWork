package com.isu.ifw.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.isu.ifw.mapper.WtmApplMapper;
import com.isu.ifw.mapper.WtmInoutHisMapper;
import com.isu.ifw.mapper.WtmWorktimeMapper;
import com.isu.ifw.repository.WtmApplCodeRepository;

@Service("mobileService")
public class WtmMobileServiceImpl implements WtmMobileService{
	
	private final Logger logger = LoggerFactory.getLogger("ifwFileLog");
	
	@Autowired
	WtmWorktimeMapper timeMapper;
	
	@Autowired
	WtmInoutHisMapper inoutMapper;

	@Autowired
	WtmApplMapper applMapper;
	
	@Autowired
	WtmApplCodeRepository wtmApplCodeRepo;

	//기간 리스트 조회
	@Override
	public List<Map<String, Object>> getTermList(Map<String, Object> paramMap) throws Exception  {

		return timeMapper.getTermList(paramMap);	
	}

	//기간 내에 포함된 부서원 조회
	@Override
	public List<Map<String, Object>> getTeamList(Map<String, Object> paramMap) throws Exception  {

		return timeMapper.getTeamList(paramMap);	
	}

	//특정 기간 타각이력
	@Override
	public List<Map<String, Object>> getTeamDetail(Map<String, Object> paramMap) throws Exception {

		return inoutMapper.getInoutListTerm(paramMap);
	}
	
	//결재라인조회
	public List<Map<String, Object>> getApprLines(Map<String, Object> paramMap) throws Exception {
		return null;
		
	}

}