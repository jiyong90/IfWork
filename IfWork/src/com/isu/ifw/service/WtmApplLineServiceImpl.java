package com.isu.ifw.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isu.ifw.entity.WtmApplCode;
import com.isu.ifw.entity.WtmApplLine;
import com.isu.ifw.mapper.WtmApplMapper;
import com.isu.ifw.repository.WtmApplCodeRepository;
import com.isu.ifw.repository.WtmApplLineRepository;
import com.isu.ifw.util.WtmUtil;
import com.isu.ifw.vo.WtmApplLineVO;


@Service("applLineService")
public class WtmApplLineServiceImpl implements WtmApplLineService{
	
	private final Logger logger = LoggerFactory.getLogger("ifwDBLog");
	
	@Autowired
	WtmApplMapper applMapper;
	
	@Autowired
	WtmApplLineRepository applLineRepo;
	
	@Autowired
	WtmApplCodeRepository applCodeRepo;
	
	@Override
	public List<WtmApplLineVO> getApplLine(Long tenantId, String enterCd, String sabun, String applCd, String userId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		paramMap.put("tenantId", tenantId);
		paramMap.put("applCd", applCd);
		paramMap.put("d", WtmUtil.parseDateStr(new Date(), "yyyyMMdd"));
		
		List<WtmApplLineVO> result = new ArrayList<WtmApplLineVO>();


		List<WtmApplLineVO> applLines = null;
		// 한성 모터스를 위한 결재선..
		// hr결재선과 같게 해달라고 하여 HR결재선을 조회하도록 한다.
		if(tenantId == 92) {
			applLines = applMapper.getWtmApplLineHS(paramMap);
		} else {
			applLines = applMapper.getWtmApplLine(paramMap);
		}

		WtmApplCode applCode = applCodeRepo.findByTenantIdAndEnterCdAndApplCd(tenantId, enterCd, applCd);
		if(applCode!=null && applCode.getApplLevelCd()!=null && !"".equals(applCode.getApplLevelCd())) {
			int applLevel = Integer.parseInt(applCode.getApplLevelCd());
			
			int lineCnt = 0; 
			if(applLines!=null && applLines.size()!=0) {
				for(WtmApplLineVO applLine : applLines) {
					if(tenantId == 92) {
						if(!WtmApplService.APPL_LINE_S.equals(applLine.getApprTypeCd()) || (WtmApplService.APPL_LINE_S.equals(applLine.getApprTypeCd()))) {
							result.add(applLine);
						}

					} else {
						if(!WtmApplService.APPL_LINE_S.equals(applLine.getApprTypeCd()) || (WtmApplService.APPL_LINE_S.equals(applLine.getApprTypeCd()) && lineCnt < applLevel)) {
							result.add(applLine);
						}
					}
					if(WtmApplService.APPL_LINE_S.equals(applLine.getApprTypeCd()))
						lineCnt++;
				}
				
				//발신처 & 수신처가 없다면 본인 결재
				if(applLines!=null && applLines.size()==1) {
					//신청자
					WtmApplLineVO applicant = applLines.get(0);
					String applSabun = applicant.getSabun();
					String applSabunNm = applicant.getEmpNm();
					
					WtmApplLineVO sender = new WtmApplLineVO();
					sender.setApprSeq(applLines.size() + 1);
					sender.setApprTypeCd(WtmApplService.APPL_LINE_S);
					sender.setSabun(applSabun);
					sender.setEmpNm(applSabunNm);
					result.add(sender);
				}
				
			}
			
		}
		
		return result;
	}

