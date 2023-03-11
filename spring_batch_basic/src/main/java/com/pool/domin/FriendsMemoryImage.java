package com.pool.domin;

import java.io.Serializable;
import jakarta.persistence.*;

@Entity
@Table
public class FriendsMemoryImage implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long memoryImageId;
	private String memoryImageName;
	private String memoryImageBase64;

	public FriendsMemoryImage() {

	}

	public Long getMemoryImageId() {
		return memoryImageId;
	}

	public void setMemoryImageId(Long memoryImageId) {
		this.memoryImageId = memoryImageId;
	}

	public String getMemoryImageName() {
		return memoryImageName;
	}

	public void setMemoryImageName(String memoryImageName) {
		this.memoryImageName = memoryImageName;
	}

	public String getMemoryImageBase64() {
		return memoryImageBase64;
	}

	public void setMemoryImageBase64(String memoryImageBase64) {
		this.memoryImageBase64 = memoryImageBase64;
	}

	@Override
	public String toString() {
		return "FriendsMemoryImage [memoryImageId=" + memoryImageId + ", memoryImageName=" + memoryImageName
				+ ", memoryImageBase64=" + memoryImageBase64 + "]";
	}

}
