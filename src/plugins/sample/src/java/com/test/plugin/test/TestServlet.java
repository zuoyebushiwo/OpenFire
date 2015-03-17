package com.test.plugin.test;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jivesoftware.admin.AuthCheckFilter;

/**
 * @author ZhangXueJun
 * @date 2015年3月17日
 */
public class TestServlet extends HttpServlet {

	public TestServlet() {
		super();
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("============调用servlet=============");
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doGet(request, response);
	}

	@Override
	public void init() throws ServletException {
		AuthCheckFilter.addExclude("test/testservlet");
		System.out.println("==========init()============");
	}

	@Override
	public void destroy() {
		System.out.println("==========destroy()=========");
		AuthCheckFilter.addExclude("test/testservlet");
	}

}
