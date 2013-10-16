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
	protected String username;
	protected String password;
	protected String imageURL;
	protected Boolean isAdmin;
	protected Collection<GrantedAuthority> authorities;
	

	public MyUser(String id, String firstName,
			String lastName, String username,
			String password, String imageURL, Boolean isAdmin,
			Collection<GrantedAuthority> authorities ) {
		
		super(username, password, true, true, true, true, authorities);
		
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.imageURL = imageURL;
		this.isAdmin = isAdmin;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}
	
	public Boolean getIsAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(Boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
	
	public Collection<GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(Collection<GrantedAuthority> authorities) {
		this.authorities = authorities;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}

