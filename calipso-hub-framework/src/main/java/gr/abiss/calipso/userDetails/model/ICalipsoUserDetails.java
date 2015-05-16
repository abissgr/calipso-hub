package gr.abiss.calipso.userDetails.model;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.social.security.SocialUserDetails;

import com.wordnik.swagger.annotations.ApiModel;

public interface ICalipsoUserDetails extends SocialUserDetails {

	public String getId();

	public void setId(String id);

	public String getFirstName();

	public void setFirstName(String firstName);

	public String getLastName();

	public void setLastName(String lastName);

	public Date getLastPassWordChangeDate();

	public void setLastPassWordChangeDate(Date lastPassWordChangeDate);

	public String getEmail();

	public void setEmail(String email);

	public String getEmailHash();

	public void setEmailHash(String emailHash);

	public String getAvatarUrl();

	public void setAvatarUrl(String avatarUrl);

	public Date getBirthDay();

	public void setBirthDay(Date birthDay);

	public Date getLastVisit();

	public void setLastVisit(Date lastVisit);

	public Date getLastPost();

	public void setLastPost(Date lastPost);

	public Short getLoginAttempts();

	public void setLoginAttempts(Short loginAttempts);

	public Boolean getActive();

	public void setActive(Boolean active);

	public String getInactivationReason();

	public void setInactivationReason(String inactivationReason);

	public Date getInactivationDate();

	public void setInactivationDate(Date inactivationDate);

	public String getLocale();

	public void setLocale(String locale);

	public String getDateFormat();

	public void setDateFormat(String dateFormat);

	public boolean isAdmin();

	public void setAdmin(boolean isAdmin);

	public boolean isSiteAdmin();

	public void setSiteAdmin(boolean isSiteAdmin);

	public void setRedirectUrl(String redirectUrl);

	public String getRedirectUrl();

	public void setConfirmationToken(String confirmationToken);

	public void setResetPasswordToken(String resetPasswordToken);

	public Map<String, String> getMetadata();

	public void setMetadata(Map<String, String> metadata);

	public void addMetadatum(String predicate, String object);

	public String getConfirmationToken();

	public String getResetPasswordToken();

	public void setUsername(String username);

	public void setPassword(String password);

	public void setAuthorities(List<? extends GrantedAuthority> authorities);

	public Long getNotificationCount();
	
	public void setNotificationCount(Long notificationCount);
	

}