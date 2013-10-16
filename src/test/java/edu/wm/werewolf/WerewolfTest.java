package edu.wm.werewolf;

import static org.junit.Assert.*;

import java.net.UnknownHostException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;

import edu.wm.werewolf.dao.IGameDAO;
import edu.wm.werewolf.dao.IPlayerDAO;
import edu.wm.werewolf.dao.IUserDAO;
import edu.wm.werewolf.exceptions.NoUserFoundException;
import edu.wm.werewolf.exceptions.UserAlreadyExistsException;
import edu.wm.werewolf.model.MyUser;

public class WerewolfTest {

	private IPlayerDAO playerDao;
	private IUserDAO userDao;
	private IGameDAO gameDao;
	
	@Test
	public void test() {
//		try {
//			
//			MongoClient mongo = new MongoClient("localhost", 8080);
//			
//			// if database doesn't exists, MongoDB will create it for you
//			DB db = mongo.getDB("werewolf");
//			
//			// if collection doesn't exists, MongoDB will create it for you			
//			DBCollection userColl = db.getCollection("users");
//			//Not creating players/game collection because techncially when I call
//			// the other methods like MongoPlayer (createplayer) it should be created 
//			// via that method
//			
//			//Create some Users
//			BasicDBObject user1 = new BasicDBObject();
//			user1.put("id", "1");
//			user1.put("firstName", "John");
//			user1.put("lastname", "Deer");
//			user1.put("username", "userONE");
//			user1.put("password", "1234");
//			user1.put("imageURL", "image1");
//			user1.put("isAdmin", false);
//			// Not sure what to do for Collection<GrantedAuthority> authorities
//			user1.put("authorities", null);
//			userColl.insert(user1);
//			
//			BasicDBObject user2 = new BasicDBObject();
//			user2.put("id", "2");
//			userColl.insert(user2);	
//			
//			MyUser user3 = new MyUser("3", "Jack", "Qu", "userTHREE", "4321", 
//									"image3", true, null);
//			
//			try {
//				userDao.createUser(user3);
//			} catch (UserAlreadyExistsException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}		
//			
//			
//			try {
//				userDao.getUserById("1");
//			} catch (NoUserFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			userDao.getUserByUsername("userONE");
//			userDao.getUserByUsername("userTWO");
//			
//			
//			
//			
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//	    } catch (MongoException e) {
//	    	e.printStackTrace();
//	    }
		
	}

}
