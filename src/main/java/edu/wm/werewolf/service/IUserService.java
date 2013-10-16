package edu.wm.werewolf.service;

import edu.wm.werewolf.exceptions.UserAlreadyExistsException;

public interface IUserService {

	void createUser(String id, String username, String password, String firstname,
			String lastname) throws UserAlreadyExistsException;

	void removeUser(String username);

}
