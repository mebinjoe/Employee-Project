<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<link id="bootstrap-style" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">	
	<link href="${pageContext.request.contextPath}/resources/css/bootstrap-responsive.min.css" rel="stylesheet">
	<link id="base-style" href="${pageContext.request.contextPath}/resources/css/style.css" rel="stylesheet">
	<link id="base-style-responsive" href="${pageContext.request.contextPath}/resources/css/style-responsive.css" rel="stylesheet">
	<link href='http://fonts.googleapis.com/css?family=Open+Sans:300italic,400italic,600italic,700italic,800italic,400,300,600,700,800&subset=latin,cyrillic-ext,latin-ext' rel='stylesheet' type='text/css'>

	<link href="${pageContext.request.contextPath}/resources/css/hover.css" rel="stylesheet" media="all">
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/body_background.css">
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/table_style.css">
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/link_style.css">
	<script src="https://code.jquery.com/jquery-1.7.1.min.js"></script>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>View Skills</title>
</head>
<style> /* style for submit button */
	input[class="submitSearch"] { 
    	color:#050; 
    	font: bold 84% 'trebuchet ms',helvetica,sans-serif; 
    	background-color:#fed; 
   	 	border:1px solid; 
    	border-color: #696 #363 #363 #696; 
	}
</style>

<style> /* style for searchbox */
	#searchText {
		border:2px solid #59BAE8;
		border-radius:10px;
		height: 20px;
		width: 240px;
	}

	select {
  		font-family: Lucida Sans Unicode;
  		border: 2px solid #59BAE8;
	}
</style>

<input type="hidden" name="action" value="VIEWSKILLS" /> <br /> 
	
<body>

<c:if test="${deleteStatus}">
<script>
  alert("Skill deleted succesfully");        
</script>
</c:if>

<div style="width: 100%; margin-left: 20px;">
<p>
 <c:if test="${role != 'user'}">  
   		 <form method="GET" action='UserController' name="search" id="search"> 
	  	 	<input type="hidden" name="action" value="search" /> 
	  	 	<input type="hidden" name="userrole" value="${role}" /> 
		 	<input type="text" id="searchText" name="searchText"  class="searchText" id="searchText"/>  &nbsp;
		    <select  name="searchType" id="searchType" class="searchType">
		    		<option value="0">-Select-</option>
					<option value="1">First Name</option>
					<option value="2">Last Name</option> 
					<option value="3">Email</option>
					<option value="4">Department Name</option>
					<option value="5">Department Manager</option>
					<option value="skill">Skill Name</option>
					</select>&nbsp;
			<input name="search" id="submit" type="submit" class="submitSearch" value="Search" />
		  	<div id="myDiv"></div>
		    <br><br>
	    </form> 
	</c:if>	
