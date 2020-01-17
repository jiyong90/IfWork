package com.isu.ifw.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.isu.ifw.vo.ReturnParam;

/**
 * 로그인 서비스
 *
 * @author ParkMoohun
 *
 */
@Service("WtmInboxService")
public interface WtmInboxService{
	
	public void setInbox(Long tenantId, String enterCd, String sabun, Long applCodeId, String type, String title, String contents, String checkYn);
	
	public ReturnParam getInboxCount(Long tenantId, String enterCd, String sabun);

	public ReturnParam getInboxList(Long tenantId, String enterCd, String sabun);
	
	public ReturnParam setInboxCheckYn(Long tenantId, String enterCd, String sabun, long id);
	
	public void sendPushMessage(Long tenantId, String enterCd, String category, List<String> targetEmp, String title, String content)throws Exception;
}