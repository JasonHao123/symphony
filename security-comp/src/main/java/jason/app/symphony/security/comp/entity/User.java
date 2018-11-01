package jason.app.symphony.security.comp.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name="USER_LOGIN")
public class User {
	
    @Id
    @Column(name="USER_LOGIN_ID")
	private String username;
    
    @Column(name="CURRENT_PASSWORD")
    private String password;
    
    @Column(name="ENABLED")
    private String enabled;
    
    @Column(name="PARTY_ID")
    private String partyId;
    
    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name="PARTY_ROLE",joinColumns = {@JoinColumn(name = "PARTY_ID")}, 
            inverseJoinColumns = {@JoinColumn(name = "ROLE_TYPE_ID")})
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

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

	public String getEnabled() {
		return enabled;
	}

	public void setEnabled(String enabled) {
		this.enabled = enabled;
	}

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}
    
    
}
