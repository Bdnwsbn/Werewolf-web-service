package edu.wm.werewolf.service;

public interface IUserService {

	void createUser(String username, String password, String firstname,
			String lastname);

	void removeUser(String username);

}
