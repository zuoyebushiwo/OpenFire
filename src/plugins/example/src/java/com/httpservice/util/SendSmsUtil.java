package com.httpservice.util;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.dom4j.io.XMPPPacketReader;
import org.jivesoftware.openfire.PacketRouter;
import org.jivesoftware.openfire.RoutingTable;
import org.jivesoftware.openfire.XMPPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;

import com.httpservice.enity.SmsEnity;

/**
 * @author ZhangXueJun
 * @date 2015年3月17日
 */
public class SendSmsUtil {
	private static Map<Integer, XMPPPacketReader> parsers = new ConcurrentHashMap<Integer, XMPPPacketReader>();
	private static XmlPullParserFactory factory = null;
	private static final Logger log = LoggerFactory
			.getLogger(SendMessageUtil.class);

	/**
	 * Send a single message interface
	 */
	public String sendSingleMessage(JSONObject ae, RoutingTable routingTable)
			throws JSONException {
		String state = "failure";
		SmsEnity se = new SmsEnity();
		String fromid = ae.getString("sender");
		String domain = ae.getString("domain");
		String resources = ae.getString("resources");
		String toId = ae.getString("toIds");
		String msgType = ae.getString("msgType");
		String msgNo = ae.getString("msgNo");
		String smsType = ae.getString("smsType");
		// RoutingTable routingTable = ae.getRoutingTable();
		String token = "";// 暂时为空
		String smsBody = ae.getString("smsBody");
		String schoolid = ae.getString("schoolid");
		// 0指定用户发送，1给学生发送，2全班发送，3，多个班级发送6.发给学生 家长你那发给老师是0
		String rectype = ae.getString("rectype");
		String classid = "";
		if ("10014".equals(msgType)) {
			classid = ae.getString("classId");
		}
		String sender = fromid + "@" + domain + "/" + resources;
		StringBuffer sb = new StringBuffer();
		if (toId.length() > 0) {
			String tos[] = toId.split(",");
			for (int i = 0; i < tos.length; i++) {
				String to = tos[i] + "@" + domain;
				Message packet;
				if (!sender.contains(to) && !sender.equals(to)) {
					packet = assemblyMessages(to, sender, "1", msgType, msgNo,
							null, null, null, classid);
					if ("2".equals(smsType))
						sb.append(tos[i] + ",");
					PacketRouter router = XMPPServer.getInstance()
							.getPacketRouter();
					router.route(packet);
					log.info("send: " + packet);
					state = "ok";
					if ("1".equals(smsType)) {
						if (null == routingTable.getClientRoute(new JID(to
								+ "/" + resources)))
							sb.append(tos[i] + ",");
					}
				}
			}
			String receiveids = sb.toString();
			// Send SMS
			if (!"".equals(smsType) && receiveids.length() > 0
					&& null != rectype) {
				se.setSendid(fromid);
				se.setToken(token);
				if (",".equals(sb.substring(sb.length() - 1)))
					receiveids = receiveids.substring(0,
							receiveids.length() - 1);
				se.setSendtype(rectype);
				se.setReceiveid(receiveids);
				String body;
				try {
					body = java.net.URLEncoder.encode(smsBody, "UTF-8");
					se.setContent(body);
				} catch (UnsupportedEncodingException e) {
					log.error("send sms UnsupportedEncodingException:"
							+ e.getMessage());
				}
				se.setSchoolid(schoolid);
				se.setGroupid("");
				se.setClassid("");
				se.setSerial_num(String.valueOf(System.currentTimeMillis()));
				SendThread.push(se);
			}
		}
		return state;
	}

	public boolean isDigit(String sender, String to) {
		return to.contains(sender);
	}

	/**
	 * Send group business messages
	 */
	public String sendGroupMessage(AdditionalEntities ae) {
		// 短信发送
		return null;
	}

	/**
	 * The message format assembled
	 */
	public Message assemblyMessages(String... to) {
		// 封装消息
		return new org.xmpp.packet.Message();
	}
}
