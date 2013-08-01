package org.teiid.tools.vdbmanager.client;

import java.io.Serializable;

/*
 * Tracks info about the Property Definition for use on the client
 */
public class PropertyObj extends Object implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private String displayName;
	private boolean isRequired = false;
	private boolean isModifiable = false;
	private boolean isHidden = false;
	private String value;
	private String defaultValue;

	public PropertyObj() {
	}
		
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public boolean isRequired() {
		return isRequired;
	}

	public void setRequired(boolean isRequired) {
		this.isRequired = isRequired;
	}

	public boolean isModifiable() {
		return isModifiable;
	}

	public void setModifiable(boolean isModifiable) {
		this.isModifiable = isModifiable;
	}
	
	public String getDefault() {
		return this.defaultValue;
	}
	
	public void setDefault(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	public boolean isHidden() {
		return isHidden;
	}

	public void setHidden(boolean isHidden) {
		this.isHidden = isHidden;
	}
}