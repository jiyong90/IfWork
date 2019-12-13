package com.isu.ifw.common.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isu.ifw.common.Page;
import com.isu.ifw.common.entity.CommManagementInfomation;
import com.isu.ifw.common.repository.CommManagementInfomationDao;
import com.isu.ifw.common.repository.CommManagementInfomationRepository;
import com.isu.ifw.vo.ReturnParam;

@Service
public class TenantConfigManagerServiceImpl implements TenantConfigManagerService {

	
	Map<String,String> configValues = null;
	
	@Resource
	private CommManagementInfomationRepository managementRepository;
	
	@Resource
	private CommManagementInfomationDao managementDao;
	
	@Override
	public String getConfigValue(Long tenantId, String key, boolean reloadConfig, String defaultValue) {
		
		String returnValue = null;
		
		// 캐쉬된 내용을 써야 하는 상황이면, 미리 읽어두었다가 사용함.
		if(!reloadConfig){
			if(configValues != null && configValues.containsKey(key+tenantId)){
				// 캐쉬된 내용이 있으면 그대로 사용.
				return configValues.get(key+tenantId);
			}else{
				// 캐쉬된 내용이 없으면 먼저 읽고 캐쉬한 후 반환.
				List<CommManagementInfomation> miList = managementRepository.findByTenantId(tenantId);
				
				if (miList == null)
					return null;
				
				Iterator<CommManagementInfomation> itor = miList.iterator();
				
				while (itor.hasNext()) {
					
					CommManagementInfomation mi = itor.next();
					String infoKey = mi.getInfoKey();
					
					String value = mi.getInfoData();
					
					String longValue = mi.getClobData();
					
					if(mi.getInfoType() != null && "longtext".equalsIgnoreCase(mi.getInfoType())  && longValue != null){
						value = longValue;
					}
					
					if(configValues == null)
						configValues = new HashMap<String,String>();
					
					configValues.put(infoKey+tenantId, value);
					
				}
				
				returnValue = configValues.get(key+tenantId);		
			}
		}
		// 캐쉬 없이 바로 반환해야 하는 상황이면 바로 찾음
		else{
			returnValue = null; // 가독성을 위해 한 번 더 쓴다.
			CommManagementInfomation information = managementRepository.findByTenantIdAndInfoKey(tenantId, key);
			if(information != null ){
				String value = information.getInfoData();
				
				String longValue = information.getClobData();
				
				if(information.getInfoType() != null && "longtext".equalsIgnoreCase(information.getInfoType())  && longValue != null){
					returnValue = longValue;
				}
				else{
					returnValue = value;
				}
			}
		}

		if(returnValue == null)
			return defaultValue;
		else
			return returnValue;
		
	}

	@Override
	public CommManagementInfomation setConfigValue(Long tenantId, String key, String value, String desc) {
		CommManagementInfomation information = null;

		// 업데이트를 위해 조회함. 
		information = managementRepository.findByTenantIdAndInfoKey(tenantId, key);
		
		if(information == null){
			information = new CommManagementInfomation(); // information 초기화한다.
			information.setInfoKey(key);
			information.setTenantId(tenantId);
			information.setInfoType("string"); // 정보 타입 값은 기본으로 string 이라는 값을 가짐. 만일 값의 길이가 500 바이트를 넘으면 longtext로 바뀜
			information.setInfoName(desc);
		}
		
		if(value == null){
			// 만일 저장해야 하는 값이 널이면, 모든 값을 널로 세팅함
			information.setClobData(null);
			information.setInfoData(null);
		}
		else{
			// 값의 길이이 따라 ... 타입을 바꾸어 저장한다.
			int length = value.getBytes().length;
			if(length < 500){
				information.setInfoData(value);
				information.setClobData(null);
				information.setInfoType("string");
			}else{
				information.setClobData(value);
				information.setInfoData(null);
				information.setInfoType("longtext");
			}
		}
		
		information = managementRepository.save(information);

		return information;
	}

	@Override
	public void deleteConfigValue(Long tenantId, String key) {
		CommManagementInfomation information = null;
		// 삭제를 위해 조회함. 
		information = managementRepository.findByTenantIdAndInfoKey(tenantId, key);
		
		// 검색된 녀석 삭제
		if(information != null)
			managementRepository.delete(information);

	}

	@Override
	public List<Map> getManagementList(Long tenantId, String userKey) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		List<Map<String, Object>> infos = managementDao.findByTenantKey(tenantId);
		List result = new ArrayList();

