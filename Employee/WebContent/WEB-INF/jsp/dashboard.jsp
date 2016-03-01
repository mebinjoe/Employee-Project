<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <title>Dashboard</title>
        <meta name="viewport" content="initial-scale=1.0, user-scalable=no">  
        <meta name="viewport" content="width=device-width, initial-scale=1">              
        <!-- <meta http-equiv="cache-control" content="max-age=0" />
		<meta http-equiv="cache-control" content="no-cache" />
		<meta http-equiv="expires" content="0" />
		<meta http-equiv="pragma" content="no-cache" />	 -->			
		<link id="bootstrap-style" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">	
		<link href="${pageContext.request.contextPath}/resources/css/bootstrap-responsive.min.css" rel="stylesheet">
		<link id="base-style" href="${pageContext.request.contextPath}/resources/css/style.css" rel="stylesheet">
		<link id="base-style-responsive" href="${pageContext.request.contextPath}/resources/css/style-responsive.css" rel="stylesheet">
		<link href='http://fonts.googleapis.com/css?family=Open+Sans:300italic,400italic,600italic,700italic,800italic,400,300,600,700,800&subset=latin,cyrillic-ext,latin-ext' rel='stylesheet' type='text/css'>	
        <link href="${pageContext.request.contextPath}/resources/css/hover.css" rel="stylesheet" media="all">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/body_background.css">
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/table_style.css">
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/link_style.css">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">		
		<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery-1.6.1.min.js"></script>	
		<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery-2.1.4.js"></script> 
		<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jscharts.js?version=1"></script>  
		<script src="https://code.jquery.com/jquery-1.7.1.min.js"></script>
    </head> 
    
<!--  <script>    
 $(document).ready( function(){
	    $('#alertsTable').dataTable({
	  "aLengthMenu": [[10, 25, 50, 100], ["10 Per Page", "25 Per Page", "50 Per Page", "100 Per Page"]]
	    });
	});
</script>  -->
    
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

