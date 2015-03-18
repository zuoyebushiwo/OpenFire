package com.httpservice.plugin;

import java.io.File;
import java.util.Map;

import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.util.JiveGlobals;
import org.jivesoftware.util.PropertyEventDispatcher;
import org.jivesoftware.util.PropertyEventListener;
import org.jivesoftware.util.StringUtils;

import com.httpservice.util.SendThread;

/**
 * @author ZhangXueJun
 * @date 2015年3月17日
 */
public class HttpServicePlugin implements Plugin, PropertyEventListener {

	private XMPPServer server;

	private String secret;
	private boolean enabled;
	private String smsAddrs;
	private SendThread sendThread;

	@Override
	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		server = XMPPServer.getInstance();
		secret = JiveGlobals.getProperty("plugin.httpservice.secret", "");
		// If no secret key has been assigned to the http service yet, assign a
		// random one.
		if (secret.equals("")) {
			secret = StringUtils.randomString(8);
			setSecret(secret);
		}

		// See if the service is enabled or not.
		enabled = JiveGlobals.getBooleanProperty("plugin.httpservice.enabled",
				false);

		// Get the list of IP addresses that can use this service. An empty list
		// means that this filter is disabled.
		smsAddrs = JiveGlobals.getProperty("plugin.httpservice.smsAddrs", "");
		// Listen to system property events
		PropertyEventDispatcher.addListener(this);

		if (null == sendThread) {
			sendThread = new SendThread();
		}
	}

	@Override
	public void destroyPlugin() {
		// Stop listening to system property events
		PropertyEventDispatcher.removeListener(this);
	}

	/**
	 * Returns the secret key that only valid requests should know.
	 *
	 * @return the secret key.
	 */
	public String getSecret() {
		return secret;
	}

	/**
	 * Sets the secret key that grants permission to use the httpservice.
	 *
	 * @param secret
	 *            the secret key.
	 */
	public void setSecret(String secret) {
		JiveGlobals.setProperty("plugin.httpservice.secret", secret);
		this.secret = secret;
	}

	public String getSmsAddrs() {
		return smsAddrs;
	}

	public void setSmsAddrs(String smsAddrs) {
		JiveGlobals.setProperty("plugin.httpservice.smsAddrs", smsAddrs);
		this.smsAddrs = smsAddrs;
	}

	/**
	 * Returns true if the http service is enabled. If not enabled, it will not
	 * accept requests to create new accounts.
	 *
	 * @return true if the http service is enabled.
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Enables or disables the http service. If not enabled, it will not accept
	 * requests to create new accounts.
	 *
	 * @param enabled
	 *            true if the http service should be enabled.
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		JiveGlobals.setProperty("plugin.httpservice.enabled", enabled ? "true"
				: "false");
	}

	@Override
	public void propertySet(String property, Map<String, Object> params) {
		if (property.equals("plugin.httpservice.secret")) {
			this.secret = (String) params.get("value");
		} else if (property.equals("plugin.httpservice.enabled")) {
			this.enabled = Boolean.parseBoolean((String) params.get("value"));
		} else if (property.equals("plugin.httpservice.smsAddrs")) {
			this.smsAddrs = (String) params.get("smsAddrs");
		}
	}

	@Override
	public void propertyDeleted(String property, Map<String, Object> params) {
		if (property.equals("plugin.httpservice.secret")) {
			this.secret = "";
		} else if (property.equals("plugin.httpservice.enabled")) {
			this.enabled = false;
		} else if (property.equals("plugin.httpservice.smsAddrs")) {
			this.smsAddrs = "";
		}
	}

	@Override
	public void xmlPropertySet(String property, Map<String, Object> params) {

	}

	@Override
	public void xmlPropertyDeleted(String property, Map<String, Object> params) {

	}

}
