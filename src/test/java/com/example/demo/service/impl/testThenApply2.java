package com.example.demo.service.impl;

import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class testThenApply2 {

	public static void main(String[] args) {
		CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
			info("====future=====");
			return 2;
		});
		future.thenApply(res -> {
			info("====future=====");
			return res + 1;
		}).thenApply(res -> {
			info("====future=====");
			return res + 1;
		}).thenAccept(System.out::println);

		CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> {
			info("====future2=====");
			return 2;
		});
		future2.thenApplyAsync(res -> {
			info("====future2=====");
			return res + 1;
		}).thenApplyAsync(res -> {
			info("====future2=====");
			return res + 1;
		}).thenAccept(System.out::println);
	}

	public static void info(String mes) {
		String name = Thread.currentThread().getName();
		System.out.println("[ " + name + " ]: " + mes);
	}
}
