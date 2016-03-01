package com.app.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.ui.ModelMap;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import com.app.model.Message;
import com.app.model.Skills;
import com.app.model.UserDetails;
import com.app.model.Department;
import com.app.model.Inbox;
import com.app.model.UserUpdate;
import com.app.service.EmployeeService;
import com.mysql.jdbc.ResultSetMetaData;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.app.model.Role;

@Service("employeeService")
public class EmployeeServiceImpl implements EmployeeService {
	
	public DataSource dataSource;
	
	private JdbcTemplate jdbcTemplate;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;		
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
		
	@Override
	public boolean isValidUser(String username,String password) throws SQLException { 			
		String sql = "select count(*) from employee where username=? and binary password=?";
		boolean result = false;
		int count =  jdbcTemplate.queryForObject(sql, new String[]{username, password}, Integer.class);
			   
		if (count > 0) {
			result = true;
		}
		else {
			result = false;
		}
		return result;	
	}	
	
	
	public String getUserRole(String username) throws SQLException {		
		UserDetails userDetails = getUser(username);
		if(userDetails!=null) {
			return userDetails.getRole();			
		}		
		return "";
	}	
	
	
	public int getUserId(String username) throws SQLException {		
		UserDetails userDetails = getUser(username);
		if(userDetails!=null) {
			return userDetails.getUserId();			
		}		
		return 0;
	}	
	
	
	public UserDetails getUser(String username) throws SQLException {						
		UserDetails user= null;				
		String sql = "select * from employee where username=?";				
		user = jdbcTemplate.queryForObject(sql, new Object[] {username}, new BeanPropertyRowMapper<UserDetails>(UserDetails.class) );
		return user;
	}	
	
	
	public List<Department> getAllDepartments() throws SQLException {	
		List<Department> departments = new ArrayList<Department>();
		String sql = "select * from department";
		departments = jdbcTemplate.query(sql, new BeanPropertyRowMapper<Department>(Department.class));
		return departments;
	}
	
	
	public void deleteUser(int userId) throws SQLException {
		String sql = "delete from employee where userid=?";	
		jdbcTemplate.update(sql, userId);
	}	
	
	
	public List<UserDetails> getAllUsersForUser(String username) throws SQLException {
		List<UserDetails> users = new ArrayList<UserDetails>();
		String sql = "SELECT employee.userid,"
					+ "employee.firstname, "
					+ "employee.lastname,"
					+ " employee.dob,"
					+ " employee.email,"
					+ " department.deptname,"
					+ " department.deptmanager, employee.username"
					+ " from"
					+ " employee left join department on "
					+ "employee.deptid = department.deptid "
					+ "where username <> ? and role !='admin' ";
		users = jdbcTemplate.query(sql,new Object[] {username} ,new BeanPropertyRowMapper<UserDetails>(UserDetails.class));
		return users;
	}	
	
		
	public List<UserDetails> getAllUsers() throws SQLException {											
		String sql = "SELECT employee.userid,"
					+ "employee.firstname, "
					+ "employee.lastname,"
					+ " employee.dob,"
					+ " employee.email,"
					+ " department.deptname,"
					+ " department.deptmanager, employee.username"
					+ " from"
					+ " employee left join department on "
					+ "employee.deptid = department.deptid where role = 'user' ";	
		List<UserDetails> users = jdbcTemplate.query(sql, new BeanPropertyRowMapper<UserDetails>(UserDetails.class));			
		return users;
	}	
	
			
	public UserDetails getUserById(int userId) throws SQLException {
		UserDetails user = new UserDetails();
		String sql = "SELECT employee.userid, "
				+ "employee.firstname, employee.lastname, "
				+ "employee.dob, employee.email, employee.deptid,"				
				+ "employee.role, employee.username, employee.password, "
				+ "department.deptname, department.deptmanager "
				+ "from employee left join department on "
				+ "employee.deptid = department.deptid where userid = ?";
		user = jdbcTemplate.queryForObject(sql, new Object[] {userId}, new BeanPropertyRowMapper<UserDetails>(UserDetails.class) );
		return user;
	}
	
	
	public UserDetails getUserByUserName(String username) throws SQLException {			
		UserDetails user = new UserDetails();
		String sql = "SELECT employee.userid,"
					+ "employee.firstname, "
					+ "employee.lastname,"
					+ " employee.dob, employee.email, employee.role, employee.username, employee.password,"
					+ " department.deptname, department.deptmanager from"
					+ " employee left join department on "
					+ "employee.deptid = department.deptid where username= ?";
		user = jdbcTemplate.queryForObject(sql, new Object[] {username}, new BeanPropertyRowMapper<UserDetails>(UserDetails.class) );
		Role r = new Role();
		if(username.equals("admin")) {
			r.setName("ROLE_ADMIN");
		}
		else if(username.equals("manager")){
			r.setName("ROLE_MANAGER");
		}
		else {
			r.setName("ROLE_USER");
		}		
        List<Role> roles = new ArrayList<Role>();
        roles.add(r);
        user.setAuthorities(roles);
		return user;
	}
	
	
	public int addUser(UserDetails user, int deptId) throws SQLException {					
		int userId = 0;	
		Connection connection =  dataSource.getConnection();
		try {			
			PreparedStatement preparedStatement = connection.prepareStatement
					("insert into employee(firstname,lastname,dob,email,deptid,role,username,password) values (?, ?, ?, ?, ?, ?, ?, ? )",
							PreparedStatement.RETURN_GENERATED_KEYS);			
			preparedStatement.setString(1, user.getFirstName());
			preparedStatement.setString(2, user.getLastName());				
			preparedStatement.setDate(3,new java.sql.Date(user.getDob().getTime()));
			preparedStatement.setString(4, user.getEmail());
			preparedStatement.setInt(5, deptId);
			preparedStatement.setString(6, user.getRole());			
			preparedStatement.setString(7, user.getUserName());
			preparedStatement.setString(8, user.getPassword());					
			preparedStatement.executeUpdate();	
			
			ResultSet rs = preparedStatement.getGeneratedKeys();			
			if (rs.next()){
				userId=rs.getInt(1);				
			}	
			
		} catch (SQLException e) {
			e.printStackTrace();			
			JDialog dialog = new JOptionPane("User already exists.!!!",JOptionPane.ERROR_MESSAGE).createDialog("Error"); 
	        dialog.setAlwaysOnTop(true);
	        dialog.setVisible(true);
	        dialog.dispose();
	  }	finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {}
			}
		}	
		return userId;
	} 
	
	
	public void addUserFromFile(String text) throws SQLException {		
		String[] columnName= text.split(" ");
		Connection connection =  dataSource.getConnection();
		try {	
			int deptId;
			PreparedStatement preparedStatement = connection.prepareStatement("select deptid from department where deptname = ? ");
			String deptName = columnName[4];
			preparedStatement.setString(1, deptName);
			ResultSet rs = preparedStatement.executeQuery();	
			while (rs.next()) {		
				deptId = rs.getInt("deptid");		
				preparedStatement = connection.prepareStatement
					("insert into employee(firstname,lastname,dob,email,deptid,role,username,password) values (?, ?, ?, ?, ?, ?, ?, ? )");								
				preparedStatement.setString(1, columnName[0]);
				preparedStatement.setString(2, columnName[1]);				
				preparedStatement.setString(3, columnName[2]);
				preparedStatement.setString(4, columnName[3]);
				preparedStatement.setInt(5, deptId);
				preparedStatement.setString(6, columnName[6]);			
				preparedStatement.setString(7, columnName[7]);
				preparedStatement.setString(8, columnName[8]);					
				preparedStatement.executeUpdate();
			}			
		} catch (SQLException e) {
			e.printStackTrace();				
	}finally {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {}
		}
	}		
	} 
	
		
	public UserDetails updateUser(UserDetails user) throws SQLException {  // UserUpdate userUpdate,	
		Connection connection =  dataSource.getConnection();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement
					("delete from emp_update where userid=?");			
			preparedStatement.setInt(1, user.getUserId());
			preparedStatement.executeUpdate();	
		} catch (SQLException e) {
				e.printStackTrace();
		}		
		
		try {
			PreparedStatement preparedStatement = connection.prepareStatement
					("delete from inbox where (userid = ? and isRejected = 1 and privilege = 'forUser' and type = 'isUserInfo')");			
			preparedStatement.setInt(1, user.getUserId());
			preparedStatement.executeUpdate();	
		} catch (SQLException e) {
				e.printStackTrace();
		}		
				
		try {
			PreparedStatement preparedStatement = connection.prepareStatement
					("insert into emp_update(userid,firstname,lastname,dob,email,deptid,role,username,password) values ( ?, ?, ?, ?, ?, ?, ?, ?, ? )");
			preparedStatement.setInt(1, user.getUserId());
			preparedStatement.setString(2, user.getFirstName());
			preparedStatement.setString(3, user.getLastName());				
			preparedStatement.setDate(4,new java.sql.Date(user.getDob().getTime()));
			preparedStatement.setString(5, user.getEmail());
			preparedStatement.setInt(6, user.getDeptId());
			preparedStatement.setString(7, user.getRole());			
			preparedStatement.setString(8, user.getUserName());
			preparedStatement.setString(9, user.getPassword());					
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
	  }finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {}
			}
		}
		return user;		
	}
	
	
	public UserUpdate getUserByIdForUpdate(int userId) throws SQLException {
		Connection connection =  dataSource.getConnection();
		UserUpdate userUpdate = new UserUpdate();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("SELECT emp_update.emp_update_id, "
					+ "emp_update.userid, emp_update.firstname, emp_update.lastname, "
					+ "emp_update.dob, emp_update.email, emp_update.deptid,	emp_update.role, emp_update.username, "
					+ "emp_update.password, department.deptname "
					+ "from emp_update left join department on "
					+ "	emp_update.deptid = department.deptid where userid = ?");
			preparedStatement.setInt(1, userId);
			ResultSet rs = preparedStatement.executeQuery();			
			if (rs.next()) {				
				userUpdate.setUserId(rs.getInt("userid"));
				userUpdate.setFirstName(rs.getString("firstname"));
				userUpdate.setLastName(rs.getString("lastname"));
				userUpdate.setDob(rs.getDate("dob"));
				userUpdate.setEmail(rs.getString("email"));	
				userUpdate.setDeptId(rs.getInt("deptid"));
				userUpdate.setDeptName(rs.getString("deptname"));
				userUpdate.setUserName(rs.getString("username"));
				userUpdate.setPassword(rs.getString("password"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
	  }finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {}
			}
		}
		return userUpdate;
	}
	
		
	public UserUpdate addUserAfterUpdate(UserUpdate userUpdate) throws SQLException {	
		Connection connection =  dataSource.getConnection();
			try {
				PreparedStatement preparedStatement = connection.prepareStatement
						("UPDATE employee JOIN emp_update ON emp_update.emp_update_id "
								+ "SET   employee.firstname = ? ,"
								+ "employee.lastname = ? ,"							
								+ "employee.email = ? ,"
								+ "employee.username = ? ,"
								+ "employee.password = ? ,"
								+ "employee.deptid = ?"													
								+ " where employee.userid = ?");				
				preparedStatement.setString(1, userUpdate.getFirstName());
				preparedStatement.setString(2, userUpdate.getLastName());
				preparedStatement.setString(3, userUpdate.getEmail());	
				preparedStatement.setString(4, userUpdate.getUserName());
				preparedStatement.setString(5, userUpdate.getPassword());
				preparedStatement.setInt(6, userUpdate.getDeptId());
				preparedStatement.setInt(7, userUpdate.getUserId());
				preparedStatement.executeUpdate();
			} catch (SQLException e) {
			  e.printStackTrace();
		 }finally {
				if (connection != null) {
					try {
						connection.close();
					} catch (SQLException e) {}
				}
			}
			return userUpdate;			
	}
	
	
	public UserDetails addUserAfterUpdateByManager(UserDetails user) throws SQLException {	
		Connection connection =  dataSource.getConnection();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement
					("UPDATE employee JOIN emp_update ON emp_update.emp_update_id "
							+ "SET   employee.firstname = ? ,"
							+ "employee.lastname = ? ,"							
							+ "employee.email = ? ,"
							+ "employee.username = ? ,"
							+ "employee.password = ? ,"
							+ "employee.deptid = ?"													
							+ " where employee.userid = ?");				
			preparedStatement.setString(1, user.getFirstName());
			preparedStatement.setString(2, user.getLastName());
			preparedStatement.setString(3, user.getEmail());	
			preparedStatement.setString(4, user.getUserName());
			preparedStatement.setString(5, user.getPassword());
			preparedStatement.setInt(6, user.getDeptId());
			preparedStatement.setInt(7, user.getUserId());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
		  e.printStackTrace();
	 }finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {}
			}
		}
		return user;			
	}
	
	
	public int addSkill(Skills skill) throws SQLException {
		Connection connection =  dataSource.getConnection();
		int skillId=0;
		try {
			PreparedStatement preparedStatement = connection.prepareStatement
					("insert into skills(userid,skillname,level,experience,approved) values (?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);	
			preparedStatement.setInt(1,skill.getUserId());
			preparedStatement.setString(2,skill.getSkillName());
			preparedStatement.setString(3, skill.getLevel());
			preparedStatement.setDouble(4,skill.getExperience());
			preparedStatement.setInt(5,skill.getApproved());						
			preparedStatement.executeUpdate();	
			
			ResultSet rs = preparedStatement.getGeneratedKeys();
			if (rs.next()){
				skillId=rs.getInt(1);
			}
			
		} catch (SQLException e) {
				e.printStackTrace();
		}
		
		try {
			PreparedStatement preparedStatement = connection.prepareStatement
					("delete from skills where flag = 1 and userid=?");			
			preparedStatement.setInt(1, skill.getUserId());
			preparedStatement.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
		}finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {}
			}
		}
		return skillId;
	}
	
	
	public void approveSkill(int skillId) throws SQLException {	
		String sql = "update skills set approved=1 where skillid = ?";	
		jdbcTemplate.update(sql, skillId);
	}	
	
	
	public Skills getSkillById(int skillId) throws SQLException {
		Connection connection =  dataSource.getConnection();
		Skills skill = null;
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("select * from skills where skillid=?");
			preparedStatement.setInt(1, skillId);
			ResultSet rs = preparedStatement.executeQuery();			
			if (rs.next()) {	
				skill = new Skills();				
				skill.setSkillId(rs.getInt("skillid"));
				skill.setUserId(rs.getInt("userid"));
				skill.setSkillName(rs.getString("skillName"));
				skill.setLevel(rs.getString("level"));
				skill.setExperience(rs.getDouble("experience"));
				skill.setApproved(rs.getInt("approved"));
			}
		}catch (SQLException e) {
			e.printStackTrace();
	   }finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {}
			}
		}	
		return skill;
   }
	
	
	public Skills getRejectedSkillById(int skillId) throws SQLException {	
		Skills skill = null;
		String sql = "select * from skills_update where skillid=?";
		skill = jdbcTemplate.queryForObject(sql, new Object[] {skillId}, new BeanPropertyRowMapper<Skills>(Skills.class));
		return skill;
   }
	
			
	public List<Skills> getAllSkillsOfUsers() throws SQLException {
		List<Skills> skills = new ArrayList<Skills>();
		String sql = "SELECT skills.skillid, skills.userid, employee.firstname, "
					+ "skills.skillname, skills.level, skills.experience, skills.approved from "
					+ "skills left join employee on skills.userid = employee.userid where approved = 1 ORDER BY employee.firstname";
		 skills = jdbcTemplate.query(sql, new BeanPropertyRowMapper<Skills>(Skills.class));	
		 return skills;
	}
	
	
	public List<Skills> getSkillsToApprove(int userId) throws SQLException {
		List<Skills> skills = new ArrayList<Skills>();
		String sql = "SELECT skills.skillid, skills.userid, employee.firstname,"
					+ "skills.skillname, skills.level, skills.experience, skills.approved, inbox.inboxid from "
					+ "skills left join employee on skills.userid = employee.userid left join inbox on "
					+ "skills.userid = inbox.userid where approved = 0 and flag = 0 and status = 0 and type= 'skillRequest' and skills.userid = ?"; 
		skills = jdbcTemplate.query(sql,new Object[] {userId} , new BeanPropertyRowMapper<Skills>(Skills.class));				
		return skills;
   }	
	
			
	public List<Skills> getApprovedSkillsOfUser(int userId) throws SQLException {	
		List <Skills> skills = new ArrayList<Skills>();	
		String sql = "SELECT skills.skillid, skills.userid, skills.skillname, employee.firstname, skills.level,"
						+ "skills.experience from skills left join employee on skills.userid = employee.userid where "
						+ "skills.userid= ? and skills.approved = 1 ";
		skills = jdbcTemplate.query(sql,new Object[] {userId} , new BeanPropertyRowMapper<Skills>(Skills.class));
		return skills;	
	}
	
	
	public void rejectSkill(int skillId) throws SQLException {	
		String sql = "update skills set flag = 1 where skillid = ?";
		jdbcTemplate.update(sql, skillId);
	}
	
	
	public void deleteSkill(int skillId) throws SQLException {
		String sql = "delete from skills where skillid=?";
		jdbcTemplate.update(sql, skillId);
	}
	

	public int addToInbox(int userId, String message) throws SQLException {
		Connection connection =  dataSource.getConnection();
		Date date = new Date();
		java.text.SimpleDateFormat sdf =     new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentTime = sdf.format(date);  	  
		int inboxId = 0;
		try {
			PreparedStatement preparedStatement = connection.prepareStatement
					("insert into inbox(userid, message, requested_time) values ( ?, ?, ?)",PreparedStatement.RETURN_GENERATED_KEYS);				
			preparedStatement.setInt(1,userId);
			preparedStatement.setString(2, message);
			preparedStatement.setString(3, currentTime);
			preparedStatement.executeUpdate();
			
			ResultSet rs = preparedStatement.getGeneratedKeys();
			if (rs.next()){
				inboxId=rs.getInt(1);
			}
		} catch (SQLException e) {
				e.printStackTrace();
	  }
		try {
			PreparedStatement preparedStatement = connection.prepareStatement
					("delete from inbox where status = 1 and type = 'skillRequest' and userid= ?");				
			preparedStatement.setInt(1,userId);			
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
	 }finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {}
			}
		}
		return inboxId;
   }	
	
	
	public void addToInboxAsSkill(int inboxId, int skillId) throws SQLException {
		if(skillId != 0) {
			String sql = "insert into skillmap(inboxid, skillid) values ( ?, ?)";	
			jdbcTemplate.update(sql, inboxId ,skillId);
	  }
	}
	

	public List<Inbox> getInboxMessage() throws SQLException {	//int userId	
		Connection connection =  dataSource.getConnection();
		List<Inbox> inList = new ArrayList<Inbox>();
		try {
			PreparedStatement statement = connection.prepareStatement 
					("select inbox.inboxid, inbox.userid, inbox.message, inbox.status, "
							+ "inbox.requested_time, inbox.isRejected, inbox.privilege,"
							+ "inbox.type, skillmap.skillid from inbox "
							+ "left join skillmap on inbox.inboxid=skillmap.inboxid	"
							+ "where status = 0 and (type = 'UserRequest' or type = 'skillRequest' ) "); 			
			ResultSet rs = statement.executeQuery();			
			while (rs.next()) {	
				Inbox inbox= new Inbox();			
				inbox.setInboxId(rs.getInt("inboxid"));
				inbox.setUserId(rs.getInt("userid"));
				inbox.setMessage(rs.getString("message"));					
				inbox.setStatus(rs.getInt("status"));
				inbox.setRequestedTime(rs.getString("requested_time"));
				inbox.setIsRejected(rs.getInt("isRejected"));
				inbox.setPrivilege(rs.getString("privilege"));
				inbox.setType(rs.getString("type"));
				inbox.setSkillId(rs.getInt("skillid"));
				inList.add(inbox);					
			}	
		} catch (SQLException e) {
		e.printStackTrace();
	 }finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {}
			}
	 }
		return inList;
	}
	
	
	public List<Inbox> getInboxMessageForUser(int userId) throws SQLException {	//int userId	
		Connection connection =  dataSource.getConnection();
		List<Inbox> inList = new ArrayList<Inbox>();
		try {
			PreparedStatement statement = connection.prepareStatement 
					("select inbox.inboxid, inbox.userid, inbox.message, "
							+ "inbox.status, inbox.isRejected, inbox.privilege, inbox.type, "
							+ "skillmap.skillid from inbox left join skillmap on inbox.inboxid=skillmap.inboxid "
							+ "where inbox.status = 1 and inbox.isRejected = 1 "
							+ "and ( inbox.privilege = 'forAdminAndUser' or inbox.privilege = 'forUser' or inbox.privilege = 'expired' ) and userid = ? "); 
			statement.setInt(1,userId);
			ResultSet rs = statement.executeQuery();			
			while (rs.next()) {	
				Inbox inbox= new Inbox();
				inbox.setUserId(rs.getInt("userId"));
				inbox.setInboxId(rs.getInt("inboxid"));
				inbox.setUserId(rs.getInt("userid"));
				inbox.setMessage(rs.getString("message"));					
				inbox.setStatus(rs.getInt("status"));
				inbox.setIsRejected(rs.getInt("isRejected"));
				inbox.setPrivilege(rs.getString("privilege"));
				inbox.setType(rs.getString("type"));
				inbox.setSkillId(rs.getInt("skillid"));
				inList.add(inbox);					
			}	
		} catch (SQLException e) {
		e.printStackTrace();
	 }finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {}
			}
	 }	
		return inList;
	}
	
	
	public List<Inbox> getUserRequests() throws SQLException {		
		Connection connection =  dataSource.getConnection();
		List<Inbox> inList = new ArrayList<Inbox>();
		try {
			PreparedStatement statement = connection.prepareStatement 
					("select inbox.inboxid, inbox.userid, inbox.message, inbox.status, "
							+ "inbox.requested_time, inbox.isRejected, inbox.privilege, "
							+ "inbox.type, skillmap.skillid from inbox "
							+ "left join skillmap on inbox.inboxid=skillmap.inboxid "
							+ "where status = 0 and (type = 'UserRequest' or type = 'skillRequest' )"); 
			ResultSet rs = statement.executeQuery();			
			while (rs.next()) {	
				Inbox inbox= new Inbox();
				inbox.setInboxId(rs.getInt("inboxid"));
				inbox.setUserId(rs.getInt("userid"));
				inbox.setMessage(rs.getString("message"));				
				inbox.setStatus(rs.getInt("status"));
				inbox.setIsRejected(rs.getInt("isRejected"));
				inbox.setType(rs.getString("type"));
				inbox.setRequestedTime(rs.getString("requested_time"));
				inbox.setSkillId(rs.getInt("skillid"));
				inList.add(inbox);					
			}	
		} catch (SQLException e) {
		  e.printStackTrace();
	}finally {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {}
		}
		}	
		return inList;
	}
	
	
	public List<Inbox> getUserSkillRequests() throws SQLException {			
		List<Inbox> inList = new ArrayList<Inbox>();
		String sql = "select inbox.inboxid, inbox.userid, inbox.message, inbox.status, "
							+ "inbox.requested_time, inbox.isRejected, inbox.privilege, "
							+ "inbox.type, skillmap.skillid from inbox "
							+ "left join skillmap on inbox.inboxid=skillmap.inboxid "
							+ "where status = 0 and (type = 'skillRequest' )"; 
		inList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<Inbox>(Inbox.class));			
		return inList;
	}
	
	
	public List<Inbox> getUserEditRequests() throws SQLException {	
		List<Inbox> inList = new ArrayList<Inbox>();
		String sql = "select inbox.inboxid, inbox.userid, inbox.message, inbox.type, inbox.status, "
							+ "inbox.requested_time, inbox.isRejected, inbox.privilege,inbox.type "
							+ "from inbox left join skillmap on inbox.inboxid=skillmap.inboxid "
							+ "where status = 0 and (type = 'userRequest')"; 
		inList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<Inbox>(Inbox.class));
		return inList;
	}
	
	
	public List<Inbox> getNotifications() throws SQLException {
		List<Inbox> inList = new ArrayList<Inbox>();
		String sql = "select * from inbox where ( privilege = 'forAdminAndUser' or privilege = 'forAdmin') "; 
		inList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<Inbox>(Inbox.class));
		return inList;
	}
	
	
	public List<Inbox> getNotificationsForUser(int userId) throws SQLException {	
		Connection connection =  dataSource.getConnection();
		List<Inbox> inList = new ArrayList<Inbox>();
		try {
			PreparedStatement statement = connection.prepareStatement 
					("select * from inbox where ( privilege = 'forAdminAndUser' or privilege = 'forAdmin') and userid = ?"); 
			statement.setInt(1,userId);
			ResultSet rs = statement.executeQuery();			
			while (rs.next()) {	
				Inbox inbox= new Inbox();
				inbox.setInboxId(rs.getInt("inboxid"));
				inbox.setUserId(rs.getInt("userid"));
				inbox.setMessage(rs.getString("message"));				
				inbox.setStatus(rs.getInt("status"));
				inbox.setIsRejected(rs.getInt("isRejected"));
				inList.add(inbox);					
			}	
		} catch (SQLException e) {
		  e.printStackTrace();
	}finally {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {}
		}
	}
	return inList;
	}
	
	
	public void addSkillToCloneTable(int skillId) throws SQLException {
		Connection connection =  dataSource.getConnection();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS skills_update SELECT * FROM skills LIMIT 0");
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			PreparedStatement preparedStatement = connection.prepareStatement
					("INSERT INTO skills_update (skillid, userid,skillname, level, experience, approved, flag) "
							+ "SELECT skillid, userid,skillname, level, experience, approved, flag "
							+ "FROM skills where skillid= ?");
			preparedStatement.setInt(1, skillId);							
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
	  }finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {}
			}
		}
		
	}
	
	
	public void isRejected(int inboxId) throws SQLException {	
		String sql = "update inbox set isRejected=1 where inboxid = ?";	
		jdbcTemplate.update(sql, inboxId);
	}
	
	
	public void isRead(int inboxId) throws SQLException {
		String sql = "update inbox set status=1 where inboxid = ?";	
		jdbcTemplate.update(sql, inboxId);
	}
	
	
	public void isRequestedForUserUpdate(int inboxId) throws SQLException {
		String sql = "update inbox set type = 'userRequest' where inboxid = ?";
		jdbcTemplate.update(sql, inboxId);
	}
	
	
	public void isRequestedForSkillUpdate(int inboxId) throws SQLException {
		String sql = "update inbox set type = 'skillRequest' where inboxid = ?";	
		jdbcTemplate.update(sql, inboxId);
	}
	
			
	public void isAccepted(int inboxId) throws SQLException {
		String sql = "update inbox set status = 1, privilege = 'forAdminAndUser', type = 'isUserInfo' where inboxid = ?";
		jdbcTemplate.update(sql, inboxId);
	}
	
	
	public void isAcceptedForAdminSkill(int inboxId) throws SQLException {
		String sql = "update inbox set status = 1, privilege = 'forAdmin', type = 'isSkill' where inboxid = ?";	
		jdbcTemplate.update(sql, inboxId);
	}
	
	
	public void isAcceptedForAdminExpiredRequest(int inboxId) throws SQLException {	
		String sql = "update inbox set status = 1, isRejected = 1, privilege = 'forAdmin' where inboxid = ?";	
		jdbcTemplate.update(sql, inboxId);
	}
	
	
	public void isAcceptedForAdminEditRequest(int inboxId) throws SQLException {
		String sql = "update inbox set status = 1, privilege = 'forAdmin', type = 'isUserInfo' where inboxid = ?";
		jdbcTemplate.update(sql, inboxId);
	}
	
	
	public void isAcceptedForUserSkill(int inboxId) throws SQLException {
		String sql = "update inbox set status = 1, privilege = 'forUser', type = 'isSkill' where inboxid = ?";
		jdbcTemplate.update(sql, inboxId);
	}
	

	public void isAcceptedForUserEditRequest(int inboxId) throws SQLException {				
		String sql = "update inbox set status = 1, privilege = 'forUser', type = 'isUserInfo' where inboxid = ?";
		jdbcTemplate.update(sql, inboxId);
	}
	
	
	public void deleteRejectedSkillStatusOfUser(int userId) throws SQLException {	
		String sql = "delete  from inbox where userid = ? and status = 1 and isRejected = 1 and privilege = 'forUser' and type = 'isSkill'";
		jdbcTemplate.update(sql, userId);
	}

	
	public List<Inbox> getReadMessage() throws SQLException {
		List<Inbox> readMsg = new ArrayList<Inbox>();
		String sql = "select * from inbox where status = 1 ";
		readMsg = jdbcTemplate.query(sql, new BeanPropertyRowMapper<Inbox>(Inbox.class));
		return readMsg;
	}
	
	public List<UserDetails> getAllUsersForJson() throws SQLException {											
		String sql = "SELECT employee.userid,"
				+ "employee.firstname,"
				+ "employee.lastname,"
				+ "employee.dob,"
				+ "employee.email,"
				+ "employee.username,"
				+ "employee.password,"
				+ "employee.role,"
				+ "department.deptid,"
				+ "department.deptname,"
				+ "department.deptmanager"
				+ " from "
				+ "employee left join department on "
				+ "employee.deptid = department.deptid where role = 'user'" ;	
		List<UserDetails> users = jdbcTemplate.query(sql, new BeanPropertyRowMapper<UserDetails>(UserDetails.class));			
		return users;
	}	
	
	
	public List<Inbox> getTotalRequestCount() throws SQLException {	
		Connection connection =  dataSource.getConnection();
		List<Inbox> noOfRequests = new ArrayList<Inbox>();	
		try{
			PreparedStatement statement = connection.prepareStatement 
					("SELECT COUNT(DISTINCT requested_time) "
							+ "FROM    inbox where (type = 'userRequest' or type='skillRequest') "
							+ "GROUP   BY  DATE(requested_time) "); 
			ResultSet rs = statement.executeQuery();			
			while (rs.next()) {	
				Inbox inbox= new Inbox();
				inbox.setCount(rs.getInt(1));				
				noOfRequests.add(inbox);			
	        }
			 
		} catch (SQLException e) {
		  e.printStackTrace();
	 }finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {}
			}
		}
	return noOfRequests;	
	}
	
				
	public List<Inbox> getRequestCountForUser(int userId) throws SQLException {	
		Connection connection =  dataSource.getConnection();
		List<Inbox> noOfRequests = new ArrayList<Inbox>();		
		try{
			PreparedStatement statement = connection.prepareStatement 
					("SELECT COUNT(DISTINCT requested_time) "
							+ "FROM    inbox where (type = 'userRequest' or type='skillRequest') and userid = ? "
							+ "GROUP   BY  DATE(requested_time) "); 
			statement.setInt(1, userId);
			ResultSet rs = statement.executeQuery();			
			if (rs.next()) {	
				Inbox inbox= new Inbox();
				inbox.setCount(rs.getInt(1));				
				noOfRequests.add(inbox);			
	        }
						 
		} catch (SQLException e) {
		  e.printStackTrace();
	 }finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {}
			}
		}
	return noOfRequests;
	
	}	
	
	
	public void isExpired(int inboxId) throws SQLException {
		String sql = "update inbox set status = 1, isRejected = 1, privilege = 'expired' where inboxid = ?";
		jdbcTemplate.update(sql, inboxId);
	}
	

	public List<UserDetails> getUsersBySearch(String searchText, String searchType) throws SQLException {	
		Connection connection =  dataSource.getConnection();
		List<UserDetails> users = new ArrayList<UserDetails>();
		try {								
			PreparedStatement preparedStatement = connection.prepareStatement("SELECT employee.userid,"
					+ "employee.firstname, "
					+ "employee.lastname,"
					+ " employee.dob,"
					+ " employee.email,"
					+ " department.deptname,"
					+ " department.deptmanager, employee.username"
					+ " from"
					+ " employee left join department on "
					+ "employee.deptid = department.deptid where role = 'user' "
					+ "and (firstname LIKE ? or lastname LIKE ? or email LIKE ? or deptname LIKE ? or deptmanager LIKE ?)");	
			
			switch(searchType){
				case "1":
					preparedStatement.setString(1, "%"+searchText+"%");
					preparedStatement.setString(2, "''");
					preparedStatement.setString(3, "''");
					preparedStatement.setString(4, "''");
					preparedStatement.setString(5, "''");
				break;
				
				case "2":
					preparedStatement.setString(1, "''");
					preparedStatement.setString(2, "%"+searchText+"%");
					preparedStatement.setString(3, "''");
					preparedStatement.setString(4, "''");
					preparedStatement.setString(5, "''");
				break;
				
				case "3":
					preparedStatement.setString(1, "''");
					preparedStatement.setString(2, "''");
					preparedStatement.setString(3, "%"+searchText+"%");
					preparedStatement.setString(4, "''");
					preparedStatement.setString(5, "''");
				break;
				
				case "4":
					preparedStatement.setString(1, "''");
					preparedStatement.setString(2, "''");					
					preparedStatement.setString(3, "''");
					preparedStatement.setString(4, "%"+searchText+"%");
					preparedStatement.setString(5, "''");
				break;
				
				case "5":
					preparedStatement.setString(1, "''");
					preparedStatement.setString(2, "''");					
					preparedStatement.setString(3, "''");
					preparedStatement.setString(4, "''");
					preparedStatement.setString(5, "%"+searchText+"%");
				break;
			
			default:
				preparedStatement.setString(1, "''");
				preparedStatement.setString(2, "''");
				preparedStatement.setString(3, "''");
				preparedStatement.setString(4, "''");
				preparedStatement.setString(5, "''");
				break;
			}
			
			ResultSet rs=preparedStatement.executeQuery();				
			while (rs.next()) {				
					UserDetails user = new UserDetails();
					user.setUserId(rs.getInt("userid"));
					user.setFirstName(rs.getString("firstname"));
					user.setLastName(rs.getString("lastname"));
					user.setDob(rs.getDate("dob"));
					user.setEmail(rs.getString("email"));					
					user.setDeptName(rs.getString("deptname"));
					user.setDeptManager(rs.getString("deptmanager"));	
					user.setUserName(rs.getString("username"));
					users.add(user);
				}			
			} catch (SQLException e) {
			  e.printStackTrace();
		}finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {}
			}
		}
	  return users;
	}	
	
	
	public List<Skills> getSkillsBySearch(String searchText, String searchType) throws SQLException {
		Connection connection =  dataSource.getConnection();
		List<Skills> skills = new ArrayList<Skills>();
		try {								
			PreparedStatement preparedStatement = connection.prepareStatement("SELECT skills.skillid,skills.userid,"
					+ "skills.skillname,skills.level,skills.experience,employee.firstname from "
					+ "skills left join employee on	skills.userid = employee.userid where approved = 1 "
					+ "and (skillname LIKE ?)");	
			
			switch(searchType){
				case "skill":
					preparedStatement.setString(1, "%"+searchText+"%");				
					break;
				default:
					preparedStatement.setString(1, "''");					
					break;
			}
			ResultSet rs=preparedStatement.executeQuery();				
			while (rs.next()) {					
					Skills skill = new Skills();					
					skill.setSkillId(rs.getInt("skillid"));	
					skill.setUserId(rs.getInt("userid"));
					skill.setFirstName(rs.getString("firstname"));
					skill.setSkillName(rs.getString("skillname"));
					skill.setLevel(rs.getString("level"));
					skill.setExperience(rs.getFloat("experience"));				
					skills.add(skill);	
				}			
			} catch (SQLException e) {
			  e.printStackTrace();
		}finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {}
			}
		}
	return skills;
	}
	

	public void deleteUserClone(int userId) throws SQLException {
		String sql = "delete from emp_update where userid=?";
		jdbcTemplate.update(sql, userId);
	}
	
	
	public void dropSkillClone() throws SQLException {
		String sql = "DROP TABLE IF EXISTS skills_update";	
		jdbcTemplate.update(sql);
	}
	
	
	public UserDetails getUserInfoFromRequest(HttpServletRequest request) throws SQLException {	
		Connection connection =  dataSource.getConnection();
		UserDetails user = new UserDetails();  		
			if(!request.getParameter("userId").isEmpty()) {
				int userId = Integer.parseInt(request.getParameter("userId"));
				if(userId > 0) {
					user.setUserId((userId));
				}
			}
			   try {
				   user.setFirstName(request.getParameter("firstName"));    
				   user.setLastName(request.getParameter("lastName"));
				   user.setRole(request.getParameter("role"));
				   
				   if(request.getParameter("userId").isEmpty()) { // get date of birth for new user
						String startDate = request.getParameter("dob");
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
						Date tempDate = simpleDateFormat.parse(startDate);
						user.setDob(tempDate);
				   }
				   else { // get date of birth for existing user
					   String startDate = request.getParameter("dob");
					   SimpleDateFormat readFormat = new SimpleDateFormat("dd-MM-yyyy");
					   Date date = null;
					   try {
					     date = readFormat.parse( startDate );
					   } catch ( ParseException e ) {
					         e.printStackTrace();
					   }
					   user.setDob(date);
				   }
			       user.setEmail(request.getParameter("email"));        
			       user.setUserName(request.getParameter("username"));
			       user.setPassword(request.getParameter("password")); 			       
			       
			       String[] dept = request.getParameterValues("deptId");
			       
			       if(request.getParameter("userId").isEmpty()) {  //get deptId for new user
			    	   if (dept != null) {
			    		   for(String item : dept){
			    			   String keyValue[]= item.split(":");		               
			    			   user.setDeptId(Integer.parseInt(keyValue[0]));			    			   
			    		   }
			    	   }
			       	}
			       else {		//get deptId and deptName for existing user
			    	   if (dept != null) {
			    		   for(String item : dept){
			    			   String keyValue[]= item.split(":");		               
			    			   user.setDeptId(Integer.parseInt(keyValue[0]));
			    			   user.setDeptName(keyValue[1]);
			    		   }
			    	   }
			    	   
			       }
			       
			   } catch (Exception e){
		        	e.printStackTrace();
		       }finally {
					if (connection != null) {
						try {
							connection.close();
						} catch (SQLException e) {}
					}
				}
	return user;
    }
	
		
	public Skills getSkillForUser(HttpServletRequest request, String role, int userID) {
		Skills skill = new Skills();
		try { 
			if(role.equals("manager")) {
				skill.setUserId(Integer.parseInt(request.getParameter("Id")));
			}
			else {
				int userId = userID; 				
				skill.setUserId(userId);
			}
			skill.setSkillName(request.getParameter("skillname"));
			skill.setLevel(request.getParameter("level"));		
			try{   			
				skill.setExperience(Double.parseDouble(request.getParameter("experience")));    			
				skill.setApproved(0);
				} catch (Exception e) {      
					e.printStackTrace();
		 			}  		
  			} catch (Exception e) {      
			e.printStackTrace();
  			}        
	    
	return skill;
	} 
	
	
	public Skills getSkillForUserFromRequest(HttpServletRequest request) throws SQLException {
		Skills skill = new Skills();
		try { 
				skill.setUserId(Integer.parseInt(request.getParameter("userID")));									
				skill.setSkillName(request.getParameter("skillName"));
				skill.setLevel(request.getParameter("level"));					 			
				skill.setExperience(Double.parseDouble(request.getParameter("experience")));    			
				skill.setApproved(0);
				 		
  			} catch (Exception e) {      
			e.printStackTrace();
  			}        
	    
	return skill;
	} 
	
	
	public void setSkillFlag(int skillId) throws SQLException {	
		String sql = "update skills set flag = 1 where skillid = ?";
		jdbcTemplate.update(sql, skillId);
	}
	
	
	public void exportDataToCSV() throws SQLException {	
		Connection connection =  dataSource.getConnection();
		String filename = "C:\\Users\\mebin.j\\Desktop\\Employee_Details.csv";
		try {
            FileWriter fw = new FileWriter(filename);   
            Statement statement = connection.createStatement();	
            
			ResultSet rs = (ResultSet) statement.executeQuery("SELECT employee.userid, employee.firstname,"
					+ "	employee.lastname, employee.dob, employee.email, department.deptname,"
					+ "	department.deptmanager, employee.role,  employee.username, employee.password from "
					+ "employee left join department on	employee.deptid = department.deptid where role = 'user'	");
			ResultSetMetaData meta = (ResultSetMetaData) rs.getMetaData();
			
			fw.append(meta.getColumnLabel(1));
			fw.append(',');
            fw.append(' ');
            fw.append(meta.getColumnLabel(2));
			fw.append(',');
            fw.append(' ');
            fw.append(meta.getColumnLabel(3));
			fw.append(',');
            fw.append(' ');
            fw.append(meta.getColumnLabel(4));
			fw.append(',');
            fw.append(' ');
            fw.append(meta.getColumnLabel(5));
			fw.append(',');
            fw.append(' ');
            fw.append(meta.getColumnLabel(6));
			fw.append(',');
            fw.append(' ');
            fw.append(meta.getColumnLabel(7));
			fw.append(',');
            fw.append(' ');
            fw.append(meta.getColumnLabel(8));
			fw.append(',');
            fw.append(' ');
            fw.append(meta.getColumnLabel(9));
			fw.append(',');
            fw.append(' ');
            fw.append(meta.getColumnLabel(10));			
            fw.append('\n');
            fw.append('\n');			
			
            while (rs.next()) {
                fw.append(rs.getString(1));
                fw.append(',');
                fw.append(' ');
                fw.append(rs.getString(2));
                fw.append(',');
                fw.append(' ');
                fw.append(rs.getString(3));
                fw.append(',');
                fw.append(' ');
                fw.append(rs.getString(4));
                fw.append(',');
                fw.append(' ');
                fw.append(rs.getString(5));
                fw.append(',');
                fw.append(' ');
                fw.append(rs.getString(6));
                fw.append(',');
                fw.append(' ');
                fw.append(rs.getString(7));
                fw.append(',');
                fw.append(' ');
                fw.append(rs.getString(8));
                fw.append(',');
                fw.append(' ');
                fw.append(rs.getString(9));
                fw.append(',');
                fw.append(' ');
                fw.append(rs.getString(10));               
                fw.append('\n');
            }
            fw.flush();
            fw.close();
            connection.close();
            System.out.println("CSV File is created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {}
			}
		}			
	}
	
	
	public void markEditedField(ModelMap modelMap) throws SQLException {		
		List<Inbox> userRequestList = new ArrayList<>();
	  	userRequestList = getUserEditRequests();
	  	List<Message> messageList = new ArrayList<Message>();
	  	
	  	for (int i = 0; i < userRequestList.size(); i++) {
	 
			String userRequest = userRequestList.get(i).getMessage();  
			String type = userRequestList.get(i).getType(); 
			int inboxId = userRequestList.get(i).getInboxId();
			int userId = userRequestList.get(i).getUserId();
			
			String[] words = userRequest.split(" "); 			
			String str1= words[0] + " " + words[1] + " " + words[2] + " " + words[3]+ " " + words[4] + " " + words[5] + " " ;     								
			String str2= words[6];
			String str3= " " +words[7]+" " +words[8] + " " +words[9]; 
			
			Message msg =new Message();  	
				msg.setStr1(str1);
				msg.setStr2(str2);
				msg.setStr3(str3);
				msg.setInboxId(inboxId);
				msg.setUserId(userId);
				msg.setType(type);
				messageList.add(msg);      							
		} 
	  	modelMap.addAttribute("requests", getUserSkillRequests());
	  	modelMap.addAttribute("messageList", messageList);  
	}
	
	
	public void compareUserObjects(UserDetails user,UserDetails updatedUser) throws SQLException{
		Inbox inbox = new Inbox();
		int inboxId;
		if (!(updatedUser.getFirstName()).equals(user.getFirstName())) {
			inbox.setMessage("Change of FirstName from " +updatedUser.getFirstName() +" to " +user.getFirstName() +" for user " +updatedUser.getFirstName());
			inboxId= addToInbox(user.getUserId(), inbox.getMessage());
			isRequestedForUserUpdate(inboxId);
		}
		if (!(updatedUser.getLastName()).equals(user.getLastName())) {
			inbox.setMessage("Change of LastName from " +updatedUser.getLastName() +" to " +user.getLastName() +" for user " +updatedUser.getFirstName());
			inboxId=addToInbox(user.getUserId(), inbox.getMessage());
			isRequestedForUserUpdate(inboxId);
		}
		if	(!(updatedUser.getEmail()).equals(user.getEmail())) {
			inbox.setMessage("Change of Email from " +updatedUser.getEmail() +" to " +user.getEmail() +" for user " +updatedUser.getFirstName());
			inboxId=addToInbox(user.getUserId(), inbox.getMessage());	
			isRequestedForUserUpdate(inboxId);
		}
		if (!(updatedUser.getUserName()).equals(user.getUserName())) {
			inbox.setMessage("Change of Username from " +updatedUser.getUserName() +" to " +user.getUserName() +" for user " +updatedUser.getFirstName());
			inboxId=addToInbox(user.getUserId(), inbox.getMessage());	
			isRequestedForUserUpdate(inboxId);
		}
		if (!(updatedUser.getPassword()).equals(user.getPassword())) {
			inbox.setMessage("Change of Password from " +updatedUser.getPassword() +" to " +user.getPassword() +" for user " +updatedUser.getFirstName());
			inboxId=addToInbox(user.getUserId(), inbox.getMessage());
			isRequestedForUserUpdate(inboxId);
		}
		if ((updatedUser.getDeptId()) != (user.getDeptId())) {
			inbox.setMessage("Change of Department from " +updatedUser.getDeptName() +" to " +user.getDeptName() +" for user " +updatedUser.getFirstName());
			inboxId=addToInbox(user.getUserId(), inbox.getMessage());
			isRequestedForUserUpdate(inboxId);
		} 
	}
	
	
	public void compareUpdatedUserObjectsForApproval(UserUpdate userUpdate, UserDetails userDetails) throws SQLException{
		Inbox inbox = new Inbox();
		if (!(userUpdate.getFirstName()).equals(userDetails.getFirstName())) {
			inbox.setMessage("Approved Change for First Name from " +userDetails.getFirstName() +" to "+userUpdate.getFirstName());
			int inboxId = addToInbox(userDetails.getUserId(), inbox.getMessage());
			isAcceptedForUserEditRequest(inboxId);
			inbox.setMessage("Approved Change for First Name from " +userDetails.getFirstName() +" to "+userUpdate.getFirstName()+ " for user " +userUpdate.getFirstName());
			int inbxId = addToInbox(userDetails.getUserId(), inbox.getMessage());
			isAcceptedForAdminEditRequest(inbxId);		
		}
		if (!(userUpdate.getLastName()).equals(userDetails.getLastName())) {
			inbox.setMessage("Approved Change for Last Name from " +userDetails.getLastName() +" to " +userUpdate.getLastName());
			int inboxId = addToInbox(userDetails.getUserId(), inbox.getMessage());
			isAcceptedForUserEditRequest(inboxId);
			inbox.setMessage("Approved Change for Last Name from " +userDetails.getLastName() +" to "+userUpdate.getLastName()+ " for user " +userUpdate.getFirstName());
			int inbxId = addToInbox(userDetails.getUserId(), inbox.getMessage());
			isAcceptedForAdminEditRequest(inbxId);		
		}
		if (!(userUpdate.getEmail()).equals(userDetails.getEmail())) {
			inbox.setMessage("Approved Change for Email from " +userDetails.getEmail() +" to " +userUpdate.getEmail());
			int inboxId = addToInbox(userDetails.getUserId(), inbox.getMessage());
			isAcceptedForUserEditRequest(inboxId);
			inbox.setMessage("Approved Change for Email from " +userDetails.getEmail() +" to "+userUpdate.getEmail()+ " for user " +userUpdate.getFirstName());
			int inbxId = addToInbox(userDetails.getUserId(), inbox.getMessage());
			isAcceptedForAdminEditRequest(inbxId);		
		}
		if (!(userUpdate.getUserName()).equals(userDetails.getUserName())) {
			inbox.setMessage("Approved Change for User Name from " +userDetails.getUserName() +" to " +userUpdate.getUserName());
			int inboxId = addToInbox(userDetails.getUserId(), inbox.getMessage());
			isAcceptedForUserEditRequest(inboxId);
			inbox.setMessage("Approved Change for User Name from " +userDetails.getUserName() +" to "+userUpdate.getUserName()+ " for user " +userUpdate.getFirstName());
			int inbxId = addToInbox(userDetails.getUserId(), inbox.getMessage());
			isAcceptedForAdminEditRequest(inbxId);		
		}
		if (!(userUpdate.getPassword()).equals(userDetails.getPassword())) {
			inbox.setMessage("Approved Change for Password from " +userDetails.getPassword() +" to " +userUpdate.getPassword());
			int inboxId = addToInbox(userDetails.getUserId(), inbox.getMessage());		
			isAcceptedForUserEditRequest(inboxId);
			inbox.setMessage("Approved Change for Password from " +userDetails.getPassword() +" to "+userUpdate.getPassword()+ " for user " +userUpdate.getFirstName());
			int inbxId = addToInbox(userDetails.getUserId(), inbox.getMessage());
			isAcceptedForAdminEditRequest(inbxId);
		}
		if ((userUpdate.getDeptId()) != (userDetails.getDeptId())) {
			inbox.setMessage("Approved Change for Department from " +userDetails.getDeptName() +" to " +userUpdate.getDeptName());
			int inboxId = addToInbox(userDetails.getUserId(), inbox.getMessage());
			isAcceptedForUserEditRequest(inboxId);
			inbox.setMessage("Approved Change for Department fom " +userDetails.getDeptName() +" to " +userUpdate.getDeptName() +" for user " +userUpdate.getFirstName());
			int inbxId = addToInbox(userDetails.getUserId(), inbox.getMessage());
			isAcceptedForAdminEditRequest(inbxId);
		}	
		
	}
	
	
	public void compareUpdatedUserObjectsForApprovalByManager(UserDetails userDetails, UserDetails user) throws SQLException{
		Inbox inbox = new Inbox();
		if (!(userDetails.getFirstName()).equals(user.getFirstName())) {
			inbox.setMessage("Approved Change for First Name from " +userDetails.getFirstName() +" to "+user.getFirstName());
			int inboxId = addToInbox(userDetails.getUserId(), inbox.getMessage());
			isAcceptedForUserEditRequest(inboxId);
			inbox.setMessage("Approved Change for First Name from " +userDetails.getFirstName() +" to "+user.getFirstName()+ " for user " +userDetails.getFirstName());
			int inbxId = addToInbox(userDetails.getUserId(), inbox.getMessage());
			isAcceptedForAdminEditRequest(inbxId);		
		}
		if (!(userDetails.getLastName()).equals(user.getLastName())) {
			inbox.setMessage("Approved Change for Last Name from " +userDetails.getLastName() +" to " +user.getLastName());
			int inboxId = addToInbox(userDetails.getUserId(), inbox.getMessage());
			isAcceptedForUserEditRequest(inboxId);
			inbox.setMessage("Approved Change for Last Name from " +userDetails.getLastName() +" to "+user.getLastName()+ " for user " +userDetails.getFirstName());
			int inbxId = addToInbox(userDetails.getUserId(), inbox.getMessage());
			isAcceptedForAdminEditRequest(inbxId);		
		}
		if (!(userDetails.getEmail()).equals(user.getEmail())) {
			inbox.setMessage("Approved Change for Email from " +userDetails.getEmail() +" to " +user.getEmail());
			int inboxId = addToInbox(userDetails.getUserId(), inbox.getMessage());
			isAcceptedForUserEditRequest(inboxId);
			inbox.setMessage("Approved Change for Email from " +userDetails.getEmail() +" to "+user.getEmail()+ " for user " +userDetails.getFirstName());
			int inbxId = addToInbox(userDetails.getUserId(), inbox.getMessage());
			isAcceptedForAdminEditRequest(inbxId);		
		}
		if (!(userDetails.getUserName()).equals(user.getUserName())) {
			inbox.setMessage("Approved Change for User Name from " +userDetails.getUserName() +" to " +user.getUserName());
			int inboxId = addToInbox(userDetails.getUserId(), inbox.getMessage());
			isAcceptedForUserEditRequest(inboxId);
			inbox.setMessage("Approved Change for User Name from " +userDetails.getUserName() +" to "+user.getUserName()+ " for user " +userDetails.getFirstName());
			int inbxId = addToInbox(userDetails.getUserId(), inbox.getMessage());
			isAcceptedForAdminEditRequest(inbxId);		
		}
		if (!(userDetails.getPassword()).equals(user.getPassword())) {
			inbox.setMessage("Approved Change for Password from " +userDetails.getPassword() +" to " +user.getPassword());
			int inboxId = addToInbox(userDetails.getUserId(), inbox.getMessage());		
			isAcceptedForUserEditRequest(inboxId);
			inbox.setMessage("Approved Change for Password from " +userDetails.getPassword() +" to "+user.getPassword()+ " for user " +userDetails.getFirstName());
			int inbxId = addToInbox(userDetails.getUserId(), inbox.getMessage());
			isAcceptedForAdminEditRequest(inbxId);
		}
		if ((userDetails.getDeptId()) != (user.getDeptId())) {
			inbox.setMessage("Approved Change for Department from " +userDetails.getDeptName() +" to " +user.getDeptName());
			int inboxId = addToInbox(userDetails.getUserId(), inbox.getMessage());
			isAcceptedForUserEditRequest(inboxId);
			inbox.setMessage("Approved Change for Department fom " +userDetails.getDeptName() +" to " +user.getDeptName() +" for user " +userDetails.getFirstName());
			int inbxId = addToInbox(userDetails.getUserId(), inbox.getMessage());
			isAcceptedForAdminEditRequest(inbxId);
		}	
	}
	
	
	public void compareUpdatedUserObjectsForRejection(UserUpdate userUpdate, UserDetails user) throws SQLException{
		int inboxId;
		Inbox inbox = new Inbox();
	if (!(userUpdate.getFirstName()).equals(user.getFirstName())) {
		inbox.setMessage("Rejected Change for First Name from " +user.getFirstName() +" to "+userUpdate.getFirstName());
		inboxId = addToInbox(user.getUserId(), inbox.getMessage());
		isRejected(inboxId);
		isAcceptedForUserEditRequest(inboxId);
		inbox.setMessage("Rejected Change for First Name from " +user.getFirstName() +" to "+userUpdate.getFirstName()+ " for user " +user.getFirstName());
		int inbxId = addToInbox(user.getUserId(), inbox.getMessage());
		isAcceptedForAdminEditRequest(inbxId);
		isRejected(inbxId);		
	}
	if (!(userUpdate.getLastName()).equals(user.getLastName())) {
		inbox.setMessage("Rejected Change for Last Name from " +user.getLastName() +" to " +userUpdate.getLastName());
		inboxId = addToInbox(user.getUserId(), inbox.getMessage());
		isRejected(inboxId);
		isAcceptedForUserEditRequest(inboxId);
		inbox.setMessage("Rejected Change for Last Name from " +user.getLastName() +" to "+userUpdate.getLastName()+ " for user " +user.getFirstName());
		int inbxId = addToInbox(user.getUserId(), inbox.getMessage());
		isAcceptedForAdminEditRequest(inbxId);
		isRejected(inbxId);		
	}
	if (!(userUpdate.getEmail()).equals(user.getEmail())) {
		inbox.setMessage("Rejected Change for Email from " +user.getEmail() +" to " +userUpdate.getEmail());
		inboxId = addToInbox(user.getUserId(), inbox.getMessage());
		isRejected(inboxId);
		isAcceptedForUserEditRequest(inboxId);
		inbox.setMessage("Rejected Change for Email from " +user.getEmail() +" to "+userUpdate.getEmail()+ " for user " +user.getFirstName());
		int inbxId = addToInbox(user.getUserId(), inbox.getMessage());
		isAcceptedForAdminEditRequest(inbxId);
		isRejected(inbxId);		
	}
	if (!(userUpdate.getUserName()).equals(user.getUserName())) {
		inbox.setMessage("Rejected Change for User Name from " +user.getUserName() +" to " +userUpdate.getUserName());
		inboxId = addToInbox(user.getUserId(), inbox.getMessage());
		isRejected(inboxId);
		isAcceptedForUserEditRequest(inboxId);
		inbox.setMessage("Rejected Change for User Name from " +user.getUserName() +" to "+userUpdate.getUserName()+ " for user " +user.getFirstName());
		int inbxId = addToInbox(user.getUserId(), inbox.getMessage());
		isAcceptedForAdminEditRequest(inbxId);
		isRejected(inbxId);		
	}
	if (!(userUpdate.getPassword()).equals(user.getPassword())) {
		inbox.setMessage("Rejected Change for Password from " +user.getPassword() +" to " +userUpdate.getPassword());
		inboxId = addToInbox(user.getUserId(), inbox.getMessage());
		isRejected(inboxId);
		isAcceptedForUserEditRequest(inboxId);
		inbox.setMessage("Rejected Change for Password from " +user.getPassword() +" to "+userUpdate.getPassword()+ " for user " +user.getFirstName());
		int inbxId = addToInbox(user.getUserId(), inbox.getMessage());
		isAcceptedForAdminEditRequest(inbxId);
		isRejected(inbxId);		
	}
	if	((userUpdate.getDeptId()) != (user.getDeptId())) {
		inbox.setMessage("Rejected Change for Department from " +user.getDeptName() +" to " +userUpdate.getDeptName());
		inboxId = addToInbox(user.getUserId(), inbox.getMessage());
		isRejected(inboxId);
		isAcceptedForUserEditRequest(inboxId);
		inbox.setMessage("Rejected Change for Department from "+user.getDeptName() +" to " +userUpdate.getDeptName()+ " for user " +user.getFirstName());
		int inbxId = addToInbox(user.getUserId(), inbox.getMessage());
		isAcceptedForAdminEditRequest(inbxId);
		isRejected(inbxId);		
	}	
	}
	
	
	public void addApprovedSkills(UserDetails user, String skillName, int skillId) throws SQLException {	
		Inbox inbox = new Inbox();
		inbox.setMessage("Approved Skill " +skillName);        		
    	int inboxId = addToInbox(user.getUserId(), inbox.getMessage());
    	addToInboxAsSkill(inboxId, skillId);
    	isAcceptedForUserSkill(inboxId);
    	isRead(inboxId);
		inbox.setMessage("Approved Skill " +skillName +" for user " +user.getFirstName());
		int inbxId = addToInbox(user.getUserId(), inbox.getMessage());
		addToInboxAsSkill(inbxId, skillId);
		isAcceptedForAdminSkill(inbxId);
	}
	
	
	public void addRejectedSkills(UserDetails user, String skillName, int skillId) throws SQLException {
		Inbox inbox = new Inbox();
		inbox.setMessage("Rejected Skill " +skillName);
    	int inboxId = addToInbox(user.getUserId(), inbox.getMessage());
    	addToInboxAsSkill(inboxId, skillId);
    	isAcceptedForUserSkill(inboxId);
    	isRejected(inboxId);
		isRead(inboxId);		
		inbox.setMessage("Rejected Skill " +skillName +" for user " +user.getFirstName());
		int inbxId = addToInbox(user.getUserId(), inbox.getMessage());		
		isRejected(inbxId);
		isAcceptedForAdminSkill(inbxId);
	}
	
	
	public void addSkillsForUserByManager(UserDetails user, String skillName, int skillId) throws SQLException {	
		Inbox inbox = new Inbox();
		inbox.setMessage("Added Skill " +skillName +" for user " +user.getFirstName() +" by Admin/Manager ");
		int inboxId = addToInbox(user.getUserId(), inbox.getMessage());
		addToInboxAsSkill(inboxId, skillId);
		isAcceptedForAdminSkill(inboxId);
		inbox.setMessage("Added Skill " +skillName);
		int inbxId = addToInbox(user.getUserId(), inbox.getMessage());
		addToInboxAsSkill(inbxId, skillId);
		isAcceptedForUserSkill(inbxId);
	}
	
	
	public void addSkillsForUser(UserDetails user, String skillName, int skillId) throws SQLException {	
		Inbox inbox = new Inbox();
		inbox.setMessage(""+user.getFirstName() +" added skill " +skillName);
		int inboxId= addToInbox(user.getUserId(), inbox.getMessage());	
		addToInboxAsSkill(inboxId, skillId);
		isRequestedForSkillUpdate(inboxId);
	}
	
	
	public void deleteSkillsOfUser(UserDetails user, String skillName, int skillId) throws SQLException {	
		Inbox inbox = new Inbox();
		inbox.setMessage("Removed Skill " +skillName +" of user " +user.getFirstName());
		int inboxId = addToInbox(user.getUserId(), inbox.getMessage());
		addToInboxAsSkill(inboxId, skillId);
		isAcceptedForAdminSkill(inboxId);
		inbox.setMessage("Removed Skill " +skillName);
		int inbxId = addToInbox(user.getUserId(), inbox.getMessage());
		addToInboxAsSkill(inbxId, skillId);
		isAcceptedForUserSkill(inbxId);
	}
	
	
	public void expiredMessagesForAdmin(UserDetails user,UserUpdate updateuser) throws SQLException{
		int inboxId;
		Inbox inbox = new Inbox();
		if (!(updateuser.getFirstName()).equals(user.getFirstName())) {
			inbox.setMessage("Change of FirstName from " +updateuser.getFirstName() +" to " +user.getFirstName() +" for user " +updateuser.getFirstName() +" (Expired Request...!)");
			inboxId= addToInbox(user.getUserId(), inbox.getMessage());
			isRequestedForUserUpdate(inboxId);
			isAcceptedForAdminExpiredRequest(inboxId);
		}
		if (!(updateuser.getLastName()).equals(user.getLastName())) {
			inbox.setMessage("Change of LastName from " +updateuser.getLastName() +" to " +user.getLastName() +" for user " +updateuser.getFirstName() +" (Expired Request...!)");
			inboxId=addToInbox(user.getUserId(), inbox.getMessage());
			isRequestedForUserUpdate(inboxId);
			isAcceptedForAdminExpiredRequest(inboxId);
		}
		if	(!(updateuser.getEmail()).equals(user.getEmail())) {
			inbox.setMessage("Change of Email from " +updateuser.getEmail() +" to " +user.getEmail() +" for user " +updateuser.getFirstName() +" (Expired Request...!)");
			inboxId=addToInbox(user.getUserId(), inbox.getMessage());	
			isRequestedForUserUpdate(inboxId);
			isAcceptedForAdminExpiredRequest(inboxId);
		}
		if (!(updateuser.getUserName()).equals(user.getUserName())) {
			inbox.setMessage("Change of Username from " +updateuser.getUserName() +" to " +user.getUserName() +" for user " +updateuser.getFirstName() +" (Expired Request...!)");
			inboxId=addToInbox(user.getUserId(), inbox.getMessage());	
			isRequestedForUserUpdate(inboxId);
			isAcceptedForAdminExpiredRequest(inboxId);
		}
		if (!(updateuser.getPassword()).equals(user.getPassword())) {
			inbox.setMessage("Change of Password from " +updateuser.getPassword() +" to " +user.getPassword() +" for user " +updateuser.getFirstName() +" (Expired Request...!)");
			inboxId=addToInbox(user.getUserId(), inbox.getMessage());
			isRequestedForUserUpdate(inboxId);
			isAcceptedForAdminExpiredRequest(inboxId);
		}
		if ((updateuser.getDeptId()) != (user.getDeptId())) {
			inbox.setMessage("Change of Department from " +updateuser.getDeptName() +" to " +user.getDeptName() +" for user " +updateuser.getFirstName() +" (Expired Request...!)");
			inboxId=addToInbox(user.getUserId(), inbox.getMessage());
			isRequestedForUserUpdate(inboxId);
			isAcceptedForAdminExpiredRequest(inboxId);
		} 
	}
	
	
	public void checkMessageStatus() throws SQLException {		
		List<Inbox> ExpiredMsgList = new ArrayList<>();
		ExpiredMsgList = getUserRequests();
		for (int i = 0; i < ExpiredMsgList.size(); i++) {
			int inboxId = ExpiredMsgList.get(i).getInboxId();
			int userId= ExpiredMsgList.get(i).getUserId();
			String requestedTime = ExpiredMsgList.get(i).getRequestedTime();
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String currentTime = sdf.format(date);
			Date d1 = null;
			Date d2 = null;
			try {
				 d1 = sdf.parse(requestedTime);
				 d2 = sdf.parse(currentTime);
			} catch (ParseException e) {
				  e.printStackTrace();
			}  
			// Get millisecond from each, and subtract.
			long diff = d2.getTime() - d1.getTime();
			//long diffHours = diff / (60 * 60 * 1000); 
			//long diffSeconds = diff / 1000 % 60;  
			long diffMinutes = diff / (60 * 1000) % 60;
			UserDetails user = getUserById(userId);
			UserUpdate updatedUser = getUserByIdForUpdate(userId);			
			
			if(diffMinutes >= 300){
				isExpired(inboxId);
				expiredMessagesForAdmin(user, updatedUser);	
			}
		}		
	 }
	
	
	public void markEditedField(HttpServletRequest request) throws SQLException {		
		List<Inbox> userRequestList = new ArrayList<>();
	  	userRequestList = getUserEditRequests();
	  	List<Message> messageList = new ArrayList<Message>();
	  	
	  	for (int i = 0; i < userRequestList.size(); i++) {
	 
			String userRequest = userRequestList.get(i).getMessage();  
			String type = userRequestList.get(i).getType(); 
			int inboxId = userRequestList.get(i).getInboxId();
			int userId = userRequestList.get(i).getUserId();
			
			String[] words = userRequest.split(" "); 			
			String str1= words[0] + " " + words[1] + " " + words[2] + " " + words[3]+ " " + words[4] + " " + words[5] + " " ;     								
			String str2= words[6];
			String str3= " " +words[7]+" " +words[8] + " " +words[9]; 
			
			Message msg =new Message();  	
				msg.setStr1(str1);
				msg.setStr2(str2);
				msg.setStr3(str3);
				msg.setInboxId(inboxId);
				msg.setUserId(userId);
				msg.setType(type);
				messageList.add(msg);      							
			} 
	request.setAttribute("requests", getUserSkillRequests()); 		
	request.setAttribute("messageList", messageList); 
	}
	
	
	public void writeXmlFile(String role, int userId) throws SQLException {			
		File file = new File("C:\\Users\\mebin.j\\Spring Workspace\\Employee\\WebContent\\resources\\xml\\data.xml");
  		
  		if(file.exists() && !file.isDirectory()){
  			file.delete();  			
  		} else{
  			System.out.println("Delete operation is failed for " +file.getName());
  		}

		List<Inbox> noOfRequest = new ArrayList<Inbox>();		
	
		if(role.equals("user")) {
			noOfRequest = getRequestCountForUser(userId);	
		}
		else {
			noOfRequest = getTotalRequestCount();
		}
		
	    try {
	    	DocumentBuilderFactory dFact = DocumentBuilderFactory.newInstance();
	        DocumentBuilder build = dFact.newDocumentBuilder();
	        Document doc = build.newDocument();
	        Element root = doc.createElement("JSChart");
	        doc.appendChild(root);
	        Element dataset = doc.createElement("dataset");
			root.appendChild(dataset);
			dataset.setAttribute("type", "bar");
			int count = 1;
	        for (int i = 0; i < noOfRequest.size(); i++) {
	        	Element data = doc.createElement("data");	           
	            dataset.appendChild(data);
	            String requestCount = String.valueOf(noOfRequest.get(i).getCount());
	            data.setAttribute("value", requestCount);
	            String dayCount = Integer.toString(count);
	            data.setAttribute("unit", dayCount);  
	            count++;   
	        }
	        count++; 
	        
	        // Save the document to the disk file
	        TransformerFactory tranFactory = TransformerFactory.newInstance();
	        Transformer aTransformer = tranFactory.newTransformer();

	       /* // format the XML nicely
	        aTransformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");*/

	        aTransformer.setOutputProperty(
	                "{http://xml.apache.org/xslt}indent-amount", "4");
	        aTransformer.setOutputProperty(OutputKeys.INDENT, "yes");

	        DOMSource source = new DOMSource(doc);
	        try {
	            // location and name of XML file you can change as per need
	            FileWriter fos = new FileWriter("C:\\Users\\mebin.j\\Spring Workspace\\Employee\\WebContent\\resources\\xml\\data.xml");
	            StreamResult result = new StreamResult(fos);
	            aTransformer.transform(source, result);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	    } catch (TransformerException ex) {
	        System.out.println("Error outputting document");

	    } catch (ParserConfigurationException ex) {
	        System.out.println("Error building document");
	    }
	}
	
	
	public boolean compareUserObjects(UserUpdate userUpdate, UserDetails userDetails ){
		
		if((userUpdate.getUserId()) != (userDetails.getUserId())) {
			return true;
		}		
		else if(!(userUpdate.getFirstName()).equals(userDetails.getFirstName())) {
			return true;
		}
		else if(!(userUpdate.getLastName()).equals(userDetails.getLastName())) {
			return true;
		}
		else if(!(userUpdate.getDob()).equals(userDetails.getDob())) {
			return true;
		}
		else if(!(userUpdate.getEmail()).equals(userDetails.getEmail())) {
			return true;
		}
		else if((userUpdate.getDeptId()) != (userDetails.getDeptId())) {
			return true;
		}
		else if(!(userUpdate.getDeptName()).equals(userDetails.getDeptName())) {
			return true;
		}
		else if(!(userUpdate.getUserName()).equals(userDetails.getUserName())) {
			return true;
		}
		else if(!(userUpdate.getPassword()).equals(userDetails.getPassword())) {
			return true;
		}	
		else {
			return false;
		}
	}
	

}

	
	
	
	




