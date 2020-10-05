package com.isu.ifw.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isu.ifw.entity.WtmFlexibleEmp;
import com.isu.ifw.mapper.WtmFlexibleEmpMapper;
import com.isu.ifw.repository.WtmFlexibleEmpRepository;
import com.isu.ifw.repository.WtmOtSubsApplRepository;

@Service
public class WtmAsyncService {
	
	private static final Logger logger = LoggerFactory.getLogger("ifwFileLog");
	
	@Autowired
	WtmFlexibleEmpMapper wtmFlexibleEmpMapper;
		
	@Autowired
	WtmFlexibleEmpService flexEmpService;
	
	@Autowired
	WtmFlexibleEmpRepository wtmFlexibleEmpRepo;
	
	@Autowired
	WtmFlexibleEmpMapper flexEmpMapper;
	
	@Autowired
	WtmOtSubsApplRepository otSubsApplRepo;
		
	@Autowired private WtmCalcService calcService;
	
		@Async("threadPoolTaskExecutor")
		@Transactional
		public void createWorkTermtimeByEmployee(Long tenantId, String enterCd, String sabun, String symd, String eymd, String userId, boolean initResult) {
			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			paramMap.put("sabun", sabun);
			paramMap.put("symd", symd);
			paramMap.put("eymd", eymd);
			paramMap.put("userId", userId);
			paramMap.put("pId", userId); 
			
			if(initResult)
				wtmFlexibleEmpMapper.initWtmFlexibleEmpOfWtmWorkDayResult(paramMap);

			calcService.P_WTM_FLEXIBLE_EMP_WORKTERM_C(tenantId, enterCd, sabun, symd);
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
					createWorkTermtimeByEmployee(emp.getTenantId(), emp.getEnterCd(), emp.getSabun(), ymd, ymd, userId, false);
				}
			}
			
		}
		
}
