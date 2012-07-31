package org.teiid.tools.vdbmanager.client;

import java.io.Serializable;

public class DataItem extends Object implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String data;
	private String type;

	public DataItem() {
	}
	
	public DataItem(String data, String type) {
		this.data = data;
		this.type = type;
	}

	public String getData() {
		return this.data;
	}
	
	public void setData(String data) {
	    this.data = data;	
	}
	
	public String getType() {
		return this.type;
	}
	
	public void setType(String type) {
	    this.type = type;	
	}

}