package com.isu.ifw.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isu.ifw.entity.WtmInbox;
import com.isu.ifw.mapper.WtmApplMapper;
import com.isu.ifw.mapper.WtmInboxMapper;
import com.isu.ifw.repository.WtmInboxRepository;
import com.isu.ifw.util.WtmUtil;
import com.isu.option.vo.ReturnParam;

@Transactional
@Service
public class WtmInboxServiceImpl implements WtmInboxService{
	
	private final Logger logger = LoggerFactory.getLogger("ifwDBLog");
	
	@Autowired
	private SimpMessagingTemplate template;
	
	@Resource
	WtmInboxRepository inboxRepository;
	
	@Autowired
	WtmApplMapper applMapper;
	
	@Resource
	WtmInboxMapper inboxMapper;
	
	@Async("threadPoolTaskExecutor")
	@Override
	public void setInbox(Long tenantId, String enterCd, String sabun, Long applCodeId, String type, String title, String contents, String checkYn) {
		WtmInbox data = new WtmInbox();
		data.setEnterCd(enterCd);
		data.setSabun(sabun);
		data.setTenantId(tenantId);
		data.setType(type);
		data.setTitle(title);
		data.setContents(contents);
		data.setApplCodeId(String.valueOf(applCodeId));
		data.setCheckYn(checkYn);
		
		try {
			MDC.put("inbox", data.toString());
			logger.info("setInbox", MDC.get("sessionId"), MDC.get("logId"), "S");
			
			data = inboxRepository.save(data);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			MDC.remove("inbox");
			//connect("/api/${tenantId}/${enterCd}/${empNo}/navTop", navTopVue.webSocketCallback);
			if (data != null && data.getId() != null) {
				String url = "/api/"+tenantId+"/"+enterCd+"/"+sabun+"/navTop";
				System.out.println(url);
				this.template.convertAndSend(url, data);
			}
		}
	}
	
	@Override
	public ReturnParam getInboxList(Long tenantId, String enterCd, String sabun) {
		
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");
		
		List<WtmInbox> inboxList = null;
		int apprCnt = 0;
		try {
			inboxList = inboxRepository.findByTenantIdAndEnterCdAndSabunAndCheckYn(tenantId, enterCd, sabun, "N");
			//미결건수 
			Map<String, Object> paramMap = new HashMap();
			paramMap.put("tenantId", tenantId);
			paramMap.put("enterCd", enterCd);
			paramMap.put("sabun", sabun);
			
			apprCnt = applMapper.countByApprList02(paramMap);
			
		
		}catch(Exception e) {
			e.printStackTrace();
			rp.setFail("조회 시 오류가 발생했습니다.");
			return rp;
		}

		//알림 리스트
		rp.put("inboxList", inboxList);
		rp.put("apprCount", apprCnt);
		return rp;
	
	}

	@Override
	public ReturnParam getInboxCount(Long tenantId, String enterCd, String sabun) {
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");
		
		Map<String, Object> toDoPlanDays = null;
		//유연근무제 근무 계획 작성 여부
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("tenantId", tenantId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		paramMap.put("ymd", WtmUtil.parseDateStr(new Date(), null));
		int inboxCount = 0;
		try {
			toDoPlanDays = inboxMapper.getToDoPlanDays(paramMap);
			
			//알림 카운트
			inboxCount = inboxRepository.countByTenantIdAndEnterCdAndSabunAndCheckYn(tenantId, enterCd, sabun, "N");
		}catch(Exception e) {
			e.printStackTrace();
			rp.setFail("조회 시 오류가 발생했습니다.");
			return rp;
		}
		
		//근무 계획 작성 알림
		rp.put("workPlan", toDoPlanDays);
		
		//미결건수 
		int apprCnt = applMapper.countByApprList02(paramMap);
		rp.put("apprCount", apprCnt);
		//알림 카운트
		rp.put("inboxCount", inboxCount);
		
		return rp;
	}
	
	@Override
	public ReturnParam setInboxCheckYn(Long tenantId, String enterCd, String sabun, long id) {
		ReturnParam rp = new ReturnParam();
		rp.setSuccess("");
		try {
			
			WtmInbox inbox = inboxRepository.findById(id);
			if(inbox == null) {
				rp.setFail("메세지를 찾을 수 없습니다.");
				return rp;
			}
			
			inbox.setCheckYn("Y");
			inboxRepository.save(inbox);
		} catch(Exception e) {
			e.printStackTrace();
			rp.setFail("메세지 상태 업데이트 중 오류가 발생했습니다.");
		}
		
		return rp;
	}
}