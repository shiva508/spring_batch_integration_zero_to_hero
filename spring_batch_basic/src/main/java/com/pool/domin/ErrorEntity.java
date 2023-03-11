package com.pool.domin;

import java.io.Serializable;


import jakarta.persistence.*;

@Entity
@Table(name = "ERROR_DATA")
public class ErrorEntity implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long errorId;
	private String jobName;
	private String actionType;
	private String input;
	private String message;
	private Integer lineNumber;

	public ErrorEntity() {

	}

	public Long getErrorId() {
		return errorId;
	}

	public void setErrorId(Long errorId) {
		this.errorId = errorId;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(Integer lineNumber) {
		this.lineNumber = lineNumber;
	}

	@Override
	public String toString() {
		return "ErrorEntity [errorId=" + errorId + ", jobName=" + jobName + ", actionType=" + actionType + ", input="
				+ input + ", message=" + message + ", lineNumber=" + lineNumber + "]";
	}

}
