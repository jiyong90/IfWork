package com.isu.ifw.common;

import java.util.List;

/**
 * jqgrid에서 바로 사용할 수 있는 '페이지'를 의미하는 목록 데이터 구조.
 * @author admin
 *
 */
public class Page <E>{

	int total; // 전체 페이지 수
	int records; // 전체 레코드 수
	int page; // 현재 페이지
	List<E> rows; // 조회 결과
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getRecords() {
		return records;
	}
	public void setRecords(int records) {
		this.records = records;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public List<E> getRows() {
		return rows;
	}
	public void setRows(List<E> rows) {
		this.rows = rows;
	}
	
	
	
}
