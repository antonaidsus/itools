<%@ include file="/WEB-INF/jsp/header.jsp" %>
<form method="post" action="admin.htm"> 
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
      
    <tr>   <td alignment="right" width="20%">Role:</td>  </tr>
    <tr> <td alignment="right" width="20%">          
    <INPUT type="text" maxlength="40" size="10" name="role" value="<c:out value="${user.role}"/>"/>
    </td>  </tr>
    
    <tr>   <td alignment="right" width="20%"><INPUT type = "submit" value="Submit"/></td>  </tr>
    </table>
</form>
<%@ include file="/WEB-INF/jsp/footer.jsp" %>