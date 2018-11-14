package jason.app.symphony.commons.http.model;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public class SymphonyUser extends org.springframework.security.core.userdetails.User {
	private String partyId;
	private String schema;
	public SymphonyUser(String username, String password,String partyId, boolean enabled, boolean accountNonExpired,
			boolean credentialsNonExpired, boolean accountNonLocked,
			Collection<? extends GrantedAuthority> authorities) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
		this.partyId = partyId;
	}
	public String getPartyId() {
		return partyId;
	}
	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}
	public String getSchema() {
		return schema;
	}
	public void setSchema(String schema) {
		this.schema = schema;
	}

}
