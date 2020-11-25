package com.isu.ifw.service;

import com.isu.ifw.entity.WtmAnnualUsed;
import com.isu.ifw.mapper.WtmAnnualCreateMapper;
import com.isu.ifw.repository.WtmAnnualUsedRepository;
import com.isu.ifw.vo.WtmAnnualCreateVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 연차사용 Service
 */
@Service
public class WtmAnnualUsedServiceImpl implements WtmAnnualUsedService{

	@Resource
	WtmAnnualUsedRepository usedRepository;

	@Autowired
	WtmAnnualCreateMapper annualCreateMapper;

	/**
	 * 연차 사용 저장
	 * @param tenantId
	 * @param enterCd
	 * @param userId
	 * @param sabun
	 * @param yy
	 * @param taaTypeCd
	 * @param symd
	 * @param eymd
	 * @param createCnt
	 * @param note
	 */
	public void save(Long tenantId, String enterCd, String userId, String sabun, String yy, String taaTypeCd, String symd, String eymd, Double createCnt, String note) throws Exception{

		WtmAnnualUsed annualUsed = new WtmAnnualUsed();
		annualUsed.setTenantId(tenantId);
		annualUsed.setEnterCd(enterCd);
		annualUsed.setSabun(sabun);
		annualUsed.setYy(yy);
		annualUsed.setSymd(symd.replaceAll("-", ""));
		annualUsed.setEymd(eymd.replaceAll("-", ""));
		annualUsed.setCreateCnt(createCnt);
		annualUsed.setUpdateId(userId);
		annualUsed.setTaaTypeCd(taaTypeCd);
		annualUsed.setNote(note);

		usedRepository.save(annualUsed);
	}

	/**
	 *
	 * @param taaTypeCd
	 * @param tenantId
	 * @param cd
	 * @param enterCd
	 * @param userId
	 * @param sabun
	 * @param yy
	 * @param symd
	 * @param eymd
	 * @return
	 */
	public WtmAnnualCreateVo getMyAnnualInfo(Long tenantId, String enterCd, String userId, String sabun, String yy, String symd, String eymd, String annualTaCd) throws Exception {

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("tenantId",tenantId);
		paramMap.put("enterCd",enterCd);
		paramMap.put("sabun", sabun);
		paramMap.put("yy",yy);
		paramMap.put("annualTaCd",annualTaCd);

		WtmAnnualCreateVo createVo = annualCreateMapper.getMyAnnualInfo(paramMap);
		return createVo;
	}

	/**
	 * 연차 발생일수
	 * @param tenantId
	 * @param enterCd
	 * @param sabun
	 * @param yy
	 * @param annualTaCd
	 * @return
	 */
	public WtmAnnualCreateVo getMyCreatCnt(Long tenantId, String enterCd, String sabun, String yy, String annualTaCd) throws Exception {
		WtmAnnualCreateVo annualCreateVo = new WtmAnnualCreateVo();
		annualCreateVo.setTenantId(tenantId);
		annualCreateVo.setEnterCd(enterCd);
		annualCreateVo.setSabun(sabun);
		annualCreateVo.setYy(yy);
		annualCreateVo.setTaaTypeCd(annualTaCd);

		WtmAnnualCreateVo createVo = annualCreateMapper.getMyCreatCnt(annualCreateVo);
		return createVo;
	}
}
