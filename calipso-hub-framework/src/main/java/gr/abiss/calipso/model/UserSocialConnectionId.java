package gr.abiss.calipso.model;

import java.io.Serializable;

import javax.persistence.Column;

public class UserSocialConnectionId implements Serializable{

	@Column(length = 255, nullable = false)
	private String userId;
	@Column(length = 255, nullable = false)
	private String providerId;
	private Integer rank;
	
	public UserSocialConnectionId() {
		
	}
	
	public UserSocialConnectionId(String userId, String providerId, Integer rank) {
		super();
		this.userId = userId;
		this.providerId = providerId;
		this.rank = rank;
	}
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getProviderId() {
		return providerId;
	}
	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}
	public Integer getRank() {
		return rank;
	}
	public void setRank(Integer rank) {
		this.rank = rank;
	}
	
}
