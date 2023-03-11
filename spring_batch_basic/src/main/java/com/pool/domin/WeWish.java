package com.pool.domin;

import java.io.Serializable;
import java.util.Date;
import jakarta.persistence.*;

@Entity
@Table
public class WeWish implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long weWishId;
	private String nameOfWishe;
	private String nameOfWisher;
	private String heroMessage;
	private String subHeroMessage;
	private Date wishCreatedDate;
	private Date wishScheduledDate;
	private String emailOfWisher;
	private String phoneNumber;
	@Lob
	private String heroImage;
	@Lob
	private String subHeroImage;

	public WeWish() {

	}

	public Long getWeWishId() {
		return weWishId;
	}

	public WeWish setWeWishId(Long weWishId) {
		this.weWishId = weWishId;
		return this;
	}

	public String getNameOfWishe() {
		return nameOfWishe;
	}

	public WeWish setNameOfWishe(String nameOfWishe) {
		this.nameOfWishe = nameOfWishe;
		return this;
	}

	public String getNameOfWisher() {
		return nameOfWisher;
	}

	public WeWish setNameOfWisher(String nameOfWisher) {
		this.nameOfWisher = nameOfWisher;
		return this;
	}

	public String getHeroMessage() {
		return heroMessage;
	}

	public WeWish setHeroMessage(String heroMessage) {
		this.heroMessage = heroMessage;
		return this;
	}

	public String getSubHeroMessage() {
		return subHeroMessage;
	}

	public WeWish setSubHeroMessage(String subHeroMessage) {
		this.subHeroMessage = subHeroMessage;
		return this;
	}

	public Date getWishCreatedDate() {
		return wishCreatedDate;
	}

	public WeWish setWishCreatedDate(Date wishCreatedDate) {
		this.wishCreatedDate = wishCreatedDate;
		return this;
	}

	public Date getWishScheduledDate() {
		return wishScheduledDate;
	}

	public WeWish setWishScheduledDate(Date wishScheduledDate) {
		this.wishScheduledDate = wishScheduledDate;
		return this;
	}

	public String getEmailOfWisher() {
		return emailOfWisher;
	}

	public WeWish setEmailOfWisher(String emailOfWisher) {
		this.emailOfWisher = emailOfWisher;
		return this;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public WeWish setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
		return this;
	}

	public String getHeroImage() {
		return heroImage;
	}

	public WeWish setHeroImage(String heroImage) {
		this.heroImage = heroImage;
		return this;
	}

	public String getSubHeroImage() {
		return subHeroImage;
	}

	public WeWish setSubHeroImage(String subHeroImage) {
		this.subHeroImage = subHeroImage;
		return this;
	}

}
