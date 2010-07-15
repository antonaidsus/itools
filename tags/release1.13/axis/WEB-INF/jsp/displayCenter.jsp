<%@ include file="/WEB-INF/jsp/header.jsp" %>

<form method="post"  action="changeCenter.htm"> 
<table width="100%" bgcolor="f8f8ff" border="0" cellspacing="0" cellpadding="5">
    <tr>   <td alignment="right" width="20%">Center Name:</td> </tr>
    <tr> <td alignment="right" width="20%">          
    <INPUT type="text" maxlength="40" size="10" name="name" value="<c:out value="${center.name}"/>"/>     
    </td>  </tr>
    
    <tr>   <td alignment="right" width="20%">Full Name:</td> </tr>
    <tr> <td alignment="right" width="20%">
    <INPUT type="text" maxlength="100" size="100" name="fullName" value="<c:out value="${center.fullName}"/>"/>     
    </td>  </tr>
 
     <tr>   <td alignment="right" width="20%">Home:</td>  </tr>
    <tr> <td alignment="right" width="20%">          
     <INPUT type="text" maxlength="100" size="100" name="homeUrl" value="<c:out value="${center.homeUrl}"/>"/>
    </td>  </tr>
    
    <tr>   <td alignment="right" width="20%">URL:</td>  </tr>
    <tr> <td alignment="right" width="20%">          
     <INPUT type="text" maxlength="100" size="100" name="url" value="<c:out value="${center.url}"/>"/>
    </td>  </tr>
    <tr> <td alignment="right" width="20%"><INPUT type = "submit" name="updateFromUrl" value="Update Center Data From Above URL"/></td></tr>
    <tr>   <td alignment="right" width="20%">External:</td>  </tr>
    <tr> <td alignment="right" width="20%">          
     <INPUT type="checkbox" name="external" <c:if test="${center.external}"> checked="checked" </c:if> />
    </td>  </tr>
    
     </table>
     <INPUT type = "submit" name="update" value="Update"/>
     <INPUT type = "submit" name="delete" value="Delete"/>
     
</form>
<%@ include file="/WEB-INF/jsp/footer.jsp" %>