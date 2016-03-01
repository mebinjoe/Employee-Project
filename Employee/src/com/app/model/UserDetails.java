package com.app.model;
import java.util.Date;
import java.util.List;

public class UserDetails {
	
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
	
	/* Spring Security fields*/
    private List<Role> authorities;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;
	
	
	public List<Role> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(List<Role> authorities) {
		this.authorities = authorities;
	}

	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}	

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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
