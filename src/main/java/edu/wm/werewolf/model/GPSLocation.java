package edu.wm.werewolf.model;

public class GPSLocation {

	private double lat;
	private double lng;

	// General Constructor
	public GPSLocation() {

	}
	
	// GPSLocation Constructor
	public GPSLocation(double lat, double lng) {
		super();
		this.lat = lat;
		this.lng = lng;
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