		try {
			if (infos != null && infos.size() > 0) {
				for (Map<String, Object> managementList : infos) {
					Map resultMap = new HashMap();
					resultMap.put("managementInfomationId", managementList.get("managementInfomationId"));
					resultMap.put("infoKey", managementList.get("infoKey"));
					resultMap.put("infoName", managementList.get("infoName"));
					resultMap.put("infoType", managementList.get("infoType"));
					resultMap.put("infoData", managementList.get("infoData"));
					resultMap.put("clobData", managementList.get("clobData"));
					result.add(resultMap);
				}
				return result;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}
	
	@Override
	public List<Map> getManagementListByInfoKeyLikeClobData(String infoKey, String clobData, String userKey) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		
		List<Map<String, Object>> infos = managementDao.findByInfoKeyLikeClobData(infoKey, clobData);
		List result = new ArrayList();

		try {
			if (infos != null && infos.size() > 0) {
				for (Map managementList : infos) {
					Map resultMap = new HashMap();
					resultMap.put("tenantId", managementList.get("tenantId"));
					resultMap.put("clobData", managementList.get("clobData"));
					result.add(resultMap);
				}
				return result;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	@Override
	public List<Map> getManagementList(Long tenantId, String userKey, String searchKey, String searchData)
			throws Exception {

		List<Map<String, Object>> infos = managementDao.findByTenantKeyAndSearchKey(tenantId, searchKey,
				searchData);
		List result = new ArrayList();

		List<CommManagementInfomation> infoList = managementRepository.findByTenantIdAndInfoKeyAndInfoData(tenantId, searchKey, searchKey, searchData, searchData);

		try {
			if (infoList != null && infoList.size() > 0) {
				for (CommManagementInfomation info : infoList) {
					Map resultMap = new HashMap();
					ObjectMapper mapper = new ObjectMapper();
					boolean isCheck = false;
					resultMap.put("managementInfomationId", info.getManagementInfomationId());
					resultMap.put("infoKey", info.getInfoKey());
					resultMap.put("infoName", info.getInfoName());
					resultMap.put("infoType", info.getInfoType());
					resultMap.put("infoData", info.getInfoData());
					resultMap.put("clobData", info.getClobData());
					resultMap.put("tenantId", info.getTenantId());
					resultMap.put("isCheck", isCheck);

					result.add(resultMap);
				}
				return result;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	@Transactional
	@Override
	public ReturnParam deleteInfo(Map manageVo) throws Exception {

		Long manageId = Long.parseLong(manageVo.get("managementInfomationId").toString());
		managementDao.deleteInfo(manageId);

		ReturnParam rp = new ReturnParam();
		rp.setSuccess("success");

		return rp;
	}

	@Transactional
	@Override
	public ReturnParam saveInfo(Long tenantId, Map manageVo) throws Exception {
		ReturnParam rp = new ReturnParam();
		rp.setMessage("");

		try {
			if (manageVo != null) {
				CommManagementInfomation manageInfo = new CommManagementInfomation();
				String infoKey = manageVo.get("infoKey").toString();
				List<Map<String, Object>> managements = managementDao.findByTenantKeyAndInfoKey(tenantId,
						infoKey);
				for (Map<String, Object> management : managements) {
					if (manageVo.get("infoKey").toString().equals(management.get("infoKey"))) {
						rp.setMessage("duplicated");
						return rp;
					}
				}
				manageInfo.setClobData(manageVo.get("clobData").toString());
				manageInfo.setInfoData(manageVo.get("infoData").toString());
				manageInfo.setInfoKey(manageVo.get("infoKey").toString());
				manageInfo.setInfoName(manageVo.get("infoName").toString());
				manageInfo.setInfoType(manageVo.get("infoType").toString());
				manageInfo.setTenantId(tenantId);
				// commManagementInfomationDao.saveInfo(manageInfo);
				managementRepository.save(manageInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
			rp.setFail("fail");
			rp.setMessage("실패했습니다.");
			return rp;
		}
		return rp;
	}

	@Transactional
	@Override
	public ReturnParam updateInfo(Map manageVo, Long tenantId, String userKey) throws Exception {
		ReturnParam rp = new ReturnParam();

		try {
			if (!manageVo.get("infoKey").toString().equals("") && manageVo.get("infoKey").toString() != null) {
				CommManagementInfomation managementInfo = managementRepository.findByTenantIdAndInfoKey(tenantId, manageVo.get("infoKey").toString());
				managementInfo.setInfoKey(manageVo.get("infoKey").toString());
				managementInfo.setInfoData(manageVo.get("infoData").toString());
				managementInfo.setClobData(manageVo.get("lobData").toString());

				managementRepository.save(managementInfo);
				rp.setSuccess("success");
			} else {
				rp.setMessage("null");
				return rp;
			}
		} catch (Exception e) {
			e.printStackTrace();
			rp.setFail("fail");
			rp.setMessage("실패했습니다.");
			return rp;
		}

		return rp;
	}

	@Transactional
	@Override
	public ReturnParam updateInfo(Map manageVo, Long tenantId, String userKey, String columns) throws Exception {
		ReturnParam rp = new ReturnParam();

		try {
			if (!manageVo.get("infoKey").toString().equals("") && manageVo.get("infoKey").toString() != null) {
				CommManagementInfomation managementInfo = managementRepository.findByTenantIdAndInfoKey(tenantId, manageVo.get("infoKey").toString());
				if (columns.contains(manageVo.get("infoKey").toString())) {
					managementDao.backup(tenantId, manageVo.get("infoKey").toString(), userKey);
				}

				managementInfo.setInfoKey(manageVo.get("infoKey").toString());
				managementInfo.setInfoData(manageVo.get("infoData").toString());
				managementInfo.setClobData(manageVo.get("clobData").toString());

				managementRepository.save(managementInfo);
				rp.setSuccess("success");
			} else {
				rp.setMessage("null");
				return rp;
			}
		} catch (Exception e) {
			e.printStackTrace();
			rp.setFail("fail");
			rp.setMessage("실패했습니다.");
			return rp;
		}

		return rp;
	}

	@Override
	public Map getInformationsList(Long tenantId, String searchKey, String searchData, int startPg, int pgCount) {
		// TODO Auto-generated method stub
		Map result = new HashMap();

		ObjectMapper mapper = new ObjectMapper();

		try {
			// 데이터 조회
			Page<Map<String, Object>> page = managementDao.findByTenantKeyAndCondition(tenantId, searchKey, searchData, ((startPg + 1) / pgCount), pgCount);
			List<Map<String, Object>> results = page.getRows();
			result.put("recordsTotal", page.getTotal());
			result.put("recordsFiltered", page.getRecords());

			List infos = new ArrayList();

			if (results != null && results.size() > 0) {
				int index = 1; // startPg +1
				for (Map<String, Object> r : results) {
					Map manageInfo = new HashMap();

					manageInfo.put("rnum", index++);
					manageInfo.put("managementInfomationId", r.get("managementInfomationId"));
					manageInfo.put("infoKey", r.get("infoKey")!=null?r.get("infoKey"):"");
					manageInfo.put("infoName", r.get("infoName")!=null?r.get("infoName"):"");
					manageInfo.put("infoType", r.get("infoType")!=null?r.get("infoType"):"");
					manageInfo.put("infoData", r.get("infoData")!=null?r.get("infoData"):"");
					manageInfo.put("clobData", r.get("clobData")!=null?r.get("clobData"):"");

					infos.add(manageInfo);
				}

			}
			result.put("data", infos);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return result;
	}

	@Override
	public Map getInformationsList(Long tenantId, String searchKey, String searchData, int startPg, int pgCount,
			String userKey, String userToken, String sortColumn, String sortDir) {
		// TODO Auto-generated method stub

		Map result = new HashMap(); // 해당 객체에 담아서 보내줘야하는듯

		try {
			List<Map<?, ?>> infos = new ArrayList();
			List<Map<String, Object>> results = managementDao.findByTenantIdAndCondition(tenantId);
			List<Map<String, Object>> dtCols = null;// 페이징처리할때는 이형태로보내주는건가? List index 1개당 맵형태로 담는듯?

			if (results != null && results.size() > 0) {
				result.put("recordsTotal",
						new BigDecimal(results.size()).divide(new BigDecimal(pgCount), BigDecimal.ROUND_UP).intValue());// 페이징에
																														// 몇
																														// 레코드가
																														// 들어가는지?
				result.put("recordsFiltered", results.size());// 전체 레코드 수

				int index = 1; // startPg +1;
				int firstLoop = 0;

				for (Map<String, Object> r : results) {
					if (firstLoop == 0) {
						Map<String, Object> dtColMap = getInformationHeader(tenantId, userKey, userToken);
						if (dtColMap != null)
							dtCols = (List<Map<String, Object>>) dtColMap.get("dtCols");
						firstLoop++;
					}

					Map manageInfo = new HashMap();
					manageInfo.put("rnum", index++);
					manageInfo.put("managementInfomationId", r.get("mamagementInfomationId"));
					manageInfo.put("infoKey", r.get("infoKey"));
					manageInfo.put("infoName", r.get("infoName"));
					manageInfo.put("infoType", r.get("infoType"));
					manageInfo.put("infoData", r.get("infoData"));
					manageInfo.put("clobData", r.get("clobData"));

					infos.add(manageInfo);
				}
			}
			Map<String, Object> dtCol = dtCols.get(Integer.valueOf(sortColumn));
			// CollectionsSort(appls, dtCol.get("data").toString(), sortDir); 자바 정렬,,
			CollectionsSort(infos, dtCol.get("data").toString(), sortDir);
			int dataIdx = 0;
			int addIdx = 0;
			List res = new ArrayList();
			for (Map data : infos) {
				if (startPg <= dataIdx) {
					data.put("rnum", addIdx + 1);
					res.add(data);
					addIdx++;
				}
				if (pgCount == addIdx) {
					break;
				}
				dataIdx++;
			}
			result.put("data", res);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return result;
	}

	private ReturnParam getInformationHeader(Long tenantId, String userKey, String userToken) throws Exception {
		// TODO Auto-generated method stub
		ReturnParam resultMap = new ReturnParam();
		resultMap.setMessage("");
		List<Map<String, Object>> dtCols = new ArrayList<Map<String, Object>>();
		Map<String, Object> col = new HashMap<String, Object>();

		String stepType = "";

		col = new HashMap<>();
		col.put("title", "management_infomation_id");
		col.put("data", "managementInfomationId");
		col.put("name", "managementInfomationId");
		col.put("visible", false);
		dtCols.add(col);

		col = new HashMap<>();
		col.put("title", "info_key");
		col.put("data", "infoKey");
		col.put("name", "infoKey");
		col.put("className", "dt-center");
		col.put("orderable", true);
		dtCols.add(col);

		col = new HashMap<>();
		col.put("title", "info_name");
		col.put("data", "infoName");
		col.put("name", "infoName");
		col.put("className", "dt-center");
		col.put("orderable", true);
		dtCols.add(col);

		col = new HashMap<>();
		col.put("title", "info_type");
		col.put("data", "infoType");
		col.put("name", "infoType");
		col.put("className", "dt-center");
		col.put("orderable", true);
		dtCols.add(col);

		resultMap.put("dtCols", dtCols);

		return resultMap;
	}

	protected void CollectionsSort(List datas, String key, String sortDir) {
		Collections.sort(datas, new Comparator<Map>() {
			@Override
			public int compare(Map first, Map second) {
				try {
					// 소트 필드가 숫자 일경우 변환에 도전!
					try {
						int fKey = 0;
						int sKey = 0;
						fKey = Integer.valueOf(first.get(key).toString());
						sKey = Integer.valueOf(second.get(key).toString());

						if (sortDir.equals("asc")) {
							if (fKey > sKey) {
								return 1;
							} else if (fKey < sKey) {
								return -1;
							} else /* if (firstValue == secondValue) */ {
								return 0;
							}
						} else {
							if (fKey > sKey) {
								return -1;
							} else if (fKey < sKey) {
								return 1;
							} else /* if (firstValue == secondValue) */ {
								return 0;
							}
						}
					} catch (Exception e1) {

					}

					float fKey = 0;
					float sKey = 0;
					fKey = Float.valueOf(first.get(key).toString());
					sKey = Float.valueOf(second.get(key).toString());

					if (sortDir.equals("asc")) {
						if (fKey > sKey) {
							return 1;
						} else if (fKey < sKey) {
							return -1;
						} else /* if (firstValue == secondValue) */ {
							return 0;
						}
					} else {
						if (fKey > sKey) {
							return -1;
						} else if (fKey < sKey) {
							return 1;
						} else /* if (firstValue == secondValue) */ {
							return 0;
						}
					}

				} catch (Exception e) {
					String firstKey = "";
					if (first.get(key) != null && !"".equals(first.get(key).toString()))
						firstKey = first.get(key).toString();
					String secondKey = "";
					if (second.get(key) != null && !"".equals(second.get(key).toString()))
						secondKey = second.get(key).toString();
					if (sortDir.equals("asc")) {
						return firstKey.compareTo(secondKey);
					} else {
						return secondKey.compareTo(firstKey);
					}
				}
			}
		});
	}
	
	@Transactional
	@Override
	public ReturnParam updateUrlInfo(Map manageVo, Long tenantId, String userKey) throws Exception {
		ReturnParam rp = new ReturnParam();

		try {
			List<CommManagementInfomation> managementInfo = managementRepository.findByTenantIdAndInfoKeyLike(tenantId, "%HR.%");
			for(int i = 0; i < managementInfo.size(); i++) {
				CommManagementInfomation info = managementInfo.get(i);
				if(info.getInfoType().toString().equals("string")) {
					String data = info.getInfoData();
					if(!data.contains("ifm"))
						continue;
					System.out.println(data);
					String url = manageVo.get("url").toString() + data.substring(data.indexOf("/ifm"), data.length());
					info.setInfoData(url);
					managementRepository.save(info);
				}
				
			}
			rp.setSuccess("success");
		} catch (Exception e) {
			e.printStackTrace();
			rp.setFail("fail");
			rp.setMessage("실패했습니다.");
			return rp;
		}

		return rp;
	}

	@Override
	public int countByTenantId(Long tenantId) {
		return managementRepository.countByTenantId(tenantId);
	}

}
