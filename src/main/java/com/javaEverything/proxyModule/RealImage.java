package com.javaEverything.proxyModule;

/**
 * @author linmeng
 * @date 2023/2/22 00:25
 */

public class RealImage implements Image {
	@Override
	public void display(String name) {
		System.out.println(name + " display");
	}
}
