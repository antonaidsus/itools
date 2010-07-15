<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core' %>
<%@ page import="org.acegisecurity.ui.AbstractProcessingFilter" %>
<%@ page import="org.acegisecurity.ui.webapp.AuthenticationProcessingFilter" %>
<%@ page import="org.acegisecurity.AuthenticationException" %>

<html>
  <head>
    <title>Login</title>
    <SCRIPT language="JAVASCRIPT" TYPE="TEXT/JAVASCRIPT">
    function forgetPassword() {
        window.location.href="forgetPassword.jsp";
    }
    function createAccount() {
        window.location.href="displayUser.jsp";
    }
    </SCRIPT>
    
  </head>

  <body>
    <h1>Login</h1>

    <%-- this form-login-page form is also used as the 
         form-error-page to ask for a login again.
         --%>
    <c:if test="${not empty param.login_error}">
      <font color="red">
        Your login attempt was not successful, try again.<BR><BR>
        Reason: <%= ((AuthenticationException) session.getAttribute(AbstractProcessingFilter.ACEGI_SECURITY_LAST_EXCEPTION_KEY)).getMessage() %>
      </font>
    </c:if>

    <form action="<c:url value='j_acegi_security_check'/>" method="POST">
      <table>
        <tr>
        <td>User:</td>
        <td colspan='2'>
        <input type='text' name='j_username' <c:if test="${not empty param.login_error}">value='<%= session.getAttribute(AuthenticationProcessingFilter.ACEGI_SECURITY_LAST_USERNAME_KEY) %>'</c:if>>
        </td>
        <td> </td>
        </tr>
        <tr>
        <td>Password:</td>
        <td colspan='2'><input type='password' name='j_password'></td>
        <td> </td>
        </tr>
        <tr>
        <td>  </td>
        <td> <input name="submit" type="submit" value="login"></td>
        <td> <input name="Create a new Account" value="Create a new Account" type="button" onclick="createAccount()"></td>
        <td> <input name="Forget Password?" value="Forget Password?" type="button" onclick="forgetPassword()"> </td>
        </tr>
        
      </table>

    </form>

  </body>
</html>
