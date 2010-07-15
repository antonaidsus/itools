<%@ include file="/WEB-INF/jsp/header.jsp" %>

<P>
<H2>Welcome to CCB-iTools Web service page</H2>
<P>
<FORM method="GET" action="webservices/resourcesFinder">
    <B>Key Word</B>
    <INPUT type="hidden" name="method" value="findResources" />
    <BR><INPUT type="text" maxlength="10" size="10" name="keyword"/>
    <BR><BR>
    <B>Category</B>
    <BR><!--INPUT type="text" maxlength="10" size="10" name="category"/-->
    <SELECT name="category" size = 1>
    <OPTION>Name</OPTION>
    <OPTION>Description</OPTION>
    <OPTION>Authors</OPTION>
    <OPTION>Version, Date, Stage</OPTION>
    <OPTION>Keywords</OPTION>
    <OPTION>Implementation Language</OPTION>
    <OPTION>Data Input</OPTION>
    <OPTION>Data Output</OPTION>
    <OPTION>Platforms tested</OPTION>
    <OPTION>License</OPTION>
    <OPTION>Organization</OPTION>
    <OPTION>Development Stage</OPTION>
    <OPTION>URL</OPTION>
    <OPTION>Resource Type</OPTION>
    
    </SELECT>
  
  <P>
  <INPUT type = "submit" value="Search"  />
</FORM>
<P>
<BR>
<%@ include file="/WEB-INF/jsp/footer.jsp" %>