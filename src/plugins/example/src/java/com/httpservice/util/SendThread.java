package com.httpservice.util;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.NoHttpResponseException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.jivesoftware.openfire.XMPPServer;

import com.httpservice.enity.SmsEnity;
import com.httpservice.plugin.HttpServicePlugin;

/**
 * @author ZuoYe
 * @date 2015年3月17日
 */
public class SendThread {

	private static HttpClient client;
	private static HttpServicePlugin httpServicePlugin;

	/**
	 * Perform a task thread pool
	 */
	private static Executor executor;

	/**
	 * Failed to perform HTTP request queue
	 */
	private static BlockingQueue<Runnable> executeQueue = new LinkedBlockingQueue<Runnable>(
			500);

	/**
	 * The HTTP request failure retry count
	 */
	private static final int MAX_RETRY_COUNT = 3;

	/**
	 * The HTTP request queue failure
	 */
	private static BlockingQueue<Runnable> failQueue = new LinkedBlockingQueue<Runnable>();

	/**
	 * Submit task thread is enabled
	 */
	private static boolean start = false;

	/**
	 * The background task threads
	 */
	private static Thread worker;

	private static SendSmsUtil ssu;

	static {
		ssu = new SendSmsUtil();
		httpServicePlugin = (HttpServicePlugin) XMPPServer.getInstance()
				.getPluginManager().getPlugin("httpedu");
		client = new HttpClient();
		initialize(client);
		executor = new ThreadPoolExecutor(1, 3, 3 * 1000,
				TimeUnit.MILLISECONDS, executeQueue, new RejectedHandler());
	}

	private static void initialize(HttpClient client) {
		MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
		HttpConnectionManagerParams connectionParams = connectionManager
				.getParams();

		connectionParams.setConnectionTimeout(2000);
		connectionParams.setDefaultMaxConnectionsPerHost(500);
		connectionParams.setMaxTotalConnections(500);
		client.setHttpConnectionManager(connectionManager);

		HttpClientParams httpParams = client.getParams();
		httpParams.setParameter(HttpMethodParams.RETRY_HANDLER,
				new HttpMethodRetryHandler() {
					@Override
					public boolean retryMethod(HttpMethod method,
							IOException exception, int executionCount) {
						if (method == null) {
							return false;
						}
						if (executionCount > MAX_RETRY_COUNT) {
							return false;
						}
						if (!method.isRequestSent()) {
							return true;
						}
						if (exception instanceof NoHttpResponseException) {
							return true;
						}
						return false;
					}
				});
	}

	public static boolean push(SmsEnity msg) {
		return push(msg, false);
	}

	public static boolean push(final SmsEnity msg, boolean reliable) {
		PostMethod postMethod = null;
		try {
			String urlAddrs = httpServicePlugin.getSmsAddrs();
			postMethod = createPostMethod(msg, urlAddrs);
			client.executeMethod(postMethod);
			int statusCode = postMethod.getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				String retMsg = new String(postMethod.getResponseBody());
				retMsg = URLDecoder.decode(retMsg, "UTF-8");
				if ("".equals(retMsg) || null == retMsg) {
					LOG.info("sms send success! sendid: " + msg.getSendid()
							+ " Receiveid: " + msg.getReceiveid()
							+ " Content: " + msg.getContent());
				} else {
					throw new PushException(retMsg);
				}
				return true;
			} else if (statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
				throw new PushException("Push server internal error: "
						+ HttpStatus.SC_INTERNAL_SERVER_ERROR);
			}
		} catch (IOException e1) {
			if (reliable) {
				try {
					failQueue.put(new Runnable() {
						public void run() {
							push(msg, true);
						}
					});
					LOG.info(Thread.currentThread().getName()
							+ ": in the queue...");
					if (!start) {
						startup();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (PushException e2) {
			LOG.info("The sms Push failure and throws PushException"
					+ e2.getMessage() + "\n" + "sms content-> Sendid:"
					+ msg.getSendid() + " Receiveid: " + msg.getReceiveid()
					+ " Content: " + msg.getContent() + " Sendtype:"
					+ msg.getSendtype());
		} catch (Exception e3) {
			LOG.info("The sms Push failure and throws PushException"
					+ e3.getMessage() + "\n" + "sms content-> Sendid:"
					+ msg.getSendid() + " Receiveid: " + msg.getReceiveid()
					+ " Content: " + msg.getContent() + " Sendtype:"
					+ msg.getSendtype());
		} finally {
			postMethod.releaseConnection();
		}
		return false;
	}

	private static PostMethod createPostMethod(SmsEnity se, String uri)
			throws Exception {
		PostMethod postMethod = null;
		postMethod = new PostMethod(uri);
		if (null != se.getSendid() && !"".equals(se.getSendid()))
			postMethod
					.addParameter(new NameValuePair("sendid", se.getSendid()));
		if (null != se.getToken() && !"".equals(se.getSendid()))
			postMethod.addParameter(new NameValuePair("token", se.getToken()));
		postMethod.addParameter(new NameValuePair("sendtype",
				se.getSendtype() != null ? se.getSendtype() : ""));
		if (null != se.getReceiveid() && !"".equals(se.getReceiveid()))
			postMethod.addParameter(new NameValuePair("receiveid", se
					.getReceiveid()));
		if (null != se.getClassid() && !"".equals(se.getClassid()))
			postMethod.addParameter(new NameValuePair("classid", se
					.getClassid()));
		postMethod.addParameter(new NameValuePair("schoolid",
				se.getSchoolid() != null ? se.getSchoolid() : ""));
		if (null != se.getGroupid() && !"".equals(se.getGroupid()))
			postMethod.addParameter(new NameValuePair("groupid", se
					.getGroupid()));
		if (null != se.getSerial_num() && !"".equals(se.getSerial_num()))
			postMethod.addParameter(new NameValuePair("serial_num", se
					.getSerial_num()));
		if (null != se.getContent() && !"".equals(se.getContent()))
			postMethod.addParameter(new NameValuePair("content", se
					.getContent()));
		return postMethod;
	}

	/**
	 * Start a background thread
	 */
	private synchronized static void startup() {
		LOG.info(Thread.currentThread().getName()
				+ " : Start a background thread...");
		if (!start) {
			start = true;
			worker = new Thread() {
				@Override
				public void run() {
					workStart(this);
				}
			};
			worker.setName("worker thread");
			worker.start();
		}
	}

	/**
	 * Submit to perform tasks
	 * 
	 * @param thread
	 */
	private static void workStart(Thread thread) {
		while (start && worker == thread) {
			Runnable task = failQueue.poll();
			LOG.info(Thread.currentThread().getName()
					+ " : The queue of the communist party of China has a number of tasks: "
					+ failQueue.size());
			LOG.info(Thread.currentThread().getName() + " : From the queue"
					+ task == null ? "no" : "" + "Take out the task...");

			if (task != null) {
				executor.execute(task);
			} else {
				try {
					Thread.sleep(5 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	static class RejectedHandler implements RejectedExecutionHandler {
		@Override
		public void rejectedExecution(Runnable task, ThreadPoolExecutor executor) {
			if (!executor.isShutdown()) {
				if (!executor.isTerminating()) {
					try {
						failQueue.put(task);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

}
