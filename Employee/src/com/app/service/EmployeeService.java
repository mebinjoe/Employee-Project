package com.app.service;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.ui.ModelMap;

import com.app.model.Department;
import com.app.model.Inbox;
import com.app.model.Skills;
import com.app.model.UserDetails;
import com.app.model.UserUpdate;



public interface EmployeeService {		
	
	public boolean isValidUser(String username, String password) throws SQLException;		
	
	public String getUserRole(String username) throws SQLException;
	
	public int getUserId(String username) throws SQLException;
	
	public int addUser(UserDetails user, int deptId) throws SQLException;
	
	public void addUserFromFile(String text) throws SQLException;
	
	public List<Department> getAllDepartments() throws SQLException;
	
	public void deleteUser(int userId) throws SQLException;
	
	public UserDetails updateUser(UserDetails user) throws SQLException;
	
	public List<UserDetails> getAllUsersForUser(String username) throws SQLException;
	
	public List<UserDetails> getAllUsers() throws SQLException;
	
	public UserDetails getUserById(int userId) throws SQLException;
	
	public UserUpdate getUserByIdForUpdate(int userId) throws SQLException;
	
	public UserUpdate addUserAfterUpdate(UserUpdate userUpdate) throws SQLException;
	
	public UserDetails addUserAfterUpdateByManager(UserDetails user) throws SQLException;
	
	public UserDetails getUserByUserName(String username) throws SQLException;
	
	public List<UserDetails> getAllUsersForJson() throws SQLException;
	
	public int addSkill(Skills skill) throws SQLException;
	
	public void approveSkill(int skillId) throws SQLException;
	
	public Skills getSkillById(int skillId) throws SQLException;
	
	public Skills getRejectedSkillById(int skillId) throws SQLException;
	
	public List<Skills> getAllSkillsOfUsers() throws SQLException;
	
	public List<Skills> getSkillsToApprove(int userId) throws SQLException;
	
	public List<Skills> getApprovedSkillsOfUser(int userId) throws SQLException;
	
	public void rejectSkill(int skillId) throws SQLException;
	
	public void deleteSkill(int skillId) throws SQLException;
	
	public int addToInbox(int userId, String message) throws SQLException;
	
	public void addToInboxAsSkill(int inboxId, int skillId) throws SQLException;
	
	public List<Inbox> getInboxMessage() throws SQLException;
	
	public List<Inbox> getInboxMessageForUser(int userId) throws SQLException;
	
	public List<Inbox> getUserRequests() throws SQLException;
	
	public List<Inbox> getUserSkillRequests() throws SQLException;
	
	public List<Inbox> getUserEditRequests() throws SQLException;
	
	public List<Inbox> getNotifications() throws SQLException;
	
	public List<Inbox> getNotificationsForUser(int userId) throws SQLException;
	
	public void addSkillToCloneTable(int skillId) throws SQLException;
	
	public void isRejected(int inboxId) throws SQLException;
	
	public void isRead(int inboxId) throws SQLException;
	
	public void isRequestedForUserUpdate(int inboxId) throws SQLException;
	
	public void isRequestedForSkillUpdate(int inboxId) throws SQLException;
	
	public void isAccepted(int inboxId) throws SQLException;
	
	public void isAcceptedForAdminSkill(int inboxId) throws SQLException;
	
	public void isAcceptedForAdminExpiredRequest(int inboxId) throws SQLException;
	
	public void isAcceptedForAdminEditRequest(int inboxId) throws SQLException;
	
	public void isAcceptedForUserSkill(int inboxId) throws SQLException;
	
	public void isAcceptedForUserEditRequest(int inboxId) throws SQLException;
	
	public void deleteRejectedSkillStatusOfUser(int userId) throws SQLException;
	
	public List<Inbox> getReadMessage() throws SQLException;
	
	public List<Inbox> getTotalRequestCount() throws SQLException;
	
	public List<Inbox> getRequestCountForUser(int userId) throws SQLException;
	
	public void isExpired(int inboxId) throws SQLException;
	
	public List<UserDetails> getUsersBySearch(String searchText, String searchType) throws SQLException;
	
	public List<Skills> getSkillsBySearch(String searchText, String searchType) throws SQLException;
	
	public void deleteUserClone(int userId) throws SQLException;
	
	public void dropSkillClone() throws SQLException;
	
	public UserDetails getUserInfoFromRequest(HttpServletRequest request) throws SQLException;
	
	public Skills getSkillForUser(HttpServletRequest request, String role, int userId);	
	
	public void setSkillFlag(int skillId) throws SQLException;
	
	public void exportDataToCSV() throws SQLException;
	
	public void compareUserObjects(UserDetails user,UserDetails updatedUser) throws SQLException;	

	public void compareUpdatedUserObjectsForApproval(UserUpdate userUpdate, UserDetails userDetails) throws SQLException;		
	
	public void compareUpdatedUserObjectsForApprovalByManager(UserDetails userDetails, UserDetails user) throws SQLException;		

	public void compareUpdatedUserObjectsForRejection(UserUpdate userUpdate, UserDetails user) throws SQLException;			

	public void addApprovedSkills(UserDetails user, String skillName, int skillId) throws SQLException;			

	public void addRejectedSkills(UserDetails user, String skillName, int skillId) throws SQLException;	
	
	public void addSkillsForUserByManager(UserDetails user, String skillName, int skillId) throws SQLException;			
	
	public void addSkillsForUser(UserDetails user, String skillName, int skillId) throws SQLException;			
	
	public void deleteSkillsOfUser(UserDetails user, String skillName, int skillId) throws SQLException;			

	public void expiredMessagesForAdmin(UserDetails user,UserUpdate updateuser) throws SQLException;		
	
	public void checkMessageStatus() throws SQLException;	
	
	public void markEditedField(ModelMap modelMap) throws SQLException ;			
	
	public void writeXmlFile(String role, int userId) throws SQLException;
	
	public Skills getSkillForUserFromRequest(HttpServletRequest request)throws SQLException;	
	
	public boolean compareUserObjects(UserUpdate userUpdate, UserDetails userDetails );			
	
}



