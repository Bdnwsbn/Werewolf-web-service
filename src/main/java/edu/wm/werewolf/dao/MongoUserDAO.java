package edu.wm.werewolf.dao;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;

import edu.wm.werewolf.model.MyUser;
import edu.wm.werewolf.model.Player;
import edu.wm.werewolf.model.User;

public class MongoUserDAO implements IUserDAO {
	
	private final Logger logger = LoggerFactory.getLogger(MongoUserDAO.class);
	
	public static final String DATABASE_NAME = "werewolf";
	public static final String COLLECTION_NAME = "users";

	@Autowired private MongoClient mongoClient;

	private DBCollection getCollection(){
		DB db;
		DBCollection coll = null;
		
		if (mongoClient == null){
			logger.error("No mongo instance!");
		}
		
		try {
			db = mongoClient.getDB(DATABASE_NAME);
			coll = db.getCollection(COLLECTION_NAME);
		}
		catch (MongoException ex) {}
		
		return coll;
	}
	
	// This method is necessary since we are using a BasicDBObject to query. 
	// Since its a BasicDBObject, we can convert it to a User object and set the user attributes based on values
	// stored in MongoDB for the particular user
	private User convertFromObject(DBObject o) {
		User u = new User();
		
		ObjectId streamid = (ObjectId) o.get("_id");
		
		String id = streamid.toString();
		u.setId(id);
		u.setFirstName((String) o.get("firstName"));
		u.setLastName((String) o.get("lastName"));
		u.setImageURL((String) o.get("imageURL"));
		u.setUsername((String) o.get("username"));
		u.setHashedPassword((String) o.get("hashedPassword"));
		u.setIsAdmin((String) o.get("isAdmin"));
		return u;
	}
	
	// Need a throw no NoUserFoundException ???? 
	@Override
	public User getUserById(String id) {
		DBCollection coll = getCollection();
		
		BasicDBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(id));
		
		DBObject o = coll.findOne(query);
		
		User u = convertFromObject(o);
		
		return u;
	}
	
	@Override
	public void removeUserById(String username) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public User getUserByName(String name){
		DBCollection coll = getCollection();
		
		BasicDBObject query = new BasicDBObject();
		query.put("username", name);
		
		DBObject o = coll.findOne(query);
		
		User u = convertFromObject(o);
		
		return u;
	}
	
	
	@Override
	public void createUser(User user) {
		// TODO Auto-generated method stub
			
	}
	

	// Is making another ConvertFromObject for MyUser right? 
		// Do we do make this ConvertfromObject in UserServiceImpl??
	@Override
	public List<MyUser> getAllUsers() {
		// MyUser object list
		List<MyUser> users = new ArrayList<>();
		
		// Java MongoDB cursor
		// Find all users, convertFromObject to MyUser object
		DBCollection collection = getCollection();
		DBCursor cursor = collection.find();
		while(cursor.hasNext()){
			
			// Object found is a DBObject
			DBObject obj = cursor.next();
			
			// Convert DBObject to User object
			MyUser user = convertFromObject(obj);
		}
		
		return users;
	}

}
