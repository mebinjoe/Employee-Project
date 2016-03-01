package com.app.handler;

import java.sql.SQLException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component; 
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;

import com.app.model.UserDetails;
import com.app.service.EmployeeService;
 
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
 
    @Autowired
    private EmployeeService employeeService;
 
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials(); 
        UserDetails user = new UserDetails();
        
		try {
			user = employeeService.getUserByUserName(username);
		} catch (SQLException e) {			
			e.printStackTrace();
		}
 
        if (user == null) {
            throw new BadCredentialsException("Username not found.");
        }
 
        if (!password.equals(user.getPassword())) {
            throw new BadCredentialsException("Wrong password.");
        }
 
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities(); 
        return new UsernamePasswordAuthenticationToken(user, password, authorities);
    }
 
    @Override
    public boolean supports(Class<?> arg0) {
        return true;
    }
}