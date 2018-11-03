package jason.app.symphony.security.comp.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import jason.app.symphony.security.comp.dao.UserDao;
import jason.app.symphony.security.comp.entity.Role;

/**
 * A custom {@link UserDetailsService} where user information
 * is retrieved from a JPA repository
 */
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {
	
	@Autowired
	private UserDao userDao;

	/**
	 * Returns a populated {@link UserDetails} object. 
	 * The username is first retrieved from the database and then mapped to 
	 * a {@link UserDetails} object.
	 */
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {
			jason.app.symphony.security.comp.entity.User domainUser = userDao.findByUsername(username);
			if(domainUser==null) throw new UsernameNotFoundException("user not found!");
			boolean enabled = true;
			boolean accountNonExpired = true;
			boolean credentialsNonExpired = true;
			boolean accountNonLocked = true;
			
			return new jason.app.symphony.commons.http.model.User(
					domainUser.getUsername(), 
					domainUser.getPassword().replace("{SHA}", ""),
					domainUser.getPartyId(),
					!"N".equalsIgnoreCase(domainUser.getEnabled()),
					accountNonExpired,
					credentialsNonExpired,
					accountNonLocked,
					getGrantedAuthorities(domainUser.getRoles()));
		}catch(NoResultException|EmptyResultDataAccessException  e) {
			throw new UsernameNotFoundException("user not found!");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	

	
	/**
	 * Wraps {@link String} roles to {@link SimpleGrantedAuthority} objects
	 * @param roles {@link String} of roles
	 * @return list of granted authorities
	 */
	public static List<GrantedAuthority> getGrantedAuthorities(List<Role> roles) {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		if(roles!=null) {
			for (Role role : roles) {
				authorities.add(new SimpleGrantedAuthority("ROLE_"+role.getId()));
			}
		}
		return authorities;
	}
}
