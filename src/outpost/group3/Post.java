package outpost.group3;

import outpost.group3.Loc;

public class Post {
	private int id;
	private Loc currentLoc;
	private Loc expectedLoc;
	private Loc targetLoc;
	private boolean alive;
	private boolean updated;
	private int simIndex;
	
	Post(int id, Loc loc, int simIndex) {
		this.id = id;
		this.currentLoc = loc;
		this.alive = true;
		this.updated = true;
		this.simIndex = simIndex;
	}
	
	public int getId() {
		return id;
	}
	
	public Loc getCurrentLoc() {
		return currentLoc;
	}
	
	public void setCurrentLoc(Loc loc) {
		this.currentLoc = loc;
	}
	
	public Loc getExpectedLoc() {
		return expectedLoc;
	}
	
	public void setExpectedLoc(Loc loc) {
		this.expectedLoc = loc;
	}
	
	public Loc getTargetLoc() {
		return targetLoc;
	}
	
	public void setTargetLoc(Loc loc) {
		this.targetLoc = loc;
	}
	
	public int getSimIndex() {
		return simIndex;
	}
	
	public void setSimIndex(int simIndex) {
		this.simIndex = simIndex;
	}
	
	public boolean isAlive() {
		return alive;
	}
	
	public void destroy() {
		alive = false;
	}
	
	public boolean isUpdated() {
		return updated;
	}
	
	public void setUpdated(boolean updated) {
		this.updated = updated;
	}
}
