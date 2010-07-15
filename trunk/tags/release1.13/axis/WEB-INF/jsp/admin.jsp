<%@ include file="/WEB-INF/jsp/header.jsp" %>
<H2> Welcome to NCBC iTOOls Administer Page </H2>

<p>
<form method="post" action="displayUser.htm"> 
<INPUT type = "submit"  size="50" value="Modify Profile For User"  />
<SELECT  name="name" size="1">
<c:forEach var="username" items="${users}">
<Option> <c:out value="${username}" /> </OPTION>
</c:forEach>
</SELECT>
</form>

<p>
<form method="post" action="displayCenter.htm"> 
<INPUT type = "submit"   size="50" value="Modify Center"  />
<SELECT  name="center" size="1">
<c:forEach var="centername" items="${centers}">
<Option> <c:out value="${centername.name}" /> </OPTION>
</c:forEach>
</SELECT>
</form>

<form method="post" action="restrictedResource.htm"> 
<INPUT type = "submit"  size="50" value="Update Ontology From"  />
<INPUT type="text" maxlength="100" size="100" name="urlStr" value="<c:out value="${ontologyUrl}"/>"/>
<INPUT type="hidden" name="method" value="updateOntology"/>

</form>

<br><p>
<H3>BiositeMap Search</H3>
<UL>
<LI><A href="websearch.htm?method=startSearch">Manually start to search BioSiteMap files.</A>
<LI><A href="websearch.htm?method=startSearch&forceupdate=true">Manually start to search BioSiteMap files and <b>force update</b>.</A>
<LI><A href="websearch.htm?method=getBioSiteMapHistory">Search History</A>
</UL>
<%@ include file="/WEB-INF/jsp/footer.jsp" %>