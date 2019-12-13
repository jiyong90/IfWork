package com.isu.ifw.common.repository;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.isu.ifw.common.Page;
import com.isu.ifw.common.PagingQueryWrapper;
import com.isu.ifw.common.entity.CommManagementInfomation;
 

@Repository("WtmManagementInfomationDao")
public class CommManagementInfomationDao {

      @PersistenceContext
      private EntityManager em;
      
      private JdbcTemplate jdbcTemplate;
      private NamedParameterJdbcTemplate namedJdbcTemplate;
      
      @Autowired
      @Qualifier(value="dataSource")
      public void setDataSource(DataSource datasource){
         this.jdbcTemplate = new JdbcTemplate(datasource);
         this.namedJdbcTemplate = new NamedParameterJdbcTemplate(datasource); 
      }
//      public CommManagementInfomation findByManagementInfoId(Long managementInfomationId) {
//         return em.find(CommManagementInfomation.class, managementInfomationId);
//      }
//      public List<CommManagementInfomation> getInfomationList(String infoKey,Long tenantId){
//
//         Query q = em.createNamedQuery("CommManagementInfomation",CommManagementInfomation.class);
//         q.setParameter("infoKey", infoKey);
//         q.setParameter("tenantId", tenantId);
//         return (List<CommManagementInfomation>)q.getResultList();
//      }
      public List<Map<String, Object>> findByTenantKey(Long tenantId) {
         Object[] parameter = new Object[] {tenantId};
         String sql = "SELECT "
               + "management_infomation_id AS managementInfomationId"
               + ", info_key AS infoKey"
               + ", info_name AS infoName"
               + ", info_type AS infoType"
               + ", info_data AS infoData"
               + ", clob_data AS clobData"
               + ", tenant_id AS tenantId"
               + " FROM comm_management_infomation r WHERE tenant_id = ? ";
         return jdbcTemplate.queryForList(sql,parameter);
         
      }
      public List<Map<String, Object>> findByTenantKeyAndInfoKey(Long tenantId,String infoKey) {
         Object[] parameter = new Object[] {tenantId,infoKey};
         String sql = "SELECT "
               + "management_infomation_id AS managementInfomationId"
               + ", info_key AS infoKey"
               + ", info_name AS infoName"
               + ", info_type AS infoType"
               + ", info_data AS infoData"
               + ", clob_data AS clobData"
               + ", tenant_id AS tenantId"
               + " FROM comm_management_infomation r WHERE tenant_id = ? "
               + "AND info_key = ?";
         return jdbcTemplate.queryForList(sql,parameter);
         
      }
      public List<Map<String, Object>> findByTenantKeyAndSearchKey(Long tenantId, String searchKey,
            String searchData) {
         Object[] parameter = new Object[] {tenantId};
         String sql="SELECT "
               + "management_infomation_id AS managementInfomationId"
               + ", info_key AS infoKey"
               + ", info_name AS infoName"
               + ", info_type AS infoType"
               + ", info_data AS infoData"
               + ", clob_data AS clobData"
               + ", tenant_id AS tenantId"
               + " FROM comm_management_infomation r WHERE tenant_id = ? ";
         if(!"".equals(searchKey) && "".equals(searchData)) {
            String[] searchKeys = searchKey.split(" ");
           
            for(int i=0;i<searchKeys.length;i++) {
               if(i==0) sql+="and (";
               else    sql+="or ";
               
               sql+="info_key like '%"+searchKeys[i]+"%' or info_name like '%"+searchKeys[i]+"%' ";
               
               if((i+1)==searchKeys.length) sql+= " )";
            }
         }else if(!"".equals(searchData) && "".equals(searchKey)) {
            String[] searchDatas = searchData.split(" ");
            
            for(int i=0;i<searchDatas.length;i++) {
               if(i==0) sql+="and (";
               else    sql+="or ";
               
               sql+="info_data like '%"+searchDatas[i]+"%' or clob_data like '%"+searchDatas[i]+"%' ";
               
               if((i+1)==searchDatas.length) sql+= " )";
            }
            
         }
         else if(!"".equals(searchKey) && !"".equals(searchData)) {
            String[] searchDatas = searchData.split(" ");
            
            for(int i=0;i<searchDatas.length;i++) {
               if(i==0) sql+="and (";
               else    sql+="or ";
               
               sql+="info_data like '%"+searchDatas[i]+"%' or clob_data like '%"+searchDatas[i]+"%' ";
               
               if((i+1)==searchDatas.length) sql+= " ) ";
            }
            
            String[] searchKeys = searchKey.split(" ");
           
            for(int i=0;i<searchKeys.length;i++) {
               if(i==0) sql+="and (";
               else    sql+="or ";
               
               sql+="info_key like '%"+searchKeys[i]+"%' or info_name like '%"+searchKeys[i]+"%' ";
               
               if((i+1)==searchKeys.length) sql+= " )";
            }
         }else {
            sql+=" order by management_infomation_id";
         }
         return jdbcTemplate.queryForList(sql,parameter);
      }
      
