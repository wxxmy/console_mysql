package com.example.demo.service.impl;

import lombok.SneakyThrows;

class Race implements Runnable {

	// 胜利者
	private static String winner = null;

	@SneakyThrows
	@Override
	public void run() {
		for (int i = 1; i < 101; i++) {
			boolean flag = isGameOver(i);
			if (flag) {
				break;
			}

			String name = Thread.currentThread().getName();
			if (name.equals("兔子") && i % 10 == 0) {
				Thread.sleep(100);
			} else {
				Thread.sleep(1);
			}
			System.out.println(name + "跑了" + i + "步");
		}
	}

	// 游戏是否结束方法
	public boolean isGameOver(int steps) {
		if (winner != null) {
			return true;
		}
		if (steps >= 100) {
			winner = Thread.currentThread().getName();
			System.out.println("游戏结束,胜利者是：" + winner);
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		Race race = new Race();
		new Thread(race, "兔子").start();
		new Thread(race, "乌龟").start();
	}
}