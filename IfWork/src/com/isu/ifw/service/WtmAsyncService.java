package com.isu.ifw.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isu.ifw.entity.WtmFlexibleEmp;
import com.isu.ifw.mapper.WtmFlexibleEmpMapper;
import com.isu.ifw.repository.WtmFlexibleEmpRepository;

@Service
public class WtmAsyncService {
	
	@Autowired
	WtmFlexibleEmpMapper wtmFlexibleEmpMapper;
		
	@Autowired
	WtmFlexibleEmpService flexEmpService;
	
	@Autowired
	WtmFlexibleEmpRepository wtmFlexibleEmpRepo;
		
	
	
		@Async("threadPoolTaskExecutor")
		public void createWorkTermtimeByEmployee(Long tenantId, String enterCd, String sabun, String symd, String eymd, String userId) {
			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			paramMap.put("sabun", sabun);
			paramMap.put("symd", symd);
			paramMap.put("eymd", eymd);
			paramMap.put("pId", userId);
			wtmFlexibleEmpMapper.createWorkTermBySabunAndSymdAndEymd(paramMap);
		}
		
		@Async("threadPoolTaskExecutor")
		public void initWtmFlexibleEmpOfWtmWorkDayResult(Long tenantId, String enterCd, String sabun, String symd, String eymd, String userId) {
			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			paramMap.put("sabun", sabun);
			paramMap.put("symd", symd);
			paramMap.put("eymd", eymd);
			paramMap.put("userId", userId);
			paramMap.put("pId", userId); 
			
			wtmFlexibleEmpMapper.initWtmFlexibleEmpOfWtmWorkDayResult(paramMap);
		}
		/**
		 * 일 마감
		 * @param tenantId
		 * @param enterCd
		 * @param userId
		 */
		@Async("threadPoolTaskExecutor")
		public void workdayClose(Long tenantId, String enterCd, String userId) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			Date today = new Date();
			//Date today = WtmUtil.toDate("20191018", "yyyyMMdd");
			String ymd = sdf.format(today);
			
			flexEmpService.calcApprDayInfo(tenantId, enterCd, ymd, ymd, "");
			
			//Calendar cal = new GregorianCalendar();
			//cal.add(Calendar.DATE, -1);
			//Date yesterday = cal.getTime();
			
			//flexEmpService.calcApprDayInfo(tenantId, enterCd, sdf.format(yesterday), sdf.format(yesterday), "");
			
			//workterm 호출.
			List<WtmFlexibleEmp> empList = wtmFlexibleEmpRepo.findByTenantIdAndEnterCdAndYmdBetween(tenantId, enterCd, ymd);
			if(empList!=null && empList.size()>0) {
				for(WtmFlexibleEmp emp : empList) {
					createWorkTermtimeByEmployee(emp.getTenantId(), emp.getEnterCd(), emp.getSabun(), ymd, ymd, userId);
				}
			}
			
		}
}
