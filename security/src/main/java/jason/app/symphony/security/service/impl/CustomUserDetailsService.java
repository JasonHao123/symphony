package jason.app.symphony.security.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import jason.app.symphony.commons.http.model.SymphonyUser;
import jason.app.symphony.security.entity.Permission;
import jason.app.symphony.security.entity.Role;
import jason.app.symphony.security.entity.UserLogin;
import jason.app.symphony.security.repository.RoleRepository;
import jason.app.symphony.security.repository.UserRepository;

/**
 * A custom {@link UserDetailsService} where user information
 * is retrieved from a JPA repository
 */
@Transactional
public class CustomUserDetailsService implements UserDetailsService {
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private RoleRepository roleRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired(required=false)
	private AuthenticationManager authManager;

	/**
	 * Returns a populated {@link UserDetails} object. 
	 * The username is first retrieved from the database and then mapped to 
	 * a {@link UserDetails} object.
	 */
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {
			UserLogin result = userRepo.findOne(username);
			if(result==null) throw new UsernameNotFoundException("user not found!");
			UserLogin domainUser = result;
			return new SymphonyUser(
					domainUser.getUsername(), 
					domainUser.getPassword(),
					domainUser.getUserId(),
					"Y".equals(domainUser.getEnabled()),
					"Y".equals(domainUser.getAccountNonExpired()),
					"Y".equals(domainUser.getCredentialNonExpired()),
					"Y".equals(domainUser.getNonLocked()),
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
				if(role.getPermissions()!=null) {
					for(Permission permission:role.getPermissions()) {
						authorities.add(new SimpleGrantedAuthority("PERM_"+permission.getId()));
					}
				}
			}
		}
		return authorities;
	}



	public SymphonyUser createUser(String email, String password) throws Exception {
		
		UserLogin login = userRepo.findOne(email);
		if(login!=null) {
			throw new Exception("User already exists");
		}else {
			UserLogin user = new UserLogin();
			user.setUsername(email);
			user.setPassword(passwordEncoder.encodePassword(password, null));
			user.setAccountNonExpired("Y");
			user.setCreateDate(new Date());
			user.setCredentialNonExpired("Y");
			user.setEnabled("Y");
			user.setLastUpdate(new Date());
			user.setNonLocked("Y");
			user.setRoles(findOrCreateRoles("USER"));
			userRepo.save(user);
			return new SymphonyUser(
					user.getUsername(), 
					user.getPassword(),
					user.getUserId(),
					"Y".equals(user.getEnabled()),
					"Y".equals(user.getAccountNonExpired()),
					"Y".equals(user.getCredentialNonExpired()),
					"Y".equals(user.getNonLocked()),
					getGrantedAuthorities(user.getRoles()));
		}

	}



	private List<Role> findOrCreateRoles(String... roles ) {
		List<Role> result = new ArrayList<Role>();
		for(String role:roles) {
			result.add(findOrCreateRole(role));
		}
		return result;
	}
	private Role findOrCreateRole(String roleId) {
		Role role = roleRepo.findOne(roleId);
		if(role!=null) {
			return role;
		}else {
			Role r = new Role();
			r.setId(roleId);
			r.setName(roleId);
			r= roleRepo.save(r);
			return r;
		}
	}



	public void signin(String username,String password) {
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
		Authentication auth = authManager.authenticate(token);
		SecurityContextHolder.getContext().setAuthentication(auth);
	}

}
