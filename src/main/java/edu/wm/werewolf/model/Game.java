package edu.wm.werewolf.model;

import java.util.Date;

public class Game {

	private int dayNightFreq;
	private Date createdDate;
	private Boolean isRunning;
	private long timer;

	// Constructor
	public Game(int dayNightFreq, Date createdDate) {
		super();
		this.dayNightFreq = dayNightFreq;
		this.createdDate = createdDate;
	}

	// Getter & Setters
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
	
	public boolean isNight(){
		Date d = new Date();
		Long dt = d.getTime() - this.createdDate.getTime();
		if (dt / dayNightFreq % 2 == 0)
			return true;
		return false;
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

}