	@Transactional
	@Override
	public void saveWtmApplLine(Long tenantId, String enterCd, int apprLvl, Long applId, String applCd, String sabun, String userId) {
		
		//결재라인 저장
		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<WtmApplLine> applLines = applLineRepo.findByApplIdOrderByApprSeqAsc(applId);
		paramMap.put("enterCd", enterCd);
		paramMap.put("sabun", sabun);
		paramMap.put("tenantId", tenantId);
		paramMap.put("d", WtmUtil.parseDateStr(new Date(), null));
		paramMap.put("applCd", applCd);
		//결재라인 조회 기본으로 3단계까지 가져와서 뽑아  쓰자
		//List<WtmApplLineVO> applLineVOs = applMapper.getWtmApplLine(paramMap);
		List<WtmApplLineVO> applLineVOs = getApplLine(tenantId, enterCd, sabun, applCd, userId);
		
		//기본 결재라인이 없으면 저장도 안됨.
		if(applLineVOs != null && applLineVOs.size() > 0){
			
			if(applLines != null && applLines.size() > 0) {
				
				int whileLoop = 0;
				for(WtmApplLine applLine : applLines) {
					
					if(whileLoop < applLineVOs.size()) {
						WtmApplLineVO applLineVO = applLineVOs.get(whileLoop);
						
						if(!applLine.getApprSabun().equals(applLineVO.getSabun())) {
							applLine.setApprSeq(applLineVO.getApprSeq());
							applLine.setApprSabun(applLineVO.getSabun());
							applLine.setApprTypeCd(applLineVO.getApprTypeCd());
							applLine.setUpdateDate(new Date());
							applLine.setUpdateId(userId);
							applLineRepo.save(applLine);
						}
					} else {
						//기존 결재라인이 더 많으면 지운다. 임시저장이니.. 바뀔수도 있을 것 같아서..
						applLineRepo.delete(applLine);
					}
					
					whileLoop++;
				}
				
				//기 저장된 결재라인보다 조회한 결재라인이 더 길면 추가
				if(applLines.size() < applLineVOs.size()) {
					int cnt = applLineVOs.size()-applLines.size();
					
					for(int i=0; i<cnt; i++) {
						WtmApplLineVO applLineVO = applLineVOs.get(applLineVOs.size()-i-1);
						
						WtmApplLine applLine = new WtmApplLine();
						applLine.setApplId(applId);
						applLine.setApprSeq(applLineVO.getApprSeq());
						applLine.setApprSabun(applLineVO.getSabun());
						applLine.setApprTypeCd(applLineVO.getApprTypeCd());
						applLine.setUpdateDate(new Date());
						applLine.setUpdateId(userId);
						
						applLineRepo.save(applLine);
						
					}
				}
				
			} else {
				int i = 0;
				//신규생성
				for(WtmApplLineVO applLineVO : applLineVOs) {
					WtmApplLine applLine = new WtmApplLine();
					applLine.setApplId(applId);
					applLine.setApprSeq(applLineVO.getApprSeq());
					applLine.setApprSabun(applLineVO.getSabun());
					applLine.setApprTypeCd(applLineVO.getApprTypeCd());
					applLine.setUpdateId(userId);
					
					if(i==0)
						applLine.setApprDate(new Date());
					
					applLineRepo.save(applLine);
					
					i++;
				}
			}

			/*
			//결재라인 코드는 1,2,3으로 되어있다 이렇게 써야한다!!!! 1:1단계, 2:2단계, 3:3단계
			int applCnt = apprLvl; 
			
			//기 저장된 결재라인과 비교
			if(applLines != null && applLines.size() > 0) {
				int i=1; // apprSeq
				int whileLoop = 0;
				int lineCnt = 0;
				for(WtmApplLine applLine : applLines) {
					WtmApplLineVO applLineVO = applLineVOs.get(whileLoop);
					
					if(whileLoop < applLineVOs.size()) {
						
						if(!WtmApplService.APPL_LINE_S.equals(applLineVO.getApprTypeCd()) || (WtmApplService.APPL_LINE_S.equals(applLineVO.getApprTypeCd()) && lineCnt < applCnt)) {
							applLine.setApplId(applId);
							applLine.setApprSeq(i);
							applLine.setApprSabun(applLineVO.getSabun());
							applLine.setApprTypeCd(applLineVO.getApprTypeCd());
							applLine.setUpdateId(userId);
							applLineRepo.save(applLine);
							i++;
						}
						if(WtmApplService.APPL_LINE_S.equals(applLineVO.getApprTypeCd()))
							lineCnt++;
						
					}else {
						//기존 결재라인이 더 많으면 지운다. 임시저장이니.. 바뀔수도 있을 것 같아서..
						applLineRepo.delete(applLine);
					}
					whileLoop++;
				} 
				
			}else {
				//신규생성
				int i=1; // apprSeq
				int lineCnt = 0; 
				
				for(WtmApplLineVO applLineVO : applLineVOs) {
					

					//발신결재 결재레벨 체크
					if(!WtmApplService.APPL_LINE_S.equals(applLineVO.getApprTypeCd()) || (WtmApplService.APPL_LINE_S.equals(applLineVO.getApprTypeCd()) && lineCnt < applCnt)) {
						
						WtmApplLine applLine = new WtmApplLine();
						applLine.setApplId(applId);
						applLine.setApprSeq(i);
						applLine.setApprSabun(applLineVO.getSabun());
						applLine.setApprTypeCd(applLineVO.getApprTypeCd());
						applLine.setUpdateId(userId);
						applLineRepo.save(applLine);
						i++;
					}
					
					if(WtmApplService.APPL_LINE_S.equals(applLineVO.getApprTypeCd()))
						lineCnt++;
				}
			}
			
			//발신처 & 수신처가 없다면 본인 결재
			if(applLineVOs!=null && applLineVOs.size()==1) {
				WtmApplLine sender = new WtmApplLine();
				sender.setApplId(applId);
				sender.setApprSeq(applLineVOs.size()+1);
				sender.setApprSabun(sabun);
				sender.setApprTypeCd(WtmApplService.APPL_LINE_S);
				sender.setUpdateId(userId);
				applLineRepo.save(sender);
			}
			*/
			
		}
		//결재라인 저장 끝
	}
	
}