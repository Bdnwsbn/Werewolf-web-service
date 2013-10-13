package edu.wm.werewolf.dao;

import java.util.List;

import edu.wm.werewolf.exceptions.NoUserFoundException;
import edu.wm.werewolf.model.MyUser;
import edu.wm.werewolf.model.User;

public interface IUserDAO {

	void createUser(MyUser user);

	MyUser getUserById(String id) throws NoUserFoundException;

	MyUser getUserByUsername(String name);

	void removeUserById(String username);
	
	void setAdmin(MyUser user);

	List<MyUser> getAllUsers();

	void dropAllUsers();


}
