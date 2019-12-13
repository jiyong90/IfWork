package com.isu.ifw.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.isu.ifw.entity.WtmCode;
import com.isu.ifw.entity.WtmCodeGrp;
import com.isu.ifw.entity.WtmTimeBreakMgr;
import com.isu.ifw.mapper.LoginMapper;
import com.isu.ifw.mapper.WtmApplMapper;
import com.isu.ifw.mapper.WtmCalendarMapper;
import com.isu.ifw.mapper.WtmInoutHisMapper;
import com.isu.ifw.mapper.WtmWorktimeMapper;
import com.isu.ifw.repository.WtmCodeGrpRepository;
import com.isu.ifw.repository.WtmCodeRepository;
import com.isu.ifw.vo.ReturnParam;

@Service("mobileService")
public class WtmMobileServiceImpl implements WtmMobileService{
	
	private final Logger logger = LoggerFactory.getLogger("ifwFileLog");
	
	@Autowired
	WtmWorktimeMapper timeMapper;
	
	//기간 리스트 조회
	@Override
	public List<Map<String, Object>> getTermList(Map<String, Object> paramMap) {

		return timeMapper.getTermList(paramMap);	
	}

	//기간 내에 포함된 부서원 조회
	@Override
	public List<Map<String, Object>> getTeamList(Map<String, Object> paramMap) {

		return timeMapper.getTeamList(paramMap);	
	}

}