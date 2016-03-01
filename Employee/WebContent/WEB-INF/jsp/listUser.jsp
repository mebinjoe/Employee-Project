<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html >
<html lang="en">
<head>
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<link id="bootstrap-style" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">	
	<link href="${pageContext.request.contextPath}/resources/css/bootstrap-responsive.min.css" rel="stylesheet">
	<link id="base-style" href="${pageContext.request.contextPath}/resources/css/style.css" rel="stylesheet">
	<link id="base-style-responsive" href="${pageContext.request.contextPath}/resources/css/style-responsive.css" rel="stylesheet">
	<link href='http://fonts.googleapis.com/css?family=Open+Sans:300italic,400italic,600italic,700italic,800italic,400,300,600,700,800&subset=latin,cyrillic-ext,latin-ext' rel='stylesheet' type='text/css'>	
	<link href="${pageContext.request.contextPath}/resources/css/hover.css" rel="stylesheet" media="all">
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/style_searchbox.css">
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/body_background.css">
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/table_style.css">
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/link_style.css">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">		
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery-1.6.1.min.js"></script>	
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery-2.1.4.js"></script>	    
	<title>View All Employees</title>
</head>

<!-- <script>
$(window).load(function() {
//.......................
});
</script> -->

<script src="https://code.jquery.com/jquery-1.7.1.min.js"></script>
<script type="text/javascript">
	function approve() { 		
 		alert('Approved User Detail Change request');	 	
	}
</script>

<style> /* style for highlighting message mark */
	mark { 
    	background-color: #F77373;
    	color: black;
	}
</style>

<style>
.highlight { background-color: yellow }
</style>

 <!-- <script>  /* refresh the page */   
setTimeout(function(){
	window.location.reload(1);
}, 120000); /* 2min */
</script>  -->


  <!-- Table for Search Box
   -->
  
<body>

<c:if test="${noSearchFound}">
<script>
  alert("No Search Results found");        
</script>
</c:if>



<div style="width: 100%; margin-left: 20px;">
	<p>
  	 <c:if test="${role != 'user'}">  
   		 <form method="GET" action='/employee/UserController' name="search" id="search"> 
	  	 	<input type="hidden" name="action" value="search" /> 
	  	 	<input type="hidden" name="userrole" value="${role}" /> 
		 	<input type="text" id="searchText" name="searchText"  class="searchText" id="searchText"/> &nbsp;&nbsp;&nbsp;
		    <select  name="searchType" id="searchType" class="searchType">
		    		<option value="0">-Select-</option>
					<option value="1">First Name</option>
					<option value="2">Last Name</option> 
					<option value="3">Email</option>
					<option value="4">Department Name</option>
					<option value="5">Department Manager</option>
					<option value="skill">Skill Name</option>
					</select>&nbsp;&nbsp;&nbsp;
			<input name="search" id="submit" type="submit" class="submitSearch" value="Search" />
		  	<div id="myDiv"></div>
		    <br><br>
	    </form> 
	</c:if>	 
	</p>
</div>   


	<!-- ***
	***Table for user list for admin and manager	
 	*** -->
 <c:if test="${role == 'admin' || role == 'manager'}">  
 	<div class="row-fluid sortable" style= "margin-left: 20px;">		
		<div class="box span11" style="width:1280px;">
			<div class="box-header" data-original-title>
				<h2><i class="halflings-icon white user"></i><span class="break"></span>Employees</h2>		
			</div>
			<div class="box-content">
				<table class="table table-striped table-bordered bootstrap-datatable datatable">
					<thead>
						<tr>
							<th>First Name</th>
                			<th>Last Name</th>
                			<th>DOB</th>
                			<th>Email</th>                
                			<th>Department Name</th>
                			<th>Department Manager</th> 
               				<th>UserName</th>                				                                      
                			<th style="text-align:center">Operation</th>                             
                			<th style="text-align:center">Skills</th>
						</tr>
					</thead>   
					<tbody>
						<c:forEach items="${users}" var="user"> 
							<tr>
								<td><c:out value="${user.firstName}" /></td>
								<td class="center"><c:out value="${user.lastName}" /></td>
                    			<td class="center"><fmt:formatDate pattern="yyyy-MMM-dd" value="${user.dob}" /></td>
                    			<td class="center"><c:out value="${user.email}" /></td>
                    			<td class="center"><c:out value="${user.deptName}" /></td> 
                    			<td class="center"><c:out value="${user.deptManager}" /></td>
                    			<td class="center"><c:out value="${user.userName}" /></td> 	
                    			 
                    			<td class="center">	                  
                    				<a class="btn btn-info" href="UserController?action=editUser&userId=<c:out value="${user.userId}"/>" >Update
                    					<i class="halflings-icon white edit"></i> 
                    				</a> 
                     				<a class="btn btn-danger" href="UserController?action=deleteUser&userId=<c:out value="${user.userId}"/>" onclick="return confirm('Are you sure you want to delete this user ?');">Delete
                     					<i class="halflings-icon white trash"></i> 
                     				</a>                     				
                     			</td>
                     			<td class="center">	 
                    				<a class="btn btn-success" href="UserController?action=viewSkill&Id=<c:out value="${user.userId}"/>" >View Skills
                    					<i class="halflings-icon white th-list"></i> 
                    				</a>
                    			</td>                    				                   		
							</tr>	 						
						</c:forEach> 							
					</tbody>							
				</table>            
			</div>
		</div><!--/span-->			
	</div><!--/row-->
