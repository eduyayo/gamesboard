<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>



<html class="bg-black">
    <head>
        <title>Registration Page</title>
    </head>
    <body class="bg-black">

        <div class="form-box" id="login-box">
            <div class="header">Register New Membership</div>
               <c:url value="/user/register" var="url"/>
               <form:form action="${url}" commandName="user" method="POST" enctype="utf8" role="form">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <c:if test="${user.signInProvider != null}">
                        <form:hidden path="signInProvider"/>
                    </c:if>
                <div class="body bg-gray">
                    <div id="form-group-firstName" class="form-group">
                        <label class="control-label" for="user-firstName"><spring:message code="label.user.firstName"/>:</label>
                        <form:input id="user-firstName" path="firstName" cssClass="form-control"/>
                        <form:errors id="error-firstName" path="firstName" cssClass="help-block"/>
                    </div>
                    <div id="form-group-lastName" class="form-group">
                        <label class="control-label" for="user-lastName"><spring:message code="label.user.lastName"/>:</label>
                        <form:input id="user-lastName" path="lastName" cssClass="form-control"/>
                        <form:errors id="error-lastName" path="lastName" cssClass="help-block"/>
                    </div>
                    <div id="form-group-email" class="form-group">
                        <label class="control-label" for="user-email"><spring:message code="label.user.email"/>:</label>
                        <form:input id="user-email" path="email" cssClass="form-control"/>
                        <form:errors id="error-email" path="email" cssClass="help-block"/>
                    </div>
                    <c:if test="${user.signInProvider == null}">
                         <div id="form-group-password" class="form-group ">
                             <label class="control-label" for="user-password"><spring:message code="label.user.password"/>:</label>
                             <form:password id="user-password" path="password" cssClass="form-control"/>
                             <form:errors id="error-password" path="password" cssClass="help-block"/>
                         </div>
                         <div id="form-group-passwordVerification" class="form-group ">
                             <label class="control-label" for="user-passwordVerification"><spring:message code="label.user.passwordVerification"/>:</label>
                             <form:password id="user-passwordVerification" path="passwordVerification" cssClass="form-control"/>
                             <form:errors id="error-passwordVerification" path="passwordVerification" cssClass="help-block"/>
                         </div>
                    </c:if>
                </div>
                <div class="footer">                    

                    <button type="submit" class="btn bg-olive btn-block">Sign me up</button>

                    <a href="<c:url value="/login"/>" class="text-center">I already have a membership</a>
                </div>
            </form:form>

            <div class="margin text-center">
                <span>Register using social networks</span>
                <br/>
                <button class="btn bg-light-blue btn-circle"><i class="fa fa-facebook"></i></button>
                <button class="btn bg-aqua btn-circle"><i class="fa fa-twitter"></i></button>
                <%-- 
                <button class="btn bg-red btn-circle"><i class="fa fa-google-plus"></i></button>
                 --%>

            </div>
        </div>


        <!-- jQuery 2.0.2 -->
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/2.0.2/jquery.min.js"></script>
        <!-- Bootstrap -->
        <script src="../../js/bootstrap.min.js" type="text/javascript"></script>

    </body>
</html>