<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
	<link href="${pageContext.request.contextPath}/resources/css/hover.css" rel="stylesheet" media="all">
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/body_background.css">
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/skill_add_form.css">
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/link_style.css">	
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/drop_down_add_skill.css">	
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">	
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery-1.7.1.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery-ui-1.8.18.custom.min.js"></script>
	<title>Add Skills</title>
</head>
<script src="http://code.jquery.com/jquery-2.1.0.min.js"></script>
<script>
$(document).ready(function () {
    var usedNames = {};
    $(".level option").each(function () {
        if (usedNames[this.value]) {
            $(this).remove();
        } else {
            usedNames[this.value] = this.text;
        }
    });
});
</script>


<body> 
    <form method="POST" action='/employee/addSkill' name="addSkill" class="AddSkill" >
           
        <input type="hidden" name="action" value="ADDSKILL" /> <br />    
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>    
        <input type="hidden" name="skillid" value="<c:out value="${skill.skillId}" />" /> <br /> 
         
                
        <c:if test="${role == 'admin' || role == 'manager' && (userID == 10 || userID == 11) }"> 
        	<c:if test="${not empty skillStatus}">
        		<p>
        		<label for="username">Employee Name :</label>             
          			<select type="text" name="Id" id="Id" style="width: 150px; padding: 9px;">
          				<option value="">-Select-</option>						
						<c:forEach items="${users}" var="user">									
							<option value="${user.userId}" >${user.firstName}</option>																	
						</c:forEach>				 			
					</select><br>
        		</p>
        	</c:if>
        </c:if>
         	 
        
        <p>     
        <label for="skillname">Skill :</label>             
          	<input type="text" name="skillname" id="skillName" value= "<c:out value="${skill.skillName}"/>" required title="Add some skill" autocomplete="off"/> <br /> 
        </p>
        <p>
        <%-- <label for="level">Level :</label>         
         <input type="text" name="level" id="level" value="<c:out value="${skill.level}" />"required title="Rate your Level.&#10; Example: Beginner, Intermediate, Expert" autocomplete="off"/> <br />  --%>
         <label for="level">Level :</label>
         <c:choose>
         	<c:when test="${empty skill.skillName}">
         		<select type="text" name="level" id="level" required title="Rate your Level.&#10; Example: Beginner, Intermediate, Expert" style="width: 220px; padding: 8px;" >	
         			<option value="">-Select-</option>	
         			<option value="Beginner">Beginner</option>
         			<option value="Intermediate">Intermediate</option>
         			<option value="Advanced">Advanced</option>
         			<option value="Expert">Expert</option>        		
				</select><br>
			</c:when>
			<c:otherwise>
				<select type="text" name="level" class="level" id="level"  value="<c:out value="${skill.level}"/>" required title="Rate your Level.&#10; Example: Beginner, Intermediate, Expert" style="width: 220px; padding: 8px;" >         			
         			<option selected value="${skill.level}">${skill.level}</option>
         			<option value="Beginner">Beginner</option>
         			<option value="Intermediate">Intermediate</option>
         			<option value="Advanced">Advanced</option>
         			<option value="Expert">Expert</option>        		
				</select><br>			
			</c:otherwise>
		</c:choose>
		</p>
		<p>
        <label for="experience">Experience :</label> 
        	<input type="text" name="experience" id="experience" value="<c:out value="${skill.experience}" />" required title="Experience in years" autocomplete="off"/> <br /> 
        </p>
        <input type="hidden" name="approved" value="<c:out value="${skill.approved}" />" /> <br />    
       
       <!--  <input type="submit" value="Save" class="submitSkill" onClick="return verify();"/> <br /><br /> -->
        
        
        <c:choose>
			<c:when test="${(role == 'admin' || role == 'manager') && (empty isInsertSkill)}">	        
        		<!-- skill Requests --> &emsp;  
        		<input type="hidden" name="userID" value="${userID}" id="userID"/> 
        		<input type="hidden" name="skillId" value="${skillId}" id="skillId" />
        		<input type="hidden" name="inboxId" value="${inboxId}" id="inboxId" />
	   		    <button type="button" name="action" class="approveSkill" value="Approve" >Approve</button> &nbsp; &nbsp; 
	    		<button type="button" name="action" class="rejectSkill" value="Reject" >Reject</button>  
	    		<br /><br /><br /><br />			
			</c:when>	
			<c:otherwise>
				<c:if test = "${role == 'user' || isInsertSkill == 1}" >
					<input type="submit" value="Save" class="submitSkill" onClick="return verify();"/> <br /><br /><br />
				</c:if>
			</c:otherwise>
		</c:choose>
				
   </form> 
   
    <script>
 $(document).on('click', 'button.approveSkill', function () {	
	var inboxId = $('#inboxId').val();
	var skillId = $('#skillId').val();
	var userID = $('#userID').val();
	var skillName = $('#skillName').val();
	var level = $('#level').val();
	var experience = $('#experience').val();
	$.ajax({
		type: "Get",
		url: "../employee/approveSkill",
		data: {skillId : skillId , inboxId : inboxId , 
			userID : userID , skillName : skillName ,
			level : level , experience : experience ,
			action : 'approveSkill'},
		success: function() {
			alert('Skill Approved');
			window.location.reload(true);
		},		    
		error: function(xhr, thrownError)	{
			alert('Error ' + thrownError); 
		}
	});
 });

$(document).on('click', 'button.rejectSkill', function () { 
	var inboxId = $('#inboxId').val();
	var skillId = $('#skillId').val();
	$.ajax({
		type: "Get",
	    url: "../employee/rejectSkill",
	    data: {skillId : skillId , inboxId : inboxId, action : 'rejectSkill'},
	    success: function() {
	    	alert('Skill Rejected');
	    	window.location.reload(true);
		},		    
		error: function(xhr, thrownError)	{
			alert('Error ' + thrownError); 
		}
	});
 });
</script>
    
<script type="text/javascript">
	function verify() { 		
 		alert('Skills Successfully added');	 	
	}
</script>

<a href="${pageContext.request.contextPath}/home" class="link hvr-float-shadow">Home</a><br></br>
	
<c:url var="logoutUrl" value="/logout"/>
	<form action="${logoutUrl}" id="logout" method="post">  		
  		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
	</form>
	<a href="#" onclick="document.getElementById('logout').submit();" class="link hvr-float-shadow">Logout</a>
  
    
</body>
</html>