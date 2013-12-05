package edu.wm.werewolf.service;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import edu.wm.werewolf.dao.IUserDAO;
import edu.wm.werewolf.exceptions.UserAlreadyExistsException;
import edu.wm.werewolf.model.MyUser;

@Service
public class UserServiceImpl implements UserDetailsService, IUserService{
	
	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
			
	@Autowired private IUserDAO userDao;
	
	@Override
	public MyUser loadUserByUsername(String name) throws UsernameNotFoundException {
		
		MyUser u = userDao.getUserByUsername(name);
		
		logger.info("Got: " + u);
		
		if (u == null)
			throw new UsernameNotFoundException(name);
		
		return u;
	}
	
	@Override
	public void createUser(String id, String username, String password, 
			String firstname, String lastname) throws UserAlreadyExistsException{
		
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		MyUser u = new MyUser(id, firstname, lastname, username, password,
								"user.png", false, authorities);
		userDao.createUser(u);
	}
	
	@Override
	public void removeUser(String username){
		userDao.removeUserByUsername(username);
	}

}
