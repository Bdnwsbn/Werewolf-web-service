package edu.wm.werewolf.model;

import java.util.Collection;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

// Point of MyUser Class was to extend the already existing Spring Security User Class.
// SS User implements UserDetails interface - from UD we get user authentication 
@Document(collection = "users")
public class MyUser extends User {

	
	private static final long serialVersionUID = 1L;

	@Id
	protected String id;
	protected String firstName;
	protected String lastName;
	protected String imageURL;
	
	public MyUser(String firstName,
			String lastName, String username,
			String password, String imageURL,
			Collection<GrantedAuthority> authorities ) {
		
		super(username, password, true, true, true, true, authorities);
		
		this.firstName = firstName;
		this.lastName = lastName;
		this.imageURL = imageURL;
	}
	
	public String getId(){
		return id;
	}
	
	public void setId(String id){
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}

