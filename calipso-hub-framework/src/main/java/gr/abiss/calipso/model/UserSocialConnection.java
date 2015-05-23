package gr.abiss.calipso.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity 
@IdClass(UserSocialConnectionId.class)
@Table(name = "UserConnection")
public class UserSocialConnection implements Serializable{

	@Id 
	@Column(length = 255, nullable = false)
	private String userId;
	@Id 
	@Column(length = 255, nullable = false)
	private String providerId;
	@Id 
	private Integer rank;
	private String providerUserId;
	@Column(nullable = false)
	private String displayName;
	@Column(length = 1000, nullable = false)
	private String profileUrl;
	private String imageUrl;
	@Column(nullable = false)
	private String accessToken;
	private String secret;
	private String refreshToken;
	private Long expireTime;

	public UserSocialConnection(){
		
	}
	
	public UserSocialConnection(String userId, String providerId,
			String providerUserId, int rank, String displayName,
			String profileUrl, String imageUrl, String accessToken,
			String secret, String refreshToken, Long expireTime) {
		this.setUserId(userId);
		this.setProviderId(providerId);
		this.setProviderUserId(providerUserId);
		this.setRank(rank);
		this.setDisplayName(displayName);
		this.setProfileUrl(profileUrl);
		this.setImageUrl(imageUrl);
		this.setAccessToken(accessToken);
		this.setSecret(secret);
		this.setRefreshToken(refreshToken);
		this.setExpireTime(expireTime);

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

	public String getProviderUserId() {
		return providerUserId;
	}

	public void setProviderUserId(String providerUserId) {
		this.providerUserId = providerUserId;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getProfileUrl() {
		return profileUrl;
	}

	public void setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public Long getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Long expireTime) {
		this.expireTime = expireTime;
	}

}
