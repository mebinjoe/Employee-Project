package com.app.controller;

import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.Base64;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.app.service.EmployeeService;


@RestController
@RequestMapping("/users/")
public class UserRestController {

	@Autowired
	public EmployeeService employeeService; 	
	
	@RequestMapping(value = "/{userId}", method = RequestMethod.GET,headers="Accept=application/json")
	public ModelAndView getUser(@PathVariable int userId, HttpServletRequest request, ModelAndView model) throws SQLException {		
		MappingJackson2JsonView view = new MappingJackson2JsonView();
		view.setPrettyPrint(true);
		model = new ModelAndView(view);		
        String username = getAuthenticationHeader(request);
                
        if(username.equals("admin") || username.equals("manager")) {		
        	model.addObject("employees",employeeService.getUserById(userId));
        	return model;
        }
        else {
        	model = new ModelAndView("403");
        	return model;
        }		
	}
	
	
	@RequestMapping(method = RequestMethod.GET,headers="Accept=application/json")	
	public ModelAndView getAllUsers(HttpSession session, ModelAndView model, HttpServletRequest request) throws SQLException {					 
	    MappingJackson2JsonView view = new MappingJackson2JsonView();
		view.setPrettyPrint(true);	
		model = new ModelAndView(view);		
		String username = getAuthenticationHeader(request);
	        
	    if(username.equals("admin") || username.equals("manager")) {
	    	model.addObject("employees", employeeService.getAllUsersForJson());
	    }
	    else {
	    	int userId = employeeService.getUserId(username);
	    	model.addObject("employees", employeeService.getUserById(userId));
	    }      
        return model;    	
	}	
	
	
	public String getAuthenticationHeader(HttpServletRequest request) {
		String username = null;
		final String authorization = request.getHeader("Authorization");
		if (authorization != null && authorization.startsWith("Basic")) {
			/* Authorization: Basic base64credentials */
		        String base64Credentials = authorization.substring("Basic".length()).trim();
		        String credentials = new String(Base64.getDecoder().decode(base64Credentials),Charset.forName("UTF-8"));
		    /* credentials = username:password */
		        final String[] values = credentials.split(":",2); 
		        username = values[0];
		}
		return username;
	}	
		
}