<body> 
	<table id="dashboard" class="dashboard" cellspacing="0" cellpadding="0">  	 <!-- main table dashboard -->
   		<tr>
    		<th colspan=2 class="heading">Dashboard</th>
    	</tr>
    	<td>&nbsp;</td>
    	
    	<tr>	    																<!-- bar chart --> 
    		<td valign="top">
    			<div id="graph" style= "margin-left: 20px;">   
					<script type="text/javascript">					
						$(document).ready(function(){
							$.ajaxSetup({ cache: false });
							$.ajax({
								type: "GET",								
								contentType: "application/xml",
								datatype: "xml",
								cache: false,
								error: function(jqXHR, textStatus, errorThrown) {
									console.log('Error fetching xml : ' + errorThrown);
								},
								success: function(xml) {
									var myChart = new JSChart('graph', 'bar');
									myChart.setDataXML('${pageContext.request.contextPath}/resources/xml/data.xml');
									myChart.setAxisNameX('Number of Days');
									myChart.setAxisNameY('Number of Requests');
									myChart.setAxisPaddingLeft(50);
									myChart.setAxisNameColor('#655D5D');
									myChart.setAxisNameFontSize(9);
									/* myChart.setAxisValuesDecimals(1); */
									myChart.setAxisValuesColor('#9C1919');
									myChart.setTitle('Request Count Status');
									 myChart.setTitleColor('#333');
									myChart.setBarSpacingRatio(80);
									/* myChart.setTitleColor('#B6C2D0'); */
									/* 	myChart.setGridColor('#5D5F5D'); */										
									myChart.draw();
								}
							});					
					 });					
					</script>					
				</div>
    		</td>
    		
    		<td valign="top">   																		<!-- notificatios table --> 		 
    			<div class="row-fluid sortable" style= "margin-left: 0px;" >		
					<div class="box span11" style="width:600px;">
						<div class="box-header">				
							<h2><i class="halflings-icon bell"></i><span class="break"></span>Notifications</h2>
						</div>
						<div class="box-content">
							<table class="table table-striped table-bordered bootstrap-datatable datatable" id=alertsTable">
								<thead>
									<tr> 
          								<th>Notifications/Alerts</th>
            						</tr>
								</thead>   
								<tbody>
									<c:forEach items="${notifications}" var="inbox">
    									<tr>
    										<%-- <td class="inboxId" style="display:none">${inbox.inboxId}</td> 
    										<td class="userId" style="display:none">${inbox.userId}</td>   --%>
    										<input type="hidden" name="inboxId" value="${inbox.inboxId}" />
    										<input type="hidden" name="userId" value="${inbox.userId}" />     					
    						
        									<c:choose>
        										<c:when test="${inbox.isRejected == 0}"> 	
        											<td> <input type="hidden" name="message" />${inbox.message}</td>
        										</c:when>
        										<c:otherwise>
        											<td> <input type="hidden" name="message" /><mark>${inbox.message}</mark></td>        						
        										</c:otherwise>
        									</c:choose>        					
        								</tr>        				
        							</c:forEach>
								</tbody>
							</table>  					            
						</div>
					</div><!--/span-->			
				</div><!--/row-->		
    		</td>    		
  		</tr>
  		
		<td>&nbsp;</td>		
	
			
  		<tr>	  																<!-- inbox table -->  	
			<td valign="top">			
    			<div class="row-fluid sortable" style= "margin-left: 20px;" >		
					<div class="box span11" style="width:600px;">
						<div class="box-header">				
							<h2><i class="halflings-icon inbox"></i><span class="break"></span>Inbox</h2>
						</div>
						<div class="box-content">
							<table class="table table-striped table-bordered bootstrap-datatable datatable">
								<thead>
									<tr> 
          								<th>Messages</th>
            						</tr>
								</thead>   
								<tbody>
									<c:forEach items="${inboxList}" var="inbox">
    									<tr>
    										<%-- <td class="inboxId" style="display:none">${inbox.inboxId}</td> 
    										<td class="userId" style="display:none">${inbox.userId}</td>   --%>
    										<input type="hidden" name="inboxId" value="${inbox.inboxId}" />
    										<input type="hidden" name="userId" value="${inbox.userId}" /> 
      		    				
    										<c:if test="${inbox.isRejected == 0}">  
    											<c:if test="${inbox.type == 'userRequest' || inbox.type == 'isUserInfo'}"> 
    												<td>
    													<a href="UserController?action=editUserRequest&userId=<c:out value="${inbox.userId}"/>&inboxId=<c:out value="${inbox.inboxId}"/>" class="op">    						 
    														<input type="hidden" name="message" />${inbox.message}
    													</a>
    												</td>
    											</c:if>
    											<c:if test="${inbox.type == 'skillRequest' || inbox.type == 'isSkill'}"> 
        											<td> 
        												<a href="UserController?action=editSkill&skillId=<c:out value="${inbox.skillId}"/>&inboxId=<c:out value="${inbox.inboxId}"/>&userId=<c:out value="${inbox.userId}"/>"
        									 			class="op">
        													<input type="hidden" name="message" />${inbox.message}
        												</a>
        											</td>  
        										</c:if> 
    										</c:if>
    										<c:if test="${inbox.isRejected == 1}">     						  					
    											<c:if test="${inbox.type == 'userRequest' || inbox.type == 'isUserInfo'}">    
    												<td>     								
        												<a href="UserController?action=editUserRequest&userId=<c:out value="${inbox.userId}"/>&inboxId=<c:out value="${inbox.inboxId}"/>" class="op">
        													<input type="hidden" name="message" /><mark>${inbox.message} </mark>
        												</a>
        											</td>
        										</c:if>
        										<c:if test="${inbox.type == 'skillRequest' || inbox.type == 'isSkill'}"> 
        											<td>        								
        												<a href="UserController?action=editRejectedSkill&skillId=<c:out value="${inbox.skillId}"/>&inboxId=<c:out value="${inbox.inboxId}"/>&userId=<c:out value="${inbox.userId}"/>"
        									 				class="op">
        													<input type="hidden" name="message" /><mark>${inbox.message} </mark>
        												</a>
        											</td>  
        										</c:if>       				   
        									</c:if>  
										</tr>	        						
        							</c:forEach> 
								</tbody>
							</table>  					            
						</div>
					</div><!--/span-->			
				</div><!--/row-->	
			</td>
    	
    	<c:if test="${role == 'manager' || role == 'admin'}">  
			<td valign="top"> 																			<!-- request table -->									
				<div class="row-fluid sortable" >		
					<div class="box span10" style="width:600px;">
						<div class="box-header">				
							<h2><i class="halflings-icon envelope"></i><span class="break"></span>Requests</h2>
						</div>
						<div class="box-content">
							<form method="GET" action='/employee/UserController' name="approveRequest" > 
								<input type="hidden" name="action" value="approveRequest" />
								<table class="table table-striped table-bordered bootstrap-datatable datatable" align="center" width="503px">
									<thead>
										<c:if test="${role == 'manager' || role == 'admin'}"> 
											<th width="320px" style="text-align:center;">Requests</th>
											<th style="text-align:center;">Approve/Reject</th>                
										</c:if>
									</thead>   
									<tbody>
										<c:forEach items="${requests}" var="inbox">
											<tr>
												<%-- <td class="inboxId" style="display:none">${inbox.inboxId}</td> 
												<td class="userId" style="display:none">${inbox.userId}</td>  
												<td class="skillId" style="display:none">${inbox.skillId}</td>  --%>
												<input type="hidden" id="skillId" name="skillId" value="${inbox.skillId}" />
												<input type="hidden" id="inboxId" name="inboxId" value="${inbox.inboxId}" />
												<input type="hidden" name="userId" value="${inbox.userId}" />  
    					    						
												<c:if test="${role == 'manager' || role == 'admin'}">         		 			<!-- skill requests -->
													<c:if test="${inbox.type == 'skillRequest'}">         				
														<td style="margin-left:-40px;">											
															<input type="hidden" name="message" />${inbox.message}									
														</td>
														<td style="text-align:right;">        								
															<button class="btn btn-success" type="button" id="approveSkill">Approve
																<i class="fa-icon-check"></i> 
															</button> &nbsp; 
															<button class="btn btn-danger" type="button" id="rejectSkill" >Reject
																<i class="halflings-icon white trash"></i> 
															</button>    								
														</td>        			
													</c:if>        					
												</c:if>    
											</tr>				
										</c:forEach>

										<c:forEach items="${messageList}" var="msg">						<!-- message requests -->
											<c:if test="${msg.type == 'userRequest'}"> 
												<tr>  
													<%-- <td class="inboxId" style="display:none">${msg.inboxId}</td>  
													<td class="userId" style="display:none">${msg.userId}</td>    --%>										
													<input type="hidden" id=inbxId" name="inboxId" value="${msg.inboxId}" />
													<input type="hidden" id="usrId" name="userId" value="${msg.userId}" />       						
													<td style="float:left; margin-left:-40px;">
														<c:out value="${msg.str1}" /><mark><c:out value="${msg.str2}" /></mark><c:out value="${msg.str3}" />									
													</td>	
													<td style="text-align:right;">
														<input class="btn btn-success" type="submit" name="Approve" value="Approve" onClick="return approve();" > &nbsp;
															<span class="fa-icon-check"></span>																						
														<button class="btn btn-danger" type="button" name="action" value="rejectRequest" >Reject
																<i class="halflings-icon white trash"></i> 
														</button>
													</td>  
												</tr>
											</c:if>	    											
										</c:forEach>
									</tbody>
								</table> 
							</form>							            
						</div>
					</div><!--/span-->			
				</div><!--/row-->			
			</td>
		</c:if>
		
		</tr>		
	</table>
        	 		
 <script>
 $(document).on('click', '#approveSkill', function () {
	var skillId = $('#skillId').val();
	var inboxId = $('#inboxId').val();	
	$.ajax({
		type: "GET",
		url: "../employee/approveSkill",
		data: {skillId : skillId , inboxId : inboxId},
		success: function() {
			alert('Skill Approved');
			window.location.reload(true);
		},		    
		error: function(xhr, thrownError)	{
			alert('Error ' + thrownError); 
		}
	});
  $(this).closest('tr').remove();
 });

$(document).on('click', '#rejectSkill', function () { 
	var skillId = $('#skillId').val();
	var inboxId = $('#inboxId').val();
	$.ajax({
		type: "GET",
	    url: "../employee/rejectSkill",
	    data: {skillId : skillId , inboxId : inboxId},
	    success: function() {
	    	alert('Skill Rejected');
	    	window.location.reload(true);
		},		    
		error: function(xhr, thrownError)	{
			alert('Error ' + thrownError); 
		}
	});
  $(this).closest('tr').remove();
 });
</script>

        	 		
</body>
    
    <br></br>
    
 <script>
$(document).on('click', 'button.rejectRequest', function () { 
	var inboxId = $('#inbxId').val();	
	var userId = $('#usrId').val();	
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
	$(this).closest('tr').remove();	
 });
</script>
 
<a href="${pageContext.request.contextPath}/home" class="link hvr-float-shadow">Home</a><br></br>   
    
<c:url var="logoutUrl" value="/logout"/>
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
    
</html>