</p>
</div>
	
	<div class="row-fluid sortable" style= "margin-left: 20px;" >		
					<div class="box span8">
						<div class="box-header">				
							<h2><i class="halflings-icon list-alt" ></i><span class="break"></span>Skills</h2>
						</div>
						<div class="box-content">
							<table class="table table-striped table-bordered bootstrap-datatable datatable">
								<thead>
									<tr> 
          								<c:if test="${role == 'manager' || role == 'admin' }">  
            								<th>Employee Name</th>
            							</c:if>                                 
                						<th>Skills</th>                
                						<th>Level</th>
                						<th>Experience</th> 
                						<th style="text-align:center">Operation</th>  
            						</tr>
								</thead>   
								<tbody>
									<c:forEach items="${skills}" var="skill">
                						<tr>
                							<c:if test="${role == 'manager' || role == 'admin' }">  
            									<td><c:out value="${skill.firstName}" /></td> 
            								</c:if>                 	    		                	              	           	                              
                    							<td><c:out value="${skill.skillName}" /></td>                                       
                    							<td><c:out value="${skill.level}" /></td>
                    							<td><c:out value="${skill.experience}" /></td> 
                    							<td style="text-align:center"><a class="btn btn-info" href="UserController?action=editSkill&skillId=<c:out value="${skill.skillId}"/>&userId=<c:out value="${skill.userId}"/>"  class="op">Update
                    								<i class="halflings-icon white edit"></i> 
                    								</a>
                    								<a class="btn btn-danger" href="UserController?action=deleteSkill&skillId=<c:out value="${skill.skillId}"/>" onclick="return confirm('Are you sure you want to delete this skill?');" class="op">Delete
                    									<i class="halflings-icon white trash"></i> 
                    								</a>
                    							</td>                        				
                						</tr>
            						</c:forEach> 
								</tbody>
							</table>  					            
						</div>
					</div><!--/span-->			
				</div><!--/row-->	
				
				<!-- start: JavaScript-->
		<script src="${pageContext.request.contextPath}/resources/js/jquery-1.9.1.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/jquery-migrate-1.0.0.min.js"></script>	
		<script src="${pageContext.request.contextPath}/resources/js/jquery-ui-1.10.0.custom.min.js"></script>	
		<script src="${pageContext.request.contextPath}/resources/js/jquery.ui.touch-punch.js"></script>	
		<script src="${pageContext.request.contextPath}/resources/js/modernizr.js"></script>	
		<script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>	
		<script src="${pageContext.request.contextPath}/resources/js/jquery.cookie.js"></script>	
		<script src='${pageContext.request.contextPath}/resources/js/fullcalendar.min.js'></script>	
		<script src='${pageContext.request.contextPath}/resources/js/jquery.dataTables.min.js'></script>
		<script src="${pageContext.request.contextPath}/resources/js/excanvas.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/jquery.flot.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/jquery.flot.pie.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/jquery.flot.stack.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/jquery.flot.resize.min.js"></script>	
		<script src="${pageContext.request.contextPath}/resources/js/jquery.chosen.min.js"></script>	
		<script src="${pageContext.request.contextPath}/resources/js/jquery.uniform.min.js"></script>		
		<script src="${pageContext.request.contextPath}/resources/js/jquery.cleditor.min.js"></script>	
		<script src="${pageContext.request.contextPath}/resources/js/jquery.noty.js"></script>	
		<script src="${pageContext.request.contextPath}/resources/js/jquery.elfinder.min.js"></script>	
		<script src="${pageContext.request.contextPath}/resources/js/jquery.raty.min.js"></script>	
		<script src="${pageContext.request.contextPath}/resources/js/jquery.iphone.toggle.js"></script>	
		<script src="${pageContext.request.contextPath}/resources/js/jquery.uploadify-3.1.min.js"></script>	
		<script src="${pageContext.request.contextPath}/resources/js/jquery.gritter.min.js"></script>	
		<script src="${pageContext.request.contextPath}/resources/js/jquery.imagesloaded.js"></script>	
		<script src="${pageContext.request.contextPath}/resources/js/jquery.masonry.min.js"></script>	
		<script src="${pageContext.request.contextPath}/resources/js/jquery.knob.modified.js"></script>	
		<script src="${pageContext.request.contextPath}/resources/js/jquery.sparkline.min.js"></script>	
		<script src="${pageContext.request.contextPath}/resources/js/counter.js"></script>	
		<script src="${pageContext.request.contextPath}/resources/js/retina.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/custom.js"></script>
	<!-- end: JavaScript-->
	
     
</body><br> <br>


<c:if test="${role == 'manager' || role == 'admin' }">  
	<p><a href="UserController?action=dashboard&role=<c:out value="${role}"/>&userId=<c:out value="${userId}"/>" class="link hvr-float-shadow">Dashboard</a></p>
 
 	
    <c:if test="${role == 'admin'}">
		<p><a href="UserController?action=insertUser" class="link hvr-float-shadow">Add New Employee</a></p>
    </c:if>

	<p><a href="UserController?action=addSkill" class="link hvr-float-shadow">Skills</a></p>   
	<p><a href="UserController?action=viewSkill" class="link hvr-float-shadow">View Skills</a></p><br>

</c:if>

<a href="${pageContext.request.contextPath}/home" class="link hvr-float-shadow">Home</a><br></br>

<c:url var="logoutUrl" value="/logout"/>
	<form action="${logoutUrl}" id="logout" method="post">  		
  		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
	</form>
	<a href="#" onclick="document.getElementById('logout').submit();" class="link hvr-float-shadow">Logout</a>
 
</html>