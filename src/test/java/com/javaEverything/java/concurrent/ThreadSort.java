package com.javaEverything.java.concurrent;

import org.junit.jupiter.api.Test;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 三个线程顺序执行
 *
 * @author linmeng
 * @date 2023/3/9 20:02
 */

public class ThreadSort {
	/**
	 * @Author linmeng
	 * @Description 使用{@code java.lang.Thread.join()}方法
	 * @date 2023/3/9 20:03
	 **/
	@Test
	public void threadSortJoin() {
		Thread A = new Thread(() -> {
			System.out.println("Thread A run");
		});
		Thread B = new Thread(() -> {
			try {
				A.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			System.out.println("Thread B run");
		});
		Thread C = new Thread(() -> {
			try {
				A.join();
				B.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Thread C run");
		});

		A.start();
		B.start();
		C.start();
	}

	/**
	 * @return void
	 * @Author linmeng
	 * @Description 给线程B、C定义两个信号量为0的Semaphore，A线程中执行完毕释放B线程对应的信号量，B线程执行完毕释放C线程的信号量。
	 * @date 2023/3/9 20:10
	 **/
	@Test
	public void threadSortSemaphore() {
		Semaphore sb = new Semaphore(0);
		Semaphore sc = new Semaphore(0);
		Thread A = new Thread(() -> {
			System.out.println("Thread A run");
			sb.release();
		});
		Thread B = new Thread(() -> {
			try {
				sb.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Thread B run");
			sc.release();
		});
		Thread C = new Thread(() -> {
			try {
				sc.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Thread C run");
		});
		A.start();
		B.start();
		C.start();
	}

	/**
	 * @param
	 * @return void
	 * @Author linmeng
	 * @Description 定义一个number指定线程执行顺序，定义三个condition，number等于几，唤醒第几个线程执行
	 * @date 2023/3/9 23:07
	 **/
	@Test
	public void threadSortReentrantLock() {
		ReentrantLock lock = new ReentrantLock();
		Condition conditionA = lock.newCondition();
		Condition conditionB = lock.newCondition();
		Condition conditionC = lock.newCondition();
		AtomicInteger number = new AtomicInteger(1);
		Thread A = new Thread(() -> {
			System.out.println(1);
			lock.lock();
			try {
				while (number.get() != 1) {
					conditionA.await();
				}
				System.out.println("Thread A run");
				number.set(2);
				conditionB.signal();

			} catch (Exception e) {
			} finally {
				lock.unlock();
			}
		});
		Thread B = new Thread(() -> {
			System.out.println(2);
			lock.lock();
			try {
				while (number.get() != 2) {
					conditionB.await();
				}
				System.out.println("Thread B run");
				number.set(3);
				conditionC.signal();

			} catch (Exception e) {
			} finally {
				lock.unlock();
			}
		});
		Thread C = new Thread(() -> {
			System.out.println(3);
			while (number.get()!=2){

			}
			lock.lock();
			try {
				while (number.get() != 3) {
					conditionC.await();
				}
				System.out.println("Thread C run");
				number.set(1);
				conditionA.signal();

			} catch (Exception e) {
			}finally {
				lock.unlock();
			}
		});

		A.start();
		B.start();
		C.start();
	}

}
