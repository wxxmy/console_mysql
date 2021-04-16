package com.example.demo.service.impl;

public class TestYeild {

	public static void main(String[] args) {
		System.out.println(
				Thread.currentThread().getName() + "线程的优先级->" + Thread.currentThread().getPriority());
		MyYeild myYeild = new MyYeild();
		Thread a = new Thread(myYeild, "a");
		Thread b = new Thread(myYeild, "b");
		Thread c = new Thread(myYeild, "c");
		Thread d = new Thread(myYeild, "d");
		Thread e = new Thread(myYeild, "e");
		Thread f = new Thread(myYeild, "f");
		Thread g = new Thread(myYeild, "g");
		a.setPriority(1);
		b.setPriority(2);
		c.setPriority(3);
		d.setPriority(4);
		e.setPriority(5);
		f.setPriority(6);
		g.setPriority(7);
		a.start();
		b.start();
		c.start();
		d.start();
		e.start();
		f.start();
		g.start();
	}
}

class MyYeild implements Runnable {

	@Override
	public void run() {
//		System.out.println(Thread.currentThread().getName()+"线程开始执行");
//		Thread.yield();
		System.out.println(
				Thread.currentThread().getName() + "线程的有限级是->" + Thread.currentThread().getPriority());
//		System.out.println(Thread.currentThread().getName()+"线程结束执行");
	}
}
