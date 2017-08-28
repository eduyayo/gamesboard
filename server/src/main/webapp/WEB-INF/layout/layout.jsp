<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<html class="bg-white">
<head>
        <meta charset="UTF-8">
        <title>AdminLTE | Log in</title>
        <meta content='width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no' name='viewport'>
        <!-- bootstrap 3.0.2 -->
        <link href="${pageContext.request.contextPath}/static/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
        <!-- font Awesome -->
        <link href="${pageContext.request.contextPath}/static/css/font-awesome.min.css" rel="stylesheet" type="text/css" />
        <!-- Theme style -->
        <link href="${pageContext.request.contextPath}/static/css/AdminLTE.css" rel="stylesheet" type="text/css" />

        <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
        <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
        <!--[if lt IE 9]>
          <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
          <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
        <![endif]-->

        <!-- jQuery 2.0.2 -->
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/2.0.2/jquery.min.js"></script>
        <!-- Bootstrap -->
        <script src="${pageContext.request.contextPath}/static/js/bootstrap.min.js" type="text/javascript"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery-ui-1.10.3.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/AdminLTE/app.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/AdminLTE/dashboard.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/AdminLTE/demo.js"></script>
        
        
        <!-- Bootstrap -->
        <script src="${pageContext.request.contextPath}/static/js/bootstrap.min.js" type="text/javascript"></script>
        <!-- Morris.js charts -->
        <script src="//cdnjs.cloudflare.com/ajax/libs/raphael/2.1.0/raphael-min.js"></script>
        <script src="${pageContext.request.contextPath}/static/js/plugins/morris/morris.min.js" type="text/javascript"></script>
        <!-- Sparkline -->
        <script src="${pageContext.request.contextPath}/static/js/plugins/sparkline/jquery.sparkline.min.js" type="text/javascript"></script>
        <!-- jvectormap -->
        <script src="${pageContext.request.contextPath}/static/js/plugins/jvectormap/jquery-jvectormap-1.2.2.min.js" type="text/javascript"></script>
        <script src="${pageContext.request.contextPath}/static/js/plugins/jvectormap/jquery-jvectormap-world-mill-en.js" type="text/javascript"></script>
        <!-- jQuery Knob Chart -->
        <script src="${pageContext.request.contextPath}/static/js/plugins/jqueryKnob/jquery.knob.js" type="text/javascript"></script>
        <!-- daterangepicker -->
        <script src="${pageContext.request.contextPath}/static/js/plugins/daterangepicker/daterangepicker.js" type="text/javascript"></script>
        <!-- datepicker -->
        <script src="${pageContext.request.contextPath}/static/js/plugins/datepicker/bootstrap-datepicker.js" type="text/javascript"></script>
        <!-- Bootstrap WYSIHTML5 -->
        <script src="${pageContext.request.contextPath}/static/js/plugins/bootstrap-wysihtml5/bootstrap3-wysihtml5.all.min.js" type="text/javascript"></script>
        <!-- iCheck -->
        <script src="${pageContext.request.contextPath}/static/js/plugins/iCheck/icheck.min.js" type="text/javascript"></script>
        
        <script src="${pageContext.request.contextPath}/static/js/plugins/slimScroll/jquery.slimscroll.min.js" type="text/javascript"></script>
        

        <!-- AdminLTE App -->
        <script src="${pageContext.request.contextPath}/static/js/AdminLTE/app.js" type="text/javascript"></script>

        <!-- AdminLTE dashboard demo (This is only for demo purposes) -->
        <script src="${pageContext.request.contextPath}/static/js/AdminLTE/dashboard.js" type="text/javascript"></script>

        <!-- AdminLTE for demo purposes -->
        <script src="${pageContext.request.contextPath}/static/js/AdminLTE/demo.js" type="text/javascript"></script>
        
        
    <sitemesh:write property="head"/>
</head>
<body class="skin-black pace-done fixed">

<%--
<div class="page">
    <div class="navbar navbar-default" role="navigation">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-ex1-collapse">
                <span class="sr-only"><spring:message code="label.navigation.toggle.navigation.button"/></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <span class="navbar-brand">Spring Social Example</span>
        </div>
        <div class="collapse navbar-collapse navbar-ex1-collapse">
            <ul class="nav navbar-nav navbar-left">
                <sec:authorize access="isAuthenticated()">
                    	<c:url var="url" value="/"></c:url>
                    <li><a href="${url}"><spring:message code="label.navigation.home.link"/></a></li>
                </sec:authorize>
            </ul>
            <ul class="nav navbar-nav navbar-right">
                <sec:authorize access="isAnonymous()">
                    <li><a href="/user/register"><spring:message code="label.navigation.registration.link"/></a></li>
                </sec:authorize>
                <sec:authorize access="isAuthenticated()">
                    <li>
                    	<c:url var="url" value="/logout"></c:url>
                        <form action="${url}" method="POST">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            <button type="submit" class="btn btn-default navbar-btn">
                                <spring:message code="label.navigation.logout.link"/>
                            </button>
                        </form>
                    </li>
                </sec:authorize>
            </ul>
            <sec:authorize access="isAuthenticated()">
                <p class="nav navbar-nav navbar-right navbar-text sign-in-text">
                    <sec:authentication property="principal.socialSignInProvider" var="signInProvider"/>
                    <c:if test="${signInProvider == 'FACEBOOK'}">
                        <i class="icon-facebook-sign"></i>
                    </c:if>
                    <c:if test="${signInProvider == 'TWITTER'}">
                        <i class="icon-twitter-sign"></i>
                    </c:if>
                    <c:if test="${empty signInProvider}">
                        <spring:message code="label.navigation.signed.in.as.text"/>
                    </c:if>
                    <sec:authentication property="principal.username"/>
                </p>
            </sec:authorize>
        </div><!-- /.navbar-collapse -->
    </div>
</div>
 --%>
            <sitemesh:write property="body"/>
</body>
</html>