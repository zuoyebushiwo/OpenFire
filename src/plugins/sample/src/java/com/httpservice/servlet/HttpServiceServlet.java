package com.httpservice.servlet;

import javax.servlet.http.HttpServlet;

import org.jivesoftware.openfire.RoutingTable;
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

}
