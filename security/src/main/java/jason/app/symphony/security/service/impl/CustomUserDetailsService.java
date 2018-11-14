package jason.app.symphony.security.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import jason.app.symphony.commons.http.model.SymphonyUser;
import jason.app.symphony.security.entity.Permission;
import jason.app.symphony.security.entity.Role;
import jason.app.symphony.security.entity.UserLogin;
import jason.app.symphony.security.repository.UserRepository;

/**
 * A custom {@link UserDetailsService} where user information
 * is retrieved from a JPA repository
 */
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {
	
	@Autowired
	private UserRepository userRepo;

	/**
	 * Returns a populated {@link UserDetails} object. 
	 * The username is first retrieved from the database and then mapped to 
	 * a {@link UserDetails} object.
	 */
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {
			Optional<UserLogin> result = userRepo.findById(username);
			if(!result.isPresent()) throw new UsernameNotFoundException("user not found!");
			UserLogin domainUser = result.get();
			return new SymphonyUser(
					domainUser.getUsername(), 
					domainUser.getPassword(),
					domainUser.getUserId(),
					domainUser.getEnabled(),
					domainUser.getAccountExpired(),
					domainUser.getCredentialExpired(),
					domainUser.getLocked(),
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
				for(Permission permission:role.getPermissions()) {
					authorities.add(new SimpleGrantedAuthority("PERM_"+permission.getId()));
				}
			}
		}
		return authorities;
	}
}
