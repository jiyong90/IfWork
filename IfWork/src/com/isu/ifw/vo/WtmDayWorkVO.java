package com.isu.ifw.vo;

import java.util.List;

public class WtmDayWorkVO {
	private String day;
	private String holidayYn;
	private String timeNm;
	private List<WtmDayPlanVO> plans;
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getHolidayYn() {
		return holidayYn;
	}
	public void setHolidayYn(String holidayYn) {
		this.holidayYn = holidayYn;
	}
	public List<WtmDayPlanVO> getPlans() {
		return plans;
	}
	public void setPlans(List<WtmDayPlanVO> plans) {
		this.plans = plans;
	}
	public String getTimeNm() {
		return timeNm;
	}
	public void setTimeNm(String timeNm) {
		this.timeNm = timeNm;
	}
	
	
}

