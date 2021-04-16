package com.example.demo.service.impl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CompletableFutureDemo2 {

	public static void main(String[] args) {
		long l1 = System.currentTimeMillis();
		ExecutorService pool = Executors
				.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> {
			try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(Thread.currentThread().getName() + "  ====>正在运行");
			return "徐";
		}, pool).whenComplete((i, e) -> {
			System.out.println(i + ":" + e);
		});
		CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> {
			try {
				TimeUnit.SECONDS.sleep(3);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(Thread.currentThread().getName() + "  ====>正在运行");
			return "铭";
		}, pool).whenComplete((i, e) -> {
			System.out.println(i + ":" + e);
		});
		CompletableFuture<String> f3 = CompletableFuture.supplyAsync(() -> {
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(Thread.currentThread().getName() + "  ====>正在运行");
			return "阳";
		}, pool).whenComplete((i, e) -> {
			System.out.println(i + ":" + e);
		});
		CompletableFuture<Void> all = CompletableFuture.allOf(f1, f2, f3);
		all.join();
		System.out.println(f1.join() + f2.join() + f3.join());

		long l2 = System.currentTimeMillis();
		System.out.println(
				"===============================================================================================>   程序运行了-----> "
						+ (l2 - l1)
						+ " ms     <===============================================================================================");
		pool.shutdown();
	}

}
