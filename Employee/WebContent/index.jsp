<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE HTML>
<html>
<head>
		<meta charset="utf-8">
		<link href="${pageContext.request.contextPath}/resources/css/login_style.css" rel='stylesheet' type='text/css' />
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<link href='http://fonts.googleapis.com/css?family=Open+Sans:600italic,400,300,600,700' rel='stylesheet' type='text/css'>
		<title>User Login</title>
</head>

<script type="application/x-javascript"> 
	addEventListener("load", function() {
		setTimeout(hideURLbar, 0); 
	}, false); 
	function hideURLbar(){ 
		window.scrollTo(0,1); 
	} 
</script>


<body>
	<div class="main">
		<div class="login-form">
			<h1>User Login</h1>
				<div class="head">
					<img src="${pageContext.request.contextPath}/resources/images/user.png" alt=""/>
				</div>	
		<font color="red" size="4px">&nbsp;&nbsp;&nbsp;${message}</font>	
		
		<c:url value="/j_spring_security_check" var="loginUrl" />
		<form name='loginForm' action="${loginUrl}" method='POST'>

			<!-- <input type="hidden" name="action" value="LOGIN" /> <br />  -->
			<span  style="font-size:20px">Username:</span>
			<input type="text" name="username" required title= "Username can't be empty" autocomplete="off"/><br/><br/>
			<span  style="font-size:20px">Password:</span>
			<input type="password" name="password" required title= "Please enter your password" onkeypress="isCapLockOn(event)" autocomplete="off"/>
			<div id="keyStage" style="color:Red;font-size:80%;"></div><br/><br/>
			<div id="capsWarningDiv" style="visibility:hidden">Caps Lock is on.</div> 
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			<input type="submit" value="Login"/>
			<button type="reset" value="Reset">Reset</button>

	   	</form>
		</div>
	</div> 	
		<c:if test="${not empty error}">
   			Error: ${error}
		</c:if>	
		
	
 <script type="text/javascript">
	function isCapLockOn(e)	{
		var charKeyCode = e.keyCode ? e.keyCode : e.which; // To work with both MSIE & Netscape
		var shiftKey = e.shiftKey ? e.shiftKey : ((charKeyCode == 16) ? true : false);
		// Check both the condition as described above
		if (((charKeyCode >= 65 && charKeyCode <= 90) && !shiftKey) || ((charKeyCode >= 97 && charKeyCode <= 122) && shiftKey))
		{
			// Caps lock is on
			document.getElementById('keyStage').innerHTML = "Caps lock : <b>On</b>";
		}
		else
		{
			// Caps lock is off.
			document.getElementById('keyStage').innerHTML = "Caps lock : <b>Off</b>";
		}
	}
</script>

</body>

</html>

