package edu.wm.werewolf.exceptions;

public class NoPlayerFoundException extends Exception {
	
	
	private String userId;
	
	private static final long serialVersionUID = 1L;
	
	public NoPlayerFoundException(String userId) {
		
		super(); // Runs parent's constructor, use since this is an extension of Exception
		this.userId = userId;
	}

	public String getUserID() {
		return userId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	

}
