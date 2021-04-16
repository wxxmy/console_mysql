package com.example.demo.service.impl;

import java.util.concurrent.atomic.AtomicInteger;

public class TestThread implements Runnable {

	private int ticketNums = 10;

	@Override
	public void run() {
		//模拟延时
		boolean flag = true;
		while (flag) {
			flag = getTicket();
		}
	}

	private synchronized boolean getTicket() {
		if (ticketNums > 0) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(
					Thread.currentThread().getName() + "-->拿到了" + ticketNums-- + "号票，" + (ticketNums > 0 ? (
							"还剩下" + ticketNums + "张票") : "票卖完了"));
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		TestThread ticket = new TestThread();
		new Thread(ticket, "小明").start();
		new Thread(ticket, "老师").start();
		new Thread(ticket, "黄牛党").start();
	}
}
