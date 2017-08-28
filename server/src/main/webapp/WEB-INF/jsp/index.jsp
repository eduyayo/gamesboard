<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
<meta charset="UTF-8">
<title>AdminLTE | Dashboard</title>
</head>
<body>
    <div>ok!daads</div>
    <div id="res">res no value</div>
    <script>
    var device = null;
    try{device = eval("deviceApp")} catch(e){};
    document.getElementById("res").innerHTML = "before test";
    if (device && device.onLoggedIn) {
        document.getElementById("res").innerHTML = "before call";
    	device.onLoggedIn();
        document.getElementById("res").innerHTML = "after call";
    } else {
        document.getElementById("res").innerHTML = "nooo";
    	alert("Nooooooooooooooo!");
    }
    </script>
</body>
</html>


<%--
	<html>
	<head>
	<meta charset="UTF-8">
	<title>AdminLTE | Dashboard</title>
	</head>
	<body class="skin-blue">
		<div>
			<a href="${connection.profileUrl}"> <img
				src="${connection.imageUrl}" />
			</a>
		</div>
		<div>
		   <ul>
		   <c:forEach items="${friends}" var="row" >
	        <li>${row.id} ${row.name}</li>
		   </c:forEach>
	       </ul>
		</div>
	</body>
	</html>
 --%>