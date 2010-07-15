<%@ include file="/WEB-INF/jsp/header.jsp" %>
<SCRIPT language="JAVASCRIPT" TYPE="TEXT/JAVASCRIPT">
  function validForm(passForm) {
      if (passForm.passwd.value != passForm.passwd2.value) {       
          alert("Entered Password did not match");
          passForm.passwd.focus();
          passForm.passwd.select();
          return false;
      }
      return true;
  }
</SCRIPT>

<form method="post"  action="changeUser.htm" onsubmit="return validForm(this)"> 
<INPUT type="hidden" name="id" value="<c:out value="${user.id}"/>" /> 
<INPUT type="hidden" name="new" value="<c:if test="${user==null}">yes</c:if>"/>
<table width="100%" bgcolor="f8f8ff" border="0" cellspacing="0" cellpadding="5">
    <tr>   <td alignment="right" width="20%">User Name:</td>   
    </tr>
    <tr> <td alignment="right" width="20%">          
    <INPUT type="text" maxlength="40" size="10" name="name" value="<c:out value="${user.name}"/>"/>     
    </td>  </tr>
 
     <tr>   <td alignment="right" width="20%">Email:</td>  </tr>
    <tr> <td alignment="right" width="20%">          
     <INPUT type="text" maxlength="40" size="20" name="email" value="<c:out value="${user.email}"/>"/>
    </td>  </tr>
    
    <tr>   <td alignment="right" width="20%">New Password:</td>  </tr>
    <tr> <td alignment="right" width="20%">          
     <INPUT type="password" maxlength="40" size="20" name="passwd"/>
    </td>  </tr>
    
    <tr>   <td alignment="right" width="20%">Confirm Password:</td>  </tr>
    <tr> <td alignment="right" width="20%">          
     <INPUT type="password" maxlength="40" size="20" name="passwd2"/>
    </td>  </tr>
    
    <c:if test="${!user.administrator}">  
    <tr>   <td alignment="right" width="20%">Role:</td>  </tr>
    <tr> <td alignment="right" width="20%">          
    <SELECT name="role" size="1">
        <OPTION <c:if test="${!user.expertUser}"> SELECTED </c:if>>ROLE_NORMALUSER</OPTION>
        <OPTION <c:if test="${user.expertUser}"> SELECTED </c:if>>ROLE_EXPERTUSER</OPTION>
    </SELECT>
    </td>  </tr>
    </c:if>
    <tr>   <td alignment="right" width="20%"><INPUT type = "submit" value="Submit""/></td>  </tr>
    </table>
</form>
<%@ include file="/WEB-INF/jsp/footer.jsp" %>