package com.test.plugin;

import java.io.File;
import java.util.Map;

import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.util.PropertyEventDispatcher;
import org.jivesoftware.util.PropertyEventListener;

import com.test.plugin.iqhandle.TestIQHandle;

/**
 * @author ZuoYe
 * @date 2015年3月17日
 */
public class TestPlugin implements Plugin, PropertyEventListener {

	private XMPPServer server;

	@Override
	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		server = XMPPServer.getInstance();
		server.getIQRouter().addHandler(new TestIQHandle());
		PropertyEventDispatcher.addListener(this);
		System.out.println("==========插件初始化=============");
	}

	@Override
	public void destroyPlugin() {
		PropertyEventDispatcher.removeListener(this);
		System.out.println("==========插件销毁动作=============");
	}

	@Override
	public void propertySet(String property, Map<String, Object> params) {
		
	}

	@Override
	public void propertyDeleted(String property, Map<String, Object> params) {
		// TODO Auto-generated method stub

	}

	@Override
	public void xmlPropertySet(String property, Map<String, Object> params) {
		// TODO Auto-generated method stub

	}

	@Override
	public void xmlPropertyDeleted(String property, Map<String, Object> params) {
		// TODO Auto-generated method stub

	}

}
