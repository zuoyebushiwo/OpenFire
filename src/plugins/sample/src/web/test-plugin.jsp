<%@ page
	import="java.util.*,
			org.jivesoftware.openfire.XMPPServer,
			org.jivesoftware.util.*,
			com.test.plugin.TestPlugin"
	errorPage="error.jsp"%>

<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt"%>

<%-- Define Administration Bean --%>
<jsp:useBean id="admin" class="org.jivesoftware.util.WebManager" />
<c:set var="admin" value="${admin.manager}" />
<%
	admin.init(request, response, session, application, out);
	String path = request.getContextPath();
	System.out.println("path= " + path);
%>

<html>
<head>
<title>User Service Properties</title>
<meta name="pageID" content="test_plugin" />
</head>
<body>
	<p>==============================================</p>
	<form action="<%=path%>/plugins/sample/test">
		<fieldset>
			<legend>test plugin</legend>
			<div>
				<input type="text" size="15" /><br> 
				<input type="text" size="15" /><br>
			</div>
		</fieldset>
		<br> <br> <input type="submit" value="Save Settings">
	</form>
</body>
</html>