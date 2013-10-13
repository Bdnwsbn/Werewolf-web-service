package edu.wm.werewolf.exceptions;

public class UserAlreadyExistsException extends Exception{
	
	private String id;
	
	private static final long serialVersionUID = 1L;
	
	public UserAlreadyExistsException(String id) {
		
		super();
		this.id = id;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
