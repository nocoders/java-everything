package com.javaEverything.java;

/**
 * 测试 父类、子类 静态代码块、构造代码块、构造方法执行顺序
 *
 * @author linmeng
 * @date 2023/3/1 14:42
 */
public class DemoTest {

	public static void main(String[] args) {
		new Son();
		System.out.println("再次执行===============");
		new Son();
	}

}


class Parent {

	{
		System.out.println("父类构造代码块");
	}

	static {
		System.out.println("父类静态代码块");
	}

	public Parent() {
		System.out.println("父类构造方法");
	}

}

class Son extends Parent {

	{
		System.out.println("子类构造代码块");
	}

	static {
		System.out.println("子类静态代码块");
	}

	public Son() {
		System.out.println("子类构造方法");
	}

}
