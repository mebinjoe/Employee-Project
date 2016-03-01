package com.app.model;

import java.util.Date;

public class UserUpdate {
	
	private int emp_update_Id;
	private int userId;
	private String firstName;
	private String lastName;
	private Date dob;
	private String email;
	private String role;
	private String userName;
	private String password;
	private int deptId;	
	private String deptName;
	private String deptManager;	
	
	public int getEmp_update_Id() {
		return emp_update_Id;
	}
	
	public void setEmp_update_Id(int emp_update_Id) {
		this.emp_update_Id = emp_update_Id;
	}
	
	public int getUserId() {
		return userId;
	}
	
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {		
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public Date getDob() {
		return dob;
	}
	
	public void setDob(Date dob) {
		this.dob = dob;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getRole() {
		return role;
	}
	
	public void setRole(String role) {
		this.role = role;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public int getDeptId() {
		return deptId;
	}
	
	public void setDeptId(int deptId) {
		this.deptId = deptId;
	}
	
	public String getDeptName() {
		return deptName;
	}
	
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	
	public String getDeptManager() {
		return deptManager;
	}
	
	public void setDeptManager(String deptManager) {
		this.deptManager = deptManager;
	}	
}
