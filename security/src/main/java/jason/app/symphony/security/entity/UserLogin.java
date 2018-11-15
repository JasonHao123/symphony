package jason.app.symphony.security.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class UserLogin {
	
	@Id
	private String username;
	
	@Column
	private String password;
	
	@Column
	private String userId;
	
	@Column
	private String accountNonExpired;
	
	@Column
	private String enabled;
	
	@Column
	private String credentialNonExpired;
	
	@Column
	private String nonLocked;
	
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
	
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUpdate;
	
    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name="USER_LOGIN_ROLE",joinColumns = {@JoinColumn(name = "USER_ID")}, 
            inverseJoinColumns = {@JoinColumn(name = "ROLE_ID")})
    private List<Role> roles;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getEnabled() {
		return enabled;
	}

	public void setEnabled(String enabled) {
		this.enabled = enabled;
	}

	public String getAccountNonExpired() {
		return accountNonExpired;
	}

	public void setAccountNonExpired(String accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	public String getCredentialNonExpired() {
		return credentialNonExpired;
	}

	public void setCredentialNonExpired(String credentialNonExpired) {
		this.credentialNonExpired = credentialNonExpired;
	}

	public String getNonLocked() {
		return nonLocked;
	}

	public void setNonLocked(String nonLocked) {
		this.nonLocked = nonLocked;
	}

	
	
}
