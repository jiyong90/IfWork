package com.isu.ifw.service;

import java.util.List;
import java.util.Map;

import com.isu.ifw.vo.ReturnParam;

/**
 * 
 * @author 
 *
 */
public interface WtmInoutService {
	
	//근무계획없이 출퇴근 버튼 각각
	public Map<String, Object> getMenuContext(Long tenantId, String enterCd, String sabun);
	//근무계획보기
	public Map<String, Object> getMenuContext2(Long tenantId, String enterCd, String sabun);
	//웹버전
	public Map<String, Object> getMenuContextWeb(Long tenantId, String enterCd, String sabun);
	//출퇴근정보 업데이트
	public void updateTimecardUnplanned(Map<String, Object> paramMap) throws Exception;
	//delet를 이쪽으로 옮김. calc에서 갱신되지 않은 dayresult로 계산해서..
	public void updateTimecardUnplanned2(Map<String, Object> paramMap) throws Exception;
	//퇴근취소 업데이트
	public void updateTimecardCancel(Map<String, Object> paramMap, String unplanned) throws Exception;
	//휴게정보 업데이트
	public void updateTimecardExcept(Map<String, Object> paramMap) throws Exception;

	//출퇴근정보 업데이트
	public void updateTimecardCancelWeb(Map<String, Object> paramMap) throws Exception;
	//출퇴근정보 업데이트
	public void updateTimecard(Map<String, Object> paramMap) throws Exception;
	//외출복귀정보 업데이트
	public ReturnParam updateGoBack(Map<String, Object> paramMap) throws Exception;

	//타각 후처리 로직
	public void inoutPostProcess(Map<String, Object> paramMap, String unplanned);
	//delte빼고calc만
	public void inoutPostProcess2(Map<String, Object> paramMap, String unplanned);
	//타각데이터 저장
	public boolean insertTimeStamp(Map<String, Object> paramMap) throws Exception;
	//타각데이터 업데이트
	public boolean updateTimeStamp(Map<String, Object> paramMap) throws Exception;
	public List<Map<String, Object>> getMyInoutList(Map<String, Object> paramMap) throws Exception;
	public List<Map<String, Object>> getMyInoutHistory(Map<String, Object> paramMap) throws Exception;
	public Map<String, Object> getMyInoutDetail(Map<String, Object> paramMap) throws Exception;
	public ReturnParam cancel(Map<String, Object> paramMap) throws Exception;
}
