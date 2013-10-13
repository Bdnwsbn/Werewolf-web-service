package edu.wm.werewolf.exceptions;

public class NoUserFoundException extends Exception {

	private String id;
	
	private static final long serialVersionUID = 1L;
	
	public NoUserFoundException(String id) {
		
		super(); // Runs parent's constructor, use since this is an extension of Exception
		this.id = id;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
