package com.isu.ifw.util;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.isu.ifw.vo.StringUtil;

public class WtmUtil {
	
	public static String parseDateStr(Date d, String format)  { 
		if(format == null || format.equals(""))
			format = "yyyyMMdd";
		DateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(d);
	}
	
	public static Date toDate(String dateStr, String format) {
		if(format == null || format.equals("")) {
			format = "yyyyMMdd";
		}
		SimpleDateFormat formatter = new SimpleDateFormat(format);
	    try {
			return formatter.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		} 
	}
	
	public static Date addDate(Date d, int addDay) {
		Calendar cal = Calendar.getInstance();
	    cal.setTime(d);
	    cal.add(Calendar.DATE, addDay);
	    return cal.getTime();
	}

 
	public static long dayCnt(String begin, String end) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
	    Date beginDate = formatter.parse(begin);
	    Date endDate = formatter.parse(end);
	 
	    long diff = endDate.getTime() - beginDate.getTime();
	    long diffDays = diff / (24 * 60 * 60 * 1000) + 1;


	    return diffDays;
	}

	//ibsheet parameter convert
	public static synchronized  HashMap<String, Object> requestInParamsMultiDML( HttpServletRequest request, String paramNames, String paramValues ) {
		String[] exCols 					= null;
		HashMap<String, Object> returnMap 	= new HashMap<String, Object>();
		HashMap<String, String> map 		= null;
		// COLS LIST
		List<Serializable>  mergeRows		= new ArrayList<Serializable>();
		List<Serializable>  insertRows		= new ArrayList<Serializable>();
		List<Serializable>  updateRows 		= new ArrayList<Serializable>();
		List<Serializable>  deleteRows 		= new ArrayList<Serializable>();
		List<Serializable>  subTempList		= new ArrayList<Serializable>();
		int rowSize 				= 0;
		int colSize 				= 0;
		String paramName 			= "";

		if(paramNames != null){
			String[] colNames  = paramNames.split(",");
			String[] cols      = null;
			if(paramValues != null && !paramValues.equals("")){
				cols = paramValues.split(",");
			}else{
				cols = paramNames.split(",");
			}
			Map<?, ?> paramMap 		= request.getParameterMap();
			if(paramMap.get(cols[0]) != null){
				rowSize = ((String[]) paramMap.get( cols[0]) ).length;
				colSize = cols.length;
				// COLS �씤寃�
				if ( null != cols || cols.length > 0 )  {
					// ROW �삎�떇
					for ( int i = 0; i < rowSize; i++ )  {
						map = new HashMap<String, String>();
						for ( int j = 0; j < colSize; j++ )  {
							if(paramMap.get(cols[j]) == null) {
								System.out.println(cols[j]+" ================ null value");
							}
							map.put( colNames[j] , StringUtil.replaceSingleQuot( ((String[]) paramMap.get(cols[j]) )[i] ) );
						}
						if( 	map.get("sStatus").equals("I") ){ insertRows.add(map); mergeRows.add(map);}
						else if(map.get("sStatus").equals("U") ){ updateRows.add(map); mergeRows.add(map);}
						else if(map.get("sStatus").equals("D") ){ deleteRows.add(map); }
					}
					returnMap.put( "mergeRows" , mergeRows );
					returnMap.put( "insertRows" , insertRows );
					returnMap.put( "updateRows" , updateRows );
					returnMap.put( "deleteRows" , deleteRows );
				}
				Enumeration<?> enumeration = request.getParameterNames();

				// COLS �씠�쇅�쓽 寃껊뱾
				for ( int i = 0; i < paramMap.size(); i++ )
				{
					paramName = ( String ) enumeration.nextElement();
					if ( existCols( cols, paramName ) ) { continue; }
					exCols = (String[]) paramMap.get( paramName );
					if ( exCols.length > 1)
					{
						for ( int j = 0; j < exCols.length; j++ )
						{
							subTempList.add( exCols[j] );
						}
						returnMap.put( paramName, subTempList );
					} else {
						returnMap.put( paramName, exCols[0] );
					}
				}
			}
		}
		return returnMap;
	}

	public static synchronized boolean existCols(String[] cols, String paramName) {
		for (int i = 0; i < cols.length; i++) {
			if (paramName.equals(cols[i])) { return true; }
		}
		return false;
	}
	
	public static String paramToString(HttpServletRequest request) {
		String data = "";
		Enumeration params = request.getParameterNames();
		while (params.hasMoreElements()){
		    String name = (String)params.nextElement();
		    data += name+":"+request.getParameter(name)+",";
		}
		return data;
	}
}
