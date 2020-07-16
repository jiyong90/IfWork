package com.isu.ifw.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.isu.ifw.mapper.WtmWorkTimeApprListMapper;

@Service("WtmWorkTimeApprListService")
public class WtmWorkTimeApprListServiceImpl implements WtmWorkTimeApprListService{
	
	@Autowired
	WtmWorkTimeApprListMapper wtmWorkTimeApprListMapper;

	@Override
	public List<Map<String, Object>> getWorkTimeApprList(Map<String, Object> paramMap) throws Exception {
		return wtmWorkTimeApprListMapper.getWorkTimeApprList(paramMap);
	}

}
