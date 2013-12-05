package edu.wm.werewolf.model;

import java.util.Date;

public class Kill {

	private String killerId;
	private String victimId;
	private Date timestamp;
	private double lat;
	private double lng;
	
	public Kill() {
		
	}

	// Constructor
	public Kill(String killerId, String victimId, Date timestamp, double lat,
			double lng) {
		super();
		this.killerId = killerId;
		this.victimId = victimId;
		this.timestamp = timestamp;
		this.lat = lat;
		this.lng = lng;
	}

	// Getters & Setters
	public String getKillerId() {
		return killerId;
	}

	public void setKillerId(String killerId) {
		this.killerId = killerId;
	}

	public String getVictimId() {
		return victimId;
	}

	public void setVictimId(String victimId) {
		this.victimId = victimId;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
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
}
