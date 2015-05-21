package com.hoo.server.plugin;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <b>function:</b> sample servlet
 * 
 * @author hoojo
 * @createDate 2013-3-4 下午04:15:20
 * @file SampleServlet.java
 * @package com.hoo.server.plugin
 * @project OpenfirePlugin
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public class SampleServlet extends HttpServlet {

	private static final long serialVersionUID = -5404916983906926869L;

	@Override
	public void init() throws ServletException {
		super.init();
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
//		super.doGet(request, response);

		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		System.out.println("请求SampleServlet GET Method");
		out.print("请求SampleServlet GET Method");
		out.flush();
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		super.doPost(request, response);

		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		System.out.println("请求SampleServlet POST Method");
		out.print("请求SampleServlet POST Method");
		out.flush();
	}

	@Override
	public void destroy() {
		super.destroy();
	}
	
	public static void main(String[] args) {
		System.out.println(1112111);
	}
	
}