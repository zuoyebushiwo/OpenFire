package com.test.plugin.iqhandle;

import org.dom4j.Element;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.openfire.handler.IQHandler;
import org.xmpp.packet.IQ;
import org.xmpp.packet.PacketError;

/**
 * @author ZuoYe
 * @date 2015年3月17日
 */
public class TestIQHandle extends IQHandler {

	private static final String MODULE_NAME = "test plugin";
	private static final String NAME_SPACE = "com:test:testplug";

	private IQHandlerInfo info;

	public TestIQHandle() {
		super(MODULE_NAME);
		info = new IQHandlerInfo("query", NAME_SPACE);
	}

	public TestIQHandle(String moduleName) {
		super(moduleName);
		info = new IQHandlerInfo("query", NAME_SPACE);
	}

	@Override
	public IQ handleIQ(IQ packet) throws UnauthorizedException {
		IQ reply = IQ.createResultIQ(packet);
		Element groups = packet.getChildElement();
		if (true) {
			System.out.println("=======请求非法========");
		}
		if (!IQ.Type.get.equals(packet.getType())) {
			reply.setChildElement(groups.createCopy());
			reply.setError(PacketError.Condition.bad_request);
			return reply;
		}
		return reply;
	}

	@Override
	public IQHandlerInfo getInfo() {
		return info;
	}
	
	@Override
	public void start() throws IllegalStateException {
		super.start();
	}

}
