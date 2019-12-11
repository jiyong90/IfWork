package com.isu.ifw.vo;

import java.util.HashMap;

/**
 * API 에서 사용하는 모든 결과 파라미터의 최 상위 클래스
 * 상태/메시지/resourceId를 기본 속성 으로 갖는다.
 * @author admin
 *
 */
public class ReturnParam extends HashMap<String, Object> {

	/*String status; // 작업상태 상태 
	String message; // 작업상태에 따른 메시지
	String resourceId; // 리소스ID - 단말기에 부여된 고유 ID
*/	
	public String getStatus() {
		return (String)get("status");
		//return status;
	}
	public void setStatus(String status) {
	
		this.put("status",status);
	}
	/*public String getMessage() {
		return message;
	}*/
	public void setMessage(String message) {
//		this.message = message;
		put("message",message);
	}
	/*public String getResourceId() {
		return resourceId;
		
	}*/
	public void setResourceId(String resourceId) {
//		this.resourceId = resourceId;
		put("resourceId",resourceId);
	}
	
	/**
	 * 실패 메시지를 세팅. 이 때 status도  FAIL로 자동으로 바뀐다. 
	 * 
	 * @param message 저장될 메시지
	 */
	public void setFail(String message){
		//this.status = "FAIL";
		setStatus("FAIL");
		setMessage(message);
	}
	
	/**
	 * 성공 메시지를 세팅. 이 때 status도 OK로 자동으로 바뀐다.
	 * 
	 * @param message 저장될 메시지
	 */
	public void setSuccess(String message){
//		this.status = "OK";
		setStatus("OK");
		setMessage(message);
	}
	
	
}