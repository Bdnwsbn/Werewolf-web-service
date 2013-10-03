package edu.wm.werewolf.model;

public class Player {

	private String id;
	private String userId;
	private boolean isDead;
	private double lat;
	private double lng;
	private boolean isWerewolf;
	private String votedAgainst;

	// General Constructor
	public Player() {

	}

	// Player Constructor
	public Player(String id, String userId, boolean isDead, double lat, 
			double lng, boolean isWerewolf, String votedAgainst) {
		super();
		this.id = id;
		this.userId = userId;
		this.isDead = isDead;
		this.lat = lat;
		this.lng = lng;
		this.isWerewolf = isWerewolf;
		this.votedAgainst = votedAgainst;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public boolean isDead() {
		return isDead;
	}

	public void setDead(boolean isDead) {
		this.isDead = isDead;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public boolean isWerewolf() {
		return isWerewolf;
	}

	public void setWerewolf(boolean isWerewolf) {
		this.isWerewolf = isWerewolf;
	}

	public String getVotedAgainst() {
		return votedAgainst;
	}

	public void setVotedAgainst(String votedAgainst) {
		this.votedAgainst = votedAgainst;
	}

}
