package com.httpservice;

import java.util.Date;

/**
 * @author ZhangXueJun
 * @date 2015年3月18日
 */
public class TestHttpedu {

	@SuppressWarnings("static-access")
	public static void main(String[] args) {

		String datetime = (new java.text.SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss SSS")).format(new Date());
		System.out.println(datetime);

		int a = 0;
		for (int i = 0; i < 1; i++) {
			MyThread my = new MyThread(a++);
			my.start();
			try {
				my.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
