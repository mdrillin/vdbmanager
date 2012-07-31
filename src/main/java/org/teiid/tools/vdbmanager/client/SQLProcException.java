package org.teiid.tools.vdbmanager.client;

import java.io.Serializable;

public class SQLProcException extends Exception implements Serializable {

	private static final long serialVersionUID = 1L;
	private String sqlDetail;

	public SQLProcException() {
	}

	public SQLProcException(String sqlDetail) {
		this.sqlDetail = sqlDetail;
	}

	public String getSqlDetail() {
		return this.sqlDetail;
	}
}