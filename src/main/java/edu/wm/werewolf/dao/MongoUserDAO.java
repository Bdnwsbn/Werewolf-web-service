package edu.wm.werewolf.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCrypt;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;

import edu.wm.werewolf.exceptions.NoUserFoundException;
import edu.wm.werewolf.exceptions.UserAlreadyExistsException;
import edu.wm.werewolf.model.MyUser;

public class MongoUserDAO implements IUserDAO {
	
	private final Logger logger = LoggerFactory.getLogger(MongoUserDAO.class);
	
	public static final String DATABASE_NAME = "werewolf";
	public static final String COLLECTION_NAME = "users";

	@Autowired private DB db;

	private DBCollection getCollection(){
		DBCollection coll = null;
		
		if (db == null){
			logger.error("No mongo instance!");
		}
		
		try {
			coll = db.getCollection(COLLECTION_NAME);
		}
		catch (MongoException ex) {}
		
		return coll;
	}
	

	private MyUser convertFromObject(DBObject o) {
		ObjectId streamid = (ObjectId) o.get("_id");		
		String id = streamid.toString();

		Collection<GrantedAuthority> authorities = new ArrayList<>();
		Boolean isAdmin = (Boolean) o.get("isAdmin");
		if (isAdmin) {
			authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		}
		else {
			authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		}
		
		MyUser u = new MyUser(id, (String) o.get("firstName"),(String) o.get("lastName"), (String) o.get("username"),
				(String) o.get("password"), (String) o.get("imageURL"), (Boolean) o.get("isAdmin"),
				authorities);
		
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
	public void removeUserByUsername(String username) {
		DBCollection coll = getCollection();
		BasicDBObject user = new BasicDBObject();
		user.put("username", username);
		coll.remove(user);
	}
	
	@Override
	public MyUser getUserByUsername(String name){
		DBCollection coll = getCollection();
		
		BasicDBObject query = new BasicDBObject();
		query.put("username", name);
		
		DBObject o = coll.findOne(query);
//		DBCursor cursor = coll.find(query);
//		while(cursor.hasNext()){
//			DBObject o = cursor.next();
//			MyUser u = convertFromObject(o);
//			return u;
//		}
		

		MyUser u = convertFromObject(o);
		
		return u;
	}
	

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

		String pw_hash = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
		
		BasicDBObject userDoc = new BasicDBObject();
		userDoc.put("id", user.getId());
		userDoc.put("firstName", user.getFirstName());
		userDoc.put("lastName", user.getLastName());
		userDoc.put("username", user.getUsername());
		userDoc.put("password", pw_hash);
		userDoc.put("imageURL", user.getImageURL());
		userDoc.put("isAdmin", user.getIsAdmin());
		
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
