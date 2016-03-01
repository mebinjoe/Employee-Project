package com.app.model;

public class Message {
	
	private String str1;
	private String str2;
	private String str3;
	private int inboxId;
	private int userId;
	private String type;; 
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStr1() {
		return str1;
	}
	public void setStr1(String str1) {
		this.str1 = str1;
	}
	public String getStr2() {
		return str2;
	}
	public void setStr2(String str2) {
		this.str2 = str2;
	}
	public String getStr3() {
		return str3;
	}
	public void setStr3(String str3) {
		this.str3 = str3;
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
	
}
