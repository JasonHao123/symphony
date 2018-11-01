package jason.app.symphony.security.comp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="ROLE_TYPE")
public class Role {
    @Id
    @Column(name="ROLE_TYPE_ID")
    private String id;
    
    @ManyToOne
    @JoinColumn(name="PARENT_TYPE_ID",referencedColumnName="ROLE_TYPE_ID")
    private Role parent;
    
    @Column
    private String description;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Role getParent() {
		return parent;
	}

	public void setParent(Role parent) {
		this.parent = parent;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

    
}
