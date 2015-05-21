package com.httpservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author ZuoYe
 * @date 2015年3月18日
 */
public class MyThread extends Thread {

	private int i;

	public MyThread(int i) {
		this.i = i;
	}

	@Override
	public void run() {
		httpedu();
	}

	public void httpedu() {
		JSONObject jb = new JSONObject();
		try {
			jb.put("secret", "montnets@123");// montnets@123
			jb.put("optType", "1");
			// 你需要发送的参数
			try {
				testPost(jb);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void testPost(JSONObject json) throws IOException, JSONException {
		URL url = new URL("http://127.0.0.1:9090/plugins/example/httpservice");
		URLConnection connection = url.openConnection();
		connection.setDoOutput(true);
		OutputStreamWriter out = new OutputStreamWriter(
				connection.getOutputStream(), "UTF-8");
		String j = json.toString();
		out.write(j);
		out.flush();
		out.close();
		// 返回
		String sCurrentLine;
		String sTotalString;
		sCurrentLine = "";
		sTotalString = "";
		InputStream l_urlStream;
		l_urlStream = connection.getInputStream();
		BufferedReader l_reader = new BufferedReader(new InputStreamReader(
				l_urlStream));
		while ((sCurrentLine = l_reader.readLine()) != null) {
			sTotalString += sCurrentLine;
		}
		System.out.println("返回第" + i + " 结果：" + sTotalString);
		if (sTotalString.indexOf("ok") == -1) {
			System.out.println(sTotalString + " : " + json.toString());
		}
	}

}
