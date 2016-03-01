<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/link_style.css">	
	<link href="${pageContext.request.contextPath}/resources/css/hover.css" rel="stylesheet" media="all">
	<title>Not Authorized...!</title>
</head>
<style>
html, body {
    position:fixed;
    top:0;
    bottom:0;
    left:0;
    right:0;
    transform: scale(0.97);
   	transform-origin: -180% -60%;
}

h4 {
	font-family: verdana;
	color: #B45B5B;
	font-size: 15px;
}
</style>
<body>
<c:url value="/login" var="login" />
<div class="errorpanel" style="text-align:center;">			
	<h4>Click Here to <a href="${pageContext.request.contextPath}" class="link hvr-float-shadow" style="font-size:25px;">Login</a></h4>
</div>
</body>
</html>