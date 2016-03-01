package com.app.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import org.springframework.stereotype.Controller;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.http.HttpStatus;

import com.app.service.EmployeeService;
import com.app.util.ImportContacts;
import com.app.model.Department;
import com.app.model.Inbox;
import com.app.model.Skills;
import com.app.model.UserDetails;
import com.app.model.UserUpdate;


@Controller
@SessionAttributes({"role" , "userId"})
public class UserController {	

	private List<UserDetails> users =  new ArrayList<>(); 
        
    @Autowired
    public EmployeeService employeeService;    
    
    @RequestMapping(value="/welcome", method = RequestMethod.GET)
    public String userLogin(ModelMap model, Principal principal, HttpSession session) throws SQLException { 
    	UserDetails user = new UserDetails();
    	user = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = user.getUserName(); //get logged in username
    	String userRole = employeeService.getUserRole(username);    	
	    int userId = employeeService.getUserId(username);			  
	    model.addAttribute("role", userRole);
	    session.setAttribute("role", userRole);
	    model.addAttribute("userId", userId); //session
	    employeeService.writeXmlFile(userRole, userId); //generate xml file for bar graph
	
	    if (userRole.equals("admin")) {				    	
	    	users = employeeService.getAllUsersForUser(username);    		
	    	model.addAttribute("users", users);
	    	user = employeeService.getUserByUserName(username);
	    	model.addAttribute("role", user.getRole());
	    	model.addAttribute("inList", employeeService.getNotifications());
	    }
	    else if (userRole.equals("user")) {	
		    user = employeeService.getUserByUserName(username);  
		    if(user!=null) {
		    	users =  new ArrayList<>();
		    	users.add(user);
		    }   
		    model.addAttribute("users", users);
	    }
	    else if (userRole.equals("manager")) {	
	    	users = employeeService.getAllUsersForUser(username);
	    	user = employeeService.getUserByUserName(username);			    	
	    	model.addAttribute("role", user.getRole());			    	
	    	model.addAttribute("users", users);
	    	employeeService.checkMessageStatus(); // check message expiry    				  	
	    	employeeService.markEditedField(model); //highlight edited field 	
	    }     	
    	return "listUser";     
    }
    
     
    @RequestMapping(value="/login", method = RequestMethod.GET)
    public String logins(ModelMap model) {     	
    	return "login";     
    }
    
     
    @RequestMapping(value="/loginError", method = RequestMethod.GET)
    public String loginError(HttpServletRequest request) {    		
    	request.setAttribute("message", "Invalid Credentials!!");
		return "login"; 
    }
	
	
	@RequestMapping(value = "/logout", method = RequestMethod.POST )
	public ModelAndView logout(HttpServletRequest request, ModelAndView model, HttpServletResponse response) {
		model = new ModelAndView("login");
		CookieClearingLogoutHandler cookieClearingLogoutHandler = new CookieClearingLogoutHandler(AbstractRememberMeServices.SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY);	    
	    cookieClearingLogoutHandler.logout(request, response, null);
	    
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth != null){    
	         new SecurityContextLogoutHandler().logout(request, response, auth);	       
	    }
	    SecurityContextHolder.getContext().setAuthentication(null);
	    cookieClearingLogoutHandler.logout(request, response, null);
	    request.getSession().invalidate();
        return model;       
    }
	
	
	@RequestMapping(value = "/home", method = RequestMethod.GET )
	public ModelAndView home(ModelAndView model, @ModelAttribute(value = "role") String role, @ModelAttribute(value = "userId") int userId, ModelMap modelMap) throws SQLException {
		model = new ModelAndView("listUser");		
		
		if (role.equals("admin")) {	
			UserDetails user = employeeService.getUserById(userId);
			String username = user.getUserName();
			users = employeeService.getAllUsersForUser(username);    		
			modelMap.addAttribute("users", users);			  
			modelMap.addAttribute("role", user.getRole());			 
			modelMap.addAttribute("inList", employeeService.getNotifications());
		}
		else if (role.equals("user")) {	
			UserDetails user = employeeService.getUserById(userId);
			if(user!=null) {
				users =  new ArrayList<>();
				users.add(user);
			} 
			modelMap.addAttribute("users", users);
		}
		else if (role.equals("manager")) {	
			UserDetails user = employeeService.getUserById(userId);
			String username = user.getUserName();
			users = employeeService.getAllUsersForUser(username);
			modelMap.addAttribute("role", user.getRole());			   
			modelMap.addAttribute("users", users);
			employeeService.checkMessageStatus(); // check message expiry    				  	
			employeeService.markEditedField(modelMap); //highlight edited field 	
		} 		
		return model;		
    }	
	
	
	@RequestMapping(value = "/UserController", method = RequestMethod.GET, params = {"action=insertUser"})
    public ModelAndView insertUser(ModelAndView model, HttpServletRequest request) throws SQLException {
		model = new ModelAndView("addUser");
		int isEdit = 1;
		model.addObject("isEdit", isEdit);	
		List<Department> department = new ArrayList<Department>();
		department = employeeService.getAllDepartments(); 
    	model.addObject("department", department);
    	return model;
    }
	
	
	@RequestMapping(value = "/UserController", method = RequestMethod.GET, params = {"action=editUser"})
    public ModelAndView editUser(@RequestParam int userId, ModelAndView model, HttpServletRequest request) throws SQLException {		
		model = new ModelAndView("addUser");
		int isEdit = 1;
		model.addObject("isEdit", isEdit);
        UserDetails user = employeeService.getUserById(userId);
        model.addObject("user", user);
        List<Department> department = new ArrayList<Department>();
        department = employeeService.getAllDepartments();
    	model.addObject("department", department);
    	return model;
    }
	
	
	@RequestMapping(value = "/UserController", method = RequestMethod.GET, params = {"action=deleteUser"})
    public ModelAndView deleteUser(@RequestParam int userId, ModelAndView model, HttpServletRequest request) throws SQLException {
		model = new ModelAndView("listUser");
		employeeService.deleteUser(userId);        
		model.addObject("users", employeeService.getAllUsers());
		return model;
	}	
	
	
	@RequestMapping(value = "/addUser", method = RequestMethod.POST )
	public String addUser(HttpServletRequest request, Model model, @ModelAttribute("role") String role, @ModelAttribute("action") String action) throws SQLException {
		
		if(action.equals("USERADD")) {
			int userId = 0;
			int deptId = 0;			
			UserDetails user = employeeService.getUserInfoFromRequest(request); 		
			deptId = user.getDeptId();
			userId = employeeService.addUser(user, deptId); 		     
			model.addAttribute("users", employeeService.getAllUsers());
			Inbox inbox = new Inbox();
			if (userId != 0) {
				inbox.setMessage("Welcome " +user.getFirstName() +" " +user.getLastName());
				int inboxId = employeeService.addToInbox(userId, inbox.getMessage());
				employeeService.isAccepted(inboxId);
			}
			model.addAttribute("inList" , employeeService.getNotifications());		
			return "listUser";
		}
		
		else if(action.equals("EDIT")) {
			UserDetails user = employeeService.getUserInfoFromRequest(request);  
			int userId = Integer.parseInt(request.getParameter("userId"));
			UserDetails userObj = employeeService.getUserById(userId);
			employeeService.compareUserObjects(user, userObj);
			employeeService.updateUser(user);
			user = employeeService.getUserById(userId);
			if (user!=null) {
				users =  new ArrayList<>();
				users.add(user);
			}
			model.addAttribute("users", users);
			model.addAttribute("notifications" , employeeService.getNotificationsForUser(userId)); //notifications
			model.addAttribute("inboxList" , employeeService.getInboxMessageForUser(userId)); //inbox
		
			if(role.equals("user")) {
				return "listUser";
			}
			else {
				return "dashboard";
			}
		
		}
		return "listUser";
	}	
		
	
	@RequestMapping(value = "/UserController", method = RequestMethod.GET, params = {"action=insertSkill"})
    public ModelAndView insertSkill(@ModelAttribute(value="userId") int userID, ModelAndView model, HttpServletRequest request, @ModelAttribute("role") String userRole) throws SQLException {
		model = new ModelAndView("addSkill");
    	model.addObject("users", employeeService.getAllUsers());     	
    	int isInsertSkill = 1;
    	model.addObject("isInsertSkill", isInsertSkill);
    	if(!userRole.equals(null)){
    		String role = userRole;       		
    		model.addObject("role", role);
    		if(role.equals("admin") || role.equals("manager")) {
    			int skillStatus = 1;
    			model.addObject("skillStatus", skillStatus);
    		}
    	}
    	model.addObject("userID", userID);
    	return model;
    }
	
	
	@RequestMapping(value = "/addSkill", method = RequestMethod.POST )
	public String addSkill(HttpServletRequest request, Model model, @ModelAttribute("role") String role, @ModelAttribute("userId") int userId) throws SQLException {		
		Skills skill = new Skills();
		
		if(role.equals("user")) {
			skill= employeeService.getSkillForUser(request, role, userId);
		}
		else {
			int useriD = Integer.parseInt(request.getParameter("Id"));
			skill= employeeService.getSkillForUser(request, role, useriD);			
		}
		
    	int skillId= employeeService.addSkill(skill);     	
    	int userID = skill.getUserId();
    	String skillName = skill.getSkillName();
    	UserDetails user = employeeService.getUserById(userID);
    	List<UserDetails> users =  new ArrayList<>();     
    	
    	if(role.equals("admin") || role.equals("manager")){        		
    		employeeService.approveSkill(skillId);
    		employeeService.addSkillsForUserByManager(user, skillName, skillId);
    	}
    	else {        		
    		employeeService.addSkillsForUser(user, skillName, skillId);
    	}     	  	
    	model.addAttribute("skill", skill);
    	
    	if(role.equals("admin") || role.equals("manager")) {
    		users = employeeService.getAllUsers();
    	}
    	else if(role.equals("user")){
    		user = employeeService.getUserById(userID);
    	}
    	else {
    		user = employeeService.getUserById(userID);
    	}
    	 
		if(user!=null) {			 
			users.add(user);
		}    
		model.addAttribute("users", users);    	
		return "listUser";		
	}
	
	
	@RequestMapping(value = "/UserController", method = RequestMethod.GET, params = {"action=viewSkill"})
    public ModelAndView viewSkill(HttpServletRequest request, ModelAndView model, @ModelAttribute("role") String role, @ModelAttribute("userId") int userId ) throws SQLException {		
		model = new ModelAndView("viewSkill");
		
    	if ((request.getParameter("userId") == null) && request.getParameter("Id") == null) {
    		if (role.equals("manager") || role.equals("admin")) {  
    			model.addObject("role", role);
    			model.addObject("skills", employeeService.getAllSkillsOfUsers()); 
    		}        		     	
    		else {
    			int userid = userId;
    			model.addObject("skills", employeeService.getApprovedSkillsOfUser(userid));
    		}
    	}
    	else {
			int userid = Integer.parseInt(request.getParameter("Id"));
			model.addObject("skills", employeeService.getApprovedSkillsOfUser(userid));
		} 		
    	return model;
    }
	
	
	@RequestMapping(value = "/UserController", method = RequestMethod.GET, params = {"action=deleteSkill"})
    public ModelAndView deleteSkill(ModelAndView model, @RequestParam(value="skillId") int skillId, @ModelAttribute("role") String role, @ModelAttribute("userId") int userId, 
    		HttpServletResponse response) throws SQLException, IOException {
		
		model = new ModelAndView("viewSkill");
		Skills skill = new Skills();
    	skill= employeeService.getSkillById(skillId);
    	int userid = skill.getUserId();
    	UserDetails user = employeeService.getUserById(userid);
    	String skillName = skill.getSkillName();
    	employeeService.deleteSkill(skillId);     
    	
    	if (role.equals("manager") || role.equals("admin")) { 
    		model.addObject("skills", employeeService.getAllSkillsOfUsers());
    		employeeService.deleteSkillsOfUser(user, skillName, skillId);
    	}
    	else {    		
    		model.addObject("skills", employeeService.getApprovedSkillsOfUser(userId));        		
    	}  
    			
    	model.addObject("deleteStatus", true);
    	return model;
	}
	
	
	@RequestMapping(value = "/UserController", method = RequestMethod.GET, params = {"action=dashboard"})
    public ModelAndView dashboard(ModelAndView model, HttpServletRequest request, @ModelAttribute("role") String role, @ModelAttribute("userId") int userId, ModelMap modelMap) throws SQLException {
		model = new ModelAndView("dashboard");
    	
    	if (role.equals("admin") || role.equals("manager")) {
    		model.addObject("notifications" , employeeService.getNotifications()); //notifications
    		model.addObject("inboxList" , employeeService.getInboxMessage()); //inbox
    		employeeService.checkMessageStatus(); // check message expiry    				  	
    		employeeService.markEditedField(modelMap); //highlight edited field 
    	}
    	else if (role.equals("user")) {
    		model.addObject("notifications" , employeeService.getNotificationsForUser(userId)); //notifications
    		model.addObject("inboxList" , employeeService.getInboxMessageForUser(userId)); //inbox
    	}      	
    	return model;
	}
  		
	
	@RequestMapping(value = "/exportContacts", method = RequestMethod.GET)
	public String exportContacts() throws SQLException {
		employeeService.exportDataToCSV();
		return "listUser";
	}
	
	
	@RequestMapping(value = "/importContacts", method = RequestMethod.GET)
	public String importContacts() throws SQLException {
		new ImportContacts();
		return "listUser";
	}
	
	
	@RequestMapping(value = "/UserController", method = RequestMethod.GET, params = {"action=search"})
	public ModelAndView search(@RequestParam(value = "searchText") String searchText, @RequestParam(value="searchType") String searchType, HttpServletResponse response, ModelAndView model) throws SQLException {							
		List<Skills> skills =  new ArrayList<Skills>(); 
		model = new ModelAndView();
		
    	if (searchText == null || searchText.isEmpty()){
    		model = new ModelAndView("listUser");
			model.addObject("noSearchFound", true);
			return model;
    	}        	
    	else {
    		if (searchType.equals("skill")) {
    			model = new ModelAndView("viewSkill");
    			skills= employeeService.getSkillsBySearch(searchText,searchType);
    			model.addObject("skills", skills); 
    			return model;
    		}
    		else {
    			model = new ModelAndView("listUser");
    			users = employeeService.getUsersBySearch(searchText,searchType);
    			model.addObject("users", users); 
    			return model;
    		}
    	}    
    }
	
	
	@RequestMapping(value = "/UserController", method = RequestMethod.GET, params = {"action=approveRequest"})
	public ModelAndView approveRequest(@RequestParam(value = "userId") int userId, @RequestParam(value = "inboxId") int inboxId,  HttpServletRequest request, ModelMap modelMap) throws SQLException {	
		ModelAndView model = new ModelAndView("dashboard");
        UserDetails user = new UserDetails();        	
        boolean userFieldChangeStatus = false;
        boolean userFieldChangeStatusAfterUpdate = false; 
        UserUpdate userUpdate = employeeService.getUserByIdForUpdate(userId);
        UserDetails userDetails = employeeService.getUserById(userId);
        userFieldChangeStatus = employeeService.compareUserObjects(userUpdate, userDetails);
    	    		    		
    		if (request.getParameter("username") != null  && !request.getParameter("username").isEmpty() ){
    			user = employeeService.getUserInfoFromRequest(request);        	
        		userFieldChangeStatusAfterUpdate = employeeService.compareUserObjects(userUpdate, user); 
        		
        		if(userFieldChangeStatus && !userFieldChangeStatusAfterUpdate) {
        			employeeService.compareUpdatedUserObjectsForApproval(userUpdate, userDetails);
        			employeeService.addUserAfterUpdate(userUpdate);
        		}
        		else if(userFieldChangeStatus && userFieldChangeStatusAfterUpdate) {
        			employeeService.compareUpdatedUserObjectsForApprovalByManager(userDetails, user);            	
        			employeeService.addUserAfterUpdateByManager(user);
        		}
        		employeeService.updateUser(user);
    		}
        
    		else {
    			if(userFieldChangeStatus) {
    				employeeService.compareUpdatedUserObjectsForApproval(userUpdate, userDetails);
    				employeeService.addUserAfterUpdate(userUpdate);  
    			}
    		}
        
    	employeeService.isRead(inboxId);
    	model.addObject("notifications" , employeeService.getNotifications()); //notifications
    	model.addObject("inboxList" , employeeService.getInboxMessage()); //inbox
    	employeeService.checkMessageStatus(); // check message expiry    				  	
    	employeeService.markEditedField(modelMap); //highlight edited field 
    	return model; 
	}
	
	
	@RequestMapping(value = "/rejectRequest", method = RequestMethod.GET)
	public ModelAndView rejectRequest(@RequestParam(value = "inboxId") int inboxId, @RequestParam(value = "userId") int userId, ModelMap modelMap) throws SQLException  {
		ModelAndView model = new ModelAndView("dashboard");		
    	UserUpdate userUpdate = employeeService.getUserByIdForUpdate(userId);
    	UserDetails user = employeeService.getUserById(userId);
    	employeeService.isRead(inboxId);
    	employeeService.compareUpdatedUserObjectsForRejection(userUpdate, user);
    	model.addObject("notifications" , employeeService.getNotifications()); //notifications
    	model.addObject("inboxList" , employeeService.getInboxMessage()); //inbox
    	employeeService.checkMessageStatus(); // check message expiry    				  	
    	employeeService.markEditedField(modelMap); //highlight edited field  
    	return model;
	}
	
	
	@RequestMapping(value = "/approveSkill", method = RequestMethod.GET)
	public ModelAndView approveSkill(HttpServletRequest request, ModelMap modelMap) throws SQLException {
		ModelAndView model = new ModelAndView("dashboard");
		Skills skillSet = new Skills();
		Skills skill = new Skills();
    	int skillId = Integer.parseInt(request.getParameter("skillId").trim());
    	int skillIdAfterUpdate = 0;
    	
    	if (request.getParameter("skillName") != null  && !request.getParameter("skillName").isEmpty() ){ 
    		skillSet = employeeService.getSkillForUserFromRequest(request);  
    		employeeService.setSkillFlag(skillId);
    		skillIdAfterUpdate = employeeService.addSkill(skillSet);
    		employeeService.approveSkill(skillIdAfterUpdate);
    		skill = employeeService.getSkillById(skillIdAfterUpdate);
    	}
    	else {
    		employeeService.approveSkill(skillId);
        	skill = employeeService.getSkillById(skillId); 
    	}
    	
    	int userId = skill.getUserId();
		String skillName = skill.getSkillName();
    	UserDetails user = employeeService.getUserById(userId);  
    	int inboxId = Integer.parseInt(request.getParameter("inboxId"));
    	employeeService.isRead(inboxId);        	
    	employeeService.deleteRejectedSkillStatusOfUser(userId); 
    	employeeService.addApprovedSkills(user, skillName, skillId); 
    	int skillStatus = 1;
    	model.addObject("skillStatus", skillStatus);
    	model.addObject("notifications" , employeeService.getNotifications()); //notifications
    	model.addObject("inboxList" , employeeService.getInboxMessage()); //inbox
    	employeeService.checkMessageStatus(); // check message expiry    				  	
    	employeeService.markEditedField(modelMap); //highlight edited field
    	return model;
	}
	
	
	@RequestMapping(value = "/rejectSkill", method = RequestMethod.GET)
	public ModelAndView rejectSkill(HttpServletRequest request) throws SQLException {
		ModelAndView model = new ModelAndView("dashboard");
		int skillId = Integer.parseInt(request.getParameter("skillId").trim());	        	
		employeeService.addSkillToCloneTable(skillId);
    	Skills skill = employeeService.getSkillById(skillId);
    	employeeService.rejectSkill(skillId);        	
    	int userId = skill.getUserId();
    	String skillName = skill.getSkillName();
    	UserDetails user = employeeService.getUserById(userId);        	
    	int inboxId = Integer.parseInt(request.getParameter("inboxId"));
    	employeeService.isRead(inboxId);        	
    	employeeService.addRejectedSkills(user, skillName, skillId);
    	return model;
	}
	
	
	@RequestMapping(value = "/UserController", method = RequestMethod.GET, params = {"action=editSkill"})
	public ModelAndView editSkill(HttpServletRequest request, @RequestParam(value = "userId") int userId, @ModelAttribute("role") String role) throws SQLException {
		ModelAndView model = new ModelAndView("addSkill");		
    	int skillId = Integer.parseInt(request.getParameter("skillId")); 
    	if (role.equals("admin") || role.equals("manager") ) {
    		int inboxId = Integer.parseInt(request.getParameter("inboxId"));         
    		model.addObject("inboxId", inboxId);
    	}
    	Skills skill = employeeService.getSkillById(skillId); 	
    	model.addObject("skill", skill);      
    	model.addObject("skillId", skillId); 
    	model.addObject("userID", userId); 
    	return model;
	}
	
	
	@RequestMapping(value = "/UserController", method = RequestMethod.GET, params = {"action=editRejectedSkill"})
	public ModelAndView editRejectedSkill(HttpServletRequest request, @RequestParam(value = "userId") int userId, @ModelAttribute("role") String role) throws SQLException {
		ModelAndView model = new ModelAndView("addSkill");
    	int skillId = Integer.parseInt(request.getParameter("skillId"));       
    	Skills skill = employeeService.getRejectedSkillById(skillId);  	
    	model.addObject("skill", skill); 
    	return model;
	}	
	
	
	@RequestMapping(value = "/UserController", method = RequestMethod.GET, params = {"action=editUserRequest"})
	public ModelAndView editUserRequest(@RequestParam(value = "userId") int userId, @ModelAttribute("role") String role, HttpServletRequest request) throws SQLException {
		ModelAndView model = new ModelAndView("addUser");
		
    	if (role.equals("admin") || role.equals("manager") ) {
    		int inboxId = Integer.parseInt(request.getParameter("inboxId"));
    		model.addObject("inboxId", inboxId);        	
    	}
    	UserUpdate userUpdate = employeeService.getUserByIdForUpdate(userId);
    	model.addObject("user", userUpdate);       
    	List<Department> department = new ArrayList<Department>();
    	department = employeeService.getAllDepartments();
    	model.addObject("userId", userId);        	
    	model.addObject("department", department);
    	return model;
	}	
	
	
	@RequestMapping(value ={"/login", "/addUser", "/addSkill"} ,method = RequestMethod.GET )
	public String methodNotAllowedException(HttpServletRequest request) {
		request.setAttribute("message", "Please Login First..!");
		return "login";
	}
	
		
	@ExceptionHandler(Throwable.class)
    public String handleException(Throwable t) {
        return "redirect:/403";
    }
	

    @ExceptionHandler(Exception.class)
    public String handleExceptionUnAuthorized(Throwable t) {
        return "redirect:/403";
    }    
    
        
    @RequestMapping(value = "/400")
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public String handle404(Exception  exception) {     	
        return "403";
    }
    
    
    @RequestMapping(value = "/403")    
    public String handle403(Exception  exception) {     	
        return "403";
    }
    
    
    @RequestMapping(value = "/500")    
    public String handle500(Exception  exception) {    	
        return "500";
    }
    
    
    @RequestMapping(value = "/401")    
    public String handle401(Exception  exception) {     	
        return "401";
    }  
    
    
    @ExceptionHandler(HttpSessionRequiredException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason="The session has expired")	
    public String handleSessionExpired(){		
      return "sessionTimeout";
    }
    	
}

