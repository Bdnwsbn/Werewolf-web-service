package edu.wm.werewolf.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoAction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.GrantedAuthority;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;

import edu.wm.werewolf.exceptions.NoUserFoundException;
import edu.wm.werewolf.exceptions.UserAlreadyExistsException;
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
	
// ****Error here: overwrites username, and password to null!!!1****
	private MyUser convertFromObject(DBObject o) {
		MyUser u = new MyUser(null, null, null, null, null, null, null);
		
		ObjectId streamid = (ObjectId) o.get("_id");
		
		String id = streamid.toString();
		
		u.setId(id);
		u.setFirstName((String) o.get("firstName"));
		u.setLastName((String) o.get("lastName"));
		u.setUsername((String) o.get("username"));
		u.setPassword((String) o.get("password"));
		u.setImageURL((String) o.get("imageURL"));
		u.setIsAdmin((Boolean) o.get("isAdmin"));
		u.setAuthorities((Collection<GrantedAuthority>) o.get("authorities"));
		
		return u;
	}
	
	@Override
	public MyUser getUserById(String id) throws NoUserFoundException {
		DBCollection coll = getCollection();
		
		BasicDBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(id));
		
		DBObject o = coll.findOne(query);
		
		MyUser u = convertFromObject(o);
		
		return u;
	}
	
	@Override
	public void removeUserById(String id) {
		DBCollection coll = getCollection();
		BasicDBObject user = new BasicDBObject();
		user.put("_id", new ObjectId(id));
		coll.remove(user);
	}
	
	@Override
	public MyUser getUserByUsername(String name){
		DBCollection coll = getCollection();
		
		BasicDBObject query = new BasicDBObject();
		query.put("username", name);
		
		DBObject o = coll.findOne(query);
		
		MyUser u = convertFromObject(o);
		
		return u;
	}
	

	// Check to see if USER already exists in DB! if yes throw error
	@Override
	public void createUser(MyUser user) throws UserAlreadyExistsException {
		DBCollection collection = getCollection();
		
		// Check to see is User already exists
		BasicDBObject query = new BasicDBObject();
		query.put("id", user.getId());
		DBCursor cursor = collection.find(query);
		while(cursor.hasNext()) {
			if(cursor != null) {
				throw new UserAlreadyExistsException(user.getId());
			}
		}

		BasicDBObject userDoc = new BasicDBObject();
		userDoc.append("id", user.getId());
		userDoc.append("firstName", user.getFirstName());
		userDoc.append("lastName", user.getLastName());
		userDoc.append("username", user.getUsername());
		userDoc.append("password", user.getPassword());
		userDoc.append("imageURL", user.getImageURL());
		userDoc.append("isAdmin", user.getIsAdmin());
		
		collection.insert(userDoc);
			
	}
	
	@Override
	public void setAdmin(MyUser user) {
		DBCollection collection = getCollection();
		BasicDBObject newDocument = new BasicDBObject();
		
		newDocument.append("$set",  new BasicDBObject().append("isAdmin", true));
		
		BasicDBObject searchQuery = new BasicDBObject().append("_id", user.getId());

		collection.update(searchQuery, newDocument);
		
	}
	
	
	@Override
	public List<MyUser> getAllUsers() {
		List<MyUser> users = new ArrayList<>();
		
		DBCollection collection = getCollection();
		DBCursor cursor = collection.find();
		
		while(cursor.hasNext()){	
			DBObject obj = cursor.next();
			
			MyUser user = convertFromObject(obj);
			users.add(user);
		}
		
		return users;
	}
	

	@Override
	public void dropAllUsers() {
		DBCollection users = getCollection();
		users.drop();
	}

}
