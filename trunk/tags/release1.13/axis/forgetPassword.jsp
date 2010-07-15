<%@ include file="/WEB-INF/jsp/header.jsp" %>

<p> 
Please enter the user name and/or the email.
<p>
<form method="post" action="forgetPassword.htm"> 
<table width="100%" bgcolor="f8f8ff" border="0" cellspacing="0" cellpadding="5">
    <tr>   <td alignment="right" width="20%">User Name:</td>   
    </tr>
    <tr> <td alignment="right" width="20%">          
    <INPUT type="text" maxlength="40" size="10" name="name"/>     
    </td>  </tr>
 
     <tr>   <td alignment="right" width="20%">Email:</td>  </tr>
    <tr> <td alignment="right" width="20%">          
     <INPUT type="text" maxlength="40" size="20" name="email"/>
    </td>  </tr>
        
     <tr>   <td alignment="right" width="20%"><INPUT type = "submit" value="Submit"/></td>  </tr>
    </table>
</form>
<%@ include file="/WEB-INF/jsp/footer.jsp" %>