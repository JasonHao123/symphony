package jason.app.symphony.security.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

@Entity
public class Role {
	
	@Id
	private String id;
	
	@Column
	private String name;
	
    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name="ROLE_PERMISSION",joinColumns = {@JoinColumn(name = "ROLE_ID")}, 
            inverseJoinColumns = {@JoinColumn(name = "PERMISSION_ID")})
    private List<Permission> permissions;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}
	
	
}
