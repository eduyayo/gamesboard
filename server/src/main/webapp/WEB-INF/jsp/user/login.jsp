<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<html class="bg-black">
    <head>
    </head>
    <body class="skin-black  pace-done">

        <div class="form-box" id="login-box">
            <div class="header">Sign In</div>
            <form action="<c:url value="/login/authenticate"/>" method="post">
            <c:if test="${param.error eq 'bad_credentials'}">
	            <div class="box box-solid box-danger collapsed-box">
                      <div class="box-header">
                          <h3 class="box-title"><spring:message code="text.login.page.login.failed.error"/></h3>
                          <div class="box-tools pull-right">
                              <button class="btn btn-danger btn-sm" data-widget="collapse"><i class="fa fa-minus"></i></button>
                              <button class="btn btn-danger btn-sm" data-widget="remove"><i class="fa fa-times"></i></button>
                          </div>
                      </div>
                      <div class="box-body" style="display: none;">fse fhsei sifus
                          <p>ijoijcsj jssodf sojdsjf osifiojs fijsoifjs jiofsoifjs odfjosijf
<spring:message code="text.login.page.login.failed.error"/>
                          </p>
                      </div><!-- /.box-body -->
                  </div>
            </c:if>
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <div class="body bg-gray">
                    <div class="form-group">
                        <input type="text" name="userid" class="form-control" placeholder="User ID"/>
                    </div>
                    <div class="form-group">
                        <input type="password" name="password" class="form-control" placeholder="Password"/>
                    </div>          
                    <%--
                    <div class="form-group">
                        <input type="checkbox" name="remember_me"/> Remember me
                    </div>
                     --%>
                </div>
                <div class="footer">                                                               
                    <button type="submit" class="btn bg-olive btn-block">Sign me in</button>  
                    
                    <p><a href="#">I forgot my password</a></p>
                    
                    <a href="<c:url value="/user/register"/>" class="text-center">Register a new membership</a>
                </div>
            </form>

            <div class="margin text-center">
                <span>Sign in using social networks</span>
                <br/>
                <a class="btn bg-light-blue btn-circle" href="<c:url value="/auth/facebook"/>"><i class="fa fa-facebook"></i></a>
                <a class="btn bg-aqua btn-circle" href="<c:url value="/auth/twitter"/>"><i class="fa fa-twitter"></i></a>
                <%--
                	<a class="btn bg-red btn-circle" href="<c:url value="/auth/google"/>"><i class="fa fa-google-plus"></i></a>
                 --%>

            </div>
        </div>


        <!-- jQuery 2.0.2 -->
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/2.0.2/jquery.min.js"></script>
        <!-- Bootstrap -->
        <script src="${pageContext.request.contextPath}/static//js/bootstrap.min.js" type="text/javascript"></script>        

    </body>
</html>