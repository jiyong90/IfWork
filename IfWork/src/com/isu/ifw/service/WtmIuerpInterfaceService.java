package com.isu.ifw.service;

/**
 * @author KSJ
 * WTM INTERFACE TABLE DATA(인터페이스된 데이터)를 WTM TABLE로 데이터 이관
 */
public interface WtmIuerpInterfaceService {
	/**
	 * 인터페이스
	 * @param tenantId
	 */
	public void applyIntf(Long tenantId, String gubun);
	
}
