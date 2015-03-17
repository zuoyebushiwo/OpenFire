package com.httpservice.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jivesoftware.admin.AuthCheckFilter;
import org.jivesoftware.openfire.RoutingTable;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserFactory;

import com.httpservice.plugin.HttpServicePlugin;
import com.httpservice.util.SendMessageUtil;
import com.httpservice.util.ThreadPool;

/**
 * @author ZhangXueJun
 * @date 2015年3月17日
 */
public class HttpServiceServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private HttpServicePlugin plugin;
	private RoutingTable routingTable;
	private SendMessageUtil sendMessageUtil;
	private SendMessageUtil messageUtil;

	private static XmlPullParserFactory factory = null;

	private ThreadPool threadPool;

	static {
		// 类初始化
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		plugin = (HttpServicePlugin) XMPPServer.getInstance()
				.getPluginManager().getPlugin("httpedu");
		AuthCheckFilter.addExclude("httpedu/httpservice");

		if (null == routingTable)
			routingTable = XMPPServer.getInstance().getRoutingTable();
		if (null == sendMessageUtil)
			sendMessageUtil = new SendMessageUtil();
		if (null == threadPool)
			threadPool = new ThreadPool(10);
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		super.doGet(request, response);
		PrintWriter out = response.getWriter();
		BufferedReader reader = request.getReader();
		StringBuffer buffer = new StringBuffer();
		String string = "";
		while ((string = reader.readLine()) != null) {
			String x = new String(string.getBytes("ISO-8859-1"), "UTF-8");
			buffer.append(x);
		}
		reader.close();
		// Check that our plugin is enabled.
		if (!plugin.isEnabled()) {
			Log.warn("http service plugin is disabled: "
					+ request.getQueryString());
			replyError("Error1002", response, out);
			return;
		}
		// Check the request type and process accordingly
		try {
			JSONObject object = new org.json.JSONObject(buffer.toString());
			if (null == object) {
				Log.warn("json is null " + request.getQueryString());
				replyError("Error1003", response, out);
				return;
			}
			// 参数
			String secret = object.getString("secret").trim();
			String optType = object.getString("optType").trim();
			String fromid = object.getString("sender").trim();
			String domain = object.getString("domain").trim();
			String resources = object.getString("resources").trim();
			String schoolid = object.getString("schoolid").trim();
			// Check this request is authorised
			if (secret == null || !secret.equals(plugin.getSecret())) {
				Log.warn("secret is error: " + request.getQueryString());
				replyError("Error1004", response, out);
				return;
			}
			if (null == messageUtil)
				messageUtil = new SendMessageUtil();
			if ("1".equals(optType)) {
				// Personal business to send separately
				String toIds = "";
				if (toIds == null || "".equals(toIds)) {
					Log.warn("toIds is error: " + request.getQueryString());
					replyError("Error1020", response, out);
					return;
				}
				try {
					threadPool.execute(createTask(object, routingTable));
					replyMessage("ok", response, out);
				} catch (Exception e) {
					Log.warn("toIds is error: " + request.getQueryString());
				}
			} else {
				Log.warn("opttype is error: " + request.getQueryString());
				replyError("Error1021", response, out);
			}
		} catch (IllegalArgumentException e) {
			Log.error("IllegalArgumentException: ", e);
			replyError("Ex1002", response, out);
		} catch (Exception e) {
			Log.error("Exception: ", e);
			replyError("Ex1001", response, out);
		}
	}

	@SuppressWarnings("unused")
	private void replyMessage(String message, HttpServletResponse response,
			PrintWriter out) {
		response.setContentType("text/xml");
		out.println("<result>" + message + "</result>");
		out.flush();
	}

	private void replyError(String error, HttpServletResponse response,
			PrintWriter out) {
		response.setContentType("text/xml");
		out.println("<error>" + error + "</error>");
		out.flush();
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	@Override
	public void destroy() {
		threadPool.waitFinish();
		threadPool.closePool();
		super.destroy();
		// Release the excluded URL
		AuthCheckFilter.removeExclude("httpedu/httpservice");
	}

	private static Runnable createTask(final JSONObject object,
			final RoutingTable routingTable) {
		return new Runnable() {
			public void run() {
				SendMessageUtil messageUtil = new SendMessageUtil();
				messageUtil.sendSingleMessage(object, routingTable);
			}
		};
	}

}
