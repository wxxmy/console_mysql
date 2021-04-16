package com.example.demo.service.impl;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierDemo {

	public static void main(String[] args) {
		CyclicBarrier cyclicBarrier = new CyclicBarrier(7, () -> System.out.println("集齐了七颗龙珠，召唤神龙"));
		for (int i = 1; i < 8; i++) {
			int temp = i;
			new Thread(() -> {
				System.out.println("收集成功了第" + temp + "颗龙珠");
				try {
					cyclicBarrier.await();
				} catch (InterruptedException | BrokenBarrierException e) {
					e.printStackTrace();
				}
			}, String.valueOf(i)).start();
		}

	}

}
