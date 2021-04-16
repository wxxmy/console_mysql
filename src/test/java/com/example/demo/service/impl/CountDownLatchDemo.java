package com.example.demo.service.impl;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

public class CountDownLatchDemo {

	public static void main(String[] args) {
		long l1 = System.currentTimeMillis();
		CountDownLatch cdl = new CountDownLatch(10);
		for (int i = 1; i < 11; i++) {
			new Thread(() -> {
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println(Thread.currentThread().getName() + "--->出门了");
				cdl.countDown();
			}, String.valueOf(i)).start();
		}
		try {
			cdl.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("门卫把门关了");
		long l2 = System.currentTimeMillis();
		System.out.println("程序运行了---->:" + (l2 - l1) + "ms");
	}

}
