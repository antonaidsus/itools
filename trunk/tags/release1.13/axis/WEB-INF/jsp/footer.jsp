
	<hr>
	<table style="width:100%"><tr>
		<td><A href="index.jsp"/>Home</A></td>
		<authz:authorize ifAnyGranted="ROLE_NORMALUSER,ROLE_EXPERTUSER,ROLE_ADMINISTRATOR">
		<td><A href="<c:url value="/j_acegi_logout"/>">Logout</A></td>
		</authz:authorize>
		<td style="text-align:right;color:silver">NCBC-iTools :: A framework for dynamic graphical management of NCBC resources</td>
	</tr></table>	
	</body>
</html>