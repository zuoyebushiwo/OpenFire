<%@ page
	import="java.util.*,
			org.jivesoftware.openfire.XMPPServer,
			org.jivesoftware.util.*,
			com.httpservice.plugin.HttpServicePlugin"
	errorPage="error.jsp"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt"%>
<%-- Define Administration Bean --%>
<jsp:useBean id="admin" class="org.jivesoftware.util.WebManager" />
<c:set var="admin" value="${admin.manager}" />
<%
	admin.init(request, response, session, application, out);
%>
<%
	// Get parameters
	boolean save = request.getParameter("save") != null;
	boolean success = request.getParameter("success") != null;
	String secret = ParamUtils.getParameter(request, "secret");
	boolean enabled = ParamUtils
			.getBooleanParameter(request, "enabled");
	String smsAddrs = ParamUtils.getParameter(request, "smsAddrs");
	//注意：这里getPlugin("httpedu")里面的参数是插件的目录名称
	HttpServicePlugin plugin = (HttpServicePlugin) XMPPServer
			.getInstance().getPluginManager().getPlugin("httpedu");
	System.out.println("111");
	// Handle a save
	Map errors = new HashMap();
	if (save) {
		if (errors.size() == 0) {
			plugin.setEnabled(enabled);
			plugin.setSecret(secret);
			plugin.setSmsAddrs(smsAddrs);
			response.sendRedirect("http-service.jsp?success=true");
			return;
		}
	}
	System.out.println("222");
	secret = "123";
	System.out.println("33");
	enabled = true;
	smsAddrs = "";
%>
<html>
<head>
<title>HTTP Service Properties</title>
<meta name="pageID" content="http-service" />
</head>
<body>
	<p>This is an HTTP service plug-in it is mainly to complete
		business push message center system and off-line message interface
		implementation</p>
	<%
		if (success) {
	%>
	<div class="jive-success">
		<table cellpadding="0" cellspacing="0" border="0">
			<tbody>
				<tr>
					<td class="jive-icon"><img src="images/success-16x16.gif"
						width="16" height="16" border="0"></td>
					<td class="jive-icon-label">HTTP service properties edited
						successfully.</td>
				</tr>
			</tbody>
		</table>
	</div>
	<br>
	<%
		}
	%>
	<form action="http-service.jsp?save" method="post">
		<fieldset>
			<legend>HTTP Service</legend>
			<div>
				<p>This main set message center call switch, text messaging and
					SMS address configuration</p>
				<ul>
					<input type="radio" name="enabled" value="true" id="hrb01" <%=((enabled) ? "checked" : "")%>>
					<label for="hrb01"><b>Enabled</b> - HTTP service requests will be processed.</label>
					<br>
					<input type="radio" name="enabled" value="false" id="hrb02" <%=((!enabled) ? "checked" : "")%>>
					<label for="hrb02"><b>Disabled</b> - HTTP service requests will be ignored.</label>
					<br>
					<br>
					<label for="h_text_secret">Secret key:</label>
					<input type="text" name="secret" value="<%=secret%>" id="h_text_secret">
					<br>
					<br>
					<label for="h_smsAddrs">SMS address:</label>
					<input type="text" id="h_smsAddrs" name="smsAddrs" size="80" value="<%=smsAddrs == null ? "" : smsAddrs%>" />
				</ul>
			</div>
		</fieldset>
		<br> <br> <input type="submit" value="Save Settings">
	</form>
</body>
</html>