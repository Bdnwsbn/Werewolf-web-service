package edu.wm.werewolf.model;

import java.util.Date;

public class Game {

	private String id;
	private Date createdDate;
	private Boolean isNight;
	private int dayNightFreq;
	private Boolean isRunning;
	private long timer;

	
	public Game(){
		
	}
	
	// Constructor
	public Game(String id, Date createdDate, Boolean isNight, int dayNightFreq, Boolean isRunning, long timer) {
		super();
		this.id = id;
		this.createdDate = createdDate;
		this.isNight = isNight;
		this.dayNightFreq = dayNightFreq;
		this.isRunning = isRunning;
		this.timer = timer;
	}

	// Getter & Setters
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public int getDayNightFreq() {
		return dayNightFreq;
	}

	public void setDayNightFreq(int dayNightFreq) {
		this.dayNightFreq = dayNightFreq;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	

	public Boolean getIsRunning() {
		return isRunning;
	}

	public void setIsRunning(Boolean isRunning) {
		this.isRunning = isRunning;
	}

	public long getTimer() {
		return timer;
	}

	public void setTimer(long timer) {
		this.timer = timer;
	}

	public Boolean getIsNight() {
		Date d = new Date();
		Long dt = d.getTime() - this.createdDate.getTime();
		if (dt / dayNightFreq % 2 == 0)
			return true;
		return false;
	}

	public void setIsNight(Boolean isNight) {
		this.isNight = isNight;
	}

}
