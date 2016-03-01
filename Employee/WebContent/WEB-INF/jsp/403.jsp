<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/link_style.css">
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/403_style.css">
	<link href="${pageContext.request.contextPath}/resources/css/hover.css" rel="stylesheet" media="all">
	<title>Not Authorized...!</title>
</head>
<body>
<c:url value="/login" var="login" />
<div class="container-fluid">
	<div class="container">
		<div class="errorpanel" style="text-align:center;">
			<img src="${pageContext.request.contextPath}/resources/images/403.png">		
		<h4><a href="${pageContext.request.contextPath}" class="link hvr-float-shadow" style="font-size:20px;">Login</a></h4>
		</div>
	</div>
</div>
</body>
</html>