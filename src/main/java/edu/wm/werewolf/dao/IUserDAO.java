package edu.wm.werewolf.dao;

import java.util.List;

import edu.wm.werewolf.model.MyUser;
import edu.wm.werewolf.model.User;

public interface IUserDAO {

	void createUser (User user);

	User getUserById(String id);

	User getUserByName(String name);

	void removeUserById(String username);

	List<MyUser> getAllUsers();
	
}
