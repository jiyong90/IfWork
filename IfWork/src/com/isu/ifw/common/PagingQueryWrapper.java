package com.isu.ifw.common;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

/** 
 * 일반 jdbc template 을 이용한 쿼리를
 * 페이징 가능한 형태로 바꾸어 실행하도록 한다.
 * <br>
 * Mysql 용임.
 * @author admin
 *
 */
public class PagingQueryWrapper<E> {

	JdbcTemplate jdbcTemplate;
	
	/**
	 * 생성자, jdbcTemplage을 이용하여 wrapper class를 초기화한다.
	 * @param jdbcTemplate
	 */
	public PagingQueryWrapper(JdbcTemplate jdbcTemplate){
		this.jdbcTemplate = jdbcTemplate;
	}
	
	/**
	 * 주어진 쿼리를 수행하여 페이지 정보가 담긴 Page 객체를 반환한다.
	 * @param queryText jdbctemplate 사용시 쓰이는 원래의 쿼리 문자열
	 * @param params jdbctemplate 사용시 쓰이는 원래의 파라미터 
	 * @param numberOfRowsInAPage 한 페이지에 보일 레코드의 수
	 * @param startPage 조회 할 페이지 번호 (1 부터 시작함)
	 * @return
	 */
	public Page<E> queryForList(String queryText, Object[] params, int numberOfRowsInAPage, int startPage, Class typeClass){
		
		
		int records = 0;

		//startPage = startPage - 1;// 실제 쿼리는 페이지 1에 대해 0 부터 시작해야 하므로 하나 빼준다. (limit 구문의 시작은 0이기 때문)
		
		int startConst = 1; // 시작상수, startPage 가 0보타 큰 경우는 1의 값 (그 다음 값)을 가지고, 그렇지 않은 경우 0의 값을 가진다.
		
		if(startPage < 0) // 0을 파라미터로 받은 상황이면 0부터 .. 보정한다.
			startPage = 0;
		
		if(startPage == 0)
			startConst = 0;
		
		// 전체 레코드 갯수를 구하기 위한 쿼리 문장
		String countQuery= null;
		// 페이징 결과 목록을 구하기 위한 쿼리 문장
		String pagingQuery = null;
		
		

		countQuery = "SELECT count(1) as cnt FROM ("+queryText+" ) _for_counting_";
		//pagingQuery = "SELECT * FROM ("+queryText+" ) _for_paging_ limit "+(startPage* numberOfRowsInAPage+startConst)+", "+numberOfRowsInAPage;
		pagingQuery = "SELECT * FROM ("+queryText+" ) _for_paging_ limit "+(startPage* numberOfRowsInAPage)+", "+numberOfRowsInAPage;
		
		Integer count = jdbcTemplate.queryForObject(countQuery,params, Integer.class);
		List<E> queryResult = null;
		
//		System.out.println(pagingQuery);
		if(typeClass == null)
			queryResult = (List<E>) jdbcTemplate.queryForList(pagingQuery, params);
		else
			queryResult = (List<E>) jdbcTemplate.queryForList(pagingQuery, params,typeClass);
		
		
		
		
		Page p = new Page();
		p.setPage(startPage);
		p.setRecords(count);
		
		int totalPages=0;
		
		if(count == null){
			totalPages = 0;
			records =0;
		}else{
			records = count;
			totalPages = new BigDecimal(records).divide(new BigDecimal(numberOfRowsInAPage),BigDecimal.ROUND_UP).intValue();
		}
		p.setTotal(totalPages);
		
		p.setRows(queryResult);
				
		return p;
	}
	
	
	/**
	 * 주어진 쿼리를 수행하여 페이지 정보가 담긴 Page 객체를 반환한다.
	 * @param queryText jdbctemplate 사용시 쓰이는 원래의 쿼리 문자열
	 * @param numberOfRowsInAPage 한 페이지에 보일 레코드의 수
	 * @param startPage 조회 할 페이지 번호 (1 부터 시작함)
	 * @return
	 */
	public Page<E> queryForList(String queryText,  int numberOfRowsInAPage, int startPage ){
		
		return queryForList(queryText, null, numberOfRowsInAPage, startPage, null);
		/*
		int records = 0;

		startPage = startPage - 1;// 실제 쿼리는 페이지 1에 대해 0 부터 시작해야 하므로 하나 빼준다. (limit 구문의 시작은 0이기 때문)
		
		if(startPage < 0) // 0을 파라미터로 받은 상황이면 0부터 .. 보정한다.
			startPage = 0;
		
		// 전체 레코드 갯수를 구하기 위한 쿼리 문장
		String countQuery= null;
		// 페이징 결과 목록을 구하기 위한 쿼리 문장
		String pagingQuery = null;

		countQuery = "SELECT count(1) as cnt FROM ("+queryText+" ) _for_counting_";
		pagingQuery = "SELECT * FROM ("+queryText+" ) _for_paging_ limit "+startPage+", "+numberOfRowsInAPage;
		
		Integer count = jdbcTemplate.queryForObject(countQuery, Integer.class);
		List<E> queryResult = (List<E>) jdbcTemplate.queryForList(pagingQuery);
		
		Page p = new Page();
		p.setPage(startPage);
		p.setRecords(count);
		
		int totalPages=0;
		
		if(count == null){
			totalPages = 0;
			records =0;
		}else{
			records = count;
			totalPages = new BigDecimal(records).divide(new BigDecimal(numberOfRowsInAPage),BigDecimal.ROUND_UP).intValue();
		}
		p.setTotal(totalPages);
		
		p.setRows(queryResult);
				
		return p;
		*/
	}
	
	public Page<E> queryForList(String queryText, Object[] params, int numberOfRowsInAPage, int startPage){
		return queryForList(queryText, params,  numberOfRowsInAPage, startPage, null);
	}
	
	
	public static void main(String[] args){
		
		BigDecimal a = new BigDecimal(3);
		BigDecimal b = new BigDecimal(5);
		BigDecimal c = a.divide(b,BigDecimal.ROUND_UP);
		
		System.out.println(c.round(MathContext.DECIMAL32));
		
		
	}
	
}
