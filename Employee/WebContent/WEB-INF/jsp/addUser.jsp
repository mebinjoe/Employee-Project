<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
	<link href="${pageContext.request.contextPath}/resources/css/hover.css" rel="stylesheet" media="all">
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/body_background.css">
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/registration_form_style.css">
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/link_style.css">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link type="text/css"
    href="${pageContext.request.contextPath}/resources/css/ui-lightness/jquery-ui-1.8.18.custom.css" rel="stylesheet" />
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery-1.7.1.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery-1.11.1.js"></script>	
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.8.3/jquery.min.js"></script>	
</head>
	<c:choose>
    	<c:when test="${user.userId > 0}">
	    	<title>Edit Employee Details</title>
    	</c:when>    
    	<c:otherwise>
		    <title>Add New Employee</title>
		</c:otherwise>
	</c:choose>   
	
	
<body>
    <form method="POST" action='/employee/addUser' name="addUser" class="Adduser">
            
        <input type="hidden" name="${_csrf.parameterName}"  value="${_csrf.token}"/>
        <input type="hidden" name="userId" value="<c:out value="${user.userId}" />" /> <br /> 
        <c:choose>
    		<c:when test="${user.userId > 0}">
	        	<input type="hidden" name="action" value="EDIT" /> <br />
    		</c:when>    
    		<c:otherwise>
		       <input type="hidden" name="action" value="USERADD" /> <br /> 
		    </c:otherwise>
		</c:choose>   
		
		<label>
        	<span>First Name :</span>  
        	<input type="text" id="firstName" name="firstName" value="<c:out value="${user.firstName}" />" required title="First Name should not be left blank." maxlength="10" autocomplete="off"/>
        	Max Size-10 <span></span>   
        </label><br />	
        <label>
        	<span>Last Name :</span>  
        	<input type="text" id="lastName" name="lastName" value="<c:out value="${user.lastName}" />" required title="Last Name should not be left blank." maxlength="10" autocomplete="off"/>  
        	Max Size-10 <span></span>   
        </label><br /> 
        <label>
        	<span>Role :</span> 
        	 <c:choose>
    			<c:when test="${user.userId > 0}">
         			<select  type="text" id="role" name="role"  value="<c:out value="${user.role}" />">        			 
			        	<option value="user">User</option>
			        	<option value="admin" disabled>Admin</option>    
			        	<option value="manager" disabled>Manager</option>        
					</select>  
				</c:when>
				<c:otherwise>
					<select  type="text" id="role" name="role"  value="<c:out value="${user.role}" />">        			 
			        	<option value="user">User</option>
			        	<option value="admin">Admin</option>    
			        	<option value="manager">Manager</option>        
					</select> 				
				</c:otherwise>
				</c:choose>
		</label><br />
		<label>
        	<span>DOB :</span> 
         	<input type="text" id="dob" name="dob" value="<fmt:formatDate pattern="dd-MM-yyyy" value="${user.dob}" />"required title="Date of Birth should not be left blank." />         	
         	<!-- Age : <span id="age"></span> -->          	
        </label><br /> 
        <label>
        	<span>Email :</span> 
        	<input type="text" id="email" name="email" value="<c:out value="${user.email}" />" 
        	required pattern="[^@]+@[^@]+\.[a-zA-Z]{2,6}" title="Example: abc@def.hij " autocomplete="off"/>  
        </label><br /> 
        <label>
        	<span>Username :</span>      
        	<input type="text" id="username" name="username" value="<c:out value="${user.userName}" />" required title="UserName should not be left blank." maxlength="10" autocomplete="off"/>
        	Max Size-10 <span></span>   
        </label><br /> 
        <label>
        	<span>Password :</span>  
        	<input type="password" name="password" id="password" value="<c:out value="${user.password}" />" required title="Password should not be left blank."
        			onkeyup="CheckPasswordStrength(this.value)" autocomplete="off"/>
        	<span id="passstrength"></span>
        </label><br>
        
        
			<c:choose>
			 	<c:when test="${user.userId > 0}">
			 		<label>
        			<span>Department Name :</span> 
					<select type="text" name="deptId" id="deptId">						
						<c:forEach items="${department}" var="dept">									
							<option value="${dept.deptId}:${dept.deptName}" ${user.deptId == dept.deptId ? 'selected' : ''}>${dept.deptName}</option>																	
						</c:forEach>				 			
				 	</select><br><br> 				 	
			 		</label>	
			 	</c:when>
			 	<c:otherwise>	
			 	<label>
        			<span>Department Name :</span>  
					<select name="deptId" required title="Please select a department">
         				<option value="">-Select-</option>
    					<c:forEach items="${department}" var="dept">
        					<option value="${dept.deptId}">${dept.deptName}</option>
    					</c:forEach>
					</select><br><br>
				</label> 
		   	 	</c:otherwise> 
			</c:choose>
			
			
			
				<c:if test="${(role == 'admin' || role == 'manager') && (user.userId > 0 && empty isEdit)}">				
					<!-- message Requests -->						
						 <input type="hidden" name="action" value="approveRequest" />
						 <input type="hidden" name="inboxId" value="${inboxId}" id="inboxId" />
						 <input type="hidden" name="userId" value="${userId}"  id="userId"/> 
					     <button type="button" name="action" value="approveRequest" class="approveRequest" onClick="return approve();" >Approve</button> &nbsp; 					     
						 <button type="button" name="action" value="rejectRequest" class="rejectRequest" >Reject</button>					
				</c:if>
				<c:if test="${(role == 'admin' && isEdit == 1) || (role == 'manager' && isEdit == 1) || (role == 'user') }">
					<input type="submit" value="Save" class="submit" />   
				</c:if>
			
			
			
        <!-- <input type="submit" value="Save" class="submit" />    -->         
    </form>  
    
    <script>
    $(document).on('click', 'button.approveRequest', function () { 
    	var inboxId = $('#inboxId').val();
    	var userId = $('#userId').val();
    	var firstName = $('#firstName').val();
    	var lastName = $('#lastName').val();
    	var role = $('#role').val();
    	var dob = $('#dob').val();
    	var email = $('#email').val();
    	var username = $('#username').val();
    	var password = $('#password').val();
    	var deptId = $('#deptId').val();
    	
    	$.ajax({
    		type: "Get",
    		url: "../employee/UserController",
    		data: {inboxId : inboxId , userId : userId ,
    			firstName : firstName , lastName : lastName ,
    			role : role , dob : dob ,
    			email : email , username : username ,
    			password : password , deptId : deptId , 
    			action : 'approveRequest'},
    		success: function() {
    			alert('Approved User Detail change request');
    			//window.location.reload(true); 
    		},		    
    		error: function(xhr, thrownError) {
    			alert('Error ' + thrownError); 
    		}
    	});    	
     }); 
    
