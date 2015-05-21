package com.hoo.server.plugin;

import java.io.File;

import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;

/**
 * <b>function:</b> openfire server plugin sample
 * 
 * @author hoojo
 * @createDate 2013-2-28 下午05:48:22
 * @file SamplePlugin.java
 * @package com.hoo.server.plugin
 * @project OpenfirePlugin
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public class SamplePlugin implements Plugin {

	private XMPPServer server;

	@Override
	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		server = XMPPServer.getInstance();
		System.out.println("初始化…… 安装插件！");
		System.out.println(server.getServerInfo());
	}

	@Override
	public void destroyPlugin() {
		System.out.println("服务器停止，销毁插件！");
	}
	
	public static void main(String[] args) {
		System.out.println(113);
	}
}
