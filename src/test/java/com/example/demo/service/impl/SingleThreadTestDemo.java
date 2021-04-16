package com.example.demo.service.impl;

import java.util.concurrent.TimeUnit;

public class SingleThreadTestDemo {

	public static void main(String[] args) throws InterruptedException {
		long s1 = System.currentTimeMillis();
		for (int i = 1; i < 11; i++) {
			TimeUnit.SECONDS.sleep(1);
			System.out.println(i + "--->出门了");
		}
		long s2 = System.currentTimeMillis();
		System.out.println("程序运行了---->:" + (s2 - s1) + "ms");
	}
}
