package com.isu.ifw.common.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * The persistent class for the comm_module database table.
 * 
 */
@Entity
@Table(name="comm_module")
@NamedQuery(name="CommModule.findAll", query="SELECT c FROM CommModule c")
public class CommModule implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="module_id")
	private Long moduleId;

	@Column(name="module_name")
	private String moduleName;

	@Lob
	@Column(name="module_value")
	private String moduleValue;

	private String note;

	//bi-directional many-to-one association to CommTenantModule
	@OneToMany(mappedBy="commModule" , cascade=CascadeType.ALL)
	private List<CommTenantModule> commTenantModules;

	public CommModule() {
	}

	public Long getModuleId() {
		return this.moduleId;
	}

	public void setModuleId(Long moduleId) {
		this.moduleId = moduleId;
	}

	public String getModuleName() {
		return this.moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getModuleValue() {
		return this.moduleValue;
	}

	public void setModuleValue(String moduleValue) {
		this.moduleValue = moduleValue;
	}

	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public List<CommTenantModule> getCommTenantModules() {
		return this.commTenantModules;
	}

	public void setCommTenantModules(List<CommTenantModule> commTenantModules) {
		this.commTenantModules = commTenantModules;
	}

	public CommTenantModule addCommTenantModule(CommTenantModule commTenantModule) {
		getCommTenantModules().add(commTenantModule);
		commTenantModule.setCommModule(this);

		return commTenantModule;
	}

	public CommTenantModule removeCommTenantModule(CommTenantModule commTenantModule) {
		getCommTenantModules().remove(commTenantModule);
		commTenantModule.setCommModule(null);

		return commTenantModule;
	}

}