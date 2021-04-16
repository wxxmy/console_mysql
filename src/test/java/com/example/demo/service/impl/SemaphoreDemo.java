package com.example.demo.service.impl;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class SemaphoreDemo {

	public static void main(String[] args) {
		Semaphore semaphore = new Semaphore(2);
		for (int i = 1; i < 7; i++) {
			int num = i;
			new Thread(() -> {
				try {
					semaphore.acquire();
					System.out.println(num + "号车进入车库");
					TimeUnit.SECONDS.sleep(2);
					System.out.println(num + "号车离开车库");
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					semaphore.release();
				}
			}, String.valueOf(i)).start();
		}
	}

}