$(document).on('click', 'button.rejectRequest', function () { 
	var inboxId = $('#inboxId').val();
	var userId = $('#userId').val();
	
	$.ajax({
		type: "Get",
		url: "../employee/rejectRequest",
		data: {inboxId : inboxId , userId : userId , action : 'rejectRequest'},
		success: function() {
			alert('Rejected User Detail change request');
			window.location.reload(true); 
		},		    
		error: function(xhr, thrownError) {
			alert('Error ' + thrownError); 
		}
	});	
 });
</script>
    
     
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery-ui-1.8.18.custom.min.js"></script>
<script>
$('input[name=dob]').datepicker({
	onSelect: function(value, ui) {
		var today = new Date(), 
	    dob = new Date(value), 
	    age = new Date(today - dob).getFullYear() - 1970;
	    $('#age').text(age);
	},
	maxDate: '+0d',
	yearRange: '1940:2010',
	changeMonth: true,
	changeYear: true
});
</script> 
    
<script type="text/javascript">
	function CheckPasswordStrength(password) {
    	var password_strength = document.getElementById("passstrength");
        if (password.length == 0) {
        	password_strength.innerHTML = "";
            return;
        }
        var regex = new Array();
        regex.push("[A-Z]"); //Uppercase Alphabet.
        regex.push("[a-z]"); //Lowercase Alphabet.
        regex.push("[0-9]"); //Digit.
        regex.push("[$@$!%*#?&]"); //Special Character.
        var passed = 0;
        for (var i = 0; i < regex.length; i++) {
        	if (new RegExp(regex[i]).test(password)) {
            	passed++;
            }
        }
        if (passed > 2 && password.length > 8) {
        	passed++;
        }
        //Display status.
        var color = "";
        var strength = "";
        switch (passed) {
        	case 0:
            case 1:
            	strength = "Weak";
                color = "red";
                break;
            case 2:
                strength = "Good";
                color = "darkorange";
                break;
            case 3:
            case 4:
                strength = "Strong";
                color = "green";
                break;
            case 5:
                strength = "Very Strong";
                color = "darkgreen";
                break;
            }
            password_strength.innerHTML = strength;
            password_strength.style.color = color;
        }
</script>
</body>

<a href="${pageContext.request.contextPath}/home" class="link hvr-float-shadow">Home</a><br></br>

<c:url var="logoutUrl" value="/logout"/>
	<form action="${logoutUrl}" id="logout" method="post">  		
  		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
	</form>
	<a href="#" onclick="document.getElementById('logout').submit();" class="link hvr-float-shadow">Logout</a>
 
</html>