      public void deleteInfo(Long managementInfomationId) {
         String sql = "DELETE FROM WtmManagementInfomation r WHERE r.managementInfomationId = :managementInfomationId";
         Query q = em.createQuery(sql);
         q.setParameter("managementInfomationId", managementInfomationId);

         q.executeUpdate();
      }
      public void saveInfo(CommManagementInfomation management) {
         em.persist(management);
         em.flush();
      }
     @Transactional
      public void updateInfo(CommManagementInfomation management) {
         em.merge(management);
      }
	public Page<Map<String, Object>> findByTenantKeyAndCondition(Long tenantId, String searchKey, String searchData, int pageNumber, int pageLength) {
		// TODO Auto-generated method stub
		Object[] parameters = new Object[] {tenantId};
		PagingQueryWrapper<Map<String, Object>> qWrapper = new PagingQueryWrapper<Map<String, Object>>(jdbcTemplate);
		
		String sql="SELECT "
	               + "  r.management_infomation_id AS managementInfomationId "
	               + ",r.info_key AS infoKey "
	               + ",r.info_name AS infoName "
	               + ",r.info_type AS infoType "
	               + ",r.info_data AS infoData "
	               + ",r.clob_data AS clobData "
	               + ",r.tenant_id AS tenantId "
	               + " FROM comm_management_infomation r WHERE tenant_id = ? ";

		if(!"".equals(searchKey) && "".equals(searchData)) {
            String[] searchKeys = searchKey.split(" ");
           
            for(int i=0;i<searchKeys.length;i++) {
               if(i==0) sql+="and (";
               else    sql+="or ";
               
               sql+="r.info_key like '%"+searchKeys[i]+"%' or r.info_name like '%"+searchKeys[i]+"%' ";
               
               if((i+1)==searchKeys.length) sql+= " )";
            }
         }else if(!"".equals(searchData) && "".equals(searchKey)) {
            String[] searchDatas = searchData.split(" ");
            
            for(int i=0;i<searchDatas.length;i++) {
               if(i==0) sql+="and (";
               else    sql+="or ";
               
               sql+="r.info_data like '%"+searchDatas[i]+"%' or r.clob_data like '%"+searchDatas[i]+"%' ";
               
               if((i+1)==searchDatas.length) sql+= " )";
            }
            
         }
         else if(!"".equals(searchKey) && !"".equals(searchData)) {
            String[] searchDatas = searchData.split(" ");
            
            for(int i=0;i<searchDatas.length;i++) {
               if(i==0) sql+="and (";
               else    sql+="or ";
               
               sql+="r.info_data like '%"+searchDatas[i]+"%' or r.clob_data like '%"+searchDatas[i]+"%' ";
               
               if((i+1)==searchDatas.length) sql+= " ) ";
            }
            
            String[] searchKeys = searchKey.split(" ");
           
            for(int i=0;i<searchKeys.length;i++) {
               if(i==0) sql+="and (";
               else    sql+="or ";
               
               sql+="r.info_key like '%"+searchKeys[i]+"%' or r.info_name like '%"+searchKeys[i]+"%' ";
               
               if((i+1)==searchKeys.length) sql+= " )";
            }
         }else {
            sql+=" order by management_infomation_id";
         }
		
		Page<Map<String, Object>> page = qWrapper.queryForList(sql, parameters,pageLength, pageNumber);
		
		return page;
	}
	public List<Map<String, Object>> findByTenantIdAndCondition(Long tenantId) {
		// TODO Auto-generated method stub
		Object[] parameters = new Object[] {tenantId};
		
		String sql="SELECT "
	               + "management_infomation_id AS managementInfomationId"
	               + ", info_key AS infoKey"
	               + ", info_name AS infoName"
	               + ", info_type AS infoType"
	               + ", info_data AS infoData"
	               + ", clob_data AS clobData"
	               + ", tenant_id AS tenantId"
	               + " FROM comm_management_infomation r WHERE tenant_id = ? ";
	              // + " order by r.management_infomation_id ";
		
		return jdbcTemplate.queryForList(sql,parameters);
	}

    public void backup(long tenantId, String infoKey, String id) {
        Object[] parameter = new Object[] {tenantId, infoKey};
        String sql = "INSERT INTO comm_management_infomation_history "
              + " (tenant_id, info_key, info_name, info_type, info_data, clob_data, created_id) "
              	+ "  SELECT "
				+ "		  A.tenant_id as tenantId "
				+ " 	, A.info_key as infoKey "
				+ "		, A.info_name as infoName "
				+ "		, A.info_type as infoType "
				+ "		, A.info_data as infoData " 
				+ " 	, A.clob_data as clobData "
				+ " 	, '" + id +"' as createdId "
				+ "FROM comm_management_infomation A "
				+ "WHERE A.tenant_id = ? and A.info_key = ?";
        jdbcTemplate.update(sql,parameter);
     }
    
    //전체 tenant 대상으로 특정 info key 데이터만 검색(tenant-manager에서 사용
    public List<Map<String, Object>> findByInfoKey(String infoKey) {
        Object[] parameter = new Object[] {infoKey};
        String sql = "SELECT "
              + "  clob_data AS clobData"
              + ", tenant_id AS tenantId"
              + " FROM comm_management_infomation r WHERE info_key = ? ";
        return jdbcTemplate.queryForList(sql,parameter);
     }
    
    //전체 tenant 대상으로 특정 info key 데이터만 검색(tenant-manager에서 사용
    public List<Map<String, Object>> findByInfoKeyLikeClobData(String infoKey, String clobData) {
        Object[] parameter = new Object[] {infoKey};
        String sql = "SELECT "
              + "  clob_data AS clobData"
              + ", tenant_id AS tenantId"
              + " FROM comm_management_infomation r WHERE info_key = ? and clob_data like '%"+clobData+"%'";
        return jdbcTemplate.queryForList(sql,parameter);
        
     }
}