package com.pool.model.exception;

import java.util.Date;

public class CustomErrorResponse {
	private String errorCode;
	private String errorMsg;
	private Integer status;
	private Date timestamp;

	public CustomErrorResponse() {

	}

	public String getErrorCode() {
		return errorCode;
	}

	public CustomErrorResponse setErrorCode(String errorCode) {
		this.errorCode = errorCode;
		return this;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public CustomErrorResponse setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
		return this;
	}

	public Integer getStatus() {
		return status;
	}

	public CustomErrorResponse setStatus(Integer status) {
		this.status = status;
		return this;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public CustomErrorResponse setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
		return this;
	}

}
