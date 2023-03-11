package com.pool.domin;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class Contract {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String contractId;
	private String holderName;
	private int duration;
	private double amount;
	private Date creationDate;
	private String status;
	public Contract() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getContractId() {
		return contractId;
	}
	public void setContractId(String contractId) {
		this.contractId = contractId;
	}
	public String getHolderName() {
		return holderName;
	}
	public void setHolderName(String holderName) {
		this.holderName = holderName;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "Contract [contractId=" + contractId + ", holderName=" + holderName + ", duration=" + duration
				+ ", amount=" + amount + ", creationDate=" + creationDate + ", status=" + status + "]";
	}
	
	
}
