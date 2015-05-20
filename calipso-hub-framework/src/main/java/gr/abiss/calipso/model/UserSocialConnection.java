package gr.abiss.calipso.model;

import gr.abiss.calipso.model.entities.AbstractPersistable;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "UserConnection", uniqueConstraints = { @UniqueConstraint(columnNames = {
		"userId", "providerId", "rank" }) })
@AttributeOverride(name = "id", column = @Column(name = "userId", unique = true))
public class UserSocialConnection extends AbstractPersistable {

	@Column(length = 255, nullable = false)
	private String providerId;
	private String providerUserId;
	@Column(nullable = false)
	private Integer rank;
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
	
	public UserSocialConnection(String id, String providerId,
			String providerUserId, int rank, String displayName,
			String profileUrl, String imageUrl, String accessToken,
			String secret, String refreshToken, Long expireTime) {
		this.setId(id);;
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
