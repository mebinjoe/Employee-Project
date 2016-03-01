package com.app.model;

public class Inbox {

	private int inboxId;
	private int userId;
	private String message;
	private int status;
	private int isRejected;
	private String privilege;
	private String type;
	private int skillId;
	private int count;
	
	
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private String requestedTime;
	
	public String getRequestedTime() {
		return requestedTime;
	}
	
	public void setRequestedTime(String requestedTime) {
		this.requestedTime = requestedTime;
	}
	
	public int getIsRejected() {
		return isRejected;
	}
	
	public void setIsRejected(int isRejected) {
		this.isRejected = isRejected;
	}
	
	public int getInboxId() {
		return inboxId;
	}
	
	public void setInboxId(int inboxId) {
		this.inboxId = inboxId;
	}
	
	public int getUserId() {
		return userId;
	}
	
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	public String getPrivilege() {
		return privilege;
	}
	
	public void setPrivilege(String privilege) {
		this.privilege = privilege;
	}

	public int getSkillId() {
		return skillId;
	}

	public void setSkillId(int skillId) {
		this.skillId = skillId;
	}
	
	
	
}
