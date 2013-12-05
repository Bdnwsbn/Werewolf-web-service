package edu.wm.werewolf;

import static org.junit.Assert.*;

import java.net.UnknownHostException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;

import edu.wm.werewolf.dao.IGameDAO;
import edu.wm.werewolf.dao.IPlayerDAO;
import edu.wm.werewolf.dao.IUserDAO;
import edu.wm.werewolf.dao.MongoUserDAO;
import edu.wm.werewolf.exceptions.NoPlayerFoundException;
import edu.wm.werewolf.exceptions.NoUserFoundException;
import edu.wm.werewolf.exceptions.UserAlreadyExistsException;
import edu.wm.werewolf.model.MyUser;
import edu.wm.werewolf.model.Player;
import edu.wm.werewolf.service.GameService;
import edu.wm.werewolf.service.UserServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "root-context.xml" })
public class WerewolfTest {

	private final Logger logger = LoggerFactory.getLogger(WerewolfTest.class);
	
	@Autowired private UserServiceImpl userService;
	@Autowired private IUserDAO userDao;
	@Autowired private IPlayerDAO playerDao;
	
//	private IPlayerDAO playerDao;
//	private IUserDAO userDao;
//	private IGameDAO gameDao;
	
	@Test
	public void test() throws UserAlreadyExistsException, NoPlayerFoundException {
			
			userDao.dropAllUsers();
			userService.createUser("1", "ben", "1234", "ben", "katz");
			MyUser user1 = userService.loadUserByUsername("ben");
			logger.info(user1.toString()+" - success");
			
			MyUser user2 = userDao.getUserByUsername("ben");
			
			System.out.print(user1.getId());
			
			playerDao.dropAllPlayers();
			Player p = new Player("1", user1.getId(), false, false, 10.1, 10.2, "0");
			playerDao.createPlayer(p);
			Player p1 = playerDao.getPlayerById(user1.getId());
			
			logger.info(p1.toString()+" - success");
			
			System.out.print("..."+p.getId()+"\n");
			System.out.print(p.getUserId()+"\n");
			System.out.print("p = " + p);
			
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
		
	

		}
	}

