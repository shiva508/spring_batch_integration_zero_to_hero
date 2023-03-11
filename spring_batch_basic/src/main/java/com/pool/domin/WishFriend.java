package com.pool.domin;

import java.io.Serializable;
import jakarta.persistence.*;

@Entity
@Table
public class WishFriend implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long wishFriendId;
	private Long weWishId;
	private String wisherFriendName;
	private String wisherFriendMessaage;
	@Column(columnDefinition = "text")
	@Lob
	private String wisherFriendMemory;
	@Lob
	private String wisherAvtor;

	public WishFriend() {

	}

	public Long getWishFriendId() {
		return wishFriendId;
	}

	public void setWishFriendId(Long wishFriendId) {
		this.wishFriendId = wishFriendId;
	}

	public Long getWeWishId() {
		return weWishId;
	}

	public void setWeWishId(Long weWishId) {
		this.weWishId = weWishId;
	}

	public String getWisherFriendName() {
		return wisherFriendName;
	}

	public void setWisherFriendName(String wisherFriendName) {
		this.wisherFriendName = wisherFriendName;
	}

	public String getWisherFriendMessaage() {
		return wisherFriendMessaage;
	}

	public void setWisherFriendMessaage(String wisherFriendMessaage) {
		this.wisherFriendMessaage = wisherFriendMessaage;
	}

	public String getWisherFriendMemory() {
		return wisherFriendMemory;
	}

	public void setWisherFriendMemory(String wisherFriendMemory) {
		this.wisherFriendMemory = wisherFriendMemory;
	}

	public String getWisherAvtor() {
		return wisherAvtor;
	}

	public void setWisherAvtor(String wisherAvtor) {
		this.wisherAvtor = wisherAvtor;
	}

	

}