</c:if>
  
  
  <!-- ***
	***Table for user list for user	
 	*** -->
    
 <c:if test="${role == 'user'}"> 
 	<div class="box span11" style="width:1200px; margin-right:70px;">
		<div class="box-header">
			<h2><i class="halflings-icon white align-justify"></i><span class="break"></span>User Details</h2>						
		</div>
		<div class="box-content">
			<table class="table table-condensed">
				<thead>
					<tr>
						<th>First Name</th>
                <th>Last Name</th>
                <th>DOB</th>
                <th>Email</th>                
                <th>Department Name</th>
                <th>Department Manager</th> 
               	<th>UserName</th>                 
                <th>Operation</th>                       
			</tr>
		</thead>   
		<tbody>
			<c:forEach items="${users}" var="user"> 
				<tr>
					<td><c:out value="${user.firstName}" /></td>
					<td class="center"><c:out value="${user.lastName}" /></td>
                    <td class="center"><fmt:formatDate pattern="yyyy-MMM-dd" value="${user.dob}" /></td>
                    <td class="center"><c:out value="${user.email}" /></td>
                    <td class="center"><c:out value="${user.deptName}" /></td> 
                    <td class="center"><c:out value="${user.deptManager}" /></td>
                    <td class="center"><c:out value="${user.userName}" /></td> 
								
					<td class="center">
                    	<a class="btn btn-info" href="UserController?action=editUser&userId=<c:out value="${user.userId}"/>" >Update
                    		<i class="halflings-icon white edit"></i> 
                    	</a>
                    </td>  		                   		
				</tr>							
			</c:forEach>                                
		</tbody>
	</table>  						    
</div>
</div>
</c:if>

<br></br>
    
    
	 
<script>
$(document).on('click', 'button.export', function () { 

	$.ajax({
		type: "Get",
		url: "../employee/exportContacts",
		data: { action : 'exportContacts'},
		success: function() {
			alert('Data Exported Successfully');
		},		    
		error: function(xhr, thrownError) {
			alert('Error ' + thrownError); 
		}
	});	
 });
 
$(document).on('click', 'button.import', function () { 

	$.ajax({
		type: "Get",
		url: "../employee/importContacts",
		data: { action : 'importContacts'},
		success: function() {
			//alert('Data Imported Successfully');
		},		    
		error: function(xhr, thrownError) {
			alert('Error ' + thrownError); 
		}
	});	
 });
</script>

<br></br>	
<p><a href="UserController?action=dashboard&role=<c:out value="${role}"/>&userId=<c:out value="${userId}"/>" class="link hvr-float-shadow">Dashboard</a></p>
 
 	
    <c:if test="${role == 'admin'}">
		<p><a href="UserController?action=insertUser" class="link hvr-float-shadow">Add New Employee</a></p>
    </c:if>

	<p><a href="UserController?action=insertSkill" class="link hvr-float-shadow">Skills</a></p>   
	<p><a href="UserController?action=viewSkill" class="link hvr-float-shadow">View Skills</a></p><br>     	
  	
  	
	<br><c:url var="logoutUrl" value="/logout"/>
	<form action="${logoutUrl}" id="logout" method="post">  		
  		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
	</form>
	<a href="#" onclick="document.getElementById('logout').submit();" class="link hvr-float-shadow">Logout</a>
	
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

    
</body>
</html>